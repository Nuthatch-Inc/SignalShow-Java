package signals.core;

public class ImageProfile {

	public static Function getYProfile(int rise, int y0, boolean zeroCentered,
					int dimensionX, int dimensionY, double[] dataA, 
					double[] dataB, String descriptor ) {

		int xStart = 0;  
		if( zeroCentered ) {
			y0 += dimensionX/2;
			xStart = dimensionY/2; 
		} 

		double[] realProfile = new double[dimensionX]; 
		double[] imagProfile = new double[dimensionX];

		int y = y0; 
		int x = xStart; 

		if( rise > 0 ) {

			if( y < 0 ) { 

				//move into picture
				while( y < 0 ) {

					y += rise; 
					++x; 
				}

				//travel upwards from intercept
				while( x < dimensionX && y < dimensionY ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[x] = dataA[index]; 
					imagProfile[x] = dataB[index];
					y += rise; 
					++x;

				}

			} else if( y >= dimensionY ) {

				//move into picture
				while( y >= dimensionY ) {

					y -= rise; 
					--x; 
				}

				//travel downwards from intercept
				while( x >= 0 && y >= 0 ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[x] = dataA[index]; 
					imagProfile[x] = dataB[index]; 
					y -= rise; 
					--x; 

				}

			} else { //x0 is in the center

				//travel upwards from intercept
				while( x < dimensionX && y < dimensionY ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[x] = dataA[index]; 
					imagProfile[x] = dataB[index]; 
					y += rise; 
					++x;

				}

				y = y0; 
				x = xStart; 

				//travel downwards from intercept
				while( x >= 0 && y >= 0 ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[x] = dataA[index]; 
					imagProfile[x] = dataB[index]; 
					y -= rise; 
					--x; 

				}

			}

		} else if ( rise < 0 ) {

			if( y < 0 ) { 

				//move into picture
				while( y < 0 ) {

					y -= rise; 
					--x;  
				}

				//travel downwards from intercept
				while( y < dimensionY && x >= 0 ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[x] = dataA[index]; 
					imagProfile[x] = dataB[index]; 
					y -= rise; 
					--x; 

				}

			} else if( y >= dimensionY ) {

				//move into picture
				while( y >= dimensionY ) {

					y += rise; 
					++x; 
				}

				//travel upwards from intercept
				while( y >= 0 && x < dimensionX ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[x] = dataA[index]; 
					imagProfile[x] = dataB[index]; 
					y += rise; 
					++x; 
				}

			} else { //x0 is in the center

				//travel downwards from intercept
				while( y < dimensionY && x >= 0 ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[x] = dataA[index]; 
					imagProfile[x] = dataB[index]; 
					y -= rise; 
					--x; 

				}

				x = xStart; 
				y = y0; 

				//travel upwards from intercept
				while( y >= 0 && x < dimensionX ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[x] = dataA[index]; 
					imagProfile[x] = dataB[index]; 
					y += rise; 
					++x; 
				}

			}


		} else { //flat run (vertical line)

			int startIndex = (dimensionY-y0-1)*dimensionX; 
			
			for( int x1 = 0; x1 < dimensionX; ++x1 ) { 
 
				realProfile[x1] = dataA[startIndex+x1]; 
				imagProfile[x1] = dataB[startIndex+x1]; 
			} 

		}

		return FunctionFactory.createFunction1D(realProfile, imagProfile, zeroCentered,
				descriptor+" [y = "+rise+"x+"+y0+"]");

	}

	public static Function getXProfile(int run, int x0, boolean zeroCentered,
			int dimensionX, int dimensionY, double[] dataA, double[] dataB, 
			String descriptor) {

		int yStart = 0;  
		if( zeroCentered ) {
			x0 += dimensionX/2;
			yStart = dimensionY/2; 
		}

		double[] realProfile = new double[dimensionY]; 
		double[] imagProfile = new double[dimensionY];

		int y = yStart; 
		int x = x0; 

		if( run > 0 ) {

			if( x < 0 ) { 

				//move into picture
				while( x < 0 ) {

					x += run; 
					++y; 
				}

				//travel upwards from intercept
				while( x < dimensionX && y < dimensionY ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[y] = dataA[index]; 
					imagProfile[y] = dataB[index];
					x += run;
					++y; 

				}

			} else if( x >= dimensionX ) {

				//move into picture
				while( x >= dimensionX ) {

					x -= run; 
					--y; 
				}

				//travel downwards from intercept
				while( x >= 0 && y >= 0 ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[y] = dataA[index]; 
					imagProfile[y] = dataB[index]; 
					x -= run; 
					--y; 

				}

			} else { //x0 is in the center

				//travel upwards from intercept
				while( x < dimensionX && y < dimensionY ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[y] = dataA[index]; 
					imagProfile[y] = dataB[index]; 
					x += run;
					++y; 

				}

				y = yStart; 
				x = x0; 

				//travel downwards from intercept
				while( x >= 0 && y >= 0 ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[y] = dataA[index]; 
					imagProfile[y] = dataB[index]; 
					x -= run; 
					--y; 

				}

			}

		} else if ( run < 0 ) {

			if( x < 0 ) { 

				//move into picture
				while( x < 0 ) {

					x -= run; 
					--y; 
				}

				//travel downwards from intercept
				while( x < dimensionX && y >= 0 ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[y] = dataA[index]; 
					imagProfile[y] = dataB[index]; 
					x -= run; 
					--y; 

				}

			} else if( x >= dimensionX ) {

				//move into picture
				while( x >= dimensionX ) {

					x += run; 
					++y; 
				}

				//travel upwards from intercept
				while( x >= 0 && y < dimensionY ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[y] = dataA[index]; 
					imagProfile[y] = dataB[index]; 
					x += run; 
					++y; 

				}

			} else { //x0 is in the center

				//travel downwards from intercept
				while( x < dimensionX && y >= 0 ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[y] = dataA[index]; 
					imagProfile[y] = dataB[index]; 
					x -= run; 
					--y; 

				}

				y = yStart; 
				x = x0; 

				//travel upwards from intercept
				while( x >= 0 && y < dimensionY ) {

					int index = (dimensionY-y-1)*dimensionX+x; 
					realProfile[y] = dataA[index]; 
					imagProfile[y] = dataB[index]; 
					x += run; 
					++y; 

				}

			}


		} else { //flat run (vertical line)

			for( int y1 = 0; y1 < dimensionY; ++y1 ) { 

				int index = (dimensionY-y1-1)*dimensionX+x0; 
				realProfile[y1] = dataA[index]; 
				imagProfile[y1] = dataB[index]; 
			} 

		}

		return FunctionFactory.createFunction1D(realProfile, imagProfile, zeroCentered,
				descriptor+" [x = "+run+"y+"+x0+"]");
	}
	
}
