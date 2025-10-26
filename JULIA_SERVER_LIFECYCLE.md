# Julia Server Lifecycle Management

## Overview

The SignalShow Julia backend server is designed to persist across app sessions for optimal performance while automatically shutting down after periods of inactivity to conserve system resources.

## Server Behavior

### Startup

When SignalShow launches:

1. **Check for Julia installation** - Verifies Julia is installed by checking:

   - System PATH via `which julia` (macOS/Linux) or `where julia` (Windows)
   - Common installation locations:
     - `~/.juliaup/bin/julia` (juliaup default)
     - `/usr/local/bin/julia` (macOS Homebrew)
     - `/opt/homebrew/bin/julia` (macOS Apple Silicon Homebrew)
     - `/usr/bin/julia` (Linux system)
     - `C:\Users\%USERNAME%\.juliaup\bin\julia.exe` (Windows juliaup)
     - `C:\Julia\bin\julia.exe` (Windows manual install)

2. **Check if server is already running** - Pings `http://localhost:8080/health`

   - If running → Reuses existing server (instant launch!)
   - If not running → Starts new server process

3. **Start server if needed** - Spawns Julia process:

   ```bash
   julia /path/to/signalshow-backend/server.jl --port 8080
   ```

4. **Verify startup** - Waits 3 seconds, then confirms server responds to health checks

### While Running

**Active Session (SignalShow open):**

- Frontend sends health check ping every 60 seconds
- Each ping resets the server's inactivity timer
- Server remains active and responsive

**Server Activity Tracking:**

- Server tracks `last_activity` timestamp
- Updates on every:
  - Health check (`/health`)
  - API request (future endpoints)
  - Any HTTP request received

### Shutdown

**Graceful Shutdown Scenarios:**

1. **Inactivity Timeout (automatic)**

   - Triggers after **15 minutes** of no activity
   - Server prints: `⏰ Server inactive for 15 minutes, shutting down...`
   - Process exits cleanly with code 0
   - No orphaned processes or resources

2. **Manual Shutdown**

   - User presses `Ctrl+C` in server terminal
   - Server prints: `🛑 Server stopped gracefully`
   - Recommended for development/debugging

3. **App Close Behavior**
   - SignalShow window closes → Server keeps running
   - Heartbeat pings stop → 15-minute countdown begins
   - If app reopens within 15 minutes → Server reused instantly
   - If app stays closed > 15 minutes → Server auto-stops

**What does NOT stop the server:**

- Closing SignalShow window
- Closing other Nuthatch Desktop apps
- Minimizing the desktop environment

## Configuration

### Server Settings

Edit `signalshow-backend/server.jl`:

```julia
const DEFAULT_PORT = 8080
const INACTIVITY_TIMEOUT = 15 * 60  # 15 minutes in seconds
```

**Adjusting timeout:**

- 5 minutes: `5 * 60`
- 30 minutes: `30 * 60`
- 1 hour: `60 * 60`
- Disable timeout: Comment out `check_inactivity()` in background task

### Client Settings

Edit `system-rom/SignalShow.app/useJuliaServer.js`:

```javascript
const heartbeat = setInterval(async () => {
  // Ping server
}, 60000); // Ping interval in milliseconds (60000 = 1 minute)
```

**Adjusting heartbeat:**

- 30 seconds: `30000`
- 2 minutes: `120000`
- 5 minutes: `300000`

## Architecture

### Components

