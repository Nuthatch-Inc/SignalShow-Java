package signals.operation;

public class PhaseUnwrapper1D {
	
	/**
	 * Itoh's method for 1-D phase unwrapping, as described on pg. 21 of 
	 * "Two-Dimensional Phase Unwrapping: Theory, Algorithms, and Software" 
	 * by Dennis C. Ghiglia and Mark D. Pritt 
	 * @param phase the wrapped phase
	 * @return the unwrapped phase
	 */
	public static double[] unwrapItoh( double[] phase, int startingIndex ) {
		
		int dimension = phase.length;
		double[] wrapped_difference = new double[dimension]; 
		double[] unwrapped = new double[dimension];
		
		//1. Compute phase differences 
		//2. Compute wrapped phase differences
		for( int i=0; i < dimension-1; i++ ) {
			
			wrapped_difference[i] = phase[i+1]-phase[i];
			if ( wrapped_difference[i] > Math.PI ) wrapped_difference[i] -= 2*Math.PI; //wrap 
			if( wrapped_difference[i] < -Math.PI ) wrapped_difference[i] += 2*Math.PI; 
		}
		
		//3.Initialize first unwrapped value 
		unwrapped[startingIndex] = phase[startingIndex];
		
		//4. Unwrap by summing the wrapped phase differences
		for( int i=startingIndex+1; i < dimension; i++ ) {
			
			unwrapped[i] = unwrapped[i-1] + wrapped_difference[i-1];
		}
		
		for( int i=startingIndex-1; i >= 0; i-- ) {
			
			unwrapped[i] = unwrapped[i+1] - wrapped_difference[i+1];
		}
		
		return unwrapped; 
	}

	/**
	 * A phase unwrapping algorithm developed by Roger Easton
	 * @param phase
	 * @return
	 */
	public static double[] unwrap1D( double[] phase ) {
		
		double[] ph = ArrayUtilities.clone(phase);
		int dimension = ph.length;
		double initPhase = ph[ dimension / 2 ]; 
		double phase1 = ph[0]; 
		int j = 0; 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			double c = ph[i] - phase1 + 2 * j * Math.PI; 
			
			if( c > Math.PI + .000001 ) {
				
				j --;
				
			} else if( c < -Math.PI - .000001 ) {
				
				j++;
				
			}
			
			ph[i] += j * 2 * Math.PI; 
			phase1 = ph[i]; 
		}
		
		initPhase = initPhase - ph[ dimension / 2 ]; 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			ph[i] += initPhase; 
		}
		
		return ph;
		
	}
	
}
