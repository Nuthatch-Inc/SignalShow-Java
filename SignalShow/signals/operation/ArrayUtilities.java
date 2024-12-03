package signals.operation;

import java.util.ArrayList;

public class ArrayUtilities {

	public static double[] clone( double[] array ) {

		int dimension = array.length;
		double[] clone = new double[dimension]; 
		System.arraycopy( array, 0, clone, 0, dimension); 
		return clone;

	}

	public static double[][] y_swap_transpose( double[][] array ) {

		int y_dimension = array.length; 
		int x_dimension = array[0].length;

		double[][] transposed = new double[x_dimension][y_dimension];

		for( int j = 0; j < y_dimension; ++ j ) {

			for( int i = 0; i < x_dimension; ++ i ) {

				transposed[i][y_dimension-j-1] = array[j][i];
			}
		}

		return transposed;
	}

	public static double[][] y_swap( double[][] array ) {

		int y_dimension = array.length; 
		int x_dimension = array[0].length;

		double[][] swapped = new double[y_dimension][x_dimension];

		for( int j = 0; j < y_dimension; ++ j ) {

			for( int i = 0; i < x_dimension; ++ i ) {

				swapped[y_dimension-j-1][i] = array[j][i];
			}
		}

		return swapped;
	}

	public static double[][] transpose( double[][] array ) {

		int y_dimension = array.length; 
		int x_dimension = array[0].length;

		double[][] transposed = new double[x_dimension][y_dimension];

		for( int j = 0; j < y_dimension; ++ j ) {

			for( int i = 0; i < x_dimension; ++ i ) {

				transposed[i][j] = array[j][i];
			}
		}

		return transposed;
	}

	public static double[][] expand( double[] array, int x_dimension, int y_dimension ) {

		double[][] expanded = new double[y_dimension][x_dimension];

		int idx = 0; 

		for( int j = 0; j < y_dimension; ++ j ) {

			for( int i = 0; i < x_dimension; ++ i ) {

				expanded[j][i] = array[idx++]; 
			}
		}

		return expanded;
	}

	public static double[][] y_swap_expand( double[] array, int x_dimension, int y_dimension ) {

		double[][] expanded = new double[y_dimension][x_dimension];

		int idx = 0; 

		for( int j = 0; j < y_dimension; ++ j ) {

			for( int i = 0; i < x_dimension; ++ i ) {

				expanded[y_dimension-j-1][i] = array[idx++]; 
			}
		}

		return expanded;
	}

	public static double[] flattenTranspose( double[][] array ) {

		int x_dimension = array.length; 
		int y_dimension = array[0].length; 

		double[] flattened = new double[x_dimension*y_dimension];

		int idx = 0; 

		for( int j = 0; j < y_dimension; ++ j ) {

			for( int i = 0; i < x_dimension; ++ i ) {

				flattened[idx++] = array[i][j];
			}
		}

		return flattened; 

	}

	public static double[] flatten( double[][] array ) {

		int y_dimension = array.length; 
		int x_dimension = array[0].length; 

		double[] flattened = new double[x_dimension*y_dimension];

		int idx = 0; 

		for( int j = 0; j < y_dimension; ++ j ) {

			for( int i = 0; i < x_dimension; ++ i ) {

				flattened[idx++] = array[j][i];
			}
		}

		return flattened; 

	}

	public static double[] y_swap_flatten( double[][] array ) {

		int y_dimension = array.length; 
		int x_dimension = array[0].length; 

		double[] flattened = new double[x_dimension*y_dimension]; 

		int idx = 0; 

		for( int j = 0; j < y_dimension; ++ j ) {

			for( int i = 0; i < x_dimension; ++ i ) {

				flattened[idx++] = array[y_dimension-j-1][i];
			}
		}

		return flattened; 

	}

	public static double[] correctRoundoff( double[] array ) {

		int dimension = array.length; 
		double threshold = 1E-10;

		for( int i = 0; i < dimension; i++ ) {

			if ( Math.abs(array[i]) < threshold ) array[i] = 0;
		}

		return array;

	}

