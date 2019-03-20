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
 * @(#)AADLJavaProgram.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLJavaProgram.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ssac.aadlc.codegen.expression.ACodeJavaAction;
import ssac.aadlc.io.FileUtil;

/**
 * AADL から生成された Javaソースコードの管理
 * 
 * @version 1.30	2009/12/02
 */
public class AADLJavaProgram
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private ACodePackage packageCode = null;
	private final ArrayList<ACodeJavaAction> headerCodes = new ArrayList<ACodeJavaAction>();
	private final ArrayList<AADLCode> mainCodes = new ArrayList<AADLCode>();
	
	private final HashMap<Integer,Integer> lineMap = new HashMap<Integer,Integer>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLJavaProgram() {
		
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return (headerCodes.isEmpty() && mainCodes.isEmpty());
	}
	
	public void clear() {
		this.headerCodes.clear();
		this.mainCodes.clear();
		this.lineMap.clear();
	}
	
	public boolean hasHeaderCode() {
		return (!headerCodes.isEmpty());
	}
	
	public boolean hasMainCode() {
		return (!mainCodes.isEmpty());
	}
	
	public Map<Integer, Integer> getLineMap() {
		return lineMap;
	}
	
	public int getLineNoByJavaLine(int javaLineNo) {
		int retLine = -1;
		Integer aln = this.lineMap.get(javaLineNo);
		if (aln != null) {
			retLine = aln.intValue();
		}
		return retLine;
	}
	
	public ACodePackage getPackageCode() {
		return packageCode;
	}
	
	public void setPackageCode(ACodePackage code) {
		if (code != null && code.getPackagePath() != null) {
			packageCode = code;
		} else {
			packageCode = null;
		}
	}
	
	public Collection<ACodeJavaAction> getHeaderCodes() {
		return this.headerCodes;
	}
	
	public Collection<AADLCode> getMainCodes() {
		return this.mainCodes;
	}
	
	public void addHeaderCode(ACodeJavaAction codeHeader) {
		this.headerCodes.add(codeHeader);
	}
	
	public void addMainCode(AADLCode codeMain) {
		this.mainCodes.add(codeMain);
	}
	
	public String output() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		writeJavaCode(pw);
		
		return sw.toString();
	}
	
	public String toString() {
		StringWriter sw = new StringWriter();
		BufferedWriter bw = new BufferedWriter(sw);

		try {
			writeJavaCode(bw);
		}
		catch (Exception ex) {
			;	// 例外は無視
		}
		finally {
			FileUtil.closeStream(bw);
		}
		
		return sw.toString();
	}
	
	public void output(BufferedWriter bw)
		throws IOException
	{
		writeJavaCode(bw);
	}
	
	public void buildLineMap(Map<Integer,Integer> mapbuf) {
		// 行番号クリア
		mapbuf.clear();
		int jlno = 1;	// Java 行番号
		
		// パッケージ 出力
		if (packageCode != null) {
			JavaLineBuffer jlb = packageCode.getJavaLineBuffer();
			Integer[] lines = jlb.getLines();
			for (Integer ln : lines) {
				if (ln.intValue() >= 0) {
					mapbuf.put(jlno, ln);
				}
				jlno++;
			}
		}
		
		// JAVA_HEADER
		Iterator<ACodeJavaAction> hit = this.headerCodes.iterator();
		while (hit.hasNext()) {
			JavaLineBuffer jlb = hit.next().getJavaLineBuffer();
			Integer[] lines = jlb.getLines();
			for (Integer ln : lines) {
				if (ln.intValue() >= 0) {
					mapbuf.put(jlno, ln);
				}
				jlno++;
			}
		}
		
		// Main
		Iterator<AADLCode> mit = this.mainCodes.iterator();
		while (mit.hasNext()) {
			JavaLineBuffer jlb = mit.next().getJavaLineBuffer();
			Integer[] lines = jlb.getLines();
			for (Integer ln : lines) {
				if (ln.intValue() >= 0) {
					mapbuf.put(jlno, ln);
				}
				jlno++;
			}
		}
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	private void writeJavaCode(BufferedWriter bw)
		throws IOException
	{
		// 行番号クリア
		this.lineMap.clear();
		int jlno = 1;	// Java 行番号
		
		// パッケージ 出力
		if (packageCode != null) {
			JavaLineBuffer jlb = packageCode.getJavaLineBuffer();
			String[] codes = jlb.getCodes();
			Integer[] lines = jlb.getLines();
			for (int i = 0; i < codes.length; i++) {
				//--- test
				//bw.writer(String.format("/* line:%-6d */  ", lines[i]));
				//--- test
				bw.write(codes[i]);
				if (lines[i].intValue() >= 0) {
					this.lineMap.put(jlno, lines[i]);
				}
				bw.newLine();
				jlno++;
			}
		}

		// JAVA_HEADER 出力
		Iterator<ACodeJavaAction> hit = this.headerCodes.iterator();
		while (hit.hasNext()) {
			JavaLineBuffer jlb = hit.next().getJavaLineBuffer();
			String[] codes = jlb.getCodes();
			Integer[] lines = jlb.getLines();
			for (int i = 0; i < codes.length; i++) {
				//--- test
				//bw.writer(String.format("/* line:%-6d */  ", lines[i]));
				//--- test
				bw.write(codes[i]);
				if (lines[i].intValue() >= 0) {
					this.lineMap.put(jlno, lines[i]);
				}
				bw.newLine();
				jlno++;
			}
		}
		
		// Main 出力
		Iterator<AADLCode> mit = this.mainCodes.iterator();
		while (mit.hasNext()) {
			JavaLineBuffer jlb = mit.next().getJavaLineBuffer();
			String[] codes = jlb.getCodes();
			Integer[] lines = jlb.getLines();
			for (int i = 0; i < codes.length; i++) {
				//--- test
				//bw.writer(String.format("/* line:%-6d */  ", lines[i]));
				//--- test
				bw.write(codes[i]);
				if (lines[i].intValue() >= 0) {
					this.lineMap.put(jlno, lines[i]);
				}
				bw.newLine();
				jlno++;
			}
		}
	}
	
	private void writeJavaCode(PrintWriter pw) {
		// 行番号クリア
		this.lineMap.clear();
		int jlno = 1;	// Java 行番号
		
		// パッケージ 出力
		if (packageCode != null) {
			JavaLineBuffer jlb = packageCode.getJavaLineBuffer();
			String[] codes = jlb.getCodes();
			Integer[] lines = jlb.getLines();
			for (int i = 0; i < codes.length; i++) {
				//--- test
				//pw.print(String.format("/* line:%-6d */  ", lines[i]));
				//--- test
				pw.println(codes[i]);
				if (lines[i].intValue() >= 0) {
					this.lineMap.put(jlno, lines[i]);
				}
				jlno++;
			}
		}

		// JAVA_HEADER 出力
		Iterator<ACodeJavaAction> hit = this.headerCodes.iterator();
		while (hit.hasNext()) {
			JavaLineBuffer jlb = hit.next().getJavaLineBuffer();
			String[] codes = jlb.getCodes();
			Integer[] lines = jlb.getLines();
			for (int i = 0; i < codes.length; i++) {
				//--- test
				//pw.print(String.format("/* line:%-6d */  ", lines[i]));
				//--- test
				pw.println(codes[i]);
				if (lines[i].intValue() >= 0) {
					this.lineMap.put(jlno, lines[i]);
				}
				jlno++;
			}
		}
		
		// Main 出力
		Iterator<AADLCode> mit = this.mainCodes.iterator();
		while (mit.hasNext()) {
			JavaLineBuffer jlb = mit.next().getJavaLineBuffer();
			String[] codes = jlb.getCodes();
			Integer[] lines = jlb.getLines();
			for (int i = 0; i < codes.length; i++) {
				//--- test
				//pw.print(String.format("/* line:%-6d */  ", lines[i]));
				//--- test
				pw.println(codes[i]);
				if (lines[i].intValue() >= 0) {
					this.lineMap.put(jlno, lines[i]);
				}
				jlno++;
			}
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

}
