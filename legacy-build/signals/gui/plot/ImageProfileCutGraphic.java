package signals.gui.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;


@SuppressWarnings("serial")
public class ImageProfileCutGraphic extends ImageCursorGraphic {

	Line2D.Double line;
	
	BasicStroke linestroke; 
	
	public ImageProfileCutGraphic(Dimension displayDimension) {
		super(displayDimension);
		line = PlottingGraphics.yAxis(displayDimension.height, 0, 0); 
		lineStroke = new BasicStroke( 3 );
	}

//	public void setVerticalLine( int x0 ) {
//		
//		double x0Location = mathToPlotX(x0); 
//		line = PlottingGraphics.yAxis(displayDimension.height, x0Location, 0); 
//		repaint();
//	}
	
	public void setYLine( int rise, int y0 ) {
		
		int xStart = zeroCentered ? dimension / 2 : 0;
		int y = y0; 
		int x = xStart; 
		
		int x1 = 0, y1 = 0, x2 = 0, y2 = 0; 

		if( rise > 0 ) {

			if( y < 0 ) { 

				//move into picture
				while( y < 0 ) {

					y += rise; 
					++x; 
				}
				
				x1 = x; 
				y1 = y; 

				//travel upwards from intercept
				while( x < dimension-1 && y < dimension-1 ) {

					y += rise; 
					++x;  
				}
				
				x2 = x; 
				y2 = y; 

			} else if( y >= dimension ) {

				//move into picture
				while( y >= dimension ) {

					y -= rise; 
					--x; 
				}

				x1 = x; 
				y1 = y; 
				
				//travel downwards from intercept
				while( x > 0 && y > 0 ) {

					y -= rise; 
					--x; 

				}
				
				x2 = x; 
				y2 = y; 

			} else { //y0 is in the center

				//travel upwards from intercept
				while( x < dimension-1 && y < dimension-1 ) {

					y += rise;
					++x; 

				}
				
				x1 = x; 
				y1 = y; 
				

				y = y0; 
				x = xStart; 

				//travel downwards from intercept
				while( x > 0 && y > 0 ) {

					y -= rise; 
					--x; 

				}
				
				x2 = x; 
				y2 = y; 

			}

		} else if ( rise < 0 ) {

			if( y < 0 ) { 

				//move into picture
				while( y < 0 ) {

					y -= rise; 
					--x;
				}
				
				x1 = x; 
				y1 = y;

				//travel upwards from intercept
				while( y < dimension-1 && x > 0 ) {

					y -= rise; 
					--x;

				}
				
				x2 = x; 
				y2 = y; 

			} else if( y >= dimension ) {

				//move into picture
				while( y >= dimension ) {

					y += rise; 
					++x; 
				}
				
				x1 = x; 
				y1 = y;

				//travel downwards from intercept
				while( y > 0 && x < dimension-1 ) {

					y += rise; 
					++x; 

				}
				
				x2 = x; 
				y2 = y; 

			} else { //y0 is in the center

				//travel upwards from intercept
				while( y < dimension-1 && x > 0 ) {

					y -= rise; 
					--x;

				}
				
				x1 = x; 
				y1 = y;

				y = y0; 
				x = xStart; 

				//travel downwards from intercept
				while( y > 0 && x < dimension-1 ) {

					y += rise; 
					++x; 

				}
				
				x2 = x; 
				y2 = y; 

			}


		} else { //flat rise( horizontal line)

			x1 = 0; 
			y1 = y0; 
			x2 = dimension; 
			y2 = y0; 

		}
		
		//int xOffset; 
		int yOffset; 
		
		if( zeroCentered ) {
			
			//xOffset = Math.max( 0, (y_dimension - x_dimension) / 2 ); 
			yOffset = Math.max( 0, (x_dimension - y_dimension) / 2 ); 
			
		} else {
			
			//xOffset = 0;
			yOffset = Math.max( 0, (x_dimension - y_dimension) );
		}
		
		
		
		line = new Line2D.Double(mathToPlotX(x1),mathToPlotY(y1+yOffset),
				mathToPlotX(x2),mathToPlotY(y2+yOffset));
		
		repaint();
	}
	
