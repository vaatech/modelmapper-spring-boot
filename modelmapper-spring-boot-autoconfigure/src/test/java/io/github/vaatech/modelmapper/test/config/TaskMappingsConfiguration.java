package io.github.vaatech.modelmapper.test.config;

import io.github.vaatech.modelmapper.Customizer;
import io.github.vaatech.modelmapper.ModelMapperBuilderCustomizer;
import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskCreateRequest;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import org.junit.platform.commons.util.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;

@Configuration
public class TaskMappingsConfiguration {

    static final Converter<String, String> DESCRIPTION_CONVERTER = context -> {
        String description = context.getSource();
        return StringUtils.isBlank(description) ? null : description;
    };

    static final Customizer<TypeMap<TaskCreateRequest, Task>> TYPE_MAP_CUSTOMIZER = typeMap -> {
        typeMap.addMappings(mapper -> {
            mapper.skip(Task::setCreatedBy);
            mapper.skip(Task::setCreatedAt);
            mapper.skip(Task::setCreatedAt);
            mapper.skip(Task::setId);
            mapper.using(DESCRIPTION_CONVERTER).map(TaskCreateRequest::getDescription, Task::setDescription);
        });
    };

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
    ModelMapperBuilderCustomizer taskDtoToTaskTypeMapConfiguration(final org.modelmapper.config.Configuration config) {
        return builder -> builder.typeMapOf(TaskDto.class, Task.class, config);
    }

    @Bean
    @Order(5)
    ModelMapperBuilderCustomizer taskCreateRequestToTaskMappings() {
        return builder -> builder.typeMapOf(TaskCreateRequest.class, Task.class, TYPE_MAP_CUSTOMIZER);
    }

    @Bean
    @Order(10)
    ModelMapperBuilderCustomizer taskCreateRequestToTaskMappingsOverride() {
        return builder -> builder.typeMapOf(TaskCreateRequest.class, Task.class, typeMap -> {
            typeMap.addMapping(source -> LocalDate.now(), Task::setCreatedAt);
        });
    }
}