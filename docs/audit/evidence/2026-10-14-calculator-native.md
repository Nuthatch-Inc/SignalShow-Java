# Native calculator verification — October 14, 2026

Juliet confirmed the desktop was available. This short native Mac run closes the
pending check for BUG-056–058. No production changes were made during this native
batch. The prior full package remains 1,122 passing tests.

## Build and environment

macOS arm64, isolated test bundle at
`/private/tmp/signalshow-fixed-smoke/SignalShow.app`, with local audit JAR SHA-256
`c43785bc37ee0bb2d66338724576ad6fce6809390f12e8dd2a85a8186780de7e`.
Its private home was `/private/tmp/signalshow-calculator-home`. The published
release and real user configuration were not replaced. All creation, operator
selection, edits and exports used the visible application through CUA. Focus and
text readback were checked before submitting typed values.

## Native workflows and saved results

Create a centered 256×256 real/imaginary image. Build the real channel using
Cartesian constants as `2 + 3 × 4 × 5`, then insert parentheses around `3 × 4`.
Set the imaginary channel to a Cartesian Gaussian in x: amplitude 2, center 0,
width 16, order 2, with constant 1 in y. The exported source is independently
checked against `62 + 2i exp(-pi ((column-128)/16)^2)` at every pixel. The old
creation grouping returned real 70 instead of 62.

| Native action | Saved result |
| --- | --- |
| Create the grouped complex source | `grouped-source.txt`: real 62; imaginary Gaussian, peak 2 |
| New Mathematical System → New Unary Operation → Threshold; type Maximum 3, Minimum -1, Threshold .75 | `threshold-075.txt`: both channels thresholded independently |
| Select the start of the expression; prepend New Unary Operation → Real | `real-threshold-075.txt`: real 3, imaginary 0; both operator symbols visible |
| Leave and reopen the saved chain; select its inner Threshold; type Maximum 4 | `real-threshold-edited.txt`: real 4, imaginary 0; the saved result follows the edit |
| Clear the same chain; New Binary Operation defaults to source + source | `clear-rebuild-sum.txt`: both channels twice the source |
| Leave and reopen that rebuilt chain | `clear-rebuild-reopened.txt`: identical to its pre-reopen export |
| Extend with Times; insert parentheses around the middle operand, giving `z + (z) × z` | `grouped-operation.txt`: `z + z²`, center 3902 + 250i; old parser would give 7680 + 496i |

All seven files were saved through **File → Save Complex Data**, which reads the
selected library object rather than the editor's temporary preview. The archive
`2026-10-14-calculator-native-exports.zip` preserves these exact files. Run
`python3 docs/audit/evidence/2026-10-14-calculator-native-verify.py` to repeat the
independent comparisons; results and hashes are in the adjacent `-results.json`.
The checks cover 917,504 scalar samples, not 917,504 distinct usage scenarios.

## Visual behavior and cleanup

Native screenshots showed readable Plus/Times/Real/Threshold and parenthesis
symbols. Threshold's three inputs remained above the preview area. The real
preview was fully visible; scrolling provides access to the lower imaginary
preview when parameters occupy additional height. With Real or Times selected,
both full channel previews were visible. The adjacent PNG was saved through the
application's Save Screen Shot command and visually inspected; it captures the
final grouped expression and both rendered result panels.

No exception/error/crash was captured; stdout contained only the expected message
about defaults in the fresh private home. The owned native process exited with
code 0. A post-quit AX lookup reopened an empty test window; that helper-created
window was also quit. Final process inspection found no SignalShow processes.
Keep post-quit verification process-based so the UI helper does not relaunch it.
Only one test instance was open at a time. Existing user servers were preserved.

This verifies the stated Mac interactions and exports, not all native workflows,
other sizes, a long session or Windows. No commit, push, release, automation,
agent or usage reset was used. Weekly usage at completion: 57% remaining.
