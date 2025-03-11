package io.github.vaatech.modelmapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration;
import org.modelmapper.internal.util.Assert;

class TypeMapConfigurer<S, D> {

    private final Class<S> sourceType;
    private final Class<D> destinationType;
    private String typeMapName;
    private Configuration configuration;
    private Customizer<TypeMap<S, D>> typeMapCustomizer;

    TypeMapConfigurer(Class<S> sourceType, Class<D> destinationType) {
        Assert.notNull(sourceType, "sourceType");
        Assert.notNull(destinationType, "destinationType");
        this.sourceType = sourceType;
        this.destinationType = destinationType;
    }

    TypeMapConfigurer<S, D> name(String typeMapName) {
        if (this.typeMapName != null) {
            throw new IllegalStateException("TypeMap name already set");
        }
        Assert.notNull(typeMapName, "typeMapName");
        this.typeMapName = typeMapName;
        return this;
    }

    TypeMapConfigurer<S, D> configuration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    TypeMapConfigurer<S, D> typeMap(Customizer<TypeMap<S, D>> typeMapCustomizer) {
        this.typeMapCustomizer = (this.typeMapCustomizer != null
                ? this.typeMapCustomizer.andThen(typeMapCustomizer)
                : typeMapCustomizer);
        return this;
    }

    void configure(final ModelMapper modelMapper) {
        TypeMap<S, D> typeMap;

        if (typeMapName == null && configuration == null) {
            typeMap = modelMapper.createTypeMap(sourceType, destinationType);
        } else if (configuration == null) {
            typeMap = modelMapper.createTypeMap(sourceType, destinationType, typeMapName);
        } else if (typeMapName == null) {
            typeMap = modelMapper.createTypeMap(sourceType, destinationType, configuration);
        } else {
            typeMap = modelMapper.createTypeMap(sourceType, destinationType, typeMapName, configuration);
        }

        if (typeMapCustomizer != null) {
            typeMapCustomizer.customize(typeMap);
        }
    }
}