```
┌─────────────────────────────────────────────────────────────┐
│                    SignalShow React App                      │
│  (system-rom/SignalShow.app/)                               │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ useJuliaServer.js (Custom Hook)                      │  │
│  │ - Detects Julia installation                         │  │
│  │ - Checks for running server                          │  │
│  │ - Starts server if needed                            │  │
│  │ - Sends heartbeat pings (every 60s)                  │  │
│  └──────────────────────────────────────────────────────┘  │
│                          ↓ Tauri commands                   │
└──────────────────────────┼──────────────────────────────────┘
                           ↓
┌──────────────────────────┼──────────────────────────────────┐
│                    Tauri Rust Backend                        │
│  (src-tauri/src/julia_server.rs)                            │
│                                                              │
│  - get_julia_path() → Finds Julia executable                │
│  - check_julia_server(port) → HTTP GET /health              │
│  - start_julia_server(path, port) → Spawns process          │
│  - stop_julia_server() → Terminates process                 │
└──────────────────────────┼──────────────────────────────────┘
                           ↓ Process spawn
┌──────────────────────────┼──────────────────────────────────┐
│                  Julia HTTP Server Process                   │
│  (signalshow-backend/server.jl)                             │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │ Main Thread: HTTP.serve()                          │    │
│  │ - Listens on localhost:8080                        │    │
│  │ - Routes: /health, /api/version                    │    │
│  │ - Updates last_activity on each request            │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │ Background Task: Inactivity Monitor                │    │
│  │ - Checks every 60 seconds                          │    │
│  │ - Calculates: time() - last_activity               │    │
│  │ - If > 15 minutes → exit(0)                        │    │
│  └────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### State Flow

```
App Launch
    ↓
┌───────────────────────┐
│ Check Julia installed │
└───────┬───────────────┘
        │
    ┌───┴────┐
    │ Found? │
    └───┬────┘
        │
    ┌───┴──────────────────────────────┐
    │                                   │
   Yes                                 No
    │                                   │
    ↓                                   ↓
┌────────────────────┐     ┌──────────────────────────┐
│ Check server:8080  │     │ Show installation guide  │
└────────┬───────────┘     └──────────────────────────┘
         │
    ┌────┴─────┐
    │ Running? │
    └────┬─────┘
         │
    ┌────┴────────────────────┐
    │                          │
   Yes                        No
    │                          │
    ↓                          ↓
┌─────────────────┐    ┌────────────────┐
│ Reuse server    │    │ Start server   │
│ (instant!)      │    │ (3s wait)      │
└────────┬────────┘    └────────┬───────┘
         │                      │
         └──────────┬───────────┘
                    ↓
         ┌─────────────────────┐
         │ Start heartbeat     │
         │ (ping every 60s)    │
         └─────────────────────┘
                    ↓
         ┌─────────────────────┐
         │ Server active       │
         │ last_activity fresh │
         └─────────────────────┘

App Close
    ↓
┌─────────────────────┐
│ Heartbeat stops     │
└─────────┬───────────┘
          ↓
┌─────────────────────────┐
│ 15-minute countdown     │
│ starts                  │
└─────────┬───────────────┘
          │
    ┌─────┴────────────────┐
    │                       │
Reopen within 15min    Wait > 15min
    │                       │
    ↓                       ↓
┌──────────────┐    ┌───────────────┐
│ Server alive │    │ Server exits  │
│ Instant use! │    │ Clean shutdown│
└──────────────┘    └───────────────┘
```

## Benefits

### Performance

- **Instant relaunch** - Reusing existing server eliminates 10-15 second Julia startup time
- **Package preloading** - HTTP.jl, DSP.jl, etc. already loaded in memory
- **Zero cold-start** - Server ready immediately for signal processing

### Resource Management

- **Automatic cleanup** - No manual shutdown needed
- **No orphaned processes** - Inactivity timeout ensures clean exit
- **Minimal overhead** - Idle server uses <50MB RAM
- **Smart persistence** - Runs only when potentially needed

### User Experience

- **Seamless workflow** - Close/reopen app without interruption
- **Battery friendly** - Auto-stops when truly inactive
- **Zero configuration** - Works out of the box
- **Development friendly** - Fast iteration during testing

## Monitoring

### Check Server Status

**From command line:**

```bash
# Check if server is running
curl http://localhost:8080/health

