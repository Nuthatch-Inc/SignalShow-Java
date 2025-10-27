/**
 * 
 */
package signals.action;

import java.awt.event.ActionEvent;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;

import signals.core.CombineTermsRule;
import signals.core.Core;
import signals.core.FunctionCreationOptions;
import signals.core.FunctionPart2D;
import signals.core.FunctionTerm2D;
import signals.core.RealImagFunction2D;
import signals.core.Constants.FunctionType;
import signals.functionterm.CosineFunctionTerm1D;
import signals.functionterm.SineFunctionTerm1D;
import signals.functionterm.XYFunctionTerm2D;
import signals.io.ResourceLoader;


/**
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public class NewFourierBasisFunction2DAction extends NewComplexFunctionAction {

	static final String description = "New 2D Fourier Basis Function";

	/**
	 * 
	 */
	public NewFourierBasisFunction2DAction() {
		super(description, ResourceLoader.createImageIcon("/guiIcons/fourierBasis2D.png")); 
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		FunctionCreationOptions options = Core.getFunctionCreationOptions();

		CosineFunctionTerm1D cosTerm = new CosineFunctionTerm1D( null ); 
		SineFunctionTerm1D sinTerm = new SineFunctionTerm1D( null ); 

		CosineFunctionTerm1D cosaX = (CosineFunctionTerm1D) cosTerm.getDefaultInstance(); 
		CosineFunctionTerm1D cosbY = (CosineFunctionTerm1D) cosTerm.getDefaultInstance(); 
		SineFunctionTerm1D sinaX = (SineFunctionTerm1D) sinTerm.getDefaultInstance(); 
		SineFunctionTerm1D sinbY= (SineFunctionTerm1D) sinTerm.getDefaultInstance(); 
		double width = options.getDefaultWidthX2D();
		cosaX.setWidth( width ); 
		cosbY.setWidth( width ); 
		sinaX.setWidth( width ); 
		sinbY.setWidth( width );
	
		ParameterBlock pbr1 = new ParameterBlock();
		pbr1.addSource( cosaX );
		pbr1.addSource( cosbY ); 
		
		ParameterBlock pbr2 = new ParameterBlock();
		pbr2.addSource( sinaX );
		pbr2.addSource( sinbY ); 

		ParameterBlock pbi1 = new ParameterBlock();
		pbi1.addSource( sinaX );
		pbi1.addSource( cosbY );
		
		ParameterBlock pbi2 = new ParameterBlock();
		pbi2.addSource( cosaX );
		pbi2.addSource( sinbY );

		XYFunctionTerm2D realTerm1 = new XYFunctionTerm2D( pbr1 ); 
		XYFunctionTerm2D realTerm2 = new XYFunctionTerm2D( pbr2 );
		XYFunctionTerm2D imagTerm1 = new XYFunctionTerm2D( pbi1 ); 
		XYFunctionTerm2D imagTerm2 = new XYFunctionTerm2D( pbi2 ); 

		ArrayList<FunctionTerm2D> realList = new ArrayList<FunctionTerm2D>(); 
		ArrayList<FunctionTerm2D> imagList = new ArrayList<FunctionTerm2D>();

		realList.add( realTerm1 ); 
		realList.add( realTerm2 ); 
		
		imagList.add( imagTerm1 );
		imagList.add( imagTerm2 );

		int[] ruleR = {CombineTermsRule.FUNCTION_TERM, CombineTermsRule.SUBTRACT, CombineTermsRule.FUNCTION_TERM};
		int[] ruleI = {CombineTermsRule.FUNCTION_TERM, CombineTermsRule.ADD, CombineTermsRule.FUNCTION_TERM};

		FunctionPart2D realPart = new FunctionPart2D( realList, new CombineTermsRule( ruleR ) ); 
		FunctionPart2D imagPart = new FunctionPart2D( imagList, new CombineTermsRule( ruleI ) ); 

		RealImagFunction2D function = new RealImagFunction2D( 
				realPart, imagPart, "Fourier basis function", options.getDefaultDimensionX2D(), 
				options.getDefaultDimensionY2D(), options.isZeroCentered2D() );

		function.setType( FunctionType.FOURIER_BASIS ); 

		Core.addToMainList(function); 
	}

}
