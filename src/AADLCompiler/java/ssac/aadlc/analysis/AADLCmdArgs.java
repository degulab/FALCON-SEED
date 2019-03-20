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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AADLCmdArgs.java	1.70	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLCmdArgs.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLCmdArgs.java	1.10	2008/05/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLCmdArgs.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

import java.util.Collection;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ssac.aadlc.analysis.type.prim.APrimString;

/**
 * AADL コマンドライン引数
 * 
 * 
 * @version 1.70	2011/06/29
 */
public class AADLCmdArgs extends AADLSymbolMap
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final Pattern patCmdArg = Pattern.compile("\\A\\$(\\d+)\\z");
	static private final int MIN_NUMBER = 1;		// $1 から
	static private final int MAX_NUMBER = 99;	// $99 まで
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private int minArgNo = -1;
	private int maxArgNo = -1;
	
	private final TreeMap<Integer,AADLSymbol> numberMap = new TreeMap<Integer,AADLSymbol>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLCmdArgs() {
		
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return numberMap.isEmpty();
	}
	
	public int getSymbolCount() {
		return numberMap.size();
	}
	
	public int getMinArgNo() {
		return this.minArgNo;
	}
	
	public int getMaxArgNo() {
		return this.maxArgNo;
	}
	
	public void clear() {
		numberMap.clear();
		this.minArgNo = -1;
		this.maxArgNo = -1;
		super.clear();
	}
	
	public Collection<Integer> numbers() {
		return this.numberMap.keySet();
	}
	
	public AADLSymbol getSymbol(Integer argNo) {
		return this.numberMap.get(argNo);
	}
	
	public Collection<AADLSymbol> getSymbols() {
		return numberMap.values();
	}

	// '$xxxx' 形式のパラメータでシンボルを設定する
	public void setSymbol(String cmdArg) {
		// Ignore, if exist symbol
		if (hasSymbol(cmdArg)) {
			return;
		}
		
		// Check
		Matcher mc = patCmdArg.matcher(cmdArg);
		if (!mc.matches()) {
			throw new IllegalArgumentException("Pattern not supported!");
		}

		// get number
		String strnum = mc.group(1);
		Integer argNum = Integer.parseInt(strnum);
		
		// Limit check
		if (argNum.intValue() < MIN_NUMBER || MAX_NUMBER < argNum.intValue()) {
			throw new IllegalArgumentException("Number out of range!");
		}
		
		// create symbol
		String javaSymbol = "_arg" + argNum.toString();
		AADLSymbol newSymbol = new AADLSymbol(true, cmdArg, javaSymbol, APrimString.instance);	// const variable
		newSymbol.setInitialized();	// to set initialized
		super.setSymbol(newSymbol);
		numberMap.put(argNum, newSymbol);

		// update min/max
		if (this.minArgNo > 0)
			this.minArgNo = Math.min(this.minArgNo, argNum.intValue());
		else
			this.minArgNo = argNum.intValue();
		this.maxArgNo = Math.max(this.maxArgNo, argNum.intValue());
	}

	// 最大の引数番号まで、シンボルを補完する
	public void complementSymbolsToMaxArgNo() {
		if (maxArgNo > 0) {
			for (int i = 1; i <= maxArgNo; i++) {
				if (!numberMap.containsKey(i)) {
					String no = String.valueOf(i);
					String cmdArg = "$".concat(no);
					String javaSymbol = "_arg".concat(no);
					AADLSymbol newSymbol = new AADLSymbol(true, cmdArg, javaSymbol, APrimString.instance);	// const variable
					newSymbol.setInitialized();	// to set initialized
					super.setSymbol(newSymbol);
					numberMap.put(i, newSymbol);
				}
			}
			minArgNo = 1;
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
