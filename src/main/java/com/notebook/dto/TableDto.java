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
public class TableDto {
    private Long id;
    private String[][] data;
    private LocalDateTime createdAt;
}
