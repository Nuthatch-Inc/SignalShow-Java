package signals.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import signals.core.BinaryOperation;
import signals.core.Core;
import signals.core.Function;
import signals.core.FunctionProducer;
import signals.gui.DialogTemplate;
import signals.gui.GUIDimensions;
import signals.gui.VerticalThumbnailList;
import signals.operation.ConvolveOp;
import signals.operation.CorrelateOp;

@SuppressWarnings("serial")
public abstract class ConvolveDemoSetup extends DialogTemplate {

	protected VerticalThumbnailList function1Selector;
	protected VerticalThumbnailList function2Selector;
	protected JRadioButton correlationButton;
	protected JRadioButton convolutionButton;
	protected JLabel operationLabel;
	protected JLabel function2Label;
	protected ConvolveOp convolveOp;
	protected CorrelateOp correlateOp;
	protected JCheckBox wrapAround, normalizeFilter;
	protected JPanel typePanel;

	public ConvolveDemoSetup(String title) {

		super(Core.getGUI().getFrame(), title);

		function1Selector = getVariableList();
		function2Selector = getVariableList();

		correlationButton = new JRadioButton("Correlation");
		convolutionButton = new JRadioButton("Convolution");

		wrapAround = new JCheckBox("Wrap Around");
		wrapAround.setSelected(true);

		normalizeFilter = new JCheckBox("Normalize Filter/Reference");
		normalizeFilter.setSelected(true);

		ButtonGroup bg = new ButtonGroup();
		bg.add(correlationButton);
		bg.add(convolutionButton);
		convolutionButton.setSelected(true);

		typePanel = new JPanel();
		typePanel.add(correlationButton);
		typePanel.add(convolutionButton);
		typePanel.add(wrapAround);
		typePanel.add(normalizeFilter);

		convolveOp = new ConvolveOp(null);
		correlateOp = new CorrelateOp(null);

		ActionListener typeListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (correlationButton.isSelected()) {

					operationLabel.setIcon(correlateOp.getOpIcon());
					function2Label.setText("Reference:");

				} else {

					operationLabel.setIcon(convolveOp.getOpIcon());
					function2Label.setText("Filter:");
				}

			}
		};

		convolutionButton.addActionListener(typeListener);
		correlationButton.addActionListener(typeListener);

		Dimension selectorSize = new Dimension(GUIDimensions.LIST_WIDTH + 25, 300);

		JPanel operationSelectorPanel = new JPanel();
		operationLabel = new JLabel(convolveOp.getOpIcon());
		operationSelectorPanel.add(operationLabel);

		JPanel operationPanel = new JPanel();

		// first selector
		JScrollPane sp1 = new JScrollPane(function1Selector);
		sp1.setPreferredSize(selectorSize);
		JPanel selectorPanel1 = new JPanel(new BorderLayout());
		selectorPanel1.add(sp1, BorderLayout.CENTER);
		selectorPanel1.add(new JLabel("Input:"), BorderLayout.NORTH);
		operationPanel.add(selectorPanel1);

		// operation
		operationPanel.add(operationSelectorPanel);

		// second selector
		JScrollPane sp2 = new JScrollPane(function2Selector);
		sp2.setPreferredSize(selectorSize);
		JPanel selectorPanel2 = new JPanel(new BorderLayout());
		selectorPanel2.add(sp2, BorderLayout.CENTER);
		function2Label = new JLabel("Filter:");
		selectorPanel2.add(function2Label, BorderLayout.NORTH);
		operationPanel.add(selectorPanel2);

		addContentPanel(operationPanel, BorderLayout.CENTER);
		addContentPanel(typePanel, BorderLayout.NORTH);
	}

	public abstract VerticalThumbnailList getVariableList();

	public abstract void getDemo(Function input1, Function input2,
			boolean reversedKernel, BinaryOperation op, boolean wrapAround, boolean normalize);

	@Override
	public boolean commitSettings() {

		// get the first two functions
		Function input1 = ((FunctionProducer) function1Selector.getSelectedItem()).getFunction();
		Function input2 = ((FunctionProducer) function2Selector.getSelectedItem()).getFunction();

		if (convolutionButton.isSelected()) {

			getDemo(input1, input2, true, convolveOp, wrapAround.isSelected(), normalizeFilter.isSelected());

		} else {

			// Correlation: input1 is the input signal, input2 is the reference (which gets
			// conjugated)
			// Roger Easton's definition: f ⋆ m = IFFT(FFT(f) · conj(FFT(m)))
			getDemo(input1, input2, false, correlateOp, wrapAround.isSelected(), normalizeFilter.isSelected());

		}

		return true;
	}

}