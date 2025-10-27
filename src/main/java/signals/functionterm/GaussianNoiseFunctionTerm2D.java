package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;


public class GaussianNoiseFunctionTerm2D extends NoiseFunctionTerm2D {

	public GaussianNoiseFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GaussianNoiseFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	@Override
	public NoiseFunctionTerm1D getNoise1DInstance() {

		return (NoiseFunctionTerm1D) (new GaussianNoiseFunctionTerm1D(null)).getDefaultInstance();
	}

}
