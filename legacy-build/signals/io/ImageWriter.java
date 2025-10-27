package signals.io;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JOptionPane;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function2D;
import signals.core.Constants.Part;
import signals.gui.plot.ImageDisplayMath;

public class ImageWriter {

	public static enum ImageType { TIFF, PNG, JPG, BMP, TEXT }; 

	public static boolean writeImage( File file, Function2D function, Part part, String extension ) {
		
		return writeImage( file, function, part, extensionToType( extension ) ); 
	}
	
	public static boolean writeImage( File file, Function2D function, Part part, ImageType type ) {

		double[] data = ((Function)function).getPart(part); 
		RenderedImage tiledImage = ImageDisplayMath.data2Image( data, function.getDimensionX(), function.getDimensionY() );

		ImageDisplayMath disp = new ImageDisplayMath(); 
		RenderedImage scaled = disp.autoScale(tiledImage); 
		PlanarImage output = (PlanarImage) ImageDisplayMath.convertToDisplayType(scaled);
		BufferedImage bi = output.getAsBufferedImage();

		return writeImage( file, bi, type ); 
	}

	public static String typeToExtension( ImageType type ) { 

		switch( type ) {

		case BMP: 

			return ".bmp"; 
		case JPG: 

			return ".jpg"; 

		case PNG: 

			return ".png"; 

		case TIFF: 

			return ".tiff"; 

		case TEXT: 

			return ".txt"; 

		}

		return null; 
	}
	
	public static String replaceExtension( String filename, ImageType type ) {
		
		String extension = Utils.getExtension(filename);
		
		if( extension == null || extension.trim().length() == 0 ) {
			
			filename = filename + typeToExtension( type ); 
			
		} else if( type != extensionToType( extension ) ) {
			
			String strippedName = Utils.getNameNoExtension(filename); 
			filename = strippedName + typeToExtension( type ); 
		}
		
		return filename; 
	}

	public static ImageType extensionToType( String extension ) { 

		String ext = extension.toLowerCase(); 

		if( ext.equals( ".tiff") || ext.equals( ".tif")) {

			return ImageType.TIFF; 

		} else if( ext.equals( ".png")) {

			return ImageType.PNG; 

		} else if( ext.equals( ".jpg") || ext.equals( ".jpeg")) {

			return ImageType.JPG; 

		} else if( ext.equals( ".txt")) {

			return ImageType.TEXT; 
			
		} else if( ext.equals( ".bmp")) {

			return ImageType.BMP; 
		} 

		return null; 
	}

	public static boolean writeImage( File file, BufferedImage image, ImageType type ) { 

		String path = file.getPath(); 

		try{ 
			switch( type ) {

			case BMP: 

				JAI.create("filestore",image,path,"BMP"); 
				
				break; 

			case JPG: 

				ImageIO.write(image,"JPG",new File( path )); 

				break; 

			case PNG: 

				ImageIO.write(image,"PNG",new File( path )); 
				
				break; 

			case TIFF: 

				JAI.create("filestore",image,path,"TIFF");  

				break; 

			} 

		} catch(Exception e) { 

			JOptionPane.showMessageDialog(Core.getFrame(),
					"Image was not saved.",
					"File I/O Error",
					JOptionPane.ERROR_MESSAGE);

			return false; 

		}

		return true; 
	}


	public static boolean writeText( File file, Function2D function, Part part ) {

		String path = file.getPath();
		String extension = Utils.getExtension(path); 

		if( extension == null || extension.trim().length() == 0 || ImageType.TEXT != extensionToType( extension ) ) {

			path = path + typeToExtension( ImageType.TEXT ); 

		}
		
		
		//open the file
		FileWriter writer = null; 
		try {

			writer = new FileWriter(new File(path));

			//write the dimension to the file
			writer.write( ""+function.getDimensionX() + " " + function.getDimensionY() ); 

			//write the data to the file
			double[] data = ((Function)function).getPart(part); 

			int i = 0; 

			//for each row
			for( int y = 0; y < function.getDimensionY(); ++y ) {

				//write a newline
				writer.write('\n');

				//for each col 
				for( int x = 0; x < function.getDimensionX(); ++x ) {

					writer.write(""+data[i++] + " "); 

				}

			}

			//close the file
			writer.close();


		} catch (IOException e) {
			JOptionPane.showMessageDialog(Core.getFrame(),
					"Data was not saved.",
					"File I/O Error",
					JOptionPane.ERROR_MESSAGE);
			return false; 
		} 

		return true; 
	}



}
