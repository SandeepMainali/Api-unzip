package org.example.api.Controller;

import org.example.api.Entity.ExcelFile;
import org.example.api.Entity.Pdf;
import org.example.api.service.ExcelFileService;
import org.example.api.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api")
public class ExcelFileController {

    @Autowired
    private ExcelFileService excelFileService;

    @Autowired
    private PdfService pdfService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && (originalFilename.toLowerCase().endsWith(".xlsx") || originalFilename.toLowerCase().endsWith(".xls"))) {
                excelFileService.saveFile(originalFilename, file);
                redirectAttributes.addFlashAttribute("successMessage", "Excel file uploaded successfully.");
            } else if (originalFilename != null && originalFilename.toLowerCase().endsWith(".zip")) {
                pdfService.unzipAndSaveToDatabase(file.getBytes());
                redirectAttributes.addFlashAttribute("successMessage", "PDF file uploaded successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Unsupported file type.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload file.");
        }
        return "redirect:/api/listall";
    }


    @GetMapping("/listall")
    public String listAllFiles(Model model) {
        try {
            // Retrieve all Excel file names
            List<String> allFilenames = excelFileService.getAllFilenames();
            model.addAttribute("allFilenames", allFilenames);

            // Retrieve all PDFs
            List<Pdf> pdfs = pdfService.getAllPdf();
            model.addAttribute("pdfs", pdfs);

            // Retrieve combined list (assuming Pdf has 'createAt', 'folderName', 'filename', and 'extension' fields)
            List<Object> combinedList = new ArrayList<>();
            combinedList.addAll(allFilenames);
            combinedList.addAll(pdfs);
            model.addAttribute("combinedList", combinedList);

            return "sheetdata"; // Assuming you have a Thymeleaf template named "sheetdata.html"
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve files: " + e.getMessage());
            return "sheetdata"; // Thymeleaf template name
        }
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


    @GetMapping("/view-pdf/{filename}")
    public ResponseEntity<ByteArrayResource> viewPdf(@PathVariable String filename) {
        byte[] pdfContent = pdfService.getPdfContent(filename);

        if (pdfContent != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename + ".pdf");
            ByteArrayResource resource = new ByteArrayResource(pdfContent);
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }

    }


}

