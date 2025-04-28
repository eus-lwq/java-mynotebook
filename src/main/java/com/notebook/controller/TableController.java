package com.notebook.controller;

import com.notebook.dto.TableDto;
import com.notebook.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TableController {
    private final TableService tableService;

    @PostMapping("/api/pages/{pageId}/tables")
    public ResponseEntity<TableDto> createTable(
            @PathVariable Long pageId,
            @RequestBody TableDto request) {
        return ResponseEntity.ok(tableService.createTable(pageId, request.getData()));
    }

    @GetMapping("/api/tables/{id}")
    public ResponseEntity<TableDto> getTable(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.getTable(id));
    }

    @PutMapping("/api/tables/{id}")
    public ResponseEntity<TableDto> updateTable(
            @PathVariable Long id,
            @RequestBody TableDto request) {
        return ResponseEntity.ok(tableService.updateTable(id, request.getData()));
    }

    @DeleteMapping("/api/tables/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.ok().build();
    }
}
