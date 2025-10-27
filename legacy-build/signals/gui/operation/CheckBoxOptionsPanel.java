package signals.gui.operation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import signals.core.Operation;
import signals.operation.BooleanOperation;

@SuppressWarnings("serial")
public class CheckBoxOptionsPanel extends OperationOptionsPanel implements ActionListener {

	JCheckBox checkBox; 
	
	public CheckBoxOptionsPanel( Operation dependent, String name ) {
		
		super( dependent );
		checkBox = new JCheckBox( name ); 
		checkBox.addActionListener(this); 
		checkBox.setSelected(getSelected()); 
		add( checkBox ); 
	}

	public void actionPerformed(ActionEvent e) {
		
		setSelected( checkBox.isSelected() );
		updateInterface();
	}
	
	public void setSelected( boolean tf ) {
		
		((BooleanOperation)dependent).setSelected( tf ); 
	}
	
	public boolean getSelected() {
		
		return ((BooleanOperation)dependent).isSelected(); 
	}
}
