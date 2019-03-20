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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)IMutableDataSeries.java	2.1.0	2013/07/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

/**
 * データ系列の 2 フィールド情報を設定可能なインタフェース。
 * 
 * @version 2.1.0	2013/07/20
 * @since 2.1.0
 */
public interface IMutableDataSeries extends IDataSeries
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 任意に指定された系列名を返す。設定されていない場合は <tt>null</tt> を返す。
	 * @return	設定されている系列名、設定されていない場合は <tt>null</tt>
	 */
	public String getLegend();

	/**
	 * 任意の系列名を設定する。設定を解除する場合は <tt>null</tt> を指定する。
	 * @param legend	任意の系列名
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setLegend(String legend);

	/**
	 * 系列の X 値とするデータ列を設定する。
	 * @param field	データ列の情報
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setXField(IDataField field);

	/**
	 * 系列の Y 値とするデータ列を設定する。
	 * @param field	データ列の情報
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setYField(IDataField field);
}
