package signals.gui;


public class TextListItem {
	
	String text; 
	
	//value of this cell
	Object value; 

	/**
	 * @param smallIcon
	 * @param largeIcon
	 */
	public TextListItem(Object value, String text ) {
		super();
		this.value = value;
		this.text = text; 
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString() {
		
		return text; 
	}


}
