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
 *  Copyright 2007-2008  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)FileConverter.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileConverter.java	0.93	2007/09/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileConverter.java	0.92	2007/08/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileConverter.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import exalge2.ExAlgeSet;
import exalge2.ExBasePatternSet;
import exalge2.ExBaseSet;
import exalge2.ExTransfer;
import exalge2.TransMatrix;
import exalge2.TransTable;
import exalge2.io.FileUtil;
import exalge2.io.IDataOutput;
import exalge2.io.UnsupportedFormatException;
import exalge2.io.csv.CsvFormatException;
import exalge2.io.xml.XmlDocument;

/**
 * ファイルフォーマット変換ユーティリティ
 * 
 * @version 0.982	2009/09/13
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.91
 */
public class FileConverter {
	
	static private final Class inputClasses[] = new Class[]{
		ExTransfer.class,
		ExAlgeSet.class,
		ExBaseSet.class,
		TransTable.class,
		TransMatrix.class,
		ExBasePatternSet.class,
	};
	
	/**
	 * 指定された CSV ファイルを読み込み、CSV ファイルフォーマットに応じたオブジェクトの
	 * インスタンスを返す。
	 * <br>指定された CSV ファイルのフォーマットを自動判別し、フォーマットに適した
	 * オブジェクトのインスタンスを生成する。生成されるオブジェクトは、次の通り。
	 * <ul>
	 * <li>{@link exalge2.ExAlgeSet} - 交換代数集合
	 * <li>{@link exalge2.ExBaseSet} - 基底集合
	 * <li>{@link exalge2.ExBasePatternSet} - 基底パターン集合
	 * <li>{@link exalge2.TransTable} - 振替変換テーブル
	 * <li>{@link exalge2.TransMatrix} - 按分変換テーブル
	 * <li>{@link exalge2.ExTransfer} - 変換テーブル
	 * </ul>
	 * <p>
	 * フォーマットに適したオブジェクトが存在しない場合は、
	 * {@link UnsupportedFormatException} 例外をスローする。
	 * {@link UnsupportedFormatException} には、この例外の原因が含まれる場合がある。
	 * その場合、{@link UnsupportedFormatException#getCause()} で取得できる。
	 * 
	 * @param csvFile CSV ファイル
	 * 
	 * @return 生成されたオブジェクトのインスタンス
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * 
	 * @since 0.92
	 */
	static public Object fromCSV(File csvFile)
		throws FileNotFoundException, IOException, UnsupportedFormatException
	{
		return FileUtil.newObjectFromCSV(inputClasses, csvFile);
	}
	
	/**
	 * 指定された文字セットで指定された CSV ファイルを読み込み、
	 * CSV ファイルフォーマットに応じたオブジェクトのインスタンスを返す。
	 * <br>指定された CSV ファイルのフォーマットを自動判別し、フォーマットに適した
	 * オブジェクトのインスタンスを生成する。生成されるオブジェクトは、次の通り。
	 * <ul>
	 * <li>{@link exalge2.ExAlgeSet} - 交換代数集合
	 * <li>{@link exalge2.ExBaseSet} - 基底集合
	 * <li>{@link exalge2.ExBasePatternSet} - 基底パターン集合
	 * <li>{@link exalge2.TransTable} - 振替変換テーブル
	 * <li>{@link exalge2.TransMatrix} - 按分変換テーブル
	 * <li>{@link exalge2.ExTransfer} - 変換テーブル
	 * </ul>
	 * <p>
	 * フォーマットに適したオブジェクトが存在しない場合は、
	 * {@link UnsupportedFormatException} 例外をスローする。
	 * {@link UnsupportedFormatException} には、この例外の原因が含まれる場合がある。
	 * その場合、{@link UnsupportedFormatException#getCause()} で取得できる。
	 * 
	 * @param csvFile CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return 生成されたオブジェクトのインスタンス。生成できない場合は <tt>null</tt>
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * 
	 * @since 0.93
	 */
	static public Object fromCSV(File csvFile, String charsetName)
		throws FileNotFoundException, IOException, UnsupportedEncodingException, UnsupportedFormatException
	{
		return FileUtil.newObjectFromCSV(inputClasses, csvFile, charsetName);
	}
	
