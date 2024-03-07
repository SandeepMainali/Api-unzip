package org.example.api.Controller;

import org.example.api.Entity.ExcelFile;
import org.example.api.service.ExcelFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api")
public class ExcelFileController {

    @Autowired
    private ExcelFileService excelFileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            excelFileService.saveFile(file.getOriginalFilename(), file);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }

    @GetMapping("/excelfiles")
    public String listExcelFiles(Model model) {
        List<String> allFilenames = excelFileService.getAllFilenames();
        model.addAttribute("allFilenames", allFilenames);
        return "sheetdata"; // Assuming you have a Thymeleaf template named "sheetdata.html"
    }

    @GetMapping("/sheetdata")
    public String getSheetData(@RequestParam(required = false) String filename, Model model) {
        if (filename != null && !filename.isEmpty()) {
            List<List<String>> sheetData = excelFileService.getSheetDataByFilename(filename);
            model.addAttribute("sheetData", sheetData);
        }
        List<String> allFilenames = excelFileService.getAllFilenames();
        model.addAttribute("allFilenames", allFilenames);
        return "sheetdata"; // Thymeleaf template name
    }



}

