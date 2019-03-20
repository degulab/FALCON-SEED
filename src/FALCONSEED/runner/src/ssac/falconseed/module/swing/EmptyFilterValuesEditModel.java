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
 * @(#)EmptyFilterValuesEditModel.java	3.1.0	2014/05/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EmptyFilterValuesEditModel.java	2.0.0	2012/11/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.falconseed.module.ModuleArgConfig;

/**
 * フィルタ実行時引数値を保持する、要素が空のデータモデル。
 * 
 * @version 3.1.0	2014/05/18
 * @since 2.0.0
 */
public class EmptyFilterValuesEditModel extends AbFilterValuesEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public EmptyFilterValuesEditModel() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public boolean isEditing() {
		return false;
	}

	@Override
	public boolean isArgsEditable() {
		return false;
	}

	@Override
	public boolean isArgsHistoryEnabled() {
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	@Override
	protected boolean verifyInputArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		return true;	// no error
	}

	@Override
	protected boolean verifyOutputArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		return true;	// no error
	}

	@Override
	protected boolean verifyStringArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		return true;	// no error
	}

	@Override
	protected boolean verifyPublishArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		return true;	// no error
	}

	@Override
	protected boolean verifySubscribeArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		return true;	// no error
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
