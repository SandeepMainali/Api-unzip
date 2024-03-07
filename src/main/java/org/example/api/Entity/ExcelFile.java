package org.example.api.Entity;


import jakarta.persistence.*;

@Entity
 public class ExcelFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] sheetdata;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getSheetdata() {
        return sheetdata;
    }

    public void setSheetdata(byte[] sheetdata) {
        this.sheetdata = sheetdata;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }





    // Getters and setters
}
