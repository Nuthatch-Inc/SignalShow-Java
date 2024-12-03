package signals.gui.datagenerator;

import java.util.ArrayList;

import javax.swing.JComponent;

import signals.core.DataGenerator;
import signals.functionterm.AnalyticFunctionTerm;
import signals.functionterm.NoiseFunctionTerm2D;


@SuppressWarnings("serial")
public class EditNoiseFunctionTerm2DPanel extends EditAnalyticFunctionTermPanel {

	 int x_dimension; 
	 int y_dimension;
	 boolean zeroCentered;
	 
	 Noise2DFunctionTermEditor editor; 
	
	public EditNoiseFunctionTerm2DPanel( GUIEventBroadcaster broadcaster, GUIEvent.Descriptor modifiedDescriptor ) {

		super( broadcaster, modifiedDescriptor );
		constructorLayout(); 
	}
	
	public void setFunctionTerm(DataGenerator newTerm) {
		
		if( !(newTerm instanceof NoiseFunctionTerm2D) ) return; 
		
		super.setFunctionTerm(newTerm); 
		((Noise2DFunctionTermEditor)editor).setFunctionTerm((AnalyticFunctionTerm)newTerm);
		((Noise2DFunctionTermEditor)editor).setIndices( x_dimension, y_dimension, zeroCentered );
	}

	public void setIndices( int x_dimension, int y_dimension, boolean zeroCentered ) {
		
		this.x_dimension = x_dimension; 
		this.y_dimension = y_dimension; 
		this.zeroCentered = zeroCentered;
		((Noise2DFunctionTermEditor)editor).setIndices( x_dimension, y_dimension, zeroCentered );
	}

	@Override
	public void setupEditor() {}

	@Override
	public void createEditor() {
		editor = new Noise2DFunctionTermEditor( broadcaster, modifiedDescriptor, this );
	}

	@Override
	public JComponent getEditor() {
		return editor; 
	}
	
	public void GUIEventOccurred(GUIEvent e) {

		if( e.getSource().equals(this) ) return; 

		switch( e.getDescriptor() ) {

		case INDICES_MODIFIED_2D: 
			
			ArrayList<Object> values = e.getValues(); 
			setIndices( (Integer)values.get(0), (Integer)values.get(1), (Boolean)values.get(2) );

			break; 
			
		}

	}

}
