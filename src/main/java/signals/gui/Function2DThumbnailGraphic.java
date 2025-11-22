package signals.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.media.jai.InterpolationBilinear;
import javax.media.jai.PlanarImage;

import signals.core.Function2D;
import signals.gui.plot.ImageDisplayMath;

import com.sun.media.jai.widget.DisplayJAI;

public class Function2DThumbnailGraphic extends ThumbnailGraphic {

	//source of data
	Function2D function;

	//use this to display the image
	DisplayJAI smallDisplay, largeDisplay;
	
	ImageDisplayMath imageMath; 


	/**
	 * @param function
	 */
	public Function2DThumbnailGraphic(Function2D function) {
		super();
		this.function = function;
		imageMath = new ImageDisplayMath(); 
	}
	
	public static Dimension getSmallDimension() {
		
		return GUIDimensions.smallImageThumbnailDimension;
	}

	public static Dimension getLargeDimension() {
		
		return GUIDimensions.largeImageThumbnailDimension;
	}

	@Override
	public void paintLargeGraphic(Graphics2D g) {

		//if the small graphic is "dirty", create it
		if( dirtyLargeGraphic ) {

			if( largeDisplay == null) largeDisplay = new DisplayJAI( getScaledImage( getLargeDimension() ) );
			else largeDisplay.set(getScaledImage( getSmallDimension() ) );
			dirtyLargeGraphic = false;
		}
		
		largeDisplay.paintComponent(g);
	}

	@Override
	public void paintSmallGraphic(Graphics2D g) {

		//if the small graphic is "dirty", create it
		if( dirtySmallGraphic ) { 

			if( smallDisplay == null) smallDisplay = new DisplayJAI( getScaledImage( getSmallDimension() ) );
			else smallDisplay.set(getScaledImage( getSmallDimension() ) );
			dirtySmallGraphic = false;
		}

		smallDisplay.paintComponent(g);
	}
	
	public void paintGraphic(Graphics2D g, int width, int height ) {

		//if the small graphic is "dirty", create it
		if( dirtySmallGraphic ) { 
			
			Dimension d = new Dimension( width, height ); 

			if( smallDisplay == null) smallDisplay = new DisplayJAI( getScaledImage( d ) );
			else smallDisplay.set(getScaledImage( d ) );
			dirtySmallGraphic = false;
		}

		smallDisplay.paintComponent(g);
	}
	
	public PlanarImage getScaledImage( Dimension d ) {
		
		int width = function.getDimensionX(); 
		int height = function.getDimensionY();

		double[] re = function.getReal(); 
		return (PlanarImage) imageMath.getScaledImage(re, width, height, d, 
					function.isZeroCentered(), new InterpolationBilinear() );
		
	}
	

}
