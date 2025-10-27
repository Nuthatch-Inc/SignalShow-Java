package signals.core;

import java.awt.image.renderable.ParameterBlock;

public abstract class BinaryOperation extends Operation {

	public BinaryOperation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BinaryOperation(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
	}
	
	public abstract Function create( Function inputA, Function inputB );

}
