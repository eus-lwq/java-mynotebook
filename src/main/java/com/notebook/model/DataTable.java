package com.notebook.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tables")
public class DataTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Column(name = "table_data", columnDefinition = "JSON")
    private String tableData;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public String[][] getData() {
        if (tableData == null || tableData.isEmpty()) {
            return new String[][] {{""}};
        }
        
        try {
            // Manual parsing of the JSON array to avoid Jackson deserialization issues
            // Remove outer quotes if present (from database storage)
            String jsonData = tableData;
            if (jsonData.startsWith("\"") && jsonData.endsWith("\"")) {
                jsonData = jsonData.substring(1, jsonData.length() - 1);
                // Unescape internal quotes
                jsonData = jsonData.replace("\\\"", "\"");
            }
            
            // Simple parsing for 2D array in JSON format [["val1","val2"],["val3","val4"]]
            if (!jsonData.startsWith("[") || !jsonData.endsWith("]")) {
                System.err.println("Invalid JSON format for table data: " + jsonData);
                return new String[][] {{""}};
            }
            
            // Remove outer brackets
            jsonData = jsonData.substring(1, jsonData.length() - 1);
            
            // Split into rows
            String[] rowsJson = splitJsonArray(jsonData);
            String[][] result = new String[rowsJson.length][];
            
            for (int i = 0; i < rowsJson.length; i++) {
                String rowJson = rowsJson[i];
                // Remove brackets from row
                rowJson = rowJson.substring(1, rowJson.length() - 1);
                
                // Split into cells
                String[] cellsJson = splitJsonArray(rowJson);
                result[i] = new String[cellsJson.length];
                
                for (int j = 0; j < cellsJson.length; j++) {
                    String cellJson = cellsJson[j];
                    // Remove quotes from cell value
                    if (cellJson.startsWith("\"") && cellJson.endsWith("\"")) {
                        cellJson = cellJson.substring(1, cellJson.length() - 1);
                    }
                    result[i][j] = cellJson;
                }
            }
            
            return result;
        } catch (Exception e) {
            System.err.println("Error parsing table data: " + e.getMessage());
            e.printStackTrace();
            return new String[][] {{""}};
        }
    }
    
    // Helper method to split JSON array elements correctly (handling nested quotes)
    private String[] splitJsonArray(String jsonArrayContent) {
        java.util.List<String> elements = new java.util.ArrayList<>();
        int depth = 0;
        boolean inQuotes = false;
        StringBuilder currentElement = new StringBuilder();
        
        for (int i = 0; i < jsonArrayContent.length(); i++) {
            char c = jsonArrayContent.charAt(i);
            
            if (c == '"' && (i == 0 || jsonArrayContent.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
                currentElement.append(c);
            } else if (c == '[' && !inQuotes) {
                depth++;
                currentElement.append(c);
            } else if (c == ']' && !inQuotes) {
                depth--;
                currentElement.append(c);
            } else if (c == ',' && depth == 0 && !inQuotes) {
                elements.add(currentElement.toString().trim());
                currentElement = new StringBuilder();
            } else {
                currentElement.append(c);
            }
        }
        
        if (currentElement.length() > 0) {
            elements.add(currentElement.toString().trim());
        }
        
        return elements.toArray(new String[0]);
    }
    
    public void setData(String[][] data) {
        if (data == null) {
            this.tableData = "[[\"\"]]";
            return;
        }
        
        try {
            StringBuilder json = new StringBuilder();
            json.append("[");
            
            for (int i = 0; i < data.length; i++) {
                if (i > 0) json.append(",");
                json.append("[");
                
                for (int j = 0; j < data[i].length; j++) {
                    if (j > 0) json.append(",");
                    String cellValue = data[i][j];
                    if (cellValue == null) cellValue = "";
                    
                    // Escape quotes and special characters
                    cellValue = cellValue.replace("\"", "\\\"");
                    json.append("\"").append(cellValue).append("\"");
                }
                
                json.append("]");
            }
            
            json.append("]");
            this.tableData = json.toString();
        } catch (Exception e) {
            System.err.println("Error serializing table data: " + e.getMessage());
            e.printStackTrace();
            this.tableData = "[[\"\"]]";
        }
    }
}
