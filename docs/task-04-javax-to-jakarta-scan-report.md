# javax → jakarta Scan Report

Date: 2025-12-03
Repository: BankApp-179898 (Java 21 / Spring Boot 3)

Objective
- Scan all Java sources under src/main/java and src/test/java for javax.* imports.
- Replace with jakarta.* equivalents for common mappings:
  - javax.persistence → jakarta.persistence
  - javax.validation → jakarta.validation
  - javax.servlet → jakarta.servlet
  - javax.xml.bind → jakarta.xml.bind
  - javax.annotation → jakarta.annotation
- Add "// MANUAL REVIEW REQUIRED: verify package/class" for ambiguous mappings.
- Update package-info.java and module-info.java if present.
- Provide concise diff of modified files and list files not auto-migrated with reasons.

Scan Scope
- src/main/java
- src/test/java

Files Scanned (35)
- src/main/java/com/coding/exercise/bankapp/BankingApplication.java
- src/main/java/com/coding/exercise/bankapp/config/ApplicationConfig.java
- src/main/java/com/coding/exercise/bankapp/config/DataInitializer.java
- src/main/java/com/coding/exercise/bankapp/config/OpenApiConfig.java
- src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java
- src/main/java/com/coding/exercise/bankapp/controller/AccountController.java
- src/main/java/com/coding/exercise/bankapp/controller/CustomerController.java
- src/main/java/com/coding/exercise/bankapp/controller/HealthController.java
- src/main/java/com/coding/exercise/bankapp/controller/HomeController.java
- src/main/java/com/coding/exercise/bankapp/repository/AccountRepository.java
- src/main/java/com/coding/exercise/bankapp/repository/CustomerAccountXRefRepository.java
- src/main/java/com/coding/exercise/bankapp/repository/CustomerRepository.java
- src/main/java/com/coding/exercise/bankapp/repository/TransactionRepository.java
- src/main/java/com/coding/exercise/bankapp/domain/AccountInformation.java
- src/main/java/com/coding/exercise/bankapp/domain/AddressDetails.java
- src/main/java/com/coding/exercise/bankapp/domain/BankInformation.java
- src/main/java/com/coding/exercise/bankapp/domain/ContactDetails.java
- src/main/java/com/coding/exercise/bankapp/domain/CustomerDetails.java
- src/main/java/com/coding/exercise/bankapp/domain/TransactionDetails.java
- src/main/java/com/coding/exercise/bankapp/domain/TransferDetails.java
- src/main/java/com/coding/exercise/bankapp/model/Account.java
- src/main/java/com/coding/exercise/bankapp/model/Address.java
- src/main/java/com/coding/exercise/bankapp/model/BankInfo.java
- src/main/java/com/coding/exercise/bankapp/model/Contact.java
- src/main/java/com/coding/exercise/bankapp/model/Customer.java
- src/main/java/com/coding/exercise/bankapp/model/CustomerAccountXRef.java
- src/main/java/com/coding/exercise/bankapp/model/Transaction.java
- src/main/java/com/coding/exercise/bankapp/service/BankingService.java
- src/main/java/com/coding/exercise/bankapp/service/BankingServiceImpl.java
- src/main/java/com/coding/exercise/bankapp/service/helper/BankingServiceHelper.java
- src/test/java/com/coding/exercise/bankapp/BankingApplicationTests.java
- src/test/java/com/coding/exercise/bankapp/endpoint/EndpointChecksIT.java
- src/test/java/com/coding/exercise/bankapp/openapi/OpenApiDocsAccessibilityTest.java
- src/test/java/com/coding/exercise/bankapp/controller/CustomerControllerTest.java
- src/test/java/com/coding/exercise/bankapp/controller/HealthControllerTest.java

Findings
- No javax.* imports were found in any scanned files.
- Entities already use jakarta.persistence.*.
- No usage of javax.validation, javax.servlet, javax.xml.bind (JAXB), or javax.annotation was detected.
- No package-info.java or module-info.java are present in the scanned source sets.

Changes Applied
- None. No replacements were required.

Concise Diff (unified)
- No files modified.

Ambiguous/Manual Review Notes
- None. No javax.* usages found; therefore no ambiguous cases inserted.

Unmigrated Files Report
- None.

Compatibility Notes
- The project appears already migrated to Jakarta (e.g., jakarta.persistence usage across entity classes).
- With Spring Boot 3.x, jakarta.* imports are expected and align with current dependencies.

Conclusion
- javax→jakarta migration is complete for the scanned source sets; no further action required.
