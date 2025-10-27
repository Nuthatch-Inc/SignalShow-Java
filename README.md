# SignalShow

## Overview

SignalShow is an educational signal processing application, originally built in Java Swing. This proposal explores modernizing it for web and desktop platforms.

**Vision**: "SignalShow is to DSP what Desmos is to algebra"

**Status**: Early prototype phase

### Versions

1. **Web Version** - Browser-based prototype in React/Plotly.js
2. **Desktop Version** - Proposed Tauri app with optional Julia backend
3. **Java Version** - Original Swing GUI with 80+ functions (legacy)

---

## Web Prototype

**Repository**: `/nuthatch-desktop`

### Features

- 4 signal types: Sine, Cosine, Chirp, Gaussian
- Interactive waveform visualization (Plotly.js)
- FFT analysis
- Window functions: Hanning, Hamming, Blackman
- 5 educational demos
- JSON export
- Responsive 3-column layout

### Components

- **SignalDisplay** - Interactive waveform plots
- **FunctionGenerator** - Signal parameter controls
- **OperationsPanel** - FFT and windowing operations
- **DemoGallery** - Educational examples

### Development

```bash
cd /Users/julietfiss/src/nuthatch-desktop
npm install
npm run dev
# Open http://localhost:5173
```

### Planned (Phase 2)

- Rust/WebAssembly backend for performance
- Additional signal types and operations
- Advanced filtering
- 3D visualizations

---

## Java Desktop Version

The original implementation with 80+ functions and 40+ operations, now using Maven.

### Key Features

- Analytic function generators (Gaussian, Chirp, Delta, Bessel, windows, noise models, etc.)
- 1‑D / 2‑D interactive plotting (zoom, pan, overlays)
- Frequency-domain and convolution operations
- Educational demos (sampling, filtering, holography, more)
- Modular operation architecture

### Building with Maven

```bash
# Compile
mvn clean compile

# Run directly
mvn exec:java

# Or use convenience scripts
./compile.sh    # Compile the project
./run-maven.sh  # Run with Maven
./package.sh    # Create executable JAR
./run.sh        # Run from JAR
```

### Project Structure

```
src/main/java/           # Java source files
  SignalShow.java        # Main entry point
  signals/               # Application packages
src/main/resources/      # Images, icons, documentation
pom.xml                  # Maven configuration
legacy-build/            # Original build artifacts
  jai_core.jar           # JAI core classes (installed to local Maven)
  jai_codec.jar          # JAI codec classes (installed to local Maven)
```

### Prerequisites

- Java Development Kit (JDK) 11+
- Bundled JAI jars: `jai_core.jar`, `jai_codec.jar`

### Quick Start

```bash
./run-signalshow.sh
```

This script verifies JAI jars, sets the classpath, and launches the main class.

### Building from Source

```bash
find SignalShow -name "*.java" > /tmp/sigshow-srcs.txt
javac -d . @/tmp/sigshow-srcs.txt
java -cp "SignalShow:SignalShow/jai_core.jar:SignalShow/jai_codec.jar" SignalShow
```

Or one-liner:

```bash
find SignalShow -name "*.java" -print0 | xargs -0 javac -d . && \
java -cp "SignalShow:SignalShow/jai_core.jar:SignalShow/jai_codec.jar" SignalShow
```

---

## Troubleshooting

| Symptom                                             | Likely Cause                     | Resolution                                                                             |
| --------------------------------------------------- | -------------------------------- | -------------------------------------------------------------------------------------- |
| `NoClassDefFoundError: javax/media/jai/PlanarImage` | JAI jars not on classpath        | Ensure classpath includes JAI jars. Remove any extracted `SignalShow/javax` directory. |
| `SecurityException: sealing violation`              | Duplicate package definition     | Delete stray `javax/` directory.                                                       |
| `UnsatisfiedLinkError` for JAI native libs          | Platform mismatch (x86 vs arm64) | Run under Rosetta with x86 JDK, or obtain arm64 native builds.                         |

### Diagnostics

List native libraries:

```bash
find . -type f \( -name "*.so" -o -name "*.dylib" -o -name "*.jnilib" -o -name "*.dll" \) -print
```

Check jar contents:

```bash
jar tf SignalShow/jai_core.jar | grep javax/media/jai/PlanarImage.class
```

Remove accidentally extracted classes:

```bash
rm -rf SignalShow/javax
```

---

## Extending the Application

New analytic functions or operations can be added by creating classes under `signals/...` subpackages and wiring them into existing registries or menus.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit changes with clear messages
4. Open a pull request

## License

Refer to LICENSE file or header comments in source. JAI libraries are bundled in binary form; their original licensing terms apply.
