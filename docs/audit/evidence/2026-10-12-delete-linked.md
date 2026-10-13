# Delete source/result usage — October 12, 2026

Native macOS arm64/JDK 25, unchanged 449-test build. Created a complex chirp and
radial complex exponential at 256×256 and their multiplication. Saved the result;
removed the chirp through the main list Delete button; selected the operation,
plotted and saved it. Its existing result is identical in all 65,536 complex
samples. Existing operators retain their source object after list removal.

Edited the surviving exponential amplitude 1 → 1.1, revisited and saved the
operation. All complex samples equal 1.1 times the previous result within
4.45e-16. The original product independently matches the sum-of-phases formula
within 2.85e-14. Filename typing was read back before every save.

Deleted the operation: the remaining image editor opened immediately with its
1.1 amplitude intact. Deleted that final disposable image; the list and content
became empty. Created a fresh chirp and opened Plot successfully. No errors in
the native log; app closed and process exit verified.

No source change or additional automated test was needed. This checks ordinary
single-selection removal and remaining-document use, not exhaustive lifecycle,
undo, multi-selection deletion, retained-memory bounds or Windows behavior.
