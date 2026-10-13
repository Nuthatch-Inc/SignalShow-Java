"""Read native complex exports; compare analytic sources, spatial sums and direct DFT.
Usage: python3 signalshow-spatial-verifier.py [export_directory]
Also reads .txt.gz artifacts saved beside this script.
"""
from pathlib import Path
import gzip, hashlib, json, sys
import numpy as np
base=Path(sys.argv[1]) if len(sys.argv)>1 else Path('/private/tmp/signalshow-roger-home')
W,H=128,64
x=np.arange(W)-W//2
y=H//2-1-np.arange(H)
X,Y=np.meshgrid(x,y)
report=[]
def read(name):
    paths=[base/(name+'.txt'),base/(name+'.txt.gz'),base/('2026-10-13-'+name+'.txt.gz')]
    p=next(p for p in paths if p.exists())
    raw=gzip.decompress(p.read_bytes()) if p.suffix=='.gz' else p.read_bytes()
    s=raw.decode().splitlines()
    assert s[0]=='2D 128 64' and s[1]=='true', (name,s[:2])
    v=np.array([float(a) for a in '\n'.join(s[4:]).split()]);assert v.size==2*W*H
    return (v[:W*H]+1j*v[W*H:]).reshape(H,W),s[2],hashlib.sha256(raw).hexdigest()
def check(name,expected):
    a,descriptor,digest=read(name)
    err=float(np.max(np.abs(a-expected)))
    bound=1e-9+1e-10*float(np.max(np.abs(expected)))
    assert np.isfinite(a).all() and err<bound, (name,err,bound)
    peak=np.unravel_index(np.argmax(a.real),a.shape)
    report.append(dict(file=name+'.txt',descriptor=descriptor,complexSamples=a.size,sha256=digest,maxComplexError=err,
        realRange=[float(a.real.min()),float(a.real.max())],imaginaryRange=[float(a.imag.min()),float(a.imag.max())],
        peakRealCoordinate=[int(x[peak[1]]),int(y[peak[0]])]))
    return a

def gaussian(amplitude):
    return amplitude*np.exp(-np.pi*((X+6)/12)**2)*np.exp(-np.pi*((Y-3)/8)**2)+0.25j
# The ordinary Rectangle editor displays full width; its boundary has half weight.
def rect(d,half):
    return np.where(abs(d)<half,1,np.where(abs(d)==half,.5,0))
b=rect(X-10,4)*rect(Y+5,2)

def spatial_sum(a,correlation):
    result=np.zeros((H,W),dtype=complex)
    # Sum only nonzero reference samples. Shifts follow x rightward, y upward.
    for r,c in np.argwhere(b!=0):
        shift=(int(y[r]),int(-x[c])) if correlation else (int(-y[r]),int(x[c]))
        result+=np.roll(a,shift,axis=(0,1))*b[r,c]
    return result

def direct_dft(a):
    # Separable definition of the DFT; no FFT implementation is used as an oracle.
    ex=np.exp(-2j*np.pi*np.outer(x,x)/W)
    ey=np.exp(-2j*np.pi*np.outer(y,y)/H)
    return ey@a@ex.T/np.sqrt(W*H)

check('spatial-rectangle',b)
for amplitude,suffix in [(1.5,''),(2,'-edited')]:
    a=gaussian(amplitude)
    check('spatial-gaussian'+suffix,a)
    convolved=spatial_sum(a,False)
    correlated=spatial_sum(a,True)
    check('spatial-convolve'+suffix,convolved)
    check('spatial-correlate'+suffix,correlated)
    check('spatial-forward'+suffix,direct_dft(correlated))
    check('spatial-inverse'+suffix,correlated)
check('spatial-inverse-convolve-edited',spatial_sum(gaussian(2),False))
print(json.dumps({'environment':'NumPy '+np.__version__,'checks':report,'passed':len(report),
    'totalComplexSamples':sum(r['complexSamples'] for r in report),'maxComplexError':max(r['maxComplexError'] for r in report)},indent=2))
