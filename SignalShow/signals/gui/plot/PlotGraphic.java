package signals.gui.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import signals.core.Constants;
import signals.core.Core;
import signals.core.Constants.PlotStyle;
import signals.gui.GUIDimensions;

@SuppressWarnings("serial")
public class PlotGraphic extends JPanel {

	//colors that apply to the entire plot
	protected Color axesColor, gridlinesColor, cursorColor, cursorColorBright, cursorColorDark;

	//stores information about conversion between math and plot coordinates
	protected PlottingMath plottingMath;

	//origin in plotting coordinates
	double[] origin; 

	//all data sequences which are shown on this plot
	protected ArrayList<DataSequence> dataSequences;

	//graphics
	protected GeneralPath horizontalGridlines, verticalGridlines; 
	protected GeneralPath tickMarksX, tickMarksY;
	protected Line2D.Double xAxis, yAxis; 
	protected Line2D.Double cursor;
	protected Rectangle2D.Double cursorBox;

	boolean draggingCursor;
	double cursorLocation;

	//strokes in which to paint certain components
	protected BasicStroke cursorStroke, axesStroke; 

	//tick mark information
	protected ArrayList<String> ticksXLabels, ticksYLabels;
	protected ArrayList<Integer> ticksXLocations, ticksYLocations;

	//extrema of all data 
	protected double[] xExtrema, yExtrema; 

	//true if certain components should be painted
	protected boolean tickMarksOn;
	protected boolean verticalGridlinesOn, horizontalGridlinesOn;
	protected boolean cursorOn; 

	//true if the graphics need to be redone before the next painting
	protected boolean dirtyAxes, dirtyTickMarks, dirtyGridlinesV, dirtyGridlinesH, dirtyCursor, dirtyDimension; 

	//true if the values on the x axis are indices (false if they are values, as in an argand diagram)
	protected boolean indexedXAxis; 

	//true if the values on the Y axis should be pi scaled
	protected boolean piScaledYAxis; 

	//true if this window should be automatically resized
	protected boolean autofitOn; 

	boolean mouseListenersAdded;

	PlotCursorPanel plotPanel;

	PlotStyle defaultPlotStyle;

	int cursorMode; 
	
	/**
	 * Constructor with default dimension
	 * @param size
	 */
	public PlotGraphic( Dimension size ) {

		this(); 
		setPreferredSize( size );
		plottingMath = new PlottingMath( size.width, size.height, GUIDimensions.OFFSET );
		dirtyDimension = true; 
	}

	/**
	 * 
	 *
	 */
	public PlotGraphic() {

		super( true ); 
		defaultPlotStyle = PlotStyle.FILLED;

		verticalGridlinesOn = false; 
		horizontalGridlinesOn = false; 
		cursorOn = false;

		setBackground( Core.getColors().getBackgroundColor() );
		axesColor = Core.getColors().getAxesColor();
		gridlinesColor = Core.getColors().getGridlinesColor();
		cursorColorBright = new Color( 0, 187, 255 );
		cursorColor = cursorColorDark = new Color( 0, 153, 221 );
		draggingCursor = false;

		cursorStroke = new BasicStroke( 3 ); 
		axesStroke = new BasicStroke( 1 );

		indexedXAxis = true; 
		piScaledYAxis = false;

		mouseListenersAdded = false;
		autofitOn = true;
		dataSequences = new ArrayList<DataSequence>(); 
		setDirty();

		dirtyDimension = true;
		autofitOn = true; 
	}

	/**
	 * @param piScaledYAxis the piScaledYAxis to set
	 */
	public void setPiScaledYAxis(boolean piScaledYAxis) {
		this.piScaledYAxis = piScaledYAxis;
		dirtyTickMarks = true;
		repaint();
	}

	public void addMouseListeners() {

		if( !mouseListenersAdded ) {
			addMouseListener( new CursorMouseListener() );
			addMouseMotionListener( new CursorMouseMotionListener() );
			mouseListenersAdded = true; 
		}
	}

	public void dragCursor( MouseEvent e ) {

		double newCursorLocation = plottingMath.plotToMathX( e.getX() );
		if ( newCursorLocation <= plottingMath.getXWindowMax() && newCursorLocation >= plottingMath.getXWindowMin()) {

			if( plotPanel != null ) plotPanel.setCursorLocation( newCursorLocation );
			cursorLocation = newCursorLocation;
			dirtyCursor = true;
			repaint();
		}
	}

