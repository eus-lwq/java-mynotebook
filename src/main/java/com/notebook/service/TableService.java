package com.notebook.service;

import com.notebook.dto.TableDto;
import com.notebook.model.DataTable;
import com.notebook.model.Page;
import com.notebook.repository.PageRepository;
import com.notebook.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository;
    private final PageRepository pageRepository;
    private final PageService pageService;

    public TableDto getTable(Long id) {
        DataTable table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with id: " + id));
        
        // This will validate ownership through the notebook chain
        pageService.getPage(table.getPage().getId());
        
        return convertToDto(table);
    }

    @Transactional
    public TableDto createTable(Long pageId, String[][] data) {
        // This will validate ownership
        pageService.getPage(pageId);
        
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new EntityNotFoundException("Page not found with id: " + pageId));
        
        DataTable table = new DataTable();
        table.setPage(page);
        table.setData(data);
        
        table = tableRepository.save(table);
        return convertToDto(table);
    }

    @Transactional
    public TableDto updateTable(Long id, String[][] data) {
        DataTable table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table not found"));
        
        // This will validate ownership through the notebook chain
        pageService.getPage(table.getPage().getId());
        
        table.setData(data);
        table = tableRepository.save(table);
        return convertToDto(table);
    }

    @Transactional
    public void deleteTable(Long id) {
        DataTable table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table not found"));
        
        // This will validate ownership through the notebook chain
        pageService.getPage(table.getPage().getId());
        
        tableRepository.delete(table);
    }

    private TableDto convertToDto(DataTable table) {
        return new TableDto(
                table.getId(),
                table.getData(),
                table.getCreatedAt()
        );
    }
}
