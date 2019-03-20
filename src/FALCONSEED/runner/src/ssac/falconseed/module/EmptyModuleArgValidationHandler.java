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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)EmptyModuleArgValidationHandler.java	1.22	2012/08/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

/**
 * モジュール実行時引数の正当性検査における空のイベントハンドラ。
 * このクラスの実装では、イベントに対し何も処理を行わない。
 * 
 * @version 1.22	2012/08/22
 * @since 1.22
 */
public class EmptyModuleArgValidationHandler implements IModuleArgValidationHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final EmptyModuleArgValidationHandler	_instance = new EmptyModuleArgValidationHandler();
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public EmptyModuleArgValidationHandler getInstance() {
		return _instance;
	}

	public void clearError() {}

	public void setError(String errmsg) {}
}
