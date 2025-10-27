package signals.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial")
public class ParameterEditor extends JSpinner implements DocumentListener, FocusListener {

	int index; 

	String oldVal; 

	ParameterUser dependent; 

	JTextField textField; 

	String currentText; 

	public ParameterEditor( int cols, SpinnerNumberModel model, int index, ParameterUser dependent ) {	

		super( model );
		
		textField = ((JSpinner.DefaultEditor)getEditor()).getTextField();
		textField.setColumns(cols);
		this.index = index; 
		this.dependent = dependent;
		textField.getDocument().addDocumentListener( this );
		textField.addFocusListener( this );
		currentText = ""; 
	}

	public void changedUpdate(DocumentEvent e) {}

	public void insertUpdate(DocumentEvent e) {
		
		//if( !editable ) return; 
		if( !textField.getText().equals( oldVal ) ) {

			changeDependent();
		}

	}

	//not necessary to implement
	public void removeUpdate(DocumentEvent e) { 
		
		if( !textField.getText().equals( oldVal ) ) {

			changeDependent();
		}
	}

	public void setText( String text ) {

		textField.setText(text);
	}

	public void changeDependent() {
		
		String text = textField.getText(); 
		String trimmed = text.trim();
		if (trimmed.length() != 0) { 
			if( !trimmed.equals(".") && !trimmed.equals("-")) {
				try { 
					dependent.parameterChanged(index, trimmed);
					oldVal = text; 
				} catch( Exception e ) {} 
			}
		}
	}

	public void focusGained(FocusEvent e) {

	}

	public void focusLost(FocusEvent e) {

		String text = textField.getText(); 
		String trimmed = text.trim();
		if (trimmed.length() == 0) { 
			textField.setText( oldVal );
		}

	}
}
