"""Independent signed-image and cyclic sample-reversal native replay.
Reverse retains index0 and negates the selected sample index modulo its length;
this is the established operation convention, distinct from a literal image flip.
"""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=128,64;r,c=np.mgrid[:H,:W];x=c-W//2;y=H//2-1-r
checks=[]
def check(name,expected):
 p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists());raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
 s=raw.decode().splitlines();assert s[0]=='2D 128 64' and s[1]=='true'
 v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
 a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
 err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err)
 checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,maxComplexError=err))
def rect(a,center,fullwidth):
 dist=abs(a-center);return np.where(dist<fullwidth/2,1,np.where(dist==fullwidth/2,.5,0))
def reverse(a,axis):return np.take(a,(-np.arange(a.shape[axis]))%a.shape[axis],axis=axis)
for suffix,cx,phase,cy in [('initial',5,30,8),('edited',-4,60,-6)]:
 real=2*np.sin(2*np.pi*(x-cx)/24+np.radians(phase))*rect(y,-7,40)
 imag=-.75*rect(x,-10,16)*np.exp(-np.pi*((y-cy)/22)**2)
 src=real+1j*imag;absolute=abs(real)+1j*abs(imag);revx=reverse(absolute,1);revxy=reverse(revx,0)
 for name,a in [('source',src),('absolute',absolute),('reverse-x',revx),('reverse-xy',revxy)]:check('signed-'+name+'-'+suffix,a)
check('signed-reverse-y-switched',reverse(absolute,0))
check('signed-reverse-yy-switched',absolute)
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in checks),convention='Cyclic sample-index reversal with first row/column retained; not a geometric flip or independent approval of coordinate-reflection semantics.'),indent=2))
