package signals.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;


public abstract class ThumbnailGraphic {
		
	//used for blank "padding" around the content
	public static int OFFSET = 10;
	
	//true if the graphics are "out of date" - the source data has changed
	protected boolean dirtySmallGraphic, dirtyLargeGraphic; 
	
	/**
	 * 
	 */
	public ThumbnailGraphic() {
		super();
		setDirty(); 
	}

	/**
	 * paints a small representation of this function to a graphics context
	 * @param g the graphics context on which to paint this function
	 */
	public abstract void paintSmallGraphic( Graphics2D g ); 
	
	/**
	 * paints a large representation of this function to a graphics context
	 * @param g the graphics context on which to paint this function
	 */
	public abstract void paintLargeGraphic( Graphics2D g );
	
	public void paintGraphic( Graphics2D g, int w, int h ) {}; 

	/**
	 * @param dirty the dirty to set
	 */
	public void setDirty() {
		dirtySmallGraphic = true; 
		dirtyLargeGraphic = true;
	}
	
	public static Dimension getSmallDimension() {
		
		return GUIDimensions.smallPlotThumbnailDimension;
	}

	public static Dimension getLargeDimension() {
		
		return GUIDimensions.largePlotThumbnailDimension;
	}
	
	public void mouseClicked(MouseEvent e) {}
	
}
