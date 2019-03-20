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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)IEditorDocument.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IEditorDocument.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.document;

import java.io.File;
import java.io.IOException;

import ssac.aadl.module.setting.AbstractSettings;
import ssac.falconseed.editor.plugin.IComponentManager;
import ssac.util.process.CommandExecutor;

/**
 * エディタ・ドキュメント共通インタフェース。
 * 
 * @version 1.10	2011/02/14
 */
public interface IEditorDocument
{
	/**
	 * 表示に関するリソースを開放する。
	 * このドキュメントがコンパイルもしくは実行可能なドキュメントの場合、
	 * このメソッドの実行によってコンパイルもしくは実行に影響があってはならない。
	 */
	public void releaseViewResources();
	/**
	 * このドキュメントのタイトルを取得する。
	 * @return	このドキュメントのタイトル
	 */
	public String getTitle();
	/**
	 * このドキュメントの保存先ファイルを取得する。
	 * 保存先ファイルが定義されていない場合は <tt>null</tt> を返す。
	 * @return	保存先ファイル
	 */
	public File getTargetFile();
	/**
	 * このドキュメントに保存先ファイルが指定されているかを判定する。
	 * @return	保存先ファイルが指定されていれば <tt>true</tt>
	 */
	public boolean hasTargetFile();
	/**
	 * このドキュメントの保存先を、指定されたパスに設定する。
	 * このメソッドでは、保存先の正当性検証は行わず、ターゲットを指定されたパスに
	 * 設定するのみとなる。編集状態なども変更されない。
	 * @param newTarget	新しい保存先となる抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void setTargetFile(File newTarget);
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	ドキュメントが読み込まれた時点での、最終更新日時
	 * @since 1.10
	 */
	public long lastModifiedTimeWhenLoadingTargetFile();
	/**
	 * このドキュメントの保存先ファイルの、現在の最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	最終更新日時
	 * @since 1.10
	 */
	public long lastModifiedTimeWhenCurrentTargetFile();
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を、
	 * 保存先ファイルの現在の更新日時で更新する。
	 * @since 1.10
	 */
	public void updateLastModifiedTimeWhenLoadingTargetFile();
	/**
	 * このドキュメントを管理するコンポーネント・マネージャを返す。
	 * @return	コンポーネント・マネージャ
	 */
	public IComponentManager getManager();

	/**
	 * このドキュメントの保存先ファイルが移動可能かを判定する。
	 * @return	移動可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.10
	 */
	public boolean canMoveTargetFile();
	/**
	 * このドキュメントが新規に作成され、一度も保存されていないかを判定する。
	 * @return	このドキュメントが新規に作成され保存されていない場合は <tt>true</tt> を返す。
	 */
	public boolean isNewDocument();
	/**
	 * このドキュメントが編集されているかを判定する。
	 * @return	編集されていれば <tt>true</tt> を返す。
	 */
	public boolean isModified();
	/**
	 * このドキュメントの編集状態を設定する。
	 * @param modified	編集状態とする場合は <tt>true</tt> を指定する。
	 */
	public void setModifiedFlag(boolean modified);
	/**
	 * 現在のドキュメントに関連付けられたファイルのエンコーディング名を返す。
	 * このメソッドが返すエンコーディング名は、読み込み時もしくは保存時に
	 * 適用されたものとなる。
	 * @return	エンコーディング名(<tt>null</tt> 以外)
	 */
	public String getLastEncodingName();
	/**
	 * このドキュメントを指定されたファイルに保存する。
	 * 指定されたファイルがすでに存在している場合、このドキュメントの内容で
	 * 上書きする。
	 * @param targetFile	保存先ファイル
	 * @throws NullPointerException	<code>targetFile</code> が <tt>null</tt> の場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void save(File targetFile) throws IOException;
	/**
	 * このドキュメントに関連付けられている設定情報を、最新の情報に更新する。
	 * @return	最新の情報に更新された場合に <tt>true</tt> を返す。
	 */
	public boolean refreshSettings();
	/**
	 * このドキュメントに関連付けられている設定情報を取得する。
	 * @return	ドキュメントの設定情報を返す。設定情報を持たない場合は <tt>null</tt> を返す。
	 */
	public AbstractSettings getSettings();
	/**
	 * このドキュメントにおける実行可能ファイルの有無を判定する。
	 * @return	ドキュメントにおける実行可能ファイルが存在している場合は <tt>true</tt>
	 */
	public boolean hasExecutableFile();
	/**
	 * このドキュメントにおける実行可能ファイルを取得する。
	 * このメソッドがファイルを返した場合、そのファイルは必ず存在している。
	 * @return	実行可能ファイルが存在していればそのファイルオブジェクトを返す。
	 * 			それ以外の場合は <tt>null</tt> を返す。
	 */
	public File getExecutableFile();
	/**
	 * このドキュメントがコンパイル可能か判定する。
	 * @return	このドキュメントがコンパイル可能ドキュメントなら <tt>true</tt> を返す。
	 */
	public boolean isCompilable();
	/**
	 * このドキュメントが実行可能か判定する。
	 * @return	このドキュメントが実行可能ドキュメントなら <tt>true</tt> を返す。
	 */
	public boolean isExecutable();
	/**
	 * このドキュメントが保存されているファイルでコンパイルを実行するための
	 * 実行情報を生成する。
	 * @return	生成された {@link CommandExecutor} オブジェクトを返す。
	 * @throws IllegalStateException	コンパイル可能な状態ではない場合
	 */
	public CommandExecutor createCompileExecutor();
	/**
	 * このドキュメントに関連するモジュールを実行するための実行情報を生成する。
	 * 関連するモジュールはドキュメントの形式に依存する。
	 * @return	生成された {@link CommandExecutor} オブジェクトを返す。
	 * @throws IllegalStateException	実行可能な状態ではない場合
	 */
	public CommandExecutor createModuleExecutor();
	/**
	 * コンパイルの実行が終了した時点で、エディタフレームから呼び出されるハンドラ。
	 * 基本的に、コンパイル結果を収集し、必要な設定情報を更新するためのインタフェースとなる。
	 */
	public void onCompileFinished();
}
