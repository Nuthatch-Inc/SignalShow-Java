# Automated input after unlock — October 13, 2026

Juliet confirmed the Mac was unlocked. Reconnected through CUA and launched one
SignalShow audit instance using the unchanged 549-test bundle (commit 8021b9f).
Before launch, process inspection found no existing audit instance.

Pointer/menu input created a 2D real/imaginary function, selected the Image family
and then Constant. Automated name entry left `f0` unchanged. Explicit focus,
window Raise and a coordinate click did not resolve it. Amplitude entry left the
field at `1` when `3` was requested; supported paste also left it unchanged.
Refreshing the CUA connection and retrying one numeric edit produced the same
result. No Return was sent after any failed readback. The captured AX state is
saved compressed alongside this note.

Read-only macOS inspection reported an on-console session and no secure-input PID
in the session properties examined. This does not establish a cause. No new
SignalShow exception appeared in the launch log. Clicks working while text entry
fails is not sufficient evidence of an application defect or a macOS defect.

A manual comparison is pending: Juliet was asked whether typing `3` directly into
the Amplitude field works. One audit window remains open for that check, owned PID
98337; no test loop or sampler is running. Close this instance when it is no longer
being used. The final native preview deletion/heap comparison remains incomplete;
no new memory or saved-result claim is made by this attempted run. All prior
passing checks remain as recorded; the 549-test suite was not rerun for this
input-only check. No product code changed and nothing was pushed.
