package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.PlanarImage;

import signals.core.DataGeneratorTypeModel;
import signals.core.Zeros;
import signals.gui.plot.ImageDisplayMath;

public class ZeroFunctionTerm2D extends ImageFunctionTerm2D {

	public ZeroFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static int filename_idx = 0; 

	public ZeroFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}
	
	/* (non-Javadoc)
	 * @see signals.core.ImageFunctionTerm2D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		super.initTypeModel(model);
		
		Object[] source_defaults = {};
		model.setSourceDefaults( source_defaults );
		
//		model.setLargeIcon("/functionIcons/Zero1DLarge.png");
//		model.setSmallIcon("/functionIcons/Zero1DSmall.png");
		model.setName("Zero");
		
		model.setDocPath("/functiondoc/zero.html");
	}
	
	public double[] create(int x_dimension, int y_dimension, boolean zeroCentered) {
		
		return Zeros.zeros(x_dimension*y_dimension); 
	}

	public double[] createCropped( int x_dimension, int y_dimension, boolean zeroCentered ) {
		
		return create( x_dimension, y_dimension, zeroCentered ); 
	}
	
	public double[] createScaledPadded( int x_dimension, int y_dimension, boolean zeroCentered ) {
		
		return create( x_dimension, y_dimension, zeroCentered ); 
	}
	
	@Override
	public Interpolation getInterpolation() {
		
		return new InterpolationNearest();
	}

	@Override
	public PlanarImage loadImage() {
		
		int dimension = 2*2; 
		double[] data = new double[dimension];
		return (PlanarImage) ImageDisplayMath.data2Image(data, 2, 2);
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "0["+variables[0]+"]0[" + variables[1] + "]"; 
	}

}
