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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ConfigRecognitionException.java	3.3.0	2016/05/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.parser;

/**
 * <code>[Excel to CSV]</code> 変換定義解析時の例外。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class ConfigRecognitionException extends ConfigException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -5362776188260450883L;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 変換定義解析エラーの詳細情報 **/
	private final ConfigErrorDetail	_detail;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

//	public ConfigRecognitionException() {
//		super();
//	}
	
	public ConfigRecognitionException(ConfigErrorDetail detail) {
		super(detail.toString());
		_detail = detail;
	}
	
	public ConfigRecognitionException(ConfigErrorDetail detail, Throwable cause) {
		super(detail.toString(), cause);
		_detail = detail;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ConfigErrorDetail getDetail() {
		return _detail;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
