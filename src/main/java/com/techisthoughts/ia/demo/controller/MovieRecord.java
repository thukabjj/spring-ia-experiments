package com.techisthoughts.ia.demo.controller;

import java.util.List;

public record MovieRecord(
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
