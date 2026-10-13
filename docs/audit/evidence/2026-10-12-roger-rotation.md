# Roger workflow: native crash reproduction and correction

macOS arm64, Oracle JDK 25+37; baseline 28a4ffc plus the current uncommitted fixes.
Windows remains untested. The supplied PDF describes one representative workflow,
not the application's only acceptance test.

## Reproduced failure

The native nine-step workflow reached `Rotate(sum, 45°) × circular mask` and threw
`ArrayIndexOutOfBoundsException` from `TimesOp.create`: index 65536 for length 65536.
The thumbnail retained the previous unmasked result and the export dialog would
not open. The process stayed alive; this was an AWT event-thread failure that broke
normal actions, not a JVM crash or a measured memory limit.

`ImageDisplayMath.image2Data` allocated width × height + 1 samples. Rotating a
256×256 image therefore returned 65537 samples; arithmetic with another ordinary
256×256 image failed. Reading exactly the raster samples corrects both real and
imaginary rotation results at their shared conversion boundary.

Four RogerWorkflowTest cases failed before the fix and pass after it: rotations
followed by actual Times/Plus operations for 256×256 and 128×64 images, and the full
analytic composition, ordinary angle edit and TIFF write/readback. The full suite
and package pass 402 tests. Logs are linked from the matrix.

## Native recheck

Recreated every supplied step in the corrected app: magnitude/phase rings with
phase center 128, width 16 and power 2; Cartesian step; nested Real/Threshold and
Times; radial chirp; Rotate(step, 180°); duplicate with replacement operands;
Plus; radial rectangle of full width 226; Rotate(sum, 45°) × mask.
Used Threshold maximum 1, minimum 0 and cutoff 0.5; the PDF does not specify these.

The completed result is circular, real and nonnegative. Export now opens and
saves a readable 256×256 8-bit TIFF, range 0–255, 65,914 bytes. The native log has
no exception. The saved PNG is a lossless rendering of that TIFF. The historical
TIFFs have different black/white regions; this is not a claim of identical pixels
or exact historical parameter recovery. The geometry and composition are checked;
the corrected threshold contract has independent regression evidence.

## Text entry

Before the startup correction, diagnostic logging showed mouse events but no
focused Java window or key events. Existing fields are ordinary editable Swing
controls. `GUI` now requests foreground activation at launch when the desktop
supports it. In the final build, CUA click, select-all, typing and Enter changed
phase center to 128, angles to 180 and 45, aperture width to 226, and the export
filename. This requires the app to retain keyboard focus. Direct AX `setValue`
remains unsupported by this JDK's native bridge; no custom test-only input path
was added. An experimental global focus listener was removed because it was not
needed for the successful test. All input-event instrumentation was removed.

## Additional ordinary-use failures

- At 2306×1484 screenshot pixels (about 1153×742 logical points), all Threshold
  controls and full-width previews remain reachable, but their long single row
  crowds the operation documentation out of the center panel. This still needs
  a compact parameter layout; it is not a tiny-window edge case.
- The duplicated chain shares operation instances. Changing the original
  Threshold from 0.5 to 0.75 also shows 0.75 in the duplicate, despite separate
  operands. Fix operation ownership and add a real-output regression before
  marking duplicate-edit independence checked.

The test app was closed after this batch and process exit verified. No SignalShow
instance is currently running. The matrix preview remains on port 8092.
