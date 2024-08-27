package com.backend.fileNest.service;

import com.backend.fileNest.model.User;
import com.backend.fileNest.response.DocumentResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface DocumentService {
    List<DocumentResponse> getAllDocument(String email);
    Resource loadFileAsResource(String fileId, String email);
    String storeFile(MultipartFile file, List<String> tags, String email);

    String deleteDocument(String fileId, String email);

    List<DocumentResponse> getDocumentByTags(List<String> tags, String email);
}
