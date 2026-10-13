# Read-only HPROF ownership diagnostic; format per OpenJDK heapDumper.cpp.
from pathlib import Path
from collections import deque,Counter
import mmap,struct,json,sys
p=Path(sys.argv[1]);f=p.open('rb');m=mmap.mmap(f.fileno(),0,access=mmap.ACCESS_READ);z=m.find(b'\0');ids=struct.unpack_from('>I',m,z+1)[0];assert ids==8
sizes={2:ids,4:1,5:2,6:4,7:8,8:1,9:2,10:4,11:8};strings={};names={};classes={};objects={};roots={};static={}
def u2(p):return struct.unpack_from('>H',m,p)[0]
def u4(p):return struct.unpack_from('>I',m,p)[0]
def ident(p):return struct.unpack_from('>Q',m,p)[0]
def heap(pos,end):
 while pos<end:
  tag=m[pos];pos+=1
  if tag in (0xff,1,2,3,4,5,6,7,8):
   oid=ident(pos);roots[oid]=hex(tag);pos+=ids
   pos+={0xff:0,1:ids,2:8,3:8,4:4,5:0,6:4,7:0,8:8}[tag]
  elif tag==0x20:
   oid=ident(pos);pos+=ids+4;superid=ident(pos);loader=ident(pos+ids);pos+=6*ids+4
   n=u2(pos);pos+=2
   for _ in range(n):
    typ=m[pos+2];pos+=3+sizes[typ]
   refs=[(superid,'superclass'),(loader,'classloader')];n=u2(pos);pos+=2
   for _ in range(n):
    sid=ident(pos);typ=m[pos+ids];pos+=ids+1
    if typ==2:refs.append((ident(pos),sid))
    pos+=sizes[typ]
   fields=[];n=u2(pos);pos+=2
   for _ in range(n):
    fields.append((ident(pos),m[pos+ids]));pos+=ids+1
   classes[oid]=(superid,fields);static[oid]=refs
  elif tag==0x21:
   oid=ident(pos);cid=ident(pos+ids+4);n=u4(pos+2*ids+4);pos+=2*ids+8;objects[oid]=('instance',cid,pos,n);pos+=n
  elif tag==0x22:
   oid=ident(pos);n=u4(pos+ids+4);cid=ident(pos+ids+8);pos+=2*ids+8;objects[oid]=('array',cid,pos,n);pos+=n*ids
  elif tag==0x23:
   oid=ident(pos);n=u4(pos+ids+4);typ=m[pos+ids+8];pos+=ids+9;objects[oid]=('primitive',typ,pos,n);pos+=n*sizes[typ]
  else:raise ValueError((hex(tag),pos,end))
 assert pos==end
pos=z+1+4+8
while pos<len(m):
 tag=m[pos];n=u4(pos+5);start=pos+9;end=start+n
 if tag==1:strings[ident(start)]=m[start+ids:end].decode('utf-8',errors='replace')
 elif tag==2:names[ident(start+4)]=ident(start+8+ids)
 elif tag in (0x0c,0x1c):heap(start,end)
 pos=end
cn={cid:strings[sid] for cid,sid in names.items()};schemas={}
def schema(cid):
 if cid not in schemas:
  sup,fs=classes[cid];schemas[cid]=[(cn.get(cid,str(cid)),strings[sid],typ) for sid,typ in fs]+(schema(sup) if sup else [])
 return schemas[cid]
def typename(oid):
 if oid in cn:return 'class '+cn[oid]
 obj=objects.get(oid)
 if not obj:return 'unknown'
 if obj[0]=='primitive':return {7:'double[]',8:'byte[]',10:'int[]'}.get(obj[1],str(obj[1])+'[]')+' length '+str(obj[3])
 return cn.get(obj[1],hex(obj[1]))
def edges(oid,strong):
 if oid in static:
  for target,label in static[oid]:
   if target:yield target,strings.get(label,label)
  return
 obj=objects.get(oid)
 if not obj:return
 kind,cid,pos,n=obj
 if kind=='array':
  for i in range(n):
   target=ident(pos+i*ids)
   if target:yield target,'['+str(i)+']'
 elif kind=='instance':
  end=pos+n
  for decl,label,typ in schema(cid):
   if typ==2:
    target=ident(pos)
    if target and not(strong and decl=='java/lang/ref/Reference' and label=='referent'):yield target,label
   pos+=sizes[typ]
  assert pos==end,(typename(oid),pos-end)
def traverse(strong):
 parent={r:None for r in roots};q=deque(roots)
 while q:
  oid=q.popleft()
  for target,field in edges(oid,strong):
   if target not in parent:parent[target]=(oid,field);q.append(target)
 return parent
strong=traverse(True);allrefs=None
results=[]
for oid,obj in objects.items():
 if not (obj[0]=='primitive' and obj[1]==7 and obj[3]>=65536):continue
 reachable=oid in strong
 if not reachable and allrefs is None:allrefs=traverse(False)
 parent=strong if reachable else allrefs
 chain=[];node=oid
 while node in parent and parent[node] is not None:
  owner,field=parent[node];chain.append({'owner':typename(owner),'id':hex(owner),'field':field,'target':typename(node)});node=owner
 chain.reverse();results.append({'object':typename(oid),'id':hex(oid),'stronglyReachable':reachable,'root':roots.get(node),'path':chain})
report={'file':p.name,'objects':len(objects),'classes':len(classes),'roots':len(roots),'stronglyReachableObjects':len(strong),'arrays':results}
Path('/private/tmp/signalshow-editor-array-root-paths.json').write_text(json.dumps(report,indent=2)+'\n')
print(json.dumps({'objects':len(objects),'largeDoubleArrays':len(results),'strongLargeDoubleArrays':sum(r['stronglyReachable'] for r in results)},indent=2))
for r in results:
 print(r['object'],r['id'],'strong',r['stronglyReachable'],'root',r['root'])
 print(' -> '.join(x['owner']+'.'+x['field'] for x in r['path']))

