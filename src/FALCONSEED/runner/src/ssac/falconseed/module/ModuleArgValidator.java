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
 * @(#)ModuleArgValidator.java	3.1.0	2014/05/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgValidator.java	1.22	2012/08/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.io.File;

import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.io.VirtualFile;

/**
 * モジュール実行時引数の正当性検査クラス。
 * 
 * @version 3.1.0	2014/05/14
 * @since 1.22
 */
public class ModuleArgValidator
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final ModuleArgValidator	_instance = new ModuleArgValidator();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public ModuleArgValidator getInstance() {
		return _instance;
	}
	
	/**
	 * 値の正当性を検証する。
	 * このメソッドは、編集の可不可に関わらず値を検証し、エラーステートを更新する。
	 * @param arg		引数値が格納されたオブジェクト
	 * @param handler	引数値にエラーが発生した際に、エラー情報を受け取るハンドラ
	 * @return	値が正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>handler</em> が <tt>null</tt> の場合
	 */
	public boolean verifyValue(final IModuleArgConfig arg, final IModuleArgValidationHandler handler) {
		if (arg == null)
			return true;	// no checked
		
		switch (arg.getType()) {
			case IN :
				return verifyInputFileValue(arg, handler);
			case OUT :
				return verifyOutputFileValue(arg, handler);
			case PUB :
				return verifyPublishValue(arg, handler);
			case SUB :
				return verifySubscribeValue(arg, handler);
			default :
				return verifyStringValue(arg, handler);
		}
	}

	/**
	 * パブリッシュ属性の引数値が正当かを判定する。
	 * 次の条件に一致した場合に正当と判定する。
	 * <ul>
	 * <li>文字列が空ではでない。
	 * </ul>
	 * @param item	判定対象のデータ
	 * @param handler	引数値にエラーが発生した際に、エラー情報を受け取るハンドラ(<tt>null</tt> は不可)
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	public boolean verifyPublishValue(IModuleArgConfig item, final IModuleArgValidationHandler handler) {
		if (item.getValue() == null) {
			handler.setError(RunnerMessages.getInstance().msgErrorRequiredMqttPubAddr);
			return false;
		} else {
			String strValue = item.getValue().toString();
			if (strValue.length() > 0) {
				// TODO:パブリッシュ宛先のフォーマットチェック
				handler.clearError();
				return true;
			} else {
				handler.setError(RunnerMessages.getInstance().msgErrorRequiredMqttPubAddr);
				return false;
			}
		}
	}

	/**
	 * サブスクライブ属性の引数値が正当かを判定する。
	 * 次の条件に一致した場合に正当と判定する。
	 * <ul>
	 * <li>文字列が空ではでない。
	 * </ul>
	 * @param item	判定対象のデータ
	 * @param handler	引数値にエラーが発生した際に、エラー情報を受け取るハンドラ(<tt>null</tt> は不可)
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	public boolean verifySubscribeValue(IModuleArgConfig item, final IModuleArgValidationHandler handler) {
		if (item.getValue() == null) {
			handler.setError(RunnerMessages.getInstance().msgErrorRequiredMqttSubAddr);
			return false;
		} else {
			String strValue = item.getValue().toString();
			if (strValue.length() > 0) {
				// TODO:サブスクライブ宛先のフォーマットチェック
				handler.clearError();
				return true;
			} else {
				handler.setError(RunnerMessages.getInstance().msgErrorRequiredMqttSubAddr);
				return false;
			}
		}
	}

	/**
	 * 文字列属性の引数値が正当かを判定する。
	 * 次の条件に一致した場合に正当と判定する。
	 * <ul>
	 * <li>文字列が空ではでない。
	 * </ul>
	 * @param item	判定対象のデータ
	 * @param handler	引数値にエラーが発生した際に、エラー情報を受け取るハンドラ(<tt>null</tt> は不可)
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean verifyStringValue(IModuleArgConfig item, final IModuleArgValidationHandler handler) {
		if (item.getValue() == null) {
			handler.setError(RunnerMessages.getInstance().msgErrorRequiredString);
			return false;
		} else {
			String strValue = item.getValue().toString();
			if (strValue.length() > 0) {
				handler.clearError();
				return true;
			} else {
				handler.setError(RunnerMessages.getInstance().msgErrorRequiredString);
				return false;
			}
		}
	}

	/**
	 * 入力ファイル属性の引数値が正当かを判定する。
	 * 次の条件のどれかに一致した場合に正当と判定する。
	 * <ul>
	 * <li>テンポラリファイル指定である。
	 * <li>閲覧ファイルが指定されている。
	 * <li>ファイルのパスが指定されており、そのファイルが存在している。
	 * <li>
	 * </ul>
	 * @param item	判定対象のデータ
	 * @param handler	引数値にエラーが発生した際に、エラー情報を受け取るハンドラ(<tt>null</tt> は不可)
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean verifyInputFileValue(IModuleArgConfig item, final IModuleArgValidationHandler handler) {
		IMExecArgParam param = item.getParameterType();
		if (param instanceof MExecArgTempFile) {
			// エラーなし
			handler.clearError();
			return true;
		}

		Object value = item.getValue();
		if (value instanceof ModuleArgID) {
			// モジュール引数の値を利用するため、エラーなし
			handler.clearError();
			return true;
		}
		
		if (!(value instanceof VirtualFile) && !(value instanceof File)) {
			handler.setError(RunnerMessages.getInstance().msgErrorRequiredInputFile);
			return false;
		}
		
		boolean exists;
		if (value instanceof VirtualFile) {
			exists = ((VirtualFile)value).exists();
		} else {
			exists = ((File)value).exists();
		}
		if (!exists) {
			handler.setError(RunnerMessages.getInstance().msgErrorInputFileNotFound);
			return false;
		}
		
		// エラーなし
		handler.clearError();
		return true;
	}
	
	/**
	 * 出力ファイル属性の引数値が正当かを判定する。
	 * 次の条件のどれかに一致した場合に正当と判定する。
	 * <ul>
	 * <li>テンポラリファイル指定である。
	 * <li>ファイルのパスが指定されている。
	 * <li>
	 * </ul>
	 * @param item	判定対象のデータ
	 * @param handler	引数値にエラーが発生した際に、エラー情報を受け取るハンドラ(<tt>null</tt> は不可)
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean verifyOutputFileValue(IModuleArgConfig item, final IModuleArgValidationHandler handler) {
		IMExecArgParam param = item.getParameterType();
		if (param instanceof MExecArgTempFile) {
			// エラーなし
			handler.clearError();
			return true;
		}

		Object value = item.getValue();
		if (item.getOutToTempEnabled()) {
			String strPrefix = item.getTempFilePrefix();
			if (strPrefix != null && strPrefix.length() < 3) {
				handler.setError(RunnerMessages.getInstance().msgErrorRequiredOver3chars);
				return false;
			}
		}
		else if (!(value instanceof VirtualFile) && !(value instanceof File)) {
			handler.setError(RunnerMessages.getInstance().msgErrorRequiredOutputFile);
			return false;
		}
		
		// エラーなし
		handler.clearError();
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
