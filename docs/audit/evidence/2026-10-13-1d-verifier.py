from pathlib import Path
import json,math,hashlib
h=Path('/private/tmp/signalshow-roger-home');report=[];N=256
def read(name):
 p=h/('usage1d-'+name+'.txt');lines=p.read_text().splitlines();assert lines[0]=='1D 256';v=list(map(float,' '.join(lines[4:]).split()));assert len(v)==2*N;return lines,[complex(*p) for p in zip(v[:N],v[N:])],p

def check(name,expected,centered=True,descriptor=None):
 lines,v,p=read(name);assert (lines[1]=='true')==centered,(name,'origin');
 if descriptor is not None:assert lines[2]==descriptor,(name,lines[2])
 error=max(abs(a-b) for a,b in zip(v,expected));assert error<1e-10,(name,error,lines[2]);report.append({'file':p.name,'samples':N,'zeroCentered':centered,'maxAbsoluteError':error,'descriptor':lines[2],'sha256':hashlib.sha256(p.read_bytes()).hexdigest()});return v

def sine(amp,phase,centered=True):return [complex(amp*math.sin(2*math.pi*((i-N//2 if centered else i)-8)/32+math.radians(phase)),.5) for i in range(N)]
rect=[complex(1.5 if abs(i-N//2-12)<16 else .75 if abs(i-N//2-12)==16 else 0,0) for i in range(N)]
check('rectangle',rect,descriptor='Rectangle source')
for stage,amp,phase in [('before',2,30),('after',3,-15)]:
 s=sine(amp,phase);check('sine-'+stage,s,descriptor='Sine source');prod=[a*b for a,b in zip(s,rect)];check('product-'+stage,prod);th=[complex(int(a.real>=.6),int(a.imag>=.6)) for a in prod];check('threshold-'+stage,th);check('real-'+stage,[complex(a.real,0) for a in th])
for origin,centered in [('centered',True),('corner',False)]:
 s=sine(3,-15,centered)
 if not centered:check('corner-source',s,centered,descriptor='Sine source')
 loaded=check(origin+'-reopened',s,centered,descriptor='Sine source')
 check(origin+'-reopened-magnitude',[complex(a.real*a.real+a.imag*a.imag,0) for a in s],centered)
Path('/private/tmp/signalshow-1d-verification.json').write_text(json.dumps(report,indent=2)+'\n');print(f'{len(report)} native exports verified, {N} complex samples each; max error {max(r["maxAbsoluteError"] for r in report):.3g}.')
