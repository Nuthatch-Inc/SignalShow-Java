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
	
	public void GUIEventOccurred(GUIEvent e) {}
	
}
