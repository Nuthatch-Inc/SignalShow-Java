"""Replay native windowed sine image -> componentwise sign -> offset -> magnitude."""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W=H=256;r,c=np.mgrid[:H,:W];X=c-W//2;Y=H//2-1-r
checks=[]
def check(name,expected):
 p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists());raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
 s=raw.decode().splitlines();assert s[0]=='2D 256 256' and s[1]=='true'
 v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
 a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
 err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err)
 checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,maxComplexError=err))
def rect(d,width):return np.where(abs(d)<width/2,1,np.where(abs(d)==width/2,.5,0))
for suffix,phaseX,wy,wb,ix,ib,phaseY in [('initial',20,-12,48,15,80,10),('edited',50,10,36,-18,60,40)]:
 if suffix=='edited' and len(sys.argv)>2 and sys.argv[2]=='initial':break
 window=np.where(abs(Y-wy)<=wb,.8*(.54+.46*np.cos(np.pi*(Y-wy)/wb)),0)
 a=1.2*np.sin(2*np.pi*X/30+np.radians(phaseX))*window+1j*.5*rect(X-ix,ib)*np.sin(2*np.pi*Y/28+np.radians(phaseY))
 sign=np.sign(a.real)+1j*np.sign(a.imag);offset=sign+1+.5j
 for stage,v in [('source',a),('sign',sign),('offset',offset),('magnitude',abs(offset))]:check('windowed-'+stage+'-'+suffix,v)
 if suffix=='edited':
  check('windowed-offset-changed',sign+.25-.75j);check('windowed-magnitude-changed',abs(sign+.25-.75j))
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in checks)),indent=2))
