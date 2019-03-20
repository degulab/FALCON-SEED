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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import ssac.util.nio.ICloseable;

/**
 * 永続化されたプリミティブ配列のリーダー
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public class PrimitiveStoredArrayReader extends StoredArrayHeader implements ICloseable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 標準のバッファ容量となるバイト数 **/
    static protected final int DEFAULT_BUFFER_CAPACITY	= 2048;

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
     * デフォルトのバッファ容量で、指定されたファイルを読み込むリーダーを生成する。
     * @param file
     * @throws IOException	入出力エラーが発生した場合
     * @throws	NullPointerException	<em>file</em> が <tt>null</tt> の場合
     */
    public PrimitiveStoredArrayReader(File file) throws IOException
    {
        this(file, DEFAULT_BUFFER_CAPACITY);
    }

    /**
     * 指定されたバッファ容量で、指定されたファイルを読み込むリーダーを生成する。
     * @param file
     * @param bufferCapacity
     * @throws IOException	入出力エラーが発生した場合
     * @throws	NullPointerException	<em>file</em> が <tt>null</tt> の場合
     */
    public PrimitiveStoredArrayReader(File file, int bufferCapacity) throws IOException
    {
    	this._file = file;
    	setBufferCapacity(bufferCapacity);
    	open();
    }

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

    /**
     * このリーダーが読み込むファイルを返す。
     */
    public File getFile()
    {
        return _file;
    }

    /**
     * このストリームが開いている場合に <tt>true</tt> を返す。
     */
    public boolean isOpen()
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
    		try {
    			_fileChannel.close();
    		}
    		finally {
    			_fileChannel = null;
    		}
    	}
    }

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

    /**
     * このストリームのファイルチャネルを取得する。
     * @param flag	ファイルチャネルが存在しない場合に、新たなファイルチャネルを生成する場合は <tt>true</tt> を指定する。
     * @return	ファイルチャネルが存在する場合は <code>FileChannel</code> オブジェクトを返す。
     * 			存在しない場合は <tt>null</tt> を返す。
     * @throws IOException	入出力エラーが発生した場合
     */
    protected FileChannel getFileChannel(boolean flag) throws IOException
    {
        if(_fileChannel == null && flag)
            _fileChannel = newFileChannel();
        return _fileChannel;
    }

    /**
     * このリーダーに設定されたファイルのファイルチャネルを生成する。
     * @return	生成された <code>FileChannel</code> オブジェクト
     * @throws IOException	入出力エラーが発生した場合
     */
    protected FileChannel newFileChannel() throws IOException
    {
        FileInputStream fileinputstream = new FileInputStream(getFile());
        return fileinputstream.getChannel();
    }

    //protected void setFileChannel(FileChannel filechannel)
    //{
    //    _fileChannel = filechannel;
    //}

    /**
     * このリーダーのバッファ容量を返す。
     */
    protected int getBufferCapacity()
    {
        return _bufferCapacity;
    }

    /**
     * このリーダーのバッファ容量を設定する。
     * @param bufferCapacity
     */
    protected void setBufferCapacity(int bufferCapacity)
    {
        this._bufferCapacity = bufferCapacity;
    }

    /**
     * このリーダーのストリームをオープンする。
     * このメソッドは、ファイル先頭からヘッダ情報を読み込み、
     * ファイルポインタを先頭レコードに設定する。
     * @throws IOException	入出力エラーが発生した場合
     */
    protected final void open() throws IOException
    {
        FileChannel filechannel = getFileChannel(true);
        readHeader();
        filechannel.position(getHeaderSize());
    }

    /**
     * ヘッダ情報を読み込む。
     * @throws IOException	入出力エラーが発生した場合
     */
    protected void readHeader() throws IOException
    {
        byte abyte0[] = StoredArrayHeader.VERSION_BYTES;
        int i = StoredArrayHeader.MAX_VALUE_COUNT_POSITION;
        int j = i - 0;
        ByteBuffer bytebuffer = getValueBuffer(j);
        bytebuffer.clear();
        int k = bytebuffer.limit();
        bytebuffer.limit(j);
        FileChannel filechannel = getFileChannel(true);
        int l = filechannel.read(bytebuffer, 0L);
        if(l != j)
        {
            String s = "Target file does not have header.";
            throw new IOException(s);
        }
        bytebuffer.flip();
        byte abyte1[] = new byte[abyte0.length];
        bytebuffer.get(abyte1, 0, abyte0.length);
        if(!Arrays.equals(abyte0, abyte1))
        {
            String s1 = "unexpected version string";
            throw new IOException(s1);
        }
        long l1 = bytebuffer.getLong(StoredArrayHeader.VALUE_COUNT_POSITION);
        if(l1 < 0L)
        {
            String s2 = "Out of bounds : " + 0L + " <= " + l1 + " < " + 9223372036854775807L;
            throw new IOException(s2);
        } else
        {
            setValueCount(l1);
            bytebuffer.clear();
            bytebuffer.limit(k);
            return;
        }
    }

    protected ByteBuffer getValueBuffer(int i) throws IOException
    {
        ByteBuffer bytebuffer = _valueBuffer;
        if(bytebuffer == null)
        {
            i = Math.max(i, getBufferCapacity());
            bytebuffer = createValueBuffer(i);
            _valueBuffer = bytebuffer;
        } else
        if(bytebuffer.capacity() < i)
        {
            int j;
            for(j = bytebuffer.capacity(); j < i; j <<= 1);
            ByteBuffer bytebuffer1 = createValueBuffer(j);
            bytebuffer = bytebuffer1;
            setBufferCapacity(j);
            _valueBuffer = bytebuffer;
        }
        return bytebuffer;
    }

    protected ByteBuffer createValueBuffer(int i)
    {
        int j = Math.max(2048, i);
        return ByteBuffer.allocate(j);
    }

    protected long getValueCount()
    {
        return _valueCount;
    }

    protected void setValueCount(long l)
    {
        _valueCount = l;
    }

    protected ByteBuffer readValueBuffer(long l, long l1) throws IOException
    {
        int i = (int)(l1 - l);
        ByteBuffer bytebuffer = getValueBuffer(i);
        bytebuffer.clear();
        bytebuffer.limit(i);
        FileChannel filechannel = getFileChannel(true);
        filechannel.read(bytebuffer, l);
        bytebuffer.flip();
        return bytebuffer;
    }

    protected void checkIndex(long l)
    {
        if(l < 0L || getValueCount() <= l)
        {
            String s = "Out of bounds : " + 0L + " <= " + l + " < " + getValueCount();
            throw new IndexOutOfBoundsException(s);
        } else
        {
            return;
        }
    }

    protected void read(long al[], int i, int j, long l) throws IOException
    {
        checkIndex(l);
        int k = j - i;
        int i1 = k * 8;
        l = l * 8L + getHeaderSize();
        ByteBuffer bytebuffer = readValueBuffer(l, l + (long)i1);
        if(bytebuffer.limit() != i1)
        {
            String s = i1 + " != " + bytebuffer.limit();
            throw new IOException(s);
        }
        for(int j1 = 0; j1 < k; j1++)
            al[i + j1] = bytebuffer.getLong();

        bytebuffer.clear();
    }

    protected void read(double ad[], int i, int j, long l) throws IOException
    {
        checkIndex(l);
        int k = j - i;
        int i1 = k * 8;
        l = l * 8L + getHeaderSize();
        ByteBuffer bytebuffer = readValueBuffer(l, l + (long)i1);
        if(bytebuffer.limit() != i1)
        {
            String s = i1 + " != " + bytebuffer.limit();
            throw new IOException(s);
        }
        for(int j1 = 0; j1 < k; j1++)
            ad[i + j1] = bytebuffer.getDouble();

        bytebuffer.clear();
    }
}
