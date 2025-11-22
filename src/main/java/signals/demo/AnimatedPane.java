package signals.demo; 

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

import signals.gui.IconCache;
import signals.gui.ResizablePane;


/**
 * This is the base class for animated demonstrations
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public abstract class AnimatedPane extends JPanel implements ResizablePane {
	
	public static int TIMER_INCREMENT = 50; //faster/slower by this number of milliseconds
	
	//the swing timer controlling the animation
	Timer timer; 
	int timeOut;
	
	//actions for all of the buttons
	protected Action playAction, stopAction, stepAction, toBeginAction, toEndAction, fasterAction, slowerAction, stepBackAction, exportAction;
	
	public AnimatedPane() {
	
		//
		// set up the timer
		//
		timeOut = TIMER_INCREMENT * 3; 
		timer = new Timer( timeOut, new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				
				if( lastStep() ) {
					
					//similar to calling stop 
					setEnabled( false ); 
					stopAction.setEnabled( false ); 
					playAction.setEnabled( false );
					fasterAction.setEnabled( false ); 
					slowerAction.setEnabled( false );
					toEndAction.setEnabled( false );
					toBeginAction.setEnabled( true ); 
					stepBackAction.setEnabled( true );
					
				} else {
					
					step();
				}
			}
		});
		
		//create all the actions
		playAction = new PlayAction(); 
		stepAction = new StepAction(); 
		stopAction = new StopAction();
		toBeginAction = new ToBeginAction();
		toEndAction = new ToEndAction();
		fasterAction = new FasterAction(); 
		slowerAction = new SlowerAction();
		exportAction = new ExportAction();
		stepBackAction = new StepBackAction();
		
		stopAction.setEnabled( false );
		fasterAction.setEnabled( false ); 
		slowerAction.setEnabled( false );
		toBeginAction.setEnabled( false ); //already at the beginning
		stepBackAction.setEnabled( false );
		exportAction.setEnabled(false); 
		
	} 
	
	/**
	 * Demos should call this function to add a toolbar with buttons 
	 */
	public JComponent createAnimationToolBar() {
		
		JPanel animationToolBar = new JPanel(); 
		
		animationToolBar.add( new JButton( toBeginAction ) );
		animationToolBar.add( new JButton(slowerAction) );
		animationToolBar.add( new JButton(stepAction) );
		animationToolBar.add( new JButton(playAction) );
		animationToolBar.add( new JButton(stopAction) );
		animationToolBar.add( new JButton(fasterAction) );
		animationToolBar.add( new JButton(toEndAction) );
		
		return animationToolBar; 
		
	} 
	
	/**
	 * Demos should override the following methods
	 */
	public void export() {} 
	
	public boolean lastStep() {
		
		return false;
	} 
	
	public boolean firstStep() {
		
		return false;
	}
	
	public void toEnd() {} 

	public void toBegin() {} 
	
	public void step() {}
	
	public void stepBack() {}

	public class PlayAction extends AbstractAction {
		
		public PlayAction() {
		
			super( "", IconCache.getIcon("/demoIcons/play.png") );
			
		}

		public void actionPerformed( ActionEvent e ) {
		
			play(); 
			
		}
	
	} 
	
	public void play() {
		
		playAction.setEnabled( false ); //already pressed
		stepAction.setEnabled( false ); //either in step mode or play mode
		stopAction.setEnabled( true ); //can be stopped
		fasterAction.setEnabled( true ); //can be sped up
		slowerAction.setEnabled( true ); //can be slowed down
		toEndAction.setEnabled( true ); //can be moved to the beginning
		toBeginAction.setEnabled( true ); //can be moved to the end
		stepBackAction.setEnabled( false );
		exportAction.setEnabled(true); 
		//start the timer
		timer.start();
	}
	
	public class StopAction extends AbstractAction {
		
		public StopAction() {
			
			super( "", IconCache.getIcon("/demoIcons/stop.png") );
			
		}

		public void actionPerformed( ActionEvent e ) {
		
			stop();
	
		}
	
	}
	
	public void stop() {
		
		stopAction.setEnabled( false ); 
		stepAction.setEnabled( true );
		playAction.setEnabled( true );
		fasterAction.setEnabled( false ); 
		slowerAction.setEnabled( false );
		toEndAction.setEnabled( true ); 
		toBeginAction.setEnabled( true );
		stepBackAction.setEnabled( true );
		
		//stop the timer
		if( timer.isRunning() ) {
					
			timer.stop();
		} 
	}
	
	public class StepAction extends AbstractAction {
		
		public StepAction() {
			
			super( "", IconCache.getIcon("/demoIcons/step.png") );
			
		}

		public void actionPerformed( ActionEvent e ) {
			
			stepBackAction.setEnabled( true );
			
			if( lastStep() ) {
				
				//similar to calling stop 
				setEnabled( false ); 
				stopAction.setEnabled( false ); 
				playAction.setEnabled( false );
				fasterAction.setEnabled( false ); 
				slowerAction.setEnabled( false );
				toEndAction.setEnabled( false );
				toBeginAction.setEnabled( true ); 
				
			} else {
			
				stepForward(); 
				exportAction.setEnabled(true); 
			}
			
		}
	
	}
	
	public void stepForward() {
	
		playAction.setEnabled( true ); 
		toEndAction.setEnabled( true );
		toBeginAction.setEnabled( true );
		step();
		
	}
	
	public class StepBackAction extends AbstractAction {
		
		public StepBackAction() {
			
			super( "step back" );
			
		}

		public void actionPerformed( ActionEvent e ) {
			
			if( firstStep() ) {
				
				setEnabled( false ); 
				stopAction.setEnabled( false );
				stepAction.setEnabled( true );
				toBeginAction.setEnabled( false );
				playAction.setEnabled( true ); 
				
			} else {
				
				stepBack();
			} 
		}
	
	} 

	
	public class SlowerAction extends AbstractAction {
		
		public SlowerAction() {
			
			super( "", IconCache.getIcon("/demoIcons/slower.png") );
			
		}

		public void actionPerformed( ActionEvent e ) {
			
			fasterAction.setEnabled( true );
			
			//set timer to larger increment
			timeOut += TIMER_INCREMENT; 
			timer.setDelay( timeOut );
		}
	
	} 
	
	public class FasterAction extends AbstractAction {
		
		public FasterAction() {
			
			super( "", IconCache.getIcon("/demoIcons/faster.png"));
			
		}

		public void actionPerformed( ActionEvent e ) {
			
			timeOut -= TIMER_INCREMENT; 
			timer.setDelay( timeOut );
			if( timeOut <= 0 )  setEnabled( false );
			
//			//set timer to larger increment
//			if( timeOut <= TIMER_INCREMENT ) { 
//				
//				setEnabled( false );
//				
//			} else {
//				
//				timeOut -= TIMER_INCREMENT; 
//				timer.setDelay( timeOut );
//			}
			
			slowerAction.setEnabled( true );
		
		}
	
	} 
	
	public class ToBeginAction extends AbstractAction {
		
		public ToBeginAction() {
			
			super( "", IconCache.getIcon("/demoIcons/toBegin.png") );
			
		}

		public void actionPerformed( ActionEvent e ) {
			
			//stop the timer
			if( timer.isRunning() ) {
						
				timer.stop();
			} 
			
			setEnabled( false );
			stepAction.setEnabled( true ); 
			stopAction.setEnabled( false ); 
			playAction.setEnabled( true );
			fasterAction.setEnabled( false ); 
			slowerAction.setEnabled( false );
			toEndAction.setEnabled( true );
			stepBackAction.setEnabled( false );
			exportAction.setEnabled(false); 
			
			toBegin(); 
		
		}
	
	} 
	
	public class ToEndAction extends AbstractAction {
		
		public ToEndAction() {
			
			super( "", IconCache.getIcon("/demoIcons/toEnd.png") );
			
		}

		public void actionPerformed( ActionEvent e ) {
			
			//stop the timer
			if( timer.isRunning() ) { 
						
				timer.stop();
			} 
			
			setEnabled( false );
			stepAction.setEnabled( false ); 
			stopAction.setEnabled( false ); 
			playAction.setEnabled( false );
			fasterAction.setEnabled( false ); 
			slowerAction.setEnabled( false );
			toBeginAction.setEnabled( true );
			stepBackAction.setEnabled( true );
			
			toEnd(); 
		
		}
	
	}
	
	public class ExportAction extends AbstractAction {
		
		public ExportAction() {
			
			super( "Save Result As Data " );
			
		}

		public void actionPerformed( ActionEvent e ) {
			
			export(); 
		}
	
	} 
	
}