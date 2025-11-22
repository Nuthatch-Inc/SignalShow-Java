# Build Instructions

## Essential Maven Commands

**Clean and compile from scratch:**
```bash
mvn clean compile
```
- `clean` deletes the `target/` directory (removes old compiled files)
- `compile` compiles all Java source files to `target/classes/`

**Build the executable JAR file:**
```bash
mvn package
```
- This runs `compile` plus creates the JAR file in `target/signalshow-1.0.0-SNAPSHOT.jar`
- This is what you need to run the application

**Skip tests during packaging (faster):**
```bash
mvn package -DskipTests
```

**Run the application:**
```bash
./run.sh
```
- This script checks if the JAR exists, builds it if needed, then runs it

## Common Workflow

**After making code changes:**
```bash
mvn package -DskipTests && ./run.sh
```

**Full clean build:**
```bash
mvn clean package -DskipTests
```

## Key Points

- **`mvn compile`** is NOT enough to run the app - it only compiles classes but doesn't create the JAR
- **`mvn package`** is what you need - it compiles AND creates the runnable JAR
- The app runs from the JAR file, not directly from compiled classes
- `target/` directory contains all build output (can be safely deleted with `mvn clean`)

## Other Useful Commands

**Run tests:**
```bash
mvn test
```

**See dependency tree:**
```bash
mvn dependency:tree
```

**Install to local Maven repository:**
```bash
mvn install
```
