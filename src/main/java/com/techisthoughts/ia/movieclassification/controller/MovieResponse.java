package com.techisthoughts.ia.movieclassification.controller;

import java.util.List;

public record MovieResponse(
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
) { }
