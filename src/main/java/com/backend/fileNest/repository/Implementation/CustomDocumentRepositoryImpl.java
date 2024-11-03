package com.backend.fileNest.repository.Implementation;

import com.backend.fileNest.model.Document;
import com.backend.fileNest.repository.CustomDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

public class CustomDocumentRepositoryImpl implements CustomDocumentRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Document> searchDocuments(String searchText, String fileType, Date startDate, Date endDate, List<String> tags) {
        Query query = new Query();

        // Search text in title or OCR text (case-insensitive)
        if (searchText != null && !searchText.isEmpty()) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("title").regex(searchText, "i"),
                    Criteria.where("ocr_text").regex(searchText, "i")
            ));
        }

        // File type filter
        if (fileType != null && !fileType.isEmpty()) {
            query.addCriteria(Criteria.where("metaData.file_type").is(fileType));
        }

        // Date range filter
        if (startDate != null && endDate != null) {
            query.addCriteria(Criteria.where("metaData.uploaded_at").gte(startDate).lte(endDate));
        }

        // Tags filter
        if (tags != null && !tags.isEmpty()) {
            query.addCriteria(Criteria.where("metaData.tags").in(tags));
        }

        return mongoTemplate.find(query, Document.class);
    }
}
