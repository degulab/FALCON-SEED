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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FileAccessException.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io;

import java.io.File;

/**
 * ファイルアクセスに失敗した場合の例外のラッパー。
 * この例外では、失敗した操作の対象である <code>File</code> オブジェクトを保持する。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class FileAccessException extends RuntimeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final File _targetFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FileAccessException(File targetFile) {
		super();
		this._targetFile = targetFile;
	}
	
	public FileAccessException(File targetFile, String message) {
		super(message);
		this._targetFile = targetFile;
	}
	
	public FileAccessException(File targetFile, Throwable cause) {
		super(cause);
		this._targetFile = targetFile;
	}
	
	public FileAccessException(File targetFile, String message, Throwable cause) {
		super(message, cause);
		this._targetFile = targetFile;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasTarget() {
		return (_targetFile != null);
	}
	
	public File getTarget() {
		return _targetFile;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String message = getLocalizedMessage();
		sb.append(getClass().getName());
		if (message != null) {
			sb.append(": " + message);
		}
		if (_targetFile != null) {
			sb.append(": \"" + _targetFile.toString() + "\"");
		}
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