	/**
	 * centers the values in a double[] array in the next highest power of 2
	 * @param values
	 * @param dimension
	 * @return
	 */
	public static double[] centerAndPad1D( ArrayList<Double> values ) {

		int baseSize = values.size();
		int power2Size = 1 << (int)(Math.ceil(Math.log(baseSize)/Math.log(2))); 
		double[] output = new double[power2Size]; 
		int offset = ( power2Size - baseSize ) / 2; 
		for( int i = 0; i < baseSize; ++i ) {

			output[i+offset] = values.get(i);
		}

		return output;

	}

	public static int getSquareDimension( int width, int height ) {

		return 1 << (int)(Math.log(Math.max(width, height))/Math.log(2)); 
	}

	public static double[] centerAndPad2D( int width, int height, int[] values ) {

		int power2Size = getSquareDimension( width, height );
		int dimension = power2Size*power2Size;
		double[] output = new double[dimension]; 
		int offsetX = ( power2Size - width ) / 2; 
		int offsetY = ( power2Size - height ) / 2; 

		int idx = 0; 
		for( int j = offsetY; j < height+offsetY; ++j ) {
			for( int i = offsetX; i < width+offsetX; ++i ) {

				output[j*power2Size + i] = values[idx++];
			}
		}

		return output;

	}

	public static double[] centerAndPad2D( int width, int height, ArrayList<Double> values ) {

		int power2Size = getSquareDimension( width, height );
		int dimension = power2Size*power2Size;
		double[] output = new double[dimension]; 
		int offsetX = ( power2Size - width ) / 2; 
		int offsetY = ( power2Size - height ) / 2; 

		int idx = 0; 
		for( int j = offsetY; j < height+offsetY; ++j ) {
			for( int i = offsetX; i < width+offsetX; ++i ) {

				output[j*power2Size + i] = values.get(idx++);
			}
		}

		return output;

	}

	public static double[] translate1D( double[] data, int shiftAmount ) {

		int dimension = data.length;
		double[] shifted = new double[dimension];

		for( int i = 0; i < dimension; i++ ) {

			int oldIndex = i - shiftAmount; 

			while( oldIndex < 0 ) {

				oldIndex = dimension + oldIndex;	
			} 

			if( oldIndex >= dimension ) {

				oldIndex %= dimension;
			}

			shifted[i] = data[oldIndex];

		}

		return shifted;
	}

	public static double[] translate1DLinear( double[] data, int shiftAmount ) {

		int dimension = data.length;
		double[] shifted = new double[dimension];

		for( int i = 0; i < dimension; i++ ) {

			int oldIndex = i - shiftAmount; 

			if( oldIndex >= 0 && oldIndex < dimension ) {

				shifted[i] = data[oldIndex];
			}
		}

		return shifted;
	}

	public static double[] translate2DLinear( double[] input, int x_dimension, int y_dimension, int shiftAmountX,
			int shiftAmountY ) {

		shiftAmountY = -shiftAmountY;

		double[][] expanded = ArrayUtilities.expand(input, x_dimension, y_dimension);
		double[][] output = new double[y_dimension][x_dimension]; 

		if( shiftAmountX != 0 ) { //x direction

			//shift in x direction
			for( int i = 0; i < x_dimension; ++i ) {

				int oldIndex = i - shiftAmountX; 

				if( oldIndex >= 0 && oldIndex < x_dimension ) {

					for( int j = 0; j < y_dimension; ++ j )  { 

						output[j][i] = expanded[j][oldIndex];
					}
				}
			}

			if( shiftAmountY != 0 ) { //x and y directions
				
				expanded = new double[y_dimension][x_dimension];

				for( int j = 0; j < y_dimension; ++ j ) {

					int oldIndex = j - shiftAmountY; 

					if( oldIndex >= 0 && oldIndex < y_dimension ) {

						for( int i = 0; i < x_dimension; ++ i ) {  

							expanded[j][i] = output[oldIndex][i]; 
						}
					}

				}

				return ArrayUtilities.flatten( expanded );

			} else { //x direction only 

				return ArrayUtilities.flatten( output );
			}

		} else if( shiftAmountY != 0 ) { //y direction only 

			//shift in y direction only
			for( int j = 0; j < y_dimension; ++ j ) {

				int oldIndex = j - shiftAmountY; 

				if( oldIndex >= 0 && oldIndex < y_dimension ) {

					for( int i = 0; i < x_dimension; ++ i ) {  

						output[j][i] = expanded[oldIndex][i]; 
					}
				}

			}

			return ArrayUtilities.flatten( output );

		} 

		return input; //neither x nor y direction

	}

