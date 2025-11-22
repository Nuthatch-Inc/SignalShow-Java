# Maven Migration Plan for SignalShow

## Executive Summary

This document outlines the plan to migrate the SignalShow Java project from a traditional `javac`-based build system (circa 2005-2009) to a modern Maven-based build system, enabling Java 21 LTS upgrade and contemporary development practices.

---

## Current State Analysis

### Project Characteristics
- **Age**: Originally written 2005-2009
- **Source Files**: ~395 Java files
- **Build System**: Manual `javac` compilation via shell scripts
- **Package Structure**: Uses Java packages (`signals.*` and default package)
- **Dependencies**: 
  - Java Advanced Imaging (JAI): `jai_core.jar`, `jai_codec.jar` (bundled)
  - Java Swing (built-in)
- **Entry Point**: `SignalShow.java` (default package)
- **Current Java**: Unspecified (targeting JDK 11+)
- **Target Java**: Java 21 LTS

### Current Directory Structure
```
SignalShow-Java/
├── SignalShow/
│   ├── SignalShow.java          # Main class (default package)
│   ├── signals/                 # Application packages
│   │   ├── action/
│   │   ├── core/
│   │   ├── demo/
│   │   ├── functionterm/
│   │   ├── gui/
│   │   ├── io/
│   │   ├── operation/
│   │   └── ...
│   ├── jai_core.jar             # Bundled dependency
│   ├── jai_codec.jar            # Bundled dependency
│   ├── images/                  # Resources
│   ├── demoIcons/
│   ├── guiIcons/
│   └── ...
├── run-signalshow.sh            # Launch script
└── README.md
```

### Current Build Process
1. Shell script compiles all `.java` files with `javac`
2. Manually sets classpath to include JAI jars
3. Runs `java SignalShow` to launch application

### Pain Points
- ❌ Manual dependency management
- ❌ No standardized build lifecycle
- ❌ Cannot use modern Java tooling (upgrade assistants, linters, etc.)
- ❌ Difficult for new contributors to build
- ❌ No test framework integration
- ❌ No IDE auto-configuration

---

## Migration Goals

### Primary Objectives
1. ✅ **Enable Java 21 LTS upgrade** with automated tooling support
2. ✅ **Standardize build process** using Maven conventions
3. ✅ **Automate dependency management** (replace bundled JARs)
4. ✅ **Preserve all functionality** (zero behavioral changes)
5. ✅ **Maintain backward compatibility** (keep old scripts working initially)

### Secondary Benefits
- Modern IDE integration (IntelliJ, VS Code, Eclipse)
- Easy test framework addition (JUnit 5)
- CI/CD ready (GitHub Actions, etc.)
- Reproducible builds
- Dependency vulnerability scanning
- Future-proof for Java ecosystem evolution

---

## Migration Steps

### Phase 1: Maven Project Setup (No Code Changes)

#### Step 1.1: Create `pom.xml`
Create Maven Project Object Model file in project root with:
- **GroupId**: `com.signalshow` (or your preferred namespace)
- **ArtifactId**: `signalshow`
- **Version**: `1.0.0-SNAPSHOT`
- **Java Version**: 21 (source and target compatibility)
- **Main Class**: `SignalShow`

#### Step 1.2: Configure Dependencies
Replace bundled JARs with Maven coordinates:
- **JAI Core**: `javax.media.jai:jai-core:1.1.3` (from Maven Central or GeoTools repository)
- **JAI Codec**: `javax.media.jai:jai-codec:1.1.3`

**Note**: JAI is old and may require adding GeoTools or OSGeo repositories if not in Maven Central.

#### Step 1.3: Configure Build Plugins
- **maven-compiler-plugin**: Set Java 21 source/target
- **maven-jar-plugin**: Configure main class manifest
- **maven-shade-plugin**: Create executable JAR with dependencies (fat JAR)
- **exec-maven-plugin**: Enable `mvn exec:java` for development

#### Step 1.4: Resource Configuration
Configure Maven to include non-Java resources:
- Images from `SignalShow/images/`
- Icons from `SignalShow/*Icons/` directories
- Any other data files

---

### Phase 2: Directory Restructure

#### Step 2.1: Create Maven Standard Directories
```
mkdir -p src/main/java
mkdir -p src/main/resources
mkdir -p src/test/java
mkdir -p src/test/resources
```

#### Step 2.2: Move Source Files
**Java Sources:**
```
SignalShow/SignalShow.java        → src/main/java/SignalShow.java
SignalShow/signals/**/*.java      → src/main/java/signals/**/*.java
```

**Resources:**
```
SignalShow/images/                → src/main/resources/images/
SignalShow/demoIcons/             → src/main/resources/demoIcons/
SignalShow/guiIcons/              → src/main/resources/guiIcons/
SignalShow/plotIcons/             → src/main/resources/plotIcons/
SignalShow/operationIcons/        → src/main/resources/operationIcons/
```