	/**
	 * 指定された XML ファイルを読み込み、XML ファイルフォーマットに応じたオブジェクトの
	 * インスタンスを返す。
	 * <br>指定された XML ファイルのフォーマットを自動判別し、フォーマットに適した
	 * オブジェクトのインスタンスを生成する。生成されるオブジェクトは、次の通り。
	 * <ul>
	 * <li>{@link exalge2.ExAlgeSet} - 交換代数集合
	 * <li>{@link exalge2.ExBaseSet} - 基底集合
	 * <li>{@link exalge2.ExBasePatternSet} - 基底パターン集合
	 * <li>{@link exalge2.TransTable} - 振替変換テーブル
	 * <li>{@link exalge2.TransMatrix} - 按分変換テーブル
	 * <li>{@link exalge2.ExTransfer} - 変換テーブル
	 * </ul>
	 * <p>
	 * フォーマットに適したオブジェクトが存在しない場合は、
	 * {@link UnsupportedFormatException} 例外をスローする。
	 * {@link UnsupportedFormatException} には、この例外の原因が含まれる場合がある。
	 * その場合、{@link UnsupportedFormatException#getCause()} で取得できる。
	 * 
	 * @param xmlFile XML ファイル
	 * 
	 * @return 生成されたオブジェクトのインスタンス
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * 
	 * @since 0.92
	 */
	static public Object fromXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException,
				IOException, UnsupportedFormatException
	{
		return FileUtil.newObjectFromXMLFile(inputClasses, xmlFile);
	}
	
	/**
	 * 指定された XML ドキュメントを読み込み、XML フォーマットに応じたオブジェクトの
	 * インスタンスを返す。
	 * <br>指定された XML ドキュメントの形式を自動判別し、フォーマットに適した
	 * オブジェクトのインスタンスを生成する。生成されるオブジェクトは、次の通り。
	 * <ul>
	 * <li>{@link exalge2.ExAlgeSet} - 交換代数集合
	 * <li>{@link exalge2.ExBaseSet} - 基底集合
	 * <li>{@link exalge2.ExBasePatternSet} - 基底パターン集合
	 * <li>{@link exalge2.TransTable} - 振替変換テーブル
	 * <li>{@link exalge2.TransMatrix} - 按分変換テーブル
	 * <li>{@link exalge2.ExTransfer} - 変換テーブル
	 * </ul>
	 * <p>
	 * フォーマットに適したオブジェクトが存在しない場合は、
	 * {@link UnsupportedFormatException} 例外をスローする。
	 * {@link UnsupportedFormatException} には、この例外の原因が含まれる場合がある。
	 * その場合、{@link UnsupportedFormatException#getCause()} で取得できる。
	 * 
	 * @param doc XML ドキュメント
	 * 
	 * @return 生成されたオブジェクトのインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws UnsupportedFormatException XML ドキュメントのフォーマットがサポートされていない場合
	 * 
	 * @since 0.92
	 */
	static public Object fromXML(Document doc)
		throws UnsupportedFormatException
	{
		// XML Document の読み込み
		XmlDocument xmldoc = new XmlDocument(doc);
		return FileUtil.newObjectFromXmlDocument(inputClasses, xmldoc);
	}

	/**
	 * 指定された CSV ファイルを、指定された出力先ファイルへ、XML ファイルフォーマットで出力する。
	 * <br>指定された CSV ファイルのフォーマットを自動判別し、同じフォーマットの XML ファイルへ
	 * 変換する。
	 * 
	 * @param csvFile 変換元となる CSV ファイル
	 * @param xmlFile 出力先 XML ファイル
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 * @throws TransformerConfigurationException <code>Transformer</code> インスタンスを生成できない場合
	 * @throws TransformerException ファイルへの変換中に回復不能なエラーが発生した場合
	 */
	static public void convertCSV2XML(File csvFile, File xmlFile)
		throws FileNotFoundException, IOException, UnsupportedFormatException,
				FactoryConfigurationError, ParserConfigurationException,
				DOMException,
				TransformerConfigurationException, TransformerException
	{
		Object ret = FileUtil.newObjectFromCSV(inputClasses, csvFile);
		
		IDataOutput data = null;
		try {
			data = (IDataOutput)ret;
		}
		catch (ClassCastException ex) {
			throw new UnsupportedFormatException(ex);
		}
		
		data.toXML(xmlFile);
	}

