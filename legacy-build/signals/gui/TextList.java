package signals.gui;

import java.awt.Rectangle;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

//A variable table that stores variables the user creates
@SuppressWarnings("serial")
public class TextList extends JList { 

	DefaultListModel model;

	/**
	 * Creates a new Object list with elements of size "size"
	 * @param renderer renderer to render the cells in this list
	 */
	public TextList( ListCellRenderer renderer ) {

		model = new DefaultListModel();
		setModel( model );
		setCellRenderer( renderer );
		setFixedCellHeight(-1);

	} 
	
	public TextList clone() {
		
		TextList newList = new TextList( getCellRenderer() ); 
		
		for( int i = 0; i < model.getSize(); i++ ) {
			
			newList.addItem(model.get(i));
		}
		
		return newList; 
	}


	/**
	 * Removes all Objects from this list
	 */
	public void removeAllItems() {

		model = new DefaultListModel();
		setModel( model );
		repaint();
	}
	
	public void setSelectedIndex( int index ) {
		super.setSelectedIndex( index );
		ensureIndexIsVisible( index );
	}
	
	public void selectNext() {
		
		setSelectedIndex( Math.min( model.getSize()-1, getSelectedIndex()+1));
	}
	
	
	public void selectPrev() {
		
		setSelectedIndex( Math.max( 0 , getSelectedIndex()-1));
	}


	/**
	 * Get the number of elements in this list
	 * @return the number of elements in the list
	 */
	public int getListSize() {

		return ( model.getSize() );
	}

	/**
	 * Adds a Object and set it as the selected Object
	 * @param newEntry the item to add
	 */ 
	public void addItem( Object newEntry ) {

		model.addElement( newEntry );
		setSelectedValue( newEntry, true );
	}


	/**
	 * Return the Object at given index
	 * @param index get Object at this index
	 * @return the Object at the provided index
	 */
	public Object getItemAt( int index ) {

		return model.getElementAt( index );

	} 

	/**
	 * Remove the currently selected Object
	 */
	public void removeSelected() {

		int selectedIndex = getSelectedIndex(); 
		removeItem( getSelectedValue() );
		if( getListSize() > 0 ) {
			
			setSelectedIndex( Math.max( 0 , selectedIndex-1));
		}

	}


	/**
	 * Get the currently selected Object
	 * @return the selected Object
	 */
	public Object getSelectedItem() {

		return getSelectedValue();
	}

	/**
	 * Remove the Object at a selected index
	 * @param index remove the Object at this index
	 */
	public void removeItemAt( int index ) {

		Object toRemove = (Object)model.getElementAt( index ); 
		removeItem( toRemove );

	}

	/**
	 * Remove the Object passed in as a parameter
	 * @param toRemove remove this funcion from the list
	 */
	public void removeItem( Object toRemove ) {

		model.removeElement( toRemove ); 

	} 

	/**
	 * Inserts the given item into the list after the currently selected item 
	 * @param toAdd the item to add
	 */
	public void insertAfterSelected( Object toAdd ) {

		int index = getSelectedIndex() + 1; 
		model.add( index, toAdd );
		setSelectedValue( toAdd, true );
	}

	//modified from http://www.chka.de/swing/components/scrolling.html
	public static final int
	NONE = 0,
	TOP = 1,
	VCENTER = 2,
	BOTTOM = 4,
	LEFT = 8,
	HCENTER = 16,
	RIGHT = 32;
	
	public void scrollToTop() {
		scroll( TOP );
	}

	public void scroll(int part)
	{
		scroll(part & (LEFT|HCENTER|RIGHT), part & (TOP|VCENTER|BOTTOM));
	}

	public void scroll(int horizontal, int vertical)
	{
		Rectangle visible = getVisibleRect();
		Rectangle bounds = getBounds();

		switch (vertical)
		{
		case TOP:     visible.y = 0; break;
		case VCENTER: visible.y = (bounds.height - visible.height) / 2; break;
		case BOTTOM:  visible.y = bounds.height - visible.height; break;
		}

		switch (horizontal)
		{
		case LEFT:    visible.x = 0; break;
		case HCENTER: visible.x = (bounds.width - visible.width) / 2; break;
		case RIGHT:   visible.x = bounds.width - visible.width; break;
		}

		scrollRectToVisible(visible);

	} 
}
