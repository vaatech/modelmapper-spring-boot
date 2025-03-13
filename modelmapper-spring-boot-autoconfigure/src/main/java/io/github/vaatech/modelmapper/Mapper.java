package io.github.vaatech.modelmapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.List;
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

    public <S, D> D map(S entity, Class<D> outClass) {
        Objects.requireNonNull(entity, "Source object must not be null");
        Objects.requireNonNull(outClass, "Destination type must not be null");

        return getMapper().map(entity, outClass);
    }

    public <S, D> List<D> map(List<S> source) {
        Objects.requireNonNull(source, "Source object must not be null");

        return getMapper().map(source, new TypeToken<>() {}.getType());
    }

    public <S, D> D map(S entity, D destination) {
        Objects.requireNonNull(entity, "Source object must not be null");
        Objects.requireNonNull(destination, "Destination object must not be null");

        getMapper().map(entity, destination);

        return destination;
    }
}