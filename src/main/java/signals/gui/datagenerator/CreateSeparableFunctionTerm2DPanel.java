package signals.gui.datagenerator;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import signals.core.Core;
import signals.core.DataGenerator;
import signals.functionterm.SeparableFunctionTerm2D;
import signals.gui.plot.SeparableFunctionTerm2DPreviewPanel;

@SuppressWarnings("serial")
public abstract class CreateSeparableFunctionTerm2DPanel extends JTabbedPane implements CreateDataGeneratorInterface, 
	GUIEventListener {
	
	protected CreateAnalyticSubTerm1DPanel termPanel1;
	protected CreateAnalyticSubTerm1DPanel termPanel2;
	protected SeparableFunctionTerm2DPreviewPanel previewPanel;
	
	int xDimension, yDimension; 
	boolean zeroCentered;
	
	//broadcasts events for this part of the interface	
	GUIEventBroadcaster broadcaster; 
	
	public CreateSeparableFunctionTerm2DPanel( GUIEventBroadcaster broadcaster, boolean editable ) {
		
		this.broadcaster = broadcaster;
		broadcaster.addGUIEventListener(this); 
		
		termPanel1 = new CreateAnalyticSubTerm1DPanel( broadcaster, getDescriptor1(), editable );
		termPanel2 = new CreateAnalyticSubTerm1DPanel( broadcaster, getDescriptor2(), editable ); 
		previewPanel = new SeparableFunctionTerm2DPreviewPanel();

		setIndices( Core.getFunctionCreationOptions().getDefaultDimensionX2D(), 
					Core.getFunctionCreationOptions().getDefaultDimensionY2D(), 
					Core.getFunctionCreationOptions().isZeroCentered2D() ); 
		
		JPanel contentPanelx = new JPanel(); 
		contentPanelx.setLayout( new BorderLayout() ); 
		
		contentPanelx.add( termPanel1, BorderLayout.CENTER );
		
		addTab("", getTermPanelIcon1(), contentPanelx);
		
		JPanel contentPanely = new JPanel(); 
		contentPanely.setLayout( new BorderLayout() ); 
		
		contentPanely.add( termPanel2, BorderLayout.CENTER );
		addTab("", getTermPanelIcon2(), contentPanely);
		
		JPanel contentPanelxy = new JPanel(); 		
		contentPanelxy.add( previewPanel );
		addTab( "", getPreviewIcon(), contentPanelxy );
	
		//add a listener 
		addChangeListener( new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				
				if( getSelectedIndex() == 2 ) {
				
					previewPanel.setFunction( (SeparableFunctionTerm2D)getDataGenerator(), xDimension, yDimension, zeroCentered ); 
				}
			}
			
		});
		
		//add( tabbedPane, BorderLayout.CENTER );
	
	}
	
	public abstract ImageIcon getTermPanelIcon1(); 
	public abstract ImageIcon getTermPanelIcon2(); 
	public abstract ImageIcon getPreviewIcon(); 
	
	public abstract String getTermPanelName1(); 
	public abstract String getTermPanelName2();
	
	public abstract GUIEvent.Descriptor getDescriptor1(); 
	public abstract GUIEvent.Descriptor getDescriptor2(); 
	
	public void setDocPath( String path ) {
		
		termPanel1.setDocPath(path); 
		termPanel2.setDocPath(path); 
	}

	public void setListNew(DataGenerator function) {
		
		termPanel1.setListNew(((SeparableFunctionTerm2D)function).getFunctionTerm1DA());
		termPanel2.setListNew(((SeparableFunctionTerm2D)function).getFunctionTerm1DB());
		
	}

	public void setListExisting(DataGenerator function) {
	
		termPanel1.setListExisting(((SeparableFunctionTerm2D)function).getFunctionTerm1DA());
		termPanel2.setListExisting(((SeparableFunctionTerm2D)function).getFunctionTerm1DB());
		
	} 
	
	public void setDataGenerator(DataGenerator function ) { 
		
		termPanel1.setDataGenerator(((SeparableFunctionTerm2D)function).getFunctionTerm1DA());
		termPanel2.setDataGenerator(((SeparableFunctionTerm2D)function).getFunctionTerm1DB());
		
		if( getSelectedIndex() == 2 ) {
			
			previewPanel.setFunction( (SeparableFunctionTerm2D)getDataGenerator(), xDimension, yDimension, zeroCentered ); 
		}
	}
	
	public void setIndices( int xDimension, int yDimension, boolean zeroCentered ) { 
		
		this.xDimension = xDimension; 
		this.yDimension = yDimension; 
		this.zeroCentered = zeroCentered;
	}
	
	public void updateDependents() {
		
		broadcaster.broadcast(this, GUIEvent.Descriptor.MODIFIED, (SeparableFunctionTerm2D)getDataGenerator() ); 
	}

}