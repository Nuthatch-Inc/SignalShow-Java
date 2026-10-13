package signals.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import signals.core.BinaryOperation;
import signals.core.Constants;
import signals.core.Core;
import signals.core.Function;
import signals.core.Function2D;
import signals.gui.plot.ImageDisplayPanel;
import signals.operation.ArrayMath;
import signals.operation.CalculusOperations;

@SuppressWarnings("serial")
public class Convolve2D extends AnimatedPane {

	//two original inputs 
	Function function, kernel, output; 

	//reversed kernel 
	Function reversedKernel;
	
	// Labels for display
	String panelTitle;
	
	// Whether this is convolution (true) or correlation (false)
	boolean isConvolution;

	//reversed and shifted kernel 
	Function reversedShiftedKernel; 

	//temporary display
	ImageDisplayPanel display, outputDisplay; 

	int stepNumber; 

	//dimension of signals
	int dimensionX, dimensionY; 

	ConvolutionDemoKernel movingKernel;

	double[] outputReal, outputImag;

	boolean zeroCentered; 

	Dimension displayDimension; 

	double[] inputReal, inputImag, kernelReal, kernelImag;

	JRadioButton realButton, imagButton; 

	BinaryOperation op; 

	boolean wrapAround; 
	
	boolean normalize; 

	public Convolve2D( Function function, Function kernel, boolean reverseKernel, BinaryOperation op, boolean wrapAround,
			boolean normalize) {

		this.function = function;
		this.kernel = kernel; 
		this.op = op; 
		this.wrapAround = wrapAround; 
		this.normalize = normalize;
		this.isConvolution = reverseKernel;
		
		// For both convolution and correlation:
		// - First function = Input (stationary)
		// - Second function = Filter (convolution) or Reference (correlation) - this one slides
		// Correlation conjugates the second input, matching CorrelateOp.
		if (isConvolution) {
			panelTitle = "Input and Filter";
		} else {
			panelTitle = "Input and Reference";
		} 
		
		dimensionX = ((Function2D)function).getDimensionX();
		dimensionY = ((Function2D)function).getDimensionY();
		zeroCentered = function.isZeroCentered(); 

		JPanel inputPanel, outputPanel; 

		displayDimension = new Dimension( 256, 256 ); 

		display = new ImageDisplayPanel(displayDimension);  
		outputDisplay = new ImageDisplayPanel(displayDimension); 

		display.setIndices(dimensionX, dimensionY, zeroCentered);
		outputDisplay.setIndices(dimensionX, dimensionY, zeroCentered);

		inputPanel = new JPanel(); 
		inputPanel.setBorder(BorderFactory.createTitledBorder(panelTitle)); 
		outputPanel = new JPanel(); 
		outputPanel.setBorder(BorderFactory.createTitledBorder("Output")); 

		inputPanel.add(display); 
		outputPanel.add( outputDisplay ); 

		realButton = new JRadioButton("Show Real"); 
		imagButton = new JRadioButton("Show Imaginary"); 
		ButtonGroup partGroup = new ButtonGroup(); 
		partGroup.add(realButton); 
		partGroup.add(imagButton); 
		JPanel partPanel = new JPanel(); 
		partPanel.add( realButton ); 
		partPanel.add( imagButton ); 
		realButton.setSelected(true); 

		ActionListener partListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				display(); 

			}

		}; 

		realButton.addActionListener( partListener ); 
		imagButton.addActionListener( partListener ); 

		movingKernel = new ConvolutionDemoKernel(function, kernel, reverseKernel, wrapAround, normalize);
		this.kernel = movingKernel.kernel;
		reversedKernel = movingKernel.at(0, 0);

		inputReal = function.getPart( Constants.Part.REAL_PART ); 
		inputImag = function.getPart( Constants.Part.IMAGINARY_PART ); 

		JPanel contentPanel = new JPanel(); 
		contentPanel.add( inputPanel ); 
		contentPanel.add( outputPanel ); 
		contentPanel.add( partPanel ); 

		setLayout( new BorderLayout() ); 
		add( contentPanel, BorderLayout.CENTER ); 
		add( createAnimationToolBar(), BorderLayout.NORTH ); 

		step(); 

	}

	public void display() {

		if( realButton.isSelected() ) { 

			//display input
			double[] sum = ArrayMath.add(kernelReal, inputReal); 
			display.display(sum); 
			
			//display.display(kernelImag, kernelReal, inputReal);  

			//display output 
			outputDisplay.display(outputReal); 

		} else {

			//display input
			//display.display(kernelImag, kernelReal, inputImag);
			
			double[] sum = ArrayMath.add(kernelImag, inputImag);
			display.display(sum); 

			//display output 
			outputDisplay.display(outputImag); 

		}

	}

	public void sizeChanged() {

	}

	private void reset() {
		stepNumber = 0; 
	}

	public boolean lastStep() {
		return stepNumber >= dimensionX * dimensionY + 3;
	}

	public void step() {

		if (lastStep()) return;

		switch( stepNumber ) {

		case 0: 

			kernelReal = kernel.getPart( Constants.Part.REAL_PART ); 
			kernelImag = kernel.getPart( Constants.Part.IMAGINARY_PART ); 

			outputReal = new double[dimensionX*dimensionY]; 
			outputImag = new double[dimensionX*dimensionY]; 

			break; 

		case 1: 

			// Display the reversed filter, or conjugated correlation reference.
			kernelReal = reversedKernel.getPart( Constants.Part.REAL_PART ); 
			kernelImag = reversedKernel.getPart( Constants.Part.IMAGINARY_PART ); 

			break; 

		case 2: 

			//display the input signal and the rotated kernel that has been shifted back
			reversedShiftedKernel = movingKernel.atOutput(0);

			kernelReal = reversedShiftedKernel.getPart( Constants.Part.REAL_PART ); 
			kernelImag = reversedShiftedKernel.getPart( Constants.Part.IMAGINARY_PART ); 

			break; 
		default: 

			//display the input signal, the rotated kernel that has been shifted back then forward, 
			//the product of the input signal and the shifted, rotated kernel, and the output. 
			reversedShiftedKernel = movingKernel.atOutput(stepNumber - 3);

			kernelReal = reversedShiftedKernel.getPart( Constants.Part.REAL_PART );
			kernelImag = reversedShiftedKernel.getPart( Constants.Part.IMAGINARY_PART );

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


		}

		display(); 

		stepNumber++; 

	}//step

	@Override
	public void stepBack() {
		int target = Math.max(0, stepNumber - 2);
		toBegin();
		for (int i = 0; i < target && !lastStep(); i++) step();
	}

	@Override
	public void toBegin() {
		reset(); 
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

		return animationToolBar; 

	} 

	@Override
	public void toEnd() {

		output = movingKernel.complete(function, op);
		stepNumber = dimensionX * dimensionY + 3;
		outputReal = output.getPart( Constants.Part.REAL_PART );
		outputImag = output.getPart( Constants.Part.IMAGINARY_PART );

		kernelReal = kernel.getPart( Constants.Part.REAL_PART ); 
		kernelImag = kernel.getPart( Constants.Part.IMAGINARY_PART ); 

		display(); 
	}

	@Override
	public void export() {
		Core.getFunction2DList().addItem(output); 
	}

	@Override
	public boolean firstStep() { 
		return stepNumber <= 1;
	}

}
