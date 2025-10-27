package signals.core;

import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;

import signals.functionterm.ConstantFunctionTerm1D;
import signals.functionterm.DataFunctionTerm1D;
import signals.functionterm.DataFunctionTerm2D;
import signals.functionterm.PolarFunctionTerm2D;
import signals.functionterm.ZeroFunctionTerm1D;
import signals.functionterm.ZeroFunctionTerm2D;

public class FunctionFactory {

	public static Function createRadialFunction2D(FunctionTerm1D realRadialTerm, int xDimension,
				int yDimension, boolean zeroCentered, String name) {

		ZeroFunctionTerm2D zero2D = (ZeroFunctionTerm2D) new ZeroFunctionTerm2D(null).getDefaultInstance();
		
		ArrayList<FunctionTerm2D> realList = new ArrayList<FunctionTerm2D>(); 
		ArrayList<FunctionTerm2D> imagList = new ArrayList<FunctionTerm2D>();
		
		ConstantFunctionTerm1D angle = (ConstantFunctionTerm1D) new ConstantFunctionTerm1D(null).getDefaultInstance(); 
		
		ParameterBlock pb = new ParameterBlock();
		pb.addSource( realRadialTerm );
		pb.addSource( angle ); 
		
		PolarFunctionTerm2D polarTerm = new PolarFunctionTerm2D( pb );

		realList.add( polarTerm ); 
		imagList.add( zero2D );

		CombineTermsRule rule = CombineTermsRule.getDefaultRule(); 

		FunctionPart2D realPart = new FunctionPart2D( realList, rule ); 
		FunctionPart2D imagPart = new FunctionPart2D( imagList, rule ); 

		return new RealImagFunction2D( realPart, imagPart, name, 
				xDimension, yDimension, zeroCentered ); 
	}

	public static Function createFunction1D( FunctionTerm1D realTerm, int dimension,
			boolean zeroCentered, String name ) {

		ZeroFunctionTerm1D term1D = (ZeroFunctionTerm1D) new ZeroFunctionTerm1D(null).getDefaultInstance(); 

		ArrayList<FunctionTerm1D> realList = new ArrayList<FunctionTerm1D>(); 
		ArrayList<FunctionTerm1D> imagList = new ArrayList<FunctionTerm1D>();

		realList.add( realTerm ); 
		imagList.add( term1D );

		CombineTermsRule rule = CombineTermsRule.getDefaultRule(); 

		FunctionPart1D realPart = new FunctionPart1D( realList, rule ); 
		FunctionPart1D imagPart = new FunctionPart1D( imagList, rule ); 

		FunctionCreationOptions options = Core.getFunctionCreationOptions();

		return new RealImagFunction1D( realPart, imagPart, "", 
				dimension, options.isZeroCentered1D() );

	}

	public static Function createZeroFunction2D() {

		ZeroFunctionTerm2D term2D = (ZeroFunctionTerm2D) new ZeroFunctionTerm2D(null).getDefaultInstance(); 

		ArrayList<FunctionTerm2D> realList = new ArrayList<FunctionTerm2D>(); 
		ArrayList<FunctionTerm2D> imagList = new ArrayList<FunctionTerm2D>();

		realList.add( term2D ); 
		imagList.add( term2D );

		CombineTermsRule rule = CombineTermsRule.getDefaultRule(); 

		FunctionPart2D realPart = new FunctionPart2D( realList, rule ); 
		FunctionPart2D imagPart = new FunctionPart2D( imagList, rule ); 

		FunctionCreationOptions options = Core.getFunctionCreationOptions();

		return new RealImagFunction2D( realPart, imagPart, "zero", 
				options.getDefaultDimensionX2D(), options.getDefaultDimensionY2D(),
				options.isZeroCentered2D() ); 
	}
	
	public static Function createMagPhaseZeroFunction2D() {
		
		ZeroFunctionTerm2D term2D = (ZeroFunctionTerm2D) new ZeroFunctionTerm2D(null).getDefaultInstance(); 

		ArrayList<FunctionTerm2D> realList = new ArrayList<FunctionTerm2D>(); 
		ArrayList<FunctionTerm2D> imagList = new ArrayList<FunctionTerm2D>();

		realList.add( term2D ); 
		imagList.add( term2D );

		CombineTermsRule rule = CombineTermsRule.getDefaultRule(); 

		FunctionPart2D realPart = new FunctionPart2D( realList, rule ); 
		FunctionPart2D imagPart = new FunctionPart2D( imagList, rule ); 

		FunctionCreationOptions options = Core.getFunctionCreationOptions();

		return new MagPhaseFunction2D( realPart, imagPart, "zero", 
				options.getDefaultDimensionX2D(), options.getDefaultDimensionY2D(),
				options.isZeroCentered2D() ); 
	}
	
	

	public static Function createZeroFunction1D() {

		ZeroFunctionTerm1D term1D = (ZeroFunctionTerm1D) new ZeroFunctionTerm1D(null).getDefaultInstance(); 

		ArrayList<FunctionTerm1D> realList = new ArrayList<FunctionTerm1D>(); 
		ArrayList<FunctionTerm1D> imagList = new ArrayList<FunctionTerm1D>();

		realList.add( term1D ); 
		imagList.add( term1D );

		CombineTermsRule rule = CombineTermsRule.getDefaultRule(); 

		FunctionPart1D realPart = new FunctionPart1D( realList, rule ); 
		FunctionPart1D imagPart = new FunctionPart1D( imagList, rule ); 

		FunctionCreationOptions options = Core.getFunctionCreationOptions();

		return new RealImagFunction1D( realPart, imagPart, "zero", 
				options.getDefaultDimension1D(), options.isZeroCentered1D() );
	}

