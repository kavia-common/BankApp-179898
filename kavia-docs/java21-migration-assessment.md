# Java 21 Migration Assessment for BankApp-179898

## Overview

This document assesses the effort to migrate the BankApp-179898 Spring Boot application from Java 8 to Java 21. It inventories current dependencies and tooling, identifies files requiring changes, analyzes impact areas, proposes a step-by-step migration plan, outlines a test/validation strategy, and lists risks with a rollback plan. All findings are grounded in the current repository state.

## 1. Inventory

### 1.1 Spring Boot and Java level
- Spring Boot: 2.1.4.RELEASE (from pom.xml parent)
- Java: 1.8 (Java 8) configured via pom properties:
  - java.version=1.8
  - maven.compiler.source=1.8
  - maven.compiler.target=1.8
  - maven.compiler.release=8

### 1.2 Dependencies (from pom.xml)
- org.springframework.boot:spring-boot-starter-actuator
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-security
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-devtools (runtime)
- com.h2database:h2 (runtime)
- org.projectlombok:lombok (optional)
- io.springfox:springfox-swagger2:2.9.2
- io.springfox:springfox-swagger-ui:2.9.2
- org.springframework.boot:spring-boot-starter-test (test)
- org.springframework.security:spring-security-test (test)

Notable transitive implications:
- Spring 5.1.x and Spring Security 5.1.x via Spring Boot 2.1.4.
- Jackson 2.9.x era via Boot constraints.

### 1.3 Build plugins and tools
- maven-compiler-plugin 3.8.1
- spring-boot-maven-plugin (version inherited from Boot parent)
- Maven Wrapper:
  - Maven: 3.6.3 (distributionUrl)
  - Wrapper: io.takari maven-wrapper 0.4.2
- .mvn/jvm.config with add-opens flags for running on JDKs with modules:
  - --add-opens jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
  - --add-opens java.base/java.lang=ALL-UNNAMED
  - --add-opens java.base/java.util=ALL-UNNAMED
- Shell/scripts:
  - mvnw, mvnw.cmd, mvn shim, run.sh, start, start.sh, Procfile, Procfile.windows, Makefile
- CI: None detected in repo (no .github/workflows, no Jenkinsfile, etc.)

### 1.4 Application frameworks/features
- Spring MVC REST controllers
- Spring Data JPA with H2 in-memory DB
- Spring Security (WebSecurityConfigurerAdapter)
- Swagger via Springfox 2.9.2
- Lombok annotations
- JUnit 4 for tests (SpringRunner)

## 2. Files requiring changes for Java 21

Migrating to Java 21 will necessitate updates to tooling, Spring Boot baseline, and some code adaptations. The following files are likely to require edits:

- pom.xml:
  - Set java.version to 21 and configure maven-compiler-plugin release 21 (or via properties)
  - Upgrade Spring Boot parent to a version compatible with Java 21 (Spring Boot 3.2/3.3 recommended)
  - Replace Springfox with springdoc-openapi for Swagger/OpenAPI (Springfox 2.9.2 does not work on Spring Boot 3)
  - Adjust test dependencies (JUnit 5 is default for Boot 3)
  - Ensure jakarta.* namespace compatibility (Boot 3 migrates from javax.* to jakarta.*)
- .mvn/jvm.config:
  - Re-evaluate or remove add-opens flags; ideally not required for Maven build on Java 21 with proper plugin versions
- Maven Wrapper:
  - Upgrade Maven to 3.9.6+ and wrapper to maven-wrapper 3.2.0+ to ensure Java 21 support
  - Files: .mvn/wrapper/maven-wrapper.properties, mvnw, mvnw.cmd
- Scripts and runtime launchers:
  - run.sh, start, start.sh, Procfile(.windows) – verify Java 21 availability (no code change needed but document requirement)
