package signals.core;

import java.util.ArrayList;

import signals.gui.datagenerator.OperatorSystemToolBar1D;

public class OperatorSystem1D extends OperatorSystem { 
	
	public OperatorSystem1D() {
		super();
	}

	public OperatorSystem1D(CombineOpsRule combineOpsRule,
			ArrayList<FunctionProducer> inputList, ArrayList<Operation> opList) {
		super(combineOpsRule, inputList, opList);
	}

	public void show() {
		OperatorSystemToolBar1D toolBar = new OperatorSystemToolBar1D( this );
		Core.getGUI().setToolBar( toolBar );
		toolBar.selectButton(showState); 
	}

	public OperatorSystem1D clone() {
		
		ArrayList<Operation> copiedOperations = copyOperations();
		CombineOpsRule copiedRule = new CombineOpsRule(combineOpsRule.getInfix().clone(), copiedOperations);
		return new OperatorSystem1D(copiedRule, new ArrayList<>(inputList), copiedOperations);
	}

}
