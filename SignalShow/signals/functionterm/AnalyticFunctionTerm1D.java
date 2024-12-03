package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.text.NumberFormat;

import javax.swing.SpinnerNumberModel;

import signals.core.Constants;
import signals.core.Core;
import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;
import signals.core.FunctionTerm1D;

/**
 * Abstract base class for analytic 1D special functions
 * @author Juliet
 *
 */
public abstract class AnalyticFunctionTerm1D extends FunctionTerm1D 
implements AnalyticFunctionTerm {

	public AnalyticFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AnalyticFunctionTerm1D(DataGenerator datagenerator,
			ParameterBlock paramBlock) {
		super(datagenerator, paramBlock);
		// TODO Auto-generated constructor stub
	}

	/**
	 * if the function has an amplitude parameter that is not 1, returns "A x " 
	 */
	public String amplitudeMultiplierString() {
		if( !hasAmplitude() || getAmplitude() == 1) return ""; 
		NumberFormat formatter = Core.getDisplayOptions().getFormat();
		return formatter.format(getAmplitude())+" \u00D7 ";
	}

	/**
	 * returns "(x - x0)/b" or "x/b" or "x-x0" or "x" (if x0 is pos). If x0 is neg, returns (x + x0) 
	 */
	public String formattedParamString( String param ) {

		NumberFormat formatter = Core.getDisplayOptions().getFormat();

		String shifted = null; 
		if( !hasCenter() || getCenter() == 0 ) shifted = param; 
		else if(getCenter() > 0) shifted = param + " - " + formatter.format( getCenter() ); 
		else shifted = param + " + " + formatter.format( -1*getCenter() );

		if( !hasWidth() || getWidth() == 1 ) return shifted; //return "x" or "x - x0" 
		if( !hasCenter() || getCenter() == 0 ) return param + "/" + formatter.format(getWidth()); //return "x / b" 
		return "(" + shifted + ") / " + formatter.format(getWidth()); //return "(x - x0) / b" 

	}

	/**
	 * returns "(x + x0)/b" or "x/b" or "x+x0" or "x" (if x0 is pos). If x0 is neg, returns (x - x0) 
	 */
	public String formattedParamStringNeg( String param ) {

		NumberFormat formatter = Core.getDisplayOptions().getFormat();

		String shifted = null; 
		if( !hasCenter() || getCenter() == 0 ) shifted = param; 
		else if(getCenter() > 0) shifted = param + " + " + formatter.format( getCenter() ); 
		else shifted = param + " - " + formatter.format( -1*getCenter() );

		if( !hasWidth() || getWidth() == 1 ) return shifted; //return "x" or "x + x0" 
		if( !hasCenter() || getCenter() == 0 ) return param + "/" + formatter.format(getWidth()); //return "x / b" 
		return "(" + shifted + ") / " + formatter.format(getWidth()); //return "(x + x0) / b" 

	}

	public int amplitude_idx, center_idx, width_idx;

	//original or base value for the width parameter of this function
	protected double baseWidth; 

	/**
	 * Constructor that accepts a ParameterBlock
	 */
	public AnalyticFunctionTerm1D( ParameterBlock paramBlock ) {

		super( paramBlock );
		amplitude_idx = 0;
		center_idx = 1;
		width_idx = 2;
	}

	@SuppressWarnings("unchecked")
	public void initTypeModel( DataGeneratorTypeModel model ) {

		//define styleCode
		model.setStyleCode( Constants.StyleCode.ANALYTIC_FUNCTION );

		//default paramClasses
		Class[] param_classes = { Double.class, Double.class, Double.class };
		model.setParamClasses( param_classes );

		//default paramNames
		String[] param_names = { "A"+'\u2080', "x"+'\u2080', "b" }; 
		model.setParamNames( param_names );

		Object[] param_defaults = { new Double(1), new Double( 0 ), new Double( 32 ) }; 
		model.setParamDefaults( param_defaults );

		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel centerSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 32, -100000.0, 100000.0, 1.0);

		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, centerSpinnerModel, widthSpinnerModel };
		model.setSpinnerModels(spinnerModels);

		model.setDocPath("/functiondoc/default.html");
	}

	/**
	 * @see signals.core.FunctionTerm1D#setWidthScale(double)
	 */
	@Override
	public void setWidthScale(double scale) {

		setWidth( scale * baseWidth ); 

	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm#isHalfWidthDefined()
	 */
	public boolean isHalfWidthDefined() {
		return false;
	}


	/**
	 * @return the amplitude
	 */
	public double getAmplitude() {

		return paramBlock.getDoubleParameter(amplitude_idx);
	}

	/**
	 * @param amplitude the amplitude to set
	 */
	public void setAmplitude(double amplitude) {

		paramBlock.set( amplitude, amplitude_idx );

	}

	public boolean hasAmplitude() {

		return true; 
	}

	/**
	 * @return the center
	 */
	public double getCenter() {

		return paramBlock.getDoubleParameter(center_idx);
	}

	public boolean hasCenter() {

		return true; 
	}

	/**
	 * @param center the center to set
	 */
	public void setCenter(double center) {

		paramBlock.set( center, center_idx );

	}

	/**
	 * @return the width
	 */
	public double getWidth() {

		return paramBlock.getDoubleParameter(width_idx);
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {

		paramBlock.set( width, width_idx );

	}

	public boolean hasWidth() {

		return true;
	}

	public boolean hasInitialPhase() {

		return false; 
	}

	public double getInitialPhase() {
		return 0;
	}

	public void setInitialPhase( double initialPhase ) {} 

	public boolean hasOrder() {
		
		return false;
	}
	
	public int getOrder() {
		return 0; 
	}
	
	public void setOrder( int order ) {}  
	
	/*
	 * (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(int)
	 */
	public double[] create(int dimension, boolean zeroCentered ) {

		//create indices based on dimension and first index
		int firstIndex = 0; 
		if( zeroCentered ) {

			firstIndex = -dimension/2;
		}

		double[] indices = new double[dimension];
		for( int i = 0; i < dimension; ++i ) {

			indices[i] = firstIndex + i;
		}

		return create(indices);
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm#getAmplitudeIndex()
	 */
	public int getAmplitudeIndex() {
		return amplitude_idx;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm#getCenterIndex()
	 */
	public int getCenterIndex() {
		return center_idx;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm#getWidthIndex()
	 */
	public int getWidthIndex() {
		return width_idx;
	}

}
