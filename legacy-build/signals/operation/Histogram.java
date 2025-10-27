package signals.operation;

public class Histogram {
	
	/**
	* Bin Tag 
	* This signals.operation takes data and places it into one of numBins "bins", where the bins range between 
	* minBinVal and maxBinVal. What is returned is an integer array the size of the original data
	* where the value at index i is the bin number that corresponds to the original data at index i. 
	* @param input - data to take the histogram of
	* @param numBings - the number of bins to use in the histogram
	* @param minBinVal - the value of the first bin. A typical value is the minimum data value or 0. 
	* @param maxBinVal - the value to use for the last bin. A typical value is the maximum data value or 255.
	* precondition - maxBinVal > minBinVal
	*/ 
	public static int[] binTag( double[] input, int numBins, double minBinVal, double maxBinVal ) {
		
		
		int dimension = input.length;
		
		int[] binTag = new int[dimension];
		
		double range = maxBinVal - minBinVal; 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			//subtracting the minimum brings the values between 0 and range
			//dividing by range brings the values between 0 and 1
			//multiplying by numbins brings the values between 0 and numbins
			double bin = numBins * ( input[i] - minBinVal ) / range; 
			
			if( bin < 0 ) {
				
				binTag[i] = 0; 
				
			} else if( bin > numBins -1 ) {
				
				binTag[i] = numBins - 1;  
				
			} else {
				
				binTag[i] = (int)bin; 
				
			}
			
		}
		
