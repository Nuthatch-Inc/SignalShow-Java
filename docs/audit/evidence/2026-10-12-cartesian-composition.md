# Cartesian composition and threshold — October 12, 2026

Native 455-test build, macOS arm64/JDK25. Created an editable real/imaginary
256×256 image. First Cartesian term: Rectangle in x (displayed full width16)
and Gaussian in y (width16). Added another Cartesian Gaussian in x (width16),
with its y factor constant. Incremented only the second Gaussian width to17.
Returning to the first term showed Rectangle width16 and y-Gaussian width16.
Plot → Edit retained the expression. The plus icon stayed a plus throughout.

Saved the source and applied Threshold with ordinary default cutoff0.5,
minimum0, maximum1. All 65,536 source samples match
rect(x; half-width8) * exp(-pi*y²/16²) + exp(-pi*x²/17²) within2.23e-16.
Every threshold sample agrees independently, and both imaginary channels are
zero. Saved using default names and copied to distinct audit fixtures; typing
is not claimed. The earlier isolated f0.txt fixture was backed up first.

This is additional native confirmation of the corrected term ownership and
operator rendering, with both x and y edited factors. No new source change.
No native exceptions since the resize correction; app closed/exited.

Observed lower-priority equation issue: the rectangle's text says RECT[x/8]
while the width field and samples describe full width16. Rectangle stores a
half-width but formats it as the denominator of the standard RECT notation.
Recorded for later equation review; ordinary authoring and sample verification
continue first under Juliet's stated priorities. Broader grouping/removal
scenarios remain separate pending work.
