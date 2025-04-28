package com.notebook.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphDto {
    private Long id;
    private String type;
    private Long tableId;
    private Object config;
    private LocalDateTime createdAt;
}
