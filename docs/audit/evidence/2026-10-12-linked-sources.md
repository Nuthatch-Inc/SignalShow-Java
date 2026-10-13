# Linked source edits and reused results — October 12, 2026

## Reproduced defect and correction

OperatorSystem compared inputs with their original IDs even after recomputing an
edited source. The updated chain therefore remained dirty permanently. Three
reads after one edit in a three-stage Plus → Threshold → Real chain performed
**27 additions and 9 threshold passes**, instead of one each. This was reproduced
in both 128-sample 1D and 128×64 2D production paths. It demonstrates repeated
allocations during ordinary use, not the cause of a particular historical crash.

The system now snapshots the input versions used by each regenerated result and
reads each input once per dirty check. Two before/after regressions verify
independent expected samples, repeated source edits, operand replacement, cache
eviction and stable reuse. Addition and threshold each run once after an edit.
The targeted ownership/insertion group passes 14 checks; full package passes
**439 tests**, with no failures/errors/skips.

## Native verification

Two 256×256 complex chirps: source A has width 16, phase 0°; source B has width 32,
phase 45°. Multiply A × B, then use that operation system as the input of Real.
Save sources and both results, change A's amplitude from 1 to 1.1 using the native
spinner, return to both operations and save again. All 65,536 complex samples
match independent multiplication before and after; the downstream Real output
matches the product's real part exactly and has zero imaginary samples. All
65,536 real output samples change. Replacing the product's second input with A
propagates to the existing downstream chain; Re(A × A) also matches exactly.

The native log is clean; the single test instance was closed and process exit
verified. Screenshot and JSON comparisons accompany this record. Fixtures are
`linked source A before.txt`, `linked source A after.txt`, `linked source B.txt`,
`linked product before.txt`, `linked product after.txt`, `linked real before.txt`,
`linked real after.txt` and `linked real replacement.txt` in the isolated test home.

Native keyboard focus was intermittent. Initial source parameter/name edits were
read back from labeled controls. Later operations used the native spinner and
saved with the displayed default `.txt` filename; each result was copied to its
descriptive evidence filename before the next save. This does not certify typed
operation-result filenames or resolve the focus issue. Parameter automation must
reacquire controls by label after each edit because AX indices can change.

## Next ordinary usage defect

Reopening the existing multiplication leaves its final input visibly selected,
but the input picker blank. Clicking that already-selected term does nothing.
Selecting the other input and returning reveals the expected choices, after
which replacement works. Native screenshot: `2026-10-12-operation-reopen-before.png`.
Construction suppresses selection events before initialization and never presents
the final selection afterward. This is BUG-018 and is the next active check.

Scope: normal linked edits/replacement and result reuse. Native resizing and
incompatible-dimension handling remain a separate pending scenario. No Windows,
peak-memory or full performance benchmark claim is made.
