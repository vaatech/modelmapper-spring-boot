package com.github.vaatech.modelmapper.test.model.post;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostResponse {
    private String id;
    private String title;
    private String content;
    private String summary;
    private String tags;
    private String publishDate;
    private String createdAt;
    private String updatedAt;
}