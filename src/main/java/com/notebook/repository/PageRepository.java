package com.notebook.repository;

import com.notebook.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {
    List<Page> findByNotebookId(Long notebookId);
}
