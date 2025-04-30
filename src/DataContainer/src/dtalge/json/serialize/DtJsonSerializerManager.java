/*
 * @(#)DtJsonIOHelper.java	0.1.0	2022/07/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.serialize;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import dtalge.DtAlgeSet;
import dtalge.DtBase;
import dtalge.Dtalge;
import dtalge.container.DtBinder;
import dtalge.container.DtSlip;
import dtalge.container.DtSlipList;
import dtalge.container.DtSlipMap;
import dtalge.json.io.DtJsonFetchedToken;
import dtalge.json.io.DtJsonInputReader;
import dtalge.json.io.DtJsonOutputWriter;
import exalge2.ExAlgeSet;
import exalge2.ExBase;
import exalge2.Exalge;
import net.arnx.jsonic.JSONEventType;

/**
 * {@link DtJsonSerializer} インターフェースを実装するオブジェクトのマネージャー。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtJsonSerializerManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected Class<?> JSONMAP_CLASSES[] = {
		Map.class,	
	};
	
	static protected Class<?> JSONARRAY_CLASSES[] = {
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** <code>DtJsonSerializerManager</code> オブジェクトの、アプリケーション唯一のインスタンス **/
	static private DtJsonSerializerManager	_instance;
	/** ターゲットクラスとシリアライザークラスのマップ **/
	private Map<Class<?>, Class<? extends DtJsonSerializer>>	_mapTargetClassToSerializer;
	/** タイプ名とシリアライザークラスのマップ **/
	private Map<String, Class<? extends DtJsonSerializer>>		_mapTypeNameToSerializer;
	/** シリアライザークラスとシリアライザーインスタンスのマップ **/
	private Map<Class<? extends DtJsonSerializer>, DtJsonSerializer>	_mapSerializerToInstance;
	/** デフォルトシリアライザー **/
	private DtJsonDefaultSerializer	_defSerializer;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected DtJsonSerializerManager() {
		_mapTargetClassToSerializer = new HashMap<>();
		_mapTypeNameToSerializer = new HashMap<>();
		_mapSerializerToInstance = new HashMap<>();
		
		putSerializerInfo(ExBase.class, DtJsonExBaseSerializer.class);
		putSerializerInfo(Exalge.class, DtJsonExalgeSerializer.class);
		putSerializerInfo(ExAlgeSet.class, DtJsonExAlgeSetSerializer.class);
		putSerializerInfo(DtBase.class, DtJsonDtBaseSerializer.class);
		putSerializerInfo(Dtalge.class, DtJsonDtalgeSerializer.class);
		putSerializerInfo(DtAlgeSet.class, DtJsonDtAlgeSetSerializer.class);
		putSerializerInfo(DtSlip.class, DtJsonDtSlipSerializer.class);
		putSerializerInfo(DtSlipList.class, DtJsonDtSlipListSerializer.class);
		putSerializerInfo(DtSlipMap.class, DtJsonDtSlipMapSerializer.class);
		putSerializerInfo(DtBinder.class, DtJsonDtBinderSerializer.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * アプリケーションで唯一の {@code DtJsonSerializerManager} インスタンスを取得する。
	 * @return {@code DtJsonSerializerManager} インスタンス
	 */
	static public synchronized DtJsonSerializerManager instance() {
		if (_instance == null) {
			_instance = new DtJsonSerializerManager();
		}
		return _instance;
	}
	
	/**
	 * 指定されたオブジェクト型名に対応する {@link DtJsonSerializer} インターフェース実装オブジェクトを取得する。
	 * @param typename	オブジェクト型名
	 * @return	オブジェクト型名に対応する {@link DtJsonSerializer} インターフェース実装オブジェクト、対応するものが存在しない場合は {@code null}
	 */
	public DtJsonSerializer getSerializerByTypeName(String typename) {
		if (typename != null && !typename.isEmpty()) {
			Class<? extends DtJsonSerializer> serializer = _mapTypeNameToSerializer.get(typename.toLowerCase());
			return (serializer==null ? null : _mapSerializerToInstance.get(serializer));
		}
		else {
			return null;
		}
	}
	
	/**
	 * 指定されたオブジェクト型に対応する {@link DtJsonSerializer} インターフェース実装オブジェクトを取得する。
	 * @param cls	オブジェクト型を示す {@link java.lang.Class} オブジェクト
	 * @return	オブジェクト型に対応する {@link DtJsonSerializer} インターフェース実装オブジェクト、対応するものが存在しない場合は {@code null}
	 */
	public DtJsonSerializer getSerializerByClass(Class<?> cls) {
		Class<? extends DtJsonSerializer> serializer = _mapTargetClassToSerializer.get(cls);
		return (serializer==null ? null : _mapSerializerToInstance.get(serializer));
	}
	
	/**
	 * 指定されたオブジェクトからオブジェクト型を取得し、そのオブジェクト型に対応する {@link DtJsonSerializer} インターフェース実装オブジェクトを取得する。
	 * @param obj	任意のオブジェクト
	 * @return	オブジェクト型に対応する {@link DtJsonSerializer} インターフェース実装オブジェクト、対応するものが存在しない場合は {@code null}
	 */
	public DtJsonSerializer getSerializerByObject(Object obj) {
		if (obj != null) {
			return getSerializerByClass(obj.getClass());
		}
		else {
			return null;
		}
	}
	
	/**
	 * {@link DtJsonDefaultSerializer} のインスタンスを返す。
	 * @return	{@link DtJsonDefaultSerializer} のインスタンス
	 */
	public DtJsonDefaultSerializer getDefaultSerializer() {
		if (_defSerializer == null) {
			_defSerializer = new DtJsonDefaultSerializer();
		}
		return _defSerializer;
	}
	
	/**
	 * 指定された JSON オブジェクトのキー(名前)が、タイプ名を表す接頭文字で始まるかを判定する。
	 * @param name	判定するキー
	 * @return	タイプ名を表す接頭文字で始まる文字列の場合は <code>true</code>
	 */
	public boolean startWithTypeNamePrefix(String name) {
		if (name != null && name.length() > 1) {
			if (name.startsWith(DtJsonSerializer.OBJTYPENAME_PREFIX_STR)) {
				return true;
			}
		}
		// not start with
		return false;
	}
	
	/**
	 * 指定されたオブジェクトを、JSON フォーマットで <em>writer</em> に出力する。
	 * @param writer	出力先のライター
	 * @param target	出力対象のオブジェクト
	 * @param omitTypeName	データ型内で必要な場合を除き、データ型名の出力を省略する場合は <code>true</code>、データ型名を出力する場合は <code>false</code>
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void serializeByDefault(DtJsonOutputWriter writer, Object target, boolean omitTypeName) throws IOException
	{
		DtJsonSerializer serializer = getSerializerByObject(target);
		if (serializer != null) {
			serializer.serialize(writer, target, omitTypeName);
		}
		else {
			serializer = getDefaultSerializer();
			serializer.serialize(writer, target, omitTypeName);
		}
	}
	
	/**
	 * 指定された <em>reader</em> から、JSON フォーマットで読み込む。
	 * @param reader	入力ソースとなるリーダー
	 * @return	読み込まれたオブジェクト
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public Object deserializeByDefault(DtJsonInputReader reader) throws IOException
	{
		DtJsonSerializer serializer = getDefaultSerializer();
		return serializer.deserialize(reader, null);
	}
	
	/**
	 * 指定された <em>reader</em> から、<em>cls</em> に指定されたオブジェクトとして、JSON フォーマットで読み込む。
	 * @param <T>		<em>cls</em> に指定されたクラス
	 * @param reader	入力ソースとなるリーダー
	 * @param cls		読み込むオブジェクトのクラス
	 * @return	読み込まれたオブジェクト
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public <T> T deserializeAsDataType(DtJsonInputReader reader, Class<T> cls) throws IOException
	{
		DtJsonSerializer serializer = getSerializerByClass(cls);
		if (serializer == null) {
			throw new IllegalArgumentException("Unsupported data type: " + (cls==null ? "null" : cls.getName()));
		}
		
		// データ型の記述があれば、判別する
		DtJsonFetchedToken fetched = reader.fetchNextToken();
		if (fetched != null && fetched.type() == JSONEventType.START_OBJECT) {
			//--- 最初のキーを取得
			fetched = reader.fetchNextToken();
			if (fetched != null && fetched.type() == JSONEventType.NAME && startWithTypeNamePrefix(fetched.getString())) {
				// データ型名なので、先読みを消費
				reader.consumeAllFetchedTokens();
				
				// 型名の同一性チェック
				if (!fetched.getString().equalsIgnoreCase(serializer.getTypeName())) {
					// エラー
					throw reader.createIllegalSpecifiedValue(fetched.getString(), serializer.getTypeName(), fetched.getString());
				}
				
				// deserialize
				Object obj = serializer.deserialize(reader, null);
				
				// オブジェクトの終端まで読み飛ばす
				serializer.ensureEndTypeNamedObject(reader);
				
				// 完了
				return cls.cast(obj);
			}
		}

		// データ型名の指定なし
		Object obj = serializer.deserialize(reader, null);
		return cls.cast(obj);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private final void putSerializerInfo(Class<?> cls, Class<? extends DtJsonSerializer> serializercls)
	{
		// シリアライザーインスタンスの生成
		DtJsonSerializer inst;
		try {
			//inst = serializercls.newInstance(); --- deprecated
			inst = serializercls.getDeclaredConstructor().newInstance();
		}
		catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException ex) {
			throw new IllegalArgumentException("JSON Serializer (" + serializercls.getName() + ") can be created new instance.", ex);
		}
		
		// ターゲットクラスとシリアライザークラスのマップ
		_mapTargetClassToSerializer.put(cls, serializercls);
		
		// タイプ名とシリアライザークラスのマップ
		String typename = DtJsonSerializer.OBJTYPENAME_PREFIX_STR + cls.getSimpleName();
		_mapTypeNameToSerializer.put(typename.toLowerCase(), serializercls);
		
		// シリアライザークラスとシリアライザーインスタンスのマップ
		_mapSerializerToInstance.put(serializercls, inst);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
