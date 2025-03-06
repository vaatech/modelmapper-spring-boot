package io.github.vaatech.modelmapper.test.model.task;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TaskDto {
    private Long taskId;
    private String createdBy;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate createdAt;
    private String priority;
}
