/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Stairstep Function
 */
public class StairstepFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public StairstepFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StairstepFunctionTerm1D(DataGenerator datagenerator,
			ParameterBlock paramBlock) {
		super(datagenerator, paramBlock);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public StairstepFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] stairstep = new double[ dimension ];		
		
		double width = getWidth()*2; //width of a single step
		double center = getCenter(); //intercept
		double amplitude = getAmplitude();	
						
		for ( int i = 0; i < dimension; i++ )  { 			
		
			stairstep[i] = amplitude * Math.floor((( indices[i]-center + width/2 ) / width )); 
		}
		
		return stairstep;
		
	}
	
	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#isHalfWidthDefined()
	 */
	@Override
	public boolean isHalfWidthDefined() {
		return true;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/Stairstep1DLarge.png");
//		model.setSmallIcon("/functionIcons/Stairstep1DSmall.png");
		model.setName("Stairstep");
		model.setDocPath("/functiondoc/stairstep.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "stairstep["+formattedParamString(variables[0])+"]"; 
	}
	
}