# Expected response:
# {"status":"ok","version":"1.0.0","timestamp":1730000000.0,"last_activity":1730000000.0}
```

**Check server process:**

```bash
# macOS/Linux
ps aux | grep "julia.*server.jl"

# Windows
tasklist | findstr julia
```

### Server Logs

Server prints to stdout when started:

```
╔════════════════════════════════════════╗
║     SignalShow Backend Server         ║
║     Signal Processing with Julia      ║
╚════════════════════════════════════════╝

🚀 Starting SignalShow backend server...
📡 Port: 8080
🔗 Health check: http://localhost:8080/health
⏱️  Started at: 2025-10-26T04:30:15.123
⏰ Auto-shutdown after 15 minutes of inactivity

Press Ctrl+C to stop the server
==================================================
[ Info: Listening on: 127.0.0.1:8080, thread id: 1
```

On inactivity shutdown:

```
⏰ Server inactive for 15 minutes, shutting down...
```

## Troubleshooting

### Server Won't Start

**Symptom:** SignalShow shows "Julia server could not be started"

**Solutions:**

1. Check Julia installation:

   ```bash
   julia --version
   # Should show: julia version 1.12.1 (or higher)
   ```

2. Verify packages installed:

   ```bash
   julia -e 'using HTTP, JSON3, DSP, FFTW'
   # Should complete without errors
   ```

3. Check port availability:

   ```bash
   lsof -i :8080  # macOS/Linux
   netstat -an | findstr 8080  # Windows
   ```

4. Check server file exists:
   ```bash
   ls -la /Users/julietfiss/src/SignalShow-Java/signalshow-backend/server.jl
   ```

### Server Keeps Stopping

**Symptom:** Server exits unexpectedly

**Possible Causes:**

1. **Inactivity timeout too short** - Increase `INACTIVITY_TIMEOUT` in server.jl
2. **Heartbeat not working** - Check browser console for errors
3. **Port conflict** - Another process using port 8080
4. **Julia crash** - Check for error messages in terminal

### Server Won't Reuse

**Symptom:** New server starts every time despite old one running

**Solutions:**

1. Check health endpoint manually:

   ```bash
   curl http://localhost:8080/health
   ```

2. Verify CORS headers allow requests:

   ```bash
   curl -v http://localhost:8080/health
   # Should show: Access-Control-Allow-Origin: *
   ```

3. Check Rust backend logs for HTTP errors

### Multiple Servers Running

**Symptom:** Multiple Julia server processes

**Solution:**

```bash
# Kill all Julia servers
pkill -f "julia.*server.jl"

# Restart SignalShow - will start fresh server
```

## Future Enhancements

### Planned Features

- [ ] **Configurable timeout** - UI setting for inactivity duration
- [ ] **Server status indicator** - Visual badge showing server state
- [ ] **Manual start/stop controls** - Settings panel buttons
- [ ] **Activity log** - Track server uptime and request count
- [ ] **Multi-instance support** - Share server across multiple SignalShow windows
- [ ] **Server metrics** - Memory usage, request latency, uptime stats
- [ ] **Wake-on-demand** - Start server automatically when needed, not on app launch
- [ ] **Custom port selection** - Allow user to change default port
- [ ] **Server health notifications** - Alert when server stops unexpectedly

### Potential Optimizations

- **Faster startup** - Use Julia system image for instant server launch
- **Persistent cache** - Store compiled functions across sessions
- **WebSocket support** - Real-time updates for long-running computations
- **Process pooling** - Maintain multiple Julia workers for parallel processing

## Related Documentation

- [Julia Installation Guide](./JULIA_INSTALLATION_GUIDE.md)
- [Julia Auto-Start Implementation](./JULIA_AUTOSTART_IMPLEMENTATION.md)
- [SignalShow Julia Server Auto-Start](../nuthatch-desktop/docs/SIGNALSHOW_JULIA_SERVER_AUTOSTART.md)
- [Quick Start Guide](./QUICK_START.md)
