#!/usr/bin/env python3
"""Replay compiled Java Phase Detour against NumPy's inverse FFT and enumerated cells.

JDK seeded generator specification:
https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Random.html
FFT convention: https://numpy.org/doc/stable/reference/generated/numpy.fft.ifft2.html
Native interaction and full Chirplet parity are outside this numerical replay.
"""
import argparse
import hashlib
import json
from pathlib import Path
import subprocess
import tempfile
import numpy as np

JAVA = r'''
package signals.core;
import java.awt.image.renderable.ParameterBlock;
import signals.gui.Colors;
import signals.operation.PhaseDetourHologramOp;
public class PhaseDetourOracle {
  public static void main(String[] args) {
    Core.get().functionCreationOptions=new FunctionCreationOptions();
    Core.get().displayOptions=new DisplayOptions(); Core.get().colors=new Colors();
    for(int w:new int[]{16,8}) for(boolean centered:new boolean[]{false,true})
    for(boolean random:new boolean[]{false,true}) for(boolean diffuse:new boolean[]{false,true})
    for(boolean wide:new boolean[]{false,true}) {
      int h=w==16?8:16; double[] real=new double[w*h],imag=new double[w*h];
      for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
        double x=col-(centered?w/2:0), y=h-1-row-(centered?h/2:0);
        real[row*w+col]=Math.exp(-(x-1.7)*(x-1.7)/17-(y+.8)*(y+.8)/6);
        imag[row*w+col]=.4*Math.cos(.2*(row*w+col));
      }
      Function source=FunctionFactory.createFunction2D(real,imag,centered,"oracle object",w,h);
      PhaseDetourHologramOp op=new PhaseDetourHologramOp(new ParameterBlock());
      op.setRandomPhase(random);op.setErrorDiffusion(diffuse);op.setWiderApertures(wide);
      Function result=op.create(source); StringBuilder bits=new StringBuilder();
      for(double v:result.getReal()) { if(v!=0 && v!=1) throw new AssertionError("nonbinary"); bits.append((int)v); }
      for(double v:result.getImaginary()) if(v!=0) throw new AssertionError("imaginary output");
      System.out.println(w+","+h+","+centered+","+random+","+diffuse+","+wide+":"+bits);
    }
  }
}
'''


def java_uniform(count):
    """Independent Python implementation of the JDK's specified 48-bit sequence."""
    mask = (1 << 48) - 1
    state = (1 ^ 0x5DEECE66D) & mask
    def next_bits(bits):
        nonlocal state
        state = (state * 0x5DEECE66D + 0xB) & mask
        return state >> (48 - bits)
    return np.array([((next_bits(26) << 27) + next_bits(27)) / 2**53 for _ in range(count)])


def expected_cells(spectrum, diffuse, wide):
    h, w = spectrum.shape
    values = spectrum.ravel() / np.max(np.abs(spectrum))
    directions = np.exp(2j * np.pi * (np.arange(8) - 4) / 8)
    heights = np.arange(9) / 8
    pixels = np.zeros((h*8, w*8), dtype=np.uint8)
    residual = 0j
    for k, value in enumerate(values):
        z = value + (residual if diffuse else 0)
        height = int(np.argmin(np.abs(abs(z) - heights)))
        column = int(np.argmax((z * np.conj(directions)).real))
        residual = z - height/8 * directions[column]
        columns = [column] if not wide else [column, (column-1)%8, (column+1)%8]
        for c in columns:
            pixels[(k//w)*8+8-height:(k//w+1)*8, (k%w)*8+c] = 1
    return pixels.ravel()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jar', type=Path, required=True, help='Packaged JAI dependencies; target/classes take precedence')
    parser.add_argument('--output', type=Path)
    args = parser.parse_args()
    repo = Path(__file__).resolve().parents[3]
    classpath = str(repo/'target/classes') + ':' + str(args.jar.resolve())
    with tempfile.TemporaryDirectory(prefix='signalshow-phase-oracle-') as directory:
        source = Path(directory)/'PhaseDetourOracle.java'; source.write_text(JAVA)
        subprocess.run(['javac','-cp',classpath,'-d',directory,str(source)],check=True,capture_output=True)
        actual = subprocess.run(['java','-Djava.awt.headless=true','-cp',directory+':'+classpath,
            'signals.core.PhaseDetourOracle'],check=True,text=True,capture_output=True).stdout
    cases=[]
    for line in actual.splitlines():
        flags, bits = line.split(':'); w,h,centered,random,diffuse,wide=flags.split(',')
        w,h=int(w),int(h); centered,random,diffuse,wide=[v=='true' for v in (centered,random,diffuse,wide)]
        x=np.arange(w)-(w//2 if centered else 0)
        y=np.arange(h-1,-1,-1)-(h//2 if centered else 0)
        amplitude=np.exp(-(x[None,:]-1.7)**2/17-(y[:,None]+.8)**2/6)
        phase=2*np.pi*java_uniform(w*h).reshape(h,w)-np.pi if random else np.zeros((h,w))
        obj=amplitude*np.exp(1j*phase)
        # Ascending physical y, move the source origin to FFT index 0, then
        # return the spectrum to centered coordinates and top-to-bottom raster rows.
        bottom_up=obj[::-1]
        if centered: bottom_up=np.fft.ifftshift(bottom_up)
        spectrum=np.fft.fftshift(np.fft.ifft2(bottom_up)*w*h)[::-1]
        expected=expected_cells(spectrum,diffuse,wide)
        observed=np.frombuffer(bits.encode('ascii'),dtype=np.uint8)-ord('0')
        np.testing.assert_array_equal(observed,expected,err_msg=flags)
        cases.append({'width':w,'height':h,'centered':centered,'randomPhase':random,
                      'errorDiffusion':diffuse,'wideApertures':wide,'pixels':len(expected),'mismatches':0})
    assert len(cases)==32
    classfile=repo/'target/classes/signals/operation/PhaseDetourHologramOp.class'
    result={'oracle':'numpy.fft.ifft2 with physical-coordinate ordering and enumerated binary cells',
            'numpyVersion':np.__version__,'passed':True,'cases':cases,
            'totalPixels':sum(c['pixels'] for c in cases),'phaseDetourClassSHA256':hashlib.sha256(classfile.read_bytes()).hexdigest(),
            'limits':'Java retains inverse FFT and fixed seed 1; Chirplet uses forward FFT and fresh random phase. No native/GPU or Windows run.'}
    serialized=json.dumps(result,indent=2)+'\n'
    if args.output: args.output.write_text(serialized)
    print(json.dumps({'passed':True,'cases':len(cases),'totalPixels':result['totalPixels'],'numpyVersion':np.__version__}))


if __name__=='__main__': main()
