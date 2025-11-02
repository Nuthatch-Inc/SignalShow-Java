package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import signals.core.Core;
import signals.core.DataGenerator;
import signals.gui.GUIDimensions;
import signals.io.Browser;
import signals.io.ResourceLoader;

/**
 * 
 * @author julietbernstein
 *
 *
 *
 *
 */
@SuppressWarnings("serial")
public abstract class CreateDataGeneratorPanel extends JPanel implements GUIEventListener {

	protected DataGeneratorSelectorPanel selector;

	//newstate means that a new function has been clicked on
	protected boolean newState;
	protected DataGenerator currentDataGenerator;

	GUIEventBroadcaster broadcaster; 
	GUIEvent.Descriptor modifiedDescriptor;
	boolean editable; 
	
	//used to hold the static help documentation when editor is not available (i.e. for predefined functions)
	protected JEditorPane docPane;
	protected JPanel docWrapperPanel;

	public CreateDataGeneratorPanel( final GUIEventBroadcaster broadcaster, boolean editable ) {

		this( broadcaster, GUIEvent.Descriptor.MODIFIED, editable ); 
	}

	public CreateDataGeneratorPanel( final GUIEventBroadcaster broadcaster, 
			final GUIEvent.Descriptor modifiedDescriptor, boolean editable ) {

		this.editable = editable; 
		this.modifiedDescriptor = modifiedDescriptor; 
		this.broadcaster = broadcaster; 
		broadcaster.addGUIEventListener(this); 

		//preferred size
		setPreferredSize( new Dimension( GUIDimensions.LIST_WIDTH + GUIDimensions.largePlotThumbnailDimension.width + 400 ,
				GUIDimensions.largePlotThumbnailDimension.height + 50 )); 

		selector = new DataGeneratorSelectorPanel( getDataGeneratorList(), getDataGeneratorMap()); 

		selector.getList().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){

				if( e.getValueIsAdjusting() ) return; 

				//if a new function has been clicked on 
				//if the user did not click on the same function they just clicked on
				if( newState && !selector.isAdjusting() ) { 

					int newIndex = selector.getList().getSelectedIndex();

					if( newIndex != selector.getActiveIndex() ) { 

						selector.setActiveIndex( newIndex );
						DataGenerator generator = selector.getSelectedItem(); 

						//create a default function and send to the calculator
						setDataGenerator( generator.getDefaultInstance() );
						setDefaultWidth( true ); 
						broadcaster.broadcast(this, modifiedDescriptor, currentDataGenerator ); 

					} 

				}

			}});

		selector.setEnabled(editable);
		setBorder( Core.getBorders().getHEmptyBorder() ); 
		
		if( !editable ) {

			docPane = new JEditorPane();
			docPane.setEditable(false);
			docPane.setBorder(Core.getBorders().getBuffer()); 

			docPane.addHyperlinkListener(new HyperlinkListener() {

				public void hyperlinkUpdate(HyperlinkEvent e) {

					if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
						Browser.showInBrowser( e.getURL().toString() ); 

				}

			}); 
			
			// Create fixed-width wrapper panel to prevent width jumping
			JScrollPane docScrollPane = new JScrollPane(docPane);
			docWrapperPanel = new JPanel(new BorderLayout()) {
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
			docWrapperPanel.add(docScrollPane, BorderLayout.CENTER);
		}

		//other components and layout
		createEditor(); 
		constructorLayout(); 
	}
	
	public void setDocPath( String filename ) {
		
		if( !editable )
		ResourceLoader.loadPage( docPane, filename );
	}

	public abstract void constructorLayout();

	/**
	 * 
	 * @return the FunctionTerm1D selected by the user
	 */
	public DataGenerator getDataGenerator() {

		return currentDataGenerator;
	}

	public void setDataGenerator(DataGenerator updatedTerm) {

		currentDataGenerator = updatedTerm; 
		updateEditor();
	}


	public void setList( DataGenerator updatedTerm ) {

		newState = false; //make newstate false so that a new function is not created 
		setDataGenerator( updatedTerm );
		selector.displayAllSelectExactMatch(currentDataGenerator.getClass().getName());
		selector.setActiveIndex( -1 );
		newState = true;
	}

	public void setListExisting(DataGenerator updatedTerm) {

		setDefaultWidth( false ); 
		setList( updatedTerm ); 
	}

	public void setListNew(DataGenerator updatedTerm) {

		setList( updatedTerm );
		setDefaultWidth( true ); //order of these instructions is important
	}

	public abstract void setDefaultWidth( boolean on ); 

	public abstract void createEditor(); 

	public abstract HashMap<String, ArrayList<Class< ? extends DataGenerator>>> getDataGeneratorMap(); 

	public abstract ArrayList<Class< ? extends DataGenerator>> getDataGeneratorList();

	public abstract void updateEditor(); 

	public abstract JComponent getEditor(); 

}