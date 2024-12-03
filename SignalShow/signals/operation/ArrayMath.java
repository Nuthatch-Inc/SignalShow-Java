package signals.operation;


import java.util.Arrays;

public class ArrayMath {

	/**
	 * Logarithm
	 * calculates natural logarithm of the magnitude of the array values
	 */
	public static double[][] logarithm( double[] real, double[] imaginary ) {

		int dimension = real.length; 

		double[] logR = new double[dimension]; 
		double[] logI = new double[dimension]; 

		for ( int i = 0; i < dimension; i++ )  {

			double magnitude = Math.sqrt( real[i] * real[i] + imaginary[i] * imaginary[i] );
			logI[i] =  0;
			logR[i] = ( magnitude == 0 ) ? -100 : Math.log( magnitude ); 

		}

		double[][] output = { logR, logI }; 
		return output;	

	} //logarithm

	/** 
	 * Invert
	 */
	public static double[][] invert( double[] real, double[] imaginary ) {

		int dimension = real.length;

		double[] outR = new double[dimension];
		double[] outI = new double[dimension];

		// question add overflow checks
		for( int i = 0; i < dimension; i++ ) {

			double denom = magnitude( real[i], imaginary[i] );

			denom = ( denom > 1E70 ) ? 0 : ( denom * denom );

			if( denom != 0 ) {

				outR[i] = real[i] / denom;
				outI[i] = -imaginary[i] / denom;
			}
		}

		double[][] output = { outR, outI }; 
		return output;	

	} 

	public static double min( double[] array ) {

		double min = Double.MAX_VALUE; 

		for( int i = 0; i < array.length; i++ ) {

			if( array[i] < min ) min = array[i]; 
		}

		return min; 
	}

	public static double max( double[] array ) {

		double max = Double.MIN_VALUE; 

		for( int i = 0; i < array.length; i++ ) {

			if( array[i] > max ) max = array[i]; 
		}

		return max; 
	}
	
	public static double[] sample( double[] input, boolean zeroCentered, int width ) {
		
		int dimension = input.length; 
		int firstIndex = zeroCentered ? -dimension/2 : 0;
		double[] comb = new double[ dimension ];		
		
		for ( int i = 0; i < dimension; i++ )  { //i in array coordinates
			double x = i + firstIndex;
			comb[i] = ( x/width == (int)( x/width ) ) ? input[i] : 0;
		}	
		
		return comb;
	
	}

	public static double[] negate( double[] array ) {

		double[] negated = new double[array.length]; 

		for( int i = 0; i < array.length; i++ ) {

			negated[i] = -array[i];
		}

		return negated; 
	}

	//complex multiplication of 2 arrays. will only multiply to first input.length
	public static double[][] multiply( double[] real1, double[] imag1, double[] real2, double[] imag2 ) {

		int dimension = real1.length; 
		double[] productR = new double[dimension];
		double[] productI = new double[dimension]; 

		for( int i = 0; i < dimension; i++ ) {

			productR[i] = real1[i] * real2[i] - imag1[i] * imag2[i]; 
			productI[i] = real1[i] * imag2[i] + real2[i] * imag1[i];
		}

		double[][] output = { productR, productI }; 
		return output;

	}

	public static double[] modulate( double[] array1, double[] array2 ) {

		int dimension = array1.length; 
		double[] product = new double[dimension];

		for( int i = 0; i < dimension; i++ ) {

			product[i] = array1[i] * array2[i];

		}

		return product;

	}

	public static double[] subtract( double[] array1, double[] array2 ) {

		int dimension = array1.length; 
		double[] difference = new double[dimension];

		for( int i = 0; i < dimension; i++ ) {

			difference[i] = array1[i] - array2[i];

		}

		return difference;

	}
	
	public static double[] add( double[] array1, double[] array2 ) {

		int dimension = array1.length; 
		double[] sum = new double[dimension];

		for( int i = 0; i < dimension; i++ ) {

			sum[i] = array1[i] + array2[i];

		}

		return sum;

	}

	public static double[] subtract( double[] array1, double value ) {

		int dimension = array1.length; 
		double[] difference = new double[dimension];

		for( int i = 0; i < dimension; i++ ) {

			difference[i] = array1[i] - value;

		}

		return difference;

	}

