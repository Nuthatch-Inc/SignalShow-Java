package signals.gui.plot;

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;

public class ImageModelCache {
	
	@SuppressWarnings("unchecked")
	protected HashMap<String, SoftReference> sampleModelsDouble; 
	
	@SuppressWarnings("unchecked")
	protected HashMap<String, SoftReference> colorModelsDouble;

	
	@SuppressWarnings("unchecked")
	protected ImageModelCache() {
		
		sampleModelsDouble = new HashMap<String, SoftReference>(); 
		colorModelsDouble = new HashMap<String, SoftReference>(); 
		
	}
	
	private static class SingletonHolder { 
		private final static ImageModelCache INSTANCE = new ImageModelCache();
	}

	public static ImageModelCache getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public static SampleModel getSampleModelDouble( int x_dim, int y_dim ) {
		
		return (SampleModel) getInstance().loadSampleModelDouble( x_dim, y_dim).get();
	}
	
	@SuppressWarnings("unchecked")
	private SoftReference loadSampleModelDouble( int x_dim, int y_dim ) {
		
		String key = "" + x_dim + ' ' + y_dim; 
		
		SoftReference ref = null; 
		if( !sampleModelsDouble.containsKey(key) || sampleModelsDouble.get(key).get() == null ) {
			
			ref = new SoftReference( RasterFactory.createBandedSampleModel( 
					DataBuffer.TYPE_DOUBLE, x_dim, y_dim, 1 ) );
			sampleModelsDouble.put( key, ref );
			
		} else ref = sampleModelsDouble.get(key); 
		
		return ref; 
	}
	
	public static ColorModel getColorModelDouble( int x_dim, int y_dim ) {
		
		return (ColorModel) getInstance().loadColorModelDouble( x_dim, y_dim).get();
	}
	
	@SuppressWarnings("unchecked")
	private SoftReference loadColorModelDouble(int x_dim, int y_dim ) {
		
		
		String key = "" + x_dim + ' ' + y_dim; 
		
		SoftReference ref = null; 
		if( !colorModelsDouble.containsKey(key) || colorModelsDouble.get(key).get() == null ) {
			
			ref = new SoftReference( PlanarImage.createColorModel( getSampleModelDouble( x_dim, y_dim ) ) );
			colorModelsDouble.put( key, ref );
	
			
		} else ref = colorModelsDouble.get(key); 
		
		return ref; 
	}
	
}
