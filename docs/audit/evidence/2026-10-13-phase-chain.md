# Magnitude/phase creation and component extraction — October 13, 2026

Mac arm64/JDK25, unchanged592-test package from2473080; JAR SHA256
0d3519910dbf0ac841a1826fec5b4fdd402d3838b831c9191e396037a9328dc1.
CUA only, one instance, closed/exit verified at completion. No code changes or
unnecessary full-suite repetition. No push.

Create centered64×128 magnitude/phase image. Magnitude Constant1.5; phase Cartesian
Line(x)×Line(y), both amplitude1, x-center3,width12; y-center−5,width24.
Thus z=1.5 exp(i((x−3)/12)((y+5)/24)). Save source; make Phase→Threshold0 branch,
separate Real(source)→Threshold.25 branch, and Real(Threshold0(Phase(source))).
Save all five outputs. The source was explicitly selected for the Real branch;
the phase-threshold output was selected for the final Real branch.

Edit magnitude to.8, phase x-center−7,width10 and y-width20. Save source and all
five results after revisiting. All12 exports/98,304 complex samples pass against
independent complex exponential/argument, component extraction and threshold
expressions. Max error4.441e−16/tol1e−9. Phase extracts principal phase from complex
samples; it is distinct from the authored phase, which can extend beyond±π.

Threshold processes both channels: threshold0 maps imaginary0 to high1. The
final Real discards that component, as in Roger's Re(thresh(...)) workflow.
This existing convention is recorded rather than silently redefined. The.25
threshold on Real(source) leaves the imaginary channel zero.

Both component editors, typed parameter readback, branched live updates and
operation/result layout work. Native log clean. No crash or major wrong result
found. Native evidence covers this rectangular, nonzero-magnitude phase field;
other phase cases, exact branch cuts, near-zero magnitudes, Windows and full
handbook review remain separate. Tiny phase/low-amplitude behavior was not pursued.

Replay: `python3 docs/audit/evidence/2026-10-13-phase-chain-verifier.py docs/audit/evidence`.
