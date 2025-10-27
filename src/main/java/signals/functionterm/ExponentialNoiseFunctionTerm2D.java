package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;


public class ExponentialNoiseFunctionTerm2D extends NoiseFunctionTerm2D {

	public ExponentialNoiseFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ExponentialNoiseFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	@Override
	public NoiseFunctionTerm1D getNoise1DInstance() {

		return (NoiseFunctionTerm1D) (new ExponentialNoiseFunctionTerm1D(null)).getDefaultInstance();
	}

}
