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
 * @(#)ProjectSettings.java	1.14	2009/12/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import ssac.util.io.VirtualFile;

/**
 * プロジェクト設定情報を保持するクラス。
 * 
 * @version 1.14	2009/12/16
 * @since 1.14
 */
public class ProjectSettings extends PackageSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String SECTION = "project";
	static public final String KEY_FILES = SECTION + ".files";
	static public final String KEY_FILES_NUM  = KEY_FILES + ".num";
	static public final String KEY_FILES_PATH = KEY_FILES + ".path";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** プロジェクトから除外されるファイルのセット **/
	private final HashSet<VirtualFile>	projExclusiveFiles = new HashSet<VirtualFile>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ProjectSettings() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * プロジェクト除外ファイルセットが空であれば <tt>true</tt> を返す。
	 */
	public boolean isEmptyExclusiveProjectFiles() {
		return projExclusiveFiles.isEmpty();
	}

	/**
	 * 除外されるプロジェクトファイル数を返す。
	 */
	public int getNumExclusiveProjectFiles() {
		return projExclusiveFiles.size();
	}

	/**
	 * 登録されているプロジェクトファイルのうち、存在しないもののみを除外する。
	 * このメソッドは、プロパティファイルそのものは変更せず、あくまでも
	 * このオブジェクトが保持するキャッシュのみを変更する。
	 * @return	プロジェクトファイルセットが変更された場合に <tt>true</tt> を返す。
	 */
	public boolean cleanupExclusiveProjectFiles() {
		boolean ret = false;
		Iterator<VirtualFile> it = projExclusiveFiles.iterator();
		while (it.hasNext()) {
			VirtualFile f = it.next();
			if (!f.exists()) {
				it.remove();
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * 抽象パスの変更を除外ファイルセットに反映する。
	 * このメソッドは <em>oldFile</em> から <em>newFile</em> への変更を反映するため、
	 * 除外ファイルセットの該当するものを新しい抽象パスに変更する。
	 * <em>newFile</em> がディレクトリの場合は、そのディレクトリの下位抽象パスを
	 * 新しい抽象パスに変更する。
	 * @param oldFile	変更前の抽象パス
	 * @param newFile	変更後の抽象パス
	 * @return	プロジェクト除外ファイルセットが変更された場合に <tt>true</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean renameProjectFiles(VirtualFile oldFile, VirtualFile newFile) {
		if (oldFile == null)
			throw new NullPointerException("'oldFile' argument is null.");
		if (newFile == null)
			throw new NullPointerException("'newFile' argument is null.");
		if (projExclusiveFiles.isEmpty())
			return false;
	
		boolean modified = false;
		VirtualFile[] exclusives = projExclusiveFiles.toArray(new VirtualFile[projExclusiveFiles.size()]);
		for (VirtualFile exclusive : exclusives) {
			if (oldFile.equals(exclusive)) {
				projExclusiveFiles.remove(exclusive);
				projExclusiveFiles.add(newFile);
				modified = true;
			}
			else if (exclusive.isDescendingFrom(oldFile)) {
				String relpath = exclusive.relativePathFrom(oldFile);
				VirtualFile newExclusive = newFile.getChildFile(relpath);
				projExclusiveFiles.remove(exclusive);
				projExclusiveFiles.add(newExclusive);
				modified = true;
			}
		}
		
		return modified;
	}

	/**
	 * 指定されたファイル自身もしくはその親がプロジェクト除外ファイルとして登録されている
	 * 場合に <tt>true</tt> を返す。親のパースは <em>projRoot</em> で指定されたパスまでとする。
	 * ただし、<em>projRoot</em> は除外されているかどうかは確認しない。したがって、
	 * <em>file</em> と <em>projRoot</em> が同じ位置を示す抽象パスの場合、<em>file</em> が
	 * プロジェクト除外ファイルとして登録されている場合でも必ず <tt>false</tt> を返す。
	 * もし <em>projRoot</em> が <em>file</em> の子である場合、このメソッドは <em>projRoot</em> を
	 * 上限とはみなさない。
	 * @param file	判定する抽象パス
	 * @param projRoot	親のパースの上限とする抽象パス。このパスは判定の対象とはならない。
	 * @return	自身もしくはその親がプロジェクト除外ファイルとして登録済みであれば <tt>true</tt> を返す。
	 */
	public boolean containsAncestorExclusiveProjectFile(VirtualFile file, VirtualFile projRoot) {
		VirtualFile ancestor = file;
		while (ancestor != null && !ancestor.equals(projRoot)) {
			if (projExclusiveFiles.contains(ancestor)) {
				// ancestor found in exclusive set
				return true;
			}
			ancestor = ancestor.getParentFile();
		}
		return false;
	}

	/**
	 * 指定されたファイルがプロジェクト除外ファイルとして登録されている場合に <tt>true</tt> を返す。
	 */
	public boolean containsExclusiveProjectFile(VirtualFile file) {
		return projExclusiveFiles.contains(file);
	}

	/**
	 * 指定されたファイルのコレクションに含まれる要素すべてが、
	 * プロジェクト除外ファイルである場合に <tt>true</tt> を返す。
	 */
	public boolean containsAllExclusiveProjectFiles(Collection<VirtualFile> c) {
		return projExclusiveFiles.containsAll(c);
	}

	/**
	 * 指定されたファイルをプロジェクト除外ファイルとする。
	 * @param file	プロジェクトから除外するファイル
	 * @return	プロジェクトファイルセットが変更された場合に <tt>true</tt> を返す
	 * @throws IllegalArgumentException	<code>file</code> が <tt>null</tt> の場合
	 */
	public boolean addExclusiveProjectFile(VirtualFile file) {
		if (file == null)
			throw new IllegalArgumentException("file argument is null.");
		return projExclusiveFiles.add(file);
	}

	/**
	 * 指定されたディレクトリをプロジェクト除外ファイルセットに追加する。
	 * 除外ファイルセットにディレクトリを追加したときは、その下位にあるすべての
	 * ファイルは除外セットから削除する。
	 * @param dir	除外セットに追加するディレクトリ
	 * @return	プロジェクトファイルセットが変更された場合に <tt>true</tt> を返す。
	 * @throws IllegalArgumentException	<code>dir</code> が <tt>null</tt> の場合
	 */
	public boolean addExclusiveProjectDirectory(VirtualFile dir) {
		if (dir==null)
			throw new IllegalArgumentException("'dir' argument is null.");
		boolean modified = false;
		Iterator<VirtualFile> it = projExclusiveFiles.iterator();
		while (it.hasNext()) {
			VirtualFile exclusive = it.next();
			if (exclusive.isDescendingFrom(dir) && !exclusive.equals(dir)) {
				it.remove();
				modified = true;
			}
		}
		if (projExclusiveFiles.add(dir)) {
			modified = true;
		}
		return modified;
	}

	/**
	 * 指定されたコレクションに含まれるすべてのファイルを、
	 * プロジェクトから除外する。
	 * @param c	プロジェクトから除外するファイルのコレクション
	 * @return	プロジェクトファイルセットが変更された場合に <tt>true</tt> を返す。
	 * @throws IllegalArgumentException	コレクションに含まれるファイルが <tt>null</tt> の場合
	 */
	public boolean addAllExclusiveProjectFiles(Collection<VirtualFile> c) {
		boolean modified = false;
		for (VirtualFile f : c) {
			if (f == null)
				throw new IllegalArgumentException("collection includes null.");
			if (projExclusiveFiles.add(f))
				modified = true;
		}
		return modified;
	}

	/**
	 * 指定されたファイルをプロジェクト除外ファイルセットから削除する。
	 * @param file	削除するファイル
	 * @return	プロジェクトファイルセットが変更された場合に <tt>true</tt> を返す。
	 */
	public boolean removeExclusiveProjectFile(VirtualFile file) {
		return projExclusiveFiles.remove(file);
	}

	/**
	 * 指定されたディレクトリ以下のファイルをプロジェクト除外ファイルセットから削除する。
	 * @param file	削除するディレクトリ
	 * @return	プロジェクトファイルセットが変更された場合に <tt>true</tt> を返す。
	 */
	public boolean removeExclusiveProjectDirectory(VirtualFile file) {
		boolean modified = false;
		Iterator<VirtualFile> it = projExclusiveFiles.iterator();
		while (it.hasNext()) {
			VirtualFile exclusive = it.next();
			if (exclusive.isDescendingFrom(file)) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * 指定されたコレクションに含まれるすべてのファイルを、
	 * プロジェクトファイルセットから削除する。
	 * @param c	削除するファイルのコレクション
	 * @return	プロジェクトファイルセットが変更された場合に <tt>true</tt> を返す。
	 */
	public boolean removeAllExclusiveProjectFiles(Collection<VirtualFile> c) {
		return projExclusiveFiles.removeAll(c);
	}

	/**
	 * 指定されたコレクションに含まれないファイルのみを、
	 * プロジェクトファイルセットから削除する。
	 * @param c	プロジェクトファイルセットに残すファイルのコレクション
	 * @return	プロジェクトファイルセットが変更された場合に <tt>true</tt> を返す。
	 */
	public boolean retainAllExclusiveProjectFiles(Collection<VirtualFile> c) {
		return projExclusiveFiles.retainAll(c);
	}

	/**
	 * プロジェクトファイルセットのすべてのファイルを削除する。
	 */
	public void removeAllExclusiveProjectFiles() {
		projExclusiveFiles.clear();
	}

	//------------------------------------------------------------
	// Implement AbstractSettings interfaces
	//------------------------------------------------------------

	@Override
	public void commit() throws IOException {
		// ファイルパスを相対パスに変換し、プロパティに保存
		Integer numPaths = props.getInteger(KEY_FILES_NUM, Integer.valueOf(0));
		int index = 0;
		if (!projExclusiveFiles.isEmpty()) {
			for (VirtualFile f : projExclusiveFiles) {
				setAbsoluteVirtualFileProperty(getFilePathKey(index), f);
				index++;
			}
		}
		//--- 余分なプロパティは削除
		for (; index < numPaths.intValue(); index++) {
			props.clearProperty(getFilePathKey(index));
		}
		//--- クラスパス総数を保存
		props.setInteger(KEY_FILES_NUM, projExclusiveFiles.size());
		
		// プロパティの保存
		super.commit();
	}

	@Override
	public void rollback() {
		// プロパティの読み込み
		super.rollback();
		
		// プロパティからファイルパスを取得する
		//--- プロパティのファイルパスは基本的に相対パスで保存されている
		//--- 相対パスの場合は、このプロパティファイルからの相対とする
		projExclusiveFiles.clear();
		Integer numPaths = props.getInteger(KEY_FILES_NUM, null);
		if (numPaths != null) {
			for (int i = 0; i < numPaths.intValue(); i++) {
				VirtualFile file = getAbsoluteVirtualFileProperty(getFilePathKey(i), null);
				if (file != null) {
					projExclusiveFiles.add(file);
				}
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private final String getFilePathKey(int index) {
		return KEY_FILES_PATH + Integer.toString(index+1);
	}
}
