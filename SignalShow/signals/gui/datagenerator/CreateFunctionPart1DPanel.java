package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import signals.core.CombineTermsRule;
import signals.core.FunctionPart1D;
import signals.core.FunctionTerm1D;

@SuppressWarnings("serial")
public class CreateFunctionPart1DPanel extends JPanel {
	
	//broadcasts events for this part of the interface	
	GUIEventBroadcaster broadcaster; 
	
	//keeps track of all function terms and the way they are combined
	FunctionCalculator1D calculator; 
	
	//combobox to select the dimension of this function
	JComboBox dimensionSelector; 
	
	//used for choosing whether the indices of this function are zero-centered
	JCheckBox zeroCenteredCheckBox; 
	
	CreateDataGeneratorPanel termPanel; 

	public CreateFunctionPart1DPanel( GUIEventBroadcaster broadcaster, boolean createDefaultNew ) {
		
		this( broadcaster, createDefaultNew, true ); 
	}
	
	public void setDocPath( String path ) {
		
		if( termPanel != null ) termPanel.setDocPath(path); 
	}
	
	public CreateFunctionPart1DPanel( GUIEventBroadcaster broadcaster, boolean createDefaultNew, boolean editable ) {
	
		this.broadcaster = broadcaster; 
		
		setLayout( new BorderLayout( ) );
		termPanel = new CreateFunctionTerm1DPanel( broadcaster, editable );
		calculator = new FunctionCalculator1D( broadcaster, editable ); 
		
		add( calculator, BorderLayout.NORTH );
		add( termPanel, BorderLayout.CENTER );
		
		//add a blank function to the term panel
		if( createDefaultNew )
			calculator.createNewFunction();
	}
	
	/**
	 * 
	 * @return the functionPart1D that the user has created
	 */
	public FunctionPart1D getFunctionPart1D() {
		
		ArrayList<FunctionTerm1D> termsList = calculator.getFunctionTerm1DList();
		int[] rule = calculator.getCodeList();
		CombineTermsRule combineRule = new CombineTermsRule( rule );
		FunctionPart1D part = new FunctionPart1D( termsList, combineRule );
	
		return part;
	}
	
	
	public void setFunctionPart1D( FunctionPart1D part ) {
		
		ArrayList<FunctionTerm1D> termsList = part.getTermsList(); 
		CombineTermsRule rule = part.getCombineTermsRule(); 
		calculator.setFunctionTerm1DList(termsList, rule);
	}
	
	public void setIndices( double[] indices ) {
		
		broadcaster.broadcast( this, GUIEvent.Descriptor.INDICES_MODIFIED_1D, indices ); 
	}

}
