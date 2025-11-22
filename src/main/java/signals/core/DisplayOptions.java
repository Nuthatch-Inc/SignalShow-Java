package signals.core;

import java.awt.Font;
import java.text.NumberFormat;

public class DisplayOptions {

	NumberFormat format;

	Font labelFont; 
	Font axisFont; 
	
	public DisplayOptions() {
		
		format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(3);
		
		labelFont = new Font( "sansserif", Font.PLAIN, 14 );
		axisFont = new Font("Monospaced", Font.PLAIN, 11);
	}

	public Font getLabelFont() {
		return labelFont;
	}

	/**
	 * @return the format
	 */
	public NumberFormat getFormat() {
		return format;
	}
	
	public Font getAxisFont() {
		return axisFont;
	}
	
}