	/**
	 * @param plotPanel the plotPanel to set
	 */
	public void setPlotPanel(PlotCursorPanel plotPanel) {
		this.plotPanel = plotPanel;
	}

	/**
	 * @return the dataSequences
	 */
	public ArrayList<DataSequence> getDataSequences() {
		return dataSequences;
	}

	/**
	 * Informs this plot that graphics should be redone before the next painting
	 *
	 */
	public void setDirty() {

		dirtyAxes = true;
		dirtyTickMarks = true; 
		dirtyGridlinesV = true; 
		dirtyGridlinesH = true;
		dirtyCursor = true;
		for( DataSequence sequence : dataSequences ) sequence.setDirty();
	}

	public void removeAll() {

		dataSequences = new ArrayList<DataSequence>();
		setCursorOn( false );
		setDirty();
	}

	/**
	 * Adds a new data sequence to this plot
	 * @param xData x-coordinates of the data to add
	 * @param yData y-coordinates of the data to add
	 * @param name label for the data to add
	 */
	public void add( double[] xData, double[] yData, String name ) {

		dataSequences.add( new DataSequence( xData, yData, name, defaultPlotStyle ) );
		if( autofitOn && plottingMath != null ) autofit();
	}


	public boolean isAutofitOn() {
		return autofitOn;
	}

	public void setAutofitOn(boolean autofitOn) {
		this.autofitOn = autofitOn;
	}

	/**
	 * Call this method when the size of this plot should be updated
	 *
	 */
	public void sizeChanged() {

		dirtyDimension = true; 
	}

	public void calculateDimension() {

		tickMarksOn = ( getWidth() > 300 && getHeight() > 150 ); //set a minimum default size for tick marks
		if( plottingMath == null ) {

			plottingMath = new PlottingMath( getWidth(), getHeight(), GUIDimensions.OFFSET ); 

		} else {

			plottingMath.setWidth( getWidth() );
			plottingMath.setHeight( getHeight() );
		}

		origin = plottingMath.getOrigin();
		if( autofitOn ) autofit(); 
		setDirty();
		dirtyDimension = false;

	}
	
	public void lockScale( double[] xData, double[] yData ) {
		
		double[] x = PlottingMath.extrema(xData); 
		double[] y = PlottingMath.extrema(yData); 
		lockExtrema( x, y ); 
	}
	                                                         
	
	public void lockExtrema( double[] x, double[] y ) {
		
		double[] xAdjusted = PlottingMath.contrastAdjustExtrema(x, 1); 
		double[] yAdjusted = PlottingMath.contrastAdjustExtrema(y, 1); 
		
		xExtrema = xAdjusted; 
		yExtrema = yAdjusted; 
		plottingMath.setWindow( xAdjusted, yAdjusted );
		origin = plottingMath.getOrigin();
		autofitOn = false;
		setDirty(); 
		
	}

	/**
	 * Sets the window to be a bounding box around the plots
	 *
	 */
	public void autofit() {

		xExtrema = dataSequences.get( 0 ).getXExtrema(); 
		yExtrema = dataSequences.get( 0 ).getYExtrema(); 

		for( int i=1; i < dataSequences.size(); i++ ) {

			xExtrema = plottingMath.compareExtrema( xExtrema, dataSequences.get(i).getXExtrema() );
			yExtrema = plottingMath.compareExtrema( yExtrema, dataSequences.get(i).getYExtrema() );
		}

		xExtrema = PlottingMath.contrastAdjustExtrema( xExtrema, 1 );
		yExtrema = PlottingMath.contrastAdjustExtrema( yExtrema, 1 );

		plottingMath.setWindow( xExtrema, yExtrema );
		origin = plottingMath.getOrigin();
		setDirty(); 

	}	    
	
	

	public double[] getXExtrema() {
		return xExtrema;
	}

	public double[] getYExtrema() {
		return yExtrema;
	}

