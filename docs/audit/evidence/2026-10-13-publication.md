# GitHub1.2.5 publication — October 13, 2026

Juliet explicitly authorized merging into main and publishing1.2.5 as the latest
GitHub release. Main was fast-forwarded from28a4ffc to8f6dad1; its tag triggered
run38502634539. The finished installer checks prevented draft creation/publication:

- Windows:610 tests, zero assertion failures, three cleanup errors because TIFF
  outputs remained locked. JAI FileStore owns its output stream until its rendered
  operation is disposed. TIFF and BMP callers now dispose after successful encoding.
  Added repeated export→rename→pixel-readback→delete checks for both formats.
  Local focused16 and full612 tests pass. Windows CI retest remains pending.
- Mac: build succeeded; the payload allowlist rejected `.package`. OpenJDK25u's
  `jdk.jpackage.internal.PackageFile` writes this small package-name marker into
  the app directory. Permit that specific regular file alongside the existing
  `.jpackage.xml`, launcher configuration and single current JAR. All other files
  and directories remain rejected. The JAR must match the one just built and must
  exclude JUnit, compiled project tests and nested JARs.

Failed workflow:https://github.com/Nuthatch-Inc/SignalShow-Java/actions/runs/38502634539
JDK source:https://github.com/openjdk/jdk25u/blob/master/src/jdk.jpackage/share/classes/jdk/jpackage/internal/PackageFile.java

No1.2.5 release exists yet. Replace only the unpublished tag, using a lease against
its observed object2d690621ea9ca8cd84a07ea0ef87d3ae0c74a257, after committing the
corrections. Keep1.2.4 public until both native builds and package checks succeed.
