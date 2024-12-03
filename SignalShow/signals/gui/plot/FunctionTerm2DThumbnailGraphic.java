package signals.gui.plot;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.media.jai.InterpolationBilinear;
import javax.media.jai.PlanarImage;

import signals.core.FunctionTerm2D;
import signals.gui.GUIDimensions;
import signals.gui.ThumbnailGraphic;

import com.sun.media.jai.widget.DisplayJAI;

public class FunctionTerm2DThumbnailGraphic extends ThumbnailGraphic {

	//source of data
	FunctionTerm2D function;

	//use this to display the image
	DisplayJAI smallDisplay, largeDisplay;
	
	//indices
	int xDimension, yDimension; 
	boolean zeroCentered;
	
	ImageDisplayMath imageMath;

	/**
	 * @param function
	 * @param dimension
	 * @param dimension2
	 * @param zeroCentered
	 */
	public FunctionTerm2DThumbnailGraphic(FunctionTerm2D function, int dimension, int dimension2, boolean zeroCentered) {
		super();
		this.function = function;
		xDimension = dimension;
		yDimension = dimension2;
		this.zeroCentered = zeroCentered;
		imageMath = new ImageDisplayMath();
	}

	public void setIndices( int xDimension, int yDimension, boolean zeroCentered ) {
		
		this.xDimension = xDimension; 
		this.yDimension = yDimension; 
		this.zeroCentered = zeroCentered;
		setDirty();
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
	
	public PlanarImage getScaledImage( Dimension d ) {
		
		double[] data = function.create( xDimension, yDimension, zeroCentered );
		return (PlanarImage) imageMath.getScaledImage(data, xDimension, yDimension, d, zeroCentered, new InterpolationBilinear());   
		
	}

	/**
	 * @param function the function to set
	 */
	public void setFunction(FunctionTerm2D function, int xDimension, int yDimension, boolean zeroCentered) {
		this.function = function;
		this.xDimension =  xDimension; 
		this.yDimension = yDimension; 
		this.zeroCentered = zeroCentered;
		setDirty();
	}
}
