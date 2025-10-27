package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;


public class PoissonNoiseFunctionTerm2D extends NoiseFunctionTerm2D {

	public PoissonNoiseFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PoissonNoiseFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	@Override
	public NoiseFunctionTerm1D getNoise1DInstance() {

		return (NoiseFunctionTerm1D) (new PoissonNoiseFunctionTerm1D(null)).getDefaultInstance();
	}

}
