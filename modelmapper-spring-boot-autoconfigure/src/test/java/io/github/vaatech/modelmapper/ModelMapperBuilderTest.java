package io.github.vaatech.modelmapper;

import io.github.vaatech.modelmapper.test.TestHelper;
import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import io.github.vaatech.modelmapper.test.model.task.TaskPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelMapperBuilderTest {

    @Nested
    class ModelMapperBuilderWithConverterTest {

        private static final Map<Long, Task> repository = new HashMap<>();

        @BeforeEach
        void setUp() {
            repository.put(
                    1L,
                    Task.builder()
                            .id(1L)
                            .name("Task 1")
                            .description("")
                            .createdAt(LocalDate.now())
                            .priority(TaskPriority.MODERATE)
                            .build());
        }

        @Test
        void test() {
            TestHelper.withModelMapperContext()
                    .withPropertyValues(
                            "modelmapper.field-matching-enabled=true",
                            "modelmapper.validate-enabled=true",
                            "modelmapper.skip-null-enabled=true",
                            "modelmapper.matching-strategy=Strict")
                    .withUserConfiguration(TaskMappingsConfiguration.class)
                    .run(ctx -> {
                        ModelMapper modelMapper = TestHelper.getModelMapper(ctx);
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

            static final Converter<TaskDto, Task> TASK_CREATE_REQUEST_TO_TASK_CONVERTER = context -> {
                TaskDto createRequest = context.getSource();
                Task task = context.getDestination();
                String description = createRequest.getDescription();
                task.setDescription(StringUtils.isBlank(description) ? null : description);
                return task;
            };

            static final Provider<Task> TASK_PROVIDER = (request) -> {
                TaskDto taskDto = (TaskDto) request.getSource();
                return repository.get(taskDto.getTaskId());
            };

            @Bean
            ModelMapperBuilderCustomizer taskMappings() {
                return builder -> builder
                        .typeMap(TaskDto.class, Task.class, config -> config
                                .configuration(Customizer.withDefaults())
                                .customize(typeMap -> typeMap
                                        .addMapping(TaskDto::getTaskId, Task::setId)
                                        .setProvider(TASK_PROVIDER)
                                        .setPostConverter(TASK_CREATE_REQUEST_TO_TASK_CONVERTER)));
            }
        }
    }

}
