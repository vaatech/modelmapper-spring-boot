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
    StandardModelMapperBuilderCustomizer standardModelMapperBuilderCustomizer(ModelMapperProperties modelMapperProperties) {
        return new StandardModelMapperBuilderCustomizer(modelMapperProperties);
    }

    static final class StandardModelMapperBuilderCustomizer implements ModelMapperBuilderCustomizer, Ordered {
        private final ModelMapperProperties props;

        StandardModelMapperBuilderCustomizer(ModelMapperProperties props) {
            this.props = props;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public void customize(ModelMapperBuilder builder) {
            builder.configuration(cnf -> {
                nullSafeSet(ModelMapperProperties::getDestinationNameTokenizer, cnf::setDestinationNameTokenizer);
                nullSafeSet(ModelMapperProperties::getSourceNameTokenizer, cnf::setSourceNameTokenizer);
                nullSafeSet(ModelMapperProperties::getDestinationNameTransformer, cnf::setDestinationNameTransformer);
                nullSafeSet(ModelMapperProperties::getSourceNameTransformer, cnf::setSourceNameTransformer);
                nullSafeSet(ModelMapperProperties::getDestinationNamingConvention, cnf::setDestinationNamingConvention);
                nullSafeSet(ModelMapperProperties::getSourceNamingConvention, cnf::setSourceNamingConvention);
                nullSafeSet(ModelMapperProperties::getMatchingStrategy, cnf::setMatchingStrategy);
                nullSafeSet(ModelMapperProperties::getFieldAccessLevel, cnf::setFieldAccessLevel);
                nullSafeSet(ModelMapperProperties::getMethodAccessLevel, cnf::setMethodAccessLevel);
                nullSafeSet(ModelMapperProperties::getFieldMatchingEnabled, cnf::setFieldMatchingEnabled);
                nullSafeSet(ModelMapperProperties::getAmbiguityIgnored, cnf::setAmbiguityIgnored);
                nullSafeSet(ModelMapperProperties::getFullTypeMatchingRequired, cnf::setFullTypeMatchingRequired);
                nullSafeSet(ModelMapperProperties::getImplicitMatchingEnabled, cnf::setImplicitMappingEnabled);
                nullSafeSet(ModelMapperProperties::getPreferNestedProperties, cnf::setPreferNestedProperties);
                nullSafeSet(ModelMapperProperties::getSkipNullEnabled, cnf::setSkipNullEnabled);
                nullSafeSet(ModelMapperProperties::getCollectionsMergeEnabled, cnf::setCollectionsMergeEnabled);
                nullSafeSet(ModelMapperProperties::getDeepCopyEnabled, cnf::setDeepCopyEnabled);
            });
        }

        private <T> void nullSafeSet(Function<ModelMapperProperties, T> property, Consumer<T> setter) {
            Optional.ofNullable(property.apply(props)).ifPresent(setter);
        }
    }
}
