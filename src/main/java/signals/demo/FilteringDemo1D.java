package signals.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

import signals.core.Function;
import signals.core.Function1D;
import signals.core.FunctionFactory;
import signals.gui.plot.Function1DTabPanel;
import signals.gui.plot.Indices;
import signals.operation.Transforms;

@SuppressWarnings("serial")
public class FilteringDemo1D extends FilteringDemo {
	
	int dimension; 
	boolean zeroCentered; 
	double[] indices; 
	
	Function1DTabPanel spatialPanel, frequencyPanel; 
	
	public FilteringDemo1D(Function input) {
		super(input);
		
		editorPanel.setLayout(new GridLayout(3, 1)); 
		add( editorPanel, BorderLayout.EAST ); 
		
		transform = Transforms.fft1D(input, false, Transforms.NORMALIZE_ROOT_N); 
		
		dimension = ((Function1D)input).getDimension();
		zeroCentered = input.isZeroCentered(); 
		indices = Indices.indices1D(dimension, zeroCentered); 
		
		Dimension size = new Dimension( 700, 170 ); 
		spatialPanel = new Function1DTabPanel( size );
		spatialPanel.lockScale(input);
		frequencyPanel = new Function1DTabPanel( size );
		frequencyPanel.lockScale(transform); 
		
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

		filter = FunctionFactory.createFunction1D(filterTerm, dimension, zeroCentered, "Filter"); 
		Function filtered = timesOp.create(filter, transform); 
		reconstructed = Transforms.fft1D(filtered, true, Transforms.NORMALIZE_ROOT_N); 
		
	}

	@Override
	public void display() {
		
		//transform and filter
		frequencyPanel.removePlots(); 
		frequencyPanel.addFunction((Function1D)transform, "Fourier Transform"); 
		frequencyPanel.addFunction((Function1D)filter, "Frequency-Domain Filter"); 
		
		//inverse transform
		spatialPanel.removePlots(); 
		spatialPanel.addFunction((Function1D)reconstructed, "Filtering Result (Spatial Domain)"); 
		
	}

}
