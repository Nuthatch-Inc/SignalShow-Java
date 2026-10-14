"""Replay native dimension edits through existing Fourier and magnitude systems."""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
checks=[]
def check(name,expected,W,H):
 p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists());raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
 s=raw.decode().splitlines();assert s[0]==f'2D {W} {H}' and s[1]=='true'
 v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
 a=(v[:W*H]+1j*v[W*H:]).reshape(H,W)
 err=float(np.max(abs(a-expected)));assert np.isfinite(a).all() and err<1e-9,(name,err,s[2])
 checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],dimensions=[W,H],complexSamples=a.size,maxComplexError=err))
def rect(d,width):return np.where(abs(d)<width/2,1,np.where(abs(d)==width/2,.5,0))
for W,H in [(128,64),(256,128),(64,256)]:
 if W!=128 and len(sys.argv)>2 and sys.argv[2]=='initial':break
 x=np.arange(W)-W//2;y=H//2-1-np.arange(H);X,Y=np.meshgrid(x,y)
 a=1.3*np.exp(-np.pi*((X+8)/18)**2)*np.exp(-np.pi*((Y-5)/14)**2)+.4j*rect(X-10,16)*rect(Y+9,12)
 ex=np.exp(-2j*np.pi*np.outer(x,x)/W);ey=np.exp(-2j*np.pi*np.outer(y,y)/H)
 for stage,v in [('source',a),('forward',ey@a@ex.T/np.sqrt(W*H)),('inverse',a),('magnitude',abs(a))]:check(f'resize-{stage}-{W}x{H}',v,W,H)
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in checks)),indent=2))
