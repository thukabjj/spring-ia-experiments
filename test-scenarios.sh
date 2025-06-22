#!/bin/bash

# =============================================================================
# Movie Classification Performance Test Suite
# Comprehensive testing of all optimization strategies with observability
# =============================================================================

set -e

# Configuration
BASE_URL="http://localhost:8588"
RESULTS_DIR="test-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$RESULTS_DIR/test_run_$TIMESTAMP.log"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Create results directory
mkdir -p "$RESULTS_DIR"

# Logging function
log() {
    echo -e "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Function to make API calls and measure time
api_call() {
    local url="$1"
    local method="${2:-GET}"
    local description="$3"
    local expected_status="${4:-200}"

    log "${BLUE}Testing: $description${NC}"
    log "URL: $method $url"

    local start_time=$(date +%s.%N)
    local response=$(curl -s -w "\n%{http_code}" -X "$method" "$url" 2>/dev/null || echo -e "\nERROR")
    local end_time=$(date +%s.%N)

    local status_code=$(echo "$response" | tail -n1)
    local body=$(echo "$response" | head -n -1)
    local duration=$(echo "$end_time - $start_time" | bc -l)

    if [[ "$status_code" == "$expected_status" ]]; then
        log "${GREEN}✓ SUCCESS${NC} - ${duration}s - Status: $status_code"
        echo "$body" > "$RESULTS_DIR/$(echo "$description" | tr ' ' '_' | tr '[:upper:]' '[:lower:]')_$TIMESTAMP.json"
        return 0
    else
        log "${RED}✗ FAILED${NC} - ${duration}s - Status: $status_code"
        log "Response: $body"
        return 1
    fi
}

# Function to extract metrics from response
extract_metric() {
    local file="$1"
    local metric_path="$2"
    jq -r "$metric_path" "$file" 2>/dev/null || echo "N/A"
}

# Function to run performance comparison
performance_comparison() {
    log "${PURPLE}=== PERFORMANCE COMPARISON TEST ===${NC}"

    local strategies=("blocking" "optimized" "ultra-fast" "lightning")
    local results_file="$RESULTS_DIR/performance_comparison_$TIMESTAMP.csv"

    echo "Strategy,Chunks,Questions,Embeddings,Time(ms),Throughput(chunks/sec),Cache Hit Rate" > "$results_file"

    for strategy in "${strategies[@]}"; do
        log "${CYAN}Testing $strategy strategy...${NC}"

        case $strategy in
            "blocking")
                api_call "$BASE_URL/api/movies/process?strategy=FIXED_SIZE&questionsPerChunk=2" "POST" "Blocking Strategy Test"
                ;;
            "optimized")
                api_call "$BASE_URL/api/optimized/movies/process-fast?strategy=FIXED_SIZE&questionsPerChunk=2" "POST" "Optimized Strategy Test"
                ;;
            "ultra-fast")
                api_call "$BASE_URL/api/ultra-fast/movies/process-lightning?strategy=FIXED_SIZE&questionsPerChunk=2&maxChunks=5" "POST" "Ultra-Fast Strategy Test"
                ;;
            "lightning")
                # Run twice to test caching
                api_call "$BASE_URL/api/ultra-fast/movies/process-lightning?strategy=FIXED_SIZE&questionsPerChunk=2&maxChunks=5" "POST" "Lightning Strategy Test (Cold)"
                api_call "$BASE_URL/api/ultra-fast/movies/process-lightning?strategy=FIXED_SIZE&questionsPerChunk=2&maxChunks=5" "POST" "Lightning Strategy Test (Warm)"
                ;;
        esac

        # Extract metrics if successful
        local result_file="$RESULTS_DIR/$(echo "${strategy}_strategy_test" | tr '-' '_')_$TIMESTAMP.json"
        if [[ -f "$result_file" ]]; then
            local chunks=$(extract_metric "$result_file" ".chunksProcessed // 0")
            local questions=$(extract_metric "$result_file" ".questionsGenerated // 0")
            local embeddings=$(extract_metric "$result_file" ".embeddingsCreated // 0")
            local time_ms=$(extract_metric "$result_file" ".processingTimeMs // 0")
            local throughput=$(extract_metric "$result_file" ".throughput // \"N/A\"")
            local cache_rate=$(extract_metric "$result_file" ".cacheHitRate // \"0.0%\"")

            echo "$strategy,$chunks,$questions,$embeddings,$time_ms,$throughput,$cache_rate" >> "$results_file"
        fi
    done

    log "${GREEN}Performance comparison saved to: $results_file${NC}"
}

