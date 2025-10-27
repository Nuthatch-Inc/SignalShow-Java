package signals.gui.datagenerator;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;

import signals.core.Function;
import signals.core.OperatorSystem;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public abstract class OperatorSystemToolBar extends DataGeneratorToolBar {

	OperatorSystem system; 
	JToggleButton editButton; 

	public static final String EDIT = "EDIT"; 
	
	public OperatorSystemToolBar(OperatorSystem system) {
		super();
		this.system = system;
	}
	
	

	@Override
	public void setShowableState(String state) {
		
		system.setShowState(state); 
	}

	public void addButtons() {

		editButton = new JToggleButton( new editAction() ); 
		buttons.add( editButton ); 
		super.addButtons(); 
	}

	public void selectButton(String buttonCode) {
		
		if( buttonCode.equals(PLOT) ) {
			
			plotButton.setSelected(true); 
			plot(); 
			
		} else if( buttonCode.equals(FFT) ) {
			
			fftButton.setSelected(true); 
			plotFFT(); 
			
		} else if( buttonCode.equals(EDIT) ) {
			
			editButton.setSelected(true); 
			edit();
			
		} else if( buttonCode.equals(HISTOGRAM) ) {
			
			histogramButton.setSelected(true); 
			plotHistogram();
		}

	}
	
	@Override
	public String getFFTTitle() {
		
		return "FFT of Result";
	}

	@Override
	public String getHistogramTitle() {
	
		return "Histogram of Result";
	}

	@Override
	public String getPlotTitle() {
	
		return "Plot of Result";
	}
	
	public abstract void edit(); 

	public void GUIEventOccurred(GUIEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public Function getFunction() {
	
		return system.getFunction(); 
	}

	public class editAction extends AbstractAction {

		public editAction() {

			super("Edit System", ResourceLoader.createImageIcon("/guiIcons/editA.png"));
		}

		public void actionPerformed(ActionEvent e) {

			edit(); 

		}

	}

}