	/**
	 * sets the window to any rectangle
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 */
	public void setWindow( double[] xExtrema, double[] yExtrema ) {

		plottingMath.setWindow( xExtrema[0], xExtrema[1], yExtrema[0], yExtrema[1] ); 
		origin = plottingMath.getOrigin(); 
		this.xExtrema = xExtrema; 
		this.yExtrema = yExtrema; 
		
		setDirty();
	}

	/**
	 * @return the defaultPlotStyle
	 */
	public PlotStyle getDefaultPlotStyle() {
		return defaultPlotStyle;
	}

	/**
	 * @param defaultPlotStyle the defaultPlotStyle to set
	 */
	public void setDefaultPlotStyle(PlotStyle defaultPlotStyle) {
		this.defaultPlotStyle = defaultPlotStyle;
	}

	/**
	 * @param cursorOn the cursorOn to set
	 */
	public void setCursorOn(boolean cursorOn) {
		this.cursorOn = cursorOn;
		repaint();
	}

	/**
	 * @return the cursorLocation
	 */
	public double getCursorLocation() {
		return cursorLocation;
	}

	/**
	 * @param cursorLocation the cursorLocation to set
	 */
	public void setCursorLocation(double cursorLocation) {
		this.cursorLocation = cursorLocation;
		dirtyCursor = true; 
		repaint();
	}


	protected void setTickMarkLocations() {

		if( dirtyTickMarks ) {

			if(indexedXAxis) plottingMath.createTicksX( xExtrema ); else plottingMath.createTicksX();
			plottingMath.createTicksY( piScaledYAxis );
			dirtyTickMarks = false;
		}
	}

	public void createGridlinesH() {

		setTickMarkLocations();
		horizontalGridlines = PlottingGraphics.createGridH( plottingMath.getTicksYLocations(), getWidth() );
	}

	public void createGridlinesV() {

		setTickMarkLocations();
		verticalGridlines = PlottingGraphics.createGridV( plottingMath.getTicksXLocations(), getHeight() );
	}

	public void createAxes() {

		xAxis = PlottingGraphics.xAxis( getWidth(), origin[1], GUIDimensions.OFFSET ); 
		yAxis = PlottingGraphics.yAxis( getHeight(), origin[0], GUIDimensions.OFFSET ); 
	}

	public void createTickMarks() {

		setTickMarkLocations();

		ticksXLocations = plottingMath.getTicksXLocations();
		ticksYLocations = plottingMath.getTicksYLocations();

		tickMarksX = PlottingGraphics.createTicksX( ticksXLocations, origin[1] );
		tickMarksY = PlottingGraphics.createTicksY( ticksYLocations, origin[0] );	

		ticksXLabels = plottingMath.getTicksXLabels(); 
		ticksYLabels = plottingMath.getTicksYLabels(); 
	}

	public void createCursor() {

		double plotCursorX = plottingMath.mathToPlotX(cursorLocation);
		cursor = PlottingGraphics.yAxis( getHeight(), plotCursorX, GUIDimensions.OFFSET );
		cursorBox = new Rectangle2D.Double( plotCursorX-3, 0, 6, getHeight() );

	}

	/**
	 * Draws the gridlines 
	 * @param g2
	 */
	public void drawGridlines( Graphics2D g2 ) {

		if( horizontalGridlinesOn ) {

			g2.setColor( gridlinesColor ); 
			g2.draw( horizontalGridlines );
		}

		if( verticalGridlinesOn ) {

			g2.setColor( gridlinesColor ); 
			g2.draw( verticalGridlines );
		}
	}
	
	public void refreshGridLines() {
		
		if( horizontalGridlinesOn ) {

			if( dirtyGridlinesH ) {

				createGridlinesH(); 
				dirtyGridlinesH = false;
			}

		}

		if( verticalGridlinesOn ) {

			if( dirtyGridlinesV ) {

				createGridlinesV();
				dirtyGridlinesV = false;
			}
		}
	}

	/**
	 * Draws the axes
	 * @param g2
	 */
	public void drawAxes( Graphics2D g2 ) {

		g2.setStroke( axesStroke );
		g2.setColor( axesColor ); 
		g2.draw( xAxis ); 
		g2.draw( yAxis );

	}
	
	public void refreshAxes() {
		
		if( dirtyAxes ) {

			createAxes();
			dirtyAxes = false; 
		}
	}
	
