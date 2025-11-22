/**
 * 
 */
package signals.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import signals.functionterm.*;
import signals.operation.*; 

/**
 * @author Juliet
 *
 */
public class DataGeneratorCollections {
	
	ArrayList<Class<? extends DataGenerator>> unary1DList; 
	HashMap<String, ArrayList<Class<? extends DataGenerator>>> unary1DMap; 
	
	ArrayList<Class<? extends DataGenerator>> binary1DList; 
	HashMap<String, ArrayList<Class<? extends DataGenerator>>> binary1DMap; 
	
	ArrayList<Class<? extends DataGenerator>> unary2DList; 
	HashMap<String, ArrayList<Class<? extends DataGenerator>>> unary2DMap;
	
	ArrayList<Class<? extends DataGenerator>> binary2DList; 
	HashMap<String, ArrayList<Class<? extends DataGenerator>>> binary2DMap;

	ArrayList<Class<? extends DataGenerator>> functionTerm1DList;
	HashMap<String, ArrayList<Class<? extends DataGenerator>>> functionTerm1DMap;
	
	ArrayList<Class<? extends DataGenerator>> image2DList;
	HashMap<String, ArrayList<Class<? extends DataGenerator>>> image2DMap;
	
	ArrayList<Class<? extends DataGenerator>> analytic2DList;
	HashMap<String, ArrayList<Class<? extends DataGenerator>>> analytic2DMap;
	
	ArrayList<Class<? extends DataGenerator>> noise2DList;
	HashMap<String, ArrayList<Class<? extends DataGenerator>>> noise2DMap;
	
	public DataGeneratorCollections() {
		
		loadOperation1DList(); 
		loadOperation2DList();
		loadFunctionTerm1DList();
		loadImage2DList();
		loadNoise2DList();
		loadAnalytic2DList(); 
	}

	public HashMap<String, ArrayList<Class<? extends DataGenerator>>> createMap( ArrayList<Class<? extends DataGenerator>> classlist ) {

		HashMap<String, ArrayList<Class<? extends DataGenerator>>> map = new HashMap<String, ArrayList<Class<? extends DataGenerator>>>( classlist.size() * 10 ); //initial capacity  

		for ( Class<? extends DataGenerator> item : classlist ) {
	
			String longName = StringProcess.classNameConvert(item.getName()).toLowerCase();
			
			Scanner scanner  = new Scanner( longName );
			
			//add the long name
			ArrayList<Class<? extends DataGenerator>> sublist = map.get(longName); 
			if( sublist == null ) {

				sublist = new ArrayList<Class<? extends DataGenerator>>(); 
				sublist.add( item );
				map.put(longName, sublist);

			} else if( !sublist.contains(item) ) {

				sublist.add(item);

			} //end else
			
			//add each word
			while( scanner.hasNext() ) { 

				String name = scanner.next();
				int length = name.length();
				for( int i = 0; i <= length; i++ ) {

					//Add the item to the list of data generators mapped to each substring in the name
					String sub = name.substring( 0, i );
					sublist = map.get(sub); 
					if( sublist == null ) {

						sublist = new ArrayList<Class<? extends DataGenerator>>(); 
						sublist.add( item );
						map.put(sub, sublist);

					} else if( !sublist.contains(item) ) {

						sublist.add(item);

					} //end else

				} //end inner for

			}//end outer for

		} //end while

		return map;
	}

	/**
	 * @return the functionTerm1DList
	 */
	public ArrayList<Class<? extends DataGenerator>> getFunctionTerm1DList() {

		return functionTerm1DList;
	}
	

	/**
	 * @return the functionTerm1DMap
	 */
	public HashMap<String, ArrayList<Class<? extends DataGenerator>>> getFunctionTerm1DMap() {

		return functionTerm1DMap;
	} 
	
	/**
	 * @return the image2DList
	 */
	public ArrayList<Class<? extends DataGenerator>> getImage2DList() {
		return image2DList;
	}

