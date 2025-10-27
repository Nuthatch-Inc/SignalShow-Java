# File-Based Architecture

**Purpose**: Transform SignalShow into a file-based system where all user objects (signals, operations, workspaces) are portable files

---

## Current vs Proposed

**Java SignalShow** (Current):
- All objects exist in memory only
- No persistence between sessions
- Cannot share work easily

**Nuthatch SignalShow** (Proposed):
- All objects stored as files
- Automatic persistence
- Portable across sessions/systems
- External editing supported
- Version control compatible

---

## Design Goals

1. **Portability** - Share signals, functions, workspaces as files
2. **Persistence** - Work auto-saved and recoverable
3. **Editability** - Power users can edit files externally
4. **Composability** - Files can be combined and referenced
5. **Version Control** - Works with Git
6. **File System Integration** - Leverages Nuthatch Desktop's Files.app
7. **Educational** - Students can examine/modify files to learn

---

## File Types

### 1D Signals (`.sig1d`)

**Example**: `chirp_signal.sig1d`

```json
{
  "type": "signal1d",
  "version": "1.0",
  "metadata": {
    "name": "Chirp Signal",
    "description": "Linear frequency sweep 10Hz to 100Hz",
    "created": "2025-10-25T14:30:00Z",
    "tags": ["chirp", "frequency-sweep"]
  },
  "parameters": {
    "sampleRate": 1000,
    "duration": 1.0,
    "generator": "chirp",
    "generatorParams": {"startFreq": 10, "endFreq": 100, "amplitude": 1.0}
  },
  "data": {
    "format": "json-array",
    "x": [0, 0.001, 0.002, ...],
    "y": [0, 0.0314, 0.0628, ...]
  }
}
```

**Alternative compact** (large datasets):
```json
{
  "data": {
    "format": "binary-base64",
    "encoding": "float32-le",
    "x": "AAAAAAAA8D8AAABAAACAQAAAwEA...",
    "y": "zcxMPZqZmT4AAIA+mpmZPgAA..."
  }
}
```

**Use cases**:
- User-generated signals (chirps, pulses, noise)
- Imported audio samples
- Intermediate results

---

### 2D Signals (`.sig2d`)

**Example**: `lena_image.sig2d`

```json
{
  "type": "signal2d",
  "version": "1.0",
  "metadata": {"name": "Lena Image", "tags": ["image", "test-pattern"]},
  "parameters": {
    "width": 512,
    "height": 512,
    "channels": 1,
    "colorSpace": "grayscale",
    "generator": "imported",
    "sourceFile": "lena.png"
  },
  "data": {
    "format": "binary-base64",
    "encoding": "uint8",
    "values": "iVBORw0KGgoAAAANSUhEUg..."
  }
}
```

**Use cases**:
- Imported images
- 2D FFT results
- Spectrograms
- Diffraction patterns

---

### Operation Chains (`.sigop`)

**Example**: `lowpass_filter_chain.sigop`

```json
{
  "type": "operation-chain",
  "version": "1.0",
  "metadata": {"name": "Lowpass Filter + Normalize", "tags": ["filter"]},
  "operations": [
    {
      "id": "op1",
      "type": "filter",
      "params": {"filterType": "butterworth", "order": 4, "cutoff": 1000}
    },
    {
      "id": "op2",
      "type": "normalize",
      "params": {"method": "peak", "targetAmplitude": 1.0}
    }
  ]
}
```

**Use cases**:
- Reusable processing pipelines
- Teaching examples (convolution, FFT, filtering)
- Batch processing recipes

---

### Workspaces (`.sigws`)

**Example**: `lecture_5_fourier_analysis.sigws`

```json
{
  "type": "workspace",
  "version": "1.0",
  "metadata": {"name": "Lecture 5: Fourier Analysis", "created": "2025-10-25T09:00:00Z"},
  "signals": [
    {"id": "sig1", "file": "chirp_signal.sig1d", "position": {"x": 100, "y": 50}},
    {"id": "sig2", "file": "square_wave.sig1d", "position": {"x": 100, "y": 200}}
  ],
  "operations": [
    {"id": "fft1", "type": "fft", "input": "sig1", "output": "fft_result"}
  ],
  "plots": [
    {"id": "plot1", "type": "waveform", "source": "sig1", "position": {"x": 400, "y": 50}},
    {"id": "plot2", "type": "spectrum", "source": "fft_result", "position": {"x": 400, "y": 200}}
  ]
}
```

**Use cases**:
- Lecture demonstrations
- Student assignments
- Complex multi-signal analysis

---

### Project Files (`.sigproj`)

**Example**: `acoustic_analysis_project.sigproj`

```json
{
  "type": "project",
  "version": "1.0",
  "metadata": {"name": "Acoustic Analysis Project", "description": "Room impulse response study"},
  "structure": {
    "signals/": ["microphone_1.sig1d", "microphone_2.sig1d"],
    "operations/": ["noise_reduction.sigop", "cross_correlation.sigop"],
    "results/": ["impulse_response.sig1d", "frequency_response.sig1d"],
    "workspaces/": ["analysis_workspace.sigws"]
  },
  "settings": {
    "defaultSampleRate": 48000,
    "audioBackend": "julia-server"
  }
}
```

**Use cases**:
- Complex multi-file projects
- Research work
- Student portfolios

---

## File Operations

### Create
```javascript
// Generate signal → auto-save as .sig1d
const signal = await backend.generateChirp(10, 100, 1.0, 1000);
await fileManager.save(signal, 'chirp_signal.sig1d');
```

