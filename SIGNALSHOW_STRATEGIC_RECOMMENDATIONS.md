# SignalShow Modernization – Strategic Recommendations

**Author**: Principal Architecture Review (DSP & Optics)

**Date**: October 26, 2025

**Scope**: Long-term product vision and roadmap guardrails for bringing SignalShow from its legacy Java implementation into a differentiated, sustainable web/desktop platform that serves university teaching, textbook production, and research/industry prototyping.

---

## 1. Product Positioning & Differentiation

- **Core identity**: SignalShow should own the intersection of _signal processing_, _optics_, and _story-driven pedagogy_. Instead of competing head-on with Desmos or GeoGebra in general math, emphasize:
  - Deep frequency-domain intuition (Fourier, wavelets, cepstrum, holography) with live comparisons and annotated explanations.
  - Coupled 1D ↔ 2D workflows (e.g., time traces → diffraction patterns) that mainstream tools do not offer.
  - Reproducible figure pipelines for educators and textbook authors ("generate once, keep provenance").
- **Signature experiences** to market:
  1. "Concept Studio" – curated interactive narratives (sampling theorem, interferometry, modulation) with educator notes.
  2. "Optics Lab" – 2D aperture & propagation playground with exportable animations.
  3. "Signal Clinic" – guided diagnosis of noisy or distorted signals (students drop in data, follow corrective recipes).
- **Partnership angle**: Pitch SignalShow as _complementary_ to MATLAB/Python workflows by offering instant visual explainers and exports that drop back into those ecosystems.

---

## 2. Personas & Journey Expectations

| Persona                 | Day-One Goals                                                   | Advanced Needs                                | Platform Implications                                                            |
| ----------------------- | --------------------------------------------------------------- | --------------------------------------------- | -------------------------------------------------------------------------------- |
| **Instructor**          | Launch pre-built demo in seconds; annotate live; export slides. | Build custom sequences, push to LMS.          | Requires offline availability, hotkey/scriptable cues, shareable lesson bundles. |
| **Student**             | Explore parameters safely; capture lab notes.                   | Compare theory vs measured data, collaborate. | Needs guardrails, scaffolded steps, sandbox mode, cross-device sync.             |
| **Researcher / Author** | Produce publication-quality figures with provenance.            | Re-run pipelines when data changes.           | Versioned exports, metadata embedding, CLI/headless mode.                        |
| **Content Creator**     | Script storyboards, export video narration.                     | Integrate with external editing suites.       | JSON timeline format, Manim/After Effects bridges.                               |

**Recommendation**: Plan UI layers that scale from _Guided Mode_ (step-by-step) to _Expert Mode_ (full control with scripting). Document default presets for each persona.

---

## 3. Experience Architecture

1. **Progressive Onboarding**
   - First-run tour covering signal selection, operations chain, and comparison panes.
   - Contextual help cards tied to textbook chapters or IEEE curriculum topics.
2. **Workspace Metaphor**
   - Tabbed workspaces (Time, Frequency, Spatial, Parameter Study) with synchronized cursors.
   - Split-view comparisons (Original vs. Processed vs. Reference) using shared crosshair.
3. **Narrative Timeline**
   - Timeline strip that records parameter changes → enables replay/exports and explains the sequence.
   - Export timeline as JSON/Markdown for lesson plans.
4. **Auditory Feedback**
   - Optional sonification (play the waveform, before/after) to reinforce learning and cover accessibility needs for visually impaired users.

---

## 4. Content & Curriculum Strategy

- Ship with **concept packs** aligned to ABET-accredited DSP courses (sampling, z-transform, modulation, optics). Each pack includes:
  - Interactive demos
  - Instructor talking points
  - Student exercises with answer keys
  - Related real-world datasets (audio, optics, biomedical)
- Build **lesson template format** (Markdown + JSON) that can be shared, version-controlled, and imported/exported.
- Partner with **OpenStax / IEEE TryEngineering** for curated material; avoid re-creating broad math curricula.

---

## 5. Interoperability & Data Standards

- **File formats**:
  - Support import/export of WAV, CSV, NumPy `.npy`, MATLAB `.mat`, HDF5 for multi-dimensional data.
  - Preserve metadata (sample rate, units, processing chain) in exported SignalShow workspaces.
- **External pipeline hooks**:
  - Command-line interface (CLI) to render figures headlessly (for CI or LaTeX workflows).
  - Jupyter/Pluto notebooks integration: python/julia client libraries that send data to a running SignalShow session for visualization.
  - LTI 1.3 deep-linking support for LMS integration (Canvas, Blackboard) to embed live demos.
- **Plugin spec**:
  - Define a sandboxed plugin API for custom operations / visualizers (JS or WebAssembly modules) so universities can extend without forking.

---

## 6. Performance & Technical Roadmap

