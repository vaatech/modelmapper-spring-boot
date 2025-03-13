package io.github.vaatech.modelmapper;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.getModelMapper;
import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.withModelMapperContext;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeMapCustomizerPostConverterTest {

    @Test
    void contextLoads() {
        withModelMapperContext().withUserConfiguration(ModelMapperConfiguration.class).run(context -> {
            ModelMapper modelMapper = getModelMapper(context);

            Order order = new Order();
            order.id = UUID.randomUUID().toString();
            DeliveryAddress da1 = new DeliveryAddress();
            da1.addressId = 123;
            DeliveryAddress da2 = new DeliveryAddress();
            da2.addressId = 456;
            order.deliveryAddress = new DeliveryAddress[]{da1, da2};

            OrderDTO dto = modelMapper.map(order, OrderDTO.class);

            assertThat(dto.id).isEqualTo(order.id);
            assertThat(dto.deliveryAddress_addressId).isEqualTo(new Integer[]{123, 456});
        });
    }

    @Configuration
    static class ModelMapperConfiguration {

        @Bean
        ConfigurationCustomizer configurationCustomizer() {
            return configuration -> configuration
                    .setFieldMatchingEnabled(Boolean.TRUE)
                    .setFieldAccessLevel(AccessLevel.PACKAGE_PRIVATE);
        }

        @Bean
        ModelMapperBuilderCustomizer customizerToCustomerDTOMappings() {
            return builder -> builder.typeMapOf(Order.class, OrderDTO.class, typeMap -> {
                typeMap.setPostConverter(context -> {
                    DeliveryAddress[] deliveryAddress = context.getSource().deliveryAddress;
                    context.getDestination().deliveryAddress_addressId = new Integer[deliveryAddress.length];
                    for (int i = 0; i < deliveryAddress.length; i++)
                        context.getDestination().deliveryAddress_addressId[i] = deliveryAddress[i].addressId;
                    return context.getDestination();
                });
            });
        }
    }

    public static class Order {
        String id;
        DeliveryAddress[] deliveryAddress;
    }

    public static class DeliveryAddress {
        Integer addressId;
    }

    public static class OrderDTO {
        String id;
        Integer[] deliveryAddress_addressId;
    }
}
