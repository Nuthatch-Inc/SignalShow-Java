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
import signals.operation.NormalizationOperation;
import signals.operation.Transforms;

@SuppressWarnings("serial")
public class NormalizationOptionsPanel extends OperationOptionsPanel implements ActionListener {

	public NormalizationOptionsPanel( Operation dependent ) {
		
		super( dependent );
		
		//add radio button group for normalization 
		JRadioButton normalizeNoneRB = new JRadioButton( "No Normalization"); 
		JRadioButton normalizeNRB = new JRadioButton("Divide by N"); 
		JRadioButton normalizeSqrtRB = new JRadioButton( "Divide by \u221AN"); 
		
		normalizeNoneRB.setActionCommand( ""+Transforms.NORMALIZE_NONE );
		normalizeNRB.setActionCommand( ""+Transforms.NORMALIZE_N );
		normalizeSqrtRB.setActionCommand( ""+Transforms.NORMALIZE_ROOT_N );
		
		normalizeNoneRB.addActionListener( this ); 
		normalizeNRB.addActionListener( this ); 
		normalizeSqrtRB.addActionListener( this ); 
		
		ButtonGroup normalizationGroup = new ButtonGroup(); 
		normalizationGroup.add(normalizeNoneRB); 
		normalizationGroup.add(normalizeNRB);
		normalizationGroup.add(normalizeSqrtRB); 
		
		switch( getNormalization() ) {
		
		case Transforms.NORMALIZE_N: 
			normalizeNRB.setSelected(true); 
			break; 
		case Transforms.NORMALIZE_NONE: 
			normalizeNoneRB.setSelected(true); 
			break; 
		case Transforms.NORMALIZE_ROOT_N: 
			normalizeSqrtRB.setSelected(true); 
			break; 
		}
		
		JPanel normalizationPanel = new JPanel(); 
		normalizationPanel.setLayout(new BoxLayout( normalizationPanel, BoxLayout.PAGE_AXIS ) ); 
		normalizationPanel.add(normalizeNoneRB); 
		normalizationPanel.add(normalizeNRB); 
		normalizationPanel.add(normalizeSqrtRB); 
		
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border title = BorderFactory.createTitledBorder(
			       loweredetched, "Normalization");
		normalizationPanel.setBorder(title);

		add( normalizationPanel ); 
		
	}

	public void actionPerformed(ActionEvent e) {
		
		setNormalization( (Integer.parseInt(e.getActionCommand())) );
		updateInterface();
	}
	
	public void setNormalization( int normalization ) {
		
		((NormalizationOperation)dependent).setNormalization( normalization ); 
	}
	
	public int getNormalization() {
		
		return ((NormalizationOperation)dependent).getNormalization(); 
	}
}
