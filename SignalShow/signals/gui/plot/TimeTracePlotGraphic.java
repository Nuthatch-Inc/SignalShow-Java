package signals.gui.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

@SuppressWarnings("serial")
public class TimeTracePlotGraphic extends ArgandPlotGraphic {

	public TimeTracePlotGraphic() {
		super();
		createDotTrailColors();
	}

	public TimeTracePlotGraphic(Dimension size) {
		super(size);
		createDotTrailColors();
	}


	public void setDirtyCursor() {
		
		dirtyCursor = true; 
	}
	
	public void createCursor() {

		double plotCursorX = plottingMath.mathToPlotX(cursorLocationX);
		double plotCursorY = plottingMath.mathToPlotY(cursorLocationY);
		cursor = new Line2D.Double( origin[0], origin[1], plotCursorX, plotCursorY ); 

		double[] x = dataSequences.get(0).xData; 
		double[] y = dataSequences.get(0).yData; 
		
		double[] plotCoords = null; 

		
		num_dots = Math.min(MAX_DOTS, x.length);
		for( int i = 0; i < num_dots; i++ ) { //newest first
			
			plotCoords = plottingMath.mathToPlotCoordinates ( x[i], y[i] );

			Ellipse2D.Double dot = new Ellipse2D.Double( plotCoords[0] - TRIAL_DOT_WIDTH, 
					plotCoords[1] - TRIAL_DOT_WIDTH, 2*TRIAL_DOT_WIDTH, 2*TRIAL_DOT_WIDTH ); 

			dotTrail[i] = dot; 
		}

		//create cursor dot
		cursorDot = new Ellipse2D.Double( plotCursorX - CURSOR_DOT_WIDTH, 
				plotCursorY - CURSOR_DOT_WIDTH, 2*CURSOR_DOT_WIDTH, 2*CURSOR_DOT_WIDTH ); 
	}


	public void drawDots( Graphics2D g2 ) {

		for( int i = num_dots-1; i >=0 ; i-- ) { //draw the lightest first

			Color color = dotTrailColors[i];
			g2.setColor(color); 	
			g2.fill(dotTrail[i]); 	

		}
	}

}
