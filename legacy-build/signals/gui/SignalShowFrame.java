package signals.gui;

import java.awt.HeadlessException;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class SignalShowFrame extends JFrame {

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public SignalShowFrame(String title) throws HeadlessException {
		super(title);
		setIconImage( IconCache.getIcon("/guiIcons/smallFrame.png").getImage() ); 
			
	}

}
