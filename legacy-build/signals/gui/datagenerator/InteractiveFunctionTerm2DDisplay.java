package signals.gui.datagenerator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import signals.functionterm.AnalyticFunctionTerm2D;
import signals.functionterm.ImageFunctionTerm2D;
import signals.gui.GUIDimensions;
import signals.gui.plot.InteractiveImageGraphic;
import signals.gui.plot.PlottingGraphics;

@SuppressWarnings("serial")
public class InteractiveFunctionTerm2DDisplay extends InteractiveImageGraphic implements AnalyticFunctionTerm2DEditor {

	EditAnalyticFunctionTerm2DInterface editor; 

	AnalyticFunctionTerm2D currentFunction;

	Line2D.Double centerLineX, centerLineY, widthLineX, widthLineY;
	Rectangle2D.Double centerBoxX, centerBoxY, widthBoxX, widthBoxY;  

	double centerX, centerY, widthX, widthY; //values in math coordinates
	boolean dirtyCenterX, dirtyCenterY, dirtyWidthX, dirtyWidthY;

	double xShiftFactor, yShiftFactor;
	
	boolean linesOn;
	
	boolean hasWidthX, hasWidthY; 

	Color centerLineColorDark, centerLineColorBright, widthLineColorDark, widthLineColorBright, 
	centerLineColorX, centerLineColorY, widthLineColorX, widthLineColorY;

	boolean draggingCenterX, draggingCenterY, draggingWidthX, draggingWidthY;
	
	boolean halfWidthDefined; 
	
	public InteractiveFunctionTerm2DDisplay( EditAnalyticFunctionTerm2DInterface editImageFunctionTermPanel ) {

		super( GUIDimensions.largeImageThumbnailDimension ); 
		this.editor = editImageFunctionTermPanel;

		linesOn = false;
		draggingCenterX = draggingCenterY = draggingWidthX = draggingWidthY = false; 
		dirtyCenterX = dirtyCenterY = dirtyWidthX = dirtyWidthY = true; 

		centerLineColorY = centerLineColorX = centerLineColorDark = Color.orange;  
		centerLineColorBright = Color.yellow;
		widthLineColorY = widthLineColorX = widthLineColorDark = new Color( 0, 154, 221 );
		widthLineColorBright = new Color( 0, 187, 255 );
		
	}
	
	public void setHasWidthX(boolean hasWidthX) {
		this.hasWidthX = hasWidthX;
	}



	public void setHasWidthY(boolean hasWidthY) {
		this.hasWidthY = hasWidthY;
	}

	public void createImageAxesPanel() {
		
		imageAxesPanel = new LinesImageAxesPanel();
		imageAxesPanel.addMouseListener( new LinesMouseListener() );
		imageAxesPanel.addMouseMotionListener( new LinesMouseMotionListener() );
	}
	
	/* (non-Javadoc)
	 * @see signals.gui.InteractiveImageGraphic#setIndices(int, int, boolean)
	 */
	@Override
	public void setIndices(int x_dimension, int y_dimension, boolean zeroCentered) {
	
		super.setIndices(x_dimension, y_dimension, zeroCentered);
		if( currentFunction != null ) { 
			centerX = currentFunction.getCenterX(); 
			centerY = currentFunction.getCenterY(); 
			if( zeroCentered ) {
				
				centerX += dimension/2; 
				centerY += dimension/2;
			}
			if( hasWidthX) {
				
				widthX = currentFunction.getWidth() / 2.0; 
				createWidthLineX();
			}
			if( hasWidthY) { 
				
				widthY = currentFunction.getHeight() / 2.0; 
				createWidthLineY();
			}
			createCenterLineX();
			createCenterLineY();
			
			setDirty(); 
			refreshView(); 
			repaint();
		}
	}

