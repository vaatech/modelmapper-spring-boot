package com.github.vaatech.modelmapper;

import org.modelmapper.Converter;

public class ConverterConfigurer<S, D> extends ModelMapperConfigurer {

    private Converter<S, D> converter;
    private Class<S> sourceType;
    private Class<D> destinationType;

    public  ConverterConfigurer<S, D> with(Converter<S, D> converter) {
        this.converter = converter;
        return this;
    }

    public ConverterConfigurer<S, D> with(Class<S> sourceType, Class<D> destinationType) {
        this.sourceType = sourceType;
        this.destinationType = destinationType;
        return this;
    }

    @Override
    void configure() {
        if (converter == null) return;

        if (sourceType != null && destinationType != null) {
            getModelMapper().addConverter(converter, sourceType, destinationType);
            return;
        }

        getModelMapper().addConverter(converter);
    }
}
