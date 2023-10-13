package io.github.vaatech.modelmapper;

import org.modelmapper.PropertyMap;

public class PropertyMapConfigurer<S, D> extends ModelMapperConfigurer {

    private PropertyMap<S, D> propertyMap;

    public PropertyMapConfigurer<S, D> with(PropertyMap<S, D> propertyMap) {
        this.propertyMap = propertyMap;
        return this;
    }

    @Override
    void configure() {

        if (propertyMap != null) {
            getModelMapper().addMappings(propertyMap);
        }
    }
}