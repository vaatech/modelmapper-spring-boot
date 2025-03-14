package io.github.vaatech.modelmapper;

import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskCreateRequest;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import io.github.vaatech.modelmapper.test.model.task.TaskPriority;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.time.Month;

import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.getModelMapper;
import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.withModelMapperContext;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeMapCustomizerOverrideMappingsTest {

    @Test
    void whenTaskMappingsConfigurationLoadShouldSucceedMapping() {
        withModelMapperContext()
                .withUserConfiguration(
                        TaskMappingsConfiguration.class,
                        TaskMappingsConfigurationOverride.class)
                .run(context -> {
                    ModelMapper mapper = getModelMapper(context);

                    TaskCreateRequest request = TaskCreateRequest.builder()
                            .name("Task 1")
                            .description("Task 1 Description")
                            .startDate(LocalDate.of(2025, Month.JANUARY, 15))
                            .endDate(LocalDate.of(2025, Month.JANUARY, 17))
                            .priority("30")
                            .build();

                    Task task = mapper.map(request, Task.class);

                    assertThat(task.getName()).isEqualTo(request.getName());
                    assertThat(task.getDescription()).isEqualTo(request.getDescription());
                    assertThat(task.getStartDate()).isEqualTo(request.getStartDate());
                    assertThat(task.getEndDate()).isEqualTo(request.getEndDate());
                    assertThat(String.valueOf(task.getPriority().level())).isEqualTo(request.getPriority());
                    assertThat(task.getCreatedAt()).isNotNull();
                });
    }

    @Configuration
    static class TaskMappingsConfiguration {

        @Bean
        ModelMapperBuilderCustomizer taskToTaskDtoMappings() {
            return builder -> builder.typeMapOf(Task.class, TaskDto.class, typeMap -> {
                typeMap.addMapping(Task::getId, TaskDto::setTaskId);
            });
        }

        @Bean
        ModelMapperBuilderCustomizer taskDtoToTaskMappings() {
            return builder -> builder.typeMapOf(TaskDto.class, Task.class, typeMap -> { //
                typeMap.addMapping(TaskDto::getTaskId, Task::setId);
            });
        }

        @Bean
        ModelMapperBuilderCustomizer
        taskDtoToTaskTypeMapConfiguration(final org.modelmapper.config.Configuration config) {
            return builder -> builder.typeMapOf(TaskDto.class, Task.class, config);
        }

        static final Converter<String, String> DESCRIPTION_CONVERTER = context -> {
            String description = context.getSource();
            return StringUtils.isBlank(description) ? null : description;
        };

        static final Converter<String, TaskPriority> PRIORITY_CONVERTER = context -> {
            String source = context.getSource();
            try {
                Integer level = Integer.parseInt(source);
                return TaskPriority.valueOf(level);
            } catch (NumberFormatException e) {
                return TaskPriority.valueOf(source);
            }
        };

        @Bean
        @Order(5)
        ModelMapperBuilderCustomizer taskCreateRequestToTaskMappings() {
            return builder -> builder.typeMapOf(TaskCreateRequest.class, Task.class, typeMap -> {
                typeMap.addMappings(mapper -> {
                    mapper.skip(Task::setCreatedBy);
                    mapper.skip(Task::setCreatedAt);
                    mapper.skip(Task::setId);
                    mapper.using(DESCRIPTION_CONVERTER).map(TaskCreateRequest::getDescription, Task::setDescription);
                    mapper.using(PRIORITY_CONVERTER).map(TaskCreateRequest::getPriority, Task::setPriority);
                });
            });
        }
    }

    @Configuration
    static class TaskMappingsConfigurationOverride {

        @Bean
        @Order(10)
        ModelMapperBuilderCustomizer taskCreateRequestToTaskMappingsOverride() {
            return builder -> builder.typeMapOf(TaskCreateRequest.class, Task.class, typeMap -> {
                typeMap.addMapping(source -> LocalDate.now(), Task::setCreatedAt);
            });
        }
    }
}

