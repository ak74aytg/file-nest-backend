package com.backend.fileNest.controller;

import com.backend.fileNest.response.DocumentResponse;
import com.backend.fileNest.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {
    @Autowired
    DocumentService documentService;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, Principal principal){
        String email = principal.getName();
        try {
            String response = documentService.storeFile(file, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllFiles(Principal principal){
        String email = principal.getName();
        return ResponseEntity.ok(documentService.getAllDocument(email));
    }
}
