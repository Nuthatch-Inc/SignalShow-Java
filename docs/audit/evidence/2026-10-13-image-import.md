# Ordinary image imports — October 13, 2026

BUG-023 prevented ordinary images from loading correctly. The native Load Image
path used a JAI JPEG decoder that depends on removed `com.sun.image.codec.jpeg`
classes. Roger's 1920×1620 Circular_resolution_target.jpg produced File I/O Error
on JDK 25. Separately, square padding rounded dimensions down to a power of two:
a 192×128 image or text array could throw, while 160×96 data could be clipped.

Extracted the native action's decoder into ImageImport and replaced obsolete JAI
file decoding with JDK ImageIO. Raw grayscale values remain unchanged; color
images retain the existing red-channel interpretation. Square padding now rounds
up with integer arithmetic. Both image and text padding callers share the fix.
No UI controls or wording were added.

Six regression cases use real image files with independently known samples:
JPEG 192×128 and 128×128, RGB PNG 192×128, grayscale TIFF 128×128, RGB BMP 160×96,
and 192×128 text values. Before correction: one assertion failure, four errors,
one pass. After correction: all six pass, including every centered sample and a
Real → Threshold chain. The full package passes 463 tests without failures,
errors or skips. Source diff check passes; raw logs retain original whitespace.

In the native packaged app, Load Image now accepts Roger's JPEG. Replacing it
with the supplied 256×256 test_REAL_PART.tiff also succeeds. Save Complex Data
preserves every TIFF pixel exactly (65,536 real samples, zero imaginary channel).
Threshold at 127.5 produces the independently expected binary image at every
pixel, with 27,831 nonzero samples. Plot and save remain usable. Native files are
in the isolated home as `import jpeg.txt`, `import tiff.txt`, and
`import tiff threshold.txt`. The user originals were not changed.

Native typing was verified: select the current field text, type the replacement,
read back the exact value, then submit. One initial filename insertion appended
within existing text; it was detected before submission and corrected by selecting
the full old value. Threshold typing was also read back before Return.

Limits: JPEG loading/resampled output is checked for usability, not exact
resampling accuracy. Its term preview appeared smaller than the displayed size
controls implied; imported-image parameter edits need the next bounded check.
PNG/BMP and text evidence is programmatic, not native chooser coverage. This
batch does not certify all file variants, transformed imports, or Windows.
Native app closed and process exit verified. This batch's log slice contains
configuration load/save only; older log sections retain the reproduced failures.
