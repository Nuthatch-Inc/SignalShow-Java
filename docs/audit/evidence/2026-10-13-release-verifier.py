from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-1.2.5-home')
x=np.arange(256)-128;g=np.tile(2*np.exp(-np.pi*(x/16)**2),(256,1));checks=[]
for n,e in [('gaussian',g),('intensity',g*g)]:
 p=next(p for p in [base/(n+'.txt'),base/('2026-10-13-release-'+n+'.txt.gz')] if p.exists())
 raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
 s=raw.decode().splitlines();assert s[0]=='2D 256 256' and s[1]=='true'
 v=np.array([float(x) for x in '\n'.join(s[4:]).split()]);assert len(v)==131072
 a=(v[:65536]+1j*v[65536:]).reshape(256,256);err=float(np.max(abs(a-e)))
 assert np.isfinite(a).all() and err<1e-9,(n,err)
 checks.append(dict(file=n+'.txt',sha256=hashlib.sha256(raw).hexdigest(),dimensions=[256,256],complexSamples=a.size,maxComplexError=err))
wrong=Path('/private/tmp/signalshow-1.2.txt')
if len(sys.argv)==1:assert hashlib.sha256(wrong.read_bytes()).hexdigest()=='d0cedbc92e09058d8c22ace7a985372dc3dd2438828b2f8fe5e08fe7a450a97f','Unintended external path was changed'
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,passed=2,totalComplexSamples=131072,maxComplexError=max(c['maxComplexError'] for c in checks)),indent=2))