	public void refreshTickMarks() {
		
		if( tickMarksOn ) {

			if( dirtyTickMarks ) {

				createTickMarks(); 
				dirtyTickMarks = false;
			}

		}
	}

	public void drawTickMarks( Graphics2D g2 ) {

		if( tickMarksOn ) {

	
			g2.draw( tickMarksX ); 
			g2.draw( tickMarksY );
			g2.setFont(Core.getDisplayOptions().getAxisFont());

			for( int i = 0; i < ticksYLabels.size(); i++ ) { 

				g2.drawString(ticksYLabels.get( i ), (int)origin[0] + 10, ticksYLocations.get( i ));

			}
			for( int i = 0; i < ticksXLabels.size(); i++ ) { 

				g2.drawString( ticksXLabels.get( i ), ticksXLocations.get( i ), (int)origin[1] + 20 );

			}

		}
	}

	public void drawCursor( Graphics2D g2 ) {

		if( cursorOn ) { 

			g2.setStroke( cursorStroke );
			g2.setColor( cursorColor );
			g2.draw( cursor );
		}

	}
	
	public void refreshCursor() {
		
		if( cursorOn ) { 

			if( dirtyCursor ) {

				createCursor(); 
				dirtyCursor = false;
			}

		}
		
	}

	public void paintComponent( Graphics g ) {

		// makes use of graphics 2D painting
		Graphics2D g2 = ( Graphics2D ) g;

		//set the rendering hints for antialiasing if possible
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON );

		if( dirtyDimension ) calculateDimension();
		
		refreshGridLines(); 
		for( DataSequence dataSequence : dataSequences ) dataSequence.refreshGraphics();
		refreshAxes(); 
		refreshTickMarks(); 
		refreshCursor(); 
		
