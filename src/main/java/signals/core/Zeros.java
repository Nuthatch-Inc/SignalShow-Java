package signals.core;

import java.lang.ref.SoftReference;
import java.util.HashMap;

public class Zeros {
	
	@SuppressWarnings("unchecked")
	protected HashMap<Integer, SoftReference> zeros; 
	
	// Protected constructor is sufficient to suppress unauthorized calls to the constructor
	@SuppressWarnings("unchecked")
	protected Zeros() {
		
		zeros = new HashMap<Integer, SoftReference>(); 
	}
	
	/*
	 * returns true if the input object is the zero array
	 */
	public static boolean isZero( double[] array ) {
		
		return array.equals(zeros(array.length)); 
	}
	
	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	 * or the first access to SingletonHolder.instance , not before.
	 */
	private static class SingletonHolder { 
		private final static Zeros INSTANCE = new Zeros();
	}

	public static Zeros getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public static double[] zeros( int dimension ) {
		
		//return new double[dimension];
		return (double[]) getInstance().getZeros(dimension).get();
	}
	
	@SuppressWarnings("unchecked")
	private SoftReference getZeros( int key ) {
		
		SoftReference zeroArray = null; 
		if( !zeros.containsKey(key) || zeros.get(key).get() == null ) {
			
			zeroArray = new SoftReference( new double[key] );
			zeros.put( key, zeroArray );
			
		} else zeroArray = zeros.get(key); 
		
		return zeroArray; 
	}
	
}
