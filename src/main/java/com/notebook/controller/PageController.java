package com.notebook.controller;

import com.notebook.dto.PageDto;
import com.notebook.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PageController {
    private final PageService pageService;

    @GetMapping("/api/notebooks/{notebookId}/pages")
    public ResponseEntity<List<PageDto>> getPagesInNotebook(@PathVariable Long notebookId) {
        return ResponseEntity.ok(pageService.getPagesInNotebook(notebookId));
    }

    @PostMapping("/api/notebooks/{notebookId}/pages")
    public ResponseEntity<PageDto> createPage(
            @PathVariable Long notebookId,
            @RequestBody PageDto request) {
        return ResponseEntity.ok(pageService.createPage(
            notebookId,
            request.getTitle(),
            request.getContent()
        ));
    }

    @GetMapping("/api/pages/{id}")
    public ResponseEntity<PageDto> getPage(@PathVariable Long id) {
        return ResponseEntity.ok(pageService.getPage(id));
    }

    @PutMapping("/api/pages/{id}")
    public ResponseEntity<PageDto> updatePage(
            @PathVariable Long id,
            @RequestBody PageDto request) {
        return ResponseEntity.ok(pageService.updatePage(
            id,
            request.getTitle(),
            request.getContent()
        ));
    }

    @DeleteMapping("/api/pages/{id}")
    public ResponseEntity<Void> deletePage(@PathVariable Long id) {
        pageService.deletePage(id);
        return ResponseEntity.ok().build();
    }
}
