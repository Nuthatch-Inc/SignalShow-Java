from pathlib import Path
from array import array
import hashlib,json,math,sys
h=Path('/private/tmp/signalshow-roger-home');reports=[];prefix=sys.argv[1] if len(sys.argv)>1 else 'memory'
def read(size,kind):
 p=h/f'{prefix}-{size}-{kind}.txt'
 with p.open() as f:
  assert f.readline().strip()==f'2D {size} {size}'
  assert f.readline().strip()=='true'
  name=f.readline().strip();assert f.readline().strip()==''
  v=array('d',map(float,f.read().split()))
 assert len(v)==2*size*size
 return v,name,hashlib.sha256(p.read_bytes()).hexdigest()
for size,amplitude in [(256,3),(512,2),(1024,1.5)]:
 source,sname,shash=read(size,'source');threshold,tname,thash=read(size,'threshold');product,pname,phash=read(size,'product');n=size*size;on=0
 for i in range(n):
  x=source[i];assert math.isfinite(x) and 0<=x<=1,(size,i,x)
  expected=int(x>=.5);on+=expected
  assert threshold[i]==expected and product[i]==amplitude*expected,(size,i,threshold[i],product[i])
  assert source[n+i]==threshold[n+i]==product[n+i]==0,(size,'imaginary',i)
 reports.append({'size':size,'complexSamplesPerFile':n,'constantAmplitude':amplitude,'selectedPixels':on,'noiseMin':min(source[:n]),'noiseMax':max(source[:n]),'noiseMean':sum(source[:n])/n,'files':{k:{'descriptor':desc,'sha256':sha} for k,desc,sha in [('source',sname,shash),('threshold',tname,thash),('product',pname,phash)]}})
Path('/private/tmp/signalshow-'+prefix+'-values.json').write_text(json.dumps(reports,indent=2)+'\n');print('Nine exports verified: every sample of threshold and product matches its source at 256, 512 and 1024 square.')
