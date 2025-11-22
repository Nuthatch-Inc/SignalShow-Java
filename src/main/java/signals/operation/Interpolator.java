package signals.operation;

public class Interpolator {
	
	/**
	* "Zooms in" on the center of an array using linear interpolation
	*/
	public static double[] zoomCenter( double[] input, double zoomFactor ) {
		
		double NOver2 = input.length / 2.0; 
		double start = NOver2 * ( 1 - 1/zoomFactor );
		double end = NOver2 * ( 1 + 1/zoomFactor ) - 1;
		return linearInterpolate( input, start, end, input.length ); 
		
	}//zoomCenter
	//TODO: this method doesn't work.
	public static double[] linearInterpolate( double[] input, double[] indices ) {
		
		double offset;
		double[] output = new double[indices.length];
		int floor, ceil;

		for ( int i = 0; i < indices.length; ++i ) {
			
			floor = Math.max( 0 , (int) Math.floor( indices[i] ) );
			ceil = Math.min( indices.length-1,(int) Math.ceil( indices[i] ) );
			offset = indices[i] - floor;
			output[i] = input[floor] + ( input[ceil] - input[floor] ) * offset;
		}

		return output;
		
	}

	public static double[] linearInterpolate( double[] input, int samples ) {
		
		double current = 0;
		double delta = (double)( input.length - 1 ) / (double)( samples - 1 );
		double offset;
		double[] output = new double[samples];
		int floor, ceil;

		for ( int i = 0; i < samples; ++i, current += delta ) {
			
			floor = (int) Math.floor( current );
			ceil = Math.min( (int) Math.ceil( current ), input.length-1 );
			offset = current - floor;
			output[i] = input[floor] + ( input[ceil] - input[floor] ) * offset;
		}

		return output;
	}
	
	/**
	* Linear Interpolator written by Carl Kelso and modified by Juliet
	 * @param input    An array of input values as doubles
	 * @param start    The starting index for interpolated data (may be non-integral)
	 * @param end      The ending index for interpolated data (may be non-integral) 
	 * @param samples  The number of samples in the output
	 * @throws IndexOutOfBoundsException Thrown when start or end exceed the bounds of input
	 * @return		   An array with samples length containing interpolated output
	 */
	public static double[] linearInterpolate( double[] input, double start, double end, int samples ) {
		
		int dimension = input.length;
		
		if ( start < 0 || end < 0 || start > dimension - 1 || end > dimension - 1 ) {
			throw new IndexOutOfBoundsException();
		}

		start = start < 0 ? 0 : start;
		start = start > (dimension - 1) ? (dimension - 1) : start; 

		end = end < 0 ? 0 : end;
		end = end > (dimension - 1) ? (dimension - 1) : end; 


		double current = start;
		double delta = (double)( end - start ) / (double)( samples - 1 );
		double offset;
		double[] output = new double[samples];
		int floor, ceil;

		for ( int i = 0; i < samples; ++i, current += delta ) {
			floor = (int) Math.floor( current );
			ceil = (int) Math.ceil( current );
			offset = current - floor;
			output[i] = input[floor] + ( input[ceil] - input[floor] ) * offset;
		}

		return output;
	}
}
