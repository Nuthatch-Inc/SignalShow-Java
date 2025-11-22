package signals.demo;

import signals.core.BinaryOperation;
import signals.core.Core;
import signals.core.Function;
import signals.gui.IconCache;

public class ConvolutionModel2D extends ConvolutionModel {

	public ConvolutionModel2D(Function input1, Function input2,
			boolean reverseKernel, BinaryOperation op, int step, boolean wrapAround, boolean normalize) {
		super(IconCache.getIcon("/guiIcons/ConvolveDemo2D.png"), 
				input1, input2, reverseKernel, op, step, wrapAround, normalize);
	}
	
	public ConvolutionModel2D clone() {
		
		return new ConvolutionModel2D( input1, input2, reverseKernel, op, step, wrapAround, normalize ); 
	}

	public void show() {

		Convolve2D demo = new Convolve2D(input1, input2, reverseKernel, op, wrapAround, normalize );  
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
