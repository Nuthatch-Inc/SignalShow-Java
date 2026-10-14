"""Replay native nonzero complex radial image -> Exponent -> Invert."""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=128,64;r,c=np.mgrid[:H,:W];radius=np.hypot(c-W//2,H//2-1-r)
checks=[]
def check(name,expected):
 p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists());raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
 s=raw.decode().splitlines();assert s[0]=='2D 128 64' and s[1]=='true'
 v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
 a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
 err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err)
 checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,maxComplexError=err))
s0=1.2*np.exp(1j*(2*np.pi*(radius-3)/45+np.radians(35)))
s1=.8*np.exp(1j*(2*np.pi*(radius-3)/36+np.radians(55)))
for name,expected in [('source-initial',s0),('square-initial',s0**2),('inverse-square-initial',1/s0**2),('root-initial',s0**.5),('inverse-root-initial',1/s0**.5),('source-edited',s1),('root-edited',s1**.5),('complex-edited',s1**(.5+.25j)),('inverse-complex-edited',1/s1**(.5+.25j))]:check('power-after-'+name,expected)
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in checks)),indent=2))
