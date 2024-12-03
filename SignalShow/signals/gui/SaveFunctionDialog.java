package signals.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import signals.core.Constants;
import signals.core.Core;
import signals.core.Constants.Part;

@SuppressWarnings("serial")
public abstract class SaveFunctionDialog extends SaveDialog {
	
	protected Part part;  
	ArrayList<JCheckBox> buttons; 
	
	public SaveFunctionDialog(String title, String defaultName) {
		super(Core.getFrame(), title, defaultName);
	}
	
	protected void addPartSelectors() {

		JPanel partPanel = new JPanel(); 
		partPanel.setLayout( new GridLayout( 0, 2 )); 
		buttons = new ArrayList<JCheckBox>(); 
		
		//get output parts used last time
		ArrayList<Part> outputParts = Core.getIOOptions().getOutputParts(); 
		
		for( Part part : Part.values() ) {
			
			String label = Constants.partLabel(part);
			JCheckBox button = new JCheckBox( label ); 
			button.setActionCommand( label );
			buttons.add( button ); 
			partPanel.add( button ); 
			
			if( outputParts.contains(part) ) {
				button.setSelected(true);
			}
		}
		partPanel.setBorder( Core.getBorders().getEtchedBufferBorder() ); 
		contentPanel.add(partPanel, BorderLayout.CENTER);
		
	}
	
	public ArrayList<Part> getSelectedParts() {
		
		ArrayList<Part> checkedParts = new ArrayList<Part>(); 
		
		for( JCheckBox button : buttons ) {
			
			if( button.isSelected() ) {
				
				String label = button.getActionCommand(); 
				for( Part part : Part.values() ) {
					
					if( Constants.partLabel(part).equals( label ) ) checkedParts.add(part);  
				}
				
			}
		}
		
		Core.getIOOptions().setOutputParts(checkedParts); 
		return checkedParts;
	}
	

}
