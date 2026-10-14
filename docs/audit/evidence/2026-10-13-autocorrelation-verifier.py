"""Replay complex rectangles -> autocorrelation -> complex scale -> real."""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=64,128;x=np.arange(W)-W//2;y=H//2-1-np.arange(H);X,Y=np.meshgrid(x,y)
checks=[]
def check(name,expected):
 p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists());raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
 s=raw.decode().splitlines();assert s[0]=='2D 64 128' and s[1]=='true'
 v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
 a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
 err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err)
 checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,maxComplexError=err))
def rect(d,width):return np.where(abs(d)<width/2,1,np.where(abs(d)==width/2,.5,0))
for suffix,ry,rw,ix,iw,ia in [('initial',-3,12,-4,8,.5),('edited',-7,20,6,12,.75)]:
 if suffix=='edited' and len(sys.argv)>2 and sys.argv[2]=='initial':break
 a=rect(X-5,16)*rect(Y-ry,rw)+1j*ia*rect(X-ix,iw)*rect(Y-7,18)
 # R(lag) = sum_u f(u+lag) conjugate(f(u)), periodic coordinates.
 corr=np.zeros((H,W),complex)
 for r,c in np.argwhere(a!=0):corr+=np.roll(a,(int(y[r]),int(-x[c])),axis=(0,1))*np.conj(a[r,c])
 assert abs(corr[H//2-1,W//2]-np.sum(abs(a)**2))<1e-9
 for stage,v in [('source',a),('result',corr),('scaled',.01*corr),('real',(.01*corr).real)]:check('autocorrelation-'+stage+'-'+suffix,v)
 if suffix=='edited':
  check('autocorrelation-complex-gain',(.02+.01j)*corr)
  check('autocorrelation-real-complex-gain',((.02+.01j)*corr).real)
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in checks)),indent=2))
