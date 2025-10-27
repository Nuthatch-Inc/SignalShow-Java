# JavaScript DSP Libraries Research

**Purpose**: Evaluate JavaScript DSP libraries for v1.0 web deployment

---

## Executive Summary

JavaScript libraries are **10-100x slower** than Rust WASM but provide:
- 100% browser compatibility (no WASM required)
- Small bundle sizes (10-150KB total)
- Zero-installation fallback

**Strategy**: Use JavaScript for v1.0, add WASM in v1.5 for performance

---

## Recommended Libraries

### 1. fft.js (FFT/IFFT)

**Stats**:
- Version: 4.0.4
- License: MIT
- Stars: 315
- Downloads: ~30k/week
- Bundle: ~3KB gzipped

**Features**:
- Radix-4 FFT (optimized for power-of-4 sizes)
- Real FFT optimization (~25% faster for real signals)
- Inverse FFT
- **Limitation**: Requires power-of-2 sizes only

**Performance**:
- 4096 samples: ~15,676 ops/sec (~64μs per transform)
- 8192 samples: ~6,896 ops/sec (~145μs)
- **2-3x faster** than alternative JS FFT libraries

**Usage**:
```javascript
const FFT = require("fft.js");
const fft = new FFT(2048); // Power of 2 required
const output = fft.createComplexArray();
fft.realTransform(output, realInput); // 25% faster for real signals
```

**Recommendation**: ✅ Use for v1.0

---

### 2. dsp.js (Filters, Windows, Oscillators)

**Stats**:
- License: MIT
- Stars: 1.7k
- Bundle: ~50KB
- ⚠️ **Unmaintained since 2015**

**Features**:
- FIR/IIR filters (lowpass, highpass, bandpass)
- Window functions (Hamming, Hanning, Blackman)
- Oscillators (sine, square, sawtooth, triangle)
- Envelope generators (ADSR)
- Convolution

**Limitations**:
- No security updates since 2015
- Slower than modern implementations
- Limited documentation

**Usage**:
```javascript
const lowpass = new IIRFilter(DSP.LOWPASS, 1000, 44100, 2);
lowpass.process(buffer);
```

**Recommendation**: ⚠️ Use carefully, consider rewriting filters

---

### 3. math.js (General Math)

**Stats**:
- Version: 12.x
- License: Apache-2.0
- Stars: 14k
- Downloads: ~2M/week
- Bundle: ~150KB minified

**Features**:
- Complex numbers (native support)
- Matrices and linear algebra
- Statistics functions
- Expression parser
- Unit conversions

**Performance**:
- Slower than specialized libraries
- Large bundle size

**Usage**:
```javascript
const math = require('mathjs');
const complex = math.complex(2, 3); // 2 + 3i
const matrix = math.matrix([[1, 2], [3, 4]]);
```

**Recommendation**: ✅ Use for complex arithmetic, avoid for performance-critical ops

---

### 4. stdlib-js (Special Functions)

**Stats**:
- License: Apache-2.0
- Modular: ~1-5KB per function
- Active maintenance

**Features**:
- Special functions (erf, gamma, bessel)
- Statistical distributions
- Random number generators
- BLAS-level operations

**Performance**: Moderate (pure JavaScript)

**Usage**:
```javascript
const erf = require('@stdlib/math-base-special-erf');
const result = erf(1.0); // 0.8427...
```

**Recommendation**: ✅ Use for special functions in v1.0

---

## Performance Comparison

| Operation | JavaScript | Rust WASM | Julia | Notes |
|-----------|------------|-----------|-------|-------|
| FFT (4096) | ~64μs | ~5μs | <1μs | fft.js vs rustfft vs FFTW |
| Sine generation (4096) | ~500μs | ~50μs | <10μs | Math.sin() vs native |
| Convolution (1000) | ~50ms | ~1ms | <1ms | Direct O(N²) vs FFT-based |
| Filter (4096) | ~2ms | ~200μs | <50μs | IIR/FIR filtering |

**Conclusion**: JavaScript is 10-100x slower, but sufficient for teaching demos (4096 samples in <100ms total)

