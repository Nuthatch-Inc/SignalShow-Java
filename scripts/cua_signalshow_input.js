/**
 * Load this function into the documented CUA REPL, then pass its SignalShow App
 * binding. This is not a Node/Robot test runner. All UI actions use CUA.
 * See docs/audit/evidence/2026-10-15-native-input.md for scope and recovery.
 */
async function replaceSignalShowField(app, label, value, method = "type") {
  if (typeof value !== "string" || !["type", "paste"].includes(method)) {
    throw new Error("Use a string value and method type or paste");
  }
  let state = await app.getAXState({emit: false, disableDiffing: true});
  const fields = state.split("\n")
    .map(line => line.match(/^\s*(\d+) text field .*Description:\s*(.*?)\s*, Value:\s?(.*)$/))
    .filter(match => match && match[2] === label);
  if (fields.length !== 1) throw new Error("Expected one named field: " + label);
  await app.click(Number(fields[0][1]));
  state = await app.getAXState({emit: false});
  const focused = state.match(/The focused UI element is .*text field .*Description:\s*(.*?)\s*, Value:/);
  if (!focused || focused[1] !== label) {
    throw new Error("Field lacks keyboard focus; no keys sent: " + label);
  }
  await app.pressKey("super+a");
  if (method === "paste") await app.paste(value);
  else await app.typeText(value);
  state = await app.getAXState({emit: false});
  const readback = state.match(/The focused UI element is .*text field .*Description:\s*(.*?)\s*, Value:\s?(.*)/);
  if (!readback || readback[1] !== label || readback[2] !== value) {
    throw new Error("Text readback mismatch; no Return sent: " + label);
  }
  await app.pressKey("Return");
  return await app.getAXState({emit: false, disableDiffing: true});
}
