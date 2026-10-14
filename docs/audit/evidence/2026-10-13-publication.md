# GitHub1.2.5 publication — October 13, 2026

Juliet explicitly authorized merging into main and publishing1.2.5 as the latest
GitHub release. Main was fast-forwarded from28a4ffc to8f6dad1; its tag triggered
run38502634539. The finished installer checks prevented draft creation/publication:

- Windows:610 tests, zero assertion failures, three cleanup errors because TIFF
  outputs remained locked. JAI FileStore owns its output stream until its rendered
  operation is disposed. TIFF and BMP callers now dispose after successful encoding.
  Added repeated export→rename→pixel-readback→delete checks for both formats.
  Local focused16 and full612 tests pass. Windows CI retest passed all612 cases, including the formerly locked TIFF files.
- Mac: build succeeded; the payload allowlist rejected `.package`. OpenJDK25u's
  `jdk.jpackage.internal.PackageFile` writes this small package-name marker into
  the app directory. Permit that specific regular file alongside the existing
  `.jpackage.xml`, launcher configuration and single current JAR. All other files
  and directories remain rejected. The JAR must match the one just built and must
  exclude JUnit, compiled project tests and nested JARs.

Failed workflow:https://github.com/Nuthatch-Inc/SignalShow-Java/actions/runs/38502634539
JDK source:https://github.com/openjdk/jdk25u/blob/master/src/jdk.jpackage/share/classes/jdk/jpackage/internal/PackageFile.java

## Published result

The failed tag was replaced before publication, using its observed object as a
force-with-lease guard. Corrected main/tag source is
684d5219d76ccb563d9261c1e451c9f9f232ac95. Run38503319138 succeeded in all three jobs:
Mac installer, Windows installer, draft release. Both platforms pass612 automated
tests. Windows native interactive acceptance remains deferred.

The finished DMG was mounted read-only and the finished MSI administratively
extracted in CI. Both app directories contain exactly `.package`, `SignalShow.cfg`
and `signalshow-1.2.5.jar`; the ordinary native launcher/runtime/icon files remain
outside that app-data directory. Each installed JAR matches its own job's shaded
build, with no embedded project tests, JUnit, nested JARs, old builds or reports.
The Mac build is arm64; Windows is x64. Both use Temurin25.0.4+101. The separate
portable JAR is the Mac job's shaded JAR and requires Java25+.

Downloaded all four draft assets and verified exact sizes, GitHub asset SHA256
digests and the uploaded SHA256SUMS. Published release412294402 on October13,2026
(Pacific), with draft=false, prerelease=false and latest=true. The unauthenticated
latest URL resolves to1.2.5, and all three executable URLs return HTTP200. No
GitHub website action was required from Juliet.

Release:https://github.com/Nuthatch-Inc/SignalShow-Java/releases/tag/v1.2.5
Latest:https://github.com/Nuthatch-Inc/SignalShow-Java/releases/latest
Successful workflow:https://github.com/Nuthatch-Inc/SignalShow-Java/actions/runs/38503319138

Public assets are only the Windows MSI, Mac DMG, portable JAR and SHA256SUMS.txt.
Asset IDs, sizes and hashes are in2026-10-13-publication-assets.json. Historical
1.2.4 and other releases/drafts were preserved. The1.2.5 source tag remains at the
code build; subsequent documentation records publication without retagging.

Local dist/1.2.5 now mirrors those published binaries and includes an updated local
portable ZIP, instructions and LOCAL-SHA256SUMS. This supersedes the earlier local
610-test candidate, while its dated evidence remains historical. No Java app is
open, no release build remains running, and the audit preview server is preserved.
