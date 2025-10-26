# SignalShow File-Based Architecture

**Date**: October 25, 2025  
**Status**: Planning Phase  
**Related Documents**:

- [NUTHATCH_PLATFORM_PORT_ANALYSIS.md](./NUTHATCH_PLATFORM_PORT_ANALYSIS.md)
- [ARCHITECTURE.md](./ARCHITECTURE.md)

---

## Overview

### Current State (Java SignalShow)

In the Java version of SignalShow, all objects the user interacts with to craft visualizations are **internal to the application**:

- 1D functions
- 2D signals/images
- Operation chains
- Filter parameters
- Workspaces

These objects exist only in memory and are not directly portable or editable outside the SignalShow environment.

### Proposed State (Nuthatch Desktop SignalShow)

Transform SignalShow into a **file-based system** where:

- All user-created objects are stored as files
- Files are portable across sessions and systems
- Files can be edited externally (with appropriate tools)
- Entire workspaces can be saved, shared, and version-controlled
- Integration with Nuthatch Desktop's Files.app for browsing and management

---

## Design Goals

1. **Portability**: Users can share signals, functions, and workspaces as files
2. **Persistence**: Work is automatically saved and recoverable
3. **Editability**: Power users can edit files externally (text editors, scripts)
4. **Composability**: Files can be combined, imported, and referenced
5. **Version Control**: Files work with Git and other VCS systems
6. **File System Integration**: Leverage Nuthatch Desktop's native file operations
7. **Educational Value**: Students can examine and modify signal files to learn

---

## File Types & Extensions

### Primary File Types

#### 1. **1D Function Files** (`.sig1d`)

**Purpose**: Store 1D signal data and metadata

**Example**: `chirp_signal.sig1d`

**Proposed Schema**:

```json
{
  "type": "signal1d",
  "version": "1.0",
  "metadata": {
    "name": "Chirp Signal",
    "description": "Linear frequency sweep from 10Hz to 100Hz",
    "created": "2025-10-25T14:30:00Z",
    "modified": "2025-10-25T15:45:00Z",
    "author": "Jane Doe",
    "tags": ["chirp", "frequency-sweep", "demo"]
  },
  "parameters": {
    "sampleRate": 1000,
    "duration": 1.0,
    "numSamples": 1000,
    "generator": "chirp",
    "generatorParams": {
      "startFreq": 10,
      "endFreq": 100,
      "amplitude": 1.0
    }
  },
  "data": {
    "format": "json-array",
    "x": [0, 0.001, 0.002, ...],
    "y": [0, 0.0314, 0.0628, ...]
  }
}
```

**Alternative Compact Format** (for large datasets):

```json
{
  "type": "signal1d",
  "version": "1.0",
  "metadata": { ... },
  "parameters": { ... },
  "data": {
    "format": "base64-float32",
    "x": "base64-encoded-binary-data",
    "y": "base64-encoded-binary-data"
  }
}
```

---

#### 2. **2D Signal/Image Files** (`.sig2d`)

**Purpose**: Store 2D signals, spectrograms, images

**Example**: `gaussian_2d.sig2d`

**Proposed Schema**:

```json
{
  "type": "signal2d",
  "version": "1.0",
  "metadata": {
    "name": "2D Gaussian",
    "description": "Gaussian function in 2D",
    "created": "2025-10-25T14:30:00Z"
  },
  "dimensions": {
    "width": 512,
    "height": 512,
    "xRange": [-5, 5],
    "yRange": [-5, 5]
  },
  "parameters": {
    "generator": "gaussian2d",
    "generatorParams": {
      "centerX": 0,
      "centerY": 0,
      "sigmaX": 1.0,
      "sigmaY": 1.0,
      "amplitude": 1.0
    }
  },
  "data": {
    "format": "png-base64",
    "encoding": "base64",
    "imageData": "iVBORw0KGgoAAAANSUhEUgAA..."
  }
}
```

---

#### 3. **Operation Chain Files** (`.sigOp`)

**Purpose**: Store sequences of operations applied to signals

**Example**: `lowpass_filter_chain.sigOp`

**Proposed Schema**:

```json
{
  "type": "operation-chain",
  "version": "1.0",
  "metadata": {
    "name": "Low-Pass Filter Chain",
    "description": "Butterworth filter + normalization"
  },
  "operations": [
    {
      "id": "op1",
      "type": "fft",
      "params": {}
    },
    {
      "id": "op2",
      "type": "lowpass-filter",
      "params": {
        "cutoffFreq": 50,
        "order": 4,
        "filterType": "butterworth"
      }
    },
    {
      "id": "op3",
      "type": "ifft",
      "params": {}
    },
    {
      "id": "op4",
      "type": "normalize",
      "params": {
        "method": "peak"
      }
    }
  ]
}
```

---

#### 4. **Workspace Files** (`.sigWorkspace`)

**Purpose**: Store entire SignalShow sessions with multiple signals and visualizations

**Example**: `fourier_analysis_lab.sigWorkspace`

