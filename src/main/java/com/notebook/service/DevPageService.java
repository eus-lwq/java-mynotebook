package com.notebook.service;

import com.notebook.dto.PageDto;
import com.notebook.model.Notebook;
import com.notebook.model.Page;
import com.notebook.repository.NotebookRepository;
import com.notebook.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Development version of the PageService that doesn't require authentication
 */
@Service
@RequiredArgsConstructor
@Profile("dev")
@Primary
public class DevPageService extends PageService {
    private final PageRepository pageRepository;
    private final NotebookRepository notebookRepository;
    private final DevNotebookService devNotebookService;

    @Override
    public List<PageDto> getPagesInNotebook(Long notebookId) {
        // No ownership validation in dev mode
        return pageRepository.findByNotebookId(notebookId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PageDto getPage(Long id) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Page not found"));
        
        // No ownership validation in dev mode
        
        return convertToDto(page);
    }

    @Override
    @Transactional
    public PageDto createPage(Long notebookId, String title, String content) {
        // Initialize default user
        devNotebookService.initDefaultUser();
        
        Notebook notebook = notebookRepository.findById(notebookId)
                .orElseThrow(() -> new EntityNotFoundException("Notebook not found"));
        
        // No ownership validation in dev mode
        
        Page page = new Page();
        page.setTitle(title);
        page.setContent(content);
        page.setNotebook(notebook);
        
        page = pageRepository.save(page);
        return convertToDto(page);
    }

    @Override
    @Transactional
    public PageDto updatePage(Long id, String title, String content) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Page not found"));
        
        // No ownership validation in dev mode
        
        page.setTitle(title);
        page.setContent(content);
        
        page = pageRepository.save(page);
        return convertToDto(page);
    }

    @Override
    @Transactional
    public void deletePage(Long id) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Page not found"));
        
        // No ownership validation in dev mode
        
        pageRepository.delete(page);
    }

    private PageDto convertToDto(Page page) {
        return PageDto.builder()
                .id(page.getId())
                .title(page.getTitle())
                .content(page.getContent())
                .notebookId(page.getNotebook().getId())
                .createdAt(page.getCreatedAt())
                .build();
    }
}