	/**
	 * @return the image2DMap
	 */
	public HashMap<String, ArrayList<Class<? extends DataGenerator>>> getImage2DMap() {
		return image2DMap;
	}
	
	
	public ArrayList<Class<? extends DataGenerator>> getAnalytic2DList() {
		return analytic2DList;
	}

	public HashMap<String, ArrayList<Class<? extends DataGenerator>>> getAnalytic2DMap() {
		return analytic2DMap;
	}

	/**
	 * @return the noise2DList
	 */
	public ArrayList<Class<? extends DataGenerator>> getNoise2DList() {
		return noise2DList;
	}

	/**
	 * @return the noise2DMap
	 */
	public HashMap<String, ArrayList<Class<? extends DataGenerator>>> getNoise2DMap() {
		return noise2DMap;
	}
	
	/**
	 * @return the unary1DList
	 */
	public ArrayList<Class<? extends DataGenerator>> getUnary1DList() {
		return unary1DList;
	}

	/**
	 * @return the unary1DMap
	 */
	public HashMap<String, ArrayList<Class<? extends DataGenerator>>> getUnary1DMap() {
		return unary1DMap;
	}

	/**
	 * @return the binary1DList
	 */
	public ArrayList<Class<? extends DataGenerator>> getBinary1DList() {
		return binary1DList;
	}

	/**
	 * @return the binary1DMap
	 */
	public HashMap<String, ArrayList<Class<? extends DataGenerator>>> getBinary1DMap() {
		return binary1DMap;
	}

	/**
	 * @return the unary2DList
	 */
	public ArrayList<Class<? extends DataGenerator>> getUnary2DList() {
		return unary2DList;
	}

	/**
	 * @return the unary2DMap
	 */
	public HashMap<String, ArrayList<Class<? extends DataGenerator>>> getUnary2DMap() {
		return unary2DMap;
	}

	/**
	 * @return the binary2DList
	 */
	public ArrayList<Class<? extends DataGenerator>> getBinary2DList() {
		return binary2DList;
	}

	/**
	 * @return the binary2DMap
	 */
	public HashMap<String, ArrayList<Class<? extends DataGenerator>>> getBinary2DMap() {
		return binary2DMap;
	}

	public void loadOperation1DList() {
		
		unary1DList = new ArrayList<Class<? extends DataGenerator>>();
		unary1DList.add( FourierTransformOp.class ); 
		unary1DList.add( SignumOp.class ); 
		unary1DList.add( AutoCorrelateOp.class ); 
		unary1DList.add( ScaleOp.class ); 
		unary1DList.add( OffsetOp.class ); 
		unary1DList.add( ExponentOp.class ); 
		unary1DList.add( SubtractMeanOp.class ); 
		unary1DList.add( AbsoluteValueOp.class ); 
		unary1DList.add( NegateOp.class ); 
		unary1DList.add( ConjugateOp.class );
		unary1DList.add( DerivativeOp1D.class );
		unary1DList.add( IntegralOp1D.class );
		unary1DList.add( ReverseOp1D.class );
		unary1DList.add( ClipOp.class );
		unary1DList.add( SquareRootOp.class );
		unary1DList.add( ThresholdOp.class );
		unary1DList.add( MagnitudeOp.class );
		unary1DList.add( SquaredMagnitudeOp.class );
		unary1DList.add( PhaseOp.class );
		//unary1DList.add( new UnwrapOp.class );
		unary1DList.add( TranslateOp1D.class );
		unary1DList.add( InvertOp.class );
		unary1DList.add( CepstrumOp.class );
		unary1DList.add( ComplementFilterOp.class );
		unary1DList.add( RealOp.class );

		
		unary1DMap = createMap( unary1DList );
		
		binary1DList = new ArrayList<Class<? extends DataGenerator>>();
		binary1DList.add( PlusOp.class ); 
		binary1DList.add( MinusOp.class );
		binary1DList.add( TimesOp.class ); 
		binary1DList.add( DivideOp.class ); 
		binary1DList.add( ConvolveOp.class ); 
		binary1DList.add( CorrelateOp.class ); 
		
		binary1DMap = createMap( binary1DList ); 
		
	}

