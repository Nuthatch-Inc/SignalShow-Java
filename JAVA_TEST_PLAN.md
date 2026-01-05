# Java Test Suite Plan

## Overview

This document outlines a comprehensive test plan for the SignalShow Java implementation, designed to:

1. Mirror the JavaScript test coverage in `nuthatch-desktop`
2. Verify Java implementations match JavaScript implementations
3. Reference Chapter 8 textbook definitions when discrepancies are found

## Reference: JavaScript Test Files

| JavaScript Test File      | Lines | Coverage                                                                   |
| ------------------------- | ----- | -------------------------------------------------------------------------- |
| `correlation-ops.test.js` | 485   | Correlation, convolution, autocorrelation, FFT vs time-domain verification |
| `operations.test.js`      | 881   | All unary and binary signal operations                                     |
| `generators.test.js`      | 2100+ | All function generators (sine, cosine, gaussian, etc.)                     |
| `Signal1D.test.js`        | ~200  | Signal1D class functionality                                               |
| `Signal2D.test.js`        | ~200  | Signal2D class functionality                                               |

## Current Java Test Status

### ✅ Completed (172 tests total - ALL PASSING)

- **Operations2DTest.java** (26 tests) - 2D convolution, correlation, autocorrelation

  - Delta identity properties
  - Commutativity (convolution) vs non-commutativity (correlation)
  - Conjugation properties
  - Hermitian symmetry
  - Energy conservation

- **Operations1DTest.java** (~35 tests) - 1D convolution, correlation, autocorrelation, FFT

  - Delta identity, commutativity, shift property
  - Time-domain matching for convolution/correlation
  - FFT round-trip identity, linearity, even/odd symmetry
  - Autocorrelation Hermitian property, energy at origin

- **Generators1DTest.java** (~38 tests) - All 1D function generators

  - Sine, cosine, rectangle, triangle, delta, step
  - Gaussian, sinc, Lorentz, chirp, comb
  - Bessel J0, window functions (Hamming, Hanning, Welch)
  - Generator property tests

- **UnaryOperations1DTest.java** (~38 tests) - Unary signal operations

  - Magnitude (non-negative, Pythagorean theorem)
  - Phase (range [-π, π], special angles)
  - Conjugate (involution, complex)
  - Negate, scale, reverse, normalize
  - Real/imaginary extraction, absolute value, squared magnitude
  - FFT property tests

- **BinaryOperations1DTest.java** (~29 tests) - Binary signal operations

  - Addition (commutative, associative, identity)
  - Subtraction (non-commutative, negation)
  - Multiplication (commutative, associative, distributive)
  - Division (inverse of multiplication)
  - Complex arithmetic for all operations

- **SignalTestUtils.java** - Shared test utilities
  - Signal creation: delta, rect, sine, cosine, gaussian, triangle
  - FFT operations, origin indexing, complex arithmetic helpers
  - Assertion helpers for signal comparison

### ℹ️ Not Needed (Phase 5)

Signal1DTest and Signal2DTest are not needed because:

- Function1D/Function2D classes require Core initialization (GUI)
- Current test architecture uses raw arrays + Transforms directly (no GUI dependencies)
- All mathematical behavior is well tested through existing tests

---

## Phase 1: Core 1D Operations Tests

### File: `Operations1DTest.java`

Port from `correlation-ops.test.js` (485 lines):

#### 1.1 Convolution Tests (`ConvolveOp`)

```java
// Convolution definition: f * g = IFFT(FFT(f) · FFT(g))
- testConvolutionWithDelta()           // δ * f = f (identity)
- testConvolutionIsCommutative()       // f * g = g * f
- testConvolutionWithShiftedDelta()    // δ(x-a) * f(x) = f(x-a)
- testConvolutionVsTimeDomain()        // FFT method matches direct sum
- testConvolutionPreservesEnergy()     // Parseval's theorem
- testConvolutionOfTwoRects()          // rect * rect = triangle (approximate)
- testConvolutionOfGaussians()         // Gaussian * Gaussian = wider Gaussian
```

#### 1.2 Correlation Tests (`CorrelateOp`)

