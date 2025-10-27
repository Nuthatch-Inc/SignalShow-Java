package signals.gui.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

@SuppressWarnings("serial")
public class ArgandPlotGraphic extends PlotGraphic {

	//dot representing current position of cursor
	Ellipse2D.Double cursorDot;

	//trail of dots that grows fainter over time (circular buffer) 
	Ellipse2D.Double[] dotTrail; 
	Color[] dotTrailColors; 

	double cursorLocationX, cursorLocationY; 

	public static final int TRIAL_DOT_WIDTH = 4; 
	public static final int CURSOR_DOT_WIDTH = 6; 
	public static final int MAX_DOTS = 48; 
	public static final int INCREMENT = 5; 
	public static final int LIGHT_GRAY = INCREMENT * MAX_DOTS; 

	int light_gray; 
	int newest_dot_index; 
	int num_dots; 
	boolean buffer_full; 

	public ArgandPlotGraphic() {
		super();
		createDotTrailColors();
	}

	public ArgandPlotGraphic(Dimension size) {
		super(size);
		createDotTrailColors();
	}
	
	public void initialize() {
		
		removeAll();
		cursorDot = new Ellipse2D.Double(0,0,0,0);
		newest_dot_index = -1; 
		buffer_full = false; 
		dotTrail = new Ellipse2D.Double[MAX_DOTS];
	}

	public void createDotTrailColors() {

		initialize(); 
		dotTrailColors = new Color[MAX_DOTS];  

		for( int i = 0; i < MAX_DOTS; i++ ) { 

			dotTrailColors[i] = new Color(i*INCREMENT, i*INCREMENT, i*INCREMENT); //colors: darkest -> lightest

		}

	}

	//Do not add mouse listeners
	public void addMouseListeners() {}

	public void setCursorLocation( double cursorLocationX, double cursorLocationY ) {

		this.cursorLocationX = cursorLocationX; 
		this.cursorLocationY = cursorLocationY; 
		dirtyCursor = true; 
		repaint(); 
	}

	public void createCursor() {

		double plotCursorX = plottingMath.mathToPlotX(cursorLocationX);
		double plotCursorY = plottingMath.mathToPlotY(cursorLocationY);
		cursor = new Line2D.Double( origin[0], origin[1], plotCursorX, plotCursorY ); 

		//add dot to the dot trail
		Ellipse2D.Double dot = new Ellipse2D.Double( plotCursorX - TRIAL_DOT_WIDTH, 
				plotCursorY - TRIAL_DOT_WIDTH, 2*TRIAL_DOT_WIDTH, 2*TRIAL_DOT_WIDTH ); 

		newest_dot_index++; 
		if( newest_dot_index == MAX_DOTS ) {

			buffer_full = true; 
			newest_dot_index = 0; 
		}

		dotTrail[newest_dot_index] = dot;  //dots: circular buffer. most recent is located at (newest_dot_index) lower values = older

		//create cursor dot
		cursorDot = new Ellipse2D.Double( plotCursorX - CURSOR_DOT_WIDTH, 
				plotCursorY - CURSOR_DOT_WIDTH, 2*CURSOR_DOT_WIDTH, 2*CURSOR_DOT_WIDTH ); 
	}

	public void drawCursor( Graphics2D g2 ) { 

		drawDots( g2 ); 
		g2.setColor(cursorColor);
		g2.fill(cursorDot);
		super.drawCursor(g2); 

	}

	public void drawDots( Graphics2D g2 ) {

		if( buffer_full ) { //buffer has filled up at least once

			int dot_idx = newest_dot_index; 

			for( int i = dotTrailColors.length-1; i >=0 ; i-- ) { //draw the lightest first

				Color color = dotTrailColors[i];
				g2.setColor(color); 	
				dot_idx = (dot_idx + 1) % MAX_DOTS; 
				g2.fill(dotTrail[dot_idx]); 	

			}

		} else { //buffer has never filled up

			for( int i = 0; i <= newest_dot_index; i++ ) {

				Color color = dotTrailColors[newest_dot_index-i]; 
				g2.setColor(color); 	
				g2.fill(dotTrail[i]); 
			}

		}
	}

}
