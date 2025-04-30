/*
 * @(#)SchemaCsvDataTable	3.2.1	2015/07/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaCsvDataTable	3.2.0	2015/06/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.table;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;

/**
 * CSV 形式でのテーブルデータのスキーマを保持するクラス。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaCsvDataTable<E extends SchemaCsvDataField> extends SchemaDataTable<E>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -2587896643201102296L;
	
	static protected final String	XmlAttrHeaderRecords	= "HeaderRecords";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ヘッダー行数 **/
	private int	_headerRecords;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 標準のパラメータで、要素が空の新しいインスタンスを生成する。
	 */
	public SchemaCsvDataTable() {
		super();
	}

	/**
	 * 標準のパラメータで、指定された容量を持つ、要素が空の新しいインスタンスを生成する。
	 * @param initialCapacity	初期容量(0 以上)
	 * @throws IllegalArgumentException	<em>initialCapacity</em> が負の場合
	 */
	public SchemaCsvDataTable(int initialCapacity) {
		super(initialCapacity);
	}
	
	/**
	 * 指定されたオブジェクトの内容をコピーした、新しいインスタンスを生成する。
	 * @param copyListElements	コレクション要素をコピーする場合は <tt>true</tt>、コピーしない場合は <tt>false</tt>
	 * @param src	コピー元とするオブジェクト
	 * @throws NullPointerException	コピー元オブジェクトが <tt>null</tt> の場合
	 */
	public SchemaCsvDataTable(boolean copyListElements, final SchemaCsvDataTable<? extends E> src) {
		super(copyListElements, src);
		_headerRecords = src._headerRecords;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getHeaderRecordCount() {
		return _headerRecords;
	}

	public void setHeaderRecordCount(int count) {
		_headerRecords = (count < 0 ? 0 : count);
	}
	
	public boolean updateHeaderRecordCount(int count) {
		if (count < 0)
			count = 0;
		if (count == _headerRecords)
			return false;
		//--- modified
		_headerRecords = count;
		return true;
	}

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = 31 * h + _headerRecords;
		return h;
	}

	//------------------------------------------------------------
	// Read Interfaces for XML
	//------------------------------------------------------------

	@Override
	protected boolean xmlParseAttributeValue(SchemaConfigReader reader, XMLStreamReader xmlReader, String attrName, String attrValue)
	throws XMLStreamException
	{
		// element no
		if (XmlAttrHeaderRecords.equals(attrName)) {
			if (attrValue == null || attrValue.isEmpty()) {
				// invalid header record count
				setHeaderRecordCount(0);
			}
			else {
				// 整数変換
				try {
					setHeaderRecordCount(Integer.parseInt(attrValue));
				}
				catch (Throwable ex) {
					String msg = String.format("<%s> : \"%s\" attribute value is not Integer value : value=\"%s\"", getXmlElementName(), attrName, attrValue);
					throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
				}
			}
			return true;
		}
		
		return super.xmlParseAttributeValue(reader, xmlReader, attrName, attrValue);
	}

	//------------------------------------------------------------
	// Write Interfaces for XML
	//------------------------------------------------------------

	@Override
	protected void xmlWriteStartAttributes(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException {
		super.xmlWriteStartAttributes(writer, xmlWriter);
		
		xmlWriter.writeAttribute(XmlAttrHeaderRecords, Integer.toString(_headerRecords));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void appendParamString(StringBuilder buffer) {
		super.appendParamString(buffer);
		
		buffer.append(", headerRecords=");
		buffer.append(_headerRecords);
	}

	@Override
	protected boolean equalFields(Object obj) {
		if (!super.equalFields(obj))
			return false;
		
		SchemaCsvDataTable<?> aTable = (SchemaCsvDataTable<?>)obj;
		
		if (aTable._headerRecords != this._headerRecords)
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
