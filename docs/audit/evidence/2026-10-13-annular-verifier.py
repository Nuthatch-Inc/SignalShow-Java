"""Replay native annular aperture -> complex mask -> Fourier intensity."""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W=H=128;x=np.arange(W)-W//2;y=H//2-1-np.arange(H);X,Y=np.meshgrid(x,y)
ex=np.exp(-2j*np.pi*np.outer(x,x)/W);ey=np.exp(-2j*np.pi*np.outer(y,y)/H)
checks=[]
def check(name,expected):
 p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists());raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
 s=raw.decode().splitlines();assert s[0]=='2D 128 128' and s[1]=='true'
 v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
 a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
 err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err)
 checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,maxComplexError=err))
for suffix,cx,cy,outer,inner,amp,period,phase in [('initial',4,-6,24,8,1,40,25),('edited',-5,4,28,10,.8,30,60)]:
 if suffix=='edited' and len(sys.argv)>2 and sys.argv[2]=='initial':break
 r=np.hypot(X-cx,Y-cy);aperture=.75*((r>=inner)&(r<=outer))
 wave=amp*np.exp(1j*(2*np.pi*np.hypot(X,Y)/period+np.radians(phase)))
 masked=aperture*wave
 # Direct separable DFT definition using physical image coordinates, sqrt(N) normalization.
 fourier=ey@masked@ex.T/np.sqrt(W*H)
 intensity=abs(fourier)**2
 assert abs(np.sum(intensity)-np.sum(abs(masked)**2))<1e-9
 for stage,a in [('aperture',aperture),('wave',wave),('masked',masked),('fourier',fourier),('intensity',intensity)]:check('annular-'+stage+'-'+suffix,a)
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in checks)),indent=2))
