package com.techisthoughts.ia.movieclassification.converter;

import com.opencsv.bean.AbstractBeanField;

import java.util.List;

public class PercentageSuggestedToFriendsFamilyConverter extends AbstractBeanField<List<String>, String> {
    @Override
    protected String convert(String suggestedToFriendsFamily) {
        // Check if the input is a valid percentage
        try {
            double percentage = Double.parseDouble(suggestedToFriendsFamily.trim().replace("%", ""));
            if (percentage < 0 || percentage > 100) {
                return "Invalid Percentage";
            }
            return String.format("%.2f%%", percentage);
        } catch (NumberFormatException e) {
            return "Invalid Percentage";
        }
    }
}