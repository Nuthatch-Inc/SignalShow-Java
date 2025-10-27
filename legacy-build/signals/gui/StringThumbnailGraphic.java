/**
 * 
 */
package signals.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Draws a character on a thumbnail graphic 
 * @author Juliet
 *
 */
public class StringThumbnailGraphic extends ThumbnailGraphic {

	static Font smallFont = new Font( "sansserif", Font.BOLD, 80 );
	static Font largeFont = new Font( "sansserif", Font.BOLD, 120 );
	String text; 
	Color textColor; 
	Dimension smallSize, largeSize; 
	
	public StringThumbnailGraphic( String text, Dimension smallSize, Dimension largeSize ) {
		
		this.text = text;
		this.smallSize = smallSize; 
		this.largeSize = largeSize; 
		textColor = Color.black;
	}
	
	/*
	 * (non-Javadoc)
	 * @see signals.gui.ThumbnailGraphic#paintLargeGraphic(java.awt.Graphics2D)
	 */
	public void paintLargeGraphic(Graphics2D g2) {
		
		paintGraphic( g2, largeFont, largeSize );
	}

	/* (non-Javadoc)
	 * @see signals.gui.ThumbnailProducer#paintSmallGraphic(java.awt.Graphics2D)
	 */
	public void paintSmallGraphic(Graphics2D g2) {
		
		paintGraphic( g2, smallFont, smallSize );
	}
	
	public void paintGraphic( Graphics2D g2, Font font, Dimension size ) {
	
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		
		g2.setFont( font );
		g2.setColor( textColor );
        g2.drawString(text, 5, size.height/2 + 20);
		
	}

}
