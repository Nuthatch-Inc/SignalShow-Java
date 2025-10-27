# Fractional Fourier Transform Implementation Plan

## Overview

Implement Fractional Fourier Transform (FrFT) to create 2D representations that interpolate between space and frequency domains for advanced signal analysis and educational demonstrations.

## Feature Specification (from FEAT-006)

### Fractional Fourier Transform

The Fractional Fourier Transform is a generalization of the classical Fourier Transform that rotates signals in the time-frequency plane by arbitrary angles.

**Mathematical Definition**:
The α-order Fractional Fourier Transform is defined as:

```
F^α{f(x)} = ∫ K_α(x, u) f(u) du
```

Where the kernel K_α depends on the fractional order α:

- α = 0: Identity (space domain)
- α = 0.5 (90°): Classical Fourier Transform (frequency domain)
- α = 0.25, 0.75, etc.: Intermediate rotations

### Input/Output

- **Input**: 1D function (Function1D) - can be real or complex
- **Output**: 2D function (Function2D) showing the signal at multiple rotation angles
  - X dimension: Spatial coordinate x
  - Y dimension: Fractional transform order α (angles from 0° to 180°)
  - Each row: FrFT at a specific angle

**Alternative Interpretation**:
Display both space and frequency domains simultaneously along orthogonal axes, with intermediate FrFT angles interpolating between them.

### Use Cases

- Advanced signal analysis and time-frequency methods
- Educational demonstrations of space-frequency duality
- Research applications in optics and quantum mechanics
- Visualizing the continuous transition from space to frequency

## Mathematical Background

### Fractional Fourier Transform Theory

The FrFT can be understood as a rotation in the time-frequency plane by angle φ = απ/2.

**Special Cases**:

- α = 0: F⁰{f(x)} = f(x) (identity)
- α = 1: F¹{f(x)} = F{f(x)} (standard Fourier transform)
- α = 2: F²{f(x)} = f(-x) (flip)
- α = 3: F³{f(x)} = F⁻¹{f(x)} (inverse Fourier)
- α = 4: F⁴{f(x)} = f(x) (back to identity)

**Additivity Property**:
F^α{F^β{f}} = F^(α+β){f}

**Kernel for α ≠ integer**:

```
K_α(x, u) = A_α exp[iπ(x² cot φ - 2xu csc φ + u² cot φ)]
```

Where:

- φ = απ/2 (rotation angle)
- A_α = √[(1 - i cot φ) / 2π]

### Discrete Fractional Fourier Transform (DFrFT)

For a discrete signal of length N, several algorithms exist:

1. **Sampling-based method** (Ozaktas et al.)

   - Most accurate for well-sampled signals
   - Requires interpolation and resampling
   - Computationally intensive

2. **Eigendecomposition method** (Dickinson & Steiglitz)

   - Based on eigenvectors of DFT matrix
   - More stable for discrete signals
   - Easier to implement

3. **Chirp decomposition method**
   - Express FrFT as chirp multiplications and convolutions
   - Fast computation using FFT
   - Good for real-time applications

**Eigendecomposition Approach** (Recommended for educational tool):

- Compute eigenvectors of DFT matrix (Hermite functions for continuous case)
- FrFT is DFT matrix raised to power α
- F^α = V Λ^α V^†
  - V: eigenvector matrix
  - Λ: diagonal eigenvalue matrix
  - α: fractional power

### Key Properties

1. **Unitary Transform**: Preserves energy
2. **Rotation Interpretation**: Rotates Wigner distribution by angle φ
3. **Periodicity**: F^(α+4) = F^α
4. **Reversibility**: F^α{F^(-α){f}} = f
5. **Dimensionality**: For display purposes, creates 2D output from 1D input

## Existing Infrastructure Analysis

### ✅ Available Components

1. **FFT Operations** (`signals.operation.Transforms`)

   - `computeFFT1D()` - For α = 1 case (standard FT)
   - Can be used to verify FrFT at α = 1
   - Normalization options

