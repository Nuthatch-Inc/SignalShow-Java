# Desmos Research: Implementation Analysis and SignalShow Feasibility

## What is Desmos and Why Does It Matter for SignalShow?

### Overview

**Desmos** is a free, web-based graphing calculator (www.desmos.com/calculator) widely regarded as one of the most elegant and accessible mathematical visualization tools ever created. Used by 75+ million people annually, Desmos has become the gold standard for interactive mathematical exploration in education.

### Why Desmos is Relevant to SignalShow

The SignalShow modernization vision statement describes the goal as **"Desmos for signal processing"**—meaning SignalShow should embody the same principles that make Desmos exceptional:

1. **Zero Barrier to Entry**: Desmos requires no installation, no account, no tutorial. Users can immediately start plotting equations and see results. SignalShow should similarly allow instant signal exploration without friction.

2. **Immediate Visual Feedback**: Every change in Desmos (dragging a slider, editing an equation) produces instant visual updates. SignalShow should provide the same immediacy for signal operations (filtering, FFT, modulation).

3. **Exploratory Learning**: Desmos encourages experimentation through sliders, draggable points, and dynamic graphs. Users discover mathematical relationships by playing with parameters. SignalShow should enable similar exploratory learning for signal processing concepts.

4. **Beautiful, Minimal Interface**: Desmos's UI is clean and uncluttered—an expression list sidebar, a graph paper canvas, and parameter controls. Nothing extraneous. SignalShow should adopt this visual simplicity.

5. **Accessibility**: Desmos leads the industry in accessibility features (keyboard navigation, screen reader support, Braille modes, audio trace). SignalShow should match this commitment to inclusivity.

### The Research Question

Given Desmos's excellence, this document investigates: **Can we build SignalShow on top of Desmos's technology, or reuse their code/libraries?**

This would potentially save development time and leverage battle-tested rendering and interaction systems. However, as this research reveals, the answer is more nuanced than a simple yes or no.

---

## Executive Summary

**Research Question**: Can SignalShow be built on top of Desmos, or can we reuse Desmos code/libraries for SignalShow development?

**Key Findings**:

- **No open-source code**: Desmos is proprietary, closed-source software. Only 1 repository (JOAS) exists on GitHub with 4 stars - not the calculator code.
- **Embedding only**: Desmos provides a comprehensive JavaScript API for **embedding** their calculator, not for accessing their rendering/computation engine.
- **Cannot build on top**: SignalShow cannot be built "on top of Desmos" in a technical sense - you can only embed Desmos as a black box component.
- **No shared libraries**: Desmos's internal rendering engine, WebGL implementation, and math computation libraries are not available for reuse.
- **Different domains**: Desmos is optimized for 2D/3D mathematical functions; SignalShow focuses on 1D signal processing (time-domain, frequency-domain, filtering).

**Recommendation**: Study Desmos's **design philosophy and UX patterns**, but implement SignalShow independently using modern web technologies (WebGL for plotting, Web Audio API for signal processing, JavaScript/TypeScript for computation).

---

## 1. Desmos Technology Overview

### 1.1 What Desmos Is

**Desmos Studio, PBC** (Public Benefit Corporation) provides a suite of free mathematical visualization tools:

- **Graphing Calculator** (flagship product) - 2D function plotting
- **3D Calculator** - 3D surface visualization
- **Scientific Calculator** - Evaluation and symbolic math
- **Four-Function Calculator** - Basic arithmetic
- **Geometry Tool** - Geometric constructions
- **Matrix Calculator** - Linear algebra

**Usage**: 75+ million users annually worldwide

**Business Model**: Free consumer tools + paid partnerships (textbook publishers, assessment companies, education platforms)

### 1.2 Technical Architecture (Inferred)

Desmos does not publicly document their internal architecture, but from available evidence:

**Frontend**:

- JavaScript/TypeScript web application
- Custom rendering engine (likely WebGL-based for performance)
- MathQuill for LaTeX math input/rendering
- Custom graph paper rendering with interactive elements

