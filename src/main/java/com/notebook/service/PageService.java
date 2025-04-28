package com.notebook.service;

import com.notebook.dto.PageDto;
import com.notebook.model.Notebook;
import com.notebook.model.Page;
import com.notebook.repository.NotebookRepository;
import com.notebook.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import com.notebook.dto.TableDto;
import com.notebook.dto.GraphDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    private final NotebookRepository notebookRepository;
    private final NotebookService notebookService;

    public List<PageDto> getPagesInNotebook(Long notebookId) {
        // This will validate ownership
        notebookService.getNotebook(notebookId);
        
        return pageRepository.findByNotebookId(notebookId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PageDto getPage(Long id) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Page not found"));
        
        // This will validate ownership
        notebookService.getNotebook(page.getNotebook().getId());
        
        return convertToDto(page);
    }

    @Transactional
    public PageDto createPage(Long notebookId, String title, String content) {
        Notebook notebook = notebookRepository.findById(notebookId)
                .orElseThrow(() -> new EntityNotFoundException("Notebook not found"));
        
        // This will validate ownership
        notebookService.getNotebook(notebookId);
        
        Page page = new Page();
        page.setNotebook(notebook);
        page.setTitle(title);
        page.setContent(content);
        
        page = pageRepository.save(page);
        return convertToDto(page);
    }

    @Transactional
    public PageDto updatePage(Long id, String title, String content) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Page not found"));
        
        // This will validate ownership
        notebookService.getNotebook(page.getNotebook().getId());
        
        page.setTitle(title);
        page.setContent(content);
        
        page = pageRepository.save(page);
        return convertToDto(page);
    }

    @Transactional
    public void deletePage(Long id) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Page not found"));
        
        // This will validate ownership
        notebookService.getNotebook(page.getNotebook().getId());
        
        pageRepository.delete(page);
    }

    private PageDto convertToDto(Page page) {
        // Initialize empty lists for tables and graphs
        List<TableDto> tableDtos = new ArrayList<>();
        List<GraphDto> graphDtos = new ArrayList<>();
        
        // Safely convert tables if they exist
        if (page.getTables() != null) {
            try {
                tableDtos = page.getTables().stream()
                    .map(table -> new TableDto(
                        table.getId(),
                        table.getData(),
                        table.getCreatedAt()))
                    .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Error converting tables: " + e.getMessage());
                // Continue with empty tables list
            }
        }
        
        // Safely convert graphs if they exist
        if (page.getGraphs() != null) {
            try {
                graphDtos = page.getGraphs().stream()
                    .map(graph -> new GraphDto(
                        graph.getId(),
                        graph.getType().toString(),
                        graph.getTableId(),
                        graph.getConfig(),
                        graph.getCreatedAt()))
                    .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Error converting graphs: " + e.getMessage());
                // Continue with empty graphs list
            }
        }
        
        return PageDto.builder()
                .id(page.getId())
                .title(page.getTitle())
                .content(page.getContent())
                .createdAt(page.getCreatedAt())
                .tables(tableDtos)
                .graphs(graphDtos)
                .notebookId(page.getNotebook().getId())
                .build();
    }
}
