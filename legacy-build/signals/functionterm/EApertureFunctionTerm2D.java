package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;
import signals.operation.ArrayUtilities;

public class EApertureFunctionTerm2D extends AnalyticFunctionTerm2D {

	public EApertureFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EApertureFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}
	
	@SuppressWarnings("unchecked")
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
		
		model.setName("E");
		
		Class[] param_classes = { Double.class, Double.class, Double.class, Double.class, Double.class };
		model.setParamClasses( param_classes );
		
		String[] param_names = { "Amplitude", "X-Center", "Y-Center", "Width", "Height" }; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double( 1.0 ), new Double( 0.0 ), new Double( 0.0 ), 
				new Double( 30.0 ), new Double( 50.0 ) };
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel xCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel yCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 30.0, 0, 100000.0, 1.0);
		SpinnerNumberModel heightSpinnerModel = new SpinnerNumberModel( 50.0, 0, 100000.0, 1.0);
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, xCenterSpinnerModel, yCenterSpinnerModel,
								widthSpinnerModel, heightSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		model.setDocPath("/functiondoc/E.html");
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
		
		Apertures.setRectangularAperture(array, x_dimension, y_dimension, zeroCentered, 
				xCenter + (2/5.)*width, yCenter, (3/5.)*width, (3/5.)*height, 0);
		
		Apertures.setRectangularAperture(array, x_dimension, y_dimension, zeroCentered, 
				xCenter, yCenter, (1/2.)*width, (1/5.)*height, amplitude); 
		
		return ArrayUtilities.flatten(array);
	}

	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "E";
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