		//paint the background
		super.paintComponent( g );
		drawGridlines( g2 );
		for( DataSequence dataSequence : dataSequences ) dataSequence.paintGraphics(g2);
		drawAxes( g2 ); 
		drawTickMarks( g2 );
		drawCursor( g2 ); 
	} 

	/**
	 * Represents a plot of a single (x,y) sequence
	 * @author Juliet
	 *
	 */
	public class DataSequence {

		//data (in math coordinates)
		protected double[] xData, yData;

		//extrema of data 
		protected double[] xExtrema, yExtrema; 

		//the color in which this sequence is drawn
		protected Color color; 

		//the stroke used to paint this sequence
		protected BasicStroke stroke; 

		//the style in which this sequence is drawn
		protected Constants.PlotStyle plotStyle; 

		//graphics that should be filled in and drawn
		protected GeneralPath fillGraphic, drawGraphic;

		//the size of the plot symbols
		protected float size; 

		//true if this data sequence is visible
		protected boolean visible; 

		//name of this data sequence
		protected String name; 

		//whether this graphic is dirty
		protected boolean dirtyGraphic; 

		/**
		 * @param data
		 * @param data2
		 */
		public DataSequence( double[] xData, double[] yData, String name, Constants.PlotStyle plotStyle ) {
			super();
			this.xData = xData;
			this.yData = yData;
			this.name = name;
			this.plotStyle = plotStyle;

			color = Core.getColors().getDefaultColor( dataSequences.size() );
			size = 2; 
			stroke = new BasicStroke( size );
			visible = true;

			calculateExtrema();

			dirtyGraphic = true; 
		}

		public void calculateExtrema() {

			xExtrema = PlottingMath.extrema( xData );
			yExtrema = PlottingMath.extrema( yData );
		}

		public void createGraphics() {

			double[][] plotCoords = plottingMath.mathToPlotCoordinates ( xData, yData );

			switch( plotStyle ) {

			case FILLED: 
				fillGraphic = PlottingGraphics.filledGraphic(plotCoords[0], plotCoords[1], origin[1]); 
				drawGraphic = null; 
				break; 
			case HISTOGRAM:
				fillGraphic = PlottingGraphics.histogramGraphic(plotCoords[0], plotCoords[1], origin[1]); 
				drawGraphic = null; 
				break;
			case SMOOTH: 
				drawGraphic = PlottingGraphics.smoothGraphic(plotCoords[0], plotCoords[1]); 
				fillGraphic = null; 
				break; 
			case STEM: 
				drawGraphic = PlottingGraphics.stemGraphic(plotCoords[0], plotCoords[1], origin[1], size);
				fillGraphic = null; 
				break;
			case SCATTER:
				fillGraphic = PlottingGraphics.scatterGraphic(plotCoords[0], plotCoords[1], size);
				drawGraphic = null;
				break;
			}

		}

		/**
		 * Paints this datasequence onto a graphics context
		 * @param g2 the graphics context on which to paint this data sequence
		 */
		public void paintGraphics( Graphics2D g2 ) {

			if( visible ) { 

				//set the correct stroke for this data sequence
				g2.setStroke( stroke );

				//set the correct color for this data sequence
				g2.setColor( color );

				if( fillGraphic != null ) g2.fill( fillGraphic );
				if( drawGraphic != null ) g2.draw( drawGraphic );

			}
		}
		
		public void refreshGraphics() {
			
			if( visible ) { 

				if( dirtyGraphic ) {

					createGraphics();
					dirtyGraphic = false;
				}

			}
		}

		/**
		 * @return the color
		 */
		public Color getColor() {
			return color;
		}

		/**
		 * @param color the color to set
		 */
		public void setColor(Color color) {
			this.color = color;
			repaint(); 
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
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
			setDirty(); 
			repaint(); 
		}

		/**
		 * @return the size
		 */
		public float getSize() {
			return size;
		}

		/**
		 * @param size the size to set
		 */
		public void setSize(float size) {
			this.size = size;
		}

		/**
		 * @return the stroke
		 */
		public BasicStroke getStroke() {
			return stroke;
		}

		/**
		 * @param stroke the stroke to set
		 */
		public void setStroke(BasicStroke stroke) {
			this.stroke = stroke;
		}

		/**
		 * @return the visible
		 */
		public boolean isVisible() {
			return visible;
		}

		/**
		 * @param visible the visible to set
		 */
		public void setVisible(boolean visible) {
			this.visible = visible;
		}

		/**
		 * @param sets the graphic dirty
		 */
		public void setDirty() {
			dirtyGraphic = true;
		}

		/**
		 * @return the xData
		 */
		public double[] getXData() {
			return xData;
		}

		/**
		 * @param data the xData to set
		 */
		public void setXData(double[] data, boolean refit) {
			xData = data;
			
			if( refit && plottingMath != null ) {
			
				xExtrema = PlottingMath.extrema(xData);
				autofit(); 
				
			}
			
			setDirty(); 
			repaint();
		}

		/**
		 * @return the yData
		 */
		public double[] getYData() {
			return yData;
		}

		/**
		 * @param data the yData to set
		 */
		public void setYData(double[] data, boolean refit) {
			yData = data;
			
			if( refit && plottingMath != null ) {
				
				yExtrema = PlottingMath.extrema(yData);
				autofit();
				
			}
			
			setDirty(); 
			repaint();
		}

		/**
		 * @return the xExtrema
		 */
		public double[] getXExtrema() {
			return xExtrema;
		}

		/**
		 * @return the yExtrema
		 */
		public double[] getYExtrema() {
			return yExtrema;
		}

	} //end dataSequence inner class

	public class CursorMouseMotionListener extends MouseMotionAdapter {

		public void mouseMoved(MouseEvent e) {

			if( cursorOn ) {

				boolean contains = cursorBox.contains(e.getX(), e.getY()); 

				if( contains && cursorColor == cursorColorDark ) {

					cursorColor = cursorColorBright;
					repaint(); 

				} else if( !contains && cursorColor == cursorColorBright ) {

					cursorColor = cursorColorDark;
					repaint();
				}

			}	

		}

		public void mouseDragged(MouseEvent e) {

			if( draggingCursor ) { 

				dragCursor(e); 

			} 
		}

	} //end inner class cursorMouseMotionListener

	public class CursorMouseListener extends MouseAdapter {

		public void mousePressed( MouseEvent e) {

			if( cursorOn && cursorBox.contains(e.getX(), e.getY()) ) { 

				draggingCursor = true;

			} 
		}

		public void mouseReleased(MouseEvent e) {

			draggingCursor = false; 
		}

	} 
}