2. **1D Function Architecture** (`signals.core.Function1D`)

   - `getReal()`, `getImaginary()` - Access signal data
   - Complex signal support

3. **2D Function Creation** (`signals.core.FunctionFactory`)

   - `createFunction2D()` - Create output showing FrFT at multiple angles
   - Handles complex-valued functions

4. **Operation Framework** (`signals.core.UnaryOperation`)

   - Base class for 1D → 2D operations
   - Integration with GUI system
   - Type checking

5. **Complex Arithmetic** (`signals.operation.ArrayMath`)
   - Complex multiplication and operations
   - Useful for kernel evaluation

### ❌ Components to Implement

1. **FrFT Computation Algorithm**

   - Choose implementation approach (eigendecomposition recommended)
   - Compute FrFT at multiple α values
   - Handle special cases (α = 0, 1, 2, 3)

2. **DFT Eigenvector Computation**

   - For eigendecomposition method
   - Can be precomputed for efficiency
   - Hermite-like functions in discrete case

3. **Angle Selection & Sampling**

   - Which α values to compute (e.g., 0, 0.1, 0.2, ..., 2.0)
   - Spacing between angles
   - Number of rows in output 2D function

4. **Kernel Evaluation**

   - For direct computation methods
   - Chirp multiplication factors
   - Phase terms

5. **Output Formatting**
   - Complex-valued output
   - Proper axis labeling (α on one axis, x on other)
   - Default display mode (magnitude)

## Implementation Strategy

### Phase 1: Core FrFT Algorithm (MVP)

**Goal**: Compute FrFT at multiple fractional orders

#### Step 1.1: FrFT Computation Class

- Create `FractionalFourierOp` class extending `UnaryOperation`
- Implement type checking (accept only Function1D)
- Set up basic structure for 1D → 2D transform
- Output is complex-valued

#### Step 1.2: Algorithm Selection

**Recommendation**: Eigendecomposition method for simplicity and stability

**Alternative**: If eigendecomposition is too complex, use chirp method:

1. Multiply by chirp: exp(iπx² cot φ)
2. Convolve with chirp: exp(iπx² csc φ)
3. Multiply by chirp again

**Decision**: Start with direct formula for specific angles, then optimize

#### Step 1.3: Special Cases Implementation

First implement exact cases:

- α = 0: Return input signal as-is
- α = 1: Use standard FFT
- α = 2: Return flipped signal f(-x)
- α = 3: Use inverse FFT

This provides validation points.

#### Step 1.4: General Fractional Orders

For non-integer α, implement chosen algorithm:

**Option A: Simplified Eigendecomposition**

```
1. Compute DFT eigenvectors (Hermite functions)
2. Project input onto eigenvectors
3. Rotate eigenvalues by α: λ_k^α
4. Reconstruct signal
```

**Option B: Chirp Method**

```
1. Calculate angle φ = απ/2
2. Multiply by exp(iπx² cot φ)
3. Convolve with exp(iπx² csc φ) using FFT
4. Multiply by exp(iπx² cot φ)
```

Implement Option B first (simpler, uses existing FFT).

#### Step 1.5: Multi-Angle Computation

- Define array of α values: [0, 0.05, 0.1, ..., 1.95, 2.0]
  - Typical: 41 angles from α = 0 to 2 (0 to 180°)
- For each α:
  - Compute FrFT
  - Store as row in 2D output
- Result: 2D array (num_angles × signal_length)

#### Step 1.6: Output Construction

- Create Function2D
- X-axis: spatial coordinate (signal length N)
- Y-axis: fractional order α (number of angles)
- Label appropriately
- Set metadata for display

### Phase 2: Optimization & Refinement

**Goal**: Improve accuracy and performance

#### Step 2.1: Eigenvector Precomputation

- For eigendecomposition method:
  - Compute DFT eigenvectors once per signal length
  - Cache for reuse
  - Significant speedup for repeated use

