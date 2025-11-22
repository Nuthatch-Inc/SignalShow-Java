/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Cosine Function
 */
public abstract class NoiseFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public NoiseFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int seed_idx; 
	
	/**
	 * @param paramBlock
	 */
	public NoiseFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		center_idx = 0; 
		amplitude_idx = 1; 
		seed_idx = 2;
		
	}
	
	public abstract double[] create( int dimension );
	
	
	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
	
		return create( indices.length );
	}

	/**
	 * @return the center
	 */
	public double getCenter() {
		
		return paramBlock.getDoubleParameter(center_idx);
	}
	
	public double getMean() {
		
		return paramBlock.getDoubleParameter(center_idx);
	}

	/**
	 * @param center the center to set
	 */
	public void setCenter(double center) {
		
		paramBlock.set( center, center_idx );
	}
	
	/**
	 * @return the amplitude
	 */
	public double getAmplitude() {
		
		return paramBlock.getDoubleParameter(amplitude_idx);
	}
	
	public double getStDev() {
		
		return paramBlock.getDoubleParameter(amplitude_idx);
	}

	/**
	 * @param amplitude the amplitude to set
	 */
	public void setAmplitude(double amplitude) {
		
		paramBlock.set( amplitude, amplitude_idx );

	}

	/**
	 * @return the initial phase
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
	 * @see signals.core.AnalyticFunctionTerm1D#hasWidth()
	 */
	@Override
	public boolean hasWidth() {
		return false; 
	}
	
	/**
	 * Subclasses should override this method if they are only defined at integers.
	 * @return 
	 */
	public boolean isIntegerDefined() {
		
		return false; 
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
		
		Class[] param_classes = { Double.class, Double.class, Integer.class };
		model.setParamClasses( param_classes );
		
		String[] param_names = { ""+'\u03BC', ""+'\u03C3', "Seed"}; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(0), new Double( 1 ), new Integer( 1 )}; 
		model.setParamDefaults( param_defaults );
	
		SpinnerNumberModel meanSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel stdevSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel seedSpinnerModel = new SpinnerNumberModel( new Integer(1), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { meanSpinnerModel, stdevSpinnerModel, seedSpinnerModel };
		model.setSpinnerModels(spinnerModels);
	}
}
