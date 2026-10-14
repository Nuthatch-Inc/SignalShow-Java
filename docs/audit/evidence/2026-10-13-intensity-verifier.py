"""Replay native Triangle/Gaussian -> Offset -> Clip -> Square Root exports.
Square Root is documented and checked componentwise, not as a complex root.
"""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=64,128
row,col=np.mgrid[:H,:W];x=col-W//2;y=H//2-1-row
checks=[]
def check(name,expected):
    p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists())
    raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
    s=raw.decode().splitlines();assert s[0]=='2D 64 128' and s[1]=='true'
    v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
    a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
    err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err)
    checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,maxComplexError=err,realRange=[float(a.real.min()),float(a.real.max())],imaginaryRange=[float(a.imag.min()),float(a.imag.max())]))
for suffix,amp,cx,offset,maximum in [('initial',2,5,-.25+.16j,.9),('edited',1.25,-6,-.1+.36j,.64)]:
    src=amp*np.maximum(1-abs((x-cx)/20),0)*np.exp(-np.pi*((y+9)/24)**2)
    shifted=src+offset
    clipped=np.clip(shifted.real,0,maximum)+1j*np.clip(shifted.imag,0,maximum)
    root=np.sqrt(clipped.real)+1j*np.sqrt(clipped.imag)
    for term,expected in [('source',src),('offset',shifted),('clip',clipped),('sqrt',root)]:check('intensity-'+term+'-'+suffix,expected)
check('intensity-clip-copy',np.clip(shifted.real,0,.36)+1j*np.clip(shifted.imag,0,.36))
check('intensity-clip-original-after-copy',clipped)
check('intensity-sqrt-after-copy',root)
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(x['complexSamples'] for x in checks),maxComplexError=max(x['maxComplexError'] for x in checks)),indent=2))
