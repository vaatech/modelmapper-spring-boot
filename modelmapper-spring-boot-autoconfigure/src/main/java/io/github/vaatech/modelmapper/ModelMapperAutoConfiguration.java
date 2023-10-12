package io.github.vaatech.modelmapper;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.spring.SpringIntegration;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(ModelMapperProperties.class)
@Import({ModelMapperPropertiesConfiguration.class, ModelMapperBuilderStandardConfiguration.class})
@ConditionalOnClass(ModelMapper.class)
public class ModelMapperAutoConfiguration {

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

    @Configuration(proxyBeanMethods = false)
    static class ModelMapperBuilderConfiguration {

        @Bean
        @ConditionalOnMissingBean
        ModelMapperBuilder modelMapperBuilder(ModelMapperBuilderCustomizers customizers) {
            final ModelMapperBuilder builder = ModelMapperBuilder.mapper();
            return customizers.customize(builder);
        }

        @Bean
        @ConditionalOnMissingBean
        ModelMapperBuilderCustomizers modelMapperBuilderCustomizers(
                ObjectProvider<ModelMapperBuilderCustomizer> customizers) {
            return new ModelMapperBuilderCustomizers(customizers);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class ModelMapperSpringProviderConfiguration {

        /**
         * Build {@link Provider} for Spring Integration.
         *
         * @param beanFactory Spring Bean Factory
         * @return Spring Provider
         */
        @Bean
        @ConditionalOnProperty(
                prefix = ModelMapperProperties.PROPERTIES_PREFIX,
                name = ModelMapperProperties.PROPERTIES_SPRING_PROVIDER_ENABLED)
        public Provider<?> springProvider(BeanFactory beanFactory) {
            return SpringIntegration.fromSpring(beanFactory);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class ModelMapperConfiguration {

        @Bean
        @Primary
        @ConditionalOnMissingBean
        ModelMapper modelMapper(ModelMapperProperties properties, ModelMapperBuilder builder) {

            log.info("Configure ModelMapper with ModelMapperAutoConfiguration.");
            final ModelMapper modelMapper = builder.build();

            if (properties.isValidateEnabled()) {
                modelMapper.validate();
                log.info("Validate ModelMapper Configuration succeed.");
            }

            loggingConfiguration(modelMapper);
            return modelMapper;
        }
    }
}