**Documentation (stays in root or moves to docs/):**
```
SignalShow/doc/                   → docs/api/ (or keep in place)
SignalShow/functiondoc/           → docs/functions/
SignalShow/operationdoc/          → docs/operations/
```

#### Step 2.3: Remove Bundled JARs
Once Maven is working:
```
rm SignalShow/jai_core.jar
rm SignalShow/jai_codec.jar
```
(Maven will download these automatically)

#### Step 2.4: Update Resource Loaders
Any code using `ResourceLoader` or loading resources may need path updates:
- Old: `images/icon.png`
- New: `/images/icon.png` (leading slash for classpath resources)

This will be verified during testing.

---

### Phase 3: Update Build Scripts

#### Step 3.1: Create New Maven Launch Script
Create `run-maven.sh`:
```bash
#!/usr/bin/env bash
# Build and run using Maven
mvn clean compile
mvn exec:java -Dexec.mainClass="SignalShow"
```

#### Step 3.2: Update Existing Script (Backward Compatibility)
Modify `run-signalshow.sh` to detect Maven and use it preferentially:
```bash
if command -v mvn &> /dev/null && [ -f pom.xml ]; then
    echo "Using Maven build..."
    mvn clean compile exec:java -Dexec.mainClass="SignalShow"
else
    # Fall back to old javac method
    [existing script logic]
fi
```

#### Step 3.3: Create Quick Reference Scripts
```bash
compile.sh     → mvn clean compile
run.sh         → mvn exec:java
package.sh     → mvn clean package
test.sh        → mvn test
```

---

### Phase 4: Java 21 Upgrade

#### Step 4.1: Compile with Java 21
With Maven configured for Java 21, run:
```bash
mvn clean compile
```

Address any compilation errors:
- Deprecated API usage (unlikely for Swing app)
- Removed APIs (very rare between Java 11 and 21)
- Warnings about legacy code patterns

#### Step 4.2: Run Automated Upgrade Tools
Now that Maven is set up, use the Java upgrade tools:
```bash
# These tools will now work!
generate_upgrade_plan_for_java_project
setup_development_environment_for_upgrade
upgrade_java_project_using_openrewrite
```

OpenRewrite can automatically fix:
- Deprecated API usage
- Modern Java idioms
- Security vulnerabilities
- Code style modernization

#### Step 4.3: Test Application
Run full application and test major features:
- [ ] Application launches
- [ ] GUI renders correctly
- [ ] Signal generation works
- [ ] Operations (FFT, filtering, etc.) function
- [ ] Image loading/saving works
- [ ] Demos run without errors

---

### Phase 5: Validation & Cleanup

#### Step 5.1: Verify Resource Loading
Check that all resources load correctly:
- Icons appear in GUI
- Images load properly
- Demo data accessible

#### Step 5.2: Update Documentation
Update `README.md` to reflect Maven build:
```markdown
## Building with Maven

### Prerequisites
- Java 21 (JDK)
- Maven 3.8+

### Quick Start
```bash
mvn clean package
java -jar target/signalshow-1.0.0-SNAPSHOT-shaded.jar
```

### Development
```bash
mvn clean compile      # Compile
mvn exec:java          # Run
mvn test               # Run tests
mvn package            # Create JAR
```
\```
```

#### Step 5.3: Create `.gitignore` for Maven
Add Maven-specific ignores:
```
target/
*.iml
.idea/
.classpath
.project
.settings/
```

#### Step 5.4: Archive Old Build System ✅ COMPLETED

The legacy build system has been removed. Only JAI dependencies remain in `legacy-build/`:
```
legacy-build/
├── jai_core.jar
└── jai_codec.jar
```

These JAR files must be retained as they are not available in public Maven repositories and are required for:
- Initial setup (installing to local Maven repository)
- GitHub Actions CI/CD builds

---

## Detailed File Changes

### New Files to Create

#### `pom.xml` (Maven configuration)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.signalshow</groupId>
    <artifactId>signalshow</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>SignalShow</name>
    <description>Educational signal and image processing application</description>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <exec.mainClass>SignalShow</exec.mainClass>
    </properties>

    <repositories>
        <!-- May need GeoTools or OSGeo for JAI -->
        <repository>
            <id>osgeo</id>
            <name>OSGeo Release Repository</name>
            <url>https://repo.osgeo.org/repository/release/</url>
        </repository>
    </repositories>

    <dependencies>
        <!-- Java Advanced Imaging -->
        <dependency>
            <groupId>javax.media.jai</groupId>
            <artifactId>jai-core</artifactId>
            <version>1.1.3</version>
        </dependency>
        <dependency>
            <groupId>javax.media.jai</groupId>
            <artifactId>jai-codec</artifactId>
            <version>1.1.3</version>
        </dependency>
        
        <!-- Testing (to be added later) -->
        <!-- <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.1</version>
            <scope>test</scope>
        </dependency> -->
    </dependencies>

    <build>
        <plugins>
            <!-- Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <release>21</release>
                </configuration>
            </plugin>

            <!-- Executable JAR with dependencies -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>SignalShow</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Exec Plugin for mvn exec:java -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <mainClass>SignalShow</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### `.mvn/maven.config` (optional - Maven wrapper config)
