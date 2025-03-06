package com.github.vaatech.modelmapper;

import org.modelmapper.Module;
import org.modelmapper.*;
import org.modelmapper.config.Configuration;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModelMapperBuilder {

    private final AtomicBoolean building = new AtomicBoolean();
    private final ModelMapper modelMapper;
    private final Configuration configuration;
    private final Map<TypePair<?, ?>, TypeMapConfigurer<?, ?>> configurers = new LinkedHashMap<>();
    private Provider<?> provider;
    private Condition<?, ?> condition;
    private final List<Converter<?, ?>> converters = new ArrayList<>();
    private final List<Module> modules = new ArrayList<>();

    ModelMapperBuilder() {
        this.modelMapper = new ModelMapper();
        this.configuration = modelMapper.getConfiguration();
    }

    public static ModelMapperBuilder builder() {
        return new ModelMapperBuilder();
    }

    /**
     * Builds the ModelMapper and returns it or null.
     *
     * @return the ModelMapper to be built or null if the implementation allows it.
     */
    public ModelMapper build() {
        if (this.building.compareAndSet(false, true)) {
            return doBuild();
        }
        throw new IllegalStateException("This ModelMapper has already been built");
    }

    public final ModelMapper getModelMapper() {
        if (!this.building.get()) {
            throw new IllegalStateException("This ModelMapper has not been built");
        }
        return this.modelMapper;
    }

    /**
     * Customize global configuration
     */
    public ModelMapperBuilder configuration(Customizer<Configuration> customizer) {
        customizer.customize(configuration);
        return this;
    }

    public <S, D> ModelMapperBuilder typeMap(final Class<S> source,
                                             final Class<D> destination) {
        return typeMap(source, destination, Customizer.withDefaults());
    }

    public <S, D> ModelMapperBuilder typeMap(final Class<S> source,
                                             final Class<D> destination,
                                             final Customizer<TypeMapConfigurer<S, D>> customizer) {
        return typeMap(source, destination, null, customizer);
    }

    public <S, D> ModelMapperBuilder typeMap(final Class<S> source,
                                             final Class<D> destination,
                                             final String typeMapName) {
        return typeMap(source, destination, typeMapName, Customizer.withDefaults());
    }

    public <S, D> ModelMapperBuilder typeMap(final Class<S> source,
                                             final Class<D> destination,
                                             final String typeMapName,
                                             final Customizer<TypeMapConfigurer<S, D>> customizer) {

        TypePair<S, D> typePair = TypePair.of(source, destination, typeMapName);
        TypeMapConfigurer<S, D> typeMapConfigurer =
                getOrAdd(typePair, new TypeMapConfigurer<>(source, destination));

        if (typeMapName != null) {
            typeMapConfigurer.name(typeMapName);
        }

        if (customizer != null) {
            customizer.customize(typeMapConfigurer);
        }
        return this;
    }

    public ModelMapperBuilder provider(Provider<?> provider) {
        this.provider = provider;
        return this;
    }

    public <S, D> ModelMapperBuilder condition(Condition<S, D> condition) {
        this.condition = condition;
        return this;
    }

    public <S, D> ModelMapperBuilder converter(Converter<S, D> converter) {
        this.converters.add(converter);
        return this;
    }

    public ModelMapperBuilder module(Module module) {
        this.modules.add(module);
        return this;
    }

    private ModelMapper doBuild() {

        if (provider != null) {
            modelMapper.getConfiguration().setProvider(provider);
        }

        if (condition != null) {
            modelMapper.getConfiguration().setPropertyCondition(condition);
        }

        Collection<TypeMapConfigurer<?, ?>> configurers = this.configurers.values();
        for (TypeMapConfigurer<?, ?> configurer : configurers) {
            configurer.configure(this.modelMapper);
        }

        this.converters.forEach(modelMapper::addConverter);
        this.modules.forEach(modelMapper::registerModule);

        return modelMapper;
    }

    private <S, D> TypeMapConfigurer<S, D> getOrAdd(final TypePair<S, D> typePair,
                                                    final TypeMapConfigurer<S, D> configurer) {
        TypeMapConfigurer<S, D> existingConfig = getConfigurer(typePair);
        if (existingConfig != null) {
            return existingConfig;
        }
        return add(typePair, configurer);
    }

    private <S, D> TypeMapConfigurer<S, D> add(final TypePair<S, D> typePair,
                                               final TypeMapConfigurer<S, D> configurer) {
        Assert.notNull(typePair, "typePair cannot be null");
        Assert.notNull(configurer, "configurer cannot be null");
        synchronized (this.configurers) {
            this.configurers.putIfAbsent(typePair, configurer);
        }
        return configurer;
    }

    @SuppressWarnings("unchecked")
    private <S, D> TypeMapConfigurer<S, D> getConfigurer(TypePair<S, D> typePair) {
        TypeMapConfigurer<?, ?> config = this.configurers.get(typePair);
        if (config == null) {
            return null;
        }
        return (TypeMapConfigurer<S, D>) config;
    }

    static class TypePair<S, D> {
        private final Class<S> sourceType;
        private final Class<D> destinationType;
        private final String name;
        private final int hashCode;

        private TypePair(Class<S> sourceType, Class<D> destinationType, String name) {
            this.sourceType = sourceType;
            this.destinationType = destinationType;
            this.name = name;
            hashCode = computeHashCode();
        }

        static <T1, T2> TypePair<T1, T2> of(Class<T1> sourceType, Class<T2> destinationType, String name) {
            return new TypePair<>(sourceType, destinationType, name);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this)
                return true;
            if (!(other instanceof TypePair<?, ?> otherPair))
                return false;
            if (name == null) {
                if (otherPair.name != null)
                    return false;
            } else if (!name.equals(otherPair.name))
                return false;
            return sourceType.equals(otherPair.sourceType)
                    && destinationType.equals(otherPair.destinationType);
        }

        @Override
        public final int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            String str = sourceType.getName() + " to " + destinationType.getName();
            if (name != null)
                str += " as " + name;
            return str;
        }

        private int computeHashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + sourceType.hashCode();
            result = prime * result + destinationType.hashCode();
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }
    }
}
