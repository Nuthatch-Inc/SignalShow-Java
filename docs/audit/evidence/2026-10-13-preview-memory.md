# Obsolete image preview buffers — October 13, 2026

Continuation of BUG-031 after local commit 0174857. The prior collected snapshot
identified retained 512-square arrays in old function previews, calculator
thumbnails and operation-result previews. See [the measured ownership paths](2026-10-13-editor-memory-after-array-roots.txt).

The 2D editor now has an explicit cleanup owner around its existing layout.
Leaving it releases its calculator cells/renderer and nested image previews;
replacing a term releases that term's old previews as well. Display cleanup drops
its data and JAI rendered-image references. JAI's display widget rejects null, so
it receives a one-pixel blank image. The signal definition is preserved. Clearing
an operation preview releases both displayed image buffers and can display a
subsequent valid result normally.

Four new EDT cases (Constant/Cylinder × replacement/retirement) failed before and
pass after. They assert actual buffer ownership, original sample preservation and
reopened preview behavior. An extended operation-editor test independently exposed
its retained rendered image (one failure out of the existing 1D/2D pair); that now
passes, including reuse after clearing. Existing two-term, imported-image and
image-editing regressions pass. **Full package: 549 tests, zero failures/errors/skips.**
The first JUnit failures printed full arrays; the complete log is compressed, with
a bounded text excerpt alongside.

Native CUA: repeated the prior Constant×Cylinder workflow, 512 square, explicit
inputs, Plot/Edit transitions, source amplitude 3→2.5 and reopened output. Both
exports match every byte of the independently verified preceding run's output;
262,144 complex samples per file. Collected live heap was 134.2 MiB, compared with
169.5 MiB at the same stage before preview cleanup. No native application exception
was observed during this completed portion.

**Final native deletion/heap verification is incomplete.** macOS locked before
that action batch completed; the tool required manual unlock. No empty-library AX
snapshot was produced. Do not label the live measurement post-deletion, or claim
that native preview-array reclamation is verified. The disposable process was
terminated after preserving the exports because desktop control was unavailable;
this was intentional cleanup, not a crash. The final build is in the local audit
bundle, ready for the bounded comparison after Juliet unlocks the Mac.

Other image families and imported-data lifetime are not certified by these
Constant/Cylinder cases. Existing functional import regressions establish editing
behavior, not memory reclamation. JAI tile/soft caches, manual use, Windows, other
JVMs, larger images and long sessions remain separate limits. No push or release.

The subsequent [completed native comparison](2026-10-13-preview-memory-resumed.md)
resolves the pending deletion check above: zero document models and zero strongly
reachable large pixel arrays remain after deleting the test documents; collected
heap is 20.1 MiB. The original interrupted-run evidence remains historical.
