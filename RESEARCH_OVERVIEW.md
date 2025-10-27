# SignalShow Web Modernization - Research Overview

**Vision**: "SignalShow is to DSP what Desmos is to algebra"

**Goal**: Transform SignalShow from a Java Swing application into a modern web platform for university teaching, research figure generation, and educational content creation.

**Status**: Web UI prototype complete; Julia backend and desktop version proposed

---

## Current Status

**Phase 1 (Web UI)**: Four React components implemented and tested
- SignalDisplay - Interactive waveform plots (Plotly.js)
- FunctionGenerator - Signal parameter controls (4 signal types)
- OperationsPanel - FFT and windowing operations  
- DemoGallery - 5 educational examples

**Backend**: JavaScript implementation functional (zero installation)

**Next Priorities**:
1. **v1.0**: WAV/CSV I/O, accessibility (WCAG 2.2 AA), guided mode, timeline recording, concept packs
2. **v1.5**: WASM performance, LMS integration, Docker appliance
3. **v2.0**: Tauri app, plugin API, Manim export, Julia runtime

See [`ROADMAP_REVISIONS.md`](./ROADMAP_REVISIONS.md) for timeline and [`SIGNALSHOW_STRATEGIC_RECOMMENDATIONS.md`](./SIGNALSHOW_STRATEGIC_RECOMMENDATIONS.md) for positioning strategy.

---

## Product Positioning

### Market Position

SignalShow occupies a unique niche:
- Only web-based DSP education tool with publication-quality exports
- Only platform coupling 1D signal analysis with 2D optics simulations
- Only tool offering reproducible figure pipelines with provenance tracking
- Only modern stack for DSP labs (J-DSP is legacy Java)

### Competitive Differentiation

**vs. MATLAB**: Free, web-based, beautiful UI, instant sharing  
**vs. Desmos**: Deep DSP/optics vs. general math; advanced operations  
**vs. GeoGebra**: Focused expertise in signals vs. broad math  
**vs. Observable**: GUI-first vs. code-first; Julia backend option  
**vs. J-DSP**: Modern React/Julia stack vs. legacy Java; story-driven demos  
**vs. PhET**: Specialist DSP/optics vs. general physics; publication workflow

Full competitive analysis in [`SIMILAR_PROJECTS_COMPARISON.md`](./SIMILAR_PROJECTS_COMPARISON.md).

### Target Partnerships

- Textbook publishers (interactive companion demos)
- OpenStax (open-source DSP textbook integration)
- IEEE Education Society (workshop co-sponsorship)
- ABET (align concept packs with accreditation outcomes)

---

## User Personas

Four primary audiences with distinct needs:

**1. Dr. Elena Martinez - Instructor (40%)**  
Goal: Make abstract DSP concepts tangible  
Needs: Pre-built concept packs, presentation mode, LMS integration, progress tracking

**2. Alex Chen - Student (45%)**  
Goal: Build intuition through exploration  
Needs: Guided mode, undo/redo, WAV/CSV import, lab report export

**3. Dr. Raj Patel - Researcher (10%)**  
Goal: Publication-quality figures with reproducibility  
Needs: High-DPI exports, CLI batch generation, expert mode, Python/Julia API

**4. Maya Rodriguez - Content Creator (5%)**  
Goal: Educational video production  
Needs: Timeline recording, Manim export, custom branding, 4K rendering

Full personas in [`PERSONAS.md`](./PERSONAS.md).

---

## Project Context

### Current State (Java Version)

- Technology: Java Swing GUI (2005-2009 codebase)
- Files: ~395 Java files
- Features:
  - 80+ analytic functions (Gaussian, Chirp, Bessel, Delta, windows, noise)
  - 40+ operations (FFT, filtering, convolution, correlation, derivatives)
  - Interactive demos (sampling, filtering, holography, Lissajous curves)
  - 1D & 2D support (signals and images)
  - Java Advanced Imaging (JAI) integration

### Proposed Future State

- Platform: Nuthatch Desktop modular app (web + desktop hybrid)
- Computation: Julia backend for mathematical operations
- Frontend: React 19+ with modern JavaScript
- Visualization: Plotly.js, D3.js, Three.js (3Blue1Brown-inspired graphics)
- File extensions: `.sig1d`, `.sig2d`, `.sigOp`, `.sigWorkspace`, `.sigDemo`
- Use cases: Live demos, guided labs, publication figures, video production, shareable workspaces

---

## Technology Research

