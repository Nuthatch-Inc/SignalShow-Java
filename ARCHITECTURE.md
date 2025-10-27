# System Architecture

**Purpose**: Technical design for SignalShow modernization

---

## Design Principles

Based on analysis of Desmos, Observable, and GeoGebra:

1. **Shareable URLs** - Encode state in URL for instant sharing
2. **Real-time interaction** - Zero-latency slider feedback  
3. **Touch support** - Works on tablets/iPads (44px minimum targets)
4. **Keyboard shortcuts** - Power user efficiency
5. **Clean UI** - Minimal, professional aesthetic
6. **Universal export** - PNG/SVG/JSON/MP4 one-click
7. **Community library** - User-submitted demos

---

## System Overview

**Layers**:

1. **Presentation** - React + TypeScript UI
2. **State** - Zustand store with file-based persistence
3. **Computation** - Hybrid backend (Julia/JavaScript/WASM)
4. **Visualization** - Plotly.js + D3.js + Three.js

**File Formats**:
- `.sig1d` - 1D signals (JSON)
- `.sig2d` - 2D images (JSON) 
- `.sigOp` - Operation configs
- `.sigWorkspace` - Complete sessions
- `.sigDemo` - Pre-built examples

---

## Backend Abstraction

**Strategy**: Automatic environment detection

```javascript
const backend = window.__TAURI__
  ? new JuliaServerBackend()  // Desktop: optimal
  : new WebBackend();          // Browser: zero install
```

**Desktop Mode (Tauri)**:
- Julia server (localhost:8080)
- 100% performance, complete features
- Graceful fallback to JavaScript

**Web Mode (Browser)**:
- Pure JavaScript (Math.sin, fft.js)
- 5-10% native performance
- ~60% feature coverage
- Zero installation

**Future**: Rust + WASM (60-95% performance)

---

## Component Architecture

**Core React Components**:

1. **FunctionGenerator**
   - Signal type selector
   - Parameter sliders
   - Real-time preview
   - Preset library

2. **OperationsPanel**
   - Operation selector (FFT, filters, etc.)
   - Operation chain builder
   - Parameter configuration

3. **SignalDisplay**
   - Plotly.js waveform plots
   - Time/frequency domain
   - Zoom, pan, annotations
   - Export controls

4. **DemoGallery**
   - Pre-built examples
   - Topic browsing
   - One-click load

5. **WorkspaceManager**
   - File I/O (save/load)
   - Import (WAV/CSV)
   - Export (PNG/SVG/JSON)
   - Undo/redo history

---

## Data Flow

**User Interaction**:
```
User adjusts slider
  → Zustand store update
  → Backend computation (debounced)
  → Plot update (optimistic UI)
```

**Operation Chain**:
```
Signal → Operation 1 → Operation 2 → Result
                ↓
         Intermediate cached
```

**File Operations**:
```
Load → Parse JSON → Restore state → Render
Save → Serialize state → Write JSON
```

---

## API Design

### Julia Backend (HTTP + WebSocket)

**REST Endpoints**:
- `POST /api/generate/sine` - Generate signal
- `POST /api/operations/fft` - Compute FFT
- `POST /api/operations/filter` - Apply filter
- `GET /health` - Server status

**WebSocket**:
- Real-time streaming for heavy operations
- Progress updates for long computations
- Binary transfer for large arrays

**Data Format**:
```json
{
  "type": "sine",
  "params": {"frequency": 440, "duration": 1.0},
  "samples": [0.0, 0.1, ...],
  "sampleRate": 8000
}
```

### JavaScript Backend

**Internal API** (same interface):
```javascript
await backend.generateSine(440, 1.0, 8000);
await backend.fft(signal);
await backend.filter(signal, 'lowpass', {cutoff: 1000});
```

---

## State Management

**Zustand Store**:

```javascript
{
  signals: {
    'sig1': {data: [...], sampleRate: 8000},
    'sig2': {data: [...], sampleRate: 8000}
  },
  operations: [
    {type: 'fft', params: {...}, result: 'sig2'}
  ],
  ui: {
    selectedSignal: 'sig1',
    plotMode: 'time-frequency'
  }
}
```

**Persistence**:
- Auto-save to localStorage (web)
- Native file system (desktop)
- URL encoding for sharing

---

## Performance

**Targets**:
- Slider interaction: <16ms (60fps)
- FFT (4096 samples): <100ms
- Plot render: <50ms
- Startup: <2 seconds

**Optimization**:
- Debounce heavy computations
- Web Workers for background processing
- Signal decimation for large datasets
- Plotly.js streaming mode
- WebGL rendering for 3D

**Budgets**:
| Operation | Target | JavaScript | Julia | WASM (planned) |
|-----------|--------|------------|-------|----------------|
| FFT 4096 | <100ms | ~100ms | ~1ms | ~5ms |
| Filter | <50ms | ~50ms | ~1ms | ~3ms |
| Render | <50ms | ~30ms | N/A | N/A |

---

## Deployment

### Web (Static Hosting)

- React build → Vercel/Netlify
- PWA with service worker (offline UI)
- No backend server needed
- CDN for assets

### Desktop (Tauri)

- Single installer: DMG/MSI/AppImage
- Optional Julia runtime (on-demand download)
- Auto-updates
- Native file I/O

### Classroom (Docker)

- Pre-configured environment
- Julia server + web UI
- LMS integration ready
- Multi-user support

---

## Security

**Web Mode**:
- Client-side only (no server)
- CSP headers
- HTTPS required

**Desktop Mode**:
- Julia server localhost only
- No external network access
- Sandboxed file operations

**Future Considerations**:
- Plugin sandboxing (WASM)
- Cloud save encryption
- SSO integration

---

## Extensibility

**Plugin System** (v2.0):

```javascript
// Custom operation plugin
export default {
  name: 'MyFilter',
  operation: async (signal, params) => {
    // Custom DSP logic
    return processedSignal;
  }
}
```

**UI Extensions**:
- Custom visualizations
- New signal generators
- Export formats

---

## Testing Strategy

**Unit Tests**:
- Backend abstraction layer
- Signal generators
- Operations (compare Julia vs JS)

**Integration Tests**:
- End-to-end workflows
- File I/O
- API contracts

**Performance Tests**:
- Benchmark suite
- Regression tracking
- Browser compatibility

**Visual Regression**:
- Plot rendering consistency
- UI component screenshots

---

## Migration from Java

**Approach**: Incremental migration

**Phase 1** - Core functions (v1.0):
- 6-8 essential generators
- 8-10 basic operations
- Simple 2D plots

**Phase 2** - Complete library (v1.5):
- All 80+ functions
- All 40+ operations
- 3D visualization

**Phase 3** - Advanced features (v2.0):
- Desktop app
- Julia backend
- Plugin system

**Code Reuse**:
- Reference Java for algorithms
- Port formulas to Julia/JavaScript
- Maintain test compatibility

---

## Technical Debt Management

**Avoid**:
- Monolithic components
- Hard-coded configurations
- Tight coupling to backends
- Browser-specific code

**Best Practices**:
- TypeScript for type safety
- ESLint + Prettier
- Component testing
- Documentation as code
- Semantic versioning

---

**Key Architecture Decisions**:
1. Backend abstraction enables web/desktop from single codebase
2. File-based data model ensures reproducibility
3. Hybrid computation supports both power users (Julia) and accessibility (JavaScript)
4. Progressive enhancement from JavaScript → WASM → Julia