#### Step 2.2: Interpolation Quality

- For chirp method:
  - Ensure proper sampling during convolution
  - Handle edge effects
  - Proper normalization

#### Step 2.3: Numerical Stability

- Handle small angles (α near 0, 2, 4)
- Avoid division by small numbers in cot, csc terms
- Use special case formulas when α is near integer

#### Step 2.4: Performance

- Reduce number of angles if computation is slow
- Consider adaptive angle spacing
- Progress indicator for long computations

### Phase 3: GUI Integration & Validation

**Goal**: User-friendly interface and testing

#### Step 3.1: Options Panel

- Create `FractionalFourierOptionsPanel`
- Options:
  - Number of angles (e.g., 21, 41, 81)
  - Angle range (typically 0 to 2, could extend to 0 to 4)
  - Display mode: magnitude, real, imaginary, phase
  - Algorithm selection (if multiple implemented)

#### Step 3.2: Menu Integration

- Add to Transform menu (or Time-Frequency submenu)
- Group with WDF and Ambiguity Function
- Keyboard shortcut
- Enable only when 1D function is selected

#### Step 3.3: Testing & Validation

Test signals:

1. **Gaussian pulse**:

   - FrFT of Gaussian is Gaussian at all angles
   - Validates correctness across all α

2. **Chirp signal**:

   - Should focus at specific angle
   - Demonstrates rotation in time-frequency plane

3. **Delta function**:

   - α = 0: Delta in space
   - α = 1: Constant in frequency
   - Interpolates between

4. **Pure sinusoid**:

   - α = 0: Sinusoid in space
   - α = 1: Delta in frequency
   - Smooth transition

5. **Verify special cases**:
   - α = 0 matches input
   - α = 1 matches FFT
   - α = 2 matches flip
   - α = 3 matches inverse FFT

## File Structure

```
src/main/java/signals/
├── operation/
│   ├── FractionalFourierOp.java        # Main FrFT operation
│   └── FrFTUtilities.java              # Chirp functions, eigenvectors
├── gui/operation/
│   └── FractionalFourierOptionsPanel.java  # GUI options
└── action/
    └── FractionalFourierAction.java    # Menu action (if needed)
```

## Algorithm Pseudocode (Chirp Method)

