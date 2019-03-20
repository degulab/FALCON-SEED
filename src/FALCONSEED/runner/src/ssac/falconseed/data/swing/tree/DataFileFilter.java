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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)DataFileFilter.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.data.swing.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;

/**
 * データファイル表示用ファイルフィルタ
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class DataFileFilter implements VirtualFileFilter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final boolean DEFAULT_IGNORECASE_FLAG = true;
	
	static protected final String[] defaultExcludeFiles = null;
	
	static protected final String[] defaultIncludeFiles = {"*"};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	protected boolean	_showHiddenFiles;
	protected List<NamePattern>	_excludePatterns;
	protected List<NamePattern>	_includePatterns;
	
	protected Map<String,NamePattern>	_mapExcludePatterns;
	protected Map<String,NamePattern>	_mapIncludePatterns;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DataFileFilter() {
		this(false,
				(defaultIncludeFiles==null ? null : Arrays.asList(defaultIncludeFiles)),
				(defaultExcludeFiles==null ? null : Arrays.asList(defaultExcludeFiles)));
	}
	
	public DataFileFilter(boolean showHiddenFiles, Collection<? extends String> includePatterns, Collection<? extends String> excludePatterns)
	{
		this._showHiddenFiles = showHiddenFiles;
		this._mapExcludePatterns = createPatternMap(excludePatterns);
		this._mapIncludePatterns = createPatternMap(includePatterns);
		updateIncludePatterns();
		updateExcludePatterns();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void clearIncludePatterns() {
		this._mapIncludePatterns = null;
		updateIncludePatterns();
	}
	
	public void clearExcludePatterns() {
		this._mapExcludePatterns = null;
		updateExcludePatterns();
	}
	
	public boolean isEmptyIncludePatterns() {
		return (_mapIncludePatterns == null ? false : _mapIncludePatterns.isEmpty());
	}
	
	public boolean isEmptyExcludePatterns() {
		return (_mapExcludePatterns == null ? false : _mapExcludePatterns.isEmpty());
	}
	
	public boolean containsIncludePattern(String pattern) {
		return (_mapIncludePatterns == null ? false : _mapIncludePatterns.containsKey(pattern));
	}
	
	public boolean containsExcludePattern(String pattern) {
		return (_mapExcludePatterns == null ? false : _mapExcludePatterns.containsKey(pattern));
	}
	
	public boolean addIncludePattern(String pattern) {
		if (pattern ==null || pattern.length() <= 0)
			return false;

		boolean ret = false;
		
		if (_mapIncludePatterns == null) {
			_mapIncludePatterns = createPatternMap(Arrays.asList(pattern));
			updateIncludePatterns();
			ret = true;
		} else {
			if (putPattern(_mapIncludePatterns, pattern)) {
				updateIncludePatterns();
				ret = true;
			}
		}
		
		return ret;
	}
	
	public boolean addExcludePattern(String pattern) {
		if (pattern ==null || pattern.length() <= 0)
			return false;

		boolean ret = false;
		
		if (_mapExcludePatterns == null) {
			_mapExcludePatterns = createPatternMap(Arrays.asList(pattern));
			updateExcludePatterns();
			ret = true;
		} else {
			if (putPattern(_mapExcludePatterns, pattern)) {
				updateExcludePatterns();
				ret = true;
			}
		}
		
		return ret;
	}

	public boolean accept(VirtualFile pathname) {
		// 隠しファイルは表示しない
		if (!_showHiddenFiles && pathname.isHidden()) {
			return false;
		}
		
		// 名前のパターンチェック
		String fname = pathname.getName();
		
		// 含めるパターンは表示
		if (!_includePatterns.isEmpty()) {
			for (NamePattern np : _includePatterns) {
				if (np.matches(fname)) {
					return true;
				}
			}
		}
		
		// 除外するパターンなら非表示
		if (!_excludePatterns.isEmpty()) {
			for (NamePattern np : _excludePatterns) {
				if (np.matches(fname)) {
					return false;
				}
			}
		}
		
		// 表示許可する
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean putPattern(Map<String,NamePattern> map, String pattern) {
		if (map.containsKey(pattern)) {
			return false;
		}
		
		NamePattern np = NamePattern.parsePattern(DEFAULT_IGNORECASE_FLAG, pattern);
		map.put(pattern, np);
		return true;
	}
	
	protected void updateIncludePatterns() {
		if (_mapIncludePatterns != null && !_mapIncludePatterns.isEmpty()) {
			if (_includePatterns == null) {
				_includePatterns = new ArrayList<NamePattern>(_mapIncludePatterns.values());
			} else {
				_includePatterns.clear();
				_includePatterns.addAll(_mapIncludePatterns.values());
			}
		}
		else if (_includePatterns == null) {
			_includePatterns = new ArrayList<NamePattern>(0);
		}
		else {
			_includePatterns.clear();
		}
	}
	
	protected void updateExcludePatterns() {
		if (_mapExcludePatterns != null && !_mapExcludePatterns.isEmpty()) {
			if (_excludePatterns == null) {
				_excludePatterns = new ArrayList<NamePattern>(_mapExcludePatterns.values());
			} else {
				_excludePatterns.clear();
				_excludePatterns.addAll(_mapExcludePatterns.values());
			}
		}
		else if (_excludePatterns == null) {
			_excludePatterns = new ArrayList<NamePattern>(0);
		}
		else {
			_excludePatterns.clear();
		}
	}
	
	static protected Map<String,NamePattern> createPatternMap(Collection<? extends String> patterns)
	{
		Map<String,NamePattern> ret = null;
		if (patterns != null && !patterns.isEmpty()) {
			Map<String,NamePattern> map = new HashMap<String, NamePattern>();
			for (String pattern : patterns) {
				if (pattern != null && pattern.length() > 0) {
					NamePattern np = NamePattern.parsePattern(DEFAULT_IGNORECASE_FLAG, pattern);
					map.put(pattern, np);
				}
			}
			if (!map.isEmpty()) {
				ret = map;
			}
		}
		return ret;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class NamePattern
	{
		protected final String	_fixed;
		protected final String	_starts;
		protected final String	_ends;
		protected final String[]	_patterns;
		protected final boolean	_ignoreCase;
		protected final boolean	_onlyWildcard;
		
		static public NamePattern parsePattern(boolean ignoreCase, String pattern) {
			// ワイルドカードを検索
			int firstWildcardPos = pattern.indexOf('*');
			if (firstWildcardPos < 0) {
				// 固定パターン
				if (ignoreCase) {
					pattern = pattern.toLowerCase();
				}
				return new NamePattern(ignoreCase, pattern, null, null, null);
			}
			
			// 終端のワイルドカードを検索
			int lastWildcardPos = pattern.lastIndexOf('*');
			
			// 先頭固定文字列を保存
			String startsPattern;
			if (firstWildcardPos > 0) {
				startsPattern = pattern.substring(0, firstWildcardPos);
				if (ignoreCase) {
					startsPattern = startsPattern.toLowerCase();
				}
			} else {
				startsPattern = null;
			}
			
			// 終端固定文字列を保存
			String endsPattern;
			if (lastWildcardPos >= 0 && lastWildcardPos < (pattern.length()-1)) {
				endsPattern = pattern.substring(lastWildcardPos+1);
				if (ignoreCase) {
					endsPattern = endsPattern.toLowerCase();
				}
			} else {
				endsPattern = null;
			}
			
			// 中間固定文字列を保存
			String[] midPatterns = null;
			if ((lastWildcardPos - firstWildcardPos) > 1) {
				String mid = pattern.substring(firstWildcardPos+1, lastWildcardPos);
				StringTokenizer st = new StringTokenizer(mid, "*", false);
				int num = st.countTokens();
				if (num > 0) {
					String[] tokens = new String[num];
					int i = 0;
					if (ignoreCase) {
						while (st.hasMoreTokens()) {
							tokens[i++] = st.nextToken().toLowerCase();
						}
					} else {
						while (st.hasMoreTokens()) {
							tokens[i++] = st.nextToken();
						}
					}
					midPatterns = tokens;
				}
			}
			
			// パターン生成
			if (startsPattern==null && endsPattern == null && midPatterns==null) {
				//--- only wildcard
				return new NamePattern(false, null, null, null, null);
			} else {
				//--- exist patterns
				return new NamePattern(ignoreCase, null, startsPattern, endsPattern, midPatterns);
			}
		}
		
		protected NamePattern(boolean ignoreCase, String fixedPattern, String starts, String ends, String[] patterns) {
			this._ignoreCase = ignoreCase;
			this._fixed      = fixedPattern;
			this._starts     = starts;
			this._ends       = ends;
			this._patterns   = patterns;
			if (fixedPattern==null && starts==null && ends==null && patterns==null) {
				this._onlyWildcard = true;
			} else {
				this._onlyWildcard = false;
			}
		}
		
		public boolean isWildcardOnly() {
			return _onlyWildcard;
		}
		
		public boolean matches(String str) {
			// wildcard only
			if (_onlyWildcard) {
				//--- all matched
				return true;
			}
			
			// change lower case
			if (_ignoreCase) {
				str = str.toLowerCase();
			}
			
			// matches fixed
			if (_fixed != null) {
				return _fixed.equals(str);
			}
			
			// matches starts
			if (_starts != null) {
				if (!str.startsWith(_starts)) {
					//--- not matched
					return false;
				}
				str = str.substring(_starts.length());
			}
			
			// matches ends
			if (_ends != null) {
				if (!str.endsWith(_ends)) {
					//--- not matched
					return false;
				}
				str = str.substring(0, (str.length() - _ends.length()));
			}
			
			// matches middle patterns
			if (_patterns != null) {
				int findPos = 0;
				for (String mid : _patterns) {
					findPos = str.indexOf(mid, findPos);
					if (findPos < 0) {
						//--- not matched
						return false;
					}
					findPos += mid.length();
				}
			}
			
			// all patterns matched
			return true;
		}
	}
}
