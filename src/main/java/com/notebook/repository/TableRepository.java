package com.notebook.repository;

import com.notebook.model.DataTable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TableRepository extends JpaRepository<DataTable, Long> {
    List<DataTable> findByPageId(Long pageId);
}
