package com.backend.fileNest.service.Implementation;

import com.backend.fileNest.model.Document;
import com.backend.fileNest.model.MetaData;
import com.backend.fileNest.model.User;
import com.backend.fileNest.repository.DocumentRepository;
import com.backend.fileNest.repository.UserRepository;
import com.backend.fileNest.response.DocumentResponse;
import com.backend.fileNest.response.TagsResponse;
import com.backend.fileNest.service.DocumentService;
import com.backend.fileNest.service.OcrService;
import jakarta.annotation.PostConstruct;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    @Autowired
    private OcrService ocrService;
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
    public List<DocumentResponse> getAllDocument(String email, Integer pageSize, Integer pageNumber) {

        Pageable pageable = PageRequest
                .of(pageNumber,
                        pageSize,
                        Sort.by(Sort.Direction.DESC, "metaData.uploaded_at")
                );

        Page<Document> documentPage = documentRepository.findAll(pageable);

        User user = userRepository.findByEmail(email);

        List<Document> docs = documentPage.stream()
                .filter(
                        (document ->
                                document.getUploadedBy().getId().equals(user.getId())
                        )
                )
                .toList();

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
            String filename = user.getEmail()+doc.getTitle();
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
                user.getEmail()+file.getOriginalFilename() :
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
            System.out.println(doc.getId());
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
    public String deleteDocuments(List<String> fileIds, String email) {
        return null;
    }

    @Override
    public List<DocumentResponse> searchDocument(String searchText, String fileType, Date startDate, Date endDate, List<String> tags, String email) {
        if (startDate == null) {
            startDate = new Date(System.currentTimeMillis() - (365L * 24 * 60 * 60 * 1000));
        }
        if (endDate == null) {
            endDate = new Date();
        }
        if (tags == null) {
            tags = new ArrayList<>();
        }

        List<Document> documents = documentRepository.searchDocuments(searchText, fileType, startDate, endDate, tags);

        User user = userRepository.findByEmail(email);

        List<Document> docs = documents.stream()
                .filter(
                        (document ->
                                document.getUploadedBy().getId().equals(user.getId())
                        )
                )
                .toList();

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
    public List<TagsResponse> getAllTags(String email) {
        User user = userRepository.findByEmail(email);
        List<Document> docs = documentRepository.findByUploadedBy(user);
        if (docs == null || docs.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, Integer> tagFrequency;
        tagFrequency = new HashMap<>();
        for (Document doc :
                docs) {
            List<String> tagList = doc.getMetaData().getTags();
            for (String tag :
                    tagList) {
                tagFrequency.put(tag, tagFrequency.getOrDefault(tag, 0) + 1);
            }
        }
        if(tagFrequency.isEmpty()) return new ArrayList<>();
        List<Map.Entry<String, Integer>> sortedTags = new ArrayList<>(tagFrequency.entrySet());
        sortedTags.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        int counter = 3;
        int others = 0;
        List<TagsResponse> response = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedTags) {
            if(counter!=0){
                TagsResponse tag = TagsResponse.builder()
                        .name(entry.getKey())
                        .file_with_tag(entry.getValue())
                        .build();
                response.add(tag);
                counter--;
            }else{
                others+=entry.getValue();
            }
        }
        response.add(TagsResponse.builder().file_with_tag(others).name("others").build());
        return response;
    }

    @Override
    public String extractTexts(String fileId, String email) {
        User user = userRepository.findByEmail(email);
        Optional<Document> optionalDocument = documentRepository.findById(fileId);
        if (optionalDocument.isEmpty()) {
            throw new RuntimeException("Document not found!");
        }
        Document document = optionalDocument.get();
        if (!document.getUploadedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed to access this document!");
        }
        Path filePath = Paths.get(document.getFile_url());
        File file = filePath.toFile();
        String extractedText;
        try {
            extractedText = ocrService.extractTextFromFile(file);
        } catch (IOException | TesseractException e) {
            throw new RuntimeException("Failed to extract text from file", e);
        }
        document.setOcr_text(extractedText);
        documentRepository.save(document);
        return extractedText;
    }

    @Override
    public String getOcrStatus(String fileId) {
        Optional<Document> optionalDocument = documentRepository.findById(fileId);
        if (optionalDocument.get().getOcr_text()==null){
            return "incomplete";
        }
        return "completed";
    }


    @Override
    public String getFileUri(String fileId, String email) {
        User user = userRepository.findByEmail(email);
        List<Document> documents = documentRepository.findByUploadedBy(user);
        Document doc = documents.stream()
                .filter(document -> document.getId().equals(fileId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Assuming your API is hosted on "http://localhost:8080"
        String baseUrl = "http://localhost:8080/file-nest";
        String downloadUri = baseUrl + "/documents/" + fileId;

        return downloadUri;
    }


    @Override
    public DocumentResponse getFileByID(String fileId, String email){
        User user = userRepository.findByEmail(email);
        Optional<Document> optionalDocument = documentRepository.findByUploadedByAndId(user, fileId);
        Document document = optionalDocument.orElse(null);
//        System.out.println(document);
        assert document != null;
        return DocumentResponse.builder()
                .file_id(document.getId())
                .metaData(document.getMetaData())
                .title(document.getTitle())
                .ocrText(document.getOcr_text())
                .build();
    }

}
