package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.Constants;
import signals.core.DataGeneratorTypeModel;
import signals.operation.ArrayUtilities;

public class CassegrainMultiArmFunctionTerm2D extends AnalyticFunctionTerm2D {
	
	public CassegrainMultiArmFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	int spacing_idx, numArms_idx, numApertures_idx, innerRadius_idx; 

	public CassegrainMultiArmFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
		innerRadius_idx = 4; 
		spacing_idx = 5; 
		numArms_idx = 6; 
		numApertures_idx = 7; 
	}
	
	@SuppressWarnings("unchecked")
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
		
//		model.setLargeIcon("/functionIcons/CassegrainMultiarm2DLarge.png");
//		model.setSmallIcon("/functionIcons/CassegrainMultiarm2DSmall.png");
		model.setName("Cassegrain Multiarm");
		
		//define styleCode
		model.setStyleCode( Constants.StyleCode.IMAGE );
		
		Class[] param_classes = { Double.class, Double.class, Double.class, Double.class,
									Double.class, Double.class, Integer.class, Integer.class};
		model.setParamClasses( param_classes );
		
		String[] param_names = { "Amplitude", "X-Center", "Y-Center", "Outer Radius", 
								"Inner Radius", "Spacing", "Number Arms", "Number Apertures"}; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double( 1.0 ), new Double( 0.0 ), new Double( 0.0 ), 
				new Double( 8.0 ), new Double(4.0), new Double( 32 ), new Integer(3), new Integer(3) };
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel xCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel yCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 8, 0, 100000.0, 1.0);
		SpinnerNumberModel innerRadiusSpinnerModel = new SpinnerNumberModel( 4, 0, 100000.0, 1.0);
		SpinnerNumberModel spacingSpinnerModel = new SpinnerNumberModel( 16.0, 0, 100000.0, 1.0);
		SpinnerNumberModel numArmsSpinnerModel = new SpinnerNumberModel( new Integer(3), 
				new Integer(0), new Integer(100000), new Integer(1) );
		SpinnerNumberModel numAperturesSpinnerModel = new SpinnerNumberModel( new Integer(3), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, xCenterSpinnerModel, yCenterSpinnerModel,
								widthSpinnerModel, innerRadiusSpinnerModel,
									spacingSpinnerModel, numArmsSpinnerModel, numAperturesSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/cassegrainmultiarm.html");
	}
	
	public double getInnerRadius() {
		
		return paramBlock.getDoubleParameter(innerRadius_idx);
	}
	
	
	public double getSpacing() {
		
		return paramBlock.getDoubleParameter(spacing_idx);
	}
	
	public int getNumArms() {
		
		return paramBlock.getIntParameter(numArms_idx);
	}
	
	public int getNumApertures() {
		
		return paramBlock.getIntParameter(numApertures_idx);
	}
	
	@Override
	public double[] create(int x_dimension, int y_dimension,
			boolean zeroCentered) {
		
		double[][] array = new double[y_dimension][x_dimension]; 
		
		double xCenter = getCenterX(); 
		double yCenter = getCenterY(); 
		double radius = getWidth(); 
		double amplitude = getAmplitude(); 
		double innerRadius = getInnerRadius(); 
		double spacing = getSpacing(); 
		int numArms = getNumArms(); 
		int numApertures = getNumApertures(); 
		
		double angle_increment = 2.0 * Math.PI / numArms; 
		
		//for each arm 
		for( int arm = 0; arm < numArms; arm ++ ) {
			
			double theta = angle_increment * arm; 
			
			double cos = Math.cos(theta); 
			double sin = Math.sin(theta); 
			
			//for each aperture on the arm 
			for( int aperture = 1; aperture <= numApertures; aperture++ ) {
				
				Apertures.setCassegrainAperture(array, x_dimension, y_dimension, zeroCentered, 
					 xCenter - spacing*aperture*sin, yCenter - spacing*aperture*cos, radius, innerRadius, amplitude); 
				
			}
			
		}
		
		return ArrayUtilities.flatten(array);
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Cassegrain Multiarm";
	}

	@Override
	public boolean hasHeight() {
		return false;
	}

	@Override
	public boolean hasWidth() {
		return false;
	}

}
