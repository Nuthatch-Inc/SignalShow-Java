package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.PlanarImage;

import signals.core.DataGeneratorTypeModel;
import signals.gui.plot.ImageDisplayMath;

public class ConstantFunctionTerm2D extends ImageFunctionTerm2D {

	public ConstantFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ConstantFunctionTerm2D(ParameterBlock paramBlock) {
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
		
		model.setName("Constant");
		
		model.setDocPath("/functiondoc/constant.html");
	}

	@Override
	public Interpolation getInterpolation() {
		return new InterpolationNearest();
	}


	@Override
	public PlanarImage loadImage() {
		
		int dimension = 2*2; 
		double[] data = new double[dimension];
		for( int i = 0; i < dimension; ++i ) {
			
			data[i] = 1.0; 
		}
		return (PlanarImage) ImageDisplayMath.data2Image(data, 2, 2);
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "1["+variables[0]+"]1[" + variables[1] + "]"; 
	}

}
