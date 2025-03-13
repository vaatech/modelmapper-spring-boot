package io.github.vaatech.modelmapper;

import org.modelmapper.Module;
import org.modelmapper.*;
import org.modelmapper.config.Configuration;

import java.lang.reflect.Type;
import java.util.Collection;

class ImmutableModelMapper extends ModelMapper {

    private final ModelMapper delegate;

    private static final String ERROR_MSG = "ModelMapper cannot be configured. Use customizers for creating and configuring type maps.";

    ImmutableModelMapper(ModelMapper modelMapper) {
        this.delegate = modelMapper;
    }

    @Override
    public <S, D> void addConverter(Converter<S, D> converter) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> void addConverter(Converter<S, D> converter, Class<S> sourceType, Class<D> destinationType) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> addMappings(PropertyMap<S, D> propertyMap) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> createTypeMap(Class<S> sourceType, Class<D> destinationType) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> createTypeMap(Class<S> sourceType, Class<D> destinationType, Configuration configuration) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> createTypeMap(Class<S> sourceType, Class<D> destinationType, String typeMapName) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> createTypeMap(Class<S> sourceType, Class<D> destinationType, String typeMapName, Configuration configuration) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> createTypeMap(S source, Class<D> destinationType) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> createTypeMap(S source, Class<D> destinationType, Configuration configuration) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> createTypeMap(S source, Class<D> destinationType, String typeMapName) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> createTypeMap(S source, Class<D> destinationType, String typeMapName, Configuration configuration) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public Configuration getConfiguration() {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> getTypeMap(Class<S> sourceType, Class<D> destinationType) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> getTypeMap(Class<S> sourceType, Class<D> destinationType, String typeMapName) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> typeMap(Class<S> sourceType, Class<D> destinationType) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> typeMap(Class<S> sourceType, Class<D> destinationType, String typeMapName) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> emptyTypeMap(Class<S> sourceType, Class<D> destinationType) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> emptyTypeMap(Class<S> sourceType, Class<D> destinationType, String typeMapName) {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public Collection<TypeMap<?, ?>> getTypeMaps() {
        throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        return delegate.map(source, destinationType);
    }

    @Override
    public <D> D map(Object source, Class<D> destinationType, String typeMapName) {
        return delegate.map(source, destinationType, typeMapName);
    }

    @Override
    public void map(Object source, Object destination) {
        delegate.map(source, destination);
    }

    @Override
    public void map(Object source, Object destination, String typeMapName) {
        delegate.map(source, destination, typeMapName);
    }

    @Override
    public <D> D map(Object source, Type destinationType) {
        return delegate.map(source, destinationType);
    }

    @Override
    public <D> D map(Object source, Type destinationType, String typeMapName) {
        return delegate.map(source, destinationType, typeMapName);
    }

    @Override
    public void validate() {
        delegate.validate();
    }

    @Override
    public ModelMapper registerModule(Module module) {
        throw new IllegalStateException(ERROR_MSG);
    }

    Configuration getConfig() {
        return delegate.getConfiguration();
    }

    Collection<TypeMap<?, ?>> typeMaps() {
        return delegate.getTypeMaps();
    }
}
