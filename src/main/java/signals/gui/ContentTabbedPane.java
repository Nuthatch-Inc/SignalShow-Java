package signals.gui;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import signals.io.ResourceLoader;


@SuppressWarnings("serial")
public class ContentTabbedPane extends JPanel { 

	int nextTabIndex; 
	SubTabbedPane tabbedPane; 
	IconPanel iconPanel;
	CardLayout layout; 

	//	these tags identify each card
	final static String TABBED = "t";
	final static String ICON = "i";

	static final int[] TAB_MNEMONICS = { KeyEvent.VK_0, KeyEvent.VK_1, 
		KeyEvent.VK_2, KeyEvent.VK_3, 
		KeyEvent.VK_4, KeyEvent.VK_5,
		KeyEvent.VK_6, KeyEvent.VK_7, 
		KeyEvent.VK_8, KeyEvent.VK_9}; 

	public ContentTabbedPane() {

		tabbedPane = new SubTabbedPane();	
		nextTabIndex = 0;	
		iconPanel = new IconPanel(ResourceLoader.createImageIcon("/guiIcons/welcome.png"));

		layout = new CardLayout();
		setLayout( layout );
		add( tabbedPane, TABBED );
		add( iconPanel, ICON );
		layout.show(this, ICON);
	}

	public void showIconPanel() { 

		layout.show(this, ICON);
	}

	public void showTabbedPane() { 

		layout.show(this, TABBED);
	}
	
	public Component getSelectedTab() {
		
		return tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
	}

	public void addTab(String title, Component c) {

		tabbedPane.addTab( title, c );
	}

	public Dimension getContentSize() {

		Dimension d = getSize(); 
		return new Dimension( d.width-5, d.height - 20 );
	}

	public void removeSelectedTab() {
		
		tabbedPane.remove(tabbedPane.getSelectedIndex()); 
	}
	
	public class SubTabbedPane extends JTabbedPane {

		public SubTabbedPane() {
			
			addChangeListener( new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					
					int selectedIndex = getSelectedIndex(); 
					
					if( getTabCount() > 0 && selectedIndex > -1 ) {
						Component c = getComponentAt( getSelectedIndex() );
//						if (c instanceof ToolBarContributor ) {
//							
//							Core.getGUI().setTempToolBarObjects(((ToolBarContributor) c).getToolBarComponents()); 
//							
//						} else {
//							
//							Core.getGUI().setTempToolBarObjects(null);
//						}
						
						if( c instanceof ResizablePane ) ((ResizablePane) c).sizeChanged();
						
					} 
//					else {
//						
//						Core.getGUI().setTempToolBarObjects(null);
//					}
				}

			});
		}

		public void addTab( String title, Component c) {

			//first check to see if next tab index is 10 
			if( nextTabIndex == 10 ) {

				removeTabAt( 9 );
				super.addTab( "9 " + title, c );
				setMnemonicAt( 9, KeyEvent.VK_9);
				setSelectedIndex( 9 ); 

			} else {

				super.addTab( ""+ nextTabIndex + " " + title, c ); 
				setMnemonicAt( nextTabIndex, TAB_MNEMONICS[nextTabIndex] ); 
				setSelectedIndex( nextTabIndex );
				nextTabIndex++; 
			}

			if( getTabCount() == 1 ) showTabbedPane();

		}

		public void removeAll() {

			tabbedPane.removeAll(); 
			nextTabIndex = 0;
		}

		public void remove( int tabIndex ) {

			super.remove( tabIndex );

			//for each tab after this one, slide mnemonics down

			int numTabsLeft = tabbedPane.getTabCount(); 
			int tabToSet = tabIndex; //the tab that was ahead of the tab just removed

			while( tabToSet < numTabsLeft ) {

				StringBuffer title = new StringBuffer( tabbedPane.getTitleAt( tabToSet ) );
				title.setCharAt( 0, (char)( 48 + tabToSet) );
				tabbedPane.setTitleAt( tabToSet, title.toString() );
				tabbedPane.setMnemonicAt( tabToSet, TAB_MNEMONICS[tabToSet] );
				tabToSet++; 
			} 

			nextTabIndex--;
			if( getTabCount() == 0 ) showIconPanel();
		}
	}

	public class IconPanel extends JPanel {

		ImageIcon icon; 

		public IconPanel( ImageIcon icon) {

			this.icon = icon; 
			setBackground( new Color( 240, 240, 240 ) );
		}

		public void paintComponent( Graphics g ) {

			super.paintComponent(g);
			icon.paintIcon(this, g, (getWidth()-icon.getIconWidth())/2, (getHeight()-icon.getIconHeight())/2);

		}
	}

}