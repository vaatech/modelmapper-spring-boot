package io.github.vaatech.modelmapper;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.util.LambdaSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelMapperBuilderCustomizers {

    private final List<ModelMapperBuilderCustomizer> customizers;

    public ModelMapperBuilderCustomizers(ObjectProvider<? extends ModelMapperBuilderCustomizer> customizers) {
        this.customizers = (customizers != null)
                ? new ArrayList<>(customizers.orderedStream().toList())
                : Collections.emptyList();
    }

    public ModelMapperBuilder customize(ModelMapperBuilder builder) {
        LambdaSafe.callbacks(ModelMapperBuilderCustomizer.class, this.customizers, builder)
                .withLogger(TransactionManagerCustomizers.class)
                .invoke((customizer) -> customizer.customize(builder));
        return builder;
    }
}
