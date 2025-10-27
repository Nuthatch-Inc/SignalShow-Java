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
 * Represents a 1D Gaussian Function
 */
public class GaussianNoiseFunctionTerm1D extends NoiseFunctionTerm1D { 
	
	public GaussianNoiseFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GaussianNoiseFunctionTerm1D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create( int dimension ) {	
		
		double stDev = getStDev(); 
		double mean = getMean();
		int seed = getSeed(); 
		
		double[] noise = new double[ dimension ];	
		
		Random rand = new Random( seed ); 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			noise[i] = rand.nextGaussian() * stDev + mean;
			
		}	
		
		return noise;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/GaussianNoiseDistribution1DLarge.png");
//		model.setSmallIcon("/functionIcons/GaussianNoiseDistribution1DSmall.png");
		model.setName("Gaussian Noise");
		
		model.setDocPath("/functiondoc/gaussiannoise.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		NumberFormat format = Core.getDisplayOptions().getFormat();
		return "N\uFF5E(" + format.format(getMean()) +", " + format.format(getStDev()) +")"; 
	}
}
