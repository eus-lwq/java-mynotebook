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
        // In a real implementation, this would parse the JSON tableData
        // For now, returning a simple placeholder
        return new String[][] {{""}};  
    }
    
    public void setData(String[][] data) {
        // In a real implementation, this would convert the 2D array to JSON
        // and set it to tableData
        this.tableData = "[[]]"; // Simple placeholder
    }
}
