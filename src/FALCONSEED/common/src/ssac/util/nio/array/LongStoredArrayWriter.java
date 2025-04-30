/*
 * @(#)LongStoredArrayWriter.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.array;

import java.io.File;
import java.io.IOException;

/**
 * Long配列を永続化するためのライタ
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public class LongStoredArrayWriter extends PrimitiveStoredArrayWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
    static protected final int DEFAULT_CACHE_SIZE = 1024;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

    protected final int _maxCacheSize;
    
    protected long[]	_cache;
    protected int		_cachedSize;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

    public LongStoredArrayWriter(File file) throws IOException
    {
        this(file, DEFAULT_CACHE_SIZE);
    }

    public LongStoredArrayWriter(File file, int maxCacheSize) throws IOException
    {
        super(file, 8 * maxCacheSize);
        this._maxCacheSize = maxCacheSize;
    }

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

    public int getCachedSize()
    {
        return _cachedSize;
    }

    public int getMaxCacheSize()
    {
        return _maxCacheSize;
    }

    public void flush() throws IOException
    {
        int i = getCachedSize();
        if(0 < i)
        {
            long al[] = getCache();
            write(al, 0, i);
            _cachedSize = 0;
        }
        writeValueCount();
    }

    public void add(long l) throws IOException
    {
        int i = getCachedSize();
        long al[] = getCache();
        al[i++] = l;
        setCachedSize(i);
        if(i == al.length)
            flush();
    }

    public void addAll(long al[], int i, int j) throws IOException
    {
        long al1[] = getCache();
        int k = j - i;
        int l = al1.length;
        do
        {
            if(0 >= k)
                break;
            int i1 = getCachedSize();
            int j1 = k;
            if(l - i1 < j1)
                j1 = l - i1;
            System.arraycopy(al, 0, al1, i1, j1);
            i1 += j1;
            k -= j1;
            setCachedSize(i1);
            if(i1 == l)
                flush();
        } while(true);
    }

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

    protected void setCachedSize(int cacheSize)
    {
        this._cachedSize = cacheSize;
    }

    protected long[] getCache()
    {
        if(_cache == null)
            _cache = new long[getMaxCacheSize()];
        return _cache;
    }
}
