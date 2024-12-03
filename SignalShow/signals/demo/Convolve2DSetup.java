package signals.demo;

import signals.core.BinaryOperation;
import signals.core.Core;
import signals.core.Function;
import signals.gui.VerticalThumbnailList;

@SuppressWarnings("serial")
public class Convolve2DSetup extends ConvolveDemoSetup {

	public Convolve2DSetup() {
		super("New 2D Convolution Demo"); 
	}

	@Override
	public void getDemo(Function input1, Function input2,
			boolean reversedKernel, BinaryOperation op, boolean wrapAround, boolean normalize ) {
		ConvolutionModel model = new ConvolutionModel2D(input1, input2, reversedKernel, op, new Integer(0), wrapAround, 
					normalize); 
		Core.getGUI().getFunctionList().addItem(model); 
		
	}

	@Override
	public VerticalThumbnailList getVariableList() {
		return Core.getFunction2DList(); 
	}


}