	public static Function createMagPhaseZeroFunction1D() {

		ZeroFunctionTerm1D term1D = (ZeroFunctionTerm1D) new ZeroFunctionTerm1D(null).getDefaultInstance(); 

		ArrayList<FunctionTerm1D> realList = new ArrayList<FunctionTerm1D>(); 
		ArrayList<FunctionTerm1D> imagList = new ArrayList<FunctionTerm1D>();

		realList.add( term1D ); 
		imagList.add( term1D );

		CombineTermsRule rule = CombineTermsRule.getDefaultRule(); 

		FunctionPart1D realPart = new FunctionPart1D( realList, rule ); 
		FunctionPart1D imagPart = new FunctionPart1D( imagList, rule ); 

		FunctionCreationOptions options = Core.getFunctionCreationOptions();

		return new MagPhaseFunction1D( realPart, imagPart, "zero", 
				options.getDefaultDimension1D(), options.isZeroCentered1D() );
	}
	
	public static Function createFunction1D( double[] real, double[] imag, boolean zeroCentered, String name ) {

		DataFunctionTerm1D dataTerm = new DataFunctionTerm1D( null ); 

		ParameterBlock pbr = dataTerm.getDefaultParamBlock(); 
		pbr.addSource( real ); 

		ParameterBlock pbi = dataTerm.getDefaultParamBlock(); 
		pbi.addSource( imag );

		DataFunctionTerm1D realTerm = new DataFunctionTerm1D( pbr ); 
		double width = zeroCentered ? real.length/2.0 : real.length; 
		realTerm.setWidth(width);
		DataFunctionTerm1D imagTerm = new DataFunctionTerm1D( pbi );
		imagTerm.setWidth(width);

		ArrayList<FunctionTerm1D> realList = new ArrayList<FunctionTerm1D>(); 
		ArrayList<FunctionTerm1D> imagList = new ArrayList<FunctionTerm1D>();

		realList.add( realTerm ); 
		imagList.add( imagTerm );

		int[] rule = {1};
		CombineTermsRule ctrule = new CombineTermsRule( rule );

		FunctionPart1D realPart = new FunctionPart1D( realList, ctrule ); 
		FunctionPart1D imagPart = new FunctionPart1D( imagList, ctrule ); 

		Function function = new RealImagFunction1D( realPart, imagPart, "", real.length, zeroCentered );
		function.setEquation(name); 

		return function;  
	}
	
	public static Function createMagPhaseFunction2D( double[] mag, double[] phase, boolean zeroCentered, String name,
			int x_dimension, int y_dimension ) {

		DataFunctionTerm2D dataTerm = new DataFunctionTerm2D( null ); 
		ParameterBlock pbr = dataTerm.getDefaultParamBlock(); 
		pbr.addSource( mag );
		pbr.addSource( x_dimension );
		pbr.addSource( y_dimension );

		ParameterBlock pbi = dataTerm.getDefaultParamBlock(); 
		pbi.addSource( phase );
		pbi.addSource( x_dimension );
		pbi.addSource( y_dimension );

		DataFunctionTerm2D realTerm = new DataFunctionTerm2D( pbr ); 
		realTerm.setWidth(x_dimension);
		realTerm.setHeight(y_dimension);
		DataFunctionTerm2D imagTerm = new DataFunctionTerm2D( pbi );
		imagTerm.setWidth(x_dimension);
		imagTerm.setHeight(y_dimension);

		ArrayList<FunctionTerm2D> realList = new ArrayList<FunctionTerm2D>(); 
		ArrayList<FunctionTerm2D> imagList = new ArrayList<FunctionTerm2D>();

		realList.add( realTerm ); 
		imagList.add( imagTerm );

		int[] rule = {1};
		CombineTermsRule ctrule = new CombineTermsRule( rule );

		FunctionPart2D magPart = new FunctionPart2D( realList, ctrule ); 
		FunctionPart2D phasePart = new FunctionPart2D( imagList, ctrule ); 

		Function function = new MagPhaseFunction2D( magPart, phasePart, "", x_dimension, y_dimension, zeroCentered );
		function.setEquation(name); 
		return function; 
	}

	public static Function createFunction2D( double[] real, double[] imag, boolean zeroCentered, String name,
			int x_dimension, int y_dimension ) {

		DataFunctionTerm2D dataTerm = new DataFunctionTerm2D( null ); 
		ParameterBlock pbr = dataTerm.getDefaultParamBlock(); 
		pbr.addSource( real );
		pbr.addSource( x_dimension );
		pbr.addSource( y_dimension );

		ParameterBlock pbi = dataTerm.getDefaultParamBlock(); 
		pbi.addSource( imag );
		pbi.addSource( x_dimension );
		pbi.addSource( y_dimension );

		DataFunctionTerm2D realTerm = new DataFunctionTerm2D( pbr ); 
		realTerm.setWidth(x_dimension);
		realTerm.setHeight(y_dimension);
		DataFunctionTerm2D imagTerm = new DataFunctionTerm2D( pbi );
		imagTerm.setWidth(x_dimension);
		imagTerm.setHeight(y_dimension);

		ArrayList<FunctionTerm2D> realList = new ArrayList<FunctionTerm2D>(); 
		ArrayList<FunctionTerm2D> imagList = new ArrayList<FunctionTerm2D>();

		realList.add( realTerm ); 
		imagList.add( imagTerm );

		int[] rule = {1};
		CombineTermsRule ctrule = new CombineTermsRule( rule );

		FunctionPart2D realPart = new FunctionPart2D( realList, ctrule ); 
		FunctionPart2D imagPart = new FunctionPart2D( imagList, ctrule ); 

		Function function = new RealImagFunction2D( realPart, imagPart, "", x_dimension, y_dimension, zeroCentered );
		function.setEquation(name); 
		return function; 
	}

}
