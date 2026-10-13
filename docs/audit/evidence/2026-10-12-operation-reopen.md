# Reopened operation input picker — October 12, 2026

BUG-018 is resolved. Loading an expression suppressed its selection events until
after construction, then initialized only the preview. The final input appeared
selected but its picker remained blank. The editor now presents the existing
selection through the normal event path once initialization is complete.

Two actual editor/calculator/CardLayout checks, one each for 1D and 2D, failed
before and pass after. They substitute only the desktop library and its repaint
callback so the component can open without a JFrame. They verify immediate picker
visibility and selection matching the final input. Related insertion/layout
checks pass (13 focused checks total); full package passes **441 tests**.

Native check: create a complex chirp and a radial complex exponential, create
multiplication, visit the chirp and reopen Edit System. Both eligible inputs are
visible immediately, with the final input selected. Clicking it again keeps the
picker available. Choosing the chirp changes the expression to `radial complex
exponential x complex chirp`; Plot and save succeed with 256×256 complex samples.
The output was saved with the displayed default filename and copied to
`/private/tmp/signalshow-roger-home/reopened picker replacement.txt` for evidence.
Native log is clean; test instance closed and process exit verified.

Before screenshot is linked from the preceding linked-source batch; after
screenshot and focused/package logs accompany this record. macOS arm64/JDK 25;
Windows remains untested. This checks opening, selection and replacement, not
all expression-editing transitions.
