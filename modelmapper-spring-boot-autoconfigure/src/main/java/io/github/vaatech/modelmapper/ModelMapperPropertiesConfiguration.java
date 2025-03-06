package io.github.vaatech.modelmapper;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NamingConvention;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ObjectUtils;

@Slf4j
@ConditionalOnClass(ModelMapper.class)
@Configuration(proxyBeanMethods = false)
class ModelMapperPropertiesConfiguration {

    private static final String LOG_FORMAT_INVALID = "{} not allow [{}], convert to null.";

    /**
     * Configure {@link Converter} bean from {@link String} to {@link NameTokenizer}.
     */
    @Configuration(proxyBeanMethods = false)
    static class NameTokenizerConverterConfiguration {

        @Bean
        @ConfigurationPropertiesBinding
        NameTokenizerConverter nameTokenizerConverter() {
            return new NameTokenizerConverter();
        }

        static class NameTokenizerConverter implements Converter<String, NameTokenizer> {
            @Override
            public NameTokenizer convert(String source) {
                if (ObjectUtils.isEmpty(source)) {
                    return null;
                }
                if (source.equalsIgnoreCase(NameTokenizers.CAMEL_CASE.toString())) {
                    return NameTokenizers.CAMEL_CASE;
                }
                if (source.equalsIgnoreCase(NameTokenizers.UNDERSCORE.toString())) {
                    return NameTokenizers.UNDERSCORE;
                }
                log.error(LOG_FORMAT_INVALID, NameTokenizer.class.getName(), source);
                return null;
            }
        }
    }

    /**
     * Configure {@link Converter} from {@link String} to {@link NameTransformer}.
     */
    @Configuration(proxyBeanMethods = false)
    static class NameTransformerConverterConfiguration {

        @Bean
        @ConfigurationPropertiesBinding
        NameTransformerConverter nameTransformerConverter() {
            return new NameTransformerConverter();
        }

        static class NameTransformerConverter implements Converter<String, NameTransformer> {
            @Override
            public NameTransformer convert(String source) {
                if (ObjectUtils.isEmpty(source)) {
                    return null;
                }
                if (source.equalsIgnoreCase(NameTransformers.JAVABEANS_ACCESSOR.toString())) {
                    return NameTransformers.JAVABEANS_ACCESSOR;
                }
                if (source.equalsIgnoreCase(NameTransformers.JAVABEANS_MUTATOR.toString())) {
                    return NameTransformers.JAVABEANS_MUTATOR;
                }
                log.error(LOG_FORMAT_INVALID, NameTransformer.class.getName(), source);
                return null;
            }
        }
    }

    /**
     * Configure {@link Converter} from {@link String} to {@link NamingConvention}.
     */
    @Configuration(proxyBeanMethods = false)
    static class NamingConventionConverterConfiguration {

        @Bean
        @ConfigurationPropertiesBinding
        NamingConventionConverter namingConventionConverter() {
            return new NamingConventionConverter();
        }

        static class NamingConventionConverter implements Converter<String, NamingConvention> {
            @Override
            public NamingConvention convert(String source) {
                if (ObjectUtils.isEmpty(source)) {
                    return null;
                }
                if (source.equalsIgnoreCase(NamingConventions.JAVABEANS_ACCESSOR.toString())) {
                    return NamingConventions.JAVABEANS_ACCESSOR;
                }
                if (source.equalsIgnoreCase(NamingConventions.JAVABEANS_MUTATOR.toString())) {
                    return NamingConventions.JAVABEANS_MUTATOR;
                }
                if (source.equalsIgnoreCase(NamingConventions.NONE.toString())) {
                    return NamingConventions.NONE;
                }
                log.error(LOG_FORMAT_INVALID, NamingConvention.class.getName(), source);
                return null;
            }
        }
    }

    /**
     * Configure {@link Converter} bean from {@link String} to {@link MatchingStrategy}.
     */
    @Configuration(proxyBeanMethods = false)
    static class MatchingStrategyConverterConfiguration {

        @Bean
        @ConfigurationPropertiesBinding
        MatchingStrategyConverter matchingStrategyConverter() {
            return new MatchingStrategyConverter();
        }

        static class MatchingStrategyConverter implements Converter<String, MatchingStrategy> {
            @Override
            public MatchingStrategy convert(String source) {
                if (ObjectUtils.isEmpty(source)) {
                    return null;
                }
                if (source.equalsIgnoreCase(MatchingStrategies.LOOSE.toString())) {
                    return MatchingStrategies.LOOSE;
                }
                if (source.equalsIgnoreCase(MatchingStrategies.STANDARD.toString())) {
                    return MatchingStrategies.STANDARD;
                }
                if (source.equalsIgnoreCase(MatchingStrategies.STRICT.toString())) {
                    return MatchingStrategies.STRICT;
                }
                log.error(LOG_FORMAT_INVALID, MatchingStrategy.class.getName(), source);
                return null;
            }
        }
    }
}
