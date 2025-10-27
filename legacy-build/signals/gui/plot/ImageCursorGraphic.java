package signals.gui.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import signals.core.Deconstructable;



@SuppressWarnings("serial")
public class ImageCursorGraphic extends InteractiveImageGraphic implements Deconstructable {

	protected Color cursorColorX, cursorColorXBright, cursorColorXDark;
	protected Color cursorColorY, cursorColorYBright, cursorColorYDark;

	protected Line2D.Double cursorX, cursorY;
	protected Rectangle2D.Double cursorBoxX, cursorBoxY;

	boolean draggingCursorX, draggingCursorY;

	double cursorLocationX, cursorLocationY;
	boolean dirtyCursorX, dirtyCursorY; 

	protected boolean cursorOn; 

	boolean mouseListenersAdded;

	ImageCursorPanel imageCursorPanel;

	/**
	 * @param function
	 */
	public ImageCursorGraphic( Dimension displayDimension ) {
		
		super( displayDimension );

		cursorOn = false;

		cursorColorXBright = new Color( 0, 187, 255 );
		cursorColorX = cursorColorXDark = new Color( 0, 153, 221 );
		draggingCursorX = false;

		cursorColorYBright = Color.yellow;
		cursorColorY = cursorColorYDark = Color.orange;
		draggingCursorY = false;

		mouseListenersAdded = false;
		dirtyCursorX = true; 
		dirtyCursorY = true;
	
	}
	
	@Override
	public void deconstruct() {
		// TODO Auto-generated method stub
		super.deconstruct();
	}
	
	public void createImageAxesPanel() {
		
		imageAxesPanel = new CursorImageAxesPanel();
	}
	
	/**
	 * @param imageCursorPanel the imageCursorPanel to set
	 */
	public void setImageCursorPanel(ImageCursorPanel imageCursorPanel) {
		this.imageCursorPanel = imageCursorPanel;
	}

	public void addMouseListeners() {

		if( !mouseListenersAdded ) { 
			imageAxesPanel.addMouseListener( new CursorMouseListener() );
			imageAxesPanel.addMouseMotionListener( new CursorMouseMotionListener() );
			mouseListenersAdded = true; 
		}
	}

	/**
	 * @return the cursorLocation
	 */
	public double getCursorLocationX() {
		return cursorLocationX;
	}

	/**
	 * @param cursorLocation the cursorLocation to set
	 */
	public void setCursorLocationX(double cursorLocationX) {
		this.cursorLocationX = cursorLocationX;
		dirtyCursorX = true; 
		repaint();
	}

	/**
	 * @return the cursorLocation
	 */
	public double getCursorLocationY() {
		return cursorLocationY;
	}

	/**
	 * @param cursorLocation the cursorLocation to set
	 */
	public void setCursorLocationY(double cursorLocationY) {
		this.cursorLocationY = cursorLocationY;
		dirtyCursorY = true; 
		repaint();
	}

	/**
	 * @param cursorOn the cursorOn to set
	 */
	public void setCursorOn(boolean cursorOn) {
		this.cursorOn = cursorOn;
		((CursorImageAxesPanel)imageAxesPanel).createCursorX(); 
		((CursorImageAxesPanel)imageAxesPanel).createCursorY();
		repaint();
	}

	public void setDirty() {
		
		dirtyCursorX = true; 
		dirtyCursorY = true;
	}
	
	public void dragCursorX( MouseEvent e ) {

		if( e.getX() < ( displayDimension.width ) && e.getX()  >= 0 ) { 
		
			double newCursorLocationX = plotToMathX( e.getX() );
			if( imageCursorPanel != null ) imageCursorPanel.setCursorLocationX( newCursorLocationX );
			cursorLocationX = newCursorLocationX;
			dirtyCursorX = true;
			repaint();
		}
	}

	public void dragCursorY( MouseEvent e ) {

		
		if( e.getY() < displayDimension.height && e.getY() >= 0 ) { 
			double newCursorLocationY = plotToMathY( e.getY() );
			if( imageCursorPanel != null ) imageCursorPanel.setCursorLocationY( newCursorLocationY );
			cursorLocationY = newCursorLocationY;
			dirtyCursorY = true;
			repaint();
		}

	}
	
	public class CursorImageAxesPanel extends ImageAxesPanel {
		
		
		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			drawCursorX( (Graphics2D)g ); 
			drawCursorY( (Graphics2D)g ); 

		}
		
		/**
		 * draw the stroke 
		 * @param g2
		 */
		public void drawCursorX( Graphics2D g2 ) {

			if( cursorOn ) { 

				if( dirtyCursorX ) {

					createCursorX(); 
					dirtyCursorX = false;
				}

				g2.setStroke( lineStroke );
				g2.setColor( cursorColorX );
				g2.draw( cursorX );
			}

		}

		/**
		 * draw the stroke 
		 * @param g2
		 */
		public void drawCursorY( Graphics2D g2 ) {

			if( cursorOn ) { 

				if( dirtyCursorY ) {

					createCursorY(); 
					dirtyCursorY = false;
				}

				g2.setStroke( lineStroke );
				g2.setColor( cursorColorY );
				g2.draw( cursorY );
			}

		}

		
		public void createCursorX() { 

			double plotCursorX = mathToPlotX( cursorLocationX );
			cursorX = PlottingGraphics.yAxis( displayDimension.height, plotCursorX, 0 );
			cursorBoxX = new Rectangle2D.Double( plotCursorX-3, 0, 6, displayDimension.height );
		}

		public void createCursorY() { 

			double plotCursorY = mathToPlotY( cursorLocationY );
			cursorY = PlottingGraphics.xAxis( displayDimension.width, plotCursorY, 0 );
			cursorBoxY = new Rectangle2D.Double( 0, plotCursorY-3, displayDimension.width, 6 );
		}

	}

	public class CursorMouseMotionListener extends MouseMotionAdapter {

		public void mouseMoved(MouseEvent e) {

			if( cursorOn ) {

				boolean containsX = cursorBoxX.contains(e.getX(), e.getY()); 

				if( containsX && cursorColorX == cursorColorXDark ) {

					cursorColorX = cursorColorXBright;
					repaint(); 

				} else if( !containsX && cursorColorX == cursorColorXBright ) {

					cursorColorX = cursorColorXDark;
					repaint();
				}

				boolean containsY = cursorBoxY.contains(e.getX(), e.getY()); 

				if( containsY && cursorColorY == cursorColorYDark ) {

					cursorColorY = cursorColorYBright;
					repaint(); 

				} else if( !containsY && cursorColorY == cursorColorYBright ) {

					cursorColorY = cursorColorYDark;
					repaint();
				}

			}	

		}

		public void mouseDragged(MouseEvent e) {

			if( draggingCursorX ) { 

				dragCursorX(e); 

			} else if( draggingCursorY ) { 

				dragCursorY(e); 

			}
		}

	} //end inner class cursorMouseMotionListener

	public class CursorMouseListener extends MouseAdapter {

		public void mousePressed( MouseEvent e) {

			if( cursorOn && cursorBoxX.contains(e.getX(), e.getY()) ) { 

				draggingCursorX = true;

			} else if ( cursorOn && cursorBoxY.contains(e.getX(), e.getY()) ) { 

				draggingCursorY = true;

			}
		}

		public void mouseReleased(MouseEvent e) {

			draggingCursorX = false; 
			draggingCursorY = false; 
		}

	} //end inner class CursorMouseListener 

}
