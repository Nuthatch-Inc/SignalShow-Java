# Native intensity adjustment chain — October 13, 2026

Mac arm64/JDK25; unchanged578-test package, code1a4b7df. JAR SHA256
c92a82fbaa49f34640451e83568997a6b2c156c7f6cc82806f5b5d7b69bb6d88.
All creation, editing, operation insertion, duplication, saving and view actions
used native Swing controls via CUA. No test-only model injection.

## Ordinary workflow

Create centered64×128 real/imaginary `intensity image`, real Cartesian Triangle ×
Gaussian, imaginary zero. Horizontal Triangle: amplitude2, center5, width20.
Vertical Gaussian: amplitude1, center−9, width24, exponent2. The off-screen Triangle
AX row had no frame; native search selected it normally. This was tool targeting,
not an application failure. Typed parameters were read back before Return.

Create three linked systems: Offset(−0.25+0.16i), Clip[0,0.9], Square Root. Save
source and each result. Edit horizontal amplitude2→1.25 and center5→−6; change
Offset to−0.1+0.36i and Clip maximum to0.64. Revisit and save all four stages.
Duplicate Clip; its controls retain[0,0.64]. Change copy maximum to0.36 and save.
Revisit original: controls remain[0,0.64]. Save original Clip and downstream Square
Root again; visit the final Plot. Save screenshots/AX, close app and verify exit.

## Results and scope

All11 exports/90,112 complex samples match independent triangle and Gaussian
formulas, complex addition, per-channel clipping and componentwise square root.
Maximum error2.221e−16, tolerance1e−9. The copied Clip edit does not change the
original or its downstream result. Source and parameter edits propagate; controls
remain usable. Native log clean. No product changes or extra model tests needed;
the earlier578-test package remains valid.

Square Root intentionally operates independently on real and imaginary channels,
as its local help states. This check uses nonnegative clipped channels and does
not certify a complex principal root or negative-input behavior. Windows, other
origins and full handbook/equation review remain outside this bounded check.
Replay: `python3 docs/audit/evidence/2026-10-13-intensity-verifier.py docs/audit/evidence`.
Compressed raw exports and checksums are retained with the replay script.
