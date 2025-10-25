# SignalShow Web - Technology Stack Research

**Last Updated**: October 25, 2025  
**Status**: Initial Research Phase  
**Target**: Educational signal processing platform for web and desktop

---

## Executive Summary

This document outlines the recommended technology stack for modernizing SignalShow as a web-based application with Julia computation backend, React frontend, and beautiful interactive visualizations.

**Key Decision**: Julia backend + JavaScript frontend architecture (NOT Julia compiled to WebAssembly)

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     USER INTERFACE LAYER                     │
│  React + TypeScript + Modern UI Components + Visualizations │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ WebSocket/HTTP API
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                  COMPUTATION BACKEND LAYER                   │
│       Julia Server (Signal Processing Operations)           │
└─────────────────────────────────────────────────────────────┘
```

---

## Core Technology Decisions

### 1. Computation Engine: **Julia** ✓ CONFIRMED

**Decision**: Julia running as a backend server (NOT WebAssembly compilation)

#### Rationale
- **Performance**: Julia is designed for scientific computing, ~2-3x faster than JavaScript for numerical operations
- **Ecosystem**: Rich signal processing packages (DSP.jl, FFTW.jl, Interpolations.jl, etc.)
- **Code Quality**: More readable and maintainable than porting algorithms to JavaScript
- **Compatibility**: Easy to reuse or reference original SignalShow logic patterns

#### Integration Strategy: **Backend Server Architecture**
Julia will run as a separate process communicating with the web frontend via WebSocket or HTTP.

**Why not WebAssembly?**
- Julia WebAssembly support is still experimental (as of Oct 2025)
- Current tools (PackageCompiler.jl) primarily target native binaries, not WASM
- Classroom use cases benefit from local Julia REPL access anyway
- Allows users to extend with their own Julia code

#### Key Julia Packages

| Package | Purpose | Status |
|---------|---------|--------|
| **HTTP.jl** | Web server for API | Stable, 8.5k+ stars |
| **DSP.jl** | Digital signal processing | Stable, core package |
| **FFTW.jl** | Fast Fourier Transform | Stable, wraps FFTW C library |
| **Interpolations.jl** | Signal interpolation/resampling | Stable |
| **Images.jl** | 2D image processing | Stable |
| **Plots.jl** | (Optional) Server-side plotting | Stable |
| **Pluto.jl** | Inspiration for interactive architecture | Very active, 5.2k stars |
| **Blink.jl** | Julia-Electron integration (for desktop) | Stable, 370 stars |

---

### 2. Frontend Framework: **React** ✓ CONFIRMED

**Decision**: React 18+ with TypeScript

#### Rationale
- **Ecosystem**: Massive library support for scientific visualization
- **Component Model**: Perfect for modular signal processing operations
- **Performance**: Virtual DOM handles frequent plot updates efficiently
- **Hiring/Support**: Easiest to find developers and community help
- **Education**: React is taught in many university CS programs

#### Competitive Validation
React is the **de facto standard** for modern interactive educational platforms:
- ✅ **Observable Notebooks** - Built with React for notebook cells and interactive visualizations
- ✅ **Desmos** - Uses React for their calculator interface (confirmed via job postings)
- ✅ **GeoGebra** - Migrated to React for web version after moving from Java Swing
- ✅ **Mathigon** - Built entirely with React + TypeScript

**Key Insight**: All four competitors chose React. This validates it's the right choice for interactive educational software with complex state management.

#### React Architecture Decisions

| Aspect | Choice | Rationale | Competitive Validation |
|--------|--------|-----------|----------------------|
| **Language** | TypeScript | Type safety for complex numerical data | Observable, Mathigon use TypeScript |
| **State Management** | Zustand or Jotai | Lightweight, modern, better than Redux for this use case | Observable uses custom reactive cells (similar philosophy) |
| **Build Tool** | Vite | Faster than Create React App, modern ESM support | Industry standard for new React projects |
| **Styling** | Tailwind CSS + shadcn/ui | Modern, customizable, beautiful components | **Desmos aesthetic**: shadcn/ui enables similar clean, professional look |
| **Math Rendering** | KaTeX | Faster than MathJax, good enough for equations | Desmos uses KaTeX, GeoGebra uses MathJax (we choose faster) |

---

### 3. Visualization Libraries

#### Primary Plotting: **Plotly.js** ✓ RECOMMENDED

**Why Plotly.js wins**:
- ✅ Publication-quality output (SVG/PNG export)
- ✅ Interactive by default (zoom, pan, hover)
- ✅ Complex plots (3D, contour, heatmaps, spectrograms)
- ✅ React integration (react-plotly.js)
- ✅ 16k+ GitHub stars, active development
- ✅ Free and open source

#### Competitive Validation
- **Desmos**: Custom WebGL renderer for extreme performance, but Plotly.js matches their feature set for our use case
- **Observable**: Uses Observable Plot (D3-based, 5k stars) - similar philosophy to Plotly.js (declarative)
- **GeoGebra**: Custom canvas renderer for geometry, Plotly.js exceeds their plotting capabilities
- **Mathigon**: Uses D3.js for custom visualizations, Plotly.js is easier to use with comparable quality

**Key Insight**: Competitors either build custom renderers (huge effort) or use D3.js (requires more code). Plotly.js gives us **Desmos-quality plots with Observable-simple code**.

**Features Matching SignalShow**:
- 1D line plots (time/frequency domain)
- 2D heatmaps (spectrograms, images)
- Complex number visualization
- Multiple subplots
- Annotations and labels

**Example Use Cases**:
```javascript
// Time-domain signal plot
<Plot
  data={[{
    x: timeArray,
    y: signalArray,
    type: 'scatter',
    mode: 'lines',
    name: 'Signal'
  }]}
  layout={{
    title: 'Time Domain',
    xaxis: {title: 'Time (s)'},
    yaxis: {title: 'Amplitude'}
  }}
