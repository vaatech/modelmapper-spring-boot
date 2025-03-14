package io.github.vaatech.modelmapper;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Module;
import org.modelmapper.*;
import org.modelmapper.internal.InheritingConfiguration;
import org.modelmapper.record.RecordModule;
import org.modelmapper.record.RecordValueReader;
import org.modelmapper.spring.SpringIntegration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Slf4j
@AutoConfiguration
@ConditionalOnClass(ModelMapper.class)
@EnableConfigurationProperties(ModelMapperProperties.class)
@Import({ModelMapperPropertiesConfiguration.class, ModelMapperStandardConfiguration.class})
class ModelMapperAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ModelMapper.class)
    static class ModelMapperConfiguration {

        @Bean
        @ConditionalOnProperty(prefix = ModelMapperProperties.PROPERTIES_PREFIX, name = "locked-enabled", matchIfMissing = true)
        static ModelMapperBeanPostProcessor postProcessor() {
            return new ModelMapperBeanPostProcessor();
        }

        static class ModelMapperBeanPostProcessor implements BeanPostProcessor {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof ModelMapper mapper) {
                    return new ImmutableModelMapper(mapper);
                }
                return bean;
            }
        }

        @Bean
        InitializingBean mapper(ModelMapper mapper) {
            return () -> Mapper.INSTANCE.setMapper(mapper);
        }

        @Bean
        @ConditionalOnMissingBean(ModelMapper.class)
        ModelMapper modelMapper(final ModelMapperProperties properties,
                                final ModelMapperBuilder builder) {

            log.info("Configure ModelMapper with ModelMapperAutoConfiguration.");
            final ModelMapper modelMapper = builder.build();

            if (properties.getValidateEnabled()) {
                modelMapper.validate();
                log.info("Validate ModelMapper Configuration succeed.");
            }

            return modelMapper;
        }

        @Bean
        @ConditionalOnMissingBean
        ModelMapperBuilder
        modelMapperBuilder(final ModelMapperBuilderCustomizers customizers,
                           final ObjectProvider<ConfigurationCustomizer> configurationCustomizers) {
            final ModelMapperBuilder builder = ModelMapperBuilder.builder();
            configurationCustomizers.orderedStream().forEach(builder::configuration);
            return customizers.customize(builder);
        }

        @Bean
        ModelMapperBuilderCustomizers
        modelMapperBuilderCustomizers(ObjectProvider<ModelMapperBuilderCustomizer> customizers) {
            return new ModelMapperBuilderCustomizers(customizers);
        }

        @Bean
        ModelMapperBuilderCustomizer convertersCustomizer(ObjectProvider<Converter<?, ?>> convertersProvider) {
            return builder -> convertersProvider.orderedStream().forEach(builder::converter);
        }

        @Bean
        ModelMapperBuilderCustomizer modulesCustomizer(ObjectProvider<Module> modulesProvider) {
            return builder -> modulesProvider.orderedStream().forEach(builder::module);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(org.modelmapper.config.Configuration.class)
    static class ModelMapperConfigurationConfiguration {

        @Bean
        ConfigurationCustomizer providerCustomizer(ObjectProvider<Provider<?>> providerProvider) {
            return configuration -> providerProvider.ifAvailable(configuration::setProvider);
        }

        @Bean
        ConfigurationCustomizer conditionCustomizer(ObjectProvider<Condition<?, ?>> conditionProvider) {
            return configuration -> conditionProvider.ifAvailable(configuration::setPropertyCondition);
        }

        @Bean
        @Scope(SCOPE_PROTOTYPE)
        org.modelmapper.config.Configuration
        mapperConfiguration(final ObjectProvider<ConfigurationCustomizer> configurationCustomizers) {
            var config = new InheritingConfiguration();
            configurationCustomizers.orderedStream().forEach(customizer -> customizer.customize(config));
            return config;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ModelMapper.class, SpringIntegration.class})
    static class ModelMapperSpringProviderConfiguration {

        /**
         * Build {@link Provider} for Spring Integration.
         *
         * @param beanFactory Spring Bean Factory
         * @return Spring Provider
         */
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(
                prefix = ModelMapperProperties.PROPERTIES_PREFIX,
                name = ModelMapperProperties.PROPERTIES_SPRING_PROVIDER_ENABLED)
        Provider<?> springProvider(BeanFactory beanFactory) {
            return SpringIntegration.fromSpring(beanFactory);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ModelMapper.class, RecordModule.class})
    static class ModelMapperModuleRecordConfiguration {

        @Bean
        ConfigurationCustomizer recordValueReaderCustomizer() {
            return configuration -> configuration.addValueReader(new RecordValueReader());
        }
    }
}
