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
    Resource loadFileAsResource(String fileId);
    String storeFile(MultipartFile file, String email) throws Exception;
}
