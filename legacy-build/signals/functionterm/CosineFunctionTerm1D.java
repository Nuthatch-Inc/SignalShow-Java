/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;
import signals.core.StringConversions;

/**
 * @author Juliet
 * Represents a 1D Cosine Function
 */
public class CosineFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public CosineFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static int phase_idx = 3; 
	
	/**
	 * @param paramBlock
	 */
	public CosineFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/**
	 * @return the initial phase
	 */
	public double getInitialPhase() {
		
		return paramBlock.getDoubleParameter(phase_idx);
	}
	
	/**
	 * @param phase the initial phase to set
	 */
	public void setInitialPhase(double phase) {
		
		paramBlock.set( phase, phase_idx );

	}
	
	public boolean hasInitialPhase() {
		
		return true; 
	}
	
	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] cosine = new double[ dimension ];		
		
		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
		double initialPhase = getInitialPhase();
		
		double twoPiOverPeriod = 2 * Math.PI / width;					
		for ( int i = 0; i < dimension; ++i )  { 
			
			cosine[i] = amplitude * Math.cos( twoPiOverPeriod * (indices[i] - center) + Math.toRadians(initialPhase) );  
			
		}	
		
		return cosine;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
		
		//parameter classes with exponent
		Class[] param_classes = { Double.class, Double.class, Double.class, Double.class };
		model.setParamClasses( param_classes );
		
		String[] param_names = 
			{ "A"+'\u2080', "x"+'\u2080', "X"+'\u2080', '\u03D5'+""+'\u2080'+" ("+'\u00B0'+")" }; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1), new Double( 0 ), new Double( 32 ), new Double( 0 )}; 
		model.setParamDefaults( param_defaults );
		
	//	model.setLargeIcon("/functionIcons/Cosine1DLarge.png");
	//	model.setSmallIcon("/functionIcons/Cosine1DSmall.png");
		model.setName("Cosine");
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel centerSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 32.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel phaseSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 90 );
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, centerSpinnerModel, 
				widthSpinnerModel, phaseSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/cos.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
	
		return amplitudeMultiplierString() + "cos[2\u03C0("+formattedParamString(variables[0])+")"+ StringConversions.PhaseString(getInitialPhase())+"]"; 
	}
}
