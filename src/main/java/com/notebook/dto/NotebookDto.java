package com.notebook.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotebookDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private List<PageDto> pages;
}