```java
Function2D computeFractionalFourier(Function1D input) {
    double[] real = input.getReal();
    double[] imag = input.getImaginary();
    int N = real.length;

    // Define fractional orders to compute
    double alphaMin = 0.0;
    double alphaMax = 2.0;
    int numAngles = 41;
    double[] alphas = new double[numAngles];
    for (int i = 0; i < numAngles; i++) {
        alphas[i] = alphaMin + i * (alphaMax - alphaMin) / (numAngles - 1);
    }

    // Initialize output
    double[] outputReal = new double[numAngles * N];
    double[] outputImag = new double[numAngles * N];

    // For each fractional order
    for (int a = 0; a < numAngles; a++) {
        double alpha = alphas[a];

        // Handle special cases
        if (Math.abs(alpha % 1.0) < 1e-6) {
            // Integer order: use special formulas
            computeIntegerFrFT(real, imag, (int)Math.round(alpha),
                              outputReal, outputImag, a * N, N);
            continue;
        }

        // General fractional order: chirp method
        double phi = alpha * Math.PI / 2;  // Rotation angle

        // Compute chirp factors
        double cotPhi = Math.cos(phi) / Math.sin(phi);
        double cscPhi = 1.0 / Math.sin(phi);

        // Step 1: Multiply by exp(iπx² cot φ)
        double[] step1Real = new double[N];
        double[] step1Imag = new double[N];
        for (int n = 0; n < N; n++) {
            double x = (n - N/2.0) / N;  // Normalized coordinate
            double phase = Math.PI * x * x * cotPhi;
            double chirpReal = Math.cos(phase);
            double chirpImag = Math.sin(phase);

            // Complex multiplication
            step1Real[n] = real[n] * chirpReal - imag[n] * chirpImag;
            step1Imag[n] = real[n] * chirpImag + imag[n] * chirpReal;
        }

        // Step 2: Convolve with exp(iπx² csc φ) using FFT
        double[] convKernelReal = new double[N];
        double[] convKernelImag = new double[N];
        for (int n = 0; n < N; n++) {
            double x = (n - N/2.0) / N;
            double phase = Math.PI * x * x * cscPhi;
            convKernelReal[n] = Math.cos(phase);
            convKernelImag[n] = Math.sin(phase);
        }

        // FFT convolution
        double[][] step2 = convolveFFT(step1Real, step1Imag,
                                       convKernelReal, convKernelImag);

        // Step 3: Multiply by exp(iπx² cot φ) again
        for (int n = 0; n < N; n++) {
            double x = (n - N/2.0) / N;
            double phase = Math.PI * x * x * cotPhi;
            double chirpReal = Math.cos(phase);
            double chirpImag = Math.sin(phase);

            int outputIndex = a * N + n;
            outputReal[outputIndex] = step2[0][n] * chirpReal - step2[1][n] * chirpImag;
            outputImag[outputIndex] = step2[0][n] * chirpImag + step2[1][n] * chirpReal;
        }
    }

    return FunctionFactory.createFunction2D(
        outputReal, outputImag,
        true,  // zero-centered
        "FrFT{" + input.getCompactDescriptor() + "}",
        numAngles, N
    );
}

// Helper: Convolution via FFT
double[][] convolveFFT(double[] signalReal, double[] signalImag,
                       double[] kernelReal, double[] kernelImag) {
    // FFT of signal and kernel
    double[][] signalFFT = Transforms.computeFFT1D(signalReal, signalImag, ...);
    double[][] kernelFFT = Transforms.computeFFT1D(kernelReal, kernelImag, ...);

    // Multiply in frequency domain
    double[] productReal = new double[signalReal.length];
    double[] productImag = new double[signalReal.length];
    for (int i = 0; i < signalReal.length; i++) {
        productReal[i] = signalFFT[0][i] * kernelFFT[0][i] -
                        signalFFT[1][i] * kernelFFT[1][i];
        productImag[i] = signalFFT[0][i] * kernelFFT[1][i] +
                        signalFFT[1][i] * kernelFFT[0][i];
    }

    // Inverse FFT
    return Transforms.computeFFT1D(productReal, productImag, ..., true);
}

// Helper: Integer-order FrFT
void computeIntegerFrFT(double[] real, double[] imag, int order,
                       double[] outReal, double[] outImag, int offset, int N) {
    order = order % 4;  // Periodicity of 4

    if (order == 0) {
        // Identity
        System.arraycopy(real, 0, outReal, offset, N);
        System.arraycopy(imag, 0, outImag, offset, N);
    } else if (order == 1) {
        // Standard FFT
        double[][] fft = Transforms.computeFFT1D(real, imag, ...);
        System.arraycopy(fft[0], 0, outReal, offset, N);
        System.arraycopy(fft[1], 0, outImag, offset, N);
    } else if (order == 2) {
        // Flip: f(-x)
        for (int i = 0; i < N; i++) {
            outReal[offset + i] = real[N - 1 - i];
            outImag[offset + i] = imag[N - 1 - i];
        }
    } else { // order == 3
        // Inverse FFT
        double[][] ifft = Transforms.computeFFT1D(real, imag, ..., true);
        System.arraycopy(ifft[0], 0, outReal, offset, N);
        System.arraycopy(ifft[1], 0, outImag, offset, N);
    }
}
```

## Testing Strategy

### Unit Tests

1. **Gaussian Pulse Test**

   - Input: Gaussian exp(-x²/2σ²)
   - Expected: Gaussian at all fractional orders (self-similar under FrFT)
   - Validates: Correctness of FrFT algorithm

