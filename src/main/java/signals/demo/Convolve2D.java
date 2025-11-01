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
import signals.operation.ReverseOp2D;
import signals.operation.TranslateOp2D;

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

	//product of function and reversed shifted kernel
	Function product; 

	//temporary display
	ImageDisplayPanel display, outputDisplay; 

	int stepNumber; 

	//dimension of signals
	int dimensionX, dimensionY; 

	//operators that perform the math 
	TranslateOp2D rotateOp;  
	ReverseOp2D reverseOp; 

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
		
		// For convolution: Input and Filter
		// For correlation: Function 2 and Function 1
		if (isConvolution) {
			panelTitle = "Input and Filter";
		} else {
			panelTitle = "Function 2 and Function 1";
		} 
		
		dimensionX = ((Function2D)function).getDimensionX();
		dimensionY = ((Function2D)function).getDimensionY();
		zeroCentered = function.isZeroCentered(); 

		JPanel inputPanel, outputPanel; 

		displayDimension = new Dimension( 256, 256 ); 

		display = new ImageDisplayPanel(displayDimension);  
		outputDisplay = new ImageDisplayPanel(displayDimension); 

		display.setIndices(dimensionX, dimensionY, true);
		outputDisplay.setIndices(dimensionX, dimensionY, true);

		inputPanel = new JPanel(); 
		inputPanel.setBorder(BorderFactory.createTitledBorder(panelTitle)); 
		outputPanel = new JPanel(); 
		outputPanel.setBorder(BorderFactory.createTitledBorder("Output")); 

		inputPanel.add(display); 
		outputPanel.add( outputDisplay ); 

		rotateOp = new TranslateOp2D(null);
		rotateOp.setWrapAround(wrapAround); 
		reverseOp = new ReverseOp2D(null); 

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

		//reverse if convolution, not correlation
		if( reverseKernel ) {

			//reverse in both x and y directions
			reverseOp.setXDirection(true); 
			reversedKernel = reverseOp.create(kernel); 
			reverseOp.setXDirection(false); 
			reversedKernel = reverseOp.create(reversedKernel); 

		} else {

			reversedKernel = kernel; 
		}

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
			
			double[] sum = ArrayMath.add(kernelReal, inputImag); 
			display.display(sum); 

			//display output 
			outputDisplay.display(outputImag); 

		}

	}


	public void sizeChanged() {

	}

	/**
	 * Calculates the reversed input, shifts it back by dimension/2
	 */
	private void reset() {

		stepNumber = 0; 
		rotateOp.setShiftAmountX( 0 );
		rotateOp.setShiftAmountY(dimensionY-1);

	}

	public boolean lastStep() { //TODO

		if( zeroCentered ) {

			return ( ( rotateOp.getShiftAmountY() == -dimensionY/2+1 ) && //TODO - done
					( rotateOp.getShiftAmountX() == (dimensionX/2-1) ) ); 
		}

		return ( ( rotateOp.getShiftAmountY() == 1 ) &&  //TODO - done
				( rotateOp.getShiftAmountX() == (dimensionX-1) ) ); 


	}

	public void step() {

		switch( stepNumber ) {

		case 0: 

			kernelReal = kernel.getPart( Constants.Part.REAL_PART ); 
			kernelImag = kernel.getPart( Constants.Part.IMAGINARY_PART ); 

			outputReal = new double[dimensionX*dimensionY]; 
			outputImag = new double[dimensionX*dimensionY]; 

			display(); 

			break; 

		case 1: 

			//display the rotated kernel
			kernelReal = reversedKernel.getPart( Constants.Part.REAL_PART ); 
			kernelImag = reversedKernel.getPart( Constants.Part.IMAGINARY_PART ); 

			display(); 

			break; 

		case 2: 

			//display the input signal and the rotated kernel that has been shifted back
			rotateOp.setShiftAmountX( zeroCentered ? -dimensionX/2 : 0 );
			rotateOp.setShiftAmountY( zeroCentered ? dimensionY/2 : dimensionY-1 ); //TODO - done
			reversedShiftedKernel = rotateOp.create(reversedKernel);

			kernelReal = reversedShiftedKernel.getPart( Constants.Part.REAL_PART ); 
			kernelImag = reversedShiftedKernel.getPart( Constants.Part.IMAGINARY_PART ); 

			display(); 

			break; 
		default: 

			//display the input signal, the rotated kernel that has been shifted back then forward, 
			//the product of the input signal and the shifted, rotated kernel, and the output. 
			reversedShiftedKernel = rotateOp.create(reversedKernel);

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

		//increment shift amount
		rotateOp.incrementShiftAmountX(); 
		if( zeroCentered ) {
			if( rotateOp.getShiftAmountX() == dimensionX/2 ) {

				rotateOp.setShiftAmountX( -dimensionX/2 );
				rotateOp.decrementShiftAmountY();  //TODO done

			} 
		} else {
			if( rotateOp.getShiftAmountX() == dimensionX ) {

				rotateOp.setShiftAmountX( 0 );
				rotateOp.decrementShiftAmountY(); //TODO done

			} 

		}

		}

		display(); 

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

		output = op.create(function, kernel);
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
		return stepNumber==0;
	}

}
