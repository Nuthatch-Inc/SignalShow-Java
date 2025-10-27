package signals.gui.datagenerator;

import javax.swing.JPanel;

import signals.functionterm.AnalyticFunctionTerm;

@SuppressWarnings("serial")
public abstract class AnalyticFunctionTermEditor extends JPanel implements GUIEventListener {

	GUIEventBroadcaster broadcaster; 
	
	GUIEvent.Descriptor modifiedDescriptor; 
	
	public AnalyticFunctionTermEditor( GUIEventBroadcaster broadcaster, GUIEvent.Descriptor modifiedDescriptor ) {
		
		this.broadcaster = broadcaster; 
		this.modifiedDescriptor = modifiedDescriptor; 
		broadcaster.addGUIEventListener(this); 
	}

	public abstract void setFunctionTerm( AnalyticFunctionTerm function );

}