package signals.operation;

import java.awt.Point;
import java.util.ArrayList;

/**
 * The following code is adapted from C-language code for Goldstein's 
 * algorithm with dipole residue removal found in: 
 * 
 * Dennis C. Ghiglia and Mark D. Pritt. (1998). Two-Dimensional Phase 
 * Unwrapping: Theory, Algorithms, and Software. New York: John-Wiley & Sons.  
 * 
 * The algorithm was originally proposed by Goldstein, Zebker, and Werner in: 
 * 
 * R. M. Goldstein, H. S. Zebker, and C. L. Werner, "Satellite radar interferometry:
 * two-dimensional phase unwrapping," Radio Science, Vol. 23, No. 4, pp. 713-720, 1988.
 *
 * In their book, Ghiglia and Pritt detailed important steps which were not included
 * in the original publication. They learned about these steps by speaking with Richard
 * M. Goldstein. They also added dipole residue removal to the algorithm as a preprocessing step.
 * 
 * @author Juliet
 *
 */
public class PhaseUnwrapper2D {

	public static final double PI = Math.PI; 
	public static final double MINUS_PI = -Math.PI; 
	public static final double TWOPI = 2*PI;
	public static final double MINUS_TWOPI = -TWOPI;

	//possible residue charges
	public static final int NEGATIVE = -1; 
	public static final int POSITIVE = 1; 
	public static final int NEUTRAL = 0; 

	//initial phase data
	double[] phase; 

	//unwrapped phase data
	double[] unwrapped; 

	//array dimensions
	int xsize, ysize; 
	int dimension;

	//whether each pixel is a residue and what kind it is
	int[] residue_charge; 

	boolean[] branch_cut;
	boolean[] balanced; 
	boolean[] active; 

	ArrayList<Point> residue_indices;
	ArrayList<Integer> branchcut_indices;

	//pixel indices that have been unwrapped, waiting to have their neighbors unwrapped
	ArrayList<Point> adjoin_list; 

	/**
	 * @param phase
	 * @param xsize
	 * @param ysize
	 */
	public PhaseUnwrapper2D(double[] phase, int xsize, int ysize) {
		setPhase( phase, xsize, ysize );
	}

	public void setPhase(double[] phase, int xsize, int ysize) {

		this.phase = phase;
		this.xsize = xsize;
		this.ysize = ysize;

		dimension = phase.length; 
		residue_charge = new int[dimension];
		branch_cut = new boolean[dimension];
		active = new boolean[dimension];
		balanced = new boolean[dimension];
		unwrapped = new double[dimension];
		adjoin_list = new ArrayList<Point>();
		residue_indices = new ArrayList<Point>();
		branchcut_indices = new ArrayList<Integer>();
	}

	public double[] unwrap() {

		find_residues();
		remove_dipoles();
		branchCut(); 

		//reuse balanced array
		balanced = new boolean[dimension];

		unwrapAroundCuts(); 

		return unwrapped;
	}

	/**
	 * computes the phase derivative of the parameters p1 and p2. 
	 * Adapted from the gradient function on page 325 of 
	 * "Two-Dimensional Phase Unwrapping: Theory, Algorithms, and Software"
	 * by Dennis C. Ghiglia and Mark D. Pritt.
	 */
	public static double gradient( double p1, double p2 ) {

		double r = p1 - p2; 
		if ( r > PI ) r -= TWOPI; //wrap 
		if( r < MINUS_PI ) r += TWOPI; 
		return r; 
	}

	/**
	 * identifies positive and negative residues in the phase data 
	 * Adapted from the residues function on page 325 of 
	 * "Two-Dimensional Phase Unwrapping: Theory, Algorithms, and Software"
	 * by Dennis C. Ghiglia and Mark D. Pritt.
	 */
	public void find_residues() {

		double pos_thresh = 0.02*PI; 
		double neg_thresh = 0.02*MINUS_PI;

		for( int j = 0; j < ysize - 1; ++j ) {

			for( int i = 0; i < xsize - 1; ++i ) {

				int k = j * xsize + i; 

				double r = gradient( phase[k+1], phase[k] ) +
				gradient( phase[k+1+xsize], phase[k+1] ) + 
				gradient( phase[k+xsize], phase[k+1+xsize] ) +
				gradient( phase[k], phase[k+xsize] );

				if( r > pos_thresh ) {

					residue_charge[k] = POSITIVE;  
					residue_indices.add( new Point( i, j ) );
				}
				else if ( r < neg_thresh ) {

					residue_charge[k] = NEGATIVE; 
					residue_indices.add( new Point( i, j ) );
				}
			}
		}
	}

