"""Verify all native exported pixels and compare the previous completed run."""
from pathlib import Path
import gzip, hashlib, json
root = Path(__file__).parent
reports = []
for stage, amplitude in [('product', 3.0), ('edited-product', 2.5)]:
    path = root / f'2026-10-13-preview-memory-resumed-{stage}.txt.gz'
    raw = gzip.decompress(path.read_bytes())
    lines = raw.decode().splitlines()
    assert lines[0] == '2D 512 512' and lines[1] == 'true'
    values = list(map(float, ' '.join(lines[4:]).split()))
    count = 512*512
    assert len(values) == 2*count
    error = 0.0
    for row in range(512):
        for col in range(512):
            x, y = col-256, 255-row
            expected = amplitude if (x-128)**2 + (y-192)**2 <= 64**2 else 0.0
            error = max(error, abs(values[row*512+col]-expected), abs(values[count+row*512+col]))
    assert error == 0
    prior = gzip.decompress((root / f'2026-10-13-preview-memory-{stage}.txt.gz').read_bytes())
    assert raw == prior
    reports.append(dict(file=path.name, complex_samples=count, max_error=error, sha256=hashlib.sha256(raw).hexdigest(), byte_identical_to_previous=True))
print(json.dumps(reports, indent=2))
