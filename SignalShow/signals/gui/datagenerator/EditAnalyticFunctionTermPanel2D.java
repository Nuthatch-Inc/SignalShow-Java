package signals.gui.datagenerator;

import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;
import signals.functionterm.AnalyticFunctionTerm2D;
import signals.gui.ParameterEditor;
import signals.gui.SpringUtilities;

@SuppressWarnings("serial")
public class EditAnalyticFunctionTermPanel2D extends EditAnalyticFunctionTermPanel implements EditAnalyticFunctionTerm2DInterface {

	protected AnalyticFunctionTerm2DEditor editor;
	AnalyticFunctionTerm2D currentFunction;

	int x_dimension, y_dimension; 
	boolean zeroCentered;

	public EditAnalyticFunctionTermPanel2D( GUIEventBroadcaster broadcaster, GUIEvent.Descriptor modifiedDescriptor  ) {

		super( broadcaster, modifiedDescriptor );	
		constructorLayout(); 
	}

	public void setIndices( int x_dimension, int y_dimension, boolean zeroCentered ) {

		this.x_dimension = x_dimension; 
		this.y_dimension = y_dimension; 
		this.zeroCentered = zeroCentered;
		editor.setIndices(x_dimension, y_dimension, zeroCentered);

	}

	public void setAmplitude(double newAmplitude) {

		textFields[currentFunction.getAmplitudeIndex()].setText( formatter.format( newAmplitude ) );
	}

	public void setCenterX(double newCenter) {

		textFields[currentFunction.getXCenterIndex()].setText( formatter.format( newCenter ) );
	}

	public void setCenterY(double newCenter) {

		textFields[currentFunction.getYCenterIndex()].setText( formatter.format( newCenter ) );
	}

	public void setWidth(double newWidth) {

		textFields[currentFunction.getWidthIndex()].setText( formatter.format( newWidth ) );
	}

	public void setHeight(double newHeight) {

		textFields[currentFunction.getHeightIndex()].setText( formatter.format( newHeight ) );
	}

	public void setDefaultWidthOff() {

		setDefaultWidth = false; 
	}

	public void setDefaultWidthOn() {

		setDefaultWidth = true; 
		if( currentFunction != null ) {

			currentFunction.setWidth(x_dimension); 
			currentFunction.setHeight(y_dimension);

			if( !zeroCentered ) {

				currentFunction.setCenterX(x_dimension/2); 
				currentFunction.setCenterY(y_dimension/2);
			}

			editor.setFunctionTerm( currentFunction );
			updateDependents();
		}
	}
	
	public void setFunctionTerm(DataGenerator newTerm) {
		
		if( !(newTerm instanceof AnalyticFunctionTerm2D) ) return; 
	
		currentFunction = (AnalyticFunctionTerm2D)newTerm;
		paramPanel.removeAll();
		
		addSelectors(); 
		
		if( setDefaultWidth && currentFunction.hasWidth() ) {
			
			currentFunction.setWidth( Math.sqrt(defaultWidth) );
		}
		
		String[] labels = currentFunction.getTypeModel().getParamNames();
		SpinnerNumberModel[] spinnerModels = currentFunction.getSpinnerModels();
		int numPairs = labels.length;
		textFields = new ParameterEditor[numPairs];
	
		for (int i = 0; i < numPairs; i++) {
			JLabel l = new JLabel(labels[i]+": ", JLabel.TRAILING);
			paramPanel.add(l);
			SpinnerNumberModel model = spinnerModels[i];

			
			textFields[i] = new ParameterEditor( 3, model, i, this ); //3 columns 
			l.setLabelFor(textFields[i]);
			paramPanel.add(textFields[i]);
		}
	
		int numBlank = ROWS*COLS - 2*numPairs;
	
		for (int i = 0; i < numBlank; i++) {
	
			paramPanel.add( Box.createHorizontalStrut( H_SIZE ) );
		}
	
		SpringUtilities.makeCompactGrid(paramPanel,
				ROWS, COLS, 		//rows, cols
				H_SPACE, V_SPACE,        //initX, initY
				H_SPACE, V_SPACE);       //xPad, yPad
	
		paramPanel.revalidate();
		paramPanel.repaint();
		
		setupEditor();
	}

	public void setupEditor() {
		
		refreshEditor();
		editor.setIndices(x_dimension, y_dimension, zeroCentered);
		editor.setFunctionTerm( currentFunction );
	}

	@SuppressWarnings("unchecked")
	public void parameterChanged(int index, String newValue) {

		DataGeneratorTypeModel model = currentFunction.getTypeModel();
		Class[] classes =  model.getParamClasses();

		if( classes[index] == Integer.class ) {

			((DataGenerator)currentFunction).setParameter( Integer.parseInt(newValue), index );

		} else {

			double value = Double.parseDouble(newValue);
			((DataGenerator)currentFunction).setParameter( value, index );
		}

		editor.setFunctionTerm( currentFunction );
		updateDependents();
	}

	@Override
	public void createEditor() {
		
		editor = new InteractiveFunctionTerm2DDisplay(this);
		
	}
	
	public void setDefaultWidth( boolean on ) {
		
		if( on ) {
			
			 setDefaultWidthOn(x_dimension); 
			
		} else setDefaultWidthOff(); 
	}

	@Override
	public void updateDependents() {
		
		if( currentFunction != null ) broadcaster.broadcast(this, modifiedDescriptor, currentFunction ); 
	}

	@Override
	public JComponent getEditor() {

		return (JComponent)editor; 
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