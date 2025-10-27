/**
 * 
 */
package signals.gui.datagenerator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import signals.core.Core;
import signals.core.FunctionTerm1D;
import signals.functionterm.AnalyticFunctionTerm;
import signals.gui.plot.FunctionTerm1DThumbnailGraphic;
import signals.gui.plot.PlottingGraphics;
import signals.gui.plot.PlottingMath; 
import signals.gui.ThumbnailGraphic;

/**
 * @author Juliet
 * Allows the user to interact with and adjust 1D currentFunction terms
 */
@SuppressWarnings("serial")
public class AnalyticInteractivePlot extends AnalyticFunctionTerm1DEditor {
	
	public static final int LINE_WIDTH = 8; 
	public static final int HALF_LINE_WIDTH = 4; 

	protected AnalyticInteractivePlotGraphic graphic; 

	boolean hasAmplitude, hasWidth, hasCenter; 
	Line2D.Double amplitudeLine, centerLine, widthLine;
	Rectangle2D.Double amplitudeBox, centerBox, widthBox;  
	
	double amplitude, center, width; 
	
	boolean linesOn;
	
	EditAnalyticFunctionTermPanel editor; 
	
	Color amplitudeLineColorDark, amplitudeLineColorBright, centerLineColorDark, centerLineColorBright, 
			widthLineColorDark, widthLineColorBright, amplitudeLineColor, centerLineColor, widthLineColor;
	
	boolean draggingAmplitude, draggingCenter, draggingWidth;
	
	public AnalyticInteractivePlot(GUIEventBroadcaster broadcaster,
			GUIEvent.Descriptor modifiedDescriptor, EditAnalyticFunctionTermPanel editor) {
		
		super(broadcaster, modifiedDescriptor);
		
		this.editor = editor;
		setBackground( Color.white ); 
		setOpaque( true );
		graphic = new AnalyticInteractivePlotGraphic( null, null );
		setPreferredSize( ThumbnailGraphic.getLargeDimension() );
		setMaximumSize( ThumbnailGraphic.getLargeDimension() );
		setMinimumSize( ThumbnailGraphic.getLargeDimension() );
		addMouseMotionListener( new LineMouseMotionListener() );
		addMouseListener( new LineMouseListener() );
		setBorder();
		linesOn = false;
		
		draggingAmplitude = draggingCenter = draggingWidth = false; 
		
		amplitudeLineColor = amplitudeLineColorDark = new Color( 187, 0, 34); 
		amplitudeLineColorBright = Color.red;
		centerLineColor = centerLineColorDark = Color.orange;  
		centerLineColorBright = new Color( 255, 255, 51 );
		widthLineColor = widthLineColorDark = new Color( 0, 85, 0 ); 
		widthLineColorBright = new Color( 0, 238, 17 );
	}
	
	public void setBorder() {
		
		setBorder( Core.getBorders().getLineBorder() );
	}

	public void setIndices( double[] indices ) {

		super.setIndices(indices);
		graphic.setIndices( indices );
		repaint();
	}

	public void setFunctionTerm( AnalyticFunctionTerm term ) {

		super.setFunctionTerm(term);
		graphic.setFunction( currentFunction ); 
		updateAmplitude(); 
		updateCenter();
		updateWidth();
		repaint();
	}
	
	public void updateAmplitude() {
		
		if( currentFunction.hasAmplitude() ) {
			amplitude = currentFunction.getAmplitude();
			hasAmplitude = true; 
		} else {
			hasAmplitude = false; 
		}
	}

	public void updateCenter() {
		
		if( currentFunction.hasCenter() ) {
			center = currentFunction.getCenter(); 
			hasCenter = true; 
		} else {
			hasCenter = false; 
		}
	}
	
	public void updateWidth() {
		
		if( currentFunction.hasWidth() ) { 
			width = currentFunction.getWidth();
			hasWidth = true; 
		} else {

			hasWidth = false;
		}
	}
	
	
	public void paintComponent( Graphics g ) {

		super.paintComponent(g);
		graphic.paintLargeGraphic((Graphics2D)g);
	}
	
	public void dragWidth( MouseEvent e ) {

		double initWidth = graphic.getPlottingMath().plotToMathX( e.getX() );
		double center = currentFunction.getCenter();
		if ( initWidth <= graphic.getPlottingMath().getXWindowMax() && initWidth > center ) {
			double newWidth = initWidth - center; 
			editor.setWidth( newWidth );
		}
	}
	
