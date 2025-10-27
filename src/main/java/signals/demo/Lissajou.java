package signals.demo;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import signals.core.Core;
import signals.core.DataGeneratorTypeModel;
import signals.functionterm.AnalyticFunctionTerm1D;
import signals.functionterm.CosineFunctionTerm1D;
import signals.gui.datagenerator.AnalyticFunctionEditorPanel;
import signals.gui.plot.ArgandDiagram;
import signals.gui.plot.Indices;

@SuppressWarnings("serial")
public class Lissajou extends ArgandDiagram  {
	
	CosineFunctionTerm1D cosineR, cosineI; 
	DataGeneratorTypeModel cosineTypeModel; 
	double[] indices; 

	public Lissajou() {

		super(); 

		CosineFunctionTerm1D cosine = new CosineFunctionTerm1D( null  ); 
		cosineR = (CosineFunctionTerm1D) cosine.getDefaultInstance(); 
		cosineI = (CosineFunctionTerm1D) cosine.getDefaultInstance(); 

		double defaultWidth = Core.getFunctionCreationOptions().getDefaultWidth1D(); 
		cosineR.setWidth(defaultWidth); 
		cosineI.setWidth(defaultWidth);
		cosineTypeModel = cosineR.getTypeModel();

		int dimension = 256; 
		boolean zeroCentered = false; 
		indices = Indices.indices1D( dimension, zeroCentered ); 
		
		CosineEditorPanel editorR = new CosineEditorPanel( cosineR ); 
		CosineEditorPanel editorI = new CosineEditorPanel( cosineI ); 
		
		editorR.setBorder(BorderFactory.createTitledBorder("Real Part")); 
		editorI.setBorder(BorderFactory.createTitledBorder("Imaginary Part")); 
		
		JPanel editorPanel = new JPanel(); 
		editorPanel.setLayout(new GridLayout(2, 1)); 
		editorPanel.add(editorR); 
		editorPanel.add(editorI); 
		
		addToMain(editorPanel); 

		setData(cosineR.create(indices), cosineI.create(indices) ); 
	}
	
	public void refreshArgand() {
		
		stop(); 
		setData(cosineR.create(indices), cosineI.create(indices) ); 
		play(); 
	}

	public class CosineEditorPanel extends AnalyticFunctionEditorPanel {

		public CosineEditorPanel(AnalyticFunctionTerm1D currentFunction) {
			super(currentFunction);
		}

		@Override
		public void parameterChanged() {
			refreshArgand(); 
		}
	
	}

}
