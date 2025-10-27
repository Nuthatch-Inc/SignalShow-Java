# Feature Mapping

**Purpose**: Map 80+ Java functions and 40+ operations to Julia/JavaScript implementations

---

## Implementation Strategy

**Three-tier backend**:
1. **JavaScript** (v1.0) - Pure browser, ~60% coverage
2. **Rust WASM** (v1.5) - Near-native speed, ~85% coverage
3. **Julia Server** (v2.0) - Complete DSP library, 100% coverage

**Priority**: v1.0 features marked **bold**

---

## 1D Function Generators

### Basic Waveforms (v1.0 JavaScript)

| Function | Parameters | Implementation | Notes |
|----------|------------|----------------|-------|
| **Sinc** | Amplitude, Center, Width | `A * sinc(π/width * (x - center))` | Cardinal sine |
| **Sine** | Amplitude, Center, Period, Phase | `A * sin(2π/width * (x - center) + φ)` | Basic sinusoid |
| **Cosine** | Amplitude, Center, Period, Phase | `A * cos(2π/width * (x - center) + φ)` | Basic cosinusoid |
| **Chirp** | Amplitude, Center, Width, Phase, Exp | `A * cos(π/width^n * (x - center)^n + φ)` | Frequency sweep |
| **Gaussian** | Amplitude, Width (σ), Center, Exp | `A * exp(-((x - center)/σ)^n)` | Bell curve |
| **Rectangle** | Amplitude, Width, Center | Custom rect function | Rectangular pulse |
| **Triangle** | Amplitude, Width, Center | `A * max(0, 1 - abs(x - center)/width)` | Triangular pulse |
| **Delta** | Amplitude, Center | Kronecker delta | Discrete impulse |
| **Step** | Amplitude, Center | Heaviside step | Step function |
| **Constant** | Amplitude | `fill(A, n)` | DC signal |
| **Zero** | None | `zeros(n)` | All zeros |
| **Square Wave** | Amplitude, Center, Period, Phase | `A * sign(cos(2π/width * (x - center) + φ))` | Periodic pulses |
| **Data** | Data array | Load from file/array | User-supplied |

**v1.5 additions**: Complex sinusoid, Line, Monomial, Lorentzian, Comb, Double slit, Besinc

**Backend**:
- JavaScript: Math.sin/cos, custom functions
- Julia: SpecialFunctions.jl (sinc, Bessel), DSP.jl

---

### Window Functions

| Function | Parameters | Implementation | Backend |
|----------|------------|----------------|---------|
| **Hamming** | Amplitude, Center, Width | `A * (0.54 + 0.46*cos(π(x-c)/w))` for \|x-c\|≤w | JavaScript → WASM |
| **Hanning** | Amplitude, Center, Width | `A * 0.5 * (1 + cos(π(x-c)/w))` for \|x-c\|≤w | JavaScript → WASM |
| Welch | Width, Center | Parabolic window | WASM |
| Parzen | Width, Center | Piecewise triangular | WASM |

**Backend**:
- JavaScript: Custom implementations
- Rust WASM: DSP library (v1.5)
- Julia: DSP.jl (v2.0)

---

### Noise Functions

| Function | Parameters | Distribution | Backend |
|----------|------------|--------------|---------|
| **Gaussian** | σ (std dev), μ (mean), seed | N~(μ, σ) | JavaScript (Box-Muller) |
| **Uniform** | μ (mean), Half-Width, seed | U~(μ-hw, μ+hw) | JavaScript (Math.random) |
| Poisson | λ (rate), seed | Poisson(λ) | JavaScript (Knuth) → Julia |
| Binomial | n (trials), p (prob), seed | Binomial(n, p) | JavaScript → Julia |
| Exponential | β (mean), seed | Exp(1/β) | JavaScript (inverse transform) |
| Rayleigh | σ (scale), seed | Rayleigh(σ) | Julia only |
| Salt & Pepper | density, seed | Impulse noise | JavaScript |

**Backend**:
- JavaScript: Box-Muller (Gaussian), inverse transform (Exponential)
- Julia: Distributions.jl (all distributions, v2.0)

