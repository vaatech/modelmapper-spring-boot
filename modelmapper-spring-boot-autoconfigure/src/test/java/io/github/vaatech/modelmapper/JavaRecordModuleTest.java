package io.github.vaatech.modelmapper;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.getModelMapper;
import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.withModelMapperContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.modelmapper.config.Configuration.AccessLevel.PACKAGE_PRIVATE;

public class JavaRecordModuleTest {

    record UserRecord(String userId, String userName) {
    }

    static class User {
        String userId;
        String userName;
    }

    @Test
    public void shouldMapRecordToBean() {
        withModelMapperContext()
                .withUserConfiguration(ModelMapperConfiguration.class)
                .run(context -> {
                    ModelMapper modelMapper = getModelMapper(context);
                    User user = modelMapper.map(new UserRecord("id", "name"), User.class);
                    assertThat(user.userId).isEqualTo("id");
                    assertThat(user.userName).isEqualTo("name");
                });
    }

    @Test
    public void shouldMapNullValue() {
        withModelMapperContext()
                .withUserConfiguration(ModelMapperConfiguration.class)
                .run(context -> {
                    ModelMapper modelMapper = getModelMapper(context);
                    User user = modelMapper.map(new UserRecord("id", null), User.class);
                    assertThat(user.userId).isEqualTo("id");
                    assertThat(user.userName).isNull();
                });
    }

    @Configuration
    static class ModelMapperConfiguration {

        @Bean
        ConfigurationCustomizer configurationCustomizer() {
            return configuration -> configuration
                    .setFieldMatchingEnabled(true)
                    .setFieldAccessLevel(PACKAGE_PRIVATE)
                    .setMethodAccessLevel(PACKAGE_PRIVATE);
        }

        @Bean
        ModelMapperBuilderCustomizer userRecordToUserMappingsCustomizer() {
            return builder -> builder.typeMapOf(UserRecord.class, User.class);
        }
    }
}
