# Completed native preview cleanup check — October 13, 2026

Native CUA on macOS arm64, JDK 25, unchanged final 549-test bundle from 8021b9f.
The run completed after Juliet requested restarting the input-stalled application.
Only one SignalShow instance ran. Its name, parameter and save-path fields accepted
automated typing with readback before Return. No new application exception appeared.

The matched workflow created a centered 512×512 Constant (amplitude 3) and Cylinder
(radius 64, center 128,192), multiplied them with explicit input choices, saved
complex data, returned to the Constant editor, changed amplitude to 2.5, reopened
the result and saved it again. Every real sample equals the indicated amplitude
inside the circle, zero outside; all imaginary samples are zero. Both exports
are byte-identical to the preceding independently checked native run. The adjacent
standalone verifier checks all 262,144 complex samples per export.

Then returned to Edit System and deleted the operation, Cylinder and Constant
through the native main document list. The adjacent AX snapshot shows an empty
library. At live and deleted stages, GC.class_histogram preceded GC.heap_info.
A collected HPROF after deletion was inspected with the existing read-only parsers.

| Matched 512-square workload | Before ownership fix | After ownership fix | After preview cleanup |
| --- | ---: | ---: | ---: |
| Collected live heap, MiB | 227.9 | 169.5 | 113.7 |
| Collected deleted heap, MiB | 142.6 | 79.5 | 20.1 |
| Remaining document models | 3 | 0 | 0 |
| Strong double arrays ≥65,536 elements | Not recorded | 19 | 0 |

The final snapshot retains two large zero arrays only through SoftReference
cache entries, not strong paths. No obsolete image preview buffer remains strongly
reachable in this tested workflow. This closes the observed BUG-031 ownership
and pixel-retention paths. Heap totals include JVM/runtime allocations and are
measurements of these runs, not exact object-retained-size attribution or capacity
limits. This does not certify other image families, imported-data ownership,
Windows/manual usage, larger images or long sessions.

The binary dump stays local at
/private/tmp/signalshow-preview-memory-resumed-deleted.hprof. Compact roots,
histograms, heap information, exports and the bounded native log are adjacent.
Closed PID 5092 through the native Close button and verified no audit process
remained. No product source changed in this follow-up, so the previously passing
549-test package was not rerun. Nothing pushed or released.
