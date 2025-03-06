package com.github.vaatech.modelmapper;

import java.util.Objects;

@FunctionalInterface
public interface Customizer<T> {

    /**
     * Performs the customizations on the input argument.
     *
     * @param t the input argument
     */
    void customize(T t);

    /**
     * Returns a {@link Customizer} that does not alter the input argument.
     *
     * @return a {@link Customizer} that does not alter the input argument.
     */
    static <T> Customizer<T> withDefaults() {
        return (t) -> {
        };
    }

    default Customizer<T> andThen(Customizer<? super T> after) {
        Objects.requireNonNull(after);
        return (t) -> {
            this.customize(t);
            after.customize(t);
        };
    }
}
