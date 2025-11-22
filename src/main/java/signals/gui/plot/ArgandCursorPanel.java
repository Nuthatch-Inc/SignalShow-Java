package signals.gui.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JLabel;

import signals.core.Constants.PlotStyle;

@SuppressWarnings("serial")
public class ArgandCursorPanel extends PlotCursorPanel {

	double[] real, imag; 

	int stepIndex; 
	
	public ArgandCursorPanel(Dimension plotSize) {
		super(plotSize);
		labels = new ArrayList<JLabel>();
	}

	public void setFunction( double[] real, double[] imag ) {

		((ArgandPlotGraphic)graphic).initialize(); 
		this.real = real;  
		this.imag = imag; 
		cursorModeOn = true;
		cursorIndex = 0;
		graphic.setDefaultPlotStyle(PlotStyle.SMOOTH); 
		graphic.add(real, imag, "" ); 
		int color = ArgandPlotGraphic.LIGHT_GRAY;
		graphic.getDataSequences().get(0).setColor(new Color(color, color, color)); 
		((ArgandPlotGraphic)graphic).setCursorLocation( real[cursorIndex], imag[cursorIndex] );
		graphic.setCursorOn( true );
		stepIndex = 0; 
	}

	public void createGraphic( Dimension plotSize ) {

		graphic = new ArgandPlotGraphic( plotSize );
	}

	public void setCursorLabels() {

	}

	public void moveLeft() {

		cursorIndex = cursorIndex == 0 ? (real.length-1) : cursorIndex - 1; 
		((ArgandPlotGraphic)graphic).setCursorLocation( real[cursorIndex], imag[cursorIndex] );

	}

	public void moveRight() {
		
		stepIndex = ( stepIndex + 1 ) % 4;

		if( stepIndex == 0 ) {
		
			cursorIndex = cursorIndex == (real.length-1) ? 0 : cursorIndex + 1; 
			((ArgandPlotGraphic)graphic).setCursorLocation( real[cursorIndex], imag[cursorIndex] );
		}
		
	}

}
