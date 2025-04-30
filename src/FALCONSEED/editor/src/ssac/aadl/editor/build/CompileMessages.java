/*
 * @(#)CompileMessages.java	1.02	2008/05/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.build;

import java.util.ArrayList;

/**
 * コンパイル時に出力されるメッセージ。
 * ソースコード１行分のメッセージを保持する。
 * 
 * @version 1.02 2008/05/23
 * 
 * @since 1.02
 */
public class CompileMessages extends ArrayList<CompileMessageElem>
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CompileMessages() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getMessageText() {
		StringBuffer sb = new StringBuffer();
		for (CompileMessageElem elem : this) {
			if (elem != null) {
				String strElem = getElementText(elem);
				if (strElem != null) {
					if (sb.length() > 0) {
						sb.append("\n");
					}
					sb.append(strElem);
				}
			}
		}
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String getElementText(CompileMessageElem elem) {
		String ret = null;
		if (elem.hasMessages()) {
			String strcn = elem.hasColumnNo() ? String.valueOf(elem.getColumnNo()) : "-";
			ret = "Column:" + strcn + " : " + elem.getMessages();
		}
		return ret;
	}
}
