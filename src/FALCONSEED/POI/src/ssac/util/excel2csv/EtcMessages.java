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
 * @(#)EtcMessages.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv;

import java.util.ResourceBundle;

import ssac.util.io.FieldResource;
import ssac.util.logging.AppLogger;

/**
 * <code>[Excel to CSV]</code> の文字列リソース。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcMessages extends FieldResource
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * 文字列リソースの唯一のインスタンス
	 */
	static private EtcMessages instance = null;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected String getResourceName() {
		return EtcMessages.class.getName();
	}
	
	static public EtcMessages getInstance() {
		if (instance == null) {
			instance = new EtcMessages();
			String path = getResourceName();
			try {
				ResourceBundle resource = ResourceBundle.getBundle(path);
				instance.updateFields(resource);
			} catch (RuntimeException ex) {
				AppLogger.debug(ex);
			}
		}
		return instance;
	}
	
	static public String getUnexpectedElement(String expected, String actual) {
		return "expected element=" + expected + " but actual=" + actual;
	}

	//------------------------------------------------------------
	// Messages
	//------------------------------------------------------------
	
	//
	// Config Worning messages
	//
	//public String warnConfigIgnoreAfterSecondColumn		= "第2列以降の値は無視されます。";
	public String warnConfigIgnoreAfterSecondColumn		= "The values after 2nd column in the row were ignored.";
	//public String warnConfigIgnoreAfterThirdColumn		= "第3列以降の値は無視されます。";
	public String warnConfigIgnoreAfterThirdColumn		= "The values after 3rd column in the row were ignored.";
	//public String warnConfigParamRowPositionOutOfRange	= "行位置が処理対象領域を超えています。";
	public String warnConfigParamRowPositionOutOfRange	= "Row position out of target area.";
	//public String warnConfigParamColPositionOutOfRange = "列位置が処理対象領域を超えています。";
	public String warnConfigParamColPositionOutOfRange 	= "Column position out of target area.";
	//public String warnConfigParamRowCountOutOfRange		= "指定された行数では処理対象領域を超えています。";
	public String warnConfigParamRowCountOutOfRange		= "Out of target area by the specified number of rows.";
	//public String warnConfigParamColCountOutOfRange		= "指定された列数では処理対象領域を超えています。";
	public String warnConfigParamColCountOutOfRange		= "Out of target area by the specified number of columns.";
	//public String warnConfigParamCellPositionOutOfRange	= "セル位置が処理対象領域を超えています。";
	public String warnConfigParamCellPositionOutOfRange	= "Cell position out of target area.";
	//public String warnConfigParamGroupRowCountIndivisible	= "指定されたグループ数では処理対象領域の行数を割り切れません。";
	public String warnConfigParamGroupRowCountIndivisible	= "Target area's number of rows is indivisible by the specified group count.";
	//public String warnConfigParamGroupColCountIndivisible	= "指定されたグループ数では処理対象領域の列数を割り切れません。";
	public String warnConfigParamGroupColCountIndivisible	= "Target area's number of columns is indivisible by the specified group count.";
	
	//
	// Config Error messages
	//
	//public String errConfigSheetNotFound				= "変換定義シートが存在しません。";
	public String errConfigSheetNotFound				= "Conversion definition sheet does not exist.";
	//public String errConfigCommandUndefined			= "未定義のコマンドです。";
	public String errConfigCommandUndefined				= "Command is not defined.";
	//public String errConfigCommandUnspecified			= "コマンドが指定されていません。";
	public String errConfigCommandUnspecified			= "Command is not specified.";
	//public String errConfigCommandUnsupported			= "サポートされてないコマンドです。";
	public String errConfigCommandUnsupported			= "Command is not supported in the command block.";
	//public String errConfigCommandBlockEndNotFound	= "コマンドブロック終了コマンドが記述されていません。";
	public String errConfigCommandBlockEndNotFound		= "End command of the command block is not specified.";
	//public String errConfigCommandNothingRequired		= "コマンドブロックに必須のコマンドが記述されていません。";
	public String errConfigCommandNothingRequired		= "Required command is not specified in the command block.";
	//public String errConfigCommandAlreadySpecified	= "コマンドはすでに指定されています。";
	public String errConfigCommandAlreadySpecified		= "Command is already specified.";
	//public String errConfigCommandParamIllegalValue	= "コマンドのパラメータ値が不正です。";
	public String errConfigCommandParamIllegalValue		= "Command's parameter value is illegal.";
	//public String errConfigOptionUndefined			= "未定義のオプションです。";
	public String errConfigOptionUndefined				= "Option is not defined.";
	//public String errConfigOptionUnsupported			= "コマンドでサポートされていないオプションです。";
	public String errConfigOptionUnsupported			= "Option is not supported at the command.";
	//public String errConfigOptionNothingRequired		= "コマンドに必須のオプションが記述されていません。";
	public String errConfigOptionNothingRequired		= "Required option is not specified at the command.";
	//public String errConfigOptionIllegalValue			= "オプション値が不正です。";
	public String errConfigOptionIllegalValue			= "Option value is illegal.";
	//public String errConfigOptionAlreadySpecified		= "オプションはすでに指定されています。";
	public String errConfigOptionAlreadySpecified		= "Option is already specified.";
	//public String errConfigOptionSheetNotFound		= "'sheet' オプションに指定されたシートは存在しません。";
	public String errConfigOptionSheetNotFound			= "'sheet' option's sheet does not exist.";
	//public String errConfigParamRowPositionOutOfSheet	= "行位置がシートの最大領域を超えています。";
	public String errConfigParamRowPositionOutOfSheet	= "Row position is out of sheet's maximum area.";
	//public String errConfigParamColPositionOutOfSheet	= "列位置がシートの最大領域を超えています。";
	public String errConfigParamColPositionOutOfSheet	= "Column position is out of sheet's maximum area.";
	//public String errConfigParamRowCountOutOfSheet		= "指定された行数では処理対象領域がシートの最大領域を超えています。";
	public String errConfigParamRowCountOutOfSheet		= "Target area is out of sheet's maximum area by the specified number of rows.";
	//public String errConfigParamColCountOutOfSheet		= "指定された列数では処理対象領域がシートの最大領域を超えています。";
	public String errConfigParamColCountOutOfSheet		= "Target area is out of sheet's maximum area by the specified number of columns.";
	//public String errConfigParamCellPositionOutOfSheet	= "セル位置がシートの最大領域を超えています。";
	public String errConfigParamCellPositionOutOfSheet	= "Cell position is out of sheet's maximum area.";
	//public String errConfigParamRowPositionIllegal		= "行位置が不正です。";
	public String errConfigParamRowPositionIllegal		= "Row position is illegal.";
	//public String errConfigParamColPositionIllegal		= "列位置が不正です。";
	public String errConfigParamColPositionIllegal		= "Column position is illegal.";
	//public String errConfigParamCellPositionIllegal		= "セル位置が不正です。";
	public String errConfigParamCellPositionIllegal		= "Cell position is illegal.";
	//public String errConfigCmdCsvFileBeginNotFound	= "'#csv-file-begin' コマンドが記述されていません。";
	public String errConfigCmdCsvFileBeginNotFound		= "'#csv-file-begin' command is not specified.";

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
