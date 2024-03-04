package org.example.api.Controller;
import org.example.api.Entity.Pdf;
import org.example.api.Entity.Product;
import org.example.api.helper.Helper;
import org.example.api.service.PdfService;
import org.example.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;


@Controller
public class ProductController {

    //  @Autowired
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
     PdfService pdfService;

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file,
                               @RequestParam(value = "sheetNames", required = false) List<String> sheetNames,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload.");
            return "redirect:/list?success=false";
        }

        try {
            if (Helper.checkExcelFormat(file)) {
                if (sheetNames != null && !sheetNames.isEmpty()) {
                    productService.save(file, sheetNames);
                } else {
                    productService.save(file, Helper.getAllSheetNames(file));
                }
                redirectAttributes.addFlashAttribute("message", "File is uploaded and data is saved to db");
                return "redirect:/list?success=true";
            } else {
                pdfService.unzipAndSaveToDatabase(file.getBytes());
                redirectAttributes.addFlashAttribute("message", "File has been successfully unzipped and saved to the database.");
                return "redirect:/list?success=true";
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Failed to process the file: " + e.getMessage());
            return "redirect:/list?success=false";
        }
    }

    @GetMapping("/list")
    public String getAllProduct(Model model, @RequestParam(value = "success", required = false) Boolean success) {
        try {
            List<Product> products = productService.getAllProducts();
            List<Pdf> pdfs = pdfService.getAllPdf();
            List<Object> combinedList = new ArrayList<>();
            combinedList.addAll(products);
            combinedList.addAll(pdfs);
            model.addAttribute("combinedList", combinedList);
            if (success != null) {
                if (success) {
                    model.addAttribute("message", "Upload successful.");
                } else {
                    model.addAttribute("message", "Upload failed. Please try again.");
                }
            }
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve products: " + e.getMessage());
            return "index";
        }
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

//   @GetMapping("/all")
//    public ResponseEntity<?> getProducts(@RequestParam(required = false) String sheetName) {
//        if (sheetName != null) {
//            List<Product> products = productService.getAllProductsBySheetName(sheetName);
//            if (!products.isEmpty()) {
//                return ResponseEntity.ok(products);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } else {
//            return ResponseEntity.badRequest().body("Sheet name parameter is required.");
//        }
//    }

//    @DeleteMapping("/alls")
//    public ResponseEntity<?> deleteProductsBySheetName(@RequestParam(required = false) String sheetName) {
//        if (sheetName != null) {
//            int deletedCount = productService.deleteAllProductsBySheetName(sheetName);
//
//            if (deletedCount > 0) {
//                return ResponseEntity.ok().body("Deleted " + deletedCount + " products for the provided sheet name: " + sheetName);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } else {
//            return ResponseEntity.badRequest().body("Sheet name parameter is required.");
//        }
//    }



//
//    @GetMapping("/products")
//    public ResponseEntity<List<Product>> getAllProduct() {
//        List<Product> products = productService.getAllProducts();
//        return ResponseEntity.ok().body(products);
//    }




//    @GetMapping("/filenames")
//    public ResponseEntity<List<String>> getAllPdfFilenames() {
//        List<String> filenames = pdfService.getAllPdfFilenames();
//        return ResponseEntity.ok().body(filenames);
//    }





}


