package signals.core;

import java.text.NumberFormat;

public class StringConversions {

	/**
	 * 
	 * @param phaseInDegrees
	 * @return +/- (phase in radians in terms of pi) 
	 */
	public static String PhaseString( double phaseInDegrees ) {
		
		String phaseString = "";  
		NumberFormat format = Core.getDisplayOptions().getFormat();
		if( phaseInDegrees > 0 ) phaseString = " + "+format.format(Math.toRadians(phaseInDegrees)/Math.PI) + "\u03C0";
		else if( phaseInDegrees < 0 ) phaseString = " - "+format.format(Math.toRadians(-1*phaseInDegrees)/Math.PI) + "\u03C0";
		return phaseString; 
	}
	
}
