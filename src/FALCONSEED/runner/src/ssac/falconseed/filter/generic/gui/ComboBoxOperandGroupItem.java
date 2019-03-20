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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ComboBoxOperandGroupItem.java	3.2.0	2015/06/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui;

import javax.swing.ComboBoxModel;

/**
 * 汎用フィルタのプロパティ編集パネルにおけるオペランド値のグループと値リストとの対応を保持するクラス。
 * このオブジェクトが保持する値リストは、<code>ComboBoxModel</code> となる。
 * なお、このオブジェクトは不変である。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class ComboBoxOperandGroupItem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 値グループを示す列挙値 **/
	private final GenericSchemaValueGroups	_group;
	/** 値グループに対応する <code>ComboBoxModel</code> インスタンス、もしくは <tt>null</tt> **/
	private final ComboBoxModel				_cmbModel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ComboBoxOperandGroupItem(GenericSchemaValueGroups group, ComboBoxModel cmbModel) {
		_group = group;
		_cmbModel = cmbModel;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public GenericSchemaValueGroups getValueGroup() {
		return _group;
	}
	
	public ComboBoxModel getComboBoxModel() {
		return _cmbModel;
	}

	@Override
	public String toString() {
		return _group.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
