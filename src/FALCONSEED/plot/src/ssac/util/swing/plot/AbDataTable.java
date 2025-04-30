/*
 * @(#)IDataTable.java	2.1.0	2013/07/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

/**
 * <code>IDataTable</code> の共通実装。
 * 
 * @version 2.1.0	2013/07/18
 * @since 2.1.0
 */
public abstract class AbDataTable implements IDataTable
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

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 指定されたフィールドの名称を返す。
	 * この実装では、ヘッダーの内容をアンダースコアで接続した一つの文字列として返す。
	 * ヘッダーがない場合やヘッダーの内容がすべて空文字の場合は、指定されたインデックスに
	 * 1 を加算したフィールド番号を返す。
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	フィールド名
	 * @throws IndexOutOfBoundsException	フィールドインデックスが範囲外の場合
	 */
	public String getFieldName(int fieldIndex) {
		validFieldIndex(fieldIndex, getFieldCount());
		long numRecords = getHeaderRecordCount();
		
		StringBuilder sb = new StringBuilder();
		if (numRecords > 0L) {
			for (long r = 0L; r < numRecords; ++r) {
				Object value = getHeaderValue(r, fieldIndex);
				String str = (value==null ? "" : value.toString());
				if (str.length() > 0) {
					if (sb.length() > 0) {
						sb.append('_');
					}
					sb.append(str);
				}
			}
		}
		
		if (sb.length() <= 0) {
			sb.append(fieldIndex + 1);
		}
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void checkNegativeHeaderRecordIndex(long headerRecordIndex) {
		if (headerRecordIndex < 0L) {
			throw new IndexOutOfBoundsException("Header record index is negative : " + headerRecordIndex);
		}
	}
	
	protected void checkNegativeDataRecordIndex(long dataRecordIndex) {
		if (dataRecordIndex < 0L) {
			throw new IndexOutOfBoundsException("Data record index is negative : " + dataRecordIndex);
		}
	}
	
	protected void checkNegativeFieldIndex(int fieldIndex) {
		if (fieldIndex < 0) {
			throw new IndexOutOfBoundsException("Field index is negative : " + fieldIndex);
		}
	}
	
	protected void validHeaderRecordIndex(long headerRecordIndex, long limitSize) {
		if (headerRecordIndex < 0L || headerRecordIndex >= limitSize) {
			throw new IndexOutOfBoundsException("Header record index out of range (" + limitSize + ") : " + headerRecordIndex);
		}
	}
	
	protected void validDataRecordIndex(long dataRecordIndex, long limitSize) {
		if (dataRecordIndex < 0L || dataRecordIndex >= limitSize) {
			throw new IndexOutOfBoundsException("Data record index out of range (" + limitSize + ") : " + dataRecordIndex);
		}
	}
	
	protected void validFieldIndex(int fieldIndex, int limitSize) {
		if (fieldIndex < 0 || fieldIndex >= limitSize) {
			throw new IndexOutOfBoundsException("Field index out of range (" + limitSize + ") : " + fieldIndex);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
