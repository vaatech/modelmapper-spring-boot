package io.github.vaatech.modelmapper.test.model.task;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TaskResponse {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String createdByUp;
    private LocalDate creationDate;
    private String priority;
}
