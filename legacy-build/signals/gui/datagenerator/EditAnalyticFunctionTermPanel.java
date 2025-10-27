package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import signals.core.Core;
import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;
import signals.functionterm.AnalyticFunctionTerm;
import signals.functionterm.DataFunctionTerm1D;
import signals.gui.ParameterEditor;
import signals.gui.ParameterUser;
import signals.gui.SpringUtilities;

// Layout: 
// ***:___ ***:___ ***:___
// ***:___ ***:___ ***:___ 
// some ***:___ may be "blank"
@SuppressWarnings("serial")
public abstract class EditAnalyticFunctionTermPanel extends JPanel implements ParameterUser, 
				GUIEventListener {

	public static final int H_SPACE = 10;
	public static final int V_SPACE = 5;
	public static final int H_SIZE = 50;
	public static final int V_SIZE = 20;
	public static final int ROWS = 3;
	public static final int COLS = 6;
	
	protected JPanel paramPanel;
	protected JPanel plotPanel;
	ParameterEditor[] textFields;
	AnalyticFunctionTerm currentFunction;
	protected NumberFormat formatter;
	
	GUIEventBroadcaster broadcaster; 

	double defaultWidth;
	boolean setDefaultWidth;
	
	//descriptor to broadcast when the editor modifies the data
	GUIEvent.Descriptor modifiedDescriptor; 

	public EditAnalyticFunctionTermPanel( GUIEventBroadcaster broadcaster, GUIEvent.Descriptor modifiedDescriptor ) {
		
		this.broadcaster = broadcaster; 
		broadcaster.addGUIEventListener(this); 
		
		paramPanel = new JPanel( new SpringLayout() );
		
		formatter = Core.getDisplayOptions().getFormat(); 
		
		this.modifiedDescriptor = modifiedDescriptor; 
		
		createEditor(); 
	}
	
	public void constructorLayout() {
		
		//layout
		setLayout( new BorderLayout() ); 
		add( paramPanel, BorderLayout.SOUTH ); 
		
		plotPanel = new JPanel(); 
		add( plotPanel, BorderLayout.CENTER );
		refreshEditor();
	}

	public void refreshEditor() {
		
		plotPanel.removeAll(); 
		plotPanel.add(getEditor()); 
		plotPanel.revalidate();
		plotPanel.repaint();
		
	}
	
	public abstract void createEditor(); 
	
	public abstract JComponent getEditor(); 

	public void setAmplitude(double newAmplitude) {
		
		textFields[currentFunction.getAmplitudeIndex()].setText( formatter.format( newAmplitude ) );
	}

	public void setCenter(double newCenter) {
	
		textFields[currentFunction.getCenterIndex()].setText( formatter.format( newCenter ) );
	}

	public void setWidth(double newWidth) {
		
		//for a halfwidth-defined function, the value in the spinner is twice the stored width
		//get the value in the model and multiply it by 2 for display
		if( currentFunction.isHalfWidthDefined() ) newWidth *= 2.0;
		textFields[currentFunction.getWidthIndex()].setText( formatter.format( newWidth ) );
	}
	
	public void setDefaultWidthOff() {
		
		setDefaultWidth = false; 
	}
	
	public void setDefaultWidthOn( double defaultWidth ) {
		
		this.defaultWidth = defaultWidth; 
		setDefaultWidth = true; 
		if( currentFunction != null && currentFunction.hasWidth() ) {
			
			if( defaultWidth == 360 || defaultWidth == 90 ) setWidth( 30 ); 
			else if( currentFunction instanceof DataFunctionTerm1D ) setWidth( defaultWidth / 2.0 );
			else if( currentFunction.isHalfWidthDefined() ) setWidth( Math.sqrt(defaultWidth) / 2.0 );
			else setWidth( Math.sqrt(defaultWidth) );
		}
	}
	
	public void addSelectors() {};

	public void setFunctionTerm(DataGenerator newTerm) {
		
		boolean needsetup = ( currentFunction != null && currentFunction.getClass() != newTerm.getClass() ); 
		
		currentFunction = (AnalyticFunctionTerm)newTerm;
		paramPanel.removeAll();
		
		addSelectors(); 
		
		if( setDefaultWidth && currentFunction.hasWidth() ) {
			
			//a halfwidth-defined function stores a width value of default width / 2, 
			if( currentFunction instanceof DataFunctionTerm1D ) currentFunction.setWidth( defaultWidth / 2.0 );
			else if( currentFunction.isHalfWidthDefined() ) currentFunction.setWidth( Math.sqrt(defaultWidth) / 2.0 );
			else currentFunction.setWidth( Math.sqrt(defaultWidth) );
		}
		
		String[] labels = currentFunction.getTypeModel().getParamNames();
		SpinnerNumberModel[] spinnerModels = currentFunction.getSpinnerModels();
		int numPairs = labels.length;
		textFields = new ParameterEditor[numPairs];
	
		for (int i = 0; i < numPairs; i++) {
			JLabel l = new JLabel(labels[i]+": ", JLabel.TRAILING);
			paramPanel.add(l);
			SpinnerNumberModel model = spinnerModels[i];
			
			if( currentFunction.isHalfWidthDefined() && i == currentFunction.getWidthIndex() ) {
				
				//for a halfwidth-defined function, the value in the spinner is twice the stored width
				//get the value in the model and multiply it by 2 for display
				Double newVal = new Double( model.getNumber().doubleValue() * 2  );
				model.setValue(newVal);
		
			}
			
			textFields[i] = new ParameterEditor( 3, model, i, this ); //3 columns 
			l.setLabelFor(textFields[i]);
			paramPanel.add(textFields[i]);
		}
	
		int numBlank = ROWS*COLS - 2*numPairs;
	
		for (int i = 0; i < numBlank; i++) {
	
			paramPanel.add( Box.createHorizontalStrut( H_SIZE ) );
		}
	
		SpringUtilities.makeCompactGrid(paramPanel,
				ROWS, COLS, 		//rows, cols
				H_SPACE, V_SPACE,        //initX, initY
				H_SPACE, V_SPACE);       //xPad, yPad
	
		paramPanel.revalidate();
		paramPanel.repaint();
		
		if( needsetup ) setupEditor();
	}
	
	public abstract void setupEditor();

	@SuppressWarnings("unchecked")
	public void parameterChanged(int index, String newValue) {
		
		DataGeneratorTypeModel model = currentFunction.getTypeModel();
		Class[] classes =  model.getParamClasses();
		
		if( classes[index] == Integer.class ) {
			
			((DataGenerator)currentFunction).setParameter( Integer.parseInt(newValue), index );
			
		} else {
		
			double value = Double.parseDouble(newValue);
			//for a halfwidth-defined function, the value in the spinner is twice the stored width
			//get the value in the spinner and divide it by 2 before storing. 
			if( index == currentFunction.getWidthIndex() ) {
				
				if( value <= 0 ) return; 
				if( currentFunction.isHalfWidthDefined() ) value /= 2;
			}
			
			((DataGenerator)currentFunction).setParameter( value, index );
		}
		
		updateDependents();
	}
	
	public void updateDependents() {
		
		broadcaster.broadcast(this, modifiedDescriptor, currentFunction);
	}
	
}