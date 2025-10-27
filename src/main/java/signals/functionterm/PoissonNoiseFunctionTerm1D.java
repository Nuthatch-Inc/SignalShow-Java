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
 * Represents a 1D Poisson Function
 */
public class PoissonNoiseFunctionTerm1D extends NoiseFunctionTerm1D { 
	
	public PoissonNoiseFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PoissonNoiseFunctionTerm1D(ParameterBlock paramBlock) {
		super(paramBlock);
		seed_idx = 1;
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create( int dimension ) {		
	
		double mean = getCenter();
		int seed = getSeed(); 
		
		double[] noise = new double[ dimension ];	
		
		Random rand = new Random( seed ); 
		
		double ea = Math.exp( mean ); 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			double alpha = ea * rand.nextDouble();  //mean ^ ( n/n! )
			double sum = 1; 
			double product = 1; 
			double index = 1;
			
			while( sum <= alpha ) {
				
				product = product * mean  / index;
				sum += product;
				index++; 
			}
			
			noise[i] = index - 1; 
			
		}	
		
		return noise;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/PoissonNoiseDistribution1DLarge.png");
//		model.setSmallIcon("/functionIcons/PoissonNoiseDistribution1DSmall.png");
		model.setName("Poisson Noise");
		
		String[] param_names = { "" + '\u03BB', "Seed"}; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1),  new Integer( 1 )}; 
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel meanSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel seedSpinnerModel = new SpinnerNumberModel( new Integer(1), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { meanSpinnerModel, seedSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/poissonnoise.html");
	
	}
	
	public boolean isIntegerDefined() {
		
		return true;
	}

	/* (non-Javadoc)
	 * @see signals.function.NoiseFunctionTerm1D#hasWidth()
	 */
	@Override
	public boolean hasAmplitude() {
		return false;
	}
	
	public double getAmplitude() {
		
		return getCenter();
	}
	
	@Override
	public String getEquation(String[] variables) {
		NumberFormat format = Core.getDisplayOptions().getFormat();
		return "Pois\uFF5E(" + format.format(getCenter()) +")"; 
	}
}
