package signals.gui.datagenerator;

import java.util.ArrayList;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function2D;
import signals.core.FunctionPart2D;
import signals.functionterm.CosineFunctionTerm1D;
import signals.functionterm.SeparableFunctionTerm2D;

@SuppressWarnings("serial")
public class FourierBasisReImFunction2DToolBar extends ReImFunction2DToolBar {

	public FourierBasisReImFunction2DToolBar(Function function) {
		super(function);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void showEditor(boolean newState, FunctionPart2D part) {
		super.showEditor(newState, part);
		editor.setDocPath("/functiondoc/fourierbasis.html");
	}

	@Override
	public boolean isEditable() {
	
		return false; 
	}
	
	public void GUIEventOccurred(GUIEvent e) {

		if( e.getDescriptor().equals(GUIEvent.Descriptor.INDICES_MODIFIED_2D)) {

			ArrayList<Object> values = e.getValues(); 
			((Function2D)function).setDimensionX((Integer)values.get(0)); 
			((Function2D)function).setDimensionY((Integer)values.get(1));
			function.setZeroCentered((Boolean)values.get(2)); 

		} else  { 
			
			if( openTab.equals(EDIT_A ) ){

				FunctionPart2D real = ((CreateFunctionPart2DPanel)editor).getFunctionPart2D();
				SeparableFunctionTerm2D realTerm1 =  (SeparableFunctionTerm2D) real.getTermsList().get(0);
				SeparableFunctionTerm2D realTerm2 =  (SeparableFunctionTerm2D) real.getTermsList().get(1); 
				
				CosineFunctionTerm1D cosaX = (CosineFunctionTerm1D) realTerm1.getFunctionTerm1DA(); 
				CosineFunctionTerm1D cosbY = (CosineFunctionTerm1D) realTerm1.getFunctionTerm1DB();
				
				CosineFunctionTerm1D sinaX = (CosineFunctionTerm1D) realTerm2.getFunctionTerm1DA(); 
				CosineFunctionTerm1D sinbY = (CosineFunctionTerm1D) realTerm2.getFunctionTerm1DB();
				

				//get the index of the selected calculator item
				int index = editor.getCalculator().getSelectedIndex(); 
				
				if( index == 1 ) { //editing cos(ax)cos(by), need to change sin(ax) and sin(by)
					
					sinaX.setAmplitude(cosaX.getAmplitude()); 
					sinbY.setAmplitude(cosbY.getAmplitude()); 
					
					sinaX.setWidth(cosaX.getWidth()); 
					sinbY.setWidth(cosbY.getWidth()); 
					
					sinaX.setCenter(cosaX.getCenter()); 
					sinbY.setCenter(cosbY.getCenter()); 
					
					sinaX.setInitialPhase(cosaX.getInitialPhase()); 
					sinbY.setInitialPhase(cosbY.getInitialPhase());
					
					
				} else { //index is 3, editing sin(ax)sin(by), need to change cos(ax) and cos(by) 
					
					cosaX.setAmplitude(sinaX.getAmplitude()); 
					cosbY.setAmplitude(sinbY.getAmplitude()); 
					
					cosaX.setWidth(sinaX.getWidth()); 
					cosbY.setWidth(sinbY.getWidth()); 
					
					cosaX.setCenter(sinaX.getCenter()); 
					cosbY.setCenter(sinbY.getCenter()); 
					
					cosaX.setInitialPhase(sinaX.getInitialPhase()); 
					cosbY.setInitialPhase(sinbY.getInitialPhase()); 
					
				}
				
				editor.getCalculator().refreshList(); 
				function.freeMemory(); 
				((Function2D)function).setPartA(real);  
				Core.getFunctionList().refresh(); 

			} else if( openTab.equals(EDIT_B ) ){

				FunctionPart2D imag = ((CreateFunctionPart2DPanel)editor).getFunctionPart2D();
				SeparableFunctionTerm2D imagTerm1 =  (SeparableFunctionTerm2D) imag.getTermsList().get(0);
				SeparableFunctionTerm2D imagTerm2 =  (SeparableFunctionTerm2D) imag.getTermsList().get(1); 
				
				CosineFunctionTerm1D sinaX = (CosineFunctionTerm1D) imagTerm1.getFunctionTerm1DA(); 
				CosineFunctionTerm1D cosbY = (CosineFunctionTerm1D) imagTerm1.getFunctionTerm1DB();
				
				CosineFunctionTerm1D cosaX = (CosineFunctionTerm1D) imagTerm2.getFunctionTerm1DA(); 
				CosineFunctionTerm1D sinbY = (CosineFunctionTerm1D) imagTerm2.getFunctionTerm1DB();

				//get the index of the selected calculator item
				int index = editor.getCalculator().getSelectedIndex(); 
				
				if( index == 1 ) { //editing sin(ax)cos(by), need to change cos(ax) and sin(by)
					
					cosaX.setAmplitude(sinaX.getAmplitude()); 
					sinbY.setAmplitude(cosbY.getAmplitude()); 
	
					cosaX.setWidth(sinaX.getWidth()); 
					sinbY.setWidth(cosbY.getWidth()); 
					
					cosaX.setCenter(sinaX.getCenter()); 
					sinbY.setCenter(cosbY.getCenter()); 
					
					cosaX.setInitialPhase(sinaX.getInitialPhase()); 
					sinbY.setInitialPhase(cosbY.getInitialPhase()); 
					
				} else { //index is 3, editing cos(ax)sin(by), need to change sin(ax) and cos(by) 
					
					sinaX.setAmplitude(cosaX.getAmplitude()); 
					cosbY.setAmplitude(sinbY.getAmplitude()); 
					
					sinaX.setWidth(cosaX.getWidth()); 
					cosbY.setWidth(sinbY.getWidth()); 
					
					sinaX.setCenter(cosaX.getCenter()); 
					cosbY.setCenter(sinbY.getCenter()); 
					
					sinaX.setInitialPhase(cosaX.getInitialPhase()); 
					cosbY.setInitialPhase(sinbY.getInitialPhase()); 
					
				}
				
				editor.getCalculator().refreshList(); 
				function.freeMemory();
				((Function2D)function).setPartB(imag);  
				Core.getFunctionList().refresh(); 

			}

		}

	}

}
