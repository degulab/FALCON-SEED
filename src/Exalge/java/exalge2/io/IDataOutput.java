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
 *  Copyright 2007-2014  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)IDataOutput.java	0.984	2014/04/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IDataOutput.java	0.93	2007/09/03
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;

import exalge2.io.xml.XmlDocument;

/**
 * データ出力インタフェースを提供する。
 * <p>
 * データ出力インタフェースでは、次の出力先を規定している。
 * <ul>
 * <li>CSV ファイル
 * <li>XML ファイル
 * <li>XML ドキュメント({@link exalge2.io.xml.XmlDocument})
 * </ul>
 * ファイルフォーマット、XML ドキュメントの形式については、
 * 各実装クラスにて規定されているものとする。
 * 
 * 
 * @version 0.984 2014/04/28
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.93
 *
 */
public interface IDataOutput {
	
	/**
	 * オブジェクトの内容を、指定のファイルに CSV フォーマットで出力する。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 */
	public void toCSV(File csvFile) throws IOException, FileNotFoundException;
	
	/**
	 * オブジェクトの内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 */
	public void toCSV(File csvFile, String charsetName) throws IOException, FileNotFoundException, UnsupportedEncodingException;

	/**
	 * オブジェクトの内容を、CSVフォーマットの文字列に変換する。
	 * 
	 * @return	CSVフォーマットの文字列
	 * @since 0.984
	 */
	public String toCsvString();
	
	/**
	 * オブジェクトの内容を、指定のファイルに XML フォーマットで出力する。
	 * 
	 * @param xmlFile 出力先ファイル
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 * @throws TransformerConfigurationException <code>Transformer</code> インスタンスを生成できない場合
	 * @throws TransformerException ファイルへの変換中に回復不能なエラーが発生した場合
	 * 
	 */
	public void toXML(File xmlFile) throws FactoryConfigurationError, ParserConfigurationException,
											 DOMException,
											 TransformerConfigurationException, TransformerException;
	
	/**
	 * オブジェクトの内容から、XML ドキュメントを生成する。
	 * 
	 * @return 生成された XML ドキュメント({@link exalge2.io.xml.XmlDocument} インスタンス)
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 * 
	 */
	public XmlDocument toXML() throws FactoryConfigurationError, ParserConfigurationException, DOMException;
	
	/**
	 * オブジェクトの内容を、XMLフォーマットの文字列に変換する。
	 * 
	 * @return XMLフォーマットの文字列
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 * 
	 * @since 0.984
	 */
	public String toXmlString() throws FactoryConfigurationError, ParserConfigurationException, DOMException,
										TransformerConfigurationException, TransformerException;

}