	public void loadOperation2DList() { //Warning: copy & paste: make sure to change from 1D to 2D
		
		unary2DList = new ArrayList<Class<? extends DataGenerator>>();
		unary2DList.add( FourierTransformOp.class ); 
		unary2DList.add( SignumOp.class ); 
		unary2DList.add( AutoCorrelateOp.class ); 
		unary2DList.add( RotateOp2D.class ); 
		unary2DList.add( ScaleOp.class ); 
		unary2DList.add( OffsetOp.class ); 
		unary2DList.add( ExponentOp.class ); 
		unary2DList.add( SubtractMeanOp.class ); 
		unary2DList.add( AbsoluteValueOp.class ); 
		unary2DList.add( NegateOp.class );
		unary2DList.add( ConjugateOp.class );
		unary2DList.add( DerivativeOp2D.class );
		unary2DList.add( IntegralOp2D.class );
		unary2DList.add( ReverseOp2D.class );
		unary2DList.add( ClipOp.class );
		unary2DList.add( SquareRootOp.class );
		unary2DList.add( ThresholdOp.class );
		unary2DList.add( MagnitudeOp.class );
		unary2DList.add( SquaredMagnitudeOp.class );
		unary2DList.add( PhaseOp.class );
		//unary2DList.add( UnwrapOp.class );
		unary2DList.add( TranslateOp2D.class );
		unary2DList.add( InvertOp.class );
		unary2DList.add( CepstrumOp.class );
		unary2DList.add( PhaseDetourHologramOp.class );
		unary2DList.add( HologramEncoderOp.class );
		unary2DList.add( ComplementFilterOp.class );
		unary2DList.add( RealOp.class );
		
		unary2DMap = createMap( unary2DList );
		
		binary2DList = new ArrayList<Class<? extends DataGenerator>>();
		
		binary2DMap = createMap( binary2DList ); 
		binary2DList.add( PlusOp.class ); 
		binary2DList.add( MinusOp.class );
		binary2DList.add( TimesOp.class );
		binary2DList.add( DivideOp.class ); 
		binary2DList.add( ConvolveOp.class ); 
		binary2DList.add( CorrelateOp.class ); 
	}
	
	public void loadNoise2DList() {
		
		noise2DList = new ArrayList<Class<? extends DataGenerator>>();
		noise2DList.add( GaussianNoiseFunctionTerm2D.class);
		noise2DList.add( ExponentialNoiseFunctionTerm2D.class);
		noise2DList.add( LorentzNoiseFunctionTerm2D.class );
		noise2DList.add( PoissonNoiseFunctionTerm2D.class);
		noise2DList.add( RandomPhaseFunctionTerm2D.class );
		noise2DList.add( RayleighNoiseFunctionTerm2D.class);
		noise2DList.add( SaltAndPepperNoiseFunctionTerm2D.class);
		noise2DList.add( UniformNoiseFunctionTerm2D.class);
		
		noise2DMap = createMap( noise2DList );
	}

	public void loadImage2DList() {
		
		image2DList = new ArrayList<Class<? extends DataGenerator>>(); 
		image2DList.add( ZeroFunctionTerm2D.class );
		image2DList.add( AFunctionTerm2D.class );
		image2DList.add( BoatFunctionTerm2D.class );
		image2DList.add( BarChartFunctionTerm2D.class );
		image2DList.add( ConstantFunctionTerm2D.class );
		image2DList.add( EFunctionTerm2D.class );
		image2DList.add( GradientFunctionTerm2D.class );
		image2DList.add( LenaFunctionTerm2D.class );
		image2DList.add( MRIPhase1FunctionTerm2D.class );
		image2DList.add( SARPhase1FunctionTerm2D.class );
		image2DList.add( StepWedgeFunctionTerm2D.class );
		image2DList.add( DataFunctionTerm2D.class );
		
		image2DMap = createMap( image2DList );
	}
	