	public void setXLine( int run, int x0 ) {
		
		int yStart = zeroCentered ? dimension / 2 : 0;
		int y = yStart; 
		int x = x0; 
		
		int x1 = 0, y1 = 0, x2 = 0, y2 = 0; 

		if( run > 0 ) {

			if( x < 0 ) { 

				//move into picture
				while( x < 0 ) {

					x += run; 
					++y; 
				}
				
				x1 = x; 
				y1 = y; 

				//travel upwards from intercept
				while( x < dimension-1 && y < dimension-1 ) {

					x += run;
					++y; 
				}
				
				x2 = x; 
				y2 = y; 

			} else if( x >= dimension ) {

				//move into picture
				while( x >= dimension ) {

					x -= run; 
					--y; 
				}

				x1 = x; 
				y1 = y; 
				
				//travel downwards from intercept
				while( x > 0 && y > 0 ) {

					x -= run; 
					--y; 

				}
				
				x2 = x; 
				y2 = y; 

			} else { //x0 is in the center

				//travel upwards from intercept
				while( x < dimension-1 && y < dimension-1 ) {

					x += run;
					++y; 

				}
				
				x1 = x; 
				y1 = y; 
				

				y = yStart; 
				x = x0; 

				//travel downwards from intercept
				while( x > 0 && y > 0 ) {

					x -= run; 
					--y; 

				}
				
				x2 = x; 
				y2 = y; 

			}

		} else if ( run < 0 ) {

			if( x < 0 ) { 

				//move into picture
				while( x < 0 ) {

					x -= run; 
					--y; 
				}
				
				x1 = x; 
				y1 = y;

				//travel downwards from intercept
				while( x < dimension-1 && y > 0 ) {

					x -= run; 
					--y; 

				}
				
				x2 = x; 
				y2 = y; 

			} else if( x >= dimension ) {

				//move into picture
				while( x >= dimension ) {

					x += run; 
					++y; 
				}
				
				x1 = x; 
				y1 = y;

				//travel upwards from intercept
				while( x > 0 && y < dimension-1 ) {

					x += run; 
					++y; 

				}
				
				x2 = x; 
				y2 = y; 

			} else { //x0 is in the center

				//travel downwards from intercept
				while( x < dimension-1 && y > 0 ) {
					
					x -= run; 
					--y; 

				}
				
				x1 = x; 
				y1 = y;

				y = yStart; 
				x = x0; 

				//travel upwards from intercept
				while( x > 0 && y < dimension-1 ) {

					x += run; 
					++y; 

				}
				
				x2 = x; 
				y2 = y; 

			}


		} else { //flat run (vertical line)

			x1 = x0; 
			y2 = 0; 
			x2 = x0; 
			y2 = dimension; 

		}
		
		int xOffset; 
		//int yOffset; 
		
		if( zeroCentered ) {
			
			xOffset = Math.max( 0, (y_dimension - x_dimension) / 2 ); 
			//yOffset = Math.max( 0, (x_dimension - y_dimension) / 2 ); 
			
		} else {
			
			xOffset = 0;
			//yOffset = Math.max( 0, (x_dimension - y_dimension) );
		}
		
		
		
		line = new Line2D.Double(mathToPlotX(x1+xOffset),mathToPlotY(y1),
				mathToPlotX(x2+xOffset),mathToPlotY(y2));
		
		repaint();
	}
	
	public void createImageAxesPanel() {
		
		imageAxesPanel = new ImageProfileCutPanel();
	}
	
	public class ImageProfileCutPanel extends ImageAxesPanel {
		
		public void paintComponent( Graphics g ) {
			
			super.paintComponent(g); 
			
			Graphics2D g2 = (Graphics2D)g; 
			g2.setStroke( lineStroke );
			g2.setColor(new Color( 0, 187, 255 ));
			g2.draw( line );
		}
	}
	
}
