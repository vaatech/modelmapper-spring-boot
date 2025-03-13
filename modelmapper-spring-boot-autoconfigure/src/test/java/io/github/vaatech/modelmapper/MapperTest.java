package io.github.vaatech.modelmapper;

import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import io.github.vaatech.modelmapper.test.model.task.TaskPriority;
import org.junit.jupiter.api.Test;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.withModelMapperContext;
import static org.assertj.core.api.Assertions.assertThat;

public class MapperTest {

    @Test
    void whenMapListShouldMap() {
        withModelMapperContext().run(context -> {
            TaskDto task01 = TaskDto.builder()
                    .name("Task 1")
                    .priority("MODERATE")
                    .build();
            TaskDto task02 = TaskDto.builder()
                    .name("Task 2")
                    .priority("CRITICAL")
                    .build();

            List<TaskDto> dtos = List.of(task01, task02);

            Type listType = new TypeToken<List<Task>>() {}.getType();
            List<Task> tasks = Mapper.INSTANCE.map(dtos, listType);

            assertThat(tasks).isNotNull();
            assertThat(tasks).isNotEmpty();
            assertThat(tasks).hasSize(2);

            Task task1 = tasks.get(0);
            assertThat(task1.getName()).isEqualTo("Task 1");
            assertThat(task1.getPriority()).isEqualTo(TaskPriority.MODERATE);

            Task task2 = tasks.get(1);
            assertThat(task2.getName()).isEqualTo("Task 2");
            assertThat(task2.getPriority()).isEqualTo(TaskPriority.CRITICAL);
        });
    }
}
