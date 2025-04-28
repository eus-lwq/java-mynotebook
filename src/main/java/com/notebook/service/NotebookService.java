package com.notebook.service;

import com.notebook.dto.NotebookDto;
import com.notebook.model.Notebook;
import com.notebook.model.User;
import com.notebook.repository.NotebookRepository;
import com.notebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotebookService {
    private final NotebookRepository notebookRepository;
    private final UserRepository userRepository;

    public List<NotebookDto> getAllNotebooks() {
        User currentUser = getCurrentUser();
        return notebookRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public NotebookDto getNotebook(Long id) {
        Notebook notebook = notebookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notebook not found with id: " + id));
        validateOwnership(notebook);
        return convertToDto(notebook);
    }

    @Transactional
    public NotebookDto createNotebook(String title) {
        User currentUser = getCurrentUser();
        
        Notebook notebook = new Notebook();
        notebook.setTitle(title);
        notebook.setUser(currentUser);
        
        notebook = notebookRepository.save(notebook);
        return convertToDto(notebook);
    }

    @Transactional
    public NotebookDto updateNotebook(Long id, String title) {
        Notebook notebook = notebookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notebook not found with id: " + id));
        validateOwnership(notebook);
        
        notebook.setTitle(title);
        notebook = notebookRepository.save(notebook);
        return convertToDto(notebook);
    }

    @Transactional
    public void deleteNotebook(Long id) {
        Notebook notebook = notebookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notebook not found with id: " + id));
        validateOwnership(notebook);
        
        notebookRepository.delete(notebook);
    }

    private NotebookDto convertToDto(Notebook notebook) {
        return NotebookDto.builder()
                .id(notebook.getId())
                .title(notebook.getTitle())
                .createdAt(notebook.getCreatedAt())
                .build();
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private void validateOwnership(Notebook notebook) {
        User currentUser = getCurrentUser();
        if (!notebook.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("Not authorized to access this notebook");
        }
    }
}