/>

// Frequency domain (FFT result)
<Plot
  data={[{
    x: frequencyArray,
    y: magnitudeArray,
    type: 'scatter',
    mode: 'lines'
  }]}
  layout={{
    title: 'Frequency Spectrum',
    xaxis: {title: 'Frequency (Hz)'},
    yaxis: {title: 'Magnitude'}
  }}
/>

// 2D image/spectrogram
<Plot
  data={[{
    z: imageData,  // 2D array
    type: 'heatmap',
    colorscale: 'Jet'
  }]}
/>
```

#### Secondary/Special Purpose Libraries

| Library | Use Case | Why |
|---------|----------|-----|
| **D3.js** | Custom interactive demos, educational animations | Ultimate flexibility, 3Blue1Brown uses D3 concepts |
| **Three.js** | 3D visualizations (if needed for advanced demos) | WebGL performance, beautiful 3D |
| **Recharts** | Simple charts in UI (not main plots) | React-native, easier than Plotly for basic UI charts |
| **Manim-inspired** | See animation section below | Educational video production |

---

### 4. Mathematical Animation: **3Blue1Brown Style**

#### The Manim Situation

**Manim** (Grant Sanderson's library) is **Python-based** and designed for VIDEO PRODUCTION, not interactive web apps.

**Key Insights from Research**:
- **ManimGL (3b1b/manim)**: Grant's personal version, actively maintained (81.5k stars)
- **Manim Community Edition**: Community-maintained fork (35.2k stars), more stable, better docs
- **Both are Python-only**: No official JavaScript port exists
- **Web attempts exist but limited**: Several npm packages claim "Manim-like" but are incomplete

#### Our Multi-Pronged Approach

##### **Approach 1: Manim Integration for Video Production** ✓ RECOMMENDED

Use actual **Python Manim** to generate educational videos and animations from SignalShow:

**Workflow**:
```
1. User creates signal/operation in SignalShow (web or desktop)
2. SignalShow exports configuration as JSON
3. Python script reads JSON and generates Manim code
4. Manim renders high-quality video
5. User can publish to YouTube, embed in papers, etc.
```

**Benefits**:
- Authentic 3Blue1Brown quality
- Grant Sanderson's exact aesthetic
- Perfect for educational video production
- Can be automated via scripts

**Implementation**:
```javascript
// SignalShow exports configuration
const config = {
  scenes: [{
    type: 'sampling_demo',
    signal: { type: 'sine', frequency: 5, amplitude: 1 },
    sampleRate: 12,
    duration: 10
  }]
};

// Python script (manim_generator.py) reads this and creates:
class SamplingDemo(Scene):
    def construct(self):
        # Generate scene from config
        signal = FunctionGraph(lambda x: np.sin(5*x))
        self.play(Create(signal))
        # ... rest of animation
