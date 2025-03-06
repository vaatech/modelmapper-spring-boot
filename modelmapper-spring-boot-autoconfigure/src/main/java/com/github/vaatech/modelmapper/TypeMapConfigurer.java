package com.github.vaatech.modelmapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration;
import org.modelmapper.internal.util.Assert;

public class TypeMapConfigurer<S, D> {

    private final Class<S> sourceType;
    private final Class<D> destinationType;
    private String typeMapName;
    private Customizer<Configuration> configurationCustomizer;
    private Customizer<TypeMap<S, D>> typeMapCustomizer;

    TypeMapConfigurer(Class<S> sourceType, Class<D> destinationType) {
        Assert.notNull(sourceType, "sourceType");
        Assert.notNull(destinationType, "destinationType");
        this.sourceType = sourceType;
        this.destinationType = destinationType;
    }

    public TypeMapConfigurer<S, D> name(String typeMapName) {
        this.typeMapName = typeMapName;
        return this;
    }

    public TypeMapConfigurer<S, D> configuration(Customizer<Configuration> configurationCustomizer) {
        this.configurationCustomizer = (this.configurationCustomizer != null
                ? this.configurationCustomizer.andThen(configurationCustomizer)
                : configurationCustomizer);
        return this;
    }

    public TypeMapConfigurer<S, D> customize(Customizer<TypeMap<S, D>> typeMapCustomizer) {
        this.typeMapCustomizer = (this.typeMapCustomizer != null
                ? this.typeMapCustomizer.andThen(typeMapCustomizer)
                : typeMapCustomizer);
        return this;
    }

    /**
     * If {@code this.configuration} is present, it will be used to create the new type map and if
     * there is one already created with the same source, destination and name it will throw an exception.
     */
    void configure(final ModelMapper modelMapper) {
        TypeMap<S, D> typeMap;

        Configuration configuration = modelMapper.getConfiguration().copy();
        if (configurationCustomizer != null) {
            configurationCustomizer.customize(configuration);
        }

        if (typeMapName == null) {
            typeMap = modelMapper.createTypeMap(sourceType, destinationType, configuration);
        } else {
            typeMap = modelMapper.createTypeMap(sourceType, destinationType, typeMapName, configuration);
        }

        if (typeMapCustomizer != null) {
            typeMapCustomizer.customize(typeMap);
        }
    }
}
