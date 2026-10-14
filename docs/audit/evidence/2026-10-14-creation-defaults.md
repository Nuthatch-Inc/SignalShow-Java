# Creation defaults and independent axes — October 14, 2026

Ordinary rectangular image editing exposed three settings errors (BUG-059):
Function2DToolBar wrote the selected height into the width preference and the
2D origin into the 1D preference; FunctionCreationOptions read the width
preference when returning the default height. After editing an image to 128×64,
the next real/imaginary or magnitude/phase image became 64×64. Its origin stayed
at the old 2D value while the next 1D signal's origin changed unexpectedly.

Eight actual-control regression cases fail before correction, covering 128×64
and 64×128, both origins, real/imaginary and magnitude/phase source editors.
Each case changes the real width/height combo boxes and origin checkbox, then
calls the same factories as the two ordinary 2D creation menu commands. Only
the JFrame destination and library repaint are supplied without a native window;
control events and model publication run normally.

The three preference references are corrected. After the fix, all eight cases
pass, including both newly created image types, separate 1D origin, cached source
regeneration and a complex addition chain with each new image. Expectations are
explicit constant/phased-complex arithmetic, with every output sample checked.
The 200-test focused creation/resize/imported-editor group also passes.

Before/after logs: `2026-10-14-creation-defaults-before.log.gz` and
`2026-10-14-creation-defaults-after.log.gz`. Test:
`src/test/java/signals/core/CreationDefaultsWorkflowTest.java`.
Full package and short native verification are recorded below. No commit, push,
release, Windows or capacity-edge claim.

## Full package and native Mac verification

All **1,130 tests pass**, zero failures/errors/skips, with Maven package success
in 26.928 seconds. Evidence: `2026-10-14-creation-defaults-package.log.gz`.
The new executable audit JAR is SHA-256
`b195e33a79dfbabd1575645f2d62e9aaeb312c2e5cee96994f01a44e368bde96`.
All seven changed production class files match `target/classes`. No nested JAR,
project tests/JUnit or audit documents were packaged. Details: adjacent
`-artifact.txt`. This is still a local audit build retaining version 1.2.5, not
a newly published download.

One isolated native test bundle used `/private/tmp/signalshow-defaults-home`.
All controls/actions below were operated through CUA; typing was focused and
read back before submission. The width/height selections were committed with
Return and read back as 128 and 64. Native screenshots also confirmed the origin
checkbox unchecked.

1. Create a real/imaginary 2D image. Select Width 128, Height 64 and uncheck
   Zero-Centered. Add a Cartesian Constant in x with amplitude 2, constant 1 in y.
   Save Complex Data as `edited-source`: 128×64, corner origin, all values 2+0i.
2. Create a magnitude/phase 2D image. It opens at 128×64 with corner origin.
   Add Cartesian Constant magnitude 3 and retain zero phase. Save as `next-polar`: 3+0i.
3. New 2D Mathematical System → New Binary Operation (Plus). Explicitly choose
   the first source for the first operand and retain the magnitude/phase source
   for the second. Save `mixed-rectangular-sum`: 128×64, corner origin, 5+0i.
4. Create another real/imaginary image; save `next-cartesian`: 128×64, corner
   origin, zero channels. The defaults survive the intervening operation editor.
5. Create a 1D real/imaginary signal and save `next-one-d`: 256 samples, still
   zero-centered. The 2D origin edit no longer changes the 1D setting.

The five actual native files are in `-native-exports.zip`. Replay with
`python3 docs/audit/evidence/2026-10-14-creation-defaults-native-verify.py`; the
adjacent `-native-results.json` records headers, sample counts, values and hashes.
This verifies 66,048 scalar samples across five saved outcomes. The headless
regressions additionally cover 64×128, both origins, both channels and complex
phase values; the native fixture is deliberately small and bounded.

No app exception or crash occurred. The native log contains only the fresh-home
default-config notice. The sole app exited 0 and final process inspection found
no SignalShow instance. No other app configuration, server or release was changed.
Weekly usage remains 57% available; no usage reset was used.

Lower-priority observation: newly created ordinary functions still receive the
same default name `f0`. The four NewReIm/NewMagPhase actions read getNumFunctions
without incrementing it. This affects labeling, not the verified sample values;
it is recorded for later usability work rather than expanded in this major-error
round. Explicitly selecting operands avoids relying on those duplicate names.
