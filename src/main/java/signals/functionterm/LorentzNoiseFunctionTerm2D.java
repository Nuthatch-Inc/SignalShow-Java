package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;


public class LorentzNoiseFunctionTerm2D extends NoiseFunctionTerm2D {

	public LorentzNoiseFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LorentzNoiseFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	@Override
	public NoiseFunctionTerm1D getNoise1DInstance() {

		return (NoiseFunctionTerm1D) (new LorentzNoiseFunctionTerm1D(null)).getDefaultInstance();
	}

}
