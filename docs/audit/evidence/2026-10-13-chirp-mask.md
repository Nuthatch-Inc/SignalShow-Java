# Native complex chirp and aperture chain — October 13, 2026

Mac arm64/JDK25; native578-test package at code commit1a4b7df, unchanged by
this batch. SHA256 c92a82fbaa49f34640451e83568997a6b2c156c7f6cc82806f5b5d7b69bb6d88.
All creation, parameter editing, operation insertion, file saving and view actions
used the running Swing app through CUA. No model injection or UI backdoor.

## Fixture and interactions

1. New 2D Complex Chirp, centered128×64, named `aperture chirp`. Set amplitude1.5,
   radial center2, width18, phase30°, exponent2 through the real-part editor.
2. New 2D real/imaginary function, Aperture → Cylinder, named `chirp stop`.
   Set centered128×64, amplitude0.8, center(7,−4), radius22, imaginary zero.
3. New system: Times. Explicitly select chirp as first operand and aperture as
   second. New linked system: Conjugate. New linked system: Squared Magnitude.
   Save the two sources and each of the three results with Save Complex Data.
4. Edit chirp **through the imaginary-part editor**: amplitude2, width24, phase75°.
   Move aperture x-center7→−9 and radius22→18. Revisit and save all five stages.
5. Visit final Plot of Result; the intensity is a translated disk of2.56 with
   zero imaginary component. Revisit chirp real editor: values2,2,24,75,2 retained.
   Both source thumbnails and downstream previews update. Operators and controls
   remain visible and usable. Close the app; process inspection verifies exit.

## Independent results

The replay script constructs the chirp directly as
`A exp(i[π((hypot(x,y)−2)/alpha)^2 + phase])` on the centered pixel coordinates,
constructs the circular mask from squared distance, then uses complex multiply,
conjugation and absolute-square. It does not call Java or reuse application arrays
as expected answers. All10 exports/81,920 complex samples pass at tolerance1e−9;
maximum error2.115e−14. Both channels, dimensions and origin are checked. Native
log is clean. Automated typing and parameter retention pass.

Replay: `python3 docs/audit/evidence/2026-10-13-chirp-mask-verifier.py docs/audit/evidence`.
Compressed raw exports, descriptors, SHA256s, two screenshots, final AX values and
the bounded launch log are retained alongside this note. No source changes or
additional tests were necessary; do not rerun the passing package for evidence-only
edits. No Windows, capacity, arbitrary chirp orders or handbook certification.
