package io.github.vaatech.modelmapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Configuration(proxyBeanMethods = false)
class ModelMapperBuilderStandardConfiguration {

    @Bean
    StandardModelMapperConfigurationCustomizer
    standardModelMapperConfigurationCustomizer(ModelMapperProperties modelMapperProperties) {
        return new StandardModelMapperConfigurationCustomizer(modelMapperProperties);
    }

    static final class StandardModelMapperConfigurationCustomizer implements ConfigurationCustomizer, Ordered {
        private final ModelMapperProperties props;

        StandardModelMapperConfigurationCustomizer(ModelMapperProperties props) {
            this.props = props;
        }

        @Override
        public int getOrder() {
            // To apply default configurations first
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public void customize(org.modelmapper.config.Configuration cnf) {
            nullSafeSet(ModelMapperProperties::getSourceNameTokenizer, cnf::setSourceNameTokenizer);
            nullSafeSet(ModelMapperProperties::getDestinationNameTokenizer, cnf::setDestinationNameTokenizer);
            nullSafeSet(ModelMapperProperties::getSourceNamingConvention, cnf::setSourceNamingConvention);
            nullSafeSet(ModelMapperProperties::getDestinationNamingConvention, cnf::setDestinationNamingConvention);
            nullSafeSet(ModelMapperProperties::getSourceNameTransformer, cnf::setSourceNameTransformer);
            nullSafeSet(ModelMapperProperties::getDestinationNameTransformer, cnf::setDestinationNameTransformer);
            nullSafeSet(ModelMapperProperties::getMatchingStrategy, cnf::setMatchingStrategy);
            nullSafeSet(ModelMapperProperties::getFieldAccessLevel, cnf::setFieldAccessLevel);
            nullSafeSet(ModelMapperProperties::getMethodAccessLevel, cnf::setMethodAccessLevel);
            nullSafeSet(ModelMapperProperties::getFieldMatchingEnabled, cnf::setFieldMatchingEnabled);
            nullSafeSet(ModelMapperProperties::getAmbiguityIgnored, cnf::setAmbiguityIgnored);
            nullSafeSet(ModelMapperProperties::getFullTypeMatchingRequired, cnf::setFullTypeMatchingRequired);
            nullSafeSet(ModelMapperProperties::getImplicitMatchingEnabled, cnf::setImplicitMappingEnabled);
            nullSafeSet(ModelMapperProperties::getPreferNestedProperties, cnf::setPreferNestedProperties);
            nullSafeSet(ModelMapperProperties::getSkipNullEnabled, cnf::setSkipNullEnabled);
            nullSafeSet(ModelMapperProperties::getUseOSGiClassLoaderBridging, cnf::setUseOSGiClassLoaderBridging);
            nullSafeSet(ModelMapperProperties::getCollectionsMergeEnabled, cnf::setCollectionsMergeEnabled);
            nullSafeSet(ModelMapperProperties::getDeepCopyEnabled, cnf::setDeepCopyEnabled);
        }

        private <T> void nullSafeSet(Function<ModelMapperProperties, T> property, Consumer<T> setter) {
            Optional.ofNullable(property.apply(props)).ifPresent(setter);
        }
    }
}