	public static double[] scale( double[] array1, double value ) {

		int dimension = array1.length; 
		double[] product = new double[dimension];

		for( int i = 0; i < dimension; i++ ) {

			product[i] = array1[i] * value;

		}

		return product;

	}

	public static double[][] divide( double[] real1, double[] imag1, double[] real2, double[] imag2 ) { 

		int dimension = real1.length;

		double[] realOut = new double[dimension];
		double[] imagOut = new double[dimension];

		for( int i = 0; i < dimension; i++ ) {

			double denom = real2[i] * real2[i] + imag2[i] * imag2[i];

			if ( denom != 0 ) {

				realOut[i] = ( real1[i] * real2[i] + imag1[i] * imag2[i] ) /denom;
				imagOut[i] = ( imag1[i] * real2[i] - real1[i] * imag2[i] ) /denom;

			} else if ( ( i >= 1 ) && ( i < dimension - 1 ) ){ //i% - 1 >= 0 AND i% + 1 <= N% - 1 

				//~ dreal1 = array1r(i% + 1) - array1r(i% - 1)
				double dreal1 = real1[i + 1] - real1[i-1]; 
				//~ dimag1 = array1i(i% + 1) - array1i(i% - 1)
				double dimag1 = imag1[i + 1] - imag1[i - 1];
				//~ dreal2 = array2r(i% + 1) - array2r(i% - 1)
				double dreal2 = real2[i + 1] - real2[i - 1];
				//~ dimag2 = array2i(i% + 1) - array2i(i% - 1)
				double dimag2 = imag2[i + 1] - imag2[i - 1];
				//~ numer1# = dreal1 * dreal2 + dimag1 * dimag2
				double numer1 = dreal1 * dreal2 + dimag1 * dimag2; 
				//~ numer2# = dimag1 * dreal2 - dreal1 * dimag2
				double numer2 = dimag1 * dreal2 - dreal2 * dimag2; 
				//~ denom# = dreal2 * dreal2 + dimag2 * dimag2
				denom = dreal2 * dreal2 + dimag2 * dimag2;
				//~ IF denom# <> 0 THEN
				if( denom != 0 ) {
					//~ Real = xsng!(numer1# / denom#)
					realOut[i] = numer1 / denom; 
					//~ Imag = xsng!(numer2# / denom#)
					imagOut[i] = numer2 / denom;
				}

			}

		}//end loop

		double[][] output = { realOut, imagOut };
		return output;

	} //divide

	/*
	 * Magnitude
	 */ 
	public static double magnitude( double inreal, double inimag ) {

		double magnitude = inreal * inreal + inimag * inimag; 
		//question: precision
		return(  ( magnitude > 3E76 ) ? 1.7E38 : Math.sqrt( magnitude ) ); 

	}

	public static double phase( double real, double imag ) {

		double threshold = 1E-10; 

		double dreal = 0; 
		double dimag = 0; 
		double dphase = 0;

		if(  Math.abs( real ) >= threshold ) dreal = real;

		if( Math.abs( imag ) >= threshold ) dimag = imag; 

		if (dreal != 0)  dphase = Math.atan( dimag / dreal );

		if ( ( dreal < 0 ) && ( dimag == 0 ) ) dphase = -Math.PI;

		if ( ( dreal < 0 ) && ( dimag < 0 ) )  dphase = dphase - Math.PI;

		if ( ( dreal < 0 ) && ( dimag > 0 ) ) dphase = dphase + Math.PI; 

		if ( ( dreal == 0 ) && ( dimag > 0 ) ) dphase = Math.PI / 2.0;

		if ( ( dreal >= 0 )  && ( dimag == 0 ) ) dphase = 0;

		if ( ( dreal == 0 ) && ( dimag < 0 ) ) dphase = -Math.PI / 2.0; 

		return dphase; 
	}

