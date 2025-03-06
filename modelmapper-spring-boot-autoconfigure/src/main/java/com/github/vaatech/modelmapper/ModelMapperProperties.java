package com.github.vaatech.modelmapper;

import lombok.Data;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NamingConvention;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = ModelMapperProperties.PROPERTIES_PREFIX)
public class ModelMapperProperties {
    public static final String PROPERTIES_PREFIX = "modelmapper";
    public static final String PROPERTIES_SPRING_PROVIDER_ENABLED = "spring-provider-enabled";

    private NameTokenizer sourceNameTokenizer = NameTokenizers.CAMEL_CASE;
    private NameTokenizer destinationNameTokenizer = NameTokenizers.CAMEL_CASE;
    private NamingConvention sourceNamingConvention = NamingConventions.JAVABEANS_ACCESSOR;
    private NamingConvention destinationNamingConvention = NamingConventions.JAVABEANS_MUTATOR;
    private NameTransformer sourceNameTransformer = NameTransformers.JAVABEANS_ACCESSOR;
    private NameTransformer destinationNameTransformer = NameTransformers.JAVABEANS_MUTATOR;
    private MatchingStrategy matchingStrategy = MatchingStrategies.STANDARD;
    private AccessLevel fieldAccessLevel = AccessLevel.PUBLIC;
    private AccessLevel methodAccessLevel = AccessLevel.PUBLIC;
    private Boolean fieldMatchingEnabled = Boolean.FALSE;
    private Boolean ambiguityIgnored = Boolean.FALSE;
    private Boolean fullTypeMatchingRequired = Boolean.FALSE;
    private Boolean implicitMatchingEnabled = Boolean.TRUE;
    private Boolean preferNestedProperties = Boolean.TRUE;
    private Boolean skipNullEnabled = Boolean.FALSE;
    private Boolean useOSGiClassLoaderBridging = Boolean.FALSE;
    private Boolean collectionsMergeEnabled = Boolean.FALSE;
    private Boolean deepCopyEnabled = Boolean.FALSE;
    private Boolean validateEnabled = Boolean.FALSE;
    private Boolean springProviderEnabled = Boolean.FALSE;
}
