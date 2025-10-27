/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Triangle Function
 */
public class TriangleFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public TriangleFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TriangleFunctionTerm1D(DataGenerator datagenerator,
			ParameterBlock paramBlock) {
		super(datagenerator, paramBlock);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public TriangleFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] triangle = new double[ dimension ];		
		
		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
									
		for ( int i = 0; i < dimension; ++i )  { 
			
			double argument = Math.abs(indices[i] - center); 
			if( argument < width ) triangle[i] = amplitude * ( 1 - argument / width );
			
		}	
		
		return triangle;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/Triangle1DLarge.png");
//		model.setSmallIcon("/functionIcons/Triangle1DSmall.png");
		model.setName("Triangle");
		
		model.setDocPath("/functiondoc/tri.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "TRI["+formattedParamString(variables[0])+"]"; 
	}
	
	
}
