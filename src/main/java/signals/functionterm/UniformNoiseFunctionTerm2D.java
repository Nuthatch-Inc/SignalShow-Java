package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;


public class UniformNoiseFunctionTerm2D extends NoiseFunctionTerm2D {

	public UniformNoiseFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UniformNoiseFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	@Override
	public NoiseFunctionTerm1D getNoise1DInstance() {

		return (NoiseFunctionTerm1D) (new UniformNoiseFunctionTerm1D(null)).getDefaultInstance();
	}

}
