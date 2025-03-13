package io.github.vaatech.modelmapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.getModelMapper;
import static io.github.vaatech.modelmapper.ApplicationContextRunnerHelper.withModelMapperContext;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeMapCustomizerDeepMappingTest {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private String street;
        private String city;
    }

    @Nested
    class MappingWithPropertyMap {

        @Test
        void whenDeepMappingConfigurationShouldMap() {
            withModelMapperContext().withUserConfiguration(ModelMapperConfiguration.class).run(context -> {
                ModelMapper modelMapper = getModelMapper(context);

                Person person = new Person();
                Address address = new Address();
                address.setStreet("1234 Main street");
                address.setCity("San Francisco");
                person.setAddress(address);

                PersonDTO dto = modelMapper.map(person, PersonDTO.class);

                assertThat(dto.getStreet()).isEqualTo(person.getAddress().getStreet());
                assertThat(dto.getCity()).isEqualTo(person.getAddress().getCity());
            });
        }

        @Configuration
        static class ModelMapperConfiguration {

            @Bean
            ModelMapperBuilderCustomizer personToPersonDTOMappings() {
                return builder -> builder.typeMapOf(Person.class, PersonDTO.class, typeMap -> {
                    typeMap.addMappings(mapping -> {
                        mapping.map(source -> source.getAddress().getStreet(), PersonDTO::setStreet);
                        mapping.map(source -> source.getAddress().getCity(), PersonDTO::setCity);
                    });
                });
            }
        }

        @Getter
        @Setter
        public static class Person {
            private Address address;
        }

        @Getter
        @Setter
        public static class PersonDTO {
            private String city;
            private String street;
        }
    }

    @Nested
    class MappingWithNames {

        @Test
        void whenMappingWithDefaultConfigurationProjectionsShouldMap() {
            withModelMapperContext().run(context -> {
                Customer customer = new Customer("Joe Smith");
                Address billingAddress = new Address("2233 Pike Street", "Seattle");
                Address shippingAddress = new Address("1234 Market Street", "San Francisco");
                Order order = new Order(customer, billingAddress, shippingAddress);

                ModelMapper modelMapper = getModelMapper(context);
                OrderDTO dto = modelMapper.map(order, OrderDTO.class);

                assertThat(dto.getCustomerName()).isEqualTo(order.getCustomer().getName());
                assertThat(dto.getShippingStreetAddress()).isEqualTo(order.getShippingAddress().getStreet());
                assertThat(dto.getShippingCity()).isEqualTo(order.getShippingAddress().getCity());
                assertThat(dto.getBillingStreetAddress()).isEqualTo(order.getBillingAddress().getStreet());
                assertThat(dto.getBillingCity()).isEqualTo(order.getBillingAddress().getCity());
            });
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class Customer {
            private String name;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class Order {
            private Customer customer;
            private Address billingAddress;
            private Address shippingAddress;
        }

        @Getter
        @Setter
        public static class OrderDTO {
            private String customerName;
            private String shippingStreetAddress;
            private String shippingCity;
            private String billingStreetAddress;
            private String billingCity;
        }
    }
}
