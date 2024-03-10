//package org.example.api.Controller;
//import org.example.api.Entity.Pdf;
//import org.example.api.service.PdfService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import java.util.List;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//
//
//@Controller
//public class ProductController {
//
//    //  @Autowired
//
//
//    @Autowired
//    PdfService pdfService;
//
//    @PostMapping("/upload")
//    public String handleUpload(@RequestParam("file") MultipartFile file,
//                               Model model) {
//        if (file.isEmpty()) {
//            return "redirect:/list"; // No file uploaded, redirect to list page
//        }
//
//        try {
//            pdfService.unzipAndSaveToDatabase(file.getBytes()); // Unzip and save file contents
//            return "redirect:/list"; // Success, redirect to list page
//        } catch (IOException e) {
//            return "redirect:/list"; // Error occurred, redirect to list page
//        }
//    }
//
//
//
//
//    @GetMapping("/list")
//    public String getAllPdf(Model model, @RequestParam(value = "success", required = false) Boolean success) {
//        try {
//            List<Pdf> pdfs = pdfService.getAllPdf();
//            model.addAttribute("combinedList", pdfs);
//            if (success != null) {
//                model.addAttribute("message", success ? "Upload successful." : "Upload failed. Please try again.");
//            }
//            return "index";
//        } catch (Exception e) {
//            model.addAttribute("error", "Failed to retrieve PDFs: " + e.getMessage());
//            return "index";
//        }
//    }
//
//
//
//    @GetMapping("/view-pdf/{filename}")
//    public ResponseEntity<ByteArrayResource> viewPdf(@PathVariable String filename) {
//        byte[] pdfContent = pdfService.getPdfContent(filename);
//
//        if (pdfContent != null) {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_PDF);
//            headers.setContentDispositionFormData("attachment", filename + ".pdf");
//            ByteArrayResource resource = new ByteArrayResource(pdfContent);
//            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//
//    }
//
//
//
//
//
//
//
//}
//
//
