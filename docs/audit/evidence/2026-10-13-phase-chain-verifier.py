"""Independent native magnitude/phase creation and extraction/threshold replay."""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=64,128;row,col=np.mgrid[:H,:W];x=col-W//2;y=H//2-1-row
checks=[]
def check(name,expected):
 p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists());raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
 s=raw.decode().splitlines();assert s[0]=='2D 64 128' and s[1]=='true'
 v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
 a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
 err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err)
 checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,maxComplexError=err))
for suffix,mag,cx,bx,by in [('initial',1.5,3,12,24),('edited',.8,-7,10,20)]:
 phi=((x-cx)/bx)*((y+5)/by);src=mag*np.exp(1j*phi);phase=np.angle(src)
 # Threshold is componentwise: imaginary0 reaches high output at threshold0.
 mask=(phase>=0).astype(float)+1j*np.ones((H,W))
 values=[('source',src),('phase',phase),('phase-mask',mask),('real',src.real),('real-mask',(src.real>=.25).astype(float)),('real-phase-mask',mask.real)]
 for name,expected in values:check('phase-chain-'+name+'-'+suffix,expected)
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in checks)),indent=2))