- Code references that must change for Spring Boot 3 / Java 21:
  - SecurityConfig.java:
    - WebSecurityConfigurerAdapter is removed; must migrate to component-based SecurityFilterChain
  - Tests:
    - Migrate from JUnit 4 (SpringRunner, @RunWith) to JUnit 5 (jupiter)
  - JPA entities:
    - Boot 3 uses Jakarta EE 9+; javax.persistence.* -> jakarta.persistence.*
    - Update imports in model/*.java accordingly
  - Swagger configuration:
    - Remove springfox configuration (ApplicationConfig.java) and replace with springdoc-openapi or pure OpenAPI config
  - Date/time:
    - Consider migrating java.util.Date fields to java.time.* (LocalDateTime/Instant) where appropriate for modern Java usage (not required but recommended)
- application.yml:
  - H2 console path may change with Boot 3, and management endpoints property names differ; verify and adjust if needed

If Dockerfiles or CI exist elsewhere, update base images/toolchains to JDK 21 and maven 3.9.x, but none detected in repo.

## 3. Impact analysis

### 3.1 Language level changes
- Java 21 enables modern syntax and APIs; compilation must target 21 (release 21). No code currently uses Java 21 features; primary concern is compatibility with dependencies and frameworks.

### 3.2 JDK modules/APIs changes
- JDK 21 continues Java Platform Module System (JPMS). The existing add-opens flags in .mvn/jvm.config were workarounds for annotation processing issues on JDK 17+. With an up-to-date maven-compiler-plugin and Lombok version (via Boot 3 BOM), explicit add-opens are generally unnecessary. Remove or minimize them to avoid warnings and ensure clean builds.

### 3.3 Reflection/module access
- Spring and Lombok operate without extra add-opens when using supported versions. Migrating to Boot 3 resolves most reflective access issues that plagued older Boot versions on JDK 17+.

### 3.4 TLS/crypto changes
- JDK 21 tightens defaults: TLS 1.0/1.1 disabled, weak algorithms removed. This app does not configure TLS explicitly; no direct impact expected. If external connections were used (none present), verify cipher compatibility.

### 3.5 Concurrency and date-time
- The app uses synchronized blocks in BankingServiceImpl and java.util.Date in entities. Java 21 introduces Virtual Threads (Project Loom) which can be leveraged later but is optional. Consider moving to java.time classes; Hibernate 6 (with Boot 3) supports java.time out of the box. No breaking changes from core JDK in current code.

### 3.6 H2 compatibility
- Upgrade H2 with Boot 3 managed version. Ensure console path and JDBC URL remain valid:
  - Typical console path in Boot 3: /h2-console (still ok)
  - application.yml currently enables console (spring.h2.console.enabled: true). Boot 3 property remains similar. Validate at runtime.

### 3.7 Swagger/Springfox
- Springfox 2.9.2 is incompatible with Spring Boot 3 and Jakarta. Must remove Springfox and switch to springdoc-openapi (e.g., org.springdoc:springdoc-openapi-starter-webmvc-ui).

### 3.8 Spring Security
- WebSecurityConfigurerAdapter is removed in Spring Security 5.7+ and Boot 3.
- Must define a SecurityFilterChain bean, replace antMatchers with requestMatchers, and use authorizeHttpRequests DSL.

### 3.9 Jakarta EE namespace
- Boot 3 uses jakarta.*. All javax.persistence.* imports in entities must be updated to jakarta.persistence.*. Replacing package names is required for compilation.

### 3.10 Testing framework
- JUnit 4 to JUnit 5 migration. Replace @RunWith(SpringRunner.class) with @ExtendWith(SpringExtension.class) or rely on SpringBootTest’s meta-configuration in Boot 3 with junit-jupiter.

## 4. Step-by-step migration plan

The safest path is to upgrade framework stack before switching the compiler target.

1) Prepare toolchain
- Upgrade Maven Wrapper to Maven 3.9.6+ and wrapper 3.2.0+.
- Ensure local JDK 21 is installed in build environment.

Example wrapper updates (maven-wrapper.properties):
```
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
```

2) Upgrade Spring Boot to 3.2.x or 3.3.x
- In pom.xml, update parent to Boot 3.3.x (supports Java 21) and set java.version=21.

Snippet (pom.xml, top):
```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.3.4</version>
  <relativePath/>
</parent>

<properties>
  <java.version>21</java.version>
  <maven.compiler.release>21</maven.compiler.release>
  <maven.compiler.parameters>true</maven.compiler.parameters>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

- With Boot 3, you can typically omit explicit maven-compiler-plugin configuration if relying on Boot defaults. If retaining it, ensure:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.11.0</version>
  <configuration>
    <release>21</release>
    <parameters>true</parameters>
  </configuration>
</plugin>
```

3) Migrate from Springfox to springdoc-openapi
- Remove io.springfox dependencies and ApplicationConfig Swagger setup.
- Add springdoc OpenAPI starter:
```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.6.0</version>
</dependency>
```
- The Swagger UI will be available at /swagger-ui.html or /swagger-ui/index.html under the app context-path.

4) Migrate to Jakarta namespaces in JPA entities
- Replace all javax.persistence imports with jakarta.persistence in:
  - src/main/java/com/.../model/*.java
- Example diff for an entity:
```diff
- import javax.persistence.Entity;
+ import jakarta.persistence.Entity;
```
- Repeat for Id, GeneratedValue, Column, OneToOne, ManyToOne, Temporal, TemporalType, etc.

5) Update Spring Security configuration
- Replace WebSecurityConfigurerAdapter with a @Bean SecurityFilterChain.
- Example replacement for SecurityConfig.java:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .headers(headers -> headers.frameOptions(frame -> frame.disable()))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
        .anyRequest().authenticated()
      )
      .httpBasic(Customizer.withDefaults());

    return http.build();
  }
}
```
- If basic auth user/password is kept in application.yml, Boot 3 still supports spring.security.user.*.

6) Update tests to JUnit 5
- Remove JUnit 4 annotations and dependencies.
- In pom.xml, rely on spring-boot-starter-test in Boot 3 (includes junit-jupiter).
- Update test class:
```java
@SpringBootTest
class BankingApplicationTests {

  @Test
  void contextLoads() {
  }
}
```
- Remove @RunWith(SpringRunner.class).

7) Evaluate Lombok and add-opens
- With modern Lombok (managed by Boot 3 BOM), explicit add-opens in .mvn/jvm.config can be removed. Start by deleting all --add-opens lines and only re-introduce if absolutely necessary.

8) Verify H2 console and properties
- Boot 3 supports spring.h2.console.enabled as-is. Verify console at /bank-api/h2-console.

9) Build and run with Java 21
- ./mvnw -q -DskipTests clean package
- ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"

10) Optional improvements
- Consider replacing java.util.Date with java.time.Instant/LocalDateTime in domain and entity classes for modern Java idioms and better timezone handling.

## 5. Test plan

### 5.1 Unit and integration tests
- Ensure spring-boot-starter-test uses JUnit Jupiter (no vintage).
- Convert existing JUnit 4 test to JUnit 5.
- Add tests around:
  - Customer CRUD endpoints happy paths and error conditions
  - Account creation and transfer logic (insufficient funds, missing accounts)
  - Repository interactions with H2

### 5.2 Runtime checks
- Application starts on Java 21 with Boot 3.3.x without reflective access warnings.
- H2 console available at /bank-api/h2-console/.
- Swagger UI available at /bank-api/swagger-ui.html or /bank-api/swagger-ui/index.html.

### 5.3 Endpoints smoke test
- GET /bank-api/customers/all returns 200
- POST /bank-api/customers/add with sample JSON returns 200/201
- POST /bank-api/accounts/add/{customerNumber} with sample JSON returns 201
- PUT /bank-api/accounts/transfer/{customerNumber} with sample JSON returns 200 or 400 for insufficient funds
- GET /bank-api/accounts/{accountNumber} returns 200 or 404
- GET /bank-api/accounts/transactions/{accountNumber} returns 200 (list)

Use curl commands in a script or Postman collection; validate status codes and key payload fields.

### 5.4 Environment matrix
- Java 21 Temurin/OpenJDK on Linux (CI/container)
- Local dev macOS with JDK 21
- Windows with JDK 21 (mvnw.cmd path works)

### 5.5 Static checks
- mvn -q -DskipTests clean verify
- Spotless/Checkstyle optional future additions

## 6. Risks and rollback

### 6.1 Risks
- Spring Boot 2.1 -> 3.x is a major migration crossing Jakarta EE namespace changes.
- Springfox removal can impact API docs; ensure springdoc replacement is configured and paths permitted through security.
- Security config changes may inadvertently block endpoints; verify permitAll paths.
- JPA mapping changes due to jakarta.* may expose subtle issues; validate CRUD flows.
- Maven wrapper/tooling mismatch with Java 21 can break builds if not upgraded.

### 6.2 Mitigations
- Apply changes in small PRs: wrapper/tooling, Boot upgrade, Jakarta imports, Security, Swagger replacement, tests.
- Maintain feature parity and run smoke tests after each step.
- Use Spring Boot migration guide (2.x to 3.x) as reference during code updates.

### 6.3 Rollback plan
- Keep a branch/tag with the current Java 8 + Boot 2.1.4 state.
- If issues arise post-upgrade, revert to that branch and continue development while issues are addressed.
- Use feature flags to gate new behavior if necessary (not currently required).

## 7. Actionable code-change snippets

### 7.1 pom.xml (core upgrade outline)
```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.3.4</version>
  <relativePath/>
</parent>

<properties>
  <java.version>21</java.version>
  <maven.compiler.release>21</maven.compiler.release>
  <maven.compiler.parameters>true</maven.compiler.parameters>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<dependencies>
  <!-- core starters -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
  </dependency>

  <!-- H2 runtime -->
  <dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
  </dependency>

  <!-- Lombok -->
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
  </dependency>

  <!-- Replace Springfox with Springdoc -->
  <dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
  </dependency>

  <!-- testing -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
  </dependency>
</dependencies>

<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.11.0</version>
      <configuration>
        <release>21</release>
        <parameters>true</parameters>
      </configuration>
    </plugin>
  </plugins>
</build>
```

Remove:
- springfox-swagger2 and springfox-swagger-ui dependencies.

### 7.2 Replace Swagger config (remove Springfox ApplicationConfig and use springdoc defaults)
- Delete ApplicationConfig.java or leave class to host custom OpenAPI configuration if needed later. Springdoc will auto-configure Swagger UI at /swagger-ui.

Optional minimal config:
```java
@Configuration
public class OpenApiConfig {
  // Optional custom OpenAPI beans can be added here in the future
}
```

### 7.3 Security configuration replacement
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .headers(headers -> headers.frameOptions(frame -> frame.disable()))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
        .anyRequest().authenticated()
      )
      .httpBasic(Customizer.withDefaults());

    return http.build();
  }
}
```

### 7.4 JPA jakarta imports example (Transaction.java)
```diff
- import javax.persistence.Column;
- import javax.persistence.Entity;
- import javax.persistence.GeneratedValue;
- import javax.persistence.GenerationType;
- import javax.persistence.Id;
- import javax.persistence.Temporal;
- import javax.persistence.TemporalType;
+ import jakarta.persistence.Column;
+ import jakarta.persistence.Entity;
+ import jakarta.persistence.GeneratedValue;
+ import jakarta.persistence.GenerationType;
+ import jakarta.persistence.Id;
+ import jakarta.persistence.Temporal;
+ import jakarta.persistence.TemporalType;
```

Apply similar changes to all entities in model package:
- Account.java, Address.java, BankInfo.java, Contact.java, Customer.java, CustomerAccountXRef.java, Transaction.java

### 7.5 Test migration to JUnit 5
```diff
- import org.junit.Test;
- import org.junit.runner.RunWith;
- import org.springframework.test.context.junit4.SpringRunner;
+ import org.junit.jupiter.api.Test;

- @RunWith(SpringRunner.class)
 @SpringBootTest
-public class BankingApplicationTests {
+class BankingApplicationTests {

-   @Test
-   public void contextLoads() {
-   }
+   @Test
+   void contextLoads() {
+   }
 }
```

### 7.6 Remove add-opens from .mvn/jvm.config
- Start by emptying .mvn/jvm.config. If subsequent build warnings occur that require opens, document and re-add minimal necessary ones.

## 8. Validation checklist

- [ ] Maven Wrapper: 3.9.x, wrapper 3.2.x
- [ ] Java 21 set in pom
- [ ] Spring Boot 3.3.x parent
- [ ] Springfox removed; springdoc added
- [ ] Security config migrated to SecurityFilterChain
- [ ] Entities moved to jakarta.persistence imports
- [ ] Tests run on JUnit 5
- [ ] App starts and exposes:
  - /bank-api/swagger-ui.html
  - /bank-api/h2-console
  - endpoints functional
- [ ] No reflective access warnings on Java 21
- [ ] Smoke tests pass

## 9. Notes specific to this codebase

- Controllers currently use io.swagger.annotations; with springdoc, these may be replaced by io.swagger.v3.oas.annotations if you want to keep annotations. Springdoc also works by inferring from Spring MVC without annotations. Consider progressive replacement later.
- The code uses java.util.Date and @Temporal(TemporalType.TIME). JPA in Hibernate 6 supports java.time; consider migrating in a follow-up to improve robustness but not required for Java 21.
- Security: current config permits H2 console and root. Maintain parity in new SecurityFilterChain.

## 10. References

- Spring Boot 3.x migration guide (2.x to 3.x)
- Spring Security without WebSecurityConfigurerAdapter
- springdoc-openapi starter documentation
- Maven Wrapper 3.2.x docs
