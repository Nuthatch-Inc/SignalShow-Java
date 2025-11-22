package signals.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;

public interface ThumbnailProducer {
	
	//TODO: add "right click" descriptions"
	
	/**
	 * paints a small representation of this function to a graphics context
	 * @param g the graphics context on which to paint this function
	 */
	public void paintSmallGraphic( Graphics2D g );
	
	public void setDirty(); 
	
	/**
	 * paints a large representation of this function to a graphics context
	 * @param g the graphics context on which to paint this function
	 */
	public void paintLargeGraphic( Graphics2D g );
	
	/**
	 * 
	 * @return the large dimension of this thumbnail
	 */
	public Dimension getLargeThumbailSize(); 
	
	/**
	 * 
	 * @return the small dimension of this thumbnail
	 */
	public Dimension getSmallThumbnailSize(); 
	
	/**
	 * 
	 * @return compact descriptor for tool tips 
	 */
	public String getCompactDescriptor();
	
	/**
	 * 
	 * @return long descriptor for tool tips
	 */
	public String getLongDescriptor();

	public void paintGraphic(Graphics2D g, int width, int height); 

}
