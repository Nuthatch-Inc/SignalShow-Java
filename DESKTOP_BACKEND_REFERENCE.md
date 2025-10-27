# Desktop Backend & Implementation Reference

**Status**: Early-stage proposals and historical reference documentation

---

## Part V: Desktop Version Backend Proposal (Optional)

**Note**: Early-stage concept for Nuthatch Desktop/Tauri deployment. May be deferred or abandoned in favor of web-only deployment.

### Julia Server Lifecycle Management

**Proposed Behavior**: Julia backend server persists across app sessions with automatic shutdown after 15 minutes of inactivity.

**Startup Process**:

1. Check Julia installation in PATH and common locations (`~/.juliaup/bin/julia`, `/usr/local/bin/julia`, `/opt/homebrew/bin/julia`, `C:\Users\%USERNAME%\.juliaup\bin\julia.exe`)
2. Ping `http://localhost:8080/health` to detect running server
3. If not running, spawn `julia server.jl --port 8080`
4. Verify startup with health check after 3 seconds

**Runtime**: Health check pings every 60 seconds reset inactivity timer. Server tracks `last_activity` timestamp on all HTTP requests.

**Shutdown**: Automatic after 15 minutes of inactivity (configurable via `INACTIVITY_TIMEOUT` constant). Server persists when app closes but stops after timeout expires. Manual shutdown via Ctrl+C.

### Auto-Start Implementation

**Proposed Architecture**: Tauri backend (`src-tauri/src/julia_server.rs`) provides commands for Julia process management. React frontend (`SignalShow.app/app.jsx`) implements auto-start logic on component mount.

**Tauri Commands**:

- `check_julia_server`: HTTP health check to `http://localhost:8080/health`
- `start_julia_server`: Spawn Julia process with platform-independent handling
- `stop_julia_server`: Terminate server process
- `get_julia_path`: Locate Julia executable in PATH

**Dependencies**: `reqwest` (HTTP client), `tokio` (async runtime)

**User Experience**: App displays server status (checking, running, Julia not installed, failed to start). Automatic startup on launch, cleanup on unmount.

---

## Part VI: Implementation Reference

### Julia Installation

**Recommended Method**: juliaup (official version manager)

**macOS/Linux**:

```bash
curl -fsSL https://install.julialang.org | sh
source ~/.bashrc  # or ~/.zshrc
julia --version
```

**Windows**:

```powershell
winget install julia -s msstore
# or download from julialang.org
```

**System Requirements**: 64-bit OS (macOS 10.9+, Windows 7+, Linux), 2GB RAM minimum (8GB recommended), 500MB disk space for Julia + 1-2GB for packages.

**Package Installation** (for backend):

```julia
using Pkg
Pkg.add(["HTTP", "JSON", "DSP", "FFTW", "Images"])
```

**Verification**:

```bash
julia --version
julia -e 'using HTTP, DSP, FFTW; println("Packages loaded")'
```

### Quick Start

**Manual Server Testing**:

```bash
# Start server
julia signalshow-backend/server.jl --port 8080

# Test health endpoint
curl http://127.0.0.1:8080/health

# Stop server
pkill -f "julia.*server.jl"
```

**Development Mode** (Tauri):

```bash
cd nuthatch-desktop/src-tauri
cargo build

cd ..
npm run tauri dev
```

**Expected Behavior**: SignalShow app automatically checks Julia installation, detects/starts server, displays "Server running" status.