---

### Special Functions

| Function | Parameters | Implementation | Backend |
|----------|------------|----------------|---------|
| Bessel J0 | Order, Amplitude, Width, Center | `besselj(order, x)` | Julia only (SpecialFunctions.jl) |
| Bessel Y0 | Order, Amplitude, Width, Center | `bessely(order, x)` | Julia only |
| Error | Amplitude, Center, Width | `erf(x)` | Julia / stdlib-js |
| Airy | Amplitude, Width, Center | `airy(x)` | Julia only |
| Legendre | Degree, Amplitude | `legendre(n, x)` | Julia only |
| Hermite | Degree, Amplitude | Custom polynomial | Julia only |

**Backend**:
- JavaScript: Limited (stdlib-js for erf)
- Julia: SpecialFunctions.jl (v2.0 required for advanced functions)

---

## 2D Function Generators

### Apertures & Optical Elements (v1.5+)

| Function | Parameters | Implementation | Backend |
|----------|------------|----------------|---------|
| Circle | Radius, Center | `r < radius ? 1 : 0` | JavaScript |
| Rectangle | Width, Height, Center | Rect comparison | JavaScript |
| Annulus | Inner/Outer Radius | `inner < r < outer ? 1 : 0` | JavaScript |
| Gaussian 2D | σx, σy, Center | `exp(-(x²/σx² + y²/σy²))` | JavaScript |
| Cassegrain | Primary/Secondary radii | Annulus + center block | JavaScript |
| Zernike | n, m coefficients | Zernike polynomials | Julia only (v2.0) |
| Airy Disk | Radius | `2*J1(x)/x` | Julia only (Bessel) |

**Backend**:
- JavaScript: Basic apertures (v1.5)
- Julia: Advanced optical functions (Images.jl, v2.0)

---

### Image/Data Functions

| Function | Parameters | Implementation | Backend |
|----------|------------|----------------|---------|
| **Data 2D** | Image array | Load from file | JavaScript (v1.0) |
| Checkerboard | Size, Frequency | Periodic pattern | JavaScript |
| Gaussian Noise 2D | σ, μ, seed | 2D randn | JavaScript |
| Sinusoid 2D | fx, fy, Phase | `sin(2π(fx*x + fy*y) + φ)` | JavaScript |

**Import formats**: PNG, JPEG → grayscale conversion (JavaScript, v1.0)

---

## Operations

### Transform Operations

| Operation | Parameters | Implementation | Backend |
|-----------|------------|----------------|---------|
| **FFT** | None | fft(signal) | **fft.js (v1.0)** → rustfft (v1.5) → FFTW.jl (v2.0) |
| **IFFT** | None | ifft(spectrum) | Same as FFT |
| **FFT2** | None | fft2(image) | Julia only (FFTW.jl, v2.0) |
| **IFFT2** | None | ifft2(spectrum) | Julia only (v2.0) |
| **DCT** | None | dct(signal) | WASM → Julia |
| **Hilbert** | None | Analytic signal | Julia only (DSP.jl, v2.0) |

**Performance**:
- JavaScript (fft.js): ~100ms for 4096 samples
- Rust WASM (rustfft): ~5ms for 4096 samples (v1.5)
- Julia (FFTW.jl): <1ms for 4096 samples (v2.0)

---

### Convolution/Correlation

| Operation | Parameters | Implementation | Backend |
|-----------|------------|----------------|---------|
| **Convolve** | Signal2 | Direct convolution | JavaScript (v1.0, slow) → WASM (v1.5) |
| **Correlate** | Signal2 | Cross-correlation | JavaScript → WASM |
| Autocorrelate | None | Self-correlation | WASM |
| Convolve 2D | Image2 | 2D convolution | Julia only (Images.jl, v2.0) |

**Performance**:
- JavaScript: O(N²) direct (slow for N>1000)
- WASM: FFT-based O(N log N) (v1.5)
- Julia: FFTW-optimized (v2.0)

---

### Arithmetic & Manipulation

