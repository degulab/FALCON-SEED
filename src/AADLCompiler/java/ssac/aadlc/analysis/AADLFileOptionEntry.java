/*
 * @(#)AADLKeyword.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

import org.antlr.runtime.Token;

import ssac.aadlc.codegen.ACodeObject;

/**
 * AADLファイル識別子のオプション要素
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class AADLFileOptionEntry
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final Token		_id;
	private final ACodeObject	_exp;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLFileOptionEntry(Token id, ACodeObject exp) {
		if (id == null)
			throw new NullPointerException("'id' is null.");
		this._id  = id;
		this._exp = exp;
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String name() {
		return _id.getText();
	}
	
	public Token identifier() {
		return _id;
	}
	
	public ACodeObject expression() {
		return _exp;
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
