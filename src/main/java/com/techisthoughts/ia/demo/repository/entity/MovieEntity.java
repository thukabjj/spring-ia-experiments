package com.techisthoughts.ia.demo.repository.entity;

import com.redis.om.spring.annotations.*; // Ensure Document is from here
import com.redis.om.spring.indexing.DistanceMetric;
import com.redis.om.spring.indexing.VectorType;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull; // Keep if needed, or use javax.validation.constraints.NotNull
import redis.clients.jedis.search.schemafields.VectorField; // This is fine for schema definition

import java.util.Arrays;
import java.util.Objects;

@Document // Change this from @RedisHash
public class MovieEntity {

    @Id
    @Searchable
    private String movieTitle;

    @Indexed
    private String genre;

    @Indexed
    private String releaseYear;

    @Indexed
    private String averageRating;

    @Indexed
    private String reviewHighlights;

    @Indexed
    private String minuteOfLifeChangingInsight;

    @Indexed
    private String howDiscovered;

    @Indexed
    private String meaningfulAdviceTaken;

    @Indexed
    private String suggestedToFriendsFamily;



    // It seems you have @Vectorize on 'text' and also a separate 'embedding' field.
    // @Vectorize will automatically create an embedding from the 'text' field and store it
    // in a field named by 'destination' (which is 'embeddedText' here).
    // You might not need the separate 'embedding' byte[] field if @Vectorize is handling it.
    // If you intend to manually set the embedding, then @Vectorize might not be what you want here,
    // or you need to align its destination.

    @NonNull
    @Searchable // Make the original text searchable
    private String text; // This is the source for vectorization if using @Vectorize

    // If @Vectorize is used on 'text' to create 'embeddedText', this manual 'embedding' field might be redundant
    // or needs to be the destination of @Vectorize.
    // Let's assume for now you want to manage embeddings manually or that @Vectorize is not fully configured yet.
    // The dimension 384 here must match the output dimension of your embedding model (e.g., nomic-embed-text is often 768 or more).
    @Indexed(
            schemaFieldType = SchemaFieldType.VECTOR,
            algorithm = VectorField.VectorAlgorithm.HNSW,
            type = VectorType.FLOAT32,
            dimension = 768, // IMPORTANT: This MUST match your embedding model's output dimension
            distanceMetric = DistanceMetric.COSINE,
            initialCapacity = 10
    )
    private byte[] embedding; // This field will store the vector

    // Constructor and other methods remain the same for now
    // ... (rest of your class)
    public MovieEntity(String movieTitle, String genre, String releaseYear, String averageRating,
                       String reviewHighlights, String minuteOfLifeChangingInsight, String howDiscovered,
                       String meaningfulAdviceTaken, String suggestedToFriendsFamily, String text,
                       byte[] embedding) {
        this.movieTitle = movieTitle;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.averageRating = averageRating;
        this.reviewHighlights = reviewHighlights;
        this.minuteOfLifeChangingInsight = minuteOfLifeChangingInsight;
        this.howDiscovered = howDiscovered;
        this.meaningfulAdviceTaken = meaningfulAdviceTaken;
        this.suggestedToFriendsFamily = suggestedToFriendsFamily;
        this.text = text; // The original text
        this.embedding = embedding; // The pre-computed or externally generated embedding
    }

    public MovieEntity() {
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public String getReviewHighlights() {
        return reviewHighlights;
    }

    public void setReviewHighlights(String reviewHighlights) {
        this.reviewHighlights = reviewHighlights;
    }

    public String getMinuteOfLifeChangingInsight() {
        return minuteOfLifeChangingInsight;
    }

    public void setMinuteOfLifeChangingInsight(String minuteOfLifeChangingInsight) {
        this.minuteOfLifeChangingInsight = minuteOfLifeChangingInsight;
    }

    public String getHowDiscovered() {
        return howDiscovered;
    }

    public void setHowDiscovered(String howDiscovered) {
        this.howDiscovered = howDiscovered;
    }

    public String getMeaningfulAdviceTaken() {
        return meaningfulAdviceTaken;
    }

    public void setMeaningfulAdviceTaken(String meaningfulAdviceTaken) {
        this.meaningfulAdviceTaken = meaningfulAdviceTaken;
    }

    public String getSuggestedToFriendsFamily() {
        return suggestedToFriendsFamily;
    }

    public void setSuggestedToFriendsFamily(String suggestedToFriendsFamily) {
        this.suggestedToFriendsFamily = suggestedToFriendsFamily;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(byte[] embedding) {
        this.embedding = embedding;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MovieEntity that = (MovieEntity) o;
        return  Objects.equals(movieTitle, that.movieTitle) && Objects.equals(genre, that.genre) && Objects.equals(releaseYear, that.releaseYear) && Objects.equals(averageRating, that.averageRating) && Objects.equals(reviewHighlights, that.reviewHighlights) && Objects.equals(minuteOfLifeChangingInsight, that.minuteOfLifeChangingInsight) && Objects.equals(howDiscovered, that.howDiscovered) && Objects.equals(meaningfulAdviceTaken, that.meaningfulAdviceTaken) && Objects.equals(suggestedToFriendsFamily, that.suggestedToFriendsFamily) && Objects.equals(text, that.text) && Objects.deepEquals(embedding, that.embedding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieTitle, genre, releaseYear, averageRating, reviewHighlights, minuteOfLifeChangingInsight, howDiscovered, meaningfulAdviceTaken, suggestedToFriendsFamily, text, Arrays.hashCode(embedding));
    }

    @Override
    public String toString() {
        return "MovieEntity{" +
                "movieTitle='" + movieTitle + '\'' +
                ", genre='" + genre + '\'' +
                ", releaseYear='" + releaseYear + '\'' +
                ", averageRating='" + averageRating + '\'' +
                ", reviewHighlights='" + reviewHighlights + '\'' +
                ", minuteOfLifeChangingInsight='" + minuteOfLifeChangingInsight + '\'' +
                ", howDiscovered='" + howDiscovered + '\'' +
                ", meaningfulAdviceTaken='" + meaningfulAdviceTaken + '\'' +
                ", suggestedToFriendsFamily='" + suggestedToFriendsFamily + '\'' +
                ", text='" + text + '\'' +
                ", embedding=" + Arrays.toString(embedding) +
                '}';
    }
}
