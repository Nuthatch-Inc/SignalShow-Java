package signals.gui.plot;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.DataBufferDouble;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import java.awt.image.RenderedImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.RenderedOp;


public class ImageDisplayMath {

	double minValue, maxValue;

	RenderedImage resizedImage; 

	boolean adjustExtrema; 
	double adjustment; 

	public ImageDisplayMath() {

		minValue = maxValue = -1; 
		adjustExtrema = true; 
		adjustment = .01; 
	}

	/**
	 * @return the minValue from the last autoscaled image 
	 */
	public double getMinValue() {
		return minValue;
	}

	/**
	 * @return the maxValue from the last autoscaled
	 */
	public double getMaxValue() {
		return maxValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * Scales and image so that 0 = min and 255 = max
	 * Modified from from JAIStuff by Rafael Santos - https://jaistuff.dev.java.net/code/display.DisplayDEM.html
	 * @param image
	 * @return
	 */
	public RenderedImage autoScale( RenderedImage image ) {

		setAutoScale( image ); 
		return scale( image ); 

	}

	public static RenderedImage mergeBands( RenderedImage band1, RenderedImage band2, RenderedImage band3 ) {

		ParameterBlockJAI pbjai = new ParameterBlockJAI("bandmerge"); 
		pbjai.setSource(band1, 0); 
		pbjai.setSource(band2, 1); 
		pbjai.setSource(band3, 2); 
		return JAI.create("bandmerge",pbjai,null);  

	}

	public void setAutoScale( RenderedImage image ) {

		ParameterBlock pbMaxMin = new ParameterBlock();
		pbMaxMin.addSource(image);
		RenderedOp extrema = JAI.create("extrema", pbMaxMin);

		double[] allMins = (double[])extrema.getProperty("minimum");
		double[] allMaxs = (double[])extrema.getProperty("maximum");

		double max = Double.MIN_VALUE; 
		double min = Double.MAX_VALUE; 

		for( int i = 0; i < allMins.length; i++ ) {

			if( allMins[i] < min ) min = allMins[i]; 
			if( allMaxs[i] > max ) max = allMaxs[i]; 
		}


		if( adjustExtrema ) {

			double[] extremaToAdjust = { min, max }; //need to check for other bands in image
			double[] adjustedExtrema = contrastAdjustExtrema( extremaToAdjust, adjustment ); 
			minValue = adjustedExtrema[0]; 
			maxValue = adjustedExtrema[1];

		} else {

			minValue = min; 
			maxValue = max; 

		}

	}

	public boolean isAdjustExtrema() {
		return adjustExtrema;
	}

	public void setAdjustExtrema(boolean adjustExtrema) {
		this.adjustExtrema = adjustExtrema;
	}

	public RenderedImage scale( RenderedImage image ) {

		double scale = 255.0/(maxValue-minValue);

		double[] scalar = { scale }; 
		double[] offset = { -(minValue*scale) };

		ParameterBlock pbRescale = new ParameterBlock();
		pbRescale.add(scalar);
		pbRescale.add(offset);
		pbRescale.addSource(image);
		return JAI.create("rescale", pbRescale,null);
	}

	public static double[] contrastAdjustExtrema( double[] input_extrema, double adjustment ) {

		double minVal = input_extrema[0];
		double maxVal = input_extrema[1];

		double threshold = adjustment / 100;
		if ( (maxVal - minVal) < threshold ) {
			minVal -= adjustment;
			maxVal += adjustment; 
		}

		double extrema[] = {minVal, maxVal};
		return extrema;
	}

	public static RenderedImage scaleImage( RenderedImage image, double scale ) {

		ParameterBlock pbRescale = new ParameterBlock();

		double[] scalar = { scale }; 
		double[] offset = { 0 }; 

		pbRescale.add(scalar);
		pbRescale.add(offset);
		pbRescale.addSource(image);
		return JAI.create("rescale", pbRescale,null);

	}

	public static RenderedImage getResizedImage( RenderedImage image, 
			int x_dimension, int y_dimension, Interpolation interpolation ) {

		ParameterBlock pb = new ParameterBlock();  
		pb.addSource(image);  
		pb.add((float)((double)x_dimension)/image.getWidth());  
		pb.add((float)((double)y_dimension/image.getHeight()));
		pb.add(0.0f);
		pb.add(0.0f);  
		pb.add( interpolation );  

		// Creates a new, scaled image and uses it on the DisplayJAI component
		return JAI.create("scale", pb);
	}

	public static RenderedImage data2Image( double[] data, int xDimension, int yDimension ) {

		DataBufferDouble dbuffer = new DataBufferDouble( data, data.length );
		
//		SampleModel sampleModel = RasterFactory.createBandedSampleModel( 
//				DataBuffer.TYPE_DOUBLE, xDimension, yDimension, 1 );
//		ColorModel colorModel = PlanarImage.createColorModel( sampleModel );
		
		SampleModel sampleModel = ImageModelCache.getSampleModelDouble(xDimension, yDimension);
		ColorModel colorModel = ImageModelCache.getColorModelDouble(xDimension, yDimension); 
		
		Raster raster = RasterFactory.createWritableRaster(sampleModel, dbuffer, new Point( 0,0) );
		TiledImage image = new TiledImage(0,0,xDimension,yDimension,0,0, sampleModel, colorModel); 
		image.setData( raster );

		return image;
	}

	public static RenderedImage data2Image( byte[] data, int xDimension, int yDimension ) {

		DataBufferByte dbuffer = new DataBufferByte( data, data.length );
		SampleModel sampleModel = RasterFactory.createBandedSampleModel(
				DataBuffer.TYPE_BYTE, xDimension, yDimension, 1);
		ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
		Raster raster = RasterFactory.createWritableRaster(sampleModel, dbuffer, new Point(0,0));
		TiledImage image = new TiledImage(0,0,xDimension,yDimension,0,0, sampleModel, colorModel);
		image.setData( raster );

		return image;
	}

	public static double[] image2Data( RenderedImage image ) {

		int width = image.getWidth(); 
		int height = image.getHeight();
		Raster inputRaster = image.getData();  
		double[] pixels = new double[width*height+1];  
		inputRaster.getPixels(0,0,width,height,pixels);
		return pixels; 
	}

	/**
	 * 
	 * @param xDimension original dimension of the image
	 * @param yDimension original dimension of the image
	 * @param d dimension of the thumbnail to be produced
	 * @return
	 */
	public static Dimension getThumbDimension( int xDimension, int yDimension, Dimension d ) {

		//Code to maintain aspect ratio modified from http://schmidt.devlib.org/java/save-jpeg-thumbnail.html
		int thumbWidth = (int) d.getWidth(); 
		int thumbHeight = (int) d.getHeight();
		double thumbRatio = (double)thumbWidth / (double)thumbHeight;
		double imageRatio = (double)xDimension / (double)yDimension;

		if ( thumbRatio < imageRatio ) {

			thumbHeight = (int)( thumbWidth / imageRatio );

		} else {

			thumbWidth = (int)( thumbHeight * imageRatio );
		}

		return new Dimension( thumbWidth, thumbHeight );
	}

	public RenderedImage getScaledImage( double[] data, int xDimension, int yDimension, Dimension d, 
			boolean zeroCentered, Interpolation interpolation ) {

		return getScaledImage( data, xDimension, yDimension, d, zeroCentered, interpolation, true ); 
	}

	public RenderedImage getScaledImage( double[] dataR, double[] dataG, double[] dataB,
			int xDimension, int yDimension, Dimension d,
			boolean zeroCentered, Interpolation interpolation, boolean autoscale ) {


		RenderedImage RenderedImageR = data2Image( dataR, xDimension, yDimension );
		RenderedImage RenderedImageG = data2Image( dataG, xDimension, yDimension );
		RenderedImage RenderedImageB = data2Image( dataB, xDimension, yDimension );

		RenderedImage colorImage = mergeBands(RenderedImageR, RenderedImageG, RenderedImageB);

		resizedImage = (xDimension == d.width && yDimension == d.height) ? colorImage : 
			resize( colorImage, xDimension, yDimension, d, zeroCentered, interpolation );

		if( autoscale ) {

			setAutoScale(colorImage); 
		}

		return getScaledImage(); 

	}


	/**
	 * Creates an image scaled between 0 and 1 of dimension d
	 * @param data input data
	 * @param xDimension the dimension of the data in the x direction
	 * @param yDimension the dimension of the data in the y direction
	 * @param d the dimension of the desired image
	 * @return
	 */
	public RenderedImage getScaledImage( double[] data, int xDimension, int yDimension, Dimension d, 
			boolean zeroCentered, Interpolation interpolation, boolean autoscale ) {

		RenderedImage renderedImage = data2Image( data, xDimension, yDimension );

		resizedImage = (xDimension == d.width && yDimension == d.height) ? renderedImage : 
			resize( renderedImage, xDimension, yDimension, d, zeroCentered, interpolation ); 

		if( autoscale ) {

			setAutoScale(renderedImage); 
		}

		return getScaledImage(); 

	}

	public RenderedImage resize( RenderedImage RenderedImage, int xDimension, int yDimension, Dimension d, 
			boolean zeroCentered, Interpolation interpolation ) {

		Dimension thumbDimension = getThumbDimension(xDimension, yDimension, d); 

		ParameterBlock pb = new ParameterBlock();  
		pb.addSource(RenderedImage);  
		pb.add((float)(thumbDimension.width/(double)xDimension));  
		pb.add((float)(thumbDimension.height/(double)yDimension));

		if( zeroCentered ) {

			pb.add((float)(d.getWidth()-thumbDimension.width)/2.0f);
			pb.add((float)(d.getHeight()-thumbDimension.height)/2.0f);  

		} else if( xDimension > yDimension ) {

			pb.add((float)(d.getWidth()-thumbDimension.width));
			pb.add((float)(d.getHeight()-thumbDimension.height)); 

		} else {
			pb.add( 0.0f ); 
			pb.add( 0.0f );
		}

		pb.add(interpolation);  

		// Creates a new, scaled image and uses it on the DisplayJAI component
		return JAI.create("scale", pb); 
	}

	//code from http://www.hanhuy.com/pfn/java-image-thumbnail-comparison?page=2
	static BufferedImage awtScaleImage(BufferedImage image,
			int maxSize, int hint) {
		// We use AWT Image scaling because it has far superior quality
		// compared to JAI scaling.  It also performs better (speed)!
	
		int w = image.getWidth();
		int h = image.getHeight();
		float scaleFactor = 1.0f;
		if (w > h)
			scaleFactor = ((float) maxSize / (float) w);
		else
			scaleFactor = ((float) maxSize / (float) h);
		w = (int)(w * scaleFactor);
		h = (int)(h * scaleFactor);
		// since this code can run both headless and in a graphics context
		// we will just create a standard rgb image here and take the
		// performance hit in a non-compatible image format if any
		Image i = image.getScaledInstance(w, h, hint);
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(i, null, null);
		g.dispose();
		i.flush();
		return image;
	}


	public RenderedImage getScaledImage() {

		RenderedImage surrogateImage = scale( resizedImage );
		return convertToDisplayType( surrogateImage );
	}

	public static RenderedImage convertToDisplayType( RenderedImage image ) {

		ParameterBlock pbConvert = new ParameterBlock();
		pbConvert.addSource(image);
		pbConvert.add(DataBuffer.TYPE_BYTE);
		return JAI.create("format", pbConvert);

	}
}
