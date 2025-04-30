/*
 * @(#)SchemaInputCsvDataField	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.table;

import ssac.aadl.fs.module.schema.type.SchemaValueType;

/**
 * CSV 形式での入力列データのスキーマを保持するクラス。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaInputCsvDataField extends SchemaCsvDataField
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

	public SchemaInputCsvDataField() {
		super();
	}
	
	public SchemaInputCsvDataField(SchemaValueType valueType) {
		super(valueType);
	}

	public SchemaInputCsvDataField(String name, SchemaValueType valueType, Object value) {
		super(name, valueType, value);
	}
	
	public SchemaInputCsvDataField(final SchemaInputCsvDataField src) {
		super(src);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
