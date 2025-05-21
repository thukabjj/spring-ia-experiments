package com.techisthoughts.ia.demo.service;

import com.techisthoughts.ia.demo.controller.MovieRecord;
import io.deephaven.csv.CsvSpecs;
import io.deephaven.csv.reading.CsvReader;
import io.deephaven.csv.sinks.SinkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    // Define constants for your CSV column names for robustness
    private static final String COL_MOVIE_TITLE = "Movie Title";
    private static final String COL_GENRE = "Genre";
    private static final String COL_RELEASE_YEAR = "Release Year";
    private static final String COL_AVERAGE_RATING = "Average Rating";
    private static final String COL_NUMBER_OF_REVIEWS = "Number of Reviews";
    private static final String COL_REVIEW_HIGHLIGHTS = "Review Highlights";
    private static final String COL_MINUTE_OF_LIFE_CHANGING_INSIGHT = "Minute of Life-Changing Insight";
    private static final String COL_HOW_DISCOVERED = "How Discovered";
    private static final String COL_MEANINGFUL_ADVICE_TAKEN = "Meaningful Advice Taken";
    private static final String COL_SUGGESTED_TO_FRIENDS_FAMILY = "Suggested to Friends/Family (Y/N %)";

    // Optional: For consistent double formatting
    // private static final DecimalFormat RATING_FORMAT = new DecimalFormat("#.0");


    public List<MovieRecord> readMoviesFromCsv(String filePath) {
        List<MovieRecord> movieRecords = new ArrayList<>();
        File csvFile = new File(filePath);

        if (!csvFile.exists()) {
            LOG.error("CSV file not found at path: {}", filePath);
            return movieRecords;
        }

        final CsvSpecs specs = CsvSpecs.builder()
                .hasHeaderRow(true)
                .build();

        try (InputStream inputStream = new FileInputStream(csvFile)) {
            final CsvReader.Result resultTable = CsvReader.read(specs, inputStream, SinkFactory.arrays());

            long numRows = resultTable.numRows();
            if (numRows == 0) {
                LOG.info("CSV file {} is empty or contains only a header.", filePath);
                return movieRecords;
            }

            Map<String, CsvReader.ResultColumn> columnsByName = new HashMap<>();
            for (CsvReader.ResultColumn column : resultTable) {
                columnsByName.put(column.name(), column);
            }

            String[] requiredColumns = {
                    COL_MOVIE_TITLE, COL_GENRE, COL_RELEASE_YEAR, COL_AVERAGE_RATING,
                    COL_NUMBER_OF_REVIEWS, COL_REVIEW_HIGHLIGHTS, COL_MINUTE_OF_LIFE_CHANGING_INSIGHT,
                    COL_HOW_DISCOVERED, COL_MEANINGFUL_ADVICE_TAKEN, COL_SUGGESTED_TO_FRIENDS_FAMILY
            };

            for (String colName : requiredColumns) {
                if (!columnsByName.containsKey(colName)) {
                    LOG.error("Required column '{}' not found in CSV file: {}. Please check CSV headers and constants.", colName, filePath);
                    return movieRecords;
                }
                if (columnsByName.get(colName).data() == null) {
                    LOG.error("Data array for column '{}' is null in CSV file: {}.", colName, filePath);
                    return movieRecords;
                }
            }

            for (int i = 0; i < numRows; i++) {
                try {
                    String movieTitle = getStringValue(columnsByName.get(COL_MOVIE_TITLE), i);
                    String genre = getStringValue(columnsByName.get(COL_GENRE), i);
                    String releaseYear = getStringValue(columnsByName.get(COL_RELEASE_YEAR), i);
                    String averageRating = getStringValue(columnsByName.get(COL_AVERAGE_RATING), i);
                    String numberOfReviews = getStringValue(columnsByName.get(COL_NUMBER_OF_REVIEWS), i);
                    List<String> reviewHighlights = splitReviewHighlights(getStringValue(columnsByName.get(COL_REVIEW_HIGHLIGHTS), i));
                    String minuteOfInsight = getStringValue(columnsByName.get(COL_MINUTE_OF_LIFE_CHANGING_INSIGHT), i);
                    String howDiscovered = getStringValue(columnsByName.get(COL_HOW_DISCOVERED), i);
                    String adviceTaken = getStringValue(columnsByName.get(COL_MEANINGFUL_ADVICE_TAKEN), i);
                    String suggestedToFriendsFamily = getStringValue(columnsByName.get(COL_SUGGESTED_TO_FRIENDS_FAMILY), i);
                    String isSuggestedToFriendsFamily = "";
                    String percentageSuggestedToFriendsFamily = "";
                    if (suggestedToFriendsFamily != null && !suggestedToFriendsFamily.isEmpty()) {
                        String[] parts = suggestedToFriendsFamily.trim().split("\\s+");
                        if (parts.length == 2) {
                            percentageSuggestedToFriendsFamily = parts[0];
                            isSuggestedToFriendsFamily = parts[1];
                        } else if (parts.length == 1) {
                            // Fallback: if only one part, assume it's the percentage
                            percentageSuggestedToFriendsFamily = parts[0];
                        }
                    }

                    movieRecords.add(new MovieRecord(
                            movieTitle,
                            genre,
                            releaseYear,
                            averageRating,
                            numberOfReviews,
                            reviewHighlights,
                            minuteOfInsight,
                            howDiscovered,
                            adviceTaken,
                            isSuggestedToFriendsFamily,
                            percentageSuggestedToFriendsFamily
                    ));
                } catch (Exception e) {
                    LOG.error("Error processing row {} from CSV file {}: {}", i, filePath, e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            LOG.error("Error reading CSV file {}: {}", filePath, e.getMessage(), e);
        }
        LOG.info("Successfully read {} movie records from {}", movieRecords.size(), filePath);
        return movieRecords;
    }

    /**
     * Splits the review highlights string into an array, separating by ' / ' and trimming quotes and whitespace.
     * Example: "\"Will Smith’s struggle hit hard. A must-watch!\" / \"Overly sentimental but inspiring.\""
     * becomes: ["Will Smith’s struggle hit hard. A must-watch!", "Overly sentimental but inspiring."]
     */
  private List<String> splitReviewHighlights(String highlights) {
      if (highlights == null || highlights.isEmpty()) return List.of();
      // Remove leading/trailing quotes, then split, then trim quotes from each part
      String[] parts = highlights.split("\\s*/\\s*");
      List<String> result = new ArrayList<>();
      for (String part : parts) {
          result.add(part.replaceAll("^\"+|\"+$", "").trim());
      }
      return result;
  }

    /**
     * Helper method to safely get a String value from a ResultColumn's data array for a given row index.
     * Handles common inferred types like String[], int[], double[], long[], float[], boolean[]
     * and converts them to String.
     *
     * @param column   The CsvReader.ResultColumn.
     * @param rowIndex The row index.
     * @return The string value, or an empty string if null, out of bounds, or type not handled.
     */
    private String getStringValue(CsvReader.ResultColumn column, int rowIndex) {
        if (column == null || column.data() == null) {
            return "";
        }

        Object dataArrayObj = column.data();
        String value = "";

        try {
            if (rowIndex < 0) return ""; // Basic bounds check

            if (dataArrayObj instanceof String[]) {
                String[] dataArray = (String[]) dataArrayObj;
                if (rowIndex < dataArray.length) {
                    value = dataArray[rowIndex];
                }
            } else if (dataArrayObj instanceof int[]) {
                int[] dataArray = (int[]) dataArrayObj;
                if (rowIndex < dataArray.length) {
                    value = String.valueOf(dataArray[rowIndex]);
                }
            } else if (dataArrayObj instanceof double[]) {
                double[] dataArray = (double[]) dataArrayObj;
                if (rowIndex < dataArray.length) {
                    // value = RATING_FORMAT.format(dataArray[rowIndex]); // Optional: for specific formatting
                    value = String.valueOf(dataArray[rowIndex]);
                }
            } else if (dataArrayObj instanceof long[]) {
                long[] dataArray = (long[]) dataArrayObj;
                if (rowIndex < dataArray.length) {
                    value = String.valueOf(dataArray[rowIndex]);
                }
            } else if (dataArrayObj instanceof float[]) {
                float[] dataArray = (float[]) dataArrayObj;
                if (rowIndex < dataArray.length) {
                    value = String.valueOf(dataArray[rowIndex]);
                }
            } else if (dataArrayObj instanceof boolean[]) {
                boolean[] dataArray = (boolean[]) dataArrayObj;
                if (rowIndex < dataArray.length) {
                    value = String.valueOf(dataArray[rowIndex]);
                }
            } else if (dataArrayObj instanceof Object[]) { // Fallback for other object arrays
                Object[] dataArray = (Object[]) dataArrayObj;
                if (rowIndex < dataArray.length && dataArray[rowIndex] != null) {
                    value = String.valueOf(dataArray[rowIndex]);
                }
            } else {
                // This case might occur if deephaven-csv uses a different array type not yet handled
                // or if the column was empty and resulted in an unexpected data() type.
                LOG.warn("Unhandled data array type for column '{}': {}. Attempting String.valueOf().",
                        column.name(), dataArrayObj.getClass().getSimpleName());
                // As a last resort, try to get the element if it's an array of objects and convert.
                // This is less safe and depends on the unknown array structure.
                // For robust handling, identify the actual type and add an `else if` block.
                if (dataArrayObj.getClass().isArray()) {
                    Object[] tempArray = (Object[]) dataArrayObj; // This cast itself might fail
                    if (rowIndex < tempArray.length && tempArray[rowIndex] != null) {
                        value = String.valueOf(tempArray[rowIndex]);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            LOG.warn("Row index {} out of bounds for column '{}' (type: {}).",
                    rowIndex, column.name(), dataArrayObj.getClass().getSimpleName());
            return ""; // Return empty string on bounds error
        } catch (ClassCastException e) {
            // This catch might be redundant if the instanceof checks are comprehensive,
            // but kept for safety.
            LOG.warn("Unexpected ClassCastException for column '{}' (type: {}): {}. Attempting String.valueOf().",
                    column.name(), dataArrayObj.getClass().getSimpleName(), e.getMessage());
            // Attempt a generic String.valueOf if direct casting failed but it's an array
            if (dataArrayObj.getClass().isArray()) {
                try {
                    Object[] tempArray = (Object[]) dataArrayObj;
                    if (rowIndex < tempArray.length && tempArray[rowIndex] != null) {
                        return String.valueOf(tempArray[rowIndex]);
                    }
                } catch (Exception ex) {
                    LOG.error("Further error during fallback String.valueOf for column '{}': {}", column.name(), ex.getMessage());
                }
            }
            return ""; // Return empty string on casting error
        }

        return value != null ? value : ""; // Ensure null is converted to empty string
    }
}