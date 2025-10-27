/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.text.NumberFormat;
import java.util.Vector;

import signals.core.Core;
import signals.core.DataGeneratorTypeModel;
import signals.core.FunctionTerm2D;

/**
 * @author Juliet
 */
public abstract class NoiseFunctionTerm2D extends FunctionTerm2D 
						implements AnalyticFunctionTerm { 


	public NoiseFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	NoiseFunctionTerm1D noise1D;

	public NoiseFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
		if( noise1D == null ) noise1D = getNoise1DInstance();
	}
	
	public NoiseFunctionTerm1D getFunctionTerm1D() {
		
		return noise1D;
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
	 * returns "(x - x0)/b" or "x/b" or "x-x0" or "x" 
	 */
	public String formattedParamString( String param ) {
		
		NumberFormat formatter = Core.getDisplayOptions().getFormat();
		
		String shifted = null; 
		if( !hasCenter() || getCenter() == 0 ) shifted = param; 
		else shifted = param + " - " + formatter.format( getCenter() ); 
		
		if( !hasWidth() || getWidth() == 1 ) return shifted; //return "x" or "x - x0" 
		if( !hasCenter() || getCenter() == 0 ) return param + " / " + formatter.format(getWidth()); //return "x / b" 
		return "(" + shifted + ") / " + formatter.format(getWidth()); //return "(x - x0) / b" 

	}
	
	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm#isHalfWidthDefined()
	 */
	public boolean isHalfWidthDefined() {
		return false;
	}

	/* (non-Javadoc)
	 * @see signals.core.DataGenerator#getParamBlock()
	 */
	@Override
	public ParameterBlock getParamBlock() {
		return noise1D.getParamBlock();
	}

	/* (non-Javadoc)
	 * @see signals.core.DataGenerator#getParamBlockClone()
	 */
	@Override
	public ParameterBlock getParamBlockClone() {
		return noise1D.getParamBlockClone();
	}

	/* (non-Javadoc)
	 * @see signals.core.DataGenerator#getParameters()
	 */
	@Override
	public Vector<Object> getParameters() {
		return noise1D.getParameters();
	}

	/* (non-Javadoc)
	 * @see signals.core.DataGenerator#getTypeModel()
	 */
	@Override
	public DataGeneratorTypeModel getTypeModel() {
		// TODO Auto-generated method stub
		return super.getTypeModel();
	}

	/* (non-Javadoc)
	 * @see signals.core.DataGenerator#setParamBlock(java.awt.image.renderable.ParameterBlock)
	 */
	@Override
	public void setParamBlock(ParameterBlock paramBlock) {
		// TODO Auto-generated method stub
		super.setParamBlock(paramBlock);
	}

	/* (non-Javadoc)
	 * @see signals.core.DataGenerator#getParameter(int)
	 */
	@Override
	public Object getParameter(int index) {
		return noise1D.getParameter(index);
	}

	/* (non-Javadoc)
	 * @see signals.core.DataGenerator#setParameter(java.lang.Object, int)
	 */
	@Override
	public void setParameter(Object value, int index) {
		noise1D.setParameter(value, index);
	}
	
	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm#getAmplitudeIndex()
	 */
	public int getAmplitudeIndex() {
		return noise1D.getAmplitudeIndex();
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm#getCenterIndex()
	 */
	public int getCenterIndex() {
		return noise1D.getCenterIndex();
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm#getWidthIndex()
	 */
	public int getWidthIndex() {
		return noise1D.getWidthIndex();
	}
	
	public abstract NoiseFunctionTerm1D getNoise1DInstance();
	
	/**
	 * @return the amplitude
	 */
	public double getAmplitude() {
		
		return noise1D.getAmplitude();
	}

	/**
	 * @param amplitude the amplitude to set
	 */
	public void setAmplitude(double amplitude) {
		
		noise1D.setAmplitude(amplitude);

	}
	
	public boolean hasAmplitude() {
		
		return noise1D.hasAmplitude(); 
	}
	
	/**
	 * @return the center
	 */
	public double getCenter() {
		
		return noise1D.getCenter();
	}
	
	public boolean hasCenter() {
		
		return noise1D.hasCenter();
	}

	/**
	 * @param center the center to set
	 */
	public void setCenter(double center) {
		
		noise1D.setCenter(center);
	}
	
	/**
	 * @return the width
	 */
	public double getWidth() {
		
		return noise1D.getWidth();
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		
		noise1D.setWidth(width); 

	}
	
	public boolean hasWidth() {
		
		return noise1D.hasWidth();
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
		if( noise1D == null ) noise1D = getNoise1DInstance();

		DataGeneratorTypeModel model1D = noise1D.getTypeModel(); 
		
		model.setName(model1D.getName());
		model.setParamNames( model1D.getParamNames() );
		model.setParamDefaults( model1D.getParamDefaults() ); 
		model.setParamClasses( model1D.getParamClasses() );
		model.setSpinnerModels( model1D.getSpinnerModels() );
		model.setDocPath( model1D.getDocPath() );
		
	}

	@Override
	public double[] create(int x_dimension, int y_dimension, boolean zeroCentered) {
		return noise1D.create(x_dimension*y_dimension);
	}
	
	@Override
	public String getEquation(String[] variables) {
		return noise1D.getEquation(variables);
	}
}
