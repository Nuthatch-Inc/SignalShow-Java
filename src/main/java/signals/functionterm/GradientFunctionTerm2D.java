package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGeneratorTypeModel;

public class GradientFunctionTerm2D extends PredefinedImageFunctionTerm2D {

	public GradientFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GradientFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	/* (non-Javadoc)
	 * @see signals.core.ImageFunctionTerm2D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		super.initTypeModel(model);
		
		Object[] source_defaults = { "/images/gradient.png" };
		model.setSourceDefaults( source_defaults );
		
//		model.setLargeIcon("/functionIcons/Gradient2DLarge.png");
//		model.setSmallIcon("/functionIcons/Gradient2DSmall.png");
		model.setName("Gradient");
		
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Gradient"; 
	}
}