**Computation Engine**:

- Proprietary JavaScript math engine
- Symbolic manipulation capabilities
- Numerical optimization (Levenberg-Marquardt for regressions)
- Variable Projection algorithm for nonlinear least squares
- Adaptive sampling for curve plotting
- Interval arithmetic for inequality shading

**No Backend**: Entirely client-side computation (all processing happens in the browser)

### 1.3 Known Technologies

**Confirmed**:

- **MathQuill**: LaTeX equation editor (open-source, used by Desmos)
- **LaTeX**: Math notation standard
- **WebGL**: Likely used for high-performance 2D/3D rendering
- **Canvas API**: Fallback or additional rendering
- **Web Audio API**: Used for tone generation and sonification features

**Proprietary**:

- Math expression parser and evaluator
- Function plotting and adaptive sampling algorithms
- Regression fitting algorithms
- Symbolic differentiation engine
- Optimization routines

---

## 2. Desmos API Analysis

### 2.1 API Capabilities

Desmos provides a **JavaScript embedding API** (currently v1.9) that allows:

**Embedding**:

```javascript
<script src="https://www.desmos.com/api/v1.9/calculator.js?apiKey=YOUR_KEY"></script>
<div id="calculator"></div>
<script>
  var calculator = Desmos.GraphingCalculator(document.getElementById('calculator'));
  calculator.setExpression({ latex: 'y = x^2' });
</script>
```

**Programmatic Control**:

- `setExpression()` / `removeExpression()` - Add/remove equations
- `setState()` / `getState()` - Save/load entire calculator state
- `setMathBounds()` - Control viewport
- `screenshot()` / `asyncScreenshot()` - Export images
- `observe()` / `observeEvent()` - React to user interactions
- `HelperExpression` - Monitor variable values

**Configuration Options** (90+ settings):

- UI elements (keypad, expressions list, graph paper visibility)
- Math features (sliders, tables, inequalities, regressions)
- Accessibility (Braille modes, audio trace, screen reader support)
- Styling (colors, line styles, point styles, fonts)
- Behavior (drag modes, auto-zoom, projection modes)

### 2.2 What the API Does NOT Provide

**No access to**:

- Rendering engine internals
- Math computation engine
- Function evaluation primitives
- Plotting algorithms
- Symbolic math capabilities
- Data structures for expressions

**Limitations**:

- Calculator is a **black box** - you control it through API methods, but cannot access internal code
- Cannot extend with custom functions beyond Desmos's built-in set
- Cannot customize rendering beyond provided style options
- Cannot use Desmos's math engine for non-graphing purposes

### 2.3 API Use Cases

**What you CAN do**:

- Embed interactive math visualizations in educational content
- Create custom math applications with Desmos as the graphing component
- Programmatically generate and manipulate graphs
- Build math assessment tools
- Create interactive demonstrations and activities

**Example Partners**:

- Khan Academy (interactive exercises)
- Pearson, McGraw-Hill (textbook platforms)
- College Board, ACT (digital assessments)
- Curriculum developers (custom activities)

---

## 3. Open Source Status

### 3.1 GitHub Presence

**Desmos GitHub Organization**: `github.com/desmos`

- **1 public repository**: JOAS (4 stars, 2 forks) - appears to be utility code, not calculator
- **0 calculator source code** repositories
- **0 rendering engine** repositories
- **0 math library** repositories

### 3.2 Available Sample Code

Desmos provides **example implementations** showing API usage:

- Examples hosted at `desmos.com/api/v1.9/docs/examples/*.html`
- Sample CMS application on GitHub (`desmosinc/desmos-sample-cms`)
- These show **how to use the API**, not how Desmos works internally

### 3.3 Licensing

**Desmos Calculator API**:

- Free for development (demo API key provided)
- Production use requires API key (contact partnerships@desmos.com)
- Self-hosting option available (private GitHub repo access for partners)
- Terms of Service apply

**Important**: Self-hosting still provides **compiled JavaScript bundles**, not source code.

---

## 4. Technical Implementation Insights

