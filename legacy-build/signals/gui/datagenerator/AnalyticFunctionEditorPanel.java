package signals.gui.datagenerator;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import signals.functionterm.AnalyticFunctionTerm1D;
import signals.gui.ParameterEditor;
import signals.gui.ParameterUser;
import signals.gui.SpringUtilities;

@SuppressWarnings("serial")
public abstract class AnalyticFunctionEditorPanel extends JPanel implements ParameterUser {

	public static final int H_SPACE = 5;
	public static final int V_SPACE = 5;
	public static final int H_SIZE = 30;
	public static final int V_SIZE = 20;
	public static final int ROWS = 3;
	public static final int COLS = 6;

	AnalyticFunctionTerm1D currentFunction; 

	public AnalyticFunctionEditorPanel() {

		super( new SpringLayout() );
	}

	public AnalyticFunctionEditorPanel(AnalyticFunctionTerm1D currentFunction) {

		this(); 
		setFunction( currentFunction ); 
	}

	public void setFunction( AnalyticFunctionTerm1D currentFunction ) {

		removeAll(); 
		this.currentFunction = currentFunction; 			
		String[] labels = currentFunction.getTypeModel().getParamNames();
		SpinnerNumberModel[] spinnerModels = currentFunction.getSpinnerModels();
		int numPairs = labels.length;
		ParameterEditor[] textFields = new ParameterEditor[numPairs];

		for (int i = 0; i < numPairs; i++) {
			JLabel l = new JLabel(labels[i]+": ", JLabel.TRAILING);
			add(l);
			SpinnerNumberModel model = spinnerModels[i];
			textFields[i] = new ParameterEditor( 3, model, i, this ); //3 columns 
			l.setLabelFor(textFields[i]);
			add(textFields[i]);
		}

		int numBlank = ROWS*COLS - 2*numPairs;

		for (int i = 0; i < numBlank; i++) {

			add( Box.createHorizontalStrut( H_SIZE ) );
		}

		SpringUtilities.makeCompactGrid(this,
				ROWS, COLS, 		//rows, cols
				H_SPACE, V_SPACE,        //initX, initY
				H_SPACE, V_SPACE);       //xPad, yPad

		revalidate(); 
		repaint(); 

	}

	@SuppressWarnings("unchecked")
	public void parameterChanged(int index, String newValue) {

		Class[] classes = currentFunction.getTypeModel().getParamClasses();

		if( classes[index] == Integer.class ) {

			currentFunction.setParameter( Integer.parseInt(newValue), index );

		} else {

			double value = Double.parseDouble(newValue);

			if( index == currentFunction.getWidthIndex() ) {

				if( value <= 0 ) return; 
			}

			currentFunction.setParameter( value, index );
		}

		parameterChanged(); 

	}

	public abstract void parameterChanged(); 

}
