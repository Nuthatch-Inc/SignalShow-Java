"""Replay native rectangular Fourier basis editing and spectrum detection."""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=128,64;x=np.arange(W)-W//2;y=H//2-1-np.arange(H);X,Y=np.meshgrid(x,y)
checks=[]
def check(name,expected):
 p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists());raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
 s=raw.decode().splitlines();assert s[0]=='2D 128 64' and s[1]=='true'
 v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
 a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
 err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err)
 checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,maxComplexError=err))
for suffix,ax,ay,cx,cy,px,py,phx,phy in [('initial',1.5,.8,2,-3,32,16,30,-20),('edited',.75,1.1,-4,5,16,32,60,25)]:
 if suffix=='edited' and len(sys.argv)>2 and sys.argv[2]=='initial':break
 a=ax*ay*np.exp(1j*(2*np.pi*((X-cx)/px+(Y-cy)/py)+np.radians(phx+phy)))
 # An integer-bin complex wave has exactly one nonzero normalized DFT coefficient.
 f=np.zeros((H,W),complex);r=H//2-1-int(H/py);c=W//2+int(W/px)
 f[r,c]=ax*ay*np.exp(1j*(np.radians(phx+phy)-2*np.pi*(cx/px+cy/py)))
 mag=abs(f);thresh=(mag>=.5).astype(float)
 for stage,v in [('source',a),('fourier',f),('magnitude',mag),('threshold',thresh)]:check('basis-'+stage+'-'+suffix,v)
if not (len(sys.argv)>2 and sys.argv[2]=='initial'):check('basis-threshold-above-peak',np.zeros((H,W)))
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in checks)),indent=2))
