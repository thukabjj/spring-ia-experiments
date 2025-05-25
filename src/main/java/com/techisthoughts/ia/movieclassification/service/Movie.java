package com.techisthoughts.ia.movieclassification.service;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.techisthoughts.ia.movieclassification.converter.IsSuggestedToFriendsFamilyConverter;
import com.techisthoughts.ia.movieclassification.converter.PercentageSuggestedToFriendsFamilyConverter;
import com.techisthoughts.ia.movieclassification.converter.ReviewHighlightsConverter;

import java.util.List;

public record Movie(
        @CsvBindByName(column = "Movie Title")
        String movieTitle,

        @CsvBindByName(column = "Genre")
        String genre,

        @CsvBindByName(column = "Release Year")
        String releaseYear,

        @CsvBindByName(column = "Average Rating")
        String averageRating,

        @CsvBindByName(column = "Number of Reviews")
        String numberOfReviews,

        @CsvCustomBindByName(column = "Review Highlights", converter = ReviewHighlightsConverter.class)
        List<String> reviewHighlights,

        @CsvBindByName(column = "Minute of Life-Changing Insight")
        String minuteOfLifeChangingInsight,

        @CsvBindByName(column = "How Discovered")
        String howDiscovered,

        @CsvBindByName(column = "Meaningful Advice Taken")
        String meaningfulAdviceTaken,

        @CsvCustomBindByName(column = "Suggested to Friends/Family (Y/N %)", converter = IsSuggestedToFriendsFamilyConverter.class)
        String isSuggestedToFriendsFamily,

        @CsvCustomBindByName(column = "Suggested to Friends/Family (Y/N %)", converter = PercentageSuggestedToFriendsFamilyConverter.class)
        String percentageSuggestedToFriendsFamily
) {
}
