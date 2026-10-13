# Ordinary 2D band-filter edits — October 13, 2026

Four added EDT workflow cases cover centered/uncentered 32×16 complex images and
Ideal/Gaussian Band Pass/Band Reject. Real production demo controls select the
filter and edit amplitude, center, separation and slit width. Known Fourier modes
provide independent expected reconstructed images. The tests also check that pass
plus reject recovers the scaled source, both channel displays follow the result,
dimensions/origin are preserved, and source samples remain unchanged.

The initial display-bound assertion was too strict for the existing three-decimal
scale controls; its 0.000029 rounding difference was not treated as a product bug.
Display checks allow half the final decimal place. Full sample checks retain
1e-9 tolerance. No production math or UI behavior was changed in this batch.
Focused class: 14 passed. Full offline package: 553 passed, zero failures/errors/skips.

Native CUA on macOS arm64/JDK 25: created an uncentered 256×256 image with real
cos(2πx/16), imaginary zero, and saved it. Independent comparison checked all
65,536 complex samples; maximum real error 1.12e-16, imaginary exactly zero.
Opened 2D Frequency Filtering and exercised nine recorded stages:

- Ideal pass at separation 16, width 8 preserves amplitude 1; reject removes it.
- Moving the pass band to separation 12, width 4 removes it; editing separation
  back to 16 restores it.
- Gaussian pass at separation 12, slit width 8 has expected gain 0.455938...;
  displayed bounds ±0.456. Reject has complementary bounds ±0.544.
- Center 4 and amplitude 0.7 produce bounds ±0.7. Imaginary remains zero;
  switching back to Real returns the ±0.7 result.

AX records capture fields and scales, and the screenshot shows the Gaussian
stripe result with editable controls visible. Zero outputs display the standard
±0.01 constant-image range. Native result samples were not exported: the demo
has no result export in this flow. Strict whole-image values are verified through
its actual Swing calculation path; native checks establish field routing, channel
switching, visible patterns and scales. Other inputs, 1D band filtering and Windows
remain separate scope. No native exception appeared. Closed the only test app and
verified process exit. Nothing pushed or released.

The subsequent ordinary reopen check confirms a separate setting-lifetime defect:
after Gaussian Band Pass, center 4, separation 12, width 8 and amplitude 0.7,
selecting the source and returning to Filtering Demo restores Ideal Low Pass,
amplitude 1, center 0 and width 32. Output changes from ±0.7 to ±1. Before/after
AX records are saved as 2026-10-13-filter-reopen-*. The demo-settings scenario
remains open; this successful band-edit check does not certify reopening.
