package signals.demo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import signals.core.Function;
import signals.functionterm.AnalyticFunctionTerm1D;
import signals.functionterm.DoubleSlitFunctionTerm1D;
import signals.functionterm.GaussianDoubleSlitFunctionTerm1D;
import signals.functionterm.GaussianFunctionTerm1D;
import signals.functionterm.HighPassFunctionTerm1D;
import signals.functionterm.RectangleFunctionTerm1D;
import signals.gui.datagenerator.AnalyticFunctionEditorPanel;
import signals.operation.TimesOp;

/*
 * Abstract filtering demo allows user to choose low pass, band pass, or high pass filter
 * type ideal or gaussian
 */
@SuppressWarnings("serial")
public abstract class FilteringDemo extends JPanel {

	Function filter; 
	Function input, transform, reconstructed; 
	
	TimesOp timesOp; 
	
	//radiobuttons for filter type
	JRadioButton idealFilterButton, gaussianFilterButton; 
	JRadioButton lpFilterButton, hpFilterButton, bpFilterButton, brFilterButton; 
	
	AnalyticFunctionTerm1D filterTerm; 
	FilterParamPanel filterParamPanel; 
	JPanel filterTypePanel, filterShapePanel, editorPanel; 
	
	public FilteringDemo( Function input ) {
		
		this.input = input; 
		
		setLayout( new BorderLayout() ); 
		
		timesOp = (TimesOp) new TimesOp(null).getDefaultInstance(); 
		
		lpFilterButton = new JRadioButton( "Low Pass"); 
		hpFilterButton = new JRadioButton( "High Pass"); 
		bpFilterButton = new JRadioButton( "Band Pass"); 
		brFilterButton = new JRadioButton( "Band Reject"); 
		
		ButtonGroup filterTypeGroup = new ButtonGroup(); 
		filterTypeGroup.add( lpFilterButton ); 
		filterTypeGroup.add( hpFilterButton ); 
		filterTypeGroup.add( bpFilterButton ); 
		filterTypeGroup.add( brFilterButton ); 
		lpFilterButton.setSelected(true); 
		
		filterTypePanel = new JPanel(); 
		filterTypePanel.setLayout( new BoxLayout(filterTypePanel, BoxLayout.PAGE_AXIS)); 
		filterTypePanel.setBorder(BorderFactory.createTitledBorder("Filter Type")); 
		filterTypePanel.add(lpFilterButton); 
		filterTypePanel.add(hpFilterButton); 
		filterTypePanel.add(bpFilterButton); 
		filterTypePanel.add(brFilterButton); 
		
		idealFilterButton = new JRadioButton( "Ideal"); 
		gaussianFilterButton = new JRadioButton( "Gaussian" ); 
		
		ButtonGroup filterShapeGroup = new ButtonGroup(); 
		filterShapeGroup.add( idealFilterButton ); 
		filterShapeGroup.add( gaussianFilterButton ); 
		
		filterShapePanel = new JPanel(); 
		filterShapePanel.setBorder(BorderFactory.createTitledBorder("Filter Shape")); 
		filterShapePanel.add( idealFilterButton ); 
		filterShapePanel.add( gaussianFilterButton ); 
		filterShapePanel.setLayout( new BoxLayout( filterShapePanel, BoxLayout.PAGE_AXIS )); 
		idealFilterButton.setSelected(true); 
		
		ActionListener listener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				createFilterTerm(); 
				calculate(); 
				display(); 
				
			}

		}; 
		
		idealFilterButton.addActionListener(listener); 
		gaussianFilterButton.addActionListener(listener); 
		lpFilterButton.addActionListener(listener);
		hpFilterButton.addActionListener(listener);
		bpFilterButton.addActionListener(listener);
		brFilterButton.addActionListener(listener);
		
		filterParamPanel = new FilterParamPanel(); 
		filterParamPanel.setBorder(BorderFactory.createTitledBorder("Filter Parameters")); 
		
		editorPanel = new JPanel(); 
		editorPanel.add( filterTypePanel ); 
		editorPanel.add( filterShapePanel ); 
		editorPanel.add( filterParamPanel ); 
			
	}
	
	
	public void sizeChanged() {}
	
	public abstract void calculate(); 
	
	public abstract void display(); 
	
	public class FilterParamPanel extends AnalyticFunctionEditorPanel {

		@Override
		public void parameterChanged() {
			
			calculate(); 
			display();
			
		}
		
		
	}
	
	public void createFilterTerm() {
		
		
		if( hpFilterButton.isSelected() || lpFilterButton.isSelected() ) {
			
			if( gaussianFilterButton.isSelected() ) {
				
				filterTerm = (AnalyticFunctionTerm1D) new GaussianFunctionTerm1D(null).getDefaultInstance(); 
				
			} else if( idealFilterButton.isSelected() ) {
				
				filterTerm = (AnalyticFunctionTerm1D) new RectangleFunctionTerm1D(null).getDefaultInstance();
			}
			
		} else if( bpFilterButton.isSelected() || brFilterButton.isSelected() ) { 
			
			if( gaussianFilterButton.isSelected() ) {
				
				//gaussian band pass
				filterTerm = (AnalyticFunctionTerm1D) new GaussianDoubleSlitFunctionTerm1D(null).getDefaultInstance(); 
				
			} else if( idealFilterButton.isSelected() ) {
				
				filterTerm = (AnalyticFunctionTerm1D) new DoubleSlitFunctionTerm1D(null).getDefaultInstance();
			}
		}       
		
		if( hpFilterButton.isSelected() || brFilterButton.isSelected() ) {
			
			filterTerm = new HighPassFunctionTerm1D( filterTerm ); 
			
		}
		
		filterParamPanel.setFunction(filterTerm); 

	}
	                                

}
