package com.backend.fileNest.controller;

import com.backend.fileNest.response.DocumentResponse;
import com.backend.fileNest.response.TagsResponse;
import com.backend.fileNest.service.DocumentService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
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
        List<DocumentResponse> documents = documentService.getAllDocument(email);
        Collections.reverse(documents); // Reverse the list
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteFile(@RequestParam("file_id") String file_id, Principal principal){
        String email = principal.getName();
        System.out.println(email);
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


    @GetMapping("/uri/{file_id}")
    public ResponseEntity<String> getFileUri(@PathVariable("file_id") String file_id, Principal principal) {
        try {
            String uri = documentService.getFileUri(file_id, principal.getName());
            return ResponseEntity.ok(uri);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    @GetMapping("/{file_id}/view")
    @ResponseBody
    @PermitAll
    public ResponseEntity<?> viewFile(@PathVariable("file_id") String file_id, Principal principal) {
        try {
            Resource file = documentService.loadFileAsResource(file_id, principal.getName());

            // Determine the content type based on the file extension
            String contentType = Files.probeContentType(Paths.get(file.getURI()));
            if (contentType == null) {
                // Fallback for specific file types
                String filename = file.getFilename().toLowerCase();
                if (filename.endsWith(".csv")) {
                    contentType = "text/csv";
                } else if (filename.endsWith(".xml")) {
                    contentType = "application/xml";
                } else if (filename.endsWith(".txt")) {
                    contentType = "text/plain";
                } else if (filename.endsWith(".pdf")) {
                    contentType = "application/pdf";
                } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (filename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (filename.endsWith(".ppt") || filename.endsWith(".pptx")) {
                    contentType = "application/vnd.ms-powerpoint";
                } else if (filename.endsWith(".doc") || filename.endsWith(".docx")) {
                    contentType = "application/msword";
                } else if (filename.endsWith(".xls") || filename.endsWith(".xlsx")) {
                    contentType = "application/vnd.ms-excel";
                } else{
                    contentType = "application/octet-stream";  // Default to binary data if unknown
                }
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TagsResponse>> getAllTags(Principal principal){
        //send the top four most used tags and put the remaining files with tag others
        String email = principal.getName();
        List<TagsResponse> tags = documentService.getAllTags(email);
        return ResponseEntity.ok(tags);
    }

    @PutMapping("/ocr")
    public ResponseEntity<String> doOCR(@RequestParam("file_id") String file_id, Principal principal){
        String email = principal.getName();
        try {
            String response = documentService.extractTexts(file_id, email);
            if (response.equals("completed")){
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("done");
            }
            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(e.getMessage());
        }
    }
}
