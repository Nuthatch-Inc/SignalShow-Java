package signals.core;

public class Constants {
	
	public static final int CROP_MODE = 0, 
	SCALE_AND_PAD_MODE = 1, 
	SCALE_AND_MIRROR_MODE = 2, 
	SCALE_AND_WRAP_MODE = 3;
	
	public enum PlotStyle {
		
		STEM, SMOOTH, FILLED, HISTOGRAM, SCATTER;
	}
	
	public enum Part { 
		
		REAL_PART, IMAGINARY_PART, MAGNITUDE, PHASE, UNWRAPPED_PHASE,
		SQUARED_MAGNITUDE, LOG_SQUARED_MAGNITUDE, LOG_MAGNITUDE; 
		
	}
	
	public enum FunctionType { 
		
		NOT_COMPLEX, COMPLEX, FOURIER_BASIS; 
	}
	
	public static String partLabel( Part part ) {
		
		String toReturn = null; 
		
		switch( part ) {
		
		case REAL_PART:
			 toReturn = "Real Part";
			break; 
		case IMAGINARY_PART: 
			 toReturn = "Imaginary Part";
			break; 
		case MAGNITUDE: 
			 toReturn = "Magnitude";
			break; 
		case PHASE: 
			 toReturn = "Phase";
			break;
		case UNWRAPPED_PHASE: 
			toReturn = "Unwrapped Phase"; 
			break; 
		case SQUARED_MAGNITUDE: 
			toReturn = "Squared Magnitude"; 
			break; 
		case LOG_SQUARED_MAGNITUDE: 
			toReturn = "Log(Squared Magnitude)"; 
			break; 
		case LOG_MAGNITUDE: 
			toReturn = "Log(Magnitude)";
			break;
		
		}
		
		return toReturn;
	}
	
	public enum BasicOp {
		
		DATA, PLUS, MINUS, MULTIPLY, DIVIDE, NEGATE;
	}
	
	public static enum StyleCode {
		
		ANALYTIC_FUNCTION, DATA_FUNCTION, IMAGE;
	}

}
