package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.lang.ref.SoftReference;

import javax.media.jai.PlanarImage;

import signals.core.DataGeneratorTypeModel;
import signals.gui.plot.ImageDisplayMath;
import signals.io.ResourceLoader;

public class BarChartFunctionTerm2D extends PredefinedImageFunctionTerm2D {

	public BarChartFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static int idx_128 = 0, idx_256 = 1, idx_512 = 2, idx_1024 = 3; 
	
	public BarChartFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}
	
	@SuppressWarnings("unchecked")
	public PlanarImage getResizedImage(int x_dimension, int y_dimension, boolean zeroCentered ) {  
		
		int dim = Math.min( x_dimension, y_dimension ); 
		dim = Math.max( 128 , dim ); 
		dim = Math.min( dim, 1024 );
		
		int file_idx = 0; 
		
		switch( dim ) {
		
		case 128: file_idx = idx_128; 
			break; 
		case 256: file_idx = idx_256;
			break; 
		case 512: file_idx = idx_512; 
			break; 
		case 1024: file_idx = idx_1024;
			break;
		}
		
		image = new SoftReference( ResourceLoader.loadImage(getFilename( file_idx )) );

		//scale the image with a bilinear interpolator 
		return (PlanarImage) ImageDisplayMath.getResizedImage( getImage(), 
				x_dimension, y_dimension, getInterpolation() );
	}

	/* (non-Javadoc)
	 * @see signals.core.ImageFunctionTerm2D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		super.initTypeModel(model);
		
		Object[] source_defaults = { "/images/barchart128.png", "/images/barchart256.png", 
									"/images/barchart512.png", "/images/barchart1024.png" };
		model.setSourceDefaults( source_defaults );
		
//		model.setLargeIcon("/functionIcons/BarChart2DLarge.png");
//		model.setSmallIcon("/functionIcons/BarChart2DSmall.png");
		model.setName("Bar Chart");
	
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Bar_Chart"; 
	}
	
}
