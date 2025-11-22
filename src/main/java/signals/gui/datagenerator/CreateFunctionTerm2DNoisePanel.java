package signals.gui.datagenerator;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import signals.core.Core;
import signals.core.DataGenerator;
import signals.core.FunctionTerm2D;
import signals.functionterm.NoiseFunctionTerm2D;
import signals.gui.SpringUtilities;


@SuppressWarnings("serial")
public class CreateFunctionTerm2DNoisePanel extends CreateDataGeneratorPanel implements CreateDataGeneratorInterface {
	
	EditNoiseFunctionTerm2DPanel editor; 
	
	JPanel displayPanel; 
	
	public CreateFunctionTerm2DNoisePanel( GUIEventBroadcaster broadcaster, boolean editable ) {

		super( broadcaster, editable );
	}

	public void updateEditor() {
		
		if( currentDataGenerator != null ) {
			editor.setFunctionTerm(currentDataGenerator);
		}
	}

	@Override
	public void setDataGenerator(DataGenerator updatedTerm) {
		if( updatedTerm instanceof NoiseFunctionTerm2D)
		super.setDataGenerator(updatedTerm);
	}

	public FunctionTerm2D getFunctionTerm2D() {

		return (FunctionTerm2D)currentDataGenerator;
	}

	@Override
	public void createEditor() {
		editor = new EditNoiseFunctionTerm2DPanel( broadcaster, modifiedDescriptor );
	}

	@Override
	public ArrayList<Class< ? extends DataGenerator>> getDataGeneratorList() {
		return Core.getDataGeneratorCollections().getNoise2DList();
	}

	@Override
	public HashMap<String, ArrayList<Class< ? extends DataGenerator>>> getDataGeneratorMap() {
		return Core.getDataGeneratorCollections().getNoise2DMap(); 
	}

	@Override
	public void setDefaultWidth(boolean on) {
		//does nothing
	}
	
	@Override
	public JComponent getEditor() {
	
		return editor;
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

	public void GUIEventOccurred(GUIEvent e) {}

}
