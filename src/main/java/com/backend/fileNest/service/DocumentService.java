package com.backend.fileNest.service;

import com.backend.fileNest.model.User;
import com.backend.fileNest.response.DocumentResponse;
import com.backend.fileNest.response.TagsResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface DocumentService {
    List<DocumentResponse> getAllDocument(String email, Integer pageSize, Integer pageNumber);
    Resource loadFileAsResource(String fileId, String email);
    String storeFile(MultipartFile file, List<String> tags, String email);
    String deleteDocument(String fileId, String email);
    String deleteDocuments(List<String> fileIds, String email);
    String getFileUri(String fileId, String email);
    List<DocumentResponse> getDocumentByTags(List<String> tags, String email);
    List<TagsResponse> getAllTags(String email);
    String extractTexts(String fileId, String email);

    String getOcrStatus(String fileId);
}
