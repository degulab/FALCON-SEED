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
 * @(#)EtcParseMonitorTask.java	3.3.0	2016/05/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.excel2csv;

import ssac.falconseed.runner.RunnerMessages;
import ssac.util.excel2csv.EtcConfigDataSet;
import ssac.util.excel2csv.EtcWorkbookManager;
import ssac.util.excel2csv.parser.ConfigErrorList;
import ssac.util.excel2csv.parser.ConfigParser;
import ssac.util.swing.ProgressMonitorTask;

/**
 * <code>[Excel to CSV]</code> 変換定義パース時のプロセスタスク。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcParseMonitorTask extends ProgressMonitorTask
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 変換定義パーサー **/
	private final ConfigParser		_parser;
	/** 変換定義コマンドのリスト **/
	private EtcConfigDataSet		_cmdlist;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EtcParseMonitorTask(EtcWorkbookManager excelbook) {
		//super(title, null, null, 0, 0, 100, false);
		super(excelbook.getFile().getName(), RunnerMessages.getInstance().Excel2csv_OpenFileProgress_desc, null, 0, 0, 100, false);
		_parser = new ConfigParser(excelbook);
		_cmdlist = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ConfigErrorList getParseErrorList() {
		return _parser.getErrorList();
	}
	
	public EtcConfigDataSet getParseResult() {
		return _cmdlist;
	}

	@Override
	public void processTask() throws Throwable {
		// parse
		_cmdlist = _parser.parse();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
