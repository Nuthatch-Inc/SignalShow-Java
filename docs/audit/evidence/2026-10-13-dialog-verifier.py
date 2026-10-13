from pathlib import Path
import hashlib,json,csv
h=Path('/private/tmp/signalshow-roger-home');out={}
for name,value in [('dialog overwrite fixture.txt',3),('dialog recovery.txt',4),('dialog-after-recovery.txt',6),('dialog-after-second-save.txt',4),('dialog-after-csv.txt',4)]:
 p=h/name
 t=p.read_text().splitlines();w,hh=map(int,t[0].split()[1:]);n=w*hh;v=list(map(float,' '.join(t[4:]).split()));assert len(v)==2*n,(name,len(v));assert all(x==value for x in v[:n]);assert all(x==0 for x in v[n:]);out[name]={'size':[w,hh],'real':value,'imaginary':0,'sha256':hashlib.sha256(p.read_bytes()).hexdigest()}
cs=list(h.glob('dialog-csv-recovery*'));assert len(cs)==1
rows=list(csv.reader(cs[0].read_text().splitlines()[5:]));values=[float(x) for row in rows for x in row if x];assert len(rows)==256 and all(len(row)==256 for row in rows);assert len(values)==65536 and set(values)=={4.0},(len(values),set(values))
out[cs[0].name]={'samples':len(values),'real':4,'sha256':hashlib.sha256(cs[0].read_bytes()).hexdigest()}
assert not (h/'missing-dialog-folder').exists();assert not list(h.glob('dialog-export-cancelled*'))
out['cancelledTargetsAbsent']=True
Path('/private/tmp/signalshow-dialog-final-verification.json').write_text(json.dumps(out,indent=2)+'\n');print('Five complex files and one CSV verified; cancelled targets absent.')
