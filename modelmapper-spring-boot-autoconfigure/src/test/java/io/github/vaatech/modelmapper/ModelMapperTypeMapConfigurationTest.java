package io.github.vaatech.modelmapper;

import io.github.vaatech.modelmapper.test.config.TaskMappingsConfiguration;
import io.github.vaatech.modelmapper.test.config.TaskService;
import io.github.vaatech.modelmapper.test.config.TaskServiceImpl;
import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskCreateRequest;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.InheritingConfiguration;

import java.time.LocalDate;
import java.time.Month;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.github.vaatech.modelmapper.test.TestHelper.getModelMapper;
import static io.github.vaatech.modelmapper.test.TestHelper.withModelMapperContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModelMapperTypeMapConfigurationTest {

    static Arguments withArguments(
            String typeMapName, Supplier<ModelMapperBuilderCustomizer> customizerSupplier) {
        return Arguments.of(typeMapName, customizerSupplier);
    }

    @Nested
    @DisplayName("TypeMap Configuration Tests")
    class TypeMapConfigurationTests {

        @Nested
        @DisplayName("TypeMap Configuration Tests - Success Scenarios")
        class TypeMapConfigurationTestsSuccessScenarios {

            static Stream<Arguments> builderCustomizersWithDuplicateTypeMaps() {
                return Stream.of(
                        withArguments("[Task -> TaskDto]", () -> builder -> builder
                                .typeMapOf(Task.class, TaskDto.class)
                                .typeMapOf(Task.class, TaskDto.class)),
                        withArguments("[Task -> TaskDto with Configuration]", () -> builder -> builder
                                .typeMapOf(Task.class, TaskDto.class, new InheritingConfiguration())
                                .typeMapOf(Task.class, TaskDto.class)));
            }

            @ParameterizedTest(name = "{0}")
            @MethodSource("builderCustomizersWithDuplicateTypeMaps")
            void whenCreateTypeMapDuplicateTypeMapShouldSucceed(
                    String typeMapName, Supplier<ModelMapperBuilderCustomizer> customizerSupplier) {
                withModelMapperContext()
                        .withBean(ModelMapperBuilderCustomizer.class, customizerSupplier)
                        .run(context -> {
                            assertThat(context).hasNotFailed();
                            ModelMapper modelMapper = getModelMapper(context);
                            assertThat(modelMapper.getTypeMaps()).hasSize(1);
                        });
            }

            @Test
            void whenTaskMappingsConfigurationLoadShouldSucceed() {
                withModelMapperContext()
                        .withUserConfiguration(TaskMappingsConfiguration.class)
                        .withBean(TaskService.class, TaskServiceImpl::new)
                        .run(context -> {
                            assertThat(context).hasNotFailed();
                            ModelMapper modelMapper = getModelMapper(context);
                            assertThat(modelMapper.getTypeMaps()).hasSize(3);
                        });
            }

            @Test
            void whenTaskMappingsConfigurationLoadShouldSucceedMapping() {
                withModelMapperContext()
                        .withUserConfiguration(TaskMappingsConfiguration.class)
                        .withBean(TaskService.class, TaskServiceImpl::new)
                        .run(context -> {
                            ModelMapper mapper = getModelMapper(context);


                            TaskCreateRequest request = TaskCreateRequest.builder()
                                    .name("Task 1")
                                    .description("Task 1 Description")
                                    .startDate(LocalDate.of(2025, Month.JANUARY, 15))
                                    .endDate(LocalDate.of(2025, Month.JANUARY, 17))
                                    .priority("MODERATE")
                                    .build();

                            Task task = mapper.map(request, Task.class);

                            assertThat(task.getName()).isEqualTo(request.getName());
                            assertThat(task.getDescription()).isEqualTo(request.getDescription());
                            assertThat(task.getStartDate()).isEqualTo(request.getStartDate());
                            assertThat(task.getEndDate()).isEqualTo(request.getEndDate());
                            assertThat(task.getPriority().name()).isEqualTo(request.getPriority());
                            assertThat(task.getCreatedAt()).isNotNull();
                        });
            }
        }

        @Nested
        @DisplayName("TypeMap Configuration Tests - Failed Scenarios")
        class TypeMapConfigurationTestsFailedScenarios {

            static Stream<Arguments> builderCustomizers() {
                return Stream.of(
                        withArguments("[TaskCreateRequest -> TaskDto]", () -> builder -> builder
                                .typeMapOf(TaskCreateRequest.class, TaskDto.class)),
                        withArguments("[TaskCreateRequest -> Task]", () -> builder -> builder
                                .typeMapOf(TaskCreateRequest.class, Task.class)));
            }

            static Stream<Arguments> builderCustomizersDuplicateTypeMapsWithConfiguration() {
                return Stream.of(withArguments("[Task -> TaskDto with Configuration]", () -> builder -> builder
                        .typeMapOf(Task.class, TaskDto.class)
                        .typeMapOf(Task.class, TaskDto.class, new InheritingConfiguration())));
            }

            @ParameterizedTest(name = "{0}")
            @MethodSource("builderCustomizers")
            void whenUnmappedPropertiesStrictModeShouldFail(
                    String typeMapName, Supplier<ModelMapperBuilderCustomizer> customizerSupplier) {
                withModelMapperContext()
                        .withPropertyValues(
                                "modelmapper.validate-enabled=true", "modelmapper.matching-strategy=Strict")
                        .withBean(ModelMapperBuilderCustomizer.class, customizerSupplier)
                        .run(context -> assertThat(context).hasFailed());
            }

            @ParameterizedTest(name = "{0}")
            @MethodSource("builderCustomizers")
            void whenUnmappedPropertiesDefaultModeShouldFail(
                    String typeMapName, Supplier<ModelMapperBuilderCustomizer> customizerSupplier) {
                withModelMapperContext()
                        .withPropertyValues("modelmapper.validate-enabled=true")
                        .withBean(ModelMapperBuilderCustomizer.class, customizerSupplier)
                        .run(context -> assertThat(context).hasFailed());
            }

            @ParameterizedTest(name = "{0}")
            @MethodSource("builderCustomizersDuplicateTypeMapsWithConfiguration")
            void whenCreateTypeMapWithConfigurationTypeMapExistsShouldFail(
                    String typeMapName, Supplier<ModelMapperBuilderCustomizer> customizerSupplier) {

                withModelMapperContext()
                        .withBean(ModelMapperBuilderCustomizer.class, customizerSupplier)
                        .run(context -> assertThat(context).hasNotFailed());
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
        }
    }
}