| Operation | Parameters | Implementation | Backend |
|-----------|------------|----------------|---------|
| **Add** | Signal2, scalar | `signal1 + signal2` or `signal + scalar` | **JavaScript (v1.0)** |
| **Subtract** | Signal2, scalar | `signal1 - signal2` | JavaScript |
| **Multiply** | Signal2, scalar | `signal1 * signal2` | JavaScript |
| **Divide** | Signal2, scalar | `signal1 / signal2` | JavaScript |
| **Normalize** | Method (peak, RMS, energy) | Scale to target amplitude | JavaScript |
| **Scale** | Factor | `signal * factor` | JavaScript |
| **Shift** | Δx (time/space shift) | Index offset | JavaScript |
| **Reverse** | None | Flip array | JavaScript |
| **Absolute** | None | `abs(signal)` | JavaScript |
| **Square** | None | `signal^2` | JavaScript |
| **Sqrt** | None | `sqrt(signal)` | JavaScript |
| **Log** | Base | `log(signal)` | JavaScript |
| **Exp** | None | `exp(signal)` | JavaScript |

**All arithmetic operations**: JavaScript (v1.0), vectorized in Julia (v2.0)

---

### Complex Number Operations

| Operation | Parameters | Implementation | Backend |
|-----------|------------|----------------|---------|
| **Real** | None | Extract real part | JavaScript (v1.0) |
| **Imaginary** | None | Extract imag part | JavaScript |
| **Magnitude** | None | `sqrt(re² + im²)` | JavaScript |
| **Phase** | None | `atan2(im, re)` | JavaScript |
| **Conjugate** | None | Flip imaginary sign | JavaScript |
| **Complex Multiply** | Signal2 | `(a+bi)(c+di)` | JavaScript |

**Implementation**: 
- JavaScript: Separate real/imag arrays or complex library
- Julia: Native complex number support (v2.0)

---

### Spatial Operations

| Operation | Parameters | Implementation | Backend |
|-----------|------------|----------------|---------|
| Rotate 2D | Angle (degrees) | 2D rotation matrix | Julia (Images.jl, v2.0) |
| Scale 2D | Factor | Interpolation | Julia (v2.0) |
| Crop 2D | x, y, w, h | Extract region | JavaScript (v1.5) |
| Pad 2D | Width, Height | Zero-padding | JavaScript (v1.5) |
| Transpose | None | Swap x/y | JavaScript (v1.5) |
| Flip H/V | Axis | Mirror | JavaScript (v1.5) |

---

### Calculus Operations

| Operation | Parameters | Implementation | Backend |
|-----------|------------|----------------|---------|
| **Derivative** | None | Finite differences | JavaScript (v1.0) |
| **Integral** | None | Cumulative sum | JavaScript |
| Gradient 2D | None | Sobel/Prewitt | WASM → Julia |
| Laplacian 2D | None | Second derivative | Julia (v2.0) |

---

### Filtering & Processing

| Operation | Parameters | Implementation | Backend |
|-----------|------------|----------------|---------|
| Lowpass | Cutoff, Order | Butterworth filter | WASM (DSP lib, v1.5) → Julia (DSP.jl, v2.0) |
| Highpass | Cutoff, Order | Butterworth filter | WASM → Julia |
| Bandpass | Low/High Cutoff, Order | Butterworth filter | WASM → Julia |
| Median | Window size | Median filter | WASM → Julia |
| Moving Average | Window size | Convolution | JavaScript (v1.5) |

**Backend**:
- JavaScript: Simple FIR filters (v1.5)
- Rust WASM: dasp library (v1.5)
- Julia: DSP.jl (Butterworth, Chebyshev, elliptic filters, v2.0)

---

### Holography Operations (v2.0)

| Operation | Parameters | Implementation | Backend |
|-----------|------------|----------------|---------|
| Fresnel Propagate | Distance, Wavelength | Fresnel transform | Julia only |
| Fraunhofer Propagate | Distance, Wavelength | FFT-based | Julia only |
| Phase Unwrap | None | 2D phase unwrapping | Julia only |
| Reconstruct Hologram | Reference beam | Complex division | Julia only |

