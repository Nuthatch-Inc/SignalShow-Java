package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import signals.core.Core;
import signals.core.DataGenerator;
import signals.core.StringProcess;
import signals.gui.IconCache;
import signals.gui.TextList;
import signals.gui.TextListItem;
import signals.io.Browser;
import signals.io.ResourceLoader;

/**
 * A panel that allows the user to select a function generator
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public class DataGeneratorSelectorPanel extends JPanel {

	int activeIndex; 

	//textbox where the user can type in a class
	JTextField textField; 

	//pane with help documents
	JEditorPane docPane; 

	//list displaying the available classes
	TextList list; 

	//maps strings and substrings to arraylists of functions that can be added to a list
	HashMap<String, ArrayList<Class< ? extends DataGenerator>>> map; 

	//true if the list is currently being adjusted 
	boolean adjusting; 

	//true if the text field is being adjusted
	boolean textFieldAdjusting;

	//true if the list is being updated programmatically
	boolean programmaticAdjusting; 
	
	boolean initialized; 

	ArrayList<Class< ? extends DataGenerator>> datageneratorList; 
	
	JPanel previewPanel; // Optional preview panel (e.g., for operation output plots)

	public DataGeneratorSelectorPanel( ArrayList<Class< ? extends DataGenerator>> datageneratorList, 
			final HashMap<String, ArrayList<Class< ? extends DataGenerator>>> map ) {

		adjusting = true; 
		activeIndex = -1; //either the currently selected index OR -1 if the selected index should change

		this.datageneratorList = datageneratorList;
		this.map = map;

		//text field
		textField = new JTextField(); 
		textField.getDocument().addDocumentListener( new DocumentListener() {

			public void update() {

				if( !textFieldAdjusting ) {

					programmaticAdjusting = true; 

					String text = textField.getText().toLowerCase();
					if( text.trim().length() == 0) displayAll(); 
					else { 

						ArrayList<Class< ? extends DataGenerator>> retrieved_list = map.get( text ); 
						adjusting = true; 
						list.removeAllItems();
						if( retrieved_list != null ) {

							addTypes( retrieved_list );
						}
						list.getSelectionModel().clearSelection();
						activeIndex = -1;
						adjusting = false; 
						selectFirst();//select the first element in the list
					}

					programmaticAdjusting = false;
				}
			}

			public void changedUpdate(DocumentEvent e) {
				update(); 
			}

			public void insertUpdate(DocumentEvent e) {
				update();
			}

			public void removeUpdate(DocumentEvent e) {
				update();
			}

		});

		textField.getInputMap( JComponent.WHEN_FOCUSED ).put(KeyStroke.getKeyStroke("UP"), "up");
		textField.getActionMap().put("up", new AbstractAction() {

			public void actionPerformed(ActionEvent e) {

				list.selectPrev();  
			}

		});

		textField.getInputMap( JComponent.WHEN_FOCUSED ).put(KeyStroke.getKeyStroke("DOWN"), "down");
		textField.getActionMap().put("down", new AbstractAction() {

			public void actionPerformed(ActionEvent e) {

				list.selectNext();

			}

		});

		textField.addFocusListener( new FocusListener() {

			public void focusGained(FocusEvent e) {

				textField.selectAll(); 

			}

			public void focusLost(FocusEvent e) {}

		});

		//list
		list = new TextList( new DefaultListCellRenderer() );   
		addTypes( datageneratorList );
		//selectFirst();
		//adjusting = false;

		//search icon
		JLabel searchLabel = new JLabel(IconCache.getIcon("/guiIcons/search.png")); 

		//doc pane
		docPane = new JEditorPane();
		docPane.setEditable(false);
		docPane.setBorder(Core.getBorders().getBuffer()); 

		docPane.addHyperlinkListener(new HyperlinkListener() {

			public void hyperlinkUpdate(HyperlinkEvent e) {

				if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
					Browser.showInBrowser( e.getURL().toString() ); 

			}

		}); 


		list.addListSelectionListener( new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {

				if( e.getValueIsAdjusting() ) return; 
				if( list.getSelectedItem() != null && !adjusting ) {
					ResourceLoader.loadPage( docPane, getSelectedItem().getTypeModel().getDocPath() );
				}
			}

		}); 

		//Put the editor pane in a scroll pane.
		JScrollPane editorScrollPane = new JScrollPane(docPane);
		editorScrollPane.setBorder( Core.getBorders().getLineBufferBorder() );
		editorScrollPane.setBackground( new Color( 0, 0, 0, 0) );
		
		// Wrap scroll pane in fixed-width panel to prevent width changes when scrollbar appears
		JPanel docWrapperPanel = new JPanel(new BorderLayout()) {
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.width = 450;
				return d;
			}
			@Override
			public Dimension getMinimumSize() {
				Dimension d = super.getMinimumSize();
				d.width = 450;
				return d;
			}
			@Override
			public Dimension getMaximumSize() {
				return new Dimension(450, Integer.MAX_VALUE);
			}
		};
		docWrapperPanel.add(editorScrollPane, BorderLayout.CENTER);

		//layout
		setLayout( new BorderLayout() );

		textField.setPreferredSize(new Dimension( 110, 20));
		textField.setMinimumSize(new Dimension( 110, 20));
		textField.setMaximumSize(new Dimension( 110, 20));

		JPanel textPanel = new JPanel(); 
		textPanel.setLayout( new BorderLayout() ); 
		textPanel.setBorder(textField.getBorder()); 
		textField.setBorder(null); 
		textPanel.add( textField, BorderLayout.CENTER ); 
		textPanel.add( searchLabel, BorderLayout.WEST); 
		searchLabel.setBorder( Core.getBorders().getBuffer() );
		textPanel.setBackground( Color.white );

		JScrollPane scrollPane = new JScrollPane( list, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER  );

		JPanel typePanel = new JPanel(); 
		typePanel.setLayout( new BorderLayout() ); 
		typePanel.add(scrollPane, BorderLayout.CENTER);
		typePanel.add(textPanel, BorderLayout.NORTH); 
		typePanel.setBorder(Core.getBorders().getBuffer()); 

		add( typePanel, BorderLayout.WEST ); 
		add( docWrapperPanel, BorderLayout.CENTER ); 
		
		adjusting = false; 
		initialized = true;

	}
	
	/**
	 * Sets an optional preview panel to display on the right side of the selector.
	 * Useful for showing operation output plots or other previews.
	 * @param panel The panel to display, or null to remove the preview
	 */
	public void setPreviewPanel(JPanel panel) {
		// Remove existing preview if any
		if (previewPanel != null) {
			remove(previewPanel);
		}
		
		previewPanel = panel;
		
		// Add new preview if provided
		if (previewPanel != null) {
			add(previewPanel, BorderLayout.EAST);
		}
		
		// Update layout
		revalidate();
		repaint();
	}
	
	public void setEnabled( boolean tf ) {
		
		list.setEnabled(tf); 
		textField.setEnabled(tf); 
	}

	@SuppressWarnings("unchecked")
	public DataGenerator getSelectedItem() {

		try {
			Object[] params = new Object[1]; 
			params[0] = null; 
			return ((Class<? extends DataGenerator>)((TextListItem)list.getSelectedItem()).getValue()).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null; 
	}

	public void setText( String text ) {

		if( !programmaticAdjusting ) { 

			textFieldAdjusting = true;  
			textField.setText( text );  
			textFieldAdjusting = false; 

		} 
	}

	public void selectFirst() {

		list.setSelectedIndex( 0 );
		list.scrollToTop();
	}

	public JList getList() {

		return list;
	}

	public void addTypes( ArrayList<Class< ? extends DataGenerator>> classlist ) {

		for ( Class< ? extends DataGenerator> generator : classlist ) {
			
			String name = StringProcess.classNameConvert( generator.getName() );
			list.addItem( new TextListItem( generator, name ) );

		}

	}

	/**
	 * looks up the provided text. If it exactly matches the name of an element in the list, that element is selected.
	 * @param text
	 */
	public void displayExactMatch( String text ) {

		textField.setText(text);

		if( text.trim().length() == 0) displayAll(); 
		else { 

			text = text.toLowerCase();
			ArrayList<Class< ? extends DataGenerator>> retrieved_list = map.get( text ); 
			adjusting = true; 
			list.removeAllItems();
			if( retrieved_list != null ) {

				if( retrieved_list.size() == 1 ) { //1 match

					addTypes( retrieved_list );

				} else {

					ArrayList<Class< ? extends DataGenerator>> pruned_list = new ArrayList<Class< ? extends DataGenerator>>();
					for( Class< ? extends DataGenerator> generator : retrieved_list ) {
						
						String name = StringProcess.classNameConvert( generator.getName() ).toLowerCase();
						if( name.equals( text ) ) 
							pruned_list.add( generator );

					}
					addTypes( pruned_list );
				}
			}
			list.getSelectionModel().clearSelection(); 
			activeIndex = -1;
			adjusting = false;
			selectFirst();//select the first element in the list
		
		}
	}

	public void displayAll() {

		adjusting = true; 
		list.removeAllItems(); 
		addTypes( datageneratorList );  
		list.getSelectionModel().clearSelection();
		activeIndex = -1;

		adjusting = false;
		selectFirst();
	}

	/** 
	 * Displays all elements in the list, but selects only the exact match
	 * @param text
	 */
	public void displayAllSelectExactMatch( String text ) {

		if( text.trim().length() == 0) displayAll(); 
		else { 
 
			adjusting = true; 
			list.removeAllItems();

			//add all
			addTypes( datageneratorList ); 

			//search through list to find exact match, select
			for( int i = 0; i < datageneratorList.size(); i++ ) {

				if( datageneratorList.get(i).getName().equals( text  ) ) {
				
					list.setSelectedIndex( i );
					activeIndex = i; 
				}


			}

			adjusting = false;
			ResourceLoader.loadPage( docPane, getSelectedItem().getTypeModel().getDocPath() );
			
			//this is a workaround for a bug in Java's ensureIndexIsVisible method. 
			//without this hack, the selected index is not visible (it is just out of bounds) 
			if( activeIndex == 0 ) list.scrollToTop(); 
			else list.ensureIndexIsVisible(activeIndex-1);
		}
	}

	public boolean isAdjusting() {

		return adjusting; 
	}

	/**
	 * @return the activeIndex
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * @param activeIndex the activeIndex to set
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

}
