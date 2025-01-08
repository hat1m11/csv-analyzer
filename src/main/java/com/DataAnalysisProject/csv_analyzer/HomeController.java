package com.example.csvanalyzer;

import org.springframework.ui.Model;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {


    private final List<String[]> csvData = new ArrayList<>();

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadCsv(@RequestParam("csvFile") MultipartFile file, Model model) {
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            csvData.clear();
            for (CSVRecord record : csvParser) {
                List<String> recordData = new ArrayList<>();
                record.forEach(recordData::add);
                csvData.add(recordData.toArray(new String[0]));
            }


            model.addAttribute("message", "CSV uploaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();


            model.addAttribute("message", "Failed to upload CSV. Please try again.");
        }

        return "index";
    }




}
