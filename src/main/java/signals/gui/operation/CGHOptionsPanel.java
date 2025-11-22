package signals.gui.operation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import signals.core.Operation;
import signals.operation.PhaseDetourHologramOp;

@SuppressWarnings("serial")
public class CGHOptionsPanel extends OperationOptionsPanel implements ActionListener {

	JCheckBox diffusionCB, randomPhaseCB, widerAperturesCB; 
	
	public CGHOptionsPanel( Operation dependent ) {
		
		super( dependent );
		diffusionCB = new JCheckBox( "Error Diffusion" ); 
		diffusionCB.addActionListener(this); 
		diffusionCB.setSelected(getDiffusion()); 
		add( diffusionCB ); 

		randomPhaseCB = new JCheckBox( "Random Phase" ); 
		randomPhaseCB.addActionListener(this); 
		randomPhaseCB.setSelected(getRandomPhase()); 
		add( randomPhaseCB ); 
		
		widerAperturesCB = new JCheckBox( "3-Pixel-Wide Apertures" ); 
		widerAperturesCB.addActionListener(this); 
		widerAperturesCB.setSelected(getWiderApertures()); 
	
		add( widerAperturesCB ); 
	}

	public void actionPerformed(ActionEvent e) {
		
		if( e.getSource().equals(diffusionCB)) {
			setDiffusion( diffusionCB.isSelected() );
		} else if (e.getSource().equals(randomPhaseCB)) {
			
			setRandomPhase( randomPhaseCB.isSelected() ); 
			
		} else if( e.getSource().equals(widerAperturesCB) ) {
			
			setWiderApertures(widerAperturesCB.isSelected() ); 
		}
		
		updateInterface(); 
	}
	
	public void setDiffusion( boolean tf ) {
		
		((PhaseDetourHologramOp)dependent).setErrorDiffusion( tf ); 
	}
	
	public boolean getDiffusion() {
		
		return ((PhaseDetourHologramOp)dependent).isErrorDiffusion(); 
	}
	
	public void setRandomPhase( boolean tf ) {
		
		((PhaseDetourHologramOp)dependent).setRandomPhase( tf ); 
	}
	
	public boolean getRandomPhase() {
		
		return ((PhaseDetourHologramOp)dependent).isRandomPhase(); 
	}
	
	public boolean getWiderApertures() {
		
		return ((PhaseDetourHologramOp)dependent).isWiderApertures(); 
	}
	
	public void setWiderApertures( boolean tf ) {
		
		((PhaseDetourHologramOp)dependent).setWiderApertures( tf ); 
	}
}
