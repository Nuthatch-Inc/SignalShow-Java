"""Replay native complex-chirp/aperture/conjugation/intensity checks."""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=128,64
row,col=np.mgrid[:H,:W];x=col-W//2;y=H//2-1-row
checks=[]
def check(name,expected):
    p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists())
    raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
    s=raw.decode().splitlines();assert s[0]=='2D 128 64' and s[1]=='true'
    v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
    a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
    err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err)
    checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,maxComplexError=err,realRange=[float(a.real.min()),float(a.real.max())],imaginaryRange=[float(a.imag.min()),float(a.imag.max())]))
for suffix,amplitude,alpha,phase,cx,radius in [('initial',1.5,18,30,7,22),('edited',2,24,75,-9,18)]:
    chirp=amplitude*np.exp(1j*(np.pi*((np.hypot(x,y)-2)/alpha)**2+np.deg2rad(phase)))
    mask=.8*((x-cx)**2+(y+4)**2<=radius**2)
    product=chirp*mask
    for term,expected in [('source',chirp),('aperture',mask),('product',product),('conjugate',np.conj(product)),('intensity',abs(product)**2)]:
        check('chirp-mask-'+term+'-'+suffix,expected)
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(x['complexSamples'] for x in checks),maxComplexError=max(x['maxComplexError'] for x in checks)),indent=2))