1. **Baseline (Now)** – JavaScript backend (fft.js) for immediacy.
2. **Phase 2 (Planned)** – Rust/WebAssembly compute kernels (rustfft, dasp) with shared memory and Web Workers.
3. **Phase 3** – GPU acceleration (WebGPU kernels for FFT, convolution, diffraction) leveraging existing Nuthatch WebGPU infrastructure.
4. **Heavyweight Mode** – Optional Julia server or cloud compute pods for large datasets (>10⁶ samples) with TLS-secured APIs.
5. **Performance governance**:
   - Set target budgets (e.g., <50 ms interaction latency for sliders up to 16k samples).
   - Maintain benchmark suite covering CPU vs. WASM vs. GPU to drive regression alerts.

---

## 7. Distribution & Deployment

- **Web-first**: Static bundle + service worker (PWA) for offline caching of demos and concept packs.
- **Desktop**: Tauri build with bundled Julia runtime _optionally_ fetched on demand to reduce installer size; use differential updates.
- **Classroom Appliance**: Docker container for labs that need pre-configured server with Julia/WASM and content packs.
- **Enterprise / University IT**: SCCM-compatible installers, managed configuration profiles, SSO (OAuth/OIDC) for premium content.
- **Open-source core / premium packs**: Consider dual licensing—core engine MIT, premium concept packs under commercial license.

---

## 8. Ease of Adoption & Learning Curve

- **Guided Projects**: Templates that walk students through parameter sweeps with auto-assessed checkpoints.
- **Explainable Operations**: Hover cards showing formulae, graphical interpretations, and links to textbook sections.
- **Undo/Redo Timeline**: Visual history enabling students to experiment fearlessly.
- **Scenario Library**: Quick-start with real recordings (audio, radar, OCT) to underline relevance.

---

## 9. Accessibility & Inclusivity

- Commit to WCAG 2.2 AA compliance: keyboard-first workflows, ARIA landmarks, screen reader descriptions for plots (summaries + data exports).
- Colorblind-safe palettes, customizable line styles, adjustable point sizes.
- Provide captioning/transcripts for any video exports; use text-to-speech for narration cues.
- Sonification and haptic feedback (where hardware permits) to support multimodal learning.
- Localization-ready string catalog; prioritize Spanish, Mandarin, Hindi based on target regions.

---

## 10. Analytics, Feedback, and Privacy

- Opt-in telemetry capturing feature usage, anonymized concept completion, and performance metrics to inform roadmap.
- In-app “Send Feedback” that pairs screen recording + signal state (with consent).
- Privacy stance: FERPA/GDPR compliant, clear data retention policies, local-only mode for sensitive classrooms.

---

## 11. Community & Ecosystem

- **SignalShow Commons**: Shared repository of lessons, demos, and plug-ins with peer review and rating system.
- **Badge System** for contributors (academics can cite their modules, improving adoption).
- Quarterly virtual workshops featuring new concept packs and guest lecturers.
- Encourage cross-linking with existing open-source libraries (SciPy, JuliaImages) instead of reinventing algorithms.

---

## 12. Risk & Mitigation Summary

| Risk                                                   | Mitigation                                                                                                 |
| ------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------- |
| Desktop installer bloat (Julia runtime)                | Offer modular download; enable remote Julia option; supply WASM tier for most users.                       |
| Reinventing content already covered by Desmos/GeoGebra | Focus on DSP/optics depth, advanced analysis, figure provenance.                                           |
| classroom IT friction                                  | Provide portable web version, Docker image, offline PWA, clear security audits.                            |
| Performance regressions with large datasets            | Enforce benchmark suite, progressive disclosure of heavy features, encourage down-sampling previews.       |
| Accessibility gaps                                     | Establish accessibility review checklist per release; include educators with disabilities in beta program. |
| Fragmented plugin ecosystem                            | Versioned plugin API, certification program, curated marketplace.                                          |

---

## 13. Actionable Next Steps

1. **Vision Alignment Workshop** – Validate personas and signature experiences with 3–5 champion instructors (2 weeks).
2. **Concept Pack Backlog** – Prioritize initial packs (Sampling, Fourier Optics, Modulation) with subject-matter leads (3 weeks).
3. **Interoperability Spec** – Draft schema for workspace export/import (JSON + binary attachments) and plugin interface (4 weeks).
4. **Performance Charter** – Define latency and dataset targets, benchmarking harness for JavaScript vs. WASM vs. Julia (2 weeks).
5. **Accessibility Audit Plan** – Engage accessibility specialist to review Phase 1 UI and outline remediation (2 weeks).
6. **Distribution Blueprint** – Produce packaging decision doc covering PWA, Tauri, Docker, LMS integration (3 weeks).
7. **Community Seeding** – Identify initial partner institutions, plan for Commons launch and governance (ongoing).

---

## 14. Open Questions for Stakeholders

- Which curricula or textbooks should serve as canonical alignment targets for concept packs?
- What licensing model (if any) will govern premium content or institutional deployments?
- Do we need to support real-time hardware acquisition (oscilloscopes, cameras) in phase 1, or is import-from-file sufficient?
- How important is integration with existing university SSO/LMS systems in the initial release?
- Should SignalShow offer cloud-hosted compute for classes lacking local Julia expertise?

---

_This document should be revisited quarterly to ensure the long-term roadmap stays aligned with user feedback, technology shifts (e.g., WebGPU maturity), and institutional partnerships._
