# SignalShow Web - System Architecture

**Last Updated**: October 25, 2025  
**Status**: Initial Design Phase  
**Related**: [TECH_STACK.md](./TECH_STACK.md), [RESEARCH_OVERVIEW.md](./RESEARCH_OVERVIEW.md), [SIMILAR_PROJECTS_COMPARISON.md](./SIMILAR_PROJECTS_COMPARISON.md)

---

## UI/UX Principles (Learned from Similar Projects)

### Core Design Philosophy

**"SignalShow is to signal processing what Desmos is to algebra"**

Based on analysis of Desmos, Observable, GeoGebra, and Mathigon, we adopt these proven UX patterns:

#### 1. **Shareable URLs** (from ALL competitors) ✓ CRITICAL

- Encode entire app state in URL hash
- Enable instant sharing: "Check out this chirp demo: signalshow.org/#demo=chirp-fft"
- Support shortened URLs for classroom use
- **Technical**: Use `LZString.compressToEncodedURIComponent()` for state compression

#### 2. **Real-Time Sliders** (from Desmos) ✓ CRITICAL

- Zero latency feedback - update plot as slider moves
- Debounce heavy computations (FFT) but show immediate visual response
- **Technical**: Optimistic UI updates + background computation + Julia caching

#### 3. **Touch Support** (from GeoGebra, Mathigon, Desmos) ✓ REQUIRED

- All interactions work with mouse AND touch
- Pinch-to-zoom on plots
- Drag gestures for panning
- Large touch targets (minimum 44×44px)
- **Why**: Students use tablets for presentations, teachers use iPads

#### 4. **Keyboard Shortcuts** (from Observable, Desmos) ✓ POWER USERS

- `Ctrl+Enter`: Compute/apply operation
- `Ctrl+E`: Export current view
- `Ctrl+/`: Toggle function panel
- `Shift+?`: Show keyboard shortcuts
- Arrow keys for fine-tuning slider values

#### 5. **Beautiful, Minimal UI** (from Desmos aesthetic)

- Clean white/light gray background
- Ample whitespace
- Subtle shadows and borders
- Professional color palette (blues, grays)
- **Avoid**: Bright colors, unnecessary chrome, cluttered toolbars

#### 6. **Export Everything** (from all competitors)

- PNG/SVG for static plots (publication quality)
- JSON for configurations (reproducibility)
- MP4 for animations (unique to SignalShow via Manim)
- CSV for raw data (interoperability)
- **One-click export** - no complicated dialogs

#### 7. **Community Demo Library** (from GeoGebra's 100k materials)

- Browse demos by topic (Fourier Transform, Filters, Sampling, etc.)
- One-click "Open in SignalShow"
- User-submitted demos (moderated)
- Featured "Demo of the Month"
- **Technical**: Store demos as compressed JSON, host on GitHub/CDN

---

## Table of Contents

