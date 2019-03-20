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
 * @(#)CommandProcessNameListNode.java	2.1.0	2014/05/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * コマンドアクションもしくはコマンド修飾子に付随するプロセス名リストの構文木におけるルート要素。
 * このオブジェクトでは、プロセス名リストの構文解析終了時に、内部プロセス名集合を更新すること。
 * 
 * @version 2.1.0	2014/05/29
 * @since 2.1.0
 */
public class CommandProcessNameListNode extends CommandNode implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** プロセス名(key)とトークン(value)の変更不可能なマップ **/
	private Map<String, CommandToken>	_mapNames;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandProcessNameListNode(CommandToken token) {
		super(token);
		if (token.getType() != CommandToken.PROCID_LIST) {
			throw new IllegalArgumentException("The specified token is not Process name list root : token=" + token.toString());
		}
		_mapNames = Collections.<String,CommandToken>emptyMap();	// 不変
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * プロセス名が一つも登録されていない場合は <tt>true</tt>
	 */
	public boolean isEmptyProcessName() {
		return _mapNames.isEmpty();
	}

	/**
	 * プロセス名の重複しない、変更不可能な集合を返す。
	 * @return	プロセス名の <code>Set</code>
	 */
	public Set<String> getProcessNameSet() {
		return _mapNames.keySet();
	}

	/**
	 * プロセス名とトークンの、変更不可能なマップを返す。
	 * @return	プロセス名とトークンの <code>Map</code>
	 */
	public Map<String,CommandToken> getProcessNameMap() {
		return _mapNames;
	}

	/**
	 * 現在の子ノードを読み込み、プロセス名マップを再構成する。
	 * @return	プロセス名マップの内容が更新された場合は <tt>true</tt>
	 */
	public boolean updateProcessNameMap() {
		Map<String,CommandToken> newMap = createProcessNameMap();
		if (!newMap.isEmpty()) {
			if (newMap.equals(_mapNames)) {
				// no changes
				newMap.clear();
				return false;
			} else {
				// modified
				_mapNames = Collections.unmodifiableMap(newMap);
				return true;
			}
		}
		else {
			// no process names
			if (_mapNames.isEmpty()) {
				// no changes
				return false;
			} else {
				// modified
				_mapNames = Collections.<String,CommandToken>emptyMap();
				return true;
			}
		}
	}

	@Override
	public CommandProcessNameListNode clone()
	{
		try {
			CommandProcessNameListNode n = (CommandProcessNameListNode)super.clone();
			Map<String,CommandToken> newMap = n.createProcessNameMap();
			if (newMap.isEmpty()) {
				// no process name
				n._mapNames = Collections.<String,CommandToken>emptyMap();
			} else {
				// has process names
				n._mapNames = Collections.unmodifiableMap(newMap);
			}
			return n;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 子ノードを読み込み、プロセス名マップを生成する。
	 * @return	生成された変更可能なプロセス名マップ
	 */
	private Map<String,CommandToken> createProcessNameMap() {
		HashMap<String,CommandToken> map = new HashMap<String,CommandToken>();
		int numChildren = getNumChildren();
		for (int i = 0; i < numChildren; ++i) {
			updateProcessNameMap(map, getChild(i));
		}
		return map;
	}

	/**
	 * 指定されたノードとその子ノードを読み込み、指定されたプロセス名マップを更新する。
	 * @param map	更新対象のマップ
	 * @param node	読み込むノード
	 */
	private void updateProcessNameMap(Map<String,CommandToken> map, CommandNode node) {
		if (node.getToken().getType() == CommandToken.IDENTIFIER) {
			String name = node.getToken().getText();
			if (!map.containsKey(name)) {
				map.put(name, node.getToken());
			}
		}
		
		int numChildren = node.getNumChildren();
		for (int i = 0; i < numChildren; ++i) {
			updateProcessNameMap(map, node.getChild(i));
		}
	}
}
