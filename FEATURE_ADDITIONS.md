# SignalShow Feature Additions & Enhancements

**Status**: 🚧 Research in Progress  
**Purpose**: Identify and document modern signal processing features to enhance the educational and practical value of SignalShow on the Nuthatch Platform.

---

## Table of Contents

1. [Time-Frequency Analysis](#time-frequency-analysis)
2. [Audio Processing & Speech](#audio-processing--speech)
3. [Advanced Image Processing](#advanced-image-processing)
4. [Interactive Visualization](#interactive-visualization)
5. [Machine Learning Integration](#machine-learning-integration)
6. [Modern Communication Systems](#modern-communication-systems)
7. [Numerical Methods & Optimization](#numerical-methods--optimization)
8. [Educational Enhancements](#educational-enhancements)

---

## Time-Frequency Analysis

### Overview

Time-frequency analysis bridges the gap between time-domain and frequency-domain representations, essential for analyzing non-stationary signals (signals whose frequency content changes over time). The original SignalShow has FFT but lacks modern time-frequency tools.

### Proposed Functions

#### 1. Short-Time Fourier Transform (STFT)

**Rationale**:

- STFT is fundamental for analyzing signals with time-varying frequency content
- Essential for audio analysis, speech processing, and vibration analysis
- Provides visual spectrograms that are intuitive for understanding signal evolution
- Bridge between FFT and wavelet analysis

**Educational Value**:

- Demonstrates trade-off between time and frequency resolution
- Shows windowing effects on spectral analysis
- Introduces concepts of overlap and hop size

**Implementation**:

**Julia**:

```julia
# Using DSP.jl
using DSP

function compute_stft(signal, window_size, hop_size, window_type=:hanning)
    # Create window
    window = DSP.Windows.hanning(window_size)

    # Compute STFT
    stft_result = DSP.stft(signal, window_size, hop_size; window=window)

    return stft_result
end

# Alternative: Manual implementation for educational purposes
function stft_manual(x, n_fft, hop_length, window)
    n_frames = div(length(x) - n_fft, hop_length) + 1
    stft_matrix = zeros(ComplexF64, n_fft ÷ 2 + 1, n_frames)

    for i in 1:n_frames
        start_idx = (i-1) * hop_length + 1
        frame = x[start_idx:start_idx + n_fft - 1] .* window
        stft_matrix[:, i] = fft(frame)[1:n_fft ÷ 2 + 1]
    end

    return stft_matrix
end
```

**JavaScript**:

```javascript
// Using Web Audio API or custom implementation
function computeSTFT(signal, windowSize, hopSize) {
  const numFrames = Math.floor((signal.length - windowSize) / hopSize) + 1;
  const stftMatrix = [];

  for (let i = 0; i < numFrames; i++) {
    const start = i * hopSize;
    const frame = signal.slice(start, start + windowSize);

    // Apply window (Hanning)
    const windowed = frame.map(
      (val, idx) => val * 0.5 * (1 - Math.cos((2 * Math.PI * idx) / windowSize))
    );

    // Compute FFT (using fft.js or custom)
    const fftResult = computeFFT(windowed);
    stftMatrix.push(fftResult);
  }

  return stftMatrix;
}
```

**Parameters**:

- Window size (n_fft): 256, 512, 1024, 2048 (power of 2)
- Hop size: typically 25-50% of window size
- Window type: Hanning, Hamming, Blackman
- Overlap percentage: 0-75%

---

#### 2. Spectrogram

**Rationale**:

- Visual representation of STFT magnitude
- Industry standard for audio/speech analysis
- Intuitive time-frequency visualization
- Used in music analysis, sonar, radar, speech recognition

**Educational Value**:

- Shows how frequency content evolves over time
- Demonstrates harmonics, formants, and transients
- Teaches interpretation of time-frequency representations

**Implementation**:

**Julia**:

```julia
using DSP, Plots

function compute_spectrogram(signal, n_fft=1024, hop_length=256)
    # Compute STFT
    S = DSP.spectrogram(signal, n_fft, hop_length)

    # Convert to dB scale
    S_db = 10 * log10.(abs.(S.power) .+ 1e-10)

    return S_db, S.freq, S.time
end

# Visualization
function plot_spectrogram(signal, fs)
    S_db, freqs, times = compute_spectrogram(signal)

    heatmap(times, freqs, S_db,
            xlabel="Time (s)",
            ylabel="Frequency (Hz)",
            title="Spectrogram",
            colorbar_title="Power (dB)")
end
```

**JavaScript**:

```javascript
// Generate spectrogram from STFT
function generateSpectrogram(stftMatrix) {
  // Convert complex STFT to magnitude (dB)
  return stftMatrix.map((frame) =>
    frame.map((complexVal) => {
      const magnitude = Math.sqrt(complexVal.re ** 2 + complexVal.im ** 2);
      return 10 * Math.log10(magnitude + 1e-10);
    })
  );
}

// Visualization using Canvas or Plotly
function drawSpectrogram(canvas, spectrogramData) {
  const ctx = canvas.getContext("2d");
  const imageData = ctx.createImageData(canvas.width, canvas.height);

  // Map spectrogram values to colors (viridis, plasma, etc.)
  spectrogramData.forEach((frame, timeIdx) => {
    frame.forEach((value, freqIdx) => {
      const color = valueToColor(value, -80, 0); // dB range
      setPixel(imageData, timeIdx, freqIdx, color);
    });
  });

  ctx.putImageData(imageData, 0, 0);
}
```

---

#### 3. Wavelet Transform

**Rationale**:

- Provides multi-resolution analysis (analyzes signals at different scales)
- Better time resolution at high frequencies, better frequency resolution at low frequencies
- Essential for analyzing transient signals, discontinuities, edges
- Used in image compression (JPEG2000), denoising, feature extraction

**Educational Value**:

- Demonstrates alternative to Fourier analysis
- Shows concept of scale vs. frequency
- Introduces mother wavelets (Haar, Morlet, Daubechies)

**Implementation**:

**Julia**:

```julia
using Wavelets

function compute_cwt(signal, scales, wavelet=:morlet)
    # Continuous Wavelet Transform
    cwt_result = cwt(signal, wavelet, scales)
    return cwt_result
end

function compute_dwt(signal, wavelet=:db4, levels=5)
    # Discrete Wavelet Transform
    dwt_result = dwt(signal, wavelet(levels))
    return dwt_result
end

# Mother wavelets available
# - Haar: WT.haar
# - Daubechies: WT.db1, WT.db2, ..., WT.db10
# - Symlets: WT.sym2, WT.sym4, ..., WT.sym10
# - Coiflets: WT.coif1, WT.coif2, ..., WT.coif5
# - Morlet: for CWT
```

**JavaScript**:

```javascript
// Basic Haar wavelet transform (educational implementation)
function haarWaveletTransform(signal) {
  const transformed = [...signal];
  let length = signal.length;

  while (length > 1) {
    const temp = [];

    // Averaging (approximation coefficients)
    for (let i = 0; i < length; i += 2) {
      temp.push((transformed[i] + transformed[i + 1]) / Math.sqrt(2));
    }

    // Differencing (detail coefficients)
    for (let i = 0; i < length; i += 2) {
      temp.push((transformed[i] - transformed[i + 1]) / Math.sqrt(2));
    }

    transformed.splice(0, length, ...temp);
    length /= 2;
  }

  return transformed;
}

// For production: use wavelet.js or jswt library
```

**Parameters**:

- Wavelet family: Haar, Daubechies (db1-db10), Morlet, Mexican Hat
- Decomposition levels: 1-10 (depends on signal length)
- Scales: for CWT, array of scale values

**Educational Applications**:

- Signal denoising
- Edge detection
- Compression
- Transient analysis

---

#### 4. Hilbert Transform & Analytic Signal

**Rationale**:

- Computes instantaneous amplitude and phase
- Essential for envelope detection, amplitude demodulation
- Used in radar, communications, seismic analysis
- Enables instantaneous frequency estimation

**Educational Value**:

- Demonstrates concept of analytic signal
- Shows relationship between real signals and complex representation
- Teaches instantaneous frequency vs. spectral frequency

**Implementation**:

**Julia**:

```julia
using DSP

function hilbert_transform(x)
    # Compute analytic signal via FFT
    N = length(x)
    X = fft(x)

    # Create frequency-domain filter
    h = zeros(N)
    h[1] = 1
    h[2:N÷2] .= 2
    h[N÷2 + 1] = 1

    # Apply filter and transform back
    z = ifft(X .* h)

    return z
end

function instantaneous_envelope(x)
    z = hilbert_transform(x)
    return abs.(z)
end

function instantaneous_phase(x)
    z = hilbert_transform(x)
    return angle.(z)
end

function instantaneous_frequency(x, fs)
    phase = instantaneous_phase(x)
    # Unwrap phase
    phase_unwrapped = unwrap(phase)
    # Compute derivative
    inst_freq = diff(phase_unwrapped) * fs / (2π)
    return inst_freq
end
```

**JavaScript**:

```javascript
function hilbertTransform(signal) {
  const N = signal.length;

  // FFT of input
  const X = fft(signal);

  // Create Hilbert filter in frequency domain
  const H = new Array(N).fill(0);
  H[0] = 1;
  for (let i = 1; i < N / 2; i++) {
    H[i] = 2;
  }
  H[N / 2] = 1;

  // Apply filter and inverse FFT
  const filtered = X.map((val, i) => multiplyComplex(val, H[i]));
  const analyticSignal = ifft(filtered);

  return analyticSignal;
}

function instantaneousEnvelope(signal) {
  const analytic = hilbertTransform(signal);
  return analytic.map((c) => Math.sqrt(c.re ** 2 + c.im ** 2));
}
```

---

#### 5. Mel-Frequency Cepstral Coefficients (MFCCs)

**Rationale**:

- Standard feature extraction for speech and audio
- Used in speech recognition, speaker identification, music analysis
- Mimics human auditory perception (mel scale)
- Compact representation of spectral envelope

**Educational Value**:

- Introduces perceptual audio processing
- Demonstrates filter banks and cepstral analysis
- Shows connection between signal processing and machine learning

**Implementation**:

**Julia**:

```julia
using DSP

function mel_scale(freq)
    # Convert frequency to mel scale
    return 2595 * log10(1 + freq / 700)
end

function mel_to_freq(mel)
    # Convert mel scale to frequency
    return 700 * (10^(mel / 2595) - 1)
end

function create_mel_filterbank(n_filters, n_fft, sample_rate, fmin=0, fmax=nothing)
    fmax = fmax === nothing ? sample_rate / 2 : fmax

    # Create mel-spaced frequencies
    mel_min = mel_scale(fmin)
    mel_max = mel_scale(fmax)
    mel_points = range(mel_min, mel_max, length=n_filters + 2)
    freq_points = mel_to_freq.(mel_points)

    # Create filter bank
    filterbank = zeros(n_filters, n_fft ÷ 2 + 1)

    for i in 1:n_filters
        # Triangular filters
        for j in 1:(n_fft ÷ 2 + 1)
            freq = j * sample_rate / n_fft

            if freq >= freq_points[i] && freq <= freq_points[i+1]
                filterbank[i, j] = (freq - freq_points[i]) /
                                   (freq_points[i+1] - freq_points[i])
            elseif freq >= freq_points[i+1] && freq <= freq_points[i+2]
                filterbank[i, j] = (freq_points[i+2] - freq) /
                                   (freq_points[i+2] - freq_points[i+1])
            end
        end
    end

    return filterbank
end

function compute_mfcc(signal, sample_rate, n_mfcc=13, n_fft=512, hop_length=256)
    # Compute spectrogram
    S = spectrogram(signal, n_fft, hop_length)
    power_spectrum = abs.(S.power)

    # Apply mel filterbank
    mel_filterbank = create_mel_filterbank(40, n_fft, sample_rate)
    mel_spectrum = mel_filterbank * power_spectrum

    # Log compression
    log_mel = log10.(mel_spectrum .+ 1e-10)

    # DCT to get MFCCs
    mfcc = dct(log_mel, 2)[1:n_mfcc, :]

    return mfcc
end
```

**JavaScript** - Would require significant implementation or use of libraries like `meyda.js`

---

#### 6. Radon Transform

**Rationale**:

- Fundamental to computed tomography (CT) and medical imaging
- Maps 2D image to line integrals at various angles
- Inverse Radon transform reconstructs images from projections
- Essential for understanding medical imaging, non-destructive testing, astronomy
- Used in seismology, radar imaging, and computer vision

**Educational Value**:

- Demonstrates relationship between projections and image reconstruction
- Shows practical application of integral transforms beyond Fourier
- Teaches concepts fundamental to medical imaging (CT scans, PET scans)
- Illustrates ill-posed inverse problems and reconstruction algorithms
- Connects geometry, integrals, and image processing

**Mathematical Definition**:
The Radon transform maps a 2D function f(x,y) to line integrals:

```
R{f}(ρ, θ) = ∫∫ f(x,y) δ(x cos θ + y sin θ - ρ) dx dy
```

Where:

- ρ (rho) = perpendicular distance from origin to line
- θ (theta) = angle of the line normal
- δ = Dirac delta function

The result is a sinogram: projections at different angles.

**Implementation**:

**Julia**:

```julia
using Images, ImageTransformations

function radon_transform(img, theta_range=0:1:179)
    # Using Radon.jl package
    # or manual implementation

    height, width = size(img)
    num_angles = length(theta_range)

    # Calculate diagonal length for rho range
    diag_length = ceil(Int, sqrt(height^2 + width^2))
    rho_range = -diag_length:diag_length

    # Initialize sinogram
    sinogram = zeros(length(rho_range), num_angles)

    # Center coordinates
    center_x = width / 2
    center_y = height / 2

    for (angle_idx, theta) in enumerate(theta_range)
        theta_rad = deg2rad(theta)
        cos_theta = cos(theta_rad)
        sin_theta = sin(theta_rad)

        for (rho_idx, rho) in enumerate(rho_range)
            # For each line defined by (rho, theta), sum pixels
            # Line equation: x*cos(θ) + y*sin(θ) = ρ

            if abs(sin_theta) > abs(cos_theta)
                # Iterate over x
                for x in 1:width
                    # Solve for y: y = (ρ - x*cos(θ)) / sin(θ)
                    x_centered = x - center_x
                    y_val = (rho - x_centered * cos_theta) / sin_theta + center_y

                    y = round(Int, y_val)
                    if 1 <= y <= height
                        # Bilinear interpolation for sub-pixel accuracy
                        sinogram[rho_idx, angle_idx] += interpolate_pixel(img, x, y_val)
                    end
                end
            else
                # Iterate over y
                for y in 1:height
                    # Solve for x: x = (ρ - y*sin(θ)) / cos(θ)
                    y_centered = y - center_y
                    x_val = (rho - y_centered * sin_theta) / cos_theta + center_x

                    x = round(Int, x_val)
                    if 1 <= x <= width
                        sinogram[rho_idx, angle_idx] += interpolate_pixel(img, x_val, y)
                    end
                end
            end
        end
    end

    return sinogram, rho_range, theta_range
end

function interpolate_pixel(img, x, y)
    # Bilinear interpolation for fractional coordinates
    x1, y1 = floor(Int, x), floor(Int, y)
    x2, y2 = ceil(Int, x), ceil(Int, y)

    if x1 < 1 || x2 > size(img, 2) || y1 < 1 || y2 > size(img, 1)
        return 0.0
    end

    # Interpolation weights
    wx = x - x1
    wy = y - y1

    return (1-wx)*(1-wy)*img[y1,x1] + wx*(1-wy)*img[y1,x2] +
           (1-wx)*wy*img[y2,x1] + wx*wy*img[y2,x2]
end

# Inverse Radon Transform (Filtered Back-Projection)
function inverse_radon(sinogram, theta_range, output_size)
    # Filtered back-projection algorithm
    num_angles = length(theta_range)
    reconstruction = zeros(output_size...)

    # 1. Filter sinogram (ramp filter in frequency domain)
    filtered_sinogram = filter_sinogram(sinogram)

    # 2. Back-project filtered sinogram
    center_x = output_size[2] / 2
    center_y = output_size[1] / 2

    for (angle_idx, theta) in enumerate(theta_range)
        theta_rad = deg2rad(theta)
        cos_theta = cos(theta_rad)
        sin_theta = sin(theta_rad)

        for y in 1:output_size[1]
            for x in 1:output_size[2]
                # Calculate rho for this pixel
                x_centered = x - center_x
                y_centered = y - center_y
                rho = x_centered * cos_theta + y_centered * sin_theta

                # Interpolate sinogram value
                reconstruction[y, x] += interpolate_sinogram(
                    filtered_sinogram[:, angle_idx], rho
                )
            end
        end
    end

    # Normalize by number of angles
    reconstruction ./= num_angles

    return reconstruction
end

function filter_sinogram(sinogram)
    # Apply ramp filter in Fourier domain
    n_rho, n_angles = size(sinogram)
    filtered = similar(sinogram)

    # Create ramp filter
    freq = fftfreq(n_rho)
    ramp_filter = abs.(freq)

    for i in 1:n_angles
        # FFT, multiply by filter, inverse FFT
        projection_fft = fft(sinogram[:, i])
        filtered[:, i] = real(ifft(projection_fft .* ramp_filter))
    end

    return filtered
end
```

**JavaScript**:

```javascript
class RadonTransform {
  constructor(image, thetaStep = 1) {
    this.image = image;
    this.height = image.length;
    this.width = image[0].length;
    this.thetaRange = [];
    for (let theta = 0; theta < 180; theta += thetaStep) {
      this.thetaRange.push(theta);
    }

    // Calculate diagonal for rho range
    this.diagLength = Math.ceil(Math.sqrt(this.height ** 2 + this.width ** 2));
  }

  compute() {
    const numAngles = this.thetaRange.length;
    const numRho = 2 * this.diagLength + 1;
    const sinogram = Array(numRho)
      .fill(0)
      .map(() => Array(numAngles).fill(0));

    const centerX = this.width / 2;
    const centerY = this.height / 2;

    this.thetaRange.forEach((theta, angleIdx) => {
      const thetaRad = (theta * Math.PI) / 180;
      const cosTheta = Math.cos(thetaRad);
      const sinTheta = Math.sin(thetaRad);

      for (let rho = -this.diagLength; rho <= this.diagLength; rho++) {
        const rhoIdx = rho + this.diagLength;

        if (Math.abs(sinTheta) > Math.abs(cosTheta)) {
          // Iterate over x
          for (let x = 0; x < this.width; x++) {
            const xCentered = x - centerX;
            const yVal = (rho - xCentered * cosTheta) / sinTheta + centerY;
            const y = Math.round(yVal);

            if (y >= 0 && y < this.height) {
              sinogram[rhoIdx][angleIdx] += this.interpolatePixel(x, yVal);
            }
          }
        } else {
          // Iterate over y
          for (let y = 0; y < this.height; y++) {
            const yCentered = y - centerY;
            const xVal = (rho - yCentered * sinTheta) / cosTheta + centerX;
            const x = Math.round(xVal);

            if (x >= 0 && x < this.width) {
              sinogram[rhoIdx][angleIdx] += this.interpolatePixel(xVal, y);
            }
          }
        }
      }
    });

    return {
      sinogram: sinogram,
      rhoRange: Array.from({ length: numRho }, (_, i) => i - this.diagLength),
      thetaRange: this.thetaRange,
    };
  }

  interpolatePixel(x, y) {
    const x1 = Math.floor(x);
    const y1 = Math.floor(y);
    const x2 = Math.ceil(x);
    const y2 = Math.ceil(y);

    if (x1 < 0 || x2 >= this.width || y1 < 0 || y2 >= this.height) {
      return 0;
    }

    const wx = x - x1;
    const wy = y - y1;

    return (
      (1 - wx) * (1 - wy) * this.image[y1][x1] +
      wx * (1 - wy) * this.image[y1][x2] +
      (1 - wx) * wy * this.image[y2][x1] +
      wx * wy * this.image[y2][x2]
    );
  }

  // Inverse Radon Transform
  inverseRadon(sinogram, outputSize) {
    const reconstruction = Array(outputSize[0])
      .fill(0)
      .map(() => Array(outputSize[1]).fill(0));

    // 1. Filter sinogram (ramp filter)
    const filtered = this.filterSinogram(sinogram);

    // 2. Back-projection
    const centerX = outputSize[1] / 2;
    const centerY = outputSize[0] / 2;

    this.thetaRange.forEach((theta, angleIdx) => {
      const thetaRad = (theta * Math.PI) / 180;
      const cosTheta = Math.cos(thetaRad);
      const sinTheta = Math.sin(thetaRad);

      for (let y = 0; y < outputSize[0]; y++) {
        for (let x = 0; x < outputSize[1]; x++) {
          const xCentered = x - centerX;
          const yCentered = y - centerY;
          const rho = xCentered * cosTheta + yCentered * sinTheta;

          reconstruction[y][x] += this.interpolateSinogram(
            filtered.map((row) => row[angleIdx]),
            rho
          );
        }
      }
    });

    // Normalize
    const numAngles = this.thetaRange.length;
    return reconstruction.map((row) => row.map((val) => val / numAngles));
  }

  filterSinogram(sinogram) {
    // Apply ramp filter via FFT
    // (Would use fft.js in practice)
    return sinogram; // Placeholder
  }
}

// Usage example:
// const radon = new RadonTransform(imageData);
// const {sinogram, rhoRange, thetaRange} = radon.compute();
// const reconstructed = radon.inverseRadon(sinogram, [height, width]);
```

**Parameters**:

- **Theta range**: 0° to 180° (angular sampling)
  - Default: 1° steps (180 projections)
  - Fine sampling: 0.5° steps (360 projections)
  - Coarse: 2° or 5° steps (for speed)
- **Interpolation method**:
  - Nearest neighbor (fast, low quality)
  - Bilinear (good balance)
  - Bicubic (high quality, slower)
- **Filter type** (for inverse transform):
  - Ramp filter (standard)
  - Shepp-Logan filter (reduces artifacts)
  - Cosine filter (smoother)
  - Hamming filter (reduces noise)

**Educational Applications**:

1. **Medical Imaging**:

   - Demonstrate how CT scanners work
   - Show relationship between X-ray projections and internal structure
   - Illustrate image reconstruction from projections

2. **Inverse Problems**:

   - Show that forward transform is easy, inverse is challenging
   - Demonstrate artifacts from insufficient projections
   - Teach about ill-posed problems and regularization

3. **Geometric Understanding**:

   - Visualize how lines at different angles sample 2D space
   - Show sinogram patterns for simple shapes (circles, rectangles)
   - Demonstrate aliasing in projection space

4. **Filtered Back-Projection**:
   - Show why filtering is necessary (1/r blur without it)
   - Demonstrate different filter effects
   - Teach reconstruction algorithms

**Visualization Features**:

- **Forward transform**: Display original image and resulting sinogram side-by-side
- **Interactive angle**: Show which line in image corresponds to which row in sinogram
- **Inverse transform**: Animate back-projection process step-by-step
- **Reconstruction quality**: Compare results with different numbers of projections (10, 30, 90, 180)
- **Artifacts**: Demonstrate streaking, aliasing, and noise propagation

**Julia Packages**:

- `Radon.jl` - Direct implementation of Radon/inverse Radon transforms
- `Images.jl` - Image handling and interpolation
- `FFTW.jl` - Fast filtering operations
- `ImageTransformations.jl` - Geometric transformations

**JavaScript Libraries**:

- Custom implementation (as shown above)
- `image-js` - For image handling
- `fft.js` - For frequency domain filtering
- WebGL shaders - For GPU-accelerated back-projection

**Connection to Existing SignalShow Features**:

- Uses 2D convolution concepts (from existing operations)
- Applies Fourier transforms (existing FFT operations)
- Extends projection/summation ideas to geometric lines
- Complements existing 2D image processing functions

**Implementation Priority**: **Medium-High**

- High educational value for medical/scientific imaging
- Demonstrates advanced transform beyond Fourier
- Computationally intensive but feasible with optimization
- Strong visual/interactive potential

---

## Audio Processing & Speech

### Overview

Audio processing is a major application of DSP. Adding audio-specific functions would make SignalShow valuable for music technology, speech processing, and acoustic analysis education.

### Proposed Functions

#### 1. Pitch Detection (Autocorrelation & YIN algorithm)

**Rationale**:

- Fundamental for music analysis, tuners, speech processing
- Demonstrates autocorrelation application
- YIN algorithm is industry standard

**Educational Value**:

- Shows practical application of correlation
- Demonstrates peak finding and frequency estimation
- Teaches difference between time-domain and frequency-domain pitch detection

**Implementation**:

**Julia**:

```julia
function autocorrelation_pitch(signal, sample_rate, min_freq=80, max_freq=400)
    # Autocorrelation method
    min_lag = Int(floor(sample_rate / max_freq))
    max_lag = Int(ceil(sample_rate / min_freq))

    # Compute autocorrelation
    r = xcorr(signal, signal)

    # Find peak in valid range
    search_range = r[(length(signal) + min_lag):(length(signal) + max_lag)]
    peak_idx = argmax(search_range)

    # Convert lag to frequency
    pitch = sample_rate / (min_lag + peak_idx - 1)

    return pitch
end

function yin_pitch(signal, sample_rate, threshold=0.1)
    # YIN pitch detection algorithm
    # More accurate than simple autocorrelation

    # ... YIN implementation
    # See: "YIN, a fundamental frequency estimator for speech and music"
    # by Alain de Cheveigné and Hideki Kawahara
end
```

---

#### 2. Audio Effects (Reverb, Echo, Chorus, Flanger)

**Rationale**:

- Popular for music technology education
- Demonstrates delay lines, feedback, modulation
- Fun, engaging examples for students

**Educational Value**:

- Shows practical DSP applications
- Teaches delay-based effects
- Demonstrates LFO modulation

**Implementation Examples**:

**Julia**:

```julia
function simple_echo(signal, delay_samples, decay=0.5)
    output = copy(signal)
    for i in (delay_samples+1):length(signal)
        output[i] += decay * signal[i - delay_samples]
    end
    return output
end

function simple_reverb(signal, room_size=0.5, damping=0.5)
    # Schroeder reverb using comb and allpass filters
    # ... implementation
end

function chorus(signal, sample_rate, depth=0.002, rate=1.0)
    # Chorus effect using delayed and modulated copies
    lfo = sin.(2π * rate * (0:length(signal)-1) / sample_rate)
    delay_samples = depth * sample_rate * (1 .+ lfo)

    # Variable delay line implementation
    # ... interpolation for fractional delays
end
```

---

## Advanced Image Processing

### Proposed Functions

#### 1. Edge Detection (Sobel, Canny, Laplacian)

**Rationale**:

- Fundamental image processing operation
- Demonstrates derivative filters
- Used in computer vision, medical imaging

**Implementation**:

**Julia**:

```julia
using Images

function sobel_edge_detection(img)
    # Sobel kernels
    kernel_x = [-1 0 1; -2 0 2; -1 0 1]
    kernel_y = [-1 -2 -1; 0 0 0; 1 2 1]

    # Convolve with image
    grad_x = imfilter(img, kernel_x)
    grad_y = imfilter(img, kernel_y)

    # Compute gradient magnitude
    magnitude = sqrt.(grad_x.^2 + grad_y.^2)

    return magnitude
end

function canny_edge_detection(img, low_threshold, high_threshold)
    # Full Canny algorithm
    # 1. Gaussian blur
    # 2. Gradient computation
    # 3. Non-maximum suppression
    # 4. Double thresholding
    # 5. Edge tracking by hysteresis

    # Use Images.jl canny function
    edges = canny(img, (low_threshold, high_threshold))
    return edges
end
```

---

#### 2. Morphological Operations

**Rationale**:

- Essential for binary image processing
- Used in shape analysis, noise removal, segmentation

**Operations**: Erosion, Dilation, Opening, Closing, Morphological Gradient

---

#### 3. Frequency Domain Filtering

**Rationale**:

- Extends 1D filtering concepts to 2D
- Demonstrates ideal vs. practical filters

**Types**:

- Low-pass: Ideal, Butterworth, Gaussian
- High-pass: Edge enhancement
- Band-pass/Band-reject: Notch filters for periodic noise
- Homomorphic filtering: Illumination normalization

---

## Interactive Visualization

### Proposed Features

#### 1. Waterfall Plot

**Rationale**:

- Shows signal evolution over time in 3D
- Used in spectrum analyzers, radar displays
- Intuitive for showing temporal changes

---

#### 2. Real-time Parameter Manipulation

**Rationale**:

- Interactive learning is more engaging
- Demonstrates cause-and-effect relationships
- Enables exploration and experimentation

**Features**:

- Live sliders for all function parameters
- Real-time plot updates (debounced for performance)
- Before/after comparison view

---

#### 3. Animation & Time Evolution

**Rationale**:

- Visualize signal propagation
- Show filter impulse response
- Demonstrate sampling and aliasing dynamically

---

## Machine Learning Integration

### Proposed Features

#### 1. Signal Classification

**Rationale**:

- Modern applications use ML for signal analysis
- Demonstrates feature extraction + classification pipeline

**Examples**:

- Audio: Music genre, speech vs. noise, instrument recognition
- Biomedical: ECG classification, EEG patterns
- Communications: Modulation recognition

---

#### 2. Anomaly Detection

**Rationale**:

- Important for quality control, system monitoring
- Teaches statistical methods and thresholds

---

## Backend Implementation Strategy

### Overview

Based on the hybrid backend architecture documented in [TECH_STACK.md](./TECH_STACK.md), [ARCHITECTURE.md](./ARCHITECTURE.md), [RUST_DSP_RESEARCH.md](./RUST_DSP_RESEARCH.md), and [JAVASCRIPT_DSP_RESEARCH.md](./JAVASCRIPT_DSP_RESEARCH.md), each advanced feature has been evaluated for web deployment feasibility.

### Feature Implementation Matrix

| Feature                              | Desktop (Julia)     | Web (Rust WASM)     | Web (JavaScript)    | Priority | Notes                                           |
| ------------------------------------ | ------------------- | ------------------- | ------------------- | -------- | ----------------------------------------------- |
| **STFT**                             | ✅ DSP.jl           | ✅ rustfft + dasp   | ⚠️ fft.js (slow)    | **v1.0** | Rust WASM primary, JS fallback                  |
| **Spectrogram**                      | ✅ DSP.jl           | ✅ STFT + magnitude | ✅ Canvas rendering | **v1.0** | Visualization in browser, compute via WASM      |
| **Wavelet Transform (Basic)**        | ✅ Wavelets.jl      | ✅ Rust (Haar, Db)  | ❌ Desktop only     | v1.5     | Basic wavelets in WASM, advanced Julia only     |
| **Wavelet Transform (Advanced)**     | ✅ Wavelets.jl      | ❌ Desktop only     | ❌ Desktop only     | v2.0     | Complex wavelets (Morlet, Mexican hat) too slow |
| **Hilbert Transform**                | ✅ DSP.jl           | ✅ FFT-based        | ⚠️ fft.js (slow)    | v1.5     | Via FFT: FFT → zero negative → IFFT             |
| **Analytic Signal**                  | ✅ DSP.jl           | ✅ Hilbert + signal | ⚠️ fft.js (slow)    | v1.5     | x(t) + i·H[x(t)]                                |
| **MFCCs**                            | ✅ MFCC.jl          | ✅ Rust custom      | ❌ Desktop only     | v1.5     | FFT + mel filterbank + DCT (Rust WASM)          |
| **Radon Transform**                  | ✅ ImagePhantoms.jl | ❌ Desktop only     | ❌ Desktop only     | v2.0     | Computationally intensive (CT reconstruction)   |
| **Pitch Detection**                  | ✅ Custom           | ✅ Autocorrelation  | ✅ JavaScript       | v1.5     | Autocorrelation-based, lightweight              |
| **Audio Effects (Reverb/Echo)**      | ✅ Custom           | ✅ dasp             | ✅ Web Audio API    | v2.0     | Web Audio API preferred for browser             |
| **Edge Detection (Sobel, Canny)**    | ✅ Images.jl        | ✅ ndarray          | ✅ Canvas/ImageData | v1.5     | 2D convolution with Sobel kernels               |
| **Morphological Operations**         | ✅ Images.jl        | ✅ ndarray          | ⚠️ Slow             | v1.5     | Erosion, dilation, opening, closing             |
| **Frequency Domain Filtering**       | ✅ DSP.jl           | ✅ rustfft          | ⚠️ fft.js (slow)    | **v1.0** | FFT → mask → IFFT                               |
| **Waterfall Plot**                   | ✅ Plots.jl         | ✅ WebGL/Canvas     | ✅ WebGL/Canvas     | v1.5     | Visualization only, data from backend           |
| **Real-time Parameter Manipulation** | ✅ WebSocket        | ✅ Local compute    | ✅ Local compute    | **v1.0** | Compute in browser for responsiveness           |
| **Signal Classification (ML)**       | ✅ Flux.jl          | ❌ Desktop only     | ❌ Desktop only     | v2.0     | ML models too large for WASM                    |

**Legend**:

- ✅ **Fully supported** - Good performance, recommended
- ⚠️ **Partial support** - Available but slow, use as fallback
- ❌ **Desktop only** - Not practical for web deployment

---

### Detailed Implementation Notes

#### 1. Short-Time Fourier Transform (STFT)

**Desktop Implementation**:

```julia
using DSP
S = DSP.stft(signal, window_size, hop_size; window=hanning)
```

**Web Implementation (Rust WASM)**:

```rust
use rustfft::{FftPlanner, num_complex::Complex};
use dasp::signal;

// STFT implementation using rustfft
fn stft(signal: &[f64], window_size: usize, hop_size: usize) -> Vec<Vec<Complex<f64>>> {
    let mut planner = FftPlanner::new();
    let fft = planner.plan_fft_forward(window_size);

    let num_frames = (signal.len() - window_size) / hop_size + 1;
    let mut result = Vec::new();

    for i in 0..num_frames {
        let start = i * hop_size;
        let mut frame: Vec<Complex<f64>> = signal[start..start+window_size]
            .iter()
            .enumerate()
            .map(|(j, &x)| {
                let hann = 0.5 * (1.0 - ((2.0 * PI * j as f64) / window_size as f64).cos());
                Complex::new(x * hann, 0.0)
            })
            .collect();

        fft.process(&mut frame);
        result.push(frame);
    }

    result
}
```

**Web Implementation (JavaScript Fallback)**:

```javascript
// Using fft.js
import FFT from "fft.js";

function stft(signal, windowSize, hopSize) {
  const fft = new FFT(windowSize);
  const numFrames = Math.floor((signal.length - windowSize) / hopSize) + 1;
  const result = [];

  for (let i = 0; i < numFrames; i++) {
    const start = i * hopSize;
    const frame = signal.slice(start, start + windowSize);

    // Apply Hanning window
    const windowed = frame.map(
      (x, j) => x * 0.5 * (1 - Math.cos((2 * Math.PI * j) / windowSize))
    );

    // Compute FFT
    const spectrum = fft.createComplexArray();
    fft.realTransform(spectrum, windowed);
    result.push(spectrum);
  }

  return result;
}
```

**Bundle Impact**: Rust WASM ~60KB, JavaScript ~3KB  
**Performance**: Rust WASM 60-95% native, JS ~5-10% native  
**Strategy**: Use Rust WASM primary, fallback to JS for compatibility

---

#### 2. Spectrogram

**Implementation**: Built on STFT (above)

**Desktop**: DSP.jl `spectrogram()` function  
**Web**: STFT magnitude → convert to dB → render with Canvas/WebGL  
**Bundle Impact**: No additional libraries (uses STFT)  
**Performance**: Real-time for audio signals (<10ms for 1024-point FFT)

---

#### 3. Wavelet Transform

**Desktop Implementation (Full)**:

```julia
using Wavelets
wt = dwt(signal, wavelet(:db4))  # Daubechies-4
```

**Web Implementation (Basic Wavelets Only)**:

```rust
// Haar wavelet (simplest) in Rust
fn haar_dwt(signal: &[f64]) -> (Vec<f64>, Vec<f64>) {
    let n = signal.len() / 2;
    let mut approx = vec![0.0; n];
    let mut detail = vec![0.0; n];

    for i in 0..n {
        approx[i] = (signal[2*i] + signal[2*i+1]) / std::f64::consts::SQRT_2;
        detail[i] = (signal[2*i] - signal[2*i+1]) / std::f64::consts::SQRT_2;
    }

    (approx, detail)
}

// Daubechies-4 (basic orthogonal wavelet)
fn db4_dwt(signal: &[f64]) -> (Vec<f64>, Vec<f64>) {
    // Filter coefficients for Daubechies-4
    const H: [f64; 4] = [
        (1.0 + 3f64.sqrt()) / (4.0 * 2f64.sqrt()),
        (3.0 + 3f64.sqrt()) / (4.0 * 2f64.sqrt()),
        (3.0 - 3f64.sqrt()) / (4.0 * 2f64.sqrt()),
        (1.0 - 3f64.sqrt()) / (4.0 * 2f64.sqrt()),
    ];

    // Convolution-based implementation
    // ... (full implementation)
}
```

**Desktop-Only Wavelets**:

- Morlet wavelet (complex, continuous)
- Mexican hat (Ricker wavelet)
- Complex Gaussian wavelets
- Biorthogonal wavelets

**Reason**: Advanced wavelets require complex convolution kernels and are computationally expensive. Basic Haar and Daubechies wavelets are sufficient for education and can run in WASM.

**Bundle Impact**: Basic wavelets ~10KB, advanced wavelets desktop-only  
**Performance**: Basic wavelets ~50% native (acceptable), advanced too slow

---

#### 4. Hilbert Transform & Analytic Signal

**Desktop Implementation**:

```julia
using DSP
analytic = hilbert(signal)  # Returns complex analytic signal
envelope = abs.(analytic)
phase = angle.(analytic)
```

**Web Implementation (Rust WASM)**:

```rust
use rustfft::{FftPlanner, num_complex::Complex};

fn hilbert_transform(signal: &[f64]) -> Vec<Complex<f64>> {
    let n = signal.len();
    let mut planner = FftPlanner::new();

    // Forward FFT
    let fft = planner.plan_fft_forward(n);
    let mut buffer: Vec<Complex<f64>> = signal.iter()
        .map(|&x| Complex::new(x, 0.0))
        .collect();
    fft.process(&mut buffer);

    // Zero out negative frequencies (keep DC and Nyquist)
    buffer[n/2+1..].iter_mut().for_each(|x| *x = Complex::new(0.0, 0.0));
    // Double positive frequencies
    buffer[1..n/2].iter_mut().for_each(|x| *x = *x * 2.0);

    // Inverse FFT
    let ifft = planner.plan_fft_inverse(n);
    ifft.process(&mut buffer);

    // Normalize
    buffer.iter().map(|x| *x / n as f64).collect()
}
```

**Bundle Impact**: Reuses rustfft (~60KB already included)  
**Performance**: 60-95% native with WASM SIMD  
**Strategy**: FFT-based implementation is efficient in WASM

---

#### 5. Mel-Frequency Cepstral Coefficients (MFCCs)

**Desktop Implementation**:

```julia
using MFCC
mfccs = mfcc(signal, sample_rate)
```

**Web Implementation (Rust WASM - Custom)**:

```rust
// MFCC pipeline: FFT → Power spectrum → Mel filterbank → Log → DCT

fn mfcc(signal: &[f64], n_mfcc: usize, n_fft: usize, n_mels: usize) -> Vec<f64> {
    // 1. Compute power spectrum via FFT
    let power_spectrum = power_spectrum_fft(signal, n_fft);

    // 2. Apply mel filterbank
    let mel_spectrum = apply_mel_filterbank(&power_spectrum, n_mels);

    // 3. Log compression
    let log_mel: Vec<f64> = mel_spectrum.iter()
        .map(|&x| (x + 1e-10).ln())
        .collect();

    // 4. DCT (Discrete Cosine Transform)
    dct(&log_mel, n_mfcc)
}

fn apply_mel_filterbank(power_spec: &[f64], n_mels: usize) -> Vec<f64> {
    // Mel scale: mel = 2595 * log10(1 + f/700)
    // Create triangular filters in mel scale
    // ... (implementation)
}

fn dct(signal: &[f64], n_coeffs: usize) -> Vec<f64> {
    // Type-II DCT
    (0..n_coeffs).map(|k| {
        signal.iter().enumerate().map(|(n, &x)| {
            x * ((PI * k as f64 * (n as f64 + 0.5)) / signal.len() as f64).cos()
        }).sum::<f64>() * (2.0 / signal.len() as f64).sqrt()
    }).collect()
}
```

**JavaScript**: Not practical (too slow for real-time)

**Bundle Impact**: ~20KB (custom implementation on top of rustfft)  
**Performance**: 40-60% native (acceptable for non-realtime)  
**Use Case**: Speech recognition features, audio fingerprinting

---

#### 6. Radon Transform

**Desktop Implementation**:

```julia
using ImagePhantoms
radon_transform = radon(image, angles)
```

**Web Implementation**: ❌ **Desktop Only**

**Reason**:

- Radon transform is O(N³) for NxN image
- Used primarily for CT reconstruction (medical imaging)
- Requires heavy interpolation and projection computation
- Even with WASM, too slow for interactive use (>5 seconds for 512x512 image)

**Alternative for Web**:

- Show precomputed examples
- "Desktop-only feature" message with explanation
- Link to Julia server for Tauri desktop users

**Educational Value**: Still document the transform, show results, explain limitations

---

### Desktop-Only Features (Summary)

The following features are **not practical for web deployment** but remain available in the Tauri desktop app via Julia server:

| Feature                        | Reason                                  | Alternative for Web                     |
| ------------------------------ | --------------------------------------- | --------------------------------------- |
| **Advanced Wavelets**          | Complex kernels, slow convolution       | Basic wavelets (Haar, Db4) only         |
| **Radon Transform**            | O(N³) complexity, medical imaging       | Show precomputed examples               |
| **Signal Classification (ML)** | Large models (>10MB), training required | Show demo classifications, desktop-only |
| **Anomaly Detection (ML)**     | Statistical models, large datasets      | Simple threshold-based detection only   |

---

### Feature Availability Matrix

| Environment                | Available Features | Bundle Size    | Performance         |
| -------------------------- | ------------------ | -------------- | ------------------- |
| **Desktop (Tauri)**        | 100% (16/16)       | ~2GB (Julia)   | 100% (native Julia) |
| **Web (WASM + JS)**        | 75% (12/16)        | ~170KB gzipped | 40-95% (WASM SIMD)  |
| **Web (JS Fallback Only)** | 60% (9/16)         | ~23KB gzipped  | 5-10% (acceptable)  |

**Feature Parity**: 12 of 16 advanced features available on web (75%)

---

### Implementation Phases

**Phase 1 (v1.0 - Core Time-Frequency)**:

1. ✅ STFT (Rust WASM + JS fallback)
2. ✅ Spectrogram (STFT + Canvas visualization)
3. ✅ Frequency Domain Filtering (FFT-based)
4. ✅ Real-time Parameter Manipulation (local compute)

**Phase 2 (v1.5 - Advanced Analysis)**: 5. Hilbert Transform (Rust WASM FFT-based) 6. Analytic Signal (Hilbert + original) 7. MFCCs (Rust WASM custom pipeline) 8. Basic Wavelets (Haar, Daubechies-4) 9. Edge Detection (Sobel, Canny via ndarray) 10. Morphological Operations (ndarray) 11. Pitch Detection (autocorrelation) 12. Waterfall Plot (WebGL visualization)

**Phase 3 (v2.0 - Desktop-Only Advanced)**: 13. Advanced Wavelets (Julia only) 14. Radon Transform (Julia only) 15. Signal Classification (Julia + Flux.jl) 16. Audio Effects (Web Audio API + Julia)

---

### Performance Optimization Strategies

1. **WASM SIMD Detection**: Automatically use SIMD-optimized code when available (60-95% native vs 20-50% without)
2. **Web Workers**: Offload FFT/STFT computation to background threads
3. **Incremental Computation**: Compute spectrograms frame-by-frame for responsiveness
4. **Caching**: Cache FFT plans and filterbanks for repeated use
5. **Downsampling**: Offer quality/speed tradeoffs (512-point vs 2048-point FFT)

---

### User Experience Guidelines

**Desktop-Only Features**:

- Show "🔒 Desktop Only" badge in UI
- Explain why (performance/complexity) in tooltip
- Offer "Try Desktop Version" link
- Show precomputed examples with explanation

**Performance Indicators**:

- Show real-time performance metrics (FFT time, frame rate)
- Warn when JS fallback is used ("Performance Mode: Compatibility")
- Suggest enabling WASM SIMD in browser settings

**Progressive Enhancement**:

- Basic features work on all browsers (JS fallback)
- Enhanced performance with WASM support
- Full features with desktop app

---

## Status & Next Steps

**Current Progress**: Initial research and framework established, backend implementation strategy defined

**Immediate Next Steps**:

1. ✅ Research each feature in depth
2. ✅ Identify Julia packages and JavaScript libraries
3. ✅ **Determine backend implementation strategy (Rust WASM vs JS vs Desktop-only)**
4. ✅ **Document performance benchmarks and bundle sizes**
5. Create detailed implementation specs
6. Begin Phase 1 implementation (STFT, Spectrogram, FFT filtering)

---

**Last Updated**: 2025-10-25  
**Status**: ✅ Backend Strategy Complete - Ready for Implementation

**Backend Implementation Summary**:

- **12 of 16 features** available on web (75% parity)
- **Rust WASM** for performance-critical operations (STFT, Hilbert, MFCCs, edge detection)
- **JavaScript fallback** for compatibility (fft.js for FFT when WASM unavailable)
- **Desktop-only** for advanced features (complex wavelets, Radon, ML classification)
- **Bundle size**: ~170KB gzipped (vs 2GB+ for Julia)
- **Performance**: 40-95% of native Julia (depending on WASM SIMD support)
