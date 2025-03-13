package io.github.vaatech.modelmapper;

import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.Objects;

public enum Mapper {

    INSTANCE;

    private ModelMapper mapper;

    void setMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    ModelMapper getMapper() {
        if (Objects.isNull(mapper)) {
            throw new IllegalStateException("ModelMapper was not configured");
        }
        return mapper;
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destinationType, "destinationType");
        return getMapper().map(source, destinationType);
    }

    public <D> D map(Object source, Class<D> destinationType, String typeMapName) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destinationType, "destinationType");
        Objects.requireNonNull(typeMapName, "typeMapName");
        return getMapper().map(source, destinationType, typeMapName);
    }

    public <S, D> D map(S source, D destination) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destination, "destination");
        getMapper().map(source, destination);
        return destination;
    }

    public <S, D> D map(S source, D destination, String typeMapName) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destination, "destination");
        Objects.requireNonNull(typeMapName, "typeMapName");
        getMapper().map(source, destination, typeMapName);
        return destination;
    }

    public <D> D map(Object source, Type destinationType) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destinationType, "destinationType");
        return getMapper().map(source, destinationType);
    }

    public <D> D map(Object source, Type destinationType, String typeMapName) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destinationType, "destinationType");
        Objects.requireNonNull(typeMapName, "typeMapName");
        return getMapper().map(source, destinationType, typeMapName);
    }
}