# Build Notes: Maven resources copy failure for application.yml

Issue
- The build failed at maven-resources-plugin:3.2.0 `resources` goal with "Operation not permitted" when copying `application.yml` into `target/classes`.
- This is typically caused by a stale `target/classes/application.yml` created by a previous process with immutable/locked attributes or different ownership (e.g., created by root).

Root cause
- In some CI/container environments, a previous run or external process can create `target` files owned by root or with immutable attributes (`chattr +i`), making overwrites fail.
- Filtering or overwrite behavior of maven-resources-plugin can attempt atomic replace, which triggers permission errors on such files.

What we changed
- Disabled overwrite for resources and ensured YAML/properties are not filtered to avoid unnecessary rewriting:
  - maven-resources-plugin: overwrite=false; nonFilteredFileExtensions includes yml, yaml, properties.
- Excluded any accidental inclusion of `target/**` within resources inputs.
- Kept resources filtering disabled globally for `src/main/resources`.

What to do if the issue reoccurs locally
1) Clean target directory fully:
   - rm -rf target
2) If deletion fails, check ownership/attributes:
   - ls -la target/classes/application.yml
   - sudo chattr -i target/classes/application.yml   # if immutable
   - sudo chown -R $(whoami):$(id -gn) target
   - rm -rf target
3) Rebuild:
   - mvn -q -DskipTests package

Note
- This project pins maven-resources-plugin 3.2.0 and avoids filtering YAML to minimize file operations on resources.
- If you introduce resource filtering in the future, consider separating filtered and non-filtered resources directories to keep YAML unfiltered.
