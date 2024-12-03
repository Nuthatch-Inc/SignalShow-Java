package signals.gui.datagenerator;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;

import signals.core.Function;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public abstract class FunctionToolBar extends DataGeneratorToolBar implements GUIEventListener {

	protected Function function;
	JToggleButton editPartAButton, editPartBButton;
	protected boolean newStateA;
	protected boolean newStateB;
	public static final String EDIT_A = "EDITA";
	public static final String EDIT_B = "EDITB";

	public FunctionToolBar( Function function ) {

		this(); 
		this.function = function; 
		newStateA = newStateB = false; 
	}
	
	@Override
	public Function getFunction() {
		return function;
	}
	
	public FunctionToolBar() {

		super();
		newStateA = newStateB = true; 
		
	}
	
	public boolean isNewStateA() {
		return newStateA;
	}

	public void setNewStateA(boolean newStateA) {
		this.newStateA = newStateA;
	}

	public boolean isNewStateB() {
		return newStateB;
	}

	public void setNewStateB(boolean newStateB) {
		this.newStateB = newStateB;
	}

	@Override
	public void setShowableState(String state) {
		
		function.setShowState(state); 
	}

	public boolean isEditable() {
		
		return true; 
	}
	
	public void setNewState( boolean tf ) {
		
		newStateA = newStateB = tf; 
	}
	
	@Override
	public String getFFTTitle() {
		
		return "FFT";
	}

	@Override
	public String getHistogramTitle() {
	
		return "Histogram";
	}

	@Override
	public String getPlotTitle() {
	
		return "Plot";
	}
	
	public void addButtons() {
		
		editPartAButton = new JToggleButton( new editPartAAction() ); 
		editPartBButton = new JToggleButton( new editPartBAction() ); 
		buttons.add( editPartAButton ); 
		buttons.add( editPartBButton );
		super.addButtons(); 
	}
	
	public void selectButton(String buttonCode) {
	
		if( buttonCode.equals(PLOT) ) {
			
			plotButton.setSelected(true); 
			plot(); 
			
		} else if( buttonCode.equals(EDIT_A)) {
			
			editPartAButton.setSelected(true); 
			editPartA();
			
		} else if( buttonCode.equals(EDIT_B)) {
			
			editPartBButton.setSelected(true); 
			editPartB();
			
		} else if( buttonCode.equals(FFT)) {
			
			fftButton.setSelected(true); 
			plotFFT(); 
		} 
		
	}

	public abstract String getPartAName();

	public abstract void editPartA(); 

	public abstract String getPartBName();

	public abstract void editPartB(); 
	
	public class editPartAAction extends AbstractAction {

		public editPartAAction() {

			super("Edit " + getPartAName(), ResourceLoader.createImageIcon("/guiIcons/editA.png"));
		}

		public void actionPerformed(ActionEvent e) {
			
			editPartA(); 

		}

	}
	
	public class editPartBAction extends AbstractAction {

		public editPartBAction() {

			super("Edit " + getPartBName(), ResourceLoader.createImageIcon("/guiIcons/editB.png"));
		}

		public void actionPerformed(ActionEvent e) {
			
			editPartB(); 

		}

	}

}