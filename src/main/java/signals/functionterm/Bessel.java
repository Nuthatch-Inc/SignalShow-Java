package signals.functionterm;

/**
 * Utility class for creating bessel functions
 * @author Juliet
 *
 */
public class Bessel {

	/**
	 * FUNCTION J0 (x)   'Jon Smith, Scientific Analysis on the  Pocket Calculator, p.131
	 * @param x take the J0 bessel function of this value. 
	 */
	public static double J0( double x ) { 
		
		double J0 = 0; 
		
		if( x >= 3 ) { //Bessel function for x >= 3
			
			double x1 = (3 / x);
			double x2 = x1 * x1;
			double x3 = x2 * x1;
			double X4 = x2 * x2;
			double X5 = x3 * x2;
			double X6 = x3 * x3;
			
			double B0 = .797885;
			double b1 = -7.7E-07;
			double B2 = -.0055274;
			double B3 = -9.1024E-05;
			double B4 = 1.37237E-03;
			double B5 = -7.2805E-04;
			double B6 = 1.4476E-04;
			double F2 = B0 + b1 * x1 + B2 * x2 + B3 * x3 + B4 * X4 + B5 * X5 + B6 * X6;
		
			double T0 = -.7853982;
			double T1 = -.041664;
			double T2 = -3.954E-05;
			double T3 = 2.62573E-03;
			double T4 = -5.4125E-04;
			double T5 = -2.9333E-04;
			double T6 = 1.3558E-04;
			double alpha = x + T0 + T1 * x1 + T2 * x2 + T3 * x3 + T4 * X4 + T5 * X5 + T6 * X6;
		   
			J0 = F2 * Math.cos(alpha) / Math.sqrt(x);
			
			
		} else { //Bessel function for -3 <= x <= 3(X/3)^2
			
			double x2 = x * x / 9;                  
			double X4 = x2 * x2;
			double X6 = X4 * x2;
			double X8 = X4 * X4;
			double X10 = X8 * x2;
			double X12 = X6 * X6;

			double B0 = 1;
			double B2 = -2.249999;
			double B4 = 1.265621;
			double B6 = -.316387;
			double B8 = .044448;
			double B10 = -.0039444;
			double B12 = .00021;

			J0 = (B0 + B2 * x2 + B4 * X4 + B6 * X6 + B8 * X8 + B10 * X10 + B12 * X12);
		
		}
		
		return J0;
		
	}  
	
	/**
	 * 
	 * @param x take the J1 bessel function of this value. 
	 * @return
	 */
	public static double J1( double x ) { 
		
		double J1 = 0; 
		
		if( x >= 3 ) { //Bessel function for x >= 3
			
			double x1 = (3 / x);
			double x2 = x1 * x1;
			double x3 = x2 * x1;
			double X4 = x3 * x1;
			double X5 = X4 * x1;
			double X6 = X5 * x1;

			double B0 = .7978846;
			double b1 = 1.56E-06;
			double B2 = .0165967;
			double B3 = 1.7105E-04;
			double B4 = -2.49511E-03;
			double B5 = 1.13653E-03;
			double B6 = -2.0033E-04;
			double F2 = B0 + b1 * x1 + B2 * x2 + B3 * x3 + B4 * X4 + B5 * X5 + B6 * X6;
			
			double T0 = -2.356195;
			double T1 = .124996;
			double T2 = .0000565;
			double T3 = -.006379;
			double T4 = 7.4348E-04;
			double T5 = 7.9824E-04;
			double T6 = -2.9166E-04;
			double alpha = x + T0 + T1 * x1 + T2 * x2 + T3 * x3 + T4 * X4 + T5 * X5 + T6 * X6;
			
			J1 = (F2 * Math.cos(alpha)) / Math.sqrt(x);
			
		} else { //Bessel function for -3 <= x <= 3(X/3)^2
			
			 double x2 = x * x / 9;                  
			 double X4 = x2 * x2;
			 double X6 = X4 * x2;
			 double X8 = X4 * X4;
			 double X10 = X8 * x2;
			 double X12 = X8 * X4;

			 double B0 = .5;
			 double B2 = -.5624998;
			 double B4 = .2109357;
			 double B6 = -.0395429;
			 double B8 = .0044332;
			 double B10 = -3.1761E-04;
			 double B12 = 1.109E-05;

			J1 = x * (B0 + B2 * x2 + B4 * X4 + B6 * X6 + B8 * X8 + B10 * X10 + B12 * X12);
			
		}
		
		return J1;
		
	} 

}
