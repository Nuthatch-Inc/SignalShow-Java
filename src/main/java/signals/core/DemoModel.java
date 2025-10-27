package signals.core;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import signals.gui.GUIDimensions;
import signals.gui.IconThumbnailGraphic;
import signals.gui.ThumbnailProducer;

/**
 * Model for a demo or a system
 * @author julietbernstein
 *
 */
public abstract class DemoModel implements ThumbnailProducer, Showable {
	
	IconThumbnailGraphic graphic; 
	
	public DemoModel( ImageIcon icon ) {
		
		graphic = new IconThumbnailGraphic( icon, getSmallThumbnailSize(), getLargeThumbailSize() );
	}
	
	public void setDirty() {
		
		graphic.setDirty(); 
	}
	
	public abstract DemoModel clone(); 
	
	public void paintLargeGraphic(Graphics2D g) {
		graphic.paintLargeGraphic((Graphics2D)g); 

	}

	public void paintSmallGraphic(Graphics2D g) {
		graphic.paintSmallGraphic((Graphics2D)g); 

	}
	
	public void paintGraphic(Graphics2D g, int w, int h) {
		graphic.paintGraphic((Graphics2D)g, w, h); 

	}

	public Dimension getLargeThumbailSize() {
		return GUIDimensions.largePlotThumbnailDimension;
	}

	public Dimension getSmallThumbnailSize() {
		return GUIDimensions.smallPlotThumbnailDimension;
	}
	
}
