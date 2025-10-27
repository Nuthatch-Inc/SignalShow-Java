# Radon Transform Implementation Plan

## Overview

Implement forward and inverse Radon transform with configurable angles and filtering options for teaching CT/MRI reconstruction principles.

## Feature Specification (from FEAT-003)

### Forward Radon Transform

- Sum rows and columns to get initial projections (0° and 90°)
- Rotate image by configurable angle increments
- Use interpolation during rotation
- Constrain image to central circle to avoid edge artifacts
- Generate sinogram display (projection angles × detector positions)

### Inverse Radon Transform

- Apply filters before backprojection:
  - Rho filter (basic |ρ| frequency filter)
  - Shepp-Logan filter (rolled-off rho filter to reduce noise)
  - Other filter variations as needed
- Perform filtered backprojection
- Reconstruct 2D image from sinogram

### Configurable Parameters

- Number of projection angles (e.g., 30, 60, 90, 180)
- Filter type selection (rho, Shepp-Logan, etc.)
- Interpolation method (linear, nearest neighbor)

## Existing Infrastructure Analysis

### ✅ Available Components

1. **Interpolation** (`signals.operation.Interpolator`)

   - `linearInterpolate()` - Linear interpolation for 1D arrays
   - Can be used for image rotation

2. **FFT Operations** (`signals.operation.Transforms`)

   - `computeFFT1D()` - 1D FFT for filtering
   - `computeFFT2D()` - 2D FFT
   - Normalization options (root N, N, none)
   - Forward and inverse transforms

3. **2D Function Architecture** (`signals.core.Function2D`)

   - `getDimensionX()`, `getDimensionY()` - Get image dimensions
   - `getReal()`, `getImaginary()` - Access data arrays
   - Data stored as 1D arrays in row-major order

4. **Operation Framework** (`signals.core.UnaryOperation`)

   - Base class for operations on single functions
   - `create(Function input)` - Main operation method
   - Integration with GUI operation system

5. **Array Utilities** (`signals.operation.ArrayMath`, `signals.operation.ArrayUtilities`)
   - Array arithmetic operations
   - May have utility functions for array manipulation

### ❌ Components to Implement

1. **Image Rotation**

   - Rotate 2D image by arbitrary angle
   - Use bilinear interpolation
   - Handle boundary conditions

2. **Projection Calculation**

   - Sum along rows (0°)
   - Sum along columns (90°)
   - Sum along arbitrary angles after rotation

3. **Circular Masking**

   - Constrain image to central circle
   - Avoid edge artifacts from rotation

4. **Frequency Filters**

   - Rho filter: |ρ| in frequency domain
   - Shepp-Logan filter: |ρ| × sinc rolloff
   - Ram-Lak filter (optional)
   - Additional filters as needed

5. **Backprojection**

   - Distribute filtered projection across image at angle
   - Sum contributions from all angles
   - Normalize final result

6. **Sinogram Data Structure**
   - 2D array: [angle × detector_position]
   - Display as 2D image (Function2D)

## Implementation Strategy

### Phase 1: Forward Radon Transform (MVP)

**Goal**: Create sinogram from 2D image

#### Step 1.1: Basic Projection Operations

- Create `RadonProjection` utility class
- Implement row sum (0° projection)
- Implement column sum (90° projection)
- Test with simple test images (e.g., single point, line)

#### Step 1.2: Image Rotation

- Implement `rotateImage2D()` method
  - Use bilinear interpolation from `Interpolator`
  - Rotate around image center
  - Handle out-of-bounds with zero-padding or edge clamping

#### Step 1.3: Circular Masking

- Implement `applyCircularMask()` method
  - Calculate distance from center for each pixel
  - Set pixels outside radius to zero
  - Radius = min(width, height) / 2

#### Step 1.4: Multi-Angle Projections

- Loop over angles from 0° to 180° (exclusive)
- For each angle:
  1. Apply circular mask to input image
  2. Rotate image by angle
  3. Sum columns (always project vertically after rotation)
  4. Store projection in sinogram array

#### Step 1.5: Sinogram Display

- Create `RadonTransformOp` class extending `UnaryOperation`
- Store sinogram as Function2D
  - X dimension: detector positions (image height/width)
  - Y dimension: projection angles
- Display sinogram in GUI

### Phase 2: Inverse Radon Transform

**Goal**: Reconstruct image from sinogram

#### Step 2.1: Frequency Domain Filters

- Create `RadonFilters` utility class
- Implement filter generation:
  - `createRhoFilter(int size)` - |ρ| filter
  - `createSheppLoganFilter(int size)` - |ρ| × sinc rolloff
  - `createRamLakFilter(int size)` - |ρ| with cutoff
- Filters are 1D arrays applied to each projection

#### Step 2.2: Projection Filtering

- For each projection in sinogram:
  1. Perform 1D FFT using `Transforms.computeFFT1D()`
  2. Multiply by filter in frequency domain
  3. Perform inverse 1D FFT
  4. Extract filtered projection (real part)

#### Step 2.3: Backprojection

- Implement `backprojectFiltered()` method
- Initialize output image (zeros)
- For each angle:
  1. Get filtered projection for that angle
  2. For each pixel in output image:
     - Calculate which detector position it came from at this angle
     - Use interpolation to get projection value
     - Add to pixel value
  3. Normalize by number of angles

#### Step 2.4: Inverse Transform Operation

