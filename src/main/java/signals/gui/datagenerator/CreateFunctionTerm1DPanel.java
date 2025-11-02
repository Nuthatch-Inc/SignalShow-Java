package signals.gui.datagenerator;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import signals.core.Core;
import signals.core.DataGenerator;
import signals.core.FunctionTerm1D;
import signals.functionterm.AnalyticFunctionTerm1D;
import signals.gui.SpringUtilities;

/**
 * 
 * @author julietbernstein
 *
 * subclass of CreateFunctionTermPanel for 1D functions
 *
 *
 */
@SuppressWarnings("serial")
public class CreateFunctionTerm1DPanel extends CreateDataGeneratorPanel {

	protected EditAnalyticFunctionTerm1DPanel editor; 

	protected CardLayout layout;
	protected static final String CONTENT_PANEL = "content";
	protected static final String BLANK_PANEL = "blank";
	protected JPanel contentPanel; 

	public CreateFunctionTerm1DPanel(GUIEventBroadcaster broadcaster, boolean editable ) {
		super(broadcaster, editable);
	}

	public CreateFunctionTerm1DPanel(GUIEventBroadcaster broadcaster, GUIEvent.Descriptor modifiedDescriptor, boolean editable ) {
		super(broadcaster, modifiedDescriptor, editable);
	}

	public void createEditor() {

		editor = new EditAnalyticFunctionTerm1DPanel( broadcaster, modifiedDescriptor ); 
	}

	public ArrayList<Class< ? extends DataGenerator>> getDataGeneratorList() {

		return Core.getDataGeneratorCollections().getFunctionTerm1DList();
	}

	public HashMap<String, ArrayList<Class< ? extends DataGenerator>>> getDataGeneratorMap() {

		return Core.getDataGeneratorCollections().getFunctionTerm1DMap(); 
	}

	@Override
	public void setDefaultWidth(boolean on) {

		editor.setDefaultWidth( on ); 
	}

	public void setDataGenerator( DataGenerator updatedTerm ) {

		if( updatedTerm instanceof FunctionTerm1D )
			super.setDataGenerator(updatedTerm); 
	}

	public void updateEditor() {

		editor.setFunctionTerm((AnalyticFunctionTerm1D) currentDataGenerator);
	}

	public void showContent(boolean show_content) {

		layout.show(this, (show_content ? CONTENT_PANEL : BLANK_PANEL ) );
	}

	public void constructorLayout() {

		layout = new CardLayout();
		setLayout( layout );

		JPanel blankPanel = new JPanel(); 
		contentPanel = new JPanel(); 

		add( blankPanel, BLANK_PANEL );


		contentPanel.setLayout( new SpringLayout() ); 
		contentPanel.add( editable ? selector : docWrapperPanel );
		contentPanel.add( getEditor() );
		SpringUtilities.makeCompactGrid(contentPanel, //parent
				1, 2,
				0, 0,  //initX, initY
				0, 10); //xPad, yPad

		add( contentPanel, CONTENT_PANEL );



	}

	@Override
	public JComponent getEditor() {

		return editor; 
	}

	public void GUIEventOccurred(GUIEvent e) {

		if( e.getSource().equals(this) ) return; 

		switch( e.getDescriptor() ) {

		case NEW_SELECTED: //comes from calculator

			setListNew( (DataGenerator)(e.getValue(0)));
			showContent( true ); 

			break; 

		case EXISTING_SELECTED: //comes from calculator

			setListExisting( (DataGenerator)(e.getValue(0)));
			showContent( true ); 

			break; 

		case NON_SELECTED: //comes from calculator

			showContent( false ); 

			break; 

		}

	}
}