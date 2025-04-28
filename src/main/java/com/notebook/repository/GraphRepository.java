package com.notebook.repository;

import com.notebook.model.Graph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface GraphRepository extends JpaRepository<Graph, Long> {
    List<Graph> findByPageId(Long pageId);
    
    // Use the proper relationship path instead of direct tableId field
    List<Graph> findByTable_Id(Long tableId);
    
    // Alternative JPQL query approach
    @Query("SELECT g FROM Graph g WHERE g.table.id = :tableId")
    List<Graph> findByTableIdCustom(@Param("tableId") Long tableId);
}
