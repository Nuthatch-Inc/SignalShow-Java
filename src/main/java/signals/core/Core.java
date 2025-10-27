package signals.core;

import javax.swing.JFrame;

import signals.gui.Colors;
import signals.gui.GUI;
import signals.gui.VerticalThumbnailList;
import signals.gui.Borders;

/**
 * This is the main access point for all core functionality
 * in SignalShow. Allows control and creation of functionCreationOptions,
 * variables, the GUI, and various widgets. The class is
 * implemented using the singleton design pattern. Other
 * classes should call methods like this: Core.get().method()
 * This class also acts as a wrapper for various parts of the
 * interface and other core functionality.
 */
public class Core {

	/**
	 * this gets rid of exception for not using native acceleration
	 */
	static {
		System.setProperty("com.sun.media.jai.disableMediaLib", "true");
	}

	// GUI
	GUI gui;

	// Options
	FunctionCreationOptions functionCreationOptions;
	DisplayOptions displayOptions;
	IOOptions ioOptions;

	// Configuration
	Config config;

	// Borders
	Borders borders;

	// Colors
	Colors colors;

	// Data Generators
	DataGeneratorCollections dataGeneratorCollections;

	// number of 1D and 2D functions that the user can interact with
	public static int num1DFunctions, num2DFunctions;

	/**
	 * Private constructor suppresses generation of a (public) default constructor
	 */
	private Core() {
	}

	/**
	 * SingletonCoreHolder is loaded on the first execution of
	 * Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonCoreHolder {

		private final static Core INSTANCE = new Core();

	}

	/**
	 * Accessor for the singleton instance of Core
	 * 
	 * @return the singleton instance of Core
	 */
	public static Core get() {

		return SingletonCoreHolder.INSTANCE;

	}

	/**
	 * @return the functionCreationOptions
	 */
	public static FunctionCreationOptions getFunctionCreationOptions() {
		return get().functionCreationOptions;
	}

	/**
	 * @return the widgets
	 */
	public static Borders getBorders() {
		return get().borders;
	}

	/**
	 * 
	 * @return the main gui
	 */
	public static GUI getGUI() {

		return get().gui;
	}

	/**
	 * 
	 * @return the colors
	 */
	public static Colors getColors() {

		return get().colors;
	}

	/**
	 * @return the displayOptions
	 */
	public static DisplayOptions getDisplayOptions() {
		return get().displayOptions;
	}

	/**
	 * @return the configuration
	 */
	public static Config getConfig() {
		return get().config;
	}

	/**
	 * @return the dataGeneratorCollections
	 */
	public static DataGeneratorCollections getDataGeneratorCollections() {
		return get().dataGeneratorCollections;
	}

	public static IOOptions getIOOptions() {

		return get().ioOptions;
	}

	/**
	 * 
	 * @return the main frame
	 */
	public static JFrame getFrame() {

		return getGUI().getFrame();
	}

	public static VerticalThumbnailList getFunction1DList() {

		return getGUI().getFunction1DList();
	}

	public static VerticalThumbnailList getFunction2DList() {

		return getGUI().getFunction2DList();
	}

	public static VerticalThumbnailList getFunctionList() {

		return getGUI().getFunctionList();
	}

	public static void addToMainList(Object item) {

		getFunctionList().addItem(item);
		if (item instanceof Function1D)
			incrementNum1DFunctions();
		else if (item instanceof Function2D)
			incrementNum2DFunctions();
	}

	public static void removeSelectedFromMainList() {

		Object item = getFunctionList().getSelectedItem();

		if (item instanceof Function1D) {

			Core.decrementNum1DFunctions();

		} else if (item instanceof Function2D) {

			Core.decrementNum2DFunctions();
		}

		getFunctionList().removeSelected();

	}

	public static int getNum1DFunctions() {
		return num1DFunctions;
	}

	public static void incrementNum1DFunctions() {
		num1DFunctions++;
		if (num1DFunctions == 1) {
			Core.getGUI().setHasFunctions1D(true);
		}
		if (num2DFunctions == 0)
			Core.getGUI().setHasFunctions(true);
	}

	public static void decrementNum1DFunctions() {
		num1DFunctions--;
		if (num1DFunctions == 0) {
			Core.getGUI().setHasFunctions1D(false);
			if (num2DFunctions == 0)
				Core.getGUI().setHasFunctions(false);
		}
	}

	public static int getNum2DFunctions() {
		return num2DFunctions;
	}

	public static void incrementNum2DFunctions() {
		num2DFunctions++;
		if (num2DFunctions == 1) {
			Core.getGUI().setHasFunctions2D(true);
			if (num1DFunctions == 0)
				Core.getGUI().setHasFunctions(true);
		}
	}

	public static void decrementNum2DFunctions() {
		num2DFunctions--;
		if (num2DFunctions == 0) {
			Core.getGUI().setHasFunctions2D(false);
			if (num1DFunctions == 0)
				Core.getGUI().setHasFunctions(false);
		}
	}

	/**
	 * Launches the main user interface in a new window
	 */
	public void launchMainGUI() {

		num1DFunctions = num2DFunctions = 0;
		config = new Config(); // Load user preferences
		dataGeneratorCollections = new DataGeneratorCollections();
		functionCreationOptions = new FunctionCreationOptions();
		displayOptions = new DisplayOptions();
		colors = new Colors();
		borders = new Borders();
		ioOptions = new IOOptions();
		gui = new GUI();
	}

}
