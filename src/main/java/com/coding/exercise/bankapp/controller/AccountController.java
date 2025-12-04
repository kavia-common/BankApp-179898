package com.coding.exercise.bankapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for account and transaction operations.
 * Uses Spring Web annotations only; OpenAPI documentation is provided by springdoc automatically.
 */
@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Operations related to bank accounts and transactions")
public class AccountController {

    @Autowired
    private BankingServiceImpl bankingService;

    // PUBLIC_INTERFACE
    /**
     * Returns all accounts as a JSON array.
     *
     * Effective external URL: /bank-api/accounts
     *
     * @return list of all accounts
     */
    @Operation(summary = "List all accounts", description = "Returns the complete list of bank accounts.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AccountInformation> getAccounts() {
        return bankingService.findAllAccounts();
    }

    @Operation(summary = "Get account by number", description = "Retrieves an account by its account number.")
    @GetMapping(path = "/{accountNumber}")
    public ResponseEntity<Object> getByAccountNumber(@PathVariable Long accountNumber) {
        return bankingService.findByAccountNumber(accountNumber);
    }

    @Operation(summary = "Create new account for customer", description = "Creates a new bank account and associates it with the specified customer.")
    @PostMapping(path = "/add/{customerNumber}")
    public ResponseEntity<Object> addNewAccount(@RequestBody AccountInformation accountInformation,
                                                @PathVariable Long customerNumber) {
        return bankingService.addNewAccount(accountInformation, customerNumber);
    }

    @Operation(summary = "Transfer funds", description = "Transfers funds based on the provided transfer details for the specified customer.")
    @PutMapping(path = "/transfer/{customerNumber}")
    public ResponseEntity<Object> transferDetails(@RequestBody TransferDetails transferDetails,
                                                  @PathVariable Long customerNumber) {
        return bankingService.transferDetails(transferDetails, customerNumber);
    }

    @Operation(summary = "Get transactions by account", description = "Retrieves transaction history for the given account number.")
    @GetMapping(path = "/transactions/{accountNumber}")
    public List<TransactionDetails> getTransactionByAccountNumber(@PathVariable Long accountNumber) {
        return bankingService.findTransactionsByAccountNumber(accountNumber);
    }
}
