package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;
import signals.gui.operation.DirectionOptionsPanel;
import signals.gui.operation.OperationOptionsPanel;

public class DerivativeOp2D extends UnaryOperation implements Directional2DOperation {
	
	public DerivativeOp2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DerivativeOp2D(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}


	boolean x; //true if the derivative should be done in the x direction

	public DerivativeOp2D(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		x = true; 
	}
	
	public Function create( Function input ) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String prefix = x ? "d/dx(" : "d/dy(";
		String name = prefix + input.getCompactDescriptor() + ")"; 
		
		int x_dimension = ((Function2D)input).getDimensionX();
		int y_dimension = ((Function2D)input).getDimensionY();
	
		double[] realOut = CalculusOperations.derivative2D(real, x_dimension, y_dimension, x );
		double[] imagOut = CalculusOperations.derivative2D(imag, x_dimension, y_dimension, x );

		return FunctionFactory.createFunction2D( realOut, imagOut, zeroCentered, name, 
				x_dimension, y_dimension ); 
			
	}

	@Override
	public OperationOptionsPanel getOptionsInterface() {
		return new DirectionOptionsPanel(this);
	}

	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/DerivativeLarge.png");
//		model.setSmallIcon("/operationIcons/DerivativeSmall.png");
		model.setName("Derivative");
		
		model.setDocPath("/operationdoc/derivative.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/DerivativeOp.png"; 
	}


	public boolean isXDirection() {
		
		return x;
	}


	public void setXDirection(boolean x) {
		this.x = x;
	}

	
}