2. **Delta Function Test**

   - α = 0: Localized in space
   - α = 1: Spread in frequency (constant)
   - Smooth transition between
   - Validates: Special case handling

3. **Pure Sinusoid Test**

   - α = 0: Sinusoid in time
   - α = 1: Delta peak in frequency
   - Intermediate: Progressive focusing
   - Validates: Energy conservation, focusing behavior

4. **Chirp Signal Test**

   - Should focus sharply at specific α
   - Demonstrates time-frequency rotation
   - Validates: Rotation interpretation

5. **Special Case Validation**
   - α = 0: Output matches input
   - α = 1: Output matches standard FFT
   - α = 2: Output is flipped input
   - α = 3: Output matches inverse FFT
   - Validates: Integer-order special cases

### Integration Tests

1. **Additivity Property**

   - Compute F^α{F^β{f}}
   - Compare with F^(α+β){f}
   - Should be equal (within numerical error)
   - Validates: Mathematical correctness

2. **Energy Conservation**

   - Total energy should be constant at all α
   - ∫|F^α{f}|² dx = ∫|f|² dx
   - Validates: Unitary property

3. **Reversibility**

   - Compute F^α{F^(-α){f}}
   - Should recover original f
   - Validates: Invertibility

4. **Comparison with Known Results**
   - Use published FrFT tables or examples
   - Compare numerical values
   - Validates: Implementation accuracy

## Performance Considerations

1. **Computational Complexity**

   - For M angles and signal length N: O(M × N log N)
   - For 41 angles and N=1024: ~41 FFTs
   - Reasonable for educational purposes

2. **Memory Usage**

   - Output: M × N complex values
   - For M=41, N=1024: ~650KB
   - Temporary arrays for convolution
   - Total: ~2-3 MB

3. **Optimization Opportunities**

   - Parallel computation of different angles
   - Reduce number of angles for interactive use
   - Adaptive angle spacing (finer near interesting features)

4. **Precomputation**
   - Chirp kernels can be precomputed
   - Eigenvectors (if using eigendecomposition) computed once

## References

1. H.M. Ozaktas, Z. Zalevsky, M.A. Kutay, "The Fractional Fourier Transform with Applications in Optics and Signal Processing" (2001)
2. L.B. Almeida, "The fractional Fourier transform and time-frequency representations" (1994)
3. V. Namias, "The Fractional Order Fourier Transform and its Application to Quantum Mechanics" (1980)
4. B.W. Dickinson and K. Steiglitz, "Eigenvectors and functions of the discrete Fourier transform" (1982)
5. Roger Easton's book chapters on signal transforms

## Success Criteria

### Minimum Viable Product (MVP)

- ✅ Computes FrFT at multiple fractional orders
- ✅ Outputs 2D representation (α vs. x)
- ✅ Complex-valued output
- ✅ Special cases (α = 0, 1, 2, 3) correct
- ✅ Basic visualization (magnitude display)

### Full Implementation

- ✅ Gaussian pulse invariant across all α
- ✅ Test signals produce expected behavior
- ✅ Energy conservation verified
- ✅ Additivity property holds
- ✅ GUI integration with options panel
- ✅ Multiple display modes (magnitude, real, imaginary, phase)

### Validation

- ✅ Special cases match expected transforms
- ✅ Gaussian self-similarity verified
- ✅ Chirp focusing demonstrated
- ✅ Energy conserved at all fractional orders

## Known Limitations & Notes

1. **Sampling Requirements**:

   - Chirp method requires well-sampled signals
   - May have artifacts for undersampled signals
   - Educational opportunity to discuss sampling theorem

2. **Computation Time**:

   - O(M N log N) may be slow for large N and many angles
   - Consider limiting M for interactive use
   - Progress indicator recommended

3. **Physical Interpretation**:

   - Less intuitive than WDF or standard FFT
   - Requires more mathematical background
   - Best for advanced users and research applications

