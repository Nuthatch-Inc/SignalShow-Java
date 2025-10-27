package signals.core;

import java.awt.image.renderable.ParameterBlock;

public abstract class UnaryOperation extends Operation {

	public UnaryOperation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UnaryOperation(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
	}

	public abstract Function create( Function input );
}