	/**
	 * 指定された文字セットの CSV ファイルを、指定された出力先ファイルへ、XML ファイルフォーマットで出力する。
	 * <br>指定された CSV ファイルのフォーマットを自動判別し、同じフォーマットの XML ファイルへ
	 * 変換する。
	 * <br>このメソッドは、CSV ファイルの読み込みに、指定された文字セットを使用する。
	 * 
	 * @param csvFile 変換元となる CSV ファイル
	 * @param charsetName CSV ファイル読み込みでサポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * @param xmlFile 出力先 XML ファイル
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws CsvFormatException CSV ファイルのフォーマットが正しくない場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 * @throws TransformerConfigurationException <code>Transformer</code> インスタンスを生成できない場合
	 * @throws TransformerException ファイルへの変換中に回復不能なエラーが発生した場合
	 * 
	 * @since 0.93
	 */
	static public void convertCSV2XML(File csvFile, String charsetName, File xmlFile)
		throws FileNotFoundException, IOException, UnsupportedEncodingException, UnsupportedFormatException,
				FactoryConfigurationError, ParserConfigurationException,
				DOMException,
				TransformerConfigurationException, TransformerException
	{
		Object ret = FileUtil.newObjectFromCSV(inputClasses, csvFile, charsetName);
		
		IDataOutput data = null;
		try {
			data = (IDataOutput)ret;
		}
		catch (ClassCastException ex) {
			throw new UnsupportedFormatException(ex);
		}
		
		data.toXML(xmlFile);
	}

	/**
	 * 指定された XML ファイルを、指定された出力先ファイルへ、CSV ファイルのフォーマットで出力する。
	 * <br>指定された XML ファイルのフォーマットを自動判別し、同じフォーマットの CSV ファイルへ
	 * 変換する。
	 * 
	 * @param xmlFile 変換元となる XML ファイル
	 * @param csvFile 出力先 CSV ファイル
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * 
	 */
	static public void convertXML2CSV(File xmlFile, File csvFile)
		throws UnsupportedFormatException, FileNotFoundException, IOException,
				FactoryConfigurationError, ParserConfigurationException
	{
		Object ret = FileUtil.newObjectFromXMLFile(inputClasses, xmlFile);
		
		IDataOutput data = null;
		try {
			data = (IDataOutput)ret;
		}
		catch (ClassCastException ex) {
			throw new UnsupportedFormatException(ex);
		}
		
		data.toCSV(csvFile);
	}

	/**
	 * 指定された XML ファイルを、指定された出力先ファイルへ、
	 * 指定された文字セットの CSV ファイルのフォーマットで出力する。
	 * <br>指定された XML ファイルのフォーマットを自動判別し、同じフォーマットの CSV ファイルへ
	 * 変換する。
	 * <br>このメソッドは、CSV ファイルの書き込みに、指定された文字セットを使用する。
	 * 
	 * @param xmlFile 変換元となる XML ファイル
	 * @param csvFile 出力先 CSV ファイル
	 * @param charsetName CSV ファイル出力でサポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.93
	 */
	static public void convertXML2CSV(File xmlFile, File csvFile, String charsetName)
		throws UnsupportedFormatException, FileNotFoundException, IOException, UnsupportedEncodingException,
				FactoryConfigurationError, ParserConfigurationException
	{
		Object ret = FileUtil.newObjectFromXMLFile(inputClasses, xmlFile);
		
		IDataOutput data = null;
		try {
			data = (IDataOutput)ret;
		}
		catch (ClassCastException ex) {
			throw new UnsupportedFormatException(ex);
		}
		
		data.toCSV(csvFile, charsetName);
	}