		return binTag;
		
	}
	
	
	/**
	* Calculate Cumulative Histogram
	* @param input - data to take the histogram of
	* @param numBings - the number of bins to use in the histogram
	* @param minBinVal - the value of the first bin. A typical value is the minimum data value or 0. 
	* @param maxBinVal - the value to use for the last bin. A typical value is the maximum data value or 255.
	* precondition - maxBinVal > minBinVal
	*/ 
	public static double[] cumulativeHistogram( double[] input, int numBins, double minBinVal, double maxBinVal ) {
	
		
		double[] histogram = histogram( input, numBins, minBinVal, maxBinVal ); 
		double[] chist = new double[numBins];
		chist[0] = histogram[0];
		
		for( int i = 1; i < numBins; i++ ) {
			
			chist[i] = chist[i-1] + histogram[i];
		}

		
		return chist;
		
	} 
	
	public static double[] binIndices( int numBins, double minBinVal, double maxBinVal ) {
		
		double[] indices = new double[numBins]; 
		double increment = (maxBinVal-minBinVal)/(double)numBins;
		double val = minBinVal; 
		for( int i=0; i < numBins; i++ ) {
			indices[i] = val;
			val += increment;
		}
		return indices;
	}
	
	public static double[] integerBinIndices( int minBinVal, int maxBinVal ) {
		
		int numBins = maxBinVal - minBinVal + 1; 
		double[] indices = new double[numBins]; 
		for( int i=0; i < numBins; ++i ) {
			indices[i] = minBinVal+i;
		}
		return indices;
	}
	
	/**
	* Calculate Histogram
	* @param input - data to take the histogram of
	* @param numBins - the number of bins to use in the histogram
	* @param minBinVal - the minimum value of the first bin. A typical value is the minimum data value or 0. 
	* @param maxBinVal - the maximum value to use for the last bin. A typical value is the maximum data value or 255.
	* precondition - maxBinVal > minBinVal
	*/ 
	public static double[] histogram( double[] input, int numBins, double minBinVal, double maxBinVal ) {
		
		double[] histogram = new double[numBins];
		
		double range = maxBinVal - minBinVal; 
		int dimension = input.length;
		
		for ( int i = 0; i < dimension; i++ )  {
			
			//subtracting the minimum brings the values between 0 and range
			//dividing by range brings the values between 0 and 1
			//multiplying by numbins brings the values between 0 and numbins
			double bin = numBins * ( input[i] - minBinVal ) / range; 
			
			if( bin < 0 ) {
				
				histogram[0] += 1; 
				
			} else if( bin > numBins -1 ) {
				
				histogram[numBins-1] += 1; 
				
			} else {
				
				int index = (int)bin; 
				histogram[index] += 1;
				
			}
			
		}
		
		return histogram; 
		
	}
	

	/**
	* Calculates the histogram for distributions that are only defined at integers
	*/ 
	public static double[] integerHistogram( double[] input, int minBinVal, int maxBinVal ) {
		
		int numBins = maxBinVal - minBinVal + 1; 
		
		double[] histogram = new double[numBins];
		
		int dimension = input.length;
		
		for ( int i = 0; i < dimension; i++ )  {
			
			int bin = (int)input[i] - minBinVal;
			histogram[bin] += 1;
			
		}
		
		return histogram; 
		
	}
	
	/**
	* Calculate PDF
	* Calculates the histogram, then divides each element by the total number of pixels
	* @param input - data to take the histogram of
	* @param numBings - the number of bins to use in the histogram
	* @param minBinVal - the value of the first bin. A typical value is the minimum data value or 0. 
	* @param maxBinVal - the value to use for the last bin. A typical value is the maximum data value or 255.
	* precondition - maxBinVal > minBinVal
	*/ 
	public static double[] PDF( double[] input, int numBins, double minBinVal, double maxBinVal ) {
		
		double[] histogram = histogram( input, numBins, minBinVal, maxBinVal ); 
		double[] pdf = new double[numBins];
		double dimension = (double)(input.length); 
		
		for( int i = 1; i < numBins; i++ ) {
			
			pdf[i] = histogram[i] / dimension;
		}
		
		return pdf;
		
	}
	
	/**
	* Calculate CDF
	* @param input - data to take the histogram of
	* @param numBings - the number of bins to use in the histogram
	* @param minBinVal - the value of the first bin. A typical value is the minimum data value or 0. 
	* @param maxBinVal - the value to use for the last bin. A typical value is the maximum data value or 255.
	* precondition - maxBinVal > minBinVal
	*/ 
	public static double[] CDF( double[] input, int numBins, double minBinVal, double maxBinVal ) {
	
		
		double[] histogram = histogram( input, numBins, minBinVal, maxBinVal ); 
		double[] cdf = new double[numBins];
		double dimension = (double)(input.length); 
		cdf[0] = (double)histogram[0] / dimension;
		
		for( int i = 1; i < numBins; i++ ) {
			
			cdf[i] = cdf[i-1] + histogram[i] / dimension;
		}
		
		return cdf;
		
	} 
	

	public static double[] equalizeHistogram( double[] input, int numBins, double minBinVal, double maxBinVal ) {
		
		//first, combine calchisto and binTag in order to: 
		//1. figure out which bin each data item belongs in and
		//2. create the histogram, which will be used to create the lookup table
		
		int dimension = input.length;
		
		int[] binTag = new int[dimension]; //for each item in array, stores which bin it belongs in
		int[] histogram = new int[numBins];
		
		double range = maxBinVal - minBinVal; 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			//subtracting the minimum brings the values between 0 and range
			//dividing by range brings the values between 0 and 1
			//multiplying by numbins brings the values between 0 and numbins
			//then round to convert to an integer
			int bin = (int)Math.round( numBins * ( input[i] - minBinVal ) / range ); 
			
			if( bin < 0 ) bin = 0; 
			else if(  bin > numBins -1 ) bin = numBins - 1;
				
			histogram[bin]++;
			binTag[i] = bin; 
			
		}
		
		//next, create the lookup table by finding the cdf, then for each cdf entry, multiplying by range and adding in the minimum. 
		
		double[] lut = new double[numBins];
		double rangeOverDimension = range / (double)(dimension); 
		
		lut[0] = rangeOverDimension * (double)histogram[0] + minBinVal; //only add in minBinVal once
		
		for( int i = 1; i < numBins; i++ ) {
			
			lut[i] = lut[i-1] + rangeOverDimension * (double)histogram[i];
		}
		
		//finally, apply the lookup table
		
		double[] applied = new double[dimension]; 
		
		for ( int i = 0; i < dimension; i++ )  {
			
			applied[i] = lut[binTag[i]]; 
		}
		
		return applied;
		
	}

}
