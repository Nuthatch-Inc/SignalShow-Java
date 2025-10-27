/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.util.Random;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Uniform Function
 */
public class RandomPhaseFunctionTerm1D extends NoiseFunctionTerm1D { 
	
	public RandomPhaseFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RandomPhaseFunctionTerm1D(ParameterBlock paramBlock) {
		super(paramBlock);
		seed_idx = 0; 
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create( int dimension ) {		
		
		int seed = getSeed(); 
		
		double[] noise = new double[ dimension ];	
		
		Random rand = new Random( seed ); 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			noise[i] = 2*Math.PI*rand.nextDouble() - Math.PI;
			
		}	
		
		return noise;
	}
	
	@Override
	public boolean hasWidth() {
		return false;
	}

	@Override
	public boolean hasAmplitude() {
		return false;
	}

	@Override
	public boolean hasCenter() {
		return false;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
		
		Class[] param_classes = { Integer.class };
		model.setParamClasses( param_classes );
		
//		model.setLargeIcon("/functionIcons/RandomPhase1DLarge.png");
//		model.setSmallIcon("/functionIcons/RandomPhase1DSmall.png");
		model.setName("Random Phase");
		
		String[] param_names = { "Seed"}; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Integer( 1 )}; 
		model.setParamDefaults( param_defaults );

		SpinnerNumberModel seedSpinnerModel = new SpinnerNumberModel( new Integer(1), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { seedSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/randomphase.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return "U\uFF5E( -" + '\u03C0' +", " + '\u03C0' +")"; 
	}
}
