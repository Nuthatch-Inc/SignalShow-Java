/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.PlanarImage;

import signals.core.DataGeneratorTypeModel;
import signals.gui.plot.ImageDisplayMath;

/**
 * @author Juliet
 * Stores data that is not associated with an analytic function. Analogous to a 1D image
 */
public class DataFunctionTerm2D extends ImageFunctionTerm2D {
	
	public DataFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	int data_idx, xdim_idx, ydim_idx; 
	
	public DataFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
		data_idx = 0; 
		xdim_idx = 1; 
		ydim_idx = 2;
	}

	/* (non-Javadoc)
	 * @see signals.function.ImageFunctionTerm2D#create(int, int, boolean)
	 */
	@Override
	public double[] create(int x_dimension, int y_dimension,
			boolean zeroCentered) {
		if (!hasData()) return new double[x_dimension*y_dimension];
		
		if( x_dimension == getXDimension() && y_dimension == getYDimension() ) {
			
			return getOriginalData(); 
		}
		
		//resize image
		return super.create(x_dimension, y_dimension, zeroCentered);
	}

	@SuppressWarnings("unchecked")
	public void initTypeModel( DataGeneratorTypeModel model ) {
		
		super.initTypeModel(model);
		
		Class[] sourceClasses = { double[].class, Integer.class, Integer.class };
		model.setSourceClasses( sourceClasses );
		
		String[] sourceNames = { "original data", "original width", "original height" }; 
		model.setSourceNames( sourceNames ); 
		
//		model.setLargeIcon("/functionIcons/Data1DLarge.png");
//		model.setSmallIcon("/functionIcons/Data1DSmall.png");
		model.setName("Data");
		
		model.setDocPath("/functiondoc/data2D.html");
	}

	@Override
	public Interpolation getInterpolation() {
		return new InterpolationBilinear();
	}
	
	@Override
	public PlanarImage loadImage() {
		
		return (PlanarImage) ImageDisplayMath.data2Image( getOriginalData(), getXDimension(), getYDimension() ); 
	
	}
	
	public double[] getOriginalData() {
		
		return (double[])paramBlock.getSource(data_idx);
	}
	
	public int getXDimension() {
		
		return (Integer)paramBlock.getSource(xdim_idx);
	}
	
	public int getYDimension() {
		
		return (Integer)paramBlock.getSource(ydim_idx);
	}

	public boolean hasData() {
		return paramBlock.getNumSources() > 0;
	}
	
}
