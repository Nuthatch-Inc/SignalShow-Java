"""Independent native polar-generation and complex Divide/Minus replay."""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=128,64;r,c=np.mgrid[:H,:W];x=c-W//2;y=H//2-1-r
radius=np.hypot(x,y);theta=np.degrees(np.arctan2(y,x))%360
# Polar angle is undefined at r=0. Preserve the app's explicit90-degree
# center-pixel convention; the other8191 pixels use independent atan2 geometry.
theta[(x==0)&(y==0)]=90
checks=[]
def check(name,expected,centered=True):
 p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists());raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
 s=raw.decode().splitlines();assert s[0]=='2D 128 64' and s[1]==str(centered).lower()
 v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
 a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
 err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err)
 checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,maxComplexError=err))
for suffix,center,width,period,phase,b in [('initial',12,14,120,30,2+.5j),('edited',16,18,90,60,1.25-.75j)]:
 src=1.5*np.exp(-np.pi*((radius-center)/width)**2)*.8*np.cos(2*np.pi*theta/period+np.radians(phase))
 for name,a in [('source',src),('base',np.full((H,W),b)),('divide',src/b),('minus',src/b-b)]:check('polar-'+name+'-'+suffix,a)
check('polar-minus-source-replacement',src/b-src)
for suffix,centered,phase in [('centered',True,60),('zero-start',False,60),('phase-edited',False,90)]:
 yy=H//2-1-r if centered else H-1-r;xx=c-W//2 if centered else c
 angle=np.degrees(np.arctan2(yy,xx));angle[(xx==0)&(yy==0)]=90
 src=1.2*np.exp(-np.pi*((np.hypot(xx,yy)-16)/18)**2)*np.cos(2*np.pi*angle/90+np.radians(phase))
 b=1.25-.75j
 for name,a in [('source',src),('divide',src/b),('minus',src/b-b)]:check('polar-after-'+name+'-'+suffix,a,centered)
 if suffix!='phase-edited':check('polar-after-base-'+suffix,np.full((H,W),b),centered)
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in checks)),indent=2))
