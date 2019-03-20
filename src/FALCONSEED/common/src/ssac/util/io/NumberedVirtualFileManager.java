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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)NumberedVirtualFileManager.java	2.0.0	2012/11/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 正のシーケンス番号(1～maxValue)が終端に付加されたファイル名を管理するクラス。
 * ファイル名の大文字小文字は区別されない。
 * 
 * @version 2.0.0	2012/11/05
 * @since 2.0.0
 */
public class NumberedVirtualFileManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final VirtualFile	_parentDir;
	private final String		_prefix;
	private final String		_suffix;
	private final int			_maxValue;
	
	private List<Integer>	_freeSeqRanges = Collections.emptyList();
	private int	_curRangeIndex = -1;
	private int	_nextFreeNumber = -1;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで新しいインスタンスを生成する。
	 * このコンストラクタでは、<code>maxValue</code> には <code>Integer.MAX_VALUE</code> が設定される。
	 * <em>parentDir</em> には存在しない抽象パスも指定可能であり、存在しない場合にはすべてのシーケンス番号が利用可能となる。
	 * <em>parentDir</em> が存在しているとき、それがディレクトリではない場合は例外をスローする。
	 * @param parentDir		対象ファイルが格納される親ディレクトリ
	 * @param prefix		番号を付加するときのプレフィックス
	 * @param suffix		番号の後に付加するサフィックス、サフィックスを使用しない場合は <tt>null</tt> もしくは空文字列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>parentDir</em> が存在するときディレクトリではない場合、
	 * 										もしくは <em>prefix</em> が 1 文字以上の文字列ではない場合、
	 */
	public NumberedVirtualFileManager(VirtualFile parentDir, String prefix, String suffix) {
		this(parentDir, Integer.MAX_VALUE, prefix, suffix);
	}

	/**
	 * 指定されたパラメータで新しいインスタンスを生成する。
	 * <em>parentDir</em> には存在しない抽象パスも指定可能であり、存在しない場合にはすべてのシーケンス番号が利用可能となる。
	 * <em>parentDir</em> が存在しているとき、それがディレクトリではない場合は例外をスローする。
	 * @param parentDir		対象ファイルが格納される親ディレクトリ
	 * @param maxValue		上限となる番号を指定する。1 より小さい値の場合は例外をスローする。
	 * @param prefix		番号を付加するときのプレフィックス
	 * @param suffix		番号の後に付加するサフィックス、サフィックスを使用しない場合は <tt>null</tt> もしくは空文字列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>parentDir</em> が存在するときディレクトリではない場合、
	 * 										もしくは <em>prefix</em> が 1 文字以上の文字列ではない場合、
	 * 										もしくは <em>maxValue</em> が 1 ～ Integer.MAX_VALUE の範囲外の場合
	 */
	public NumberedVirtualFileManager(VirtualFile parentDir, int maxValue, String prefix, String suffix) {
		// check
		if (parentDir.exists() && !parentDir.isDirectory())
			throw new IllegalArgumentException("Parent directory is not directory.");
		if (maxValue < 1 || maxValue > Integer.MAX_VALUE)
			throw new IllegalArgumentException("The maxValue out of range : " + maxValue);
		if (prefix.length() < 1)
			throw new IllegalArgumentException("The prefix length less than 1.");
		
		// set
		this._parentDir = parentDir;
		this._maxValue  = maxValue;
		this._prefix    = prefix;
		this._suffix    = (suffix!=null && suffix.length()>0 ? suffix : null);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean updateFreeNumbers() {
		_freeSeqRanges = parseDirectory(_parentDir, _maxValue, _prefix, _suffix);
		rewind();
		return (!_freeSeqRanges.isEmpty());
	}
	
	public void rewind() {
		if (_freeSeqRanges.isEmpty()) {
			_curRangeIndex = -1;
			_nextFreeNumber = -1;
		}
		else {
			_curRangeIndex = 0;
			_nextFreeNumber = _freeSeqRanges.get(0);
		}
	}
	
	public VirtualFile getParentDirectory() {
		return _parentDir;
	}
	
	public int getMaxValue() {
		return _maxValue;
	}
	
	public String getPrefix() {
		return _prefix;
	}
	
	public String getSuffix() {
		return _suffix;
	}
	
	public boolean hasFreeNumber() {
		return (!_freeSeqRanges.isEmpty());
	}
	
	public boolean hasNextNumber() {
		return (_nextFreeNumber >= 1);
	}
	
	public VirtualFile nextNumberedFile() {
		if (!hasNextNumber())
			throw new NoSuchElementException("No more free sequence number.");

		VirtualFile vfFile;
		if (_suffix == null) {
			vfFile = _parentDir.getChildFile(_prefix.concat(String.valueOf(_nextFreeNumber)));
		} else {
			vfFile = _parentDir.getChildFile(_prefix.concat(String.valueOf(_nextFreeNumber)).concat(_suffix));
		}
		
		// update next
		int nextSeqNo = _nextFreeNumber + 1;
		if (nextSeqNo > _freeSeqRanges.get(_curRangeIndex+1)) {
			int nextIndex = _curRangeIndex + 2;
			if (nextIndex < _freeSeqRanges.size()) {
				nextSeqNo = _freeSeqRanges.get(nextIndex);
			} else {
				nextSeqNo = -1;
				nextIndex = -1;
			}
			_curRangeIndex = nextIndex;
		}
		_nextFreeNumber = nextSeqNo;
		
		return vfFile;
	}

	/**
	 * <em>targetFiles</em> にあるすべてのファイルを、空き番号が付加されたファイル下に配置する。
	 * このメソッドでは、ファイル名が(大文字小文字を無視して)同一の空き番号ファイル下にならないよう、
	 * 新しい空き番号ファイルに配置する。
	 * <p><em>targetFiles</em> に含まれるファイルと配置先の情報は、<em>filemap</em> に追加される。
	 * @param filemap		配置元と配置先のマップを格納するオブジェクト
	 * @param targetFiles	配置元ファイルの集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws NoSuchElementException	空き番号が不足した場合
	 */
	public void assignFreeNumberedFile(final Map<VirtualFile,VirtualFile> filemap, final Collection<VirtualFile> targetFiles) {
		ArrayList<VirtualFile> freePaths = new ArrayList<VirtualFile>(targetFiles.size());
		HashSet<VirtualFile> destPaths = new HashSet<VirtualFile>();

		for (VirtualFile vfFile : targetFiles) {
			boolean assigned = false;
			String name = vfFile.getName();
			for (VirtualFile vfBase : freePaths) {
				VirtualFile vfDestPath = vfBase.getChildFile(name);
				if (!destPaths.contains(vfDestPath)) {
					filemap.put(vfFile, vfDestPath);
					destPaths.add(vfDestPath);
					assigned = true;
					break;
				}
			}
			if (!assigned) {
				VirtualFile vfBase = nextNumberedFile();
				VirtualFile vfDestPath = vfBase.getChildFile(name);
				filemap.put(vfFile, vfDestPath);
				destPaths.add(vfDestPath);
				freePaths.add(vfBase);
			}
		}
		
		freePaths.clear();
		destPaths.clear();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected List<Integer> parseDirectory(final VirtualFile vfDir, int maxval, String prefix, String suffix) {
		String[] namelist = vfDir.list();
		if (namelist == null || namelist.length <= 0) {
			// no files
			List<Integer> ret = new ArrayList<Integer>(2);
			ret.add(1);
			ret.add(maxval);
			return ret;
		}

		prefix = prefix.toLowerCase();
		suffix = (suffix==null ? null : suffix.toLowerCase());
		int prefixLen = prefix.length();
		int suffixLen = (suffix==null ? 0 : suffix.length());
		ArrayList<Integer> existSeqList = new ArrayList<Integer>(namelist.length);
		for (String name : namelist) {
			int seqno = getSequenceNumber(name, prefix, prefixLen, suffix, suffixLen);
			if (seqno > 0) {
				existSeqList.add(seqno);
			}
		}
		if (existSeqList.isEmpty()) {
			// no sequencial file
			List<Integer> ret = new ArrayList<Integer>(2);
			ret.add(1);
			ret.add(maxval);
			return ret;
		}
		
		// make sequence number ranges
		Collections.sort(existSeqList);
		ArrayList<Integer> rangelist = new ArrayList<Integer>(existSeqList.size()+2);
		int nextSeqNo = 1;
		for (int seqno : existSeqList) {
			if (seqno > nextSeqNo) {
				rangelist.add(nextSeqNo);
				rangelist.add(seqno-1);
			}
			nextSeqNo = seqno + 1;
		}
		if (nextSeqNo <= maxval) {
			rangelist.add(nextSeqNo);
			rangelist.add(maxval);
		}
		
		// completed
		rangelist.trimToSize();
		return rangelist;
	}
	
	protected int getSequenceNumber(String filename, final String prefix, int prefixLen, final String suffix, int suffixLen) {
		if (filename.length() <= (prefixLen + suffixLen))
			return (-1);	// no sequencial file
		
		filename = filename.toLowerCase();
		if (!filename.startsWith(prefix))
			return (-1);	// no this sequencial file
		if (suffix != null && !filename.endsWith(suffix))
			return (-1);	// no this sequencial file
		String strNumber = filename.substring(prefixLen, filename.length()-suffixLen);
		int seqno;
		try {
			seqno = Integer.parseInt(strNumber);
		} catch (Throwable ignoreEx) {
			ignoreEx = null;
			seqno = (-1);
		}
		return seqno;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
