package com.backend.fileNest.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class TagsResponse {
    private String name;
    private Integer file_with_tag;
}
