package signals.gui.datagenerator;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * 
 * Event that gets thrown when high-level GUI-based events get thrown
 * 
 * @author julietbernstein
 *
 */
@SuppressWarnings("serial")
public class GUIEvent extends EventObject {
	
	/**
	 * 
	 * new selected: usually sent by a calculator meaning a new function has been created and is being displayed
	 * existing selected: usually send by a calculator meaning an existing function is being displayed
	 * modified: usually sent by an interactive interface, meaning the function has been modified by the user
	 * indices modified: the indices have been changed
	 * subpart modified: usually sent by an interactive interface, meaning part of a function has been modified 
	 * (the interface is given instructions about whether it is to use modified or subpart modified)
	 *
	 */
	public enum Descriptor { NEW_SELECTED, EXISTING_SELECTED, NON_SELECTED, MODIFIED, INDICES_MODIFIED_1D, 
		INDICES_MODIFIED_2D, SUBPART_MODIFIED_R, SUBPART_MODIFIED_THETA, SUBPART_MODIFIED_X, SUBPART_MODIFIED_Y,
		SELECTED_CODE1, SELECTED_CODE2, NOTIFY, PARAM_CHANGED }

	
	//items that can be used to describe this event
	ArrayList<Object> values; 
	
	Descriptor descriptor; 
	
	/**
	 * create a GUI event with no values
	 * @param source
	 * @param descriptor
	 */
	public GUIEvent(Object source, Descriptor descriptor) {
		super(source);
		this.descriptor = descriptor; 
	
	}
	
	/**
	 * create a GUI event with a single value
	 * @param source
	 * @param value
	 * @param descriptor
	 */
	public GUIEvent(Object source, Descriptor descriptor, Object value ) {
		super(source);
		values = new ArrayList<Object>(); 
		values.add(value); 
		this.descriptor = descriptor;
	}

	/**
	 * create a GUI event with several values
	 * @param source
	 * @param values
	 * @param descriptor
	 */
	public GUIEvent(Object source, Descriptor descriptor,  ArrayList<Object> values ) {
		super(source);
		this.values = values;
		this.descriptor = descriptor;
	}
	
	public ArrayList<Object> getValues() {
		return values;
	}
	
	public Object getValue( int index ) {
		
		return getValues().get(index); 
	}

	public void setValues(ArrayList<Object> values) {
		this.values = values;
	}

	public Descriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(Descriptor descriptor) {
		this.descriptor = descriptor;
	}

}
