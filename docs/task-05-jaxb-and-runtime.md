# Task 05 – JAXB API and Runtime for Java 21

## Purpose

This document explains why JAXB dependencies are required on Java 21, how they are configured in `pom.xml`, and how to validate they are present at runtime.

## Files Changed

- `pom.xml`

## Changes Applied

### 1. JAXB API and runtime added

Java 21 no longer ships JAXB in the JDK modules, so applications that require JAXB (directly or via libraries) must add explicit dependencies.

The following block has been added to `pom.xml`:

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

This ensures both the Jakarta XML Binding API and a compatible runtime implementation are supplied.

## Commands to Test

### 1. Build and inspect dependencies

```bash
# Build without tests (ensures dependencies can be resolved)
./mvnw -DskipTests clean package

# Inspect the dependency tree (first 200 lines)
./mvnw dependency:tree | sed -n '1,200p'
```

Look for these artifacts in the dependency tree output:

- `jakarta.xml.bind:jakarta.xml.bind-api:4.0.2`
- `org.glassfish.jaxb:jaxb-runtime:4.0.5`

### 2. Optional: Runtime smoke test (if JAXB is used)

If you have JAXB-based code paths, exercise them via existing tests, or create a small unit test that:

- Marshals a simple object to XML.
- Unmarshals XML back to an object.

Ensure this runs successfully on Java 21 with no `ClassNotFoundException` or `NoClassDefFoundError` related to JAXB classes.

## Expected Output

- `./mvnw -DskipTests clean package` completes without JAXB-related errors.
- `./mvnw dependency:tree | sed -n '1,200p'` includes:

  ```text
  jakarta.xml.bind:jakarta.xml.bind-api:4.0.2
  org.glassfish.jaxb:jaxb-runtime:4.0.5
  ```

- No runtime errors such as:

  - `java.lang.NoClassDefFoundError: jakarta/xml/bind/JAXBException`
  - `ClassNotFoundException: jakarta.xml.bind.ContextFinder`

## Rollback Steps

If you need to temporarily remove JAXB dependencies (for example, to diagnose conflicts):

1. Comment out or remove the JAXB dependency block in `pom.xml`, or:

   ```bash
   git checkout HEAD~1 -- pom.xml
   ```

2. Rebuild:

   ```bash
   ./mvnw -DskipTests clean package
   ```

3. If errors appear that mention missing JAXB classes, restore the JAXB dependencies:

   ```bash
   git checkout HEAD -- pom.xml
   ```

   followed by another build.

## Manual Review Notes

- Even if the current code does not explicitly use JAXB, some third-party libraries may require it at runtime. The explicit dependencies improve resilience across JDK versions.
- If you later upgrade dependencies that bundle their own JAXB or switch to JSON-only serialization, you may revisit whether the explicit JAXB dependencies are still needed.
- Monitor for version conflicts:
  - If you see `NoSuchMethodError` or `LinkageError` involving JAXB classes, check for multiple versions of JAXB-related artifacts in the dependency tree and adjust versions accordingly.
