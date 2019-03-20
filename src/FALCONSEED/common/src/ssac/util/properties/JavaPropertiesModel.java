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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)JavaPropertiesModel.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JavaPropertiesModel.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import ssac.util.io.Files;

/**
 * <code>java.util.Properties</code> の実装となる、プロパティ・データモデル。
 * ストリーム入出力には <code>java.util.Properties</code> の標準的な実装を利用している。
 * 
 * @version 1.17	2010/11/19
 */
public class JavaPropertiesModel extends Properties implements IExPropertyModel
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
	 * デフォルト値を持たない、空のプロパティモデルを生成する。
	 */
	public JavaPropertiesModel() {
		super();
	}

	/**
	 * 指定されたデフォルト値を持つ、空のプロパティモデルを生成する。
	 * @param defaults	デフォルト値
	 */
	public JavaPropertiesModel(Properties defaults) {
		super(defaults);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// implement IExPropertyModel interfaces
	//------------------------------------------------------------

	/**
	 * 全てのプロパティを除去する。
	 */
	public void clear() {
		super.clear();
	}

	/**
	 * 指定されたキー（および対応する値）を除去する。
	 * @param key	キー
	 */
	public void clearProperty(String key) {
		super.remove(key);
	}

	/**
	 * 指定されたキーが存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @param key	キー
	 */
	public boolean containsKey(String key) {
		return super.containsKey(key);
	}

	/*
	 * 指定されたキーが値を持つ場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @param key	キー
	 *
	public boolean hasValue(String key) {
		return (super.getProperty(key) != null);
	}

	/*
	 * 指定されたキーに対応する値が空文字列の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 * @param key	キー
	 *
	public boolean isEmpty(String key) {
		String strValue = super.getProperty(key);
		return (strValue != null && strValue.length() <= 0);
	}
	*/

	/**
	 * 指定されたキーに対応する値を取得する。
	 * @param key	キー
	 * @return	キーに対応する値
	 */
	public String getValue(String key) {
		return super.getProperty(key);
	}

	/**
	 * 指定されたキーに、指定された値を設定する。
	 * @param key	キー
	 * @param value	値
	 * @throws NullPointerException	<em>key</em> もしくは <em>value</em> が <tt>null</tt> の場合
	 */
	public void setValue(String key, String value) {
		super.setProperty(key, value);
	}

	/**
	 * ストリームからロード可能なモデルであることを示す。
	 * @return	常に <tt>true</tt>
	 */
	public boolean canLoadFromStream() {
		return true;
	}

	/**
	 * ストリームへ保存可能なモデルであることを示す。
	 * @return	常に <tt>true</tt>
	 */
	public boolean canSaveToStream() {
		return true;
	}

	/**
	 * 指定されたファイルからプロパティを読み込む。
	 * @param file	読み込むファイル
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void loadFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		try {
			loadFromStream(fis);
		}
		finally {
			Files.closeStream(fis);
		}
	}

	/**
	 * 指定されたストリームからプロパティを読み込む。
	 * @param stream	読み込むストリーム
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void loadFromStream(InputStream stream) throws IOException {
		super.load(stream);
	}

	/**
	 * 指定されたファイルへ、現在のプロパティを全て書き込む。
	 * @param file	書き込み先のファイル
	 * @param comments	コメント
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void saveFile(File file, String comments) throws IOException {
		// Make directories
		File dirPath = file.getParentFile();
		Files.ensureDirectory(dirPath);
		
		// create FileOutputStream
		FileOutputStream fos = new FileOutputStream(file);
		
		// save to stream
		try {
			saveToStream(fos, comments);
		}
		finally {
			Files.closeStream(fos);
		}
	}

	/**
	 * 指定されたストリームへ、現在のプロパティを全て書き込む。
	 * @param stream	書き込み先のストリーム
	 * @param comments	コメント
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void saveToStream(OutputStream stream, String comments) throws IOException {
		super.store(stream, comments);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
