package com.backend.fileNest.model;

import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetaData {
    private List<String> tags;
    private Date uploaded_at;
    private String file_type;
}
