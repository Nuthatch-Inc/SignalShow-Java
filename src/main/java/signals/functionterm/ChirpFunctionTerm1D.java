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
 * Represents a 1D Chirp Function
 */
public class ChirpFunctionTerm1D extends AnalyticFunctionTerm1D { 

	public ChirpFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static int phase_idx = 3; 
	public static int order_idx = 4;

	/**
	 * @param paramBlock
	 */
	public ChirpFunctionTerm1D(ParameterBlock paramBlock) {

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

	/**
	 * @return the Order
	 */
	public int getOrder() {

		return paramBlock.getIntParameter(order_idx);
	}

	/**
	 * @param Order the Order to set
	 */
	public void setOrder(int Order) {

		paramBlock.set( Order, order_idx );

	}

	public boolean hasOrder() {
		return true; 
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {

		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
		int Order = getOrder();
		double initialPhase = getInitialPhase(); 

		int dimension = indices.length; 
		double[] chirp = new double[ dimension ];	

		//get the value of the center in math coordinates
		double piOverBPow = Math.PI  / ( Math.pow( width, Order ) );

		for ( int i = 0; i < dimension; i++ )  { 

			chirp[i] = amplitude * Math.cos( piOverBPow * Math.pow( ( indices[i] - center ), Order ) + Math.toRadians(initialPhase) ); 

		}	

		return chirp;

	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);

		//parameter classes with Order
		Class[] param_classes = { Double.class, Double.class, Double.class, Double.class, Integer.class };
		model.setParamClasses( param_classes );

		//default paramNames
		String[] param_names = 
		{ "A"+'\u2080', "x"+'\u2080', "\u03B1", '\u03D5'+""+'\u2080'+" ("+'\u00B0'+")", "n" }; 
		model.setParamNames( param_names );

		Object[] param_defaults = { new Double(1), new Double( 0 ), new Double( 32 ), new Double( 0 ), new Integer( 2 ) }; 
		model.setParamDefaults( param_defaults );

		//		model.setLargeIcon("/functionIcons/Chirp1DLarge.png");
		//		model.setSmallIcon("/functionIcons/Chirp1DSmall.png");
		model.setName("Chirp");

		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel centerSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 32.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel phaseSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 90 );
		SpinnerNumberModel OrderSpinnerModel = new SpinnerNumberModel( new Integer(2), 
				new Integer(-100000), new Integer(100000), new Integer(1) );

		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, centerSpinnerModel, 
				widthSpinnerModel, phaseSpinnerModel, OrderSpinnerModel };
		model.setSpinnerModels(spinnerModels);

		model.setDocPath("/functiondoc/chirp.html");
	}

	@Override
	public String getEquation(String[] variables) {

		return amplitudeMultiplierString() + "cos[\u03C0("+formattedParamString(variables[0])+")^"+getOrder()+ StringConversions.PhaseString(getInitialPhase())+ "]"; 
	}
}
