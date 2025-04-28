package com.notebook.repository;

import com.notebook.model.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotebookRepository extends JpaRepository<Notebook, Long> {
    List<Notebook> findByUserId(Long userId);
}