	public void loadAnalytic2DList() {
		
		analytic2DList = new ArrayList<Class<? extends DataGenerator>>(); 
		analytic2DList.add( CassegrainFunctionTerm2D.class );
		analytic2DList.add( CylinderFunctionTerm2D.class );
		analytic2DList.add( CassegrainMultiArmFunctionTerm2D.class );
		analytic2DList.add( EApertureFunctionTerm2D.class );
		analytic2DList.add( MultiArmFunctionTerm2D.class );
		analytic2DList.add( RectApertureFunctionTerm2D.class );
		
		analytic2DMap = createMap( analytic2DList );
		
	}
	
	public void loadFunctionTerm1DList() { 
		
		functionTerm1DList = new ArrayList<Class<? extends DataGenerator>>(); 
		functionTerm1DList.add( ZeroFunctionTerm1D.class );
		functionTerm1DList.add( BesincFunctionTerm1D.class );
		functionTerm1DList.add( BesselFunctionTerm1D.class );
		functionTerm1DList.add( BinomialNoiseFunctionTerm1D.class );
		functionTerm1DList.add( ChirpFunctionTerm1D.class );
		functionTerm1DList.add( CombFunctionTerm1D.class );
		functionTerm1DList.add( ConstantFunctionTerm1D.class );
		functionTerm1DList.add( CosineFunctionTerm1D.class );
		functionTerm1DList.add( DeltaFunctionTerm1D.class );
		functionTerm1DList.add( DeltaPairEvenFunctionTerm1D.class );
		functionTerm1DList.add( DeltaPairOddFunctionTerm1D.class );
		functionTerm1DList.add( DoubleSlitFunctionTerm1D.class );
		functionTerm1DList.add( ExponentialNoiseFunctionTerm1D.class );
		functionTerm1DList.add( GaussianFunctionTerm1D.class );
		functionTerm1DList.add( GaussianNoiseFunctionTerm1D.class );
		functionTerm1DList.add( HammingWindowFunctionTerm1D.class );
		functionTerm1DList.add( HanningWindowFunctionTerm1D.class );
		functionTerm1DList.add( LineFunctionTerm1D.class );
		functionTerm1DList.add( LorentzianFunctionTerm1D.class );
		functionTerm1DList.add( LorentzNoiseFunctionTerm1D.class );
		functionTerm1DList.add( MonomialFunctionTerm1D.class );
		functionTerm1DList.add( AbsMonomialFunctionTerm1D.class );
		functionTerm1DList.add( ParzenWindowFunctionTerm1D.class );
		functionTerm1DList.add( PoissonNoiseFunctionTerm1D.class );
		functionTerm1DList.add( PoissonProcessFunctionTerm1D.class );
		functionTerm1DList.add( RandomPhaseFunctionTerm1D.class );
		functionTerm1DList.add( RayleighNoiseFunctionTerm1D.class );
		functionTerm1DList.add( RectangleFunctionTerm1D.class );
		functionTerm1DList.add( SaltAndPepperNoiseFunctionTerm1D.class );
		functionTerm1DList.add( SignumFunctionTerm1D.class );
		functionTerm1DList.add( SineFunctionTerm1D.class );
		functionTerm1DList.add( SincFunctionTerm1D.class );
		functionTerm1DList.add( SincSquaredFunctionTerm1D.class );
		functionTerm1DList.add( SquareWaveFunctionTerm1D.class );
		functionTerm1DList.add( StairstepFunctionTerm1D.class );
		functionTerm1DList.add( StepFunctionTerm1D.class );
		functionTerm1DList.add( StepExponentialFunctionTerm1D.class );
		functionTerm1DList.add( TriangleFunctionTerm1D.class );
		functionTerm1DList.add( UniformNoiseFunctionTerm1D.class );
		functionTerm1DList.add( WelchWindowFunctionTerm1D.class );
		functionTerm1DList.add( DataFunctionTerm1D.class );
		
		functionTerm1DMap = createMap( functionTerm1DList );
	}

}
