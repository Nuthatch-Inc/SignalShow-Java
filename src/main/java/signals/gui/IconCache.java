package signals.gui;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import javax.swing.ImageIcon;

import signals.io.ResourceLoader;

public class IconCache {
	
	@SuppressWarnings("unchecked")
	protected HashMap<String, SoftReference> icons; 
	
	@SuppressWarnings("unchecked")
	protected IconCache() {
		
		icons = new HashMap<String, SoftReference>(); 
	}
	
	public static class SingletonHolder { 
		private final static IconCache INSTANCE = new IconCache();
	}

	public static IconCache getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public static ImageIcon getIcon( String path ) {
		
		return (ImageIcon) getInstance().loadIcon(path).get();
	}
	
	@SuppressWarnings("unchecked")
	private SoftReference loadIcon( String key ) {
		
		SoftReference icon = null; 
		if( !icons.containsKey(key) || icons.get(key).get() == null ) {
			
			icon = new SoftReference( ResourceLoader.createImageIcon(key) );
			icons.put( key, icon );
			
		} else icon = icons.get(key); 
		
		return icon; 
	}
	
}
