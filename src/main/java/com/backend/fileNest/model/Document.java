package com.backend.fileNest.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

@org.springframework.data.mongodb.core.mapping.Document
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    @Id
    private String id;
    private String title;
    @DBRef
    private User uploadedBy;
    private String file_url;
    private MetaData metaData;
    @CreatedDate
    private Date create_at;
    @LastModifiedDate
    private Date updated_at;
}
