# SignalShow checkpoint — October 13, 2026

The Mac usage round and SignalShow 1.2.5 release are complete. Juliet explicitly
authorized merging into main and publishing the latest GitHub release, superseding
the earlier no-push instruction. No GitHub website action was required from her.

- Release: https://github.com/Nuthatch-Inc/SignalShow-Java/releases/tag/v1.2.5
- Latest: https://github.com/Nuthatch-Inc/SignalShow-Java/releases/latest
- Successful CI: https://github.com/Nuthatch-Inc/SignalShow-Java/actions/runs/38503319138
- Released code/tag: `684d5219d76ccb563d9261c1e451c9f9f232ac95`.

Repository: `/Users/julietfiss/src/SignalShow-Java`, branch `main`. All fixes were
merged and pushed. The final documentation commit records publication on main;
the published tag remains at the verified code build. Do not retag this release.

## Verification and remaining scope

All 612 automated tests pass locally and in the Mac arm64 and Windows x64 CI
builds. Both finished installers were extracted and inspected. Their app-data
payloads contain exactly `.package`, `SignalShow.cfg` and the current executable
JAR; necessary native launcher/runtime/icon files are outside that directory.
Packaged JARs match their respective builds. Old builds, project tests, JUnit,
nested JARs and reports are excluded.

Downloaded all four release assets and verified sizes, GitHub SHA256 digests and
SHA256SUMS. Published as Latest, with draft and prerelease disabled. Anonymous
requests confirmed Latest resolves to 1.2.5 and all executable downloads return
HTTP 200. Public assets are the Windows MSI, Apple Silicon Mac DMG, portable JAR
and SHA256SUMS.txt. The JAR requires Java 25+; native installers bundle Java.
The Mac package is ad-hoc signed, not notarized.

The matrix has 130 math entries and 78 scenarios: 74 checked, two pending Windows
interactive checks and two deferred memory checks. CI does not establish Windows
native usage acceptance. These counts describe bounded checks, not whole-app
certification. Further ordinary usage and mathematical testing precede Chirplet
parity features.

The final native resize batch passed: complex Gaussian/Rectangle images resized
128×64 → 256×128 → 64×256 through an existing FFT → inverse FFT → Magnitude chain;
12 exports and 229,376 samples verified, with a clean log.

Release verification also fixed two ordinary save/export defects:
- BUG-044: dots in parent folders redirected saves. Four regressions and the
  packaged Gaussian → Squared Magnitude save retest pass; two distinct outputs
  contain all 131,072 expected samples.
- BUG-045: TIFF/BMP exports retained file handles. Explicit disposal now permits
  repeated export, rename, readback and deletion. The three original Windows CI
  cleanup failures are resolved, and both new format regressions pass.

Evidence is in `2026-10-13-resize.md`, `2026-10-13-release.md` and
`2026-10-13-publication.md`, with associated replay scripts and logs. The publication
asset manifest supersedes the historical local 610-test candidate manifest.
`dist/1.2.5/` contains the final GitHub binaries, an updated local portable ZIP,
instructions, release notes and checksums; it remains ignored by Git.

## Resources and next action

No SignalShow app or release build is running. Native test apps were closed and
process inspection confirmed cleanup. Keep at most one app open during future
testing and close it when unused. Native input works with the Java window focused;
inspect the target field and read back values before submitting. Stop input when
Juliet uses the desktop. Bind fresh CUA helpers to each app instance; old helpers
can relaunch a closed test bundle.

The audit viewer at http://127.0.0.1:8092/docs/audit/ is preserved (prior session
19418). Test homes under `/private/tmp/signalshow-roger-home` and
`/private/tmp/signalshow-1.2.5-home` contain test data. Preserve unrelated browser
and server state. No agents, new chats, automations or resets were created.

`check_usage: true`; latest reading: October 13, weekly 70% remaining, no other
applicable window reported. Follow the Continuous Audit usage policy on resumption.
The next requested work is more ordinary usage and math coverage, with Windows
interactive checks scheduled for a later round; consult the matrix queue.
