package com.techisthoughts.ia.movieclassification.repository.entity;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.SchemaFieldType;
import com.redis.om.spring.annotations.Searchable;
import com.redis.om.spring.indexing.DistanceMetric;
import com.redis.om.spring.indexing.VectorType;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;
import redis.clients.jedis.search.schemafields.VectorField;

import java.util.List;


@Document
public record MovieEntity(@Id @Searchable String movieTitle, @Indexed @Searchable String genre,
                          @Indexed String releaseYear, @Indexed String averageRating, @Indexed String numberOfReviews,
                          @Indexed @Searchable List<String> reviewHighlights,
                          @Indexed String minuteOfLifeChangingInsight, @Indexed String howDiscovered,
                          @Indexed String meaningfulAdviceTaken, @Indexed String isSuggestedToFriendsFamily,
                          @Indexed String percentageSuggestedToFriendsFamily, @NonNull @Searchable String text,
                          @Indexed(schemaFieldType = SchemaFieldType.VECTOR,
                                  algorithm = VectorField.VectorAlgorithm.HNSW, type = VectorType.FLOAT32,
                                  dimension = 3072, distanceMetric = DistanceMetric.COSINE,
                                  initialCapacity = 10) byte[] embedding) {
}
