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
 * Represents a 1D Binomial Function
 */
public class BinomialNoiseFunctionTerm1D extends NoiseFunctionTerm1D { 
	
	public BinomialNoiseFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BinomialNoiseFunctionTerm1D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create( int dimension ) {		
		
		//probability of a single trial
		double p = getP();
		int n = getN();
		int seed = getSeed(); 
		
		double[] noise = new double[ dimension ];	
		
		Random rand = new Random( seed ); 
		
		for ( int i = 0; i < dimension; i++ )  {
			for ( int j = 0; j < n; j++ )  {
				
				if( rand.nextDouble() <= p ) {

					noise[i] += 1;
				}					
			}
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
//		model.setLargeIcon("/functionIcons/BinomialNoiseDistribution1DLarge.png");
//		model.setSmallIcon("/functionIcons/BinomialNoiseDistribution1DSmall.png");
		model.setName("Binomial Noise");
		
		Class[] param_classes = { Double.class, Integer.class, Integer.class };
		model.setParamClasses( param_classes );
		
		String[] param_names = { '\u03BC'+"="+"np", "n", "Seed"}; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(0.5), new Integer( 1 ), new Integer( 1 )}; 
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel meanSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel nSpinnerModel = new SpinnerNumberModel( new Integer(1), 
				new Integer(0), new Integer(100000), new Integer(1) );
		SpinnerNumberModel seedSpinnerModel = new SpinnerNumberModel( new Integer(1), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { meanSpinnerModel, nSpinnerModel, seedSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/binomialnoise.html");
	}
	
	/* (non-Javadoc)
	 * @see signals.function.NoiseFunctionTerm1D#hasWidth()
	 */
	@Override
	public boolean hasAmplitude() {
		return false;
	}
	
	public boolean isIntegerDefined() {
		
		return true;
	}
	
	/**
	 * @return n
	 */
	public int getN() {
		
		return paramBlock.getIntParameter(amplitude_idx);
	}
	
	/**
	 * mean = n*p => p = mean/n
	 * @return p
	 */
	public double getP() {
		
		return getCenter()/getN();
	}
	
	@Override
	public String getEquation(String[] variables) {
		return "B\uFF5E(" + getN() + ", " + getP() +")"; 
	}

}
