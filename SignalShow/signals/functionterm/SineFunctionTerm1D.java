/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.StringConversions;

/**
 * @author Juliet
 * Represents a 1D Sine Function
 */
public class SineFunctionTerm1D extends CosineFunctionTerm1D { 

	public SineFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SineFunctionTerm1D(ParameterBlock paramBlock) {
		super(paramBlock);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] sine = new double[ dimension ];		
		
		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
		double initialPhase = getInitialPhase();
		
		double twoPiOverPeriod = 2 * Math.PI / width;					
		for ( int i = 0; i < dimension; ++i )  { 
			
			sine[i] = amplitude * Math.sin( twoPiOverPeriod * (indices[i] - center) + Math.toRadians(initialPhase) );  
			
		}	
		
		return sine;
		
	}
	
	@Override
	public String getEquation(String[] variables) {

		return amplitudeMultiplierString() + "sin[2\u03C0("+formattedParamString(variables[0])+")"+ StringConversions.PhaseString(getInitialPhase())+"]"; 
	}
}
