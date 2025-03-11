package io.github.vaatech.modelmapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Module;
import org.modelmapper.*;
import org.modelmapper.internal.InheritingConfiguration;
import org.modelmapper.spring.SpringIntegration;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Slf4j
@AutoConfiguration
@ConditionalOnClass(ModelMapper.class)
@EnableConfigurationProperties(ModelMapperProperties.class)
@Import({ModelMapperPropertiesConfiguration.class, ModelMapperBuilderStandardConfiguration.class})
public class ModelMapperAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @RequiredArgsConstructor
    @ConditionalOnClass(ModelMapper.class)
    static class ModelMapperConfiguration {

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

            loggingConfiguration(modelMapper);
            return modelMapper;
        }

        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        org.modelmapper.config.Configuration
        typeMapConfiguration(final ObjectProvider<ConfigurationCustomizer> configurationCustomizers) {
            var config = new InheritingConfiguration();
            configurationCustomizers.orderedStream().forEach(customizer -> customizer.customize(config));
            return config;
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
        @ConditionalOnBean(Provider.class)
        ModelMapperBuilderCustomizer providerCustomizer(ObjectProvider<Provider<?>> providerProvider) {
            return builder -> providerProvider.ifAvailable(builder::provider);
        }

        @Bean
        @ConditionalOnBean(Condition.class)
        ModelMapperBuilderCustomizer conditionCustomizer(ObjectProvider<Condition<?, ?>> conditionProvider) {
            return builder -> conditionProvider.ifAvailable(builder::condition);
        }

        @Bean
        @ConditionalOnBean(Converter.class)
        ModelMapperBuilderCustomizer convertersCustomizer(ObjectProvider<Converter<?, ?>> convertersProvider) {
            return builder -> convertersProvider.orderedStream().forEach(builder::converter);
        }

        @Bean
        @ConditionalOnBean(Module.class)
        ModelMapperBuilderCustomizer modulesCustomizer(ObjectProvider<Module> modulesProvider) {
            return builder -> modulesProvider.orderedStream().forEach(builder::module);
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

    private static void loggingConfiguration(ModelMapper modelMapper) {
        org.modelmapper.config.Configuration configuration = modelMapper.getConfiguration();
        log.debug("ModelMapper Configuration =======================================================");
        log.debug(" SourceNameTokenizer : {}", configuration.getSourceNameTokenizer());
        log.debug(" SourceNameTransformer : {}", configuration.getSourceNameTransformer());
        log.debug(" SourceNamingConvention : {}", configuration.getSourceNamingConvention());
        log.debug(" DestinationNameTokenizer : {}", configuration.getDestinationNameTokenizer());
        log.debug(" DestinationNameTransformer : {}", configuration.getDestinationNameTransformer());
        log.debug(" DestinationNamingConvention : {}", configuration.getDestinationNamingConvention());
        log.debug(" MatchingStrategy : {}", configuration.getMatchingStrategy());
        log.debug(" FieldAccessLevel : {}", configuration.getFieldAccessLevel());
        log.debug(" MethodAccessLevel : {}", configuration.getMethodAccessLevel());
        log.debug(" FieldMatchingEnabled : {}", configuration.isFieldMatchingEnabled());
        log.debug(" AmbiguityIgnored : {}", configuration.isAmbiguityIgnored());
        log.debug(" FullTypeMatchingRequired : {}", configuration.isFullTypeMatchingRequired());
        log.debug(" ImplicitMappingEnabled : {}", configuration.isImplicitMappingEnabled());
        log.debug(" SkipNullEnabled : {}", configuration.isSkipNullEnabled());
        log.debug(" CollectionsMergeEnabled : {}", configuration.isCollectionsMergeEnabled());
        log.debug(" UseOSGiClassLoaderBridging : {}", configuration.isUseOSGiClassLoaderBridging());
        log.debug(" DeepCopyEnabled : {}", configuration.isDeepCopyEnabled());
        log.debug(" Provider : {}", configuration.getProvider());
        log.debug(" PropertyCondition : {}", configuration.getPropertyCondition());
        log.debug(" TypeMaps :");
        modelMapper.getTypeMaps().forEach(typeMap -> log.debug("  {}", typeMap));
        log.debug(" Converters :");
        configuration.getConverters().forEach(converter -> log.debug("  {}", converter));
        log.debug("ModelMapper Configuration =======================================================");
    }
}
