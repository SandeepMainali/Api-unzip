package org.example.api.repo;

import org.example.api.Entity.Pdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Pdfrepo extends JpaRepository<Pdf,Integer> {

    Pdf findAllByFilename(String filename);

    @Query("SELECT p.filename FROM Pdf p")
    List<String> findAllFilenames();

    @Query("SELECT p FROM Pdf p WHERE p.content = :content")
    Pdf findByContent(@Param("content") byte[] content);


}
