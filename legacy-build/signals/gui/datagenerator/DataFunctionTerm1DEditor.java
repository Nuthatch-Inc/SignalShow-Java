package signals.gui.datagenerator;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import signals.core.Core;
import signals.functionterm.AnalyticFunctionTerm;
import signals.functionterm.DataFunctionTerm1D;
import signals.operation.ArrayUtilities;

@SuppressWarnings("serial")
public class DataFunctionTerm1DEditor extends AnalyticFunctionTerm1DEditor {

	JPanel loadFilePanel;
	AnalyticInteractivePlot plot; 
	CardLayout layout;
	EditAnalyticFunctionTerm1DPanel editor; 
	double[] indices; 
	
	public DataFunctionTerm1DEditor( GUIEventBroadcaster broadcaster,
			GUIEvent.Descriptor modifiedDescriptor, EditAnalyticFunctionTerm1DPanel editAnalyticFunctionTerm1DPanel ) {
		
		super(broadcaster, modifiedDescriptor); 
		
		this.editor = editAnalyticFunctionTerm1DPanel;
		plot = new AnalyticInteractivePlot( broadcaster, modifiedDescriptor, editAnalyticFunctionTerm1DPanel );
		
		loadFilePanel = new JPanel(); 
		loadFilePanel.add( new JButton( new LoadFileAction() ));
		
		layout = new CardLayout(); 
		setLayout( layout );  
		add( loadFilePanel, "file"); 
		add( plot, "plot"); 
		layout.show(this, "file"); 
	}
	
	public void setFunctionTerm( AnalyticFunctionTerm term ) {
		
		if( term instanceof DataFunctionTerm1D ) { 
		
			if( ((DataFunctionTerm1D)term).hasData() ) {
				
				plot.setFunctionTerm(term);
				showPlot();  
			}
		}
	}
	
	public void showPlot() {
		
		layout.show(this, "plot"); 
	}
	
	public void setIndices( double[] indices ) {
		
		this.indices = indices;
		plot.setIndices(indices);
	}
	
	public class LoadFileAction extends AbstractAction {
		
		public LoadFileAction() {
			
			super( "Load Text File"); 
		}

		public void actionPerformed(ActionEvent e) {
			
			//show a file chooser
			File cd = Core.getIOOptions().getCurrentDirectory(); 
			JFileChooser fc = ( cd == null ) ? new JFileChooser(): new JFileChooser( cd );
			int returnVal = fc.showOpenDialog(Core.getFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				
	            File file = fc.getSelectedFile();
	            cd = file.getParentFile(); 
	            if( cd != null ) Core.getIOOptions().setCurrentDirectory( cd );
	            ArrayList<Double> values = new ArrayList<Double>();
	            try {
					Scanner scan = new Scanner( file );
					while( scan.hasNext()) {
						values.add( scan.nextDouble() );
					}
					scan.close();
					double[] data = ArrayUtilities.centerAndPad1D(values);
					
					DataFunctionTerm1D dataTerm = new DataFunctionTerm1D( null ); 
					ParameterBlock pb = dataTerm.getDefaultParamBlock(); 
					pb.addSource( data ); 
					DataFunctionTerm1D term = new DataFunctionTerm1D( pb ); 
					term.setDescriptor(file.getName()); 
					
					setFunctionTerm( term ); 
					
					editor.setFunctionTerm(term);
					editor.updateDependents();
					showPlot();
					
				} catch (FileNotFoundException e1) {} //TODO: error handling
	        }
			
		}
		
		
	}
}
