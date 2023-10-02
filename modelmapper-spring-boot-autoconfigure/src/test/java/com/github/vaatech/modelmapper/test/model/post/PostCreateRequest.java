package com.github.vaatech.modelmapper.test.model.post;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostCreateRequest {
    private String title;
    private String content;
    private String summary;
    private String tags;
}
