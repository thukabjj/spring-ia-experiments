package com.techisthoughts.ia.movieclassification.presentation.dto;

import java.util.List;

/**
 * Data Transfer Object for Movie
 */
public record MovieDto(
    String movieTitle,
    String genre,
    String releaseYear,
    String averageRating,
    String numberOfReviews,
    List<String> reviewHighlights,
    String minuteOfLifeChangingInsight,
    String howDiscovered,
    String meaningfulAdviceTaken,
    String isSuggestedToFriendsFamily,
    String percentageSuggestedToFriendsFamily
) {}
