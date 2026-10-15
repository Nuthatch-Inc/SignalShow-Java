# SignalShow accessibility check — October 15, 2026

Juliet suggested missing accessibility support as an explanation for the native
input failures. This is a bounded investigation within the authorized Continuous
Audit on main. It does not certify the application's overall accessibility.

## Confirmed application defect: BUG-070

Function Name and dimension labels were adjacent to their controls but had no
`labelFor` association. Operation parameter names appeared only in panel borders.
Generator labels named the spinner, but its editable child had no accessible name.
These omissions made ordinary controls harder to identify for assistive tools.

Connect the existing labels to the existing controls and propagate parameter
names to the spinner's editable child. The shared helper preserves the established
label-to-spinner relationship. It is used by analytic/image editors and the
histogram bin field; operation fields reuse their existing parameter titles.
No visible text, control area, numerical algorithm or keyboard policy changed.

Ten original-code cases fail at the missing names after their actual accessible
edits and computations succeed. Four 2D real/imaginary and magnitude/phase cases
cover both origins and rectangular orientations: rename, edit both channels,
update an already evaluated Intensity chain and reopen. Six nested Threshold
cases cover 1D/2D with outer Real, Conjugate or Intensity, parameter re-edits,
publication, current downstream values, source preservation and reopening.

All **91 focused checks pass**. The first broad run caught 29 existing noise
fixtures expecting the standard label-to-spinner relationship; the final helper
preserves that relationship and also names the child. The final clean package
passes **1,252 tests**, zero failures/errors/skips (31.404 seconds). All **566**
production classes match the executable; no nested builds, project tests, JUnit
or audit documents are bundled. The build remains local, unpublished version
1.2.6. See the adjacent before/after/package logs and artifact manifest.

## Native Mac check and remaining limitation

The October 14 targeted `setValue` experiment returned computer-use error -10005,
“Cannot set a value for an element that is not settable.” The field stayed `f0`.
Quit and process inspection confirmed that instance exited.

After the October 15 build, replace only the JAR in the owned temporary
`/private/tmp/signalshow-fixed-smoke/SignalShow.app`, re-sign and verify it, then
launch through CUA. Actual Mac accessibility state now describes Function Name,
Width and Height. After creating a Cartesian Gaussian term, its editable fields
expose the existing labels `A₀`, `x₀`, `b` and `n`.

A native accessibility Increment action on the amplitude stepper succeeds:

- Before: amplitude `1`, expression `GAUS[x/16]1[y]`.
- After: amplitude `1.1`, expression `1.1 × GAUS[x/16]1[y]`.

Direct `setValue` on the named Function Name text field still returns -10005;
its value remains `f0`. No keyboard/paste retry was sent. Native menu/selection
and stepper actions work, but direct native text replacement remains unverified.
The headless Java `AccessibleEditableText.setTextContents` path works and reaches
the production listeners. This separates application editing from the unresolved
Mac bridge/tool/focus question. Using Codex is not established as the cause.

Quit through the app menu and verify no SignalShow process remains. Keep at most
one test instance and do not re-query an app-bound helper after quitting.

## Source references and limits

[Oracle's Swing accessibility guidance](https://docs.oracle.com/javase/tutorial/uiswing/misc/access.html)
describes accessible names and `setLabelFor` associations.
[JTextField's Java 25 accessibility API](https://docs.oracle.com/en/java/javase/25/docs/api/java.desktop/javax/swing/JTextField.AccessibleJTextField.html)
provides editable-text support inherited by the standard fields used here.
The [OpenJDK 25 Mac JavaTextAccessibility source](https://github.com/openjdk/jdk/blob/jdk-25-ga/src/java.desktop/macosx/native/libawt_lwawt/awt/JavaTextAccessibility.m)
contains an unimplemented value setter. This source observation is a lead, not
proof that the failing native call uses that method or that it explains typing.
No runtime/OS/tool modification was attempted.

Screen-reader navigation, keyboard-only completion, icon-only creation tiles,
search fields, graphical range/profile labels and Windows accessibility remain
outside this check. Broader native post-release acceptance remains pending.

`check_usage: true`; October 15 checkpoint: weekly 98% remaining. No reset used.
Changes remain uncommitted on main. Next ordinary audit batch: actual hologram
option controls, preview/publication, edits and downstream results.
