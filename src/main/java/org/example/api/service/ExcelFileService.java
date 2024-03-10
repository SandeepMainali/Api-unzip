package org.example.api.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.api.Entity.ExcelFile;
import org.example.api.repo.ExcelFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelFileService {

    @Autowired
    private ExcelFileRepository excelFileRepository;

    public void saveFile(String filename, MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();

        // Parse Excel file
        Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(fileBytes));
        Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

        // Read and save data from Excel file
        StringBuilder contentBuilder = new StringBuilder();
        for (Row row : sheet) {
            for (Cell cell : row) {
                String cellValue = getValueAsString(cell);
                contentBuilder.append(cellValue).append(" ");
            }
            contentBuilder.append("\n");
        }

        String content = contentBuilder.toString();

        // Check if file with same filename exists
        Optional<ExcelFile> existingFile = excelFileRepository.findByFilename(filename);
        if (existingFile.isPresent()) {
            throw new RuntimeException("File with the same filename already exists.");
        }

        ExcelFile excelFile = new ExcelFile();
        excelFile.setFilename(filename);
        excelFile.setSheetdata(fileBytes);
//        excelFile.setContent(content);

        excelFileRepository.save(excelFile);
    }

    private String getValueAsString(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

    public List<ExcelFile> getAllFiles() {
        return excelFileRepository.findAll();
    }
    public List<List<String>> getSheetDataByFilename(String filename) {
        List<List<String>> sheetData = new ArrayList<>();
        ExcelFile excelFile = excelFileRepository.findByFilename(filename).orElse(null);
        if (excelFile != null) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(excelFile.getSheetdata());
                 Workbook workbook = new XSSFWorkbook(bis)) {

                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    List<String> rowData = new ArrayList<>();
                    for (Cell cell : row) {
                        rowData.add(getValueAsString(cell));
                    }
                    sheetData.add(rowData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sheetData;
    }

    public void saveExcelFile(String filename, byte[] sheetData) {
        // Create an instance of ExcelFile entity
        ExcelFile excelFile = new ExcelFile();
        excelFile.setFilename(filename);
        excelFile.setSheetdata(sheetData);

        // Save the ExcelFile entity using the repository
        excelFileRepository.save(excelFile);
    }




//    public List<ExcelFile> getAllExcelFiles() {
//        return excelFileRepository.findAll();
//    }

    public List<String> getAllFilenames() {
        List<ExcelFile> excelFiles = excelFileRepository.findAll();
        List<String> filenames = new ArrayList<>();
        for (ExcelFile excelFile : excelFiles) {
            filenames.add(excelFile.getFilename());
        }
        return filenames;
    }

}