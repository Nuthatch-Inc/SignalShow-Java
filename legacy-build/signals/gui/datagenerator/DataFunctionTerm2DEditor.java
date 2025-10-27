package signals.gui.datagenerator;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import signals.core.Core;
import signals.functionterm.AnalyticFunctionTerm2D;
import signals.functionterm.DataFunctionTerm2D;
import signals.operation.ArrayUtilities;

@SuppressWarnings("serial")
public class DataFunctionTerm2DEditor extends JPanel implements AnalyticFunctionTerm2DEditor {

	JPanel loadFilePanel;
	InteractiveFunctionTerm2DDisplay display; 
	CardLayout layout;
	EditImageFunctionTermPanel editor; 
	int x_dimension; 
	int y_dimension; 
	boolean zeroCentered; 

	public DataFunctionTerm2DEditor( EditImageFunctionTermPanel editor ) {

		this.editor = editor;
		display = new InteractiveFunctionTerm2DDisplay( editor );

		loadFilePanel = new JPanel(); 
		loadFilePanel.add( new JButton( new LoadFileAction() ));
		loadFilePanel.add( new JButton( new LoadImageAction() ));

		layout = new CardLayout(); 
		setLayout( layout );  
		add( loadFilePanel, "file"); 
		add( display, "plot"); 
		layout.show(this, "file"); 
	}

	public void showPlot() {

		layout.show(this, "plot"); 
	}

	public void setFunctionTerm(AnalyticFunctionTerm2D term) {

		if( term instanceof DataFunctionTerm2D && ((DataFunctionTerm2D)term).hasData() ) {

			display.setFunctionTerm(term);
			showPlot();  
		}

	}

	public void setIndices(int x_dimension, int y_dimension,
			boolean zeroCentered) {
		this.x_dimension = x_dimension; 
		this.y_dimension = y_dimension; 
		this.zeroCentered = zeroCentered;
		display.setIndices(x_dimension, y_dimension, zeroCentered);
	}

	public class LoadImageAction extends AbstractAction {

		public LoadImageAction() {

			super( "Load Image" ); 
		}

		public void actionPerformed(ActionEvent e) {

			//show a file chooser
			File cd = Core.getIOOptions().getCurrentDirectory(); 
			JFileChooser fc = ( cd == null ) ? new JFileChooser(): new JFileChooser( cd );
			int returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				try { 

					File file = fc.getSelectedFile();
					cd = file.getParentFile(); 
					if( cd != null ) Core.getIOOptions().setCurrentDirectory( cd );
					String name = file.getAbsolutePath();

					PlanarImage pi = JAI.create("fileload", name); 

					int width = pi.getWidth(); 
					int height = pi.getHeight(); 

					SampleModel sm = pi.getSampleModel();  
					int nbands = sm.getNumBands();  
					// We assume that we can get the pixels values in a integer array.
					java.awt.image.Raster inputRaster = pi.getData();  
					int[] values = new int[nbands*width*height];  
					inputRaster.getPixels(0,0,width,height,values); 
					int[] redChannel = new int[width*height];
					int r = 0; 
					for(int h=0;h<height;h++)  { //for now, only get the red channel
						for(int w=0;w<width;w++)  
						{  
							// Dump the values.
							int offset = h*width*nbands+w*nbands;  

							redChannel[r++] = values[offset]; 

						}  
					}  

					double[] data = ArrayUtilities.centerAndPad2D(width, height, redChannel);

					DataFunctionTerm2D dataTerm = new DataFunctionTerm2D( null ); 
					ParameterBlock pb = dataTerm.getDefaultParamBlock(); 
					pb.addSource( data ); 
					int squareDimension = ArrayUtilities.getSquareDimension(width, height);
					pb.addSource( new Integer( squareDimension ) ); 
					pb.addSource( new Integer( squareDimension ) ); 
					DataFunctionTerm2D term = new DataFunctionTerm2D( pb ); 

					editor.setFunctionTerm(term);
					editor.updateDependents();
					showPlot();

				} catch (Exception er) {
					JOptionPane.showMessageDialog(Core.getFrame(),
							"Image was not loaded. Make sure that image is square and grayscale.",
							"File I/O Error",
							JOptionPane.ERROR_MESSAGE);
				} 
			}
		}
	}

	public class LoadFileAction extends AbstractAction {

		public LoadFileAction() {

			super( "Load Text File"); 
		}

		public void actionPerformed(ActionEvent e) {

			//show a file chooser
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(Core.getFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				ArrayList<Double> values = new ArrayList<Double>();
				try {
					Scanner scan = new Scanner( file );
					int width = scan.nextInt(); 
					int height = scan.nextInt();
					while( scan.hasNext()) {
						values.add( scan.nextDouble() );
					}
					scan.close();
					double[] data = ArrayUtilities.centerAndPad2D(width, height, values);

					DataFunctionTerm2D dataTerm = new DataFunctionTerm2D( null ); 
					ParameterBlock pb = dataTerm.getDefaultParamBlock(); 
					pb.addSource( data ); 
					int squareDimension = ArrayUtilities.getSquareDimension(width, height);
					pb.addSource( new Integer( squareDimension ) ); 
					pb.addSource( new Integer( squareDimension ) ); 
					DataFunctionTerm2D term = new DataFunctionTerm2D( pb ); 

					editor.setFunctionTerm(term);
					editor.updateDependents();
					showPlot();

				} catch (FileNotFoundException e1) {} //TODO: error handling
			}

		}


	}


}
