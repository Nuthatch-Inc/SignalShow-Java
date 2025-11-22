package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;
import signals.gui.operation.CheckBoxOptionsPanel;
import signals.gui.operation.OperationOptionsPanel;
import signals.gui.operation.ParameterOptionsPanel;

public class TranslateOp1D extends UnaryOperation implements ParametricOperation, BooleanOperation {
	
	boolean wrapAround; 
	
	public TranslateOp1D() {
		super();
		wrapAround = true; 
	}


	public TranslateOp1D(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		wrapAround = true; 
	}

	protected int shiftAmount; 

	public TranslateOp1D(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		shiftAmount = 0; 
	}


	@Override
	public OperationOptionsPanel getOptionsInterface() {
		OperationOptionsPanel op = new OperationOptionsPanel(this); 
		op.add( new ParameterOptionsPanel( this )); 
		op.add( new CheckBoxOptionsPanel(this, "wrap around")); 
		return op;
	}

	public void incrementShiftAmount() {
		
		shiftAmount++; 
	}
	
	public void decrementShiftAmount() { 
		
		shiftAmount--;
	}

	public int getShiftAmount() {
		return shiftAmount;
	}


	public void setShiftAmount(int shiftAmount) {
		this.shiftAmount = shiftAmount;
	}


	public boolean isWrapAround() {
		return wrapAround;
	}


	public void setWrapAround(boolean wrapAround) {
		this.wrapAround = wrapAround;
	}


	@Override
	public Function create(Function input) {
		
		double[] real = wrapAround ? ArrayUtilities.translate1D(input.getReal(), shiftAmount) : 
			ArrayUtilities.translate1DLinear(input.getReal(), shiftAmount); 
		double[] imag = wrapAround ? ArrayUtilities.translate1D(input.getImaginary(), shiftAmount) :
			ArrayUtilities.translate1DLinear(input.getImaginary(), shiftAmount); 
		String name = "translate("+input.getCompactDescriptor()+")";
		return FunctionFactory.createFunction1D( real, imag, input.isZeroCentered(), name ); 
	}
	
	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/TranslateLarge.png");
//		model.setSmallIcon("/operationIcons/TranslateSmall.png");
		model.setName("Translate");
		
		model.setDocPath("/operationdoc/translate.html");
	}

	@Override
	public String getOpIconPath() {
		return "/operationIcons/TranslateOp.png"; 
	}
	
	public int getNumParams() {
		return 1;
	}

	public double getValue(int index) {
			
		return shiftAmount; 
	}

	public void setValue(int index, double value) {
		
		shiftAmount = (int)value; 
		
	}

	public String getParamDescriptor() {
		return "Positive: right. Negative: left";
	}

	public String getParamName( int index ) {
	
		return "amount";
	}
	
	public boolean isSelected() {

		return wrapAround;
	}

	public void setSelected(boolean tf) {
		
		wrapAround = tf; 
		
	}


}
