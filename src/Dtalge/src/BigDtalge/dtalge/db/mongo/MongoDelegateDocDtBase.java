/*
 * @(#)MongoDelegateDocDtBase.java	0.5.0	2019/02/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db.mongo;

import java.util.Objects;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Sorts;

import dtalge.DtBase;

/**
 * MongoDB のドキュメントの、データ代数基底としてのデリゲートクラス。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Debuchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public class MongoDelegateDocDtBase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** データ代数基底の名前キーを示す、ドキュメントのキー **/
	static public final String MONGO_KEY_NAME		= "name";
	/** データ代数基底のデータ型キーを示す、ドキュメントのキー **/
	static public final String MONGO_KEY_TYPE		= "type";
	/** データ代数基底の属性キーを示す、ドキュメントのキー **/
	static public final String MONGO_KEY_ATTR		= "attr";
	/** データ代数基底の主体キーを示す、ドキュメントのキー **/
	static public final String MONGO_KEY_SUBJECT	= "subject";
	/** 基底の名前キーのインデックス **/
	static public final int		MONGO_KEYIDX_NAME		= 0;
	/** 基底のデータ型キーのインデックス **/
	static public final int		MONGO_KEYIDX_TYPE		= 1;
	/** 基底の属性キーのインデックス **/
	static public final int		MONGO_KEYIDX_ATTR		= 2;
	/** 基底の主体キーのインデックス **/
	static public final int		MONGO_KEYIDX_SUBJECT	= 3;

	/** 基底キーのすべてのキーで昇順ソートするソートドキュメント **/
	static public final Bson   MONGO_SORT_DTBASE_ALL = Sorts.ascending(MONGO_KEY_NAME, MONGO_KEY_TYPE, MONGO_KEY_ATTR, MONGO_KEY_SUBJECT);

	/** 基底キーのインデックスに対応したドキュメントキーの配列 **/
	static public final String[] MOGNO_KEYS_ARRAY = {
			MONGO_KEY_NAME,
			MONGO_KEY_TYPE,
			MONGO_KEY_ATTR,
			MONGO_KEY_SUBJECT,
	};
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** MongoDB のドキュメントオブジェクト **/
	private Document	_mongoDocument;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたドキュメントを保持する、新しいインスタンスを生成する。
	 * @param doc	MongoDB ドキュメント
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected MongoDelegateDocDtBase(Document doc) {
		if (doc == null)
			throw new NullPointerException("Document object is null.");
		_mongoDocument = doc;
	}
	
	/**
	 * 指定されたデータ代数基底をドキュメントとして保持する、新しいインスタンスを生成する。
	 * @param base	ソースとするデータ代数基底
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected MongoDelegateDocDtBase(DtBase base) {
		_mongoDocument = makeDtBaseDocument(base);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 基底キー文字列を、ドキュメントに格納する形式に正規化する。
	 * @param baseKey	基底キー
	 * @return	正規化後の文字列
	 */
	static public String normalizeOmmitKey(String baseKey) {
		if (baseKey == null || baseKey.isEmpty()) {
			return "";
		}
		else if (baseKey.equals(DtBase.OMITTED)) {
			return "";
		}
		else {
			return baseKey;
		}
	}
	
	/**
	 * データ代数基底から、MongoDB ドキュメントを生成する。
	 * @param base	ソースとするデータ代数基底
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Document makeDtBaseDocument(DtBase base) {
		return appendDtBaseToDocument(new Document(), base);
	}

	/**
	 * 指定されたデータ代数基底を、指定されたドキュメントの要素として追加する。
	 * @param dest	対象のドキュメント
	 * @param base	ソースとするデータ代数基底
	 * @return	指定されたドキュメント
	 */
	static public Document appendDtBaseToDocument(Document dest, DtBase base) {
		dest.append(MONGO_KEY_NAME, normalizeOmmitKey(base.getNameKey()));
		dest.append(MONGO_KEY_TYPE, normalizeOmmitKey(base.getTypeKey()));
		dest.append(MONGO_KEY_ATTR, normalizeOmmitKey(base.getAttributeKey()));
		dest.append(MONGO_KEY_SUBJECT, normalizeOmmitKey(base.getSubjectKey()));
		return dest;
	}

	/**
	 * 指定されたデータ代数基底ドキュメントから、指定されたインデックスに相当する基底キー文字列を取得する。
	 * @param docDtBase	データ代数基底を表すドキュメントオブジェクト
	 * @param keyIndex	データ代数基底キーのインデックス
	 * @return	取得した文字列、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>keyIndex</em> が範囲外の場合
	 */
	static public String getDtBaseKeyStringByIndex(Document docDtBase, int keyIndex) {
		return docDtBase.getString(MOGNO_KEYS_ARRAY[keyIndex]);
	}

	/**
	 * 指定されたドキュメントのデータ代数基底のすべてのキーが等しいかを判定する。
	 * @param docDtBase1	判定するドキュメントの一方
	 * @param docDtBase2	判定するドキュメントのもう一方
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean equalsDtBaseElemDocument(Document docDtBase1, Document docDtBase2) {
		if (docDtBase1 == docDtBase2)
			return true;
		if (docDtBase1 == null || docDtBase2 == null)
			return false;
		
		//--- equals name key
		Object obj1 = docDtBase1.get(MONGO_KEY_NAME);
		Object obj2 = docDtBase2.get(MONGO_KEY_NAME);
		if (!Objects.equals(obj1, obj2))
			return false;
		//--- equals type key
		obj1 = docDtBase1.get(MONGO_KEY_TYPE);
		obj2 = docDtBase2.get(MONGO_KEY_TYPE);
		if (!Objects.equals(obj1, obj2))
			return false;
		//--- equals attribute key
		obj1 = docDtBase1.get(MONGO_KEY_ATTR);
		obj2 = docDtBase2.get(MONGO_KEY_ATTR);
		if (!Objects.equals(obj1, obj2))
			return false;
		//--- equals subject key
		obj1 = docDtBase1.get(MONGO_KEY_SUBJECT);
		obj2 = docDtBase2.get(MONGO_KEY_SUBJECT);
		return Objects.equals(obj1, obj2);
	}
	
	/**
	 * このオブジェクトが保持するドキュメントオブジェクトを取得する。
	 * @return	ドキュメントオブジェクト
	 */
	protected Document getDocument() {
		return _mongoDocument;
	}
	
	/**
	 * このオブジェクトに名前キーが記述されている場合に <tt>true</tt> を返す。
	 * <p>このメソッドは、キーの有無のみを判定するもので、値の有無は関知しない。
	 */
	protected boolean isSpecifiedNameKey() {
		return _mongoDocument.containsKey(MONGO_KEY_NAME);
	}
	
	/**
	 * このオブジェクトにデータ型キーが記述されている場合に <tt>true</tt> を返す。
	 * <p>このメソッドは、キーの有無のみを判定するもので、値の有無は関知しない。
	 */
	protected boolean isSpecifiedTypeKey() {
		return _mongoDocument.containsKey(MONGO_KEY_TYPE);
	}
	
	/**
	 * このオブジェクトに属性キーが記述されている場合に <tt>true</tt> を返す。
	 * <p>このメソッドは、キーの有無のみを判定するもので、値の有無は関知しない。
	 */
	protected boolean isSpecifiedAttributeKey() {
		return _mongoDocument.containsKey(MONGO_KEY_ATTR);
	}
	
	/**
	 * このオブジェクトに主体キーが記述されている場合に <tt>true</tt> を返す。
	 * <p>このメソッドは、キーの有無のみを判定するもので、値の有無は関知しない。
	 */
	protected boolean isSpecifiedSubjectKey() {
		return _mongoDocument.containsKey(MONGO_KEY_SUBJECT);
	}

	/**
	 * 基底の名前キーを返す。
	 * 
	 * @return 名前キー
	 */
	protected String getNameKey() {
		return normalizeOmmitKey(_mongoDocument.getString(MONGO_KEY_NAME));
	}

	/**
	 * 基底のデータ型キーを返す。
	 * 
	 * @return データ型キー
	 */
	protected String getTypeKey() {
		return normalizeOmmitKey(_mongoDocument.getString(MONGO_KEY_TYPE));
	}

	/**
	 * 基底の属性キーを返す。
	 * 
	 * @return 属性キー
	 */
	protected String getAttributeKey() {
		return normalizeOmmitKey(_mongoDocument.getString(MONGO_KEY_ATTR));
	}

	/**
	 * 基底のサブジェクトキーを返す。
	 * 
	 * @return サブジェクトキー
	 */
	protected String getSubjectKey() {
		return normalizeOmmitKey(_mongoDocument.getString(MONGO_KEY_SUBJECT));
	}

	/**
	 * 指定されたドキュメントから、データ代数基底を生成する。
	 * @param docDtBase	データ代数基底の要素を持つドキュメント
	 * @return	生成されたデータ代数基底
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws	IllegalArgumentException	名前キーが <tt>null</tt> もしくは空文字列の場合
	 * @throws ClassCastException	ドキュメントの要素キーに対応するデータ型が正しくない場合
	 */
	static public DtBase toDtBase(Document docDtBase) {
		//--- name key
		String strName = docDtBase.getString(MONGO_KEY_NAME);
		//--- type key
		String strType = docDtBase.getString(MONGO_KEY_TYPE);
		//--- attr key
		String strAttr = docDtBase.getString(MONGO_KEY_ATTR);
		//--- subject key
		String strSubject = docDtBase.getString(MONGO_KEY_SUBJECT);
		
		return new DtBase(strName, strType, strAttr, strSubject);
	}

	/**
	 * このオブジェクトが保持するパラメータから、新しいデータ代数基底を生成する。
	 * @return	新しいデータ代数基底のインスタンス
	 * @throws ClassCastException	データ型が正しくない場合
	 * @throws IllegalStateException	パラメータがデータ代数基底として正しくない場合
	 */
	protected DtBase toDtBase() {
		//--- name key
		String strName = _mongoDocument.getString(MONGO_KEY_NAME);
		//--- type key
		String strType = _mongoDocument.getString(MONGO_KEY_TYPE);
		//--- attr key
		String strAttr = _mongoDocument.getString(MONGO_KEY_ATTR);
		//--- subject key
		String strSubject = _mongoDocument.getString(MONGO_KEY_SUBJECT);
		
		return new DtBase(strName, strType, strAttr, strSubject);
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
		
		MongoDelegateDocDtBase that = (MongoDelegateDocDtBase)obj;
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