	public void dragAmplitude( MouseEvent e ) {
		
		
		PlottingMath plottingMath = graphic.getPlottingMath();
		double newAmplitude = plottingMath.plotToMathY( e.getY() );
		if ( newAmplitude >= plottingMath.getYWindowMax() ){

			double oldAmplitude = currentFunction.getAmplitude();
			newAmplitude = oldAmplitude + PlottingMath.windowNudgeScalar*(1+Math.abs(oldAmplitude));
			plottingMath.setYWindowMax( newAmplitude );

		} else if(  newAmplitude <= plottingMath.getYWindowMin()) {

			double oldAmplitude = currentFunction.getAmplitude();
			newAmplitude = oldAmplitude - PlottingMath.windowNudgeScalar*(1+Math.abs(oldAmplitude));
			plottingMath.setYWindowMin( newAmplitude );
		}
		
		editor.setAmplitude( newAmplitude );
	}
	
	public void dragCenter( MouseEvent e ) {
		
		PlottingMath plottingMath = graphic.getPlottingMath();
		 double newCenter = plottingMath.plotToMathX( e.getX() );
		 if ( newCenter <= plottingMath.getXWindowMax() && newCenter >= plottingMath.getXWindowMin()) {

			 editor.setCenter( newCenter );
		 }
	}
	
	public class LineMouseMotionListener extends MouseMotionAdapter {

		public void mouseMoved(MouseEvent e) {
			
			if( e== null || amplitudeBox==null ) return; 

			if( hasAmplitude ) {
				
				boolean contains = amplitudeBox.contains(e.getX(), e.getY()); 
				
				if( contains && amplitudeLineColor == amplitudeLineColorDark ) {
					
					amplitudeLineColor = amplitudeLineColorBright; 
					repaint(); 
					
				} else if( !contains && amplitudeLineColor == amplitudeLineColorBright ) {
					 
					amplitudeLineColor = amplitudeLineColorDark;
					repaint();
				}
				
			}
			
			if( hasCenter ) {
				
				boolean contains = centerBox.contains(e.getX(), e.getY()); 
				
				if( contains && centerLineColor == centerLineColorDark ) {
					
					centerLineColor = centerLineColorBright;
					repaint(); 
					
				} else if( !contains && centerLineColor == centerLineColorBright ) {
					
					centerLineColor = centerLineColorDark;
					repaint();
				}
				
			}
			
			if( hasWidth ) {
				
				boolean contains = widthBox.contains(e.getX(), e.getY());
				
				if( contains && widthLineColor == widthLineColorDark ) {
					
					widthLineColor = widthLineColorBright;
					repaint(); 
					
				} else if( !contains && widthLineColor == widthLineColorBright ) {
					 
					widthLineColor = widthLineColorDark;
					repaint();
				}
				
			}
			
			
				
		}
		
		 public void mouseDragged(MouseEvent e) {
			 
			 if( draggingAmplitude ) { 
				 
				dragAmplitude(e); 
		          
			 } else if( draggingCenter ) { 
				 
				dragCenter(e);
				         
			 } else if( draggingWidth ) {
				
				 dragWidth(e);
			 }
		 }

	}
	
	public class LineMouseListener extends MouseAdapter {
		
		public void mousePressed( MouseEvent e) {
			
			if( hasAmplitude && amplitudeBox.contains(e.getX(), e.getY()) ) { 
				
				draggingAmplitude = true;
				
			} else if( hasCenter && centerBox.contains(e.getX(), e.getY()) ) { 
				
				draggingCenter = true;
				
			} else if( hasWidth && widthBox.contains(e.getX(), e.getY()) ) { 
				
				draggingWidth = true;
			}
			
			graphic.setAutofit( false );
		}
		
