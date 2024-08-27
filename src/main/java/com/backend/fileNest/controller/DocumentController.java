package com.backend.fileNest.controller;

import com.backend.fileNest.response.DocumentResponse;
import com.backend.fileNest.service.DocumentService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {
    @Autowired
    DocumentService documentService;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "tags", required = false) List<String> tags, Principal principal){
        String email = principal.getName();
        if(tags==null || tags.isEmpty()){
            tags = new ArrayList<>();
        }
        if(file==null || file.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("file/file type not supported");
        }
        try {
            String response = documentService.storeFile(file, tags, email);
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

    @DeleteMapping
    public ResponseEntity<String> deleteFile(@RequestParam("file_id") String file_id, Principal principal){
        String email = principal.getName();
        try {
            return ResponseEntity.ok(documentService.deleteDocument(file_id, email));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{file_id}")
    @ResponseBody
    @PermitAll
    public ResponseEntity<?> getFile(@PathVariable("file_id") String file_id, Principal principal){
        try {
            Resource file = documentService.loadFileAsResource(file_id, principal.getName());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/tag")
    public ResponseEntity<?> getFilesByTags(@RequestBody List<String> tags, Principal principal){
        String email = principal.getName();
        List<DocumentResponse> response = documentService.getDocumentByTags(tags, email);
        if(response.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No documents found!");
        }
        return ResponseEntity.ok(response);
    }
}
