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
 * @(#)PrimitiveStoredArrayReader.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.array;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import ssac.util.nio.ICloseable;


/**
 * プリミティブ配列を永続化するためのライタ
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public class PrimitiveStoredArrayWriter extends StoredArrayHeader implements ICloseable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 標準のバッファ容量となるバイト数 **/
    static protected final int DEFAULT_BUFFER_CAPACITY = 1024;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
    
    private final File		_file;

    private ByteBuffer		_valueBuffer;
    private FileChannel	_fileChannel;
    private int			_bufferCapacity;
    private long			_valueCount;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

    /**
     * 標準のバッファ容量で、指定されたファイルへ書き込むライタを生成する。
     * @param file				書き込み対象となるファイル
     * @throws IOException	入出力エラーが発生した場合
     * @throws	NullPointerException	<em>file</em> が <tt>null</tt> の場合
     */
    public PrimitiveStoredArrayWriter(File file) throws IOException
    {
        this(file, DEFAULT_BUFFER_CAPACITY);
    }

    /**
     * 指定されたバッファ容量で、指定されたファイルへ書き込むライタを生成する。
     * @param file				書き込み対象となるファイル
     * @param bufferCapacity	バッファ容量
     * @throws IOException	入出力エラーが発生した場合
     * @throws	NullPointerException	<em>file</em> が <tt>null</tt> の場合
     */
    public PrimitiveStoredArrayWriter(File file, int bufferCapacity) throws IOException
    {
    	this._file = file;
    	setBufferCapacity(bufferCapacity);
        open();
    }

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

    /**
     * このライタが書き込むファイルを返す。
     */
    public File getFile()
    {
        return _file;
    }

    /**
     * このストリームが開いている場合に <tt>true</tt> を返す。
     */
    public final boolean isOpen()
    {
    	return (_fileChannel != null);
    }


    /**
     * このストリームを閉じる。
     * @throws IOException	入出力エラーが発生した場合
     */
    public final void close() throws IOException
    {
    	if (_fileChannel != null) {
    		flush();
    		try {
    			_fileChannel.close();
    		}
    		finally {
    			_fileChannel = null;
    		}
    	}
    }

    /**
     * バッファの内容をファイルへ出力する。
     * @throws IOException	入出力エラーが発生した場合
     */
    public void flush() throws IOException
    {
        writeValueCount();
    }

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

    protected FileChannel getFileChannel(boolean flag) throws IOException
    {
        if(_fileChannel == null && flag)
            _fileChannel = newFileChannel();
        return _fileChannel;
    }

    protected FileChannel newFileChannel() throws IOException
    {
        FileOutputStream fileoutputstream = new FileOutputStream(getFile());
        return fileoutputstream.getChannel();
    }

    //protected void setFileChannel(FileChannel filechannel)
    //{
    //    this._fileChannel = filechannel;
    //}

    protected final void open() throws IOException
    {
        FileChannel filechannel = getFileChannel(true);
        setValueCount(0L);
        writeHeader();
        filechannel.position(getHeaderSize());
    }

    protected int getBufferCapacity()
    {
        return _bufferCapacity;
    }

    protected void setBufferCapacity(int bufferCapacity)
    {
        this._bufferCapacity = bufferCapacity;
    }

    protected ByteBuffer getValueBuffer(int bufferCapacity) throws IOException
    {
        ByteBuffer bytebuffer = _valueBuffer;
        if(bytebuffer == null)
        {
            bufferCapacity = Math.max(bufferCapacity, getBufferCapacity());
            bytebuffer = createValueBuffer(bufferCapacity);
            _valueBuffer = bytebuffer;
        } else
        if(bytebuffer.capacity() < bufferCapacity)
        {
            int j;
            for(j = bytebuffer.capacity(); j < bufferCapacity; j <<= 1);
            ByteBuffer bytebuffer1 = createValueBuffer(j);
            bytebuffer = bytebuffer1;
            setBufferCapacity(j);
            _valueBuffer = bytebuffer;
        }
        return bytebuffer;
    }

    protected ByteBuffer createValueBuffer(int i)
    {
        return ByteBuffer.allocate(i);
    }

    public long getValueCount()
    {
        return _valueCount;
    }

    protected void setValueCount(long l)
    {
        _valueCount = l;
    }

    protected long incrementValueCount(int i)
    {
        return _valueCount += i;
    }

    protected void writeHeader() throws IOException
    {
        int i = StoredArrayHeader.MAX_VALUE_COUNT_POSITION;
        int j = i - 0;
        ByteBuffer bytebuffer = getValueBuffer(j);
        bytebuffer.clear();
        bytebuffer.put(StoredArrayHeader.VERSION_BYTES);
        bytebuffer.putLong(StoredArrayHeader.VALUE_COUNT_POSITION, getValueCount());
        bytebuffer.flip();
        FileChannel filechannel = getFileChannel(true);
        filechannel.write(bytebuffer, 0L);
        bytebuffer.clear();
    }

    protected void writeValueCount() throws IOException
    {
        long l = StoredArrayHeader.VALUE_COUNT_POSITION;
        ByteBuffer bytebuffer = getValueBuffer(8);
        bytebuffer.clear();
        bytebuffer.putLong(getValueCount());
        bytebuffer.flip();
        FileChannel filechannel = getFileChannel(true);
        filechannel.write(bytebuffer, l);
        bytebuffer.clear();
    }

    protected void write(long al[], int i, int j) throws IOException
    {
        int k = j - i;
        int l = k * 8;
        ByteBuffer bytebuffer = getValueBuffer(l);
        for(int i1 = 0; i1 < k; i1++)
            bytebuffer.putLong(al[i + i1]);

        bytebuffer.flip();
        FileChannel filechannel = getFileChannel(true);
        filechannel.write(bytebuffer);
        bytebuffer.clear();
        incrementValueCount(k);
    }

    protected void write(double ad[], int i, int j) throws IOException
    {
        int k = j - i;
        int l = k * 8;
        ByteBuffer bytebuffer = getValueBuffer(l);
        for(int i1 = 0; i1 < k; i1++)
            bytebuffer.putDouble(ad[i + i1]);

        bytebuffer.flip();
        FileChannel filechannel = getFileChannel(true);
        filechannel.write(bytebuffer);
        bytebuffer.clear();
        incrementValueCount(k);
    }
}
