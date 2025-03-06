package io.github.vaatech.modelmapper.test.config;

import io.github.vaatech.modelmapper.ModelMapperBuilderCustomizer;
import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskCreateRequest;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import org.junit.platform.commons.util.StringUtils;
import org.modelmapper.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskMappingsConfiguration {

    final Converter<String, String> DESCRIPTION_CONVERTER = context -> {
        String description = context.getSource();
        return StringUtils.isBlank(description) ? null : description;
    };

    @Bean
    ModelMapperBuilderCustomizer taskToTaskDtoMappings() {
        return builder -> builder
                .typeMap(Task.class, TaskDto.class, config -> {
                    config.customize(typeMap -> { //
                        typeMap.addMapping(Task::getId, TaskDto::setTaskId);
                    });
                });
    }

    @Bean
    ModelMapperBuilderCustomizer taskDtoToTaskMappings() {
        return builder -> builder
                .typeMap(TaskDto.class, Task.class, config -> { //
                    config.customize(typeMap -> { //
                        typeMap.addMapping(TaskDto::getTaskId, Task::setId);
                    });
                });
    }

    @Bean
    ModelMapperBuilderCustomizer taskCreateRequestToTaskMappings() {
        return builder -> builder
                .typeMap(TaskCreateRequest.class, Task.class, config -> { //
                    config.customize(typeMap -> { //
                        typeMap.addMappings(mapper -> {
                            mapper.using(DESCRIPTION_CONVERTER).map(TaskCreateRequest::getDescription, Task::setDescription);
                            mapper.skip(Task::setCreatedBy);
                            mapper.skip(Task::setCreatedAt);
                            mapper.skip(Task::setId);
                        });
                    });
                });
    }
}
