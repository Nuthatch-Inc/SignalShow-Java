# Technology Stack

**Purpose**: Technology selection for SignalShow modernization

---

## Core Stack

**Frontend**:
- React 19 + TypeScript
- shadcn/ui components (Radix UI base)
- Tailwind CSS

**State Management**:
- Zustand (lightweight, performant)

**Computation**:
- Hybrid backend (Julia/JavaScript/WASM)
- Backend abstraction layer

**Visualization**:
- Plotly.js (2D scientific plots)
- D3.js (custom animations)
- Three.js + react-three-fiber (3D)

**Desktop**:
- Tauri (v2.0+)
- Native file I/O

---

## Backend Strategy

### Three-Tier Approach

**Tier 1: JavaScript** (v1.0 - Production Ready)
- Pure browser implementation
- Math.sin/cos for signal generation
- fft.js library (192k downloads/week)
- Direct convolution algorithms
- Bundle: ~23KB
- Performance: 5-10% of native
- Features: ~60% coverage
- **Advantage**: Zero installation

**Tier 2: Rust + WebAssembly** (v1.5 - Planned)
- rustfft (FFT optimized)
- dasp (DSP operations)
- ndarray (array processing)
- Bundle: ~150KB
- Performance: 60-95% of native
- Features: ~85% coverage
- **Advantage**: Near-native speed in browser

**Tier 3: Julia Server** (v2.0 - Desktop Optional)
- HTTP.jl + WebSockets.jl
- DSP.jl, FFTW.jl, SpecialFunctions.jl
- localhost:8080
- Performance: 100% (native)
- Features: 100% coverage
- **Advantage**: Complete DSP library

### Backend Abstraction

**Automatic selection**:
```javascript
const backend = window.__TAURI__
  ? new JuliaServerBackend()  // Desktop (if available)
  : new WebBackend();          // Browser (always works)
```

**Unified API** (same interface for all backends):
```javascript
await backend.generateSine(440, 1.0, 8000);
await backend.fft(signal);
await backend.filter(signal, 'lowpass', {cutoff: 1000});
```

---

## Visualization Libraries

### Plotly.js (Primary 2D)
- **Stars**: 17k
- **Use**: Scientific plots, waveforms, spectrograms
- **Exports**: PNG, SVG, PDF
- **Performance**: 60fps for <10k points
- **Bundle**: ~3MB (use plotly.js-dist-min)

### D3.js (Custom Animations)
- **Stars**: 108k
- **Use**: Custom visualizations, transitions
- **Animation**: Smooth parameter changes
- **Integration**: Works with React

### Three.js + react-three-fiber (3D)
- **Stars**: 103k (Three.js), 30k (R3F)
- **Use**: 3D FFT surfaces, holography, diffraction
- **Performance**: 60fps with WebGL
- **Future**: v1.5 (deferred from v1.0)

---

## Julia DSP Libraries

**Core Packages**:
- **FFTW.jl** - Fastest FFT implementation
- **DSP.jl** - Filters, windows, spectral analysis
- **SpecialFunctions.jl** - Bessel, erf, gamma functions
- **Distributions.jl** - Noise generation
- **Images.jl** - 2D image operations

**HTTP Server**:
- **HTTP.jl** - REST API (~200 req/sec)
- **WebSockets.jl** - Real-time streaming
- **JSON3.jl** - Fast serialization

**Performance**:
- Julia startup: ~3-5 seconds
- First computation (JIT): ~1-2 seconds
- Subsequent calls: <10ms (FFT 4096 samples)

---

## JavaScript DSP Libraries

**FFT**:
- **fft.js** - Pure JavaScript FFT (192k downloads/week)
- **jsfft** - Alternative implementation
- Performance: ~100ms for 4096 samples

**Math**:
- **math.js** - General mathematical operations
- **stdlib-js** - Statistical functions, special functions
- Complex number support

**Audio**:
- **Web Audio API** - Native browser audio processing
- **wavefile** - WAV file I/O

**Not Using** (unmaintained):
- dsp.js - Last updated 2014
- numeric.js - Last updated 2013

