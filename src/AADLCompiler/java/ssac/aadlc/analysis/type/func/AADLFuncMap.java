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
 * @(#)AADLFuncMap.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.func;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import ssac.aadlc.analysis.type.AADLType;

/**
 * AADL組み込み関数に関するキーと定義を管理するマップ。
 * 
 * @version 1.00	2007/11/29
 */
public class AADLFuncMap
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final TreeMap<String,AADLFuncList> funcMap = new TreeMap<String,AADLFuncList>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLFuncMap() {
		
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void clear() {
		this.funcMap.clear();
	}
	
	public boolean isEmpty() {
		return this.funcMap.isEmpty();
	}
	
	public int getFuncCount() {
		int retNum = 0;
		Iterator<AADLFuncList> it = funcMap.values().iterator();
		while (it.hasNext()) {
			AADLFuncList flist = it.next();
			retNum += flist.size();
		}
		return retNum;
	}
	
	public boolean hasName(String name) {
		return this.funcMap.containsKey(name);
	}
	
	public boolean contains(String name, AADLType...paramTypes) {
		return contains(new AADLFuncType(name, null, paramTypes));
	}
	
	public boolean contains(AADLFuncType funcType) {
		boolean ret = false;
		AADLFuncList flist = this.funcMap.get(funcType.getName());
		if (flist != null && flist.contains(funcType)) {
			ret = true;
		}
		return ret;
	}
	
	public boolean hasCallableFunction(String name, AADLType...paramTypes) {
		boolean ret = false;
		AADLFuncList flist = this.funcMap.get(name);
		if (flist != null) {
			Iterator<AADLFuncType> it = flist.iterator();
			while (it.hasNext()) {
				AADLFuncType ft = it.next();
				if (ft.isCallable(name, paramTypes)) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	public Collection<AADLFuncType> getAllFunctions() {
		AADLFuncList flist = new AADLFuncList();
		Iterator<AADLFuncList> it = funcMap.values().iterator();
		while (it.hasNext()) {
			flist.addAll(it.next());
		}
		return flist;
	}
	
	public Collection<AADLFuncType> getFunctions(String name) {
		return this.funcMap.get(name);
	}
	
	public AADLFuncType get(String name, AADLType...paramTypes) {
		return get(new AADLFuncType(name, null, paramTypes));
	}
	
	public AADLFuncType get(AADLFuncType funcType) {
		AADLFuncType retType = null;
		AADLFuncList flist = this.funcMap.get(funcType.getName());
		if (flist != null) {
			Iterator<AADLFuncType> it = flist.iterator();
			while (it.hasNext()) {
				AADLFuncType ft = it.next();
				if (ft.equals(funcType)) {
					retType = ft;
					break;
				}
			}
		}
		return retType;
	}
	
	public AADLFuncType getCallable(String name, AADLType...paramTypes) {
		AADLFuncType retType = null;
		AADLFuncList flist = this.funcMap.get(name);
		if (flist != null) {
			Iterator<AADLFuncType> it = flist.iterator();
			while (it.hasNext()) {
				AADLFuncType ft = it.next();
				if (ft.isCallable(name, paramTypes)) {
					retType = ft;
					break;
				}
			}
		}
		return retType;
	}
	
	public void set(AADLFuncType funcType) {
		AADLFuncList flist = this.funcMap.get(funcType.getName());
		if (flist != null) {
			if (flist.contains(funcType)) {
				// already exist
				throw new IllegalArgumentException("Already exist function!");
			}
			flist.add(funcType);
		}
		else {
			flist = new AADLFuncList();
			flist.add(funcType);
			this.funcMap.put(funcType.getName(), flist);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	static class AADLFuncList extends ArrayList<AADLFuncType>
	{
		public AADLFuncList() {
			super();
		}
	}
}
