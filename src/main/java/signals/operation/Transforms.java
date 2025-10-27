package signals.operation;

import signals.core.Function;
import signals.core.Function2D;
import signals.core.FunctionFactory;

public class Transforms {
	
	public static final int NORMALIZE_ROOT_N = 0; 
	public static final int NORMALIZE_N = 1; 
	public static final int NORMALIZE_NONE = 2; 

	public static Function fft1D( Function input, boolean inverse, int normalization ) {

		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		boolean zeroCentered = input.isZeroCentered(); 

		double[][] output = computeFFT1D( real, imag, zeroCentered, inverse, normalization );
		String name = "FFT{ " + input.getCompactDescriptor() + " }"; 
		return FunctionFactory.createFunction1D( output[0], output[1], zeroCentered, name ); 

	}

	public static Function fft2D( Function input, boolean inverse, int normalization ) {

		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		boolean zeroCentered = input.isZeroCentered(); 
		int x_dimension = ((Function2D)input).getDimensionX();
		int y_dimension = ((Function2D)input).getDimensionY();

		double[][] output = computeFFT2D( real, imag, zeroCentered, inverse, x_dimension, y_dimension, normalization );
		String name = "FFT{ " + input.getCompactDescriptor() + " }"; 
		return FunctionFactory.createFunction2D( output[0], output[1], zeroCentered, name, x_dimension, y_dimension ); 
	}
	
	//DCT assumes range of indices is [0, N-1]
	public static double[][] computeDCT1D( double[] real, double[] imaginary ) {
		
		//create complex variable flipped, whose first half is real and imaginary, and whose second half
		// is the flipped version of real and imaginary
		
		int dimension = real.length; 
		int twoN = 2 * dimension; 
		
		double[] flippedR = new double[twoN]; 
		double[] flippedI = new double[twoN]; 
		
		double piOver2N = Math.PI / (double)( twoN ); 
		
		double[] multiplierR = new double[dimension]; 
		double[] multiplierI = new double[dimension];

		//create "flipped" arrays. These are even-dimensioned (size 2 N)
		for( int i = 0; i < dimension; i++ ) {
			
			//calculate "first half forward, second half flipped" arrays
			flippedR[twoN - 1 - i] = flippedR[i] = real[i]; 
			flippedI[twoN - 1- i] = flippedI[i] = imaginary[i]; 
			
			//calculate multiplier arrays
			multiplierR[i] = Math.cos( piOver2N * i ); 
			multiplierI[i] = -Math.sin( piOver2N * i ); 
		}
	
		//perform the forward FFT on flippedR and flippedI. Don't checkerboard (assume 0 start). Don't normalize.
		double[][] transformed = computeFFT1D(flippedR, flippedI, false, false, NORMALIZE_NONE); 
		
		//must call in this order!
		double[][] product = ArrayMath.multiply( multiplierR, multiplierI, transformed[0], transformed[1] );
		
		double[] productR = product[0];
		double[] productI = product[1];
		
		double[] outputR = new double[dimension]; 
		double[] outputI = new double[dimension]; 
		
		outputR[0] = productR[0]; 
		outputI[0] = productI[0];
	
		double sqrt2 = Math.sqrt( 2.0 ); 
		
		for( int i = 1; i < dimension; i++ ) {
			
			outputR[i] = sqrt2 * productR[i]; 
			outputI[i] = sqrt2 * productI[i];
		}
		
		double[][] output = { outputR, outputI }; 
		return output; 
		
	}//DCT
	
	
	public static double[][] computeInverseDCT1D( double[] real, double[] imaginary ) {
		
		int dimension = real.length; 
		int twoN = 2 * dimension; 
		
		double[] multiplierR = new double[twoN]; 
		double[] multiplierI = new double[twoN]; 
		
		double piOver2N = Math.PI / (double)( twoN ); 
		
		double[] inputR = new double[twoN]; 
		double[] inputI = new double[twoN]; 
		
		double a = 1.0 / Math.sqrt( 2.0 ); 
		
		for( int i = 0; i < dimension; i++ ) {
			
			//create multiplier
			multiplierR[i] = Math.cos( piOver2N * i ); 
			multiplierI[i] = Math.sin( piOver2N * i ); 
		}
		
		for( int i = dimension; i < twoN; i++ ) {
			
			//create multiplier
			multiplierR[i] = -Math.cos( piOver2N * i ); 
			multiplierI[i] = -Math.sin( piOver2N * i ); 
		}
		
		//special cases
		inputR[0] = real[0]; 
		inputI[0] = imaginary[0]; 
		
		for( int i = 1; i < dimension; i++ ) {
			
			//create input
			inputR[twoN - i] = inputR[i] = a * real[i]; 
			inputI[twoN - i] = inputI[i] = a * imaginary[i]; //0 at N
			
		}
		
		//multiply
		double[][] product = ArrayMath.multiply( multiplierR, multiplierI, inputR, inputI );
		
		//ifft
		double[][] transformed = computeFFT1D(product[0], product[1], false, true, NORMALIZE_NONE);
		
		//another normalization question
		double[] outputR = new double[dimension]; 
		double[] outputI = new double[dimension]; 
		
		for( int i = 0; i < dimension; i++ ) {
			
			outputR[i] = transformed[0][i] / twoN; 
			outputI[i] = transformed[1][i] / twoN; 
		}
		
		double[][] output = { outputR, outputI }; 
		return output; 
		
	}//IDCT
	
