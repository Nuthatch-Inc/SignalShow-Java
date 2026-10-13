# Linked-source resizing — October 12, 2026

On the native 449-test build, A × A updated after resizing the chirp source from
256×256 to 128×64. Its 8,192 saved complex samples equal the independently computed
source square exactly. Width currently also sets height; then set height separately.

With independent sources, changing A from 128×64 to 256×256 while B remained
128×64 threw an ArrayIndexOutOfBoundsException from TimesOp during thumbnail
painting. The editor kept stale result imagery. This reproduced BUG-020 using
normal size controls, not extreme or zero dimensions.

The expression evaluator now checks each binary operation's dimensions before
calling its kernel and propagates unavailable upstream results. Matching sample
counts do not make different rectangular shapes compatible. Reopened/duplicated
systems tolerate temporarily unavailable inputs. Existing previews hide stale
data; result export disables while unavailable; plot/FFT/histogram stay in the
editable expression; save/export dialogs require an actual result.

Six focused tests cover 1D/2D grow/shrink/recovery through Times → Real, actual
thumbnail painting, downstream copy/rebind, equal-count different shapes, preview
visibility, and result view routing. Before: two resize bounds exceptions, one
wrong-result assertion and one stale-preview assertion; the two view-routing
fixtures also attempted desktop display instead of returning to their editor.
After: all six pass; focused group 13 and full package 455 pass with no failures.
The view fixtures substitute only the editor-display callback for headless use.

Native 455-test build: resize one copy to 128×64 while the first remains 256×256.
The old preview disappears, the thumbnail has no false result, and Export Result
as Data is disabled. Plot, FFT and Histogram safely keep the editor available;
Save Complex Data and Export Function do not open dialogs for missing results.
No post-fix native exceptions were logged.

The attempted subsequent size restoration did not commit: native keyboard focus
became ineffective again, including a fresh session. This is not counted as a
native dimension-restoration pass. A mouse-driven check loaded the earlier
128×64 source and replaced first one operand (unavailable result), then the
other (recovered result). Plot and saving worked; all 8,192 complex samples equal
the independently computed source square exactly. Saved using the displayed
empty-base default, then copied `.txt` to the evidence fixture name; no typed
filename pass is claimed for this recovery. All test instances closed/exited.

Binary operations still require matching dimensions; there is no automatic
resampling/alignment or explanatory mismatch message. This batch preserves the
existing unavailable-result interface without inventing new UI wording. A manual
size-restoration check remains queued. General keyboard automation reliability
remains unresolved. Windows was not tested.
