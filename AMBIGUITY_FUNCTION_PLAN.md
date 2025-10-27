# Ambiguity Function Implementation Plan

## Overview

Implement the Ambiguity Function to create 2D time-frequency representations from 1D signals for radar signal processing and signal analysis applications.

## Feature Specification (from FEAT-005)

### Ambiguity Function

The Ambiguity Function is a time-frequency distribution closely related to the WDF, used extensively in radar and sonar signal processing.

**Mathematical Definition**:

```
A(x, ξ) = ∫ f(x + u/2) f*(x - u/2) e^(-i2πξu) du
```

Where:

- `x` is the time shift (delay) coordinate
- `ξ` (xi) is the Doppler frequency coordinate
- `f*` is the complex conjugate of f
- The result is a 2D function of (x, ξ)

**Relationship to WDF**:
The Ambiguity Function is related to the WDF by exchanging the roles of x and u:

- **WDF**: W(u, ξ) = FT{f(u+x/2) f\*(u-x/2)} (FT over x)
- **Ambiguity**: A(x, ξ) = FT{f(x+u/2) f\*(x-u/2)} (FT over u)

### Input/Output

- **Input**: 1D function (Function1D) - can be real or complex
- **Output**: 2D function (Function2D) representing delay-Doppler plane
  - X dimension: x (time shift/delay)
  - Y dimension: ξ (Doppler frequency from -0.5 to 0.5 in normalized units)

### Use Cases

- Radar signal processing and matched filtering
- Sonar signal analysis
- Time-delay and Doppler shift analysis
- Educational demonstrations of delay-Doppler representations
- Signal detection in noise

## Mathematical Background

### Discrete Ambiguity Function Formula

For a discrete signal f[n] with N samples:

```
A[k, m] = Σ(n=0 to N-1) f[k + n] f*[k - n] e^(-i2πmn/N)
```

Where:

- `k` is the discrete delay index (-N/2 to N/2)
- `m` is the discrete Doppler frequency index (0 to N-1, then shifted)
- `n` is the summation index (lag variable)
- The sum is taken over valid indices

**Alternative Indexing (More Common)**:

```
A[τ, ν] = Σ(n=-∞ to ∞) f[n + τ/2] f*[n - τ/2] e^(-i2πνn)
```

Where:

- `τ` (tau) is the discrete delay
- `ν` (nu) is the discrete Doppler frequency

### Key Properties

1. **Complex-Valued**: Unlike WDF, Ambiguity Function is generally complex
2. **Peak at Origin**: Maximum magnitude typically at (0, 0)
3. **Conjugate Symmetry**: A(τ, ν) = A\*(-τ, -ν) for real signals
4. **Volume Constraint**: ∫∫ |A(τ, ν)|² dτ dν = [∫ |f(t)|² dt]²
5. **Dimensionality**: Doubles dimensionality (1D → 2D)

### Comparison with WDF

| Property         | WDF W(u, ξ)                   | Ambiguity A(τ, ν)             |
| ---------------- | ----------------------------- | ----------------------------- |
| Variables        | time u, frequency ξ           | delay τ, Doppler ν            |
| Formula          | FT{f(u+x/2)f\*(u-x/2)} over x | FT{f(x+u/2)f\*(x-u/2)} over u |
| Real/Complex     | Always real                   | Generally complex             |
| Physical Meaning | Joint time-frequency          | Joint delay-Doppler           |
| Application      | Time-frequency analysis       | Radar/sonar processing        |

## Existing Infrastructure Analysis

### ✅ Available Components (Same as WDF)

1. **FFT Operations** (`signals.operation.Transforms`)

   - `computeFFT1D()` - For transforming n → ν
   - Normalization options
   - Forward and inverse transforms

2. **1D Function Architecture** (`signals.core.Function1D`)

   - `getReal()`, `getImaginary()` - Access signal data
   - `isZeroCentered()` - Check if zero is at center
   - Complex signal support

3. **2D Function Creation** (`signals.core.FunctionFactory`)

   - `createFunction2D()` - Create output delay-Doppler representation
   - Handles both real and imaginary components

4. **Operation Framework** (`signals.core.UnaryOperation`)

   - Base class for 1D → 2D operations
   - Integration with GUI system
   - Type checking (ensure input is 1D)

5. **Complex Arithmetic** (`signals.operation.ArrayMath`)
   - Complex multiplication for f[n+τ/2] × f\*[n-τ/2]
   - Complex conjugate operations

### ❌ Components to Implement

