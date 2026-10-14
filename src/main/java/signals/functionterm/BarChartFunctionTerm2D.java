package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.PlanarImage;

import signals.core.DataGeneratorTypeModel;

public class BarChartFunctionTerm2D extends PredefinedImageFunctionTerm2D {

	public BarChartFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static int idx_128 = 0, idx_256 = 1, idx_512 = 2, idx_1024 = 3; 
	
	public BarChartFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}
	
	private int selectedImageIndex;

	@Override
	public PlanarImage getResizedImage(int x_dimension, int y_dimension) {
		int dimension = Math.max(128, Math.min(1024, Math.min(x_dimension, y_dimension)));
		int fileIndex = switch (dimension) {
			case 256 -> idx_256;
			case 512 -> idx_512;
			case 1024 -> idx_1024;
			default -> idx_128;
		};
		if (fileIndex != selectedImageIndex) {
			selectedImageIndex = fileIndex;
			image = null;
		}
		return super.getResizedImage(x_dimension, y_dimension);
	}

	@Override
	public PlanarImage loadImage() {
		return loadImage(getFilename(selectedImageIndex));
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
