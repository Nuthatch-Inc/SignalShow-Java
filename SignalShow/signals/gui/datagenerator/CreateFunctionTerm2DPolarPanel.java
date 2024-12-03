package signals.gui.datagenerator;

import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import signals.core.DataGenerator;
import signals.functionterm.PolarFunctionTerm2D;
import signals.functionterm.SeparableFunctionTerm2D;
import signals.gui.IconCache;
import signals.gui.datagenerator.GUIEvent.Descriptor;
import signals.gui.plot.Indices;

@SuppressWarnings("serial")
public class CreateFunctionTerm2DPolarPanel extends CreateSeparableFunctionTerm2DPanel {

	public CreateFunctionTerm2DPolarPanel( GUIEventBroadcaster broadcaster, boolean editable ) {
		
		super( broadcaster, editable );
	}

	
	public void setDataGenerator( DataGenerator updatedTerm ) {
		
		if( updatedTerm instanceof PolarFunctionTerm2D )
		super.setDataGenerator(updatedTerm);
	}
	
	@Override
	public void setListExisting(DataGenerator function) {
		if( function instanceof PolarFunctionTerm2D )
		super.setListExisting(function);
	}

	@Override
	public void setListNew(DataGenerator function) {
		if( function instanceof PolarFunctionTerm2D )
		super.setListNew(function);
	}

	
	public DataGenerator getDataGenerator() {
		
		ParameterBlock pb2 = new ParameterBlock(); 
		pb2.addSource( termPanel1.getDataGenerator() ); 
		pb2.addSource( termPanel2.getDataGenerator() );
		
		//create a function term 2D
		PolarFunctionTerm2D term2D = new PolarFunctionTerm2D( pb2 );
		
		return term2D;
	}
	
	public void setIndices( int xDimension, int yDimension, boolean zeroCentered ) {

		super.setIndices(xDimension, yDimension, zeroCentered);
		int dimension = Math.max(xDimension, yDimension);
		
		double[] polarIndices = zeroCentered ? Indices.indices1D(360, true) : Indices.indices1D(90, false);
		double[] radialIndices = Indices.indices1D(dimension/2, false); 
		termPanel1.setIndices(radialIndices); 
		termPanel2.setIndices(polarIndices); 
		
		if( getSelectedIndex() == 2 ) {
			
			previewPanel.setFunction( (SeparableFunctionTerm2D)getDataGenerator(), xDimension, yDimension, zeroCentered ); 
		}
	}

	@Override
	public ImageIcon getTermPanelIcon1() {
		return IconCache.getIcon("/guiIcons/frLabel.png"); 
	}

	@Override
	public ImageIcon getTermPanelIcon2() {
		return IconCache.getIcon("/guiIcons/fthetaLabel.png"); 
	}
	
	@Override
	public ImageIcon getPreviewIcon() {
		return IconCache.getIcon("/guiIcons/frfthetaLabel.png"); 
	}
	
	@Override
	public Descriptor getDescriptor1() {
		return GUIEvent.Descriptor.SUBPART_MODIFIED_R; 
	}

	@Override
	public Descriptor getDescriptor2() {
		return GUIEvent.Descriptor.SUBPART_MODIFIED_THETA; 
	}
	
	@Override
	public String getTermPanelName1() {
		return "f(r)"; 
	}

	@Override
	public String getTermPanelName2() {
		return "f("+'\u03B8'+")";
	}
	
	public void GUIEventOccurred(GUIEvent e) {
		
		if( e.getSource().equals(this) ) return; 
		
		switch( e.getDescriptor() ) {
		
		case INDICES_MODIFIED_2D: 
			
			ArrayList<Object> values = e.getValues(); 
			setIndices( (Integer)values.get(0), (Integer)values.get(1), (Boolean)values.get(2) );
			
			break; 
			
		case SUBPART_MODIFIED_R: 
		case SUBPART_MODIFIED_THETA: 
			
			updateDependents(); 
			
			break; 
		
		}
		
	}


	


}