	public boolean onBorder( int x, int y ) {

		return ( x == 0 || x == (xsize-1) || y == 0 || y == (ysize-1 ) );
	}

	public boolean insideImage( int x, int y ) {

		return ( x >= 0 && x < xsize && y >= 0 && y < ysize );
	}

	/**
	 * places a branch cut between the pixel at location (a,b) and the pixel at location (c,d)
	 * Adapted from the place cut function on page 329 of 
	 * "Two-Dimensional Phase Unwrapping: Theory, Algorithms, and Software"
	 * by Dennis C. Ghiglia and Mark D. Pritt.
	 */
	public void placeCut( int a, int b, int c, int d ) {

		//residue location is upper-left corner of 4-square
		if( c > a && a > 0 ) ++a; 
		else if ( c < a && c > 0 ) ++c; 
		if ( d > b && b > 0 ) ++b; 
		else if ( d < b && d > 0 ) ++d; 

		if( a==c && b==d ) {

			int k = b*xsize+a;
			branch_cut[k] = true; 
			if( !onBorder( a, b ) ) branchcut_indices.add( k );
			return;
		}

		int m = Math.abs( a - c ); 
		int n = Math.abs( d - b );

		if( m > n ) {

			int istep = ( a < c ) ? 1 : -1; 
			double r = (double)(d-b)/(double)(c-a); 
			for( int i = a; i != c+istep; i+= istep ) {

				int j = (int)(b + (i-a)*r + 0.5);
				int k = j*xsize+i;
				branch_cut[k] = true;
				if( !onBorder( i, j ) ) branchcut_indices.add( k );
			}

		} else {

			int jstep = ( b < d ) ? 1 : -1; 
			double r = (double)(c-a)/(double)(d-b); 
			for( int j = b; j != d + jstep; j+= jstep ) {

				int i = (int)( a + (j-b)*r + 0.5 ); 
				branch_cut[j*xsize+i] = true;
				int k = j*xsize+i;
				branch_cut[k] = true;
				if( !onBorder( i, j ) ) branchcut_indices.add( k );
			}
		}
	}

	/**
	 * returns the closest border point to the pixel at (a, b) 
	 */
	public Point closestBorderPoint( int a, int b ) {

		int minX = Math.min( a , xsize-a ); 
		int minY = Math.min( b, ysize-b ); 

		Point toReturn = null; 

		if( minX < minY ) { 

			if( a < xsize - a) { 

				//make a horizontal cut from the left edge to the point
				toReturn = new Point( 0, b );

			} else {

				//make a horizontal cut from the right edge to the point
				toReturn = new Point ( xsize-1, b );
			}

		} else {


			if( b < ysize - b ) {

				//make a vertical cut from the top to the point
				toReturn = new Point( a, 0 ); 

			} else {

				//make a vertical cut from the bottom to the point
				toReturn = new Point( a, ysize-1 ); 
			}

		}

		return toReturn;
	}

	/**
	 * identifies dipole residues (pairs of adjoining residues of opposite sign), places a branch
	 * cut between them, and then removes them. 
	 * Adapted from the dipoles function on page 331 of 
	 * "Two-Dimensional Phase Unwrapping: Theory, Algorithms, and Software"
	 * by Dennis C. Ghiglia and Mark D. Pritt.
	 */
	public void remove_dipoles() {

		ArrayList<Point> new_residue_indices = new ArrayList<Point>(); 

		for( Point p : residue_indices ) {

			int i = p.x;
			int j = p.y; 

			int k = j * xsize + i;
			boolean hasNeighbor = false; 
			int neighbor = 0; 

			if( residue_charge[k] == POSITIVE ) {

				// + - 
				// 0 0 
				if( i < xsize-1 && residue_charge[k+1] == NEGATIVE ) {

					neighbor = k+1; 
					hasNeighbor = true; 

					// + 0 
					// - 0 
				} else if ( j < ysize-1 && residue_charge[k+xsize] == NEGATIVE ) {

					neighbor = k+xsize;
					hasNeighbor = true;
				}


			} else if ( residue_charge[k] == NEGATIVE ) {

				// - + 
				// 0 0 
				if( i < xsize-1 && residue_charge[k+1] == POSITIVE ) {

					neighbor = k+1; 
					hasNeighbor = true;

					// - 0 
					// + 0 
				} else if ( j < ysize-1 && residue_charge[k+xsize] == POSITIVE ) {

					neighbor = k+xsize;
					hasNeighbor = true;
				}
			}

			if( hasNeighbor ) {

				placeCut( i, j , neighbor%xsize, neighbor/xsize );
				residue_charge[k] = NEUTRAL; 
				residue_charge[neighbor] = NEUTRAL;

			} else {

				new_residue_indices.add( p ); 
			}

		}

		residue_indices = new_residue_indices;
	}

