/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Rectangle Function
 */
public class DoubleSlitFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public DoubleSlitFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static int slitwidth_idx = 3; 
	
	/**
	 * @param paramBlock
	 */
	public DoubleSlitFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#isHalfWidthDefined()
	 */
	@Override
	public boolean isHalfWidthDefined() {
		return true;
	}

	/**
	 * @return the slit width
	 */
	public double getSlitWidth() {
		
		return paramBlock.getDoubleParameter(slitwidth_idx);
	}
	
	/**
	 * @param phase the slit width to set
	 */
	public void setSlitWidth(double slitWidth) {
		
		paramBlock.set( slitWidth, slitwidth_idx );

	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 	
		
		double width = getWidth();
		double center = getCenter();
		double amplitude = getAmplitude();
		double slitWidth = getSlitWidth() / 2.0;
		
		double[] rect = new double[ dimension ];	
									
		for ( int i = 0; i < dimension; i++ )  {
			
			double argument =  Math.abs( indices[i] - center - width );
			
			if( argument <= slitWidth ) {
				
				rect[i] = amplitude; 
			}
			
			argument =  Math.abs( indices[i] - center + width );
			
			if( argument <= slitWidth ) {
				
				rect[i] = amplitude; 
			}
		}	
		
		return rect;
		
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
		
		//default paramNames
		String[] param_names = { "A"+'\u2080', "x"+'\u2080', "b", "w" };  
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1), new Double( 0 ), new Double( 32 ), new Double( 1 ) }; 
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel centerSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 32.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel slitWidthSpinnerModel = new SpinnerNumberModel( 1.0, 0.0, 100000.0, 1.0 );
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, centerSpinnerModel, 
				widthSpinnerModel, slitWidthSpinnerModel };
		model.setSpinnerModels(spinnerModels);
	
//		model.setLargeIcon("/functionIcons/DoubleSlit1DLarge.png");
//		model.setSmallIcon("/functionIcons/DoubleSlit1DSmall.png");
		model.setName("Double Slit");
		
		model.setDocPath("/functiondoc/doubleslit.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "(RECT["+formattedParamStringNeg(variables[0])+"] + RECT["+formattedParamString(variables[0])+"])"; 
	}
}
