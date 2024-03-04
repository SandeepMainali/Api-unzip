package org.example.api.repo;

import jakarta.transaction.Transactional;
import org.example.api.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product,Integer> {
//    Product findAllByFilename(String filename);


//    @Modifying
//    @Transactional
//    @Query("DELETE FROM Product p WHERE p.sheetName = :sheetName")
//    int deleteBySheetName(@Param("sheetName") String sheetName);
    List<Product> findAllBySheetName(String sheetName);




}
