package io.github.vaatech.modelmapper;

/**
 * Callback interface that can be implemented by beans wishing to further customize the {@link
 * org.modelmapper.ModelMapper} via {@link ModelMapperBuilder} retaining its default
 * autoconfiguration.
 *
 * @since 1.0.0
 */
@FunctionalInterface
public interface ModelMapperBuilderCustomizer extends Customizer<ModelMapperBuilder> {
}
