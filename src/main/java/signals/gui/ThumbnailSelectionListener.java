/**
 * 
 */
package signals.gui;

import java.util.EventListener;

/**
 * @author Juliet
 *
 */
public interface ThumbnailSelectionListener extends EventListener {

	public void selectionChanged( ThumbnailSelectionEvent e );
}
