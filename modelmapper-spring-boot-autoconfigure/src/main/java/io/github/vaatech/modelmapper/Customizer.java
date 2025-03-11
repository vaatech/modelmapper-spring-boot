package io.github.vaatech.modelmapper;

import java.util.Objects;

@FunctionalInterface
public interface Customizer<T> {

    void customize(T t);

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
