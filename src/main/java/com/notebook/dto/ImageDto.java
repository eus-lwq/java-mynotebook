package com.notebook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
    private Long id;
    private Long pageId;
    private String fileName;
    private String contentType;
    private LocalDateTime createdAt;
    private byte[] imageData;
}
