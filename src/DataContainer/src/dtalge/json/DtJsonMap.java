/*
 * @(#)DtJsonMap.java	0.1.0	2022/07/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Java オブジェクトを要素とするリスト・クラス。
 * 標準的な JSON オブジェクト(マップ)に対応するオブジェクトとなる。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtJsonMap extends LinkedHashMap<String, Object>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -631066669833397272L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public DtJsonMap() {
		super();
	}

	public DtJsonMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	public DtJsonMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public DtJsonMap(int initialCapacity) {
		super(initialCapacity);
	}

	public DtJsonMap(Map<? extends String, ? extends Object> m) {
		super(m);
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
