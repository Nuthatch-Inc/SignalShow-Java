package signals.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.Constants.Part;

public class ComplexIO {

	public static void write( File file, Function function ) {

		if( function instanceof Function1D ) {
			
			write1D( file, (Function1D)function ); 
			
		} else {
		
			write2D( file, (Function2D)function ); 
		}

	}
	
	public static Function read( File file ) {
		
		try {
			
			Scanner scan = new Scanner( file );
			
			if( scan.next().equals("1D")) { 
				
				return read1D( scan ); 
				
			} else {
				
				return read2D( scan ); 
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; 
	}
	
	private static Function read2D( Scanner scan ) {
		
		//read in the dimension 
		int dimensionX = scan.nextInt(); 
		int dimensionY = scan.nextInt(); 
		
		//read zerocentered
		boolean zeroCentered = scan.nextBoolean(); 
		
		//read name
		String name = scan.next(); 
		
		double[] real = new double[dimensionX*dimensionY]; 
		double[] imag = new double[dimensionX*dimensionY]; 
		

		int i = 0; 
		for( int y = 0; y < dimensionY; ++y ) {
			for( int x = 0; x < dimensionX; ++x ) {

				real[i++] = scan.nextDouble(); 

			}
		}
		
		i = 0; 
		for( int y = 0; y < dimensionY; ++y ) {
			for( int x = 0; x < dimensionX; ++x ) {

				imag[i++] = scan.nextDouble(); 

			}
		}
		
		
		return FunctionFactory.createFunction2D(real, imag, zeroCentered, name, dimensionX, dimensionY); 
	}
	
	private static Function read1D( Scanner scan ) {
		
		//read in the dimension 
		int dimension = scan.nextInt(); 
		
		//read zerocentered
		boolean zeroCentered = scan.nextBoolean(); 
		
		//read name
		String name = scan.next(); 
		
		double[] real = new double[dimension]; 
		double[] imag = new double[dimension]; 
		
		for( int i = 0; i < dimension; i++ ) {
			
			real[i] = scan.nextDouble(); 
		}
		
		for( int i = 0; i < dimension; i++ ) {
			
			imag[i] = scan.nextDouble(); 
		}
		
		return FunctionFactory.createFunction1D(real, imag, zeroCentered, name); 
	}

	public static boolean write1D( File file, Function1D function ) {

		//open the file
		FileWriter writer = null; 
		try {

			writer = new FileWriter(file);

			//write the dimension to the file
			writer.write( "1D "+function.getDimension() + "\n" ); 
			
			//write zerocentered
			writer.write( ""+function.isZeroCentered() + "\n" ); 
			
			//write descriptor 
			writer.write( function.getDescriptor() + "\n" ); 

			writePart1D( writer, function, Part.REAL_PART); 
			writer.write( "\n\n" ); 			
			writePart1D( writer, function, Part.IMAGINARY_PART); 

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
	
	public static boolean write2D( File file, Function2D function ) {

		//open the file
		FileWriter writer = null; 
		try {

			writer = new FileWriter(file);

			//write the dimension to the file
			writer.write( "2D "+function.getDimensionX() + " " + function.getDimensionY() + "\n" ); 
			
			//write zerocentered
			writer.write( ""+function.isZeroCentered() + "\n" ); 
			
			//write descriptor 
			writer.write( function.getDescriptor() + "\n" ); 

			writePart2D( writer, function, Part.REAL_PART); 
			writer.write( "\n\n" ); 			
			writePart2D( writer, function, Part.IMAGINARY_PART); 

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

	private static void writePart2D( FileWriter writer, Function2D function, Part part ) throws IOException {

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

	}


	private static void writePart1D( FileWriter writer, Function1D function, Part part ) throws IOException {

		//write the data to the file
		double[] data = ((Function)function).getPart(part); 

		int i = 0; 

		//for each row
		for( int y = 0; y < function.getDimension(); ++y ) {

			writer.write("\n"+data[i++]); 

		}

	}


}
