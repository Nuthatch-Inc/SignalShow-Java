/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.text.NumberFormat;
import java.util.Random;

import javax.swing.SpinnerNumberModel;

import signals.core.Core;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Exponential Function
 */
public class ExponentialNoiseFunctionTerm1D extends NoiseFunctionTerm1D { 
	
	public ExponentialNoiseFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ExponentialNoiseFunctionTerm1D(ParameterBlock paramBlock) {
		super(paramBlock);
		seed_idx = 1;
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create( int dimension ) {		
		
		//probability of a trial occuring
		double mean = getMean();
		int seed = getSeed();
		
		double[] noise = new double[ dimension ];	
		
		Random rand = new Random( seed ); 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			noise[i] = -mean * Math.log( 1 - rand.nextDouble() );
			
		}
		
		return noise;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/ExponentialNoiseDistribution1DLarge.png");
//		model.setSmallIcon("/functionIcons/ExponentialNoiseDistribution1DSmall.png");
		model.setName("Exponential Noise");
		
		Class[] param_classes = { Double.class, Integer.class };
		model.setParamClasses( param_classes );
		
		String[] param_names = { "mean="+'\u03B2', "Seed"}; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1), new Integer( 1 )}; 
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel meanSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel seedSpinnerModel = new SpinnerNumberModel( new Integer(1), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { meanSpinnerModel, seedSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/exponentialnoise.html");
	}
	
	/* (non-Javadoc)
	 * @see signals.function.NoiseFunctionTerm1D#hasWidth()
	 */
	@Override
	public boolean hasAmplitude() {
		return false;
	}
	
	@Override
	public String getEquation(String[] variables) {
		NumberFormat format = Core.getDisplayOptions().getFormat();
		return "Exp\uFF5E(" + format.format(1/getMean()) +")"; 
	}
}
