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
 */
/*
 * @(#)ExBalanceTable.java	0.1.0	2013/05/31
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.basicfilter.exalge;

/**
 * 交換代数標準形とCSVファイルの相互変換において参照される、借方および貸方科目を定義するテーブル。
 * このテーブルは、CSVファイルにより入力されるものであり、科目名と借方および貸方の対応を保持するマップである。
 * <p>
 * <b>《入力フォーマット》</b>
 * <br>
 * <code>ExBalanceTable</code> は、CSV ファイル形式の入力インタフェースを提供する。<br>
 * CSVファイルは、カンマ区切りのテキストファイルで、次のフォーマットに従う。
 * なお、科目名が重複している場合、より終端に近いものが優先される。
 * <ul>
 * <li>行の先頭から、次のようなカラム構成となる。<br>
 * <i>科目名</i>，<i>貸借属性</i>，<i>単位キー</i>，<i>主体キー</i>
 * <li>文字コードは、実行するプラットフォームの処理系に依存する。
 * <li>空行は無視される。
 * <li>行先頭文字が <code>'#'</code> で始まる行は、コメント行とみなす。
 * <li>行先頭文字が <code>'!#'</code> の場合、<code>'#'</code> 文字とする。
 * <li>行先頭文字が <code>'#'</code> もしくは <code>'!#'</code> ではない場合、文字そのものとみなす。
 * <li>貸借属性が <code>'借方'</code> もしくは <code>'debit'</code> の場合、借方属性となる。
 * <li>貸借属性が <code>'貸方'</code> もしくは <code>'credit'</code> の場合、貸方属性となる。
 * <li>貸借属性が省略（空欄）の場合、借方属性とみなす。
 * <li>単位キーには、この科目名で生成する交換代数基底の単位キーを指定する。省略可能。
 * <li>主体キーには、この科目名で生成する交換代数基底の主体キーを指定する。省略可能。
 * </ul>
 * また、CSV ファイルの読み込みにおいて、次の場合に例外がスローされる。
 * <ul>
 * <li>科目名が空欄の場合
 * <li>貸借属性が空欄ではなく、<code>'借方'</code>, <code>'debit'</code>, <code>'貸方'</code>, <code>'credit'</code> 以外の文字列が指定された場合
 * </ul>
 * 
 * @version 0.1.0	2013/05/31
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class ExBalanceTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static public final int BALANCE_UNKNOWN	= 0;
	static public final int	BALANCE_DEBIT	= 1;
	static public final int BALANCE_CREDIT	= 2;
	
	static public final String[] DEBIT_NAMES = {
		"debit", "借方", "借",
	};
	
	static public final String[] CREDIT_NAMES = {
		"credit", "貸方", "貸",
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final java.util.HashMap<String, ExBalanceElement>	_map;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ExBalanceTable() {
		_map = new java.util.HashMap<String, ExBalanceElement>();
	}

	//------------------------------------------------------------
	// Public Helper
	//------------------------------------------------------------
	
	static public final int getBalanceTypeByName(String typename) {
		if (typename != null) {
			typename = typename.toLowerCase();
			
			// debit side
			for (String dname : DEBIT_NAMES) {
				if (dname.equals(typename)) {
					return BALANCE_DEBIT;
				}
			}
			
			// credit side
			for (String cname : CREDIT_NAMES) {
				if (cname.equals(typename)) {
					return BALANCE_CREDIT;
				}
			}
		}
		
		return BALANCE_UNKNOWN;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return _map.isEmpty();
	}
	
	public int size() {
		return _map.size();
	}
	
	public void clear() {
		_map.clear();
	}
	
	public boolean containsName(Object name) {
		return _map.containsKey(name);
	}
	
	public ExBalanceElement get(Object name) {
		return _map.get(name);
	}
	
	public boolean isDebitSide(Object name) {
		ExBalanceElement elem = _map.get(name);
		if (elem == null)
			return false;
		else
			return elem.isDebitSide();
	}
	
	public boolean isCreditSide(Object name) {
		ExBalanceElement elem = _map.get(name);
		if (elem == null)
			return false;
		else
			return elem.isCreditSide();
	}
	
	public boolean isEmptyUnitKey(Object name) {
		ExBalanceElement elem = _map.get(name);
		if (elem == null)
			return false;
		else
			return elem.isEmptyUnitKey();
	}
	
	public boolean isEmptySubjectKey(Object name) {
		ExBalanceElement elem = _map.get(name);
		if (elem == null)
			return false;
		else
			return elem.isEmptySubjectKey();
	}
	
	public int getType(Object name) {
		ExBalanceElement elem = _map.get(name);
		if (elem == null)
			return BALANCE_UNKNOWN;
		else
			return elem.getType();
	}
	
	public String getUnitKey(Object name) {
		ExBalanceElement elem = _map.get(name);
		if (elem == null)
			return null;
		else
			return elem.getUnitKey();
	}
	
	public String getSubjectKey(Object name) {
		ExBalanceElement elem = _map.get(name);
		if (elem == null)
			return null;
		else
			return elem.getSubjectKey();
	}
	
	public ExBalanceElement put(String name, int type) {
		return put(name, type, null, null);
	}
	
	public ExBalanceElement put(String name, int type, String unitKey, String subjectKey) {
		if (name == null || name.length() <= 0)
			throw new IllegalArgumentException("'name' is null or empty.");
		if (type != BALANCE_DEBIT && type != BALANCE_CREDIT)
			throw new IllegalArgumentException("'type' is unknown type : " + type);
		
		ExBalanceElement elem = new ExBalanceElement(type, unitKey, subjectKey);
		return _map.put(name, elem);
	}
	
	public ExBalanceElement remove(Object name) {
		return _map.remove(name);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static class ExBalanceElement {
		private final int		_type;
		private final String	_unitKey;
		private final String	_subjectKey;
		
		public ExBalanceElement(int type) {
			this(type, null, null);
		}
		
		public ExBalanceElement(int type, String unitKey, String subjectKey) {
			_type = type;
			_unitKey = unitKey;
			_subjectKey = subjectKey;
		}
		
		public boolean isDebitSide() {
			return (_type != BALANCE_CREDIT);
		}
		
		public boolean isCreditSide() {
			return (_type == BALANCE_CREDIT);
		}
		
		public boolean isEmptyUnitKey() {
			return (_unitKey == null || _unitKey.length() <= 0);
		}
		
		public boolean isEmptySubjectKey() {
			return (_subjectKey == null || _subjectKey.length() <= 0);
		}
		
		public int getType() {
			return _type;
		}
		
		public String getUnitKey() {
			return _unitKey;
		}
		
		public String getSubjectKey() {
			return _subjectKey;
		}
	}
}
