# Frequency demo settings — October 13, 2026

BUG-036: changing documents discarded frequency-demo settings. Native Filtering
reset Gaussian Band Pass (.7 amplitude, center 4, separation 12, slit width 8)
to Ideal Low Pass and changed the result from ±.7 to ±1. Frequency Locations
reset cutoffs 4/12 to 8/24. Each model created a fresh view without saved controls.

The models now own compact control settings. Fresh views rebuild calculations
from current source data and restore those settings. Live control changes update
the model so Duplicate works even before leaving the view. Duplicates copy their
settings independently while retaining the existing linked source behavior.
Settings contain no views or pixel arrays. The existing Savable lifecycle saves
before replacing a view. This is in-session document behavior, not a new file format.

Six EDT workflow cases failed before the fix: four lost the Gaussian selection,
two lost their frequency cutoff. Afterward all six pass. They cover 1D/2D filters,
both origins, actual controls/model view factories, duplication before departure,
source edits, independent duplicate settings and whole-array analytic expected
values. Focused frequency suite: 20 pass. Full offline package: 559 pass, zero
failures/errors/skips. Existing production filtering math is unchanged.

Native CUA on macOS arm64/JDK 25, fresh packaged app:

- Created an uncentered 256×256 cosine image, period 16. Gaussian Band Pass
  retained .7/4/12/8 after visiting the source; result bounds remained ±.7.
- Duplicate retained the settings. Editing its amplitude to .9 left the original
  at .7. Editing the source amplitude from 1 to 2 recomputed original/duplicate
  bounds to ±1.4/±1.8 while preserving each demo's controls.
- Added a constant imaginary component .5. Frequency Locations retained cutoffs
  4/12, DC/high enabled, low/mid disabled, absolute disabled and Imaginary selected
  after visiting the source. Editing that source component to .25 recomputed
  the blue DC image and scale from 0–.5 to 0–.25 with selections retained.

Nine AX records and two inspected screenshots establish native controls, visible
patterns and scales. Native reconstructed sample arrays were not exported; strict
whole-array verification uses the actual Swing computation path. The bounded
native log contains no exception. Closed the only test app and verified process
exit. Restart had restored typing; its earlier failure's cause remains unknown.

Native 1D revisit and Windows are unverified. Other demonstration types require
their own settings checks. This resolves the scoped filtering/frequency-location
reopen defect; it is not whole-app settings or memory-lifetime certification.