	/**
	 * generates branch cuts using goldstein's algorithm
	 * Adapted from the goldstein branch cuts function on page 385 of 
	 * "Two-Dimensional Phase Unwrapping: Theory, Algorithms, and Software"
	 * by Dennis C. Ghiglia and Mark D. Pritt.
	 */
	public void branchCut() {

		ArrayList<Integer> active_list = new ArrayList<Integer>();

		//for each residue
		for( Point p : residue_indices ) {

			int i = p.x;
			int j = p.y; 
			int k = j * xsize + i;

			//if the residue is not balanced
			if( !balanced[k] ) {

				//mark the residue active
				active[k] = true; 
				active_list.add( k );

				//initialize charge to the charge of this residue
				int charge = residue_charge[k];

				//create a box, initially 3x3. Increase the size of the box if necessary
				boxloop: for( int boxSize = 3; boxSize <= (xsize+ysize); boxSize += 2 ) {

					int bs2 = boxSize / 2; 

					//for each active residue
					int active_list_idx = 0; 
					while( active_list_idx < active_list.size() ) {

						int active_idx = active_list.get( active_list_idx++ ); 

						//center the box at the active reside 
						int boxctr_i = active_idx % xsize; 
						int boxctr_j = active_idx / xsize;

						//for each pixel inside the box  
						for( int jj = (boxctr_j - bs2); jj <= (boxctr_j + bs2); ++jj ) {

							for ( int ii = (boxctr_i - bs2); ii <= (boxctr_i + bs2); ++ii ) {

								if( insideImage( ii, jj )) { 
									
									int kk = jj * xsize + ii; 

									//if box pixel lies on border, neutralize and make a branch cut 
									//from active pixel to the border
									if( onBorder( ii, jj ) ) {

										charge = 0; 
										Point p2 = closestBorderPoint(boxctr_i, boxctr_j);
										placeCut( p2.x, p2.y, boxctr_i, boxctr_j);

										//if the box pixel is an inactive residue 
									} else if( residue_charge[kk] != NEUTRAL && !active[kk] ) {

										//balance the residue if it has not been balanced yet 
										if( !balanced[kk] ) {

											charge += residue_charge[kk]; 
											balanced[kk] = true; 
										}

										//mark the box pixel as active 
										active_list.add( kk ); 
										active[kk] = true; 

										//place a branch cut between the active pixel and the box pixel
										placeCut( ii, jj, boxctr_i, boxctr_j );

									} //end if box pixel is an inactive residue

								} //end if inside image

								//if balanced, done.
								if( charge == 0 ) break boxloop;

							} //for ii

						} //for jj

						active_list_idx++;

					} //for each active pixel

				} //boxloop 

				//if not balanced, place a branch cut to the border
				if( charge != 0 ) {

					int near_i = 0;
					int near_j = 0;
					int rim_i = 0; 
					int rim_j = 0;

					double min_dist = Double.MAX_VALUE; 
					for( int active_idx : active_list ) {

						int ii = active_idx % xsize; 
						int jj = active_idx / xsize;

						Point p2 = closestBorderPoint(ii, jj);
						double dist = ( p2.x - ii )*( p2.x - ii ) + ( p2.y - jj )*( p2.y - jj );
						if( dist < min_dist ) {

							min_dist = dist; 
							near_i = ii; 
							near_j = jj; 
							rim_i = p2.x; 
							rim_j = p2.y;
						}
					}

					placeCut( near_i, near_j, rim_i, rim_j );

				}// end if not balanced

				//done. mark all the active pixels balanced and inactive
				for( int active_idx : active_list ) {

					active[active_idx] = false;
					balanced[active_idx] = true; 
				}
				active_list = new ArrayList<Integer>();

			} //if the residue is not balanced

		}//for each residue
	}

