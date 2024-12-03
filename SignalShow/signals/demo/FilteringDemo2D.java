package signals.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import signals.core.Function;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.gui.plot.ImageDisplayPanel;
import signals.operation.ArrayMath;
import signals.operation.Transforms;

@SuppressWarnings("serial")
public class FilteringDemo2D extends FilteringDemo {
	
	int dimension; 
	boolean zeroCentered; 
	int dimensionX, dimensionY; 
	
	ImageDisplayPanel spatialPanel, frequencyPanel; 
	
	double[] logMagnitudeTransform; //log magnitude scaled between 0 and 1  
	
	JRadioButton realButton, imagButton, magButton, phaseButton; 
	
	JPanel partPanel; 
	
	public FilteringDemo2D(Function input) {
		super(input);
		
		transform = Transforms.fft2D(input, false, Transforms.NORMALIZE_ROOT_N); 
		
		logMagnitudeTransform = ArrayMath.log10(ArrayMath.magnitude(transform.getReal(), transform.getImaginary()));
		logMagnitudeTransform = ArrayMath.subtract(logMagnitudeTransform, ArrayMath.min(logMagnitudeTransform)); 
		logMagnitudeTransform = ArrayMath.scale(logMagnitudeTransform, 1.0/ArrayMath.max(logMagnitudeTransform)); 
		
		dimensionX = ((Function2D)input).getDimensionX();
		dimensionY = ((Function2D)input).getDimensionY();
		zeroCentered = input.isZeroCentered(); 
		
		Dimension size = new Dimension( 256, 256 ); 
		spatialPanel = new ImageDisplayPanel( size );
		spatialPanel.setBorder(BorderFactory.createTitledBorder("Filtering Result (Spatial Domain)")); 
		frequencyPanel = new ImageDisplayPanel( size );
		frequencyPanel.setBorder(BorderFactory.createTitledBorder("Fourier Transform and Filter")); 
		spatialPanel.setIndices(dimensionX, dimensionY, zeroCentered); 
		frequencyPanel.setIndices(dimensionX, dimensionY, zeroCentered); 
		
		realButton = new JRadioButton( "Real"); 
		imagButton = new JRadioButton( "Imaginary"); 
		magButton = new JRadioButton( "Magnitude" ); 
		phaseButton = new JRadioButton( "Phase"); 
		
		ActionListener listener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				display(); 
				
			}
			
		}; 
		
		ButtonGroup partGroup = new ButtonGroup(); 
		partGroup.add( realButton ); 
		partGroup.add( imagButton ); 
		partGroup.add( magButton ); 
		partGroup.add( phaseButton ); 
		realButton.setSelected( true ); 
		
		realButton.addActionListener(listener); 
		imagButton.addActionListener(listener); 
		magButton.addActionListener(listener); 
		phaseButton.addActionListener(listener); 

		partPanel = new JPanel(); 
		partPanel.setBorder(BorderFactory.createTitledBorder("Output Display")); 
		partPanel.setLayout( new BoxLayout( partPanel, BoxLayout.PAGE_AXIS )); 
		partPanel.add( realButton ); 
		partPanel.add( imagButton ); 
		partPanel.add( magButton ); 
		partPanel.add( phaseButton ); 
		
		editorPanel.add( partPanel ); 
		editorPanel.setLayout(new GridLayout(2,2)); 
		add( editorPanel, BorderLayout.SOUTH ); 
		
		JPanel contentPanel = new JPanel(); 
		contentPanel.add( frequencyPanel );
		contentPanel.add( spatialPanel ); 
		add( contentPanel, BorderLayout.CENTER ); 
		createFilterTerm();
		calculate(); 
		display();
		
	}
	
	@Override
	public void calculate() {

		filter = FunctionFactory.createRadialFunction2D(filterTerm, dimensionX, dimensionY, zeroCentered, "filter");
		Function filtered = timesOp.create(filter, transform); 
		reconstructed = Transforms.fft2D(filtered, true, Transforms.NORMALIZE_ROOT_N); 
	}

	@Override
	public void display() { 
		
		//transform and filter
		frequencyPanel.display(filter.getReal(), logMagnitudeTransform, logMagnitudeTransform); 
		
		//inverse transform 
		if( realButton.isSelected() ) {
			
			spatialPanel.display(reconstructed.getReal());
			
		} else if( imagButton.isSelected() ) {
			
			
			spatialPanel.display(reconstructed.getImaginary()); 
			
		} else if( magButton.isSelected() ) {
			
			spatialPanel.display(reconstructed.getMagnitude()); 
			
		} else if( phaseButton.isSelected() ) {
			
			spatialPanel.display(reconstructed.getPhase()); 
		}
		
		
	}

}