# Function to test observability endpoints
test_observability() {
    log "${PURPLE}=== OBSERVABILITY TESTS ===${NC}"

    local endpoints=(
        "/api/observability/health:Health Check"
        "/api/observability/metrics:Comprehensive Metrics"
        "/api/observability/tokens:Token Analytics"
        "/api/observability/performance:Performance Analytics"
        "/api/observability/cache:Cache Analytics"
        "/api/observability/errors:Error Analytics"
        "/api/observability/dashboard:Dashboard Data"
        "/api/observability/features:Feature Toggles"
    )

    for endpoint_desc in "${endpoints[@]}"; do
        IFS=':' read -r endpoint description <<< "$endpoint_desc"
        api_call "$BASE_URL$endpoint" "GET" "$description"
    done
}

# Function to test token tracking
test_token_tracking() {
    log "${PURPLE}=== TOKEN TRACKING TEST ===${NC}"

    # Get initial token count
    api_call "$BASE_URL/api/observability/tokens" "GET" "Initial Token Count"
    local initial_file="$RESULTS_DIR/initial_token_count_$TIMESTAMP.json"

    # Perform some operations
    api_call "$BASE_URL/api/ultra-fast/movies/process-lightning?strategy=BY_GENRE&questionsPerChunk=3&maxChunks=3" "POST" "Token Tracking Test"

    # Get final token count
    api_call "$BASE_URL/api/observability/tokens" "GET" "Final Token Count"
    local final_file="$RESULTS_DIR/final_token_count_$TIMESTAMP.json"

    # Calculate token usage
    if [[ -f "$initial_file" && -f "$final_file" ]]; then
        local initial_tokens=$(extract_metric "$initial_file" ".totalProcessed")
        local final_tokens=$(extract_metric "$final_file" ".totalProcessed")
        local tokens_used=$((final_tokens - initial_tokens))

        log "${GREEN}Token Usage Analysis:${NC}"
        log "  Initial tokens: $initial_tokens"
        log "  Final tokens: $final_tokens"
        log "  Tokens used: $tokens_used"
        log "  Estimated cost: $(extract_metric "$final_file" ".costEstimation.estimatedCostUSD")"
    fi
}

# Function to test cache performance
test_cache_performance() {
    log "${PURPLE}=== CACHE PERFORMANCE TEST ===${NC}"

    # Clear cache by restarting or waiting for TTL
    log "Testing cache performance with repeated requests..."

    local cache_test_results="$RESULTS_DIR/cache_performance_$TIMESTAMP.csv"
    echo "Run,Time(ms),Cache Hit Rate,Throughput" > "$cache_test_results"

    for i in {1..5}; do
        log "${CYAN}Cache test run $i/5${NC}"

        local start_time=$(date +%s.%N)
        api_call "$BASE_URL/api/ultra-fast/movies/process-lightning?strategy=FIXED_SIZE&questionsPerChunk=2&maxChunks=3" "POST" "Cache Test Run $i"
        local end_time=$(date +%s.%N)

        local duration=$(echo "($end_time - $start_time) * 1000" | bc -l | cut -d. -f1)

        # Get cache metrics
        api_call "$BASE_URL/api/observability/cache" "GET" "Cache Metrics Run $i"
        local cache_file="$RESULTS_DIR/cache_metrics_run_${i}_$TIMESTAMP.json"

        if [[ -f "$cache_file" ]]; then
            local hit_rate=$(extract_metric "$cache_file" ".hitRatePercentage")
            local efficiency=$(extract_metric "$cache_file" ".efficiency.rating")

            echo "$i,$duration,$hit_rate,$efficiency" >> "$cache_test_results"
            log "  Run $i: ${duration}ms, Cache Hit Rate: $hit_rate, Efficiency: $efficiency"
        fi

        sleep 1
    done

    log "${GREEN}Cache performance test saved to: $cache_test_results${NC}"
}

# Function to test error handling
test_error_handling() {
    log "${PURPLE}=== ERROR HANDLING TEST ===${NC}"

    # Test invalid parameters
    api_call "$BASE_URL/api/ultra-fast/movies/process-lightning?strategy=INVALID&questionsPerChunk=-1" "POST" "Invalid Parameters Test" "400"

    # Test non-existent endpoints
    api_call "$BASE_URL/api/non-existent/endpoint" "GET" "Non-existent Endpoint Test" "404"

    # Get error analytics
    api_call "$BASE_URL/api/observability/errors" "GET" "Error Analytics After Tests"
}

