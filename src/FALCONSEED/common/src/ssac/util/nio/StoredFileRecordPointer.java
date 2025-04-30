/*
 * @(#)StoredFileRecordPointer.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.File;
import java.io.IOException;

import ssac.util.nio.array.LongStoredArrayReader;

/**
 * 永続化された、レコードインデックスに対応するファイルポインタ。
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public class StoredFileRecordPointer extends LongStoredArrayReader
implements IFileRecordPointer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public StoredFileRecordPointer(File file) throws IOException
	{
		super(file);
	}
	
	public StoredFileRecordPointer(File file, int maxCacheSize) throws IOException
	{
		super(file, maxCacheSize);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public long getRecordSize() {
		return super.getValueCount();
	}
	
	public long getBegin(long line) throws IOException
	{
		return this.get(2 * line);
	}
	
	public long getEnd(long line) throws IOException
	{
		return this.get(2 * line + 1);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
