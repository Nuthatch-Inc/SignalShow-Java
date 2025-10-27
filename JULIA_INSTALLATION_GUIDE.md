# Julia Installation Guide for SignalShow (Proposed)

**⚠️ PROTOTYPE STATUS**: The Desktop Version is an early-stage prototype and **not ready for use or testing**. This guide describes a **proposed** feature that is subject to change.

**Purpose**: This guide describes the proposed Julia installation process for the SignalShow Desktop Version backend server.

**Important**: SignalShow has two versions under development:

- **Web Version**: Browser-based prototype in development, no Julia installation required
- **Desktop Version (Nuthatch/Tauri)**: Early prototype with proposed Julia backend integration

**Both versions are prototypes not ready for testing or use.** This guide is for reference only.

**Target Audience**: Developers reviewing the proposed architecture

**Time Required**: Reference documentation only

---

## Table of Contents

1. [System Requirements](#system-requirements)
2. [Installation Methods](#installation-methods)
3. [Step-by-Step Installation](#step-by-step-installation)
4. [Verifying Installation](#verifying-installation)
5. [Installing Required Packages](#installing-required-packages)
6. [Running the SignalShow Backend](#running-the-signalshow-backend)
7. [Troubleshooting](#troubleshooting)
8. [Updating Julia](#updating-julia)

---

## System Requirements

### Minimum Requirements

- **Operating System**:
  - macOS 10.9+ (64-bit)
  - Windows 7+ (64-bit)
  - Linux (64-bit) - most distributions
- **RAM**: 2 GB minimum, 8 GB recommended
- **Disk Space**: 500 MB for Julia, additional 1-2 GB for packages
- **Processor**: Any modern 64-bit CPU

### Recommended Configuration

- **RAM**: 16 GB or more (for large signal processing operations)
- **Processor**: Quad-core CPU or better
- **SSD**: For faster package loading and compilation

---

## Installation Methods

There are three primary methods to install Julia:

| Method                    | Best For                   | Pros                             | Cons                         |
| ------------------------- | -------------------------- | -------------------------------- | ---------------------------- |
| **Official Installer**    | Most users                 | Simple, beginner-friendly        | Manual PATH setup on Windows |
| **juliaup** (Recommended) | Power users                | Easy updates, version management | Command-line installation    |
| **Package Manager**       | Linux/macOS advanced users | System integration               | May have older versions      |

We recommend **juliaup** for most users as it simplifies version management and updates.

---

## Step-by-Step Installation

### Option 1: juliaup (Recommended) ⭐

**juliaup** is the official Julia version manager that makes installation and updates simple.

#### macOS / Linux

1. **Open Terminal**

2. **Install juliaup**:

   ```bash
   curl -fsSL https://install.julialang.org | sh
   ```

3. **Follow the prompts**:

   - The installer will ask to modify your shell configuration (`.bashrc`, `.zshrc`, etc.)
   - Type `y` to accept

4. **Restart your terminal** or run:

   ```bash
   source ~/.bashrc  # or ~/.zshrc for zsh
   ```

5. **Verify installation**:

   ```bash
   julia --version
   ```

   You should see output like:

   ```
   julia version 1.10.0
   ```

#### Windows

1. **Open PowerShell** or **Command Prompt**

2. **Install juliaup via winget** (Windows 10/11):

   ```powershell
   winget install julia -s msstore
   ```

   **OR** download and run the installer from:

   - [https://install.julialang.org](https://install.julialang.org)

3. **Restart your terminal**

4. **Verify installation**:
   ```powershell
   julia --version
   ```

---

### Option 2: Official Installer

If you prefer a traditional installer:

#### macOS

1. **Download the installer**:

   - Go to [https://julialang.org/downloads/](https://julialang.org/downloads/)
   - Download the `.dmg` file for macOS (Intel or Apple Silicon)

2. **Install Julia**:

   - Open the downloaded `.dmg` file
   - Drag `Julia-1.10.app` to your Applications folder

3. **Add Julia to PATH** (optional but recommended):

   ```bash
   # Add to ~/.zshrc or ~/.bashrc
   export PATH="/Applications/Julia-1.10.app/Contents/Resources/julia/bin:$PATH"
   ```

   Then restart Terminal or run:

   ```bash
   source ~/.zshrc
   ```

4. **Verify**:
   ```bash
   julia --version
   ```

#### Windows

1. **Download the installer**:

   - Go to [https://julialang.org/downloads/](https://julialang.org/downloads/)
   - Download the `.exe` installer for Windows (64-bit)

2. **Run the installer**:

   - Double-click the downloaded `.exe` file
   - Follow the installation wizard
   - ✅ **Important**: Check "Add Julia to PATH" during installation

3. **Verify**:
   - Open Command Prompt or PowerShell
   ```powershell
   julia --version
   ```

#### Linux

1. **Download the tarball**:

   ```bash
   cd ~/Downloads
   wget https://julialang-s3.julialang.org/bin/linux/x64/1.10/julia-1.10.0-linux-x86_64.tar.gz
   ```

2. **Extract to `/opt`** (or your preferred location):

   ```bash
   sudo tar -xvzf julia-1.10.0-linux-x86_64.tar.gz -C /opt/
   ```

3. **Create symbolic link**:

   ```bash
   sudo ln -s /opt/julia-1.10.0/bin/julia /usr/local/bin/julia
   ```

4. **Verify**:
   ```bash
   julia --version
   ```

---

### Option 3: Package Managers

#### macOS (Homebrew)

```bash
brew install julia
```

#### Linux (APT - Ubuntu/Debian)

```bash
sudo apt update
sudo apt install julia
```

**Note**: Package manager versions may be older. For the latest version, use juliaup or the official installer.

---

## Verifying Installation

After installation, verify Julia is working correctly:

1. **Check version**:

   ```bash
   julia --version
   ```

2. **Start Julia REPL** (interactive shell):

   ```bash
   julia
   ```

   You should see:

   ```
                  _
      _       _ _(_)_     |  Documentation: https://docs.julialang.org
     (_)     | (_) (_)    |
      _ _   _| |_  __ _   |  Type "?" for help, "]" for Pkg mode.
     | | | | | | |/ _` |  |
     | | |_| | | | (_| |  |  Version 1.10.0 (2023-12-25)
    _/ |\__'_|_|_|\__'_|  |  Official https://julialang.org/ release
   |__/                   |

   julia>
   ```

3. **Test basic computation**:

   ```julia
   julia> 2 + 2
   4

   julia> println("Hello, SignalShow!")
   Hello, SignalShow!
   ```

4. **Exit Julia**:

   ```julia
   julia> exit()
   ```

   Or press `Ctrl+D`

---

## Installing Required Packages

SignalShow requires several Julia packages for signal processing. Here's how to install them:

### Method 1: Automatic Installation (Recommended)

SignalShow will include a setup script. When you first run the backend:

```bash
cd signalshow-backend
julia setup.jl
```

This will automatically install all required packages.

### Method 2: Manual Installation

If you need to install packages manually:

1. **Start Julia**:

   ```bash
   julia
   ```

2. **Enter Package Mode** (press `]`):

   ```julia
   julia> ]

   (@v1.10) pkg>
   ```

3. **Install required packages**:

   ```julia
   pkg> add HTTP WebSockets JSON3 DSP FFTW SpecialFunctions Distributions Images StatsBase Interpolations Wavelets
   ```

4. **Wait for installation** (first time may take 5-10 minutes):

   ```
   Installing packages...
   Precompiling packages...
   ```

5. **Exit Package Mode** (press Backspace):

   ```julia
   pkg> [Backspace]
   julia>
   ```

6. **Test package loading**:

   ```julia
   julia> using DSP
   julia> using FFTW
   julia> println("Packages loaded successfully!")
   Packages loaded successfully!
   ```

7. **Exit Julia**:
   ```julia
   julia> exit()
   ```

### Core Packages Explained

| Package                 | Purpose                                    | Version |
| ----------------------- | ------------------------------------------ | ------- |
| **HTTP.jl**             | Web server for REST API                    | Latest  |
| **WebSockets.jl**       | Real-time streaming                        | Latest  |
| **JSON3.jl**            | Fast JSON serialization                    | Latest  |
| **DSP.jl**              | Signal processing (FFT, filters, windows)  | Latest  |
| **FFTW.jl**             | Optimized FFT operations                   | Latest  |
| **SpecialFunctions.jl** | Bessel, erf, and other special functions   | Latest  |
| **Distributions.jl**    | Noise generators (Gaussian, Poisson, etc.) | Latest  |
| **Images.jl**           | 2D image processing                        | Latest  |
| **StatsBase.jl**        | Statistical operations                     | Latest  |
| **Interpolations.jl**   | Signal interpolation                       | Latest  |
| **Wavelets.jl**         | Wavelet transforms                         | Latest  |

---

## Running the SignalShow Backend

Once Julia and packages are installed:

### Quick Start

1. **Navigate to SignalShow backend directory**:

   ```bash
   cd path/to/signalshow-backend
   ```

2. **Start the server**:

   ```bash
   julia server.jl
   ```

3. **Wait for startup** (first run may take 10-15 seconds for compilation):

   ```
   [ Info: Precompiling packages...
   [ Info: SignalShow backend server starting...
   [ Info: Listening on http://localhost:8080
   [ Info: Ready to process signals!
   ```

4. **Verify server is running**:

   - Open browser to `http://localhost:8080/health`
   - Should see: `{"status": "ok", "version": "1.0.0"}`

5. **Launch SignalShow frontend**:
   - Open the SignalShow app (web or desktop)
   - It will automatically connect to `localhost:8080`

### Advanced: Custom Port

To run on a different port:

```bash
julia server.jl --port 9000
```

### Advanced: Production Mode

For production/teaching environments:

```bash
julia --optimize=3 --threads=auto server.jl
```

Options:

- `--optimize=3`: Maximum optimization
- `--threads=auto`: Use all available CPU cores

---

## Troubleshooting

### Issue: "julia: command not found"

**Cause**: Julia is not in your system PATH.

**Solution (macOS/Linux)**:

```bash
# Find Julia installation
which julia

# If not found, add to PATH in ~/.bashrc or ~/.zshrc:
export PATH="/path/to/julia/bin:$PATH"

# Then reload:
source ~/.bashrc  # or ~/.zshrc
```

**Solution (Windows)**:

1. Search for "Environment Variables" in Start Menu
2. Click "Edit the system environment variables"
3. Click "Environment Variables"
4. Under "User variables", find "Path"
5. Click "Edit" → "New"
6. Add: `C:\Users\YourName\AppData\Local\Programs\Julia-1.10.0\bin`
7. Click "OK" on all dialogs
8. Restart Command Prompt

---

### Issue: Package installation fails

**Symptoms**:

```
ERROR: Cannot download package
```

**Solutions**:

1. **Check internet connection**

2. **Update package registry**:

   ```julia
   julia> ]
   pkg> registry update
   pkg> add PackageName
   ```

3. **Clear package cache** (if corrupted):

   ```bash
   # macOS/Linux
   rm -rf ~/.julia/packages
   rm -rf ~/.julia/compiled

   # Windows
   rmdir /s %USERPROFILE%\.julia\packages
   rmdir /s %USERPROFILE%\.julia\compiled
   ```

   Then reinstall packages.

4. **Check firewall/proxy**:
   - Corporate networks may block package downloads
   - Configure proxy in Julia if needed

---

### Issue: Server won't start

**Symptoms**:

```
ERROR: Port 8080 already in use
```

**Solutions**:

1. **Check if another process is using port 8080**:

   ```bash
   # macOS/Linux
   lsof -i :8080

   # Windows
   netstat -ano | findstr :8080
   ```

2. **Kill the conflicting process** or **use a different port**:
   ```bash
   julia server.jl --port 8081
   ```

---

### Issue: Slow first startup

**Symptoms**: Julia takes 30+ seconds to start

**Explanation**: This is normal! Julia uses Just-In-Time (JIT) compilation:

- **First run**: Compiles packages (10-30 seconds)
- **Subsequent runs**: Uses cached compilation (2-5 seconds)

**Solutions**:

1. **Use PackageCompiler.jl** to create a system image (advanced):

   ```julia
   pkg> add PackageCompiler
   ```

   This can reduce startup to <1 second but requires ~30 minutes to build.

2. **Keep Julia server running**: Don't restart it between uses

3. **Accept the warm-up time**: 10-15 seconds is acceptable for scientific software

---

### Issue: Out of memory errors

**Symptoms**:

```
ERROR: OutOfMemoryError
```

**Solutions**:

1. **Reduce signal/image sizes** (start with smaller datasets)

2. **Increase system RAM** or close other applications

3. **Use Julia's garbage collector**:

   ```julia
   julia> GC.gc()  # Force garbage collection
   ```

4. **Process data in chunks** rather than all at once

---

## Updating Julia

### With juliaup (Recommended)

```bash
# Update to latest stable version
juliaup update

# Or install specific version
juliaup add 1.10.0
juliaup default 1.10.0
```

### With Official Installer

1. Download new version from [julialang.org](https://julialang.org/downloads/)
2. Install (will replace old version)
3. Reinstall packages (they're version-specific)

### Checking Current Version

```bash
julia --version
```

---

## Additional Resources

### Official Documentation

- **Julia Homepage**: [https://julialang.org](https://julialang.org)
- **Julia Documentation**: [https://docs.julialang.org](https://docs.julialang.org)
- **Package Documentation**: [https://juliapackages.com](https://juliapackages.com)

### Learning Julia

- **Julia Academy**: [https://juliaacademy.com](https://juliaacademy.com) (Free courses)
- **Think Julia**: Free online book for beginners
- **Julia Discourse**: [https://discourse.julialang.org](https://discourse.julialang.org) (Community forum)

### SignalShow-Specific

- **DSP.jl Documentation**: [https://docs.juliadsp.org](https://docs.juliadsp.org)
- **FFTW.jl Guide**: Included in Julia docs
- **SignalShow GitHub**: [Link to repository when available]

---

## Quick Reference Commands

```bash
# Check Julia version
julia --version

# Start Julia REPL
julia

# Install a package (in Julia REPL)
julia> ]
pkg> add PackageName

# Update all packages
pkg> update

# Start SignalShow backend
julia server.jl

# Start with optimization
julia --optimize=3 --threads=auto server.jl
```

---

## Support

If you encounter issues not covered in this guide:

1. **Check SignalShow documentation**: [Link to docs]
2. **Search Julia Discourse**: [https://discourse.julialang.org](https://discourse.julialang.org)
3. **File an issue**: [Link to GitHub issues]
4. **Contact maintainers**: [Contact information]

---

**Last Updated**: October 25, 2025  
**Julia Version Covered**: 1.10.x  
**Status**: ✅ Ready for SignalShow v1.0
