# SignalShow audit checkpoint — October 14, 2026

Juliet published the previous audit branch and authorized continuation. Prioritize
ordinary creation/editing/chains, crashes and major wrong results. Avoid tiny-error
and rare-input work. Use independent expectations; Chirplet comparison is allowed.
Juliet subsequently authorized publication of the fixes as the latest GitHub release
and requested all future development directly on main. Version 1.2.6 is now
published as latest; verification is recorded in 2026-10-14-release.md.

## Active release preparation — October 15

Juliet requested a new SignalShow release and a link when ready. Prepare **1.2.7**
on main from the verified BUG-060–070 fixes, tests and evidence. No branch or
worktree is requested. Publication is authorized; no website action is needed if
the existing GitHub access remains available.

The prior clean package passes **1,256 tests** with all 566 production classes
checked. Nineteen focused native keyboard/paste entries and four independently
verified exports pass; see `2026-10-15-native-input.md`. No experimental focus
handlers or diagnostic launchers remain in production. Direct native setValue
and recovery after hiding remain unsupported/unresolved. Broader native checks,
Windows interactive acceptance and capacity checks remain separate.

Next: clean-package version 1.2.7, commit/push main and its tag, verify both CI
installers and all four downloaded assets, then publish as latest. Record the
finished evidence in `2026-10-15-release.md`. No native app is open; preserve user
servers including 8092. Build downloads belong in temporary storage, not the repo.

## Released state (unchanged)

- Repo `/Users/julietfiss/src/SignalShow-Java`, branch `main`.
- Release source/tag `3c315c840512b0612e14ac31299b5119b4f097a7`, version **1.2.6**.
  Release evidence is committed separately afterward; application code is unchanged.
- Public latest: https://github.com/Nuthatch-Inc/SignalShow-Java/releases/tag/v1.2.6
- Full clean headless package: **1,130 tests pass**, zero failures/errors/skips;
  both GitHub platform builds pass. Source fixes and native Mac evidence are included.
- Finished Mac DMG and Windows MSI payloads exclude old builds and tests. All four
  public asset links, sizes and hashes pass verification. No website action needed.
- Downloaded Mac/portable JAR SHA-256:
  `354aa6a2c8ed4f9107f1de0be379af80184a71c501b8d8a97976cc46e4338088`.
  All 565 production classes match the local tested build. See `2026-10-14-release-assets.json`
  and the release document for local and CI evidence.
- Future development goes directly on main. No new branch/worktree is requested.
- No other workspace code changed. No app, test, build, agent or automation is running.

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