	public static double[] translate2D( double[] input, int x_dimension, int y_dimension, int shiftAmountX,
			int shiftAmountY ) {

		shiftAmountY = -shiftAmountY;

		double[][] expanded = ArrayUtilities.expand(input, x_dimension, y_dimension);
		double[][] output = new double[y_dimension][x_dimension]; 

		if( shiftAmountX != 0 ) { //x direction

			for( int i = 0; i < x_dimension; ++i ) {

				int oldIndex = i - shiftAmountX; 

				while( oldIndex < 0 ) {

					oldIndex = x_dimension + oldIndex;	
				} 

				if( oldIndex >= x_dimension ) {

					oldIndex %= x_dimension;
				}

				for( int j = 0; j < y_dimension; ++ j )  { 

					output[j][i] = expanded[j][oldIndex];
				}
			}


			if( shiftAmountY != 0 ) { //x and y directions

				for( int j = 0; j < y_dimension; ++ j ) {

					int oldIndex = j - shiftAmountY; 

					while( oldIndex < 0 ) {

						oldIndex = y_dimension + oldIndex;	
					} 

					if( oldIndex >= y_dimension ) {

						oldIndex %= y_dimension;
					}


					for( int i = 0; i < x_dimension; ++ i ) {  

						expanded[j][i] = output[oldIndex][i]; 
					}
				}

				return ArrayUtilities.flatten( expanded );

			} else { //x direction only 

				return ArrayUtilities.flatten( output );
			}

		} else if( shiftAmountY != 0 ) { //y direction only 

			for( int j = 0; j < y_dimension; ++ j ) {

				int oldIndex = j - shiftAmountY; 

				while( oldIndex < 0 ) {

					oldIndex = y_dimension + oldIndex;	
				} 

				if( oldIndex >= y_dimension ) {

					oldIndex %= y_dimension;
				}


				for( int i = 0; i < x_dimension; ++ i ) {  

					output[j][i] = expanded[oldIndex][i]; 
				}
			}

			return ArrayUtilities.flatten( output );

		} 

		return input; //neither x nor y direction
	}

	public static double[] checkerBoard( double[] array ) {

		int dimension = array.length; 
		double[] checked = clone( array );

		for( int i = 1; i < dimension; i+=2 ) {

			checked[i] = -checked[i]; 
		}

		return checked;

	}

	public static double[] checkerBoard2D( double[] array, int x_dimension, int y_dimension ) {

		double[] checked = clone( array );
		boolean negate = false; 
		int xStart; 

		for( int y = 0; y < y_dimension; ++y ) {

			xStart = negate ? 0 : 1; 
			negate = !negate;

			for( int x = xStart; x < x_dimension; x+=2 ) {

				checked[x] = -checked[x];
			}
		}

		return checked;
	}

	public static double[] normalize( double[] array, double divideBy ) {

		int dimension = array.length; 
		double multiplier = 1.0 / divideBy;

		for( int i = 0; i < dimension; i++ ) {

			array[i] *= multiplier;  
		}

		return array;

	}

	public static double[] constant( int dimension, double amplitude ) {

		double[] constant = new double[ dimension ];		

		for ( int i = 0; i < dimension; ++i )  { 
			constant[i] = amplitude; 
		}	

		return constant; 
	}

	public static double[][] checkerBoard( double[][] array ) {


		int numRows = array.length; 
		int numCols = array[0].length; 
		boolean rowPos = true; 
		boolean colPos = true; 

		for( int j = 0; j < numRows; j++ ) {

			colPos = rowPos; 

			for( int i = 0; i < numCols; i++ ) {

				if( !colPos ) {

					array[j][i] *= -1;
				}
				colPos = !colPos;

			}

			rowPos = !rowPos;
		}

		return array;

	}
}