```

**Desktop App Advantage**: Can bundle Python + Manim, one-click video export!

##### **Approach 2: Web-Based Manim-Inspired Animations** ✓ FOR INTERACTIVE DEMOS

For **real-time interactive** demos, use modern web animation libraries:

**Primary Stack**:
- **Framer Motion**: React animations, perfect for parameter tweens
- **D3.js**: Custom mathematical visualizations (what Manim uses concepts from)
- **GSAP**: Professional timeline-based animations

**Manim-Inspired Web Libraries** (evaluated):
| Library | Status | Recommendation |
|---------|--------|----------------|
| `vivid-animations` | New (2 months old), promising | ⚠️ Experimental, watch for maturity |
| `vimath` | 1 year old, interactive math | ⚠️ Limited activity |
| `mathlikeanim-rs` | Rust-based, WebAssembly | ⚠️ Complex, overkill for our needs |
| `react-manim` | Abandoned (4 years) | ❌ Do not use |
| `manichrome` | Canvas 2D engine (3 months) | ⚠️ Too new, unproven |

**Verdict**: None of the web "Manim ports" are production-ready. Better to use established animation libraries.

##### **Approach 3: Hybrid Solution** ✓ BEST OF BOTH WORLDS

**For Interactive Learning** (in-app):
- Use Framer Motion + D3.js for real-time demos
- Students can interact with sliders, see immediate results
- Fast, responsive, educational

**For Content Creation** (export):
- Export same configuration to Manim (Python)
- Render publication-quality videos
- Share on YouTube, embed in papers

**Example User Journey**:
```
1. Professor creates sampling demo in SignalShow
2. Students interact with it in real-time during lecture
3. Professor clicks "Export as Video"
4. Manim renders a polished 60fps video
5. Professor uploads to course website
```

#### Animation Libraries Deep Dive

| Library | Type | Use For | Pros | Cons | Stars |
|---------|------|---------|------|------|-------|
| **Framer Motion** | React | UI transitions, value tweens | Easy React integration, spring physics | Not designed for complex math | 23k |
| **React Spring** | React | Physics-based animations | Beautiful motion, well-maintained | Steeper learning curve | 28k |
| **D3.js** | Vanilla JS | Custom data visualizations | Ultimate control, what 3Blue1Brown references | More code required | 108k |
| **GSAP** | Vanilla JS | Timeline-based sequences | Professional, powerful, used in production | Commercial license for some features | 19k |
| **Three.js** | WebGL | 3D graphics (see section 5) | Industry standard for 3D | Overkill for 2D animations | 103k |
| **Remotion** | React + Node | Video export from React | React + video in one tool | Relatively new, learning curve | 20k |

**Recommendation for v1.0**: **Framer Motion** + **D3.js** + **Python Manim Export**
- Framer Motion: Simple parameter transitions (sliders changing signal values)
- D3.js: Complex educational demos (sampling theorem, aliasing, etc.)
- Manim: Video export for publication and YouTube content

---

### 5. Signal Processing in JavaScript (Supplementary)

While Julia handles heavy computation, JavaScript libraries are needed for:
- Client-side previews
- Lightweight operations
- Reduced server round-trips

#### JavaScript DSP Libraries

| Library | Features | Status | Recommendation |
|---------|----------|--------|----------------|
| **fft.js** | FFT/IFFT, real transform | Active, 192k downloads/week | ✅ Use for client-side FFT |
| **dsp.js** | Comprehensive DSP | Unmaintained (last update 2012) | ⚠️ Reference only |
| **scijs** | Scientific computing ecosystem | Partially maintained | ⚠️ Evaluate specific packages |
| **math.js** | General mathematical operations | Very active | ✅ Use for basic math |
| **numeric.js** | Linear algebra, numerical analysis | Unmaintained | ❌ Avoid |
| **stdlib-js** | Comprehensive math/stats | Active, well-documented | ✅ Consider for utilities |

**Recommended JavaScript Stack**:
- **fft.js**: Client-side FFT for live previews
- **math.js**: Complex number arithmetic, basic operations
- **stdlib-js**: Statistical functions, special functions (Bessel, etc.)

#### Performance Note
For serious computation (large FFTs, heavy filtering), always use Julia backend. JavaScript is 5-10x slower for these operations.

---

### 6. 3D Visualization & WebGL: **Three.js Ecosystem** ✓ MAJOR ENHANCEMENT

#### Why Add 3D Graphics?

**Current Java Version**: Primarily 2D visualizations (signal plots, FFT magnitude plots)

**Modernization Opportunity**: Add **3D visualizations** that were impractical in Java Swing:
- **3D Frequency Surfaces**: 2D FFT results as height-mapped surfaces
- **Complex Signal Spaces**: Visualize I/Q data in 3D
- **Holographic Patterns**: Cassegrain apertures, diffraction patterns in 3D
- **Filter Response Surfaces**: Magnitude/phase as 3D mesh
- **Signal Correlation Volumes**: Cross-correlation in 3D space

#### The Three.js + React Stack

**Core Library**: **Three.js** (r180) - Industry Standard for WebGL
- ⭐ **103k GitHub stars**
- Used by: Vercel, NASA, Google, major design agencies
- Mature, stable, extensive documentation
- Full WebGL support with clean JavaScript API

**React Integration**: **react-three-fiber (R3F)** ✓ STRONGLY RECOMMENDED
- ⭐ **29.7k GitHub stars**
- Official React renderer for Three.js
- "No overhead, 100% gain" - uses React's reconciler
- Write Three.js scenes using React components
- Perfect for SignalShow's component-based architecture

#### The R3F Ecosystem

**Essential Libraries**:

| Package | Purpose | Why We Need It |
|---------|---------|----------------|
| **@react-three/fiber** | Core renderer | React components for Three.js |
| **@react-three/drei** | Helpers & abstractions | Cameras, controls, loaders, common shapes |
| **@react-three/postprocessing** | Effects & filters | Bloom, depth-of-field, color grading |
| **three-stdlib** | Essential Three.js addons | Geometries, loaders, utilities |
| **leva** | GUI controls | Interactive parameter panels for demos |
| **zustand** | State management | Already in our stack, works great with R3F |

**Advanced Capabilities** (future):

| Package | Purpose | Educational Use Case |
|---------|---------|----------------------|
| **@react-three/rapier** | 3D physics | Wave propagation simulations |
| **@react-three/xr** | VR/AR support | Immersive signal visualization |
| **maath** | Math utilities | Easing, randomization, vector ops |

#### 3D Graphics Implementation Strategy

##### **Phase 1: Basic 3D Plots** (v1.0)

**Features**:
- 3D surface plots for 2D FFT results
- Interactive rotation/zoom/pan with OrbitControls
- Height-mapped frequency domain data
- Color-coded magnitude visualization

**Code Example**:
```tsx
import { Canvas } from '@react-three/fiber';
import { OrbitControls } from '@react-three/drei';

