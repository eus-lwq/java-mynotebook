package com.notebook.service;

import com.notebook.dto.ImageDto;
import com.notebook.model.Image;
import com.notebook.model.Page;
import com.notebook.repository.ImageRepository;
import com.notebook.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final PageRepository pageRepository;
    private final PageService pageService;

    public List<ImageDto> getImagesForPage(Long pageId) {
        // This will validate ownership
        pageService.getPage(pageId);
        
        return imageRepository.findByPageId(pageId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ImageDto getImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + id));
        
        // This will validate ownership through the notebook chain
        pageService.getPage(image.getPage().getId());
        
        return convertToDto(image);
    }

    @Transactional
    public ImageDto saveImage(Long pageId, byte[] imageData, String fileName, String contentType) {
        // This will validate ownership
        pageService.getPage(pageId);
        
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new EntityNotFoundException("Page not found with id: " + pageId));
        
        Image image = new Image();
        image.setPage(page);
        image.setImageData(imageData);
        image.setFileName(fileName);
        image.setContentType(contentType);
        
        image = imageRepository.save(image);
        return convertToDto(image);
    }

    @Transactional
    public void deleteImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));
        
        // This will validate ownership through the notebook chain
        pageService.getPage(image.getPage().getId());
        
        imageRepository.delete(image);
    }

    private ImageDto convertToDto(Image image) {
        return ImageDto.builder()
                .id(image.getId())
                .pageId(image.getPage().getId())
                .fileName(image.getFileName())
                .contentType(image.getContentType())
                .createdAt(image.getCreatedAt())
                .imageData(image.getImageData())
                .build();
    }
}
