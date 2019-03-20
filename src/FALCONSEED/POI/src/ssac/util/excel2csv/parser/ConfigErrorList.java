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
 * @(#)ConfigErrorList.java	3.3.0	2016/05/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.parser;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.poi.ss.usermodel.Cell;

import ssac.util.excel2csv.poi.CellPosition;

/**
 * <code>[Excel to CSV]</code> 変換定義のエラー詳細のリスト。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class ConfigErrorList extends ArrayList<ConfigErrorDetail>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -1281089481858051268L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** エラー許容上限数 **/
	private final int	_limitErrors;
	/** エラー数 **/
	private int		_numErrors;
	/** 警告数 **/
	private int		_numWarns;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ConfigErrorList() {
		this(0);
	}
	
	public ConfigErrorList(int errorlimit) {
		super();
		_limitErrors = (errorlimit < 0 ? 0 : errorlimit);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getErrorLimit() {
		return _limitErrors;
	}
	
	public int getErrorCount() {
		return _numErrors;
	}
	
	public int getWarnCount() {
		return _numWarns;
	}
	
	public void appendError(Cell cell, String message) {
		appendErrorDetail(new ConfigErrorDetail(false, cell, message));
	}
	
	public void appendError(CellPosition cellpos, String message) {
		appendErrorDetail(new ConfigErrorDetail(false, cellpos, message));
	}
	
	public void appendError(String sheetname, int rowIndex, int colIndex, String message) {
		appendErrorDetail(new ConfigErrorDetail(false, sheetname, rowIndex, colIndex, message));
	}
	
	public void appendWarn(Cell cell, String message) {
		appendErrorDetail(new ConfigErrorDetail(true, cell, message));
	}
	
	public void appendWarn(CellPosition cellpos, String message) {
		appendErrorDetail(new ConfigErrorDetail(true, cellpos, message));
	}
	
	public void appendWarn(String sheetname, int rowIndex, int colIndex, String message) {
		appendErrorDetail(new ConfigErrorDetail(true, sheetname, rowIndex, colIndex, message));
	}
	
	public void appendErrorDetail(ConfigErrorDetail detail) {
		add(detail);
		if (!detail.isWarn()) {
			if (_limitErrors > 0 && _numErrors >= _limitErrors) {
				throw new ConfigTooManyErrorsException();
			}
		}
	}

	@Override
	public void clear() {
		super.clear();
		_numErrors = 0;
		_numWarns  = 0;
	}

	@Override
	public ConfigErrorDetail remove(int index) {
		ConfigErrorDetail removed = super.remove(index);
		if (removed != null) {
			decrementCountOfLevel(removed.isWarn());
		}
		return removed;
	}

	@Override
	public boolean remove(Object o) {
		boolean removed = super.remove(o);
		if (removed) {
			decrementCountOfLevel(((ConfigErrorDetail)o).isWarn());
		}
		return removed;
	}

	@Override
	public ConfigErrorDetail set(int index, ConfigErrorDetail elem) {
		incrementCountOfLevel(elem.isWarn());
		ConfigErrorDetail olddetail = super.set(index, elem);
		decrementCountOfLevel(olddetail.isWarn());
		return olddetail;
	}

	@Override
	public boolean add(ConfigErrorDetail elem) {
		incrementCountOfLevel(elem.isWarn());
		return super.add(elem);
	}

	@Override
	public void add(int index, ConfigErrorDetail elem) {
		incrementCountOfLevel(elem.isWarn());
		super.add(index, elem);
	}

	@Override
	public boolean addAll(Collection<? extends ConfigErrorDetail> c) {
		for (ConfigErrorDetail detail : c) {
			add(detail);
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends ConfigErrorDetail> c)
	{
		for (ConfigErrorDetail detail : c) {
			incrementCountOfLevel(detail.isWarn());
		}
		return super.addAll(index, c);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected int incrementCountOfLevel(boolean warn)
	{
		if (warn) {
			return (++_numWarns);
		} else {
			return (++_numErrors);
		}
	}
	
	protected int decrementCountOfLevel(boolean warn) {
		if (warn) {
			if (_numWarns > 0)
				--_numWarns;
			return _numWarns;
		} else {
			if (_numErrors > 0)
				--_numErrors;
			return _numErrors;
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
