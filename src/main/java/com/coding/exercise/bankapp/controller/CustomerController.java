package com.coding.exercise.bankapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;

/**
 * REST controller for customer management operations.
 * Uses Spring Web annotations only; OpenAPI documentation is provided by springdoc automatically.
 */
@RestController
@RequestMapping("customers")
public class CustomerController {

    @Autowired
    private BankingServiceImpl bankingService;

    @GetMapping(path = "/all")
    public List<CustomerDetails> getAllCustomers() {
        return bankingService.findAll();
    }

    @PostMapping(path = "/add")
    public ResponseEntity<Object> addCustomer(@RequestBody CustomerDetails customer) {
        return bankingService.addCustomer(customer);
    }

    @GetMapping(path = "/{customerNumber}")
    public CustomerDetails getCustomer(@PathVariable Long customerNumber) {
        return bankingService.findByCustomerNumber(customerNumber);
    }

    @PutMapping(path = "/{customerNumber}")
    public ResponseEntity<Object> updateCustomer(@RequestBody CustomerDetails customerDetails,
                                                 @PathVariable Long customerNumber) {
        return bankingService.updateCustomer(customerDetails, customerNumber);
    }

    @DeleteMapping(path = "/{customerNumber}")
    public ResponseEntity<Object> deleteCustomer(@PathVariable Long customerNumber) {
        return bankingService.deleteCustomer(customerNumber);
    }
}
