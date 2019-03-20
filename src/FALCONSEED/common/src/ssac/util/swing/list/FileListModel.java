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
package ssac.util.swing.list;

import java.io.File;
import java.util.NoSuchElementException;

import javax.swing.DefaultListModel;

/**
 * リスト・コンポーネントによって表示される項目を保持するデータモデル。
 * 
 * @version 1.00 2008/03/24
 */
public class FileListModel extends DefaultListModel
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

	/**
	 * 要素が空の <code>FileListModel</code> インスタンスを生成する。
	 */
	public FileListModel() {
		super();
	}

	/**
	 * 引数で指定されたパスを要素とする <code>FileListModel</code> インスタンスを生成する。
	 * 
	 * @param paths パスを表す文字列の配列
	 */
	public FileListModel(String[] paths) {
		this();
		for (String path : paths) {
			this.addElement(path);
		}
	}
	
	/**
	 * 引数で指定されたパスを要素とする <code>FileListModel</code> インスタンスを生成する。
	 * 
	 * @param files <code>File</code> の配列
	 */
	public FileListModel(File[] files) {
		this();
		for (File f : files) {
			this.addElement(f);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * リスト内の指定された位置に指定された要素を挿入する。
	 * 
	 * @param index 指定の要素が挿入される位置のインデックス
	 * @param obj	挿入される要素
	 * 
	 * @throws ArrayIndexOutOfBoundsException インデックスが範囲(<code>index &lt; 0 || index &gt;= size()</code>)外の場合
	 */
	@Override
	public void add(int index, Object obj) {
		super.add(index, toElement(obj));
	}

	/**
	 * 指定された要素をリストの末尾に追加する。
	 * 
	 * @param obj	追加される要素
	 */
	@Override
	public void addElement(Object obj) {
		super.addElement(toElement(obj));
	}

	/**
	 * 指定されたオブジェクトがリスト内の要素かどうかを判定する。
	 * 
	 * @param obj	オブジェクト
	 * @return 指定されたオブジェクトがリスト内の要素と同じ場合は <code>true</code>
	 */
	@Override
	public boolean contains(Object obj) {
		return super.contains(toElement(obj));
	}

	/**
	 * 指定されたインデックスの要素を返す。
	 * 
     * <blockquote>
     * <b>注:</b> このメソッドは推奨されません。推奨されるメソッドは <code>get(int)</code> です。
     *    これは 1.2 の Collections Framework で定義された <code>List</code> インタフェースを実装します。
     * </blockquote>
     * 
     * @param	index	このリストのインデックス
     * @return			指定されたインデックスの要素
     * 
     * @throws ArrayIndexOutOfBoundsException	インデックスが負であるか、リストのサイズよりも小さくない場合
     *
     * @see #get(int)
	 */
	@Override
	public FileListElement elementAt(int index) {
		return (FileListElement)super.elementAt(index);
	}

	/**
	 * リストの最初の要素を返す。
	 * 
	 * @return	リストの最初の要素
	 * 
	 * @throws	NoSuchElementException	要素を持たない場合
	 */
	@Override
	public FileListElement firstElement() {
		return (FileListElement)super.firstElement();
	}

	/**
	 * リスト内の指定された位置にある要素を返す。
	 * 
	 * @param	index	返される要素のインデックス
	 * @return			指定されたインデックスの要素
     * 
     * @throws ArrayIndexOutOfBoundsException	インデックスが負であるか、リストのサイズよりも小さくない場合
	 */
	@Override
	public FileListElement get(int index) {
		return (FileListElement)super.get(index);
	}

	/**
	 * 指定されたインデックスの要素を返す。
	 * 
     * <blockquote>
     * <b>注:</b> このメソッドは推奨されません。推奨されるメソッドは <code>get(int)</code> です。
     *    これは 1.2 の Collections Framework で定義された <code>List</code> インタフェースを実装します。
     * </blockquote>
     * 
     * @param	index	このリストのインデックス
     * @return			指定されたインデックスの要素
     * 
     * @throws ArrayIndexOutOfBoundsException	インデックスが負であるか、リストのサイズよりも小さくない場合
     *
     * @see #get(int)
	 */
	@Override
	public FileListElement getElementAt(int index) {
		return (FileListElement)super.getElementAt(index);
	}

	/**
	 * <code>index</code> 以降に最初に現れる <code>obj</code> を検索する。
	 * 
	 * @param	obj		目的のオブジェクト
	 * @param	index	検索開始位置のインデックス
	 * @return			<code>index</code> 以降で最初に <code>obj</code> が現れる位置のインデックス。
	 * 					オブジェクトが見つからない場合は -1
	 */
	@Override
	public int indexOf(Object obj, int index) {
		return super.indexOf(toElement(obj), index);
	}
	
	/**
	 * 最初に現れる <code>obj</code> を検索する。
	 * 
	 * @param	obj	オブジェクト
	 * @return		リスト内で引数が最初に現れる位置のインデックス。オブジェクトが見つからない場合は -1
	 */
	@Override
	public int indexOf(Object obj) {
		return super.indexOf(toElement(obj));
	}

	/**
	 * 指定されたオブジェクトを、リストの指定された <code>index</code> の要素として挿入する。
	 * 
     * <blockquote>
     * <b>注:</b> このメソッドは推奨されません。推奨されるメソッドは <code>add(int,Object)</code> です。
     *    これは 1.2 の Collections Framework で定義された <code>List</code> インタフェースを実装します。
     * </blockquote>
     * 
     * @param	obj		挿入されるオブジェクト
     * @param	index	新しいオブジェクトを挿入する位置
     * 
     * @throws	ArrayIndexOutOfBoundsException	インデックスが無効だった場合
     * @see #add(int, Object)
     */
	@Override
	public void insertElementAt(Object obj, int index) {
		super.insertElementAt(toElement(obj), index);
	}

	/**
	 * リストの最後の要素を返す。
	 * 
	 * @return	リストの最後の要素
	 * @throws	NoSuchElementException	このリストが要素を持たない場合
	 */
	@Override
	public FileListElement lastElement() {
		return (FileListElement)super.lastElement();
	}

	/**
	 * 指定されたインデックスから後向きに <code>obj</code> を検索し、検出された位置のインデックスを返す。
	 * 
	 * @param	obj		目的のオブジェクト
	 * @param	index	検索開始位置のインデックス
	 * @return			<code>index</code> よりも前の位置で、<code>obj</code> がリスト内で最後に現れるインデックス。
	 * 					オブジェクトが見つからない場合は -1
	 */
	@Override
	public int lastIndexOf(Object obj, int index) {
		return super.lastIndexOf(toElement(obj), index);
	}

	/**
	 * <code>obj</code> が最後に現れる位置のインデックスを返す。
	 * 
	 * @param	obj	目的のオブジェクト
	 * @return		<code>obj</code> がリスト内で最後に現れる位置のインデックス。
	 * 				オブジェクトが見つからない場合は -1
	 */
	@Override
	public int lastIndexOf(Object obj) {
		return super.lastIndexOf(toElement(obj));
	}

	/**
	 * リスト内の指定された位置の要素を削除し、リストから削除された要素を返す。
	 * 
	 * @param	index	削除される要素のインデックス
	 * @return			リストから削除した要素
	 * 
	 * @throws	ArrayIndexOutOfBoundsException	インデックスが範囲(<code>index &lt; 0 || index &gt;= size()</code>)外の場合
	 */
	@Override
	public FileListElement remove(int index) {
		return (FileListElement)super.remove(index);
	}

	/**
	 * リストから、最初に現れた引数（下限のインデックス）を削除する。
	 * 
	 * @param	obj	削除されるオブジェクト
	 * @return		引数がリストの要素である場合は <code>true</code>、そうでない場合は <code>false</code>
	 */
	@Override
	public boolean removeElement(Object obj) {
		return super.removeElement(toElement(obj));
	}

	/**
	 * リストの指定された位置にある要素を、指定されたオブジェクトで置き換える。
	 * 
	 * @param	index	置換される要素のインデックス
	 * @param	obj		指定された位置に格納されるオブジェクト
	 * @return			指定された位置に以前あった要素
	 * 
	 * @throws	ArrayIndexOutOfBoundsException	インデックスが範囲(<code>index &lt; 0 || index &gt;= size()</code>)外の場合
	 */
	@Override
	public FileListElement set(int index, Object obj) {
		return (FileListElement)super.set(index, toElement(obj));
	}

	/**
	 * リストの指定された位置にある要素を、指定されたオブジェクトとして設定する。
	 * その位置の従来の要素は破棄される。
	 * 
	 * <blockquote>
	 * <b>注:</b> このメソッドは推奨されません。推奨されるメソッドは <code>set(int,Object)</code> です。
	 *    これは 1.2 の Collections Framework で定義された <code>List</code> インタフェースを実装します。
	 * </blockquote>
	 * 
	 * @param	obj		要素に設定されるオブジェクト
	 * @param	index	設定する要素のインデックス
	 * 
	 * @throws	ArrayIndexOutOfBoundsException	インデックスが無効だった場合
	 * @see	#set(int, Object)
	 */
	@Override
	public void setElementAt(Object obj, int index) {
		super.setElementAt(toElement(obj), index);
	}

	/**
	 * リスト内の全ての要素が正しい順序で格納されている配列を返す。
	 * 
	 * @return	リストの要素が格納されている配列
	 */
	@Override
	public FileListElement[] toArray() {
		FileListElement[] rv = new FileListElement[this.size()];
		this.copyInto(rv);
		return rv;
	}

	/**
	 * リスト内の全ての要素から <code>File</code> オブジェクトを取り出し、
	 * リスト内の順序通りに格納されている配列を返す。
	 * 
	 * @return <code>File</code> オブジェクトの配列
	 */
	public File[] toFileArray() {
		File[] rv = new File[this.size()];
		for (int i = 0; i < this.size(); i++) {
			FileListElement elem = get(i);
			rv[i] = (elem != null ? elem.getFile() : null);
		}
		return rv;
	}

	/**
	 * リスト内の全ての要素から絶対パスを取り出し、
	 * リスト内の順序通りに格納されている配列を返す。
	 * 
	 * @return 絶対パスを表す文字列の配列
	 */
	public String[] toAbsolutePathArray() {
		String[] rv = new String[this.size()];
		for (int i = 0; i < this.size(); i++) {
			String strPath = null;
			FileListElement elem = get(i);
			if (elem != null) {
				File efile = elem.getFile();
				if (efile != null) {
					strPath = efile.getAbsolutePath();
				}
			}
			rv[i] = strPath;
		}
		return rv;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * オブジェクトを <code>{@link FileListElement}</code> に変換する。
	 * 
	 * @param	obj	変換するオブジェクト
	 * @return		生成された <code>{@link FileListElement}</code> インスタンス
	 */
	protected FileListElement toElement(Object obj) {
		if (obj == null) {
			return null;
		}
		else if (obj instanceof FileListElement) {
			return (FileListElement)obj;
		}
		else if (obj instanceof File) {
			return new FileListElement((File)obj);
		}
		else {
			return new FileListElement(obj.toString());
		}
	}
}
