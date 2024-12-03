package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;
import signals.operation.ArrayUtilities;

public class RectApertureFunctionTerm2D extends AnalyticFunctionTerm2D {

	public RectApertureFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RectApertureFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}
	
	@SuppressWarnings("unchecked")
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
		
		model.setName("Rectangular Aperture");
		
		Class[] param_classes = { Double.class, Double.class, Double.class, Double.class, Double.class };
		model.setParamClasses( param_classes );
		
		String[] param_names = { "Amplitude", "X-Center", "Y-Center", "Width", "Height" }; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double( 1.0 ), new Double( 0.0 ), new Double( 0.0 ), 
				new Double( 64.0 ), new Double( 64.0 ) };
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel xCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel yCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 64.0, 0, 100000.0, 1.0);
		SpinnerNumberModel heightSpinnerModel = new SpinnerNumberModel( 64.0, 0, 100000.0, 1.0);
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, xCenterSpinnerModel, yCenterSpinnerModel,
								widthSpinnerModel, heightSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		model.setDocPath("/functiondoc/RectAperture.html");
	}
	
	@Override
	public double[] create(int x_dimension, int y_dimension,
			boolean zeroCentered) {
		
		double[][] array = new double[y_dimension][x_dimension]; 
		
		double xCenter = getCenterX(); 
		double yCenter = getCenterY(); 
		double width = getWidth(); 
		double amplitude = getAmplitude(); 
		double height = getHeight(); 
		
		Apertures.setRectangularAperture(array, x_dimension, y_dimension, zeroCentered, 
				xCenter, yCenter, width, height, amplitude);
		
		return ArrayUtilities.flatten(array);
	}

	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Rectangle";
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
