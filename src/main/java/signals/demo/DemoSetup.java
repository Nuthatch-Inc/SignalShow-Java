package signals.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import signals.core.Core;
import signals.core.Function;
import signals.core.FunctionProducer;
import signals.gui.DialogTemplate;
import signals.gui.GUIDimensions;
import signals.gui.VerticalThumbnailList;

@SuppressWarnings("serial")
public abstract class DemoSetup extends DialogTemplate {

	protected VerticalThumbnailList functionSelector;
	
	public DemoSetup( String title ) {
		
		super(Core.getGUI().getFrame(), title );
		
		functionSelector = getVariableList(); 

		Dimension selectorSize = new Dimension( GUIDimensions.LIST_WIDTH + 25, 300 ); 
	
		
		//first selector
		JScrollPane sp = new JScrollPane( functionSelector ); 
		sp.setPreferredSize(selectorSize); 
		JPanel selectorPanel = new JPanel( new BorderLayout() ); 
		selectorPanel.add( sp, BorderLayout.CENTER ); 
		selectorPanel.add( new JLabel("Input Function:"), BorderLayout.NORTH); 
		
		addContentPanel(selectorPanel, BorderLayout.CENTER); 
	}
	
	public abstract VerticalThumbnailList getVariableList(); 

	public abstract void setupDemo( Function function ); 

	@Override
	public boolean commitSettings() {
	
		setupDemo( ((FunctionProducer) functionSelector.getSelectedItem()).getFunction() ); 
		
		return true; 
	}

}