```java
// Correlation definition: f ⋆ m = IFFT(FFT(f) · conj(FFT(m)))
- testCorrelationWithDelta()           // f ⋆ δ = f
- testCorrelationIsNotCommutative()    // f ⋆ g ≠ g ⋆ f (in general)
- testCorrelationConjugatesReference() // Verify reference is conjugated
- testCorrelationPeakAtShift()         // Shifted signal correlates with peak at shift
- testCorrelationVsTimeDomain()        // FFT method matches direct sum
- testCorrelationVsJavaReference()     // Match Java reference values from JS tests
```

#### 1.3 Autocorrelation Tests (`AutoCorrelateOp`)

```java
// Autocorrelation definition: IFFT(|FFT(f)|²)
- testAutocorrelationEqualsCorrelationWithSelf() // f ⋆ f via autocorrelate = f ⋆ f via correlate
- testAutocorrelationIsEven()                    // r(-τ) = r(τ)
- testAutocorrelationPeakAtZero()                // Maximum at τ=0
- testAutocorrelationOfDelta()                   // δ ⋆ δ = δ
- testAutocorrelationOfSine()                    // Produces cosine envelope
- testAutocorrelationOfRectangle()               // Triangle shape
```

#### 1.4 FFT Round-Trip Tests

```java
- testFFTFollowedByIFFT()              // IFFT(FFT(f)) = f
- testIFFTFollowedByFFT()              // FFT(IFFT(f)) = f
- testFFTParsevalsTheorem()            // Energy in time = energy in frequency
- testFFTLinearity()                   // FFT(af + bg) = a·FFT(f) + b·FFT(g)
- testFFTShiftTheorem()                // Time shift → phase shift
```

---

## Phase 2: Unary Operations Tests

### File: `UnaryOperations1DTest.java`

Port from `operations.test.js` unary operations:

#### 2.1 Real/Imaginary Operations

```java
- testRealOfRealSignal()               // Real(f) = f when f is real
- testRealOfComplexSignal()            // Real(a + bi) = a
- testImagOfRealSignal()               // Imag(f) = 0 when f is real
- testImagOfComplexSignal()            // Imag(a + bi) = b
```

#### 2.2 Magnitude and Phase

```java
// MagnitudeOp
- testMagnitudeOfRealSignal()          // |f| = |f| (absolute value)
- testMagnitudeOfComplexSignal()       // |a + bi| = sqrt(a² + b²)
- testMagnitudeIsNonNegative()         // |f| ≥ 0

// PhaseOp
- testPhaseOfRealPositive()            // phase(+x) = 0
- testPhaseOfRealNegative()            // phase(-x) = π
- testPhaseOfPureImaginary()           // phase(i) = π/2, phase(-i) = -π/2
- testPhaseRange()                     // -π < phase ≤ π

// SquaredMagnitudeOp
- testSquaredMagnitude()               // |f|² = real² + imag²
```

#### 2.3 Conjugate and Negate

```java
// ConjugateOp
- testConjugateOfRealSignal()          // conj(f) = f when f is real
- testConjugateOfComplexSignal()       // conj(a + bi) = a - bi
- testDoubleConjugate()                // conj(conj(f)) = f

// NegateOp
- testNegateSignal()                   // -f
- testDoubleNegate()                   // -(-f) = f
```

#### 2.4 Other Unary Operations

```java
// AbsoluteValueOp
- testAbsoluteValue()                  // |x| per sample

// ScaleOp
- testScaleSignal()                    // k * f
- testScaleByZero()                    // 0 * f = 0
- testScaleByOne()                     // 1 * f = f

// NormalizeOp
- testNormalizeSignal()                // Peak = 1

// ReverseOp1D
- testReverseSignal()                  // f(-x)
- testDoubleReverse()                  // reverse(reverse(f)) = f

// TranslateOp1D (Shift)
- testShiftSignal()                    // f(x - a)
- testShiftByZero()                    // No change
- testShiftWrapsAround()               // Circular shift

// ClipOp
- testClipSignal()                     // Clamp to range
- testClipWithMinMax()                 // min/max bounds

// ThresholdOp
- testThresholdSignal()                // Binary threshold
```

---

## Phase 3: Binary Operations Tests

### File: `BinaryOperations1DTest.java`

Port from `operations.test.js` binary operations:

