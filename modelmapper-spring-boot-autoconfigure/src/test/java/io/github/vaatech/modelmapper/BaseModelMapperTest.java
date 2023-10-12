package io.github.vaatech.modelmapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = BaseModelMapperTest.TestConfiguration.class,
        properties = {
                "spring.main.banner-mode=off",
        })
@DisplayName("Default AutoConfigured ModelMapper")
public class BaseModelMapperTest {

    @Autowired
    protected ConfigurableListableBeanFactory beanFactory;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected ModelMapperProperties properties;

    @Test
    void shouldConfiguredModelMapper() {
        assertThat(modelMapper).isNotNull();
        assertThat(properties).isNotNull();

        String[] beanNamesForType =
                BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, ModelMapper.class);
        assertThat(beanNamesForType)
                .as("Auto-configured modelMapper should be present")
                .hasSize(1)
                .contains("modelMapper");
    }

    @EnableAutoConfiguration
    @Configuration
    static class TestConfiguration {
    }
}
