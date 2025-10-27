# Wigner Distribution Function (WDF) Implementation Plan

## Overview

Implement Wigner Distribution Function to create 2D time-frequency representations from 1D signals for educational demonstrations and signal analysis.

## Feature Specification (from FEAT-004)

### Wigner Distribution Function

The WDF is a time-frequency distribution that provides a hybrid space-frequency representation of a 1D signal.

**Mathematical Definition**:

```
W(u, ξ) = ∫ f(u + x/2) f*(u - x/2) e^(-i2πξx) dx
```

Where:

- `u` is the time/space coordinate
- `ξ` (xi) is the frequency coordinate
- `f*` is the complex conjugate of f
- The result is a 2D function of (u, ξ)

### Input/Output

- **Input**: 1D function (Function1D) - can be real or complex
- **Output**: 2D function (Function2D) representing time-frequency plane
  - X dimension: u (time/space positions from input)
  - Y dimension: ξ (frequency from -0.5 to 0.5 in normalized units)

### Use Cases

- Time-frequency analysis of 1D signals
- Visualizing instantaneous frequency changes
- Educational demonstrations of joint time-frequency representations
- Signal processing and radar applications

## Mathematical Background

### Discrete WDF Formula

For a discrete signal f[n] with N samples:

```
W[m, k] = 2 * Σ(τ=-N/2 to N/2) f[m + τ] f*[m - τ] e^(-i2πkτ/N)
```

Where:

- `m` is the discrete time index (0 to N-1)
- `k` is the discrete frequency index (0 to N-1, then shifted to center)
- `τ` is the lag variable
- The factor of 2 accounts for integration over both positive and negative lags

### Key Properties

1. **Real-Valued**: WDF is always real for any complex signal
2. **Time and Frequency Marginals**:
   - ∫ W(u,ξ) dξ = |f(u)|² (time-domain energy)
   - ∫ W(u,ξ) du = |F(ξ)|² (frequency-domain energy)
3. **Cross Terms**: WDF can produce interference terms between signal components
4. **Dimensionality**: Doubles dimensionality (1D → 2D)

## Existing Infrastructure Analysis

### ✅ Available Components

1. **FFT Operations** (`signals.operation.Transforms`)

   - `computeFFT1D()` - For transforming τ → ξ
   - Normalization options
   - Forward and inverse transforms

2. **1D Function Architecture** (`signals.core.Function1D`)

   - `getReal()`, `getImaginary()` - Access signal data
   - `isZeroCentered()` - Check if zero-frequency is at center
   - Complex signal support

3. **2D Function Creation** (`signals.core.FunctionFactory`)

   - `createFunction2D()` - Create output time-frequency representation
   - Proper handling of real/imaginary components

4. **Operation Framework** (`signals.core.UnaryOperation`)

   - Base class for 1D → 2D operations
   - Integration with GUI system
   - Type checking (ensure input is 1D)

5. **Complex Arithmetic** (`signals.operation.ArrayMath`)
   - Complex multiplication for f[m+τ] × f\*[m-τ]
   - Complex conjugate operations

### ❌ Components to Implement

1. **WDF Computation Algorithm**

   - Lag calculation for each time point
   - Boundary handling (what to do at edges)
   - FFT of lag products for each time point

2. **Edge Handling Strategy**

   - Zero-padding beyond signal boundaries
   - Symmetric extension
   - Circular extension (periodic assumption)

3. **Output Normalization**

   - Ensure real-valued output (handle numerical errors)
   - Proper scaling for display

4. **Frequency Axis Setup**
   - Frequency range: -0.5 to 0.5 (normalized)
   - FFT shift to center zero frequency
   - Proper labeling for display

## Implementation Strategy

### Phase 1: Core WDF Algorithm (MVP)

**Goal**: Compute basic WDF from 1D signal

#### Step 1.1: WDF Computation Class

- Create `WignerDistributionOp` class extending `UnaryOperation`
- Implement type checking (accept only Function1D)
- Set up basic structure for 1D → 2D transform

#### Step 1.2: Lag Product Calculation

- For each time point u (0 to N-1):
  - For each lag τ (-N/2 to N/2):
    - Calculate indices: n1 = u + τ, n2 = u - τ
    - Handle boundary conditions:
      - If n1 or n2 out of bounds, use zero-padding
    - Compute product: P[τ] = f[n1] × f\*[n2]
  - Result: Complex array of lag products for time u

#### Step 1.3: FFT Transform (τ → ξ)

- For each time point u:
  - Take lag product array P[τ]
  - Perform FFT: W[u, k] = FFT(P[τ])
  - FFT shift to center zero frequency
  - Extract real part (WDF should be real)

#### Step 1.4: Output Construction

- Create 2D array: dimensions N × N
  - Row index: time u
  - Column index: frequency ξ
- Use `FunctionFactory.createFunction2D()`
- Set proper labels and metadata

