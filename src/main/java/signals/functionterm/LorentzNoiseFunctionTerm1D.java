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
 * Represents a 1D Lorentz Function
 */
public class LorentzNoiseFunctionTerm1D extends NoiseFunctionTerm1D { 
	
	public LorentzNoiseFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LorentzNoiseFunctionTerm1D(ParameterBlock paramBlock) {
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
			
			noise[i] = stDev * (Math.tan( Math.PI * ( rand.nextDouble() - 0.5 ) ) ) + mean; 
			
		}	
		
		return noise;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/LorentzNoiseDistribution1DLarge.png");
//		model.setSmallIcon("/functionIcons/LorentzNoiseDistribution1DSmall.png");
		model.setName("Cauchy Lorentz Noise");
		
		String[] param_names = { "Center", "Width", "Seed"}; 
		model.setParamNames( param_names );
		
		model.setDocPath("/functiondoc/lorentznoise.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		NumberFormat format = Core.getDisplayOptions().getFormat();
		return "Cauchy\uFF5E(" + getMean() +", " + format.format(getStDev()) +")"; 
	}
}
