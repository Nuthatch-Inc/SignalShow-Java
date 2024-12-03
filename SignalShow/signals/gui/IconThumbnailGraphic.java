/**
 * 
 */
package signals.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;

/**
 * Draws a character on a thumbnail graphic 
 * @author Juliet
 *
 */
public class IconThumbnailGraphic extends ThumbnailGraphic {

	Image image; 
	int imageWidth; 
	int imageHeight; 
	Dimension smallSize, largeSize; 
	
	public IconThumbnailGraphic( ImageIcon icon, Dimension smallSize, Dimension largeSize ) {
		
		image = icon.getImage(); 
		imageWidth = icon.getIconWidth(); 
		imageHeight = icon.getIconHeight(); 
		this.smallSize = smallSize; 
		this.largeSize = largeSize;
	}
	
	/*
	 * (non-Javadoc)
	 * @see signals.gui.ThumbnailGraphic#paintLargeGraphic(java.awt.Graphics2D)
	 */
	public void paintLargeGraphic(Graphics2D g2) {
		
		paintGraphic( g2, largeSize );
	}

	/* (non-Javadoc)
	 * @see signals.gui.ThumbnailProducer#paintSmallGraphic(java.awt.Graphics2D)
	 */
	public void paintSmallGraphic(Graphics2D g2) {
		
		paintGraphic( g2, smallSize );
	}
	
	public void paintGraphic(Graphics2D g2, int width, int height) {
		
		paintGraphic( g2, new Dimension( width, height ) );
	}
	
	public void paintGraphic( Graphics2D g2, Dimension size ) {
	
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		
		int xPos = Math.abs(size.width - imageWidth)/2; 
		int yPos = Math.abs(size.height - imageHeight)/2; 
		
		g2.drawImage(image, xPos, yPos, null);
		
	}

}
