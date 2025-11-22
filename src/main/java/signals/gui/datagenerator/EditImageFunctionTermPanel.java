package signals.gui.datagenerator;

import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;
import signals.functionterm.DataFunctionTerm2D;
import signals.functionterm.ImageFunctionTerm2D;
import signals.gui.ModeSelector;
import signals.gui.ModeUser;
import signals.gui.ParameterEditor;
import signals.gui.SpringUtilities;

@SuppressWarnings("serial")
public class EditImageFunctionTermPanel extends EditAnalyticFunctionTermPanel implements ModeUser, EditAnalyticFunctionTerm2DInterface {

	protected AnalyticFunctionTerm2DEditor editor;
	ImageFunctionTerm2D currentFunction;

	int x_dimension, y_dimension; 
	boolean zeroCentered;
	
	ModeSelector modeSelector; 

	public EditImageFunctionTermPanel( GUIEventBroadcaster broadcaster, GUIEvent.Descriptor modifiedDescriptor  ) {

		super( broadcaster, modifiedDescriptor ); 
		modeSelector = new ModeSelector(this);
	
		constructorLayout(); 
	}

	public void setIndices( int x_dimension, int y_dimension, boolean zeroCentered ) {

		this.x_dimension = x_dimension; 
		this.y_dimension = y_dimension; 
		this.zeroCentered = zeroCentered;
		editor.setIndices(x_dimension, y_dimension, zeroCentered);

	}

	/* (non-Javadoc)
	 * @see signals.gui.datagenerator.EditAnalyticFunctionTerm2DInterface#setAmplitude(double)
	 */
	public void setAmplitude(double newAmplitude) {

		textFields[currentFunction.getAmplitudeIndex()].setText( formatter.format( newAmplitude ) );
	}

	/* (non-Javadoc)
	 * @see signals.gui.datagenerator.EditAnalyticFunctionTerm2DInterface#setCenterX(double)
	 */
	public void setCenterX(double newCenter) {

		textFields[currentFunction.getXCenterIndex()].setText( formatter.format( newCenter ) );
	}

	/* (non-Javadoc)
	 * @see signals.gui.datagenerator.EditAnalyticFunctionTerm2DInterface#setCenterY(double)
	 */
	public void setCenterY(double newCenter) {

		textFields[currentFunction.getYCenterIndex()].setText( formatter.format( newCenter ) );
	}

	/* (non-Javadoc)
	 * @see signals.gui.datagenerator.EditAnalyticFunctionTerm2DInterface#setWidth(double)
	 */
	public void setWidth(double newWidth) {

		textFields[currentFunction.getWidthIndex()].setText( formatter.format( newWidth ) );
	}

	/* (non-Javadoc)
	 * @see signals.gui.datagenerator.EditAnalyticFunctionTerm2DInterface#setHeight(double)
	 */
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
		
		if( !(newTerm instanceof ImageFunctionTerm2D) ) return; 
		boolean needsetup = ( currentFunction != null && currentFunction.getClass() != newTerm.getClass() ); 

		currentFunction = (ImageFunctionTerm2D)newTerm;
		paramPanel.removeAll();

		//add mode selector
		JLabel modeLabel = new JLabel("mode: ", JLabel.TRAILING);
		paramPanel.add(modeLabel);
		modeLabel.setLabelFor(modeSelector);
		paramPanel.add(modeSelector);
		modeSelector.setSelectedIndex(currentFunction.getMode());

		if( setDefaultWidth ) {

			currentFunction.setWidth(x_dimension); 
			currentFunction.setHeight(y_dimension);

			if( !zeroCentered ) {

				currentFunction.setCenterX(x_dimension/2); 
				currentFunction.setCenterY(y_dimension/2);
			}
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

		if( needsetup ) setupEditor();
	}

	public void setupEditor() {
		
		if( currentFunction instanceof DataFunctionTerm2D ) {
			
			editor = new DataFunctionTerm2DEditor(this);
			
		} else {
			
			editor = new InteractiveFunctionTerm2DDisplay(this);
		}
		
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

	public void setMode(int mode) {
		currentFunction.setMode( mode ); 
		updateDependents();
		if( !(currentFunction instanceof DataFunctionTerm2D) || ((DataFunctionTerm2D)currentFunction).hasData() ) {
			editor.setFunctionTerm( currentFunction );
		}
	}

	@Override
	public void createEditor() {
		
		editor = new InteractiveFunctionTerm2DDisplay(this);
		
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