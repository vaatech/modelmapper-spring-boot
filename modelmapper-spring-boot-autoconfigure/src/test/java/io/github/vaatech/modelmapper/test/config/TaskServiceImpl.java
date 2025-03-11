package io.github.vaatech.modelmapper.test.config;

import io.github.vaatech.modelmapper.ModelMapperBuilder;
import io.github.vaatech.modelmapper.ModelMapperBuilderCustomizer;
import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService, ModelMapperBuilderCustomizer {

    @Override
    public void customize(ModelMapperBuilder builder) {
        builder.typeMapOf(TaskDto.class, Task.class, typeMap -> { //
            typeMap.addMapping(TaskDto::getTaskId, Task::setId);
        });
    }
}