### Julia-JavaScript Integration

**Strategy**: Julia backend server + REST/WebSocket API  
**Rationale**: WebAssembly compilation not production-ready  
**Performance**: Native-speed DSP operations  
**Real-time**: WebSocket streaming for interactivity

**Stack**:
- Julia HTTP.jl + WebSockets.jl for server
- JSON3.jl for data serialization
- DSP.jl, FFTW.jl for signal processing
- Binary transfer (Float32Array) for large datasets
- HTTP.jl performance: ~200 req/sec

### Scientific Visualization

**Primary**: Plotly.js (108k ⭐) for 2D scientific plots  
**Animations**: Framer Motion (23k ⭐) + D3.js (108k ⭐)  
**3D**: Three.js (103k ⭐) + react-three-fiber (29.7k ⭐)  
**Export**: PNG/SVG/PDF via Plotly; video via Manim integration  
**Performance**: 60fps+ with WebGL

See [`ANIMATION_AND_3D_STRATEGY.md`](./ANIMATION_AND_3D_STRATEGY.md) for details.

### Signal Processing

**Primary**: Julia DSP.jl backend  
**JavaScript**: Supplementary (client-side previews)  
**Performance**: Julia is 5-10x faster than JavaScript for FFT  
**Libraries**:
- Julia DSP.jl (primary engine)
- fft.js (client-side previews, 192k downloads/week)
- math.js (basic operations)
- stdlib-js (statistical functions)

### Architecture

**Pattern**: Hybrid modular app in Nuthatch Desktop  
**State**: Zustand (React state management)  
**Deployment**: Tauri (desktop) + Vercel/Netlify (web)  
**File System**: OPFS for browser, native FS for desktop

**Data Flow**:
- User interaction → React component → Zustand store
- Store → Julia server (HTTP/WebSocket) → DSP computation
- Result → Zustand → Plotly.js → Render

See [`ARCHITECTURE.md`](./ARCHITECTURE.md) and [`TECH_STACK.md`](./TECH_STACK.md) for specifications.

---

## Feature Implementation Strategy

### Function Generation (80+ functions from Java)

**Categories**:
1. Basic waveforms (sine, cosine, chirp, Gaussian)
2. Window functions (Hanning, Hamming, Blackman, Kaiser)
3. Noise models (Gaussian, Poisson, Binomial, Shot)
4. Specialized (Bessel, Airy, Hermite polynomials, Laguerre)
5. Optical apertures (circular, rectangular, annular)
6. 2D functions (cylinders, Cassegrain mirrors)

**Implementation**:
- Core functions: Julia DSP.jl, SpecialFunctions.jl
- 2D apertures: Custom Julia implementations
- Optical diffraction: Julia FFTW.jl

**Phase 1 subset (4 functions)**:
- Sine, Cosine, Chirp, Gaussian (all implemented)

### Operations (40+ from Java)

**Categories**:
1. Frequency domain (FFT, IFFT, power spectrum)
2. Filtering (lowpass, highpass, bandpass, FIR, IIR)
3. Correlation (cross-correlation, autocorrelation)
4. Convolution (1D, 2D, circular, linear)
5. Time-frequency (STFT, wavelets, spectrograms)
6. Derivatives/integrals (numerical differentiation, integration)
7. Optics (Fresnel propagation, aperture functions)

**Implementation**:
- Julia DSP.jl (filtering, FFT, windowing)
- Julia FFTW.jl (Fourier transforms)
- Custom Julia code (optics, correlation)

**Phase 1 subset (2 operations)**:
- FFT, windowing (all implemented)

See [`FEATURE_MAPPING.md`](./FEATURE_MAPPING.md) for complete mapping.

---

## Demos & Interactive Features

**Educational Demos (5 initial)**:
- Sampling theorem
- Windowing effects on FFT
- Filter frequency responses
- Convolution visualization
- Chirp analysis

**Interactive Elements**:
- Real-time parameter adjustment
- Drag-and-drop operation chains
- Keyboard shortcuts for presentation
- Touch support for iPad

---

## Modern DSP Features

**Time-Frequency Analysis**:
- Short-Time Fourier Transform (STFT)
- Spectrograms with dB scaling
- Wavelet transforms (Haar, Daubechies, Morlet, Mexican Hat)
- Hilbert transform (envelope, instantaneous phase/frequency)
- MFCCs (Mel-Frequency Cepstral Coefficients)

