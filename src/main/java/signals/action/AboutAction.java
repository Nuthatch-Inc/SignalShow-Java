package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import signals.core.Core;
import signals.gui.IconCache;

@SuppressWarnings("serial")
public class AboutAction extends AbstractAction {

	public AboutAction() {
		super( "About SignalShow", IconCache.getIcon("/guiIcons/SignalShowIconSmall.png") );
	} 
	
	public void actionPerformed(ActionEvent e) {
		
		JOptionPane.showMessageDialog(Core.getFrame(),
			    "SignalShow 1.2.0 \n" +
			    "Copyright Juliet Bernstein 2009 \n" + 
			    "By: Juliet Bernstein \n" +
			    "Advisor: Dr. Roger L. Easton Jr. \n" +
			    "Special Thanks: Xander Fiss \n\n" + 
			    "Contact: SignalShow@gmail.com \n",
			    "About SignalShow",
			    JOptionPane.INFORMATION_MESSAGE,
			    IconCache.getIcon("/guiIcons/SignalShowIcon.png"));

	}
}
