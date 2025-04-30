/*
 * @(#)MongoDtStringThesaurusElement.java	0.5.0	2019/02/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db.mongo;

import dtalge.db.BigDtStringThesaurusElement;

/**
 * <code>MongoDtStringThesaurus</code> クラスの要素の値を保持するクラス。
 * <p>このオブジェクトは不変である。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoDtStringThesaurusElement implements BigDtStringThesaurusElement
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final String	_parent;
	private final String	_child;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public MongoDtStringThesaurusElement(String parent, String child) {
		_parent = parent;
		_child = child;
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

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
