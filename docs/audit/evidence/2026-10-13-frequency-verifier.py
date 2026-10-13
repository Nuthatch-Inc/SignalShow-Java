from pathlib import Path
import json,hashlib,math,re
root=Path(__file__).resolve().parent; home=root
def fixture(name):
 p=home/('2026-10-13-'+name+'.txt');lines=p.read_text().splitlines();shape=list(map(int,lines[0].split()[1:]));data=list(map(float,' '.join(lines[4:]).split()));assert shape==[32,16] and len(data)==1024
 expected=[math.cos(2*math.pi*(i%32)/16) if 'cosine' in name else 2 for i in range(512)]
 error=max(abs(a-b) for a,b in zip(data[:512],expected));assert error<1e-12 and all(x==0 for x in data[512:])
 centered=lines[1]=='true';assert centered==('dc' in name and 'uncentered' not in name)
 return {'name':name,'shape':shape,'centered':centered,'max_error':error,'sha256':hashlib.sha256(p.read_bytes()).hexdigest()}
def extrema(stem):
 text=(root/('2026-10-13-frequency-'+stem+'-ax.txt')).read_text()
 return [float(x) for x in re.findall(r'stepper \(selectable\) Value: ([^,]+),',text)]
gain=math.exp(-math.pi*(2/3)**2)
checks={'before_imaginary':extrema('before-imaginary')[-2:],'after_imaginary_scale':extrema('after-imaginary')[-2:], 'after_low_real':extrema('after-low-real')[-2:], 'after_high_real':extrema('after-high-real')[-2:], 'before_color_scale':extrema('after-high-real')[-4:-2], 'after_color_scale':extrema('after-color-scale')[-4:-2], 'before_dc_scale':extrema('before-dc')[-2:], 'after_dc_scale':extrema('after-dc')[-2:], 'uncentered_dc_scale':extrema('after-dc-uncentered')[-2:]}
assert abs(checks['before_imaginary'][0]-gain/2)<1e-12
assert all(abs(a-b)<1e-12 for a,b in zip(checks['after_imaginary_scale'],[.01,-.01]))
assert all(abs(a-b)<1e-12 for a,b in zip(checks['after_low_real'],[gain,-gain]))
assert all(abs(a-b)<1e-12 for a,b in zip(checks['after_high_real'],[1-gain,gain-1]))
assert checks['before_color_scale']==[-1,-1] and checks['after_color_scale']==[1,0]
assert checks['before_dc_scale']==[.01,-.01] and checks['after_dc_scale']==checks['uncentered_dc_scale']==[2,0]
result={'fixtures':[fixture(n) for n in ['frequency-cosine-before','frequency-cosine-after','frequency-dc-before','frequency-dc-after','frequency-dc-uncentered']], 'expected_low_gain':gain,'native_display_extrema':checks, 'scope':'Native source samples and displayed extrema; independent component checks verify every reconstructed sample.'}
print(json.dumps(result,indent=2))
