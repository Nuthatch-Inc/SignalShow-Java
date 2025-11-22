# JAI Dependencies

This directory contains Java Advanced Imaging (JAI) library dependencies that are **required** for SignalShow but are **not available** in public Maven repositories.

## Files

- `jai_core.jar` - JAI core classes (version 1.1.3)
- `jai_codec.jar` - JAI codec classes (version 1.1.3)

## Why These Files Must Be Kept

These JAR files cannot be deleted because:

1. **Not in Maven Central**: JAI libraries are not published to Maven Central or other public repositories
2. **Required for Builds**: The application uses JAI for image processing operations
3. **CI/CD Dependency**: GitHub Actions workflows install these JARs during automated builds
4. **Developer Setup**: New developers must install these to their local Maven repository

## Installation

To use SignalShow, you must install these JAR files to your local Maven repository:

```bash
# From the project root directory
mvn install:install-file -Dfile=legacy-build/jai_core.jar \
  -DgroupId=javax.media.jai -DartifactId=jai-core \
  -Dversion=1.1.3 -Dpackaging=jar

mvn install:install-file -Dfile=legacy-build/jai_codec.jar \
  -DgroupId=javax.media.jai -DartifactId=jai-codec \
  -Dversion=1.1.3 -Dpackaging=jar
```

This only needs to be done once per development machine.

## Legacy Code Removed

As of the Maven migration, all other legacy build artifacts have been removed from this directory. The source code now lives in the standard Maven structure:

- `src/main/java/` - Java source files
- `src/main/resources/` - Application resources
- `pom.xml` - Maven build configuration

The old `run-signalshow.sh` script and pre-Maven source code have been deleted as they are no longer needed.
