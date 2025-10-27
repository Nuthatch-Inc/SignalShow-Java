package signals.operation;

import java.util.HashMap;

/**
 * Creates and stores FFT twiddle factors
 * @author Juliet
 *
 */
public class FFTCoefficients {
	
	protected HashMap<Integer, double[]> realCoefficients; 
	protected HashMap<Integer, double[]> imagCoefficients; 
	protected HashMap<Integer, int[]> bitReverseIndices; 

	// Protected constructor is sufficient to suppress unauthorized calls to the constructor
	protected FFTCoefficients() {
		
		realCoefficients = new HashMap<Integer, double[]>(); 
		imagCoefficients = new HashMap<Integer, double[]>();
		bitReverseIndices = new HashMap<Integer, int[]>();
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	 * or the first access to SingletonHolder.instance , not before.
	 */
	private static class SingletonHolder { 
		private final static FFTCoefficients INSTANCE = new FFTCoefficients();
	}

	public static FFTCoefficients getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public static double[] getReal( int dimension, boolean inverse ) {
		
		return getInstance().getRealCoefficients(dimension, inverse);
	}
	
	public static double[] getImaginary( int dimension, boolean inverse ) {
		
		return getInstance().getImaginaryCoefficients(dimension, inverse);
	}
	
	public static int[] getBitReverseIndices( int dimension ) {
		
		return getInstance().calculateBitReverseIndices(dimension);
	}
	
	public int[] calculateBitReverseIndices( int key ) {
		 
		if( !bitReverseIndices.containsKey(key) ) {
			
			createBitReverseIndices( key );
		}
		
		return bitReverseIndices.get( key ); 
	}
	
	public double[] getRealCoefficients( int dimension, boolean inverse ) {
		
		int key = inverse ? -dimension : dimension; 
		if( !realCoefficients.containsKey(key) ) {
			
			createCoefficients( dimension, inverse );
		}
		
		return realCoefficients.get( key );
	}
	
	public double[] getImaginaryCoefficients( int dimension, boolean inverse ) {
		
		int key = inverse ? -dimension : dimension; 
		if( !imagCoefficients.containsKey(key) ) {
			
			createCoefficients( dimension, inverse );
		}
		
		return imagCoefficients.get( key );
	}
	
	public void createBitReverseIndices( int dimension ) {

		int lgDimension = (int)( Math.log( dimension ) / Math.log( 2 ) ); 
		
		int[] indices = new int[dimension]; 

		for( int i = 0; i < dimension; i++ ) {

			int reversedIndex = reverseBits( i , lgDimension );
			indices[i] = reversedIndex; 
		} 

		bitReverseIndices.put(dimension, indices); 

	} 
	
	/*
	 * reverseBits: modified from http://svn.digium.com/view/djkrazy/fft.c?rev=2&view=markup
	 */ 
	public static int reverseBits( int initial, int numBits) {

		int reversed = 0; 

		for( int i = 0; i < numBits; i++ ) {
			reversed <<= 1;
			reversed += ( initial & 1 );
			initial >>= 1;
		}

		return reversed;

	} //reverseBits
	
	public void createCoefficients( int dimension, boolean inverse ) {
		
		int multiplier = inverse ? 2 : -2; 
		double twoPi = multiplier * Math.PI;
		int lgDimension = (int)( Math.log( dimension ) / Math.log( 2 ) ); 
		
		int arraySize = dimension * lgDimension / 2;
		
		double[] realArray = new double[arraySize];
		double[] imagArray = new double[arraySize];
		int idx = 0; 
		
		for( int s = 1; s <= lgDimension; s++ ) { //s is the power of 2 currently being worked on

			int m = 1 << s; //m is a power of 2 from 2 to n
			double twoPiOverM = twoPi / m; 
			int mOver2 = m >> 1; 

			for( int j = 0; j < mOver2; j++ ) {

				for( int k = j; k < dimension; k += m ) { //one butterfly

					double phase = twoPiOverM * k; 
					realArray[idx] = Math.cos( phase ); 
					imagArray[idx++] = Math.sin( phase );

				} //end k loop 

			} //end j loop

		} //end s loop
		
		realCoefficients.put( inverse ? -dimension : dimension, realArray );
		imagCoefficients.put( inverse ? -dimension : dimension, imagArray );
	}
	
}