**Proposed Schema**:

```json
{
  "type": "workspace",
  "version": "1.0",
  "metadata": {
    "name": "Fourier Analysis Lab",
    "description": "Demo workspace for teaching FFT concepts",
    "created": "2025-10-25T14:00:00Z",
    "modified": "2025-10-25T16:00:00Z"
  },
  "layout": {
    "windows": [
      {
        "id": "win1",
        "type": "plot1d",
        "position": { "x": 0, "y": 0, "w": 600, "h": 400 },
        "signal": "signal1"
      },
      {
        "id": "win2",
        "type": "plot1d",
        "position": { "x": 620, "y": 0, "w": 600, "h": 400 },
        "signal": "signal2"
      }
    ]
  },
  "signals": {
    "signal1": {
      "source": "embedded",
      "data": {
        /* 1D signal data */
      }
    },
    "signal2": {
      "source": "file",
      "path": "./chirp_signal.ss1d"
    },
    "signal3": {
      "source": "derived",
      "baseSignal": "signal1",
      "operations": {
        "source": "file",
        "path": "./lowpass_filter_chain.ssop"
      }
    }
  },
  "activeDemo": null
}
```

---

#### 5. **Demo/Tutorial Files** (`.sigDemo`)

**Purpose**: Interactive educational demos with guided steps

**Example**: `sampling_theorem.sigDemo`

**Proposed Schema**:

```json
{
  "type": "demo",
  "version": "1.0",
  "metadata": {
    "name": "Sampling Theorem Demonstration",
    "description": "Interactive demo of Nyquist sampling",
    "author": "SignalShow Team",
    "difficulty": "beginner"
  },
  "steps": [
    {
      "title": "Create a sine wave",
      "description": "We'll create a 10Hz sine wave",
      "action": {
        "type": "create-signal",
        "params": {
          "generator": "sinusoid",
          "frequency": 10,
          "sampleRate": 1000
        }
      }
    },
    {
      "title": "Sample at Nyquist rate",
      "description": "Sample at exactly 2x the frequency (20 Hz)",
      "action": {
        "type": "resample",
        "params": {
          "targetSampleRate": 20
        }
      }
    },
    {
      "title": "Sample below Nyquist",
      "description": "Now sample at 15Hz - observe aliasing!",
      "action": {
        "type": "resample",
        "params": {
          "targetSampleRate": 15
        }
      },
      "highlight": {
        "type": "warning",
        "message": "Notice the aliased frequency!"
      }
    }
  ]
}
```

---

## File Format Considerations

### JSON vs Binary

**Option 1: JSON-based (Recommended for MVP)**

- ✅ Human-readable and editable
- ✅ Easy to debug and inspect
- ✅ Works with version control (Git diff)
- ✅ Easy to parse in JavaScript
- ✅ Supports metadata and comments (via description fields)
- ❌ Larger file sizes for big datasets
- ❌ Slower for large arrays

**Option 2: Binary with JSON header**

- ✅ Compact for large datasets
- ✅ Fast to load
- ❌ Not human-readable
- ❌ Harder to edit manually
- ❌ Version control shows as binary blob

**Option 3: Hybrid (Recommended for Production)**

```
.sig1d file structure:
- JSON header (metadata, parameters)
- Binary data section (signal samples)
```

**Recommendation**: Start with JSON for MVP, add binary support in Phase 2 when performance becomes critical.

---

## File System Integration

### Integration with Nuthatch Desktop Files.app

1. **File Type Registration**

   ```json
   // In SignalShow app.json
   {
     "fileExtensions": [
       ".sig1d",
       ".sig2d",
       ".sigOp",
       ".sigWorkspace",
       ".sigDemo"
     ]
   }
   ```

2. **Double-click to open**: Files.app can launch SignalShow with the file
3. **Thumbnails**: Generate preview images for signal files
4. **Drag-and-drop**: Import signals by dragging files into SignalShow

### File Operations

**Save Signal**:

```javascript
async function saveSignal(signal, filename) {
  const fileData = {
    type: "signal1d",
    version: "1.0",
    metadata: {
      name: signal.name,
      created: new Date().toISOString(),
    },
    parameters: signal.params,
    data: {
      format: "json-array",
      x: signal.x,
      y: signal.y,
    },
  };

  await window.fileSystem.writeFile(
    filename,
    JSON.stringify(fileData, null, 2)
  );
}
```

**Load Signal**:

```javascript
async function loadSignal(filename) {
  const content = await window.fileSystem.readFile(filename);
  const fileData = JSON.parse(content);

  // Validate version and type
  if (fileData.type !== "signal1d") {
    throw new Error("Invalid file type");
  }

  return {
    name: fileData.metadata.name,
    params: fileData.parameters,
    x: fileData.data.x,
    y: fileData.data.y,
  };
}
```

---

## File Naming Conventions

### Recommended Patterns

**Descriptive Names**:

