package signals.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import signals.core.BinaryOperation;
import signals.core.Core;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.FunctionFactory;
import signals.gui.plot.Function1DTabPanel;
import signals.operation.ArrayMath;
import signals.operation.CalculusOperations;
import signals.operation.NormalizeOp;
import signals.operation.ReverseOp1D;
import signals.operation.TranslateOp1D;

@SuppressWarnings("serial")
public class Convolve1D extends AnimatedPane {

	//two original inputs 
	Function function, kernel, output; 

	//reversed kernel 
	Function reversedKernel;
	
	// Labels for display
	String functionLabel, kernelLabel;
	
	// Whether this is convolution (true) or correlation (false)
	boolean isConvolution;

	//reversed and shifted kernel 
	Function reversedShiftedKernel; 

	//product of function and reversed shifted kernel
	Function product; 

	Function1DTabPanel inputPanel, outputPanel; 

	Integer stepNumber; 

	//dimension of signals
	int dimension; 

	//operators that perform the math 
	TranslateOp1D rotateOp; 
	ReverseOp1D reverseOp; 
	BinaryOperation op; 

	double[] outputReal, outputImag;

	boolean zeroCentered; 

	boolean wrapAround; 
	
	boolean normalize; 

	public Convolve1D( Function function, Function input2, boolean reverseKernel, BinaryOperation op, Integer step, 
			boolean wrapAround, boolean normalize ) {

		this.function = function;
		this.kernel = input2; 
		this.op = op; 
		this.stepNumber = step; 
		this.wrapAround = wrapAround; 
		this.normalize = normalize;
		this.isConvolution = reverseKernel;
		
		// For both convolution and correlation:
		// - First function (function) = Input (stationary)
		// - Second function (kernel) = Filter (convolution) or Reference (correlation) - this one slides
		// Note: CorrelateOp swaps operands internally to match Roger Easton's notation
		if (isConvolution) {
			functionLabel = "Input";
			kernelLabel = "Filter";
		} else {
			functionLabel = "Input";
			kernelLabel = "Reference";
		} 

		dimension = ((Function1D)function).getDimension();
		zeroCentered = function.isZeroCentered(); 

		Dimension size = new Dimension( 800, 170 ); 
		inputPanel = new Function1DTabPanel( size );
		outputPanel = new Function1DTabPanel( size );

		JPanel contentPanel = new JPanel(); 

		contentPanel.add( inputPanel ); 
		contentPanel.add( outputPanel );
		setLayout( new BorderLayout() ); 
		add( contentPanel, BorderLayout.CENTER ); 
		add( createAnimationToolBar(), BorderLayout.NORTH ); 

		rotateOp = new TranslateOp1D(null);
		rotateOp.setWrapAround(wrapAround); 
		reverseOp = new ReverseOp1D(null); 

		reversedKernel = reverseKernel ? reverseOp.create(kernel) : kernel;  

		step(); 

	}

	public void sizeChanged() {

		inputPanel.sizeChanged(); 
		outputPanel.sizeChanged(); 
	}

	/**
	 * Calculates the reversed input, shifts it back by dimension/2
	 */
	private void reset() {

		stepNumber = 0; 
		rotateOp.setShiftAmount( 0 );

	}

	public boolean lastStep() {

		return rotateOp.getShiftAmount() == ( zeroCentered ? dimension/2-1 : dimension - 1); 
	}

	public void step() {

		switch( stepNumber ) {

		case 0: 

			//display the input signal and the input kernel, zero the output
			if( normalize ) {
				
				NormalizeOp normalizer = new NormalizeOp( null ); 
				kernel = normalizer.create(kernel);
				
			}

			outputReal = new double[dimension]; 
			outputImag = new double[dimension]; 
			output = FunctionFactory.createFunction1D(outputReal, outputImag, zeroCentered, "output"); 

			inputPanel.removePlots(); 
			inputPanel.addFunction((Function1D)function, functionLabel); 
			inputPanel.addFunction((Function1D)kernel, kernelLabel);

			outputPanel.removePlots();
			outputPanel.addFunction((Function1D)output, "Output");

			break; 
		case 1: 

			//display the input signal and the rotated kernel 
			
			if( normalize ) {
				
				NormalizeOp normalizer = new NormalizeOp( null ); 
				reversedKernel = normalizer.create(reversedKernel);
				
			}
			
			inputPanel.removePlots();  
			inputPanel.addFunction((Function1D)function, functionLabel); 
			inputPanel.addFunction((Function1D)reversedKernel, kernelLabel);

			break; 
		case 2: 

			//display the input signal and the rotated kernel that has been shifted back
			rotateOp.setShiftAmount( zeroCentered ? -dimension/2 : 0 );
			reversedShiftedKernel = rotateOp.create(reversedKernel);

			inputPanel.removePlots();  
			inputPanel.addFunction((Function1D)function, functionLabel); 
			inputPanel.addFunction((Function1D)reversedShiftedKernel, kernelLabel);

			break; 
		default: 

			//display the input signal, the rotated kernel that has been shifted back then forward, 
			//the product of the input signal and the shifted, rotated kernel, and the output. 
			rotateOp.setShiftAmount( zeroCentered ? -dimension/2 + stepNumber - 2 : stepNumber - 2 ); 
		reversedShiftedKernel = rotateOp.create(reversedKernel);

		inputPanel.removePlots();  
		inputPanel.addFunction((Function1D)function, functionLabel); 
		inputPanel.addFunction((Function1D)reversedShiftedKernel, kernelLabel);

		//find the product of the input signals
		double[][] productArray = ArrayMath.multiply( function.getReal(), function.getImaginary(),
				reversedShiftedKernel.getReal(), reversedShiftedKernel.getImaginary() );

		double[] productR = productArray[0];
		double[] productI = productArray[1]; 

		double outR = CalculusOperations.area( productR );
		double outI = CalculusOperations.area( productI );

		int outputIndex = stepNumber - 3; 
		outputReal[outputIndex] = outR; 
		outputImag[outputIndex] = outI; 

		output = FunctionFactory.createFunction1D(outputReal, outputImag, zeroCentered, "output"); 
		outputPanel.removePlots(); 
		outputPanel.addFunction((Function1D)output, "Output"); 

		}

		stepNumber++; 

	}//step

	@Override
	public void stepBack() {
		stepNumber -= 2; 
		step(); 
	}

	@Override
	public void toBegin() {
		reset(); 
		step();
	}

	public void gotoStep( Integer step ) {

		stepNumber = step; 
		step(); 
	}

	public JComponent createAnimationToolBar() {

		JPanel animationToolBar = new JPanel(); 

		animationToolBar.add( new JButton( toBeginAction ) );
		animationToolBar.add( new JButton(slowerAction) );
		animationToolBar.add( new JButton(stepAction) );
		animationToolBar.add( new JButton(playAction) );
		animationToolBar.add( new JButton(stopAction) );
		animationToolBar.add( new JButton(fasterAction) );
		animationToolBar.add(Box.createHorizontalGlue()); 
		animationToolBar.add( new JButton(exportAction) );

		return animationToolBar; 

	} 

	@Override
	public void toEnd() {

		output = op.create(function, kernel);
		outputPanel.removePlots(); 
		outputPanel.addFunction((Function1D)output); 

		inputPanel.removePlots(); 
		inputPanel.addFunction((Function1D)function); 
		inputPanel.addFunction((Function1D)kernel);
	}

	@Override
	public void export() {
		Core.getFunctionList().addItem(output); 
	}

	@Override
	public boolean firstStep() { 
		return stepNumber==0;
	}
}
