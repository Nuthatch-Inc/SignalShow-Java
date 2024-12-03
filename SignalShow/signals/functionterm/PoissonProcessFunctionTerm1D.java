/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.text.NumberFormat;
import java.util.Random;

import javax.swing.SpinnerNumberModel;

import signals.core.Core;
import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D PoissonProcess Function
 */
public class PoissonProcessFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public PoissonProcessFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PoissonProcessFunctionTerm1D(DataGenerator datagenerator,
			ParameterBlock paramBlock) {
		super(datagenerator, paramBlock);
		// TODO Auto-generated constructor stub
	}

	int seed_idx; 
	
	/**
	 * @param paramBlock
	 */
	public PoissonProcessFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		width_idx = 1;
		seed_idx = 2;
		
	}

	/**
	 * @return the seed
	 */
	public int getSeed() {
		
		return paramBlock.getIntParameter(seed_idx);
	}
	
	/**
	 * @param phase the initial phase to set
	 */
	public void setSeed(int seed) {
		
		paramBlock.set( seed, seed_idx );

	}
	
	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 	
		double[] noise = new double[ dimension ];
		if( getWidth() < 0.007 ) return noise; 
		
		double lambda = 1/getWidth(); 
		double amplitude = getAmplitude();
		int seed = getSeed();	

		Random rand = new Random( seed );
																		
		double ea = Math.exp( lambda ); 
		int i = 0;  //i is the index that will randomly be set or not.
		boolean done = false; 
								
		while( !done ) {
			
			double alpha = ea * rand.nextDouble();  //mean ^ ( n/n! )
			double sum = 1; 
			double product = 1; 
			int index = 1;
			
			while( sum <= alpha ) {
				
				product = product * lambda / (double)index;
				sum += product;
				index++; 
			}
			
			i += index; 
			
			if( i < dimension && i >= 0 ) {
				
				noise[i] = amplitude;
				
			} else {
			
				done = true;
			}
		
		} //end outer loop 
		
		return noise;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
		
		//parameter classes with exponent
		Class[] param_classes = { Double.class, Double.class, Integer.class };
		model.setParamClasses( param_classes );
		
		String[] param_names = { "Amplitude", ""+'\u03BB', "Seed"}; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1), new Double( 0.1 ), new Integer(1) }; 
		model.setParamDefaults( param_defaults );
		
//		model.setLargeIcon("/functionIcons/PoissonProcess1DLarge.png");
//		model.setSmallIcon("/functionIcons/PoissonProcess1DSmall.png");
		model.setName("Poisson Process");
		
		SpinnerNumberModel meanSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel stdevSpinnerModel = new SpinnerNumberModel( 0.1, -100000.0, 100000.0, 0.01);
		SpinnerNumberModel seedSpinnerModel = new SpinnerNumberModel( new Integer(1), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { meanSpinnerModel, stdevSpinnerModel, seedSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/poissonprocess.html");
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#hasCenter()
	 */
	@Override
	public boolean hasCenter() {
		return false;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#hasWidth()
	 */
	@Override
	public boolean hasWidth() {
		return false;
	}
	
	@Override
	public String getEquation(String[] variables) {
		NumberFormat format = Core.getDisplayOptions().getFormat();
		return "Pois_Proc[" + format.format(getCenter()) +"]"; 
	}
}
