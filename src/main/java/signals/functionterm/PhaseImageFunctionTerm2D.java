package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;


public class PhaseImageFunctionTerm2D extends PredefinedImageFunctionTerm2D {

	public PhaseImageFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PhaseImageFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	public double[] scaleData( int[] pixels, int width, int height, int nbands ) {

		int idx = 0;
		
		double scalar = 2*Math.PI / 255.0;

		double[] data = new double[width*height];
		for(int h=0;h<height;h++) {
			for(int w=0;w<width;w++)  {

				int offset = h*width*nbands+w*nbands;	
				data[idx++] = pixels[offset] * scalar - Math.PI;
			}
		}
		
		return data;
	}
}