- Extend `RadonTransformOp` to support inverse mode
- Add filter selection parameter
- Display reconstructed image as Function2D

### Phase 3: GUI Integration & Polish

**Goal**: User-friendly interface with configurability

#### Step 3.1: Operation Options Panel

- Create options dialog for Radon transform:
  - Radio buttons: Forward / Inverse
  - Spinner: Number of angles (default: 180)
  - Dropdown: Filter type (for inverse)
  - Dropdown: Interpolation method

#### Step 3.2: Menu Integration

- Add "Radon Transform" to Transform menu
- Add keyboard shortcut
- Enable/disable based on function selection

#### Step 3.3: Testing & Validation

- Test with Shepp-Logan phantom (standard CT test image)
- Test with simple geometric shapes
- Verify reconstruction quality with different:
  - Number of angles (30, 60, 90, 180)
  - Filter types
  - Image sizes (256×256, 512×512)

## File Structure

```
src/main/java/signals/
├── operation/
│   ├── RadonTransformOp.java          # Main operation class
│   ├── RadonProjection.java           # Projection utilities
│   ├── RadonFilters.java              # Filter generation
│   └── ImageRotation.java             # Rotation utilities
├── gui/operation/
│   └── RadonTransformOptionsPanel.java # GUI options
└── action/
    └── RadonTransformAction.java      # Menu action (if needed)
```

## Mathematical Background

### Forward Radon Transform

```
R(θ, t) = ∫∫ f(x,y) δ(x cosθ + y sinθ - t) dx dy
```

- Integrate along lines at angle θ
- t is the distance from origin along perpendicular to line
- Result is projection at angle θ

### Filtered Backprojection (Inverse)

```
f(x,y) = ∫[0,π] R_filtered(θ, x cosθ + y sinθ) dθ
```

- R_filtered = IFFT(FFT(R(θ,t)) × Filter(ω))
- Filters prevent blur in reconstruction

### Rho Filter

```
H(ω) = |ω|
```

- Frequency domain: |ω|
- Enhances high frequencies
- Can amplify noise

### Shepp-Logan Filter

```
H(ω) = |ω| × sinc(ω / (2ω_max))
```

- Rolls off at high frequencies
- Reduces noise amplification
- Better for real data

## Testing Strategy

### Unit Tests

1. **Projection Tests**

   - Single pixel → should produce impulse in projection
   - Horizontal line → constant in 0° projection, impulse in 90°
   - Vertical line → impulse in 0° projection, constant in 90°

2. **Rotation Tests**

   - Rotate by 0° → image unchanged
   - Rotate by 90° → image rotated correctly
   - Rotate by 180° → image flipped

3. **Reconstruction Tests**
   - Forward + Inverse → should recover original (with some blur)
   - More angles → better reconstruction quality
   - Different filters → different noise characteristics

### Integration Tests

1. Test with Shepp-Logan phantom
2. Test with real medical image (if available)
3. Test edge cases:
   - Square vs. rectangular images
   - Small images (64×64)
   - Large images (1024×1024)
   - Odd vs. even dimensions

## Performance Considerations

1. **Interpolation Optimization**

   - Bilinear interpolation is O(1) per pixel
   - Consider lookup tables for trigonometric functions

2. **Memory Management**

   - Sinogram size: angles × detector_positions
   - Rotation creates temporary arrays
   - Consider in-place operations where possible

3. **FFT Efficiency**
   - Use existing optimized FFT from `Transforms`
   - Consider power-of-2 sizes for FFT efficiency

## References

1. Roger Easton's "Fourier Methods in Imaging" - Chapter on Radon Transform
2. Kak & Slaney, "Principles of Computerized Tomographic Imaging" (1988)
3. Standard test images: Shepp-Logan phantom

## Success Criteria

### Minimum Viable Product (MVP)

- ✅ Forward transform creates sinogram from 2D image
- ✅ Sinogram displays correctly as 2D function
- ✅ Configurable number of angles (30-180)
- ✅ Circular masking to avoid edge artifacts

### Full Implementation

- ✅ Inverse transform reconstructs image from sinogram
- ✅ Multiple filter options (Rho, Shepp-Logan)
- ✅ Reconstruction quality acceptable (< 10% error for 180 angles)
- ✅ GUI integration with options panel
- ✅ Works with standard test images

### Stretch Goals

- Additional filters (Ram-Lak, Hamming, etc.)
- Parallel beam vs. fan beam geometry
- Real-time preview with angle slider
- 3D visualization of sinogram
- Export sinogram data to CSV

## Timeline Estimate

**Phase 1 (Forward Transform)**: 4-6 hours

- Basic projections: 1 hour
- Image rotation: 1-2 hours
- Multi-angle projections: 1 hour
- Sinogram display: 1 hour
- Testing: 1 hour

**Phase 2 (Inverse Transform)**: 4-6 hours

- Filter implementation: 2 hours
- Backprojection: 2-3 hours
- Testing & validation: 1-2 hours

**Phase 3 (GUI & Polish)**: 2-3 hours

- Options panel: 1 hour
- Menu integration: 0.5 hours
- Final testing: 1 hour

**Total Estimate**: 10-15 hours

## Next Steps

1. Review plan with advisor (Roger Easton Jr)
2. Confirm mathematical approach and filter implementations
3. Start with Phase 1, Step 1.1 (Basic Projection Operations)
4. Implement and test incrementally
5. Get feedback on sinogram display before proceeding to inverse transform
