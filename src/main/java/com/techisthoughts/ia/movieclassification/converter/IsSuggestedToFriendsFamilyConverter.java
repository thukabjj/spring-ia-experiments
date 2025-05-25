package com.techisthoughts.ia.movieclassification.converter;

import com.opencsv.bean.AbstractBeanField;

import java.util.List;

public class IsSuggestedToFriendsFamilyConverter extends AbstractBeanField<List<String>, String> {
    @Override
    protected String convert(String suggestedToFriendsFamily) {
        return switch (suggestedToFriendsFamily.trim().toUpperCase()) {
                    case "Y", "YES" -> "Yes";
                    case "N", "NO" -> "No";
                    default -> "Unknown";
                };
    }
}