package signals.core;

import java.util.ArrayList;

import signals.gui.datagenerator.OperatorSystemToolBar2D;

public class OperatorSystem2D extends OperatorSystem { 
	
	public OperatorSystem2D() {
		super();
	}

	public OperatorSystem2D(CombineOpsRule combineOpsRule,
			ArrayList<FunctionProducer> inputList, ArrayList<Operation> opList) {
		super(combineOpsRule, inputList, opList);
	}

	public void show() {
		OperatorSystemToolBar2D toolBar = new OperatorSystemToolBar2D( this );
		Core.getGUI().setToolBar( toolBar ); 
		toolBar.selectButton(showState); 
	}
	
	public OperatorSystem2D clone() {
		
		ArrayList<Operation> copiedOperations = copyOperations();
		CombineOpsRule copiedRule = new CombineOpsRule(combineOpsRule.getInfix().clone(), copiedOperations);
		return new OperatorSystem2D(copiedRule, new ArrayList<>(inputList), copiedOperations);
	}


}
