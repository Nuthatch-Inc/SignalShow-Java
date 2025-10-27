package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;
import signals.gui.operation.CheckBoxOptionsPanel;
import signals.gui.operation.OperationOptionsPanel;

public class CepstrumOp extends UnaryOperation implements BooleanOperation {
	
	public CepstrumOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CepstrumOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	boolean inverse; 

	public CepstrumOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2); 
	}
	
	@Override
	public OperationOptionsPanel getOptionsInterface() {
		return new CheckBoxOptionsPanel( this, "inverse" );
	}



	public boolean isSelected() {
		return inverse;
	}



	public void setSelected(boolean inverse) {
		this.inverse = inverse;
	}



	@Override
	public Function create(Function input) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "Cepstrum{" + input.getCompactDescriptor() + "}";
		
		if( input instanceof Function1D ) {
			
			double[][] output = Transforms.cepstrum1D(real, imag, zeroCentered, inverse);
			return FunctionFactory.createFunction1D( output[0], output[1], zeroCentered, name ); 
			
		} 
		
		int x_dimension = ((Function2D)input).getDimensionX();
		int y_dimension = ((Function2D)input).getDimensionY();

		double[][] output = Transforms.cepstrum2D(real, imag, zeroCentered, inverse, 
					x_dimension, y_dimension );
		return FunctionFactory.createFunction2D( output[0], output[1], zeroCentered, name, 
				x_dimension, y_dimension ); 
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/CepstrumLarge.png");
//		model.setSmallIcon("/operationIcons/CepstrumSmall.png");
		model.setName("Cepstrum");
		
		model.setDocPath("/operationdoc/cepstrum.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/CepstrumOp.png"; 
	}

}
