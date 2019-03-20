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
 * @(#)SchemaDataField	3.2.0	2015/06/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.table;

import ssac.aadl.fs.module.schema.SchemaValueObject;
import ssac.aadl.fs.module.schema.type.SchemaValueType;

/**
 * 列データのスキーマを保持するクラス。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaDataField extends SchemaValueObject
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

	public SchemaDataField() {
		this(null, null, null);
	}
	
	public SchemaDataField(SchemaValueType valueType) {
		this(null, valueType, null);
	}
	
	public SchemaDataField(String name, SchemaValueType valueType, Object value) {
		super(name, valueType, value);
	}
	
	public SchemaDataField(final SchemaDataField src) {
		super(src);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getFieldName() {
		return getName();
	}
	
	public void setFieldName(String newFieldName) {
		setName(newFieldName);
	}
	
	public boolean updateFieldName(String newFieldName) {
		return updateName(newFieldName);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
