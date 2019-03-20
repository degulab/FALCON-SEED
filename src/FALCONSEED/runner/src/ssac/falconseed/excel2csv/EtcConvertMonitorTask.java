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
 * @(#)EtcConvertMonitorTask.java	3.3.0	2016/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.excel2csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import ssac.falconseed.runner.RunnerMessages;
import ssac.util.excel2csv.EtcConfigCsvData;
import ssac.util.excel2csv.EtcConfigDataSet;
import ssac.util.excel2csv.EtcConvertProgressHandler;
import ssac.util.excel2csv.EtcCsvWriter;
import ssac.util.excel2csv.EtcException;
import ssac.util.excel2csv.EtcWorkbookManager;
import ssac.util.logging.AppLogger;
import ssac.util.nio.csv.CsvParameters;
import ssac.util.swing.ProgressMonitorTask;

/**
 * <code>[Excel to CSV]</code> 変換実行時のプロセスタスク。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcConvertMonitorTask extends ProgressMonitorTask
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 処理対象の Excel ワークブック **/
	private EtcWorkbookManager	_excelbook;
	/** 変換定義コマンドのリスト **/
	private EtcConfigDataSet	_cmdlist;
	/** 変換結果 CSV のエンコーディングを示す文字セット名、<tt>null</tt> の場合はプラットフォーム標準 **/
	private String				_csvEncoding;
	/** 変換結果 CSV のフォーマット、<tt>null</tt> の場合はデフォルト形式 **/
	private CsvParameters		_csvFormat;
	/** キャンセル操作を受け付けた場合は <tt>true</tt> **/
	private boolean				_cancelAccepted;
	// 既存のファイルの置き換えに失敗した置き換え先と置き換え元のマップ
	LinkedHashMap<File, File>	_failedReplaceMap;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EtcConvertMonitorTask(EtcWorkbookManager excelbook, EtcConfigDataSet dataset, String csvEncoding, CsvParameters csvFormat) {
		super(excelbook.getFile().getName(), RunnerMessages.getInstance().Excel2csv_ExecProgress_predesc+"...\n\n", null, 0, 0, 100, true);
		if (excelbook == null || dataset == null)
			throw new NullPointerException();
		_excelbook   = excelbook;
		_cmdlist     = dataset;
		_csvEncoding = (csvEncoding==null || csvEncoding.isEmpty() ? null : csvEncoding);
		_csvFormat   = csvFormat;
		_cancelAccepted = false;
		_failedReplaceMap = new LinkedHashMap<File, File>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isCancelAccepted() {
		return _cancelAccepted;
	}
	
	public LinkedHashMap<File, File> getFailedReplaceFileMap() {
		return _failedReplaceMap;
	}

	@Override
	public void processTask() throws Throwable {
		// 既存のファイルの置き換え用マップ
		LinkedHashMap<File, File>	replaceFileMap = new LinkedHashMap<File, File>();
		// この操作で新規に作成されたファイルの集合
		LinkedHashSet<File>			newFileSet = new LinkedHashSet<File>();		
		
		// 変換実行
		boolean removeDestFiles = true;
		try {
			// 変換定義の変換処理数集計とテンポラリ出力用ファイル生成
			long maxProcessCount = 0L;
			for (EtcConfigCsvData item : _cmdlist) {
				// 処理数の取得
				maxProcessCount += item.getCommand().prepareExec(_excelbook, null);
				
				// キャンセル
				if (isTerminateRequested()) {
					_cancelAccepted = true;
					return;
				}
				
				// 出力先の取得
				File destFile;
				if (item.getOutputTemporaryEnabled()) {
					// テンポラリ出力(リプレースマップには載せない)
					destFile = createTemporaryFile(item.getTemporaryPrefix());
					newFileSet.add(destFile);
					item.setDestFile(destFile);
				}
				else {
					// 指定の出力先へ出力
					destFile = item.getDestFile();
					if (destFile.exists()) {
						// 出力先ファイルは存在する
						if (!replaceFileMap.containsKey(destFile)) {
							// テンポラリの出力先を生成
							File baseDir = destFile.getParentFile();
							String basename = destFile.getName();
							int fno = 1;
							do {
								destFile = new File(baseDir, basename + ".new" + String.valueOf(fno++));
							} while (destFile.exists());
							replaceFileMap.put(item.getDestFile(), destFile);
							newFileSet.add(destFile);
						}
					}
					else {
						// 出力先ファイルは新規作成
						newFileSet.add(destFile);
					}
				}
			}
			
			// キャンセル
			if (isTerminateRequested()) {
				_cancelAccepted = true;
				return;
			}
			
			// プログレスハンドラの生成
			setMaximum(100);
			setValue(0);
			EtcConvertTaskHandler handler = new EtcConvertTaskHandler(maxProcessCount);

			// 変換実行
			StringBuilder msgbuf = new StringBuilder();
			for (int index = 0; index < _cmdlist.size(); ++index) {
				if (!doItemConversion(replaceFileMap, msgbuf, index, handler)) {
					// user canceled
					_cancelAccepted = true;
					return;
				}
			}
			// すべて成功
			removeDestFiles = false;
		}
		finally {
			if (removeDestFiles) {
				// 一時ファイルを削除
				for (File newFile : newFileSet) {
					try {
						if (newFile.exists()) {
							if (newFile.delete()) {
								if (AppLogger.isDebugEnabled()) {
									AppLogger.debug("[Excel To CSV] Removed by error or canceled : " + newFile.toString());
								}
							}
						}
					} catch (Throwable ex) {}
				}
			}
		}
		
		// 成功ファイルのリプレース
		incrementValue();
		for (Map.Entry<File, File> entry : replaceFileMap.entrySet()) {
			final File newFile = entry.getValue();
			final File dstFile = entry.getKey();
			try {
				if (newFile.exists()) {
					if (!newFile.renameTo(dstFile)) {
						// ダイレクトに置き換え失敗なら、削除してから
						if (dstFile.exists()) {
							if (!dstFile.delete()) {
								throw new EtcException("Failed to remove : " + dstFile.toString());
							}
						}
						// 置き換え
						if (!newFile.renameTo(dstFile)) {
							throw new EtcException("Failed to rename : " + newFile.toString());
						}
					}
				}
			}
			catch (Throwable ex) {
				_failedReplaceMap.put(dstFile, newFile);
				if (AppLogger.isErrorEnabled()) {
					String errmsg = String.format("[Excel To CSV] Failed to replace \"%s\" to \"%s\"", newFile.toString(), dstFile.toString());
					AppLogger.error(errmsg, ex);
				}
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 1 変換定義の変換を実行する。
	 * @param replaceFileMap	既存ファイルと一時ファイルのマップ
	 * @param msgbuf	進捗ダイアログに表示するメッセージを加工するためのバッファ
	 * @param index		対象のインデックス
	 * @param handler	進捗ダイアログ更新用ハンドラ
	 * @return	成功した場合は <tt>true</tt>、キャンセルされた場合は <tt>false</tt>
	 */
	private boolean doItemConversion(LinkedHashMap<File, File> replaceFileMap, StringBuilder msgbuf, int index, EtcConvertTaskHandler handler) throws Throwable
	{
		EtcConfigCsvData item = _cmdlist.get(index);
		
		// メッセージの更新
		msgbuf.setLength(0);
		msgbuf.append(RunnerMessages.getInstance().Excel2csv_ExecProgress_rundesc);
		msgbuf.append(" (");
		msgbuf.append(index + 1);
		msgbuf.append('/');
		msgbuf.append(_cmdlist.size());
		msgbuf.append(")...\n\n");
		msgbuf.append(item.getDestFile().getName());
		msgbuf.append(" <= [");
		msgbuf.append(index + 1);
		msgbuf.append("] ");
		msgbuf.append(item.getConversionTitle()==null ? "" : item.getConversionTitle());
		setDescription(msgbuf.toString());
		
		// 変換結果の出力
		boolean removeDestFile = true;
		File destFile = item.getDestFile();
		if (replaceFileMap.containsKey(destFile)) {
			destFile = replaceFileMap.get(destFile);
		}
		EtcCsvWriter csvWriter = null;
		try {
			csvWriter = new EtcCsvWriter(destFile, _csvEncoding, item.getCommand().getOutputFieldCount(), _csvFormat);
			if (isTerminateRequested()) {
				// user canceled
				return false;
			}
			
			// 変換実行
			item.getCommand().exec(csvWriter, _excelbook, null, null, handler);
			if (isTerminateRequested()) {
				// user canceled
				return false;
			}
			
			// 変換成功
			removeDestFile = false;	// 変換結果は削除しない
		}
		catch (OutOfMemoryError ex) {
			// メモリ不足は、即座に上位例外ブロックへスロー
			throw ex;
		}
		catch (FileNotFoundException ex) {
			throw ex;
		}
		catch (UnsupportedEncodingException ex) {
			throw ex;
		}
		catch (SecurityException ex) {
			throw ex;
		}
		catch (IOException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			throw ex;
		}
		finally {
			// ファイルを閉じる
			if (csvWriter != null) {
				csvWriter.closeSilent();
			}
			
			// 失敗した場合は、出力先ファイルを消去
			if (removeDestFile && destFile.exists()) {
				try {
					destFile.delete();
				} catch (Throwable ex) {}
			}
		}
		
		// 成功
		return true;
	}
	
	private File createTemporaryFile(String prefix) throws IOException
	{
		if (prefix == null || prefix.length() < 3)
			prefix = "out";
		File tempfile = File.createTempFile(prefix, RunnerMessages.getInstance().extCSV);
		tempfile.deleteOnExit();
		return tempfile;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class EtcConvertTaskHandler extends EtcConvertProgressHandler
	{
		private int _lastPercent = 0;
		
		public EtcConvertTaskHandler() {
			super();
		}

		public EtcConvertTaskHandler(long maxValue) {
			super(maxValue);
		}
		
		@Override
		public boolean isCanceled() {
			return isTerminateRequested();
		};

		@Override
		public void setCurrentValue(long curValue) {
			super.setCurrentValue(curValue);
			
			// 進捗状況の更新
			int curPercent = (int)getProgressRate();
			if (_lastPercent != curPercent) {
				_lastPercent = curPercent;
				setValue(curPercent);
			}
		}
	}
}
