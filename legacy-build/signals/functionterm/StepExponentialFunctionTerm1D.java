/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.text.NumberFormat;

import signals.core.Core;
import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D StepExponential Function
 */
public class StepExponentialFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public StepExponentialFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StepExponentialFunctionTerm1D(DataGenerator datagenerator,
			ParameterBlock paramBlock) {
		super(datagenerator, paramBlock);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public StepExponentialFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] stepExponential = new double[ dimension ];		
		
		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
								
		for ( int i = 0; i < dimension; ++i )  { 
			
			double argument = amplitude * Math.exp( (center - indices[i]) / width ); 
			if( indices[i] > center ) { 
				
				stepExponential[i] = argument; 
				
			} else if( indices[i] < center ) { 
				
				stepExponential[i] = 0; 
				
			} else { //equal
				
				stepExponential[i] = argument / 2.0; 
			} 
			
		}	
		
		return stepExponential;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/StepExponential1DLarge.png");
//		model.setSmallIcon("/functionIcons/StepExponential1DSmall.png");
		model.setName("Step Exponential");
		
		model.setDocPath("/functiondoc/exp.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		NumberFormat formatter = Core.getDisplayOptions().getFormat();
		return amplitudeMultiplierString() + "exp[-(" +formattedParamString(variables[0])+ ")]STEP["+variables[0]+" - "+formatter.format(getCenter())+"]";
	}
}
