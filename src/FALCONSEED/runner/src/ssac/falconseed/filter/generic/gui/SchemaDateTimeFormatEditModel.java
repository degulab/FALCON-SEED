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
 * @(#)SchemaDateTimeFormatEditModel.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaDateTimeFormatEditModel.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui;

import ssac.aadl.fs.module.schema.SchemaDTFormatConfig;
import ssac.aadl.fs.module.schema.type.SchemaDateTimeFormats;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeDateTime;
import ssac.aadl.runtime.util.Objects;
import ssac.falconseed.filter.generic.gui.arg.GenericFilterArgEditModel;

/**
 * 日付時刻書式設定の編集用データモデル。
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaDateTimeFormatEditModel extends SchemaDTFormatConfig
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 日付時刻書式入力用フィルタ定義引数 **/
	private GenericFilterArgEditModel	_targetArgModel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaDateTimeFormatEditModel() {
		super();
	}
	
	public SchemaDateTimeFormatEditModel(final SchemaDTFormatConfig src) {
		super(src);
		if (src instanceof SchemaDateTimeFormatEditModel) {
			SchemaDateTimeFormatEditModel aModel = (SchemaDateTimeFormatEditModel)src;
			_targetArgModel = aModel._targetArgModel;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasFilterArgumentModel() {
		return (_targetArgModel != null);
	}
	
	public GenericFilterArgEditModel getFilterArgumentModel() {
		return _targetArgModel;
	}
	
	public boolean setFilterArgumentModel(GenericFilterArgEditModel newArgModel) {
		if (Objects.equals(newArgModel, _targetArgModel))
			return false;
		//--- modified
		_targetArgModel = newArgModel;
		return true;
	}

	//------------------------------------------------------------
	// Implements Object interfaces
	//------------------------------------------------------------

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == this);
	}

	@Override
	public String toString() {
		return toParamString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void appendParamString(StringBuilder buffer) {
		super.appendParamString(buffer);
		
		buffer.append(", filterArgModel=");
		buffer.append(_targetArgModel==null ? "null" : _targetArgModel.toParamString());
	}

	@Override
	protected SchemaDateTimeFormats getAvailableDateTimeFormats() {
		if (_targetArgModel != null) {
			// default
			return SchemaValueTypeDateTime.instance.getSubParameter();
		}
		else {
			String strPattern = getPatternString();
			if (strPattern != null) {
				// custom
				return new SchemaDateTimeFormats(strPattern);
			} else {
				// default
				return SchemaValueTypeDateTime.instance.getSubParameter();
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
