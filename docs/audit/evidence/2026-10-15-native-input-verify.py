from pathlib import Path
import math, hashlib, json, zipfile
archive=zipfile.ZipFile(Path(__file__).with_name('2026-10-15-native-input-exports.zip'))
size=256
source=[]
for row in range(size):
    y=size//2-1-row
    rect=2.0 if abs(y-3)<10 else 1.0 if abs(y-3)==10 else 0.0
    for col in range(size):
        x=col-size//2
        source.append(3.25*math.exp(-math.pi*((x+4)/24)**2)*rect)
cases={'native-source': source, 'native-chain':[4.5 if x>=1.25 else -.25 for x in source], 'native-chain-edited':[5.5 if x>=1.25 else -.25 for x in source], 'native-reloaded':[5.5 if x>=1.25 else -.25 for x in source]}
results=[]
for name, expected in cases.items():
    raw=archive.read(name+'.txt')
    lines=raw.decode().splitlines()
    assert lines[:2]==['2D 256 256','true'], lines[:2]
    values=[float(v) for line in lines[3:] for v in line.split()]
    assert len(values)==2*size*size
    maximum=0.0
    for i, actual in enumerate(values):
        wanted=expected[i] if i<size*size else 0.0
        assert math.isfinite(actual) and math.isclose(actual,wanted,rel_tol=1e-12,abs_tol=1e-10),(name,i,actual,wanted)
        maximum=max(maximum,abs(actual-wanted))
    results.append({'fixture':name,'status':'PASS','scalar_samples':len(values),'max_absolute_error':maximum,'sha256':hashlib.sha256(raw).hexdigest()})
report={'scalar_samples_checked':sum(x['scalar_samples'] for x in results),'results':results}
print(json.dumps(report,indent=2))
