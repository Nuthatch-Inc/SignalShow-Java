# Retired editor ownership — October 13, 2026

BUG-031, bounded continuation of the [earlier memory investigation](2026-10-13-memory.md).
Baseline 347dad7, macOS arm64, Oracle JDK 25, native CUA interaction. One app per
run, closed between builds and after testing; process exit verified. Local only.

## Ordinary native workflow

Create a centered Constant image, amplitude 3, enlarge 256→512 square, and Plot.
Create a centered 512-square Cylinder, radius 64, center (128,192). Create a
multiplication system, explicitly select the two images, Plot and save complex
samples. Return to Edit System; select the Constant, Edit Real Part, type 2.5,
read back before Return, reopen the system and Plot; save again. Return to Edit
System and delete the system, Cylinder and Constant. Confirm an empty library.

Both runs followed those controls and values. The four compressed exports are
byte-identical before/after at corresponding stages. The standalone verifier
checks every one of 262,144 complex samples per export against the independent
circle inequality and multiplication by 3 or 2.5; maximum error zero. Thus view
cleanup preserves the live source, linked operation, source edit and reopened
result. No crash or exception in either native launch's bounded log.

## Ownership correction

Native accessibility kept old dimension-combo listeners and operation-button
actions alive after their views were removed. Those objects still owned the
original function/system, calculator input/thumbnail cells and picker documents.
Toolbars now release their document/view ownership when replaced or removed.
The outgoing operation editor releases calculator cells, the retained renderer,
its own system and input picker. Cleanup does not change the underlying model.
The GUI saves the outgoing editor before retiring its content; installing the
same content twice is now a no-op, needed by existing operation toolbar callers.

Six Swing EDT cases failed before and pass after, covering 1D/2D function and
operation toolbars plus real operation editors, their input picker and rendered
thumbnail. They preserve complex products and reconstruct a working editor.
Only the desktop-owned library and repaint/save callback are replaced in the
headless fixture; the native run covers the actual GUI transition/save path.
Focused existing insertion/reopening/thumbnail tests also pass. Full offline
package: **545 tests, zero failures/errors/skips**.

## Collected heap observations

At each checkpoint GC.class_histogram requests collection, followed by heap_info.
The deleted-stage HPROF snapshot independently requests collection and supplies
root paths. Numbers are total reported heap, not a retained-size attribution or
an RSS/peak benchmark.

| Stage | Before | After |
| --- | ---: | ---: |
| Live edited operation, MiB | 227.9 | 169.5 |
| All three documents deleted, MiB | 142.6 | 79.5 |
| Remaining 2D function/system objects | 3 | 0 |
| Of these, strongly reachable objects | 3 | 0 |

The smaller matched workload differs from the previous five-document/1024 test;
these values must not be compared directly to that test's 484.8 MiB. Binary dumps
remain local at /private/tmp/signalshow-editor-memory-{before,after}.hprof.
Histograms, compact roots, exports, screenshots and logs are adjacent evidence.

## Remaining work

Zero document models does **not** mean every pixel allocation was released.
The after snapshot contains 21 double arrays of at least 65,536 elements, 19
strongly reachable: some through obsolete function-term editors/thumbnails and
others through JAI's tile cache. Those paths are recorded separately; soft caches
are excluded from the strong traversal. The next correction targets those
obsolete previews, with editing/reopening and native measurements repeated.
BUG-031 and the memory-lifecycle row remain open. Native root lifetimes outside
CUA, manual use, Windows, other JVMs and larger/longer workloads are unverified.

The later [preview cleanup verification](2026-10-13-preview-memory-resumed.md)
closed the observed BUG-031 paths in this matched workload: the final deleted
snapshot has zero strongly reachable large pixel arrays and 20.1 MiB collected
heap. The remaining-work description above records this earlier checkpoint.
