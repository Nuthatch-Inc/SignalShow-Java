/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Besinc Function
 */
public class BesincFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public BesincFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public BesincFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
		
		int dimension = indices.length; 
		double[] besinc = new double[ dimension ];	
		
		for ( int i = 0; i < dimension; i++ )  { //i in array coordinates
			
			double argument = Math.PI * (indices[i] - center); 
			
			if( argument ==0 ) { //special case
				
				besinc[i] = amplitude; 
				
			} else {
				
				besinc[i] = amplitude * 2 * 
					(width / ((Math.abs(argument))) * Bessel.J1( (Math.abs(argument) / width))); 
			}
			
		}
		
		return besinc;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/Besinc1DLarge.png");
//		model.setSmallIcon("/functionIcons/Besinc1DSmall.png");
		model.setName("Besinc Sombrero");
		
		model.setDocPath("/functiondoc/somb.html");
	}

	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "SOMB["+formattedParamString(variables[0])+"]"; 
	}
	
}