# Function to test concurrent load
test_concurrent_load() {
    log "${PURPLE}=== CONCURRENT LOAD TEST ===${NC}"

    local concurrent_requests=5
    local pids=()

    log "Starting $concurrent_requests concurrent requests..."

    for i in $(seq 1 $concurrent_requests); do
        (
            api_call "$BASE_URL/api/ultra-fast/movies/process-lightning?strategy=FIXED_SIZE&questionsPerChunk=2&maxChunks=2" "POST" "Concurrent Request $i"
        ) &
        pids+=($!)
    done

    # Wait for all requests to complete
    for pid in "${pids[@]}"; do
        wait "$pid"
    done

    log "${GREEN}All concurrent requests completed${NC}"

    # Check system metrics after load
    api_call "$BASE_URL/api/observability/metrics" "GET" "System Metrics After Load Test"
}

# Function to generate test report
generate_report() {
    log "${PURPLE}=== GENERATING TEST REPORT ===${NC}"

    local report_file="$RESULTS_DIR/test_report_$TIMESTAMP.html"

    cat > "$report_file" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Movie Classification Performance Test Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { background: #f4f4f4; padding: 20px; border-radius: 5px; }
        .section { margin: 20px 0; }
        .success { color: green; }
        .error { color: red; }
        .metric { background: #e8f4f8; padding: 10px; margin: 5px 0; border-radius: 3px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Movie Classification Performance Test Report</h1>
        <p><strong>Generated:</strong> $(date)</p>
        <p><strong>Test Run ID:</strong> $TIMESTAMP</p>
    </div>

    <div class="section">
        <h2>Test Summary</h2>
        <div class="metric">
            <strong>Total Test Files Generated:</strong> $(ls -1 "$RESULTS_DIR"/*_$TIMESTAMP.* 2>/dev/null | wc -l)
        </div>
        <div class="metric">
            <strong>Log File:</strong> $LOG_FILE
        </div>
    </div>

    <div class="section">
        <h2>Performance Comparison</h2>
        <p>Results saved to: performance_comparison_$TIMESTAMP.csv</p>
    </div>

    <div class="section">
        <h2>Test Files Generated</h2>
        <ul>
EOF

    # List all generated files
    for file in "$RESULTS_DIR"/*_$TIMESTAMP.*; do
        if [[ -f "$file" ]]; then
            echo "            <li>$(basename "$file")</li>" >> "$report_file"
        fi
    done

    cat >> "$report_file" << EOF
        </ul>
    </div>

    <div class="section">
        <h2>Next Steps</h2>
        <ul>
            <li>Review performance comparison CSV for optimization insights</li>
            <li>Analyze cache performance trends</li>
            <li>Check error rates and system stability</li>
            <li>Monitor token usage and costs</li>
        </ul>
    </div>
</body>
</html>
EOF

    log "${GREEN}Test report generated: $report_file${NC}"
}

# Main test execution
main() {
    log "${BLUE}=== STARTING COMPREHENSIVE TEST SUITE ===${NC}"
    log "Results will be saved to: $RESULTS_DIR"
    log "Log file: $LOG_FILE"

    # Check if service is available
    if ! curl -s "$BASE_URL/api/observability/health" > /dev/null; then
        log "${RED}ERROR: Service not available at $BASE_URL${NC}"
        log "Please ensure the application is running on port 8587"
        exit 1
    fi

    log "${GREEN}Service is available. Starting tests...${NC}"

    # Run all test suites
    test_observability
    test_token_tracking
    performance_comparison
    test_cache_performance
    test_error_handling
    test_concurrent_load

    # Generate final report
    generate_report

    log "${GREEN}=== ALL TESTS COMPLETED ===${NC}"
    log "Check the results directory for detailed output: $RESULTS_DIR"
    log "Open the HTML report: $RESULTS_DIR/test_report_$TIMESTAMP.html"
}

# Check dependencies
check_dependencies() {
    local deps=("curl" "jq" "bc")
    for dep in "${deps[@]}"; do
        if ! command -v "$dep" &> /dev/null; then
            log "${RED}ERROR: Required dependency '$dep' not found${NC}"
            log "Please install: $dep"
            exit 1
        fi
    done
}

# Script entry point
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    check_dependencies
    main "$@"
fi
