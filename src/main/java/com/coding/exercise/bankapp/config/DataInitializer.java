package com.coding.exercise.bankapp.config;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.repository.CustomerRepository;

/**
 * Seeds initial data into the in-memory H2 database on application startup.
 * Uses CommandLineRunner to create a few Customer entities (with Address and Contact)
 * so that GET /bank-api/customers returns a non-empty list.
 */
@Configuration
public class DataInitializer {

    // PUBLIC_INTERFACE
    /**
     * CommandLineRunner bean that inserts sample Customers if the repository is empty.
     *
     * Behavior:
     * - If there are already customers, it does nothing (idempotent for repeated startups).
     * - Creates three customers with minimal Address and Contact details.
     *
     * @param customerRepository Spring Data repository for Customer
     * @return a runner executed after the application context has started
     */
    @Bean
    public CommandLineRunner seedData(CustomerRepository customerRepository) {
        return args -> {
            if (customerRepository.count() > 0) {
                return;
            }

            // Customer 1000
            Customer c1 = Customer.builder()
                .firstName("John")
                .middleName("Q")
                .lastName("Doe")
                .customerNumber(1000L)
                .status("ACTIVE")
                .customerAddress(Address.builder()
                        .address1("123 Main St")
                        .address2("Apt 1")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("USA")
                        .build())
                .contactDetails(Contact.builder()
                        .emailId("john.doe@example.com")
                        .homePhone("555-0100")
                        .workPhone("555-0101")
                        .build())
                .createDateTime(new Date())
                .build();

            // Customer 1001
            Customer c2 = Customer.builder()
                .firstName("Jane")
                .middleName("A")
                .lastName("Smith")
                .customerNumber(1001L)
                .status("ACTIVE")
                .customerAddress(Address.builder()
                        .address1("456 Oak Ave")
                        .address2("")
                        .city("Metropolis")
                        .state("NY")
                        .zip("10001")
                        .country("USA")
                        .build())
                .contactDetails(Contact.builder()
                        .emailId("jane.smith@example.com")
                        .homePhone("555-0200")
                        .workPhone("555-0201")
                        .build())
                .createDateTime(new Date())
                .build();

            // Customer 1002
            Customer c3 = Customer.builder()
                .firstName("Robert")
                .middleName("B")
                .lastName("Johnson")
                .customerNumber(1002L)
                .status("INACTIVE")
                .customerAddress(Address.builder()
                        .address1("789 Pine Rd")
                        .address2("Unit 12B")
                        .city("Gotham")
                        .state("NJ")
                        .zip("07001")
                        .country("USA")
                        .build())
                .contactDetails(Contact.builder()
                        .emailId("robert.johnson@example.com")
                        .homePhone("555-0300")
                        .workPhone("555-0301")
                        .build())
                .createDateTime(new Date())
                .build();

            customerRepository.saveAll(List.of(c1, c2, c3));
        };
    }
}
