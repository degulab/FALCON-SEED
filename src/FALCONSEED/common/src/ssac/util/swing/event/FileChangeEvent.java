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
 * @(#)FileChangeEvent.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.event;

import java.io.File;
import java.util.EventObject;

import ssac.util.Objects;

/**
 * <code>File</code> オブジェクトの抽象パスが示すファイルに
 * 変更があったことを通知するイベントオブジェクト。
 * このイベントオブジェクトには、変更前の抽象パスを保持する <code>File</code> オブジェクトと、
 * 変更後の抽象パスを保持する <code>File</code> オブジェクトが格納される。
 * このオブジェクトの状態により、ファイルの変更操作を表す。<bl>
 * なお、以下の説明において、このイベントが保持する <code>File</code> オブジェクトを
 * 次のように表記する。
 * <blockquote>
 * <dl>
 * <dt><em>OLD</em></dt><dd>{@link FilePair#getOldFile()} が返す <code>File</code> オブジェクト</dd>
 * <dt><em>NEW</em></dt><dd>{@link FilePair#getNewFile()} が返す <code>File</code> オブジェクト</dd>
 * </dl>
 * </blockquote>
 * <dl>
 * <dt><em>OLD</em> == <tt>null</tt> : <em>NEW</em> != <tt>null</tt></dt>
 * <dd><em>NEW</em> の表す抽象パスが新規に作成されたことを示す。
 * アプリケーションの操作において新規作成が行われたことを示すため、実際のファイルもしくは
 * ディレクトリは存在していない可能性もある。</dd>
 * <dt><em>OLD</em> != <tt>null</tt> : <em>NEW</em> == <tt>null</tt></dt>
 * <dd><em>OLD</em> の表す抽象パスが削除されたことを示す。</dd>
 * <dt><em>OLD</em> != <tt>null</tt> : <em>NEW</em> != <tt>null</tt></dt>
 * <dd><em>OLD</em> と <em>NEW</em> が等しい場合は、ファイルもしくはディレクトリの内容が
 * 更新されたことを表す。<bl>
 * <em>OLD</em> と <em>NEW</em> の親の抽象パスが等しい場合、名前が変更されたことを示す。<bl>
 * それ以外の場合は、<em>OLD</em> から <em>NEW</em> にパスが変更された(移動した)ことを示す。
 * </dl>
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.14
 */
public class FileChangeEvent extends EventObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final FilePair[] EMPTY_ARRAY = new FilePair[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final FilePair[]	_items;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FileChangeEvent(Object source, File oldFile, File newFile) {
		this(new FilePair(oldFile, newFile));
	}
	
	public FileChangeEvent(Object source, FilePair...pairs) {
		super(source);
		if (pairs != null && pairs.length > 0) {
			this._items = new FilePair[pairs.length];
			System.arraycopy(pairs, 0, this._items, 0, pairs.length);
		} else {
			this._items = EMPTY_ARRAY;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 変更通知された要素数を返す。
	 */
	public int getItemCount() {
		return _items.length;
	}

	/**
	 * 変更通知された要素の配列を返す。
	 * このメソッドが返す配列は、このオブジェクトが保持する配列のシャローコピーとなる。
	 */
	public FilePair[] items() {
		if (_items.length > 0) {
			return (FilePair[])_items.clone();
		} else {
			return EMPTY_ARRAY;
		}
	}

	/**
	 * 指定された位置の要素を返す。
	 * @param index	取得する位置のインデックス
	 * @return	指定された位置の要素
	 */
	public FilePair getItem(int index) {
		return _items[index];
	}

	/**
	 * 変更前の抽象パスを保持する <code>File</code> オブジェクトを、
	 * 指定された位置の要素から取得する。
	 * @param index	取得する位置のインデックス
	 * @return	指定された位置に格納されている <code>File</code> オブジェクト
	 */
	public File getOldFile(int index) {
		return _items[index].getOldFile();
	}

	/**
	 * 変更後の抽象パスを保持する <code>File</code> オブジェクトを、
	 * 指定された位置の要素から取得する。
	 * @param index	取得する位置のインデックス
	 * @return	指定された位置に格納されている <code>File</code> オブジェクト
	 */
	public File getNewFile(int index) {
		return _items[index].getNewFile();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName() + " " + Integer.toString(hashCode()));
		sb.append(" ");
		sb.append(_items);
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static public class FilePair
	{
		private final File	_old;
		private final File	_new;
		
		public FilePair(File oldFile, File newFile) {
			this._old = oldFile;
			this._new = newFile;
		}
		
		public File getOldFile() {
			return _old;
		}
		
		public File getNewFile() {
			return _new;
		}

		@Override
		public int hashCode() {
			int hOld = (_old == null ? 0 : _old.hashCode());
			int hNew = (_new == null ? 0 : _new.hashCode());
			return (hOld ^ hNew);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
			if (obj instanceof FilePair) {
				return Objects.isEqual(this, obj);
			}
			
			return false;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append("old[");
			sb.append(_old);
			sb.append("] new[");
			sb.append(_new);
			sb.append("]]");
			return sb.toString();
		}
	}
}
