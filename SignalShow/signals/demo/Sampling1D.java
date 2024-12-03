package signals.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;


import signals.core.Constants;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.FunctionFactory;
import signals.functionterm.RectangleFunctionTerm1D;
import signals.gui.ParameterEditor;
import signals.gui.ParameterUser;
import signals.gui.ResizablePane;
import signals.gui.plot.Function1DTabPanel;
import signals.operation.FourierTransformOp;
import signals.operation.SampleOp1D;
import signals.operation.ScaleOp;
import signals.operation.TimesOp;

@SuppressWarnings("serial")
public class Sampling1D extends JPanel implements ResizablePane, ParameterUser {

	Function1D input; 

	Function1D filter; 
	RectangleFunctionTerm1D rect; 

	Function1DTabPanel spatialPanel, frequencyPanel; 

	SampleOp1D samplingOp; 
	FourierTransformOp transformOp;
	FourierTransformOp inverseTransformOp;
	TimesOp timesOp; 
	ScaleOp scaleOp; 
	
	ParameterEditor filterspinner; 

	JCheckBox reconstructCB; 

	public Sampling1D( Function1D input ) {
		
		this.input = input; 

		//toolbar
		JPanel toolbar = new JPanel(); 

		Dimension size = new Dimension( 800, 170 ); 
		spatialPanel = new Function1DTabPanel( size );
		frequencyPanel = new Function1DTabPanel( size );

		reconstructCB = new JCheckBox( "Reconstruct Signal" ); 
		reconstructCB.setSelected(false); 
		reconstructCB.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				filterspinner.setEnabled( reconstructCB.isSelected() );
				sample(); 
			}
			
		}); 

		rect = (RectangleFunctionTerm1D) new RectangleFunctionTerm1D().getDefaultInstance(); 
		filter = (Function1D) FunctionFactory.createFunction1D(rect, input.getDimension(), input.isZeroCentered(), "filter"); 

		SpinnerNumberModel model = new SpinnerNumberModel( 1, 1, 1000, 1); 
		ParameterEditor spinner = new ParameterEditor( 3, model, 0, this ); 
		
		rect.setWidth(input.getDimension()/2); 
		SpinnerNumberModel filtermodel = new SpinnerNumberModel( input.getDimension(), 1, 100000, 1); 
		filterspinner = new ParameterEditor( 3, filtermodel, 1, this ); 
		filterspinner.setEnabled(false); 

		JLabel spinnerLabel = new JLabel( "sampling period (pixels):" );  
		spinnerLabel.setLabelFor(spinner); 
		
		JLabel filterWidthLabel = new JLabel( "filter width: " );  
		spinnerLabel.setLabelFor(spinner); 
		
		toolbar.add( Box.createHorizontalGlue() ); 
		toolbar.add( spinnerLabel ); 
		toolbar.add( spinner ); 
		toolbar.add( Box.createHorizontalGlue() ); 
		toolbar.add( filterWidthLabel ); 
		toolbar.add( filterspinner ); 
		toolbar.add( Box.createHorizontalGlue() ); 
		toolbar.add( reconstructCB );
		toolbar.add( Box.createHorizontalGlue() ); 
		samplingOp = new SampleOp1D(); 
		transformOp = new FourierTransformOp(); 
		inverseTransformOp = new FourierTransformOp(); 
		inverseTransformOp.setInverse(true); 
		timesOp = new TimesOp();  
		scaleOp = new ScaleOp(); 

		//panels
		JPanel contentPanel = new JPanel(); 
		contentPanel.add( spatialPanel ); 
		contentPanel.add( frequencyPanel );
		setLayout( new BorderLayout() ); 
		add( contentPanel, BorderLayout.CENTER ); 
		add( toolbar, BorderLayout.NORTH ); 

		sample(); 
	}

	public void parameterChanged(int index, String newVal) {
		
		switch (index) {

		case 0: //sampling period
			
			int newPeriod = Integer.parseInt(newVal);
			if( newPeriod >= 1 ) {
				samplingOp.setSamplingPeriod(newPeriod); 
				scaleOp.setValue(0, newPeriod); 
				
				//set the default width according to the sampling period
				int newWidth = input.getDimension() / samplingOp.getSamplingPeriod(); 
				filterspinner.setText(""+newWidth); 
				
				sample(); 
			}
			
			break; 
			
		case 1: //filter width 
		
			double newWidth = Double.parseDouble(newVal);
			if( newWidth >= 0 ) {
				filter.freeMemory();
				rect.setWidth(newWidth/2); //halfwidth defined
				sample(); 
			}
			
			break; 

		}


	}

	public void sample() {

		//sample the input with the correct sampling period
		Function sampled = samplingOp.create(input); 
		Function transformSampled = transformOp.create(sampled); 

		//display the sampled function and its FFT
		spatialPanel.removePlots();  
		spatialPanel.addFunction((Function1D)sampled, "sampled");
		spatialPanel.addFunction((Function1D)input, "input"); 
		spatialPanel.setPlotStyle(1, Constants.PlotStyle.SMOOTH);
		spatialPanel.setPlotStyle(0, Constants.PlotStyle.HISTOGRAM);

		frequencyPanel.removePlots(); 
		frequencyPanel.addFunction( (Function1D)transformSampled, "FFT(sampled)"); 

		if( reconstructCB.isSelected() ) {

			//filter the signal
			Function filteredFD = timesOp.create(filter, transformSampled);
			Function reconstruction = inverseTransformOp.create(filteredFD); 
			reconstruction = scaleOp.create(reconstruction); 

			//draw the filter
			frequencyPanel.addFunction( filter, "filter"); 

			//draw the reconstruction 
			spatialPanel.addFunction( (Function1D)reconstruction, "IFFT(filtered)"); 
			spatialPanel.setPlotStyle(2, Constants.PlotStyle.SMOOTH);
		}

	}

	public void sizeChanged() {
		// TODO Auto-generated method stub

	} 

}
