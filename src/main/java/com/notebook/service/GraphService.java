package com.notebook.service;

import com.notebook.dto.GraphDto;
import com.notebook.model.Graph;
import com.notebook.model.Page;
import com.notebook.model.DataTable;
import com.notebook.repository.GraphRepository;
import com.notebook.repository.PageRepository;
import com.notebook.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class GraphService {
    private final GraphRepository graphRepository;
    private final PageRepository pageRepository;
    private final TableRepository tableRepository;
    private final PageService pageService;

    public GraphDto getGraph(Long id) {
        Graph graph = graphRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Graph not found"));
        
        // This will validate ownership through the notebook chain
        pageService.getPage(graph.getPage().getId());
        
        return convertToDto(graph);
    }

    @Transactional
    public GraphDto createGraph(Long pageId, String type, Long tableId, Object config) {
        // This will validate ownership
        pageService.getPage(pageId);
        
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new EntityNotFoundException("Page not found"));
        
        DataTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Table not found"));
        
        // Validate that the table belongs to the same page
        if (!table.getPage().getId().equals(pageId)) {
            throw new IllegalArgumentException("Table does not belong to this page");
        }
        
        Graph graph = new Graph();
        graph.setPage(page);
        graph.setTable(table);
        graph.setType(Graph.GraphType.valueOf(type.toUpperCase()));
        graph.setConfig(config.toString());
        
        graph = graphRepository.save(graph);
        return convertToDto(graph);
    }

    @Transactional
    public GraphDto updateGraph(Long id, String type, Long tableId, Object config) {
        Graph graph = graphRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Graph not found"));
        
        // This will validate ownership through the notebook chain
        pageService.getPage(graph.getPage().getId());
        
        if (tableId != null && !tableId.equals(graph.getTable().getId())) {
            DataTable newTable = tableRepository.findById(tableId)
                    .orElseThrow(() -> new EntityNotFoundException("Table not found"));
            
            // Validate that the new table belongs to the same page
            if (!newTable.getPage().getId().equals(graph.getPage().getId())) {
                throw new IllegalArgumentException("Table does not belong to this page");
            }
            
            graph.setTable(newTable);
        }
        
        graph.setType(Graph.GraphType.valueOf(type.toUpperCase()));
        graph.setConfig(config.toString());
        
        graph = graphRepository.save(graph);
        return convertToDto(graph);
    }

    @Transactional
    public void deleteGraph(Long id) {
        Graph graph = graphRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Graph not found"));
        
        // This will validate ownership through the notebook chain
        pageService.getPage(graph.getPage().getId());
        
        graphRepository.delete(graph);
    }

    private GraphDto convertToDto(Graph graph) {
        return new GraphDto(
                graph.getId(),
                graph.getType().toString(),
                graph.getTableId(),
                graph.getConfig(),
                graph.getCreatedAt()
        );
    }
}
