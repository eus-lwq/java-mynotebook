package com.notebook.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "graphs")
public class Graph {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private DataTable table;

    @Enumerated(EnumType.STRING)
    private GraphType type;

    @Column(columnDefinition = "JSON")
    private String config;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum GraphType {
        LINE, BAR, PIE
    }
    
    public Long getTableId() {
        return table != null ? table.getId() : null;
    }
    
    // Helper method to ensure table.getId() works even if table is null
    public void setTableId(Long tableId) {
        // This is a helper method for Lombok
        // Actual implementation would be handled by the service layer
    }
}
