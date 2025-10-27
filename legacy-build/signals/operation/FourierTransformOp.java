package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.UnaryOperation;
import signals.gui.operation.CheckBoxOptionsPanel;
import signals.gui.operation.NormalizationOptionsPanel;
import signals.gui.operation.OperationOptionsPanel;

public class FourierTransformOp extends UnaryOperation implements NormalizationOperation, BooleanOperation {
	
	public FourierTransformOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FourierTransformOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	int normalization; 
	boolean inverse; 

	/* (non-Javadoc)
	 * @see signals.core.Operation#getOptionsInterface()
	 */
	@Override
	public OperationOptionsPanel getOptionsInterface() {
		
		OperationOptionsPanel compoundPanel = new OperationOptionsPanel(this); 
		compoundPanel.add( new NormalizationOptionsPanel( this ) );
		compoundPanel.add( new CheckBoxOptionsPanel( this, "inverse" ) );
		return compoundPanel;
	}

	public FourierTransformOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		normalization = Transforms.NORMALIZE_ROOT_N;
		inverse = false;
	}

	/**
	 * @return the normalization
	 */
	public int getNormalization() {
		return normalization;
	}

	public boolean isSelected() {
		return inverse;
	}

	public boolean isInverse() {
		return inverse;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	public void setSelected(boolean inverse) {
		this.inverse = inverse;
	}

	/**
	 * @param normalization the normalization to set
	 */
	public void setNormalization(int normalization) {
		this.normalization = normalization;
	}
	
	@Override
	public Function create(Function input) {
		
		Function output = null; 
		
		if( input instanceof Function1D ) {
			
			output = Transforms.fft1D(input, inverse, normalization);
			
		} else {
			
			output = Transforms.fft2D(input, inverse, normalization);
		}
		
		return output;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/FFTLarge.png");
//		model.setSmallIcon("/operationIcons/FFTSmall.png");
		model.setName("FFT");
		
		model.setDocPath("/operationdoc/fft.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/FFTOp.png"; 
	}

}
