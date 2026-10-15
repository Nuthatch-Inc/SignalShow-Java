---
applyTo: "**"
---

# Java release workflow

Use this procedure when Juliet requests a SignalShow release. An explicit request
to publish authorizes the necessary commit, push, tag and GitHub publication.
Develop directly on main unless Juliet requests otherwise. Build-only requests do
not authorize publication. Complete the authorized release and provide its link;
do not hand off routine Actions monitoring or draft publication to Juliet when
available tools can perform them.

## Prepare and verify

1. Inspect main, the working tree, origin/main and the latest published version.
   Preserve unrelated work. Bump the project version in `pom.xml` (patch for fixes).
2. Write `docs/releases/<version>.md` with changes, downloads, verification scope
   and material remaining limitations. Keep native and automated evidence distinct.
3. Run a clean full package, normally
   `mvn -o -B -ntp -Djava.awt.headless=true clean package`. Inspect the result and
   executable contents; retain a compact evidence record. Validate the audit
   matrix with `python3 scripts/signalshow_audit.py --check` and the diff.
4. Commit the intended release on main, push origin/main, then create and push an
   annotated `v<version>` tag at that tested commit. Never rewrite a published tag.

## Build and inspect the release

The tag triggers `.github/workflows/build-installers.yml`. It builds macOS arm64
and Windows x64 installers with Java 25, runs the suite on both platforms, and
checks each finished installer's extracted payload. Use the root installer
scripts, which stage only the current executable JAR into `target/installer-input`.

Track the matching tag/commit run through completion. Use completion-aware waits
and concise updates. Inspect failed logs and fix the actual cause if needed;
passing local tests do not substitute for both platform jobs. A failed unpublished
tag/draft may be replaced only after confirming that it was never published.

The final job creates a **draft** with exactly four assets:

- `SignalShow-<version>.dmg`
- `SignalShow-<version>.msi`
- `signalshow-<version>.jar`
- `SHA256SUMS.txt`

Download the assets into temporary storage. Verify their hashes against GitHub's
asset digests and the checksum file. Mount the Mac DMG read-only, inspect its
payload with `scripts/verify_installer_payload.py`, confirm version, launcher
architecture and signature, and compare packaged production classes with the
local tested build. The trimmed jpackage runtime may omit `bin/java` and OS_ARCH
metadata; inspect its release file and native JVM libraries directly. Detach the
image afterward. CI verifies the extracted Windows
payload; Windows interactive usage remains separate evidence.

No old builds, project tests, JUnit, test reports, audit documents, nested JARs or
diagnostic launchers belong in the application payload. Do not add downloaded
installers or temporary application bundles to the source repository.

## Publish and hand off

Once all checks pass, publish the draft as the latest non-prerelease. Verify the
public latest-release URL and anonymous asset downloads. Save the CI run, source
commit, asset hashes and bounded verification evidence in `docs/audit/evidence/`,
update the active checkpoint, and commit/push the evidence on main.

Provide a clickable release-page link and Roger's Windows installer link. Explain
any remaining blocker plainly; ask for website action only if access actually
prevents completion. Close owned test applications and verify exit if any were
launched. Keep user servers and applications intact.
