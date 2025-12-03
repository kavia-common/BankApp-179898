package com.coding.exercise.bankapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import com.coding.exercise.bankapp.service.BankingService;

/**
 * REST controller for customer management operations.
 * <p>
 * With server.servlet.context-path configured as "/bank-api", the effective URL
 * prefixes are:
 *   - List customers:        GET  /bank-api/customers
 *   - Legacy list endpoint:  GET  /bank-api/customers/all
 *   - Create customer:       POST /bank-api/customers/add
 *   - Get by customer no.:   GET  /bank-api/customers/{customerNumber}
 *   - Update customer:       PUT  /bank-api/customers/{customerNumber}
 *   - Delete customer:       DELETE /bank-api/customers/{customerNumber}
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private BankingService bankingService;

    // PUBLIC_INTERFACE
    /**
     * Returns all customers as a JSON array.
     * This provides the conventional collection endpoint at GET /bank-api/customers.
     *
     * @return list of all customers
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDetails> getCustomers() {
        return bankingService.findAll();
    }

    // PUBLIC_INTERFACE
    /**
     * Legacy list endpoint preserved for backward compatibility.
     *
     * @return list of all customers
     */
    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDetails> getAllCustomers() {
        return bankingService.findAll();
    }

    // PUBLIC_INTERFACE
    /**
     * Create a new customer.
     *
     * @param customer customer payload
     * @return 201 Created on success
     */
    @PostMapping(path = "/add")
    public ResponseEntity<Object> addCustomer(@RequestBody CustomerDetails customer) {
        return bankingService.addCustomer(customer);
    }

    // PUBLIC_INTERFACE
    /**
     * Get details of a customer by their customerNumber.
     *
     * @param customerNumber customer number
     * @return customer details, or null if not found
     */
    @GetMapping(path = "/{customerNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerDetails getCustomer(@PathVariable Long customerNumber) {
        return bankingService.findByCustomerNumber(customerNumber);
    }

    // PUBLIC_INTERFACE
    /**
     * Update details of a customer.
     *
     * @param customerDetails new details
     * @param customerNumber customer number
     * @return 200 OK on success, 404 if customer not found
     */
    @PutMapping(path = "/{customerNumber}")
    public ResponseEntity<Object> updateCustomer(@RequestBody CustomerDetails customerDetails,
                                                 @PathVariable Long customerNumber) {
        return bankingService.updateCustomer(customerDetails, customerNumber);
    }

    // PUBLIC_INTERFACE
    /**
     * Delete a customer by customerNumber.
     *
     * @param customerNumber customer number
     * @return 200 OK on success, 400 if the customer does not exist
     */
    @DeleteMapping(path = "/{customerNumber}")
    public ResponseEntity<Object> deleteCustomer(@PathVariable Long customerNumber) {
        return bankingService.deleteCustomer(customerNumber);
    }
}
