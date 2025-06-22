package com.techisthoughts.ia.movieclassification.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Core Movie domain entity representing the Netflix Life Impact Dataset (NLID)
 */
public class Movie {

    private final String movieTitle;
    private final String genre;
    private final String releaseYear;
    private final String averageRating;
    private final String numberOfReviews;
    private final List<String> reviewHighlights;
    private final String minuteOfLifeChangingInsight;
    private final String howDiscovered;
    private final String meaningfulAdviceTaken;
    private final String isSuggestedToFriendsFamily;
    private final String percentageSuggestedToFriendsFamily;

    public Movie(String movieTitle, String genre, String releaseYear, String averageRating,
                 String numberOfReviews, List<String> reviewHighlights, String minuteOfLifeChangingInsight,
                 String howDiscovered, String meaningfulAdviceTaken, String isSuggestedToFriendsFamily,
                 String percentageSuggestedToFriendsFamily) {
        this.movieTitle = Objects.requireNonNull(movieTitle, "Movie title cannot be null");
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.averageRating = averageRating;
        this.numberOfReviews = numberOfReviews;
        this.reviewHighlights = reviewHighlights != null ? List.copyOf(reviewHighlights) : List.of();
        this.minuteOfLifeChangingInsight = minuteOfLifeChangingInsight;
        this.howDiscovered = howDiscovered;
        this.meaningfulAdviceTaken = meaningfulAdviceTaken;
        this.isSuggestedToFriendsFamily = isSuggestedToFriendsFamily;
        this.percentageSuggestedToFriendsFamily = percentageSuggestedToFriendsFamily;
    }

    // Getters
    public String getMovieTitle() { return movieTitle; }
    public String getGenre() { return genre; }
    public String getReleaseYear() { return releaseYear; }
    public String getAverageRating() { return averageRating; }
    public String getNumberOfReviews() { return numberOfReviews; }
    public List<String> getReviewHighlights() { return reviewHighlights; }
    public String getMinuteOfLifeChangingInsight() { return minuteOfLifeChangingInsight; }
    public String getHowDiscovered() { return howDiscovered; }
    public String getMeaningfulAdviceTaken() { return meaningfulAdviceTaken; }
    public String getIsSuggestedToFriendsFamily() { return isSuggestedToFriendsFamily; }
    public String getPercentageSuggestedToFriendsFamily() { return percentageSuggestedToFriendsFamily; }

    /**
     * Creates a textual representation of the movie for embedding
     */
    public String toEmbeddingText() {
        StringBuilder text = new StringBuilder();
        text.append("Movie: ").append(movieTitle);

        if (genre != null && !genre.trim().isEmpty()) {
            text.append(" | Genre: ").append(genre);
        }

        if (releaseYear != null && !releaseYear.trim().isEmpty()) {
            text.append(" | Year: ").append(releaseYear);
        }

        if (averageRating != null && !averageRating.trim().isEmpty()) {
            text.append(" | Rating: ").append(averageRating);
        }

        if (reviewHighlights != null && !reviewHighlights.isEmpty()) {
            text.append(" | Highlights: ").append(String.join(", ", reviewHighlights));
        }

        if (minuteOfLifeChangingInsight != null && !minuteOfLifeChangingInsight.trim().isEmpty()) {
            text.append(" | Life Insight: ").append(minuteOfLifeChangingInsight);
        }

        if (meaningfulAdviceTaken != null && !meaningfulAdviceTaken.trim().isEmpty()) {
            text.append(" | Advice: ").append(meaningfulAdviceTaken);
        }

        return text.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(movieTitle, movie.movieTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieTitle);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieTitle='" + movieTitle + '\'' +
                ", genre='" + genre + '\'' +
                ", releaseYear='" + releaseYear + '\'' +
                '}';
    }
}
