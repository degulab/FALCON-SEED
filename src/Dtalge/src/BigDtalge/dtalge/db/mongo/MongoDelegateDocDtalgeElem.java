/*
 * @(#)MongoDelegateDocDtalgeElem.java	0.5.0	2019/02/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db.mongo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Decimal128;

import com.mongodb.client.model.Sorts;

import dtalge.DtBase;

/**
 * MongoDB のドキュメントの、データ代数元の要素としてのデリゲートクラス。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Debuchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public class MongoDelegateDocDtalgeElem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** データ代数元の要素の基底を示す、ドキュメントのキー **/
	static public final String MONGO_KEY_BASE		= "base";
	static public final String MONGO_KEY_$BASE		= "$base";
	/** データ代数元の要素の値を示す、ドキュメントのキー **/
	static public final String MONGO_KEY_VALUE		= "value";
	static public final String MONGO_KEY_$VALUE		= "$value";

	/** すべての基底キーと値の順で昇順ソートするソートドキュメント **/
	static public final Bson   MONGO_SORT_DTALGE_ELEM_ALL = Sorts.ascending(
			MONGO_KEY_BASE + "." + MongoDelegateDocDtBase.MONGO_KEY_NAME,
			MONGO_KEY_BASE + "." + MongoDelegateDocDtBase.MONGO_KEY_TYPE,
			MONGO_KEY_BASE + "." + MongoDelegateDocDtBase.MONGO_KEY_ATTR,
			MONGO_KEY_BASE + "." + MongoDelegateDocDtBase.MONGO_KEY_SUBJECT,
			MONGO_KEY_VALUE);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** MongoDB のドキュメントオブジェクト **/
	protected Document	_mongoDocument;
	/** MongoDB のドキュメントに含まれる基底のドキュメントオブジェクト **/
	protected MongoDelegateDocDtBase	_baseDocument;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたドキュメントを保持する、新しいインスタンスを生成する。
	 * @param doc	MongoDB ドキュメント
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	ドキュメントの基底オブジェクトが存在しない場合
	 * @throws ClassCastException	ドキュメントの基底オブジェクトが JSON オブジェクトではない場合
	 */
	protected MongoDelegateDocDtalgeElem(Document doc) {
		if (doc == null)
			throw new NullPointerException("Document object is null.");
		_mongoDocument = doc;
		Document basedoc = doc.get(MONGO_KEY_BASE, Document.class);
		if (basedoc == null) {
			throw new IllegalArgumentException("Document has no 'base'.");
		}
		_baseDocument = new MongoDelegateDocDtBase(basedoc);
	}
	
	/**
	 * 指定されたデータ代数要素をドキュメントとして保持する、新しいインスタンスを生成する。
	 * @param base	ソースとするデータ代数要素の基底
	 * @param value	ソースとするデータ代数要素の値
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	protected MongoDelegateDocDtalgeElem(DtBase base, Object value) {
		_mongoDocument = makeDtalgeElemDocument(base, value);
		_baseDocument  = new MongoDelegateDocDtBase(_mongoDocument.get(MONGO_KEY_BASE, Document.class));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * データ代数元の要素から、MongoDB ドキュメントを生成する。
	 * @param base	ソースとするデータ代数基底
	 * @param value	ソースとする値
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	static public Document makeDtalgeElemDocument(DtBase base, Object value) {
		return appendDtalgeElemToDocument(new Document(), base, value);
	}
	
	/**
	 * データ代数の基底から、値を除いたデータ代数元としての MongoDB ドキュメントを生成する。
	 * @param base	ソースとするデータ代数基底
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	static public Document makeDtalgeElemWithoutValueDocument(DtBase base) {
		return new Document(MONGO_KEY_BASE, MongoDelegateDocDtBase.makeDtBaseDocument(base));
	}

	/**
	 * 指定されたデータ代数基底を、データ代数元要素として、指定されたドキュメントに追加する。
	 * @param dest	対象のドキュメント
	 * @param base	ソースとするデータ代数基底
	 * @return	指定されたドキュメント
	 * @throws NullPointerException	<em>dest</em> または <em>base</em> が <tt>null</tt> の場合
	 */
	static public Document appendDtalgeElemBaseToDocument(Document dest, DtBase base) {
		Document basedoc = MongoDelegateDocDtBase.makeDtBaseDocument(base);
		dest.append(MONGO_KEY_BASE, basedoc);
		return basedoc;
	}

	/**
	 * 指定されたデータ代数要素の値を、データ代数元要素として、指定されたドキュメントに追加する。
	 * @param dest	対象のドキュメント
	 * @param value	ソースとする値
	 * @return	指定されたドキュメント
	 * @throws NullPointerException	<em>dest</em> が <tt>null</tt> の場合
	 */
	static public Document appendDtalgeElemValueToDocument(Document dest, Object value) {
		if (value == null) {
			dest.append(MONGO_KEY_VALUE, value);
		}
		else if (value instanceof BigDecimal) {
			BigDecimal adata = (BigDecimal)value;
			if (BigDecimal.ZERO.compareTo(adata) == 0) {
				adata = BigDecimal.ZERO;
			}
			else if (adata.scale() > 0) {
				adata = adata.stripTrailingZeros();
			}
			//--- check conversion BigDecimal to Decimal128
			//--- (参考)https://groups.google.com/forum/#!topic/morphia/aUu1Z6RnYA8
			// Decimal128 は、Preceision <= 34 までのサポートなので、MathContext.128 はオーバーフローする(?)
			Object decValue;
			try {
				new Decimal128(adata);
				decValue = adata;
			}
			catch (Throwable ex) {
				// Couldn't convert BigDecimal to Decimal128
				//System.err.println("!!!!!!! Failed to convert BigDecimal to Decimal128 : " + ex.toString());
				decValue = adata.round(MathContext.DECIMAL128);
				//decValue = BigDecimal.valueOf(value.doubleValue());
			}
			//System.out.println("===BigDecima: " + value.toString());
			dest.append(MONGO_KEY_VALUE, decValue);
		}
		else {
			// TODO: データ型を考慮
			dest.append(MONGO_KEY_VALUE, value);
		}
		return dest;
	}

	/**
	 * 指定されたデータ代数元の要素を、指定されたドキュメントの要素として追加する。
	 * @param dest	対象のドキュメント
	 * @param base	ソースとするデータ代数基底
	 * @param value	ソースとする値
	 * @return	指定されたドキュメント
	 * @throws NullPointerException	<em>dest</em> または <em>base</em> が <tt>null</tt> の場合
	 */
	static public Document appendDtalgeElemToDocument(Document dest, DtBase base, Object value) {
		appendDtalgeElemBaseToDocument(dest, base);
		return appendDtalgeElemValueToDocument(dest, value);
	}

	/**
	 * 指定されたデータ代数元の要素を保持するドキュメントから、基底のみを持つフィルタードキュメントを生成する。
	 * @param docDtalgeElem	データ代数元の要素を保持するドキュメント
	 * @return	生成されたフィルタードキュメント
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Document makeFilterByDtBase(Document docDtalgeElem) {
		return new Document(MONGO_KEY_BASE, docDtalgeElem.get(MONGO_KEY_BASE));
	}
	
	/**
	 * このオブジェクトが保持するドキュメントオブジェクトを取得する。
	 * @return	ドキュメントオブジェクト
	 */
	protected Document getDocument() {
		return _mongoDocument;
	}

	/**
	 * 指定されたデータ代数基底ドキュメントから、指定されたインデックスに相当する基底キー文字列を取得する。
	 * @param docDtalgeElem	対象のドキュメントオブジェクト
	 * @param keyIndex	データ代数基底キーのインデックス
	 * @return	取得した文字列、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>keyIndex</em> が範囲外の場合
	 */
	static public String getDtBaseKeyStringByIndex(Document docDtalgeElem, int keyIndex) {
		return MongoDelegateDocDtBase.getDtBaseKeyStringByIndex(getBaseFromDocument(docDtalgeElem), keyIndex);
	}

	/**
	 * 指定されたドキュメントから、データ代数基底を表すオブジェクトを取得する。
	 * @param docDtalgeElem	対象のドキュメントオブジェクト
	 * @return	データ代数基底を表すドキュメントオブジェクト、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException	ドキュメントの基底オブジェクトが JSON オブジェクトではない場合
	 */
	static public Document getBaseFromDocument(Document docDtalgeElem) {
		return docDtalgeElem.get(MONGO_KEY_BASE, Document.class);
	}
	
	/**
	 * 指定されたドキュメントから、データ代数基底オブジェクトを取得する。
	 * @param docDtalgeElem	対象のドキュメントオブジェクト
	 * @return	データ代数基底オブジェクト、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException	ドキュメントの基底オブジェクトが JSON オブジェクトではない場合
	 */
	static public DtBase toDtBaseFromDocument(Document docDtalgeElem) {
		Document docBase = getBaseFromDocument(docDtalgeElem);
		if (docBase != null) {
			return MongoDelegateDocDtBase.toDtBase(docBase);
		} else {
			return null;
		}
	}

	/**
	 * 指定されたドキュメントから、データ代数要素の値を表すオブジェクトを取得する。
	 * @param docDtalgeElem	対象のドキュメント
	 * @return	データ代数要素の値を表すオブジェクト、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Object getValueObjectFromDocument(Document docDtalgeElem) {
		// ClassClastException(org.bson.types.Decimal128 to java.math.BigDecimal) が発生する(なぜ？)
		// データベースには BigDecimal 用コーデックも登録しているのに、なぜ？
		// とりあえず、強制変換
		Object objValue = docDtalgeElem.get(MONGO_KEY_VALUE);
		//System.out.println("[Debug] MONGO_KEY_VALUE object is " + (objValue==null ? "null" : objValue.getClass().toString()));
		//System.out.println("[Debug] MONGO_KEY_VALUE object toString result: " + String.valueOf(objValue));
		if (objValue == null) {
			// 問題なし
			return null;
		}
		else if (objValue instanceof BigDecimal) {
			// 問題なし
			return (BigDecimal)objValue;
		}
		else if (objValue instanceof Decimal128) {
			// やっかい
			return new BigDecimal(objValue.toString());
		}
		else if (objValue instanceof Number) {
			// 数値化
			return new BigDecimal(objValue.toString());
		}
		else {
			// 他のクラスは通常処理
			return objValue;
		}
	}
	
	/**
	 * 指定されたドキュメントから、データ代数要素の値を取得する。
	 * @param docDtalgeElem	対象のドキュメント
	 * @return	データ代数要素の値、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException	データ代数要素の値が <code>BigDecimal</code> 型ではない場合
	 */
	static public BigDecimal getBigDecimalValueFromDocument(Document docDtalgeElem) {
		// ClassClastException(org.bson.types.Decimal128 to java.math.BigDecimal) が発生する(なぜ？)
		// データベースには BigDecimal 用コーデックも登録しているのに、なぜ？
		// とりあえず、強制変換
		Object objValue = docDtalgeElem.get(MONGO_KEY_VALUE);
		//System.out.println("[Debug] MONGO_KEY_VALUE object is " + (objValue==null ? "null" : objValue.getClass().toString()));
		//System.out.println("[Debug] MONGO_KEY_VALUE object toString result: " + String.valueOf(objValue));
		if (objValue == null) {
			// 問題なし
			return null;
		}
		else if (objValue instanceof BigDecimal) {
			// 問題なし
			return (BigDecimal)objValue;
		}
		else if (objValue instanceof Decimal128) {
			// やっかい
			return new BigDecimal(objValue.toString());
		}
		else if (objValue instanceof Number) {
			// 数値化
			return new BigDecimal(objValue.toString());
		}
		else {
			// 他のクラスは通常処理
			return docDtalgeElem.get(MONGO_KEY_VALUE, BigDecimal.class);
		}
	}

	/**
	 * 指定されたドキュメントのデータ代数要素の基底と値のみが等しいかを判定する。
	 * @param docDtalgeElem1	判定するドキュメントの一方
	 * @param docDtalgeElem2	判定するドキュメントのもう一方
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean equalsDtalgeElemDocument(Document docDtalgeElem1, Document docDtalgeElem2) {
		if (docDtalgeElem1 == docDtalgeElem2)
			return true;
		if (docDtalgeElem1 == null || docDtalgeElem2 == null)
			return false;

		//--- equals base
		Object obj1 = docDtalgeElem1.get(MONGO_KEY_BASE);
		Object obj2 = docDtalgeElem2.get(MONGO_KEY_BASE);
		if ((obj1 instanceof BigDecimal) && (obj2 instanceof BigDecimal)) {
			if (((BigDecimal)obj1).compareTo((BigDecimal)obj2) != 0) {
				// not equals
				return false;
			}
		}
		else if (!Objects.equals(obj1, obj2)) {
			return false;
		}
		
		//--- equals value
		obj1 = docDtalgeElem1.get(MONGO_KEY_VALUE);
		obj2 = docDtalgeElem2.get(MONGO_KEY_VALUE);
		return Objects.equals(obj1, obj2);
	}

	/**
	 * このオブジェクトが保持する基底オブジェクトを取得する。
	 * @return	基底オブジェクト
	 */
	protected MongoDelegateDocDtBase getBase() {
		return _baseDocument;
	}
	
	/**
	 * このオブジェクトが保持する基底オブジェクトのみを含む、フィルターとして利用可能なドキュメントオブジェクトを取得する。
	 * @return	フィルタードキュメント
	 */
	protected Document getFilterByDtBase() {
		return makeFilterByDtBase(_mongoDocument);
	}

	/**
	 * このオブジェクトが、データ代数要素の値を保持しているかを判定する。
	 * @return	値が <tt>null</tt> ではない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean hasValue() {
		return (getValue() != null);
	}

	/**
	 * このオブジェクトが保持するデータ代数要素の値を取得する。
	 * @return	データ代数要素の値、存在しない場合もしくは <tt>null</tt> 値の場合は <tt>null</tt>
	 */
	protected Object getValue() {
		return _mongoDocument.get(MONGO_KEY_VALUE);
	}

	/*
	 * このオブジェクトのハッシュ値を返す。
	 *
	@Override
	public int hashCode() {
		return _mongoDocument.hashCode();
	}
	*/

	/*
	 * 指定されたオブジェクトが、このオブジェクトと等しいかどうかを判定する。
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 *
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj == null || !obj.getClass().equals(this.getClass())) {
			return false;
		}
		
		MongoDelegateDocDtalgeElem that = (MongoDelegateDocDtalgeElem)obj;
		return this._mongoDocument.equals(that._mongoDocument);
	}
	*/

	/*
	@Override
	public String toString() {
		return _mongoDocument.toString();
	}
	*/

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
