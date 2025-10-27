package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;


public class RandomPhaseFunctionTerm2D extends NoiseFunctionTerm2D {

	public RandomPhaseFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RandomPhaseFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	@Override
	public NoiseFunctionTerm1D getNoise1DInstance() {

		return (NoiseFunctionTerm1D) (new RandomPhaseFunctionTerm1D(null)).getDefaultInstance();
	}

}
