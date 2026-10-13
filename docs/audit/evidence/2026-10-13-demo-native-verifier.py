from pathlib import Path
import json,hashlib
home=Path(__file__).resolve().parent
def load(name):
 p=home/('2026-10-13-'+name+'.txt'); lines=p.read_text().splitlines(); size=list(map(int,lines[0].split()[1:])); n=1
 for d in size:n*=d
 data=list(map(float,' '.join(lines[4:]).split())); assert len(data)==2*n
 return [complex(data[i],data[i+n]) for i in range(n)], {'name':name,'shape':size,'centered':lines[1],'sha256':hashlib.sha256(p.read_bytes()).hexdigest()}
a,am=load('demo-sine'); b,bm=load('demo-reference'); actual,cm=load('demo-correlation-result')
assert am['centered']==bm['centered']==cm['centered']=='true'
n=len(a); assert len(b)==len(actual)==n
expected=[sum(a[j]*b[(j-i+n//2)%n].conjugate() for j in range(n)) for i in range(n)]
error=max(abs(x-y) for x,y in zip(actual,expected)); assert error<1e-9,error
fixtures=[]
for name in ['demo-input','demo-after-input']:
 data,meta=load(name);assert meta['shape']==[32,16] and all(x==2 for x in data)
 fixtures.append(meta)
result={'native_correlation': {'files':[am,bm,cm],'samples':n,'max_complex_error':error,'last_actual':[actual[-1].real,actual[-1].imag],'last_expected':[expected[-1].real,expected[-1].imag], 'real_range':[min(z.real for z in actual),max(z.real for z in actual)]},'constant_fixtures':fixtures}
print(json.dumps(result,indent=2))
