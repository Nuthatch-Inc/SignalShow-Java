package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;
import signals.gui.operation.HologramEncoderOptionsPanel;
import signals.gui.operation.OperationOptionsPanel;

public class HologramEncoderOp extends UnaryOperation {

	public HologramEncoderOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HologramEncoderOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	boolean randomPhase; 
	boolean errorDiffusion; 
	boolean widerApertures; 

	public boolean isWiderApertures() {
		return widerApertures;
	}

	public void setWiderApertures(boolean widerApertures) {
		this.widerApertures = widerApertures;
	}

	public HologramEncoderOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}

	public boolean isRandomPhase() {
		return randomPhase;
	}

	public OperationOptionsPanel getOptionsInterface() {
		
		return new HologramEncoderOptionsPanel(this); 
	}

	public void setRandomPhase(boolean randomPhase) {
		this.randomPhase = randomPhase;
	}


	public boolean isErrorDiffusion() {
		return errorDiffusion;
	}


	public void setErrorDiffusion(boolean errorDiffusion) {
		this.errorDiffusion = errorDiffusion;
	}

	@Override
	public Function create(Function input) { //Input is a 2D function

		Function2D f2D = (Function2D)input;
		int xDimension = f2D.getDimensionX(); 
		int yDimension = f2D.getDimensionY();

        double[] hologram = HologramEncoding.encode(input.getReal(), input.getImaginary(),
                xDimension, yDimension, errorDiffusion, widerApertures);
        return FunctionFactory.createFunction2D(hologram, new double[hologram.length], true,
                "hologram encode", xDimension * 8, yDimension * 8);
    }

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/HologramLarge.png");
//		model.setSmallIcon("/operationIcons/HologramSmall.png");
		model.setName("Hologram Encoder");
		
		model.setDocPath("/operationdoc/hologram.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/HologramOp.png"; 
	}

}