```
-T 1C
```

### Files to Modify

#### `.gitignore`
Add:
```
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties

# IDE
*.iml
.idea/
.vscode/
.classpath
.project
.settings/
```

---

## Risk Analysis & Mitigation

### Risks

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| JAI not in Maven Central | High | High | Use GeoTools/OSGeo repository or install to local Maven repo |
| Resource paths break | Medium | Medium | Test thoroughly; use ClassLoader for resources |
| Code incompatible with Java 21 | Low | Medium | Swing is stable; review deprecation warnings |
| Build time increase | Low | Low | Maven caching minimizes rebuilds |
| Team learning curve | Medium | Low | Provide clear docs and scripts |

### Rollback Plan
If migration fails:
1. Keep old `SignalShow/` directory intact initially
2. Git branch for migration work (`feature/maven-migration`)
3. Can revert to `run-signalshow.sh` script anytime
4. No code changes in Phase 1, easy to abandon

---

## Testing Strategy

### Pre-Migration Tests
1. Document current functionality:
   - [ ] Launch app and capture screenshots
   - [ ] Test each demo
   - [ ] Test file I/O
   - [ ] Note any existing issues

### Post-Migration Tests
1. **Smoke Tests**:
   - [ ] `mvn clean compile` succeeds
   - [ ] `mvn exec:java` launches application
   - [ ] Main window appears
   
2. **Functional Tests**:
   - [ ] All menu items accessible
   - [ ] Signal generation works
   - [ ] Operations execute without errors
   - [ ] Plots render correctly
   - [ ] Demos run successfully
   - [ ] Save/load functions work

3. **Regression Tests**:
   - [ ] Compare screenshots pre/post migration
   - [ ] Verify numerical outputs unchanged
   - [ ] Check all icons/images load

### Automated Testing (Future)
After migration, add:
- Unit tests for core algorithms
- Integration tests for file I/O
- GUI tests with AssertJ Swing

---

## Timeline Estimate

| Phase | Estimated Time | Complexity |
|-------|----------------|------------|
| Phase 1: Maven Setup | 30-60 minutes | Low |
| Phase 2: Directory Restructure | 30-45 minutes | Low |
| Phase 3: Update Scripts | 15-30 minutes | Low |
| Phase 4: Java 21 Upgrade | 1-2 hours | Medium |
| Phase 5: Validation | 1-2 hours | Medium |
| **Total** | **3-6 hours** | **Low-Medium** |

*Assumes no major compatibility issues. JAI dependency resolution may add time.*

---

## Success Criteria

Migration is successful when:
- ✅ `mvn clean package` builds without errors
- ✅ Application launches via `mvn exec:java`
- ✅ All functional tests pass
- ✅ Executable JAR works standalone: `java -jar target/signalshow-*.jar`
- ✅ Automated Java upgrade tools can be used
- ✅ Documentation updated
- ✅ Team can build and run without manual intervention

---

## Next Steps

### Immediate Actions
1. **Review this plan** - confirm approach is acceptable
2. **Choose GroupId** - decide on package namespace (e.g., `com.signalshow`)
3. **Backup current state** - commit or tag current working version
4. **Create migration branch** - `git checkout -b feature/maven-migration`

### When Ready to Proceed
Let me know, and I will:
1. Create `pom.xml` with all configurations
2. Set up Maven directory structure
3. Move source files to standard layout
4. Update build scripts
5. Test compilation and execution
6. Run Java 21 upgrade tools
7. Validate all functionality
8. Update documentation

---

## Questions to Answer Before Starting

Please confirm:
1. **GroupId preference**: `com.signalshow`, `org.signalshow`, or other?
2. **Keep old structure**: Archive `SignalShow/` directory or delete after migration?
3. **Version number**: Start with `1.0.0-SNAPSHOT` or different version?
4. **Testing**: Do you want me to proceed even if JAI dependencies require manual setup?
5. **Documentation**: Move HTML docs (`SignalShow/doc/`) or leave in place?

---

## Resources for Learning Maven

After migration, helpful resources:
- **Maven in 5 Minutes**: https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
- **POM Reference**: https://maven.apache.org/pom.html
- **Maven Lifecycle**: https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html
- **Dependency Search**: https://search.maven.org/

---

## Appendix: Maven Command Reference

Common commands you'll use:

```bash
# Clean build artifacts
mvn clean

# Compile source code
mvn compile

# Run tests (when you add them)
mvn test

# Package into JAR
mvn package

# Run application
mvn exec:java

# Full clean build
mvn clean package

# Skip tests (during development)
mvn package -DskipTests

# Update dependencies
mvn dependency:tree
mvn versions:display-dependency-updates

# Generate project from archetype (for future projects)
mvn archetype:generate
```

---

**End of Migration Plan**

*When you're ready to proceed, I will execute this plan step-by-step, keeping you informed of progress and any issues that arise.*
