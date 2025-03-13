package io.github.vaatech.modelmapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.getModelMapper;
import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.withModelMapperContext;
import static org.assertj.core.api.Assertions.assertThat;

class TypeMapCustomizerConverterTest {

    @Test
    void contextLoads() {
        withModelMapperContext().withUserConfiguration(ModelMapperConfiguration.class).run(context -> {
            ModelMapper modelMapper = getModelMapper(context);

            PaymentInfo info1 = new PaymentInfo(1, PaymentInfoType.BILLING);
            PaymentInfo info2 = new PaymentInfo(2, PaymentInfoType.SHIPPING);
            PaymentInfo info3 = new PaymentInfo(3, PaymentInfoType.BILLING);
            PaymentInfo info4 = new PaymentInfo(4, PaymentInfoType.SHIPPING);
            Customer customer = new Customer();
            customer.info = Arrays.asList(info1, info2, info3, info4);
            CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);

            assertThat(dto.billingInfo.get(0).id).isEqualTo(1);
            assertThat(dto.billingInfo.get(1).id).isEqualTo(3);
            assertThat(dto.shippingInfo.get(0).id).isEqualTo(2);
            assertThat(dto.shippingInfo.get(1).id).isEqualTo(4);
        });
    }

    @Configuration
    static class ModelMapperConfiguration {

        static class PaymentInfoConverter implements Converter<List<PaymentInfo>, List<?>> {
            private final boolean billing;

            PaymentInfoConverter(boolean billing) {
                this.billing = billing;
            }

            public List<?> convert(MappingContext<List<PaymentInfo>, List<?>> context) {
                var mappingEngine = context.getMappingEngine();
                return context.getSource().stream()
                        .filter(p -> !billing ^ PaymentInfoType.BILLING.equals(p.getType()))
                        .map(paymentInfo -> switch (paymentInfo.getType()) {
                            case BILLING -> mappingEngine.map(context.create(paymentInfo, BillingInfoDTO.class));
                            case SHIPPING -> mappingEngine.map(context.create(paymentInfo, ShippingInfoDTO.class));
                        })
                        .toList();
            }
        }

        @Bean
        ConfigurationCustomizer configurationCustomizer() {
            return configuration -> configuration
                    .setFieldMatchingEnabled(Boolean.TRUE)
                    .setFieldAccessLevel(AccessLevel.PACKAGE_PRIVATE);
        }

        @Bean
        ModelMapperBuilderCustomizer customizerToCustomerDTOMappings() {
            return builder -> builder.typeMapOf(Customer.class, CustomerDTO.class, typeMap -> {
                typeMap.addMappings(mapping -> {
                    mapping.using(new PaymentInfoConverter(true)).map(Customer::getInfo, CustomerDTO::setBillingInfo);
                    mapping.using(new PaymentInfoConverter(false)).map(Customer::getInfo, CustomerDTO::setShippingInfo);
                });
            });
        }
    }

    public enum PaymentInfoType {
        BILLING,
        SHIPPING
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentInfo {
        private int id;
        private PaymentInfoType type;
    }

    @Getter
    @Setter
    public static class Customer {
        private List<PaymentInfo> info;
    }

    @Getter
    @Setter
    public static class BillingInfoDTO {
        private int id;
    }

    @Getter
    @Setter
    public static class ShippingInfoDTO {
        private int id;
    }

    @Getter
    @Setter
    public static class CustomerDTO {
        private List<BillingInfoDTO> billingInfo;
        private List<ShippingInfoDTO> shippingInfo;
    }
}
