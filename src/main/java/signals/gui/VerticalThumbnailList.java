package signals.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import signals.core.Core;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionProducer;
import signals.core.OperatorSystem1D;
import signals.core.OperatorSystem2D;
import signals.core.Showable;

//A variable table that stores variables the user creates
@SuppressWarnings("serial")
public class VerticalThumbnailList extends JList { 

	DefaultListModel model;
	boolean selecting;
	protected int mouseOver;

	/**
	 * Creates a new Object list with elements of size "size"
	 * @param renderer renderer to render the cells in this list
	 */
	public VerticalThumbnailList() {

		model = new DefaultListModel();
		setModel( model );
		setCellRenderer( new ThumbnailListRenderer() );
		setFixedCellHeight(-1);
		selecting = true;  

		//code from http://www.digitalredemption.net/2006/01/adding-rollovers-to-jlist-comp.html
		mouseOver = -1;

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				mouseOver = locationToIndex(new Point(e.getX(), e.getY()));
				repaint();
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				mouseOver = -1;
				repaint();
			}
		});

		addListSelectionListener( new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {

				if( !e.getValueIsAdjusting() && getSelectedIndex() >= 0 ) {

					if( selecting ) { 
						select( getSelectedIndex() ); 
					}
				}

			}
		}); 

		
		addComponentListener(new ComponentListener(){
	        public void valueChanged(ComponentEvent e){
	          //if(lse.getValueIsAdjusting() == false) list.setCellRenderer(new MyRenderer());}});//reset
	          //this block comes from this recent post, and would be better
	            //http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=2&t=006729
	            //comment out the above reset line, and uncomment this block
	          //if(e. == false)  {
	        	
	        	DefaultListModel listModel = (DefaultListModel)getModel(); 
	            for(int x = 0; x < listModel.size(); x++)
	            {
	              listModel.setElementAt(listModel.getElementAt(x),x);
	            }
	          }//}

			public void componentHidden(ComponentEvent e) {
			
				valueChanged(e); 
			}

			public void componentMoved(ComponentEvent e) {
				
				valueChanged(e); 
			}

			public void componentResized(ComponentEvent e) {
				
				//TODO: resize/repaint all elements in list
				
				valueChanged(e); 
			}

			public void componentShown(ComponentEvent e) {
				
				valueChanged(e); 
			}});
	} 

	public void select( int index ) {

		//select this function, free up memory in other functions
		for( int i = 0; i < model.size(); ++i ) {

			Object item = getItemAt(index);

			if( i == index ) {

				((Showable)item).show();

			} else {

				((Showable)model.get(i)).freeMemory(); 

			}

		}           

		System.runFinalization(); 
		System.gc(); 

	}

	public int getMouseOver() {
		return mouseOver;
	}

	public boolean isSelecting() {
		return selecting;
	}

	public void setSelecting(boolean selecting) {
		this.selecting = selecting;
	}

	public VerticalThumbnailList clone() {

		VerticalThumbnailList newList = new VerticalThumbnailList(); 

		for( int i = 0; i < model.getSize(); i++ ) {

			newList.addItem(model.get(i));
		}

		return newList; 
	}

	public VerticalThumbnailList getFunction1DList() {

		return getFunction1DList( null );

	}

	public VerticalThumbnailList getFunction1DList( FunctionProducer exclude ) {

		VerticalThumbnailList newList = new VerticalThumbnailList(); 
		newList.setSelecting(false); 

		for( int i = 0; i < model.getSize(); i++ ) {

			Object item = model.get(i); 

			if( !item.equals(exclude) ) {

				if( item instanceof Function1D) {

					newList.addItem(item);

				} else if (item instanceof OperatorSystem1D) {

					if( ((OperatorSystem1D) item).getFunction() != null ) {

						newList.addItem(item);
					}
				}
			} 

		}

		return newList; 

	}

	public VerticalThumbnailList getFunction2DList() {

		return getFunction2DList( null ); 
	}

	public VerticalThumbnailList getFunction2DList( FunctionProducer exclude ) {

		VerticalThumbnailList newList = new VerticalThumbnailList(); 
		newList.setSelecting(false); 

		for( int i = 0; i < model.getSize(); i++ ) {

			Object item = model.get(i); 

			if( !item.equals(exclude) ) {

				if( item instanceof Function2D) {

					newList.addItem(item);

				} else if (item instanceof OperatorSystem2D) {

					if( ((OperatorSystem2D) item).getFunction() != null ) {

						newList.addItem(item);
					}
				}
			} 

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
		 Object item = getSelectedValue();
		 
		 removeItem( item );
		 Core.getGUI().removeContent(); 
		 if( getListSize() > 0 ) {

			 setSelectedIndex( Math.max( 0 , selectedIndex-1));

		 }  

	 }


	 public void refresh() {

		 repaint(); 
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
