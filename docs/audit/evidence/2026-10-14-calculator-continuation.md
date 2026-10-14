# Calculator continuation — October 14, 2026

Baseline: Juliet's published `a576f27e720a336d9e398ff87fd07d31671f877b`, verified against the remote branch; working tree clean on resumption. macOS arm64 / JDK 25, headless Swing on the event-dispatch thread. No native app or desktop input while Juliet uses Zoom. No commit, push or release in this batch.

## BUG-056 — Clear detaches subsequent edits from the library chain

Open A*B with complex inputs and a cached downstream Intensity result. Click Clear, add a new term and pick B. Both actual 1D/2D editors leave the original library chain empty: subsequent edits are published into a new unlisted OperatorSystem. The preview's temporary calculation can hide the missing saved result.

The regression uses real Clear/New Term/New Binary Operation buttons, input-picker mouse events, shared event dispatch, saveStateToModel and preview. Only desktop library lookup/repaint is supplied without a JFrame. A small protected repaint boundary leaves production behavior unchanged; the test does not replace model publication.

Before: 2/2 new cases fail because the rebuilt library result is null (`2026-10-14-operation-clear-before.log.gz`). Fix: retain the original system and publish the cleared expression from the shared panel implementation. After: all 26 focused editing, insertion, reopening, lifecycle and duplication tests pass (`2026-10-14-operation-clear-after.log.gz`), including both channels, downstream invalidation/recovery and reopen.

Native visual acceptance and Windows remain untested. Weekly usage at this checkpoint: 59% remaining. Next: ordinary grouping edits, including closing an inner group before a following multiplication.

## BUG-057 — Closing a group evaluates operations outside that group

Build `2 + 3*4*5` with ordinary constant image terms; insert parentheses around `3*4`. `2+(3*4)*5` becomes 70 instead of 62. Actual complex operation editors show the same error: at the first sample expected real 37.5, observed 44.5. All three new UI cases fail in `2026-10-14-grouping-before.log.gz`.

Both CombineTermsRule and CombineOpsRule popped the entire operator stack when closing a group, including operations outside the matching opening parenthesis. Stop at that opening delimiter and keep closing delimiters off the operator stack. Unfinished draft editing behavior is otherwise preserved; this is not an invalid-input sweep.

After: 128 focused tests pass in `2026-10-14-grouping-expanded.log.gz`: 92 complex arithmetic cases (52 newly added), 24 new grouped creation/copy/Intensity cases, eight actual image-creation workflows and four operation-editor workflows. Expectations are explicit arithmetic formulas, including nested groups, subtraction/division, Real/Conjugate/Magnitude, both dimensions and origins. The actual GUI cases insert parentheses and reopen the saved expression.

Comparison: the current local Chirplet `src/lib/signals/ExpressionParser.js` explicitly pops only to the matching opening delimiter (lines 196–202). Its ExpressionWorkflow tests also retain nested group boundaries. This is a source comparison, not a newly executed Chirplet application check; independent Java expected values remain the oracle.

Weekly usage remains 59% available. Next: operator replacement and Threshold parameter edits through the real operation selector and saveStateToModel.

## Actual operator selection and parameter editing

18 operation-editor cases pass, including 14 new cases: choose Plus/Minus/Times/Divide with explicit A and B inputs, replace B with C and reopen; compose Real/Conjugate/Intensity around Threshold, type Maximum 3, Minimum -1 and Threshold .75 then 1.25, check both channels, source preservation, cached linked Intensity and reopen. No production correction was needed in this batch. The initial binary fixture incorrectly assumed the default operand was the first library item; it now explicitly picks both operands, as an ordinary user would. This fixture correction is not an application bug.

Evidence: `2026-10-14-operation-selection.log.gz`. All actions are headless Swing; no native focus/typing claims. Next: prepend unary operations to an existing expression using the actual calculator insertion button.

## BUG-058 — Prepending a unary operation drops the result or consumes the wrong operand

Open Threshold(A), select the insertion cursor before it, click New Unary Operation, then select Real, Conjugate or Intensity. The calculator produces consecutive prefix operators, such as `Real Threshold(A)`. All six actual 1D/2D insertion cases lose the result. The parser previously popped the first unary operator before its operand arrived. In a larger arithmetic expression it can instead consume an earlier operand and return incorrect channel values.

Before: 22 failures in 132 tests (`2026-10-14-prefix-before.log.gz`): six actual editor cases and 16 independent complex expression cases. Fix: track whether an operand is complete; pending prefix operators nest right-to-left, while existing postfix forms keep their behavior. This also agrees with Chirplet's explicit prefix handling; Java's postfix compatibility is retained.

After: all 183 focused cases pass (`2026-10-14-prefix-after.log.gz`), including 24 actual operation-editor cases, 116 arithmetic cases, 24 grouped channel-creation cases, eight image-authoring workflows, seven Roger regressions and four duplication regressions. Eight new postfix-specific complex/origin checks protect the older form used by Roger/duplication fixtures. Full-package verification follows.

## Full verification checkpoint

`mvn -o -B -ntp -Djava.awt.headless=true package` passes **1,122 tests, zero failures/errors/skips**, 26.643 seconds. There are 125 new cases relative to Juliet's 997-test published baseline. Full log: `2026-10-14-calculator-package.log.gz`. No headless test exception was found in the full log.

Local executable JAR built and inspected: SHA-256 `c43785bc37ee0bb2d66338724576ad6fce6809390f12e8dd2a85a8186780de7e`. The five changed production classes exactly match the compiled classes. No nested JAR, project test/JUnit class or audit-document payload was found. Manifest retains `Main-Class: SignalShow`. Details: `2026-10-14-calculator-artifact.txt`. This is an uncommitted local audit build retaining the existing version; no new GitHub release or push is claimed.

The remaining verification for these fixes is a short native Mac check when Zoom is finished: clear/rebuild/reopen a chain; group a normal four-input arithmetic expression; prepend Real to Threshold and edit a normal threshold. Keep at most one owned app and verify focus before automated input. Native pixels/focus/rendering have not been tested in this round. Windows remains deferred. No native app, background audit job or scheduled continuation was started.

Final checkpoint usage: weekly 58% remaining; no other applicable window. Native verification awaits desktop availability, not usage capacity. Audit JSON/report validation and diff whitespace checks pass.

## Native follow-up after desktop availability

Juliet confirmed the desktop was available. The formerly pending Mac check now
passes all three fixes through real creation/editing controls and seven saved
complex exports. See [native evidence](2026-10-14-calculator-native.md). All owned
test windows/processes were closed. Windows remains separate and pending.