function FFT3DSurface({ fftData }: { fftData: number[][] }) {
  return (
    <Canvas camera={{ position: [5, 5, 5] }}>
      <ambientLight intensity={0.5} />
      <pointLight position={[10, 10, 10]} />
      
      {/* Surface mesh from FFT data */}
      <mesh>
        <planeGeometry args={[10, 10, 100, 100]} />
        <meshStandardMaterial 
          color="#4080ff"
          wireframe={false}
        />
      </mesh>
      
      <OrbitControls />
    </Canvas>
  );
}
```

##### **Phase 2: Advanced Visualizations** (v1.5)

**Features**:
- Complex number visualization (magnitude + phase in 3D)
- Animated filter responses
- Multi-signal 3D overlays
- Shader-based colormaps (GPU-accelerated)

**Custom Shaders**:
```glsl
// Vertex shader for height-mapped FFT
attribute vec3 position;
uniform sampler2D fftTexture;

void main() {
  vec4 fftValue = texture2D(fftTexture, uv);
  vec3 pos = position;
  pos.z = fftValue.r * heightScale;
  gl_Position = projectionMatrix * modelViewMatrix * vec4(pos, 1.0);
}
```

##### **Phase 3: Interactive 3D Demos** (v2.0)

**Features**:
- Holographic aperture simulation (Cassegrain, multi-arm)
- 3D convolution visualization
- Spatial filter design in 3D frequency space
- Real-time 3D parameter sweeps

#### Performance Considerations

**WebGL Advantages**:
- **GPU Acceleration**: Offload computation to graphics card
- **60fps+ Rendering**: Smooth animations even with complex meshes
- **Instancing**: Render thousands of objects efficiently
- **Texture Mapping**: Use GPU for FFT data storage

**React Three Fiber Benefits**:
- **Automatic Disposal**: Memory management handled by React
- **Declarative**: No manual scene management
- **Component Reusability**: Build library of 3D components
- **State Integration**: Zustand state drives 3D visualizations

#### Alternative 3D Libraries Considered

| Library | Pros | Cons | Verdict |
|---------|------|------|---------|
| **Babylon.js** | Great physics, editor | Heavier than Three.js | ❌ Overkill |
| **Plotly.js 3D** | Easy 3D plots | Limited interactivity | ✓ Use for simple plots |
| **WebGL directly** | Maximum control | Too low-level | ❌ Reinventing wheel |
| **A-Frame** | VR-focused | Not React-friendly | ❌ Wrong use case |
| **PixiJS** | 2D rendering | No 3D support | ❌ Not for 3D |

**Final Decision**: **Three.js + react-three-fiber**
- Most mature, best React integration
- Largest community, most tutorials
- Perfect balance of power and usability
- Proven in production environments

#### 3D Use Cases in SignalShow

**High-Priority 3D Features**:

1. **2D FFT Visualization** ⭐⭐⭐
   - Current: 2D heatmap
   - Enhanced: 3D surface with interactive rotation
   - Educational Value: Students understand frequency domain structure

2. **Holographic Diffraction Patterns** ⭐⭐⭐
   - Current: 2D intensity plots
   - Enhanced: 3D volumetric rendering
   - Unique Feature: Few tools show this in 3D

3. **Filter Frequency Response** ⭐⭐
   - Current: Magnitude/phase as separate 2D plots
   - Enhanced: Combined 3D surface (magnitude as height, phase as color)
   - Educational Value: See magnitude + phase relationship

4. **Signal Correlation** ⭐⭐
   - Current: 1D correlation plots
   - Enhanced: 3D correlation volume for 2D signals
   - Advanced Use: Stereo imaging, SAR processing

5. **Complex Signal Space** ⭐
   - Current: Real/imaginary as separate plots
   - Enhanced: 3D trajectory in I/Q space
   - Educational Value: Visualize modulation schemes

#### Integration with Julia Backend

**Data Flow**:
```
Julia (DSP computation)
  ↓ WebSocket
