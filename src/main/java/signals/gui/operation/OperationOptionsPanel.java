package signals.gui.operation;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JPanel;

import signals.core.Operation;
import signals.gui.datagenerator.GUIEvent;
import signals.gui.datagenerator.GUIEventBroadcaster;

@SuppressWarnings("serial")
public class OperationOptionsPanel extends JPanel {
	
	Operation dependent;
	GUIEventBroadcaster broadcaster; 
	ArrayList<OperationOptionsPanel> children; 
	
	/**
	 * @param dependent
	 */
	public OperationOptionsPanel(Operation dependent) {
		super();
		this.dependent = dependent;
	} 
	
	public void updateInterface() {
		
		if( broadcaster != null ) broadcaster.broadcast(this, GUIEvent.Descriptor.PARAM_CHANGED);
	}
	
	public void addChild( OperationOptionsPanel op ) {
		
		if( children == null ) children = new ArrayList<OperationOptionsPanel>(); 
		children.add(op); 
	}
	
	@Override
	public Component add(Component comp, int index) {
		if( comp instanceof OperationOptionsPanel ) addChild( (OperationOptionsPanel)comp ); 
 		return super.add(comp, index);
	}

	@Override
	public void add(Component comp, Object constraints, int index) {
		if( comp instanceof OperationOptionsPanel ) addChild( (OperationOptionsPanel)comp ); 
		super.add(comp, constraints, index);
	}

	@Override
	public void add(Component comp, Object constraints) {
		if( comp instanceof OperationOptionsPanel ) addChild( (OperationOptionsPanel)comp ); 
		super.add(comp, constraints);
	}

	@Override
	public Component add(Component comp) {
		if( comp instanceof OperationOptionsPanel ) addChild( (OperationOptionsPanel)comp ); 
		return super.add(comp);
	}

	public void setBroadCaster( GUIEventBroadcaster broadcaster ) {
		
		this.broadcaster = broadcaster; 
		if( children != null) for( OperationOptionsPanel child : children ) child.setBroadCaster(broadcaster); 
	}

}
