"""Verify native creation settings, independent 1D origin, and the mixed image sum."""
from pathlib import Path
import hashlib
import json
import zipfile

archive = Path(__file__).with_name("2026-10-14-creation-defaults-native-exports.zip")
expected = {
    "edited-source": ("2D 128 64", "false", 8192, 2),
    "next-polar": ("2D 128 64", "false", 8192, 3),
    "mixed-rectangular-sum": ("2D 128 64", "false", 8192, 5),
    "next-cartesian": ("2D 128 64", "false", 8192, 0),
    "next-one-d": ("1D 256", "true", 256, 0),
}
results = []
with zipfile.ZipFile(archive) as z:
    assert set(z.namelist()) == {name + ".txt" for name in expected}
    for name, (shape, centered, count, real) in expected.items():
        raw = z.read(name + ".txt")
        lines = raw.decode().splitlines()
        assert lines[:2] == [shape, centered], (name, lines[:2])
        values = [float(v) for line in lines[3:] for v in line.split()]
        assert values == [float(real)] * count + [0.] * count, name
        results.append({"fixture": name, "status": "PASS", "shape": shape,
                        "zero_centered": centered == "true", "samples_per_channel": count,
                        "real_value": real, "imaginary_value": 0,
                        "sha256": hashlib.sha256(raw).hexdigest()})
print(json.dumps({"results": results}, indent=2))
