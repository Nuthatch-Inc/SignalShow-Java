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
public abstract class Operation extends DataGenerator implements Cloneable {
	
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
	
	/** Copy the configured operation, retaining its shared type metadata. */
	@Override
	public Operation clone() {
		try {
			Operation copy = (Operation) super.clone();
			// Operations store their settings in primitive fields and this block.
			// The block's parameter/source vectors belong to the new instance.
			copy.paramBlock = paramBlock == null ? null : (ParameterBlock) paramBlock.clone();
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	public abstract String getOpIconPath(); 
}