	public static double[] phase( double real[], double[] imag ) {

		int dimension = real.length; 
		double threshold = 1E-10; 
		double phase[] = new double[ dimension ]; 

		for( int i = 0; i < dimension; ++i ) { 

			double dreal = 0; 
			double dimag = 0; 
			double dphase = 0;

			if(  Math.abs( real[i] ) >= threshold ) dreal = real[i];

			if( Math.abs( imag[i] ) >= threshold ) dimag = imag[i]; 

			if (dreal != 0)  dphase = Math.atan( dimag / dreal );

			if ( ( dreal < 0 ) && ( dimag == 0 ) ) dphase = -Math.PI;

			if ( ( dreal < 0 ) && ( dimag < 0 ) )  dphase = dphase - Math.PI;

			if ( ( dreal < 0 ) && ( dimag > 0 ) ) dphase = dphase + Math.PI; 

			if ( ( dreal == 0 ) && ( dimag > 0 ) ) dphase = Math.PI / 2.0;

			if ( ( dreal >= 0 )  && ( dimag == 0 ) ) dphase = 0;

			if ( ( dreal == 0 ) && ( dimag < 0 ) ) dphase = -Math.PI / 2.0; 

			phase[i] = dphase; 


		}

		return phase; 
	}

	public static double[] magnitude( double[] real, double[] imaginary ) {

		int dimension = real.length; 
		double magnitude[] = new double[ dimension ]; 

		for( int i = 0; i < dimension; i++ ) {
			double magSq = imaginary[i] * imaginary[i] + real[i] * real[i]; 
			magnitude[i] = ( magSq > 3E76 ) ? 1.7E38 : Math.sqrt( magSq ); 
		}

		return magnitude; 

	}

	public static double[] squaredMagnitude( double[] real, double[] imaginary ) {

		int dimension = real.length; 
		double magnitude[] = new double[ dimension ]; 

		for( int i = 0; i < dimension; i++ ) {
			magnitude[i] = imaginary[i] * imaginary[i] + real[i] * real[i];
		}

		return magnitude; 

	}

	public static double[] reverse( double[] array ) {

		int dimension = array.length; 
		double[] reversed = new double[dimension]; 

		reversed[0] = array[0];

		for( int i = 1; i < dimension; i++ ) {

			reversed[i] = array[dimension-i];
		}

		return reversed;

	}

	public static double[] reverse2D( double[] input, int x_dimension, int y_dimension, boolean x ) {

		double[][] expanded = ArrayUtilities.expand(input, x_dimension, y_dimension);
		double[][] output = new double[y_dimension][x_dimension]; 

		if( x ) {

			for( int j = 0; j < y_dimension; ++ j ) { //for each row

				for( int i = 1; i < x_dimension; ++i ) { //reversal in x-direction

					output[j][i] = expanded[j][x_dimension-i];  
				}
			}

		} else {

			for( int j = 1; j < y_dimension; ++ j ) {//for each column

				for( int i = 0; i < x_dimension; ++ i ) {  //reversal in y-direction

					output[j][i] = expanded[y_dimension-j][i]; 
				}
			}

		}

		return ArrayUtilities.flatten( output );
	}

	/*
	 * CmplxPower
	 */
	public static double[][] complexPower( double[] real, double[] imag, double realpower, double imagpower ) {

		int dimension = real.length;

		if( ( realpower == 1 ) && ( imagpower == 0 ) ) {

			double[][] output = { real, imag }; 
			return output;	

		} 

		if( ( realpower == -1 ) && ( imagpower == 0 ) ) {

			return ( invert( real, imag ) ); 
		}

		double[][] log = logarithm( real, imag );

		double[] pwrR = new double[dimension]; 
		double[] pwrI = new double[dimension]; 

		for ( int i = 0; i < dimension; i++ )  {

			pwrR[i] = realpower * log[0][i] - imagpower * log[1][i];
			pwrI[i] = imagpower * log[0][i] + realpower * log[1][i];

		}

		double[][] exp = exponent( pwrR, pwrI );

		for ( int i = 0; i < dimension; i++ )  {

			if( real[i] == 0 && imag[i] == 0 ) {

				exp[0][i] = 0;
				exp[1][i] = 0;
			}
		}

		return exp;	

	}

	/**
	 * Exponent
	 * Calculates complex exponential
	 */ 
	public static double[][] exponent( double[] real, double[] imaginary ) {

		int dimension = real.length; 

		double[] exponentR = new double[dimension]; 
		double[] exponentI = new double[dimension]; 

		for ( int i = 0; i < dimension; i++ )  {

			double re = real[i]; 
			double im = imaginary[i]; 

			if( re > 709.78 ) { 

				re = 709.78; 

			} else if( (re < -700) || ((im < -700) && (re == 0)) ) {

				exponentR[i] = 0; 
				exponentI[i] = 0;

			} else{ 

				//corrects for limitation of COS(A) for |A| > 2x10^(9)
				if ( Math.abs( im ) > 2e9 ) { 

					im = (im / Math.PI - (int)( im / Math.PI ) ) * Math.PI; 
				}

				exponentR[i] = Math.exp(re) * Math.cos(im);
				exponentI[i] = Math.exp(re) * Math.sin(im);

			}
		}		

		double[][] output = { exponentR, exponentI }; 
		return output;	

	} //exponent

