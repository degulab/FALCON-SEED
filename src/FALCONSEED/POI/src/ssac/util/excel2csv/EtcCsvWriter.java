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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)EtcCsvWriter.java	3.3.0	2016/05/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import ssac.util.nio.csv.CsvBufferedWriter;
import ssac.util.nio.csv.CsvParameters;

/**
 * <code>[Excel to CSV]</code> 専用の CSV ファイルライター。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcCsvWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** CSV フォーマット **/
	private final CsvParameters	_csvFormat;
	/** CSV ライター **/
	private CsvBufferedWriter	_writer;
	/** 現在の出力待機レコードの内容 **/
	private ArrayList<String>	_bufRecord;
	/** 出力された最大フィールド数 **/
	private int					_numMaxFields;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで、出力ファイルを開く。
	 * <em>leastFieldCount</em> が指定された場合、指定された列数を最低でも出力する。
	 * @param file				出力先ファイルの抽象パス
	 * @param encoding			エンコーディングの文字セット名、<tt>null</tt> もしくは空文字の場合はプラットフォーム標準文字コード
	 * @param leastFieldCount	最低出力フィールド数
	 * @param csvformat			CSV フォーマット、<tt>null</tt> の場合はデフォルト
	 * @throws NullPointerException			<em>file</em> が <tt>null</tt> の場合
	 * @throws FileNotFoundException 		ファイルは存在するが、普通のファイルではなくディレクトリである場合、ファイルは存在せず作成もできない場合、またはなんらかの理由で開くことができない場合
	 * @throws SecurityException			セキュリティーマネージャーが存在する場合に、セキュリティーマネージャーの <code>checkWrite</code> メソッドがファイルへの書き込みアクセスを許可しないとき
	 * @throws UnsupportedEncodingException 指定された文字エンコーディングがサポートされていない場合
	 */
	public EtcCsvWriter(File file, String encoding, int leastFieldCount, CsvParameters csvformat)
	throws FileNotFoundException, UnsupportedEncodingException
	{
		OutputStreamWriter osw = null;
		CsvBufferedWriter  cbw = null;
		try {
			// open stream writer
			if (encoding == null || encoding.isEmpty()) {
				osw = new OutputStreamWriter(new FileOutputStream(file));
			} else {
				osw = new OutputStreamWriter(new FileOutputStream(file), encoding);
			}
			
			// open csv writer
			if (csvformat == null) {
				// default format
				csvformat = new CsvParameters();
			}
			cbw = new CsvBufferedWriter(osw, csvformat);
			
			// succeeded
			_csvFormat = csvformat;
			_writer = cbw;
			_numMaxFields = (leastFieldCount < 0 ? 0 : leastFieldCount);
			_bufRecord = new ArrayList<String>();
			cbw = null;
			osw = null;
		}
		finally {
			if (cbw != null) {
				try {
					cbw.close();
				} catch (IOException ex) {}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException ex) {}
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクト生成時の CSV フォーマットを返す。
	 * このメソッドが返すオブジェクトを変更しても、このオブジェクトの動作に影響しない。
	 * @return	{@link CsvParameters} オブジェクト
	 */
	public CsvParameters getCsvFormat() {
		return _csvFormat;
	}
	
	/**
	 * ストリームを閉じる。すでに閉じている場合は、何もしない。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void close() throws IOException
	{
		if (_writer != null) {
			_writer.close();
		}
	}
	
	/**
	 * ストリームを閉じる。すでに閉じている場合は、何もしない。
	 * <p>このメソッドは、例外をスローしない。
	 */
	public void closeSilent() {
		if (_writer != null) {
			try {
				_writer.close();
			} catch (IOException ex) {}
		}
	}

	/**
	 * 出力済みレコード数を返す。
	 * @return 出力済みレコード数
	 */
	public long getWroteRecordCount() {
		return _writer.getWroteRecordCount();
	}

	/**
	 * 出力された最大フィールド数を返す。
	 * @return	最大出力フィールド数
	 */
	public long getMaxFieldCount() {
		return _numMaxFields;
	}

	/**
	 * 出力待機中のレコードバッファをクリアする。
	 */
	public void clearRecordBuffer() {
		_bufRecord.clear();
	}

	/**
	 * 出力待機中のレコードバッファを返す。
	 * @return	出力待機中のレコードバッファ
	 */
	public List<String> getRecordBuffer() {
		return _bufRecord;
	}

	/**
	 * 出力待機中のレコードを出力する。
	 * 出力待機中のレコードの内容は変更されない。
	 * @throws IOException 入出力エラーが発生した場合、もしくはストリームが閉じられている場合
	 */
	public void commitRecord() throws IOException
	{
		// レコードバッファの内容を出力
		_writer.addAllFields(_bufRecord);
		//--- 最大出力フィールド数を更新
		_numMaxFields = Math.max(_numMaxFields, _bufRecord.size());
		//--- 最大出力フィールド数に出力フィールド数を合わせる
		_writer.newRecordEnsureFieldCount(_numMaxFields);
	}

	/**
	 * 出力待機中レコードの指定されたフィールド位置に、値を設定する。
	 * この操作は挿入ではなく上書きとなる。指定されたインデックスが出力待機中レコードのフィールド数以上の場合、
	 * 自動的にフィールドを拡張する。
	 * @param index		フィールドインデックス(0～)
	 * @param strValue	設定する値
	 */
	public void setFieldValue(int index, String strValue) {
		ensureFieldIndex(index);
		_bufRecord.set(index, strValue);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean ensureFieldIndex(int index) {
		if (index < _bufRecord.size())
			return false;
		
		for (int fi = _bufRecord.size(); fi <= index; ++fi) {
			_bufRecord.add("");	// 空のフィールド
		}
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
