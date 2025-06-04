package com.techisthoughts.ia.movieclassification.service;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.techisthoughts.ia.movieclassification.converter.IsSuggestedToFriendsFamilyConverter;
import com.techisthoughts.ia.movieclassification.converter.PercentageSuggestedToFriendsFamilyConverter;
import com.techisthoughts.ia.movieclassification.converter.ReviewHighlightsConverter;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

        @CsvBindByName(column = "Movie Title")
        private String movieTitle;

        @CsvBindByName(column = "Genre")
        private String genre;

        @CsvBindByName(column = "Release Year")
        private String releaseYear;

        @CsvBindByName(column = "Average Rating")
        private String averageRating;

        @CsvBindByName(column = "Number of Reviews")
        private String numberOfReviews;

        @CsvCustomBindByName(column = "Review Highlights", converter = ReviewHighlightsConverter.class)
        private List<String> reviewHighlights;

        @CsvBindByName(column = "Minute of Life-Changing Insight")
        private String minuteOfLifeChangingInsight;

        @CsvBindByName(column = "How Discovered")
        private String howDiscovered;

        @CsvBindByName(column = "Meaningful Advice Taken")
        private String meaningfulAdviceTaken;

        @CsvCustomBindByName(column = "Suggested to Friends/Family (Y/N %)", converter = IsSuggestedToFriendsFamilyConverter.class)
        private String isSuggestedToFriendsFamily;

        @CsvCustomBindByName(column = "Suggested to Friends/Family (Y/N %)", converter = PercentageSuggestedToFriendsFamilyConverter.class)
        private String percentageSuggestedToFriendsFamily;
}