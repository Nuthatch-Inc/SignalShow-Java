package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGeneratorTypeModel;

public class StepWedgeFunctionTerm2D extends PredefinedImageFunctionTerm2D {

	public StepWedgeFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StepWedgeFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	/* (non-Javadoc)
	 * @see signals.core.ImageFunctionTerm2D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		super.initTypeModel(model);
		
		Object[] source_defaults = { "/images/stepwedge.png" };
		model.setSourceDefaults( source_defaults );
		
//		model.setLargeIcon("/functionIcons/StepWedge2DLarge.png");
//		model.setSmallIcon("/functionIcons/StepWedge2DSmall.png");
		model.setName("Step Wedge");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Step_Wedge"; 
	}
	
}
