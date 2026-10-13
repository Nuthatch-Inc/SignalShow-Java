"""Replay independent analytic and geometric checks on native complex-data exports.
Usage: python3 signalshow-rotation-verifier.py [export_directory]
Supports plain native files and the dated gzip evidence artifacts.
"""
from pathlib import Path
import gzip, hashlib, json, sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=128,64
row,col=np.mgrid[:H,:W]
x=col-W//2; y=H//2-1-row
report=[]
def check(name,expected):
    choices=[base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')]
    path=next(p for p in choices if p.exists())
    raw=gzip.decompress(path.read_bytes()) if path.suffix=='.gz' else path.read_bytes()
    lines=raw.decode().splitlines()
    assert lines[0]=='2D 128 64' and lines[1]=='true', (name,lines[:2])
    v=np.array([float(z) for z in '\n'.join(lines[4:]).split()])
    assert v.size==2*W*H
    a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
    err=float(np.max(abs(a-expected)))
    assert np.isfinite(a).all() and err<1e-9,(name,err)
    report.append(dict(file=name+'.txt',descriptor=lines[2],sha256=hashlib.sha256(raw).hexdigest(),
        complexSamples=a.size,maxComplexError=err,realRange=[float(a.real.min()),float(a.real.max())],
        imaginaryRange=[float(a.imag.min()),float(a.imag.max())]))

def source(amplitude):
    return amplitude*np.exp(-np.pi*((x-10)/12)**2)*np.exp(-np.pi*((y+5)/8)**2)+.25j

def rotate(a,angle):
    # Inverse mapping around the center of pixel centers. Positive angles use
    # image coordinates (rows increase downward); uncovered canvas remains zero.
    c,s=np.cos(np.deg2rad(angle)),np.sin(np.deg2rad(angle))
    u,v=col+.5-W/2,row+.5-H/2
    src_col=np.floor(W/2+c*u+s*v).astype(int)
    src_row=np.floor(H/2-s*u+c*v).astype(int)
    inside=(src_col>=0)&(src_col<W)&(src_row>=0)&(src_row<H)
    out=np.zeros((H,W),complex)
    out[inside]=a[src_row[inside],src_col[inside]]
    return out

check('rotation-source-before',source(2).real)
for amplitude,suffix in [(2,''),(1,'-edited')]:
    a=source(amplitude); magnitude=abs(a); mask=(magnitude>=.75).astype(complex)
    check('rotation-source'+suffix,a)
    check('rotation-magnitude'+suffix,magnitude)
    check('rotation-threshold'+suffix,mask)
    check('rotation-chain-90'+suffix,rotate(mask,90))
    check('rotation-chain-30-nearest'+suffix,rotate(mask,30))
    if not suffix:
        check('rotation-direct-90',rotate(a,90))
        check('rotation-chain-180',rotate(mask,180))
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=report,passed=len(report),
    totalComplexSamples=sum(r['complexSamples'] for r in report),
    maxComplexError=max(r['maxComplexError'] for r in report)),indent=2))
