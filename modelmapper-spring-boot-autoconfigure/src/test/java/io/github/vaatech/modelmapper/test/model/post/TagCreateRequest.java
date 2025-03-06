package io.github.vaatech.modelmapper.test.model.post;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagCreateRequest {
    private String name;
    private String slug;
    private String parent;
}
