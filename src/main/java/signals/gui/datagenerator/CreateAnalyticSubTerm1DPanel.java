package signals.gui.datagenerator;


import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import signals.gui.SpringUtilities;


@SuppressWarnings("serial")
public class CreateAnalyticSubTerm1DPanel extends CreateFunctionTerm1DPanel {
	
	/**
	 * @param termPanel2D
	 */
	public CreateAnalyticSubTerm1DPanel(GUIEventBroadcaster broadcaster, GUIEvent.Descriptor modifiedDescriptor, boolean editable ) {
		super( broadcaster, modifiedDescriptor, editable );

	}
	
	public void show( boolean show_content ) {}
	
	public void constructorLayout() {

		setLayout( new SpringLayout() ); 
		add( editable ? selector : new JScrollPane(docPane) );
		add( getEditor() );
		SpringUtilities.makeCompactGrid(this, //parent
                1, 2,
                0, 0,  //initX, initY
                0, 0); //xPad, yPad
	}
	
	public void setIndices( double[] indices ) {
		
		editor.setIndices(indices); 
	}
	
	/**
	 * Sets the default width dimension for calculating the width parameter.
	 * This is separate from setIndices() to support polar coordinates where
	 * radial indices are 0 to dimension/2 but width should be based on full dimension.
	 * @param dimension the full dimension to use for width calculation (e.g., 256)
	 */
	public void setDefaultWidthDimension( int dimension ) {
		
		((EditAnalyticFunctionTerm1DPanel)editor).setDefaultWidthDimension( dimension );
	}
	
	public void GUIEventOccurred(GUIEvent e) {}
	
}
