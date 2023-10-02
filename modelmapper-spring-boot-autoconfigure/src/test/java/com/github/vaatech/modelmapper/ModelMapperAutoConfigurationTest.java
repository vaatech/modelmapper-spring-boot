package com.github.vaatech.modelmapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.Provider;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelMapperAutoConfigurationTest {

    abstract static class BaseModelMapperMatchingStrategyTest extends BaseModelMapperTest {

        @Test
        void assertMappingStrategy() {
            MatchingStrategy matchingStrategy = modelMapper.getConfiguration().getMatchingStrategy();
            assertMatchingStrategySet(matchingStrategy);
        }

        protected abstract void assertMatchingStrategySet(MatchingStrategy matchingStrategy);
    }

    @ExtendWith(OutputCaptureExtension.class)
    abstract static class BaseModelMapperPropertyTest extends BaseModelMapperTest {
    }

    @Nested
    @DisplayName("AutoConfigured ModelMapperBuilder with standardModelMapperBuilderCustomizer bean")
    class DefaultModelMapperBuilderConfigurationTest extends BaseModelMapperTest {

        @Test
        void whenContextLoadsStandardConfigurationShouldBeSet() {
            String[] beanNamesForType =
                    BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, ModelMapperBuilderCustomizer.class);
            assertThat(beanNamesForType)
                    .as("ModelMapperBuilderCustomized should be present")
                    .hasSize(1)
                    .contains("standardModelMapperBuilderCustomizer");
        }
    }

    @Nested
    @TestPropertySource(properties = {"modelmapper.matching-strategy=Strict"})
    @DisplayName("AutoConfigured ModelMapper with strict mapping strategy")
    class StrictMappingStrategyTest extends BaseModelMapperMatchingStrategyTest {

        @Override
        protected void assertMatchingStrategySet(MatchingStrategy matchingStrategy) {
            assertThat(matchingStrategy).isNotNull();
            assertThat(matchingStrategy.isExact()).isTrue();
            assertThat(matchingStrategy.toString()).isEqualTo(MatchingStrategies.STRICT.toString());
        }
    }

    @Nested
    @DisplayName("AutoConfigured ModelMapper with default mapping strategy")
    class DefaultMappingStrategyTest extends BaseModelMapperMatchingStrategyTest {

        @Override
        protected void assertMatchingStrategySet(MatchingStrategy matchingStrategy) {
            assertThat(matchingStrategy).isNotNull();
            assertThat(matchingStrategy.isExact()).isFalse();
            assertThat(matchingStrategy.toString()).isEqualTo(MatchingStrategies.STANDARD.toString());
        }
    }

    @Nested
    @TestPropertySource(
            properties = {
                    "modelmapper.source-name-tokenizer=Camel Case",
                    "modelmapper.source-name-transformer=Javabeans Accessor",
                    "modelmapper.source-naming-convention=Javabeans Accessor",
                    "modelmapper.destination-name-tokenizer=Camel Case",
                    "modelmapper.destination-name-transformer=Javabeans Accessor",
                    "modelmapper.destination-naming-convention=Javabeans Accessor",
                    "modelmapper.matching-strategy=Loose",
                    "modelmapper.field-access-level=public",
                    "modelmapper.method-access-level=public",
                    "modelmapper.field-matching-enabled=true",
                    "modelmapper.ambiguity-ignored=true",
                    "modelmapper.full-type-matching-required=true",
                    "modelmapper.implicit-matching-enabled=true",
                    "modelmapper.skip-null-enabled=true",
                    "modelmapper.collections-merge-enabled=true",
                    "modelmapper.deep-copy-enabled=true",
                    "modelmapper.spring-provider-enabled=true",
                    "modelmapper.validate-enabled=true"
            })
    @DisplayName("Property Test Case 1 with All Properties Set")
    class PropertiesTest1 extends BaseModelMapperPropertyTest {

        @Test
        void shouldSetProperties(CapturedOutput output) {
            Configuration configuration = modelMapper.getConfiguration();
            assertThat(configuration.getSourceNameTokenizer()).isEqualTo(NameTokenizers.CAMEL_CASE);
            assertThat(configuration.getSourceNameTransformer()).isEqualTo(NameTransformers.JAVABEANS_ACCESSOR);
            assertThat(configuration.getSourceNamingConvention()).isEqualTo(NamingConventions.JAVABEANS_ACCESSOR);
            assertThat(configuration.getDestinationNameTokenizer()).isEqualTo(NameTokenizers.CAMEL_CASE);
            assertThat(configuration.getDestinationNameTransformer()).isEqualTo(NameTransformers.JAVABEANS_ACCESSOR);
            assertThat(configuration.getDestinationNamingConvention()).isEqualTo(NamingConventions.JAVABEANS_ACCESSOR);
            assertThat(configuration.getMatchingStrategy()).isEqualTo(MatchingStrategies.LOOSE);
            assertThat(configuration.getFieldAccessLevel()).isEqualTo(Configuration.AccessLevel.PUBLIC);
            assertThat(configuration.getMethodAccessLevel()).isEqualTo(Configuration.AccessLevel.PUBLIC);
            assertThat(configuration.isFieldMatchingEnabled()).isEqualTo(true);
            assertThat(configuration.isAmbiguityIgnored()).isEqualTo(true);
            assertThat(configuration.isFullTypeMatchingRequired()).isEqualTo(true);
            assertThat(configuration.isImplicitMappingEnabled()).isEqualTo(true);
            assertThat(configuration.isSkipNullEnabled()).isEqualTo(true);
            assertThat(configuration.isCollectionsMergeEnabled()).isEqualTo(true);
            assertThat(configuration.isDeepCopyEnabled()).isEqualTo(true);
            assertThat(properties.isSpringProviderEnabled()).isEqualTo(true);

            String[] beanNamesForType = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, Provider.class);
            assertThat(beanNamesForType)
                    .as("Auto-configured spring provider should be present")
                    .hasSize(1)
                    .contains("springProvider");

            assertThat(properties.isValidateEnabled()).isEqualTo(true);
            assertThat(output).contains("Validate ModelMapper Configuration succeed.");
        }
    }

    @Nested
    @TestPropertySource(
            properties = {
                    "modelmapper.source-name-tokenizer=Underscore",
                    "modelmapper.source-name-transformer=Javabeans Mutator",
                    "modelmapper.source-naming-convention=Javabeans Mutator",
                    "modelmapper.destination-name-tokenizer=Underscore",
                    "modelmapper.destination-name-transformer=Javabeans Mutator",
                    "modelmapper.destination-naming-convention=Javabeans Mutator",
                    "modelmapper.matching-strategy=Standard",
                    "modelmapper.field-access-level=protected",
                    "modelmapper.method-access-level=protected",
                    "modelmapper.field-matching-enabled=false",
                    "modelmapper.ambiguity-ignored=false",
                    "modelmapper.full-type-matching-required=false",
                    "modelmapper.implicit-matching-enabled=false",
                    "modelmapper.skip-null-enabled=false",
                    "modelmapper.collections-merge-enabled=false",
                    "modelmapper.deep-copy-enabled=false",
                    "modelmapper.spring-provider-enabled=false"
            })
    @DisplayName("Property Test Case 2 with All Properties Set")
    class PropertiesTest2 extends BaseModelMapperPropertyTest {

        @Test
        void shouldSetProperties(CapturedOutput output) {
            Configuration configuration = modelMapper.getConfiguration();
            assertThat(configuration.getSourceNameTokenizer()).isEqualTo(NameTokenizers.UNDERSCORE);
            assertThat(configuration.getSourceNameTransformer()).isEqualTo(NameTransformers.JAVABEANS_MUTATOR);
            assertThat(configuration.getSourceNamingConvention()).isEqualTo(NamingConventions.JAVABEANS_MUTATOR);
            assertThat(configuration.getDestinationNameTokenizer()).isEqualTo(NameTokenizers.UNDERSCORE);
            assertThat(configuration.getDestinationNameTransformer()).isEqualTo(NameTransformers.JAVABEANS_MUTATOR);
            assertThat(configuration.getDestinationNamingConvention()).isEqualTo(NamingConventions.JAVABEANS_MUTATOR);
            assertThat(configuration.getMatchingStrategy()).isEqualTo(MatchingStrategies.STANDARD);
            assertThat(configuration.getFieldAccessLevel()).isEqualTo(Configuration.AccessLevel.PROTECTED);
            assertThat(configuration.getMethodAccessLevel()).isEqualTo(Configuration.AccessLevel.PROTECTED);
            assertThat(configuration.isFieldMatchingEnabled()).isEqualTo(false);
            assertThat(configuration.isAmbiguityIgnored()).isEqualTo(false);
            assertThat(configuration.isFullTypeMatchingRequired()).isEqualTo(false);
            assertThat(configuration.isImplicitMappingEnabled()).isEqualTo(false);
            assertThat(configuration.isSkipNullEnabled()).isEqualTo(false);
            assertThat(configuration.isCollectionsMergeEnabled()).isEqualTo(false);
            assertThat(configuration.isDeepCopyEnabled()).isEqualTo(false);
            assertThat(configuration.getProvider()).isNull();
            assertThat(properties.isValidateEnabled()).isEqualTo(false);
            assertThat(output).doesNotContain("Validate ModelMapper Configuration succeed.");
        }
    }

    @Nested
    @TestPropertySource(
            properties = {
                    "modelmapper.source-naming-convention=None",
                    "modelmapper.destination-naming-convention=None",
                    "modelmapper.matching-strategy=Strict"
            })
    @DisplayName("Property Test Case 3 with Partial Properties Set")
    class PropertiesTest3 extends BaseModelMapperPropertyTest {

        @Test
        void shouldSetProperties() {
            Configuration configuration = modelMapper.getConfiguration();
            assertThat(configuration.getSourceNamingConvention()).isEqualTo(NamingConventions.NONE);
            assertThat(configuration.getDestinationNamingConvention()).isEqualTo(NamingConventions.NONE);
            assertThat(configuration.getMatchingStrategy()).isEqualTo(MatchingStrategies.STRICT);
        }
    }

    @Nested
    @TestPropertySource(
            properties = {
                    "modelmapper.source-name-tokenizer=",
                    "modelmapper.source-name-transformer=",
                    "modelmapper.source-naming-convention=",
                    "modelmapper.matching-strategy="
            })
    @DisplayName("Property Test Case 4 with Partial Properties Set")
    class PropertiesTest4 extends BaseModelMapperPropertyTest {

        @Test
        void shouldSetProperties() {
            Configuration configuration = modelMapper.getConfiguration();
            assertThat(configuration.getSourceNameTokenizer()).isEqualTo(NameTokenizers.CAMEL_CASE);
            assertThat(configuration.getSourceNameTransformer()).isEqualTo(NameTransformers.JAVABEANS_ACCESSOR);
            assertThat(configuration.getSourceNamingConvention()).isEqualTo(NamingConventions.JAVABEANS_ACCESSOR);
            assertThat(configuration.getMatchingStrategy()).isEqualTo(MatchingStrategies.STANDARD);
        }
    }

    @Nested
    @TestPropertySource(
            properties = {
                    "modelmapper.source-name-tokenizer=invalid",
                    "modelmapper.source-name-transformer=invalid",
                    "modelmapper.source-naming-convention=invalid",
                    "modelmapper.matching-strategy=invalid"
            })
    @DisplayName("Property Test Case 5 with Invalid Properties Set")
    class PropertiesTest5 extends BaseModelMapperPropertyTest {

        @Test
        void shouldSetProperties(CapturedOutput output) {
            Configuration configuration = modelMapper.getConfiguration();
            assertThat(configuration.getSourceNameTokenizer()).isEqualTo(NameTokenizers.CAMEL_CASE);
            assertThat(configuration.getSourceNameTransformer()).isEqualTo(NameTransformers.JAVABEANS_ACCESSOR);
            assertThat(configuration.getSourceNamingConvention()).isEqualTo(NamingConventions.JAVABEANS_ACCESSOR);
            assertThat(configuration.getMatchingStrategy()).isEqualTo(MatchingStrategies.STANDARD);
            assertThat(output).contains(
                    "org.modelmapper.spi.NameTokenizer not allow [invalid], convert to null.",
                    "org.modelmapper.spi.NameTransformer not allow [invalid], convert to null.",
                    "org.modelmapper.spi.NamingConvention not allow [invalid], convert to null.",
                    "org.modelmapper.spi.MatchingStrategy not allow [invalid], convert to null."
            );
        }
    }
}
