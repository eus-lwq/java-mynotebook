package com.notebook.controller;

import com.notebook.dto.GraphDto;
import com.notebook.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GraphController {
    private final GraphService graphService;

    @PostMapping("/api/pages/{pageId}/graphs")
    public ResponseEntity<GraphDto> createGraph(
            @PathVariable Long pageId,
            @RequestBody GraphDto request) {
        return ResponseEntity.ok(graphService.createGraph(
            pageId,
            request.getType(),
            request.getTableId(),
            request.getConfig()
        ));
    }

    @GetMapping("/api/graphs/{id}")
    public ResponseEntity<GraphDto> getGraph(@PathVariable Long id) {
        return ResponseEntity.ok(graphService.getGraph(id));
    }

    @PutMapping("/api/graphs/{id}")
    public ResponseEntity<GraphDto> updateGraph(
            @PathVariable Long id,
            @RequestBody GraphDto request) {
        return ResponseEntity.ok(graphService.updateGraph(
            id,
            request.getType(),
            request.getTableId(),
            request.getConfig()
        ));
    }

    @DeleteMapping("/api/graphs/{id}")
    public ResponseEntity<Void> deleteGraph(@PathVariable Long id) {
        graphService.deleteGraph(id);
        return ResponseEntity.ok().build();
    }
}
