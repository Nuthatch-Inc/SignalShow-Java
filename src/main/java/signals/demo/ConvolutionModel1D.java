package signals.demo;

import signals.core.BinaryOperation;
import signals.core.Core;
import signals.core.Function;
import signals.gui.IconCache;

public class ConvolutionModel1D extends ConvolutionModel {

	public ConvolutionModel1D(Function input1, Function input2,
			boolean reverseKernel, BinaryOperation op, int step, boolean wrapAround, boolean normalize) {
		super(IconCache.getIcon("/guiIcons/ConvolveDemo.png"), 
				input1, input2, reverseKernel, op, step, wrapAround, normalize);
	}
	
	public ConvolutionModel1D clone() {
		
		return new ConvolutionModel1D( input1, input2, reverseKernel, op, step, wrapAround, normalize ); 
	}

	public void show() {

		Convolve1D demo = new Convolve1D(input1, input2, reverseKernel, op, step, wrapAround, normalize );  
		Core.getGUI().removeContent(); 
		Core.getGUI().setContent(demo); 
	}
	
	public void showNew() {
		show(); 
	}

	public void freeMemory() {
		// TODO Auto-generated method stub
		
	}

}
