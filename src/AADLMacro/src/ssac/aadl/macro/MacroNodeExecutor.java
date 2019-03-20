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
 * @(#)MacroNodeExecutor.java	2.1.0	2014/05/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import ssac.aadl.macro.command.MacroAction;
import ssac.aadl.macro.command.MacroModifier;
import ssac.aadl.macro.command.MacroStatus;
import ssac.aadl.macro.data.INodeLocation;
import ssac.aadl.macro.data.MacroNode;

/**
 * AADLマクロのノード単位で実行するエグゼキューターの基本実装。
 * 実行時のマクロノード、および実行ステータス、実行順序の前後関係に関するインタフェースを提供する。
 * 
 * @version 2.1.0	2014/05/29
 * @since 2.1.0
 */
public abstract class MacroNodeExecutor implements Runnable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このエグゼキューターの前に接続されたオブジェクトのセット **/
	private Set<MacroNodeExecutor>	_prevset;
	/** このエグゼキューターの後に接続されたオブジェクトのセット **/
	private Set<MacroNodeExecutor>	_nextset;
	/** エグゼキューターの開始と同時に実行されるオブジェクトのセット **/
	private Set<MacroNodeExecutor>	_startset;
	
	/** 実行ステータス **/
	protected volatile MacroStatus	_status;
	/** 終了コード、未完もしくは未定義の場合は <tt>null</tt> **/
	protected volatile Integer		_exitCode;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroNodeExecutor() {
		_prevset  = Collections.<MacroNodeExecutor>emptySet();
		_nextset  = Collections.<MacroNodeExecutor>emptySet();
		_startset = Collections.<MacroNodeExecutor>emptySet();
		_status   = MacroStatus.UNEXECUTED;
		_exitCode = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このノードの実行順序に関するリンクの有無を判定する。
	 * @return	リンクがなければ <tt>true</tt>
	 */
	public boolean isEmptyLinks() {
		return (isEmptyPrevious() && isEmptyNexts() && isEmptyStarts());
	}

	/**
	 * このノードの前に実行されるノード実行リンクの有無を判定する。
	 * @return	リンクがなければ <tt>true</tt>
	 */
	public boolean isEmptyPrevious() {
		return _prevset.isEmpty();
	}

	/**
	 * このノードの後に実行されるノード実行リンクの有無を判定する。
	 * @return	リンクがなければ <tt>true</tt>
	 */
	public boolean isEmptyNexts() {
		return _nextset.isEmpty();
	}

	/**
	 * このノードの開始と同時に実行されるノード実行リンクの有無を判定する。
	 * @return	リンクがなければ <tt>true</tt>
	 */
	public boolean isEmptyStarts() {
		return _startset.isEmpty();
	}

	/**
	 * このノードの前に実行されるノード実行リンクをクリアする。
	 * このメソッド呼び出しにより、リンク関係も変更される。
	 */
	public void clearAllPrevious() {
		for (MacroNodeExecutor aLink : _prevset) {
			aLink.removeNext(this);
			aLink.removeStart(this);
		}
		_prevset = Collections.<MacroNodeExecutor>emptySet();
	}

	/**
	 * このノードの後に実行されるノード実行リンクをクリアする。
	 * このメソッド呼び出しにより、リンク関係も変更される。
	 */
	public void clearAllNexts() {
		for (MacroNodeExecutor aLink : _nextset) {
			aLink.removePrev(this);
		}
		_nextset = Collections.<MacroNodeExecutor>emptySet();
	}

	/**
	 * このノードの開始と同時に実行されるノード実行リンクをクリアする。
	 */
	public void clearAllStarts() {
		for (MacroNodeExecutor aLink : _startset) {
			aLink.removeStart(this);
		}
		_startset = Collections.<MacroNodeExecutor>emptySet();
	}

	/**
	 * このノードの前に実行される実行リンクに、指定されたオブジェクトが登録されているかを判定する。
	 * @param aLink	判定するオブジェクト
	 * @return	登録されていれば <tt>true</tt>
	 */
	public boolean containsPrevious(Object aLink) {
		return _prevset.contains(aLink);
	}

	/**
	 * このノードの後に実行される実行リンクに、指定されたオブジェクトが登録されているかを判定する。
	 * @param aLink	判定するオブジェクト
	 * @return	登録されていれば <tt>true</tt>
	 */
	public boolean containsNext(Object aLink) {
		return _nextset.contains(aLink);
	}
	
	/**
	 * このノードの開始と同時に実行される実行リンクに、指定されたオブジェクトが登録されているかを判定する。
	 * @param aLink	判定するオブジェクト
	 * @return	登録されていれば <tt>true</tt>
	 */
	public boolean containsStart(Object aLink) {
		return _startset.contains(aLink);
	}

	/**
	 * このノードの前に実行される実行リンクの変更不可能なセットを取得する。
	 * @return	登録されているノードのセット
	 */
	public Set<MacroNodeExecutor> getPreviousSet() {
		return (_prevset.size() > 1 ? Collections.unmodifiableSet(_prevset) : _prevset);
	}

	/**
	 * このノードの後に実行される実行リンクの変更不可能なセットを取得する。
	 * @return	登録されているノードのセット
	 */
	public Set<MacroNodeExecutor> getNextSet() {
		return (_nextset.size() > 1 ? Collections.unmodifiableSet(_nextset) : _nextset);
	}

	/**
	 * このノードの開始と同時に実行される実行リンクの変更不可能なセットを取得する。
	 * @return	登録されているノードのセット
	 */
	public Set<MacroNodeExecutor> getStartSet() {
		return (_startset.size() > 1 ? Collections.unmodifiableSet(_startset) : _startset);
	}

	/**
	 * このノードの後に実行される実行リンクに、指定されたノードを登録する。
	 * このメソッドでは、指定されたノードに対しても相互リンクを登録する。
	 * @param aLink	登録するノード
	 * @return	リンクが変更された場合は <tt>true</tt>
	 * @throws NullPointerException	登録するノードが <tt>null</tt> の場合
	 */
	public boolean addToNext(MacroNodeExecutor aLink) {
		if (aLink == null)
			throw new NullPointerException();
		
		boolean modified = false;
		if (addNext(aLink)) {
			modified = true;
			aLink.addPrev(this);
		}
		return modified;
	}

	/**
	 * このノードの開始と同時に実行される実行リンクに、指定されたノードを登録する。
	 * このメソッドでは、指定されたノードに対しても相互リンクを登録する。
	 * @param aLink	登録するノード
	 * @return	リンクが変更された場合は <tt>true</tt>
	 * @throws NullPointerException	登録するノードが <tt>null</tt> の場合
	 */
	public boolean addToStart(MacroNodeExecutor aLink) {
		if (aLink == null)
			throw new NullPointerException();
		
		boolean modified = false;
		if (addStart(aLink)) {
			modified = true;
			aLink.addPrev(this);
		}
		return modified;
	}

	/**
	 * このノードの前に実行される実行リンクから、指定されたノードを除外する。
	 * このメソッドでは、指定されたノードが除去できた場合、
	 * 指定されたノードの後に実行されるリンクと開始と同時に実行されるリンクから、このオブジェクトも除去される。
	 * @param aLink	除外するノード
	 * @return	リンクが変更された場合は <tt>true</tt>
	 */
	public boolean removeFromPrevious(MacroNodeExecutor aLink) {
		boolean modified = false;
		if (removePrev(aLink)) {
			modified = true;
			aLink.removeNext(this);
			aLink.removeStart(this);
		}
		return modified;
	}
	
	/**
	 * このノードの後に実行される実行リンクから、指定されたノードを除外する。
	 * 指定されたノードが次に実行される実行リンクのみに含まれている場合、指定されたノードに対しても相互リンクが除外される。
	 * @param aLink	除外するノード
	 * @return	リンクが変更された場合は <tt>true</tt>
	 */
	public boolean removeFromNext(MacroNodeExecutor aLink) {
		boolean modified = false;
		if (removeNext(aLink)) {
			modified = true;
			if (!containsStart(aLink)) {
				aLink.removePrev(this);
			}
		}
		return modified;
	}
	
	/**
	 * このノードの開始と同時に実行される実行リンクから、指定されたノードを除外する。
	 * 指定されたノードが開始と同時に実行される実行リンクのみに含まれている場合、指定されたノードに対しても相互リンクが除外される。
	 * @param aLink	除外するノード
	 * @return	リンクが変更された場合は <tt>true</tt>
	 */
	public boolean removeFromStart(MacroNodeExecutor aLink) {
		boolean modified = false;
		if (removeStart(aLink)) {
			modified = true;
			if (!containsNext(aLink)) {
				aLink.removePrev(this);
			}
		}
		return modified;
	}

	/**
	 * このオブジェクトに関連付けられているマクロノードのプロセス名を取得する。
	 * @return	プロセス名が設定されていればその文字列、なければ <tt>null</tt>
	 */
	public String getTargetProcessName() {
		MacroNode node = getTargetNode();
		return (node==null ? null : node.getProcessName());
	}

	/**
	 * このオブジェクトに関連付けられているマクロノードを取得する。
	 * @return	<code>MacroNode</code> オブジェクト
	 */
	abstract public MacroNode getTargetNode();

	/**
	 * このオブジェクトに関連付けられているマクロノードのアクションを取得する。
	 * @return	このノードのマクロアクション、ノードが関連付けられてないか、アクションが指定されていない場合は <tt>null</tt>
	 */
	public MacroAction getCommandAction() {
		MacroNode cmdNode = getTargetNode();
		return (cmdNode == null ? null : cmdNode.getCommandAction());
	}

	/**
	 * このオブジェクトに関連付けられているマクロノードの修飾子を取得する。
	 * @return	このノードの修飾子、ノードが関連付けられてないか、修飾子が指定されていない場合は <tt>null</tt>
	 */
	public MacroModifier getCommandModifier() {
		MacroNode cmdNode = getTargetNode();
		return (cmdNode == null ? null : cmdNode.getCommandModifier());
	}

	/**
	 * このオブジェクトの実行状態を取得する。
	 * @return	<code>MacroStatus</code> オブジェクト
	 */
	public MacroStatus getStatus() {
		synchronized (this) {
			return _status;
		}
	}

	/**
	 * このオブジェクトの実行状態を変更する。
	 * @param newStatus	新しい実行状態を表す <code>MacroStatus</code> オブジェクト
	 */
	public void setStatus(MacroStatus newStatus) {
		synchronized (this) {
			_status = (newStatus==null ? MacroStatus.UNEXECUTED : newStatus);
		}
	}

	/**
	 * 終了コードを取得する。
	 * @return	終了コード、実行が完了していないか終了コードがない場合は <tt>null</tt>
	 */
	public Integer getExitCode() {
		synchronized (this) {
			return _exitCode;
		}
	}

	/**
	 * このノードを実行する。
	 * 必要に応じて、スレッドを起動し、実行完了を待機する。
	 * @param lastExitCode	直前の終了コード 
	 * @return	正常に終了し処理が継続できる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IOException	プロセス起動に失敗した場合
	 */
	abstract public boolean execNode(int lastExitCode) throws IOException;

	/**
	 * このノードが実行されたときに開始されたプロセスオブジェクトを取得する。
	 * @return	プロセスが開始していればそのオブジェクト、なければ <tt>null</tt>
	 */
	abstract public MacroNodeProcess getProcess();

	/**
	 * このノードのプロセスオブジェクトを設定する。
	 * @param proc	設定するプロセスオブジェクト、もしくは <tt>null</tt>
	 */
	abstract public void setProcess(MacroNodeProcess proc);

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("@");
		sb.append(Integer.toHexString(hashCode()));
		sb.append('[');
		//--- command
		appendCommandString(sb, getTargetNode());
		//--- process name
		sb.append(", ");
		appendProcessNameString(sb, getTargetNode());
		sb.append(']');
		
		return sb.toString();
	}
	
	protected void appendCommandString(StringBuilder buffer, MacroNode targetNode) {
		buffer.append("command");
		if (targetNode != null) {
			INodeLocation loc = targetNode.getLocation();
			if (loc != null) {
				buffer.append('(');
				buffer.append(loc);
				buffer.append(')');
			}
			buffer.append('=');
			String cmd = targetNode.getCommandString();
			if (cmd == null) {
				buffer.append("null");
			} else {
				buffer.append('\"');
				buffer.append(cmd);
				buffer.append('\"');
			}
		}
		else {
			buffer.append("=none");
		}
	}
	
	protected void appendProcessNameString(StringBuilder buffer, MacroNode targetNode) {
		buffer.append("process name=");
		if (targetNode != null) {
			String name = targetNode.getProcessName();
			if (name == null) {
				buffer.append("null");
			} else {
				buffer.append('\"');
				buffer.append(name);
				buffer.append('\"');
			}
		}
		else {
			buffer.append("none");
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたオブジェクトを、このリンクの前に接続する。
	 * このメソッドは、リンクの関係を考慮しない。
	 * @param aLink	接続する <tt>null</tt> ではないオブジェクト
	 * @return	このオブジェクトのリンクが変更された場合は <tt>true</tt>
	 */
	protected boolean addPrev(MacroNodeExecutor aLink) {
		Set<MacroNodeExecutor> newset = addLink(_prevset, aLink);
		if (newset != null) {
			_prevset = newset;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定されたオブジェクトを、このリンクの後に接続する。
	 * このメソッドは、リンクの関係を考慮しない。
	 * @param aLink	接続する <tt>null</tt> ではないオブジェクト
	 * @return	このオブジェクトのリンクが変更された場合は <tt>true</tt>
	 */
	protected boolean addNext(MacroNodeExecutor aLink) {
		Set<MacroNodeExecutor> newset = addLink(_nextset, aLink);
		if (newset != null) {
			_nextset = newset;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定されたオブジェクトを、このリンクの開始セットに接続する。
	 * このメソッドは、リンクの関係を考慮しない。
	 * @param aLink	接続する <tt>null</tt> ではないオブジェクト
	 * @return	このオブジェクトのリンクが変更された場合は <tt>true</tt>
	 */
	protected boolean addStart(MacroNodeExecutor aLink) {
		Set<MacroNodeExecutor> newset = addLink(_startset, aLink);
		if (newset != null) {
			_startset = newset;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * このリンクの前に接続されたリンクオブジェクトから、指定されたオブジェクトを除去する。
	 * このメソッドは、リンクの関係を考慮しない。
	 * @param aLink	除去するオブジェクト
	 * @return	除去された場合は <tt>true</tt>
	 */
	protected boolean removePrev(Object aLink) {
		Set<MacroNodeExecutor> newset = removeLink(_prevset, aLink);
		if (newset != null) {
			_prevset = newset;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * このリンクの後に接続されたリンクオブジェクトから、指定されたオブジェクトを除去する。
	 * このメソッドは、リンクの関係を考慮しない。
	 * @param aLink	除去するオブジェクト
	 * @return	除去された場合は <tt>true</tt>
	 */
	protected boolean removeNext(Object aLink) {
		Set<MacroNodeExecutor> newset = removeLink(_nextset, aLink);
		if (newset != null) {
			_nextset = newset;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * このリンクの開始セットに接続されたリンクオブジェクトから、指定されたオブジェクトを除去する。
	 * このメソッドは、リンクの関係を考慮しない。
	 * @param aLink	除去するオブジェクト
	 * @return	除去された場合は <tt>true</tt>
	 */
	protected boolean removeStart(Object aLink) {
		Set<MacroNodeExecutor> newset = removeLink(_startset, aLink);
		if (newset != null) {
			_startset = newset;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定されたセットに、指定されたオブジェクトを追加する。
	 * @param orgset	変更するセット
	 * @param aLink		追加するオブジェクト
	 * @return	追加した場合は新しいセットオブジェクト、変更されなかった場合は <tt>null</tt>
	 */
	protected Set<MacroNodeExecutor> addLink(Set<MacroNodeExecutor> orgset, MacroNodeExecutor aLink) {
		int num = orgset.size();
		if (num > 1) {
			// multiple
			if (orgset.add(aLink)) {
				return orgset;
			}
		}
		else if (num == 1) {
			// singleton
			if (!orgset.contains(aLink)) {
				Set<MacroNodeExecutor> newset = new LinkedHashSet<MacroNodeExecutor>();	// 順序維持のため
				newset.addAll(orgset);
				newset.add(aLink);
				return newset;
			}
		}
		else {
			// empty
			return Collections.<MacroNodeExecutor>singleton(aLink);
		}
		
		// no changes
		return null;
	}

	/**
	 * 指定されたセットから、指定されたオブジェクトを除去する。
	 * @param orgset	変更するセット
	 * @param aLink		除去するオブジェクト
	 * @return	除去できた場合は新しいセットオブジェクト、変更されなかった場合は <tt>null</tt>
	 */
	protected Set<MacroNodeExecutor> removeLink(Set<MacroNodeExecutor> orgset, Object aLink) {
		int num = orgset.size();
		if (num > 2) {
			// multiple
			if (orgset.remove(aLink)) {
				return orgset;
			}
		}
		else if (num == 2) {
			// two entries
			if (orgset.remove(aLink)) {
				MacroNodeExecutor oneLink = orgset.iterator().next();
				orgset.clear();
				return Collections.<MacroNodeExecutor>singleton(oneLink);
			}
		}
		else if (num == 1) {
			// singleton
			if (orgset.contains(aLink)) {
				return Collections.<MacroNodeExecutor>emptySet();
			}
		}
		
		// no changes
		return null;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
