package io.github.vaatech.modelmapper.test.model.post;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagResponse {
    private String id;
    private String name;
    private String slug;
}