### 4.1 Regression Algorithm Details

From Desmos Engineering blog post on regressions:

**Approach**:

- Linear regressions: Closed-form solution via matrix algebra
- Nonlinear regressions: Levenberg-Marquardt iterative optimization
- Variable Projection for mixed linear/nonlinear parameters
- Smart initial guesses (30+ starting points covering wide parameter space)
- Automatic model reparameterization for robustness
- Constraint synthesis for special cases (trigonometric aliasing prevention)

**Strategies**:

1. Solve linear parameters exactly at each iteration
2. Auto-generate parameter restrictions based on data symmetries
3. Transform models to be unit-invariant
4. Detect and warn about unrepresentable parameter values

**Math Libraries**: None mentioned - appears to be custom implementation

### 4.2 Rendering Approach (Speculative)

Based on performance and capabilities:

**Likely architecture**:

- WebGL shaders for high-performance curve rendering
- Adaptive sampling based on curvature and zoom level
- Tile-based rendering for large graphs
- Point and line primitive batching
- Custom text rendering for labels and coordinates

**Evidence**:

- Handles thousands of points smoothly
- Real-time updates during slider manipulation
- High-quality screenshots with anti-aliasing
- Projector mode suggests resolution-independent rendering

### 4.3 Expression Evaluation

**Capabilities**:

- Lazy evaluation (only computes visible regions)
- Incremental updates (re-evaluates only changed dependencies)
- List comprehensions and interval arithmetic
- Implicit function plotting (solves for one variable)
- Parametric and polar coordinates

**No details** on actual implementation available.

---

## 5. Desmos vs. SignalShow Domain Comparison

### 5.1 Mathematical Focus

| Aspect              | Desmos                                      | SignalShow                                |
| ------------------- | ------------------------------------------- | ----------------------------------------- |
| **Primary Domain**  | 2D/3D Cartesian geometry                    | 1D time-series signals                    |
| **Input Type**      | Symbolic equations (LaTeX)                  | Numeric data & operations                 |
| **Visualization**   | Curves, surfaces, inequalities              | Waveforms, spectra, filters               |
| **Interactivity**   | Sliders, drag points, trace                 | Play audio, scrub time, adjust parameters |
| **Core Operations** | Function evaluation, derivatives, integrals | FFT, filtering, convolution, modulation   |

### 5.2 Feature Overlap

**Shared Concepts**:

- ✅ Real-time plotting of mathematical functions
- ✅ Interactive parameter sliders
- ✅ Pan/zoom navigation
- ✅ Export to image
- ✅ Expression list / library organization

**Desmos-specific**:

- LaTeX equation input
- Symbolic manipulation
- 3D surface plotting
- Geometric constructions
- Regression fitting

**SignalShow-specific**:

- Audio playback / sonification
- Frequency domain analysis (FFT)
- Filter design and application
- Spectrogram visualization
- Signal generation (waveforms, noise)
- Time-domain transformations

### 5.3 UX Philosophy Similarities

Both Desmos and SignalShow share:

- **Exploratory learning**: Encourage experimentation through immediate feedback
- **Minimal barrier to entry**: Simple initial interactions, advanced features discoverable
- **Direct manipulation**: Drag, click, and adjust rather than typing commands
- **Visual primacy**: Graph-centric rather than code/REPL-centric
- **Progressively complex**: Support both beginners and advanced users

---

## 6. Feasibility Analysis

### 6.1 Can SignalShow Be Built "On Top of" Desmos?

**Answer: No, not in a meaningful technical sense.**

**What you COULD do** (not recommended):

- Embed Desmos calculator in SignalShow UI
- Use Desmos to plot certain mathematical functions
- Use API to programmatically control Desmos graphs

**Why this is problematic**:

1. **Wrong tool**: Desmos is for 2D/3D function plotting, not signal waveforms or spectrograms
2. **No signal processing**: Desmos has no FFT, filters, convolution, audio I/O
3. **Dependency burden**: Requires internet connection (or self-hosted bundle), adds load time
4. **Black box**: Cannot customize rendering or computation for signal-specific needs
5. **Licensing**: API key requirement, terms of service constraints
6. **Overhead**: Embedding full calculator for just plotting is inefficient