---

## UI Components

**shadcn/ui** (Recommended):
- Radix UI primitives (accessible)
- Tailwind CSS styling
- Dark mode built-in
- Customizable components
- Tree-shakeable

**Key Components**:
- Sliders (parameter controls)
- Dropdowns (signal/operation selection)
- Tabs (workspace organization)
- Modals (settings, help)
- Command palette (keyboard shortcuts)

---

## Animation & Video

**Web Animations**:
- **Framer Motion** (23k stars)
  - Smooth parameter transitions
  - Spring physics
  - Gesture support
  - React integration

**Video Export** (v2.0):
- **Python Manim** (desktop only)
  - 3Blue1Brown-style animations
  - JSON config → MP4 rendering
  - Requires desktop app with bundled Python

---

## Development Tools

**Build**:
- Vite (fast dev server, optimized builds)
- TypeScript (type safety)
- ESLint + Prettier (code quality)

**Testing**:
- Vitest (unit tests)
- Playwright (E2E tests)
- React Testing Library (component tests)

**Version Control**:
- Git + GitHub
- Semantic versioning
- Conventional commits

---

## Deployment

**Web Hosting**:
- Vercel / Netlify (static hosting)
- GitHub Pages (alternative)
- PWA with service worker

**Desktop Packaging**:
- Tauri build system
- DMG (macOS)
- MSI (Windows)
- AppImage (Linux)
- Code signing support

**Container**:
- Docker (classroom deployment)
- Julia + web UI + nginx
- LMS integration ready

---

## Performance Targets

| Operation | Target | JavaScript | Julia | WASM (planned) |
|-----------|--------|------------|-------|----------------|
| Generate signal (4096) | <10ms | ~5ms | <1ms | ~2ms |
| FFT (4096) | <100ms | ~100ms | ~1ms | ~5ms |
| Filter | <50ms | ~50ms | ~1ms | ~3ms |
| Plot render | <50ms | ~30ms | N/A | N/A |
| Startup | <2s | instant | ~5s | instant |

---

## Bundle Size Targets

**v1.0 (JavaScript)**:
- React + UI: ~500KB gzipped
- Plotly.js: ~800KB gzipped
- JavaScript DSP: ~23KB
- **Total**: <2MB gzipped

**v1.5 (+ WASM)**:
- Add Rust WASM: ~150KB
- **Total**: ~2.2MB gzipped

**v2.0 (Desktop)**:
- Tauri app: ~600KB
- Julia runtime: ~400MB (optional download)
- **Total installer**: ~1MB (without Julia)

---

## Browser Support

**Target**:
- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
- Modern mobile browsers

**Required Features**:
- ES2020
- WebGL (for 3D)
- Web Workers
- IndexedDB / localStorage
- File System Access API (desktop)

---

## Accessibility

**Standards**:
- WCAG 2.2 Level AA compliance
- Keyboard navigation (all features)
- Screen reader support (ARIA)
- High contrast mode
- Resizable text

**Tools**:
- axe DevTools (automated testing)
- Screen reader testing (NVDA, VoiceOver)
- Keyboard-only testing

---

## Security

**Web**:
- CSP headers (content security policy)
- HTTPS only
- No eval() or dangerous innerHTML
- Dependency scanning (npm audit)

**Desktop**:
- Sandboxed Julia server (localhost only)
- File access controls
- No network access by default
- Code signing (macOS, Windows)

---

## Key Technology Decisions

1. **Hybrid backend** enables progressive enhancement (JavaScript → WASM → Julia)
2. **Plotly.js** for scientific visualization (proven, widely used)
3. **Zustand** over Redux (simpler, less boilerplate)
4. **shadcn/ui** over Material-UI (more customizable, better performance)
5. **Tauri** over Electron (smaller, faster, more secure)
6. **Julia** over Python (faster DSP, better type system, easier parallelization)

---

**Implementation**: Start with Tier 1 (JavaScript) for v1.0, add Tier 2 (WASM) in v1.5, offer Tier 3 (Julia) optionally in v2.0 desktop app.
