package com.techisthoughts.ia.movieclassification.infrastructure.adapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.techisthoughts.ia.movieclassification.domain.model.Movie;
import com.techisthoughts.ia.movieclassification.domain.port.MovieRepositoryPort;

/**
 * Service for loading movies from CSV files
 */
@Service
public class CsvMovieLoader {

    private static final Logger logger = LoggerFactory.getLogger(CsvMovieLoader.class);

    private final MovieRepositoryPort movieRepository;

    public CsvMovieLoader(MovieRepositoryPort movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Load movies from the NLID CSV file
     */
    public int loadMoviesFromCsv() {
        try {
            ClassPathResource resource = new ClassPathResource("NLID.csv");

            if (!resource.exists()) {
                logger.error("NLID.csv file not found in classpath");
                return 0;
            }

            logger.info("Loading movies from NLID.csv");

            try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
                List<String[]> records = reader.readAll();

                if (records.isEmpty()) {
                    logger.warn("No records found in CSV file");
                    return 0;
                }

                // Skip header row
                List<String[]> dataRecords = records.subList(1, records.size());

                List<Movie> movies = new ArrayList<>();
                int processedCount = 0;

                for (String[] record : dataRecords) {
                    try {
                        Movie movie = parseMovieFromRecord(record);
                        if (movie != null) {
                            movies.add(movie);
                            processedCount++;
                        }
                    } catch (Exception e) {
                        logger.warn("Error parsing movie record: {}", Arrays.toString(record), e);
                    }
                }

                logger.info("Parsed {} movies from CSV", movies.size());

                List<Movie> savedMovies = movieRepository.saveAll(movies);
                logger.info("Successfully saved {} movies to repository", savedMovies.size());

                return savedMovies.size();

            }

        } catch (IOException | CsvException e) {
            logger.error("Error loading movies from CSV", e);
            return 0;
        }
    }

    /**
     * Parse a movie from CSV record
     * Expected CSV format: MovieTitle,Genre,ReleaseYear,AverageRating,NumberOfReviews,ReviewHighlights,
     *                     MinuteOfLifeChangingInsight,HowDiscovered,MeaningfulAdviceTaken,
     *                     SuggestedToFriendsFamilyPercentage
     */
    private Movie parseMovieFromRecord(String[] record) {
        if (record.length < 10) {
            logger.warn("Insufficient columns in record: expected 10, got {}", record.length);
            return null;
        }

        String movieTitle = sanitize(record[0]);
        if (movieTitle.isEmpty()) {
            logger.warn("Movie title is empty, skipping record");
            return null;
        }

        String genre = sanitize(record[1]);
        String releaseYear = sanitize(record[2]);
        String averageRating = sanitize(record[3]);
        String numberOfReviews = sanitize(record[4]);

        // Parse review highlights (may contain multiple values separated by semicolons)
        List<String> reviewHighlights = parseListField(record[5]);

        String minuteOfLifeChangingInsight = sanitize(record[6]);
        String howDiscovered = sanitize(record[7]);
        String meaningfulAdviceTaken = sanitize(record[8]);

        // Parse the combined "Suggested to Friends/Family (Y/N %)" field
        String suggestedField = sanitize(record[9]);
        String isSuggestedToFriendsFamily = parseSuggestedBoolean(suggestedField);
        String percentageSuggestedToFriendsFamily = parseSuggestedPercentage(suggestedField);

        return new Movie(
            movieTitle,
            genre,
            releaseYear,
            averageRating,
            numberOfReviews,
            reviewHighlights,
            minuteOfLifeChangingInsight,
            howDiscovered,
            meaningfulAdviceTaken,
            isSuggestedToFriendsFamily,
            percentageSuggestedToFriendsFamily
        );
    }

    private String sanitize(String value) {
        return value != null ? value.trim() : "";
    }

    private List<String> parseListField(String value) {
        if (value == null || value.trim().isEmpty()) {
            return List.of();
        }

        // Split by semicolon and clean up
        return Arrays.stream(value.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * Parse boolean from combined field like "92% Y" or "65% N"
     */
    private String parseSuggestedBoolean(String combinedField) {
        if (combinedField == null || combinedField.trim().isEmpty()) {
            return "N";
        }

        String trimmed = combinedField.trim().toUpperCase();
        if (trimmed.contains("Y")) {
            return "Y";
        } else if (trimmed.contains("N")) {
            return "N";
        } else {
            // If no Y/N specified, assume Y if percentage > 50%
            String percentage = parseSuggestedPercentage(combinedField);
            try {
                int pct = Integer.parseInt(percentage.replace("%", ""));
                return pct > 50 ? "Y" : "N";
            } catch (NumberFormatException e) {
                return "N";
            }
        }
    }

    /**
     * Parse percentage from combined field like "92% Y" or "65% N"
     */
    private String parseSuggestedPercentage(String combinedField) {
        if (combinedField == null || combinedField.trim().isEmpty()) {
            return "0%";
        }

        String trimmed = combinedField.trim();
        // Extract percentage using regex
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)%");
        java.util.regex.Matcher matcher = pattern.matcher(trimmed);

        if (matcher.find()) {
            return matcher.group(1) + "%";
        }

        return "0%";
    }
}