	/**
	 * 指定された CSV ファイルを、XML ドキュメントへ変換する。
	 * <br>
	 * 指定された CSV ファイルのフォーマットを自動判別し、同じ形式の XML ドキュメントを
	 * 生成する。
	 * 
	 * @param csvFile 変換元となる CSV ファイル
	 * 
	 * @return 変換した XML ドキュメント
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 */
	static public Document convertCSV2XML(File csvFile)
		throws FileNotFoundException, IOException, UnsupportedFormatException,
				FactoryConfigurationError, ParserConfigurationException, DOMException
	{
		Object ret = FileUtil.newObjectFromCSV(inputClasses, csvFile);
		
		IDataOutput data = null;
		try {
			data = (IDataOutput)ret;
		}
		catch (ClassCastException ex) {
			throw new UnsupportedFormatException(ex);
		}
		
		XmlDocument xmldoc = data.toXML();
		return xmldoc.getDocument();
	}

	/**
	 * 指定された文字セットの CSV ファイルを、XML ドキュメントへ変換する。
	 * <br>
	 * 指定された CSV ファイルのフォーマットを自動判別し、同じ形式の XML ドキュメントを
	 * 生成する。
	 * <br>このメソッドは、CSV ファイルの読み込みに、指定された文字セットを使用する。
	 * 
	 * @param csvFile 変換元となる CSV ファイル
	 * @param charsetName CSV ファイル読み込みでサポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return 変換した XML ドキュメント
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws UnsupportedFormatException ファイルのフォーマットがサポートされていない場合
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 * 
	 * @since 0.93
	 */
	static public Document convertCSV2XML(File csvFile, String charsetName)
		throws FileNotFoundException, IOException, UnsupportedFormatException, UnsupportedEncodingException,
				FactoryConfigurationError, ParserConfigurationException, DOMException
	{
		Object ret = FileUtil.newObjectFromCSV(inputClasses, csvFile, charsetName);

		IDataOutput data = null;
		try {
			data = (IDataOutput)ret;
		}
		catch (ClassCastException ex) {
			throw new UnsupportedFormatException(ex);
		}

		XmlDocument xmldoc = data.toXML();
		return xmldoc.getDocument();
	}

	/**
	 * 指定された XML ドキュメントを、指定された出力先ファイルへ、CSV ファイルのフォーマットで出力する。
	 * <br>指定された XML ドキュメントの形式を自動判別し、同じフォーマットの CSV ファイルへ変換する。
	 * 
	 * @param doc 変換元となる XML ドキュメント
	 * @param csvFile 出力先 CSV ファイル
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws UnsupportedFormatException XML ドキュメントのフォーマットがサポートされていない場合
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	static public void convertXML2CSV(Document doc, File csvFile)
		throws FileNotFoundException, IOException, UnsupportedFormatException
	{
		// XML Document の読み込み
		XmlDocument xmldoc = new XmlDocument(doc);
		Object ret = FileUtil.newObjectFromXmlDocument(inputClasses, xmldoc);
		
		IDataOutput data = null;
		try {
			data = (IDataOutput)ret;
		}
		catch (ClassCastException ex) {
			throw new UnsupportedFormatException(ex);
		}
		
		data.toCSV(csvFile);
	}

	/**
	 * 指定された XML ドキュメントを、指定された出力先ファイルへ、
	 * 指定された文字セットの CSV ファイルのフォーマットで出力する。
	 * <br>指定された XML ドキュメントの形式を自動判別し、同じフォーマットの CSV ファイルへ変換する。
	 * <br>このメソッドは、CSV ファイルの書き込みに、指定された文字セットを使用する。
	 * 
	 * @param doc 変換元となる XML ドキュメント
	 * @param csvFile 出力先 CSV ファイル
	 * @param charsetName CSV ファイル出力でサポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws UnsupportedFormatException XML ドキュメントのフォーマットがサポートされていない場合
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.93
	 */
	static public void convertXML2CSV(Document doc, File csvFile, String charsetName)
		throws FileNotFoundException, IOException, UnsupportedEncodingException, UnsupportedFormatException
	{
		// XML Document の読み込み
		XmlDocument xmldoc = new XmlDocument(doc);
		Object ret = FileUtil.newObjectFromXmlDocument(inputClasses, xmldoc);

		IDataOutput data = null;
		try {
			data = (IDataOutput)ret;
		}
		catch (ClassCastException ex) {
			throw new UnsupportedFormatException(ex);
		}

		data.toCSV(csvFile, charsetName);
	}
}
