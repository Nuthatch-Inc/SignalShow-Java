package signals.gui.plot;

import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Creates graphics used for plots. 
 * Currently implemented:
 * -filled graphic
 * -smooth graphic 
 * TODO: 
 * -stem lines 
 * -various shapes
 * @author Juliet
 *
 */
public class PlottingGraphics {

	/**
	 * Given plotting coordinates, creates a smooth-line graphic
	 * that can be painted on a graph
	 * @param x x-coordinates to plot
	 * @param y y-coordinates to plot
	 * @return the graphic that can be painted using draw
	 */
	public static GeneralPath smoothGraphic(double[] x, double[] y ) {
		
		GeneralPath graphic = new GeneralPath(); 
	
		graphic.moveTo( (float)x[0], (float)y[0] ); 
		
		int dimension = x.length;
	
		for( int i = 0; i < dimension; i++ ) {
		
			graphic.lineTo( (float)x[i], (float)y[i] ); 
		}
	
		return graphic; 
	}
	
	/**
	 * Given plotting coordinates, creates a filled graphic
	 * that can be painted on a graph
	 * @param x x-coordinates to plot
	 * @param y y-coordinates to plot
	 * @param y0 the y-coordinate of the origin in plotting coordinates
	 * @return the graphic that can be painted using fill
	 */
	public static GeneralPath filledGraphic( double[] x, double[] y, double y0 ) {
		
		GeneralPath graphic = new GeneralPath();
	
		graphic.moveTo( (float)x[0], (float)y0 ); 
		
		int dimension = x.length;
	
		for( int i = 0; i < dimension; i++ ) {
		
			graphic.lineTo( (float)x[i], (float)y[i] ); 
		}
		
		graphic.lineTo( (float)x[dimension-1], (float)y0 );
		graphic.lineTo( (float)x[0], (float)y0 ); 
		
		return graphic; 
	}
	
	/**
	 * Given plotting coordinates, creates a histogram filled graphic
	 * that can be painted on a graph
	 * @param x x-coordinates to plot
	 * @param y y-coordinates to plot
	 * @param y0 the y-coordinate of the origin in plotting coordinates
	 * @return the graphic that can be painted using fill
	 */
	public static GeneralPath histogramGraphic( double[] x, double[] y, double y0 ) {
		
		GeneralPath graphic = new GeneralPath();
		
		int dimension = x.length;
		double gap = x[1]-x[0]; //TODO: can cause error for histogram graphic
		for( int i = 0; i < dimension; i++ ) {
			
			if( y[i] < y0 ) {
			
				graphic.append( new Rectangle2D.Double( 
					x[i], y[i], gap, y0 - y[i]  ), false ); 
				
			} else { //value is negative: draw down from axis to point
				
				graphic.append( new Rectangle2D.Double( 
					x[i], y0, gap, y[i] - y0  ), false ); 
				
			}
			
		}
	
		return graphic;
		
	}
	
	/**
	 * Given plotting coordinates, creates a step draw graphic
	 * that can be painted on a graph
	 * @param x x-coordinates to plot
	 * @param y y-coordinates to plot
	 * @param y0 the y-coordinate of the origin in plotting coordinates
	 * @param size the size of the X
	 * @return the graphic that can be painted using draw
	 */
	public static GeneralPath stemGraphic( double[] x, double[] y, double y0, double size ) {
		
		GeneralPath graphic = new GeneralPath(); 
		
		int dimension = x.length;
	
		for( int i = 0; i < dimension; i++ ) {
		
			graphic.append( new Line2D.Double( 
					x[i]-size, y[i]+size, x[i]+size, y[i]-size  ), false ); 
			
			graphic.append( new Line2D.Double( 
					x[i]-size, y[i]-size, x[i]+size, y[i]+size  ), false ); 
			
			graphic.append( new Line2D.Double( 
						x[i], y[i], x[i], y0  ), false ); 
		}
		
		return graphic; 
			
	}
	
