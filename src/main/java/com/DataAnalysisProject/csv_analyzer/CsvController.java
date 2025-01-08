package com.DataAnalysisProject.csv_analyzer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CsvController {

    // Method to calculate the mean of a list of numbers
    private double calculateMean(List<Integer> data) {
        return data.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    // Method to calculate the median of a list of numbers
    private double calculateMedian(List<Integer> data) {
        data.sort(Integer::compareTo);
        int size = data.size();
        if (size % 2 == 0) {
            return (data.get(size / 2 - 1) + data.get(size / 2)) / 2.0;
        } else {
            return data.get(size / 2);
        }
    }

    // Method to calculate the standard deviation
    private double calculateStandardDeviation(List<Integer> data, double mean) {
        double variance = data.stream()
                .mapToDouble(i -> Math.pow(i - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    @PostMapping("/upload")
    public String uploadCsv(@RequestParam("csvFile") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("message", "No file selected. Please select a CSV file to upload.");
                return "index"; // Return back to the upload form if no file selected
            }

            // Check if the file is a CSV
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
                model.addAttribute("message", "Invalid file type. Please upload a CSV file.");
                return "index"; // Return to the upload form if the file isn't a CSV
            }

            // Create an InputStreamReader to read the file
            Reader in = new InputStreamReader(file.getInputStream());

            // Parse the CSV file using Apache Commons CSV
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

            // Create lists to hold the data
            List<String> columnNames = new ArrayList<>();
            List<List<String>> data = new ArrayList<>();
            List<Integer> years = new ArrayList<>();
            List<Integer> sales = new ArrayList<>();

            // Iterate over each record (row) in the CSV
            for (CSVRecord record : records) {
                if (columnNames.isEmpty()) {
                    // Extract the column headers from the first row
                    columnNames.addAll(record.toMap().keySet());
                }

                // Extract row data and add it to the data list
                List<String> rowData = new ArrayList<>();
                for (String column : columnNames) {
                    rowData.add(record.get(column));
                }
                data.add(rowData);

                // Extract data for the chart (Year and Sales)
                years.add(Integer.parseInt(record.get("Year")));  // Assuming the CSV has "Year" as a column
                sales.add(Integer.parseInt(record.get("Sales")));  // Assuming the CSV has "Sales" as a column
            }

            // Calculate statistics
            double mean = calculateMean(sales);
            double median = calculateMedian(sales);
            double stdDev = calculateStandardDeviation(sales, mean);

            // Add parsed data and statistics to the model so it can be displayed in the view
            model.addAttribute("columns", columnNames);
            model.addAttribute("data", data);
            model.addAttribute("message", "CSV uploaded and parsed successfully!");
            model.addAttribute("xData", years);  // Data for x-axis (Years)
            model.addAttribute("yData", sales); // Data for y-axis (Sales)
            model.addAttribute("mean", mean);
            model.addAttribute("median", median);
            model.addAttribute("stdDev", stdDev);

            return "index"; // Return to the HTML page to display the data and charts
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Error processing file. Please try again.");
            return "index"; // Return to the page with an error message
        }
    }
}
