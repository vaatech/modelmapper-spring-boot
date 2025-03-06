package com.github.vaatech.modelmapper;

import com.github.vaatech.modelmapper.test.config.TaskMappingsConfiguration;
import com.github.vaatech.modelmapper.test.model.task.Task;
import com.github.vaatech.modelmapper.test.model.task.TaskCreateRequest;
import com.github.vaatech.modelmapper.test.model.task.TaskDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.InheritingConfiguration;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.vaatech.modelmapper.Customizer.withDefaults;
import static com.github.vaatech.modelmapper.test.TestHelper.getModelMapper;
import static com.github.vaatech.modelmapper.test.TestHelper.withModelMapperContext;
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
                        withArguments(
                                "[Task -> TaskDto]",
                                () ->
                                        builder ->
                                                builder
                                                        .typeMap(Task.class, TaskDto.class)
                                                        .typeMap(Task.class, TaskDto.class)),
                        withArguments(
                                "[Task -> TaskDto with Configuration]",
                                () ->
                                        builder ->
                                                builder
                                                        .typeMap(Task.class, TaskDto.class, config -> config.configuration(withDefaults()))
                                                        .typeMap(Task.class, TaskDto.class)));
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
                        .run(context -> {
                            assertThat(context).hasNotFailed();
                            ModelMapper modelMapper = getModelMapper(context);
                            assertThat(modelMapper.getTypeMaps()).hasSize(3);
                        });
            }
        }

        @Nested
        @DisplayName("TypeMap Configuration Tests - Failed Scenarios")
        class TypeMapConfigurationTestsFailedScenarios {

            static Stream<Arguments> builderCustomizers() {
                return Stream.of(
                        withArguments(
                                "[TaskCreateRequest -> TaskDto]",
                                () -> builder -> builder.typeMap(TaskCreateRequest.class, TaskDto.class)),
                        withArguments(
                                "[TaskCreateRequest -> Task]",
                                () -> builder -> builder.typeMap(TaskCreateRequest.class, Task.class)));
            }

            static Stream<Arguments> builderCustomizersDuplicateTypeMapsWithConfiguration() {
                return Stream.of(withArguments("[Task -> TaskDto with Configuration]",
                        () ->
                                builder ->
                                        builder
                                                .typeMap(Task.class, TaskDto.class)
                                                .typeMap(Task.class, TaskDto.class, config -> config.configuration(withDefaults()))));
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
