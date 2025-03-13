package io.github.vaatech.modelmapper;

import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationCustomizerTest {

    @Test
    void whenCustomizeConfigurationShouldLoad() {
        withModelMapperContext()
                .withUserConfiguration(ModelMapperCustomConfiguration.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    ModelMapper mapper = getModelMapper(context);
                    var config = getConfiguration(mapper);
                    assertThat(config.isFieldMatchingEnabled()).isTrue();
                    assertThat(config.getFieldAccessLevel()).isEqualTo(AccessLevel.PACKAGE_PRIVATE);
                    assertThat(config.getMethodAccessLevel()).isEqualTo(AccessLevel.PACKAGE_PRIVATE);
                });
    }

    @Configuration
    static class ModelMapperCustomConfiguration {

        @Bean
        ConfigurationCustomizer configurationCustomizer() {
            return configuration -> {
                assertThat(configuration.isFieldMatchingEnabled()).isFalse();
                configuration
                        .setFieldMatchingEnabled(Boolean.TRUE)
                        .setFieldAccessLevel(AccessLevel.PACKAGE_PRIVATE)
                        .setMethodAccessLevel(AccessLevel.PACKAGE_PRIVATE);
            };
        }

        @Bean
        ModelMapperBuilderCustomizer
        taskToTaskDTOMappingsCustomizer(final org.modelmapper.config.Configuration configuration) {
            assertThat(configuration.isFieldMatchingEnabled()).isTrue();
            configuration.setFieldMatchingEnabled(false);
            return builder -> builder.typeMapOf(Task.class, TaskDto.class, configuration);
        }
    }
}
