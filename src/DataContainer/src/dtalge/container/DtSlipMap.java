/*
 * @(#)DtSlipMap.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * データスリップ(<code>{@link DtSlip}</code>)のマップクラス。
 * 
 * <p>データスリップ(<code>{@link DtSlip}</code>)のインスタンスのマップであり、{@link java.util.LinkedHashMap} の実装となる。
 * したがって、挿入メソッド、ファイル入出力において、クラス内での要素の順序は基本的に維持される。
 * <p>
 * このクラスでは、<code>null</code> を許容しない。
 * <br>また、<b>この実装は同期化されない</b>。
 * <p>
 * {@code JSON} 形式でのシリアライズ／デシリアライズについては、{@link dtalge.json.DtJSON} クラスの説明を参照。
 * データスリップの {@code JSON} 形式については、{@link dtalge.json.serialize.DtJsonDtSlipMapSerializer} の説明を参照。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 * 
 * @see dtalge.json.DtJSON
 * @see dtalge.json.serialize.DtJsonDtSlipMapSerializer
 */
public class DtSlipMap extends LinkedHashMap<String, DtSlip>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 8951748343396640834L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtSlipMap() {
		super();
	}

	public DtSlipMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	public DtSlipMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public DtSlipMap(int initialCapacity) {
		super(initialCapacity);
	}

	public DtSlipMap(Map<? extends String, ? extends DtSlip> m) {
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
