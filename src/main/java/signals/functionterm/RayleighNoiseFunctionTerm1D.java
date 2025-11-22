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
 * Represents a 1D Rayleigh Function
 */
public class RayleighNoiseFunctionTerm1D extends NoiseFunctionTerm1D { 

	public RayleighNoiseFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RayleighNoiseFunctionTerm1D(ParameterBlock paramBlock) {
		super(paramBlock);
		seed_idx = 1;
	}
	
	public double getSigma() {
		
		return getCenter() / Math.sqrt( Math.PI /2.0 );
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create( int dimension ) {		
		
		double sigma = getSigma(); 
		int seed = getSeed(); 
		
		double[] noise = new double[ dimension ];	
		
		Random rand = new Random( seed ); 
		
		double multiplier = -2 * sigma * sigma; 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			noise[i] = Math.sqrt(  multiplier * Math.log( 1 - rand.nextDouble() ) );
			
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
//		model.setLargeIcon("/functionIcons/RayleighNoiseDistribution1DLarge.png");
//		model.setSmallIcon("/functionIcons/RayleighNoiseDistribution1DSmall.png");
		model.setName("Rayleigh Noise");
		
		Class[] param_classes = { Double.class, Integer.class };
		model.setParamClasses( param_classes );
		
		//mean=sigma*sqrt(pi/2)
		String[] param_names = { '\u03BC'+"="+'\u03C3'+'a'+"("+'\u03C0'+"/2)", "Seed"}; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(0.5), new Integer( 1 )}; 
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel meanSpinnerModel = new SpinnerNumberModel( 0.5, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel seedSpinnerModel = new SpinnerNumberModel( new Integer(1), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { meanSpinnerModel, seedSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/rayleighnoise.html");
	}
	
	/* (non-Javadoc)
	 * @see signals.function.NoiseFunctionTerm1D#hasWidth()
	 */
	@Override
	public boolean hasAmplitude() {
		return false;
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
		return "Rayleigh\uFF5E(" + format.format(getSigma()) +")"; 
	}
}
