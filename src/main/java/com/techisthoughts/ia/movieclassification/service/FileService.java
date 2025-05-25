package com.techisthoughts.ia.movieclassification.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

@Service
public class FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    public List<Movie> readMoviesFromCsv(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            CsvToBean<Movie> csvToBean = new CsvToBeanBuilder<Movie>(reader)
                    .withType(Movie.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Movie> records = csvToBean.parse();
            LOG.info("Successfully read {} movie records from {}", records.size(), filePath);
            return records;
        } catch (Exception e) {
            LOG.error("Error reading CSV file {}: {}", filePath, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}