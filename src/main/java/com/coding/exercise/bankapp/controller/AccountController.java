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

/**
 * REST controller for account and transaction operations.
 * Uses Spring Web annotations only; OpenAPI documentation is provided by springdoc automatically.
 */
@RestController
@RequestMapping("/accounts")
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
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AccountInformation> getAccounts() {
        return bankingService.findAllAccounts();
    }

    @GetMapping(path = "/{accountNumber}")
    public ResponseEntity<Object> getByAccountNumber(@PathVariable Long accountNumber) {
        return bankingService.findByAccountNumber(accountNumber);
    }

    @PostMapping(path = "/add/{customerNumber}")
    public ResponseEntity<Object> addNewAccount(@RequestBody AccountInformation accountInformation,
                                                @PathVariable Long customerNumber) {
        return bankingService.addNewAccount(accountInformation, customerNumber);
    }

    @PutMapping(path = "/transfer/{customerNumber}")
    public ResponseEntity<Object> transferDetails(@RequestBody TransferDetails transferDetails,
                                                  @PathVariable Long customerNumber) {
        return bankingService.transferDetails(transferDetails, customerNumber);
    }

    @GetMapping(path = "/transactions/{accountNumber}")
    public List<TransactionDetails> getTransactionByAccountNumber(@PathVariable Long accountNumber) {
        return bankingService.findTransactionsByAccountNumber(accountNumber);
    }
}