### 6.2 Can We Reuse Desmos Code/Libraries?

**Answer: No, the code is not available.**

**What IS available**:

- ❌ Rendering engine source code
- ❌ Math computation libraries
- ❌ Expression parser/evaluator
- ❌ Plotting algorithms
- ✅ MathQuill (separate open-source project, can use independently)
- ✅ API documentation and examples (learning resource only)

**What you CANNOT extract**:

- Even with self-hosting access, you receive **compiled/minified bundles**, not readable source
- No way to isolate and reuse specific components (e.g., "just the graphing engine")

### 6.3 What CAN Be Learned from Desmos?

**Design Patterns**:

- Expression list sidebar organization
- Slider-based parameter control
- Pan/zoom graph paper interaction model
- Color coding and visual hierarchy
- Keyboard shortcuts and accessibility features
- Screenshot/export functionality

**UX Principles**:

- Immediate visual feedback on all changes
- Forgiving error handling (show graph if possible, error message if not)
- Smart defaults with progressive disclosure
- Breadth of examples to inspire exploration

**Technical Inspiration** (implement independently):

- WebGL-based high-performance plotting
- Adaptive sampling for smooth curves
- Lazy evaluation for large datasets
- Client-side computation (no server required)

---

## 7. Alternative Architecture for SignalShow

### 7.1 Recommended Technology Stack

**Instead of Desmos**, build SignalShow with:

