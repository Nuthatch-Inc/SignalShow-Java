package signals.gui.datagenerator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import signals.core.Core;
import signals.core.Function;
import signals.gui.plot.HistogramCursorPanel;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public abstract class DataGeneratorToolBar extends JToolBar {

	protected ButtonGroup buttonGroup;
	JToggleButton plotButton;
	JToggleButton fftButton;
	JToggleButton histogramButton;
	protected ArrayList<JToggleButton> buttons;
	public static final String PLOT = "PLOT";
	public static final String FFT = "FFT";
	public static final String HISTOGRAM = "HISTOGRAM";
	protected String openTab;
	protected Component content;

	public DataGeneratorToolBar() {
		super();
		buttons = new ArrayList<JToggleButton>(); 
		setFloatable( false ); 
		buttonGroup = new ButtonGroup(); 
		addButtons(); 
		finalizeButtons(); 
		setBorder( BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY)));
	}

	public void setContent( Component c, String tab ) {
		
		Core.getGUI().setContent(c); 
		openTab = tab; 
		content = c; 
		setShowableState( openTab ); 
	
	}
	
	public void addButtons() {
		
	
		plotButton = new JToggleButton( new PlotAction() ); 
		fftButton = new JToggleButton( new PlotFFTAction() ); 
		histogramButton = new JToggleButton( new PlotHistogramAction() ); 
	
		buttons.add( plotButton); 
		buttons.add( fftButton); 
		buttons.add( histogramButton ); 
	
	}
	
	public abstract void setShowableState(String state); 
	public abstract String getPlotTitle(); 
	public abstract String getFFTTitle(); 
	public abstract String getHistogramTitle(); 

	public void finalizeButtons() {
		
		Font font = Core.getDisplayOptions().getLabelFont(); 
		
		for( JToggleButton button: buttons ) {
			
			buttonGroup.add(button); 
			button.setFont(font); 
			add( button ); 
		}
		
	}

	public void selectButton(String buttonCode) {
			
	
		if( buttonCode.equals(PLOT)) {
			
			plotButton.setSelected(true); 
			plot(); 
			
		} else if( buttonCode.equals(FFT)) {
			
			fftButton.setSelected(true); 
			plotFFT(); 
		}
		
	}

	public abstract void plot();

	public abstract void plotFFT();
	
	public abstract Function getFunction(); 

	public void plotHistogram() {
		
		HistogramCursorPanel plot = new HistogramCursorPanel(new Dimension( 500, 500 ));
		plot.showToolBar(); 
		plot.setPlot( getFunction() );
		setContent(plot, HISTOGRAM);
	
	}
	
	public class PlotAction extends AbstractAction {

		public PlotAction() {

			super(getPlotTitle(), ResourceLoader.createImageIcon("/guiIcons/plotFunction.png"));
			putValue( ACCELERATOR_KEY, 
					KeyStroke.getKeyStroke( KeyEvent.VK_P, ActionEvent.CTRL_MASK ) );
		}

		public void actionPerformed(ActionEvent e) {

			plot(); 

		}

	}
	
	public class PlotFFTAction extends AbstractAction {

		public PlotFFTAction() {

			super(getFFTTitle(), ResourceLoader.createImageIcon("/guiIcons/plotFFT.png"));
			putValue( ACCELERATOR_KEY, 
					KeyStroke.getKeyStroke( KeyEvent.VK_T, ActionEvent.CTRL_MASK ) );
		}

		public void actionPerformed(ActionEvent e) {

			plotFFT(); 

		}

	}
	
	public class PlotHistogramAction extends AbstractAction {

		public PlotHistogramAction() {

			super(getHistogramTitle(), ResourceLoader.createImageIcon("/guiIcons/plotHistogram.png"));
			putValue( ACCELERATOR_KEY, 
					KeyStroke.getKeyStroke( KeyEvent.VK_H, ActionEvent.CTRL_MASK ) );
		}

		public void actionPerformed(ActionEvent e) {

			plotHistogram(); 

		}

	}

}