	/**
	 * given a single pixel, insert the four neighboring pixels into the list
	 * Adapted from the update list function on page 338 of 
	 * "Two-Dimensional Phase Unwrapping: Theory, Algorithms, and Software"
	 * by Dennis C. Ghiglia and Mark D. Pritt.
	 */
	public void updateList( int x, int y, double val ) {

		//pixel to the left
		int w = y * xsize + x - 1; 
		insertList( x-1, y, val + gradient( phase[w], phase[w+1]) ); 

		//pixel to the right
		w = y * xsize + x; 
		insertList( x+1, y, val - gradient( phase[w], phase[w+1]) ); 

		//pixel above
		w = (y-1) * xsize + x; 
		insertList( x, y-1, val + gradient( phase[w], phase[w+xsize]) ); 

		//pixel below
		w = y * xsize + x; 
		insertList( x, y+1, val - gradient( phase[w], phase[w+xsize]) ); 

	}

	/**
	 * Inserts pixel at (a,b) with unwrapped phase value val into the adjoin list
	 * @param a
	 * @param b
	 * @param val
	 */
	public void insertList( int a, int b, double val ) {

		int k = b * xsize + a;

		//Conditions that must be true to process the pixel: 
		//1. it has not already been unwrapped (!balanced[k])
		//2. it is not in the adjoin list already (!active[k])
		//3. it is not a branch cut (!branch_cut[k])
		//4. it is inside the image and not on the border TODO: include border pixels

		if( !balanced[k] && !active[k] && !branch_cut[k] && 
				a > 0 && a < xsize - 1 && b > 0 && b < ysize - 1 ) {

			//Unwrap the pixel and store the value in the solution array
			unwrapped[k] = val; 

			//Insert the pixel in the adjoin list
			adjoin_list.add( new Point( a, b ) );  

			//Mark the pixel unwrapped
			balanced[k] = true;
		}

	}

	/**
	 * unwraps the phase data by Itoh's method without crossing branch cuts
	 * Adapted from the unwrap around cuts function on page 333 of 
	 * "Two-Dimensional Phase Unwrapping: Theory, Algorithms, and Software"
	 * by Dennis C. Ghiglia and Mark D. Pritt.
	 */
	public void unwrapAroundCuts() {

		//while there are more nonborder, non-branchcut pixels to be unwrapped
		for( int j = 1; j < ysize - 1; ++j ) {

			for( int i = 1; i < xsize - 1; ++i ) {

				int k = j * xsize + i; 

				//select a starting pixel
				if( !balanced[k] && !branch_cut[k] ) {

					//store its phase value in the solution array
					unwrapped[k] = phase[k];

					//mark the pixel unwrapped
					balanced[k] = true; 

					//update the adjoin list
					updateList( i, j, phase[k] );

					//while the adjoin list is not empty
					while( adjoin_list.size() != 0 ) {

						//fetch any pixel from the adjoin list
						Point p = adjoin_list.get( adjoin_list.size() - 1 ); 
						adjoin_list.remove( adjoin_list.size() - 1 ); 

						//update the adjoin list
						int kk = p.y * xsize + p.x; 
						updateList( p.x, p.y, unwrapped[kk]);
					}

				}

			}
		}

		//
		//update the branch cut pixels
		//

		for( Integer k : branchcut_indices ) {

			//if the pixel to the side is not a branch cut, use that pixel to unwrap this pixel
			if( !branch_cut[k-1] )
				unwrapped[k] = unwrapped[k-1] + gradient( phase[k], phase[k-1] ); 

			//if the pixel above is not a branch cut, use that pixel to unwrap this pixel
			else if ( !branch_cut[k-xsize] )
				unwrapped[k] = unwrapped[k-xsize] + gradient( phase[k], phase[k-xsize] );
		}

	}

}