1. **Ambiguity Function Computation Algorithm**

   - Delay-indexed computation (different from WDF's time indexing)
   - Sum over signal position n for each delay τ
   - FFT over n for each delay τ

2. **Edge Handling Strategy**

   - Zero-padding beyond signal boundaries
   - Handling negative indices for f[k - n]
   - Symmetric vs. periodic extension

3. **Output Format**

   - **Complex-valued** output (unlike WDF)
   - Proper scaling for display
   - Magnitude/phase representation options

4. **Doppler Frequency Axis Setup**
   - Frequency range: -0.5 to 0.5 (normalized)
   - FFT shift to center zero frequency
   - Proper labeling for display

## Implementation Strategy

### Phase 1: Core Ambiguity Function Algorithm (MVP)

**Goal**: Compute basic Ambiguity Function from 1D signal

#### Step 1.1: Ambiguity Function Computation Class

- Create `AmbiguityFunctionOp` class extending `UnaryOperation`
- Implement type checking (accept only Function1D)
- Set up basic structure for 1D → 2D transform
- **Note**: Output is complex (both real and imaginary parts)

#### Step 1.2: Delay Product Calculation

- For each delay τ (-N/2 to N/2):
  - For each position n (0 to N-1):
    - Calculate indices: n1 = n + τ/2, n2 = n - τ/2
    - Handle boundary conditions:
      - If n1 or n2 out of bounds, use zero-padding
      - **Note**: τ should be even for integer indexing, or use interpolation
    - Compute product: P[n] = f[n1] × f\*[n2]
  - Result: Complex array of products over n for delay τ

#### Step 1.3: FFT Transform (n → ν)

- For each delay τ:
  - Take product array P[n]
  - Perform FFT: A[τ, k] = FFT(P[n])
  - FFT shift to center zero frequency
  - **Keep both real and imaginary parts** (output is complex)

#### Step 1.4: Output Construction

- Create 2D array: dimensions N × N
  - Row index: delay τ
  - Column index: Doppler frequency ν
- Use `FunctionFactory.createFunction2D()`
- Set proper labels and metadata
- **Note**: Unlike WDF, this is a complex-valued function

### Phase 2: Edge Handling & Refinement

**Goal**: Improve boundary handling and numerical accuracy

#### Step 2.1: Index Handling for Half-Integer Delays

**Challenge**: For general τ, indices n ± τ/2 may not be integers

**Solutions**:

1. **Restrict to Even Delays** (simplest):

   - Only compute for τ = -N, -N+2, ..., N-2, N
   - Ensures n ± τ/2 are always integers
   - Output dimensions: (N/2 + 1) × N

2. **Linear Interpolation** (more accurate):

   - For n + τ/2, interpolate between f[floor(n+τ/2)] and f[ceil(n+τ/2)]
   - Same for n - τ/2
   - Allows computation at all delay values
   - Use existing `Interpolator.linearInterpolate()`

3. **Oversample Signal** (best quality):
   - Upsample signal by factor of 2 using interpolation
   - Compute at all delays on finer grid
   - Downsample output if needed

**Recommendation**: Start with option 1 (even delays), add option 2 as enhancement

#### Step 2.2: Boundary Condition Options

- Implement multiple strategies:
  - **Zero-padding** (default): Use 0 outside signal bounds
  - **Circular/Periodic**: Wrap around (modulo N)
  - **Symmetric extension**: Mirror signal at boundaries
- Add configuration option to select strategy

#### Step 2.3: Numerical Considerations

- Handle complex conjugate properly
- Preserve both real and imaginary parts
- Check for symmetry: A(τ, ν) ≟ A\*(-τ, -ν) for real signals
- Proper FFT normalization

### Phase 3: GUI Integration & Validation

**Goal**: User-friendly interface and testing

#### Step 3.1: Options Panel

- Create `AmbiguityFunctionOptionsPanel`
- Options:
  - Boundary condition selection (zero-pad, periodic, symmetric)
  - Delay sampling (even only vs. interpolated)
  - Display format:
    - Magnitude only (default for visualization)
    - Real part
    - Imaginary part
    - Phase

#### Step 3.2: Menu Integration

- Add to Transform menu (or Time-Frequency submenu with WDF)
- Keyboard shortcut
- Enable only when 1D function is selected
- Group with WDF operation

#### Step 3.3: Testing & Validation

- Test signals:
  - Rectangular pulse → sinc-like pattern in Doppler
  - Pure sinusoid → localized in Doppler, spread in delay
  - Chirp signal → characteristic ridge structure
  - Gaussian pulse → 2D Gaussian-like shape
- Verify peak at origin (0, 0)
- Check conjugate symmetry for real signals

## File Structure

```
src/main/java/signals/
├── operation/
│   ├── AmbiguityFunctionOp.java        # Main Ambiguity Function operation
│   └── AmbiguityUtilities.java         # Helper functions (optional)
├── gui/operation/
│   └── AmbiguityFunctionOptionsPanel.java  # GUI options
└── action/
    └── AmbiguityFunctionAction.java    # Menu action (if needed)
```

## Algorithm Pseudocode

```java
Function2D computeAmbiguityFunction(Function1D input) {
    // Get input signal
    double[] real = input.getReal();
    double[] imag = input.getImaginary();
    int N = real.length;

    // Initialize output (M x N matrix, where M is number of delays)
    // For even delays only: M = N/2 + 1
    int numDelays = N / 2 + 1;
    double[] outputReal = new double[numDelays * N];
    double[] outputImag = new double[numDelays * N];

    // For each delay τ (even values only)
    int delayIdx = 0;
    for (int tau = -N; tau <= N; tau += 2) {
        // Initialize product array over position n
        double[] productReal = new double[N];
        double[] productImag = new double[N];

        // For each position n
        for (int n = 0; n < N; n++) {
            // Calculate indices: n + τ/2 and n - τ/2
            int n1 = n + tau / 2;
            int n2 = n - tau / 2;

            // Boundary handling (zero-padding)
            double r1 = (n1 >= 0 && n1 < N) ? real[n1] : 0.0;
            double i1 = (n1 >= 0 && n1 < N) ? imag[n1] : 0.0;
            double r2 = (n2 >= 0 && n2 < N) ? real[n2] : 0.0;
            double i2 = (n2 >= 0 && n2 < N) ? imag[n2] : 0.0;

            // Complex conjugate of f[n2]
            double r2_conj = r2;
            double i2_conj = -i2;

            // Complex multiplication: f[n1] * conj(f[n2])
            productReal[n] = r1 * r2_conj - i1 * i2_conj;
            productImag[n] = r1 * i2_conj + i1 * r2_conj;
        }

        // FFT of product array (n → ν)
        double[][] ambigRow = Transforms.computeFFT1D(
            productReal, productImag,
            false,  // not zero-centered initially
            false,  // forward transform
            Transforms.NORMALIZE_ROOT_N
        );

        // FFT shift to center zero frequency
        double[] shiftedReal = fftshift(ambigRow[0]);
        double[] shiftedImag = fftshift(ambigRow[1]);

        // Store in output (complex-valued)
        for (int k = 0; k < N; k++) {
            int outputIndex = delayIdx * N + k;
            outputReal[outputIndex] = shiftedReal[k];
            outputImag[outputIndex] = shiftedImag[k];
        }

        delayIdx++;
    }

    // Create 2D function (complex-valued)
    return FunctionFactory.createFunction2D(
        outputReal, outputImag,
        true,  // zero-centered (frequency axis)
        "Ambiguity{" + input.getCompactDescriptor() + "}",
        numDelays, N  // dimensions
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

1. **Rectangular Pulse Test**

   - Input: Unit rectangle of width W
   - Expected: Sinc-like pattern in Doppler axis
   - Peak at origin (τ=0, ν=0)
   - Validates: Basic transform, origin peak

2. **Pure Sinusoid Test**

   - Input: `f(t) = cos(2πf₀t)` at frequency f₀
   - Expected: Horizontal line at Doppler frequency f₀
   - Spread across all delays
   - Validates: Doppler frequency localization

3. **Gaussian Pulse Test**

   - Input: `f(t) = exp(-t²/2σ²)`
   - Expected: Approximately 2D Gaussian centered at origin
   - Validates: Localization in delay-Doppler plane

4. **Chirp Signal Test**

   - Input: Linear chirp with changing frequency
   - Expected: Characteristic ridge or diagonal pattern
   - Validates: Sensitivity to frequency modulation

5. **Delta Function Test**
   - Input: Single impulse
   - Expected: Constant across Doppler (all frequencies)
   - Localized at τ = 0
   - Validates: Delay localization

### Integration Tests

1. **Conjugate Symmetry Test**

   - Input: Real signal
   - Check: A(τ, ν) ≟ A\*(-τ, -ν)
   - Validates: Mathematical property for real signals

2. **Peak at Origin Test**

   - Input: Various signals
   - Check: Maximum magnitude should occur at or near (0, 0)
   - Validates: Known Ambiguity Function property

3. **Comparison with WDF**

   - Input: Same signal to both WDF and Ambiguity
   - Relationship: Should see complementary information
   - Validates: Correct implementation relative to WDF

4. **Boundary Conditions**
   - Test different edge handling strategies
   - Verify no artifacts at boundaries
   - Validates: Edge handling implementation

## Performance Considerations

1. **Computational Complexity**

   - For signal length N with M delays: O(M × N log N)
   - For even delays only: M = N/2, so O(N² log N) (same as WDF)
   - For all delays with interpolation: O(N² log N) with higher constant

2. **Memory Usage**

   - Output array: M × N (for even delays: ~N²/2)
   - Less than WDF if restricting to even delays
   - Temporary arrays for products
   - For N=1024, M=512: ~4MB output

3. **Optimization Opportunities**
   - Pre-compute complex conjugate once (same as WDF)
   - Reuse FFT plan
   - Parallel computation of delay rows
   - Consider reducing number of delays for interactive use

## References

1. Leon Cohen, "Time-Frequency Analysis" (1995) - Chapter on Ambiguity Function
2. Roger Easton's book chapters on radar signal processing
3. P.M. Woodward, "Probability and Information Theory, with Applications to Radar" (1953)
4. A.W. Rihaczek, "Principles of High-Resolution Radar" (1969)
5. Wikipedia: Ambiguity function

## Success Criteria

### Minimum Viable Product (MVP)

- ✅ Computes Ambiguity Function from 1D signal
- ✅ Outputs 2D delay-Doppler representation
- ✅ Complex-valued output (both real and imaginary)
- ✅ Basic visualization in GUI (magnitude display)

### Full Implementation

- ✅ Conjugate symmetry for real signals
- ✅ Peak at origin verified
- ✅ Test signals produce expected patterns
- ✅ Multiple boundary condition options
- ✅ Display options (magnitude, real, imaginary, phase)
- ✅ GUI integration with options panel

### Validation

- ✅ Rectangular pulse → sinc-like Doppler pattern
- ✅ Pure sinusoid → Doppler frequency line
- ✅ Gaussian pulse → 2D Gaussian-like shape
- ✅ Conjugate symmetry property holds

## Known Limitations & Notes

1. **Half-Integer Delay Problem**:

   - Restricting to even delays loses resolution
   - Interpolation adds complexity but improves accuracy
   - Consider adding interpolation in Phase 2

2. **Complex Output**:

   - Users may be surprised it's complex (unlike WDF)
   - Default to magnitude display for intuitive visualization
   - Educational opportunity to discuss complex-valued distributions

3. **Interpretation**:

   - Less intuitive than WDF for general signals
   - More natural for radar/sonar applications
   - Emphasize complementary nature with WDF

4. **Computational Cost**:
   - Similar to WDF: O(N² log N)
   - For large N, consider progress indicator
   - May want to limit maximum signal length

## Timeline Estimate

**Phase 1 (Core Algorithm)**: 3-4 hours

- Algorithm implementation: 2 hours
  - Can reuse much of WDF structure
  - Main difference: loop over delays instead of time
- Basic testing: 1 hour
- Output construction: 1 hour

**Phase 2 (Refinement)**: 2-3 hours

- Even delay implementation: 0.5 hours
- Boundary conditions: 1 hour
- Interpolation option (optional): 1-1.5 hours

**Phase 3 (GUI & Validation)**: 2-3 hours

- Options panel: 1 hour
  - Can reuse WDF panel structure
- Menu integration: 0.5 hours
- Comprehensive testing: 1-1.5 hours

**Total Estimate**: 7-10 hours

**Note**: Faster than WDF estimate because much of the infrastructure (GUI, testing patterns) can be reused from WDF implementation.

## Implementation Dependencies

1. **WDF Should Be Implemented First**

   - WDF provides template for 1D → 2D transform
   - GUI patterns can be reused
   - Testing strategies are similar
   - Same menu structure and user workflow

2. **Shared Infrastructure**
   - Both use same FFT utilities
   - Same boundary condition options
   - Similar display requirements
   - Can share helper classes if beneficial

## Next Steps

1. **After WDF is complete**, review this plan with advisor
2. Confirm approach to half-integer delay problem
3. Decide on initial delay sampling strategy (even only vs. interpolation)
4. Start with Phase 1, Step 1.1 (Core Ambiguity computation)
5. Leverage WDF code structure where appropriate
6. Test with radar-relevant signals (chirps, pulses)
7. Compare results with WDF for same signals

## Relationship to Other Features

- **FEAT-004 (WDF)**: Very similar structure, complementary information
  - Implement WDF first, use as template
  - Can share utilities and GUI components
- **FEAT-003 (Radon Transform)**: Different algorithm but also 1D→2D
  - Experience with 1D→2D transforms will help
- **FEAT-006 (Fractional Fourier)**: Different concept, but 1D→2D output
  - Time-frequency framework knowledge carries over

## Key Differences from WDF

| Aspect                  | WDF                | Ambiguity Function |
| ----------------------- | ------------------ | ------------------ |
| Loop Variable           | Time u             | Delay τ            |
| FFT Variable            | Lag x              | Position n         |
| Output Type             | Real               | Complex            |
| Product Form            | f[u+x/2]f\*[u-x/2] | f[n+τ/2]f\*[n-τ/2] |
| Display Default         | Direct values      | Magnitude          |
| Index Challenge         | None               | Half-integer τ/2   |
| Physical Interpretation | Time-frequency     | Delay-Doppler      |
| Peak Location           | Signal-dependent   | Origin (0,0)       |

This table highlights that while the algorithms are closely related, there are important implementation differences to handle correctly.
