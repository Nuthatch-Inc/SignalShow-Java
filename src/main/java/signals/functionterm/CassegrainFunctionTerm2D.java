package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.Constants;
import signals.core.DataGeneratorTypeModel;
import signals.operation.ArrayUtilities;

public class CassegrainFunctionTerm2D extends AnalyticFunctionTerm2D {

	public CassegrainFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CassegrainFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}
	
	@SuppressWarnings("unchecked")
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
		
//		model.setLargeIcon("/functionIcons/Cassegrain2DLarge.png");
//		model.setSmallIcon("/functionIcons/Cassegrain2DSmall.png");
		model.setName("Cassegrain");
		
		//define styleCode
		model.setStyleCode( Constants.StyleCode.IMAGE );
		
		Class[] param_classes = { Double.class, Double.class, Double.class, Double.class, Double.class };
		model.setParamClasses( param_classes );
		
		String[] param_names = { "Amplitude", "X-Center", "Y-Center", "Outer Radius", "Inner Radius" }; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double( 1.0 ), new Double( 0.0 ), new Double( 0.0 ), 
				new Double( 64.0 ), new Double( 32.0 ) };
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel xCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel yCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 64.0, 0, 100000.0, 1.0);
		SpinnerNumberModel heightSpinnerModel = new SpinnerNumberModel( 32.0, 0, 100000.0, 1.0);
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, xCenterSpinnerModel, yCenterSpinnerModel,
								widthSpinnerModel, heightSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		model.setDocPath("/functiondoc/cassegrain.html");
	}
	
	@Override
	public double[] create(int x_dimension, int y_dimension,
			boolean zeroCentered) {
		
		double[][] array = new double[y_dimension][x_dimension]; 
		
		double xCenter = getCenterX(); 
		double yCenter = getCenterY(); 
		double radius = getWidth(); 
		double amplitude = getAmplitude(); 
		double innerRadius = getHeight(); 
		
		Apertures.setCassegrainAperture(array, x_dimension, y_dimension, zeroCentered, 
				xCenter, yCenter, radius, innerRadius, amplitude); 
		
		return ArrayUtilities.flatten(array);
	}

	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Cassegrain";
	}
	
	@Override
	public void setWidth(double width) {
		super.setWidth(width);
		paramBlock.set( width/2, y_width_idx );	
	}

	@Override
	public boolean hasHeight() {
		return true;
	}

	@Override
	public boolean hasWidth() {
		return true;
	}

}
