"""Check native noise use and downstream arithmetic, not RNG distribution fidelity."""
from pathlib import Path
import gzip,hashlib,json,sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=128,64;checks=[]
def read(name):
 p=next(p for p in [base/(name+'.txt'),base/('2026-10-13-'+name+'.txt.gz')] if p.exists());raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
 s=raw.decode().splitlines();assert s[0]=='2D 128 64' and s[1]=='true'
 v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert len(v)==2*W*H
 a=(v[:W*H]+1j*v[W*H:]).reshape(H,W);assert np.isfinite(a).all()
 checks.append(dict(file=name+'.txt',sha256=hashlib.sha256(raw).hexdigest(),descriptor=s[2],complexSamples=a.size,realMean=float(a.real.mean()),imaginaryMean=float(a.imag.mean())))
 return a
def compare(a,b,label):
 err=float(np.max(abs(a-b)));assert err<1e-9,(label,err);relations.append(dict(check=label,maxComplexError=err))
relations=[];data={}
for state in ['initial','edited','seed-changed']:
 src=read('noise-source-'+state);center=read('noise-centered-'+state);power=read('noise-intensity-'+state)
 compare(center,src-src.mean(),'Subtract Mean '+state)
 compare(power,abs(src-src.mean())**2,'Squared Magnitude '+state)
 data[state]=src
s0,s1,s2=[data[k] for k in ['initial','edited','seed-changed']]
assert abs(s0.real.mean()-2)<.1 and abs(s0.real.std()-.75)<.1
assert s0.imag.min()>=-1 and s0.imag.max()<=.2
compare(s1.real,1+(s0.real-2)*1.25/.75,'Gaussian mean/std edit retains seed realization')
compare(s1.imag,.3+(s0.imag+.4)*.9/.6,'Uniform mean/halfwidth edit retains seed realization')
compare(read('noise-source-after-views'),s1,'Plot/Histogram/editor revisit preserves source')
compare(s2.real,s1.real,'Changing imaginary seed preserves real channel')
assert np.mean(abs(s2.imag-s1.imag)>.001)>.95,'Seed must change the imaginary noise realization'
compare(read('noise-source-seed-restored'),s1,'Restoring seed restores exact source')
compare(read('noise-intensity-seed-restored'),abs(s1-s1.mean())**2,'Restored downstream intensity')
print(json.dumps(dict(environment='NumPy '+np.__version__,checks=checks,relations=relations,passed=len(checks),totalComplexSamples=sum(c['complexSamples'] for c in checks),maxComplexError=max(c['maxComplexError'] for c in relations),limits='Generator checks cover finite samples, broad Gaussian mean/std and uniform range, affine parameter edits, repeatable seeds and native callers; no RNG distribution certification.'),indent=2))
