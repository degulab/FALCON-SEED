/*
 * @(#)DtJsonList.java	0.1.0	2022/07/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Java オブジェクトを要素とするリスト・クラス。
 * 標準的な JSON 配列に対応するオブジェクトとなる。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtJsonList extends ArrayList<Object>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 2069388703653964996L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public DtJsonList() {
		super();
	}

	public DtJsonList(Collection<? extends Object> c) {
		super(c);
	}

	public DtJsonList(int initialCapacity) {
		super(initialCapacity);
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
