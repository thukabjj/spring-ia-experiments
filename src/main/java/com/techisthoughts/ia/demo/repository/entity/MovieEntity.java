package com.techisthoughts.ia.demo.repository.entity;

import com.redis.om.spring.annotations.*; // Ensure Document is from here
import com.redis.om.spring.indexing.DistanceMetric;
import com.redis.om.spring.indexing.VectorType;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull; // Keep if needed, or use javax.validation.constraints.NotNull
import redis.clients.jedis.search.schemafields.VectorField; // This is fine for schema definition

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Document
public record MovieEntity(
    @Id @Searchable String movieTitle,
    @Indexed @Searchable String genre,
    @Indexed String releaseYear,
    @Indexed String averageRating,
    @Indexed String numberOfReviews,
    @Indexed @Searchable List<String> reviewHighlights,
    @Indexed String minuteOfLifeChangingInsight,
    @Indexed String howDiscovered,
    @Indexed String meaningfulAdviceTaken,
    @Indexed String isSuggestedToFriendsFamily,
    @Indexed String percentageSuggestedToFriendsFamily,
    @NonNull @Searchable String text,
    @Indexed(
            schemaFieldType = SchemaFieldType.VECTOR,
            algorithm = VectorField.VectorAlgorithm.HNSW,
            type = VectorType.FLOAT32,
            dimension = 3072,
            distanceMetric = DistanceMetric.COSINE,
            initialCapacity = 10
    ) byte[] embedding
) {
}
