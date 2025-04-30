/*
 * @(#)FilterError	3.1.0	2014/05/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import ssac.util.Objects;

/**
 * フィルタ編集におけるエラー情報を保持するクラス。
 * このクラスは、不変オブジェクトとする。
 * 
 * @version 3.1.0	2014/05/19
 * @since 3.1.0
 */
public class FilterError
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final String			_errMessage;
	private final Object			_errCause;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FilterError(String errorMessage) {
		this(errorMessage, null);
	}
	
	public FilterError(String errorMessage, Object errorCause) {
		this._errMessage = errorMessage;
		this._errCause   = errorCause;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getErrorMessage() {
		return _errMessage;
	}
	
	public Object getErrorCause() {
		return _errCause;
	}

	@Override
	public int hashCode() {
		int hv = 0;
		hv = hv * 31 + (_errMessage==null ? 0 : _errMessage.hashCode());
		hv = hv * 31 + (_errCause==null ? 0 : _errCause.hashCode());
		return hv;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		return isEqualFields(obj);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("@[");
		appendParameters(sb);
		sb.append("]");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean isEqualFields(Object obj) {
		if (!(obj instanceof FilterError))
			return false;
		
		FilterError aData = (FilterError)obj;
		
		//--- basic parameters
		if (!Objects.isEqual(aData._errMessage, this._errMessage))
			return false;
		if (!Objects.isEqual(aData._errCause, this._errCause))
			return false;

		// equal all fields
		return true;
	}

	protected void appendParameters(StringBuilder buffer) {
		//--- errMessage
		buffer.append("errMessage=");
		if (_errMessage == null) {
			buffer.append("null");
		} else {
			buffer.append("\"");
			buffer.append(_errMessage);
			buffer.append("\"");
		}
		
		//--- errCause
		buffer.append(", errCause=");
		if (_errCause == null) {
			buffer.append("null");
		} else {
			buffer.append(_errCause);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
