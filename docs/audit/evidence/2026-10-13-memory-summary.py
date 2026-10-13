from pathlib import Path
import csv,json,re,hashlib
out={}
for run,prefix in [('before','signalshow-memory'),('after','signalshow-memory-after')]:
 rows=list(csv.DictReader(open('/private/tmp/'+prefix+'-samples.csv')));result={'samples':len(rows),'elapsedSeconds':float(rows[-1]['elapsedSeconds']),'sampledOverallPeakRssMiB':max(int(r['rssKiB']) for r in rows)/1024,'stages':{},'heap':{}}
 for row in rows:
  stage=row['stage'];d=result['stages'].setdefault(stage,{'rssPeakMiB':0,'lastRssMiB':0,'firstElapsedSeconds':float(row['elapsedSeconds']),'lastElapsedSeconds':0});d['lastRssMiB']=int(row['rssKiB'])/1024;d['rssPeakMiB']=max(d['rssPeakMiB'],d['lastRssMiB']);d['lastElapsedSeconds']=float(row['elapsedSeconds'])
 for stage in ['empty','256','512','1024','live','deleted']:
  s=Path('/private/tmp/'+prefix+'-'+stage+'-heap.txt').read_text();result['heap'][stage]={'usedMiB':int(re.search(r'used (\d+)K',s)[1])/1024,'committedMiB':int(re.search(r'committed (\d+)K',s)[1])/1024}
 out[run]=result
start=int(Path('/private/tmp/signalshow-memory-native-offset.txt').read_text());mid=int(Path('/private/tmp/signalshow-memory-after-native-offset.txt').read_text());log=Path('/private/tmp/signalshow-roger-native.log').read_bytes();assert b'Exception' not in log[start:] and b'Error' not in log[start:];Path('/private/tmp/signalshow-memory-before-native.log').write_bytes(log[start:mid]);Path('/private/tmp/signalshow-memory-after-native.log').write_bytes(log[mid:])
p=Path('/private/tmp/signalshow-roger-home/memory-after-corner-constant.txt');lines=p.read_text().splitlines();assert lines[:2]==['2D 512 256','false'];v=list(map(float,' '.join(lines[4:]).split()));assert len(v)==262144 and set(v[:131072])=={-2.5} and set(v[131072:])=={0.0};out['cornerRecovery']={'size':[512,256],'real':-2.5,'imaginary':0,'sha256':hashlib.sha256(p.read_bytes()).hexdigest()}
Path('/private/tmp/signalshow-memory-comparison.json').write_text(json.dumps(out,indent=2)+'\n')
for run in ['before','after']:
 a=out[run];print(run,'RSS peak',round(a['sampledOverallPeakRssMiB'],1),'heap', {k:round(v['usedMiB'],1) for k,v in a['heap'].items()})
 for size in ['256','512','1024']:
  data=[v for k,v in a['stages'].items() if k.startswith(size) and not any(x in k for x in ['export','collection'])];print(size,'non-export observed peak RSS',round(max(v['rssPeakMiB'] for v in data),1))
print('Corner-origin rectangular constant and failed-save recovery verified.')
