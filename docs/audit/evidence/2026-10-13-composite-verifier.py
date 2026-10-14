"""Replay multi-term 2D creation, arithmetic/grouping edits and linked operations."""
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
 err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err,s[2])
 checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,maxComplexError=err))
def rect(d,width):return np.where(abs(d)<width/2,1,np.where(abs(d)==width/2,.5,0))
a=1.2*np.exp(-np.pi*((X+12)/18)**2)*np.exp(-np.pi*((Y-8)/14)**2)
b=.8*rect(X-14,20)*rect(Y+6,16);c=np.sin(2*np.pi*X/24+np.radians(30))
check('composite-sum',a+b);check('composite-precedence',a+b*c);check('composite-grouped',(a+b)*c)
editedA=.9*np.exp(-np.pi*((X+6)/22)**2)*np.exp(-np.pi*((Y-5)/14)**2)
editedB=.6*rect(X-14,20)*rect(Y+6,16);editedC=np.sin(2*np.pi*X/20+np.radians(60))
edited=(editedA-editedB)*editedC
check('composite-edited',edited)
for suffix,src in [('initial',(a+b)*c),('edited',edited)]:
 clipped=np.clip(src,0,.6)
 check('composite-clipped-'+suffix,clipped);check('composite-fourier-'+suffix,ey@clipped@ex.T/128)
check('composite-copy-without-carrier',editedA-editedB)
check('composite-original-after-copy-edit',edited)
check('composite-clipped-after-copy-edit',np.clip(edited,0,.6))
check('composite-fourier-after-copy-edit',ey@np.clip(edited,0,.6)@ex.T/128)
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in checks)),indent=2))
