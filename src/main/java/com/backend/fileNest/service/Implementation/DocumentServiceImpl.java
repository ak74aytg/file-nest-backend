package com.backend.fileNest.service.Implementation;

import com.backend.fileNest.model.Document;
import com.backend.fileNest.model.MetaData;
import com.backend.fileNest.model.User;
import com.backend.fileNest.repository.DocumentRepository;
import com.backend.fileNest.repository.UserRepository;
import com.backend.fileNest.response.DocumentResponse;
import com.backend.fileNest.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @Override
    public List<DocumentResponse> getAllDocument(String email) {
        User user = userRepository.findByEmail(email);
        List<Document> docs = documentRepository.findByUploadedBy(user);
        List<DocumentResponse> response = new ArrayList<>();
        for (Document doc : docs){
            response.add(DocumentResponse.builder()
                            .file_id(doc.getId())
                            .metaData(doc.getMetaData())
                            .title(doc.getTitle())
                    .build());
        }
        return response;
    }

    @Override
    public Resource loadFileAsResource(String fileId) {
        return null;
    }

    @Override
    public String storeFile(MultipartFile file, String email) throws Exception {
        User user = userRepository.findByEmail(email);
        try {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_DIR+ File.separator+file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
        Document savedDocument = Document.builder()
                .uploadedBy(user)
                .title(file.getOriginalFilename())
                .id(UUID.randomUUID().toString())
                .file_url(UPLOAD_DIR+ File.separator+file.getOriginalFilename())
                .metaData(MetaData.builder()
                        .tags(new ArrayList<>())
                        .file_type(file.getContentType())
                        .uploaded_at(new Date(System.currentTimeMillis()))
                        .build())
                .build();
        documentRepository.save(savedDocument);
        return "Document saved successfully";
    }
}
