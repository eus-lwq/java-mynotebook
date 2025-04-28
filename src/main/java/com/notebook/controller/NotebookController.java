package com.notebook.controller;

import com.notebook.dto.NotebookDto;
import com.notebook.service.NotebookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notebooks")
@RequiredArgsConstructor
public class NotebookController {
    private final NotebookService notebookService;

    @GetMapping
    public ResponseEntity<List<NotebookDto>> getAllNotebooks() {
        return ResponseEntity.ok(notebookService.getAllNotebooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotebookDto> getNotebook(@PathVariable Long id) {
        return ResponseEntity.ok(notebookService.getNotebook(id));
    }

    @PostMapping
    public ResponseEntity<NotebookDto> createNotebook(@RequestBody NotebookDto request) {
        return ResponseEntity.ok(notebookService.createNotebook(request.getTitle()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotebookDto> updateNotebook(
            @PathVariable Long id,
            @RequestBody NotebookDto request) {
        return ResponseEntity.ok(notebookService.updateNotebook(id, request.getTitle()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotebook(@PathVariable Long id) {
        notebookService.deleteNotebook(id);
        return ResponseEntity.ok().build();
    }
}
