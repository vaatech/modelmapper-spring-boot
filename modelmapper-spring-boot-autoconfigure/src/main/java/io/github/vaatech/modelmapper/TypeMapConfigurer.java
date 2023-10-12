package io.github.vaatech.modelmapper;

import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration;
import org.modelmapper.internal.util.Assert;

public class TypeMapConfigurer<S, D> extends ModelMapperConfigurer {
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

    public TypeMapConfigurer<S, D> configuration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    public TypeMapConfigurer<S, D> name(String typeMapName) {
        this.typeMapName = typeMapName;
        return this;
    }

    public TypeMapConfigurer<S, D> customize(Customizer<TypeMap<S, D>> typeMapCustomizer) {
        this.typeMapCustomizer = typeMapCustomizer;
        return this;
    }

    /**
     * If {@code this.configuration} is present, it will be used to create the new type map and if
     * there is one already created with the same source, destination and name it will throw an
     * exception.
     *
     * @throws IllegalStateException if a TypeMap already exists for {@code sourceType} and {@code
     *                               destinationType}
     */
    @Override
    void configure() {
        TypeMap<S, D> typeMap;

        if (typeMapName == null && configuration == null) {
            typeMap = getModelMapper().typeMap(sourceType, destinationType);
        } else if (configuration == null) {
            typeMap = getModelMapper().typeMap(sourceType, destinationType, typeMapName);
        } else if (typeMapName == null) {
            typeMap = getModelMapper().createTypeMap(sourceType, destinationType, configuration);
        } else {
            typeMap = getModelMapper().createTypeMap(sourceType, destinationType, typeMapName, configuration);
        }

        if (typeMapCustomizer != null) {
            typeMapCustomizer.customize(typeMap);
        }
    }
}
