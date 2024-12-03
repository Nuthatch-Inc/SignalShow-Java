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
 * Represents a 1D Salt and Pepper Noise Function
 */
public class SaltAndPepperNoiseFunctionTerm1D extends NoiseFunctionTerm1D { 
	
	public SaltAndPepperNoiseFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	int noiseFractionIndex; 
	
	public SaltAndPepperNoiseFunctionTerm1D(ParameterBlock paramBlock) {
		super(paramBlock);
		seed_idx = 3;
		noiseFractionIndex = 2; 
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create( int dimension ) {		
	
		int seed = getSeed(); 
		double noiseFraction = getNoiseFraction(); 
		double amplitude = getAmplitude();
		double center = getCenter();
		
		double upper = center + amplitude; 
		double lower = center - amplitude;
		
		double upperThresh = 1 - noiseFraction/2.0; 
		double lowerThresh = noiseFraction/2.0;
		
		double[] noise = new double[ dimension ];	
		
		Random rand = new Random( seed ); 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			double num = rand.nextDouble(); 
			if( num > upperThresh ) noise[i] = upper; 
			else if ( num < lowerThresh ) noise[i] = lower;
			else noise[i] = center;
			
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
//		model.setLargeIcon("/functionIcons/SaltAndPepper1DLarge.png");
//		model.setSmallIcon("/functionIcons/SaltAndPepper1DSmall.png");
		model.setName("Salt And Pepper Noise");
		
		Class[] param_classes = { Double.class, Double.class, Double.class, Integer.class };
		model.setParamClasses( param_classes );
		
		String[] param_names =  { "A"+'\u2080', "x"+'\u2080', "f", "Seed" }; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(0), new Double(1), new Double(.05),  new Integer( 1 ) }; 
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel meanSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel stdevSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel noiseFractionSpinnerModel = new SpinnerNumberModel( 0.05, 0.0, 1.0, 0.05);
		SpinnerNumberModel seedSpinnerModel = new SpinnerNumberModel( new Integer(1), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { meanSpinnerModel, stdevSpinnerModel, 
												noiseFractionSpinnerModel, seedSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/saltandpeppernoise.html");
	}
	
	public double getNoiseFraction() {
		
		return paramBlock.getDoubleParameter(noiseFractionIndex);
	}

	/* (non-Javadoc)
	 * @see signals.function.NoiseFunctionTerm1D#hasWidth()
	 */
	@Override
	public boolean hasWidth() {
		return false; 
	}
	
	@Override
	public String getEquation(String[] variables) {
		NumberFormat format = Core.getDisplayOptions().getFormat();
		return amplitudeMultiplierString() + "Salt+Pepper\uFF5E(" + format.format(getCenter()) +")"; 
	}
		
}
