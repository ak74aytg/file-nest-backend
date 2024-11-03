package com.backend.fileNest.repository;

import com.backend.fileNest.model.Document;

import java.util.Date;
import java.util.List;

public interface CustomDocumentRepository {
    List<Document> searchDocuments(String searchText, String fileType, Date startDate, Date endDate, List<String> tags);
}
