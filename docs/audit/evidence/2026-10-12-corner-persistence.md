# Non-centered rectangular persistence — October 12, 2026

Using the current 437-test build in the native macOS app: create a 128×64 complex
chirp, set phase 30°, turn off Zero-Centered and name it `corner origin chirp`.
Save Complex Data, load it, and save the loaded image to a second file. Dimensions,
origin, multiword name and all 8,192 complex samples survive exactly.

Create a mathematical system from the loaded image, apply Threshold and append
Times with the same loaded image. At the displayed default cutoff 0.5, every
saved complex sample matches independent thresholding and complex multiplication
exactly. A fresh session repeats loading and operation creation, types cutoff
0.25, saves Threshold alone and then Threshold × source. Both named filenames
were read back before confirmation; every output sample agrees exactly.

The first session lost automated keyboard focus after entering operations;
attempted cutoff 0.25 and filename typing did not commit. Its actual cutoff was
0.5 and its result saved as `.txt`, subsequently copied to a descriptive fixture
name for evidence. This failed typing attempt is not counted as a pass. The
fresh session passed typing through the same public controls. The exact cause
of the intermittent native input issue is unconfirmed; no speculative focus
code was added. Automation now requires filename readback before confirming save.

Evidence: `2026-10-12-corner-verification.json`, `2026-10-12-corner-chain.png`,
and clean native log. Fixtures are in `/private/tmp/signalshow-roger-home/`:
`corner origin chirp.txt`, `corner origin reopened.txt`, `corner threshold 025.txt`
and `corner threshold 025 times.txt`. Both test instances were closed and each
process exit verified. No source changes or repeated full build were needed.

Scope: native 128×64 non-centered persistence and subsequent Threshold/Times;
earlier native centered 128×64 and model 32×16 both-origin evidence remain valid.
No editable-definition persistence or Windows validation is claimed.
