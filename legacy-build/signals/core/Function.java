package signals.core;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.lang.ref.SoftReference;

import signals.gui.ThumbnailGraphic;
import signals.gui.ThumbnailProducer;
import signals.gui.datagenerator.FunctionToolBar;
import signals.operation.ArrayMath;

/**
 * Represents a 1D or 2D complex function in terms of real and imaginary parts or magnitude and phase. 
 * @author Juliet
 */
public abstract class Function implements ThumbnailProducer, Showable, FunctionProducer {
	
	//cached data
	@SuppressWarnings("unchecked")
	protected SoftReference real, imaginary, magnitude, phase; 
	
	//descriptor for tool tips
	protected String descriptor;
	
	//thumbnail graphic 
	protected ThumbnailGraphic thumbnailGraphic;
	
	//whether or not this function is zero centered
	boolean zeroCentered;
	
	//thumbnail sizes
	Dimension largeThumbnailSize, smallThumbnailSize;
	
	//true if this function was created with an operation, so it has a custom equation
	boolean hasCustomEquation; 
	String customEquation; 
	
	long ID;  
	
	//if the function is defined as complex, real and imaginary parts are edited together
	Constants.FunctionType type; 
	
	String showState; 
	
	//true if this function has not yet been edited
	boolean newState; 
	
	/**
	 * Creates a new function with given descriptor
	 * @param descriptor
	 */
	public Function(String descriptor, boolean zeroCentered) {

		this.descriptor = descriptor;
		this.zeroCentered = zeroCentered;
		hasCustomEquation = false;
		ID = Core.getFunctionCreationOptions().getNextID(); 
		type = Constants.FunctionType.NOT_COMPLEX; 
		showState = FunctionToolBar.EDIT_A; 
		newState = true; 
	
	}

	public Constants.FunctionType getType() {
		return type;
	}

	public void setType(Constants.FunctionType type) {
		this.type = type;
	}

	public abstract Function clone(); 
	
	public Function getFunction() {
		
		return this;
	}

	public void freeMemory() {
		
		real = null; 
		imaginary = null; 
		magnitude = null; 
		phase = null; 
	
	}

	public long getID() {
		return ID;
	}
	
	public void modifyID() {
		
		ID = Core.getFunctionCreationOptions().getNextID(); 
	}

	/**
	 * @return the descriptor
	 */
	public String getDescriptor() {
	
		return descriptor; 
	}
	
	public String getShowState() {
		return showState;
	}

	public void setShowState(String showState) {
		this.showState = showState;
	}

	public void setDirty() {
		
		thumbnailGraphic.setDirty(); 
	}
	
	/**
	 * @param descriptor the descriptor to set
	 */
	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}
	
	public double[] getPart( Constants.Part part ) {
		
		double[] toReturn = null; 
		
		switch( part ) {
		
		case REAL_PART:
			 toReturn = getReal(); 
			break; 
		case IMAGINARY_PART: 
			 toReturn = getImaginary();
			break; 
		case MAGNITUDE: 
			 toReturn = getMagnitude();
			break; 
		case PHASE: 
			 toReturn = getPhase();
			break;
		case LOG_MAGNITUDE: 
			toReturn = ArrayMath.log10( getMagnitude() ); 
			break; 
		case LOG_SQUARED_MAGNITUDE: 
			toReturn = ArrayMath.log10(getSquaredMagnitude());
			break; 
		case SQUARED_MAGNITUDE: 
			toReturn = getSquaredMagnitude(); 
			break; 
		case UNWRAPPED_PHASE: 
			toReturn = getUnwrappedPhase(); 
			break; 
		}
		
		return toReturn;
	}
	
	/**
	 * 
	 * @return the real part of this function
	 */
	@SuppressWarnings("unchecked")
	public double[] getReal() {
		
		double[] re = null; 
		if( real == null || real.get() == null ) {
			
			re = createReal(); 
			real = new SoftReference( re ); 
			
		} else re = (double[]) real.get(); 
		return re; 
	}
	
	
	/**
	 * 
	 * @return the imaginary part of this function
	 */
	@SuppressWarnings("unchecked")
	public double[] getImaginary() {
		
		double[] im = null; 
		if( imaginary == null || imaginary.get() == null ) {
			
			im = createImaginary(); 
			imaginary = new SoftReference( im ); 
			
		} else im = (double[]) imaginary.get(); 
		return im; 
	}
	
	/**
	 * 
	 * @return the magnitude of this function
	 */
	@SuppressWarnings("unchecked")
	public double[] getMagnitude() {
		
		double[] ma = null; 
		if( magnitude == null || magnitude.get() == null ) {
			
			ma = createMagnitude(); 
			magnitude = new SoftReference( ma ); 
			
		} else ma = (double[]) magnitude.get(); 
		return ma; 
	}
	
	/**
	 * 
	 * @return the phase of this function
	 */
	@SuppressWarnings("unchecked")
	public double[] getPhase() { 
		
		double[] ph = null; 
		if( phase == null || phase.get() == null ) {
			
			ph = createPhase(); 
			phase = new SoftReference( ph ); 
			
		} else ph = (double[]) phase.get(); 
		return ph; 
	}
	
	public abstract double[] getUnwrappedPhase(); 
	
	public double[] getSquaredMagnitude() {
		
		double[] magnitude = getMagnitude(); 
		double[] squaredMagnitude = new double[magnitude.length]; 
		for( int i = 0; i < magnitude.length; ++i ) {
			
			squaredMagnitude[i] = magnitude[i]*magnitude[i];
		}
		
		return squaredMagnitude; 
		
	}

	protected abstract double[] createReal(); 
	protected abstract double[] createImaginary(); 
	protected abstract double[] createMagnitude(); 
	protected abstract double[] createPhase(); 
	
	/**
	 * @return the zeroCentered
	 */
	public boolean isZeroCentered() {
		return zeroCentered;
	}

	/**
	 * @param zeroCentered the zeroCentered to set
	 */
	public void setZeroCentered(boolean zeroCentered) {
		this.zeroCentered = zeroCentered;
		thumbnailGraphic.setDirty();
		modifyID();
	}
	
	/**
	 * paints a small representation of this function to a graphics context
	 * @param g the graphics context on which to paint this function
	 */
	public void paintSmallGraphic( Graphics2D g ) {
		
		thumbnailGraphic.paintSmallGraphic(g);
	}
	
	/**
	 * paints a large representation of this function to a graphics context
	 * @param g the graphics context on which to paint this function
	 */
	public void paintLargeGraphic( Graphics2D g ) {
		
		thumbnailGraphic.paintLargeGraphic(g);
	}
	
	public void paintGraphic( Graphics2D g, int w, int h ) {
		
		thumbnailGraphic.paintGraphic(g, w, h);
	}	
	
	public Dimension getLargeThumbailSize() {
		
		return largeThumbnailSize; 
	}


	public Dimension getSmallThumbnailSize() {
	
		return smallThumbnailSize;
	}
	
	public String getCompactDescriptor() {
	
		if( hasCustomEquation ) return customEquation; 
		if( descriptor == null || descriptor.equals("") ) return getEquation();
		return descriptor; 
	}
	
	public String getLongDescriptor() {
		
		if( hasCustomEquation ) return customEquation; 
		return descriptor +'[' + getIndependentVariables() + "] = " + getEquation(); 
	}
	public abstract String getIndependentVariables();
	public abstract String getEquation();
	public void setEquation( String customEquation ) {
		
		hasCustomEquation = true; 
		this.customEquation = customEquation;
	}
}