	/**
	* Cepstrum
	*/ 
	public static double[][] cepstrum1D( double real[], double imaginary[], boolean zeroCentered, boolean inverse ) {
		
		double[][] transformed = computeFFT1D( real, imaginary, zeroCentered, inverse, NORMALIZE_NONE); 
		
		double[][] output = inverse ? ArrayMath.exponent( transformed[0], transformed[1]) : 
					ArrayMath.logarithm( transformed[0], transformed[1]); 
		
		return computeFFT1D( output[0], output[1], zeroCentered, false, NORMALIZE_N );
		
	} 
	
	public static double[][] cepstrum2D( double real[], double imaginary[], boolean zeroCentered, boolean inverse, 
					int x_dimension, int y_dimension ) {
		
		double[][] transformed = computeFFT2D( real, imaginary, zeroCentered, inverse,
								x_dimension, y_dimension, NORMALIZE_NONE); 
		
		double[][] output = inverse ? ArrayMath.exponent( transformed[0], transformed[1]) : 
					ArrayMath.logarithm( transformed[0], transformed[1]); 
		
		return computeFFT2D( output[0], output[1], zeroCentered, false,
							x_dimension, y_dimension, NORMALIZE_N );
		
	} 

	public static double[][] computeFFT2D( double[] re, double[] im, boolean zeroCentered, boolean inverse,
			int x_dimension, int y_dimension, int normalization ) {

		double[][] real = ArrayUtilities.y_swap_expand( re, x_dimension, y_dimension );
		double[][] imaginary = ArrayUtilities.y_swap_expand( im, x_dimension, y_dimension );

		if( zeroCentered ) { 

			real = ArrayUtilities.checkerBoard( real );
			imaginary = ArrayUtilities.checkerBoard( imaginary );
		}
		
		int lg_x_dimension = (int)( Math.log( x_dimension ) / Math.log( 2 ) ); 
		int lg_y_dimension = (int)( Math.log( y_dimension ) / Math.log( 2 ) ); 

		//
		// FFT of each row
		//

		double[] realCoef = FFTCoefficients.getReal(x_dimension, inverse);
		double[] imagCoef = FFTCoefficients.getImaginary(x_dimension, inverse);

		double[][] temp; 
		
		for( int i = 0; i < y_dimension; ++i ) {

			temp = butterfly(real[i], imaginary[i], realCoef, 
					imagCoef, lg_x_dimension, x_dimension); 

			real[i] = temp[0]; 
			imaginary[i] = temp[1];
		}

		//transpose
		real = ArrayUtilities.transpose( real ); 
		imaginary = ArrayUtilities.transpose( imaginary ); 

		//
		// FFT of each column (rows of transposed array)
		//

		if( x_dimension != y_dimension ) {

			realCoef = FFTCoefficients.getReal(y_dimension, inverse);
			imagCoef = FFTCoefficients.getImaginary(y_dimension, inverse);
		}

		for( int i = 0; i < x_dimension; ++i ) {

			temp = butterfly(real[i], imaginary[i], realCoef, 
					imagCoef, lg_y_dimension, y_dimension); 

			real[i] = temp[0]; 
			imaginary[i] = temp[1];
		}

		//transpose
		real = ArrayUtilities.transpose( real ); 
		imaginary = ArrayUtilities.transpose( imaginary );
		
		if( zeroCentered ) { 

			real = ArrayUtilities.checkerBoard( real );
			imaginary = ArrayUtilities.checkerBoard( imaginary );
		}

		re = ArrayUtilities.y_swap_flatten( real );
		real = null; //set to null to conserve memory
		im = ArrayUtilities.y_swap_flatten( imaginary );
		imaginary = null; 
		
		if( normalization == NORMALIZE_N ) { 
			
			double normalizer = x_dimension*y_dimension;
			re = ArrayUtilities.normalize( re, normalizer ); 
			im = ArrayUtilities.normalize( im, normalizer ); 
			
		} else if ( normalization == NORMALIZE_ROOT_N ) {
		
			Double normalizer = Math.sqrt(x_dimension)*Math.sqrt(y_dimension);
			re = ArrayUtilities.normalize( re, normalizer ); 
			im = ArrayUtilities.normalize( im, normalizer );
			
		}

		double[][] output = { re, im }; 
		return output;
 
	}

