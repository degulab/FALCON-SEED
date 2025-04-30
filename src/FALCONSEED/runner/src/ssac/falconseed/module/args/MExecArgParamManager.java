/*
 * @(#)MExecArgParamManager.java	3.1.0	2014/05/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgParamManager.java	1.13	2011/11/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgParamManager.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.args;

import java.util.HashMap;
import java.util.Map;

/**
 * モジュール実行定義のモジュール引数を識別するためのオブジェクトを管理するマネージャクラス。
 * 
 * @version 3.1.0	2014/05/14
 */
public final class MExecArgParamManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private MExecArgParamManager _instance;
	
	private final Map<String,IMExecArgParam> _mapInstances;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private MExecArgParamManager() {
		_mapInstances = new HashMap<String, IMExecArgParam>();
		registInstance(_mapInstances, MExecArgDirectory.instance);
		registInstance(_mapInstances, MExecArgCsvFile.instance);
		registInstance(_mapInstances, MExecArgTextFile.instance);
		registInstance(_mapInstances, MExecArgXmlFile.instance);
		registInstance(_mapInstances, MExecArgTempFile.instance);
		registInstance(_mapInstances, MExecArgString.instance);
		registInstance(_mapInstances, MExecArgPublish.instance);
		registInstance(_mapInstances, MExecArgSubscribe.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された固有識別子に対応するオブジェクトを返す。
	 * @return	固有識別子に対応するインスタンスを返す。
	 * 			固有識別子が正当でない場合は <tt>null</tt> を返す。
	 */
	static public IMExecArgParam fromIdentifier(String identifier) {
		return getInstance()._mapInstances.get(identifier);
	}

	/**
	 * 指定されたオブジェクトの固有識別子を取得する。
	 * @return	固有識別子
	 */
	static public String getIdentifier(IMExecArgParam param) {
		return param.getClass().getName();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private MExecArgParamManager getInstance() {
		if (_instance == null) {
			_instance = new MExecArgParamManager();
		}
		return _instance;
	}
	
	static private void registInstance(Map<String,IMExecArgParam> map, IMExecArgParam instParam) {
		map.put(getIdentifier(instParam), instParam);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
