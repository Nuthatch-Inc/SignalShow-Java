/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.text.NumberFormat;
import java.util.Random;

import signals.core.Core;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Uniform Function
 */
public class UniformNoiseFunctionTerm1D extends NoiseFunctionTerm1D { 
	
	public UniformNoiseFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UniformNoiseFunctionTerm1D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create( int dimension ) {		
		
		double halfwidth = getStDev(); 
		double mean = getMean();
		int seed = getSeed(); 
		
		double[] noise = new double[ dimension ];	
		
		Random rand = new Random( seed ); 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			noise[i] = 2*halfwidth*rand.nextDouble() + mean-halfwidth;
			
		}	
		
		return noise;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/UniformNoiseDistribution1DLarge.png");
//		model.setSmallIcon("/functionIcons/UniformNoiseDistribution1DSmall.png");
		model.setName("Uniform Noise");
		
		String[] param_names = { ""+'\u03BC', "Half-Width", "Seed"}; 
		model.setParamNames( param_names );
		
		model.setDocPath("/functiondoc/uniformnoise.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		NumberFormat format = Core.getDisplayOptions().getFormat();
		double min = getMean() - getStDev(); 
		double max = getMean() + getStDev(); 
		return "U\uFF5E(" + format.format(min) +", " + format.format(max) +")"; 
	}
}
