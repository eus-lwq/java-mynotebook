package com.notebook.service;

import com.notebook.dto.NotebookDto;
import com.notebook.model.Notebook;
import com.notebook.model.User;
import com.notebook.repository.NotebookRepository;
import com.notebook.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Development version of the NotebookService that uses a default test user
 * instead of requiring authentication
 */
@Service
@Profile("dev")
@Primary
public class DevNotebookService extends NotebookService {
    // Default test user for development
    private User defaultUser;
    
    public DevNotebookService(NotebookRepository notebookRepository, UserRepository userRepository) {
        super(notebookRepository, userRepository);
        this.initDefaultUser();
    }
    
    /**
     * Initialize the default user for development
     */
    @Transactional
    public void initDefaultUser() {
        // Check if default user exists, if not create one
        if (defaultUser == null) {
            defaultUser = getUserRepository().findByUsername("dev_user")
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername("dev_user");
                    newUser.setPasswordHash("$2a$10$dL4az.3o9Z6jFIVL0eoT3.HK9EVY9D9.QMJRgNK.nFX9fyL5/5Bqa"); // "password"
                    newUser.setRole(User.Role.USER);
                    return getUserRepository().save(newUser);
                });
        }
    }
    
    @Override
    public List<NotebookDto> getAllNotebooks() {
        initDefaultUser();
        return notebookRepository.findByUserId(defaultUser.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public NotebookDto getNotebook(Long id) {
        initDefaultUser();
        Notebook notebook = notebookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notebook not found with id: " + id));
        // No ownership validation in dev mode
        return convertToDto(notebook);
    }

    @Override
    @Transactional
    public NotebookDto createNotebook(String title) {
        initDefaultUser();
        
        Notebook notebook = new Notebook();
        notebook.setTitle(title);
        notebook.setUser(defaultUser);
        
        notebook = notebookRepository.save(notebook);
        return convertToDto(notebook);
    }

    @Override
    @Transactional
    public NotebookDto updateNotebook(Long id, String title) {
        initDefaultUser();
        Notebook notebook = notebookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notebook not found with id: " + id));
        // No ownership validation in dev mode
        
        notebook.setTitle(title);
        notebook = notebookRepository.save(notebook);
        return convertToDto(notebook);
    }

    @Override
    @Transactional
    public void deleteNotebook(Long id) {
        initDefaultUser();
        Notebook notebook = notebookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notebook not found with id: " + id));
        // No ownership validation in dev mode
        
        notebookRepository.delete(notebook);
    }

    private NotebookDto convertToDto(Notebook notebook) {
        return NotebookDto.builder()
                .id(notebook.getId())
                .title(notebook.getTitle())
                .createdAt(notebook.getCreatedAt())
                .build();
    }
}
