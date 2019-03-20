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
