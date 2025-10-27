/**
 * 
 */
package signals.functionterm;

import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.lang.ref.SoftReference;

import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.swing.SpinnerNumberModel;

import signals.core.Constants;
import signals.core.DataGeneratorTypeModel;
import signals.gui.plot.ImageDisplayMath;

/**
 * @author Juliet
 *
 */
public abstract class ImageFunctionTerm2D extends AnalyticFunctionTerm2D {

	public ImageFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	int mode; 
	
	//soft reference to the image (PlanarImage) 
	@SuppressWarnings("unchecked")
	protected SoftReference image; 
	
	public ImageFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);		
		mode = Constants.SCALE_AND_PAD_MODE;
	}
	
	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}
	

	@Override
	public boolean hasHeight() {
		return true;
	}

	@Override
	public boolean hasWidth() {
		return true; 
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public abstract PlanarImage loadImage(); 
	public abstract Interpolation getInterpolation();
	
	/**
	 * 
	 * @return the image with no processing 
	 */
	@SuppressWarnings("unchecked")
	public PlanarImage getImage() {
		
		if( image != null && image.get() != null ) return ( PlanarImage ) image.get(); 
		PlanarImage img = loadImage(); 
		image = new SoftReference( img ); 
		return img; 
	}
	
	
	public int boundValue( int value, int min, int max ) {
		
		return Math.max( min, Math.min( value, max ) );
	}
	
	public double[] createScaledPadded( int x_dimension, int y_dimension, boolean zeroCentered ) {
		
		int centerX = (int)getCenterX(); 
		int centerY = (int)getCenterY(); 
		int width = (int)getWidth(); 
		int height = (int)getHeight(); 
		
		if( zeroCentered ) {
			
			centerX += x_dimension/2; 
			centerY += y_dimension/2; 
		}
		
		centerY = y_dimension - 1 - centerY; 
		
		int left = boundValue( centerX - width/2, 0, x_dimension-1 ); 
		int right = boundValue( centerX + width/2, 0, x_dimension-1 );
		int top = boundValue( centerY - height/2, 0, y_dimension-1 ); 
		int bottom = boundValue( centerY + height/2, 0, y_dimension-1 ); 
		
		int w = right-left;
		int h = bottom-top;
		
		PlanarImage scaledImage = getScaledResizedImage( w, h );
		Raster outputRaster = scaledImage.getData();
		double[] content_pixels = new double[w*h];
		outputRaster.getPixels(0,0,w,h,content_pixels);
		
		double[] pixels = new double[x_dimension*y_dimension];
		
		//set the correct pixels
		int c_idx = 0; 
		int idx = 0;
		for( int j = top; j < bottom; ++j ) {
			
			idx = j*x_dimension+left;
			for( int i=left; i < right; ++i ) {
			
				pixels[idx++] = content_pixels[c_idx++];
				
			}
		}
		
		return pixels;
	}
	
	
	public double[] createCropped( int x_dimension, int y_dimension, boolean zeroCentered ) {
		
		int centerX = (int)getCenterX(); 
		int centerY = (int)getCenterY(); 
		int width = (int)getWidth(); 
		int height = (int)getHeight(); 
		
		if( zeroCentered ) {
			
			centerX += x_dimension/2; 
			centerY += y_dimension/2; 
		}
		
		centerY = y_dimension - 1 - centerY; 
		
		int left = boundValue( centerX - width/2, 0, x_dimension-1 ); 
		int right = boundValue( centerX + width/2, 0, x_dimension-1 );
		int top = boundValue( centerY - height/2, 0, y_dimension-1 ); 
		int bottom = boundValue( centerY + height/2, 0, y_dimension-1 ); 
		
		PlanarImage scaledImage = getScaledResizedImage( x_dimension, y_dimension );
		Raster outputRaster = scaledImage.getData();
		double[] pixels = new double[x_dimension*y_dimension];
		outputRaster.getPixels(0,0,x_dimension,y_dimension,pixels);
		
		//set the top pixels to 0 
		int idx = 0;
		for( int j = 0; j < top; ++j ) {
			
			for( int i=0; i < x_dimension; ++i ) {
				
				pixels[idx++] = 0; 
			}
		}
		
		//set the left and right pixels to 0 
		for( int j = top; j <= bottom; ++j ) {
			
			idx = j*x_dimension; 
			for( int i=0; i < left; ++i ) {
				
				pixels[idx++] = 0; 
			}
			
			idx = j*x_dimension + right+1; 
			for( int i=right+1; i < x_dimension; ++i ) {
				
				pixels[idx++] = 0; 
			}
		}
		
		//set the bottom pixels to 0 
		idx = (bottom+1)*x_dimension;
		for( int j = bottom+1; j < y_dimension; ++j ) {
			
			for( int i=0; i < x_dimension; ++i ) {
				
				pixels[idx++] = 0; 
			}
		}
		
		
		return pixels;
	}
	
	@Override
	public double[] create(int x_dimension, int y_dimension, boolean zeroCentered) {
		
		int centerX = (int)getCenterX(); 
		int centerY = (int)getCenterY(); 
		int width = (int)getWidth(); 
		int height = (int)getHeight(); 
		
		if( !zeroCentered ) {
			
			centerX -= x_dimension/2; 
			centerY -= y_dimension/2; 
		}
		
		//do nothing if scaling or cropping is not needed
		if( x_dimension == width && y_dimension == height && centerX == 0 && centerY == 0 ) {
			
			PlanarImage scaledImage = getScaledResizedImage( x_dimension, y_dimension );
			Raster outputRaster = scaledImage.getData();
			double[] pixels = new double[x_dimension*y_dimension];
			outputRaster.getPixels(0,0,x_dimension,y_dimension,pixels);
			return pixels;
		}
		
		double[] output = null; 
		
		switch( mode ) {
		
		case Constants.CROP_MODE: 
			output = createCropped(x_dimension, y_dimension, zeroCentered); 
			break; 
		case Constants.SCALE_AND_PAD_MODE: 
			output = createScaledPadded(x_dimension, y_dimension, zeroCentered);
			break; 
		case Constants.SCALE_AND_WRAP_MODE:
			break; 
		case Constants.SCALE_AND_MIRROR_MODE: 
			break; 
		}
		
		return output;
	
	}
	
	/**
	 * Returns a copy of the image that has been scaled by the amplitude and resized to the given dimension
	 * @param d
	 * @param zeroCentered
	 * @return
	 */
	public PlanarImage getScaledResizedImage( int x_dimension, int y_dimension  ) {
		
		PlanarImage resized = getResizedImage(x_dimension, y_dimension );
		if( getAmplitude() == 1 ) return resized; 
		return (PlanarImage) ImageDisplayMath.scaleImage( resized, getAmplitude() );
	}
	
	/**
	 * Creates a copy of the image representing this function resized to x_dimension by x_dimension
	 * @param x_dimension
	 * @param y_dimension
	 * @param zeroCentered
	 * @return
	 */
	public PlanarImage getResizedImage( int x_dimension, int y_dimension ) { 

		return (PlanarImage) ImageDisplayMath.getResizedImage( getImage(), x_dimension, y_dimension, getInterpolation() );
		
	}
	
	@SuppressWarnings("unchecked")
	public void initTypeModel( DataGeneratorTypeModel model ) {
		
		//define styleCode
		model.setStyleCode( Constants.StyleCode.IMAGE );
		
		Class[] param_classes = { Double.class, Double.class, Double.class, Double.class, Double.class };
		model.setParamClasses( param_classes );
		
		String[] param_names = { "Amplitude", "X-Center", "Y-Center", "Width", "Height" }; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double( 1.0 ), new Double( 0.0 ), new Double( 0.0 ), 
				new Double( 32.0 ), new Double( 32.0 )};
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel xCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel yCenterSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 32.0, 0, 100000.0, 1.0);
		SpinnerNumberModel heightSpinnerModel = new SpinnerNumberModel( 32.0, 0, 100000.0, 1.0);
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, xCenterSpinnerModel, yCenterSpinnerModel,
								widthSpinnerModel, heightSpinnerModel };
		model.setSpinnerModels(spinnerModels);

	}
	
}
