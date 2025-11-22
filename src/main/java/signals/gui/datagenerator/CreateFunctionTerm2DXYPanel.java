package signals.gui.datagenerator;

import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import signals.core.DataGenerator;
import signals.functionterm.SeparableFunctionTerm2D;
import signals.functionterm.XYFunctionTerm2D;
import signals.gui.IconCache;
import signals.gui.datagenerator.GUIEvent.Descriptor;
import signals.gui.plot.Indices;

@SuppressWarnings("serial")
public class CreateFunctionTerm2DXYPanel extends CreateSeparableFunctionTerm2DPanel {

	public CreateFunctionTerm2DXYPanel( GUIEventBroadcaster broadcaster, boolean editable ) {
		
		super( broadcaster, editable );	
	}
	
	public void setDataGenerator( DataGenerator updatedTerm ) {
		
		if( updatedTerm instanceof XYFunctionTerm2D )
		super.setDataGenerator(updatedTerm);
	}
	
	@Override
	public void setListExisting(DataGenerator function) {
		if( function instanceof XYFunctionTerm2D )
		super.setListExisting(function);
	}

	@Override
	public void setListNew(DataGenerator function) {
		if( function instanceof XYFunctionTerm2D )
		super.setListNew(function);
	}
	
	public DataGenerator getDataGenerator() {
		
		ParameterBlock pb2 = new ParameterBlock(); 
		pb2.addSource( termPanel1.getDataGenerator() ); 
		pb2.addSource( termPanel2.getDataGenerator() );
		
		//create a function term 2D
		XYFunctionTerm2D term2D = new XYFunctionTerm2D( pb2 );
		
		return term2D;
	}
	
	public void setIndices( int xDimension, int yDimension, boolean zeroCentered ) {
		
		super.setIndices(xDimension, yDimension, zeroCentered);
		termPanel1.setIndices( Indices.indices1D( xDimension, zeroCentered ) ); 
		termPanel2.setIndices( Indices.indices1D( yDimension, zeroCentered ) );
		
		if( getSelectedIndex() == 2 ) {
			
			previewPanel.setFunction( (SeparableFunctionTerm2D)getDataGenerator(), xDimension, yDimension, zeroCentered ); 
		}
	}

	@Override
	public ImageIcon getTermPanelIcon1() {
		return IconCache.getIcon("/guiIcons/fxLabel.png"); 
	}

	@Override
	public ImageIcon getTermPanelIcon2() {
		return IconCache.getIcon("/guiIcons/fyLabel.png");  
	}
	
	@Override
	public ImageIcon getPreviewIcon() {
		return IconCache.getIcon("/guiIcons/fxfyLabel.png"); 
	}

	@Override
	public Descriptor getDescriptor1() {
		return GUIEvent.Descriptor.SUBPART_MODIFIED_X; 
	}

	@Override
	public Descriptor getDescriptor2() {
		return GUIEvent.Descriptor.SUBPART_MODIFIED_Y; 
	}
	
	
	public void GUIEventOccurred(GUIEvent e) {
	 
		if( e.getSource().equals(this) ) return; 
		
		switch( e.getDescriptor() ) {
		
		case INDICES_MODIFIED_2D: 
			
			ArrayList<Object> values = e.getValues(); 
			setIndices( (Integer)values.get(0), (Integer)values.get(1), (Boolean)values.get(2) );
			
			break; 
			
		case SUBPART_MODIFIED_X: 
		case SUBPART_MODIFIED_Y: 
			
			updateDependents(); 
			
			break; 
		
		}
		
	}

	@Override
	public String getTermPanelName1() {
		return "f(x)"; 
	}

	@Override
	public String getTermPanelName2() {
		return "f(y)";
	}

}
