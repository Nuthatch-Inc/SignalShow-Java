package signals.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class ModeSelector extends JComboBox {
	
	public static final String[] modes = { "crop", "scale and zero-pad" }; 

	ModeUser modeUser;
	
	public ModeSelector( ModeUser mu ) {
		
		super( modes );
		
		this.modeUser = mu;
		
		addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				int idx = getSelectedIndex(); 
				modeUser.setMode(idx);
			}
			
			
		});
	}
}
