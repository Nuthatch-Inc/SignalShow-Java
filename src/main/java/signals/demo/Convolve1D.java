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

@SuppressWarnings("serial")
public class Convolve1D extends AnimatedPane {

	// two original inputs
	Function function, kernel, output;

	// reversed kernel
	Function reversedKernel;

	// Labels for display
	String functionLabel, kernelLabel;

	// Whether this is convolution (true) or correlation (false)
	boolean isConvolution;

	// reversed and shifted kernel
	Function reversedShiftedKernel;

	Function1DTabPanel inputPanel, outputPanel;

	Integer stepNumber;

	// dimension of signals
	int dimension;

	ConvolutionDemoKernel movingKernel;
	BinaryOperation op;

	double[] outputReal, outputImag;

	boolean zeroCentered;

	boolean wrapAround;

	boolean normalize;

	public Convolve1D(Function function, Function input2, boolean reverseKernel, BinaryOperation op, Integer step,
			boolean wrapAround, boolean normalize) {

		this.function = function;
		this.kernel = input2;
		this.op = op;
		this.stepNumber = 0;
		this.wrapAround = wrapAround;
		this.normalize = normalize;
		this.isConvolution = reverseKernel;

		// For both convolution and correlation:
		// - First function (function) = Input (stationary)
		// - Second function (kernel) = Filter/Reference (this one slides)
		// Roger Easton's correlation definition: f ⋆ m = IFFT(FFT(f) · conj(FFT(m)))
		// - f = input (first argument)
		// - m = reference (second argument, gets conjugated)
		if (isConvolution) {
			functionLabel = "Input";
			kernelLabel = "Filter";
		} else {
			functionLabel = "Input";
			kernelLabel = "Reference";
		}

		dimension = ((Function1D) function).getDimension();
		zeroCentered = function.isZeroCentered();

		Dimension size = new Dimension(800, 170);
		inputPanel = new Function1DTabPanel(size);
		outputPanel = new Function1DTabPanel(size);

		JPanel contentPanel = new JPanel();

		contentPanel.add(inputPanel);
		contentPanel.add(outputPanel);
		setLayout(new BorderLayout());
		add(contentPanel, BorderLayout.CENTER);
		add(createAnimationToolBar(), BorderLayout.NORTH);

		movingKernel = new ConvolutionDemoKernel(function, input2, reverseKernel, wrapAround, normalize);
		kernel = movingKernel.kernel;
		reversedKernel = movingKernel.at(0, 0);

		step();
		if (step > 0) gotoStep(step);

	}

	public void sizeChanged() {

		inputPanel.sizeChanged();
		outputPanel.sizeChanged();
	}

	private void reset() {

		stepNumber = 0;

	}

	public boolean lastStep() {

		return stepNumber >= dimension + 3;
	}

	public void step() {

		if (lastStep()) return;

		switch (stepNumber) {

			case 0:

				// display the input signal and the input kernel, zero the output

				outputReal = new double[dimension];
				outputImag = new double[dimension];
				output = FunctionFactory.createFunction1D(outputReal, outputImag, zeroCentered, "output");

				inputPanel.removePlots();
				inputPanel.addFunction((Function1D) function, functionLabel);
				inputPanel.addFunction((Function1D) kernel, kernelLabel);

				outputPanel.removePlots();
				outputPanel.addFunction((Function1D) output, "Output");

				break;
			case 1:

				// Display the reversed filter, or conjugated correlation reference.
				inputPanel.removePlots();
				inputPanel.addFunction((Function1D) function, functionLabel);
				inputPanel.addFunction((Function1D) reversedKernel, kernelLabel);

				break;
			case 2:

				// display the input signal and the rotated kernel that has been shifted back
				reversedShiftedKernel = movingKernel.atOutput(0);

				inputPanel.removePlots();
				inputPanel.addFunction((Function1D) function, functionLabel);
				inputPanel.addFunction((Function1D) reversedShiftedKernel, kernelLabel);

				break;
			default:

				// display the input signal, the rotated kernel that has been shifted back then
				// forward,
				// the product of the input signal and the shifted, rotated kernel, and the
				// output.
				reversedShiftedKernel = movingKernel.atOutput(stepNumber - 3);

				inputPanel.removePlots();
				inputPanel.addFunction((Function1D) function, functionLabel);
				inputPanel.addFunction((Function1D) reversedShiftedKernel, kernelLabel);

				// find the product of the input signals
				double[][] productArray = ArrayMath.multiply(function.getReal(), function.getImaginary(),
						reversedShiftedKernel.getReal(), reversedShiftedKernel.getImaginary());

				double[] productR = productArray[0];
				double[] productI = productArray[1];

				double outR = CalculusOperations.area(productR);
				double outI = CalculusOperations.area(productI);

				int outputIndex = stepNumber - 3;
				outputReal[outputIndex] = outR;
				outputImag[outputIndex] = outI;

				output = FunctionFactory.createFunction1D(outputReal, outputImag, zeroCentered, "output");
				outputPanel.removePlots();
				outputPanel.addFunction((Function1D) output, "Output");

		}

		stepNumber++;

	}// step

	@Override
	public void stepBack() {
		gotoStep(Math.max(0, stepNumber - 2));
	}

	@Override
	public void toBegin() {
		reset();
		step();
	}

	public void gotoStep(Integer step) {

		toBegin();
		for (int i = 0; i < step && !lastStep(); i++) step();
	}

	public JComponent createAnimationToolBar() {

		JPanel animationToolBar = new JPanel();

		animationToolBar.add(new JButton(toBeginAction));
		animationToolBar.add(new JButton(slowerAction));
		animationToolBar.add(new JButton(stepAction));
		animationToolBar.add(new JButton(playAction));
		animationToolBar.add(new JButton(stopAction));
		animationToolBar.add(new JButton(fasterAction));
		animationToolBar.add(Box.createHorizontalGlue());
		animationToolBar.add(new JButton(exportAction));

		return animationToolBar;

	}

	@Override
	public void toEnd() {

		output = movingKernel.complete(function, op);
		outputReal = output.getReal();
		outputImag = output.getImaginary();
		stepNumber = dimension + 3;
		outputPanel.removePlots();
		outputPanel.addFunction((Function1D) output);

		inputPanel.removePlots();
		inputPanel.addFunction((Function1D) function);
		inputPanel.addFunction((Function1D) kernel);
	}

	@Override
	public void export() {
		Core.getFunctionList().addItem(output);
	}

	@Override
	public boolean firstStep() {
		return stepNumber <= 1;
	}
}