```java
// PlusOp
- testAddSignals()                     // f + g
- testAddCommutative()                 // f + g = g + f
- testAddWithZero()                    // f + 0 = f

// MinusOp
- testSubtractSignals()                // f - g
- testSubtractNotCommutative()         // f - g ≠ g - f
- testSubtractSelf()                   // f - f = 0

// TimesOp
- testMultiplySignals()                // f · g (element-wise)
- testMultiplyCommutative()            // f · g = g · f
- testMultiplyWithZero()               // f · 0 = 0
- testMultiplyWithOne()                // f · 1 = f

// DivideOp
- testDivideSignals()                  // f / g (element-wise)
- testDivideByOne()                    // f / 1 = f
- testDivideBySelf()                   // f / f = 1 (where f ≠ 0)
```

---

## Phase 4: Function Generator Tests

### File: `Generators1DTest.java`

Port from `generators.test.js` (2100+ lines):

#### 4.1 Basic Waveforms

```java
// SineFunctionTerm1D
- testSineAtOrigin()                   // sin(0) = 0
- testSineAmplitude()                  // Peak = amplitude
- testSineFrequency()                  // Period = 1/frequency
- testSinePhase()                      // Phase offset works
- testSineSymmetry()                   // sin(-x) = -sin(x) (odd)

// CosineFunctionTerm1D
- testCosineAtOrigin()                 // cos(0) = amplitude
- testCosineAmplitude()
- testCosineFrequency()
- testCosinePhase()
- testCosineSymmetry()                 // cos(-x) = cos(x) (even)
```

#### 4.2 Pulse Functions

```java
// RectangleFunctionTerm1D
- testRectAtOrigin()                   // rect(0) = 1
- testRectWidth()                      // Correct width
- testRectEdges()                      // Sharp transitions
- testRectFourierPair()                // FFT(rect) ~ sinc

// TriangleFunctionTerm1D
- testTriangleAtOrigin()               // tri(0) = 1
- testTriangleWidth()
- testTriangleSymmetry()               // Even function
- testTriangleFourierPair()            // FFT(tri) ~ sinc²

// DeltaFunctionTerm1D
- testDeltaAtOrigin()                  // δ(0) = 1
- testDeltaOffOrigin()                 // δ(x≠0) = 0
- testDeltaShifted()                   // δ(x-a) at position a
- testDeltaFourierPair()               // FFT(δ) = 1 (uniform)

// StepFunctionTerm1D
- testStepAtOrigin()                   // u(0) = 0.5 or 1 (check convention)
- testStepNegative()                   // u(x<0) = 0
- testStepPositive()                   // u(x>0) = 1
```

#### 4.3 Special Functions

```java
// GaussianFunctionTerm1D
- testGaussianAtOrigin()               // Peak at center
- testGaussianWidth()                  // σ parameter
- testGaussianSymmetry()               // Even function
- testGaussianFourierPair()            // FFT(gaussian) = gaussian

// SincFunctionTerm1D
- testSincAtOrigin()                   // sinc(0) = 1
- testSincZeroCrossings()              // Zeros at integers
- testSincFourierPair()                // FFT(sinc) ~ rect

// BesselFunctionTerm1D
- testBesselJ0AtOrigin()               // J0(0) = 1
- testBesselZeroCrossings()

// LorentzianFunctionTerm1D
- testLorentzianAtOrigin()             // Peak at center
- testLorentzianWidth()                // Half-width parameter

// ChirpFunctionTerm1D
- testChirpStartFrequency()            // Initial frequency
- testChirpEndFrequency()              // Final frequency
- testChirpSweep()                     // Frequency increases/decreases
```

#### 4.4 Window Functions

```java
// HammingWindowFunctionTerm1D
- testHammingAtCenter()                // Peak value
- testHammingAtEdges()                 // Edge values (~0.08)
- testHammingSymmetry()                // Even function

// HanningWindowFunctionTerm1D
- testHanningAtCenter()
- testHanningAtEdges()                 // Goes to 0 at edges
- testHanningSymmetry()

// WelchWindowFunctionTerm1D, ParzenWindowFunctionTerm1D, etc.
```

#### 4.5 Noise Functions

```java
// GaussianNoiseFunctionTerm1D
- testGaussianNoiseMean()              // Mean ≈ 0
- testGaussianNoiseStdDev()            // σ matches parameter

// UniformNoiseFunctionTerm1D
- testUniformNoiseRange()              // Values in [min, max]
- testUniformNoiseMean()               // Mean ≈ (min+max)/2

// PoissonNoiseFunctionTerm1D
- testPoissonNoiseMean()               // Mean ≈ λ
```

