/*
 * @(#)MongoDtNamedStringThesaurusInnerElement.java	0.5.0	2019/02/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db.mongo;

import dtalge.db.BigDtNamedStringThesaurusInnerElement;

/**
 * <code>MongoDtNamedStringThesaurus</code> クラスの最小要素を保持するクラス。
 * <p>このオブジェクトは不変である。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoDtNamedStringThesaurusInnerElement implements BigDtNamedStringThesaurusInnerElement 
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final String	_name;
	private final String	_parent;
	private final String	_child;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MongoDtNamedStringThesaurusInnerElement(String name, String parent, String child) {
		_name = name;
		_parent = parent;
		_child = child;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getParentWord() {
		return _parent;
	}

	@Override
	public String getChildWord() {
		return _child;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
