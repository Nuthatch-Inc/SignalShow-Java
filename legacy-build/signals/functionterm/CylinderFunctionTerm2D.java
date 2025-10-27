package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.Constants;
import signals.core.DataGeneratorTypeModel;
import signals.operation.ArrayUtilities;

public class CylinderFunctionTerm2D extends AnalyticFunctionTerm2D {

	public CylinderFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CylinderFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}
	
	@SuppressWarnings("unchecked")
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
		
//		model.setLargeIcon("/functionIcons/Cylinder2DLarge.png");
//		model.setSmallIcon("/functionIcons/Cylinder2DSmall.png");
		model.setName("Cylinder");
		
		//define styleCode
		model.setStyleCode( Constants.StyleCode.IMAGE );
		
		Class[] param_classes = { Double.class, Double.class, Double.class, Double.class };
		model.setParamClasses( param_classes );
		
		String[] param_names = { "Amplitude", "X-Center", "Y-Center", "Radius" }; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double( 1.0 ), new Double( 0.0 ), new Double( 0.0 ), 
				new Double( 32.0 ) };
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel xCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel yCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 64.0, 0, 100000.0, 1.0);
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, xCenterSpinnerModel, yCenterSpinnerModel,
								widthSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/cylinder.html");
	}
	
	@Override
	public double[] create(int x_dimension, int y_dimension,
			boolean zeroCentered) {
		
		double[][] array = new double[y_dimension][x_dimension]; 
		
		double xCenter = getCenterX(); 
		double yCenter = getCenterY(); 
		double radius = getWidth(); 
		double amplitude = getAmplitude(); 
	
		Apertures.setFilledAperture(array, x_dimension, y_dimension, zeroCentered, 
					xCenter, yCenter, radius, amplitude); 
		
		return ArrayUtilities.flatten(array);
	}

	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Cylinder";
	}

	@Override
	public boolean hasHeight() {
		return false;
	}

	@Override
	public boolean hasWidth() {
		return true;
	}

}
