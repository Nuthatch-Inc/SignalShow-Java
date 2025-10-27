package signals.gui.operation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.jai.Interpolation;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import signals.core.Operation;
import signals.operation.InterpolationUser;

@SuppressWarnings("serial")
public class InterpolationOptionsPanel extends OperationOptionsPanel implements ActionListener {

	public InterpolationOptionsPanel( Operation dependent ) {
		
		super( dependent );
		
		JRadioButton nearestRB = new JRadioButton( "Nearest Neighbor"); 
		JRadioButton bilinearRB = new JRadioButton("Bilinear"); 
		JRadioButton bicubicRB = new JRadioButton( "Bicubic (image independent)"); 
		JRadioButton bicubic2RB = new JRadioButton( "Bicubic (sharper, image dependent)"); 
		
		nearestRB.setActionCommand( ""+Interpolation.INTERP_NEAREST);
		bilinearRB.setActionCommand( ""+Interpolation.INTERP_BILINEAR );
		bicubicRB.setActionCommand( ""+Interpolation.INTERP_BICUBIC);
		bicubic2RB.setActionCommand( ""+Interpolation.INTERP_BICUBIC_2);
		
		nearestRB.addActionListener( this ); 
		bilinearRB.addActionListener( this ); 
		bicubicRB.addActionListener( this ); 
		bicubic2RB.addActionListener( this );
		
		ButtonGroup normalizationGroup = new ButtonGroup(); 
		normalizationGroup.add(nearestRB); 
		normalizationGroup.add(bilinearRB);
		normalizationGroup.add(bicubicRB); 
		normalizationGroup.add(bicubic2RB); 
		
		switch( getInterpolation() ) {
		
		case Interpolation.INTERP_BILINEAR: 
			bilinearRB.setSelected(true); 
			break; 
		case Interpolation.INTERP_NEAREST: 
			nearestRB.setSelected(true); 
			break; 
		case Interpolation.INTERP_BICUBIC: 
			bicubicRB.setSelected(true); 
			break;
		case Interpolation.INTERP_BICUBIC_2: 
			bicubic2RB.setSelected(true); 
			break;
		}
		
		JPanel normalizationPanel = new JPanel(); 
		normalizationPanel.setLayout(new BoxLayout( normalizationPanel, BoxLayout.PAGE_AXIS ) ); 
		normalizationPanel.add(nearestRB); 
		normalizationPanel.add(bilinearRB); 
		normalizationPanel.add(bicubicRB); 
		normalizationPanel.add(bicubic2RB);
		
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border title = BorderFactory.createTitledBorder(
			       loweredetched, "Interpolation");
		normalizationPanel.setBorder(title);

		add( normalizationPanel ); 
		
	}

	public void actionPerformed(ActionEvent e) {
		
		setInterpolation( (Integer.parseInt(e.getActionCommand())) );
		updateInterface();
	}
	
	public void setInterpolation( int interpolation ) {
		
		((InterpolationUser)dependent).setInterpolation( interpolation ); 
	}
	
	public int getInterpolation() {
		
		return ((InterpolationUser)dependent).getInterpolation(); 
	}
}
