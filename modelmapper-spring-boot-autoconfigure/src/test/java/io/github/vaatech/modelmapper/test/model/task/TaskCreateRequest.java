package io.github.vaatech.modelmapper.test.model.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {
    private Long taskId;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String priority;
}
