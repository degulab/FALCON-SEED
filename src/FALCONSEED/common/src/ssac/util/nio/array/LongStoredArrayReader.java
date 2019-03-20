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
 * @(#)LongStoredArrayReader.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.array;

import java.io.File;
import java.io.IOException;

/**
 * 永続化されたLong配列のリーダー
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public class LongStoredArrayReader extends PrimitiveStoredArrayReader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 標準のキャッシュ容量 **/
	static protected final int DEFAULT_CACHE_SIZE = 1024;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final int	_maxCacheSize;

	protected long		_cache[];
	protected long		_cachedIndex;
	protected int		_cachedSize;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public LongStoredArrayReader(File file) throws IOException
	{
		this(file, DEFAULT_CACHE_SIZE);
	}

	public LongStoredArrayReader(File file, int maxCacheSize) throws IOException
	{
		super(file, 2048);
		this._maxCacheSize = maxCacheSize;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public int getMaxCacheSize()
	{
		return _maxCacheSize;
	}

	public long size()
	{
		return getValueCount();
	}

	public long get(long l) throws IOException
	{
		if(l < 0L || size() <= l)
		{
			String s = "Out of bounds : " + 0L + " <= " + l + " < " + size();
			throw new IndexOutOfBoundsException(s);
		} else
		{
			return doGet(l);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected long getCachedIndex()
	{
		return _cachedIndex;
	}

	protected void setCachedIndex(long l)
	{
		_cachedIndex = l;
	}

	protected int getCachedSize()
	{
		return _cachedSize;
	}

	protected void setCachedSize(int i)
	{
		_cachedSize = i;
	}

	protected long[] getCache()
	{
		if(_cache == null)
			_cache = new long[getMaxCacheSize()];
		return _cache;
	}

	protected long doGet(long l) throws IOException
	{
		long l1 = getCachedIndex();
		int i = getCachedSize();
		long al[] = getCache();
		long l2 = l - l1;
		if(0L <= l2 && l2 < (long)i)
		{
			return al[(int)l2];
		} else
		{
			int j = (int)(size() - l);
			j = Math.min(j, getMaxCacheSize());
			read(al, 0, j, l);
			setCachedIndex(l);
			setCachedSize(j);
			return al[0];
		}
	}
}
