package com.example.allomaison.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResult {
    private boolean successful;
    private String url;     // successful -> upload URL
    private String error;   // failed -> error message
}
