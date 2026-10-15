/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.Constants;
import signals.core.DataGeneratorTypeModel;
import signals.operation.ArrayUtilities;
import signals.operation.Interpolator;

/**
 * @author Juliet
 * Stores data that is not associated with an analytic function. Analogous to a 1D image
 */
public class DataFunctionTerm1D extends AnalyticFunctionTerm1D {
	
	boolean hasDescriptor; 
	String descriptor; 

	public DataFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	int mode; 

	public DataFunctionTerm1D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	@Override
	public DataFunctionTerm1D getInstance(ParameterBlock paramBlock) {
		DataFunctionTerm1D result = new DataFunctionTerm1D(paramBlock);
		// Definition copies supply independent samples, but these settings live
		// outside the parameter block and must also survive duplication.
		result.mode = mode;
		result.hasDescriptor = hasDescriptor;
		result.descriptor = descriptor;
		return result;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
		hasDescriptor = true; 
	}



	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	
	
	@Override
	public String getEquation(String[] variables) {
		if( hasDescriptor ) return descriptor; 
		else return super.getEquation(variables);
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#addTo(double[], double[])
	 */
	@Override
	public double[] create(double[] indices) {
		return create(indices, indices.length, indices.length > 0 && indices[0] != 0);
	}

	@Override
	public double[] create(double[] indices, int referenceDimension, boolean referenceZeroCentered) {
		double[] output = new double[indices.length];
		if (!hasData()) return output;
		double[] data = getOriginalData();
		if (data.length == 0) return output;

		int center = (int)getCenter();
		int halfWidth = (int)getWidth();
		double start, end;
		if (mode == Constants.CROP_MODE) {
			// Crop the same domain shown in the 1D editor, regardless of how many
			// pixels request samples from it (or the order of those coordinates).
			start = referenceZeroCentered ? -referenceDimension / 2 : 0;
			end = start + referenceDimension - 1;
		} else if (mode == Constants.SCALE_AND_PAD_MODE) {
			start = center - halfWidth;
			end = start + 2 * halfWidth - 1;
		} else {
			return create(indices.length, referenceZeroCentered);
		}
		if (end < start) return output;

		for (int i = 0; i < indices.length; i++) {
			double coordinate = indices[i];
			if (coordinate < start || coordinate > end) continue;
			if (mode == Constants.CROP_MODE
					&& (coordinate < center - halfWidth || coordinate > center + halfWidth)) continue;
			double position = end == start ? 0 : (coordinate - start) * (data.length - 1) / (end - start);
			int left = Math.min((int)Math.floor(position), data.length - 1);
			int right = Math.min(left + 1, data.length - 1);
			double fraction = position - left;
			output[i] = getAmplitude() * (data[left] + fraction * (data[right] - data[left]));
		}
		return output;
	}

	public double[] getResizedData(int dimension, boolean zeroCentered) {

		double[] data = getOriginalData(); 
		double[] interpolated = null; 
		if( dimension == data.length ) interpolated = ArrayUtilities.clone(data);
		else interpolated = Interpolator.linearInterpolate( data, dimension );
		return interpolated;

	}

	public double[] getScaledResizedData( int dimension, boolean zeroCentered ) {

		double[] interpolated = getResizedData(dimension, zeroCentered); 
		double amplitude = getAmplitude(); 

		if( amplitude != 1 ) {

			for( int i = 0; i < interpolated.length; ++i ) {

				interpolated[i] *= amplitude;
			}

		}

		return interpolated; 
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm#addTo(int, double[])
	 */
	@Override
	public double[] create(int dimension, boolean zeroCentered) {
		
		if( !hasData() ) return new double[dimension];

		int center = (int)getCenter(); 
		int width = 2*(int)getWidth();

		if( !zeroCentered ) {

			center -= dimension/2; 
		}

		if( dimension == width && center == 0 ) {

			return getScaledResizedData(dimension, zeroCentered);
		}

		double[] output = null; 

		switch( mode ) {

		case Constants.CROP_MODE: 
			output = createCropped(dimension, zeroCentered); 
			break; 
		case Constants.SCALE_AND_PAD_MODE: 
			output = createScaledPadded(dimension, zeroCentered);
			break; 
		case Constants.SCALE_AND_WRAP_MODE:
			break; 
		case Constants.SCALE_AND_MIRROR_MODE: 
			break; 
		}

		return output;
	}

	public int boundValue( int value, int min, int max ) {

		return Math.max( min, Math.min( value, max ) );
	}

	private double[] createScaledPadded(int dimension, boolean zeroCentered) {
		int center = (int)getCenter() + (zeroCentered ? dimension / 2 : 0);
		int width = 2 * (int)getWidth();
		int dataLeft = center - width / 2;
		int left = Math.max(0, dataLeft);
		int right = Math.min(dimension, dataLeft + width);
		double[] output = new double[dimension];
		if (right <= left) return output;

		// Preserve the authored scale when only part of the signal is visible.
		// Clipping the bounds before resizing would squeeze all samples into view.
		double[] resized = getScaledResizedData(width, zeroCentered);
		System.arraycopy(resized, left - dataLeft, output, left, right - left);
		return output;
	}

	private double[] createCropped(int dimension, boolean zeroCentered) {

		int center = (int)getCenter();  
		int width = 2*(int)getWidth(); 

		if( zeroCentered ) {

			center += dimension/2; ; 
		}

		int left = boundValue( center - width/2, 0, dimension-1 ); 
		int right = boundValue( center + width/2, 0, dimension-1 );

		
		double[] output = getScaledResizedData(dimension, zeroCentered);
		
		for( int i = 0; i < left; ++i ) {
			
			output[i] = 0; 
		}
		
		for( int i = right+1; i < dimension; ++i ) {
			
			output[i] = 0;
		}

		return output; 
	}

	@SuppressWarnings("unchecked")
	public void initTypeModel( DataGeneratorTypeModel model ) {

		super.initTypeModel(model);

		Class[] sourceClasses = { double[].class };
		model.setSourceClasses( sourceClasses );

		String[] sourceNames = { "original data" }; 
		model.setSourceNames( sourceNames ); 

//		model.setLargeIcon("/functionIcons/Data1DLarge.png");
//		model.setSmallIcon("/functionIcons/Data1DSmall.png");
		model.setName("Data");
		
		model.setDocPath("/functiondoc/data1D.html");
	}

	public double[] getOriginalData() {

		return (double[])paramBlock.getSource(0);
	}
	
	public boolean hasData() {
		
		return paramBlock.getNumSources() > 0;
	}

	/* (non-Javadoc)
	 * @see signals.function.AnalyticFunctionTerm1D#isHalfWidthDefined()
	 */
	@Override
	public boolean isHalfWidthDefined() {
		return true;
	}
	
}
