package io.github.vaatech.modelmapper;

import io.github.vaatech.modelmapper.test.model.task.Task;
import io.github.vaatech.modelmapper.test.model.task.TaskDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.Module;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration;
import org.modelmapper.internal.InheritingConfiguration;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.getModelMapper;
import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.withModelMapperContext;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ImmutableModelMapperTest {

    @Test
    void whenAddConverterShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            Converter<String, String> stringConverter = ctx ->
                    StringUtils.hasText(ctx.getSource()) ? ctx.getSource() : null;
            assertThatThrownBy(() -> modelMapper.addConverter(stringConverter))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenAddConverterWithTypesShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            Converter<String, String> stringConverter = ctx ->
                    StringUtils.hasText(ctx.getSource()) ? ctx.getSource() : null;
            assertThatThrownBy(() -> modelMapper.addConverter(stringConverter, String.class, String.class))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenAddMappingsShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final PropertyMap<Task, TaskDto> propertyMap = new PropertyMap<>() {
                @Override
                protected void configure() {
                    map().setCreatedAt(LocalDate.now());
                }
            };

            assertThatThrownBy(() -> modelMapper.addMappings(propertyMap))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenCreateTypeMapWithSrcDestShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            assertThatThrownBy(() -> modelMapper.createTypeMap(Task.class, TaskDto.class))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenCreateTypeMapWithSrcDestConfShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final Configuration config = new InheritingConfiguration();
            assertThatThrownBy(() -> modelMapper.createTypeMap(Task.class, TaskDto.class, config))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenCreateTypeMapWithSrcDestNameShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final String name = "TypeMap Name";
            assertThatThrownBy(() -> modelMapper.createTypeMap(Task.class, TaskDto.class, name))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenCreateTypeMapWithSrcDestNameConfShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final Configuration config = new InheritingConfiguration();
            final String name = "TypeMap Name";
            assertThatThrownBy(() -> modelMapper.createTypeMap(Task.class, TaskDto.class, name, config))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenCreateTypeMapWithInstanceDestShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final Task task = new Task();
            assertThatThrownBy(() -> modelMapper.createTypeMap(task, TaskDto.class))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenCreateTypeMapWithInstanceDestConfShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final Configuration config = new InheritingConfiguration();
            final Task task = new Task();
            assertThatThrownBy(() -> modelMapper.createTypeMap(task, TaskDto.class, config))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenCreateTypeMapWithInstanceDestNameShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final String name = "TypeMap Name";
            final Task task = new Task();

            assertThatThrownBy(() -> modelMapper.createTypeMap(task, TaskDto.class, name))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenCreateTypeMapWithInstanceDestNameConfShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final Configuration config = new InheritingConfiguration();
            final String name = "TypeMap Name";
            final Task task = new Task();
            assertThatThrownBy(() -> modelMapper.createTypeMap(task, TaskDto.class, name, config))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenGetConfigurationShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            assertThatThrownBy(modelMapper::getConfiguration)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenGetTypeMapSrcDestShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            assertThatThrownBy(() -> modelMapper.getTypeMap(Task.class, TaskDto.class))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenGetTypeMapSrcDestNameShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final String name = "TypeMap Name";
            assertThatThrownBy(() -> modelMapper.getTypeMap(Task.class, TaskDto.class, name))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenTypeMapSrcDestShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            assertThatThrownBy(() -> modelMapper.typeMap(Task.class, TaskDto.class))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenTypeMapSrcDestNameShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final String name = "TypeMap Name";
            assertThatThrownBy(() -> modelMapper.typeMap(Task.class, TaskDto.class, name))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenEmptyTypeMapSrcDestShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            assertThatThrownBy(() -> modelMapper.emptyTypeMap(Task.class, TaskDto.class))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenEmptyTypeMapSrcDestNameShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final String name = "TypeMap Name";
            assertThatThrownBy(() -> modelMapper.emptyTypeMap(Task.class, TaskDto.class, name))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenGetTypeMapsShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            assertThatThrownBy(modelMapper::getTypeMaps)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }

    @Test
    void whenRegisterModuleShouldThrow() {
        withModelMapperContext().run(context -> {
            final ModelMapper modelMapper = getModelMapper(context);
            final Module module = mapper -> {};
            assertThatThrownBy(() -> modelMapper.registerModule(module))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ModelMapper cannot be configured");
        });
    }
}