### Phase 2: Edge Handling & Optimization

**Goal**: Improve boundary handling and performance

#### Step 2.1: Boundary Condition Options

- Implement multiple strategies:
  - **Zero-padding** (default): Use 0 outside signal bounds
  - **Symmetric extension**: Mirror signal at boundaries
  - **Circular/Periodic**: Wrap around (treat as periodic)
- Add configuration option to select strategy

#### Step 2.2: Numerical Stability

- Handle complex conjugate properly
- Ensure real-valued output:
  - Check imaginary part magnitude
  - Threshold small imaginary values to zero
  - Warning if large imaginary component (indicates bug)

#### Step 2.3: Performance Optimization

- Pre-allocate arrays to avoid repeated allocation
- Consider vectorization where possible
- Memory management for large signals

### Phase 3: GUI Integration & Validation

**Goal**: User-friendly interface and testing

#### Step 3.1: Options Panel

- Create `WignerDistributionOptionsPanel`
- Options:
  - Boundary condition selection (zero-pad, symmetric, circular)
  - Display options (normalization, color scale)

#### Step 3.2: Menu Integration

- Add to Transform menu (or new Time-Frequency menu)
- Keyboard shortcut
- Enable only when 1D function is selected

#### Step 3.3: Testing & Validation

- Test signals:
  - Pure sinusoid → horizontal line at corresponding frequency
  - Chirp signal → diagonal line (changing frequency)
  - Gaussian pulse → localized blob in time-frequency plane
  - Multiple sinusoids → cross-terms visible
- Compare with known WDF properties (marginals)

## File Structure

```
src/main/java/signals/
├── operation/
│   ├── WignerDistributionOp.java       # Main WDF operation
│   └── WDFUtilities.java               # Helper functions (optional)
├── gui/operation/
│   └── WignerDistributionOptionsPanel.java  # GUI options
└── action/
    └── WignerDistributionAction.java   # Menu action (if needed)
```

## Algorithm Pseudocode

```java
Function2D computeWDF(Function1D input) {
    // Get input signal
    double[] real = input.getReal();
    double[] imag = input.getImaginary();
    int N = real.length;

    // Initialize output (N x N matrix)
    double[] outputReal = new double[N * N];
    double[] outputImag = new double[N * N];

    // For each time point u
    for (int u = 0; u < N; u++) {
        // Initialize lag product array
        double[] lagProductReal = new double[N];
        double[] lagProductImag = new double[N];

        // For each lag τ
        for (int tau = -N/2; tau < N/2; tau++) {
            int n1 = u + tau;
            int n2 = u - tau;

            // Boundary handling (zero-padding)
            double r1 = (n1 >= 0 && n1 < N) ? real[n1] : 0.0;
            double i1 = (n1 >= 0 && n1 < N) ? imag[n1] : 0.0;
            double r2 = (n2 >= 0 && n2 < N) ? real[n2] : 0.0;
            double i2 = (n2 >= 0 && n2 < N) ? imag[n2] : 0.0;

            // Complex conjugate of f[n2]
            double r2_conj = r2;
            double i2_conj = -i2;

            // Complex multiplication: f[n1] * conj(f[n2])
            int tauIndex = tau + N/2;  // Shift to positive indices
            lagProductReal[tauIndex] = r1 * r2_conj - i1 * i2_conj;
            lagProductImag[tauIndex] = r1 * i2_conj + i1 * r2_conj;
        }

        // FFT of lag product (τ → ξ)
        double[][] wdfRow = Transforms.computeFFT1D(
            lagProductReal, lagProductImag,
            false,  // not zero-centered initially
            false,  // forward transform
            Transforms.NORMALIZE_ROOT_N
        );

        // FFT shift to center zero frequency
        double[] shiftedReal = fftshift(wdfRow[0]);
        double[] shiftedImag = fftshift(wdfRow[1]);

        // Store in output (should be real-valued)
        for (int k = 0; k < N; k++) {
            int outputIndex = u * N + k;
            outputReal[outputIndex] = shiftedReal[k];
            outputImag[outputIndex] = shiftedImag[k];

            // Check if imaginary part is negligible
            if (Math.abs(shiftedImag[k]) > 1e-10 * Math.abs(shiftedReal[k])) {
                System.err.println("Warning: Non-negligible imaginary component in WDF");
            }
        }
    }

    // Create 2D function
    return FunctionFactory.createFunction2D(
        outputReal, outputImag,
        true,  // zero-centered (frequency axis)
        "WDF{" + input.getCompactDescriptor() + "}",
        N, N  // dimensions
    );
}

// Helper: FFT shift to center zero frequency
double[] fftshift(double[] input) {
    int N = input.length;
    int half = N / 2;
    double[] output = new double[N];

    // Move second half to first half
    System.arraycopy(input, half, output, 0, N - half);
    // Move first half to second half
    System.arraycopy(input, 0, output, N - half, half);

    return output;
}
```

