/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)JavaLineBuffer.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen;

import java.util.ArrayList;

/**
 * Javaソースコードの行バッファ。
 * Javaソース行とAADL行との対応も保持する。
 * 
 * @version 1.00	2007/11/29
 */
public class JavaLineBuffer
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ArrayList<String> codes;
	private final ArrayList<Integer> lines;
	private int minLineNo;
	private int maxLineNo;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JavaLineBuffer() {
		this.codes = new ArrayList<String>();
		this.lines = new ArrayList<Integer>();
		this.minLineNo = -1;
		this.maxLineNo = -1;
	}
	
	@SuppressWarnings("unchecked")
	public JavaLineBuffer(JavaLineBuffer src) {
		this.codes = (ArrayList<String>)src.codes.clone();
		this.lines = (ArrayList<Integer>)src.lines.clone();
		this.minLineNo = src.minLineNo;
		this.maxLineNo = src.maxLineNo;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return codes.isEmpty();
	}
	
	public boolean hasCodes() {
		return (!codes.isEmpty());
	}
	
	public int getLineCount() {
		return codes.size();
	}
	
	public int getMinLineNo() {
		return minLineNo;
	}
	
	public int getMaxLineNo() {
		return maxLineNo;
	}
	
	public String[] getCodes() {
		return codes.toArray(new String[codes.size()]);
	}
	
	public Integer[] getLines() {
		return lines.toArray(new Integer[lines.size()]);
	}
	
	public int[] getIntLines() {
		int[] ret = new int[lines.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = lines.get(i).intValue();
		}
		return ret;
	}
	
	public void clear() {
		codes.clear();
		lines.clear();
		minLineNo = -1;
		maxLineNo = -1;
	}
	
	public void appendLine(String text, int lineNo) {
		assert text != null;
		
		codes.add(text);
		lines.add(lineNo);
		
		updateMinMaxLineNo(lineNo);
	}
	
	public void appendLine(String header, String text, String footer, int lineNo) {
		String appender = (header != null) ? (header + text) : text;
		if (footer != null) {
			appender = appender + footer;
		}
		
		appendLine(appender, lineNo);
	}
	
	public void appendLine(int lineNo, String...texts) {
		StringBuffer sb = new StringBuffer();
		for (String txt : texts) {
			sb.append(txt);
		}
		
		appendLine(sb.toString(), lineNo);
	}
	
	public void appendLineWithBlank(String text, int lineNo) {
		appendLine(" ", text, " ", lineNo);
	}
	
	public void insertLine(int index, String text, int lineNo) {
		assert index >= 0;
		assert text != null;
		
		codes.add(index, text);
		lines.add(index, lineNo);
		
		updateMinMaxLineNo(lineNo);
	}
	
	public void insertLine(int index, String header, String text, String footer, int lineNo) {
		String appender = (header != null) ? (header + text) : text;
		if (footer != null) {
			appender = appender + footer;
		}
		
		insertLine(index, appender, lineNo);
	}

	public void catHeaderAtFirstLine(String text) {
		catHeaderAt(0, text);
	}
	
	public void catHeaderAt(int index, String text) {
		assert index >= 0;
		assert text != null;
		
		codes.set(index, text + codes.get(index));
	}
	
	public void catFooterAt(int index, String text) {
		assert index >= 0;
		assert text != null;
		
		codes.set(index, codes.get(index) + text);
	}
	
	public void catFooterAtLastLine(String text) {
		assert text != null;
		
		codes.set(codes.size()-1, codes.get(codes.size()-1) + text);
	}
	
	// Javaコード全体を指定された文字列で囲む
	public void enclose(String header, String footer) {
		assert header != null;
		assert footer != null;
		
		if (isEmpty()) {
			appendLine(header + footer, 0);
		}
		else if (getLineCount() == 1) {
			codes.set(0, header + codes.get(0) + footer);
		}
		else {
			codes.set(0, header + codes.get(0));
			codes.set(codes.size()-1, codes.get(codes.size()-1) + footer);
		}
	}

	// Javaコード全体を'('')'で囲む
	public void encloseParen() {
		enclose("(", ")");
	}
	
	// Javaコードを終端に連結する
	public void add(JavaLineBuffer aLines) {
		add(null, aLines, null);
	}
	
	public void add(String header, JavaLineBuffer aLines, String footer) {
		assert aLines != null;
		
		if (aLines.getLineCount() > 0) {
			int sidx = 0;
			if (header != null) {
				appendLine(header + aLines.codes.get(sidx), aLines.lines.get(sidx));
				sidx++;
			}
			
			for (; sidx < aLines.getLineCount(); sidx++) {
				appendLine(aLines.codes.get(sidx), aLines.lines.get(sidx));
			}
			
			if (footer != null) {
				catFooterAt(codes.size()-1, footer);
			}
		}
		else {
			appendLine(header, "", footer, 0);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		if (getLineCount() > 0) {
			sb.append(codes.get(0));
			
			for (int i = 1; i < codes.size(); i++) {
				sb.append("\n");
				sb.append(codes.get(i));
			}
		}
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	private void updateMinMaxLineNo(int lineNo) {
		if (lineNo > 0) {
			// min
			if (minLineNo > 0)
				minLineNo = Math.min(minLineNo, lineNo);
			else
				minLineNo = lineNo;
			
			// max
			if (maxLineNo > 0)
				maxLineNo = Math.max(maxLineNo, lineNo);
			else
				maxLineNo = lineNo;
		}
	}
}