### Load
```javascript
// Load .sig1d → display
const signal = await fileManager.load('chirp_signal.sig1d');
plotManager.plotWaveform(signal);
```

### Edit
```javascript
// Modify parameters → save
signal.metadata.tags.push('modified');
await fileManager.save(signal, 'chirp_signal.sig1d');
```

### Reference
```javascript
// Workspace references signals by path
workspace.addSignal({file: 'signals/chirp_signal.sig1d'});
```

---

## Storage Locations

### Desktop App (Tauri)
- **User Files**: `~/Documents/SignalShow/`
- **System ROM**: `/Applications/SignalShow.app/Contents/Resources/system-rom/`
- **Temp Files**: `~/.signalshow/temp/`

**File tree**:
```
~/Documents/SignalShow/
  Projects/
    acoustic_analysis/
      acoustic_analysis_project.sigproj
      signals/
        microphone_1.sig1d
      operations/
        noise_reduction.sigop
      results/
  Signals/
    chirp_signal.sig1d
    square_wave.sig1d
  Workspaces/
    lecture_5_fourier.sigws
```

### Web App (Browser)
- **IndexedDB**: Primary storage (unlimited quota)
- **localStorage**: Settings/preferences (10MB limit)
- **File System Access API**: Optional native file I/O (Chrome/Edge)

**IndexedDB structure**:
```javascript
{
  "signals": {
    "chirp_signal": {blob, metadata},
    "square_wave": {blob, metadata}
  },
  "workspaces": {
    "lecture_5": {blob, metadata}
  }
}
```

---

## Integration with Nuthatch Desktop

### Files.app Integration
- `.sig1d` / `.sig2d` files appear in Files.app
- Double-click opens in SignalShow
- Drag-and-drop support
- Thumbnail previews (waveform icons)

### Native File Dialogs
- Use Tauri native dialogs (not browser input)
- File picker shows .sig1d / .sig2d by default
- Save dialog auto-adds extension

### Drag-and-Drop
- Drag `.sig1d` from Files.app → SignalShow canvas
- Drag `.wav` / `.png` → auto-convert to `.sig1d` / `.sig2d`
- Drag between SignalShow instances

---

## Import/Export

### Import Formats
| Format | Extension | Converts To | Notes |
|--------|-----------|-------------|-------|
| WAV audio | `.wav` | `.sig1d` | Sample rate preserved |
| PNG/JPEG | `.png`, `.jpg` | `.sig2d` | Grayscale conversion |
| CSV data | `.csv` | `.sig1d` | Two-column (x, y) format |
| NumPy binary | `.npy` | `.sig1d` / `.sig2d` | 1D or 2D arrays |

### Export Formats
| Format | Extension | Exports From | Notes |
|--------|-----------|--------------|-------|
| WAV audio | `.wav` | `.sig1d` | For playback |
| PNG image | `.png` | `.sig1d`, `.sig2d` | Plot snapshots |
| CSV data | `.csv` | `.sig1d` | (x, y) columns |
| JSON | `.json` | All | Human-readable |

---

## Version Control

**Git-friendly**:
- All files are JSON text (diffable)
- Binary data in base64 (not ideal but manageable)
- `.gitignore` template:

```gitignore
# SignalShow
.signalshow/temp/
*.sig1d.bak
*.sig2d.bak
```

**Large file storage**:
- Use Git LFS for large `.sig2d` files
- Recommended for images >1MB
- Configuration:
```bash
git lfs track "*.sig2d"
```

---

## Performance Considerations

**File size targets**:
- `.sig1d` (1000 samples): ~10KB JSON, ~4KB binary
- `.sig2d` (512×512 image): ~300KB JSON, ~256KB binary
- `.sigws` (workspace): ~5-20KB (references only)

**Loading strategy**:
- Lazy load: Load metadata first, data on demand
- Streaming: For large files, load chunks progressively
- Caching: Keep recently used files in memory

**Auto-save**:
- Debounced writes (500ms after edit)
- Dirty flag tracking
- Background save (non-blocking)

---

## Security

**Tauri app**:
- File access restricted to `~/Documents/SignalShow/`
- No arbitrary file system access
- Sandboxed Julia server (localhost only)

**Web app**:
- IndexedDB isolated per-origin
- File System Access API requires user permission
- No automatic network access

**File validation**:
- JSON schema validation on load
- Reject malformed files
- Sanitize user-provided metadata

---

## Educational Benefits

1. **Transparency** - Students see exact signal parameters in JSON
2. **Experimentation** - Edit files manually to learn effects
3. **Sharing** - Email/post signal files for homework
4. **Reproducibility** - Exact parameters preserved
5. **Version control** - Track changes over time (Git)
6. **Portability** - Works across desktop/web versions

**Example use case**:
```
Professor creates chirp_example.sig1d
↓
Posts to LMS (Moodle/Canvas)
↓
Students download and open in SignalShow
↓
Students modify parameters
↓
Students submit modified .sig1d files
```

---

## Implementation Phases

### Phase 1 (v1.0) - Basic File I/O
- Load/save `.sig1d` files
- JSON format only
- Desktop file dialogs (Tauri)
- IndexedDB storage (web)

### Phase 2 (v1.5) - Advanced Features
- `.sig2d` image support
- `.sigop` operation chains
- Binary format (base64 encoding)
- Import/export WAV, PNG, CSV

### Phase 3 (v2.0) - Full Integration
- `.sigws` workspaces
- `.sigproj` projects
- Drag-and-drop between apps
- Thumbnail previews
- Auto-save
- Git LFS support

---

**Key Decision**: Start with simple JSON `.sig1d` files in v1.0, expand to full file-based architecture in v2.0.
