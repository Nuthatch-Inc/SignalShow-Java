"""Replay independent checks against data saved by the native Mac application."""
from pathlib import Path
import hashlib
import json
import math
import zipfile

archive_path = Path(__file__).with_name("2026-10-14-calculator-native-exports.zip")
size = 256
source = [complex(62, 2 * math.exp(-math.pi * ((col - 128) / 16) ** 2))
          for row in range(size) for col in range(size)]
threshold = [complex(3, 3 if z.imag >= .75 else -1) for z in source]
expected = {
    "grouped-source": source,
    "threshold-075": threshold,
    "real-threshold-075": [complex(3, 0)] * len(source),
    "real-threshold-edited": [complex(4, 0)] * len(source),
    "clear-rebuild-sum": [2 * z for z in source],
    "clear-rebuild-reopened": [2 * z for z in source],
    "grouped-operation": [z + z * z for z in source],
}
results = []
with zipfile.ZipFile(archive_path) as archive:
    assert set(archive.namelist()) == {name + ".txt" for name in expected}
    for name, oracle in expected.items():
        raw = archive.read(name + ".txt")
        lines = raw.decode().splitlines()
        assert lines[:2] == ["2D 256 256", "true"], (name, lines[:2])
        values = [float(v) for line in lines[3:] for v in line.split()]
        assert len(values) == 2 * size * size, name
        real, imaginary = values[:size * size], values[size * size:]
        errors = []
        for index, z in enumerate(oracle):
            for observed, wanted in [(real[index], z.real), (imaginary[index], z.imag)]:
                assert math.isfinite(observed), (name, index, observed)
                assert math.isclose(observed, wanted, rel_tol=1e-12, abs_tol=1e-10), (name, index, observed, wanted)
                errors.append(abs(observed - wanted))
        center = size * (size // 2) + size // 2
        results.append({"fixture": name, "status": "PASS", "samples_per_channel": size * size,
                        "max_absolute_error": max(errors), "center": [real[center], imaginary[center]],
                        "sha256": hashlib.sha256(raw).hexdigest()})
print(json.dumps({"scalar_samples_checked": len(results) * 2 * size * size, "results": results}, indent=2))
