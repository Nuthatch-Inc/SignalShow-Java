# Ordinary complex translation and scaling — October 13, 2026

Native macOS arm64/JDK25, 578-test package at1a4b7df. All12 native exports (98,304 complex samples) pass independent analytic/indexing checks; maximum error6.11e-15, tolerance1e-9. No product change or extra full-suite run was needed.

Create a128×64 centered Fourier Basis Function from the existing2D menu. Set x-factor amplitude1.5, center3 and period32; set y-factor center−2, keeping amplitude1/period16. The result is1.5 exp(2πi[(x−3)/32+(y+2)/16]). Verify source export.

Translate by(13,−7), save with wrap unchecked, then check wrap and save. Chain Scale with real0.5/imaginary0.25, then Plus with the original source as the explicitly selected second operand. Save both stages. Edit translation to(−9,5), uncheck wrap, edit Scale to(−0.25,+0.75), and save the translated/scaled/summed results.

Reopen the original Fourier source through Edit Imaginary Part and change its y-factor period16→32. The linked real/imaginary component factors update coherently. Save the source and all three downstream stages. Shift, wrap and complex-scale settings survive document revisits; the final Plot of Result is usable. Parameter fields read back correctly before commit throughout.

The verifier constructs the complex exponential directly, uses independent source-pixel indexing for fill/wrap translations, and performs complex scalar multiplication and addition. It checks dimensions/origin and every complex value. Screenshots preserve shift controls and final channels; AX records preserve the source/scale edits. Run `python3 docs/audit/evidence/2026-10-13-shift-verifier.py docs/audit/evidence` to replay. The JSON includes all file hashes and ranges.

The bounded native log contains no exception/error lines. The single app was closed and process exit verified. This covers the stated centered rectangular fixture and ordinary parameter choices; uncentered native variants, other source families and Windows remain separate gaps. The existing Translate help text describes periodic wrapping but does not explain the unchecked zero-fill option; this prose discrepancy is recorded without changing UI wording or displacing normal-usage work.
