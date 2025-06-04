package com.techisthoughts.ia.movieclassification.repository.entity;

import java.util.List;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.SchemaFieldType;
import com.redis.om.spring.annotations.Searchable;
import com.redis.om.spring.indexing.DistanceMetric;
import com.redis.om.spring.indexing.VectorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import redis.clients.jedis.search.schemafields.VectorField;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieEntity {
    @Id
    @Searchable
    private String movieTitle;
    @Indexed
    @Searchable
    private String genre;
    @Indexed
    private String releaseYear;
    @Indexed
    private String averageRating;
    @Indexed
    private String numberOfReviews;
    @Indexed
    @Searchable
    private List<String> reviewHighlights;
    @Indexed
    private String minuteOfLifeChangingInsight;
    @Indexed
    private String howDiscovered;
    @Indexed
    private String meaningfulAdviceTaken;
    @Indexed
    private String isSuggestedToFriendsFamily;
    @Indexed
    private String percentageSuggestedToFriendsFamily;
    @NonNull
    @Searchable
    private String embeddedText;
    @Indexed(schemaFieldType = SchemaFieldType.VECTOR,
             algorithm = VectorField.VectorAlgorithm.HNSW, type = VectorType.FLOAT32,
             dimension = 3072, distanceMetric = DistanceMetric.COSINE,
             initialCapacity = 10)
    private byte[] embedding;


}