---

## Phase 5: Signal Class Tests

### File: `Signal1DTest.java`

```java
- testSignalCreation()                 // Create with size
- testSignalFromArray()                // Create from double[]
- testSignalGetSet()                   // Get/set values
- testSignalSize()                     // Correct size
- testSignalIndexing()                 // Zero-centered indexing
- testSignalCopy()                     // Deep copy
```

### File: `Signal2DTest.java`

```java
- testSignal2DCreation()
- testSignal2DFromArray()
- testSignal2DDimensions()
- testSignal2DIndexing()               // Row-major order, zero-centered
```

---

## Phase 6: Cross-Validation Tests

### File: `JavaScriptMatchTest.java`

Generate reference values from JavaScript tests, verify Java produces identical results:

```java
// Use specific input signals and compare output arrays
- testConvolutionMatchesJavaScript()
- testCorrelationMatchesJavaScript()
- testFFTMatchesJavaScript()
- testGeneratorsMatchJavaScript()
```

### Tolerance Guidelines

- Floating point comparisons: `assertEquals(expected, actual, 1e-10)`
- FFT phase: may need larger tolerance due to floating point accumulation
- Noise functions: verify statistical properties, not exact values

---

## Implementation Order

1. **Phase 1**: Core 1D Operations (highest priority - validates math)
2. **Phase 2**: Unary Operations (builds on Phase 1)
3. **Phase 3**: Binary Operations (straightforward)
4. **Phase 4**: Generators (many tests, but important)
5. **Phase 5**: Signal Classes (infrastructure)
6. **Phase 6**: Cross-Validation (final verification)

---

## Test Infrastructure Needed

### Helper Methods

```java
public class SignalTestUtils {
    // Create delta function at specified position
    public static Signal1D createDelta(int size, int position);

    // Create signal from array
    public static Signal1D fromArray(double[] real);
    public static Signal1D fromArray(double[] real, double[] imag);

    // Compare signals with tolerance
    public static void assertSignalsEqual(Signal1D expected, Signal1D actual, double tolerance);

    // Check signal properties
    public static double maxAbsValue(Signal1D signal);
    public static double sumSquared(Signal1D signal);
    public static int findPeakIndex(Signal1D signal);
}
```

### Test Base Class

```java
public class SignalTestBase {
    protected static final double TOLERANCE = 1e-10;
    protected static final int DEFAULT_SIZE = 64;

    // Common setup/teardown
}
```

---

## Notes

### Mathematical Definitions (from Chapter 8)

| Operation       | Equation | Formula                               |
| --------------- | -------- | ------------------------------------- |
| Convolution     | Eq 8.82  | `f * h = IFFT(FFT(f) · FFT(h))`       |
| Correlation     | Eq 8.86  | `f ⋆ m = IFFT(FFT(f) · conj(FFT(m)))` |
| Autocorrelation | Eq 8.87  | `IFFT(\|FFT(f)\|²)`                   |

### Key Differences: Java vs JavaScript

| Aspect          | Java                      | JavaScript                |
| --------------- | ------------------------- | ------------------------- |
| Indexing        | Zero-centered default     | Zero-centered option      |
| FFT             | Custom implementation     | Custom implementation     |
| Complex numbers | Separate real/imag arrays | Separate real/imag arrays |

### Discrepancy Resolution Process

1. Run Java test with specific input
2. Run JavaScript test with same input
3. Compare outputs
4. If different:
   - Check mathematical definition in Chapter 8
   - Determine which implementation is correct
   - Fix the incorrect implementation
   - Document in this file

---

## Test Count Estimate

| Phase                       | Tests          |
| --------------------------- | -------------- |
| Phase 1: Core 1D Operations | ~35            |
| Phase 2: Unary Operations   | ~30            |
| Phase 3: Binary Operations  | ~15            |
| Phase 4: Generators         | ~60            |
| Phase 5: Signal Classes     | ~15            |
| Phase 6: Cross-Validation   | ~10            |
| **Total**                   | **~165 tests** |

Plus existing 2D tests: **26 tests**

**Grand Total: ~191 tests**
