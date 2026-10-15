package signals.gui.operation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import signals.core.Operation;
import signals.gui.ParameterEditor;
import signals.gui.ParameterUser;
import signals.operation.ParametricOperation;

//this is a wrapper class so that ParameterEditor can be used as an OperationOptionsPanel. 
@SuppressWarnings("serial")
public class ParameterOptionsPanel extends OperationOptionsPanel implements ParameterUser {
 
	ParametricOperation paramOp; 
	
	public ParameterOptionsPanel( Operation dependent ) {
		
		super( dependent );
		
		paramOp = (ParametricOperation)dependent; 
		
		setLayout(new GridBagLayout());
		GridBagConstraints placement = new GridBagConstraints();
		placement.gridx = 0;
		placement.gridy = 0;
		placement.gridwidth = GridBagConstraints.REMAINDER;
		placement.anchor = GridBagConstraints.WEST;
		placement.insets = new Insets(5, 5, 5, 5);
		// Preserve the existing descriptor's line breaks above its fields.
		String descriptor = paramOp.getParamDescriptor().replace("&", "&amp;")
				.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
		add(new JLabel("<html>" + descriptor + "</html>"), placement);
		placement.gridy = 1;
		placement.gridwidth = 1;
		placement.weightx = 1;
		placement.fill = GridBagConstraints.HORIZONTAL;
		
		for( int i = 0; i < paramOp.getNumParams(); ++i ) {
			
			SpinnerNumberModel scaleModel = new SpinnerNumberModel(paramOp.getValue(i), -100000.0, 100000.0, 0.1); 
			ParameterEditor editor = new ParameterEditor(5, scaleModel, i, this);
			editor.setAccessibleName(paramOp.getParamName(i));
			JPanel editorPanel = new JPanel(); 
			editorPanel.add( editor );
			editorPanel.setBorder( new TitledBorder( paramOp.getParamName( i )));
			placement.gridx = i;
			add(editorPanel, placement);
			
		}
	
	}

	public void parameterChanged(int index, String newVal) {
		
		paramOp.setValue(index, Double.parseDouble(newVal)); 
		updateInterface();
	}
}
