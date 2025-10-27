package signals.core;


public class FunctionTerm1DSourceData extends SourceData1D {
	
	FunctionTerm1D term;
	double[] indices; 

	public FunctionTerm1DSourceData() {
		super(null, null );
	}

	/**
	 * @return the term
	 */
	public FunctionTerm1D getTerm() {
		return term;
	}

	/**
	 * @param term the term to set
	 */
	public void setTerm(FunctionTerm1D term) {
		this.term = term;
	}

	/**
	 * @return the indices
	 */
	public double[] getIndices1D() {
		return indices;
	}

	/**
	 * @param indices the indices to set
	 */
	public void setIndices(double[] indices) {
		this.indices = indices;
	}

	/* (non-Javadoc)
	 * @see signals.core.SourceData#getData()
	 */
	@Override
	public double[] getData() {
		return term.create(indices);
	}

	/* (non-Javadoc)
	 * @see signals.core.SourceData#isZeroCentered()
	 */
	@Override
	public boolean isZeroCentered() {
		if( indices != null )
		return indices[0] != 0; 
		return true;
	}

}
