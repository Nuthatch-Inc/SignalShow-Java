package signals.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import signals.action.*;
import signals.core.Core;
import signals.core.Deconstructable;
import signals.core.FunctionProducer;
import signals.core.Savable;
import signals.io.ResourceLoader;

/**
 * Main GUI class: it creates and controls the main frame and panels
 * as well as all the buttons and widgets shown when the program
 * starts up.
 */
public class GUI {

	/*
	 * Data
	 */

	// frame
	protected JFrame frame;

	JMenuBar menuBar;

	JToolBar variableToolBar, contentToolBar;

	JPanel mainPane;

	VerticalThumbnailList functionList;

	Component content;
	Component toolBar;

	AbstractAction deleteFunctionAction, duplicateFunctionAction, saveAction, screenshotAction,
			operate1DAction, operate2DAction, convolve1DDemoAction, convolve2DDemoAction,
			lissajouDemoAction, filtering1DDemoAction, filtering2DDemoAction,
			frequencyLocations2DDemoAction, sampling1DDemoAction, saveComplexAction, loadComplexAction;

	/**
	 * 
	 */
	public GUI() {

		// Set the look and feel to this system's look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		} // TODO: exception handling

		System.setProperty("apple.laf.useScreenMenuBar", "true");

		// instantiate actions
		deleteFunctionAction = new DeleteFunctionAction();
		duplicateFunctionAction = new DuplicateFunctionAction();
		saveAction = new SaveFunctionAction();
		saveComplexAction = new SaveComplexAction();
		loadComplexAction = new LoadComplexAction();
		screenshotAction = new ScreenShotAction();
		operate1DAction = new Operate1DAction();
		operate2DAction = new Operate2DAction();
		convolve1DDemoAction = new Convolve1DDemoAction();
		convolve2DDemoAction = new Convolve2DDemoAction();
		lissajouDemoAction = new LissajouDemoAction();
		filtering1DDemoAction = new FilteringDemo1DAction();
		filtering2DDemoAction = new FilteringDemo2DAction();
		sampling1DDemoAction = new SamplingDemo1DAction();
		frequencyLocations2DDemoAction = new FrequencyLocation2DDemoAction();
		setHasFunctions1D(false);
		setHasFunctions2D(false);
		setHasFunctions(false);

		frame = new SignalShowFrame("SignalShow");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add window listener to save configuration on exit
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Save configuration before exiting
				Core.getConfig().save();
			}
		});

		configureMenuBar();
		configurePanels();
		frame.addComponentListener(new ResizeListener());
		frame.pack();
		frame.setVisible(true);
	}

	public Dimension getContentDimension() {

		return mainPane.getSize();
	}

	public void removeContent() {

		saveContent();
		mainPane.removeAll();
		if (content != null) {

			if (content instanceof Deconstructable)
				((Deconstructable) content).deconstruct();
		}
		content = null;
		toolBar = null;
		System.runFinalization();
		System.gc();
		refreshContent();
	}

	public void refreshContent() {

		mainPane.revalidate();
		mainPane.repaint();
		System.runFinalization();
		System.gc();
	}

	public void setContent(Component c) {

		saveContent();
		if (content != null) {

			if (content instanceof Deconstructable)
				((Deconstructable) content).deconstruct();
			mainPane.remove(content);
		}
		mainPane.add(c, BorderLayout.CENTER);
		refreshContent();
		content = c;
		System.runFinalization();
		System.gc();
	}

	public void setToolBar(Component c) {

		if (toolBar != null)
			mainPane.remove(toolBar);
		mainPane.add(c, BorderLayout.NORTH);
		refreshContent();
		toolBar = c;
	}

	public Component getContent() {
		return content;
	}

	public void saveContent() {

		if (content instanceof Savable)
			((Savable) content).saveStateToModel();
	}

	/**
	 * Listens for when the user resizes a component
	 */
	private class ResizeListener implements ComponentListener {

		public void componentHidden(ComponentEvent e) {
		}

		public void componentMoved(ComponentEvent e) {
		}

		public void componentResized(ComponentEvent e) {

			if (content instanceof ResizablePane) {

				((ResizablePane) content).sizeChanged();
			}

		}

		public void componentShown(ComponentEvent e) {
		}

	}

	private void configurePanels() {

		// get screen resolution from the Java toolkit
		Dimension screenres = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenres.width - 200;
		int height = screenres.height - 180;

		functionList = new VerticalThumbnailList();

		JScrollPane scroll = new JScrollPane(functionList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		scroll.setPreferredSize(new Dimension(GUIDimensions.LIST_WIDTH + getScrollBarWidth() + 30, height));
		functionList.setPreferredSize(new Dimension(GUIDimensions.LIST_WIDTH, 5 * height));

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(Core.getBorders().getBuffer());
		panel.setMinimumSize(new Dimension(GUIDimensions.LIST_WIDTH + getScrollBarWidth() + 30, 100));
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(createVariableToolBar(), BorderLayout.SOUTH);

		mainPane = new JPanel(new BorderLayout());
		mainPane.setPreferredSize(new Dimension(width, height));
		mainPane.addComponentListener(new ResizeListener());

		mainPane.setOpaque(true);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				panel, mainPane);

		content = new IconPanel(ResourceLoader.createImageIcon("/guiIcons/welcome.png"));
		setContent(content);

		frame.setContentPane(splitPane);

	}

	private JPanel createVariableToolBar() {

		JPanel variableToolBar = new JPanel();
		variableToolBar.add(new JButton(duplicateFunctionAction));
		variableToolBar.add(new JButton(deleteFunctionAction));
		return variableToolBar;
	}

	/**
	 * Adds the desired actions to the menu bar.
	 */
	private void configureMenuBar() {

		menuBar = new JMenuBar();

		// file menu
		JMenu fileMenu = new JMenu("File");

		fileMenu.add(saveAction); // save function
		fileMenu.add(screenshotAction); // save function
		fileMenu.add(saveComplexAction);
		fileMenu.add(loadComplexAction);

		// New 1D Menu
		JMenu new1DMenu = new JMenu("1D");
		new1DMenu.add(new NewReImFunction1DAction());
		new1DMenu.add(new NewMagPhaseFunction1DAction());

		JMenu complex1DMenu = new JMenu("New 1D Predefined Complex Function");
		complex1DMenu.setIcon(ResourceLoader.createImageIcon("/guiIcons/complex1D.png"));
		complex1DMenu.add(new NewComplexChirp1DAction());
		complex1DMenu.add(new NewComplexSinusoid1DAction());
		new1DMenu.add(complex1DMenu);

		new1DMenu.addSeparator();
		new1DMenu.add(operate1DAction);

		new1DMenu.addSeparator();

		new1DMenu.add(convolve1DDemoAction);
		new1DMenu.add(filtering1DDemoAction);
		new1DMenu.add(sampling1DDemoAction);

		new1DMenu.addSeparator();

		new1DMenu.add(lissajouDemoAction);

		// New 2D Menu
		JMenu new2DMenu = new JMenu("2D");
		new2DMenu.add(new NewReImFunction2DAction());
		new2DMenu.add(new NewMagPhaseFunction2DAction());

		JMenu complex2DMenu = new JMenu("New 2D Predefined Complex Function");
		complex2DMenu.setIcon(ResourceLoader.createImageIcon("/guiIcons/complex1D.png"));
		complex2DMenu.add(new NewComplexChirp2DAction());
		complex2DMenu.add(new NewRadialComplexSinusoid2DAction());
		complex2DMenu.add(new NewFourierBasisFunction2DAction());
		new2DMenu.add(complex2DMenu);

		new2DMenu.addSeparator();
		new2DMenu.add(operate2DAction);

		new2DMenu.addSeparator();
		new2DMenu.add(convolve2DDemoAction);
		new2DMenu.add(filtering2DDemoAction);
		new2DMenu.add(frequencyLocations2DDemoAction);

		// help
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new AboutAction());

		menuBar.add(fileMenu);
		menuBar.add(new1DMenu);
		menuBar.add(new2DMenu);
		menuBar.add(helpMenu);

		frame.setJMenuBar(menuBar);

	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * @return the function1DList
	 */
	public VerticalThumbnailList getFunction1DList(FunctionProducer excludeSystem) {
		return functionList.getFunction1DList(excludeSystem);
	}

	/**
	 * @return the function2DList
	 */
	public VerticalThumbnailList getFunction2DList(FunctionProducer excludeSystem) {
		return functionList.getFunction2DList(excludeSystem);
	}

	/**
	 * @return the function1DList
	 */
	public VerticalThumbnailList getFunction1DList() {
		return functionList.getFunction1DList();
	}

	/**
	 * @return the function2DList
	 */
	public VerticalThumbnailList getFunction2DList() {
		return functionList.getFunction2DList();
	}

	/**
	 * @return the systemList
	 */
	public VerticalThumbnailList getFunctionList() {
		return functionList;
	}

	/*
	 * Call this with TRUE as the parameter if the first function has been created
	 * Call this with FALSE as the parameter if the last function has been deleted
	 */
	public void setHasFunctions(boolean tf) {

		deleteFunctionAction.setEnabled(tf);
		duplicateFunctionAction.setEnabled(tf);
		saveAction.setEnabled(tf);
		saveComplexAction.setEnabled(tf);
		screenshotAction.setEnabled(tf);

	}

	public void setHasFunctions1D(boolean tf) {

		operate1DAction.setEnabled(tf);
		convolve1DDemoAction.setEnabled(tf);
		filtering1DDemoAction.setEnabled(tf);
		sampling1DDemoAction.setEnabled(tf);
	}

	public void setHasFunctions2D(boolean tf) {

		operate2DAction.setEnabled(tf);
		convolve2DDemoAction.setEnabled(tf);
		filtering2DDemoAction.setEnabled(tf);
		frequencyLocations2DDemoAction.setEnabled(tf);
	}

	/**
	 * 
	 * @return the width of a scrollbar in this GUI
	 */
	public static int getScrollBarWidth() {

		return Integer.parseInt(UIManager.getDefaults().get("ScrollBar.width").toString());
	}

	@SuppressWarnings("serial")
	public class IconPanel extends JPanel {

		ImageIcon icon;

		public IconPanel(ImageIcon icon) {

			this.icon = icon;
			setBackground(new Color(0, 0, 0, 0));
		}

		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			icon.paintIcon(this, g, (getWidth() - icon.getIconWidth()) / 2, (getHeight() - icon.getIconHeight()) / 2);

		}
	}

}