---

## Bundle Size Analysis

**Minimal v1.0 bundle**:
- fft.js: ~3KB gzipped
- Custom filters: ~5KB
- Custom generators: ~10KB
- math.js (complex): ~150KB
- **Total**: ~170KB gzipped

**Optimization**:
- Tree-shake math.js (use only complex module): ~20KB
- **Optimized total**: ~40KB gzipped

---

## Browser Compatibility

All libraries support:
- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
- Mobile browsers (iOS 14+, Android 10+)

**No WASM required** - pure JavaScript

---

## Implementation Strategy

### v1.0 (JavaScript)
**Use**:
- fft.js for FFT/IFFT
- Custom implementations for filters (simple FIR/IIR)
- math.js for complex arithmetic
- stdlib-js for special functions (erf, etc.)

**Avoid**:
- dsp.js (unmaintained)
- Heavy libraries (tone.js, wavesurfer.js) - overkill for DSP core

**Performance target**: <100ms for typical operations (4096 samples)

---

### v1.5 (+ Rust WASM)
**Upgrade**:
- rustfft for FFT (~10x faster)
- dasp for filters (~50x faster)
- Keep JavaScript as fallback

**Progressive enhancement**:
```javascript
const backend = window.WebAssembly
  ? new WasmBackend()
  : new JavaScriptBackend();
```

---

## Missing Features

**Not available in JavaScript** (require Julia/WASM):
- Bessel functions (J, Y, I, K) - use Julia SpecialFunctions.jl
- Wavelet transforms - use Julia Wavelets.jl
- Advanced filters (elliptic, Chebyshev) - use Julia DSP.jl or Rust dasp
- 2D FFT optimization - use Julia FFTW.jl

**Workarounds**:
- Implement basic Bessel J0/Y0 approximations (limited accuracy)
- Use FFT-based convolution for filtering
- Defer advanced features to v1.5/v2.0

---

## Code Examples

### FFT Example
```javascript
// Generate 4096-sample sine wave
const sampleRate = 8000;
const freq = 440; // A4 note
const duration = 0.512; // 4096 samples
const signal = new Float32Array(4096);

for (let i = 0; i < signal.length; i++) {
  signal[i] = Math.sin(2 * Math.PI * freq * i / sampleRate);
}

// Compute FFT
const FFT = require('fft.js');
const fft = new FFT(4096);
const out = fft.createComplexArray();
fft.realTransform(out, signal);
fft.completeSpectrum(out);

// Extract magnitude spectrum
const magnitude = new Float32Array(2048);
for (let i = 0; i < 2048; i++) {
  const real = out[i * 2];
  const imag = out[i * 2 + 1];
  magnitude[i] = Math.sqrt(real * real + imag * imag);
}

// Peak should be at bin 440 / (8000/4096) ≈ 225
```

### Custom Filter Example
```javascript
// Simple lowpass FIR filter (moving average)
function movingAverage(signal, windowSize) {
  const filtered = new Float32Array(signal.length);
  
  for (let i = 0; i < signal.length; i++) {
    let sum = 0;
    let count = 0;
    
    for (let j = Math.max(0, i - windowSize + 1); j <= i; j++) {
      sum += signal[j];
      count++;
    }
    
    filtered[i] = sum / count;
  }
  
  return filtered;
}
```

---

## Recommendations Summary

1. **Use fft.js** for FFT/IFFT in v1.0 (fastest, lightweight)
2. **Avoid dsp.js** (unmaintained) - implement filters manually
3. **Use math.js** selectively (complex numbers only, tree-shake)
4. **Use stdlib-js** for special functions (erf, gamma)
5. **Target <100ms** for operations on 4096 samples (acceptable for teaching)
6. **Plan WASM upgrade** in v1.5 for 10-100x speedup
7. **Fallback gracefully** - detect WASM support, use JS if unavailable

---

**Bottom line**: JavaScript is sufficient for v1.0 teaching demos. Performance limitations (~100ms FFT) are acceptable for interactive learning. Upgrade to WASM in v1.5 for production use.
