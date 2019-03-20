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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)GenericFilterUtil.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.generic;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import ssac.aadl.fs.module.schema.SchemaConfig;
import ssac.aadl.fs.module.schema.io.SchemaConfigReader;

/**
 * 汎用フィルタ実行のための、ユーティリティ群。
 * @version 3.2.0
 * @since 3.2.0
 */
public class GenericFilterUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** JAR ファイルの拡張子 **/
	static protected final String	JAR_EXT	= ".jar";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private GenericFilterUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたファイルパスから、汎用フィルタ定義ファイルを読み込み、汎用フィルタ定義オブジェクトを生成する。
	 * <P>汎用フィルタ定義ファイルは、ファイルの拡張子が Jar の場合、Jar ファイル内の規定位置にある定義ファイルを読み込む。
	 * それ以外の場合は、定義ファイルそのものとして読み込む。
	 * @param genericFilePath	汎用フィルタ定義ファイルのパス、もしくは汎用フィルタ定義ファイルが埋め込まれた Jar ファイルのパス
	 * @return	生成された汎用フィルタ定義
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	汎用フィルタ定義ファイルが見つからない場合
	 * @throws XMLStreamException		汎用フィルタ定義ファイルの読み込みに失敗した場合
	 */
	static public SchemaConfig loadGenericSchema(String genericFilePath) throws FileNotFoundException, XMLStreamException
	{
		// 現時点では、XML ファイルを直接指定する。
		return loadFromXmlFile(new File(genericFilePath));
//		// 拡張子の判定
//		boolean isJarFile = false;
//		if (genericFilePath.length() > JAR_EXT.length()) {
//			String strext = genericFilePath.substring(genericFilePath.length() - JAR_EXT.length());
//			if (JAR_EXT.equalsIgnoreCase(strext)) {
//				// jar 拡張子
//				isJarFile = true;
//			}
//		}
//		
//		// ファイル種別に応じた読み込み
//		if (isJarFile) {
//			// load from jar
//			return loadFromJarFile(new File(genericFilePath));
//		} else {
//			// load by file
//			return loadFromXmlFile(new File(genericFilePath));
//		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたファイルを、汎用フィルタ定義が埋め込まれた jar ファイルとして読み込む。
	 * @param jarfile	jar ファイルのパス
	 * @return	汎用フィルタ定義オブジェクト
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	汎用フィルタ定義ファイルが見つからない場合
	 * @throws XMLStreamException		汎用フィルタ定義ファイルの読み込みに失敗した場合
	 */
	static protected SchemaConfig loadFromJarFile(File jarfile) {
		return null;
	}

	/**
	 * 指定されたファイルを、汎用フィルタ定義が記述された XML ファイルとして読み込む。
	 * @param xmlfile	XML ファイルのパス
	 * @return	汎用フィルタ定義オブジェクト
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	汎用フィルタ定義ファイルが見つからない場合
	 * @throws XMLStreamException		汎用フィルタ定義ファイルの読み込みに失敗した場合
	 */
	static protected SchemaConfig loadFromXmlFile(File xmlfile) throws FileNotFoundException, XMLStreamException
	{
		SchemaConfigReader reader = null;
		try {
			// open stream
			reader = new SchemaConfigReader(xmlfile);
			
			// load stream
			return reader.readConfig();
		}
		finally {
			if (reader != null) {
				reader.close();
				reader = null;
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
