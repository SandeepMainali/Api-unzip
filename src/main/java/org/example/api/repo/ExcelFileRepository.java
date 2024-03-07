package org.example.api.repo;

import org.example.api.Entity.ExcelFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExcelFileRepository extends JpaRepository<ExcelFile, Long> {
    // Add custom query methods if needed
    Optional<ExcelFile> findByFilename(String filename);
}

