# Multi-term image composition — October 13, 2026

Native Mac arm64/JDK25+37, JAI1.1.3; starting commitfbbd0cf, unchanged606-test
package SHA25635a110618d7268ee02a2e4290f0b57a2b8e616c0fbf6e66084c681b952a8bee1.
CUA-only native interaction; one app, closed and process exit verified afterward.

Create centered128×128 real terms; imaginary part remains zero:
- A: Gaussian product, amplitude1.2, centers(−12,8), widths(18,14).
- B: Rectangle product, amplitude.8, centers(14,−6), full widths(20,16).
- C: Sine in X, amplitude1, center0, period24, phase30 degrees; Constant1 in Y.

Build A+B with Add and New Term, save it; append Multiply and C, save A+B*C.
Select equals, insert Open Parenthesis; select B, insert Closed Parenthesis. Save
(A+B)*C. The temporary unfinished expressions do not crash. Create linked Clip
(minimum0,maximum.6)→Fourier Transform(Divide by √N), save both stages.

Change addition to subtraction by inserting Subtract then deleting the old Add
symbol. Buttons insert after the selected expression item; they do not replace it.
Change Gaussian amplitude.9, center(−6,5), widths(22,14), Rectangle amplitude.6,
and Sine period20, phase60 degrees. Save source and both linked stages. Duplicate
the source; delete the carrier term and its multiplication symbol only in the copy.
Save (A−B) copy, original(A−B)*C and both original chain results.

All12 exports/196,608 complex samples pass independent analytic source, arithmetic,
clipping and direct DFT checks; max1.988e−14/tolerance1e−9. Grouping and precedence
produce distinct expected images. All source edits propagate to the operations;
copy term removal does not mutate the original or its linked results. Symbols,
parameter entry and previews remain usable. Native log clean; no crash, exception
or typing failure observed. No production change or full-suite rerun needed.

Scope: selected real multi-term128×128 expressions, grouping, arithmetic/parameter
edits, copy deletion and linked operations. Other normal families, complex term
composition, origins and Windows remain separate scenarios.

Replay: `python3 docs/audit/evidence/2026-10-13-composite-verifier.py docs/audit/evidence`.
