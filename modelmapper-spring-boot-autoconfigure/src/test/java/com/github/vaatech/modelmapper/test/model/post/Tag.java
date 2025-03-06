package com.github.vaatech.modelmapper.test.model.post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Tag {
    private String name;
    private String slug;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
