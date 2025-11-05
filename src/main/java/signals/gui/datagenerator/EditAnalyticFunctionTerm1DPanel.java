package signals.gui.datagenerator;

import javax.swing.JComponent;
import javax.swing.JLabel;

import signals.core.DataGenerator;
import signals.functionterm.AnalyticFunctionTerm;
import signals.functionterm.DataFunctionTerm1D;
import signals.functionterm.NoiseFunctionTerm1D;
import signals.gui.ModeSelector;
import signals.gui.ModeUser;


@SuppressWarnings("serial")
public class EditAnalyticFunctionTerm1DPanel extends EditAnalyticFunctionTermPanel implements ModeUser {

	double[] indices;
	ModeSelector modeSelector; 
	protected AnalyticFunctionTerm1DEditor editor;

	public EditAnalyticFunctionTerm1DPanel( GUIEventBroadcaster broadcaster, GUIEvent.Descriptor modifiedDescriptor ) {

		super( broadcaster, modifiedDescriptor );
		
		modeSelector = new ModeSelector(this);
		constructorLayout(); 
	}

	public void setFunctionTerm(DataGenerator newTerm) {

		super.setFunctionTerm(newTerm); 
		editor.setIndices( indices );
		editor.setFunctionTerm((AnalyticFunctionTerm)newTerm); 
	}

	public void setIndices( double[] indices ) {

		this.indices = indices;
		editor.setIndices( indices );
	}
	
	/**
	 * Sets the default width dimension for calculating the width parameter.
	 * This is separate from setIndices() to support polar coordinates where
	 * radial indices are 0 to dimension/2 but width should be based on full dimension.
	 * @param dimension the full dimension to use for width calculation (e.g., 256)
	 */
	public void setDefaultWidthDimension( int dimension ) {
		
		setDefaultWidthOn( dimension );
	}

	public void addSelectors() {

		if( currentFunction instanceof DataFunctionTerm1D ) {

			//add mode selector
			JLabel modeLabel = new JLabel("mode: ", JLabel.TRAILING);
			paramPanel.add(modeLabel);
			modeLabel.setLabelFor(modeSelector);
			paramPanel.add(modeSelector);
			modeSelector.setSelectedIndex(((DataFunctionTerm1D)currentFunction).getMode());

		}
	}

	@Override
	public void setupEditor() {
	
		if( currentFunction instanceof NoiseFunctionTerm1D ) {

			editor = new Noise1DFunctionTermEditor( broadcaster, modifiedDescriptor, this );

		} else if ( currentFunction instanceof DataFunctionTerm1D ) {

			editor = new DataFunctionTerm1DEditor(broadcaster, modifiedDescriptor, this); 

		} else {

			editor = new AnalyticInteractivePlot(broadcaster, modifiedDescriptor, this); 

		}

		editor.setFunctionTerm( currentFunction );
		refreshEditor();

	}

	public void setMode(int mode) {
		if( currentFunction instanceof DataFunctionTerm1D ) {
			((DataFunctionTerm1D)currentFunction).setMode( mode ); 
			editor.setFunctionTerm( currentFunction );
			updateDependents();
		}

	}

	public void setDefaultWidth( boolean on ) {
		
		if( on ) {
			
			if( indices != null ) setDefaultWidthOn(indices.length); 
			
		} else setDefaultWidthOff(); 
	}
	
	public void GUIEventOccurred(GUIEvent e) {

		if( e.getSource().equals(this) ) return; 

		switch( e.getDescriptor() ) {

		case INDICES_MODIFIED_1D: 
			 
			setIndices( (double[])(e.getValue(0)));

			break; 
			
		}

	}

	@Override
	public JComponent getEditor() {

		return editor;
	}

	@Override
	public void createEditor() {
		
		editor = new AnalyticInteractivePlot( broadcaster, modifiedDescriptor, this ); 
		
	}

}
