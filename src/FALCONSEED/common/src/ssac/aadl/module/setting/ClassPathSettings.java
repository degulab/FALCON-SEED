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
 * @(#)ClassPathSettings.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ClassPathSettings.java	1.10	2009/01/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ClassPathSettings.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ssac.aadl.module.setting.ModuleSettings;
import ssac.util.swing.list.FileListModel;

/**
 * クラス・パス設定情報を保持するクラス。
 * ビルドオプションダイアログのクラス・パス・パネルにより設定される情報を
 * 操作するための機能を提供する。
 * 
 * @version 1.14	2009/12/09
 */
public class ClassPathSettings extends ModuleSettings
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final String SECTION = "ClassPath";
	
	static public final String KEY_CLASSPATHS_NUM  = SECTION + ".num";
	static public final String KEY_CLASSPATHS_PATH = SECTION + ".path";
	
	static private final String OLDKEY_CLASSPATHS = ".paths";
	static private final String KEY_CLASSPATHS = SECTION + ".paths";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ArrayList<File> classPaths = new ArrayList<File>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ClassPathSettings() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * キャッシュされているクラスパスのリストを返す。
	 * @return 抽象パスのリスト
	 * @since 1.14
	 */
	public List<File> getClassPathList() {
		return classPaths;
	}
	
	/**
	 * キャッシュされているクラスパスの配列を返す。
	 * このメソッドが返すクラスパスは、絶対パスを表すファイルとなる。
	 * @return 絶対パスを表すファイルの配列を返す。存在しない場合は要素を持たない配列を返す。
	 */
	public File[] getClassPathFiles() {
		if (classPaths.isEmpty()) {
			return EMPTY_JAVAFILE_ARRAY;
		} else {
			return classPaths.toArray(new File[classPaths.size()]);
		}
	}

	/**
	 * キャッシュされているクラスパスの配列を返す。
	 * このメソッドが返すクラスパスは、絶対パスを表す文字列となる。
	 * @return 絶対パスでのパス文字列の配列。<tt>null</tt> は返さない。
	 */
	public String[] getClassPathStrings() {
		if (classPaths.isEmpty()) {
			return EMPTY_STRING_ARRAY;
		} else {
			String[] paths = new String[classPaths.size()];
			for (int i = 0; i < classPaths.size(); i++) {
				paths[i] = classPaths.get(i).getPath();
			}
			return paths;
		}
	}

	/**
	 * キャッシュされているクラスパス(絶対パス)を格納する、
	 * <code>FileListModel</code> オブジェクトを返す。
	 * @return <code>FileListModel</code> オブジェクト
	 */
	public FileListModel getClassPathListModel() {
		FileListModel list = new FileListModel();
		if (!classPaths.isEmpty()) {
			for (File f : classPaths) {
				list.addElement(f);
			}
		}
		return list;
	}

	/**
	 * 指定したファイルの配列を新しいクラスパスとしてキャッシュする。
	 * このメソッドでは、絶対パスを表すファイルに変換する。
	 * @param files	新しいクラスパスとするファイルの配列
	 * @throws NullPointerException	配列の要素が <tt>null</tt> の場合
	 */
	public void setClassPathFiles(File[] files) {
		classPaths.clear();
		if (files != null && files.length > 0) {
			for (File f : files) {
				classPaths.add(f);
			}
		}
	}

	/*
	 * 指定したパス文字列の配列を新しいクラスパスとしてキャッシュする。
	 * ここで指定するパス文字列は絶対パスであることを前提とする。
	 * @param paths パス文字列の配列
	 * @throws IllegalArgumentException パス文字列に絶対パスではないものが含まれている場合
	 *
	public void setClassPathStrings(String[] paths) {
		if (paths != null && paths.length > 0) {
			classPaths = new File[paths.length];
			for (int i = 0; i < paths.length; i++) {
				String path = (paths[i]==null ? "" : paths[i]);
				File file = new File(path);
				if (!file.isAbsolute()) {
					throw new IllegalArgumentException("Path is not absolute : \"" + file.getPath() + "\"");
				}
				classPaths[i] = file;
			}
		}
		else {
			classPaths = EMPTY_FILE_ARRAY;
		}
	}
	*/

	/**
	 * 指定したファイルリストを新しいクラスパスとしてキャッシュする。
	 * ここで指定するパス文字列は絶対パスであることを前提とする。
	 * @param list	ファイルリスト
	 * @throws IllegalArgumentException パス文字列に絶対パスではないものが含まれている場合
	 */
	public void setClassPathList(FileListModel list) {
		if (list != null && !list.isEmpty()) {
			setClassPathFiles(list.toFileArray());
		} else {
			classPaths.clear();
		}
	}

	//------------------------------------------------------------
	// Implement AbstractSettings interfaces
	//------------------------------------------------------------

	@Override
	public void commit() throws IOException {
		// 古いプロパティは削除
		props.clearProperty(OLDKEY_CLASSPATHS);
		props.clearProperty(KEY_CLASSPATHS);
		
		// クラスパスを相対パスに変換し、プロパティに保存
		Integer numPaths = props.getInteger(KEY_CLASSPATHS_NUM, Integer.valueOf(0));
		int index = 0;
		if (!classPaths.isEmpty()) {
			int len = classPaths.size();
			for (; index < len; index++) {
				setAbsoluteJavaFileProperty(getClassPathKey(index), classPaths.get(index));
			}
		}
		//--- 余分なプロパティは削除
		for (; index < numPaths.intValue(); index++) {
			props.clearProperty(getClassPathKey(index));
		}
		//--- クラスパス総数を保存
		props.setInteger(KEY_CLASSPATHS_NUM, classPaths.size());
		
		// プロパティの保存
		super.commit();
	}

	@Override
	public void rollback() {
		// プロパティの読み込み
		super.rollback();
		
		// プロパティからクラスパスを取得する
		//--- プロパティのクラスパスは、相対パスで保存されている場合がある
		//--- 相対パスの場合は、このプロパティファイルからの相対とする
		classPaths.clear();
		if (props.containsKey(KEY_CLASSPATHS_NUM)) {
			Integer numPaths = props.getInteger(KEY_CLASSPATHS_NUM, Integer.valueOf(0));
			for (int i = 0; i < numPaths.intValue(); i++) {
				File path = getAbsoluteJavaFileProperty(getClassPathKey(i), null);
				if (path != null) {
					classPaths.add(path);
				}
			}
		}
		else if (props.containsKey(KEY_CLASSPATHS)) {
			File[] files = getAbsoluteJavaFilesProperty(KEY_CLASSPATHS, EMPTY_JAVAFILE_ARRAY);
			setClassPathFiles(files);
		}
		else {
			File[] files = getAbsoluteJavaFilesProperty(OLDKEY_CLASSPATHS, EMPTY_JAVAFILE_ARRAY);
			setClassPathFiles(files);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private final String getClassPathKey(int index) {
		return KEY_CLASSPATHS_PATH + Integer.toString(index+1);
	}
}
