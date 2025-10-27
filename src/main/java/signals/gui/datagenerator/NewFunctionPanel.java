package signals.gui.datagenerator;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import signals.core.Core;
import signals.gui.plot.FunctionOverviewPanel;

@SuppressWarnings("serial")
public abstract class NewFunctionPanel extends JPanel {

	//used for selecting the name of this function
	JTextField nameTextField;  
	
	//used for choosing whether the indices of this function are zero-centered
	JCheckBox zeroCenteredCheckBox; 
	
	//panel that holds the name, dimensions selectors, and options to center indices
	JPanel globalOptionsPanel;
	
	//function overview panel
	FunctionOverviewPanel overviewPanel; 
	
	/**
	 * @param title the title of this dialog
	 */
	public NewFunctionPanel() {
		
		nameTextField = new JTextField( 10 );
		nameTextField.setText("f"+Core.getFunctionCreationOptions().getNumFunctions()); 
		
		nameTextField.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
				textChanged(); 
			}

			public void insertUpdate(DocumentEvent e) {
				textChanged(); 	
			}

			public void removeUpdate(DocumentEvent e) {
				textChanged(); 
			}
			
			
		});

		
		zeroCenteredCheckBox = new JCheckBox( "Zero-Centered Indexing"); 
		zeroCenteredCheckBox.setSelected( Core.getFunctionCreationOptions().isZeroCentered1D() ); 
		
		//layout
		globalOptionsPanel = new JPanel( );
		globalOptionsPanel.setLayout( new BoxLayout( globalOptionsPanel, BoxLayout.LINE_AXIS));
		globalOptionsPanel.add( Box.createHorizontalStrut( 5 ) );
		globalOptionsPanel.add( new JLabel( " Function Name: " ) );
		globalOptionsPanel.add( nameTextField );
		globalOptionsPanel.add( Box.createHorizontalStrut( 20 ) );
		addDimensionSelectors(); 
		globalOptionsPanel.add( zeroCenteredCheckBox );
		globalOptionsPanel.add( Box.createHorizontalStrut( 20 ) );
		globalOptionsPanel.setBorder( Core.getBorders().getEmptyBorder() );

		
		setLayout( new BorderLayout()); 
		add( globalOptionsPanel, BorderLayout.NORTH );
		
	}
	
	/**
	 * Creates a dimension selector with given name, adds it to the panel, and returns it
	 * @param name
	 * @return
	 */
	protected JComboBox addDimensionSelector( String name ) {
		
		JComboBox dimensionSelector = new JComboBox( Core.getFunctionCreationOptions().getAvailableDimensions() ); 
		dimensionSelector.setSelectedIndex( getDefaultDimensionIndex() );
		
		globalOptionsPanel.add( new JLabel( " " + name + ": " ) );
		globalOptionsPanel.add( dimensionSelector );
		globalOptionsPanel.add( Box.createHorizontalStrut( 20 ) );
		
		return dimensionSelector;
	}
	
	protected abstract void textChanged(); 
	
	/**
	 * 
	 * @return the name of primary part A 
	 */
	protected abstract String getPrimaryAName(); 
	
	/**
	 * 
	 * @return the name of primary part B
	 */
	protected abstract String getPrimaryBName(); 
	
	/**
	 * 
	 * @return the imageIcon representing primary part A
	 */
	protected abstract ImageIcon getPrimaryAIcon(); 
	
	/**
	 * 
	 * @return the imageIcon representing primary part B
	 */
	protected abstract ImageIcon getPrimaryBIcon(); 
	
	/**
	 * Subclasses should call addDimensionSelector( name ) for each dimension
	 *
	 */
	protected abstract void addDimensionSelectors(); 

	/**
	 * 
	 * @return the text the user has typed in for the name of this function
	 */
	protected String getNameText() {
		
		return nameTextField.getText();
	}
	
	protected void setNameText( String name ) {
		
		nameTextField.setText( name );
	}
	
	public abstract int getDefaultDimensionIndex(); 
	
	/**
	 * 
	 * @return true if the user wants this function to be zeroCentered
	 */
	protected boolean isZeroCentered() {
		
		return zeroCenteredCheckBox.isSelected();
	}

	/*
	 * (non-Javadoc)
	 * @see signals.gui.DialogTemplate#commitSettings()
	 * Subclasses should call super.commitSettings() in their overrides of this method
	 */
	public boolean commitSettings() {
		
		Core.getFunctionCreationOptions().setZeroCentered1D( isZeroCentered() ); 
		Core.getFunctionCreationOptions().incrementNumFunctions(); 
		return true; 
	}
	
	public void showErrorDialog() {
		
		JOptionPane.showMessageDialog(null,
			    "The function could not be created. \n" +
			    "Check to see that your syntax is correct.",
			    "Error",
			    JOptionPane.ERROR_MESSAGE);

	}

}
