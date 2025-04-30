/*
 * @(#)MongoDelegateDocExalgeElem.java	0.991	2019/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoDelegateDocExalgeElem.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.db.mongo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Decimal128;

import com.mongodb.client.model.Sorts;

import exalge2.ExBase;

/**
 * MongoDB のドキュメントの、交換代数元の要素としてのデリゲートクラス。
 * 
 * @version 0.991
 * @since 0.990
 * 
 * @author H.Debuchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public class MongoDelegateDocExalgeElem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 交換代数元の要素の基底を示す、ドキュメントのキー **/
	static public final String MONGO_KEY_BASE		= "base";
	/** 交換代数元の要素の値を示す、ドキュメントのキー **/
	static public final String MONGO_KEY_VALUE		= "value";

	/** すべての基底キーと値の順で昇順ソートするソートドキュメント(HATキーが基底の最後) **/
	static public final Bson   MONGO_SORT_EXALGE_ELEM_ALL = Sorts.ascending(
			MONGO_KEY_BASE + "." + MongoDelegateDocExBase.MONGO_KEY_NAME,
			MONGO_KEY_BASE + "." + MongoDelegateDocExBase.MONGO_KEY_UNIT,
			MONGO_KEY_BASE + "." + MongoDelegateDocExBase.MONGO_KEY_TIME,
			MONGO_KEY_BASE + "." + MongoDelegateDocExBase.MONGO_KEY_SUBJECT,
			MONGO_KEY_BASE + "." + MongoDelegateDocExBase.MONGO_KEY_HAT,
			MONGO_KEY_VALUE);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
