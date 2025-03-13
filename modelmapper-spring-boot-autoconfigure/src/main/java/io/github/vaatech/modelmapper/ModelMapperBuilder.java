package io.github.vaatech.modelmapper;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Module;
import org.modelmapper.*;
import org.modelmapper.config.Configuration;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ModelMapperBuilder {

    private static final String EMPTY_NAME = null;

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
     * Builds the ModelMapper and returns.
     *
     * @return the ModelMapper to be built.
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
    ModelMapperBuilder configuration(Customizer<Configuration> customizer) {
        customizer.customize(configuration);
        return this;
    }

    public <S, D> ModelMapperBuilder typeMapOf(final Class<S> source,
                                               final Class<D> destination) {
        return typeMapOf(source, destination, EMPTY_NAME, null, Customizer.withDefaults());
    }

    public <S, D> ModelMapperBuilder typeMapOf(final Class<S> source,
                                               final Class<D> destination,
                                               final Customizer<TypeMap<S, D>> customizer) {
        return typeMapOf(source, destination, EMPTY_NAME, null, customizer);
    }

    public <S, D> ModelMapperBuilder typeMapOf(final Class<S> source,
                                               final Class<D> destination,
                                               final Configuration configuration) {
        return typeMapOf(source, destination, EMPTY_NAME, configuration, Customizer.withDefaults());
    }

    public <S, D> ModelMapperBuilder typeMapOf(final Class<S> source,
                                               final Class<D> destination,
                                               final Configuration configuration,
                                               final Customizer<TypeMap<S, D>> customizer) {
        return typeMapOf(source, destination, EMPTY_NAME, configuration, customizer);
    }

    public <S, D> ModelMapperBuilder typeMapOf(final Class<S> source,
                                               final Class<D> destination,
                                               final String typeMapName) {
        return typeMapOf(source, destination, typeMapName, null, Customizer.withDefaults());
    }

    public <S, D> ModelMapperBuilder typeMapOf(final Class<S> source,
                                               final Class<D> destination,
                                               final String typeMapName,
                                               final Customizer<TypeMap<S, D>> customizer) {
        return typeMapOf(source, destination, typeMapName, null, customizer);
    }

    public <S, D> ModelMapperBuilder typeMapOf(final Class<S> source,
                                               final Class<D> destination,
                                               final String typeMapName,
                                               final Configuration configuration) {
        return typeMapOf(source, destination, typeMapName, configuration, Customizer.withDefaults());
    }

    public <S, D> ModelMapperBuilder typeMapOf(final Class<S> source,
                                               final Class<D> destination,
                                               final String typeMapName,
                                               final Configuration configuration,
                                               final Customizer<TypeMap<S, D>> customizer) {

        TypePair<S, D> typePair = TypePair.of(source, destination, typeMapName);
        TypeMapConfigurer<S, D> typeMapConfigurer =
                getOrAdd(typePair, new TypeMapConfigurer<>(source, destination));

        if (typeMapName != null) {
            typeMapConfigurer.name(typeMapName);
        }

        if (configuration != null) {
            typeMapConfigurer.configuration(configuration);
        }

        if (customizer != null) {
            typeMapConfigurer.typeMap(customizer);
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

        loggingConfiguration(modelMapper);
        return new ImmutableModelMapper(modelMapper);
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

    private static void loggingConfiguration(ModelMapper modelMapper) {
        org.modelmapper.config.Configuration configuration = modelMapper.getConfiguration();
        log.debug("ModelMapper Configuration =======================================================");
        log.debug(" SourceNameTokenizer : {}", configuration.getSourceNameTokenizer());
        log.debug(" SourceNameTransformer : {}", configuration.getSourceNameTransformer());
        log.debug(" SourceNamingConvention : {}", configuration.getSourceNamingConvention());
        log.debug(" DestinationNameTokenizer : {}", configuration.getDestinationNameTokenizer());
        log.debug(" DestinationNameTransformer : {}", configuration.getDestinationNameTransformer());
        log.debug(" DestinationNamingConvention : {}", configuration.getDestinationNamingConvention());
        log.debug(" MatchingStrategy : {}", configuration.getMatchingStrategy());
        log.debug(" FieldAccessLevel : {}", configuration.getFieldAccessLevel());
        log.debug(" MethodAccessLevel : {}", configuration.getMethodAccessLevel());
        log.debug(" FieldMatchingEnabled : {}", configuration.isFieldMatchingEnabled());
        log.debug(" AmbiguityIgnored : {}", configuration.isAmbiguityIgnored());
        log.debug(" FullTypeMatchingRequired : {}", configuration.isFullTypeMatchingRequired());
        log.debug(" ImplicitMappingEnabled : {}", configuration.isImplicitMappingEnabled());
        log.debug(" SkipNullEnabled : {}", configuration.isSkipNullEnabled());
        log.debug(" CollectionsMergeEnabled : {}", configuration.isCollectionsMergeEnabled());
        log.debug(" UseOSGiClassLoaderBridging : {}", configuration.isUseOSGiClassLoaderBridging());
        log.debug(" DeepCopyEnabled : {}", configuration.isDeepCopyEnabled());
        log.debug(" Provider : {}", configuration.getProvider());
        log.debug(" PropertyCondition : {}", configuration.getPropertyCondition());
        log.debug(" TypeMaps :");
        modelMapper.getTypeMaps().forEach(typeMap -> log.debug("  {}", typeMap));
        log.debug(" Converters :");
        configuration.getConverters().forEach(converter -> log.debug("  {}", converter));
        log.debug("ModelMapper Configuration =======================================================");
    }

    private record TypePair<S, D>(Class<S> sourceType,
                                  Class<D> destinationType,
                                  String name) {

        static <T1, T2> TypePair<T1, T2> of(final Class<T1> sourceType,
                                            final Class<T2> destinationType,
                                            final String name) {

            return new TypePair<>(sourceType, destinationType, name);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + sourceType.hashCode();
            result = prime * result + destinationType.hashCode();
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public String toString() {
            String str = sourceType.getName() + " to " + destinationType.getName();
            if (name != null)
                str += " as " + name;
            return str;
        }
    }
}
