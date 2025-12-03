# Task 01 – Update pom.xml for Java 21 and Spring Boot 3.2.x

## Purpose

This document explains the changes made to `pom.xml` to migrate the project to Java 21 and Spring Boot 3.2.x. It details updated versions, plugins, and dependencies required for compatibility and OpenAPI support.

## Files Changed

- `pom.xml`

## Changes Applied

### 1. Spring Boot parent upgraded to 3.2.x

The project now inherits from `spring-boot-starter-parent:3.2.10` to align with Spring Boot 3.2.x.

```diff
-  <parent>
-    <groupId>org.springframework.boot</groupId>
-    <artifactId>spring-boot-starter-parent</artifactId>
-    <version>2.x.y</version>
-  </parent>
+  <parent>
+    <groupId>org.springframework.boot</groupId>
+    <artifactId>spring-boot-starter-parent</artifactId>
+    <!-- Spring Boot parent constrained to 3.2.x line -->
+    <version>3.2.10</version>
+    <relativePath/>
+  </parent>
```

### 2. Java 21 configuration

The build now targets Java 21. Compiler settings are centralized and explicit:

```diff
-  <properties>
-    <java.version>17</java.version>
-  </properties>
+  <properties>
+    <!-- Global Java version used across the project -->
+    <java.version>21</java.version>
+
+    <!-- Maven compiler properties aligned with Java 21 -->
+    <maven.compiler.release>21</maven.compiler.release>
+    <maven.compiler.source>21</maven.compiler.source>
+    <maven.compiler.target>21</maven.compiler.target>
+
+    <!-- Keep parameter metadata for reflection / Spring MVC -->
+    <maven.compiler.parameters>true</maven.compiler.parameters>
+
+    <!-- Default source encoding -->
+    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
+  </properties>
```

The `maven-compiler-plugin` uses `release` 21:

```diff
+    <plugins>
+      <!-- Java compilation using --release 21 -->
+      <plugin>
+        <groupId>org.apache.maven.plugins</groupId>
+        <artifactId>maven-compiler-plugin</artifactId>
+        <version>3.13.0</version>
+        <configuration>
+          <release>21</release>
+          <encoding>${project.build.sourceEncoding}</encoding>
+          <parameters>${maven.compiler.parameters}</parameters>
+        </configuration>
+      </plugin>
+
+      <!-- Enable Spring Boot packaging and spring-boot:run -->
+      <plugin>
+        <groupId>org.springframework.boot</groupId>
+        <artifactId>spring-boot-maven-plugin</artifactId>
+      </plugin>
+    </plugins>
```

### 3. Surefire plugin upgraded

To ensure test execution compatibility with Java 21 and JUnit 5, Surefire is pinned to 3.2.5:

```diff
+  <build>
+    <!-- Plugin management with Java 21 compatible Surefire -->
+    <pluginManagement>
+      <plugins>
+        <plugin>
+          <groupId>org.apache.maven.plugins</groupId>
+          <artifactId>maven-surefire-plugin</artifactId>
+          <version>3.2.5</version>
+        </plugin>
+      </plugins>
+    </pluginManagement>
+    ...
+  </build>
```

### 4. JAXB dependencies for Java 21

Java 21 no longer ships JAXB in the JDK. The following dependencies are added for XML binding support:

```diff
+    <!-- JAXB (Jakarta XML Binding) for Java 21 -->
+    <dependency>
+      <groupId>jakarta.xml.bind</groupId>
+      <artifactId>jakarta.xml.bind-api</artifactId>
+      <version>4.0.2</version>
+    </dependency>
+    <dependency>
+      <groupId>org.glassfish.jaxb</groupId>
+      <artifactId>jaxb-runtime</artifactId>
+      <version>4.0.5</version>
+    </dependency>
```

### 5. OpenAPI via springdoc-openapi

Springfox (Swagger 2.x) is no longer used. Instead, `springdoc-openapi-starter-webmvc-ui` provides OpenAPI 3 with a modern Swagger UI:

```diff
+    <!-- OpenAPI/Swagger UI via springdoc -->
+    <dependency>
+      <groupId>org.springdoc</groupId>
+      <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
+      <version>2.6.0</version>
+    </dependency>
```

If your local or historical `pom.xml` still contained Springfox, ensure references like these are removed:

```xml
<!-- Example to remove if you still see it locally -->
<dependency>
  <groupId>io.springfox</groupId>
  <artifactId>springfox-swagger2</artifactId>
</dependency>
<dependency>
  <groupId>io.springfox</groupId>
  <artifactId>springfox-swagger-ui</artifactId>
</dependency>
```

## Commands to Test

From the project root:

```bash
# Clean build with Java 21
./mvnw -DskipTests clean package

# Verify tests execute under Surefire 3.2.5
./mvnw test

# Inspect effective POM to confirm versions
./mvnw help:effective-pom | sed -n '1,200p'
```

## Expected Output

- `./mvnw -DskipTests clean package` finishes successfully.
- Build log includes:
  - `maven-compiler-plugin@3.13.0` with `--release 21`.
  - `maven-surefire-plugin@3.2.5` for test execution.
- `./mvnw test` runs JUnit 5 tests without `UnsupportedClassVersionError`.

## Rollback Steps

To undo the POM changes:

1. Restore `pom.xml` from a previous commit:

   ```bash
   git checkout HEAD~1 -- pom.xml
   ```

2. Rebuild and re-run tests:

   ```bash
   ./mvnw -DskipTests clean package
   ./mvnw test
   ```

## Manual Review Notes

- Verify all deployment environments have a JDK 21 runtime (or a toolchain mapping to Java 21).
- Check CI agents for Java 21 compatibility; older images may still be pinned to JDK 11 or 17.
- If any custom module or plugin relies on pre-3.x Spring Boot APIs, ensure that transitive upgrades do not introduce `NoSuchMethodError` at runtime—see `docs/task-06-tests-and-ci.md` and `docs/task-07-rollback-and-risk.md` for guidance.