- `chirp_10hz_to_100hz.sig1d`
- `gaussian_sigma_2.sig2d`
- `butterworth_lowpass_50hz.sigOp`

**Experiment Folders**:

```
/my-signals/
  lab1-fourier/
    square_wave.sig1d
    fft_result.sig1d
    lab1_workspace.sigWorkspace
  lab2-filtering/
    noisy_signal.sig1d
    clean_signal.sig1d
    denoise_chain.sigOp
```

**Demo Library**:

```
/demos/
  beginner/
    sampling_theorem.sigDemo
    fourier_series.sigDemo
  advanced/
    holography_simulation.sigDemo
    doppler_effect.sigDemo
```

---

## Implementation Phases

### Phase 1: Basic File I/O (Weeks 1-2)

- Implement `.sig1d` file format (JSON-based)
- Save/Load buttons in UI
- File picker integration
- Simple metadata (name, created date)

### Phase 2: Rich Metadata & Operations (Weeks 3-4)

- Add `.sigOp` operation chain files
- Generator parameter persistence
- File preview/thumbnails
- Recent files list

### Phase 3: Workspaces (Weeks 5-6)

- Implement `.sigWorkspace` format
- Save entire session state
- Multi-window layouts
- Auto-save functionality

### Phase 4: Advanced Features (Weeks 7-8)

- Binary data support for large files
- `.sigDemo` interactive tutorials
- File templates/presets
- Import/export to other formats (CSV, WAV, etc.)

---

## Open Questions for Discussion

1. **File Extension Naming**:

   - Using `.sig1d`, `.sig2d`, `.sigOp`, `.sigWorkspace`, `.sigDemo`
   - MIME type registration?
   - Case sensitivity considerations?

2. **Data Encoding**:

   - When to use JSON arrays vs Base64 vs binary?
   - Threshold file size for switching formats?
   - Compression (gzip JSON)?

3. **Referencing**:

   - Relative vs absolute paths for workspace signal references?
   - How to handle moved/missing files?
   - Embed vs reference strategy?

4. **Versioning**:

   - Schema version migration strategy?
   - Backward compatibility requirements?
   - Forward compatibility (ignoring unknown fields)?

5. **Metadata**:

   - What metadata is essential vs optional?
   - Custom user-defined metadata?
   - Standard tags/categories?

6. **Interoperability**:
   - Export to MATLAB/Python/Julia formats?
   - Import from common signal formats (WAV, CSV)?
   - Standard interchange format (HDF5)?

---

## Examples of Use Cases

### Use Case 1: Student Lab Assignment

**Scenario**: Professor assigns "Analyze this noisy ECG signal"

1. Professor provides: `ecg_noisy.ss1d`
2. Student opens in SignalShow
3. Student applies filters, saves chain: `my_denoise.ssop`
4. Student saves result: `ecg_clean.ss1d`
5. Student saves workspace: `lab3_submission.sswork`
6. Student submits workspace file for grading

### Use Case 2: Signal Processing Research

**Scenario**: Researcher experimenting with custom filters

1. Create test signal: `test_chirp.ss1d`
2. Develop filter chain: `experimental_filter_v1.ssop`
3. Iterate on parameters, save versions: `v2.ssop`, `v3.ssop`
4. Compare results in workspace: `filter_comparison.sswork`
5. Version control with Git
6. Share workspace with collaborators

### Use Case 3: Educational Demo Library

**Scenario**: Instructor creates reusable demos

1. Create demo: `aliasing_demo.ssdemo`
2. Include signals: `sine_10hz.ss1d`, `sine_10hz_undersampled.ss1d`
3. Package in folder: `demos/aliasing/`
4. Students download and run interactively
5. Students can modify and explore on their own

---

## Next Steps

**When implementation reaches this phase**:

1. **Review and finalize file format schemas**

   - Discuss extension names (`.ss1d` vs alternatives)
   - Agree on metadata fields
   - Define required vs optional fields

2. **Prototype file I/O**

   - Implement basic save/load for 1D signals
   - Test with Nuthatch Desktop file system APIs
   - Validate JSON parsing performance

3. **User testing**

   - Create sample files for common scenarios
   - Test file portability across systems
   - Gather feedback on file browsing UX

4. **Documentation**
   - File format specification document
   - API documentation for file operations
   - User guide for file management

---

## References

**Related Nuthatch Desktop Documentation**:

- [NATIVE_FILE_OPERATIONS.md](../nuthatch-desktop/docs/NATIVE_FILE_OPERATIONS.md)
- [FILE_ASSOCIATIONS.md](../nuthatch-desktop/docs/FILE_ASSOCIATIONS.md)
- Files.app integration guide

**Similar Systems**:

- MATLAB `.mat` files (binary + metadata)
- NumPy `.npy` files (binary arrays)
- HDF5 (hierarchical data format)
- JSON with binary data (like Jupyter notebooks)

---

**Status**: This document is a **planning reference** for when the SignalShow port reaches the file-based architecture phase. The schemas and formats are proposals for discussion, not final specifications.
