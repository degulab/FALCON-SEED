/*
 * @(#)FilterDataError	3.1.0	2014/05/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

/**
 * フィルタ編集におけるエラー種別とエラー情報を保持するクラス。
 * このクラスは、不変オブジェクトとする。
 * 
 * @version 3.1.0	2014/05/19
 * @since 3.1.0
 */
public class FilterDataError extends FilterError
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private FilterErrorType	_errType;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FilterDataError(FilterErrorType errorType, String errorMessage) {
		this(errorType, errorMessage, null);
	}
	
	public FilterDataError(FilterErrorType errorType, String errorMessage, Object errorCause) {
		super(errorMessage, errorCause);
		_errType = (errorType==null ? FilterErrorType.UNKNOWN : errorType);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public FilterErrorType getErrorType() {
		return _errType;
	}

	@Override
	public int hashCode() {
		int hv = 0;
		hv = hv * 31 + _errType.hashCode();
		hv = hv * 31 + super.hashCode();
		return hv;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean isEqualFields(Object obj) {
		if (!(obj instanceof FilterDataError))
			return false;
		
		FilterDataError aData = (FilterDataError)obj;
		
		//--- parameters
		if (aData._errType != this._errType)
			return false;

		//--- check super-class
		return super.isEqualFields(obj);
	}

	protected void appendParameters(StringBuilder buffer) {
		//--- errType
		buffer.append("errType=");
		buffer.append(_errType);
		buffer.append(", ");
		
		//--- append super-class
		super.appendParameters(buffer);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
