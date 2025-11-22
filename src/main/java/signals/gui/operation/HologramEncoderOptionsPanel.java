package signals.gui.operation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import signals.core.Operation;
import signals.operation.HologramEncoderOp;

@SuppressWarnings("serial")
public class HologramEncoderOptionsPanel extends OperationOptionsPanel implements ActionListener {

	JCheckBox diffusionCB, widerAperturesCB;
	
	public HologramEncoderOptionsPanel( Operation dependent ) {
		
		super( dependent );
		diffusionCB = new JCheckBox( "Error Diffusion" ); 
		diffusionCB.addActionListener(this); 
		diffusionCB.setSelected(getDiffusion()); 
		
		widerAperturesCB = new JCheckBox( "3-Pixel-Wide Apertures" ); 
		widerAperturesCB.addActionListener(this); 
		widerAperturesCB.setSelected(getWiderApertures()); 
		
		add( diffusionCB ); 
		add( widerAperturesCB ); 
	}

	public void actionPerformed(ActionEvent e) {
		
		if( e.getSource().equals(diffusionCB)) {
			setDiffusion( diffusionCB.isSelected() );
			
		} else if( e.getSource().equals(widerAperturesCB) ) {
			
			setWiderApertures(widerAperturesCB.isSelected() ); 
		}
		updateInterface();
	}
	
	public void setDiffusion( boolean tf ) {
		
		((HologramEncoderOp)dependent).setErrorDiffusion( tf ); 
	}
	
	public boolean getDiffusion() {
		
		return ((HologramEncoderOp)dependent).isErrorDiffusion(); 
	}
	
	public boolean getWiderApertures() {
		
		return ((HologramEncoderOp)dependent).isWiderApertures(); 
	}
	
	public void setWiderApertures( boolean tf ) {
		
		((HologramEncoderOp)dependent).setWiderApertures( tf ); 
	}
	
}
