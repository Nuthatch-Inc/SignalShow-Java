package signals.demo;

import javax.swing.ImageIcon;

import signals.core.BinaryOperation;
import signals.core.DemoModel;
import signals.core.Function;

public abstract class ConvolutionModel extends DemoModel {
	
	Function input1, input2; 
	boolean reverseKernel;
	BinaryOperation op;
	Integer step; 
	double[] outputReal, outputImag;
	boolean wrapAround; 
	boolean normalize; 
	
	boolean displayed; 

	public ConvolutionModel(ImageIcon icon, Function input1, Function input2,
			boolean reverseKernel, BinaryOperation op, int step, boolean wrapAround, boolean normalize) {
		super( icon );
		this.input1 = input1;
		this.input2 = input2;
		this.reverseKernel = reverseKernel;
		this.op = op;
		this.step = step;
		this.wrapAround = wrapAround; 
		this.normalize = normalize;
	}
	
	
	
	public String getShowState() {
		// TODO Auto-generated method stub
		return null;
	}



	public void setShowState(String state) {
		// TODO Auto-generated method stub
		
	}



	public boolean isDisplayed() {
		return displayed;
	}



	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}



	public double[] getOutputReal() {
		return outputReal;
	}

	public void setOutputReal(double[] outputReal) {
		this.outputReal = outputReal;
	}

	public double[] getOutputImag() {
		return outputImag;
	}

	public void setOutputImag(double[] outputImag) {
		this.outputImag = outputImag;
	}

	public Function getInput1() {
		return input1;
	}

	public void setInput1(Function input1) {
		this.input1 = input1;
	}

	public boolean isWrapAround() {
		return wrapAround;
	}



	public void setWrapAround(boolean wrapAround) {
		this.wrapAround = wrapAround;
	}



	public boolean isNormalize() {
		return normalize;
	}



	public void setNormalize(boolean normalize) {
		this.normalize = normalize;
	}



	public Function getInput2() {
		return input2;
	}

	public void setInput2(Function input2) {
		this.input2 = input2;
	}

	public boolean isReverseKernel() {
		return reverseKernel;
	}

	public void setReverseKernel(boolean reversedKernel) {
		this.reverseKernel = reversedKernel;
	}

	public BinaryOperation getOp() {
		return op;
	}

	public void setOp(BinaryOperation op) {
		this.op = op;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
	
	public void incrementStep() {
		
		step++; 
	}

	public abstract void show(); 

	public String getCompactDescriptor() {
		return "Convolution Demo";
	}

	public String getLongDescriptor() {
		return "Convolution Demo: " + input1.getLongDescriptor() + " *  " + input2.getLongDescriptor();
	}

}
