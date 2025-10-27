package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;


public class RayleighNoiseFunctionTerm2D extends NoiseFunctionTerm2D {

	public RayleighNoiseFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RayleighNoiseFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	@Override
	public NoiseFunctionTerm1D getNoise1DInstance() {

		return (NoiseFunctionTerm1D) (new RayleighNoiseFunctionTerm1D(null)).getDefaultInstance();
	}

}
