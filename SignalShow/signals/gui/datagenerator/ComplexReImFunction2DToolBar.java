package signals.gui.datagenerator;

import java.util.ArrayList;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function2D;
import signals.core.FunctionPart2D;
import signals.functionterm.AnalyticFunctionTerm1D;
import signals.functionterm.ChirpFunctionTerm1D;
import signals.functionterm.CosineFunctionTerm1D;
import signals.functionterm.SeparableFunctionTerm2D;
import signals.functionterm.SineChirpFunctionTerm1D;
import signals.functionterm.SineFunctionTerm1D;

@SuppressWarnings("serial")
public class ComplexReImFunction2DToolBar extends ReImFunction2DToolBar {

	public ComplexReImFunction2DToolBar(Function function) {
		super(function);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void showEditor(boolean newState, FunctionPart2D part) {
		super.showEditor(newState, part);
		
		SeparableFunctionTerm2D term2 = (SeparableFunctionTerm2D) part.getTermsList().get(0); 
		AnalyticFunctionTerm1D term = (AnalyticFunctionTerm1D) term2.getFunctionTerm1DA();
		
		if( term instanceof CosineFunctionTerm1D || term instanceof SineFunctionTerm1D ) {
			
			editor.setDocPath("/functiondoc/complexsinusoid.html");
			
		} else if( term instanceof ChirpFunctionTerm1D || term instanceof SineChirpFunctionTerm1D ) {
			
			editor.setDocPath("/functiondoc/complexchirp.html");
		}
	
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
				SeparableFunctionTerm2D realTerm =  (SeparableFunctionTerm2D) real.getTermsList().get(0); 
				
				AnalyticFunctionTerm1D realTerm1DA = (AnalyticFunctionTerm1D) realTerm.getFunctionTerm1DA(); 
				AnalyticFunctionTerm1D realTerm1DB = (AnalyticFunctionTerm1D) realTerm.getFunctionTerm1DB(); 
				
				FunctionPart2D imag = ((Function2D)function).getPartB(); 
				SeparableFunctionTerm2D imagTerm =  (SeparableFunctionTerm2D) imag.getTermsList().get(0); 
				
				AnalyticFunctionTerm1D imagTerm1DA = (AnalyticFunctionTerm1D) imagTerm.getFunctionTerm1DA(); 
				AnalyticFunctionTerm1D imagTerm1DB = (AnalyticFunctionTerm1D) imagTerm.getFunctionTerm1DB(); 
				
				if( realTerm1DA.hasWidth() ) imagTerm1DA.setWidth(realTerm1DA.getWidth()); 
				if( realTerm1DA.hasAmplitude()) imagTerm1DA.setAmplitude(realTerm1DA.getAmplitude()); 
				if( realTerm1DA.hasCenter()) imagTerm1DA.setCenter(realTerm1DA.getCenter()); 
				if( realTerm1DA.hasInitialPhase()) imagTerm1DA.setInitialPhase(realTerm1DA.getInitialPhase()); 
				if( realTerm1DA.hasOrder()) imagTerm1DA.setOrder(realTerm1DA.getOrder());
				
				if( realTerm1DB.hasWidth() ) imagTerm1DB.setWidth(realTerm1DB.getWidth()); 
				if( realTerm1DB.hasAmplitude()) imagTerm1DB.setAmplitude(realTerm1DB.getAmplitude()); 
				if( realTerm1DB.hasCenter()) imagTerm1DB.setCenter(realTerm1DB.getCenter()); 
				if( realTerm1DB.hasInitialPhase()) imagTerm1DB.setInitialPhase(realTerm1DB.getInitialPhase()); 
				if( realTerm1DB.hasOrder()) imagTerm1DB.setOrder(realTerm1DB.getOrder()); 
				
				((Function2D)function).setPartA(real);  
				((Function2D)function).setPartB(imag); 
			
				Core.getFunctionList().refresh();  

			} else if( openTab.equals(EDIT_B ) ){

				FunctionPart2D imag = ((CreateFunctionPart2DPanel)editor).getFunctionPart2D();
				SeparableFunctionTerm2D imagTerm =  (SeparableFunctionTerm2D) imag.getTermsList().get(0); 
				
				AnalyticFunctionTerm1D imagTerm1DA = (AnalyticFunctionTerm1D) imagTerm.getFunctionTerm1DA(); 
				AnalyticFunctionTerm1D imagTerm1DB = (AnalyticFunctionTerm1D) imagTerm.getFunctionTerm1DB(); 
				
				FunctionPart2D real = ((Function2D)function).getPartA(); 
				SeparableFunctionTerm2D realTerm =  (SeparableFunctionTerm2D) real.getTermsList().get(0); 
				
				AnalyticFunctionTerm1D realTerm1DA = (AnalyticFunctionTerm1D) realTerm.getFunctionTerm1DA(); 
				AnalyticFunctionTerm1D realTerm1DB = (AnalyticFunctionTerm1D) realTerm.getFunctionTerm1DB(); 
				
				if( imagTerm1DA.hasWidth() ) realTerm1DA.setWidth(imagTerm1DA.getWidth()); 
				if( imagTerm1DA.hasAmplitude()) realTerm1DA.setAmplitude(imagTerm1DA.getAmplitude()); 
				if( imagTerm1DA.hasCenter()) realTerm1DA.setCenter(imagTerm1DA.getCenter()); 
				if( imagTerm1DA.hasInitialPhase()) realTerm1DA.setInitialPhase(imagTerm1DA.getInitialPhase()); 
				if( imagTerm1DA.hasOrder()) realTerm1DA.setOrder(imagTerm1DA.getOrder()); 
				
				if( imagTerm1DB.hasWidth() ) realTerm1DB.setWidth(imagTerm1DB.getWidth()); 
				if( imagTerm1DB.hasAmplitude()) realTerm1DB.setAmplitude(imagTerm1DB.getAmplitude()); 
				if( imagTerm1DB.hasCenter()) realTerm1DB.setCenter(imagTerm1DB.getCenter()); 
				if( imagTerm1DB.hasInitialPhase()) realTerm1DB.setInitialPhase(imagTerm1DB.getInitialPhase());
				if( imagTerm1DB.hasOrder()) realTerm1DB.setOrder(imagTerm1DB.getOrder()); 
				
				((Function2D)function).setPartA(real);  
				((Function2D)function).setPartB(imag); 
			
				Core.getFunctionList().refresh(); 

			}

		}

	}
}
