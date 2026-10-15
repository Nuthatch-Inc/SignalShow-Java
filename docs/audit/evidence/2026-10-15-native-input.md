# SignalShow native typing investigation — October 15, 2026

Juliet requested reliable automated text entry so basic workflow testing can
continue. This is a Continuous Audit batch on main. No commit, push or release.

## Outcome and limits

Automated keyboard typing and paste work in the normal packaged Mac application
when its field has keyboard focus. Nineteen field entries passed in an ordinary
256×256 Gaussian × Rectangle → Threshold → Real workflow and a controlled restart.
Both channels of four native exports match an independent Python calculation.
This restores a usable testing path; it does **not** resolve in-process recovery
when the native application loses focus. The latter remains a recorded limitation.

The final clean package passes **1,256 tests**, zero failures/errors/skips. All
566 production classes match the executable, and it contains no project tests,
JUnit, old nested builds or diagnostic launcher. The owned temporary app uses the
normal `SignalShow` entry point again. Version remains local/unpublished 1.2.6.

## Diagnosis and rejected changes

A temporary plain Swing probe used the exact bundled Java 25+37 runtime and Aqua
look and feel. Keyboard replacement, paste and numeric spinner typing/commit all
worked. Direct native `setValue` on a plain JTextField failed with -10005, just as
in SignalShow. A separate final check on SignalShow's numeric stepper also failed
with the same not-settable error and left amplitude 1 unchanged. Direct value
replacement is therefore unavailable on these tested Mac controls; missing field
names were a separate application defect already addressed by BUG-070.

The event log reproduced another failure: after Hide SignalShow and AX Raise, a
click reached JTextField while Java had no focused window or focus owner. No keys
were sent after the focus guard failed. A targeted editable-field mouse handler
was investigated with Desktop.requestForeground, explicit component requestFocus,
and window.toFront. One initial attempt recovered focus, but repeated cycles did
not. Logs confirm the activation callback ran with an enabled, editable, showing
field in the correct window. All experimental production changes were removed.
The archived diagnostic launcher records event types/focus, not typed characters.

AX Raise, a title-bar click and opening an ordinary Save Complex Data dialog did
not establish recovery after hiding. An ordinary-app comparison using Calculator
was inconclusive: targeted native actions did not take SignalShow's Java focus,
so this is not evidence of successful cross-app focus recovery. Using Codex is
not established as the cause. No OS permissions, native bridge or JDK were changed.

## Verified normal-build workflow

All interactions used CUA, with no Robot, shell keystrokes or programmatic model
editing. The 19 successful entries were:

- Function name; Gaussian amplitude 3.25, center -4, width 24, exponent 2.
- Search for Rectangle; y-profile amplitude 2, center 3, full width 20.
- Threshold Maximum 4.5, Minimum -0.25, Threshold 0.75 then 1.25.
- Reopen the saved chain and change Maximum to 5.5; retained other parameters.
- Four export filename pastes and one rename after restarting/reloading.

The 13 parameter/name entries before restart used the same focus/readback guard
now saved in `scripts/cua_signalshow_input.js`. Search and filename fields were
checked separately because they lack the same accessible-name representation.
The helper is intended for the CUA REPL and is not an unattended desktop driver.

Native exports preserve the source and the two evaluated operation results:

| File | Independent expected samples |
| --- | --- |
| native-source.txt | 3.25 exp(-pi ((x+4)/24)^2) times a rectangle of amplitude 2, center y=3 and half-width 10; imaginary zero |
| native-chain.txt | Real of componentwise Threshold at 1.25, high 4.5 and low -0.25 |
| native-chain-edited.txt | Same threshold with high 5.5 after reopening/editing |
| native-reloaded.txt | Edited result loaded after restart, renamed and saved again |

The rectangle has half-amplitude at its two boundaries. Export storage uses
x=column-128 and y=127-row. The independent verifier checks all 524,288 scalar
samples across the four files. Threshold outputs match exactly; source comparison
uses ordinary double tolerance. These are sample counts, not separate scenarios.
The file chooser's hidden File field could not be clicked; the owned saved fixture
was copied into the isolated test home and selected normally from the visible list.
Reloading preserves samples, not the editable operation graph.

Run `python3 docs/audit/evidence/2026-10-15-native-input-verify.py` to replay the
archived exports. Result hashes are in the adjacent results JSON. The normal
packaged app was used for this entire workflow, without diagnostic instrumentation.

## Procedure for subsequent native tests

1. Use one owned isolated SignalShow instance. Inspect the current named field.
2. Click the field and require AX state to identify it as the focused UI element.
   If that check fails, send no keyboard text.
3. Select all with Command+A, type or paste, and verify the exact field readback.
   Only then press Return. Inspect the model/expression/result afterward; visible
   text alone does not establish that a parameter was committed correctly.
4. Save useful results at scenario checkpoints. If focus is lost, stop the attempt.
   For an owned disposable test session, quit, verify process exit, relaunch and
   reload saved samples or replay the scenario. This recovery was verified here.
   Do not close a user's unsaved work or mistake a sampled export for a saved graph.
5. Avoid repeated setValue, blind typing or focus loops. If a fresh restart still
   lacks focus, record native acceptance as blocked and continue headless work.
6. Close the test app when finished and verify exit without an app-bound AX call,
   which can relaunch it.

The preserved helper implements steps 1–3 for uniquely named fields. It deliberately
throws before typing without confirmed focus and before Return on readback failure.
Windows, screen-reader navigation and full post-release native acceptance remain
separate. All owned SignalShow/Calculator processes were closed and exit verified;
no native helper/build/test job remains running. Existing user servers are preserved.
Weekly usage: 97% remaining; check_usage true, no reset consumed.
