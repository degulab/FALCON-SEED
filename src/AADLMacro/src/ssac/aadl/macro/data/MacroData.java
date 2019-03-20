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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MacroData.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroData.java	1.10	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroData.java	1.00	2008/11/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ssac.aadl.macro.command.MacroAction;

/**
 * AADLマクロの定義情報集合。
 * <p>
 * このクラスは、AADLマクロ定義要素をリストとして保持する。
 * マクロ実行時には、基本的にリストに格納された順序でマクロが実行される。
 * 
 * @version 2.1.0	2014/05/29
 *
 * @since 1.00
 */
public class MacroData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/**
	 * マクロプロセス実行時の終了コードを格納する、プロセス名によるマップ。
	 */
	private final Map<String,Integer> mapProcessName = new HashMap<String,Integer>();
	/**
	 * マクロプロセス名(key)とマクロノード(value)のマップ。
	 * @since 2.1.0
	 */
	private final Map<String,MacroNode>	_mapProcName = new HashMap<String,MacroNode>();
	/**
	 * マクロ定義要素の実行順序リスト。
	 */
	private final List<MacroNode> listMacro = new ArrayList<MacroNode>();

	/**
	 * マクロ名。ファイルの場合は絶対パスを示す文字列となる。
	 */
	private String macroName = MacroNode.EMPTY_VALUE;
	/**
	 * マクロの作業ディレクトリ。
	 */
	private File   workdir   = null;
	/**
	 * マクロのテンポラリファイルのマップ
	 */
	private Map<String, File> mapTempFiles = new HashMap<String, File>();
	/**
	 * マクロ実行時引数のマップ
	 */
	private Map<String, String> mapArgs = new HashMap<String, String>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroData() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getMacroNameWithLocation(INodeLocation location) {
		if (location != null)
			return String.format("\"%s\":%s", macroName, location.toString());
		else
			return String.format("\"%s\"", macroName);
	}
	
	public String getMacroName() {
		return macroName;
	}
	
	public void setMacroName(String name) {
		this.macroName = (name == null ? MacroNode.EMPTY_VALUE : name);
	}
	
	public File getWorkDir() {
		return workdir;
	}
	
	public void setWorkDir(File dir) {
		this.workdir = dir;
	}
	
	public void clearProcessNames() {
		mapProcessName.clear();
	}
	
	public boolean isEmptyProcessNames() {
		return mapProcessName.isEmpty();
	}
	
	public Set<String> definedProcessNames() {
		return mapProcessName.keySet();
	}
	
	public int getNumProcessNames() {
		return mapProcessName.size();
	}
	
	public boolean containsProcessName(Object name) {
		return mapProcessName.containsKey(name);
	}
	
	public Integer getProcessExitCode(Object name) {
		return mapProcessName.get(name);
	}
	
	public Integer putProcessExitCode(String name, Integer value) {
		return mapProcessName.put(name, value);
	}
	
	public Integer removeProcessName(Object name) {
		return mapProcessName.remove(name);
	}
	
	public void clearMacroNodes() {
		listMacro.clear();
		mapProcessName.clear();
		_mapProcName.clear();
	}
	
	public boolean isEmptyMacroNodes() {
		return listMacro.isEmpty();
	}
	
	public int getNumMacroNodes() {
		return listMacro.size();
	}

	/**
	 * 指定されたプロセス名が定義されたマクロノードを取得する。
	 * @param name	プロセス名
	 * @return	取得できた場合はそのノード、なければ <tt>null</tt>
	 * @since 2.1.0
	 */
	public MacroNode getMacroNodeByProcessName(String name) {
		return _mapProcName.get(name);
	}
	
	public MacroNode getMacroNode(int index) {
		return listMacro.get(index);
	}

	/**
	 * 指定されたノードを、リストの終端に追加する。
	 * @param node	追加するノード
	 */
	public void addMacroNode(MacroNode node) {
		listMacro.add(node);
		MacroAction action = node.getCommandAction();
		if (action!=MacroAction.ECHO && action!=MacroAction.ERRORCOND && action!=MacroAction.COMMENT && action!=MacroAction.EXIT) {
			String procName = node.getProcessName();
			if (procName != null && procName.length() > 0) {
				mapProcessName.put(procName, null);
				_mapProcName.put(procName, node);
			}
		}
	}

	/**
	 * 指定されたノードを、リストの指定された位置に挿入する。
	 * @param index	挿入位置を示すインデックス(0～<code>getNumMacroNodes()</code>)
	 * @param node		挿入するノード
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合 <code>(index < 0 || index > getNumMacroNodes())</code>
	 * @since 2.1.0
	 */
	public void insertMacroNode(int index, MacroNode node) {
		listMacro.add(index, node);
		MacroAction action = node.getCommandAction();
		if (action!=MacroAction.ECHO && action!=MacroAction.ERRORCOND && action!=MacroAction.COMMENT && action!=MacroAction.EXIT) {
			String procName = node.getProcessName();
			if (procName != null && procName.length() > 0) {
				mapProcessName.put(procName, null);
				_mapProcName.put(procName, node);
			}
		}
	}
	
	public Map<String,Integer> processIdentifiers() {
		return mapProcessName;
	}

	/**
	 * プロセス名とマクロノードの変更不可能なマップを取得する。
	 * @return	変更不可能なマップ
	 * @since 2.1.0
	 */
	public Map<String,MacroNode> getProcessNameMap() {
		return Collections.unmodifiableMap(_mapProcName);
	}
	
	public Map<String,File> tempFiles() {
		return mapTempFiles;
	}
	
	public Map<String,String> macroArgs() {
		return mapArgs;
	}
	
	public List<MacroNode> macroNodes() {
		return listMacro;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
