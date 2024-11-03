package com.backend.fileNest.repository;

import com.backend.fileNest.model.Document;
import com.backend.fileNest.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends MongoRepository<Document, String>, CustomDocumentRepository {
    List<Document> findByUploadedBy(User user);

    Optional<Document> findByUploadedByAndId(User user, String fileId);


}
