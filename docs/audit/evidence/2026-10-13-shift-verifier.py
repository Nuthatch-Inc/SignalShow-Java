"""Verify native Fourier-source, translation, complex scaling and addition exports.
Usage: python3 signalshow-shift-verifier.py [export_directory]
"""
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
    checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],
        complexSamples=a.size,maxComplexError=err,realRange=[float(a.real.min()),float(a.real.max())],
        imaginaryRange=[float(a.imag.min()),float(a.imag.max())]))
def source(period_y): return 1.5*np.exp(2j*np.pi*((x-3)/32+(y+2)/period_y))
def shift(a,dx,dy,wrap):
    sx=col-dx;sy=row+dy
    if wrap: return a[sy%H,sx%W]
    inside=(sx>=0)&(sx<W)&(sy>=0)&(sy<H)
    out=np.zeros_like(a);out[inside]=a[sy[inside],sx[inside]]
    return out

a=source(16); shifted=shift(a,13,-7,True);scaled=(.5+.25j)*shifted
check('shift-source',a);check('shift-fill',shift(a,13,-7,False));check('shift-wrap',shifted)
check('shift-scaled',scaled);check('shift-sum',scaled+a)
for period_y,suffix in [(16,'parameters-edited'),(32,'source-edited')]:
    a=source(period_y);shifted=shift(a,-9,5,False);scaled=(-.25+.75j)*shifted
    check('shift-fill-edited' if period_y==16 else 'shift-fill-source-edited',shifted)
    check('shift-scaled-'+suffix,scaled);check('shift-sum-'+suffix,scaled+a)
check('shift-source-edited',source(32))
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),
    totalComplexSamples=sum(x['complexSamples'] for x in checks),maxComplexError=max(x['maxComplexError'] for x in checks)),indent=2))