1. [High-Level Architecture](#high-level-architecture)
2. [Component Breakdown](#component-breakdown)
3. [Data Flow](#data-flow)
4. [API Design](#api-design)
5. [State Management](#state-management)
6. [Performance Considerations](#performance-considerations)
7. [Deployment Architectures](#deployment-architectures)

---

## High-Level Architecture

### System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                   NUTHATCH DESKTOP PLATFORM                      │
│         (Window Management, File System Integration)             │
└──────────────────────────────┬──────────────────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                        │
│                     (React + TypeScript UI)                      │
├──────────────────┬──────────────────┬──────────────────────────┤
│  Function Editor │  Operation Panel │  Visualization Display   │
│  - Parameter UI  │  - Op Selection  │  - Plotly.js Charts     │
│  - Presets      │  - Op Chain      │  - D3.js Animations     │
│  - Preview      │  - Parameters    │  - Export Controls      │
└──────────────────┴──────────────────┴──────────────────────────┘
                            ↕ File-Based Data Layer
┌─────────────────────────────────────────────────────────────────┐
│                      DATA PERSISTENCE LAYER                      │
│        .sig1d | .sig2d | .sigOp | .sigWorkspace | .sigDemo     │
│               (JSON format, Git-compatible)                      │
└──────────────────────────────┬──────────────────────────────────┘
                               ↕ WebSocket/HTTP API
┌─────────────────────────────────────────────────────────────────┐
│                        APPLICATION LAYER                         │
│                      (Julia Backend Server)                      │
├──────────────────┬──────────────────┬──────────────────────────┤
│  API Handlers    │  Signal Engine   │  Operation Pipeline     │
│  - REST          │  - Functions     │  - FFT/IFFT            │
│  - WebSocket     │  - Generators    │  - Filtering           │
│  - Serialization │  - 1D/2D Data    │  - Convolution         │
└──────────────────┴──────────────────┴──────────────────────────┘
                            ↕ Julia Package Ecosystem
┌─────────────────────────────────────────────────────────────────┐
│                      COMPUTATION LAYER                           │
│              (Julia Numerical Libraries)                         │
├──────────────────┬──────────────────┬──────────────────────────┤
│  FFTW.jl        │  DSP.jl          │  Images.jl              │
│  - FFT/IFFT     │  - Filters       │  - 2D Operations        │
│  - Optimized    │  - Windows       │  - Image I/O            │
└──────────────────┴──────────────────┴──────────────────────────┘
```

---

### Backend Abstraction Layer 🔄 HYBRID ARCHITECTURE

**Status**: ✅ IMPLEMENTED (October 25, 2025)  
**Purpose**: Enable SignalShow to run with different computation backends based on deployment environment

SignalShow uses a **unified backend abstraction layer** that automatically selects the appropriate computation backend:

- **Desktop (Tauri)**: Julia server backend (optimal) with JavaScript fallback
- **Web (Browser)**: Pure JavaScript implementation (no server required)
- **Future**: Rust + WebAssembly for improved web performance

**Implementation Location**: `system-rom/SignalShow.app/backend/`  
**Documentation**: See `BACKEND_ABSTRACTION.md`, `IMPLEMENTATION_SUMMARY.md`, `DEVELOPMENT_STATUS.md`

#### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     REACT FRONTEND                               │
│              (Single Codebase, Works Everywhere)                 │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │ BACKEND FACTORY     │
                    │ (createBackend())   │
                    │ Environment Detection│
                    └──────────┬──────────┘
                               │
                ┌──────────────┴─────────────────┐
                │                                │
         window.__TAURI__?              NOT window.__TAURI__?
                │                                │
                ▼                                ▼
    ┌─────────────────────┐        ┌─────────────────────────┐
    │ DESKTOP MODE        │        │   WEB MODE              │
    ├─────────────────────┤        ├─────────────────────────┤
    │ Try Julia Server:   │        │ JavaScript Backend      │
    │                     │        │ (Always Available)      │
    │ isAvailable()?      │        │                         │
    │   ├─ YES ──────┐    │        │ • Math.sin/cos          │
    │   │            ▼    │        │ • fft.js library        │
    │   │ ┌──────────────┐│        │ • Window functions      │
    │   │ │Julia Backend ││        │ • Direct convolution    │
    │   │ │              ││        │                         │
    │   │ │• HTTP client ││        │ Bundle: ~23KB           │
    │   │ │• localhost:  ││        │ Performance: 5-10%      │
    │   │ │  8080        ││        │ Features: ~60%          │
    │   │ │• Binary data ││        │                         │
    │   │ │• 100% perf   ││        │ ✅ No server needed     │
    │   │ └──────────────┘│        │ ✅ Works in browser     │
    │   │                 │        │ ✅ Zero install         │
    │   └─ NO ───────┐    │        └─────────────────────────┘
    │                ▼    │
    │    ┌──────────────┐ │
    │    │JS Fallback   │ │
    │    │              │ │
    │    │Same as web   │ │
    │    │mode          │ │
    │    └──────────────┘ │
    │                     │
    │ ⚠️ Julia optional   │
    │ ✅ Graceful fallback│
    └─────────────────────┘

    Future: WASM Backend
    ┌─────────────────────┐
    │ Rust + WebAssembly  │
    │ • rustfft           │
    │ • dasp crates       │
    │ • 60-95% perf       │
    │ • ~150KB bundle     │
    └─────────────────────┘
```

#### Implementation Status ✅ COMPLETE (October 25, 2025)

**Completed Backend Infrastructure**:

✅ **Core Backend Files** (7 files, ~950 lines):

- `backend/types.js` - DSPBackend interface and type definitions
- `backend/julia-server.js` - Julia server HTTP client (desktop)
- `backend/javascript.js` - Pure browser implementation (web)
- `backend/factory.js` - Automatic environment detection
- `backend/test.js` - Comprehensive test suite
- `backend/README.md` - API documentation
- `test-web.html` - Standalone web test page

✅ **UI Integration** (2 files modified):

- `app.jsx` - Backend initialization and state management
- `ServerStatus.jsx` - Web/desktop mode UI adaptation

✅ **Documentation** (4 comprehensive documents):

- `IMPLEMENTATION_SUMMARY.md` - Implementation details and migration path
- `DEVELOPMENT_STATUS.md` - Current status and next steps
- `ARCHITECTURE_DIAGRAM.md` - Visual architecture diagrams
- `../docs/BACKEND_ABSTRACTION.md` - Full architecture documentation

**Current Capabilities**:

- ✅ Automatic environment detection (window.**TAURI** check)
- ✅ Backend factory with graceful fallback
- ✅ Julia server backend (HTTP client ready for API endpoints)
- ✅ JavaScript backend (production-ready, no dependencies)
- ✅ Signal generation: sine, cosine, chirp, gaussian
- ✅ FFT operations (via fft.js or naive DFT fallback)
- ✅ Window functions: Hanning, Hamming, Blackman, Rectangular
- ✅ Convolution and correlation (direct algorithms)
- ✅ Performance monitoring and backend info display

**Usage Example** (Production-Ready Code):

```javascript
import { createBackend } from "./backend/factory.js";

// Automatically selects appropriate backend
const { backend, info } = await createBackend();
console.log("Backend:", info.name); // "Julia Server" or "JavaScript (Pure)"
console.log("Environment:", info.environment); // "desktop", "web", etc.

// Same API works in both environments
const signal = await backend.generateSine(440, 1.0, 8000);
const spectrum = await backend.fft(signal);
const windowed = await backend.applyWindow(signal, "hanning");
```

**Performance Characteristics**:
| Backend | Environment | Performance | Bundle Size | Status |
|---------|-------------|-------------|-------------|--------|
| Julia Server | Desktop | 100% (native) | N/A (server) | ✅ Client ready |
| JavaScript | Web/Fallback | 5-10% | ~23KB | ✅ Complete |
| Rust WASM | Web (future) | 60-95% | ~150KB | 📋 Planned |

**Next Implementation Priorities** (Updated October 25, 2025):

**🎯 PRIORITY 1: Web Version with JavaScript (CURRENT FOCUS)**

1. **Signal Display Component** - Canvas-based waveform visualization
2. **Parameter Controls** - Frequency, duration, amplitude sliders
3. **Function Generator UI** - Dropdown + parameter forms for signal generation
4. **Basic Operations Panel** - FFT, window functions, simple filters
5. **Export Functionality** - PNG images, JSON configurations
6. **Real-time Preview** - Live updates as parameters change
7. **Demo Gallery** - Pre-built examples users can load

**Goal**: Fully functional web app in browser, zero installation required

**🔧 PRIORITY 2: Performance Optimization (AFTER WEB MVP)**

1. **WASM Backend** - Rust DSP crates for 10-20x speed improvement
2. **Web Workers** - Background processing for heavy operations
3. **Result Caching** - Avoid recomputing identical operations
4. **Lazy Loading** - Split bundle, load features on demand

**Goal**: Near-native performance for web users

**🏢 PRIORITY 3: Desktop/Tauri Version (LOWEST PRIORITY)**

1. **Julia Server API Endpoints** - Implement backend matching client
2. **Tauri Packaging** - Desktop app distribution
3. **Advanced Features** - Desktop-only capabilities (if any)
4. **Native File I/O** - Local file system integration

**Goal**: Optional native app for researchers/power users

**Rationale**:

- JavaScript backend already complete and production-ready
- Web-first enables instant access, faster iteration
- Backend abstraction supports adding Julia/Tauri later without code changes

#### Unified DSP Backend Interface

All backends implement this interface (see `backend/types.js`):

```typescript
// backend/interface.ts
interface DSPBackend {
  // Environment info
  name: string;
  version: string;
  capabilities: BackendCapabilities;

  // Signal generation
  generateSine(
    frequency: number,
    duration: number,
    sampleRate: number
  ): Promise<Signal1D>;
  generateChirp(
    f0: number,
    f1: number,
    duration: number,
    sampleRate: number
  ): Promise<Signal1D>;
  generateGaussian(
    mean: number,
    sigma: number,
    length: number
  ): Promise<Signal1D>;
  generateNoise(type: NoiseType, length: number): Promise<Signal1D>;

  // FFT operations
  fft(signal: Signal1D): Promise<FrequencyDomain>;
  ifft(spectrum: FrequencyDomain): Promise<Signal1D>;
  realFFT(signal: Signal1D): Promise<FrequencyDomain>; // Optimized for real signals

  // Filtering
  lowpassFilter(
    signal: Signal1D,
    cutoff: number,
    order?: number
  ): Promise<Signal1D>;
  highpassFilter(
    signal: Signal1D,
    cutoff: number,
    order?: number
  ): Promise<Signal1D>;
  bandpassFilter(
    signal: Signal1D,
    low: number,
    high: number,
    order?: number
  ): Promise<Signal1D>;

  // Convolution & correlation
  convolve(signal1: Signal1D, signal2: Signal1D): Promise<Signal1D>;
  correlate(signal1: Signal1D, signal2: Signal1D): Promise<Signal1D>;

  // Time-frequency analysis
  stft(
    signal: Signal1D,
    windowSize: number,
    hopSize: number
  ): Promise<Spectrogram>;
  spectrogram(
    signal: Signal1D,
    params: SpectrogramParams
  ): Promise<Spectrogram>;

  // 2D operations
  fft2D(image: Signal2D): Promise<FrequencyDomain2D>;
  convolve2D(image: Signal2D, kernel: Signal2D): Promise<Signal2D>;

  // Utilities
  resample(signal: Signal1D, newRate: number): Promise<Signal1D>;
  window(signal: Signal1D, windowType: WindowType): Promise<Signal1D>;
}

interface BackendCapabilities {
  maxSignalLength: number; // Desktop: 100M, Web: 1M
  specialFunctions: boolean; // Desktop: yes, Web: limited
  wavelets: boolean; // Desktop: yes, Web: basic
  largeDatasets: boolean; // Desktop: yes, Web: no
  precisionLevel: "research" | "educational";
}
```

#### Backend Selection Logic

```typescript
// backend/factory.ts
export function createBackend(): DSPBackend {
  // Environment detection
  const isDesktop = typeof window !== "undefined" && window.__TAURI__;

  if (isDesktop) {
    console.log("[Backend] Using Julia server (desktop mode)");
    return new JuliaServerBackend();
  } else {
    console.log("[Backend] Using hybrid web backend (browser mode)");
    return new WebBackend();
  }
}

// Usage in React app
import { createBackend } from "./backend/factory";

function App() {
  const [backend] = useState(() => createBackend());

  // All DSP operations use the same API
  const generateSignal = async () => {
    const signal = await backend.generateSine(440, 1.0, 48000);
    // Works whether backend is Julia or WASM+JS!
  };

  return <SignalShowUI backend={backend} />;
}
```

#### Julia Server Backend Implementation

```typescript
// backend/julia-server.ts
export class JuliaServerBackend implements DSPBackend {
  private serverUrl: string = "http://localhost:8080";

  async generateSine(
    freq: number,
    duration: number,
    sampleRate: number
  ): Promise<Signal1D> {
    const response = await fetch(`${this.serverUrl}/api/generate/sine`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        frequency: freq,
        duration,
        sample_rate: sampleRate,
      }),
    });

    const arrayBuffer = await response.arrayBuffer();
    return new Float32Array(arrayBuffer);
  }

  async fft(signal: Signal1D): Promise<FrequencyDomain> {
    const response = await fetch(`${this.serverUrl}/api/operations/fft`, {
      method: "POST",
      body: signal.buffer,
    });

    const arrayBuffer = await response.arrayBuffer();
    return {
      real: new Float32Array(arrayBuffer, 0, signal.length * 4),
      imag: new Float32Array(arrayBuffer, signal.length * 4),
    };
  }

  // ... all other methods call Julia server endpoints
}
```

#### Web Backend Implementation (Rust+WASM + JavaScript)

```typescript
// backend/web-backend.ts
import * as wasmModule from "../wasm/signalshow_wasm";
import FFT from "fft.js";
import { DSP } from "dsp.js";

export class WebBackend implements DSPBackend {
  private wasm: typeof wasmModule | null = null;
  private jsFFT: FFT;

  async init() {
    // Load Rust WASM module
    this.wasm = await wasmModule.default();
    console.log("[WebBackend] WASM module loaded");
  }

  async generateSine(
    freq: number,
    duration: number,
    sampleRate: number
  ): Promise<Signal1D> {
    // Use JavaScript for simple generators (lighter weight)
    const samples = Math.floor(duration * sampleRate);
    const signal = new Float32Array(samples);
    const omega = (2 * Math.PI * freq) / sampleRate;

    for (let i = 0; i < samples; i++) {
      signal[i] = Math.sin(omega * i);
    }

    return signal;
  }

  async fft(signal: Signal1D): Promise<FrequencyDomain> {
    // Use Rust WASM for performance-critical FFT
    if (this.wasm) {
      return this.wasm.fft(signal);
    } else {
      // Fallback to JavaScript FFT
      const fft = new FFT(signal.length);
      const result = fft.createComplexArray();
      fft.realTransform(result, signal);

      return {
        real: new Float32Array(result.filter((_, i) => i % 2 === 0)),
        imag: new Float32Array(result.filter((_, i) => i % 2 === 1)),
      };
    }
  }

  async lowpassFilter(
    signal: Signal1D,
    cutoff: number,
    order: number = 4
  ): Promise<Signal1D> {
    // Use Rust WASM for filtering
    if (this.wasm) {
      return this.wasm.lowpass_filter(signal, cutoff, order);
    } else {
      // Fallback to dsp.js
      const dsp = new DSP.Lowpass(cutoff, signal.length);
      return dsp.process(signal);
    }
  }

  // ... implementations prioritize WASM with JS fallbacks
}
```

#### Error Handling & Graceful Degradation

```typescript
// backend/error-handling.ts
export class BackendCapabilityError extends Error {
  constructor(
    public feature: string,
    public backend: string,
    public suggestion: string
  ) {
    super(`Feature "${feature}" not available in ${backend} backend`);
  }
}

// In components
try {
  const result = await backend.waveletsTransform(signal, "db4");
} catch (error) {
  if (error instanceof BackendCapabilityError) {
    showNotification({
      type: "warning",
      title: "Desktop-Only Feature",
      message:
        error.suggestion ||
        "This feature requires the desktop app with Julia installed.",
      action: {
        label: "Download Desktop App",
        onClick: () => window.open("https://signalshow.org/download"),
      },
    });
  }
}
```

#### Backend Capability Detection

```typescript
// Show appropriate UI based on backend capabilities
function WaveletsPanel({ backend }: { backend: DSPBackend }) {
  const hasWavelets = backend.capabilities.wavelets;

  if (!hasWavelets) {
    return (
      <FeatureNotAvailable
        feature="Wavelet Transform"
        reason="Web version supports basic wavelets only. For full wavelet analysis, use the desktop app."
        downloadLink="https://signalshow.org/download"
      />
    );
  }

  return <WaveletsUI backend={backend} />;
}
```

#### Testing Strategy

```typescript
// __tests__/backend-parity.test.ts
describe("Backend parity tests", () => {
  let juliaBackend: JuliaServerBackend;
  let webBackend: WebBackend;

  beforeAll(async () => {
    juliaBackend = new JuliaServerBackend();
    webBackend = new WebBackend();
    await webBackend.init();
  });

  test("FFT produces same results (within tolerance)", async () => {
    const testSignal = generateTestSine(440, 1.0, 48000);

    const juliaResult = await juliaBackend.fft(testSignal);
    const webResult = await webBackend.fft(testSignal);

    // Allow small numerical differences
    expect(webResult.real).toBeCloseToArray(juliaResult.real, 0.001);
    expect(webResult.imag).toBeCloseToArray(juliaResult.imag, 0.001);
  });

  test("All common operations available in both backends", () => {
    const commonOps = ["fft", "lowpassFilter", "convolve", "generateSine"];

    for (const op of commonOps) {
      expect(juliaBackend).toHaveProperty(op);
      expect(webBackend).toHaveProperty(op);
    }
  });
});
```

#### Performance Monitoring

```typescript
// utils/performance.ts
export function measureBackendPerformance(backend: DSPBackend) {
  return {
    async benchmarkFFT(size: number): Promise<number> {
      const signal = new Float32Array(size).fill(1);
      const start = performance.now();
      await backend.fft(signal);
      return performance.now() - start;
    },

    async reportPerformance() {
      const sizes = [1024, 4096, 16384, 65536];
      const results = [];

      for (const size of sizes) {
        const time = await this.benchmarkFFT(size);
        results.push({ size, time });
        console.log(`[${backend.name}] FFT(${size}): ${time.toFixed(2)}ms`);
      }

      return results;
    },
  };
}
```

#### Bundle Analysis

| Component            | Desktop | Web (Initial) | Web (Lazy) | Notes                |
| -------------------- | ------- | ------------- | ---------- | -------------------- |
| React core           | ✓       | ~150KB gz     | -          | Always loaded        |
| Backend abstraction  | ✓       | ~10KB         | -          | Minimal overhead     |
| Julia server backend | ✓       | -             | -          | Desktop only         |
| Rust WASM module     | -       | ~200KB gz     | -          | Web only             |
| JavaScript DSP libs  | -       | ~50KB gz      | -          | Web only             |
| Plotly.js (viz)      | ✓       | -             | ~250KB gz  | Loaded on demand     |
| Total footprint      | ~2GB    | ~410KB gz     | +250KB gz  | Web is 5000x smaller |

**Key Benefits**:

- ✅ Single React codebase works everywhere
- ✅ Automatic backend selection (no user config)
- ✅ Graceful degradation (web shows what it can do)
- ✅ Easy testing (same test suite for both backends)
- ✅ Performance monitoring built-in
- ✅ Clear error messages guide users to desktop app when needed

---

## Component Breakdown

### Frontend Components (React)

#### 1. App Shell

```typescript
<App>
  ├── <NavigationBar />         // Top menu, help, settings
  ├── <WorkspaceLayout>          // Main layout container
  │   ├── <FunctionPanel />      // Left: function creation
  │   ├── <CenterStage />        // Center: main visualization
  │   ├── <OperationPanel />     // Right: operations sidebar
  │   └── <BottomToolbar />      // Export, save, demo launcher
  └── <ModalManager />           // Dialogs, help overlays
</App>
```

#### 2. Function Panel (`<FunctionPanel />`)

**Purpose**: Create and configure signals/images

**Sub-components**:

- `<FunctionSelector />`: Dropdown/search for function types
- `<ParameterEditor />`: Dynamic form based on selected function
- `<FunctionPreview />`: Small thumbnail preview
- `<FunctionList />`: Active functions (stackable/overlayable)

**State**:

```typescript
interface FunctionState {
  id: string;
  type: 'gaussian' | 'chirp' | 'delta' | ... ;
  parameters: Record<string, number | boolean | string>;
  dimension: '1D' | '2D';
  data?: Float64Array | Float64Array[]; // Cached result
}
```

#### 3. Operation Panel (`<OperationPanel />`)

**Purpose**: Apply operations to signals

**Sub-components**:

- `<OperationSelector />`: Choose operation (FFT, filter, etc.)
- `<OperationChain />`: Visual pipeline of operations
- `<OperationParams />`: Parameters for selected operation
- `<UndoRedoControls />`: History navigation

**State**:

```typescript
interface OperationChainState {
  operations: Array<{
    id: string;
    type: 'fft' | 'filter' | 'convolve' | ...;
    parameters: Record<string, any>;
    enabled: boolean;
  }>;
  activeSignalId: string;
  result?: SignalData;
}
```

#### 4. Visualization Display (`<CenterStage />`)

**Purpose**: Main plotting and interaction area

**Sub-components**:

- `<PlotlyChart />`: Wrapper for react-plotly.js
- `<AnimationControls />`: Play/pause for demos
- `<ViewControls />`: Zoom, pan, reset
- `<ExportMenu />`: PNG, SVG, PDF export

**Modes**:

- **Single plot**: One signal/image
- **Multi-plot**: Compare before/after, real/imaginary
- **Interactive demo**: Animated educational content

#### 5. 3D Visualization Display (`<ThreeDViewer />`) ✨ NEW

**Purpose**: Interactive 3D visualizations using Three.js/react-three-fiber

**Sub-components**:

- `<FFT3DSurface />`: 2D FFT results as height-mapped 3D surface
- `<ComplexSignalSpace />`: I/Q trajectory in 3D
- `<HolographicPattern />`: 3D volumetric diffraction patterns
- `<FilterResponse3D />`: Magnitude/phase as combined 3D surface
- `<ThreeDControls />`: Rotation, zoom, camera presets

**State**:

```typescript
interface ThreeDViewerState {
  viewType: "fft_surface" | "complex_space" | "hologram" | "filter_response";
  data: {
    vertices: Float32Array;
    colors?: Float32Array;
    indices?: Uint16Array;
  };
  camera: {
    position: [number, number, number];
    target: [number, number, number];
  };
  rendering: {
    wireframe: boolean;
    colormap: "viridis" | "plasma" | "jet" | "grayscale";
    lighting: "standard" | "flat" | "phong";
  };
}
```

**Example Implementation**:

```tsx
import { Canvas } from "@react-three/fiber";
import { OrbitControls, GradientTexture } from "@react-three/drei";

function FFT3DSurface({ fftData, width, height }: ThreeDProps) {
  const geometry = useMemo(() => {
    const geo = new PlaneGeometry(width, height, width - 1, height - 1);
    const positions = geo.attributes.position.array;

    // Apply FFT magnitude as Z-height
    for (let i = 0; i < fftData.length; i++) {
      positions[i * 3 + 2] = fftData[i]; // Z coordinate
    }

    geo.computeVertexNormals();
    return geo;
  }, [fftData, width, height]);

  return (
    <Canvas camera={{ position: [width / 2, height / 2, width] }}>
      <ambientLight intensity={0.3} />
      <directionalLight position={[10, 10, 5]} intensity={0.7} />

      <mesh geometry={geometry}>
        <meshStandardMaterial color="#4080ff" metalness={0.3} roughness={0.7} />
      </mesh>

      <OrbitControls />
      <gridHelper args={[width, height]} />
    </Canvas>
  );
}
```

**3D Use Cases**:
| Feature | Data Source | Educational Value |
|---------|-------------|-------------------|
| 2D FFT Surface | Julia 2D FFT → height map | Understand spatial frequency structure |
| Hologram Pattern | Cassegrain/MultiArm → 3D volume | See diffraction in 3D space |
| Filter Response | Magnitude + Phase → surface | Visualize magnitude/phase relationship |
| Complex Trajectory | I/Q time series → 3D path | Modulation scheme visualization |

**Performance Optimization**:

- Use `BufferGeometry` for large meshes (>10k vertices)
- Send data as binary (Float32Array) via WebSocket
- GPU-based colormapping using custom shaders
- LOD (Level of Detail) for distant surfaces

---

#### 6. File Operations Components (Nuthatch Desktop Integration)

**Purpose**: Manage file-based workflows for signals, operations, and workspaces

**Sub-components**:

##### `<FileMenu />` (in NavigationBar)

```typescript
interface FileMenuProps {
  onSaveWorkspace: () => Promise<void>;
  onLoadWorkspace: () => Promise<void>;
  onImportSignal: () => Promise<void>;
  onExportSignal: (signalId: string) => Promise<void>;
}

// File menu structure
const FILE_MENU_ITEMS = [
  { label: "New Workspace", action: "new", shortcut: "Cmd+N" },
  { label: "Open Workspace...", action: "open", shortcut: "Cmd+O" },
  { label: "Save Workspace", action: "save", shortcut: "Cmd+S" },
  { label: "Save Workspace As...", action: "save-as", shortcut: "Cmd+Shift+S" },
  { divider: true },
  { label: "Import Signal...", action: "import-signal", shortcut: "Cmd+I" },
  { label: "Export Signal...", action: "export-signal", shortcut: "Cmd+E" },
  { label: "Export Operation Chain...", action: "export-ops" },
  { divider: true },
  { label: "Open Demo...", action: "open-demo", shortcut: "Cmd+D" },
  { label: "Recent Files", action: "recent", submenu: [] }, // Populated dynamically
];
```

##### `<RecentFilesMenu />` (submenu of FileMenu)

**Purpose**: Quick access to recently opened `.sigWorkspace` files

```typescript
interface RecentFile {
  path: string;
  name: string;
  lastOpened: string; // ISO timestamp
  thumbnail?: string; // Base64 preview image
}

function RecentFilesMenu({ files }: { files: RecentFile[] }) {
  return (
    <ul className="recent-files-menu">
      {files.slice(0, 10).map((file) => (
        <li key={file.path} onClick={() => loadWorkspace(file.path)}>
          {file.thumbnail && (
            <img src={file.thumbnail} alt="" className="file-thumbnail" />
          )}
          <div className="file-info">
            <div className="file-name">{file.name}</div>
            <div className="file-path">{file.path}</div>
          </div>
        </li>
      ))}
      <li className="menu-divider" />
      <li onClick={() => clearRecentFiles()}>Clear Recent Files</li>
    </ul>
  );
}
```

##### `<FileAssociationHandler />` (in App Shell)

**Purpose**: Handle files opened from Nuthatch Desktop (double-click, drag-drop)

```typescript
function FileAssociationHandler() {
  useEffect(() => {
    // Listen for file open events from Nuthatch Desktop
    const unsubscribe = window.nuthatch.onFileOpen(async (filePath: string) => {
      const ext = filePath.split(".").pop();

      switch (ext) {
        case "sigWorkspace":
          await loadWorkspaceFromFile(filePath);
          break;
        case "sigDemo":
          await loadDemoFromFile(filePath);
          break;
        case "sig1d":
        case "sig2d":
          await importSignalFromFile(filePath);
          break;
        case "sigOp":
          await importOperationChainFromFile(filePath);
          break;
        default:
          console.warn(`Unknown file extension: ${ext}`);
      }

      // Update recent files list
      addToRecentFiles(filePath);
    });

    return () => unsubscribe();
  }, []);

  return null; // No UI, just event handling
}
```

##### `<ExportDialog />` (Modal)

**Purpose**: Export signal with format selection and metadata editing

```typescript
interface ExportDialogProps {
  signal: FunctionState;
  onClose: () => void;
}

function ExportDialog({ signal, onClose }: ExportDialogProps) {
  const [format, setFormat] = useState<"sig1d" | "sig2d" | "json" | "csv">(
    "sig1d"
  );
  const [metadata, setMetadata] = useState({
    name: signal.name,
    description: "",
    tags: [] as string[],
  });

  const handleExport = async () => {
    if (format === "sig1d" || format === "sig2d") {
      await exportAsSigFile(signal, metadata);
    } else if (format === "json") {
      await exportAsJSON(signal);
    } else if (format === "csv") {
      await exportAsCSV(signal);
    }
    onClose();
  };

  return (
    <Modal title="Export Signal" onClose={onClose}>
      <div className="export-dialog">
        <FormField label="Format">
          <Select value={format} onChange={(e) => setFormat(e.target.value)}>
            <option value="sig1d">SignalShow 1D (.sig1d)</option>
            <option value="sig2d">SignalShow 2D (.sig2d)</option>
            <option value="json">Raw JSON</option>
            <option value="csv">CSV (data only)</option>
          </Select>
        </FormField>

        {(format === "sig1d" || format === "sig2d") && (
          <>
            <FormField label="Name">
              <Input
                value={metadata.name}
                onChange={(e) =>
                  setMetadata({ ...metadata, name: e.target.value })
                }
              />
            </FormField>

            <FormField label="Description">
              <Textarea
                value={metadata.description}
                onChange={(e) =>
                  setMetadata({ ...metadata, description: e.target.value })
                }
                placeholder="Add description for educational use..."
              />
            </FormField>

            <FormField label="Tags">
              <TagInput
                tags={metadata.tags}
                onChange={(tags) => setMetadata({ ...metadata, tags })}
                suggestions={["demo", "lab", "homework", "research"]}
              />
            </FormField>
          </>
        )}

        <div className="dialog-actions">
          <Button variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button variant="primary" onClick={handleExport}>
            Export
          </Button>
        </div>
      </div>
    </Modal>
  );
}
```

##### `<WorkspaceStateIndicator />` (in BottomToolbar)

**Purpose**: Show save state and allow quick save

```typescript
interface WorkspaceState {
  filePath: string | null; // null = unsaved workspace
  isDirty: boolean; // Has unsaved changes
  lastSaved: Date | null;
}

function WorkspaceStateIndicator({ state }: { state: WorkspaceState }) {
  const getStatusText = () => {
    if (!state.filePath) return "Unsaved Workspace";
    if (state.isDirty) return "Unsaved Changes";
    return `Saved ${formatRelativeTime(state.lastSaved)}`;
  };

  const getStatusIcon = () => {
    if (state.isDirty) return "●"; // Dot for unsaved changes
    return "✓"; // Checkmark for saved
  };

  return (
    <div
      className={`workspace-state ${state.isDirty ? "dirty" : "clean"}`}
      onClick={() => state.isDirty && saveWorkspace()}
      title={state.filePath || "No file"}
    >
      <span className="status-icon">{getStatusIcon()}</span>
      <span className="status-text">{getStatusText()}</span>
      {state.filePath && (
        <span className="file-name">{getFileName(state.filePath)}</span>
      )}
    </div>
  );
}
```

**File Operations State Management**:

```typescript
interface FileOperationsState {
  currentWorkspaceFile: string | null;
  isDirty: boolean; // Track if state differs from saved file
  recentFiles: RecentFile[];
  autoSaveEnabled: boolean;
  lastAutoSave: Date | null;
}

const useFileOperations = create<FileOperationsState>((set, get) => ({
  currentWorkspaceFile: null,
  isDirty: false,
  recentFiles: loadRecentFilesFromLocalStorage(),
  autoSaveEnabled: true,
  lastAutoSave: null,

  // Mark workspace as dirty when any change occurs
  markDirty: () => set({ isDirty: true }),

  // Save workspace
  saveWorkspace: async (saveAs: boolean = false) => {
    const state = get();
    let filePath = state.currentWorkspaceFile;

    if (!filePath || saveAs) {
      // Show save dialog
      filePath = await window.nuthatch.saveFileDialog({
        suggestedName: "my-workspace.sigWorkspace",
        accept: { "application/json": [".sigWorkspace"] },
      });

      if (!filePath) return; // User cancelled
    }

    // Serialize current state
    const workspace = serializeWorkspace(useSignalShowStore.getState());

    // Write to file
    await window.nuthatch.writeFile(
      filePath,
      JSON.stringify(workspace, null, 2)
    );

    // Update state
    set({
      currentWorkspaceFile: filePath,
      isDirty: false,
      lastAutoSave: new Date(),
    });

    // Add to recent files
    addToRecentFiles(filePath);
  },

  // Auto-save implementation
  startAutoSave: () => {
    if (!get().autoSaveEnabled) return;

    setInterval(() => {
      const state = get();
      if (state.isDirty && state.currentWorkspaceFile) {
        // Only auto-save if we have an existing file
        state.saveWorkspace(false);
      }
    }, 30000); // Every 30 seconds
  },
}));
```

---

### Backend Components (Julia)

#### 1. Server Entry Point (`server.jl`)

```julia
using HTTP, WebSockets, JSON3

# Initialize server
const PORT = 8080
server = HTTP.listen(PORT) do request
    # Route to handlers
    handle_request(request)
end
```

#### 2. API Router

```julia
# REST endpoints
GET  /api/functions                  # List available functions
POST /api/functions/generate         # Generate signal/image
GET  /api/operations                 # List available operations
POST /api/operations/execute         # Execute operation
GET  /api/presets                    # Get preset configurations

# WebSocket
WS   /ws                             # Real-time computation
```

#### 3. Signal Generator (`FunctionGenerator.jl`)

```julia
abstract type SignalFunction end

struct GaussianFunction1D <: SignalFunction
    amplitude::Float64
    mean::Float64
    sigma::Float64
end

function generate(f::GaussianFunction1D, t::Vector{Float64})
    return f.amplitude .* exp.(-((t .- f.mean) ./ f.sigma).^2 / 2)
end

# Factory pattern for all 80+ function types
function create_function(type::String, params::Dict)
    # Dispatch to appropriate constructor
end
```

#### 4. Operation Pipeline (`OperationEngine.jl`)

```julia
abstract type SignalOperation end

struct FFTOperation <: SignalOperation
    normalize::Bool
    window::Union{Nothing, Symbol}
end

function apply(op::FFTOperation, signal::Vector{ComplexF64})
    if !isnothing(op.window)
        signal = apply_window(signal, op.window)
    end
    result = fft(signal)
    if op.normalize
        result ./= length(result)
    end
    return result
end

# Chain operations
function execute_pipeline(signal, operations::Vector{SignalOperation})
    result = signal
    for op in operations
        result = apply(op, result)
    end
    return result
end
```

#### 5. Data Serialization (`Serialization.jl`)

```julia
# Convert Julia data to JSON-serializable format
function serialize_signal(data::Vector{ComplexF64})
    return Dict(
        "real" => real(data),
        "imag" => imag(data),
        "length" => length(data)
    )
end

function deserialize_signal(dict::Dict)
    return complex.(dict["real"], dict["imag"])
end

# Efficient binary format (for large data)
using MessagePack
function serialize_binary(data::Vector{ComplexF64})
    return pack(Dict(
        "real" => reinterpret(UInt8, real(data)),
        "imag" => reinterpret(UInt8, imag(data))
    ))
end
```

---

## Data Flow

### Scenario 1: Generate a Signal

```
User selects "Gaussian" from dropdown
         ↓
React state updates: selectedFunction = 'gaussian'
         ↓
UI renders parameter editor with amplitude, mean, sigma
         ↓
User adjusts parameters (e.g., sigma = 2.0)
         ↓
Debounced API call: POST /api/functions/generate
    Body: { type: "gaussian", params: { amplitude: 1, mean: 0, sigma: 2 } }
         ↓
Julia server receives request
         ↓
FunctionGenerator creates GaussianFunction1D instance
         ↓
Generates sample points (e.g., -10:0.01:10)
         ↓
Computes y = amplitude * exp(-(x - mean)^2 / (2*sigma^2))
         ↓
Serializes to JSON: { x: [...], y: [...] }
         ↓
HTTP response returns data
         ↓
React receives data, updates Zustand store
         ↓
Plotly component re-renders with new data
         ↓
User sees plot update
```

### Scenario 2: Apply FFT Operation

```
User clicks "FFT" button in operation panel
         ↓
React adds FFTOperation to operation chain
         ↓
POST /api/operations/execute
    Body: {
      signalId: "signal_123",
      operations: [{ type: "fft", normalize: true }]
    }
         ↓
Julia retrieves cached signal data
         ↓
Applies FFT using FFTW.jl
         ↓
Returns magnitude and phase (or real/imag)
         ↓
React displays dual plot: time domain + frequency domain
```

### Scenario 3: Real-Time Interactive Demo (Sampling Theorem)

```
User opens "Sampling Demo" from demos menu
         ↓
React component mounts <SamplingDemo />
         ↓
WebSocket connection established to /ws
         ↓
User drags "Sampling Rate" slider
         ↓
On each drag event (throttled to 60fps):
    WS send: { demo: "sampling", sampleRate: 12 }
         ↓
Julia receives message
         ↓
Generates continuous signal
         ↓
Resamples at specified rate
         ↓
Applies reconstruction (sinc interpolation)
         ↓
Sends back 3 traces: original, samples, reconstructed
         ↓
React receives data via WebSocket
         ↓
Plotly plot updates in real-time (~60fps)
         ↓
Animation shows aliasing effects dynamically
```

### Scenario 4: Load Workspace from File (Nuthatch Desktop)

```
User double-clicks 'chirp-demo.sigWorkspace' file in Finder/Explorer
         ↓
Nuthatch Desktop recognizes .sigWorkspace extension
         ↓
Launches SignalShow.app (or brings to focus if already running)
         ↓
SignalShow receives file path via Nuthatch API
         ↓
SignalShow reads JSON file content
         ↓
Validates file version and schema
         ↓
Deserializes workspace:
    - Extracts 2 x 1D signals (chirp, gaussian)
    - Extracts operation chain (FFT → lowpass filter)
    - Extracts visualization settings (dual plot layout)
    - Extracts camera position (for 3D view)
         ↓
SignalShow sends signals to Julia backend:
    POST /api/functions/restore
    Body: [{ type: "chirp", params: {...} }, { type: "gaussian", params: {...} }]
         ↓
Julia regenerates signal data from parameters
         ↓
Returns computed data arrays { x: [...], y: [...] }
         ↓
SignalShow updates Zustand store:
    - Adds functions to state.functions Map
    - Sets state.operations to loaded chain
    - Sets state.visualization to loaded settings
         ↓
Plotly components re-render with restored workspace
         ↓
User sees exact classroom demo state from file
```

### Scenario 5: Save Current Workspace to File

```
User clicks "File → Save Workspace As..." menu
         ↓
Nuthatch Desktop shows native file picker
         ↓
User selects save location, enters filename 'my-demo.sigWorkspace'
         ↓
SignalShow gathers current state from Zustand store:
    - All functions from state.functions Map
    - Current operation chain from state.operations
    - Visualization settings from state.visualization
    - Camera position from state.cameraPosition
         ↓
SignalShow serializes to SigWorkspaceFile JSON:
    {
      version: "1.0",
      type: "workspace",
      metadata: { name: "my-demo", created: "2025-01-15T...", ... },
      signals: { sig1d: [...], sig2d: [...] },
      operationChains: [...],
      visualization: {...}
    }
         ↓
Nuthatch Desktop writes JSON string to file system
         ↓
Success notification shown to user
         ↓
File available in Files.app with thumbnail preview
```

### Scenario 6: Import Signal from .sig1d File

```
User clicks "Add Signal → Load from File..." button
         ↓
Nuthatch Desktop shows file picker filtered to .sig1d extension
         ↓
User selects 'chirp-440hz.sig1d' file
         ↓
SignalShow reads JSON file content
         ↓
Validates schema:
    - Check version field is "1.0"
    - Check type field is "signal1d"
    - Validate function.parameters match expected schema
         ↓
SignalShow sends to Julia backend:
    POST /api/functions/generate
    Body: { type: "chirp", params: { f0: 440, f1: 880, ... } }
         ↓
Julia generates signal data
         ↓
Returns { id: "signal_xyz", x: [...], y: [...] }
         ↓
SignalShow adds to Zustand store:
    state.addFunction({
      id: "signal_xyz",
      name: "chirp-440hz",
      type: "chirp",
      params: {...},
      data: { x: [...], y: [...] }
    })
         ↓
Plotly plot updates to include new signal trace
         ↓
Signal now appears in function list panel
```

---

## API Design

### REST API Specification

#### Generate Signal

```http
POST /api/functions/generate
Content-Type: application/json

{
  "type": "gaussian",
  "params": {
    "amplitude": 1.0,
    "mean": 0.0,
    "sigma": 2.0
  },
  "dimension": "1D",
  "sampleCount": 1024,
  "range": [-10, 10]
}

Response 200 OK:
{
  "id": "signal_abc123",
  "x": [array of sample points],
  "y": [array of values],
  "metadata": {
    "type": "gaussian",
    "dimension": "1D"
  }
}
```

#### Execute Operation

```http
POST /api/operations/execute
Content-Type: application/json

{
  "signalId": "signal_abc123",
  "operation": {
    "type": "fft",
    "parameters": {
      "normalize": true,
      "window": "hanning"
    }
  }
}

Response 200 OK:
{
  "id": "result_def456",
  "frequency": [array],
  "magnitude": [array],
  "phase": [array]
}
```

#### List Available Functions

```http
GET /api/functions

Response 200 OK:
{
  "functions": [
    {
      "id": "gaussian",
      "name": "Gaussian",
      "category": "analytic",
      "dimension": ["1D", "2D"],
      "parameters": [
        { "name": "amplitude", "type": "number", "default": 1.0 },
        { "name": "mean", "type": "number", "default": 0.0 },
        { "name": "sigma", "type": "number", "default": 1.0 }
      ],
      "description": "Gaussian (normal) distribution"
    },
    ...
  ]
}
```

### WebSocket Protocol

```javascript
// Client connects
ws = new WebSocket("ws://localhost:8080/ws");

// Client sends computation request
ws.send(
  JSON.stringify({
    type: "compute",
    operation: "sampling_demo",
    parameters: {
      sampleRate: 12,
      signalFreq: 5,
    },
  })
);

// Server responds with result
ws.onmessage = (event) => {
  const result = JSON.parse(event.data);
  // result.data contains computed signal
};

// Heartbeat to keep connection alive
setInterval(() => {
  ws.send(JSON.stringify({ type: "ping" }));
}, 30000);
```

---

## Animation & Video Export: Manim Integration ✨ NEW

### Overview

**Goal**: Enable users to export **publication-quality educational videos** using Python Manim, while maintaining real-time interactivity in the web app.

**Hybrid Approach**:

- **Web App**: Interactive demos with Framer Motion + D3.js (real-time)
- **Manim Export**: Professional video rendering for YouTube, papers, presentations

### Architecture: Web-to-Manim Pipeline

```
┌─────────────────────────────────────────────────────────────┐
│                    SignalShow Web App                        │
│  ┌──────────────┐  ┌────────────┐  ┌──────────────┐        │
│  │ User creates │→ │ Configure  │→ │ Export       │        │
│  │ signal/demo  │  │ animation  │  │ as JSON      │        │
│  └──────────────┘  └────────────┘  └──────┬───────┘        │
└─────────────────────────────────────────────│────────────────┘
                                              ↓
                                    animation_config.json
                                              ↓
┌─────────────────────────────────────────────│────────────────┐
│              Python Manim Generator (Desktop Only)           │
│  ┌──────────┐  ┌─────────────┐  ┌───────────────┐          │
│  │ Read JSON│→ │ Generate    │→ │ manim render  │          │
│  │          │  │ Python code │  │               │          │
│  └──────────┘  └─────────────┘  └───────┬───────┘          │
│                                          ↓                   │
│                                   sampling_demo.mp4          │
└──────────────────────────────────────────────────────────────┘
```

### Export Configuration Schema

```typescript
interface ManimExportConfig {
  version: '1.0';
  scene: {
    name: string; // e.g., "sampling_theorem_demo"
    duration: number; // seconds
    resolution: '1080p' | '4K';
    fps: 30 | 60;
  };

  elements: Array<{
    type: 'signal' | 'operation' | 'annotation';
    startTime: number;
    duration: number;

    // For signals
    signal?: {
      functionType: 'sine' | 'gaussian' | 'rect' | ...;
      parameters: Record<string, number>;
      displayRange: [number, number];
    };

    // For operations
    operation?: {
      type: 'fft' | 'sample' | 'filter';
      inputSignal: string;
      parameters: Record<string, number>;
    };

    // Animation
    animation: {
      intro: 'create' | 'fade_in' | 'write';
      outro?: 'fade_out' | 'uncreate';
      transitions?: Array<{
        parameter: string;
        from: number;
        to: number;
        duration: number;
        easing: 'linear' | 'smooth' | 'rush_from';
      }>;
    };
  }>;

  // 3Blue1Brown aesthetics
  style: {
    theme: 'dark' | 'light';
    colorScheme: 'blue_yellow' | 'teal_pink' | 'custom';
    fontFamily: 'CMU Serif' | 'Helvetica';
  };
}
```

### Example: Exporting Sampling Theorem Demo

**Step 1: User creates demo in SignalShow**

```tsx
// Web app state
const demoConfig = {
  signal: { type: "sine", frequency: 5, amplitude: 1 },
  sampleRates: [8, 12, 20, 40], // Demonstrate Nyquist theorem
  duration: 15,
};
```

**Step 2: Export as Manim config**

```json
{
  "version": "1.0",
  "scene": {
    "name": "sampling_theorem",
    "duration": 15,
    "resolution": "1080p",
    "fps": 60
  },
  "elements": [
    {
      "type": "signal",
      "startTime": 0,
      "duration": 3,
      "signal": {
        "functionType": "sine",
        "parameters": { "frequency": 5, "amplitude": 1 },
        "displayRange": [-5, 5]
      },
      "animation": {
        "intro": "create",
        "transitions": []
      }
    },
    {
      "type": "annotation",
      "startTime": 3,
      "duration": 2,
      "text": "Original signal: 5 Hz sine wave",
      "position": [0, 3]
    },
    {
      "type": "operation",
      "startTime": 5,
      "duration": 10,
      "operation": {
        "type": "sample",
        "inputSignal": "sine_5hz",
        "parameters": { "sampleRate": 8 }
      },
      "animation": {
        "intro": "fade_in",
        "transitions": [
          {
            "parameter": "sampleRate",
            "from": 8,
            "to": 40,
            "duration": 8,
            "easing": "smooth"
          }
        ]
      }
    }
  ],
  "style": {
    "theme": "dark",
    "colorScheme": "blue_yellow",
    "fontFamily": "CMU Serif"
  }
}
```

**Step 3: Python generator creates Manim code**

```python
# manim_generator.py (auto-generated from JSON)
from manim import *

class SamplingTheorem(Scene):
    def construct(self):
        # Create original signal
        axes = Axes(x_range=[-5, 5], y_range=[-1.5, 1.5])
        signal = axes.plot(lambda x: np.sin(5 * 2 * PI * x), color=BLUE)

        self.play(Create(axes), Create(signal), run_time=3)

        # Add annotation
        label = Text("Original signal: 5 Hz sine wave").to_edge(UP)
        self.play(Write(label), run_time=2)
        self.wait(1)

        # Demonstrate sampling
        sample_rate = ValueTracker(8)
        samples = always_redraw(lambda: self.get_sample_dots(
            axes, signal, sample_rate.get_value()
        ))

        self.play(FadeIn(samples))
        self.play(
            sample_rate.animate.set_value(40),
            run_time=8,
            rate_func=smooth
        )
        self.wait(2)

    def get_sample_dots(self, axes, signal, rate):
        # Helper to create sample points
        ...
```

**Step 4: Render video**

```bash
# Desktop app runs this automatically
manim render sampling_theorem.py SamplingTheorem -qh --fps 60
```

### Implementation Phases

#### Phase 1: Export Infrastructure (v1.0)

- Export demo configurations as JSON
- Manual Manim code generation (users write Python)
- Documentation/templates for common demos

#### Phase 2: Auto-Generation (v1.5)

- Python script reads JSON and generates Manim code
- Support for basic signals and operations
- Preset animation templates

#### Phase 3: Desktop Integration (v2.0 - Tauri)

- Bundle Python + Manim in desktop app
- One-click video export button
- Progress tracking for rendering
- Automatic upload to cloud storage

#### Phase 4: Advanced Features (v2.5)

- Custom Manim animation library for SignalShow
- 3D Manim scenes (Cassegrain, holography)
- Interactive video preview before rendering
- Batch export multiple demos

### Manim vs Web Animation Comparison

| Aspect            | Web (Framer Motion + D3)    | Python Manim Export       |
| ----------------- | --------------------------- | ------------------------- |
| **Purpose**       | Real-time interactivity     | Publication-quality video |
| **Frame Rate**    | 60fps interactive           | 60fps pre-rendered        |
| **Quality**       | Browser-dependent           | Consistent, 4K possible   |
| **Interactivity** | Full user control           | None (video playback)     |
| **Use Case**      | In-class demos, exploration | YouTube, papers, MOOCs    |
| **Creation Time** | Immediate                   | Minutes to render         |
| **File Size**     | N/A (live rendering)        | MP4 video file            |
| **Platform**      | Any browser                 | Desktop app only          |

**Recommendation**: Use BOTH! Web for learning, Manim for teaching materials.

### Manim Resources Integration

**ManimCommunity Edition**:

- Repository: https://github.com/ManimCommunity/manim
- Documentation: https://docs.manim.community/
- Discord: 10k+ members for support

**Grant Sanderson's ManimGL**:

- Repository: https://github.com/3b1b/manim
- Used in actual 3Blue1Brown videos
- More feature-rich but less stable

**SignalShow-Specific Manim Library** (future):

```python
# signalshow_manim.py
from manim import *

class SignalPlot(Scene):
    """Reusable SignalShow signal visualization"""
    def create_signal(self, function_type, params, **kwargs):
        # Standard SignalShow signal rendering
        ...

class OperationAnimation(Scene):
    """Reusable operation animations (FFT, filtering, etc.)"""
    def animate_fft(self, signal, **kwargs):
        # Visualize FFT transformation
        ...
```

---

## State Management

### Shareable URL Architecture 🔗

**Inspired by**: Desmos, Observable, GeoGebra (all use URL-based sharing)

#### URL Format

```
https://signalshow.org/#state=<compressed-state>
https://signalshow.org/#demo=<demo-id>
```

#### State Compression

```typescript
import LZString from "lz-string";

interface AppState {
  functions: FunctionState[];
  operations: OperationChainState;
  visualization: {
    plotType: "1D" | "2D" | "3D";
    options: PlotlyOptions;
  };
  version: "1.0"; // For future compatibility
}

// Serialize and compress
function stateToURL(state: AppState): string {
  const json = JSON.stringify(state);
  const compressed = LZString.compressToEncodedURIComponent(json);
  return `${window.location.origin}/#state=${compressed}`;
}

// Decompress and deserialize
function urlToState(url: string): AppState {
  const hash = new URL(url).hash.slice(1); // Remove '#'
  const params = new URLSearchParams(hash);
  const compressed = params.get("state");
  if (!compressed) throw new Error("No state in URL");

  const json = LZString.decompressFromEncodedURIComponent(compressed);
  return JSON.parse(json);
}
```

#### URL Shortening (for classrooms)

```typescript
// For sharing in classrooms (shorter URLs)
async function createShortURL(state: AppState): Promise<string> {
  // Store state in database, return short ID
  const response = await fetch("/api/share", {
    method: "POST",
    body: JSON.stringify(state),
  });
  const { id } = await response.json();
  return `${window.location.origin}/#s=${id}`; // e.g., #s=abc123
}
```

#### Demo Library URLs

```typescript
// Predefined demos
const DEMO_URLS = {
  "chirp-fft": "#demo=chirp-fft",
  "sampling-theorem": "#demo=sampling-theorem",
  "convolution-intro": "#demo=convolution-intro",
};

// Browse demos
fetch("/api/demos")
  .then((res) => res.json())
  .then((demos) => {
    // Display gallery of thumbnails
    // Each links to signalshow.org/#demo=<id>
  });
```

### File-Based Persistence (Nuthatch Desktop Platform)

**Integration with Nuthatch Desktop**: SignalShow leverages Nuthatch's native file system integration and modular app architecture for comprehensive file-based workflows.

#### File Format Specifications

SignalShow uses JSON-based portable file formats optimized for educational use and cross-app integration:

| Extension       | Purpose          | Contains                                            | Use Case                                        |
| --------------- | ---------------- | --------------------------------------------------- | ----------------------------------------------- |
| `.sig1d`        | 1D Signal        | Function definition, parameters, metadata           | Share chirp configs, save custom waveforms      |
| `.sig2d`        | 2D Signal        | 2D function definition, colormap settings           | Image processing demos, aperture designs        |
| `.sigOp`        | Operation Chain  | List of operations with parameters                  | Reusable processing pipelines (FFT→filter→IFFT) |
| `.sigWorkspace` | Complete Session | All signals, operations, plot state                 | Save entire classroom demo state                |
| `.sigDemo`      | Educational Demo | Pre-configured signals + annotations + instructions | Distributable interactive tutorials             |

#### File Schema Examples

**1D Signal File (`.sig1d`)**:

```typescript
interface Sig1DFile {
  version: "1.0";
  type: "signal1d";
  metadata: {
    name: string;
    description?: string;
    created: string; // ISO timestamp
    modified: string;
    tags?: string[]; // "demo", "lab", "homework"
  };
  function: {
    type: "chirp" | "sinusoid" | "gaussian" | "rect" | "custom";
    parameters: Record<string, number>; // Frequency, amplitude, etc.
    domain: {
      start: number;
      end: number;
      numPoints: number;
    };
  };
  visualization?: {
    color: string;
    lineWidth: number;
    showMarkers: boolean;
  };
}
```

**Operation Chain File (`.sigOp`)**:

```typescript
interface SigOpFile {
  version: "1.0";
  type: "operation-chain";
  metadata: {
    name: string;
    description?: string;
    created: string;
    modified: string;
    tags?: string[];
  };
  operations: Array<{
    id: string;
    type: "fft" | "filter" | "convolve" | "resample" | "custom";
    parameters: Record<string, any>;
    enabled: boolean; // Allow toggling without deleting
  }>;
  inputRequirements?: {
    signalType: "1d" | "2d";
    minPoints?: number;
    maxPoints?: number;
  };
}
```

**Workspace File (`.sigWorkspace`)**:

```typescript
interface SigWorkspaceFile {
  version: "1.0";
  type: "workspace";
  metadata: {
    name: string;
    description?: string;
    created: string;
    modified: string;
    tags?: string[];
  };
  signals: {
    sig1d: Sig1DFile[];
    sig2d: Sig2DFile[];
  };
  operationChains: SigOpFile[];
  visualization: {
    plotType: "1D" | "2D" | "3D";
    layout: "single" | "grid" | "overlay";
    plotlyOptions: any; // Plotly.js configuration
  };
  cameraPosition?: {
    // For 3D views
    eye: { x: number; y: number; z: number };
    center: { x: number; y: number; z: number };
  };
}
```

**Demo File (`.sigDemo`)**:

```typescript
interface SigDemoFile extends SigWorkspaceFile {
  type: "demo";
  educational: {
    title: string;
    author: string;
    learningObjectives: string[];
    instructions: Array<{
      step: number;
      text: string;
      highlightElement?: string; // CSS selector for UI element
      expectedOutcome?: string;
    }>;
    quiz?: Array<{
      question: string;
      type: "multiple-choice" | "numeric" | "text";
      options?: string[];
      correctAnswer: string | number;
      explanation: string;
    }>;
  };
  initialState: SigWorkspaceFile; // Starting configuration
  checkpoints?: SigWorkspaceFile[]; // Intermediate "solution" states
}
```

#### File Operations API

```typescript
// Save operations (via Nuthatch Desktop file picker)
async function saveSig1D(signal: FunctionState): Promise<void> {
  const file: Sig1DFile = {
    version: "1.0",
    type: "signal1d",
    metadata: {
      name: signal.name,
      created: new Date().toISOString(),
      modified: new Date().toISOString(),
    },
    function: {
      type: signal.type,
      parameters: signal.params,
      domain: signal.domain,
    },
  };

  await window.nuthatch.saveFile({
    suggestedName: `${signal.name}.sig1d`,
    content: JSON.stringify(file, null, 2),
    type: "application/json",
  });
}

async function saveWorkspace(state: SignalShowState): Promise<void> {
  const workspace: SigWorkspaceFile = {
    version: "1.0",
    type: "workspace",
    metadata: {
      name: `Workspace ${new Date().toLocaleDateString()}`,
      created: new Date().toISOString(),
      modified: new Date().toISOString(),
    },
    signals: {
      sig1d: Array.from(state.functions.values())
        .filter((f) => f.dimension === "1d")
        .map((f) => ({
          /* convert to Sig1DFile */
        })),
      sig2d: Array.from(state.functions.values())
        .filter((f) => f.dimension === "2d")
        .map((f) => ({
          /* convert to Sig2DFile */
        })),
    },
    operationChains: [state.operations], // Convert to SigOpFile
    visualization: state.visualization,
    cameraPosition: state.cameraPosition,
  };

  await window.nuthatch.saveFile({
    suggestedName: "my-workspace.sigWorkspace",
    content: JSON.stringify(workspace, null, 2),
    type: "application/json",
  });
}

// Load operations (via Nuthatch Desktop file picker)
async function loadSig1D(): Promise<FunctionState> {
  const fileHandle = await window.nuthatch.openFile({
    accept: { "application/json": [".sig1d"] },
  });

  const text = await fileHandle.text();
  const file: Sig1DFile = JSON.parse(text);

  // Validate version
  if (file.version !== "1.0") {
    throw new Error(`Unsupported file version: ${file.version}`);
  }

  // Convert to internal FunctionState
  return {
    id: generateId(),
    name: file.metadata.name,
    type: file.function.type,
    params: file.function.parameters,
    domain: file.function.domain,
    dimension: "1d",
  };
}

async function loadWorkspace(): Promise<void> {
  const fileHandle = await window.nuthatch.openFile({
    accept: { "application/json": [".sigWorkspace"] },
  });

  const text = await fileHandle.text();
  const workspace: SigWorkspaceFile = JSON.parse(text);

  // Restore entire application state
  const state = useSignalShowStore.getState();
  state.clearAll();

  workspace.signals.sig1d.forEach((sig1d) => {
    state.addFunction(convertSig1DToFunctionState(sig1d));
  });

  workspace.signals.sig2d.forEach((sig2d) => {
    state.addFunction(convertSig2DToFunctionState(sig2d));
  });

  state.setOperations(
    convertSigOpToOperationChain(workspace.operationChains[0])
  );
  state.setVisualization(workspace.visualization);

  if (workspace.cameraPosition) {
    state.setCameraPosition(workspace.cameraPosition);
  }
}
```

#### Educational Workflows Enabled by File Persistence

**Student Lab Workflow**:

1. Instructor distributes `.sigDemo` file via LMS or email
2. Student double-clicks → opens in SignalShow (Nuthatch Desktop file association)
3. Student follows interactive tutorial steps with built-in quiz questions
4. Student saves modified workspace as `.sigWorkspace` file
5. Student submits workspace file for grading
6. Instructor loads student workspace to review exact signal configurations

**Research Collaboration Workflow**:

1. Researcher creates custom 2D aperture function, saves as `.sig2d`
2. Colleague opens `.sig2d` file, applies FFT operation chain (`.sigOp`)
3. Combined workspace saved as `.sigWorkspace` for paper figure
4. Workspace file archived in research data repository with DOI

**Classroom Demo Workflow**:

1. Instructor prepares chirp demo, saves as `.sigWorkspace`
2. Before class, loads workspace to resume exact configuration
3. During class, modifies parameters live (no file save)
4. After class, saves updated workspace with new examples as `.sigWorkspace`

**Cross-App Integration (Nuthatch Desktop Platform)**:

- **Files.app**: Browse, organize, and preview `.sig*` files with thumbnails
- **Portfolio.app**: Embed live SignalShow visualizations in portfolio projects
- **Note-taking apps** (future): Drag `.sig1d` file into note → embedded interactive plot
- **Version control**: All files are human-readable JSON → works with Git for classroom repos

#### File Format Versioning Strategy

```typescript
// Handle future file format changes
interface FileVersionHandler {
  version: string;
  migrate?: (oldFormat: any) => any;
}

const FILE_VERSION_HANDLERS: Record<string, FileVersionHandler> = {
  "1.0": {
    version: "1.0",
    // No migration needed (base version)
  },
  "1.1": {
    version: "1.1",
    migrate: (old: Sig1DFile) => {
      // Example: Add new field with default
      return {
        ...old,
        rendering: old.rendering ?? { antialiasing: true },
      };
    },
  },
};

function loadFileWithMigration(fileContent: string): Sig1DFile {
  const parsed = JSON.parse(fileContent);
  const handler = FILE_VERSION_HANDLERS[parsed.version];

  if (!handler) {
    throw new Error(`Unsupported file version: ${parsed.version}`);
  }

  // Migrate through version chain if needed
  let migrated = parsed;
  const versions = Object.keys(FILE_VERSION_HANDLERS).sort();
  const startIndex = versions.indexOf(parsed.version);

  for (let i = startIndex + 1; i < versions.length; i++) {
    const nextHandler = FILE_VERSION_HANDLERS[versions[i]];
    if (nextHandler.migrate) {
      migrated = nextHandler.migrate(migrated);
    }
  }

  return migrated;
}
```

### Global State Management (Zustand)

```typescript
import create from "zustand";

interface SignalShowState {
  // Functions
  functions: Map<string, FunctionState>;
  addFunction: (fn: FunctionState) => void;
  updateFunction: (id: string, params: Partial<FunctionState>) => void;
  removeFunction: (id: string) => void;

  // Operations
  operations: OperationChainState;
  addOperation: (op: Operation) => void;
  removeOperation: (id: string) => void;

  // Visualization
  visualization: VisualizationState;
  setVisualization: (viz: Partial<VisualizationState>) => void;

  // UI state
  ui: {
    functionPanelOpen: boolean;
    operationPanelOpen: boolean;
    helpModalOpen: boolean;
  };
  togglePanel: (panel: keyof UIState) => void;

  // Sharing
  shareableURL: () => string;
  loadFromURL: (url: string) => void;
}

const useSignalShowStore = create<SignalShowState>((set, get) => ({
  functions: new Map(),
  addFunction: (fn) =>
    set((state) => ({
      functions: new Map(state.functions).set(fn.id, fn),
    })),

  // ... other actions

  shareableURL: () => {
    const state = get();
    return stateToURL({
      functions: Array.from(state.functions.values()),
      operations: state.operations,
      visualization: state.visualization,
      version: "1.0",
    });
  },

  loadFromURL: (url) => {
    try {
      const state = urlToState(url);
      set({
        functions: new Map(state.functions.map((f) => [f.id, f])),
        operations: state.operations,
        visualization: state.visualization,
      });
    } catch (e) {
      console.error("Failed to load state from URL:", e);
    }
  },
}));
```

### Export/Import State

```typescript
// Export as JSON file
function exportState() {
  const state = useSignalShowStore.getState();
  const json = JSON.stringify(
    {
      functions: Array.from(state.functions.values()),
      operations: state.operations,
      visualization: state.visualization,
    },
    null,
    2
  );

  const blob = new Blob([json], { type: "application/json" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "signalshow-state.json";
  a.click();
}

// Import from JSON file
async function importState(file: File) {
  const text = await file.text();
  const state = JSON.parse(text);
  useSignalShowStore.getState().loadFromURL(stateToURL(state));
}
```

### Local Storage (Auto-save)

```typescript
// Save state to localStorage every 30 seconds
useEffect(() => {
  const interval = setInterval(() => {
    const state = useSignalShowStore.getState();
    localStorage.setItem(
      "signalshow-autosave",
      JSON.stringify({
        functions: Array.from(state.functions.values()),
        operations: state.operations,
        visualization: state.visualization,
        timestamp: Date.now(),
      })
    );
  }, 30000); // 30 seconds

  return () => clearInterval(interval);
}, []);

// Restore on app load
useEffect(() => {
  const saved = localStorage.getItem("signalshow-autosave");
  if (saved) {
    const { timestamp, ...state } = JSON.parse(saved);
    // Ask user if they want to restore (if recent)
    if (Date.now() - timestamp < 24 * 60 * 60 * 1000) {
      if (confirm("Restore previous session?")) {
        useSignalShowStore.getState().loadFromURL(stateToURL(state));
      }
    }
  }
}, []);
```

---

## State Management (Original Section)

### React State Architecture (Zustand)

```typescript
// stores/signalStore.ts
interface SignalStore {
  // Signals
  signals: Map<string, SignalData>;
  addSignal: (signal: SignalData) => void;
  removeSignal: (id: string) => void;
  updateSignal: (id: string, data: Partial<SignalData>) => void;

  // Active selection
  activeSignalId: string | null;
  setActiveSignal: (id: string) => void;

  // Operations
  operationChain: Operation[];
  addOperation: (op: Operation) => void;
  removeOperation: (id: string) => void;
  reorderOperations: (startIndex: number, endIndex: number) => void;

  // Computation state
  isComputing: boolean;
  computationProgress: number;

  // Undo/Redo
  history: HistoryState[];
  historyIndex: number;
  undo: () => void;
  redo: () => void;
}

const useSignalStore = create<SignalStore>((set, get) => ({
  signals: new Map(),
  addSignal: (signal) =>
    set((state) => ({
      signals: new Map(state.signals).set(signal.id, signal),
    })),
  // ... rest of implementation
}));
```

### State Synchronization

**Local state** (React component state):

- UI interactions (slider positions, dropdown selections)
- Animation frame data
- Temporary form values

**Global state** (Zustand):

- Signal data
- Operation chains
- Application settings
- User preferences

**Server state** (Julia backend):

- Cached computation results
- Large datasets (2D images)
- Expensive operation outputs

**Synchronization Strategy**:

1. **Optimistic updates**: Update UI immediately, sync with server async
2. **Debouncing**: Batch rapid changes (e.g., slider drag) before server call
3. **Caching**: Store server results to avoid redundant computation
4. **Invalidation**: Clear cache when parameters change

---

## Performance Considerations

### 1. Computation Performance

**Problem**: FFT on 4096 samples = ~1ms in Julia, but serialization + network = ~10-20ms

**Solutions**:

- **Caching**: Store results of expensive operations
- **Incremental computation**: Only recompute what changed
- **WebSocket**: Lower latency than HTTP for rapid updates
- **Binary serialization**: MessagePack faster than JSON for large arrays
- **Downsampling**: Send fewer points for visualization (plot doesn't need all 4096)

```julia
# Example: Intelligent downsampling
function downsample_for_plot(data::Vector{Float64}, max_points::Int=1000)
    if length(data) <= max_points
        return data
    end
    # Use LTTB (Largest Triangle Three Buckets) algorithm
    return lttb_downsample(data, max_points)
end
```

### 2. Rendering Performance

**Problem**: Plotly re-renders entire chart on data update

**Solutions**:

- **React.memo**: Memoize plot components
- **Plotly's `react` method**: Update traces without full re-render
- **Virtual scrolling**: For long lists of signals/operations
- **Canvas fallback**: Use Canvas renderer for >10k points (faster than SVG)

```typescript
// Optimized Plotly update
const updatePlot = useCallback(
  (newData) => {
    if (plotRef.current) {
      Plotly.react(plotRef.current, newData, layout, config);
    }
  },
  [layout, config]
);
```

### 3. Network Performance

**Optimization Strategies**:
| Scenario | Data Size | Strategy | Latency |
|----------|-----------|----------|---------|
| Parameter change | <1KB | HTTP POST | ~10ms |
| Signal generation | 10-100KB | HTTP POST | ~20ms |
| Interactive demo | 100KB/sec | WebSocket | ~5ms |
| Large 2D image | 1-10MB | Chunked transfer | ~100ms |

**Binary vs JSON**:

- 1024-point complex signal: JSON = 50KB, MessagePack = 16KB
- 512×512 image: JSON = 2MB, MessagePack = 1MB

### 4. Memory Management

**React**:

- Cleanup WebSocket connections in `useEffect`
- Unsubscribe from stores when component unmounts
- Clear large arrays from state when not needed

**Julia**:

- Garbage collection for old cached results
- LRU cache for signal data (evict least recently used)
- Streaming for very large datasets

---

## User Interaction Patterns

### Keyboard Shortcuts ⌨️

**Inspired by**: Observable, Desmos power users

#### Global Shortcuts

```typescript
const KEYBOARD_SHORTCUTS = {
  // File Operations (Nuthatch Desktop)
  "Cmd+N": "New Workspace",
  "Cmd+O": "Open Workspace",
  "Cmd+S": "Save Workspace",
  "Cmd+Shift+S": "Save Workspace As",
  "Cmd+I": "Import Signal",
  "Cmd+E": "Export Signal",
  "Cmd+D": "Open Demo",

  // Computation
  "Ctrl+Enter": "Apply current operation",
  "Ctrl+Shift+Enter": "Apply operation chain",

  // Navigation
  "Ctrl+/": "Toggle function panel",
  "Ctrl+\\": "Toggle operation panel",
  "Ctrl+K": "Focus function search",

  // Export
  "Ctrl+E": "Export current view as PNG",
  "Ctrl+Shift+E": "Export options menu",
  "Ctrl+S": "Save state to file",

  // View
  "Ctrl+0": "Reset zoom to fit",
  "Ctrl++": "Zoom in",
  "Ctrl+-": "Zoom out",
  "Ctrl+1": "Switch to time domain view",
  "Ctrl+2": "Switch to frequency domain view",
  "Ctrl+3": "Switch to 3D view",

  // Editing
  "Ctrl+Z": "Undo",
  "Ctrl+Shift+Z": "Redo",
  Delete: "Remove selected function/operation",

  // Help
  "Shift+?": "Show keyboard shortcuts",
  F1: "Open help documentation",
};
```

#### Parameter Fine-Tuning with Arrow Keys

**Learned from Desmos**: Arrow keys on focused slider adjust by small increments

```typescript
function SmartSlider({ value, onChange, min, max, step }: SliderProps) {
  const handleKeyDown = (e: KeyboardEvent) => {
    if (e.key === "ArrowUp" || e.key === "ArrowRight") {
      onChange(Math.min(max, value + step));
    } else if (e.key === "ArrowDown" || e.key === "ArrowLeft") {
      onChange(Math.max(min, value - step));
    } else if (e.shiftKey && (e.key === "ArrowUp" || e.key === "ArrowRight")) {
      onChange(Math.min(max, value + step * 10)); // Shift+Arrow = 10x step
    }
  };

  return <Slider onKeyDown={handleKeyDown} {...props} />;
}
```

### Touch Support 📱

**Inspired by**: GeoGebra, Desmos, Mathigon (all tablet-friendly)

#### Design Requirements

- **Minimum touch target**: 44×44px (Apple HIG, WCAG 2.1 AAA)
- **Gesture support**: Pinch-to-zoom, two-finger pan, drag
- **No hover dependency**: All features accessible without mouse hover
- **Responsive layout**: Stack panels vertically on tablets

#### Touch Gestures for Plotly Charts

```typescript
import { useGesture } from "@use-gesture/react";

function TouchablePlot({ data, layout }: PlotProps) {
  const plotRef = useRef<HTMLDivElement>(null);

  const bind = useGesture({
    // Pinch to zoom
    onPinch: ({ offset: [scale] }) => {
      Plotly.relayout(plotRef.current, {
        "xaxis.range": calculateZoomedRange(scale),
      });
    },

    // Two-finger pan
    onDrag: ({ movement: [mx, my], touches }) => {
      if (touches === 2) {
        Plotly.relayout(plotRef.current, {
          "xaxis.range": calculatePannedRange(mx, my),
        });
      }
    },

    // Double-tap to reset zoom
    onDoubleClick: () => {
      Plotly.relayout(plotRef.current, {
        "xaxis.autorange": true,
        "yaxis.autorange": true,
      });
    },
  });

  return (
    <div ref={plotRef} {...bind()}>
      <Plot data={data} layout={layout} />
    </div>
  );
}
```

#### Touch-Friendly Sliders

```typescript
// Large touch targets (60px height), visual feedback
function TouchSlider({ value, onChange, label }: TouchSliderProps) {
  return (
    <div className="touch-slider">
      <label className="text-sm font-medium mb-2">{label}</label>
      <input
        type="range"
        value={value}
        onChange={(e) => onChange(parseFloat(e.target.value))}
        className="w-full h-[60px] touch-none" // Prevent scroll while dragging
      />
      <div className="text-center text-2xl font-bold mt-2">
        {value.toFixed(2)}
      </div>
    </div>
  );
}
```

### Accessibility (WCAG 2.1 AA)

- **Keyboard navigation**: All features accessible via keyboard only
- **Screen reader support**: Proper ARIA labels for all controls
- **Color contrast**: 4.5:1 minimum for text, 3:1 for UI components
- **Focus indicators**: Visible focus rings on all interactive elements
- **Alt text**: Descriptions for all plots and images

---

## Deployment Architectures

### Architecture 1: Web Application (University Server)

```
┌─────────────────┐
│   Students      │
│   (Browsers)    │
└────────┬────────┘
         │ HTTPS
         ▼
┌─────────────────┐
│  Static Host    │
│  (Netlify)      │  React SPA
│  - HTML/CSS/JS  │
│  - CDN cached   │
└────────┬────────┘
         │ WebSocket/HTTPS
         ▼
┌─────────────────┐
│  Julia Server   │
│  (Cloud VM)     │  University/Cloud
│  - Compute      │
│  - HTTP.jl      │
│  - Multi-user   │
└─────────────────┘
```

**Pros**:

- Centralized: Easy updates, consistent experience
- No installation required
- Can handle many simultaneous users (scale horizontally)

**Cons**:

- Requires internet connection
- Server costs
- Potential latency for remote users

---

### Architecture 2: Desktop Application (Downloadable)

```
┌──────────────────────────────────────────┐
│          User's Computer                 │
│  ┌────────────────────────────────────┐  │
│  │   Tauri App Shell                  │  │
│  │   ┌──────────────┐  ┌────────────┐ │  │
│  │   │  React UI    │  │  Julia     │ │  │
│  │   │  (WebView)   │◄─┤  Runtime   │ │  │
│  │   │              │  │  (Bundled) │ │  │
│  │   └──────────────┘  └────────────┘ │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
```

**Pros**:

- Offline capable
- No server costs
- Faster (no network latency)
- Full Julia REPL access

**Cons**:

- Larger download (~200MB with Julia)
- Update distribution more complex
- Platform-specific builds (Mac/Windows/Linux)

---

### Architecture 3: Hybrid (Best of Both)

```
┌─────────────────┐
│   Web Version   │  ← For quick demos, classroom
│   (Light)       │
└─────────────────┘

┌─────────────────┐
│ Desktop Version │  ← For research, heavy computation
│   (Full)        │
└─────────────────┘

┌─────────────────┐
│  Pluto Notebook │  ← For reproducible research
│  Integration    │
└─────────────────┘
```

**Recommendation**: Build all three!

1. Start with desktop (easiest to develop/test)
2. Deploy web version (reach more users)
3. Add Pluto integration (research community)

---

## Security Considerations

### Web Version

- **CORS**: Restrict API access to known frontends
- **Rate limiting**: Prevent computation abuse
- **Input validation**: Sanitize all parameters
- **Authentication**: Optional user accounts for saving work

### Desktop Version

- **Code signing**: Sign Tauri app for macOS/Windows
- **Sandboxing**: Limit Julia process permissions
- **Auto-updates**: Secure update mechanism (Tauri built-in)

---

## Scalability Considerations

### Single User (Desktop)

- No scaling needed
- All computation local
- Memory: 2-4GB sufficient

### Small Class (~30 students, Web)

- Single Julia server
- 4-8 CPU cores
- 16GB RAM
- Load balancing not needed

### Large Deployment (100s of users, Web)

- Multiple Julia servers
- Load balancer (Nginx)
- Redis for session state
- Horizontal scaling

---

## Development Phases

### Phase 1: Proof of Concept (2-3 weeks)

```
✅ Julia HTTP server
✅ React app with Plotly
✅ 5 basic functions
✅ 3 operations (FFT, filter, convolve)
✅ End-to-end working demo
```

### Phase 2: Core Functionality (6-8 weeks)

```
□ All 80+ function types
□ All 40+ operations
□ Operation chaining
□ Export (PNG/SVG)
□ Basic UI polish
```

### Phase 3: Interactive Demos (4-6 weeks)

```
□ Sampling theorem demo
□ Filtering demo (1D and 2D)
□ Holography demo
□ Convolution visualization
□ Animation system
```

### Phase 4: Desktop App (2-3 weeks)

```
□ Tauri packaging
□ Julia runtime bundling
□ Auto-start server
□ Installers (Mac/Windows/Linux)
```

### Phase 5: Polish & Deploy (3-4 weeks)

```
□ Documentation
□ Tutorial videos
□ Testing
□ Deployment
□ Announcement/launch
```

**Total**: ~17-24 weeks (4-6 months)

---

## Next Steps

1. ✅ Complete initial research (RESEARCH_OVERVIEW.md)
2. ✅ Define technology stack (TECH_STACK.md)
3. ✅ Design architecture (this document)
4. **Next**: Build proof-of-concept

   - Simple Julia server (Gaussian function + FFT)
   - Basic React UI
   - Plotly visualization
   - Measure performance

5. **Then**: Create detailed implementation plan
   - Component specifications
   - API contract definitions
   - Database schema (if needed)
   - Testing strategy

---

**Status**: Architecture design complete. Ready for prototyping.  
**Next Document**: FEATURE_MAPPING.md - Map Java features to new architecture