JSON (FFT result: {data: Float64[][], dims: [256,256]})
  ↓ TypeScript
Convert to Three.js geometry
  ↓ GPU
Render 3D surface
```

**Optimization**: Send data as **binary** (Float32Array) instead of JSON for large 3D datasets

---

### 7. Desktop Application Framework

For downloadable standalone app requirement.

#### **Tauri** ✓ RECOMMENDED over Electron

| Aspect | Tauri | Electron | Winner |
|--------|-------|----------|--------|
| **Bundle Size** | ~600KB | ~50MB | Tauri |
| **Memory Usage** | Uses system WebView | Bundles Chromium | Tauri |
| **Security** | Rust backend, secure by default | Node.js backend | Tauri |
| **Performance** | Native Rust performance | V8 + Node overhead | Tauri |
| **Modern** | Released 2022, cutting edge | Mature (2013), larger ecosystem | Depends |
| **Julia Integration** | Need to bundle Julia separately | Easier via Node.js | Electron |

**Verdict**: **Tauri for production**, Electron for rapid prototyping

#### Competitive Validation
- **GeoGebra's Migration Story**: Moved from Java Swing → HTML5 web → Multi-platform (web + desktop + mobile)
  - We're doing similar: Java Swing → React web → Tauri desktop
  - **Key Lesson**: GeoGebra proves "web-first, then package for desktop" works at massive scale (millions of users)
- **Observable**: Web-only (no desktop app needed), but has VS Code extension for power users
  - Validates that web-first is acceptable for educational software
- **Desmos**: Web-first, then iOS/Android apps (not desktop)
  - Students prefer web/mobile over desktop installs

**SignalShow Strategy**: 
1. **v1.0**: Web-only (like Observable, Desmos)
2. **v2.0**: Tauri desktop for video export workflow (like GeoGebra's multi-platform approach)

**Tauri Advantages for SignalShow**:
- Smaller download for students (major factor)
- Better performance for real-time visualization
- Modern security model
- Still allows full web stack (React, etc.)

**Tauri + Julia Integration Plan**:
1. Package Julia runtime with Tauri app
2. Tauri Rust backend spawns Julia server process
3. Web frontend connects to Julia via localhost WebSocket
4. Works identically to browser version, but self-contained

---

### 8. UI Component Library

#### **shadcn/ui** ✓ RECOMMENDED

**Why shadcn/ui**:
- ✅ Beautiful, modern, customizable components
- ✅ Built on Radix UI (accessible, headless components)
- ✅ Tailwind CSS integration
- ✅ Copy components into your project (not npm dependency)
- ✅ Perfect for educational software aesthetic

#### Competitive Validation - The "Desmos Aesthetic"
**Goal**: Match Desmos's beautiful, minimalist, professional design

**How shadcn/ui Achieves This**:
- **Clean, uncluttered** - Desmos's signature look comes from minimal visual chrome
- **Professional yet approachable** - shadcn/ui's default styling feels "serious but friendly"
- **Customizable with Tailwind** - Can fine-tune colors, spacing, shadows to match Desmos
- **Modern accessibility** - Built on Radix UI (same foundation as many top design systems)

**Competitor UI Approaches**:
- **Desmos**: Custom CSS, minimal framework (we can match their aesthetic with shadcn/ui)
- **GeoGebra**: Material Design (looks dated compared to Desmos)
- **Observable**: Custom design system (clean but more "code editor" than "calculator")
- **Mathigon**: Colorful, playful (great for K-8, less professional than we want)

**Key Insight**: shadcn/ui gives us Desmos-level aesthetics without building a custom design system from scratch.

**Alternative Considered**: Material-UI (MUI)
- More opinionated styling
- Larger bundle size
- Not as trendy/modern as shadcn

**Component Needs**:
- Sliders (parameter adjustment) - **Desmos-style real-time feel**
- Dropdowns (function selection) - **Searchable like Observable's cell types**
- Tabs (organize operations) - **Clean like GeoGebra's tool panels**
- Modals (help text, settings) - **Unobtrusive like Desmos's help**
- Buttons, inputs (standard controls) - **Professional like all competitors**

All available in shadcn/ui.

---

## Complete Technology Stack Summary

### Frontend (Browser/Desktop)
```
├── Framework: React 18+ with TypeScript
├── Build Tool: Vite
├── State Management: Zustand
├── Routing: React Router v6
├── UI Components: shadcn/ui + Tailwind CSS
├── Visualization:
│   ├── Primary: Plotly.js (via react-plotly.js)
│   ├── Custom: D3.js v7
│   └── Optional: Three.js (for 3D)
├── Animation: Framer Motion + D3 transitions
├── Math Rendering: KaTeX
├── JavaScript DSP: fft.js + math.js
└── Desktop Packaging: Tauri
```

### Backend (Computation)
```
├── Language: Julia 1.11+ (LTS)
├── Server: HTTP.jl
├── WebSocket: WebSockets.jl
├── Signal Processing:
│   ├── DSP.jl (filters, windows, spectral analysis)
│   ├── FFTW.jl (Fourier transforms)
│   ├── Interpolations.jl (resampling)
│   └── Images.jl (2D processing)
├── Data Format: JSON or MessagePack
└── Optional: Pluto.jl integration for notebooks
```

### Development Tools
```
├── Version Control: Git + GitHub
├── Package Management:
│   ├── Frontend: npm/pnpm
│   └── Backend: Julia Pkg
├── Testing:
│   ├── Frontend: Vitest + React Testing Library
│   └── Backend: Julia Test stdlib
├── Code Quality:
│   ├── TypeScript: ESLint + Prettier
│   └── Julia: JuliaFormatter
└── CI/CD: GitHub Actions
```

### Deployment Options
```
├── Web App: Static hosting (Netlify/Vercel) + Julia server (DigitalOcean/AWS)
├── Desktop App: Tauri + bundled Julia runtime
└── Notebook: Integration with Pluto.jl notebooks
```

---

## Open Questions & Research Needed

### High Priority
- [ ] **Julia server performance**: Latency for real-time operations? (need benchmarks)
- [ ] **Data serialization**: JSON vs MessagePack vs binary formats?
- [ ] **WebSocket vs HTTP**: Which for different operation types?
- [ ] **Complex number format**: How to pass between Julia and JavaScript?
- [ ] **Image data transfer**: Efficient format for 2D arrays?

### Medium Priority
- [ ] **State persistence**: Save/load user sessions? Format?
- [ ] **Collaborative features**: Share configurations with others?
- [ ] **Export formats**: Which are priority? (PNG, SVG, PDF, MP4, GIF?)
- [ ] **Julia version management**: How to handle Julia updates?
- [ ] **Package dependencies**: Pin versions or allow updates?

### Low Priority
- [ ] **Mobile support**: Is tablet interaction important?
- [ ] **Accessibility**: Screen reader support for visualizations?
- [ ] **Internationalization**: Multiple languages for UI?
- [ ] **Plugin system**: Allow third-party extensions?

---

## Next Research Steps

1. **Build Proof-of-Concept** (Week 1)
   - Simple Julia HTTP server
   - React app with Plotly
   - Single operation (FFT) end-to-end
   - Measure latency and performance

2. **Test Visualization Libraries** (Week 1-2)
   - Implement 5 key plot types in Plotly
   - Build one interactive demo with D3.js
   - Test export to PNG/SVG quality
   - Verify 3Blue1Brown aesthetic achievable

3. **Architecture Prototyping** (Week 2)
   - Design Julia API structure
   - Plan React component hierarchy
   - Prototype state management approach
   - Test data serialization options

4. **Desktop App Prototype** (Week 3)
   - Basic Tauri shell
   - Bundle Julia runtime
   - Test auto-startup of Julia server
   - Verify cross-platform (Mac/Windows/Linux)

---

## Technology Risk Assessment

| Technology | Risk Level | Mitigation |
|------------|-----------|------------|
| Julia backend | **Low** | Well-established, good ecosystem |
| React frontend | **Very Low** | Industry standard, vast resources |
| Plotly.js | **Low** | Mature, proven for scientific plotting |
| Julia-JS integration | **Medium** | Need to design API carefully, test latency |
| Tauri desktop | **Medium** | Newer tech, but growing rapidly, good docs |
| D3.js for animations | **Medium** | Powerful but complex, learning curve |
| Real-time interactivity | **Medium** | Depends on WebSocket performance |

**Overall Risk**: **Low to Medium** - Mostly proven technologies, some integration challenges

---

## Alternative Technologies Considered & Rejected

### Pure JavaScript (No Julia)
**Why Rejected**: Performance inadequate for large FFTs, complex filtering. Would need WebAssembly ports of FFTW, which exist but are harder to maintain than Julia.

### Python Backend (Instead of Julia)
**Why Rejected**: Julia is faster, better ecosystem for signal processing, more modern. Python backend would work but offers no advantages.

### WebAssembly Compiled Julia
**Why Rejected**: Immature tooling, larger binary size, debugging difficulties. Backend server is more flexible.

### Vue.js or Svelte (Instead of React)
**Why Rejected**: Smaller ecosystems for scientific visualization. React has better Plotly integration and more examples.

### Observable Notebooks (Instead of Custom App)
**Why Considered**: Observable is beautiful for interactive data viz.  
**Why Rejected**: Want full control over UI/UX, desktop app requirement, Julia integration easier with custom app.

### Pure WebGL Visualization (Three.js only)
**Why Rejected**: Plotly is higher-level and easier for standard plots. Three.js reserved for specific 3D needs.

---

## Estimated Technology Learning Curve

**For Developer with JavaScript/React Experience**:
- Julia basics: 1-2 weeks
- DSP.jl ecosystem: 1-2 weeks
- Plotly.js: 3-5 days
- D3.js animations: 1-2 weeks
- Tauri desktop: 3-5 days
- **Total**: 6-8 weeks to proficiency

**For Developer with Julia Experience**:
- Modern React/TypeScript: 2-3 weeks
- Plotly.js + visualization: 1 week
- D3.js: 1-2 weeks
- Web deployment: 3-5 days
- **Total**: 5-7 weeks to proficiency

---

## License Compatibility Check

All recommended technologies are permissively licensed:

| Technology | License | Commercial Use | Attribution Required |
|------------|---------|----------------|---------------------|
| Julia | MIT | ✅ Yes | ❌ No |
| React | MIT | ✅ Yes | ❌ No |
| Plotly.js | MIT | ✅ Yes | ❌ No |
| D3.js | ISC | ✅ Yes | ❌ No |
| Tauri | Apache 2.0/MIT | ✅ Yes | ❌ No |
| Vite | MIT | ✅ Yes | ❌ No |
| Tailwind CSS | MIT | ✅ Yes | ❌ No |
| KaTeX | MIT | ✅ Yes | ❌ No |

**Conclusion**: No license issues. All can be used in open-source educational software.

---

**Status**: Initial research complete. Ready for proof-of-concept development.  
**Next Document**: [ARCHITECTURE.md](./ARCHITECTURE.md) - System design and component structure
