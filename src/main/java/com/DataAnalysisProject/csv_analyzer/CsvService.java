package com.DataAnalysisProject.csv_analyzer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

@Service
public class CsvService {

    public void processCSV(MultipartFile file) throws IOException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = reader.readAll();
            // Process the CSV data here, for example:
            for (String[] record : records) {
                // Print or process each row
                System.out.println(String.join(",", record));
            }
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }
}
