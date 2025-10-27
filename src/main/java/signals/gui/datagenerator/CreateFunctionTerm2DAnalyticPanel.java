package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;

import signals.core.Core;
import signals.core.DataGenerator;
import signals.core.FunctionTerm2D;
import signals.functionterm.AnalyticFunctionTerm2D;


@SuppressWarnings("serial")
public class CreateFunctionTerm2DAnalyticPanel extends CreateDataGeneratorPanel implements CreateDataGeneratorInterface {
	
	EditAnalyticFunctionTermPanel2D editor;
	
	JPanel displayPanel; 

	public CreateFunctionTerm2DAnalyticPanel( GUIEventBroadcaster broadcaster, boolean editable ) {

		super( broadcaster, editable );
	}

	public void updateEditor() {
		
		if( currentDataGenerator != null ) {
			editor.setFunctionTerm((AnalyticFunctionTerm2D)currentDataGenerator);
		}
	}

	public FunctionTerm2D getFunctionTerm2D() {

		return (FunctionTerm2D)currentDataGenerator;
	}

	@Override
	public void createEditor() {
		editor = new EditAnalyticFunctionTermPanel2D( broadcaster, modifiedDescriptor );
	}

	@Override
	public ArrayList<Class< ? extends DataGenerator>> getDataGeneratorList() {
		return Core.getDataGeneratorCollections().getAnalytic2DList();
	}

	@Override
	public HashMap<String, ArrayList<Class< ? extends DataGenerator>>> getDataGeneratorMap() {
		return Core.getDataGeneratorCollections().getAnalytic2DMap(); 
	}

	@Override
	public void setDefaultWidth(boolean on) {
		
		editor.setDefaultWidth(on); 
		
	}
	
	@Override
	public void setDataGenerator(DataGenerator updatedTerm) {
		if( updatedTerm instanceof AnalyticFunctionTerm2D )
		super.setDataGenerator(updatedTerm);
	}

	@Override
	public JComponent getEditor() {
		
		return (JComponent)editor;
	}

	@Override
	public void constructorLayout() {
		
		setLayout( new BorderLayout() ); 
		add( selector, BorderLayout.WEST );
		add( getEditor(), BorderLayout.CENTER );
		
	}

	public void GUIEventOccurred(GUIEvent e) {
		// TODO Auto-generated method stub
		
	}
}
