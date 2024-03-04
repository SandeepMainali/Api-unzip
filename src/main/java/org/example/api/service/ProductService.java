package org.example.api.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.api.Entity.Product;
import org.example.api.helper.Helper;
import org.example.api.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.Collections;
import java.util.List;
@Service
public class ProductService {

    private final ProductRepo productRepo;

    @Autowired
    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }


    public List<Product> getAllProductsBySheetName(String sheetName) {
        return productRepo.findAllBySheetName(sheetName);
    }

    public void save(MultipartFile file, List<String> sheetNames) {
        try {
            Helper helper = new Helper(productRepo);
            for (String sheetName : sheetNames) {
                List<Product> existingProducts = productRepo.findAllBySheetName(sheetName);

                if (!existingProducts.isEmpty()) {
                    System.out.println("Products from sheet '" + sheetName + "' already exist in the database. Skipping.");
                    continue;
                }
                List<Product> products = helper.convertExcelToListOfProduct(file.getInputStream(), Collections.singletonList(sheetName));
                productRepo.saveAll(products);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<Product> getAllProducts() {

        return this.productRepo.findAll();

    }

//    public int deleteAllProductsBySheetName(String sheetName) {
//        return productRepo.deleteBySheetName(sheetName);
//    }

//
//    public static List<String> getAllSheetNames(MultipartFile file) throws IOException {
//        List<String> sheetNames = new ArrayList<>();
//        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
//            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//                sheetNames.add(workbook.getSheetName(i));
//            }
//        }
//        return sheetNames;
//
//

}

