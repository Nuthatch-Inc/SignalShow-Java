package signals.core;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.ImageIcon;

import signals.gui.IconCache;
import signals.gui.operation.OperationOptionsPanel;

/**
 * One step in a system. Analogous to a function term for a function
 * @author Juliet
 *
 */
public abstract class Operation extends DataGenerator {
	
	//priority for combine ops rule
	int priority; 
	
	public Operation() {
		super();
	}

	public Operation(ParameterBlock paramBlock, int priority) {
		super(paramBlock);
		this.priority = priority;
	}
	
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	public ImageIcon getOpIcon() {
		
		return IconCache.getIcon(getOpIconPath()); 
	}
	
	/**
	 * Subclasses should override this method to return an instance of their particular interface. 
	 * @return a new instance of the interface used to set up this operation
	 */
	public OperationOptionsPanel getOptionsInterface() {
		
		return new OperationOptionsPanel( this ); 
	}
	
	public abstract String getOpIconPath(); 
}