	public static double[] log10( double[] array ) {

		double[] output = new double[array.length];

		for( int i = 0; i < array.length; ++i ) {

			output[i] = ( array[i] == 0 ) ? -100 : Math.log10(array[i]); 
		}

		return output;
	}

	public static double[] square( double[] array ) {

		double[] output = new double[array.length];

		for( int i = 0; i < array.length; ++i ) {

			output[i] = array[i]*array[i];
		}

		return output;

	}

	public static double[] logSquare( double[] array, boolean log, boolean square ) {

		double[] output = square ? square( array ) : array; 
		return ( log ? log10( output ) : output ); 
	}

	public static double[] subtractMean( double[] array ) {

		double mean = mean( array ); 

		int dimension = array.length; 

		double[] subtracted = new double[dimension]; 

		for( int i = 1; i < dimension; i++ ) {

			subtracted[i] = array[i] - mean; 
		} 

		return subtracted; 

	} //subtractMean

	/**
	 * Mean
	 * Calculates Mean Value of array
	 */ 
	public static double mean( double[] array ) {

		int dimension = array.length; 
		double total = array[0]; 

		for( int i = 1; i < dimension; i++ ) {

			total += array[i]; 
		} 

		return( (double)total/(double)dimension ); 

	}//mean

	public static double median( double[] array ) {

		int dimension = array.length; 
		int NOver2 = (int)( dimension / 2 );
		double[] clone = ArrayUtilities.clone( array ); 
		Arrays.sort( clone ); 
		return clone[NOver2]; 

	} //median


	public static double variance( double[] array ) {

		double mean = mean( array ); 
		double variance = 0; 
		int dimension = array.length; 

		for ( int i = 0; i < dimension; i++ )  {

			double difference = array[i] - mean; 
			variance += ( difference * difference ); 
		} 

		return ( variance / (double)( dimension - 1 ) ); 

	} //variance

	public static double stdev( double[] array ) {

		return Math.sqrt( variance( array )); 
	}

	public static double[] abs( double[] array ) {

		int dimension = array.length; 
		double[] absVal = new double[dimension]; 
		for( int i = 0; i < dimension; i++ ) {

			absVal[i] = Math.abs( array[i] );
		}

		return absVal;

	}//abs

	public static double[] sqrt( double[] array ) {

		int dimension = array.length; 
		double[] sqrt = new double[dimension]; 
		for( int i = 0; i < dimension; i++ ) {

			sqrt[i] = StrictMath.sqrt( array[i] );
		}

		return sqrt;

	}//abs

	/** 
	 * Threshold
	 */ 
	public static double[] threshold( double[] input, double threshold,
			double outputHigh, double outputLow ) {

		int dimension = input.length;
		double[] output = new double[dimension]; 

		for( int i = 0; i < dimension; i++ ) {

			output[i] = input[i] >= threshold ? outputLow : outputHigh; 

		}

		return output;	

	}

	public static double[] real( double[] magnitude, double[] phase ) {

		int dimension = magnitude.length; 
		double[] real = new double[dimension]; 

		for( int i = 0; i < dimension; ++i ) {

			real[i] = magnitude[i]*Math.cos( phase[i] ); 

		}

		return real; 
	}

	public static double[] imaginary( double[] magnitude, double[] phase ) {

		int dimension = magnitude.length; 
		double[] imag = new double[dimension]; 

		for( int i = 0; i < dimension; ++i ) {

			imag[i] = magnitude[i]*Math.sin( phase[i] ); 

		}

		return imag; 
	}

	/** 
	 * Threshold
	 */ 
	public static double[] clip( double[] input, double outputHigh, double outputLow ) {

		int dimension = input.length;
		double[] output = new double[dimension]; 

		for( int i = 0; i < dimension; i++ ) {

			if( input[i] > outputHigh ) {

				output[i] = outputHigh; 

			} else if (input[i] < outputLow) {

				output[i] = outputLow; 

			} else {

				output[i] = input[i];
			}

		}

		return output;	

	}

}
