# Image duplication investigation — October 12, 2026

Native 441-test build: New 2D Complex Chirp → Duplicate. The copy `f0` shows the
correct thumbnail/equation but an empty expression editor with creation-family
choices. Plot → Edit Real Part reveals the existing term. Increment the copy's
amplitude from 1 to 1.1: **both the copy and original equations change to
1.1 cos + i sin**. Returning to the original shows amplitude 1.1. The copy also
lost the predefined complex behavior that should keep real/imaginary parameters
linked inside that document.

Before screenshots accompany this record. App closed and process exit verified.
This reproduced BUG-019. The correction and after evidence follow below.

Source findings: all four Function clone methods reuse partA/partB and restore
neither existing-editor state nor FunctionType. A correction must copy mutable
terms and expression rules, including nested Cartesian/polar terms and sampled
arrays, retain predefined-complex type, and mark the copied definition as already
created. Keep the normal duplicate naming behavior. Image scaling mode is stored
outside its parameter block; separable terms' current children may differ from
the original block's sources. Noise stores an explicit seed in its parameters.

## Correction and verification

`FunctionCopy` copies terms, nested generators, sample arrays and expression rules
once per new definition. It preserves sharing within a complex definition while
separating the duplicate from the original. The four clone methods retain the
predefined complex/Fourier type and open the existing definition immediately.
Separable copies use the current edited children; image mode and explicit noise
seed/settings survive copying. Normal duplicate naming is unchanged.

Eight production-path regressions failed before and pass after: both dimensions
and representations, predefined complex sharing, replaced separable children,
sampled arrays/image mode and explicit noise settings. Cache eviction verifies
that a stale original result cannot hide aliasing. The focused copy/reopen group
passes 14 checks; the full package passes 449 tests, no failures/errors/skips.

Native 449-test build: New 2D Complex Chirp → Duplicate immediately shows the
existing editor. Increment copied amplitude 1 → 1.1; both copied channels update.
Edit Imaginary Part reads 1.1. Return to the original: amplitude remains 1 and its
equation remains unchanged. Both files were saved with verified typed filenames.
All 65,536 complex samples of the copy equal 1.1 times the original exactly.
The original matches an independent chirp formula to 2.84e-14 using SignalShow's
centered coordinates x = column − 128, y = 127 − row. An initial oracle used the
wrong y-origin; corrected the reference convention, not application code.

After screenshots and verification JSON accompany this record. No native log
exceptions; app closed and process exit verified. This fixes BUG-019. Native
coverage is the 256×256 predefined complex chirp; automated checks cover other
listed copy variants. Deletion and linked-source lifecycle remain separate.