4. **Numerical Precision**:

   - Errors accumulate in chirp method
   - Eigendecomposition may be more accurate but slower
   - Document accuracy limits

5. **Display Challenges**:
   - 2D output can be hard to interpret
   - Consider cross-sections at specific α values
   - Magnitude often clearest visualization

## Timeline Estimate

**Phase 1 (Core Algorithm)**: 5-7 hours

- Algorithm research and selection: 2 hours
- Special cases implementation: 1 hour
- Chirp method implementation: 2-3 hours
- Multi-angle computation: 1 hour
- Basic testing: 1 hour

**Phase 2 (Optimization)**: 2-3 hours

- Numerical stability: 1 hour
- Performance optimization: 1 hour
- Edge case handling: 1 hour

**Phase 3 (GUI & Validation)**: 2-3 hours

- Options panel: 1 hour
- Menu integration: 0.5 hours
- Comprehensive testing: 1-1.5 hours

**Total Estimate**: 9-13 hours

**Note**: Longer than WDF/Ambiguity because:

- More complex mathematics
- Multiple implementation options to evaluate
- More subtle numerical issues
- Less familiar algorithm (can't reuse as much from WDF)

## Implementation Dependencies

1. **Implement After WDF and Ambiguity Function**

   - Build confidence with simpler 1D→2D transforms
   - Reuse GUI patterns and display infrastructure
   - Learn from numerical challenges in those implementations

2. **Prerequisites**
   - Solid understanding of FFT infrastructure
   - Complex number handling
   - Experience with time-frequency concepts from WDF

## Next Steps

1. **After WDF and Ambiguity Function are complete**
2. Review plan with advisor
3. Decide on implementation method:
   - Chirp method (faster to implement, good for most cases)
   - Eigendecomposition (more accurate, computationally intensive)
   - Hybrid approach (special cases + general method)
4. Start with Phase 1, focusing on special cases first
5. Validate with Gaussian pulse (key test)
6. Optimize based on performance requirements

## Relationship to Other Features

- **FEAT-004 (WDF)**: Provides experience with 1D→2D transforms

  - WDF can be interpreted as rotated FrFT
  - Shared display infrastructure

- **FEAT-005 (Ambiguity Function)**: Similar 1D→2D structure

  - Both deal with time-frequency concepts
  - Shared testing patterns

- **FEAT-003 (Radon Transform)**: Different domain but also 1D→2D
  - Experience with angle-parameterized transforms

## Advanced Topics (Future Enhancements)

1. **Discrete Hermite Functions**

   - Compute proper eigenvectors of DFT
   - More accurate FrFT
   - Can be displayed separately as educational content

2. **Interactive Angle Selection**

   - Click on 2D output to see 1D slice at specific α
   - Real-time angle adjustment
   - Side-by-side space/frequency comparison

3. **Wigner Distribution Rotation**

   - Show how FrFT rotates WDF
   - Visual demonstration of relationship
   - Advanced educational demonstration

4. **Applications in Optics**
   - Lens propagation simulation
   - Fresnel diffraction
   - Connects to physical phenomena

## Key Implementation Decision: Chirp vs. Eigendecomposition

### Chirp Method (Recommended for Initial Implementation)

**Pros**:

- Easier to implement using existing FFT
- Fast: O(N log N) per angle
- Well-documented algorithm
- Good for educational purposes

**Cons**:

- Numerical errors in edge cases
- Requires careful handling of small angles
- May have artifacts for some signals

### Eigendecomposition Method

**Pros**:

- More mathematically rigorous
- Better numerical stability
- Exact for discrete signals
- Educational value (Hermite functions)

**Cons**:

- Complex to implement
- Computing eigenvectors is expensive: O(N³)
- Can precompute, but adds complexity
- Harder to debug

**Recommendation**: Start with chirp method, add eigendecomposition as optional algorithm in Phase 2 if needed.
