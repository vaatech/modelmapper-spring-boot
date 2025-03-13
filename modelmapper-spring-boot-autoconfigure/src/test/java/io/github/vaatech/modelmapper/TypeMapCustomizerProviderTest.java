package io.github.vaatech.modelmapper;

import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import io.github.vaatech.modelmapper.test.model.task.TaskPriority;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeMapCustomizerProviderTest {

    @Test
    void test() {
        withModelMapperContext()
                .withPropertyValues(
                        "modelmapper.field-matching-enabled=true",
                        "modelmapper.validate-enabled=true",
                        "modelmapper.skip-null-enabled=true",
                        "modelmapper.matching-strategy=Strict")
                .withUserConfiguration(TaskMappingsConfiguration.class)
                .run(ctx -> {
                    ModelMapper modelMapper = getModelMapper(ctx);
                    TaskDto request = TaskDto.builder()
                            .taskId(1L)
                            .name("Task 1 Updated")
                            .priority("CRITICAL")
                            .description("")
                            .build();

                    Task task = modelMapper.map(request, Task.class);

                    assertThat(task.getName()).isEqualTo("Task 1 Updated");
                    assertThat(task.getPriority()).isEqualTo(TaskPriority.CRITICAL);
                    assertThat(task.getDescription()).isNull();
                });
    }

    @Configuration
    static class TaskMappingsConfiguration {

        @Bean
        Map<Long, Task> repository() {
            Map<Long, Task> repository = new HashMap<>();
            repository.put(1L, Task.builder()
                    .id(1L)
                    .name("Task 1")
                    .createdAt(LocalDate.now())
                    .priority(TaskPriority.MODERATE)
                    .build());
            return repository;
        }

        @Bean
        ModelMapperBuilderCustomizer taskMappings(Map<Long, Task> repository) {
            return builder -> builder.typeMapOf(TaskDto.class, Task.class, typeMap -> typeMap
                    .addMapping(TaskDto::getTaskId, Task::setId)
                    .setProvider((request) -> {
                        TaskDto taskDto = (TaskDto) request.getSource();
                        return repository.get(taskDto.getTaskId());
                    })
                    .setPostConverter(context -> {
                        TaskDto createRequest = context.getSource();
                        Task task = context.getDestination();
                        String description = createRequest.getDescription();
                        task.setDescription(StringUtils.isBlank(description) ? null : description);
                        return task;
                    }));
        }
    }
}
