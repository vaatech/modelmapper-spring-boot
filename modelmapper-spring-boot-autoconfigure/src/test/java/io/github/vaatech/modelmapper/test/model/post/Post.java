package io.github.vaatech.modelmapper.test.model.post;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Post {
    private String title;
    private String content;
    private String summary;
    private Set<Tag> tags = new LinkedHashSet<>();
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
