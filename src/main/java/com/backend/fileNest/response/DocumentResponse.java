package com.backend.fileNest.response;

import com.backend.fileNest.model.MetaData;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DocumentResponse {
    private String file_id;
    private String title;
    private MetaData metaData;
    private String ocrText;
}
