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
