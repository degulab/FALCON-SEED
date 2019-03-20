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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ExDialog.java	2.0.0	2012/09/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import ssac.aadl.common.CommonMessages;

/**
 * 標準的なボタンを持つダイアログの基本実装。
 * ダイアログの下端にはボタンパネル、中央にはメインパネルが配置される。
 * このコンポーネントでは、コンポーネントならびにウィンドウのリスナーを登録し、
 * リスナーに対応するインタフェースを実装するためのフレームワークを提供する。
 * コンストラクタでは、{@link #setDefaultCloseOperation(int)} が {@link javax.swing.WindowConstants#DO_NOTHING_ON_CLOSE} を引数として呼び出され、
 * このクラス標準の <code>WindowListener</code> と <code>ComponentListener</code> が設定される。
 * このクラスのコンストラクタを呼び出した直後は、必ず {@link #initialComponent()} を呼び出すこと。
 * {@link #initialComponent()} の呼び出しによって、ダイアログ内のコンポーネントが初期化される。
 * </blockquote>
 * 
 * @version 2.0.0	2012/09/29
 * @since 2.0.0
 */
public class ExDialog extends AbDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -1749766853139096335L;

	static public final int DLGRESULT_CLOSE	= 0;
	static public final int DLGRESULT_CANCEL	= DLGRESULT_CLOSE;
	static public final int DLGRESULT_OK		= 1;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** [Apply] ボタン **/
	private JButton	_btnApply;
	/** [Ok] ボタン **/
	private JButton	_btnOk;
	/** [Cancel] ボタン **/
	private JButton	_btnCancel;
	/** メインコンポーネント **/
	private JComponent	_mainPane;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * タイトルがなく、所有者 <code>Frame</code> の指定もない非モーダルダイアログを作成します。
	 * 共有されて、隠れたフレームがダイアログの所有者として設定されます。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * <b>注：</b>
	 * <blockquote>
	 * このコンストラクタでは、所有者なしのダイアログを作成することはできません。
	 * 所有者なしのダイアログを作成するには、引数 <tt>null</tt> を指定して、<code>AbDialog(Window)</code> コンストラクタまたは <code>AbDialog(Dialog)</code> コンストラクタを使用します。
	 * </blockquote>
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog()
	 */
	public ExDialog() {
		super();
	}

	/**
	 * 所有者 <code>Frame</code> を指定して、タイトルのない非モーダルダイアログを作成します。
	 * <em>owner</em> が <tt>null</tt> の場合、共有の非表示フレームがダイアログの所有者として設定されます。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * <b>注：</b>
	 * <blockquote>
	 * このコンストラクタでは、所有者なしのダイアログを作成することはできません。
	 * 所有者なしのダイアログを作成するには、引数 <tt>null</tt> を指定して、<code>AbDialog(Window)</code> コンストラクタまたは <code>AbDialog(Dialog)</code> コンストラクタを使用します。
	 * </blockquote>
	 * @param owner		ダイアログを表示する <code>Frame</code>
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Frame)
	 */
	public ExDialog(Frame owner) {
		super(owner);
	}

	/**
	 * 所有者 <code>Frame</code>、モーダルであるかどうか、および空のタイトルを指定してダイアログを作成します。
	 * <em>owner</em> が <tt>null</tt> の場合、共有の非表示フレームがダイアログの所有者として設定されます。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * <b>注：</b>
	 * <blockquote>
	 * このコンストラクタでは、所有者なしのダイアログを作成することはできません。
	 * 所有者なしのダイアログを作成するには、引数 <tt>null</tt> を指定して、<code>AbDialog(Window)</code> コンストラクタまたは <code>AbDialog(Dialog)</code> コンストラクタを使用します。
	 * </blockquote>
	 * @param owner		ダイアログを表示する <code>Frame</code>
	 * @param modal		ダイアログが表示されているとき、ユーザーのほかのトップレベルのウィンドウへの入力をブロックするかどうかを指定する。
	 * 					<tt>true</tt> の場合、モーダリティータイププロパティーは <code>DEFAULT_MODALITY_TYPE</code> に設定される。そうでない場合、非モーダルダイアログになる
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Frame, boolean)
	 */
	public ExDialog(Frame owner, boolean modal) {
		super(owner, modal);
	}

	/**
	 * タイトルと所有者フレームを指定して、非モーダルダイアログを作成します。
	 * <em>owner</em> が <tt>null</tt> の場合、共有の非表示フレームがダイアログの所有者として設定されます。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * <b>注：</b>
	 * <blockquote>
	 * このコンストラクタでは、所有者なしのダイアログを作成することはできません。
	 * 所有者なしのダイアログを作成するには、引数 <tt>null</tt> を指定して、<code>AbDialog(Window)</code> コンストラクタまたは <code>AbDialog(Dialog)</code> コンストラクタを使用します。
	 * </blockquote>
	 * @param owner		ダイアログを表示する <code>Frame</code>
	 * @param title		ダイアログのタイトルバーに表示される <code>String</code>
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Frame, String)
	 */
	public ExDialog(Frame owner, String title) {
		super(owner, title);
	}

	/**
	 * タイトル、所有者 <code>Frame</code>、およびモーダルかどうかを指定してダイアログを作成します。
	 * <em>owner</em> が <tt>null</tt> の場合、共有の非表示フレームがダイアログの所有者として設定されます。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * <b>注：</b>
	 * <blockquote>
	 * このコンストラクタでは、所有者なしのダイアログを作成することはできません。
	 * 所有者なしのダイアログを作成するには、引数 <tt>null</tt> を指定して、<code>AbDialog(Window)</code> コンストラクタまたは <code>AbDialog(Dialog)</code> コンストラクタを使用します。
	 * </blockquote>
	 * @param owner		ダイアログを表示する <code>Frame</code>
	 * @param title		ダイアログのタイトルバーに表示される <code>String</code>
	 * @param modal		ダイアログが表示されているとき、ユーザーのほかのトップレベルのウィンドウへの入力をブロックするかどうかを指定する。
	 * 					<tt>true</tt> の場合、モーダリティータイププロパティーは <code>DEFAULT_MODALITY_TYPE</code> に設定される。そうでない場合、非モーダルダイアログになる
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Frame, String, boolean)
	 */
	public ExDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	/**
	 * タイトル、所有者 <code>Frame</code>、モーダルであるかどうか、および <code>GraphicsConfiguration</code> を指定してダイアログを作成します。
	 * <em>owner</em> が <tt>null</tt> の場合、共有の非表示フレームがダイアログの所有者として設定されます。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * <b>注：</b>
	 * <blockquote>
	 * このコンストラクタでは、所有者なしのダイアログを作成することはできません。
	 * 所有者なしのダイアログを作成するには、引数 <tt>null</tt> を指定して、<code>AbDialog(Window)</code> コンストラクタまたは <code>AbDialog(Dialog)</code> コンストラクタを使用します。
	 * </blockquote>
	 * @param owner		ダイアログを表示する <code>Frame</code>
	 * @param title		ダイアログのタイトルバーに表示される <code>String</code>
	 * @param modal		ダイアログが表示されているとき、ユーザーのほかのトップレベルのウィンドウへの入力をブロックするかどうかを指定する。
	 * 					<tt>true</tt> の場合、モーダリティータイププロパティーは <code>DEFAULT_MODALITY_TYPE</code> に設定される。そうでない場合、非モーダルダイアログになる
	 * @param gc		ターゲットスクリーンデバイスの <code>GraphicsConfiguration</code>。<em>gc</em> が <tt>null</tt> の場合は、同じ <code>GraphicsConfiguration</code> を所有フレームとして使用
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Frame, String, boolean, GraphicsConfiguration)
	 */
	public ExDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}

	/**
	 * 所有者 <code>Dialog</code> を指定して、タイトルのない非モーダルダイアログを作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する所有者 <code>Dialog</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Dialog)
	 */
	public ExDialog(Dialog owner) {
		super(owner);
	}

	/**
	 * 所有者 <code>Dialog</code> とモーダルかどうかを指定してダイアログを作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する所有者 <code>Dialog</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @param modal		ダイアログが表示されているとき、ユーザーのほかのトップレベルのウィンドウへの入力をブロックするかどうかを指定する。
	 * 					<tt>true</tt> の場合、モーダリティータイププロパティーは <code>DEFAULT_MODALITY_TYPE</code> に設定される。そうでない場合、非モーダルダイアログになる
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Dialog, boolean)
	 */
	public ExDialog(Dialog owner, boolean modal) {
		super(owner, modal);
	}

	/**
	 * タイトルと所有者ダイアログを指定して、非モーダルダイアログを作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する所有者 <code>Dialog</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @param title		ダイアログのタイトルバーに表示される <code>String</code>
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Dialog, String)
	 */
	public ExDialog(Dialog owner, String title) {
		super(owner, title);
	}

	/**
	 * タイトル、モーダルかどうか、所有者 <code>Dialog</code> を指定してダイアログを作成します。 
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する所有者 <code>Dialog</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @param title		ダイアログのタイトルバーに表示される <code>String</code>
	 * @param modal		ダイアログが表示されているとき、ユーザーのほかのトップレベルのウィンドウへの入力をブロックするかどうかを指定する。
	 * 					<tt>true</tt> の場合、モーダリティータイププロパティーは <code>DEFAULT_MODALITY_TYPE</code> に設定される。そうでない場合、非モーダルダイアログになる
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Dialog, String, boolean)
	 */
	public ExDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	/**
	 * タイトル、所有者 <code>Dialog</code>、モーダルであるかどうか、および <code>GraphicsConfiguration</code> を指定してダイアログを作成します。
	 * <b>注：</b>
	 * <blockquote>
	 * モーダルダイアログ内に作成されたポップアップコンポーネント <code>(JComboBox、JPopupMenu、JMenuBar)</code> は、軽量コンポーネントになります。
	 * </blockquote> 
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する所有者 <code>Dialog</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @param title		ダイアログのタイトルバーに表示される <code>String</code>
	 * @param modal		ダイアログが表示されているとき、ユーザーのほかのトップレベルのウィンドウへの入力をブロックするかどうかを指定する。
	 * 					<tt>true</tt> の場合、モーダリティータイププロパティーは <code>DEFAULT_MODALITY_TYPE</code> に設定される。そうでない場合、非モーダルダイアログになる
	 * @param gc		ターゲットスクリーンデバイスの <code>GraphicsConfiguration</code>。<em>gc</em> が <tt>null</tt> の場合は、同じ <code>GraphicsConfiguration</code> を所有フレームとして使用
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Dialog, String, boolean, GraphicsConfiguration)
	 */
	public ExDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}

	/**
	 * 所有者 <code>Window</code> と空のタイトルを指定して非モーダルダイアログを作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する <code>Window</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Window)
	 */
	public ExDialog(Window owner) {
		super(owner);
	}

	/**
	 * 所有者 <code>Window</code>、モーダルであるかどうか、および空のタイトルを指定してダイアログを作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する <code>Window</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @param modalityType	ダイアログが表示されているとき、ほかのウィンドウへの入力をブロックするかどうかを指定する。<tt>null</tt> 値とサポートされないモーダリティータイプは、<code>MODELESS</code> に相当する
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Window, ModalityType)
	 */
	public ExDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
	}

	/**
	 * タイトルと所有者 <code>Window</code> を指定して非モーダルダイアログを作成します。 
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する <code>Window</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @param title		ダイアログのタイトルバーに表示される <code>String</code>。ダイアログにタイトルがない場合は <tt>null</tt>
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Window, String)
	 */
	public ExDialog(Window owner, String title) {
		super(owner, title);
	}

	/**
	 * タイトル、所有者 <code>Window</code>、およびモーダルかどうかを指定してダイアログを作成します。 
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する <code>Window</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @param title		ダイアログのタイトルバーに表示される <code>String</code>。ダイアログにタイトルがない場合は <tt>null</tt>
	 * @param modalityType	ダイアログが表示されているとき、ほかのウィンドウへの入力をブロックするかどうかを指定する。<tt>null</tt> 値とサポートされないモーダリティータイプは、<code>MODELESS</code> に相当する
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Window, String, ModalityType)
	 */
	public ExDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
	}

	/**
	 * タイトル、所有者 <code>Window</code>、モーダルであるかどうか、および <code>GraphicsConfiguration</code> を指定してダイアログを作成します。 
	 * <b>注：</b>
	 * <blockquote>
	 * モーダルダイアログ内に作成されたポップアップコンポーネント <code>(JComboBox、JPopupMenu、JMenuBar)</code> は、軽量コンポーネントになります。
	 * </blockquote> 
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する <code>Window</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @param title		ダイアログのタイトルバーに表示される <code>String</code>。ダイアログにタイトルがない場合は <tt>null</tt>
	 * @param modalityType	ダイアログが表示されているとき、ほかのウィンドウへの入力をブロックするかどうかを指定する。<tt>null</tt> 値とサポートされないモーダリティータイプは、<code>MODELESS</code> に相当する
	 * @param gc		ターゲットスクリーンデバイスの <code>GraphicsConfiguration</code>。<em>gc</em> が <tt>null</tt> の場合は、同じ <code>GraphicsConfiguration</code> を所有フレームとして使用
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Window, String, ModalityType, GraphicsConfiguration)
	 */
	public ExDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このダイアログのメインパネルを返す。メインパネルが生成されていない場合は <tt>null</tt> を返す。
	 */
	public JComponent getMainPane() {
		return _mainPane;
	}

	//------------------------------------------------------------
	// Events
	//------------------------------------------------------------

	/**
	 * [OK] ボタンをクリックしたときに、{@link #onClickOkButton(ActionEvent)} から呼び出されるイベントハンドラ。
	 * <tt>true</tt> を返した場合のみ、ダイアログ終了ステータスに {@link #DLGRESULT_OK} がセットされダイアログが閉じられる。
	 * @return ダイアログを閉じる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean doOkAction() {
		return true;
	}

	/**
	 * [Cancel] ボタンをクリックしたとき、[ESC] キーでダイアログが閉じられるとき、ウィンドウのシステムメニューで
	 * ウィンドウを閉じようとしたときに、{@link #onClickCancelButton(ActionEvent)} から呼び出されるイベントハンドラ。
	 * <tt>true</tt> を返した場合のみ、ダイアログ終了ステータスに {@link #DLGRESULT_CANCEL} がセットされダイアログが閉じられる。
	 * @return ダイアログを閉じる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean doCancelAction() {
		return true;
	}

	/**
	 * [Apply] ボタンが押されたときに呼び出される。
	 */
	protected void onClickApplyButton(ActionEvent e) {}

	/**
	 * [OK] ボタンが押されたときに呼び出される。
	 * この実装では、{@link #doOkAction()} が <tt>true</tt> を返した場合のみ、
	 * ダイアログ終了ステータスに <code>DLGRESULT_OK</code> を設定してダイアログを閉じる。
	 */
	protected void onClickOkButton(ActionEvent e) {
		if (doOkAction()) {
			closeDialog(DLGRESULT_OK);
		}
	}

	/**
	 * [Cancel] ボタンが押されたとき、または [ESC] キーによってダイアログが閉じられるときに
	 * 呼び出される。また、ウィンドウのシステムメニューでウィンドウを閉じようとしたときに呼び出されるが、
	 * この場合のみ引数 <em>e</em> には <tt>null</tt> が設定される。
	 * この実装では、{@link #doCancelAction()} が <tt>true</tt> を返した場合のみ、
	 * ダイアログ終了ステータスに <code>DLGRESULT_CANCEL</code> を設定してダイアログを閉じる。
	 */
	protected void onClickCancelButton(ActionEvent e) {
		if (doCancelAction()) {
			closeDialog(DLGRESULT_CANCEL);
		}
	}

	/**
	 * ダイアログの事前初期化を行う。
	 * この実装では、ボタンパネルとメインパネルを生成し、配置する。
	 * @return 常に <tt>true</tt> を返す。
	 */
	@Override
	protected boolean preInitialComponent() {
		// create buttons pane
		JComponent cButtons = createButtonsPane();
		if (cButtons != null) {
			getContentPane().add(cButtons, BorderLayout.SOUTH);
		}
		
		// create Main pane
		_mainPane = createMainPane();
		if (_mainPane != null) {
			getContentPane().add(_mainPane, BorderLayout.CENTER);
		}
		
		return true;
	}

	/**
	 * {@link #isCloseByEscapeKeyEnabled()} が <tt>true</tt> を返すとき、
	 * ユーザーが <code>[ESC]</code> キーによってウィンドウを閉じようとしたときに呼び出されます。
	 * このメソッドでは、{@link #onClickCancelButton(ActionEvent))} をこのメソッドの引数を指定して呼び出します。
	 */
	@Override
	protected void onWindowClosingByEscapeKey(ActionEvent e) {
		onClickCancelButton(e);
	}

	/**
	 * ユーザーが、ウィンドウのシステムメニューでウィンドウを閉じようとしたときに呼び出されます。
	 * このメソッドでは、{@link #onClickCancelButton(ActionEvent))} を引数 <tt>null</tt> で呼び出します。
	 */
	@Override
	protected void onWindowClosing(WindowEvent e) {
		onClickCancelButton(null);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * [Apply] ボタンのオブジェクトを返す。ボタンが生成されていない場合は <tt>null</tt> を返す。
	 */
	protected JButton getApplyButton() {
		return _btnApply;
	}

	/**
	 * [OK] ボタンのオブジェクトを返す。ボタンが生成されていない場合は <tt>null</tt> を返す。
	 */
	protected JButton getOkButton() {
		return _btnOk;
	}

	/**
	 * [Cancel] ボタンのオブジェクトを返す。ボタンが生成されていない場合は <tt>null</tt> を返す。
	 */
	protected JButton getCancelButton() {
		return _btnCancel;
	}

	/**
	 * 標準の [Apply] ボタンを生成する。
	 * [Apply] ボタンを変更する場合は、このメソッドをオーバーライドすること。
	 * @return	生成したボタンオブジェクトを返す。[Apply] ボタンを使用しない場合は <tt>null</tt> を返す。
	 */
	protected JButton createApplyButton() {
		JButton btn = new JButton(CommonMessages.getInstance().Button_Apply);
		btn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickApplyButton(e);
			}
		});
		return btn;
	}

	/**
	 * 標準の [OK] ボタンを生成する。
	 * [OK] ボタンを変更する場合は、このメソッドをオーバーライドすること。
	 * @return	生成したボタンオブジェクトを返す。[OK] ボタンを使用しない場合は <tt>null</tt> を返す。
	 */
	protected JButton createOkButton() {
		JButton btn = new JButton(CommonMessages.getInstance().Button_OK);
		btn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOkButton(e);
			}
		});
		return btn;
	}

	/**
	 * 標準の [Cancel] ボタンを生成する。
	 * [Cancel] ボタンを変更する場合は、このメソッドをオーバーライドすること。
	 * @return	生成したボタンオブジェクトを返す。[Cancel] ボタンを使用しない場合は <tt>null</tt> を返す。
	 */
	protected JButton createCancelButton() {
		JButton btn = new JButton(CommonMessages.getInstance().Button_Cancel);
		btn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickCancelButton(e);
			}
		});
		return btn;
	}

	/**
	 * ダイアログボタンパネルに配置するボタンを生成する。
	 * ボタンを配置しない場合は <tt>null</tt> を返す。
	 * @return	配置するボタンコンポーネントの配列、配置しない場合は <tt>null</tt>
	 */
	protected JButton[] createDefaultButtons() {
		_btnApply  = createApplyButton();
		_btnOk     = createOkButton();
		_btnCancel = createCancelButton();
		
		List<JButton> list = new ArrayList<JButton>(3);
		if (_btnApply != null) {
			list.add(_btnApply);
		}
		if (_btnOk != null) {
			list.add(_btnOk);
			getRootPane().setDefaultButton(_btnOk);
		}
		if (_btnCancel != null) {
			list.add(_btnCancel);
			setCloseByEscapeKeyEnabled(true);
		}
		
		JButton[] buttons = list.toArray(new JButton[list.size()]);
		return buttons;
	}

	/**
	 * ボタンパネルの左端に表示するコンポーネントを生成する。
	 * この実装では、常に <tt>null</tt> を返す。
	 * @return	生成されたコンポーネントを返す。コンポーネントを配置しない場合は <tt>null</tt> を返す。
	 */
	protected Component createLeftComponentInButtonsPane() {
		return null;
	}

	/**
	 * ボタンパネルを生成する。
	 * @return	生成されたコンポーネントを返す。コンポーネントを配置しない場合は <tt>null</tt> を返す。
	 */
	protected JComponent createButtonsPane() {
		Box btnPanel = Box.createHorizontalBox();
		btnPanel.setBorder(createButtonsPaneBorder());
		boolean hasComponent = false;
		
		// left component
		Component cLeft = createLeftComponentInButtonsPane();
		if (cLeft != null) {
			btnPanel.add(cLeft);
			hasComponent = true;
		}
		
		// glue
		btnPanel.add(Box.createHorizontalGlue());
		
		// buttons
		JButton[] buttons = createDefaultButtons();
		if (buttons != null && buttons.length > 0) {
			adjustButtonSize(buttons);
			btnPanel.add(buttons[0]);
			for (int i = 1; i < buttons.length; i++) {
				btnPanel.add(Box.createHorizontalStrut(getDialogButtonSpacing()));
				btnPanel.add(buttons[i]);
			}
			hasComponent = true;
		}
		
		return (hasComponent ? btnPanel : null);
		
	}

	/**
	 * このダイアログのメインパネルを生成する。
	 * @return	生成されたコンポーネントを返す。メインパネルを配置しない場合は <tt>null</tt> を返す。
	 */
	protected JComponent createMainPane() {
		JPanel pnl = new JPanel();
		pnl.setBorder(createMainPaneBorder());
		return pnl;
	}

	/**
	 * 指定された配列のボタンを、各ボタンの {@link javax.swing.JButton#getPreferredSize()} が返す
	 * 最大のサイズに合わせる。
	 * @param buttons	ボタンの配列
	 */
	protected void adjustButtonSize(JButton[] buttons) {
		if (buttons == null || buttons.length <= 1)
			return;
		
		Dimension dmMaxSize = new Dimension();
		for (JButton btn : buttons) {
			Dimension dmSize = btn.getPreferredSize();
			dmMaxSize.width  = Math.max(dmMaxSize.width, dmSize.width);
			dmMaxSize.height = Math.max(dmMaxSize.height, dmSize.height);
		}
		for (JButton btn : buttons) {
			btn.setPreferredSize(dmMaxSize);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