	/**
	 * @param currentFunction the currentFunction to set
	 */
	public void setFunctionTerm( AnalyticFunctionTerm2D currentFunction ) {

		this.currentFunction = currentFunction;
		centerX = currentFunction.getCenterX(); 
		centerY = currentFunction.getCenterY(); 
		
		hasWidthX = currentFunction.hasWidth(); 
		hasWidthY = currentFunction.hasHeight(); 
		
		halfWidthDefined = currentFunction instanceof ImageFunctionTerm2D; 
		
		if( zeroCentered ) {
			
			centerX += dimension/2; 
			centerY += dimension/2;
		}
		if( hasWidthX) {
			
			if( halfWidthDefined ) widthX = currentFunction.getWidth() / 2.0; 
			else widthX = currentFunction.getWidth(); 
			createWidthLineX();
		}
		if( hasWidthY) { 
			
			if( halfWidthDefined ) widthY = currentFunction.getHeight() / 2.0; 
			else widthY = currentFunction.getHeight();
			createWidthLineY();
		}
		createCenterLineX();
		createCenterLineY();
		
		setDirty(); 
		refreshView(); 
		//repaint();
	}

	public void setDirty() {
		
		dirtyCenterX = true; 
		dirtyCenterY = true; 
		dirtyWidthX = true; 
		dirtyWidthY = true;
	}
	
	
	public void refreshView() {

		if( currentFunction != null ) {

			data = currentFunction.create( x_dimension, y_dimension, zeroCentered );
			display( data );
		}
	}
	
	public void dragCenterX( MouseEvent e ) {

		if( e.getX() < ( displayDimension.width ) && e.getX()  >= 0 ) { 
		
			centerX = plotToMathX( e.getX() );
			editor.setCenterX( zeroCentered ? centerX - dimension/2 : centerX );
		}
	}
	
	public void dragCenterY( MouseEvent e ) {

		if( e.getY() < displayDimension.height && e.getY() >= 0 ) { 
			
			centerY = plotToMathY( e.getY() );
			editor.setCenterY( zeroCentered ? centerY - dimension/2 : centerY );
		}

	}
	
	public void dragWidthX( MouseEvent e ) {

		if( e.getX() < ( displayDimension.width ) && e.getX()  >= 0 ) { 
			
			double initWidth = plotToMathX( e.getX() ) - centerX;
			if( initWidth > 0 ) {
				
				widthX = initWidth; 
				if( halfWidthDefined) editor.setWidth( widthX*2 ); 
				else editor.setWidth( widthX );
			}
	
		}
	}
	
	public void dragWidthY( MouseEvent e ) {

		if( e.getY() < ( displayDimension.height ) && e.getY()  >= 0 ) { 
			
			double initWidth = plotToMathY( e.getY() ) - centerY;
			if( initWidth > 0 ) {
				
				widthY = initWidth; 
				if( halfWidthDefined) editor.setHeight( widthY*2 ); 
				else editor.setHeight( widthY ); 
			}
	
		}
	}
	
	public void createCenterLineX() { 

		double plotCenterX = mathToPlotX( centerX ); 
		centerLineX = PlottingGraphics.yAxis( displayDimension.height, plotCenterX, 0 );
		centerBoxX = new Rectangle2D.Double( plotCenterX-4, 0, 8, displayDimension.height );
	}

	public void createCenterLineY() { 

		double plotCenterY = mathToPlotY( centerY ); 
		centerLineY = PlottingGraphics.xAxis( displayDimension.width, plotCenterY, 0 );
		centerBoxY = new Rectangle2D.Double( 0, plotCenterY-4, displayDimension.width, 8 );
	}
	
	public void createWidthLineX() { 

		double plotWidthX = mathToPlotX( centerX + widthX );
		widthLineX = PlottingGraphics.yAxis( displayDimension.height, plotWidthX, 0 );
		widthBoxX = new Rectangle2D.Double( plotWidthX-4, 0, 8, displayDimension.height );
	}

	public void createWidthLineY() { 

		double plotWidthY = mathToPlotY( centerY + widthY ); 
		widthLineY = PlottingGraphics.xAxis( displayDimension.width, plotWidthY, 0 );
		widthBoxY = new Rectangle2D.Double( 0, plotWidthY-4, displayDimension.width, 8 );
	}


	public class LinesImageAxesPanel extends ImageAxesPanel {


		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			if( !linesOn ) return; 

			Graphics2D g2 = (Graphics2D)g; 

			g2.setStroke(lineStroke);

