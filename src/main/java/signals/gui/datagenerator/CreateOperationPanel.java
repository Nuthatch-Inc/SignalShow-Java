package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;

import signals.core.DataGenerator;
import signals.core.Operation;
import signals.gui.operation.OperationOptionsPanel;

@SuppressWarnings("serial")
public abstract class CreateOperationPanel extends CreateDataGeneratorPanel {
	
	OperationOptionsPanel editor; 
	JPanel editorHolder; 
	OperationPreviewPanel previewPanel; // Preview panel for operation output

	public CreateOperationPanel(GUIEventBroadcaster broadcaster ) {
		
		this( broadcaster, true ); 
	}
	
	public CreateOperationPanel(GUIEventBroadcaster broadcaster, boolean editable ) {
		super(broadcaster, editable);
		setOpaque( true );
	}

	@Override
	public void constructorLayout() {
		
		setLayout( new BorderLayout() );
		add( selector, BorderLayout.WEST );
		
		// Wrap editorHolder to constrain its width
		editorHolder = new JPanel();
		JPanel editorWrapper = new JPanel(new BorderLayout()) {
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.width = Math.min(d.width, 300);
				return d;
			}
			
			@Override
			public Dimension getMaximumSize() {
				return new Dimension(300, Integer.MAX_VALUE);
			}
		};
		editorWrapper.add(editorHolder, BorderLayout.CENTER);
		add( editorWrapper, BorderLayout.CENTER );
	}

	@Override
	public void setDataGenerator(DataGenerator updatedTerm) {
		if(updatedTerm instanceof Operation)  
		super.setDataGenerator(updatedTerm);
		updateEditor();
		updatePreview();
	}

	@Override
	public ArrayList<Class<? extends DataGenerator>> getDataGeneratorList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, ArrayList<Class< ? extends DataGenerator>>> getDataGeneratorMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setListNew(DataGenerator updatedTerm) {
		super.setListExisting(updatedTerm);
	}

	@Override
	public void createEditor() {
		
		editor = new OperationOptionsPanel( null );  

	}

	@Override
	public JComponent getEditor() {
		return editor; 
	}

	@Override
	public void updateEditor() {
		
		editorHolder.removeAll(); 
		editor = ((Operation)currentDataGenerator).getOptionsInterface(); 
		editor.setBroadCaster(broadcaster); 
		editorHolder.add(editor); 
		editorHolder.revalidate(); 
		editorHolder.repaint();

	}

	public void GUIEventOccurred(GUIEvent e) {
		// do nothing
	}
	
	@Override
	public void setDefaultWidth(boolean on) {
		//Do nothing
	}
	
	/**
	 * Updates the operation preview panel if one is set
	 */
	protected void updatePreview() {
		if (previewPanel == null || currentDataGenerator == null) {
			return;
		}
		
		// For now, we'll just clear the preview
		// To show actual output, we'd need input functions which aren't available here
		previewPanel.clearPreview();
	}

}
