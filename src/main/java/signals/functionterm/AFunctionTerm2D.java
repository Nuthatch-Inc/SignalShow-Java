package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBilinear;

import signals.core.DataGeneratorTypeModel;

public class AFunctionTerm2D extends PredefinedImageFunctionTerm2D {

	public AFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	/* (non-Javadoc)
	 * @see signals.core.ImageFunctionTerm2D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		super.initTypeModel(model);
		
		Object[] source_defaults = { "/images/A.png" };
		model.setSourceDefaults( source_defaults );
		
//		model.setLargeIcon("/functionIcons/A2DLarge.png");
//		model.setSmallIcon("/functionIcons/A2DSmall.png");
		model.setName("A");

	}

	@Override
	public Interpolation getInterpolation() {
		return new InterpolationBilinear();
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "A"; 
	}
	
}
