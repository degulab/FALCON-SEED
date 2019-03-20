/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
