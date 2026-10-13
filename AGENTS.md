# SignalShow repository instructions

Read [the Git workflow policy](.github/instructions/github_instructions.md).
Do not commit, push or distribute builds without Juliet's request.

## Testing priorities

Juliet's standing direction, recorded October 12, 2026:

- Stabilize ordinary usage first: create images with normal parameter values,
  combine different functions in operation chains, edit inputs and operations,
  switch views, duplicate work and save/reopen results. Failures encountered
  during the first few minutes of ordinary use are the highest priority.
- Build toward hundreds of distinct usage scenarios covering functions and
  operations working together. Roger's circular-resolution target is one example
  of this class, not the application's single acceptance test. Build the suite
  incrementally; a request to record its plan does not mean implement it all now.
- Rare edge cases, technically allowed but unlikely inputs such as zero-sized
  images, tiny numerical discrepancies, and capacity extremes are lower priority.
  Do not pursue them while normal creation and operation workflows are broken.
- Track mathematical checks separately. Keep repeatable production-path and
  Swing-component evidence distinct from use of the running desktop application;
  neither test counts nor isolated arithmetic checks certify an entire workflow.
- Reproduce and fix a concrete normal workflow, verify its result and interactions,
  record evidence, and then choose the next scenario according to the active scope.

The current plan and scope are in [JAVA_TEST_PLAN.md](JAVA_TEST_PLAN.md); coverage
and evidence are maintained in [the audit matrix](docs/audit/signalshow-coverage.json).

## Test application cleanup

Keep at most one SignalShow instance open during testing. Reuse the active test
instance, quit obsolete builds before launching a replacement, and close test
instances when they are no longer being used. Verify that they exited; a Quit
action alone is not evidence of cleanup. Preserve any needed results first.

## Native input routing

Juliet observed automated keystrokes reaching her terminal on October13, then
explicitly authorized keystrokes again after finishing with the terminal. Prefer
targeted accessibility actions where practical. Before typing, inspect the target
field; read back the value before Return or submitting a dialog. If input does not
reach the field, stop that attempt rather than sending more keys blindly.

For native Swing dropdowns, the AX value may be a provisional choice. Commit
with Return after inspecting it, then confirm the rendered control and resulting
samples before treating the change as applied. An AX row click alone is not
proof that the application accepted the selection.
