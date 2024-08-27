package com.backend.fileNest.service.Implementation;

import com.backend.fileNest.model.Document;
import com.backend.fileNest.model.MetaData;
import com.backend.fileNest.model.User;
import com.backend.fileNest.repository.DocumentRepository;
import com.backend.fileNest.repository.UserRepository;
import com.backend.fileNest.response.DocumentResponse;
import com.backend.fileNest.service.DocumentService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private UserRepository userRepository;
    private final Path rootLocation = Paths.get("uploads");


    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage!", e);
        }
    }

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
    public Resource loadFileAsResource(String fileId, String email) {
        try {
            User user = userRepository.findByEmail(email);
            List<Document> documents = documentRepository.findByUploadedBy(user);
            Document doc = (Document) documents
                    .stream()
                    .filter(document -> document.getId().equals(fileId))
                    .toArray()[0];
            String filename = doc.getTitle();
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, List<String> tags, String email){
        User user = userRepository.findByEmail(email);
        String filename = file.getOriginalFilename()!=null ?
                file.getOriginalFilename() :
                "document_"+System.currentTimeMillis();
        try {
            Files.copy(file.getInputStream(),
                    this.rootLocation.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
        Document savedDocument = Document.builder()
                .uploadedBy(user)
                .title(file.getOriginalFilename())
                .id(UUID.randomUUID().toString())
                .file_url(String.valueOf(this.rootLocation.resolve(filename)))
                .metaData(MetaData.builder()
                        .tags(tags)
                        .file_type(file.getContentType().split("/")[1])
                        .uploaded_at(new Date(System.currentTimeMillis()))
                        .build())
                .build();
        documentRepository.save(savedDocument);
        return "Document saved successfully";
    }

    @Override
    public String deleteDocument(String fileId, String email) {
        try {
            User user = userRepository.findByEmail(email);
            List<Document> documents = documentRepository.findByUploadedBy(user);
            Document doc = (Document) documents
                    .stream()
                    .filter(document -> document.getId().equals(fileId))
                    .toArray()[0];
            Path filePath = Paths.get(doc.getFile_url());
            Files.deleteIfExists(filePath);
            documentRepository.delete(doc);
            return "document deleted successfully";
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Document not found!");
        }
    }

    @Override
    public List<DocumentResponse> getDocumentByTags(List<String> tags, String email) {
        User user = userRepository.findByEmail(email);
        List<Document> docs = documentRepository.findByUploadedBy(user);

        // Filter documents that contain all the specified tags
        List<Document> filteredDocs = docs.stream()
                .filter(doc -> new HashSet<>(doc.getMetaData().getTags()).containsAll(tags))
                .toList();

        return filteredDocs.stream()
                .map(doc -> DocumentResponse.builder()
                        .file_id(doc.getId())
                        .metaData(doc.getMetaData())
                        .title(doc.getTitle())
                        .build())
                .collect(Collectors.toList());
    }
}
