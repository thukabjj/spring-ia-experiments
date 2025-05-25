package com.techisthoughts.ia.movieclassification.converter;

import com.opencsv.bean.AbstractBeanField;

import java.util.Arrays;
import java.util.List;

public class ReviewHighlightsConverter extends AbstractBeanField<List<String>, String> {
    @Override
    protected List<String> convert(String value) {
        return Arrays.asList(value.split(","));
    }
}