/*
 * @(#)AADLKeyword.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

import org.antlr.runtime.Token;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;

/**
 * AADLファイル識別子に関する情報
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public final class AADLFileType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** テキストファイル識別子(&quot;txtFile&quot;) を表す ID **/
	static public final int TEXT = 0;
	/** CSV ファイル識別子(&quot;csvFile&quot;) を表す ID **/
	static public final int CSV  = 1;
	/** XML ファイル識別子(&quot;xmlFile&quot;) を表す ID **/
	static public final int XML  = 2;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private AADLFileType() {}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * トークンの文字列から、ファイル識別子に対応する ID を返す。
	 * トークンの文字列が &quot;txtFile&quot;、&quot;csvFile&quot;、&quot;xmlFile&quot; 以外の
	 * 場合は、例外をスローする。
	 * @param astFile	トークン
	 * @return	ファイル識別子に対応する ID を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws CompileException		トークンの文字列がファイル識別子ではない場合
	 */
	static public int getFileTypeByToken(Token astFile) {
		String strFileDesc = astFile.getText();
		if (strFileDesc.equals("txtFile")) {
			return TEXT;
		}
		else if (strFileDesc.equals("csvFile")) {
			return CSV;
		}
		else if (strFileDesc.equals("xmlFile")) {
			return XML;
		}
		else {
			// Unknown token
			String msg = AADLMessage.undefinedToken(strFileDesc);
			throw new CompileException(astFile.getLine(), astFile.getCharPositionInLine(), msg);
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