## Testing Strategy

### Unit Tests

1. **Pure Sinusoid Test**

   - Input: `f(t) = cos(2πf₀t)` at frequency f₀
   - Expected WDF: Horizontal line at frequency f₀
   - Validates: Basic transform, frequency localization

2. **Gaussian Pulse Test**

   - Input: `f(t) = exp(-t²/2σ²)`
   - Expected WDF: Localized blob around (t=0, ξ=0)
   - Validates: Time-frequency localization

3. **Chirp Signal Test**

   - Input: Linear chirp with increasing frequency
   - Expected WDF: Diagonal line in time-frequency plane
   - Validates: Time-varying frequency representation

4. **Delta Function Test**

   - Input: Single impulse at time t₀
   - Expected WDF: Vertical line at t₀ (all frequencies)
   - Validates: Time localization

5. **Marginal Properties**
   - Time marginal: ∫ W(u,ξ) dξ should equal |f(u)|²
   - Frequency marginal: ∫ W(u,ξ) du should equal |F(ξ)|²
   - Validates: Mathematical correctness

### Integration Tests

1. **Cross-Terms Test**

   - Input: Two separated Gaussian pulses
   - Expected: Two main terms + interference term between them
   - Validates: Known WDF behavior with multi-component signals

2. **Real vs Complex Input**

   - Test with purely real signal
   - Test with complex signal
   - Validates: Complex number handling

3. **Boundary Conditions**
   - Test different edge handling strategies
   - Verify no artifacts at boundaries
   - Validates: Edge handling implementation

## Performance Considerations

1. **Computational Complexity**

   - For signal length N: O(N² log N)
   - N FFTs of length N: N × O(N log N)
   - Consider this is acceptable for educational purposes

2. **Memory Usage**

   - Output array: N × N (quadratic in input size)
   - Temporary arrays for lag products
   - For N=1024: ~8MB output (double precision)

3. **Optimization Opportunities**
   - Pre-compute complex conjugate once
   - Reuse FFT plan if available
   - Parallel computation of rows (future enhancement)

## References

1. Leon Cohen, "Time-Frequency Analysis" (1995)
2. Roger Easton's book chapters on time-frequency representations
3. L. Cohen, "Time-Frequency Distributions—A Review" (1989)
4. Wikipedia: Wigner quasi-probability distribution

## Success Criteria

### Minimum Viable Product (MVP)

- ✅ Computes WDF from 1D signal
- ✅ Outputs 2D time-frequency representation
- ✅ Real-valued output (negligible imaginary part)
- ✅ Basic visualization in GUI

### Full Implementation

- ✅ Correct marginal properties (time and frequency)
- ✅ Test signals produce expected patterns
- ✅ Multiple boundary condition options
- ✅ GUI integration with options panel
- ✅ Works with various signal lengths (powers of 2 preferred)

### Validation

- ✅ Pure sinusoid → horizontal line
- ✅ Chirp → diagonal line
- ✅ Gaussian pulse → localized blob
- ✅ Marginals match signal energy distributions

## Known Limitations & Notes

1. **Cross-Terms**: WDF can produce interference between signal components

   - This is a fundamental property, not a bug
   - Educational opportunity to discuss WDF characteristics

2. **Negativity**: WDF can have negative values

   - Not a probability distribution despite the name
   - Negative regions are physically meaningful

3. **Resolution Trade-off**: Perfect time-frequency localization is impossible

   - Heisenberg uncertainty principle applies
   - WDF shows best possible joint resolution

4. **Computational Cost**: O(N² log N) limits practical signal lengths
   - For N=2048: may take several seconds
   - Consider progress indicator for long computations

## Timeline Estimate

**Phase 1 (Core Algorithm)**: 3-4 hours

- Algorithm implementation: 2 hours
- Basic testing: 1 hour
- Output construction: 1 hour

**Phase 2 (Edge Handling & Polish)**: 2-3 hours

- Boundary conditions: 1 hour
- Numerical stability: 1 hour
- Performance optimization: 1 hour

**Phase 3 (GUI & Validation)**: 2-3 hours

- Options panel: 1 hour
- Menu integration: 0.5 hours
- Comprehensive testing: 1-2 hours

**Total Estimate**: 7-10 hours

## Next Steps

1. Review plan with advisor (Roger Easton Jr)
2. Confirm mathematical approach and boundary handling
3. Start with Phase 1, Step 1.1 (Core WDF computation)
4. Implement and test with simple signals
5. Validate marginal properties before proceeding to GUI
6. After WDF is complete, use as template for Ambiguity Function (FEAT-005)

## Relationship to Other Features

- **FEAT-005 (Ambiguity Function)**: Very similar structure, x and u exchanged
  - Can reuse much of the WDF code
  - Implement WDF first as template
- **FEAT-006 (Fractional Fourier)**: Different algorithm, but similar 1D→2D concept
  - WDF experience will inform implementation approach
