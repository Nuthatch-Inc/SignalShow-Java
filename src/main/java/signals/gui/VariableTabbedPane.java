package signals.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class VariableTabbedPane extends JPanel {

	int selectedIndex; 
	CardLayout layout; 
	JPanel buttonPanel; 
	JPanel contentPanel; 
	int numTabs; 

	public VariableTabbedPane() {

		setLayout( new BorderLayout() ); 
		buttonPanel = new JPanel(); 
		add( buttonPanel, BorderLayout.NORTH ); 

		contentPanel = new JPanel(); 
		layout = new CardLayout(); 
		contentPanel.setLayout(layout); 
		add( contentPanel, BorderLayout.CENTER); 

		numTabs = 0; 

	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
		layout.show(contentPanel, ""+selectedIndex); 
	}

	public void addTab( ImageIcon icon, JComponent content ) {

		buttonPanel.add( new JButton(new TabButtonAction(icon, numTabs) ) ); 
		contentPanel.add(content, ""+numTabs); 
		numTabs++; 
		revalidate(); 
		repaint(); 
	}

	public class TabButtonAction extends AbstractAction {

		int index;

		public TabButtonAction(ImageIcon icon, int index) {
			super( "", icon );
			this.index = index;
		} 

		public void actionPerformed(ActionEvent e) {
			
			setSelectedIndex( index ); 
		}

	}
}
