package signals.gui.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

@SuppressWarnings("serial")
public class ImageRadiusDisplayPanel extends InteractiveImageGraphic {

	Shape innerRadiusShape, outerRadiusShape; 
	Area lowFrequencyRegion, midFrequencyRegion, highFrequencyRegion; 
	double innerRadius, outerRadius; 
	boolean dirty; 
	
	Color highFrequencyColor, midFrequencyColor, lowFrequencyColor; 
	
	public ImageRadiusDisplayPanel(Dimension displayDimension) {
		super(displayDimension);
		lowFrequencyColor = new Color( 0, 0, 255, 50 ); 
		midFrequencyColor = new Color( 0, 255, 0, 50 ); 
		highFrequencyColor = new Color( 255, 0, 0, 50 ); 
	}
	
	public double getOuterRadius() {
		return outerRadius;
	}

	public void setOuterRadius(double outerRadius) {
		this.outerRadius = outerRadius;
		dirty = true; 
		repaint(); 
	}

	public void createImageAxesPanel() {
		
		imageAxesPanel = new RadiusImageAxesPanel();
	}
	

	public double getInnerRadius() {
		return innerRadius;
	}

	public void setInnerRadius(double innerRadius) {
		this.innerRadius = innerRadius;
		dirty = true; 
		repaint(); 
	}

	public class RadiusImageAxesPanel extends ImageAxesPanel {
		
		
		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			drawRadii( (Graphics2D)g );  

		}
		
		/**
		 * draw the stroke 
		 * @param g2
		 */
		public void drawRadii( Graphics2D g2 ) {
			
			if( dirty ) {
				
				createRadii(); 
			}
			
			g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON );

			g2.setColor( highFrequencyColor ); 
			g2.fill(highFrequencyRegion); 
			g2.setColor( midFrequencyColor ); 
			g2.fill(midFrequencyRegion); 
			g2.setColor( lowFrequencyColor ); 
			g2.fill(lowFrequencyRegion); 
			
			g2.setColor( Color.darkGray );
			g2.draw( innerRadiusShape );
			g2.draw( outerRadiusShape );

			
			
		}

		
		public void createRadii() { 

			int centerX = 0;
			int centerY = 0; 
			if( zeroCentered ) {
				
				centerX += dimension/2; 
				centerY += dimension/2;
			}
			
			double cornerX = mathToPlotX(centerX - innerRadius); 
			double cornerY = mathToPlotX(centerY - innerRadius); 
			double width = mathToPlotX(2*innerRadius); 
			double height = mathToPlotX(2*innerRadius); 
			innerRadiusShape = new Ellipse2D.Double( cornerX, cornerY, width , height ); 
			
			cornerX = mathToPlotX(centerX - outerRadius);
			cornerY = mathToPlotX(centerY - outerRadius); 
			width = mathToPlotX(2*outerRadius); 
			height = mathToPlotX(2*outerRadius); 
			outerRadiusShape = new Ellipse2D.Double( cornerX, cornerY, width , height ); 
			
			Shape rectangle = new Rectangle2D.Double( 0, 0, dimension, dimension ); 
		
			lowFrequencyRegion  = new Area( innerRadiusShape ); 
			midFrequencyRegion = new Area( outerRadiusShape ); 
			highFrequencyRegion = new Area( rectangle ); 
			
			highFrequencyRegion.subtract(midFrequencyRegion); 
			midFrequencyRegion.subtract( lowFrequencyRegion ); 

		}


	}

}
