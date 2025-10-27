package signals.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import signals.core.Core;
import signals.io.Utils;
import signals.io.ImageWriter.ImageType;

@SuppressWarnings("serial")
public abstract class SaveDialog extends DialogTemplate {

	File selectedFile;
	JTextField filePathField;
	JButton fileButton;
	protected JPanel contentPanel;
	protected JPanel pathPanel;

	public SaveDialog(JFrame frame, String title, String defaultName) {
		super(frame, title);
		
		pathPanel = new JPanel();
		pathPanel.setBorder(Core.getBorders().getBuffer());
		contentPanel = new JPanel( new BorderLayout() );  
		upperPanel.add(pathPanel, BorderLayout.NORTH);
		addContentPanel( contentPanel, BorderLayout.CENTER );
		
		File cd = Core.getIOOptions().getCurrentDirectory(); 
		JFileChooser fc = ( cd == null ) ? new JFileChooser() : new JFileChooser( cd );
		cd = fc.getCurrentDirectory(); 
		final String defaultFilename = cd.getPath() + '/'+ defaultName;
		
		filePathField = new JTextField( defaultFilename, 30 ); 
		fileButton = new JButton(IconCache.getIcon("/guiIcons/open.png")); 
		fileButton.addActionListener( new ActionListener() {
	
			public void actionPerformed(ActionEvent arg0) {
				
				File cd = Core.getIOOptions().getCurrentDirectory(); 
				JFileChooser fc = ( cd == null ) ? new JFileChooser(): new JFileChooser( cd );
				cd = fc.getCurrentDirectory(); 
				fc.setSelectedFile(new File(filePathField.getText())); 
				
				int returnVal = fc.showSaveDialog(Core.getFrame());
				if( returnVal == JFileChooser.APPROVE_OPTION ) {
				
					selectedFile = fc.getSelectedFile();
				    cd = fc.getCurrentDirectory(); 
		            if( cd != null ) Core.getIOOptions().setCurrentDirectory( cd );
		            		   
		            String name = selectedFile.getPath();
		            filePathField.setText(name); 
		       
				}
				
			}
			
		} ); 
		
		JPanel filenamePanel = new JPanel(new BorderLayout()); 
		filenamePanel.add( new JLabel("Base Filename: "), BorderLayout.WEST ); 
		filenamePanel.add( filePathField, BorderLayout.CENTER ); 
		filenamePanel.add( fileButton, BorderLayout.EAST ); 
		pathPanel.add(filenamePanel); 

	}

	public File getSelectedFile(String Partname, String extension) {
	
		if( Partname.length() >= 1 ) Partname = "_" + Partname; 
		String name = Utils.getNameNoExtension(filePathField.getText()) + Partname + extension;
		return new File( name );
	}
	
	public void setSelectedFile( String text ) {
		filePathField.setText( text );
	}
	
	public abstract ImageType getDefaultType(); 
}