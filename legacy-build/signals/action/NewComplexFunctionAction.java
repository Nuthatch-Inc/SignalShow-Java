package signals.action;

import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import signals.core.CombineTermsRule;
import signals.core.Core;
import signals.core.FunctionCreationOptions;
import signals.core.FunctionPart1D;
import signals.core.FunctionPart2D;
import signals.core.FunctionTerm1D;
import signals.core.FunctionTerm2D;
import signals.core.RealImagFunction1D;
import signals.core.RealImagFunction2D;
import signals.core.Constants.FunctionType;
import signals.functionterm.PolarFunctionTerm2D;
import signals.functionterm.XYFunctionTerm2D;

@SuppressWarnings("serial")
public abstract class NewComplexFunctionAction extends AbstractAction {
	
	public NewComplexFunctionAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NewComplexFunctionAction(String name, Icon icon) {
		super(name, icon);
		// TODO Auto-generated constructor stub
	}

	public NewComplexFunctionAction(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public void addReImFunction1D( FunctionTerm1D realTerm, FunctionTerm1D imagTerm, String name ) {
		
		FunctionCreationOptions options = Core.getFunctionCreationOptions();
		
		ArrayList<FunctionTerm1D> realList = new ArrayList<FunctionTerm1D>(); 
		ArrayList<FunctionTerm1D> imagList = new ArrayList<FunctionTerm1D>();

		realList.add( realTerm ); 
		imagList.add( imagTerm );

		int[] rule = {1};

		FunctionPart1D realPart = new FunctionPart1D( realList, new CombineTermsRule( rule ) ); 
		FunctionPart1D imagPart = new FunctionPart1D( imagList, new CombineTermsRule( rule ) ); 

		RealImagFunction1D function =  new RealImagFunction1D( realPart, imagPart, name, 
				options.getDefaultDimension1D(), options.isZeroCentered1D() );
		
		function.setType( FunctionType.COMPLEX ); 
		
		Core.addToMainList(function); 
	}
	
	public void addPolarReImFunction2D( FunctionTerm1D realTermR, FunctionTerm1D realTermT, 
			FunctionTerm1D imagTermR, FunctionTerm1D imagTermT, String name ) {
		
		FunctionCreationOptions options = Core.getFunctionCreationOptions();
		
		ParameterBlock pbr = new ParameterBlock();
		pbr.addSource( realTermR );
		pbr.addSource( realTermT ); 
		
		ParameterBlock pbi = new ParameterBlock();
		pbi.addSource( imagTermR );
		pbi.addSource( imagTermT );
		
		PolarFunctionTerm2D realTerm = new PolarFunctionTerm2D( pbr ); 
		PolarFunctionTerm2D imagTerm = new PolarFunctionTerm2D( pbi ); 
		
		ArrayList<FunctionTerm2D> realList = new ArrayList<FunctionTerm2D>(); 
		ArrayList<FunctionTerm2D> imagList = new ArrayList<FunctionTerm2D>();
		
		realList.add( realTerm ); 
		imagList.add( imagTerm );
		
		int[] rule = {1};
		
		FunctionPart2D realPart = new FunctionPart2D( realList, new CombineTermsRule( rule ) ); 
		FunctionPart2D imagPart = new FunctionPart2D( imagList, new CombineTermsRule( rule ) ); 
		
		RealImagFunction2D function = new RealImagFunction2D( 
				realPart, imagPart, name, options.getDefaultDimensionX2D(), 
				options.getDefaultDimensionY2D(), options.isZeroCentered2D() );
		
		function.setType( FunctionType.COMPLEX ); 
		
		Core.addToMainList(function); 
	}
	
	public void addXYReImFunction2D( FunctionTerm1D realTermX, FunctionTerm1D realTermY, 
			FunctionTerm1D imagTermX, FunctionTerm1D imagTermY, String name ) {
		
		FunctionCreationOptions options = Core.getFunctionCreationOptions();
		
		ParameterBlock pbr = new ParameterBlock();
		pbr.addSource( realTermX );
		pbr.addSource( realTermY ); 
		
		ParameterBlock pbi = new ParameterBlock();
		pbi.addSource( imagTermX );
		pbi.addSource( imagTermY );
		
		XYFunctionTerm2D realTerm = new XYFunctionTerm2D( pbr ); 
		XYFunctionTerm2D imagTerm = new XYFunctionTerm2D( pbi ); 
		
		ArrayList<FunctionTerm2D> realList = new ArrayList<FunctionTerm2D>(); 
		ArrayList<FunctionTerm2D> imagList = new ArrayList<FunctionTerm2D>();
		
		realList.add( realTerm ); 
		imagList.add( imagTerm );
		
		int[] rule = {1};
		
		FunctionPart2D realPart = new FunctionPart2D( realList, new CombineTermsRule( rule ) ); 
		FunctionPart2D imagPart = new FunctionPart2D( imagList, new CombineTermsRule( rule ) ); 
		
		RealImagFunction2D function = new RealImagFunction2D( 
				realPart, imagPart, name, options.getDefaultDimensionX2D(), 
				options.getDefaultDimensionY2D(), options.isZeroCentered2D() );
		
		function.setType( FunctionType.COMPLEX ); 
		
		Core.addToMainList(function); 
	}

}
