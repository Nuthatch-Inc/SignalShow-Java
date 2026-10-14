# Spatial autocorrelation chain — October 13, 2026

Native Mac arm64/JDK25+37, JAI1.1.3; starting commit48061bc, unchanged606-test
package SHA25635a110618d7268ee02a2e4290f0b57a2b8e616c0fbf6e66084c681b952a8bee1.
CUA-only native interaction; one app, closed and process exit verified afterward.

Create a centered64×128 Cartesian image from distinct rectangles. Real: amplitude1,
center(5,−3), full widths16×12. Imaginary: amplitude.5, center(−4,7), widths8×18.
Create three linked systems: Auto Correlate, Scale(.01+0i), Real. Save source and
all three stages; inspect the autocorrelation's four-channel plot.

Edit real Y center−7 and full width20; imaginary X amplitude.75, center6, full
width12. Save all four stages. Change Scale to.02+.01i and save scale and Real.

All10 exports/81,920 complex samples pass independent NumPy references. Rectangle
boundary samples use established half weight. Autocorrelation is computed from
direct periodic spatial sums, without FFT: R(lag)=sum f(u+lag) conjugate(f(u)).
Zero-lag energy checked separately. Maximum complex error5.163e−12 at tolerance
1e−9; no tiny-error investigation. Complex gain mixes channels and downstream Real
keeps exactly the expected real result. Both initial and edited native result
patterns are visible and current. Native log clean; no crash, exception or typing
failure observed. No production change or full-suite rerun needed.

Scope: selected ordinary centered64×128 complex rectangles, edits, complex gain
and extraction. Other input families, origins, very large images and Windows remain
separate checks. Existing Rectangle notation issue is unchanged and lower priority.

Replay: `python3 docs/audit/evidence/2026-10-13-autocorrelation-verifier.py docs/audit/evidence`.
