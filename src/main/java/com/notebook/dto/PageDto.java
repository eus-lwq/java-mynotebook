package com.notebook.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private List<TableDto> tables;
    private List<GraphDto> graphs;
    private Long notebookId;
}