| Component             | Technology                                                                        | Why                                                    |
| --------------------- | --------------------------------------------------------------------------------- | ------------------------------------------------------ |
| **Signal Processing** | Web Audio API                                                                     | Native browser support for audio, FFT via AnalyserNode |
| **Advanced DSP**      | [dsp.js](https://github.com/corbanbrook/dsp.js/) or custom                        | FFT, filters, windowing functions                      |
| **Plotting**          | [Plot.ly](https://plotly.com/javascript/) or [Chart.js](https://www.chartjs.org/) | High-performance 2D plotting, zoom/pan, export         |
| **WebGL Rendering**   | [Three.js](https://threejs.org/) or raw WebGL                                     | Custom spectrogram/waterfall visualizations            |
| **Math Rendering**    | [MathJax](https://www.mathjax.org/) or [KaTeX](https://katex.org/)                | LaTeX equation display (if needed)                     |
| **UI Framework**      | React/Vue/Svelte                                                                  | Component-based interactive UI                         |
| **State Management**  | Redux/Zustand/Pinia                                                               | Complex signal chain and parameter management          |
| **File I/O**          | File System Access API                                                            | Load/save audio files, export data                     |

### 7.2 Core Modules to Implement

1. **Signal Generator**

   - Waveform synthesis (sine, square, sawtooth, triangle, noise)
   - Modulation (AM, FM)
   - Audio file import

2. **Signal Processor**

   - FFT/IFFT for frequency analysis
   - Filter design (lowpass, highpass, bandpass, notch)
   - Convolution and correlation
   - Windowing functions

3. **Visualizer**

   - Time-domain waveform plot
   - Frequency-domain spectrum plot
   - Spectrogram/waterfall display
   - Filter response plots (magnitude, phase)

4. **Audio Engine**

   - Playback of generated/processed signals
   - Real-time parameter adjustment during playback
   - Audio output routing

5. **Expression System**
   - Library of saved signals/operations
   - Parameter sliders and controls
   - Operation chaining (signal flow graph)

### 7.3 Learning from Desmos UX

**Adopt these patterns**:

1. **Expression List Sidebar**

   - Each signal/operation as a card
   - Show/hide, rename, delete, reorder
   - Color-coded for visual organization

2. **Immediate Feedback**

   - Plot updates instantly on parameter change
   - Audio preview on hover/click
   - Visual indication of valid/invalid operations

3. **Sliders for Parameters**

   - Frequency, amplitude, filter cutoff, etc.
   - Animate parameter sweeps
   - Logarithmic scales where appropriate

4. **Graph Paper Interaction**

   - Pan/zoom with mouse/touch
   - Cursor readout showing exact values
   - Auto-fit to data range

5. **Discoverability**
   - Guided examples for common tasks
   - Tooltips and help links
   - Progressive complexity (hide advanced features initially)

---

## 8. Comparison with Other Plotting Libraries

### 8.1 Desmos vs. Alternatives for SignalShow

| Library          | Pros for SignalShow                                 | Cons for SignalShow                                   |
| ---------------- | --------------------------------------------------- | ----------------------------------------------------- |
| **Desmos API**   | Beautiful, well-tested, accessible                  | No signal processing, wrong domain, black box         |
| **Plot.ly**      | Interactive zoom/pan, export, extensive chart types | General-purpose (not signal-optimized), larger bundle |
| **Chart.js**     | Lightweight, fast, simple API                       | Limited interactivity, no WebGL                       |
| **D3.js**        | Ultimate flexibility, full control                  | Steep learning curve, verbose code                    |
| **uPlot**        | Ultra-fast for time-series, tiny bundle             | Limited chart types, less polish                      |
| **Custom WebGL** | Maximum performance, tailored to signals            | High development effort, must handle all edge cases   |

**Recommendation**: Start with **Plot.ly** or **uPlot** for rapid prototyping, migrate to **custom WebGL** for spectrogram/waterfall displays if needed.

### 8.2 Feature Matrix

| Feature              | Desmos       | Plot.ly   | Custom WebGL             |
| -------------------- | ------------ | --------- | ------------------------ |
| 2D function plots    | ⭐⭐⭐       | ⭐⭐⭐    | ⭐⭐ (effort)            |
| Time-series          | ⭐           | ⭐⭐⭐    | ⭐⭐⭐                   |
| Spectrograms         | ❌           | ⭐⭐      | ⭐⭐⭐                   |
| Interactive zoom/pan | ⭐⭐⭐       | ⭐⭐⭐    | ⭐⭐ (effort)            |
| Real-time updates    | ⭐⭐⭐       | ⭐⭐      | ⭐⭐⭐                   |
| Export images        | ⭐⭐⭐       | ⭐⭐⭐    | ⭐⭐ (effort)            |
| Accessibility        | ⭐⭐⭐       | ⭐⭐      | ⭐ (effort)              |
| Customization        | ⭐ (limited) | ⭐⭐      | ⭐⭐⭐                   |
| Bundle size          | 🔴 Large     | 🟠 Medium | 🟢 Small                 |
| Signal processing    | ❌           | ❌        | ✅ (integrate Web Audio) |

---

## 9. Actionable Recommendations

### 9.1 Short-Term (Prototype Phase)

1. ✅ **Study Desmos UX** - Use it extensively, note interaction patterns, screenshot UI flows
2. ✅ **Use MathQuill** - If LaTeX equation input is needed (e.g., for custom signal functions)
3. ✅ **Prototype with Plot.ly** - Fastest path to interactive signal plots
4. ✅ **Integrate Web Audio API** - Build FFT and basic signal processing
5. ❌ **Do NOT embed Desmos** - It's the wrong tool and adds unnecessary complexity

### 9.2 Medium-Term (MVP Development)

1. **Build custom signal plotting** - Transition from Plot.ly to WebGL for performance
2. **Implement DSP library** - FFT, filters, convolution (dsp.js or custom)
3. **Create expression/operation system** - Like Desmos's expression list, but for signals
4. **Add audio playback** - Real-time signal sonification
5. **Design file format** - Save/load signal projects (similar to Desmos's `getState()`)

### 9.3 Long-Term (Production)

1. **Accessibility parity** - Keyboard navigation, screen reader support, Braille (study Desmos docs)
2. **Educational content** - Gallery of examples, interactive tutorials
3. **API for embedding** - Allow others to embed SignalShow like Desmos API
4. **Mobile optimization** - Touch-friendly UI, responsive layout
5. **Collaboration features** - Share signal projects via URL (like Desmos graph links)

---

## 10. Conclusion

### 10.1 Key Takeaways

**On Building with Desmos**:

- ❌ Cannot build SignalShow "on top of" Desmos in any meaningful way
- ❌ Cannot reuse Desmos code (it's proprietary and closed-source)
- ✅ CAN learn from Desmos's exceptional UX design
- ✅ CAN embed Desmos for specific 2D math visualization needs (if appropriate)

**On Independent Implementation**:

- ✅ Modern web platform provides all needed primitives (Web Audio, WebGL, Canvas)
- ✅ Open-source libraries exist for plotting, DSP, and math rendering
- ✅ SignalShow's domain (1D signals) is simpler than Desmos's (2D/3D functions)
- ✅ Full control over features, performance, and IP

### 10.2 Strategic Direction

**Recommendation**: Build SignalShow as an **independent web application** inspired by Desmos's design philosophy but implemented from scratch using modern web technologies.

**Core Inspiration from Desmos**:

1. **UX Model**: Expression list + graph paper + sliders
2. **Interaction Patterns**: Direct manipulation, immediate feedback, forgiving errors
3. **Accessibility**: Comprehensive keyboard navigation and screen reader support
4. **Simplicity**: Hide complexity, reveal advanced features progressively

**Technical Independence**:

- Custom WebGL rendering for signal-specific visualizations
- Web Audio API integration for real-time audio processing
- Modular DSP library (FFT, filters, analysis functions)
- File-based architecture for saving/loading projects

**Differentiation from Desmos**:

- Domain expertise in signal processing (time/frequency analysis)
- Audio playback and sonification capabilities
- Real-time parameter manipulation during playback
- Signal flow graph construction (like audio DAWs)

### 10.3 Next Steps

1. **Conduct deeper UX research** on Desmos:

   - Map all interaction patterns
   - Catalog keyboard shortcuts
   - Analyze error messages and help text
   - Study accessibility features

2. **Prototype signal plotting**:

   - Implement basic waveform display with Plot.ly
   - Add zoom/pan interaction
   - Test performance with long signals (1M+ samples)

3. **Build minimal DSP pipeline**:

   - Signal generator → FFT → Plot
   - Measure latency and responsiveness
   - Validate Web Audio API integration

4. **Design SignalShow's expression system**:

   - How to represent signal operations in a list
   - Parameter controls for each operation
   - Visual signal flow representation

5. **Create design mockups**:
   - Sketch UI layouts inspired by Desmos
   - Adapt patterns to signal processing domain
   - User test with target audience

---

## 11. References

### 11.1 Desmos Resources

- **Main Site**: https://www.desmos.com/
- **Graphing Calculator**: https://www.desmos.com/calculator
- **API Documentation**: https://www.desmos.com/api/v1.9/docs/index.html
- **Engineering Blog**: https://engineering.desmos.com/
- **Des-Blog**: https://blog.desmos.com/
- **GitHub**: https://github.com/desmos
- **Help Center**: https://help.desmos.com/
- **API Examples**: https://www.desmos.com/api/v1.9/docs/examples/

### 11.2 Relevant Technologies

- **MathQuill**: http://mathquill.com/ (LaTeX editor used by Desmos)
- **Web Audio API**: https://developer.mozilla.org/en-US/docs/Web/API/Web_Audio_API
- **WebGL**: https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API
- **Plot.ly**: https://plotly.com/javascript/
- **Chart.js**: https://www.chartjs.org/
- **dsp.js**: https://github.com/corbanbrook/dsp.js/

### 11.3 Academic Papers Referenced by Desmos

- Variable Projection: Golub & LeVeque (1979), Ruhe & Wedin (1980)
- Levenberg-Marquardt: Marquardt (1963)
- See: https://engineering.desmos.com/articles/regressions-improvements/

---

**Document Version**: 1.0  
**Date**: October 26, 2025  
**Author**: Research conducted for SignalShow modernization project
