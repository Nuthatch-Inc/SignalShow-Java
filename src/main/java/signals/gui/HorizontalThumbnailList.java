package signals.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class HorizontalThumbnailList extends JTable { 
	
	protected MyDefaultTableModel model;
	
	int mouseOver; 
	
	/**
	 * Creates a new Object Table with elements of size "size"
	 * @param renderer renderer to render the cells in this Table
	 */
	public HorizontalThumbnailList( Dimension size ) {
	
		super( 1, 0 );
		setPreferredSize( size );
		setRowHeight(size.height); 
		model = new MyDefaultTableModel();
		setModel( model );
		setDefaultRenderer( Object.class, new ThumbnailListRenderer() );
		setShowGrid( false );
		getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		setColumnSelectionAllowed(true);
		setRowSelectionAllowed(false);
		setTableHeader(null);
		setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		setAutoCreateColumnsFromModel(false);
		
		//code from http://www.digitalredemption.net/2006/01/adding-rollovers-to-jlist-comp.html
        mouseOver = -1;
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                mouseOver = columnAtPoint(new Point(e.getX(), e.getY()));
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                mouseOver = -1;
                repaint();
            }
        });
		
	} 
	
	public int getMouseOver() {
		return mouseOver;
	}


	public boolean isCellEditable( int row, int col ) {
		
		return false;
	}


	public void setSelected( int index ) {
		
		setColumnSelectionInterval( index, index );
		scrollRectToVisible( getCellRect( 0, index, true ) );
	}
	
	
	/**
	 * Removes all Objects from this Table
	 */
	public void removeAllItems() {
		
		model = new MyDefaultTableModel();
		setModel( model );
	}
	
	
	/**
	 * Get the number of elements in this Table
	 * @return the number of elements in the Table
	 */
	public int getListSize() {
		
		return ( model.getColumnCount() );
	}
	
	/**
	 * Adds a Object and set it as the selected Object
	 * @param newEntry the item to add
	 */ 
	public void addItem( ThumbnailProducer newEntry ) {
		
		Object[] data = { newEntry }; 	
		addColumn(new TableColumn(model.getColumnCount()));
		model.addColumn( null, data );
		setSelected( getColumnCount()-1 );
		setColumnWidths();
	}

	
	/**
	 * Return the Object at given index
	 * @param index get Object at this VISIBLE index
	 * @return the Object at the provided index
	 */
	public Object getItemAt( int index ) {
		
		return model.getValueAt( 0, getColumnModel().getColumn(index).getModelIndex() ); //row 0
		
	} 
	
	/**
	 * Remove the currently selected Object
	 */
	@SuppressWarnings("unchecked")
	public void removeSelected() {
		
		int selectedIndex = getSelectedColumn(); 
		
		TableColumn col = getColumnModel().getColumn(selectedIndex);
        int columnModelIndex = col.getModelIndex();
        Vector data = model.getDataVector();
        Vector colIds = model.getColumnIdentifiers();

        // Remove the column header from the table model
        colIds.removeElementAt(columnModelIndex);
        
        //Remove the column from the table
        removeColumn(col);
    
        // Remove the column data
        for (int r=0; r<data.size(); r++) {
            Vector row = (Vector)data.get(r);
            row.removeElementAt(columnModelIndex);
        }
        model.setDataVector(data, colIds);
    
        // Correct the model indices in the TableColumn objects
        // by decrementing those indices that follow the deleted column
        Enumeration enum1 = getColumnModel().getColumns();
        for (; enum1.hasMoreElements(); ) {
            TableColumn c = (TableColumn)enum1.nextElement();
            if (c.getModelIndex() >= columnModelIndex) {
                c.setModelIndex(c.getModelIndex()-1);
            }
        }
        model.fireTableStructureChanged();
		setSelected( Math.max( selectedIndex-1, 0) );
		setColumnWidths();
		
	}
	
	/**
	 * 
	 * @return the selected index
	 */
	public int getSelectedIndex() {
		
		return getSelectedColumn(); 
	}
	
	public boolean isLastElementSelected() {
		
		return( getSelectedIndex() == (getListSize()-1) );
	}
	
	/**
	 * Moves the cursor one position to the right
	 *
	 */
	public void selectNext() {
		
		setSelected( Math.min( model.getColumnCount()-1, getSelectedColumn()+1));
	}
	
	
	public void selectPrev() {
		
		setSelected( Math.max( 0 , getSelectedColumn()-1));
	}
	
	
	/**
	 * Get the currently selected Object
	 * @return the selected Object
	 */
	public Object getSelectedItem() {
		
		return getValueAt( 0, getSelectedColumn() );
	}
	
	/**
	 * Remove the Object at a selected index
	 * @param index remove the Object at this index
	 */
	public void removeItemAt( int index ) {
		
		removeColumn( getColumnModel().getColumn(index) );
		setSelected( Math.max( index-1, 0) );
		
	}
	
	/**
	 * Inserts the given item into the Table after the currently selected item 
	 * @param toAdd the item to add
	 */
	public void insertAfterSelected( Object newEntry ) {
		
		int index = getSelectedColumn() + 1; 
		Object[] data = { newEntry }; 	
		addColumn(new TableColumn(model.getColumnCount()));
		model.addColumn( null, data );
		moveColumn( getColumnCount()-1, index );
		setSelected( index );
		setColumnWidths();
	}
	
	 // This subclass adds a method to retrieve the columnIdentifiers
    // which is needed to implement the removal of
    // column data from the table model
    class MyDefaultTableModel extends DefaultTableModel {
        @SuppressWarnings("unchecked")
		public Vector getColumnIdentifiers() {
            return columnIdentifiers;
        }
    }
    
	public void setColumnWidths() { 
		
		for( int i = 0; i < getColumnCount(); i++ ) {
			ThumbnailProducer item = (ThumbnailProducer) getValueAt( 0, i );
			getColumnModel().getColumn(i).setMaxWidth(item.getSmallThumbnailSize().width);
		}
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

	public void scrollToLeft() {
		scroll( LEFT );
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

