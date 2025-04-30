/*
 * @(#)CompileMessageMap.java	1.10	2008/11/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CompileMessageMap.java	1.02	2008/05/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.build;

import java.util.HashMap;

import ssac.util.Strings;

/**
 * コンパイル時に出力されるメッセージ。
 * ソースコード１行分のメッセージと行番号とのマップ
 * 
 * @version 1.10	2008/11/28
 * 
 * @since 1.02
 */
public class CompileMessageMap
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final HashMap<Integer,CompileMessages> mmap = new HashMap<Integer,CompileMessages>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CompileMessageMap() {
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void clear() {
		this.mmap.clear();
	}
	
	public int size() {
		return this.mmap.size();
	}
	
	public boolean containsLine(int lineNo) {
		return this.mmap.containsKey(lineNo);
	}
	
	public CompileMessages get(int lineNo) {
		return this.mmap.get(lineNo);
	}
	
	public String getMessage(int lineNo) {
		String ret = null;
		CompileMessages cm = this.mmap.get(lineNo);
		if (cm != null) {
			ret = cm.getMessageText();
		}
		return ret;
	}
	
	public CompileMessages put(int lineNo, CompileMessages cm) {
		if (cm == null) {
			throw new NullPointerException("CompileMessages instance is null!");
		}
		
		return this.mmap.put(lineNo, cm);
	}
	
	public boolean addLineMessage(int lineNo, int colNo, String messages) {
		boolean bAdded = false;
		if (!Strings.isNullOrEmpty(messages)) {
			CompileMessageElem elem = new CompileMessageElem(lineNo, colNo, messages);
			CompileMessages cm = this.mmap.get(lineNo);
			if (cm != null) {
				cm = new CompileMessages();
				this.mmap.put(lineNo, cm);
			}
			cm.add(elem);
			bAdded = true;
		}
		return bAdded;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CompileMessageMap) {
			return ((CompileMessageMap)obj).mmap.equals(this.mmap);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.mmap.hashCode();
	}

	@Override
	public String toString() {
		return this.mmap.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
