package signals.gui.datagenerator;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import signals.core.Core;
import signals.core.DataGenerator;
import signals.core.FunctionTerm2D;
import signals.functionterm.ImageFunctionTerm2D;
import signals.gui.SpringUtilities;


@SuppressWarnings("serial")
public class CreateFunctionTerm2DImagePanel extends CreateDataGeneratorPanel implements CreateDataGeneratorInterface {
	
	EditImageFunctionTermPanel editor;
	
	JPanel displayPanel; 

	public CreateFunctionTerm2DImagePanel( GUIEventBroadcaster broadcaster, boolean editable ) {

		super( broadcaster, editable );
	}

	public void updateEditor() {
		
		if( currentDataGenerator != null ) {
			editor.setFunctionTerm((ImageFunctionTerm2D)currentDataGenerator);
		}
	}

	public FunctionTerm2D getFunctionTerm2D() {

		return (FunctionTerm2D)currentDataGenerator;
	}

	@Override
	public void createEditor() {
		editor = new EditImageFunctionTermPanel( broadcaster, modifiedDescriptor );
	}

	@Override
	public ArrayList<Class< ? extends DataGenerator>> getDataGeneratorList() {
		return Core.getDataGeneratorCollections().getImage2DList();
	}

	@Override
	public HashMap<String, ArrayList<Class< ? extends DataGenerator>>> getDataGeneratorMap() {
		return Core.getDataGeneratorCollections().getImage2DMap(); 
	}

	@Override
	public void setDefaultWidth(boolean on) {
		
		if( on ) {
			
			editor.setDefaultWidthOn(); 
			
		} else editor.setDefaultWidthOff(); 
		
	}
	
	@Override
	public void setDataGenerator(DataGenerator updatedTerm) {
		if( updatedTerm instanceof ImageFunctionTerm2D )
		super.setDataGenerator(updatedTerm);
	}

	@Override
	public JComponent getEditor() {
		
		return (JComponent)editor;
	}

	@Override
	public void constructorLayout() {
		
		setLayout( new SpringLayout() ); 
		add( selector );
		add( getEditor() );
		SpringUtilities.makeCompactGrid(this, //parent
                1, 2,
                0, 0,  //initX, initY
                0, 10); //xPad, yPad
		
	}

	public void GUIEventOccurred(GUIEvent e) {
		// TODO Auto-generated method stub
		
	}

}
