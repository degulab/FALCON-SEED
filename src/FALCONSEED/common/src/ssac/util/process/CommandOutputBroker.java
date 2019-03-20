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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CommandOutputBroker.java	3.0.0	2014/03/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.util.Locale;

/**
 * サブプロセスが出力する標準出力、標準エラー出力の内容を受け付けるクラス。
 * このクラスが、ログファイル、またはコンソール（テキストコンポーネント）などへの出力を仲介する。
 * なお、ブローカーそのものはスレッドセーフではない。
 * 
 * @version 3.0.0	2014/03/26
 */
public class CommandOutputBroker implements IOutputWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final IOutputWriter[]	EMPTY_WRITERS	= new IOutputWriter[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このブローカーが出力する宛先 **/
	protected IOutputWriter[]	_aryWriters = EMPTY_WRITERS;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandOutputBroker() {
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isWriterEmpty() {
		return (_aryWriters.length <= 0);
	}
	
	public void clearAllWriters() {
		_aryWriters = EMPTY_WRITERS;
	}
	
	public int getWriterCount() {
		return _aryWriters.length;
	}
	
	public boolean containsWriter(IOutputWriter writer) {
		if (writer != null) {
			return (indexOfWriter(writer) >= 0);
		}
		return false;
	}
	
	public void addWriter(IOutputWriter writer) {
		if (writer == null)
			throw new NullPointerException();
		IOutputWriter[] newArray = new IOutputWriter[_aryWriters.length+1];
		System.arraycopy(_aryWriters, 0, newArray, 0, _aryWriters.length);
		newArray[_aryWriters.length] = writer;
		_aryWriters = newArray;
	}
	
	public boolean removeWriter(IOutputWriter writer) {
		if (writer == null)
			return false;
		// find
		int index = indexOfWriter(writer);
		if (index < 0) {
			// not found writer
			return false;
		}
		
		// remove
		int arylen = _aryWriters.length;
		if (arylen > 1) {
			int newlen = arylen - 1;
			IOutputWriter[] newArray = new IOutputWriter[newlen];
			if (index > 0) {
				System.arraycopy(_aryWriters, 0, newArray, 0, index);
			}
			if (index < newlen) {
				System.arraycopy(_aryWriters, index+1, newArray, index, (newlen - index));
			}
			_aryWriters = newArray;
		}
		else {
			_aryWriters = EMPTY_WRITERS;
		}
		return true;
	}
	
	protected int indexOfWriter(IOutputWriter writer) {
		for (int index = 0; index < _aryWriters.length; ++index) {
			if (writer.equals(_aryWriters[index])) {
				return index;
			}
		}
		return (-1);
	}

	//------------------------------------------------------------
	// Implement ssac.util.process.ICommandOutputBroker interfaces
	//------------------------------------------------------------

	@Override
	public Object getLockObject() {
		return null;
	}

	@Override
	public boolean checkError() {
		for (IOutputWriter writer : _aryWriters) {
			if (writer.checkError()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void flush() {
		for (IOutputWriter writer : _aryWriters) {
			writer.flush();
		}
	}

	@Override
	public void close() {
		for (IOutputWriter writer : _aryWriters) {
			writer.close();
		}
	}

	@Override
	public void print(boolean isError, char c) {
		for (IOutputWriter writer : _aryWriters) {
			writer.print(isError, c);
		}
	}

	@Override
	public void print(boolean isError, String str) {
		for (IOutputWriter writer : _aryWriters) {
			writer.print(isError, str);
		}
	}

	@Override
	public void print(boolean isError, Object obj) {
		for (IOutputWriter writer : _aryWriters) {
			writer.print(isError, obj);
		}
	}

	@Override
	public void printf(boolean isError, String format, Object... args) {
		for (IOutputWriter writer : _aryWriters) {
			writer.printf(isError, format, args);
		}
	}

	@Override
	public void printf(boolean isError, Locale l, String format, Object... args) {
		for (IOutputWriter writer : _aryWriters) {
			writer.printf(isError, l, format, args);
		}
	}

	@Override
	public void println(boolean isError, char c) {
		for (IOutputWriter writer : _aryWriters) {
			writer.println(isError, c);
		}
	}

	@Override
	public void println(boolean isError, String str) {
		for (IOutputWriter writer : _aryWriters) {
			writer.println(isError, str);
		}
	}

	@Override
	public void println(boolean isError, Object obj) {
		for (IOutputWriter writer : _aryWriters) {
			writer.println(isError, obj);
		}
	}

	@Override
	public void printfln(boolean isError, String format, Object... args) {
		for (IOutputWriter writer : _aryWriters) {
			writer.printfln(isError, format, args);
		}
	}

	@Override
	public void printfln(boolean isError, Locale l, String format, Object... args) {
		for (IOutputWriter writer : _aryWriters) {
			writer.printfln(isError, l, format, args);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
