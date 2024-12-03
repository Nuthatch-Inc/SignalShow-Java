package signals.gui.datagenerator;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.FunctionPart1D;
import signals.functionterm.AnalyticFunctionTerm1D;
import signals.functionterm.ChirpFunctionTerm1D;
import signals.functionterm.CosineFunctionTerm1D;
import signals.functionterm.SineChirpFunctionTerm1D;
import signals.functionterm.SineFunctionTerm1D;

/**
 * 
 * @author julietbernstein
 *
 * Used for complex functions where the real and imaginary parts are linked in width, center, and amplitude
 *
 */
@SuppressWarnings("serial")
public class ComplexReImFunction1DToolBar extends ReImFunction1DToolBar {

	public ComplexReImFunction1DToolBar(Function function) {
		super(function);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void showEditor(boolean newState, FunctionPart1D part) {
		
		super.showEditor(newState, part);
		
		AnalyticFunctionTerm1D term = (AnalyticFunctionTerm1D) part.getTermsList().get(0);
		
		if( term instanceof CosineFunctionTerm1D || term instanceof SineFunctionTerm1D ) {
			
			editor.setDocPath("/functiondoc/complexsinusoid.html");
			
		} else if( term instanceof ChirpFunctionTerm1D || term instanceof SineChirpFunctionTerm1D) {
			
			editor.setDocPath("/functiondoc/complexchirp.html");
		}
	
	}

	@Override
	public boolean isEditable() {
	
		return false; 
	}

	public void GUIEventOccurred(GUIEvent e) {

		if( e.getDescriptor().equals(GUIEvent.Descriptor.INDICES_MODIFIED_1D)) {

			double[] indices = (double[])(e.getValue(0));
			((Function1D)function).setDimension(indices.length); 
			function.setZeroCentered(indices[0] != 0); 
			
		} else  { 
			
			if( openTab.equals(EDIT_A ) ){

				//edit the first term of the imaginary part
				FunctionPart1D real = ((CreateFunctionPart1DPanel)editor).getFunctionPart1D();
				AnalyticFunctionTerm1D realTerm = (AnalyticFunctionTerm1D) real.getTermsList().get(0); 
				
				FunctionPart1D imag = ((Function1D)function).getPartB(); 
				AnalyticFunctionTerm1D imagTerm = (AnalyticFunctionTerm1D) imag.getTermsList().get(0); 
				
				if( realTerm.hasWidth() ) imagTerm.setWidth(realTerm.getWidth()); 
				if( realTerm.hasAmplitude() ) imagTerm.setAmplitude(realTerm.getAmplitude()); 
				if( realTerm.hasCenter() ) imagTerm.setCenter(realTerm.getCenter()); 
				if( realTerm.hasInitialPhase() ) imagTerm.setInitialPhase(realTerm.getInitialPhase()); 
				if( realTerm.hasOrder() ) imagTerm.setOrder(realTerm.getOrder()); 
				
				((Function1D)function).setPartA(real);  
				((Function1D)function).setPartB(imag);  
				Core.getFunctionList().refresh(); 
			

			} else if( openTab.equals(EDIT_B ) ){

				//edit the first term of the real part
				FunctionPart1D imag = ((CreateFunctionPart1DPanel)editor).getFunctionPart1D();
				AnalyticFunctionTerm1D imagTerm = (AnalyticFunctionTerm1D) imag.getTermsList().get(0); 
				
				FunctionPart1D  real = ((Function1D)function).getPartA(); 
				AnalyticFunctionTerm1D realTerm = (AnalyticFunctionTerm1D) real.getTermsList().get(0); 
				
				if( imagTerm.hasWidth() ) realTerm.setWidth(imagTerm.getWidth()); 
				if( imagTerm.hasAmplitude() ) realTerm.setAmplitude(imagTerm.getAmplitude()); 
				if( imagTerm.hasCenter() ) realTerm.setCenter(imagTerm.getCenter()); 
				if( imagTerm.hasInitialPhase() ) realTerm.setInitialPhase(imagTerm.getInitialPhase()); 
				if( imagTerm.hasOrder() ) realTerm.setOrder(imagTerm.getOrder());

				((Function1D)function).setPartA(real);  
				((Function1D)function).setPartB(imag);  
				Core.getFunctionList().refresh();

			}

		}

	}
}