**Medical Imaging**:
- Radon transform (forward/inverse)
- Filtered back-projection for CT reconstruction
- Sinogram visualization

See [`FEATURE_ADDITIONS.md`](./FEATURE_ADDITIONS.md) for details.

---

## Hybrid Backend Strategy

**Desktop (Tauri)**:
- Full Julia server backend
- Local computation, complete feature set
- Auto-start with 15-minute timeout
- Requirements: Julia (~500MB) + packages (~1-2GB)

**Web (Browser)**:
- Rust + WebAssembly + JavaScript DSP
- No installation required
- ~95% feature parity with desktop

**Backend Abstraction**:
```javascript
const backend = window.__TAURI__
  ? new JuliaServerBackend()  // Desktop: optimal performance
  : new WebBackend();          // Browser: zero install
```

**Performance**:
| Backend | Environment | Speed | FFT (8192) | Bundle |
|---------|-------------|-------|------------|--------|
| Julia | Desktop | 100% | ~1ms | Server |
| JavaScript | Web | 5-10% | ~100ms | ~23KB |
| Rust WASM | Web | 60-95% | ~2-5ms | ~150KB |

See [`BACKEND_ABSTRACTION.md`](./docs/BACKEND_ABSTRACTION.md).

---

## UI/UX Design

**Framework**: shadcn/ui + Tailwind CSS + Radix UI
- Accessible (WCAG 2.1 compliant)
- Dark mode for classroom use
- Responsive with touch support

**3Blue1Brown Aesthetic**:
- Manim color schemes (blue/yellow, teal/pink)
- CMU Serif typography
- KaTeX for math rendering
- Smooth animations (Framer Motion spring physics)

**Workflows**:
1. Interactive exploration (student experimentation)
2. Figure generation (research publications)
3. Video production (educational content)

---

## Deployment

**Web Hosting**:
- Static hosting (Vercel/Netlify/GitHub Pages)
- PWA with offline UI support
- Julia server runs locally or cloud

**Desktop Distribution**:
- Tauri installers: DMG (macOS), MSI (Windows), AppImage (Linux)
- App signing and auto-updates
- Bundle: ~600KB Tauri + ~400MB Julia + ~200MB Python/Manim

---

## Development Roadmap

**v1.0 (4-6 months)** - Core Features:
- 6-8 prototype functions (Gaussian, sine, chirp, Gaussian, rect, delta)
- 8-10 essential operations (FFT, basic filters, convolution, correlation)
- Plotly.js 2D visualization
- JSON/PNG/SVG export
- Guided tutorials
- Web version with JavaScript backend

**v1.5 (7-9 months)** - Complete Library + 3D:
- All 80+ functions in organized batches
- All 40+ operations
- 3D visualization (2D FFT surfaces, holography)
- Demo library (5-10 key demos)
- WASM performance

**v2.0 (10-12 months)** - Desktop + Video:
- Tauri desktop app with bundled Julia
- Integrated Manim video export
- Python notebook integration
- Advanced 3D (custom shaders)

**v2.5+** - Educational Platform:
- Exercise/assignment system
- Real-time collaboration
- LMS integration
- Community demo library

---

## Implementation Status

**Research Phase**: Complete

**Documents Created**:
- ✅ TECH_STACK.md - Comprehensive technology decisions
- ✅ ARCHITECTURE.md - System design and API specs
- ✅ ANIMATION_AND_3D_STRATEGY.md - Graphics and animation details
- ✅ SIMILAR_PROJECTS_COMPARISON.md - Competitive analysis
- ✅ FEATURE_MAPPING.md - All 80+ functions and 40+ operations mapped
- ✅ FEATURE_ADDITIONS.md - Modern DSP enhancements
- ✅ BACKEND_ABSTRACTION.md - Multi-backend architecture
- ✅ PERSONAS.md - User stories and needs
- ✅ ROADMAP_REVISIONS.md - Timeline and priorities
- ✅ SIGNALSHOW_STRATEGIC_RECOMMENDATIONS.md - Positioning strategy

**Next Steps**:
1. Build proof-of-concept (Julia server + React + Plotly)
2. Performance benchmarking
3. v1.0 sprint planning
4. Iterative development with user testing

---

## Success Metrics

**Year 1**: 10,000 users  
**Year 2**: Adopted in 10 university DSP courses  
**Year 3**: Community library with 100+ demos  
**Year 5**: Standard tool for DSP education

---

**Last Updated**: October 26, 2025  
**Status**: Research complete, ready for implementation