		 public void mouseReleased(MouseEvent e) {
		        	
		      draggingAmplitude = false; 
		      draggingCenter = false;
		      draggingWidth = false; 
		      
		      graphic.setAutofit( true );
		 }

		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
			super.mouseEntered(e);
			linesOn = true; 
			repaint(); 
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
			linesOn = false;
			repaint();
		}
		 
	}

	public class AnalyticInteractivePlotGraphic extends FunctionTerm1DThumbnailGraphic {

		Dimension size;
		GeneralPath tickMarksX, tickMarksY;

		public AnalyticInteractivePlotGraphic(FunctionTerm1D currentFunction, double[] indices) {
			super(currentFunction, indices);
		}



		public PlottingMath getPlottingMath() {
			return largePlottingMath;
		}



		/* (non-Javadoc)
		 * @see signals.gui.FunctionTerm1DThumbnailGraphic#createShapeList(int, int, int)
		 */
		@Override
		public ArrayList<Shape> createShapeList(int width, int height, int offset, PlottingMath plottingMath ) {
			
			//TODO this is where to set the default plot style
			ArrayList<Shape> shapelist = super.createShapeList(width, height, offset, plottingMath);
			setLines();
			createTicksX();
			plottingMath.createTicksY( false );
			double[] origin = plottingMath.getOrigin();
			tickMarksX = PlottingGraphics.createTicksX( plottingMath.getTicksXLocations(), origin[1] );
			tickMarksY = PlottingGraphics.createTicksY( plottingMath.getTicksYLocations(), origin[0] );
			return shapelist;
		}
		
		public void createTicksX() {
			
			largePlottingMath.createTicksX( indexExtrema );
		}

		public void setLines() {

			int plotWidth = getLargeDimension().width;
			int plotHeight = getLargeDimension().height;
			
			if( hasAmplitude ) {
				double plotAmplitude = largePlottingMath.mathToPlotY(amplitude);
				amplitudeLine = PlottingGraphics.xAxis( plotWidth,
						plotAmplitude, OFFSET ); 
				amplitudeBox = new Rectangle2D.Double( 0, plotAmplitude-HALF_LINE_WIDTH, plotWidth, LINE_WIDTH );
			}

			double plotCenter = 0; 
			
			if( hasCenter ) {
			
				plotCenter = largePlottingMath.mathToPlotX(center);
				centerLine = PlottingGraphics.yAxis( plotHeight, plotCenter, OFFSET );
				centerBox = new Rectangle2D.Double( plotCenter-HALF_LINE_WIDTH, 0, LINE_WIDTH, plotHeight );
			} 

			if( hasWidth ) { 
				
				double plotWidthL = largePlottingMath.mathToPlotX(center+width);
				widthLine = PlottingGraphics.yAxis( plotHeight, plotWidthL, OFFSET ); 
				widthBox = new Rectangle2D.Double( plotWidthL-HALF_LINE_WIDTH, 0, LINE_WIDTH, plotHeight );

			}
		}


		/**
		 * displays the real and imaginary parts of the currentFunction on the graphics context
		 * @param g2 a Graphics2D context on which to paint
		 * @param shapelist list of shapes created with the createShapeList method
		 */
		public void paintGraphic(Graphics2D g2, ArrayList<Shape> shapelist ) {

			super.paintGraphic( g2, shapelist );
			
			//paint the tick marks
			g2.setColor( Color.black );
			g2.draw( tickMarksY );
			g2.draw( tickMarksX );
			ArrayList<String> ticksYLabels = largePlottingMath.getTicksYLabels();
			ArrayList<Integer> ticksYLocations = largePlottingMath.getTicksYLocations();
			ArrayList<String> ticksXLabels = largePlottingMath.getTicksXLabels();
			ArrayList<Integer> ticksXLocations = largePlottingMath.getTicksXLocations();
			double[] origin = largePlottingMath.getOrigin();
			
			for( int i = 0; i < ticksYLabels.size(); i++ ) { 
				
				g2.drawString(ticksYLabels.get( i ), (int)origin[0] + 10, ticksYLocations.get( i ));
				
			}
			for( int i = 0; i < ticksXLabels.size(); i++ ) { 
				
				g2.drawString( ticksXLabels.get( i ), ticksXLocations.get( i ), (int)origin[1] - 7 );
				
			}

			g2.setStroke( new BasicStroke( HALF_LINE_WIDTH ) );

			if( linesOn ) {
				//draw the lines
				if( hasAmplitude ) {
					g2.setColor( amplitudeLineColor );
					g2.draw( amplitudeLine ); 
				}
	
				if( hasWidth ) {
					g2.setColor( widthLineColor );
					g2.draw( widthLine ); 
				}
	
				if( hasCenter ) {
					g2.setColor( centerLineColor );
					g2.draw( centerLine ); 
				}
			}
		}

	}

}

