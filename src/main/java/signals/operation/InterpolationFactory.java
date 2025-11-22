package signals.operation;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;

public class InterpolationFactory {

	public static Interpolation create( int type ) {
		
		Interpolation toReturn = null; 
		
		switch( type ) {
		
		case Interpolation.INTERP_BILINEAR: 
			toReturn = new InterpolationBilinear(); 
			break; 
		case Interpolation.INTERP_NEAREST: 
			toReturn = new InterpolationNearest(); 
			break; 
		case Interpolation.INTERP_BICUBIC: 
			toReturn = new InterpolationBicubic(8); 
			break;
		case Interpolation.INTERP_BICUBIC_2: 
			toReturn = new InterpolationBicubic2(8);  
			break;
		
		}
		
		return toReturn;
	}
	
}
