package com.github.vaatech.modelmapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.Module;
import org.modelmapper.*;
import org.modelmapper.config.Configuration;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ModelMapperBuilder {

    private final Log logger = LogFactory.getLog(getClass());

    private final AtomicBoolean building = new AtomicBoolean();
    private final ModelMapper modelMapper;
    private final Configuration configuration;
    private final LinkedHashMap<Class<? extends ModelMapperConfigurer>, List<ModelMapperConfigurer>> configurers = new LinkedHashMap<>();
    private Provider<?> provider;
    private Condition<?, ?> condition;
    private List<Module> modules;
    private Consumer<ModelMapper> postConfigurer;

    ModelMapperBuilder() {
        this.modelMapper = new ModelMapper();
        this.configuration = modelMapper.getConfiguration();
    }

    public static ModelMapperBuilder mapper() {
        return new ModelMapperBuilder();
    }

    private ModelMapper doBuild() {

        if (provider != null) {
            modelMapper.getConfiguration().setProvider(provider);
        }

        if (condition != null) {
            modelMapper.getConfiguration().setPropertyCondition(condition);
        }

        Collection<ModelMapperConfigurer> configurers = getConfigurers();
        for (ModelMapperConfigurer configurer : configurers) {
            configurer.configure();
        }

        if (this.modules != null) {
            this.modules.forEach(modelMapper::registerModule);
        }

        if (postConfigurer != null) {
            postConfigurer.accept(modelMapper);
        }

        return modelMapper;
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

    public <C extends ModelMapperConfigurer> C apply(C configurer) {
        configurer.setBuilder(this);
        configurer.setModelMapper(modelMapper);
        add(configurer);
        return configurer;
    }

    private <C extends ModelMapperConfigurer> void add(C configurer) {
        Assert.notNull(configurer, "configurer cannot be null");
        Class<? extends ModelMapperConfigurer> clazz = configurer.getClass();
        synchronized (this.configurers) {
            List<ModelMapperConfigurer> configs = this.configurers.computeIfAbsent(clazz, (k) -> new ArrayList<>(1));
            configs.add(configurer);
        }
    }

    private Collection<ModelMapperConfigurer> getConfigurers() {
        List<ModelMapperConfigurer> result = new ArrayList<>();
        for (List<ModelMapperConfigurer> configs : this.configurers.values()) {
            result.addAll(configs);
        }
        return result;
    }

    public ModelMapperBuilder configuration(Customizer<Configuration> customizer) {
        customizer.customize(configuration);
        return this;
    }

    /**
     * An option to apply additional customizations directly to the {@link ModelMapper} instances at
     * the end, after all other config properties of the builder have been applied.
     *
     * @param configurer a configurer to apply. If several configurers are registered, they will get
     *                   applied in their registration order.
     */
    public ModelMapperBuilder postConfigurer(Consumer<ModelMapper> configurer) {
        this.postConfigurer =
                (this.postConfigurer != null ? this.postConfigurer.andThen(configurer) : configurer);
        return this;
    }

    public <S, D> TypeMapConfigurer<S, D> typeMap(Class<S> source, Class<D> dest) {
        TypeMapConfigurer<S, D> typeMapConfigurer = new TypeMapConfigurer<>(source, dest);
        return apply(typeMapConfigurer);
    }

    public <S, D> ModelMapperBuilder converter(Converter<S, D> converter) {
        ConverterConfigurer<S, D> converterConfigurer = new ConverterConfigurer<>();
        converterConfigurer.with(converter);
        apply(converterConfigurer);
        return this;
    }

    public <S, D> ConverterConfigurer<S, D> converter(Class<S> sourceType, Class<D> destinationType) {
        ConverterConfigurer<S, D> converterConfigurer = new ConverterConfigurer<>();
        converterConfigurer.with(sourceType, destinationType);
        return apply(converterConfigurer);
    }

    public <S, D> ModelMapperBuilder mappings(PropertyMap<S, D> propertyMap) {
        PropertyMapConfigurer<S, D> propertyMapConfigurer = new PropertyMapConfigurer<>();
        propertyMapConfigurer.with(propertyMap);
        apply(propertyMapConfigurer);
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

    /**
     * Specify the modules to be registered with the {@link ModelMapper}.
     *
     * <p>Multiple invocations are not additive, the last one defines the modules to register
     *
     * @see #modules(List)
     * @see org.modelmapper.Module;
     */
    //TODO autodiscover modules
    public ModelMapperBuilder modules(Module... modules) {
        return modules(Arrays.asList(modules));
    }

    /**
     * Variant of {@link #modules(Module...)} with a {@link List}.
     *
     * @see #modules(Module...)
     * @see #modules(Consumer)
     * @see org.modelmapper.Module;
     */
    public ModelMapperBuilder modules(List<Module> modules) {
        this.modules = new ArrayList<>(modules);
        return this;
    }

    /**
     * Variant of {@link #modules(Module...)} with a {@link Consumer} for full control over the
     * underlying list of modules.
     *
     * @see #modules(Module...)
     * @see #modules(List)
     * @see org.modelmapper.Module;
     */
    public ModelMapperBuilder modules(Consumer<List<Module>> consumer) {
        this.modules = (this.modules != null ? this.modules : new ArrayList<>());
        consumer.accept(this.modules);
        return this;
    }
}
