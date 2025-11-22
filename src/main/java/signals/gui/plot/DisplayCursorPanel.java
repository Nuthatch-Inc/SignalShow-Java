package signals.gui.plot;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class DisplayCursorPanel extends JPanel {

	protected JPanel titleBar;
	protected JPanel toolBar;
	protected boolean hasToolBar;
	boolean hasTitleBar;
	protected boolean cursorModeOn;
	protected ArrayList<Component> titlebarComponents;
	protected JCheckBox unwrapPhaseCheckBox;
	protected JCheckBox logMagnitudeCheckBox;
	protected JCheckBox squaredMagnitudeCheckBox;
	protected Dimension displayDimension;

	public DisplayCursorPanel() {
		super();
	}
	
	public abstract void sizeChanged(); 

	public DisplayCursorPanel(LayoutManager arg0) {
		super(arg0);
	}

	public DisplayCursorPanel(boolean arg0) {
		super(arg0);
	}

	public DisplayCursorPanel(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
	}

	/**
	 * @return the phaseUnwrapped
	 */
	public boolean isPhaseUnwrapped() {
		
		if( unwrapPhaseCheckBox == null ) return false; 
		return unwrapPhaseCheckBox.isSelected();
	}

	/**
	 * @param phaseUnwrapped the phaseUnwrapped to set
	 */
	public void setPhaseUnwrapped(boolean phaseUnwrapped) {
		if( unwrapPhaseCheckBox != null ) unwrapPhaseCheckBox.setSelected(phaseUnwrapped);
	}

	/**
	 * @return the logMagnitude
	 */
	public boolean isLogMagnitude() {
		
		if( logMagnitudeCheckBox == null ) return false; 
		return logMagnitudeCheckBox.isSelected();
	}

	/**
	 * @param logMagnitude the logMagnitude to set
	 */
	public void setLogMagnitude(boolean logMagnitude) {
		if( logMagnitudeCheckBox != null ) logMagnitudeCheckBox.setSelected(logMagnitude);
	}

	public boolean isSquaredMagnitude() {
		
		if( squaredMagnitudeCheckBox == null ) return false; 
		return squaredMagnitudeCheckBox.isSelected();
	}

	public void setSquaredMagnitude(boolean squaredMagnitude) {
		if( squaredMagnitudeCheckBox != null ) squaredMagnitudeCheckBox.setSelected(squaredMagnitude);
	}

	public abstract void setCursorLabels(); 
	
	public abstract void setTitleLabels();

}