//	/** MongoDB のドキュメントオブジェクト **/
//	protected Document	_mongoDocument;
//	/** MongoDB のドキュメントに含まれる基底のドキュメントオブジェクト **/
//	protected MongoDelegateDocExBase	_baseDocument;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
//	/**
//	 * 指定されたドキュメントを保持する、新しいインスタンスを生成する。
//	 * @param doc	MongoDB ドキュメント
//	 * @throws NullPointerException	引数が <tt>null</tt> の場合
//	 * @throws IllegalArgumentException	ドキュメントの基底オブジェクトが存在しない場合
//	 * @throws ClassCastException	ドキュメントの基底オブジェクトが JSON オブジェクトではない場合
//	 */
//	protected MongoDelegateDocExalgeElem(Document doc) {
//		if (doc == null)
//			throw new NullPointerException("Document object is null.");
//		_mongoDocument = doc;
//		Document basedoc = doc.get(MONGO_KEY_BASE, Document.class);
//		if (basedoc == null) {
//			throw new IllegalArgumentException("Document has no 'base'.");
//		}
//		_baseDocument = new MongoDelegateDocExBase(basedoc);
//	}
//	
//	/**
//	 * 指定された交換代数要素をドキュメントとして保持する、新しいインスタンスを生成する。
//	 * @param base	ソースとする交換代数要素の基底
//	 * @param value	ソースとする交換代数要素の値
//	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
//	 */
//	protected MongoDelegateDocExalgeElem(ExBase base, BigDecimal value) {
//		_mongoDocument = makeExalgeElemDocument(base, value);
//		_baseDocument  = new MongoDelegateDocExBase(_mongoDocument.get(MONGO_KEY_BASE, Document.class));
//	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 交換代数元の要素から、MongoDB ドキュメントを生成する。
	 * @param base	ソースとする交換代数基底
	 * @param value	ソースとする値
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	static public Document makeExalgeElemDocument(ExBase base, BigDecimal value) {
		return appendExalgeElemToDocument(new Document(), base, value);
	}
	
	/**
	 * 交換代数の基底から、値を除いた交換代数元としての MongoDB ドキュメントを生成する。
	 * @param base	ソースとする交換代数基底
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	static public Document makeExalgeElemWithoutValueDocument(ExBase base) {
		return new Document(MONGO_KEY_BASE, MongoDelegateDocExBase.makeExBaseDocument(base));
	}

	/**
	 * 指定された交換代数基底を、交換代数元要素として、指定されたドキュメントに追加する。
	 * @param dest	対象のドキュメント
	 * @param base	ソースとする交換代数基底
	 * @return	指定されたドキュメント
	 * @throws NullPointerException	<em>dest</em> または <em>base</em> が <tt>null</tt> の場合
	 */
	static public Document appendExalgeElemBaseToDocument(Document dest, ExBase base) {
		Document basedoc = MongoDelegateDocExBase.makeExBaseDocument(base);
		dest.append(MONGO_KEY_BASE, basedoc);
		return basedoc;
	}

	/**
	 * 指定された交換代数要素の値を、交換代数元要素として、指定されたドキュメントに追加する。
	 * @param dest	対象のドキュメント
	 * @param value	ソースとする値
	 * @return	指定されたドキュメント
	 * @throws NullPointerException	<em>dest</em> が <tt>null</tt> の場合
	 */
	static public Document appendExalgeElemValueToDocument(Document dest, BigDecimal value) {
		if (value == null) {
			dest.append(MONGO_KEY_VALUE, value);
		} else {
			if (BigDecimal.ZERO.compareTo(value) == 0) {
				value = BigDecimal.ZERO;
			}
			else if (value.scale() > 0) {
				value = value.stripTrailingZeros();
			}
			//--- check conversion BigDecimal to Decimal128
			//--- (参考)https://groups.google.com/forum/#!topic/morphia/aUu1Z6RnYA8
			// Decimal128 は、Preceision <= 34 までのサポートなので、MathContext.128 はオーバーフローする(?)
			Object decValue;
			try {
				new Decimal128(value);
				decValue = value;
			}
			catch (Throwable ex) {
				// Couldn't convert BigDecimal to Decimal128
				//System.err.println("!!!!!!! Failed to convert BigDecimal to Decimal128 : " + ex.toString());
				decValue = value.round(MathContext.DECIMAL128);
				//decValue = BigDecimal.valueOf(value.doubleValue());
			}
			//System.out.println("===BigDecima: " + value.toString());
			dest.append(MONGO_KEY_VALUE, decValue);
		}
		return dest;
	}

	/**
	 * 指定された交換代数元の要素を、指定されたドキュメントの要素として追加する。
	 * @param dest	対象のドキュメント
	 * @param base	ソースとする交換代数基底
	 * @param value	ソースとする値
	 * @return	指定されたドキュメント
	 * @throws NullPointerException	<em>dest</em> または <em>base</em> が <tt>null</tt> の場合
	 */
	static public Document appendExalgeElemToDocument(Document dest, ExBase base, BigDecimal value) {
		appendExalgeElemBaseToDocument(dest, base);
		return appendExalgeElemValueToDocument(dest, value);
	}

	/**
	 * 指定された交換代数元の要素を保持するドキュメントから、基底のみを持つフィルタードキュメントを生成する。
	 * @param docExalgeElem	交換代数元の要素を保持するドキュメント
	 * @return	生成されたフィルタードキュメント
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Document makeFilterByExBase(Document docExalgeElem) {
		return new Document(MONGO_KEY_BASE, docExalgeElem.get(MONGO_KEY_BASE));
	}
	
//	/**
//	 * このオブジェクトが保持するドキュメントオブジェクトを取得する。
//	 * @return	ドキュメントオブジェクト
//	 */
//	protected Document getDocument() {
//		return _mongoDocument;
//	}

	/**
	 * 指定された交換代数基底ドキュメントから、指定されたインデックスに相当する基底キー文字列を取得する。
	 * ハットキーも文字列として取得する。
	 * @param docExalgeElem	対象のドキュメントオブジェクト
	 * @param keyIndex	交換代数基底キーのインデックス
	 * @return	取得した文字列、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>keyIndex</em> が範囲外の場合
	 */
	static public String getExBaseKeyStringByIndex(Document docExalgeElem, int keyIndex) {
		return MongoDelegateDocExBase.getExBaseKeyStringByIndex(getBaseFromDocument(docExalgeElem), keyIndex);
	}

	/**
	 * 指定されたドキュメントから、交換代数基底を表すオブジェクトを取得する。
	 * @param docExalgeElem	対象のドキュメントオブジェクト
	 * @return	交換代数基底を表すドキュメントオブジェクト、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException	ドキュメントの基底オブジェクトが JSON オブジェクトではない場合
	 */
	static public Document getBaseFromDocument(Document docExalgeElem) {
		return docExalgeElem.get(MONGO_KEY_BASE, Document.class);
	}
	
	/**
	 * 指定されたドキュメントから、交換代数基底オブジェクトを取得する。
	 * @param docExalgeElem	対象のドキュメントオブジェクト
	 * @return	交換代数基底オブジェクト、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException	ドキュメントの基底オブジェクトが JSON オブジェクトではない場合
	 */
	static public ExBase toExBaseFromDocument(Document docExalgeElem) {
		Document docBase = getBaseFromDocument(docExalgeElem);
		if (docBase != null) {
			return MongoDelegateDocExBase.toExBase(docBase);
		} else {
			return null;
		}
	}

	/**
	 * 指定されたドキュメントから、交換代数要素の値を表すオブジェクトを取得する。
	 * @param docExalgeElem	対象のドキュメント
	 * @return	交換代数要素の値を表すオブジェクト、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Object getValueObjectFromDocument(Document docExalgeElem) {
		return docExalgeElem.get(MONGO_KEY_VALUE);
	}
	
	/**
	 * 指定されたドキュメントから、交換代数要素の値を取得する。
	 * @param docExalgeElem	対象のドキュメント
	 * @return	交換代数要素の値、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException	交換代数要素の値が <code>BigDecimal</code> 型ではない場合
	 */
	static public BigDecimal getBigDecimalValueFromDocument(Document docExalgeElem) {
		// ClassClastException(org.bson.types.Decimal128 to java.math.BigDecimal) が発生する(なぜ？)
		// データベースには BigDecimal 用コーデックも登録しているのに、なぜ？
		// とりあえず、強制変換
		Object objValue = docExalgeElem.get(MONGO_KEY_VALUE);
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
			return docExalgeElem.get(MONGO_KEY_VALUE, BigDecimal.class);
		}
	}

	/**
	 * 指定されたドキュメントの交換代数要素の基底と値のみが等しいかを判定する。
	 * @param docExalgeElem1	判定するドキュメントの一方
	 * @param docExalgeElem2	判定するドキュメントのもう一方
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean equalsExalgeElemDocument(Document docExalgeElem1, Document docExalgeElem2) {
		if (docExalgeElem1 == docExalgeElem2)
			return true;
		if (docExalgeElem1 == null || docExalgeElem2 == null)
			return false;

		//--- equals base
		Object obj1 = docExalgeElem1.get(MONGO_KEY_BASE);
		Object obj2 = docExalgeElem2.get(MONGO_KEY_BASE);
		if (!Objects.equals(obj1, obj2)) {
			return false;
		}
		
		//--- equals value
		obj1 = docExalgeElem1.get(MONGO_KEY_VALUE);
		obj2 = docExalgeElem2.get(MONGO_KEY_VALUE);
		return Objects.equals(obj1, obj2);
	}

//	/**
//	 * このオブジェクトが保持する基底オブジェクトを取得する。
//	 * @return	基底オブジェクト
//	 */
//	protected MongoDelegateDocExBase getBase() {
//		return _baseDocument;
//	}
	
//	/**
//	 * このオブジェクトが保持する基底オブジェクトのみを含む、フィルターとして利用可能なドキュメントオブジェクトを取得する。
//	 * @return	フィルタードキュメント
//	 */
//	protected Document getFilterByExBase() {
//		return makeFilterByExBase(_mongoDocument);
//	}

//	/**
//	 * このオブジェクトが、交換代数要素の値を保持しているかを判定する。
//	 * @return	値が <tt>null</tt> ではない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
//	 * @throws ClassCastException	交換代数要素の値が <code>BigDecimal</code> 型ではない場合
//	 */
//	protected boolean hasValue() {
//		return (getValue() != null);
//	}

//	/**
//	 * このオブジェクトが保持する交換代数要素の値を取得する。
//	 * @return	交換代数要素の値、存在しない場合もしくは <tt>null</tt> 値の場合は <tt>null</tt>
//	 * @throws ClassCastException	交換代数要素の値が <code>BigDecimal</code> 型ではない場合
//	 */
//	protected BigDecimal getValue() {
//		return _mongoDocument.get(MONGO_KEY_VALUE, BigDecimal.class);
//	}

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
		
		MongoDelegateDocExalgeElem that = (MongoDelegateDocExalgeElem)obj;
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
