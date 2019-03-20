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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)EtcConfigDataSet.java	3.3.0	2016/05/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv;

import java.util.ArrayList;
import java.util.Collection;

import ssac.util.excel2csv.command.CmdCsvFileNode;

/**
 * <code>[Excel to CSV]</code> における変換定義と出力設定のデータセット。
 * このオブジェクトは、Excel ファイルの変換定義におけるすべての CSV 変換出力設定を保持する。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcConfigDataSet extends ArrayList<EtcConfigCsvData>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -4658503989240606113L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean add(CmdCsvFileNode cmdnode) {
		return add(new EtcConfigCsvData(cmdnode));
	}

	//------------------------------------------------------------
	// Override ArrayList interfaces
	//------------------------------------------------------------

	@Override
	public EtcConfigCsvData set(int index, EtcConfigCsvData elem) {
		if (elem == null)
			throw new NullPointerException();
		return super.set(index, elem);
	}

	@Override
	public boolean add(EtcConfigCsvData elem) {
		if (elem == null)
			throw new NullPointerException();
		return super.add(elem);
	}

	@Override
	public void add(int index, EtcConfigCsvData elem) {
		if (elem == null)
			throw new NullPointerException();
		super.add(index, elem);
	}

	@Override
	public boolean addAll(Collection<? extends EtcConfigCsvData> c) {
		if (!c.isEmpty()) {
			for (EtcConfigCsvData node : c) {
				if (node == null) {
					throw new IllegalArgumentException("The collection has null element.");
				}
			}
		}
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends EtcConfigCsvData> c) {
		if (!c.isEmpty()) {
			for (EtcConfigCsvData node : c) {
				if (node == null) {
					throw new IllegalArgumentException("The collection has null element.");
				}
			}
		}
		return super.addAll(index, c);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
