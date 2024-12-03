package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGeneratorTypeModel;

public class EFunctionTerm2D extends PredefinedImageFunctionTerm2D {

	public EFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	/* (non-Javadoc)
	 * @see signals.core.ImageFunctionTerm2D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		super.initTypeModel(model);
		
		Object[] source_defaults = { "/images/E.png" };
		model.setSourceDefaults( source_defaults );
		
//		model.setLargeIcon("/functionIcons/E2DLarge.png");
//		model.setSmallIcon("/functionIcons/E2DSmall.png");
		model.setName("E");
	
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "E"; 
	}
	
}
