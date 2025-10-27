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
	public double[] create(double[] indices ) {

		int dimension = indices.length; 
		boolean zeroCentered = indices[0] != 0;
		return create( dimension, zeroCentered );
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
		
		int center = (int)getCenter();  
		int width = 2*(int)getWidth(); 

		if( zeroCentered ) {

			center += dimension/2; ; 
		}

		int left = boundValue( center - width/2, 0, dimension-1 ); 
		int right = boundValue( center + width/2, 0, dimension-1 );
		int w = right - left; 
		
		double[] resized = getScaledResizedData(w, zeroCentered);
		
		double[] output = new double[dimension]; 
		
		int idx = 0; 
		for( int i = left; i < right; ++i ) {
			
			output[i] = resized[idx++];
		}

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
