package signals.gui.operation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import signals.core.Operation;
import signals.operation.Directional2DOperation;

@SuppressWarnings("serial")
public class DirectionOptionsPanel extends OperationOptionsPanel implements ActionListener {

	JRadioButton XRB, YRB; 
	
	public DirectionOptionsPanel( Operation dependent ) {
		
		super( dependent );
		
		//add radio button group for direction 
		XRB = new JRadioButton( "X-Direction"); 
		YRB = new JRadioButton("Y-Direction"); 
		
		XRB.addActionListener( this ); 
		YRB.addActionListener( this ); 
		
		ButtonGroup directionGroup = new ButtonGroup(); 
		directionGroup.add(XRB); 
		directionGroup.add(YRB);
		
		if( isXDirection() ) XRB.setSelected(true); else YRB.setSelected(true); 
		
		XRB.setSelected(isXDirection()); 
		
		JPanel directionPanel = new JPanel(); 
		directionPanel.setLayout(new BoxLayout( directionPanel, BoxLayout.PAGE_AXIS ) ); 
		directionPanel.add(XRB); 
		directionPanel.add(YRB); 
		
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border title = BorderFactory.createTitledBorder(
			       loweredetched, "direction");
		directionPanel.setBorder(title);

		add( directionPanel ); 
		
	}

	public void actionPerformed(ActionEvent e) {
		
		setXDirection( XRB.isSelected() );
		updateInterface();
	}
	
	public boolean isXDirection() {
		return ((Directional2DOperation)dependent).isXDirection();
	}


	public void setXDirection(boolean x) {
		((Directional2DOperation)dependent).setXDirection(x);  
	}
}
