package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.FunctionTerm2D;
import signals.core.UnaryOperation;
import signals.functionterm.RandomPhaseFunctionTerm2D;
import signals.functionterm.ZeroFunctionTerm2D;
import signals.gui.operation.CGHOptionsPanel;
import signals.gui.operation.OperationOptionsPanel;

public class PhaseDetourHologramOp extends UnaryOperation {

	public PhaseDetourHologramOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PhaseDetourHologramOp(ParameterBlock paramBlock, int priority) {
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

	public PhaseDetourHologramOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}

	public boolean isRandomPhase() {
		return randomPhase;
	}

	public OperationOptionsPanel getOptionsInterface() {
		
		return new CGHOptionsPanel(this); 
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

		double[] magnitude = input.getReal(); 

		FunctionTerm2D randomPhaseTerm = (FunctionTerm2D) (randomPhase ?  new RandomPhaseFunctionTerm2D(null).getDefaultInstance() :
			new ZeroFunctionTerm2D(null).getDefaultInstance()); 

		Function2D f2D = (Function2D)input;
		int xDimension = f2D.getDimensionX(); 
		int yDimension = f2D.getDimensionY();
		double[] randomPhase = randomPhaseTerm.create(xDimension, yDimension, input.isZeroCentered());

		Function inputFunction = FunctionFactory.createMagPhaseFunction2D(
				magnitude, randomPhase, true, "", xDimension, yDimension);

		Function transform = Transforms.fft2D(inputFunction, true, Transforms.NORMALIZE_NONE); 

        double[] hologram = HologramEncoding.encode(transform.getReal(), transform.getImaginary(),
                xDimension, yDimension, errorDiffusion, widerApertures);
        return FunctionFactory.createFunction2D(hologram, new double[hologram.length], true,
                "hologram", xDimension * 8, yDimension * 8);
    }

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/HologramLarge.png");
//		model.setSmallIcon("/operationIcons/HologramSmall.png");
		model.setName("Phase Detour Hologram");
		
		model.setDocPath("/operationdoc/hologram.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/HologramOp.png"; 
	}

}
