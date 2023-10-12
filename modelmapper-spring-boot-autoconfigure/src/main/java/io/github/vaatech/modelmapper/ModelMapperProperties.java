package io.github.vaatech.modelmapper;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NamingConvention;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = ModelMapperProperties.PROPERTIES_PREFIX)
public class ModelMapperProperties {
    public static final String PROPERTIES_PREFIX = "modelmapper";
    public static final String PROPERTIES_SPRING_PROVIDER_ENABLED = "spring-provider-enabled";

    /**
     * Sets the tokenizer to be applied to destination property and class names during the matching
     * process.
     */
    private NameTokenizer destinationNameTokenizer;
    /**
     * Sets the tokenizer to be applied to source property and class names during the matching
     * process.
     */
    private NameTokenizer sourceNameTokenizer;

    /**
     * Sets the name transformer used to transform destination property and class names during the
     * matching process.
     */
    private NameTransformer destinationNameTransformer;

    /**
     * Sets the name transformer used to transform source property and class names during the matching
     * process.
     */
    private NameTransformer sourceNameTransformer;

    /**
     * Sets the convention used to identify destination property names during the matching process.
     */
    private NamingConvention destinationNamingConvention;

    /**
     * Sets the convention used to identify source property names during the matching process.
     */
    private NamingConvention sourceNamingConvention;

    /**
     * Determines how source and destination tokens are matched
     */
    private MatchingStrategy matchingStrategy;

    /**
     * Determines which fields are eligible for matching based on accessibility
     */
    private AccessLevel fieldAccessLevel;

    /**
     * Determines which methods are eligible for matching based on accessibility
     */
    private AccessLevel methodAccessLevel;

    /**
     * Indicates whether fields are eligible for matching
     */
    private Boolean fieldMatchingEnabled;

    /**
     * Determines whether destination properties that match more than one source property should be
     * ignored
     */
    private Boolean ambiguityIgnored;

    /**
     * Determines whether ConditionalConverters must define a full match in order to be applied
     */
    private Boolean fullTypeMatchingRequired;

    /**
     * Determines whether the implicit mapping (mapping the models intelligently) should be enabled
     */
    private Boolean implicitMatchingEnabled;

    /**
     * Determines if the implicit mapping should map the nested properties, we strongly recommend to
     * disable this option while you are mapping a model contains circular reference
     */
    private Boolean preferNestedProperties;

    /**
     * Determines whether a property should be skipped or not when the property value is null
     */
    private Boolean skipNullEnabled;

    /**
     * Determines whether the destination items should be replaced or merged while source and
     * destination have different size
     */
    private Boolean collectionsMergeEnabled;

    /**
     * Sets whether deep copy should be enabled. When false (default), ModelMapper will copy the
     * reference to the destination object of a property if they have same type. When true,
     * ModelMapper will deep-copy the property to destination object.
     */
    private Boolean deepCopyEnabled;

    /**
     * ModelMapper will attempt to match every destination property to a source property, there will
     * occasionally be destination properties that it cannot find matches for. To verify that all
     * destination properties are matched, enable call to the validate method:
     */
    private boolean validateEnabled = false;

    /**
     * ModelMapper’s Spring integration allows for the provisioning of destination objects to be
     * delegated to a Spring BeanFactory during the mapping process. This is useful when destination
     * objects have complex creation requirements, such as when they require constructor arguments or
     * complex post-instantiation initialization.
     */
    private boolean springProviderEnabled = false;
}