**Requires**: Julia (v2.0), Images.jl, FFTW.jl

---

## Implementation Libraries

### JavaScript (v1.0)

**Math**:
- Native: `Math.sin/cos/exp/log/sqrt/abs`
- math.js: General math operations
- stdlib-js: Special functions (erf, etc.)

**DSP**:
- **fft.js** (192k downloads/week) - FFT/IFFT
- Custom: Convolution, filters, windows

**Performance**: 5-10% of native, sufficient for demos

---

### Rust WASM (v1.5)

**DSP**:
- **rustfft** - Optimized FFT (60-95% native speed)
- **dasp** - Filters, convolution, interpolation
- **ndarray** - Array operations

**Build**: wasm-pack, ~150KB bundle

---

### Julia (v2.0 Desktop)

**Core**:
- **FFTW.jl** - Fastest FFT (100% native)
- **DSP.jl** - Filters, windows, spectral analysis
- **SpecialFunctions.jl** - Bessel, erf, gamma
- **Distributions.jl** - Noise generation
- **Images.jl** - 2D operations

**Server**: HTTP.jl (localhost:8080)

---

## Priority Roadmap

### v1.0 Essential Features (JavaScript)

**1D Generators** (15 functions):
- Sine, Cosine, Chirp, Sinc, Gaussian
- Rectangle, Triangle, Delta, Step
- Constant, Zero, Square Wave
- Gaussian Noise, Uniform Noise, Data

**Operations** (20 operations):
- FFT, IFFT
- Add, Subtract, Multiply, Divide, Normalize
- Real, Imag, Magnitude, Phase
- Derivative, Integral
- Shift, Reverse, Scale
- Absolute, Square, Sqrt, Log, Exp

**2D**:
- Data 2D (load images)

**Performance target**: <100ms FFT (4096 samples), <50ms arithmetic ops

---

### v1.5 Expansion Features (+ WASM)

**1D Generators** (10 additions):
- Complex Sinusoid, Besinc, Lorentzian
- Poisson/Binomial/Exponential Noise
- Line, Monomial, Comb

**Operations** (8 additions):
- FFT-based convolution (fast)
- Lowpass/Highpass/Bandpass filters
- Moving Average, Median
- Crop, Pad, Flip

**2D Generators** (8 additions):
- Circle, Rectangle, Annulus, Gaussian 2D
- Cassegrain, Checkerboard
- Gaussian Noise 2D, Sinusoid 2D

**Performance target**: <5ms FFT (4096 samples), FFT-based convolution

---

### v2.0 Complete Features (+ Julia)

**1D Generators** (All 80+ functions):
- All Bessel functions (J, Y, I, K)
- Airy, Legendre, Hermite
- Rayleigh/Lorentz noise
- All window functions

**Operations** (All 40+ operations):
- FFT2, IFFT2, DCT, Hilbert
- 2D convolution, correlation
- Butterworth/Chebyshev/Elliptic filters
- Gradient, Laplacian
- Holography operations

**2D Generators** (All optical elements):
- Zernike polynomials
- Airy Disk
- Advanced apertures

**Performance target**: <1ms FFT (4096 samples), real-time 2D processing

---

## Backend Decision Tree

```
User Action → Check capability
├─ Basic waveform (sine, gauss, etc.)
│  └─ JavaScript ✓ (v1.0)
├─ FFT < 4096 samples
│  └─ JavaScript ✓ (v1.0, ~100ms)
├─ FFT > 4096 samples
│  └─ WASM ✓ (v1.5, ~5ms) OR Julia (v2.0, <1ms)
├─ Filtering (lowpass, highpass)
│  └─ WASM ✓ (v1.5) OR Julia (v2.0)
├─ Bessel functions
│  └─ Julia ONLY (v2.0)
├─ 2D FFT, holography
│  └─ Julia ONLY (v2.0)
```

**Progressive enhancement**: Start JavaScript → Upgrade to WASM → Optionally use Julia

---

**Implementation priority**: Focus on v1.0 features (35 total: 15 generators + 20 operations) for production release, defer advanced features to v1.5/v2.0.
