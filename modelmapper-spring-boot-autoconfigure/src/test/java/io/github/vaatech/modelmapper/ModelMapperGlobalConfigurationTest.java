package io.github.vaatech.modelmapper;

import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.AbstractProvider;
import org.modelmapper.Condition;
import org.modelmapper.Provider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.getConfiguration;
import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.withModelMapperContext;
import static org.assertj.core.api.Assertions.assertThat;

public class ModelMapperGlobalConfigurationTest {

    @Nested
    class GlobalProviderTest {

        static class TaskProvider extends AbstractProvider<Task> {

            @Override
            protected Task get() {
                return new Task();
            }
        }

        static final Provider<Task> CUSTOM_PROVIDER = new TaskProvider();

        @Test
        void whenContextLoadShouldNotHaveProvider() {
            withModelMapperContext().run(context -> {
                assertThat(context).hasNotFailed();
                var config = getConfiguration(context);
                assertThat(config.getProvider()).isNull();
            });
        }

        @Test
        void whenSpringProviderShouldSucceed() {
            withModelMapperContext()
                    .withPropertyValues("modelmapper.spring-provider-enabled=true")
                    .run(context -> {
                        assertThat(context).hasNotFailed();
                        var config = getConfiguration(context);
                        assertThat(config.getProvider()).isNotNull();
                    });
        }


        @Test
        void whenCustomProviderShouldSucceed() {
            withModelMapperContext()
                    .withUserConfiguration(GlobalProviderConfigurationTest.class)
                    .run(context -> {
                        assertThat(context).hasNotFailed();
                        var config = getConfiguration(context);
                        assertThat(config.getProvider()).isNotNull();
                        assertThat(config.getProvider()).isEqualTo(CUSTOM_PROVIDER);
                    });
        }


        @Configuration
        static class GlobalProviderConfigurationTest {

            @Bean
            Provider<Task> customProvider() {
                return CUSTOM_PROVIDER;
            }
        }
    }

    @Nested
    class GlobalConditionTest {

        @Test
        void whenContextLoadsShouldNotHaveCondition() {
            withModelMapperContext().run(context -> {
                assertThat(context).hasNotFailed();
                var config = getConfiguration(context);
                assertThat(config.getPropertyCondition()).isNull();
            });
        }

        @Test
        void whenCustomConditionShouldSucceed() {
            withModelMapperContext()
                    .withUserConfiguration(GlobalConditionConfiguration.class)
                    .run(context -> {
                        assertThat(context).hasNotFailed();
                        var config = getConfiguration(context);
                        assertThat(config.getPropertyCondition()).isNotNull();
                    });
        }

        @Configuration
        static class GlobalConditionConfiguration {

            @Bean
            Condition<TaskDto, Task> taskCondition() {
                return context -> context.getSource() != null;
            }
        }
    }
}
