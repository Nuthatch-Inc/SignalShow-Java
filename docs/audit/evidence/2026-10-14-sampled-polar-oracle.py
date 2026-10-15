#!/usr/bin/env python3
"""Compare the compiled Java sampled-coordinate evaluator with NumPy interpolation.

Run after Maven compiles production classes; the packaged jar supplies dependencies.
This is a headless numerical replay, not native file-picker or desktop evidence.
"""
import argparse
import csv
import io
import json
from pathlib import Path
import subprocess
import tempfile
import numpy as np

JAVA = r'''
import java.awt.image.renderable.ParameterBlock;
import signals.functionterm.DataFunctionTerm1D;
public class SampledPolarOracle {
    public static void main(String[] args) {
        double[][] channels = {{1,.2,-.6,1.8,.4,-1.2,.8,2}, {-.3,1.1,.7,-.8,.2,.9,-1.3,.5}};
        double[] coordinates = {-180,-45,0,Math.sqrt(2),5,Math.hypot(3,4),8,13,19,20,40,55,65,89,90,179,180};
        for (int domain=0; domain<3; domain++) for (int mode=0; mode<2; mode++)
        for (int edit=0; edit<2; edit++) for (int channel=0; channel<2; channel++) {
            int dimension=domain==0?64:domain==1?90:360;
            int center=domain==0?8+3*edit:40+5*edit;
            int halfWidth=domain==0?12-2*edit:25-5*edit;
            double amplitude=edit==0?1.4:-.7;
            ParameterBlock pb=new DataFunctionTerm1D().getDefaultParamBlock();
            pb.addSource(channels[channel].clone());
            DataFunctionTerm1D term=new DataFunctionTerm1D(pb);
            term.setMode(mode); term.setCenter(center); term.setWidth(halfWidth); term.setAmplitude(amplitude);
            double[] actual=term.create(coordinates,dimension,domain==2);
            for(int i=0; i<coordinates.length; i++)
                System.out.println(domain+","+mode+","+edit+","+channel+","+coordinates[i]+","+actual[i]);
        }
    }
}
'''


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jar', type=Path, required=True)
    parser.add_argument('--output', type=Path)
    args = parser.parse_args()
    repo = Path(__file__).resolve().parents[3]
    classpath = str(repo / 'target/classes') + ':' + str(args.jar.resolve())
    with tempfile.TemporaryDirectory(prefix='signalshow-polar-oracle-') as directory:
        source = Path(directory) / 'SampledPolarOracle.java'
        source.write_text(JAVA)
        subprocess.run(['javac', '-cp', classpath, '-d', directory, str(source)], check=True, capture_output=True)
        actual = subprocess.run(['java', '-Djava.awt.headless=true', '-cp', directory + ':' + classpath,
                                 'SampledPolarOracle'], check=True, text=True, capture_output=True).stdout
    channels = np.array([[1,.2,-.6,1.8,.4,-1.2,.8,2], [-.3,1.1,.7,-.8,.2,.9,-1.3,.5]])
    errors = []
    for row in csv.reader(io.StringIO(actual)):
        domain, mode, edit, channel = map(int, row[:4])
        coordinate, observed = map(float, row[4:])
        dimension = (64, 90, 360)[domain]
        center = 8+3*edit if domain == 0 else 40+5*edit
        half_width = 12-2*edit if domain == 0 else 25-5*edit
        amplitude = 1.4 if edit == 0 else -.7
        if mode == 0:
            start = -dimension//2 if domain == 2 else 0
            end = start+dimension-1
        else:
            start, end = center-half_width, center+half_width-1
        expected = amplitude * np.interp(coordinate, np.linspace(start, end, 8), channels[channel], left=0, right=0)
        if mode == 0 and not center-half_width <= coordinate <= center+half_width:
            expected = 0
        np.testing.assert_allclose(observed, expected, rtol=0, atol=1e-10, err_msg=str(row[:5]))
        errors.append(abs(observed-expected))
    assert len(errors) == 408
    result = {'oracle': 'numpy.interp', 'numpyVersion': np.__version__, 'checks': len(errors),
              'maximumAbsoluteError': float(max(errors)), 'passed': True,
              'scope': 'Compiled Java DataFunctionTerm1D; crop/scale, radial and both angular domains, two edits, both channels',
              'limits': 'Does not execute native input, file import, or Chirplet GPU paths'}
    serialized = json.dumps(result, indent=2) + '\n'
    if args.output:
        args.output.write_text(serialized)
    print(serialized, end='')


if __name__ == '__main__':
    main()