	public static double[][] bitReverseCopy( double[][] array, int numBitsCol, int numBitsRow ) {

		int numRows = array.length; 
		int numCols = array[0].length; 
		double[][] copy = new double[numRows][numCols]; 

		int[] reversedColIndex = FFTCoefficients.getBitReverseIndices(numCols);
		int[] reversedRowIndex = FFTCoefficients.getBitReverseIndices(numRows);


		for( int row = 0; row < numRows; row++ ) {

			//int reversedRowIndex = reverseBits( row , numBitsRow );

			for( int col = 0; col < numCols; col++ ) {

				copy[reversedRowIndex[row]][reversedColIndex[col]] = array[row][col]; 
			}
		} 

		return copy;

	} 

	public static double[][] computeFFT1D( double[] real, double[] imaginary, 
			boolean zeroCentered, boolean inverse, int normalization ) {

		int dimension = real.length;

		double[] realCoef = FFTCoefficients.getReal(dimension, inverse);
		double[] imagCoef = FFTCoefficients.getImaginary(dimension, inverse);

		int lgDimension = (int)( Math.log( dimension ) / Math.log( 2 ) ); 

		//checkerboard if necessary
		if( zeroCentered ) { 

			real = swapHalves( real ); 
			imaginary = swapHalves( imaginary );
		}

		double[][] temp = butterfly( real, imaginary, realCoef, imagCoef, lgDimension, dimension );
		real = temp[0]; 
		imaginary = temp[1];

		//checkerboard if necessary
		if( zeroCentered ) { 

			real = swapHalves( real ); 
			imaginary = swapHalves( imaginary );
		}
		
		if( normalization == NORMALIZE_N ) { 
			
			real = ArrayUtilities.normalize( real, dimension ); 
			imaginary = ArrayUtilities.normalize( imaginary, dimension ); 
			
		} else if ( normalization == NORMALIZE_ROOT_N ) {
		
			real = ArrayUtilities.normalize( real, Math.sqrt( dimension ) ); 
			imaginary = ArrayUtilities.normalize( imaginary, Math.sqrt( dimension ) ); 
			
		}

		double[][] output = { real, imaginary }; 
		return output;

	}

	public static double[][] butterfly( double[] real, double[] imaginary, double[] realCoef, 
			double[] imagCoef, int lgDimension, int dimension ) {

		//set up the values so that they are ready in a handy order
		real  = bitReverseCopy( real, lgDimension ); 
		imaginary = bitReverseCopy( imaginary, lgDimension );

		int idx = 0; 
		int s, m, mOver2, j, k, kPlusMOver2;
		double oReal, oImaginary, eReal, eImaginary;

		for( s = 1; s <= lgDimension; s++ ) { //s is the power of 2 currently being worked on

			m = 1 << s; //m is a power of 2 from 2 to n
			mOver2 = m >> 1; 

			for( j = 0; j < mOver2; j++ ) {

				for( k = j; k < dimension; k += m ) { //one butterfly

					kPlusMOver2 = k + mOver2;

					oReal = ( realCoef[idx] * real[ kPlusMOver2 ] ) - 
					( imagCoef[idx] * imaginary[ kPlusMOver2 ] ); 
					oImaginary = ( realCoef[idx] * imaginary[ kPlusMOver2 ] ) + 
					( imagCoef[idx++] * real[ kPlusMOver2 ] );

					//get the second input
					eReal = real[ k ];
					eImaginary = imaginary[ k ];

					//set the sum
					real[k] = eReal + oReal; 
					imaginary[k] = eImaginary + oImaginary;

					//set the difference
					real[ kPlusMOver2 ] = eReal - oReal; 
					imaginary[ kPlusMOver2 ] = eImaginary - oImaginary; 

				} //end k loop 

			} //end j loop

		} //end s loop

		double[][] output = { real, imaginary }; 
		return output;
	}


	public static double[] swapHalves( double[] input ) {

		int half_dimension = input.length >> 1;
			double[] output = new double[input.length];

			System.arraycopy( input, 0, output, half_dimension, half_dimension ); 
			System.arraycopy(input, half_dimension, output, 0, half_dimension );

			return output;
	}

	public static double[] bitReverseCopy( double[] array, int numBits ) {

		int dimension = array.length; 
		double[] copy = new double[dimension]; 
		
		int[] bitReverseIndices = FFTCoefficients.getBitReverseIndices(dimension); 

		for( int i = 0; i < dimension; i++ ) {

			int reversedIndex = bitReverseIndices[i];
			copy[reversedIndex] = array[i]; 
		} 

		return copy;

	} 



}
