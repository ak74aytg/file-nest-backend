package com.backend.fileNest.repository;

import com.backend.fileNest.model.Document;
import com.backend.fileNest.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<Document, String> {
    List<Document> findByUploadedBy(User user);
}
