package signals.gui;
import javax.swing.border.*;
import javax.swing.*;

import signals.core.Core;

import java.awt.*;
import java.awt.event.*;

/**
* This class represents a dialog that asks the user to select various qualities of a plot
*/ 
@SuppressWarnings("serial")
public abstract class DialogTemplate extends JDialog { 
	
	
	//the upper panel holds the dialog-specific content. 
	// the lower panel holds the OK and Cancel buttons, 
	// as well as buttons like Next and Prev if desired
	protected JPanel upperPanel; 
	protected JPanel lowerPanel; 

	protected JFrame frame;

	protected Border defaultBorder; 
	
	protected OKAction okAction; 
	
	/**
	* Default Constructor
	*/ 
	public DialogTemplate( JFrame frame, String title ) {
		 
		super( frame, title, true );
		this.frame = frame;
		
		//setIconImage(Core.getIconSets().getSmallFrameIcon().getImage());
		
		// make sure that closing this dialog does not close down the entire program
		 setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		
		//set up the JPanel 
		JPanel content = new JPanel( new BorderLayout() ); 
		
		upperPanel = new JPanel( new BorderLayout() );
		lowerPanel = new JPanel(); 
		
		okAction = new OKAction(); 
		lowerPanel.add( new JButton( new CancelAction() ) ); 
		lowerPanel.add( new JButton( okAction ) ); 
		
		content.add( upperPanel, BorderLayout.CENTER );
		content.add( lowerPanel, BorderLayout.SOUTH );
		
		defaultBorder = Core.getBorders().getEtchedBufferBorder();
	
		setContentPane( content );
		
	} 
	
	public void enableSaveButton( boolean TF ) {
		
		okAction.setEnabled(TF);
	}
	
	
	public void display() {
		
		pack();
		setLocationRelativeTo( frame );
		setVisible( true );
	}
	
	/**
	 * Makes the main content panel this panel
	 * @param panel
	 */
	protected void addContentPanel( JComponent panel, String borderLayout ) {
		
		upperPanel.add(panel, borderLayout);
		
	}
	
	//returns true if everything goes well and the window should close
	abstract public boolean commitSettings();
	public void cancelSettings() {}; 
	
	/** 
	* Represents the action for selecting a color
	* 
	*/ 
	public class CancelAction extends AbstractAction {
		
		public CancelAction() {
			
			super( "Cancel" );
			
		}

		public void actionPerformed( ActionEvent e ) {
			
			cancelSettings();
			dispose();
		}
		
	}
	
	/** 
	* Represents the action for pressing the ok button
	* 
	*/ 
	public class OKAction extends AbstractAction {
		
		public OKAction() {
		
			super( "OK" );
			putValue( MNEMONIC_KEY, KeyEvent.VK_ENTER );
			
		}

		public void actionPerformed( ActionEvent e ) {
			
			//set the settings
			boolean result = commitSettings();  
			
			// get rid of this window
			if( result ) { 
				
				dispose(); 
			}
		}
		
	}
	
} 
	