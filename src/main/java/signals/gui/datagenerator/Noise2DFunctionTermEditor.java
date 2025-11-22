package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JTabbedPane;

import signals.functionterm.AnalyticFunctionTerm;
import signals.functionterm.NoiseFunctionTerm2D;
import signals.gui.GUIDimensions;
import signals.gui.plot.FunctionTerm2DDisplayPanel;

@SuppressWarnings("serial")
public class Noise2DFunctionTermEditor extends AnalyticFunctionTermEditor {
	
	HistogramInteractivePlot histogramPlot; 
	FunctionTerm2DDisplayPanel display; 
	
	 int x_dimension; 
	 int y_dimension;
	 boolean zeroCentered;
	 
	 NoiseFunctionTerm2D functionTerm; 
	
	
	public Noise2DFunctionTermEditor( GUIEventBroadcaster broadcaster, 
			GUIEvent.Descriptor modifiedDescriptor, EditNoiseFunctionTerm2DPanel editor ) {
		
		super( broadcaster, modifiedDescriptor ); 
		
		histogramPlot = new HistogramInteractivePlot( broadcaster, modifiedDescriptor, editor );
		display = new FunctionTerm2DDisplayPanel(GUIDimensions.largeImageThumbnailDimension);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		tabbedPane.setTabPlacement(JTabbedPane.LEFT);
		tabbedPane.addTab("Data", display ); 
		tabbedPane.addTab("Histogram", histogramPlot );
		add( tabbedPane, BorderLayout.CENTER );
		
	}
	
	@Override
	public void setFunctionTerm(AnalyticFunctionTerm term) {
		
		functionTerm = (NoiseFunctionTerm2D)term; 
		display.setFunctionTerm(functionTerm); 
		histogramPlot.setFunctionTerm( functionTerm.getFunctionTerm1D() ); 
		
	}

	public void setIndices( int x_dimension, int y_dimension, boolean zeroCentered ) {
	
		this.x_dimension = x_dimension; 
		this.y_dimension = y_dimension; 
		this.zeroCentered = zeroCentered;
		histogramPlot.setRawDimension( x_dimension * y_dimension ); 
		display.setIndices(x_dimension, y_dimension, zeroCentered);
		
	}

	public void setIndices(GUIEvent e) {
		ArrayList<Object> values = e.getValues(); 
		setIndices( (Integer)values.get(0), (Integer)values.get(1), (Boolean)values.get(2) );
	}
	
	public void GUIEventOccurred(GUIEvent e) {

		if( e.getSource().equals(this) ) return; 

		switch( e.getDescriptor() ) {

		case INDICES_MODIFIED_1D: //comes from general interface

			setIndices( e ); 

			break; 

		case MODIFIED: //comes from term panel

			if(e.getValue(0) instanceof NoiseFunctionTerm2D)
			setFunctionTerm((NoiseFunctionTerm2D)e.getValue(0));

			break; 

		}

	}
	
}
