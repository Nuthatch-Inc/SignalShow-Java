# SignalShow audit checkpoint — October 15, 2026

## Current state

Juliet requested a new release and a link when ready. **1.2.7 is published as the
latest release**. Work remains directly on main; no branch/worktree was created.

- Repo: `/Users/julietfiss/src/SignalShow-Java`.
- Tested source/tag: `9cc68bf56a6a8c1d43cebb23164b7a8c14e7e72d`.
- Public release: https://github.com/Nuthatch-Inc/SignalShow-Java/releases/tag/v1.2.7
- Includes BUG-060–070 math, sampled-profile, import, display and field-name fixes.
- Local clean package: **1,256 tests**, zero failures/errors/skips. Both Mac/Windows
  CI builds and finished-installer payload checks pass. All four public assets,
  hashes and latest-release redirect are verified. No website action is needed.
- Mac/portable JAR SHA-256:
  `dbd3d91504fadb27e88303632d4197804db5ce4451624c4b896f41cc184dc79e`.
  All 566 production classes match the local tested build. See
  `2026-10-15-release.md` and `2026-10-15-release-assets.json`.
- Downloads remain under `/private/tmp/signalshow-release-1.2.7`; no old builds or
  tests are included in the installers. The mounted DMG was detached.

## Next audit and limits

Continue ordinary creation/editing/chains, crashes and major wrong results; use
independent expectations and compare with Chirplet where helpful. After a few
ordinary checks pass, broaden into meaningful math edge cases. Do not prioritize
tiny errors or rare inputs. The pending native post-release scenario is next.

Nineteen focused Mac keyboard/paste entries and four independently verified
exports pass; see `2026-10-15-native-input.md` and the CUA focus/readback helper.
Direct native setValue and recovery after hiding remain unsupported/unresolved.
Experimental focus handlers were removed; no diagnostic launcher is shipped.
Save checkpoints before restarting owned disposable sessions; complex-data files
preserve samples, not editable operation graphs. Broader native Mac, Windows
interactive acceptance and capacity checks remain separate.

No build, test, watcher, automation, agent or owned native app remains running.
Preserve user servers, including audit server 8092. Native work requires current
confirmed focus/readback. Latest usage reading was weekly 97% remaining on
October 15, check_usage true; no reset used. Final release evidence is committed
separately after the source tag, without changing the application.

## Completed new batches

- BUG-056: Clear replaced the edited system with an unlisted object. Retain the
  existing owner and publish the cleared expression; rebuilding, downstream
  invalidation/recovery and reopening now pass in both dimensions.
- BUG-057: closing a parenthesis evaluated operations outside its group.
  `2+(3*4)*5` gave 70 instead of 62. Both creation and operation parsers now stop at
  the matching opening delimiter. Nested groups and both complex channels checked.
- BUG-058: consecutive unary prefix operators evaluated before their operand,
  losing results or consuming an earlier operand. Prefixes now nest right-to-left;
  postfix compatibility is retained. Actual prepend-Real/Conjugate/Intensity over
  Threshold, independent complex arithmetic and Roger regressions pass.
- Additional passing usage coverage: actual Plus/Minus/Times/Divide selection,
  explicit input replacement, nested Threshold field typing (.75 then 1.25),
  cached linked results, original-source preservation and reopening.
- New headless fixture exercises real shared events, selectors, buttons, preview
  and saveStateToModel. Only desktop library lookup/repaint is supplied separately;
  publication is not stubbed out. No JFrame or native typing used.

## Completed native follow-up and current boundary

Juliet confirmed desktop availability. Native Mac verification of BUG-056–058
passed: grouped creation, Threshold → Real insertion, normal parameter editing,
Clear/rebuild/reopen and grouped complex arithmetic. Seven actual Save Complex
Data exports pass independent comparisons across both channels. See
`2026-10-14-calculator-native.md`, its replay verifier/archive and rendered PNG.

The next ordinary-use batch found and fixed BUG-059: selecting 128×64 saved
height into width; the default-height getter also read width; 2D origin edits
changed the 1D preference. Eight actual-control cases failed before correction.
All 200 focused checks pass, followed by the full 1,130-test package. Native
checks and five exports confirm both creation modes inherit 128×64/corner origin,
the mixed image sum equals 5+0i, and new 1D signals remain centered. Evidence and
replay verification: `2026-10-14-creation-defaults.md` and adjacent files.

No confirmed crash/major wrong-result issue from this targeted follow-up remains
unfixed. Broader composition/parameter combinations and Windows interaction are
separate later scopes. Duplicate default names (`f0`) were observed and traced to
four creation actions; labeling is lower priority than this round's computation
and crash focus. No change made for that lower-priority observation.

All owned native processes are closed; both launch sessions exited 0. Do not call
an app-bound AX helper after quit: it can reopen an empty app. No build/test,
automation, agent or background audit job is running. Preserve existing
http://127.0.0.1:8092/docs/audit/ and other user servers. Release work is now
complete; all publication checks passed. Existing user servers remain available.

`check_usage: true`; release-end reading: weekly 57% remaining, no other applicable
window. Follow [usage policy](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).

## Accepted contracts and prior evidence

The published baseline contains BUG-046 through BUG-055 and 997 passing tests;
see `2026-10-14-math.md` and `2026-10-14-math-continuation.md`. All 130 registered
entries retain scoped ordinary-computation evidence. Preserve Java componentwise
square root, Bessel even extension, aperture half-extents, magnitude-only cepstrum
(zero-bin log -100), and scalar Poisson occupancy. Phase Detour retains inverse-
transform/real-amplitude preparation; its random-phase/alternate-origin preparation
and Data1D nonuniform-coordinate behavior remain incompletely verified.

Java Rotate is clockwise about the image center; Chirplet is counterclockwise in
physical coordinates. Ordinary nearest/bilinear interiors and quarter turns pass;
arbitrary bicubic/boundaries are not certified. Bessel order 5 error 4.525e-5 against
peak .522 was deliberately deferred. No unused Extended Cosine Bell refactor.

Update `docs/audit/signalshow-coverage.json`, then generate/check via
`python3 scripts/signalshow_audit.py` and `--check`. Reuse current full-package
results for documentation-only changes. Native and Windows evidence stays separate.