			g2.setColor(centerLineColorX); 
			if( dirtyCenterX ) {
				
				createCenterLineX(); 
				dirtyCenterX = false; 
			}
			g2.draw( centerLineX ); 

			g2.setColor(centerLineColorY); 
			if( dirtyCenterY ) {
				
				createCenterLineY(); 
				dirtyCenterY = false; 
			}
			g2.draw( centerLineY ); 

			if( hasWidthX ) {
				
				if( dirtyWidthX ) {
				
					createWidthLineX(); 
					dirtyWidthX = false; 
				
				}
				
				g2.setColor(widthLineColorX); 
				
				//only draw the width line if it is in bounds
				if( mathToPlotX(widthX+centerX) <= displayDimension.width )
				g2.draw( widthLineX ); 
			}

			if( hasWidthY ) {
				
				if( dirtyWidthY ) {
				
					createWidthLineY(); 
					dirtyWidthY = false; 
				
				}
				
				g2.setColor(widthLineColorY); 
				
				//if( mathToPlotY(widthY+centerY) <= displayDimension.height )
				g2.draw( widthLineY ); 
			}

		}

	}

	public class LinesMouseMotionListener extends MouseMotionAdapter {

		public void mouseMoved(MouseEvent e) {

			if( !linesOn ) return;  

			if( hasWidthX ) {
			
				boolean containsWidthX = widthBoxX.contains(e.getX(), e.getY()); 
	
				if( containsWidthX && widthLineColorX == widthLineColorDark) {
	
					widthLineColorX = widthLineColorBright;
					repaint(); 
	
				} else if( !containsWidthX && widthLineColorX == widthLineColorBright ) {
	
					widthLineColorX = widthLineColorDark; 
					repaint();
				}
			
			}
			
			if( hasWidthY ) { 

				boolean containsWidthY = widthBoxY.contains(e.getX(), e.getY()); 
	
				if( containsWidthY && widthLineColorY == widthLineColorDark) {
	
					widthLineColorY = widthLineColorBright;
					repaint(); 
	
				} else if( !containsWidthY && widthLineColorY == widthLineColorBright ) {
	
					widthLineColorY = widthLineColorDark; 
					repaint();
				}
			
			}

			boolean containsCenterX = centerBoxX.contains(e.getX(), e.getY()); 

			if( containsCenterX && centerLineColorX == centerLineColorDark) {

				centerLineColorX = centerLineColorBright;
				repaint(); 

			} else if( !containsCenterX && centerLineColorX == centerLineColorBright ) {

				centerLineColorX = centerLineColorDark; 
				repaint();
			}

			boolean containsCenterY = centerBoxY.contains(e.getX(), e.getY()); 

			if( containsCenterY && centerLineColorY == centerLineColorDark) {

				centerLineColorY = centerLineColorBright;
				repaint(); 

			} else if( !containsCenterY && centerLineColorY == centerLineColorBright ) {

				centerLineColorY = centerLineColorDark; 
				repaint();
			}

		}

		public void mouseDragged(MouseEvent e) {

			if( draggingCenterX ) { 

				dragCenterX(e); 

			} else if( draggingCenterY ) { 

				dragCenterY(e); 

			} else 	if( draggingWidthX ) { 

				dragWidthX(e); 

			} else if( draggingWidthY ) { 

				dragWidthY(e); 

			}
		}

	} //end inner class LinesMouseMotionListener

	public class LinesMouseListener extends MouseAdapter {

		public void mousePressed( MouseEvent e) {

			if( !linesOn ) return; 

			if( centerBoxX.contains(e.getX(), e.getY()) ) { 

				draggingCenterX = true;

			} else if ( centerBoxY.contains(e.getX(), e.getY()) ) { 

				draggingCenterY = true;

			} else if( hasWidthX && widthBoxX.contains(e.getX(), e.getY()) ) { 

				draggingWidthX = true;

			} else if ( hasWidthY && widthBoxY.contains(e.getX(), e.getY()) ) { 

				draggingWidthY = true;

			}
		}

		public void mouseReleased(MouseEvent e) {

			draggingCenterX = false; 
			draggingCenterY = false; 
			draggingWidthX = false; 
			draggingWidthY = false; 
		}
		
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

	} //end inner class LinesMouseListener 

}
