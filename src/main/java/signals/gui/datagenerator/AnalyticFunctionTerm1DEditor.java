package signals.gui.datagenerator;


import signals.functionterm.AnalyticFunctionTerm;
import signals.functionterm.AnalyticFunctionTerm1D;

@SuppressWarnings("serial")
public abstract class AnalyticFunctionTerm1DEditor extends AnalyticFunctionTermEditor {

	public AnalyticFunctionTerm1DEditor(GUIEventBroadcaster broadcaster, GUIEvent.Descriptor modifiedDescriptor) {
		super(broadcaster, modifiedDescriptor);
	}

	double[] indices; 

	protected AnalyticFunctionTerm1D currentFunction; 

	public void setIndices( double[] indices ) {
		this.indices = indices; 
	}

	public void setFunctionTerm( AnalyticFunctionTerm term ) {

		currentFunction = (AnalyticFunctionTerm1D)term;
	}

	public void GUIEventOccurred(GUIEvent e) {

		if( e.getSource().equals(this) ) return; 
		
		if( e.getDescriptor().equals( modifiedDescriptor )) {
			
			setFunctionTerm( (AnalyticFunctionTerm)(e.getValue(0)) ); 
		}

		switch( e.getDescriptor() ) {

		case INDICES_MODIFIED_1D: //comes from general interface

			setIndices( (double[])(e.getValue(0))); 

			break; 

		}

	}



}
