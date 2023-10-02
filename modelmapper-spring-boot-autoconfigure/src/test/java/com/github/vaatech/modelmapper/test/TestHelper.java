package com.github.vaatech.modelmapper.test;

import com.github.vaatech.modelmapper.ModelMapperAutoConfiguration;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class TestHelper {

    public static ApplicationContextRunner contextRunner() {
        return new ApplicationContextRunner();
    }

    public static ApplicationContextRunner withModelMapperContext() {
        return contextRunner()
                .withConfiguration(AutoConfigurations.of(ModelMapperAutoConfiguration.class));
    }

    public static ModelMapper getModelMapper(AssertableApplicationContext context) {
        ModelMapper modelMapper = context.getBean(ModelMapper.class);
        assertThat(modelMapper).isNotNull();
        return modelMapper;
    }
}
