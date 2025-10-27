package signals.gui.datagenerator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Vector;

import signals.gui.datagenerator.GUIEvent.Descriptor;

/**
 * broadcasts events to all registered listeners
 * @author julietbernstein
 *
 */
public class GUIEventBroadcaster {

    //the listener list
    @SuppressWarnings("unchecked")
	protected Vector listenerList;


	@SuppressWarnings("unchecked")
	public GUIEventBroadcaster() {
		super();
		listenerList = new Vector();
	}

	// This methods allows classes to register for GUIEvents
    @SuppressWarnings("unchecked")
	public synchronized void addGUIEventListener(GUIEventListener listener) {
        listenerList.add(new WeakReference(listener));
    }

    // This methods allows classes to unregister for GUIEvents
    @SuppressWarnings("unchecked")
	public synchronized void removeGUIEventListener(GUIEventListener listener) {
    	
    	  int size = listenerList.size();
    	    int i = 0;
    	    while (i < size) {
    	      WeakReference wr = (WeakReference)listenerList.get(i);
    	      GUIEventListener vml = (GUIEventListener)wr.get();
    	      if (vml == null) {
    	        listenerList.remove(wr);
    	        size--;
    	      } else {
    	        if (vml.equals(listener))
    	          listenerList.remove(wr);
    	          i++;
    	      }
    	    }
    }
    
    public synchronized void broadcast( Object source, Descriptor descriptor ) {
    	
    	fireGUIEvent( new GUIEvent( source, descriptor )); 
    }

    public synchronized void broadcast( Object source, Descriptor descriptor, Object value ) {
    	
    	fireGUIEvent( new GUIEvent( source, descriptor, value )); 
    }
    
    public synchronized void broadcast( Object source, Descriptor descriptor, ArrayList<Object> values ) {
    	
    	fireGUIEvent( new GUIEvent( source, descriptor, values )); 
    }
    
    // This private class is used to fire GUIEvents
    @SuppressWarnings("unchecked")
	private synchronized void fireGUIEvent(GUIEvent evt) {

  	  int size = listenerList.size();
	    int i = 0;
	    while (i < size) {
	      WeakReference wr = (WeakReference)listenerList.get(i); //TODO causing errors
	      GUIEventListener vml = (GUIEventListener)wr.get();
	      if (vml != null) {
	    	  vml.GUIEventOccurred(evt); 
	      }
	      i++; 
	    }
    }

}
