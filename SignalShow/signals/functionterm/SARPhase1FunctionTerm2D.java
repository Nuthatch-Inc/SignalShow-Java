package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBilinear;

import signals.core.DataGeneratorTypeModel;

public class SARPhase1FunctionTerm2D extends PhaseImageFunctionTerm2D {

	public SARPhase1FunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SARPhase1FunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	/* (non-Javadoc)
	 * @see signals.core.ImageFunctionTerm2D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		super.initTypeModel(model);
		
		Object[] source_defaults = { "/images/SAR_phase1.png" };
		model.setSourceDefaults( source_defaults );
		
//		model.setLargeIcon("/functionIcons/SARPhase12DLarge.png");
//		model.setSmallIcon("/functionIcons/SARPhase12DSmall.png");
		model.setName("SAR Phase");
	}

	@Override
	public Interpolation getInterpolation() {
		return new InterpolationBilinear();
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "SAR_Phase"; 
	}
	
}
