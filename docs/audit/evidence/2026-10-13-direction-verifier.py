"""Independent replay of native directional calculus and sampled-data reuse.
Derivatives are adjacent pixel differences; integrals are cumulative sums.
The vertical coordinate increases from bottom to top.
"""
from pathlib import Path
import gzip, hashlib, json, sys
import numpy as np
base = Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=64,128
row,col=np.mgrid[:H,:W]; x=col-W//2; y=H//2-1-row
source=2*np.maximum(1-abs((x-5)/20),0)*np.exp(-np.pi*((y+9)/24)**2)
checks=[]
def check(name,expected):
    p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists())
    raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
    lines=raw.decode().splitlines(); assert lines[0]=='2D 64 128' and lines[1]=='true'
    values=np.array([float(v) for v in '\n'.join(lines[4:]).split()]); assert len(values)==2*W*H
    actual=(values[:W*H]+1j*values[W*H:]).reshape(H,W)
    error=float(np.max(abs(actual-expected))); assert np.isfinite(actual).all() and error<1e-9,(name,error)
    checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=lines[2],complexSamples=actual.size,maxComplexError=error))
def dx(a): return np.concatenate([np.zeros((H,1)),np.diff(a,axis=1)],axis=1)
def dy(a): return np.concatenate([a[:-1]-a[1:],np.zeros((1,W))],axis=0)
def ix(a): return np.cumsum(a,axis=1)
def iy(a): return np.cumsum(a[::-1],axis=0)[::-1]
for prefix,gain in [('direction-',1),('direction-after-',1.5)]:
    a=source*gain
    check(prefix+('source-initial' if gain==1 else 'source'),a)
    for name,expected in [('derivative-x',dx(a)),('integral-x',ix(dx(a))),('derivative-y',dy(a)),('integral-y',iy(dy(a))),('export-initial',iy(dy(a)))]: check(prefix+name,expected)
frozen=2*iy(dy(source*1.5))
for name,expected in [('export-gain',frozen),('export-negate',-frozen),('source-edited',source*.5),('derivative-edited',dy(source*.5)),('integral-edited',iy(dy(source*.5))),('export-unchanged',frozen),('negate-unchanged',-frozen)]:check('direction-after-'+name,expected)
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in checks)),indent=2))
