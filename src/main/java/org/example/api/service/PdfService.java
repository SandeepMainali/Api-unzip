package org.example.api.service;

import jakarta.transaction.Transactional;
import org.example.api.Entity.Pdf;
import org.example.api.repo.Pdfrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
@Service
public class PdfService {
        @Autowired
        Pdfrepo pdfrepo;
    public void unzipAndSaveToDatabase(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
             ZipInputStream zis = new ZipInputStream(bis)) {

            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (!zipEntry.isDirectory() && zipEntry.getName().endsWith(".pdf")) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    byte[] pdfBytes = outputStream.toByteArray();
                    outputStream.close();

                    Pdf pdf = new Pdf();
                    pdf.setFolderName(getFolderName(zipEntry.getName())); // Extract folder name
                    pdf.setFilename(getFilename(zipEntry.getName())); // Extract filename
                    pdf.setFilename(truncateFilename(getFilename(zipEntry.getName())));
                    pdf.setExtension(extractExtension(pdf.getFilename()));
                    pdf.setCreateAt(LocalDateTime.now());
                    pdf.setContent(pdfBytes);
                    pdfrepo.save(pdf);
                }
            }
        }
    }


    private String getFolderName(String path) {
        String[] parts = path.split("/");
        return parts.length > 1 ? parts[0] : ""; // Assuming folder name is before the first '/' in the path
    }
//        private boolean isPdfFile(String fileName) {
//            return fileName.toLowerCase().endsWith(".pdf");
//        }

//        private byte[] readEntryContent(ZipInputStream zis) throws IOException {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            int len;
//            while ((len = zis.read(buffer)) > 0) {
//                bos.write(buffer, 0, len);
//            }
//            return bos.toByteArray();
//        }

        private String getFilename(String filePath) {
            int lastSlashIndex = filePath.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                return filePath.substring(lastSlashIndex + 1);
            } else {
                return filePath;
            }
        }


    private String truncateFilename(String filename) {
        if (filename.length() > 18) {
            return filename.substring(0, 18);
        }
        return filename;
    }

    private String extractExtension(String filename) {
        return ".pdf";
    }

//    @Transactional
//    public void savePdfAsProduct(String filename, byte[] fileContent) {
//        Pdf existingPdf = pdfrepo.findAllByFilename(filename);
//        if (existingPdf == null) {
//            Pdf pdf = new Pdf();
//            String truncatedFilename = truncateFilename(filename);
//            pdf.setFilename(truncatedFilename);
//            pdf.setExtension(extractExtension(truncatedFilename));
//            pdf.setFolderName(getFolderName(filename));
//            pdf.setContent(fileContent);
//            pdf.setCreateAt(LocalDateTime.now());
//            pdfrepo.save(pdf);
//        }
//    }

        @Transactional
        public byte[] getPdfContent(String filename) {
            Pdf p = pdfrepo.findAllByFilename(filename);
            if (p != null) {
                return p.getContent();
            } else {
                return null;
            }
        }
        public List<Pdf> getAllPdf() {
            return this.pdfrepo.findAll();
        }
        public List<String> getAllPdfFilenames() {
            return pdfrepo.findAllFilenames();
        }

}






