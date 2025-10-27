import signals.core.Core;



/**
 * Main entry point for the Program
 */
public class SignalShow {

	public static void main(String[] args) {

		//display the splash screen
		//final SplashScreen splash = SplashScreen.getSplashScreen();

		//Schedule the event-dispatching thread to create and
		//show the main GUI. 
		javax.swing.SwingUtilities.invokeLater( new Runnable() {
			public void run() {

				//new SplashPage();
				Core.get().launchMainGUI(); 
			} 
		} );

		//if( splash != null ) splash.close();

	} //main

}//SignalsShow
