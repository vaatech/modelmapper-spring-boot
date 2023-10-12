package io.github.vaatech.modelmapper;

import org.modelmapper.ModelMapper;
import org.springframework.util.Assert;

abstract class ModelMapperConfigurer {

    private ModelMapperBuilder modelMapperBuilder;
    private ModelMapper modelMapper;

    void configure() {}

    public ModelMapperBuilder and() {
        return getBuilder();
    }

    final ModelMapperBuilder getBuilder() {
        Assert.state(this.modelMapperBuilder != null, "modelMapperBuilder cannot be null");
        return this.modelMapperBuilder;
    }

    void setBuilder(ModelMapperBuilder builder) {
        this.modelMapperBuilder = builder;
    }

    final ModelMapper getModelMapper() {
        Assert.state(this.modelMapper != null, "modelMapper cannot be null");
        return this.modelMapper;
    }

    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
