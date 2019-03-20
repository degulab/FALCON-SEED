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
 * @(#)StreamManager.java	1.50	2010/09/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io.internal;

import java.util.HashSet;
import java.util.Iterator;

/**
 * <code>AbTextFileReader</code> インスタンス、
 * ならびに <code>AbTextFileWriter</code> インスタンスを保持するクラス。
 * <p>このクラスは、AADL実行モジュールのインスタンスでオープンされた
 * ストリームのインスタンスを保持し、AADL実行モジュール終了時に
 * クローズされていないストリームをクローズするために使用する。
 * 
 * @version 1.50	2010/09/29
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Yuji Onuki (Statistics Bureau)
 * @author Shungo Sakaki (Tokyo University of Technology)
 * @author Akira Sasaki (HOSEI UNIVERSITY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 * 
 * @since 1.50
 */
public class StreamManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final Object _mutex = new Object();
	
	protected final HashSet<AbTextFileReader<?>>	_readerSet;
	protected final HashSet<AbTextFileWriter>		_writerSet;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public StreamManager() {
		this._readerSet = new HashSet<AbTextFileReader<?>>();
		this._writerSet = new HashSet<AbTextFileWriter>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void cleanup() {
		synchronized (_mutex) {
			// cleanup all readers
			Iterator<AbTextFileReader<?>> rit = _readerSet.iterator();
			while (rit.hasNext()) {
				AbTextFileReader<?> reader = rit.next();
				try {
					reader.close();
				} catch (Throwable ignoreEx) { ignoreEx=null; }
				rit.remove();
			}
			// cleanup all writers
			Iterator<AbTextFileWriter> wit = _writerSet.iterator();
			while (wit.hasNext()) {
				AbTextFileWriter writer = wit.next();
				try {
					writer.close();
				} catch (Throwable ignoreEx) { ignoreEx=null; }
				wit.remove();
			}
		}
	}
	
	public boolean add(AbTextFileReader<?> reader) {
		if (reader == null)
			return false;
		
		synchronized (_mutex) {
			return _readerSet.add(reader);
		}
	}
	
	public boolean add(AbTextFileWriter writer) {
		if (writer == null)
			return false;
		
		synchronized (_mutex) {
			return _writerSet.add(writer);
		}
	}
	
	public boolean remove(AbTextFileReader<?> reader) {
		if (reader == null)
			return false;
		
		synchronized (_mutex) {
			return _readerSet.remove(reader);
		}
	}
	
	public boolean remove(AbTextFileWriter writer) {
		if (writer == null)
			return false;
		
		synchronized (_mutex) {
			return _writerSet.remove(writer);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
