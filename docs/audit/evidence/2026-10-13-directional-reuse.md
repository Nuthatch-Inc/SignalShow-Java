# Native directional calculus and exported-data reuse — October 13, 2026

Mac arm64, JDK25+37, JAI1.1.3; branch codex/signalshow-usage-stabilization,
starting commit46f05e4. CUA-only native actions. One instance at a time, closed
and exit verified before rebuild and at completion. No push or user-file changes.

## BUG-041: first-open sampled-image editor is empty

Load Complex Data on a saved64×128 image: samples plot, but the first real or
imaginary editor shows an empty expression and creation-family choices. The same
failure follows Export Result as Data from a populated operation. It prevents
ordinary reuse/editing, despite numerically correct saved results.

Both populated2D FunctionFactory methods left newState=true. The first toolbar
therefore skipped loading the existing Data terms. They now mark populated
functions as existing. Blank creation factories retain their creation state.
Function2DToolBar uses its existing setContent attachment method to retain the
active tab and permit testing the real editor without a native JFrame.

Six EDT cases cover file loading, operation output and magnitude/phase factory,
both origins. They failed with zero visible terms before the factory correction;
afterward both editors contain the original Data term and expected samples.
A prior imported-image fixture also lost its weakly registered listener to GC;
the fixture now owns a strong reference, as a production editor does. This is
test ownership cleanup, not a production broadcaster change.

Focused23 cases pass; full offline package584 tests pass, zero failures/errors/
skips. Native retest uses JAR SHA256
c37919a04da1afc01361d148c80aa6b0f141a843a0d001d4577a671a128c5f7b.

## Native actions and independent results

Load the previous intensity batch's centered64×128 Triangle×Gaussian image:
2 max(1−|(x−5)/20|,0) exp(−π((y+9)/24)²), imaginary0.
Before the fix, save source, Derivative X, Integral X of that derivative; switch
both stages to Y and save each; export the result as a data function. Six saved
arrays were correct, but both first-open editors were empty (screenshots).

After the fix, reload the same file. Data term, thumbnail and amplitude/position/
size controls appear immediately. Set amplitude1.5. Create separate linked
Derivative and Integral systems, save both X stages, then switch both to Y and
save. Export Result as Data; its initial editor is populated. Save, change only
its real amplitude to2, save, open imaginary editor (Data/amp1 retained), visit
another document and return (imaginary editor retained). Apply Negate in a new
system using this exported function. Set the loaded source amplitude to.5 and
save source, original live derivative/integral, exported copy and Negate again.
Live stages update; the independent copy and its Negate remain unchanged.

Nineteen exports/155,648 complex samples match independent NumPy expressions,
maximum error8.882e−16 at a1e−9 acceptance tolerance. Each intermediate is checked,
not just derivative/integral round-trip equality. X differences/integration run
left-to-right; Y runs bottom-to-top. See replay script and per-file hashes/results.
Real result geometry and operation symbol inspected; both sampled channel editors
show usable controls. Native before/after logs contain no application exceptions.
Typing worked with target/readback checks. One transient AX invalid-element error
was resolved by fresh state before retrying; a menu-label mismatch was corrected
from current menu contents. File chooser used screenshot-grounded clicks where
AX entries lacked frames. These tool-routing issues are not counted as app bugs.

## Limits

Native case is centered, real-valued64×128. Both-origin and polar first opening
are covered by EDT tests, not claimed native checks. Broader directional semantics,
other source families, Windows and full handbook review remain separate work.
Renaming a custom-equation source was not verified: its compact descriptor kept
the existing equation. No claim of metadata rename success is made here.

Replay: `python3 docs/audit/evidence/2026-10-13-direction-verifier.py docs/audit/evidence`.
