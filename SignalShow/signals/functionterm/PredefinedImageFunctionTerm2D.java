package signals.functionterm;

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.PlanarImage;

import signals.core.DataGeneratorTypeModel;
import signals.gui.plot.ImageDisplayMath;
import signals.io.ResourceLoader;

/**
 * Images such as "Lena" are packaged with the JAR. 
 * This class allows the user to load one of these images. 
 * @author Juliet
 *
 */
public abstract class PredefinedImageFunctionTerm2D extends ImageFunctionTerm2D {

	public PredefinedImageFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static int filename_idx = 0; 

	public PredefinedImageFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	/**
	 * Loads the image and scales it so that 0 -> 0 and 255 -> 1
	 */
	public PlanarImage loadImage() { 

		//read in the image and scale it between 0 and 1
		PlanarImage tempImage = ResourceLoader.loadImage(getFilename(filename_idx));
		SampleModel sm = tempImage.getSampleModel();
		int nbands = sm.getNumBands();
		Raster inputRaster = tempImage.getData();
		int width = tempImage.getWidth();
		int height = tempImage.getHeight();
		int[] pixels = new int[nbands*width*height];
		inputRaster.getPixels(0,0,width,height,pixels);

		double[] data = scaleData( pixels, width, height, nbands );
		return (PlanarImage) ImageDisplayMath.data2Image(data, width, height);

	}

	public double[] scaleData( int[] pixels, int width, int height, int nbands ) {

		int idx = 0;

		double amplitudeScale = 1.0 / 255.0;

		double[] data = new double[width*height];
		for(int h=0;h<height;h++) {
			for(int w=0;w<width;w++)  {

				int offset = h*width*nbands+w*nbands;	
				data[idx++] = pixels[offset] * amplitudeScale;
			}
		}

		return data;
	}

	public Interpolation getInterpolation() {

		return new InterpolationNearest();
	}

	public String getFilename( int name_idx ) { 

		return (String)paramBlock.getSource( name_idx );
	}

	@SuppressWarnings("unchecked")
	public void initTypeModel( DataGeneratorTypeModel model ) {

		super.initTypeModel(model); 

		Class[] source_classes = { String.class }; 
		model.setSourceClasses( source_classes );

		String[] source_names = { "Filename" }; 
		model.setSourceNames( source_names );

		model.setDocPath("/functiondoc/image.html");
	}

}
