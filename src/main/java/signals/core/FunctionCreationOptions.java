package signals.core;

/**
 * Contains default values for options. These can get updated
 * when the user selects a new default
 * @author Juliet
 *
 */
public class FunctionCreationOptions {

	//when creating functions, are the indices zero-centered?
	private boolean zeroCentered1D, zeroCentered2D;
	
	//number of functions currently created
	int numFunctions; 
	
	//ID used for functions
	long ID; 

	//available array sizes
	private final Integer[] availableDimensions =  
	{   	new Integer( 2 ),
			new Integer( 4 ),
			new Integer( 8 ),
			new Integer( 16 ),
			new Integer( 32 ), 
			new Integer( 64 ), 
			new Integer( 128 ), 
			new Integer( 256 ), 
			new Integer( 512 ), 
			new Integer( 1024 ),
			new Integer( 2048 ), 
			new Integer( 4096 )}; 

	//selected dimensions for array sizes 
	private int dimensionIndex1D, dimensionXIndex2D, dimensionYIndex2D; 
	
	public int dimensionToIndex( int dimension ) {
		
		int index = 0; 
		
		switch( dimension ) {
		case 2: index = 0; 
		break; 
		
		case 4: index = 1;  
			
			break;
			
		case 8: index = 2; 
			
			break; 
			
		case 16: index = 3;  
			
			break; 
			
		case 32: index = 4;  
			
			break; 
			
		case 64: index = 5;  
			
			break; 
			
		case 128: index = 6; 
			
			break; 
			
		case 256: index = 7;  
			
			break; 
			
		case 512: index = 8; 
			
			break; 
			
		case 1024: index = 9;  
			
			break; 
			
		case 2048: index = 10; 
			
			break; 
			
		case 4096: index = 11; 
			
			break; 
			
		}
		
		return index; 
		
	}

	/**
	 * Constructor loads default options
	 *
	 */
	public FunctionCreationOptions() {

		//defaults. 
		//TODO: possibly read these in from a file 
		zeroCentered1D = true;  
		zeroCentered2D = true; 
		dimensionIndex1D = 7; //256
		dimensionXIndex2D = 7; 
		dimensionYIndex2D = 7; 
		
		
		numFunctions = 0; 
		ID = 0; 
	}

	public long getNextID() {
		
		return ID++; 
	}
	

	public int getNumFunctions() {
		return numFunctions;
	}


	public void incrementNumFunctions() {
		numFunctions++;
	}

	public String getNextFunctionName() {
		
		String name = "f" + numFunctions; 
		numFunctions++; 
		return name; 
	}
	

	/**
	 * @return the dimensionIndex1D
	 */
	public int getDimensionIndex1D() {
		return dimensionIndex1D;
	}

	public double getDefaultWidth1D() {

		return Math.sqrt( getDefaultDimension1D() );
	}

	public int getDefaultDimension1D() {

		return availableDimensions[getDimensionIndex1D()];
	}

	/**
	 * @param dimensionIndex1D the dimensionIndex1D to set
	 */
	public void setDimensionIndex1D(int dimensionIndex1D) {
		this.dimensionIndex1D = dimensionIndex1D;
	}


	/**
	 * @return the zeroCentered1D
	 */
	public boolean isZeroCentered1D() {
		return zeroCentered1D;
	}

	/**
	 * @param zeroCentered1D the zeroCentered1D to set
	 */
	public void setZeroCentered1D(boolean zeroCentered1D) {
		this.zeroCentered1D = zeroCentered1D;
	}

	/**
	 * @return the zeroCentered2D
	 */
	public boolean isZeroCentered2D() {
		return zeroCentered2D;
	}

	/**
	 * @param zeroCentered2D the zeroCentered2D to set
	 */
	public void setZeroCentered2D(boolean zeroCentered2D) {
		this.zeroCentered2D = zeroCentered2D;
	}

	/**
	 * @return the availableDimensions
	 */
	public Integer[] getAvailableDimensions() {
		return availableDimensions;
	}


	/**
	 * @return the dimensionXIndex2D
	 */
	public int getDimensionXIndex2D() {
		return dimensionXIndex2D;
	}

	public double getDefaultWidthX2D() {

		return Math.sqrt( getDefaultDimensionX2D() );
	}

	public int getDefaultDimensionX2D() {

		return availableDimensions[getDimensionXIndex2D()];
	}


	/**
	 * @param dimensionXIndex2D the dimensionXIndex2D to set
	 */
	public void setDimensionXIndex2D(int dimensionXIndex2D) {
		this.dimensionXIndex2D = dimensionXIndex2D;
	}

	public double getDefaultWidthY2D() {

		return Math.sqrt( getDefaultDimensionY2D() );
	}

	public int getDefaultDimensionY2D() {

		return availableDimensions[getDimensionXIndex2D()];
	}

	/**
	 * @return the dimensionYIndex2D
	 */
	public int getDimensionYIndex2D() {
		return dimensionYIndex2D;
	}

	/**
	 * @param dimensionYIndex2D the dimensionYIndex2D to set
	 */
	public void setDimensionYIndex2D(int dimensionYIndex2D) {
		this.dimensionYIndex2D = dimensionYIndex2D;
	}

}
