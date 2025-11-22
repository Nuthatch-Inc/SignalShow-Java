package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;
import signals.gui.operation.CheckBoxOptionsPanel;
import signals.gui.operation.OperationOptionsPanel;
import signals.gui.operation.ParameterOptionsPanel;

public class TranslateOp2D extends UnaryOperation implements ParametricOperation, BooleanOperation {

	boolean wrapAround; 
	protected int shiftAmountX, shiftAmountY; 

	public TranslateOp2D() {
		super();
	}

	public TranslateOp2D(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
	}

	public TranslateOp2D(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		shiftAmountX = 0; 
		shiftAmountY = 0; 
	}

	public boolean isWrapAround() {
		return wrapAround;
	}

	public void setWrapAround(boolean wrapAround) {
		this.wrapAround = wrapAround;
	}

	@Override
	public OperationOptionsPanel getOptionsInterface() {
		
		OperationOptionsPanel op = new OperationOptionsPanel(this); 
		op.add( new ParameterOptionsPanel( this )); 
		op.add( new CheckBoxOptionsPanel(this, "wrap around")); 
		return op;
	}


	@Override
	public Function create(Function input) {

		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 

		boolean zeroCentered = input.isZeroCentered();

		String name = "translate(" + input.getCompactDescriptor() + ")"; 

		int x_dimension = ((Function2D)input).getDimensionX();
		int y_dimension = ((Function2D)input).getDimensionY();

		double[] realOut = wrapAround ? ArrayUtilities.translate2D(real, x_dimension, y_dimension, shiftAmountX, shiftAmountY) :
			ArrayUtilities.translate2DLinear(real, x_dimension, y_dimension, shiftAmountX, shiftAmountY);
		double[] imagOut = wrapAround ? ArrayUtilities.translate2D(imag, x_dimension, y_dimension, shiftAmountX, shiftAmountY) : 
			ArrayUtilities.translate2DLinear(imag, x_dimension, y_dimension, shiftAmountX, shiftAmountY);

		return FunctionFactory.createFunction2D( realOut, imagOut, zeroCentered, name, 
				x_dimension, y_dimension ); 
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
		return 2;
	}

	public double getValue(int index) {

		return (index == 0 ) ? shiftAmountX : shiftAmountY; 
	}

	public void setValue(int index, double value) {


		if( index == 0 ) shiftAmountX = (int)value; 
		else shiftAmountY = (int)value; 

	}

	public String getParamDescriptor() {
		return "Shift Amount";
	}

	public String getParamName( int index ) {

		if( index == 0 ) return "X-Direction"; 
		else return "Y-Direction"; 
	}



	public int getShiftAmountX() {
		return shiftAmountX;
	}

	public void setShiftAmountX(int shiftAmountX) {
		this.shiftAmountX = shiftAmountX;
	}

	public int getShiftAmountY() {
		return shiftAmountY;
	}

	public void setShiftAmountY(int shiftAmountY) {
		this.shiftAmountY = shiftAmountY;
	}

	public void incrementShiftAmountX() {

		shiftAmountX++; 
	}

	public void incrementShiftAmountY() {

		shiftAmountY++; 
	}
	
	public void decrementShiftAmountY() {

		shiftAmountY--; 
	}

	public boolean isSelected() {

		return wrapAround;
	}

	public void setSelected(boolean tf) {
		
		wrapAround = tf; 
		
	}

}
