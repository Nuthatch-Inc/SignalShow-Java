# Ordinary imported-image edits — October 13, 2026

BUG-024: DataFunctionTerm2D returned its original samples whenever source and
output dimensions matched, ignoring amplitude, position, size and crop settings.
Normal edits therefore left the function and downstream operations unchanged.
The shared image scaler also squeezed moved images into the clipped bounds,
and an image editor could retain the previous term's preview after replacement.

Raw-sample reuse now requires an unchanged transform. Both real/imaginary and
magnitude/phase sample factories initialize image centers for the selected origin;
ImageImport initializes the padded size. Scaling happens before clipping, and
crop uses the requested rectangle. The image editor initializes the correct data
editor on its first term and refreshes it on same-family replacement. Existing
controls, native libraries and operation paths remain in use; no UI copy added.

Nine regressions failed before correction and pass afterward. Eight exercise
amplitude, translation, half-size scaling and cropping on a 128×64 image in both
origin modes through actual Swing parameter fields, with a previously evaluated
linked Threshold result. They independently verify every sample and unchanged
original data. The ninth checks initial and replacement data previews. Focused
image suites pass 23 tests; the full package passes 472 with no failures, errors
or skips. Source diff check is clean. The obsolete memory cap remains removed.

Native checks used Roger's 256×256 TIFF in the packaged app. Typed values were
read back before Return. Saved source and linked Threshold(200) samples match
independent expectations at every pixel after amplitude 2, translation (+16,+8),
and resizing to 128×128. For half-size bilinear resizing, the independent oracle
averages each original 2×2 block, doubles it, and places it in the center.
Committed crop mode retains the central 128×128 source region, also exact before
and after Plot → Edit. The cropped Threshold output is exact. Nine exported
files each contain 65,536 checked real samples and a zero imaginary channel.

An initial AX dropdown selection changed the provisional value but did not commit
it. Those early files named `import edit cropped.txt`, `import edit threshold
cropped.txt` and `import edit cropped reopened.txt` still contain scaled images;
they are not crop evidence. A focused native check selected crop with Up, verified
the provisional value, then pressed Return. The rendered control and pixels then
changed, and crop survived view changes. This was an automation commit issue,
not a demonstrated mode-retention defect. Successful crop fixtures contain
`cropped committed` in their names. No additional code was changed for it.

The native app was closed and process exit verified. The bounded log slice
contains only configuration load/save. Screenshots show current size/amplitude
controls, previews, thumbnails and crop state. Exact native JPEG resampling,
Windows, dragging, large dimensions and every built-in image variant remain
outside this batch. Core sample persistence and other existing regressions pass;
this does not add editable-definition persistence.
