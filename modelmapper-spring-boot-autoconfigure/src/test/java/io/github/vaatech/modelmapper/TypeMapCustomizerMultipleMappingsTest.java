package io.github.vaatech.modelmapper;

import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskCreateRequest;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TypeMapCustomizerMultipleMappingsTest {

    static List<List<Class<?>>> userConfigurations = List.of(
            List.of(ModelMapperConfiguration01.class),
            List.of(ModelMapperConfiguration01.class, ModelMapperConfiguration02.class)
    );

    @ParameterizedTest
    @FieldSource("userConfigurations")
    void whenCreateTypeMapMultipleTypeMapConfigShouldSucceed(final List<Class<?>> userConfigurations) {
        withModelMapperContext()
                .withUserConfiguration(userConfigurations.toArray(Class[]::new))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    ModelMapper modelMapper = getModelMapper(context);
                    assertThat(getTypeMaps(modelMapper)).hasSize(1);
                });
    }

    @Test
    void whenUnmappedPropertiesStrictModeShouldFail() {
        withModelMapperContext()
                .withUserConfiguration(ModelMapperConfiguration03.class)
                .withPropertyValues(
                        "modelmapper.validate-enabled=true",
                        "modelmapper.matching-strategy=Strict")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void whenUnmappedPropertiesDefaultModeShouldFail() {
        withModelMapperContext()
                .withUserConfiguration(ModelMapperConfiguration03.class)
                .withPropertyValues("modelmapper.validate-enabled=true")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void whenBuildSecondTimeShouldFail() {
        withModelMapperContext().run(context -> {
            assertThat(context).hasNotFailed();
            ModelMapperBuilder modelMapper = context.getBean(ModelMapperBuilder.class);
            assertThat(modelMapper).isNotNull();
            assertThatThrownBy(modelMapper::build)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("This ModelMapper has already been built");
        });
    }

    @Configuration
    static class ModelMapperConfiguration01 {

        @Bean
        ModelMapperBuilderCustomizer
        modelMapperBuilderCustomizer01() {
            return builder -> builder
                    .typeMapOf(Task.class, TaskDto.class, "[Task -> TaskDto]");
        }

        @Bean
        ModelMapperBuilderCustomizer
        modelMapperBuilderCustomizer02() {
            return builder -> builder
                    .typeMapOf(Task.class, TaskDto.class, "[Task -> TaskDto]");
        }
    }

    @Configuration
    static class ModelMapperConfiguration02 {

        @Bean
        ModelMapperBuilderCustomizer
        modelMapperBuilderCustomizer03(final org.modelmapper.config.Configuration config) {
            return builder -> builder
                    .typeMapOf(Task.class, TaskDto.class, "[Task -> TaskDto]", config);
        }

    }

    @Configuration
    static class ModelMapperConfiguration03 {

        @Bean
        ModelMapperBuilderCustomizer
        modelMapperBuilderCustomizer01() {
            return builder -> builder
                    .typeMapOf(TaskCreateRequest.class, TaskDto.class, "[TaskCreateRequest -> TaskDto]");
        }

        @Bean
        ModelMapperBuilderCustomizer
        modelMapperBuilderCustomizer02() {
            return builder -> builder
                    .typeMapOf(TaskCreateRequest.class, Task.class, "[TaskCreateRequest -> Task]");
        }
    }
}

