SignalShow
===========

Overview
--------
SignalShow is a Java-based educational and exploratory signal processing application. It enables creation, visualization, and manipulation of 1‑D and 2‑D analytic functions and sampled data, with interactive plots and a library of signal and image processing operations (Fourier transforms, filtering, convolution, hologram demos, etc.). The user interface is implemented with Swing. Source code resides under the `SignalShow/` directory; the entry point is the top-level `SignalShow` class.

Key Features
------------
* Analytic function generators (Gaussian, Chirp, Delta, Bessel, windows, noise models, etc.)
* 1‑D / 2‑D interactive plotting (zoom, pan, overlays)
* Frequency-domain and convolution operations
* Educational demos (sampling, filtering, holography, more)
* Modular operation architecture (extend with new function/operation classes)

Project Structure (high level)
------------------------------
```
SignalShow/
  SignalShow.java          # Main entry point (default package)
  jai_core.jar             # JAI core classes (bundled)
  jai_codec.jar            # JAI codec classes (bundled)
  signals/                 # Application packages (core, gui, operations, demos, etc.)
  run-signalshow.sh        # Convenience launcher script
```

Prerequisites
-------------
* Java Development Kit (JDK) 11 or later (older versions may also work; modules are not strictly required)
* Bundled Java Advanced Imaging (JAI) jars: `jai_core.jar`, `jai_codec.jar` (already present)

Quick Start
-----------
From the repository root:

```bash
./run-signalshow.sh
```

This script:
1. Verifies required JAI jars exist.
2. Sets the classpath to include `SignalShow/` (compiled classes) and the two JAI jars.
3. Launches the main class `SignalShow`.

Building from Source
--------------------
If you need to (re)compile the Java sources (e.g. after modifications):

```bash
find SignalShow -name "*.java" > /tmp/sigshow-srcs.txt
javac -d . @/tmp/sigshow-srcs.txt
```

Then run:

```bash
java -cp "SignalShow:SignalShow/jai_core.jar:SignalShow/jai_codec.jar" SignalShow
```

Alternatively, a one-liner compile + run:

```bash
find SignalShow -name "*.java" -print0 | xargs -0 javac -d . && \
java -cp "SignalShow:SignalShow/jai_core.jar:SignalShow/jai_codec.jar" SignalShow
```

Troubleshooting
---------------
| Symptom | Likely Cause | Resolution |
|---------|--------------|-----------|
| `NoClassDefFoundError: javax/media/jai/PlanarImage` | JAI jars not on classpath or duplicate extracted classes causing conflicts | Ensure classpath includes `SignalShow/jai_core.jar:SignalShow/jai_codec.jar`. Remove any accidentally extracted `SignalShow/javax` directory. |
| `SecurityException: sealing violation: can't seal package javax.media.jai` | Duplicate package definition (e.g. extracted `javax/media/jai` alongside jar) | Delete the stray `javax/` directory so only the jar supplies the package. |
| `UnsatisfiedLinkError` referencing JAI native libs | Platform mismatch (x86 vs arm64) or missing native library | Run under Rosetta with an x86 JDK, or obtain arm64 native builds if available. |

Diagnostics Helpers
-------------------
List any native libraries (none are expected in a clean tree):
```bash
find . -type f \( -name "*.so" -o -name "*.dylib" -o -name "*.jnilib" -o -name "*.dll" \) -print
```

Check a specific class inside a jar:
```bash
jar tf SignalShow/jai_core.jar | grep javax/media/jai/PlanarImage.class
```

Remove accidentally extracted classes:
```bash
rm -rf SignalShow/javax
```

Extending the Application
-------------------------
New analytic functions or operations can be added by creating additional classes under appropriate `signals/...` subpackages (e.g. function terms or operation implementations) and wiring them into existing registries or menus in the GUI layer.

Contributing
------------
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/your-feature`).
3. Commit changes with clear messages.
4. Open a pull request describing the enhancement or fix.

License / Attribution
---------------------
Refer to any LICENSE file or header comments included in the source for licensing terms. This README describes build and runtime procedures and does not alter licensing. Java Advanced Imaging (JAI) libraries are bundled in binary form; their original licensing terms apply.

Acknowledgements
----------------
JAI libraries and any referenced academic/educational materials belong to their respective owners. The architecture facilitates educational exploration of signal and image processing concepts.

Developer Notes
---------------
Temporary local debug helper classes (e.g., small loader or reflection test snippets) are intentionally excluded via `.gitignore` to keep the repository clean. If you create ad-hoc helper files while experimenting, consider following the existing ignore patterns so they are not committed inadvertently.

