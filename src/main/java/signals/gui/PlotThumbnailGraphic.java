package signals.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.ArrayList;

import signals.core.Constants;
import signals.core.Core;
import signals.gui.plot.PlottingGraphics;
import signals.gui.plot.PlottingMath;

/**
 * A graphic for a simple plot with one data sequence. It can be painted in two sizes: small and large.
 * @author Juliet
 *
 */
public abstract class PlotThumbnailGraphic extends ThumbnailGraphic {

	protected Color backgroundColor;
	protected Color plotColor;
	protected Color axesColor;
	protected ArrayList<Shape> largeShapeList;
	protected ArrayList<Shape> smallShapeList;
	protected double[] data;
	protected double[] indices;
	protected double[] extrema;
	protected double[] indexExtrema;
	protected PlottingMath largePlottingMath;
	PlottingMath smallPlottingMath;
	protected boolean autofit;
	protected Constants.PlotStyle plotStyle;

	public PlotThumbnailGraphic( double[] indices ) {
		this.indices = indices;
		setDirty();
		resetColors();
		autofit = true; 
		indexExtrema = new double[2]; 
		extrema = new double[2];
		plotStyle = Constants.PlotStyle.FILLED;
	}

	/**
	 * @return the plotStyle
	 */
	public Constants.PlotStyle getPlotStyle() {
		return plotStyle;
	}

	/**
	 * @param plotStyle the plotStyle to set
	 */
	public void setPlotStyle(Constants.PlotStyle plotStyle) {
		this.plotStyle = plotStyle;
	}

	/**
	 * @return the extrema
	 */
	public double[] getExtrema() {
		return extrema;
	}

	/**
	 * @param indices the indices to set
	 */
	public void setIndices(double[] indices) {
		this.indices = indices;
		setDirty();
	}

	protected void resetColors() {
		
		//axes
		axesColor = Core.getColors().getAxesColor();
		
		//real part
		plotColor = Core.getColors().getRealPlotColor();
		
	}

	@Override
	public void paintLargeGraphic(Graphics2D g) {
		
		Dimension d = getLargeDimension();
		
		//if the large graphic is "dirty", create it
		if( dirtyLargeGraphic ) {
			
			createData();
			if( largePlottingMath == null ) largePlottingMath = 
				new PlottingMath( d.width, d.height, OFFSET );
			largeShapeList = createShapeList( d.width, d.height, OFFSET, largePlottingMath ); 
			dirtyLargeGraphic = false; 
		}
		
		//paint the graphic
		paintGraphic( g, largeShapeList ); 
		
	}

	@Override
	public void paintSmallGraphic(Graphics2D g) {
		
		Dimension d = getSmallDimension();
		
		//if the small graphic is "dirty", create it
		if( dirtySmallGraphic ) {
			
			createData(); 
			if( smallPlottingMath == null ) smallPlottingMath = 
				new PlottingMath( d.width, d.height, OFFSET );
			smallShapeList = createShapeList( d.width, d.height, OFFSET, smallPlottingMath ); 
			dirtySmallGraphic = false; 
		}
		
		//paint the graphic
		paintGraphic( g, smallShapeList ); 
		
	}

	/**
	 * displays the real and imaginary parts of the function on the graphics context
	 * @param g2 a Graphics2D context on which to paint
	 * @param shapelist list of shapes created with the createShapeList method
	 */
	public void paintGraphic(Graphics2D g2, ArrayList<Shape> shapelist) {
		
		//antialias graphics
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
						RenderingHints.VALUE_ANTIALIAS_ON );
	
		g2.setColor( plotColor ); 
		g2.fill( shapelist.get( 2 ) ); //TODO: add draw option
				
		//draw the axes
		g2.setStroke( new BasicStroke( 1 ) );
		g2.setColor( axesColor );
		g2.draw( shapelist.get( 0 ) ); //xAxis
		g2.draw( shapelist.get( 1 ) ); ///yAxis
	}
	
	public void paintGraphic(Graphics2D g, int width, int height) {
		
		//if the large graphic is "dirty", create it
		if( dirtySmallGraphic ) {
			
			createData();
			smallPlottingMath = 
				new PlottingMath( width, height, OFFSET );
			smallShapeList = createShapeList( width, height, OFFSET, smallPlottingMath ); 
			dirtySmallGraphic = false; 
		}
		
		//paint the graphic
		paintGraphic( g, smallShapeList ); 
		
	}

	public abstract void createData();
	
	public void setExtrema() {
		
		int dimension = indices.length;
		extrema = PlottingMath.contrastAdjustExtrema(PlottingMath.extrema(data), 1 );
		indexExtrema[0] = indices[0];
		indexExtrema[1] = indices[dimension-1];
	}

	/**
	 * creates graphics for the real an imaginary parts, x-axis, and y-axis of the function
	 * @param width width of the plot
	 * @param height height of the plot
	 * @param offset size of the empty border around the plot in pixels
	 * @return an arraylist of shapes that can be painted on a graph
	 */
	public ArrayList<Shape> createShapeList(int width, int height, int offset, PlottingMath plottingMath) { 
		
		//autofit the plot
		if( autofit ) { 
			
			setExtrema();
			plottingMath.setWindow( indexExtrema, extrema );
		}
	
		double[] origin = plottingMath.getOrigin();
	
		//create the graphics 
		ArrayList<Shape> graphics = new ArrayList<Shape>(); 
		graphics.add( PlottingGraphics.xAxis( width, origin[1], offset ) ); 
		graphics.add( PlottingGraphics.yAxis( height, origin[0], offset ) ); 
		
		double[][] plotCoords = plottingMath.mathToPlotCoordinates ( indices, data );
		
		switch( plotStyle ) {
	
		case FILLED: 
			graphics.add( PlottingGraphics.filledGraphic(plotCoords[0], plotCoords[1], origin[1]) ); 
			break; 
		case HISTOGRAM:
			graphics.add( PlottingGraphics.histogramGraphic(plotCoords[0], plotCoords[1], origin[1]) ); 
			break;
		case SMOOTH: 
			graphics.add( PlottingGraphics.smoothGraphic(plotCoords[0], plotCoords[1]) ); 
			break; 
		case STEM: 
			graphics.add( PlottingGraphics.stemGraphic(plotCoords[0], plotCoords[1], origin[1], 3) ); 
			break;
		case SCATTER:
			graphics.add( PlottingGraphics.scatterGraphic(plotCoords[0], plotCoords[1], 3) ); 
			break;
		}
		
		return graphics; 
		
	}

	/**
	 * @return the autofit
	 */
	public boolean isAutofit() {
		return autofit;
	}

	/**
	 * @param autofit the autofit to set
	 */
	public void setAutofit(boolean autofit) {
		this.autofit = autofit;
	}

}