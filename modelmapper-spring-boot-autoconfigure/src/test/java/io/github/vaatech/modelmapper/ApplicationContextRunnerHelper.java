package io.github.vaatech.modelmapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationContextRunnerHelper {

    static ApplicationContextRunner contextRunner() {
        return new ApplicationContextRunner();
    }

    static ApplicationContextRunner withModelMapperContext() {
        return contextRunner()
                .withConfiguration(AutoConfigurations.of(ModelMapperAutoConfiguration.class));
    }

    static ModelMapper getModelMapper(AssertableApplicationContext context) {
        ModelMapper modelMapper = context.getBean(ModelMapper.class);
        assertThat(modelMapper).isNotNull();
        return modelMapper;
    }

    static Configuration getConfiguration(AssertableApplicationContext context) {
        ModelMapper modelMapper = getModelMapper(context);
        if (modelMapper instanceof ImmutableModelMapper mapper) {
            return mapper.getConfig();
        }
        return modelMapper.getConfiguration();
    }

    static Configuration getConfiguration(ModelMapper modelMapper) {
        if (modelMapper instanceof ImmutableModelMapper mapper) {
            return mapper.getConfig();
        }
        return modelMapper.getConfiguration();
    }

    static Collection<TypeMap<?, ?>> getTypeMaps(ModelMapper modelMapper) {
        if (modelMapper instanceof ImmutableModelMapper mapper) {
            return mapper.typeMaps();
        }
        return modelMapper.getTypeMaps();
    }
}
