package signals.io;

import java.awt.Image;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.net.URL;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;

public class ResourceLoader {

	public static URL getURL( String path ) {

		URL url = ResourceLoader.class.getResource(path);

		if (url != null) {
			return url; 
		} else {
			System.err.println("Couldn't find file: " + path );
			return null; 
		}
	}
	
	public static void loadPage( JEditorPane page, String urlPath ) {
		
		URL url = getURL( urlPath ); 
		
		try {
			page.setPage(url);
		} catch (IOException e) {
			System.err.println("Attempted to read a bad URL: " + url);
		}
		
		
	}

	/**
	 * Returns an imageIcon for a graphic used by the Signals program, assuming the graphic
	 * is in the same Jar file or directory as this class
	 */ 
	public static ImageIcon createImageIcon(String path) {

		URL imgURL = ResourceLoader.class.getResource(path);

		if (imgURL != null) {

			return new ImageIcon(imgURL);

		} else {

			System.err.println("Couldn't find file: " + path);
			return null;
		}

	}

	public static PlanarImage loadImage( String path ) {

		ImageIcon icon = createImageIcon( path ); 
		ParameterBlock pb = new ParameterBlock();
		Image image = icon.getImage();
		pb.add( image );
		return (PlanarImage)JAI.create("awtImage", pb);

	}
}
