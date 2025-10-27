package signals.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.ArrayList;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function1D;
import signals.gui.plot.Indices;
import signals.gui.plot.PlottingGraphics;
import signals.gui.plot.PlottingMath;

/**
 * A graphic representing Function1D. Both the real and imaginary parts are painted
 * @author Juliet
 *
 */
public class Function1DThumbnailGraphic extends PlotThumbnailGraphic {
	
	//various colors representing different parts of the plot
	protected Color imagColor;
	
	//the function that this thumbnail graphic represents
	Function function;
	
	double[] data2; //stores the imaginary part
	
	/**
	 * @param function
	 */
	public Function1DThumbnailGraphic(Function1D function) {
		super( Indices.indices1D( function.getDimension(), function.isZeroCentered() ) );
		this.function = function;
	}
	
	protected void resetColors() {
		
		super.resetColors();
		
		//real part
		plotColor = Core.getColors().getRealPlotColor();
		
		//imaginary part
		imagColor = Core.getColors().getImagPlotColor();
	
	}


	/**
	 * displays the real and imaginary parts of the function on the graphics context
	 * @param g2 a Graphics2D context on which to paint
	 * @param shapelist list of shapes created with the createShapeList method
	 */
	public void paintGraphic(Graphics2D g2, ArrayList<Shape> shapelist ) {
		
		//antialias graphics
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
						RenderingHints.VALUE_ANTIALIAS_ON );
	
		//draw the plot with real and imaginary parts
		g2.setColor( imagColor );
		g2.fill( shapelist.get( 3 ) ); //first plot: imaginary part
		
		super.paintGraphic(g2, shapelist);
	}
	
	/**
	 * creates graphics for the real an imaginary parts, x-axis, and y-axis of the function
	 * @param width width of the plot
	 * @param height height of the plot
	 * @param offset size of the empty border around the plot in pixels
	 * @return an arraylist of shapes that can be painted on a graph
	 */
	public ArrayList<Shape> createShapeList( int width, int height, int offset, PlottingMath plottingMath ) { 
	 
		ArrayList<Shape> graphics = super.createShapeList(width, height, offset, plottingMath);
		
		double[][] imagCoords = plottingMath.mathToPlotCoordinates ( indices, data2 );
		double[] origin = plottingMath.getOrigin();
		 
		graphics.add( PlottingGraphics.filledGraphic(imagCoords[0], imagCoords[1], origin[1]) ); 
		
		return graphics; 
		
	}

	public void setExtrema() {
		
		int dimension = indices.length;
		extrema = PlottingMath.contrastAdjustExtrema(PlottingMath.extrema(data2, PlottingMath.extrema(data)), 1);
		indexExtrema[0] = indices[0];
		indexExtrema[1] = indices[dimension-1];
	}

	@Override
	public void createData() {
		data = function.getReal();
		data2 = function.getImaginary();
	}

	/**
	 * @return the function
	 */
	public Function getFunction() {
		return function;
	}

	/**
	 * @param function the function to set
	 */
	public void setFunction(Function1D function) {
		this.function = function;
		setIndices( Indices.indices1D( function.getDimension(), function.isZeroCentered() ) );
	}
}
