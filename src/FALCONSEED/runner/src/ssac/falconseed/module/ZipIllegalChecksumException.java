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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ZipIllegalChecksumException.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

/**
 * ZIPファイル解凍時にチェックサム不正を通知する例外
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class ZipIllegalChecksumException extends ZipDecodeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final String	_entryName;
	private final long	_validChecksum;
	private final long	_illegalChecksum;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ZipIllegalChecksumException(String entryName, long validChecksum, long illegalChecksum)
	{
		this(null, entryName, validChecksum, illegalChecksum);
	}
	
	public ZipIllegalChecksumException(String message, String entryName, long validChecksum, long illegalChecksum)
	{
		super(message);
		this._entryName = entryName;
		this._validChecksum = validChecksum;
		this._illegalChecksum = illegalChecksum;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String zipEntryName() {
		return _entryName;
	}
	
	public long validChecksumValue() {
		return _validChecksum;
	}
	
	public long illegalChecksumValue() {
		return _illegalChecksum;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("[entryName=");
		sb.append(_entryName);
		sb.append(" / validChecksum=");
		sb.append(_validChecksum);
		sb.append(" / illegalChecksum=");
		sb.append(_illegalChecksum);
		sb.append("]");
        String message = getLocalizedMessage();
        if (message != null) {
        	sb.append(" : ");
        	sb.append(message);
        }
        return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
