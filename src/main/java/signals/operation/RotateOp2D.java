package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.SpinnerNumberModel;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;
import signals.gui.operation.InterpolationOptionsPanel;
import signals.gui.operation.OperationOptionsPanel;
import signals.gui.operation.ParameterOptionsPanel;
import signals.gui.plot.ImageDisplayMath;

/*
 * Parameter block contains the rotation angle in degrees
 */
public class RotateOp2D extends UnaryOperation implements InterpolationUser, ParametricOperation {
	
	int interpolation; 
	public double angle; 
	
	public RotateOp2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RotateOp2D(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see signals.core.Operation#getOptionsInterface()
	 */
	@Override
	public OperationOptionsPanel getOptionsInterface() {
		
		OperationOptionsPanel compoundPanel = new OperationOptionsPanel( this ); 
		compoundPanel.add( new InterpolationOptionsPanel( this )); 
		compoundPanel.add( new ParameterOptionsPanel(this)); 
		return compoundPanel;
	}

	public RotateOp2D(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		interpolation = Interpolation.INTERP_BILINEAR;
		angle = 90; 
	}

	/**
	 * @return the interpolation
	 */
	public int getInterpolation() {
		return interpolation;
	}

	/**
	 * @param interpolation the interpolation to set
	 */
	public void setInterpolation(int interpolation) {
		this.interpolation = interpolation;
	}
	
	@Override
	public Function create(Function input) {
		
		Function2D f2D = (Function2D)input; 
		
 	    float centerX = f2D.getDimensionX()/2f; 
 	    float centerY = f2D.getDimensionY()/2f;  
 	    
 	   double[] inputReal = input.getReal(); 
 	   double[] inputImag = input.getImaginary(); 
 	    
 	    //rotate the real part
 	    ParameterBlock pb = new ParameterBlock();
		pb.addSource( ImageDisplayMath.data2Image(inputReal, f2D.getDimensionX(), f2D.getDimensionY()));  
		pb.add(centerX);  
		pb.add(centerY);  
		pb.add((float)Math.toRadians(angle));  
		pb.add(InterpolationFactory.create(interpolation));  
		PlanarImage rotatedImageReal = JAI.create("rotate", pb);
		
		//rotate the imaginary part
		pb.setSource(ImageDisplayMath.data2Image(inputImag, 
				f2D.getDimensionX(), f2D.getDimensionY()), 0); 
		PlanarImage rotatedImageImag = JAI.create("rotate", pb);
		
		// Create a ParameterBlock with information for the cropping.
	    ParameterBlock pbC = new ParameterBlock();
	    pbC.addSource(rotatedImageReal);
	    pbC.add(0.0f);
	    pbC.add(0.0f);
	    pbC.add((float)f2D.getDimensionX());
	    pbC.add((float)f2D.getDimensionY());
	    // Create the output image by cropping the input image.
	    rotatedImageReal = JAI.create("crop",pbC,null);
	    
	    pbC.setSource(rotatedImageImag, 0); 
	    rotatedImageImag = JAI.create("crop",pbC,null);
		
		double[] realData = ImageDisplayMath.image2Data(rotatedImageReal);
		double[] imagData = ImageDisplayMath.image2Data(rotatedImageImag);
		
		return FunctionFactory.createFunction2D(realData, imagData, input.isZeroCentered(), 
				"Rot{"+input.getCompactDescriptor()+"}", f2D.getDimensionX(), f2D.getDimensionY()); 
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/RotateLarge.png");
//		model.setSmallIcon("/operationIcons/RotateSmall.png");
		model.setName("Rotate");
		
		Class[] param_classes = { Double.class};
		model.setParamClasses( param_classes );
		
		//default paramNames
		String[] param_names = { "Angle of Rotation in Degrees"}; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(90) }; 
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel angleSpinnerModel = new SpinnerNumberModel( 90, -100000.0, 100000.0, 1);
		
		SpinnerNumberModel[] spinnerModels = { angleSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/operationdoc/rotate.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/RotateOp.png"; 
	}

	public int getNumParams() {
	
		return 1;
	}

	public String getParamDescriptor() {
		return "Angle of Rotation (deg)";
	}

	public String getParamName(int index) {
	
		return "Angle";
	}

	public double getValue(int index) {

		return angle;
	}

	public void setValue(int index, double value) {
		
		angle = value; 
	}

}
