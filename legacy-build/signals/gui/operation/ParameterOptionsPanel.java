package signals.gui.operation;

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
		
		add( new JLabel( paramOp.getParamDescriptor() )); 
		
		for( int i = 0; i < paramOp.getNumParams(); ++i ) {
			
			SpinnerNumberModel scaleModel = new SpinnerNumberModel(paramOp.getValue(i), -100000.0, 100000.0, 0.1); 
			ParameterEditor editor = new ParameterEditor(5, scaleModel, i, this);
			JPanel editorPanel = new JPanel(); 
			editorPanel.add( editor );
			editorPanel.setBorder( new TitledBorder( paramOp.getParamName( i )));
			add( editorPanel );
			
		}
	
	}

	public void parameterChanged(int index, String newVal) {
		
		paramOp.setValue(index, Double.parseDouble(newVal)); 
		updateInterface();
	}
}