	/**
	 * Given plotting coordinates, creates a step draw graphic
	 * that can be painted on a graph
	 * @param x x-coordinates to plot
	 * @param y y-coordinates to plot
	 * @param size the size of the X
	 * @return the graphic that can be painted using draw
	 */
	public static GeneralPath scatterGraphic( double[] x, double[] y, double size ) {
		
		GeneralPath graphic = new GeneralPath(); 
		
		int dimension = x.length;
		
		for( int i = 0; i < dimension; i++ ) {
		
			graphic.append( new Ellipse2D.Double( 
						x[i] - size, y[i] - size, 2 * size, 2 * size  ), false ); 
		}
		
		return graphic;
		
	}
	
	/**
	 * Creates a graphic for the x-axis
	 * @param width width of the plot
	 * @param y0 y-coordinate of the origin in plotting coordinates
	 * @param offset empty border around the plot in pixels
	 * @return a line representing the x-axis
	 */
	public static Line2D.Double xAxis( int width, double y0, int offset ) {
	
		return new Line2D.Double( 0.0, y0, ( width -1 ) + 2*offset, y0 );
		
	}
	
	/**
	 * Creates a graphic for the y-axis
	 * @param height height of the plot
	 * @param x0 x-coordinate of the origin in plotting coordinates
	 * @param offset empty border around the plot in pixels
	 * @return a line representing the y-axis
	 */
	public static Line2D.Double yAxis( int height, double x0, int offset ) {
		
		return new Line2D.Double( x0, 0.0, x0, ( height -1 ) + 2*offset );	
	}
	
	public static GeneralPath createTicksY( ArrayList<Integer> ticksYLocations, double x0 ) {
		
		return createTicksY( ticksYLocations, x0, 0 );
	}
	
	/**
	 * Creates a graphic representing tick marks for the Y-axis
	 * @param ticksYLocations pixel locations of the tick marks
	 * @param x0 x-coordinate of the origin in plotting coordinates
	 * @return a graphic representing tick marks for the Y-axis
	 */
	public static GeneralPath createTicksY( ArrayList<Integer> ticksYLocations, double x0, int offset  ) {
		
		GeneralPath tickMarksY = new GeneralPath();
		for( Integer markY : ticksYLocations ) {
			
			tickMarksY.append( new Line2D.Double( x0 - 5, markY +offset, x0 + 5, markY+offset ), false );
		}
		
		return tickMarksY; 
	}
	
	/**
	 * Creates a graphic representing tick marks for the X-axis
	 * @param ticksXLocations pixel locations of the tick marks
	 * @param x0 x-coordinate of the origin in plotting coordinates
	 * @return a graphic representing tick marks for the X-axis
	 */
	public static GeneralPath createTicksX( ArrayList<Integer> ticksXLocations, double y0 ) {
		
		GeneralPath tickMarksX = new GeneralPath();
		for( Integer markX : ticksXLocations ) {
		
			tickMarksX.append( new Line2D.Double( markX, y0 - 5, markX, y0 + 5  ), false );
		}
		
		return tickMarksX; 
	}
	
	/**
	 * Creates a graphic representing the horizontal grid lines
	 * @param ticksYLocations pixel locations of the tick marks
	 * @param width width of the plot in pixels
	 * @return a graphic representing the horizontal grid lines
	 */
	public static GeneralPath createGridH( ArrayList<Integer> ticksYLocations, double width ) {
		
		GeneralPath gridlinesH = new GeneralPath();
		for( Integer markY : ticksYLocations ) {
		
			gridlinesH.append( new Line2D.Double( 0, markY, width, markY  ), false );
		}
		
		return gridlinesH; 
	}
	
	/**
	 * Creates a graphic representing the horizontal grid lines
	 * @param ticksXLocations pixel locations of the tick marks
	 * @param height height of the plot in pixels
	 * @return a graphic representing the vertical grid lines
	 */
	public static GeneralPath createGridV( ArrayList<Integer> ticksXLocations, double height ) {
		
		GeneralPath gridlinesV = new GeneralPath();
		for( Integer markX : ticksXLocations ) {
	
			gridlinesV.append( new Line2D.Double( markX, 0, markX, height  ), false );
		}
		
		return gridlinesV; 
	}
	
}
