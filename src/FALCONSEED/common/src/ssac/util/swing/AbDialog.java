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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AbDialog.java	3.2.2	2015/10/15 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbDialog.java	2.0.0	2012/09/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import ssac.util.properties.ExConfiguration;

/**
 * 最も基本的なダイアログの基本実装。
 * このコンポーネントでは、コンポーネントならびにウィンドウのリスナーを登録し、
 * リスナーに対応するインタフェースを実装するためのフレームワークを提供する。
 * コンストラクタでは、{@link #setDefaultCloseOperation(int)} が {@link javax.swing.WindowConstants#DO_NOTHING_ON_CLOSE} を引数として呼び出され、
 * このクラス標準の <code>WindowListener</code> と <code>ComponentListener</code> が設定される。
 * このクラスのコンストラクタを呼び出した直後は、必ず {@link #initialComponent()} を呼び出すこと。
 * {@link #initialComponent()} の呼び出しによって、ダイアログ内のコンポーネントが初期化される。
 * </blockquote>
 * 
 * @version 3.2.2
 * @since 2.0.0
 */
public abstract class AbDialog extends AbBaseDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -5577134230214650308L;

	protected final String KEY_DIALOGCLOSE_BY_ESC = "close.by.escape";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private ComponentListener	_listenerComponentEvents;
	private WindowListener		_listenerWindowEvents;
	private WindowStateListener	_listenerWindowStateEvents;
	private WindowFocusListener	_listenerWindowFocusEvents;

	/** ダイアログの結果となるステータス **/
	private int					_dialogResult = 0;

//	/** 位置情報を保存するフラグ **/
//	private boolean			_flgStoreLocation = true;
//	/** サイズ情報を保存するフラグ **/
//	private boolean			_flgStoreSize = true;
//	/** 最小サイズを維持するフラグ **/
//	private boolean			_flgKeepMinimumSize = false;
	/** このダイアログの状態を保存する際のキープレフィックス **/
	private String			_configPrefix;
	/** このダイアログの状態を保存する <code>ExConfiguration</code> オブジェクト **/
	private ExConfiguration	_configProps;

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
	public AbDialog() {
		super();
		constructAbDialog();
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
	public AbDialog(Frame owner) {
		super(owner);
		constructAbDialog();
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
	public AbDialog(Frame owner, boolean modal) {
		super(owner, modal);
		constructAbDialog();
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
	public AbDialog(Frame owner, String title) {
		super(owner, title);
		constructAbDialog();
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
	public AbDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		constructAbDialog();
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
	public AbDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		constructAbDialog();
	}

	/**
	 * 所有者 <code>Dialog</code> を指定して、タイトルのない非モーダルダイアログを作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する所有者 <code>Dialog</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Dialog)
	 */
	public AbDialog(Dialog owner) {
		super(owner);
		constructAbDialog();
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
	public AbDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		constructAbDialog();
	}

	/**
	 * タイトルと所有者ダイアログを指定して、非モーダルダイアログを作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する所有者 <code>Dialog</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @param title		ダイアログのタイトルバーに表示される <code>String</code>
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Dialog, String)
	 */
	public AbDialog(Dialog owner, String title) {
		super(owner, title);
		constructAbDialog();
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
	public AbDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		constructAbDialog();
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
	public AbDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		constructAbDialog();
	}

	/**
	 * 所有者 <code>Window</code> と空のタイトルを指定して非モーダルダイアログを作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する <code>Window</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Window)
	 */
	public AbDialog(Window owner) {
		super(owner);
		constructAbDialog();
	}

	/**
	 * 所有者 <code>Window</code>、モーダルであるかどうか、および空のタイトルを指定してダイアログを作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する <code>Window</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @param modalityType	ダイアログが表示されているとき、ほかのウィンドウへの入力をブロックするかどうかを指定する。<tt>null</tt> 値とサポートされないモーダリティータイプは、<code>MODELESS</code> に相当する
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Window, ModalityType)
	 */
	public AbDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		constructAbDialog();
	}

	/**
	 * タイトルと所有者 <code>Window</code> を指定して非モーダルダイアログを作成します。 
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param owner		ダイアログを表示する <code>Window</code>。このダイアログに所有者がいない場合は <tt>null</tt> 
	 * @param title		ダイアログのタイトルバーに表示される <code>String</code>。ダイアログにタイトルがない場合は <tt>null</tt>
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Window, String)
	 */
	public AbDialog(Window owner, String title) {
		super(owner, title);
		constructAbDialog();
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
	public AbDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		constructAbDialog();
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
	public AbDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		constructAbDialog();
	}
	
	private final void constructAbDialog() {
		// setup basic window events
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addComponentListener(getDefaultComponentListener());
		addWindowListener(getDefaultWindowListener());
	}

	/**
	 * ウィンドウの初期化を行う。
	 * このメソッドは、コンストラクタ呼び出し直後に、最初に呼び出す。
	 * @return	初期化に成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean initialComponent() {
		if (!preInitialComponent())
			return false;
		
		if (!onInitialComponent())
			return false;
		
		if (!postInitialComponent())
			return false;
		
		// 設定のリストア
		if (hasConfiguration()) {
			onRestoreConfiguration(getConfiguration(), getConfigurationPrefix());
//			Rectangle rcBounds = getBounds();
//			SwingTools.convertIntoAllScreenDesktopBounds(rcBounds);
//			setSize(rcBounds.getSize());
//			setLocation(rcBounds.getLocation());
		} else {
			// サイズの調整
			pack();
			setLocationRelativeTo(null);
		}
		
		return true;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ダイアログの終了ステータスを取得する。
	 * ダイアログの終了ステータスが一度も設定されていない場合、
	 * このメソッドは 0 を返す。
	 * @return	ダイアログの終了ステータス
	 */
	public int getDialogResult() {
		return _dialogResult;
	}

	/**
	 * ダイアログの終了ステータスを設定する。
	 * @param result	新しい終了ステータス
	 */
	public void setDialogResult(int result) {
		_dialogResult = result;
	}

//	/**
//	 * ウィンドウの最小サイズを維持する場合に <tt>true</tt> を返す。
//	 */
//	public boolean isKeepMinimumSize() {
//		return _flgKeepMinimumSize;
//	}
//
//	/**
//	 * ウィンドウの最小サイズを維持するかどうかを設定する。
//	 * @param toAllow	最小サイズを維持する場合は <tt>true</tt>、維持しない場合は <tt>false</tt>
//	 */
//	public void setKeepMinimumSize(boolean toAllow) {
//		if (_flgKeepMinimumSize != toAllow) {
//			_flgKeepMinimumSize = toAllow;
//			if (toAllow) {
//				ensureMinimumSize();
//			}
//		}
//	}
//
//	/**
//	 * ダイアログ位置の保存が許可されている場合は <tt>true</tt> を返す。
//	 */
//	public boolean isStoreLocation() {
//		return _flgStoreLocation;
//	}
//
//	/**
//	 * ダイアログサイズの保存が許可されている場合は <tt>true</tt> を返す。
//	 */
//	public boolean isStoreSize() {
//		return _flgStoreSize;
//	}
//
//	/**
//	 * ダイアログ位置の保存を許可もしくは禁止する。
//	 * @param toAllow	保存を許可する場合は <tt>true</tt>、禁止する場合は <tt>false</tt>
//	 */
//	public void setStoreLocation(boolean toAllow) {
//		_flgStoreLocation = toAllow;
//	}
//
//	/**
//	 * ダイアログサイズの保存を許可もしくは禁止する。
//	 * @param toAllow	保存を許可する場合は <tt>true</tt>、禁止する場合は <tt>false</tt>
//	 */
//	public void setStoreSize(boolean toAllow) {
//		_flgStoreSize = toAllow;
//	}

	/**
	 * このダイアログの標準位置を返す。
	 * @return	ダイアログの標準位置を返す。標準位置を指定しない場合は <tt>null</tt> を返す。
	 */
	public Point getDefaultLocation() {
		return null;
	}

//	/**
//	 * このダイアログの標準サイズを返す。
//	 * @return	ダイアログの標準サイズを返す。標準サイズを指定しない場合は <tt>null</tt> を返す。
//	 */
//	public Dimension getDefaultSize() {
//		return null;
//	}

	/**
	 * <code>[ESC]</code> キーによってウィンドウが閉じられる場合は <tt>true</tt> を返す。
	 */
	public boolean isCloseByEscapeKeyEnabled() {
		InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		Object key = imap.get(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		return (KEY_DIALOGCLOSE_BY_ESC.equals(key));
	}

	/**
	 * <code>[ESC]</code> キーによってウィンドウを閉じるかどうかを設定する。
	 * @param toEnable	キー入力でウィンドウを閉じる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public void setCloseByEscapeKeyEnabled(boolean toEnable) {
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		if (toEnable) {
			// set [ESC] key action
			InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			imap.put(ks, KEY_DIALOGCLOSE_BY_ESC);
			getRootPane().getActionMap().put(KEY_DIALOGCLOSE_BY_ESC, new AbstractAction(){
				@Override
				public void actionPerformed(ActionEvent e) {
					onWindowClosingByEscapeKey(e);
				}
			});
		}
		else {
			// remove [ESC] key action
			InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			Object key = imap.get(ks);
			if (KEY_DIALOGCLOSE_BY_ESC.equals(key)) {
				imap.remove(ks);
			}
			getRootPane().getActionMap().remove(KEY_DIALOGCLOSE_BY_ESC);
		}
	}

	/**
	 * ダイアログの状態を保存するキーのプレフィックスを返す。
	 * プレフィックスが設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getConfigurationPrefix() {
		return _configPrefix;
	}

	/**
	 * ダイアログの状態を保存する <code>ExConfiguration</code> オブジェクトが設定されていれば <tt>true</tt> を返す。
	 */
	public boolean hasConfiguration() {
		return (_configProps != null);
	}

	/**
	 * ダイアログの状態を保存する <code>ExConfiguration</code> オブジェクトを返す。
	 * @return	このダイアログに設定されている <code>ExConfiguration</code> オブジェクトを返す。
	 * 			設定されていない場合は <tt>null</tt> を返す。
	 */
	public ExConfiguration getConfiguration() {
		return _configProps;
	}

	/**
	 * ダイアログの状態を保存する <code>ExConfiguration</code> オブジェクトと、
	 * 保存時のキーのプレフィックスを設定する。
	 * @param prefix	キーのプレフィックスとする文字列
	 * @param config	<code>ExConfiguration</code> オブジェクト
	 */
	public void setConfiguration(String prefix, ExConfiguration config) {
		this._configPrefix = prefix;
		this._configProps  = config;
	}

	//------------------------------------------------------------
	// Window events
	//------------------------------------------------------------
	
	//************************************************************
	// Setup events
	//************************************************************

	/**
	 * コンポーネントの初期化の前に必要な初期化を行う。
	 * このメソッドは、{@link #initialComponent()} の中で最初に呼び出される。
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean preInitialComponent() {
		// place holder
		return true;
	}

	/**
	 * コンポーネントの初期化を行う。
	 * このメソッドは、{@link #initialComponent()} の中で、{@link #preInitialComponent()} の次に呼び出される。
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean onInitialComponent() {
		// place holder
		return true;
	}

	/**
	 * コンポーネントの初期化後の処理を行う。
	 * このメソッドは、{@link #initialComponent()} の中で、{@link #onInitialComponent()} の次に呼び出される。
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean postInitialComponent() {
		// place holder
		return true;
	}

	/**
	 * ダイアログを閉じてリソースを破棄します。
	 * このメソッドでは、<code>hasConfiguration()</code> が <tt>true</tt> を返した場合に <code>onStoreConfiguration()</code> を呼び出し、
	 * 最後に <code>dispose()</code> を実行します。
	 * 通常、このメソッドは {@link #closeDialog(int)} から呼び出されます。
	 * <p><b>注：</b>
	 * <blockquote>
	 * このメソッドの実行では、ダイアログリソースがすでに破棄されているかどうかは判定しません。
	 * <code>onStoreConfiguration</code> が呼び出されたときに、ダイアログリソースがすでに破棄されている場合は、
	 * システムが例外をスローしますので、このメソッドを直接呼び出す際は注意してください。
	 * </blockquote>
	 * @see #dispose()
	 * @see #hasConfiguration()
	 * @see #onStoreConfiguration(ExConfiguration, String)
	 * @see #closeDialog(int)
	 */
	protected void doCloseDialog() {
		if (hasConfiguration()) {
			onStoreConfiguration(getConfiguration(), getConfigurationPrefix());
		}
		this.dispose();
	}

	/**
	 * 指定された値をダイアログ終了ステータスとして設定した後、ダイアログを閉じてリソースを破棄します。
	 * このメソッドではダイアログ終了ステータスを終了した後、{@link #isDisplayable()} が <tt>true</tt> を返した場合のみ、
	 * {@link #doCloseDialog()} を呼び出します。
	 * @see #isDisplayable()
	 * @see #doCloseDialog()
	 */
	protected void closeDialog(int result) {
		setDialogResult(result);
		if (isDisplayable()) {
			doCloseDialog();
		}
	}

	/**
	 * コンポーネントの状態を、設定された <code>ExConfiguration</code> オブジェクトからリストアする。
	 * このメソッドは、{@link #initialComponent()} の中で、{@link #postInitialComponent()} の次に呼び出される。
	 * このコンポーネントに <code>ExConfiguration</code> オブジェクトが設定されていない場合は呼び出されない。
	 * @param config	<code>ExConfiguration</code> オブジェクト(必ず <tt>null</tt> 以外)
	 * @param prefix	プロパティのアクセスに使用するキーのプレフィックス、プレフィックスを使用しない場合は <tt>null</tt>
	 */
	protected void onRestoreConfiguration(final ExConfiguration config, final String prefix) {
		restoreWindowStatus(config, prefix);
//		// restore window size
//		Dimension winSize = null;
//		if (isStoreSize()) {
//			winSize = config.getWindowSize(prefix);
//			if (winSize == null) {
//				getDefaultSize();
//			}
//		} else {
//			winSize = getDefaultSize();
//		}
//		//--- set size
//		if (winSize == null) {
//			this.pack();
//		} else {
//			this.setSize(winSize);
//		}
//		
//		// restore location
//		Point winPos = null;
//		if (isStoreLocation()) {
//			winPos = config.getWindowLocation(prefix);
//			if (winPos == null) {
//				winPos = getDefaultLocation();
//			}
//		} else {
//			winPos = getDefaultLocation();
//		}
//		//--- setLocation
//		if (winPos == null) {
//			this.setLocationRelativeTo(null);
//		} else {
//			this.setLocation(winPos);
//		}
	}
	
	/**
	 * コンポーネントの状態を、設定された <code>ExConfiguration</code> オブジェクトに保存する。
	 * このメソッドは、{@link #doCloseDialog()} の中で、{@link #dispose()} を呼び出す直前に呼び出される。
	 * このコンポーネントに <code>ExConfiguration</code> オブジェクトが設定されていない場合は呼び出されない。
	 * @param config	<code>ExConfiguration</code> オブジェクト(必ず <tt>null</tt> 以外)
	 * @param prefix	プロパティのアクセスに使用するキーのプレフィックス、プレフィックスを使用しない場合は <tt>null</tt>
	 */
	protected void onStoreConfiguration(final ExConfiguration config, final String prefix) {
		storeWindowStatus(config, prefix);
//		// store current window location
//		if (isStoreLocation()) {
//			config.setWindowLocation(prefix, this.getLocationOnScreen());
//		}
//		
//		// store current window size
//		if (isStoreSize()) {
//			Dimension defSize = getDefaultSize();
//			Dimension curSize = getSize();
//			// defSize == curSize なら、プロパティのエントリは null とする
//			if (defSize != null && defSize.equals(curSize)) {
//				curSize = null;
//			}
//			config.setWindowSize(prefix, curSize);
//		}
	}
	
	//************************************************************
	// Component events
	//************************************************************

	/**
	 * ウィンドウの位置が変わると呼び出されます。
	 */
	protected void onWindowMoved(ComponentEvent e) {}

	/**
	 * ウィンドウのサイズが変わると呼び出されます。
	 */
	protected void onWindowResized(ComponentEvent e) {}

	/**
	 * ウィンドウが可視になると呼び出されます。
	 */
	protected void onWindowShown(ComponentEvent e) {}

	/**
	 * ウィンドウが不可視になると呼び出されます。
	 */
	protected void onWindowHidden(ComponentEvent e) {}
	
	//************************************************************
	// Window events
	//************************************************************

	/**
	 * {@link #isCloseByEscapeKeyEnabled()} が <tt>true</tt> を返すとき、
	 * ユーザーが <code>[ESC]</code> キーによってウィンドウを閉じようとしたときに呼び出されます。
	 * このメソッドでは、{@link #closeDialog(int))} を引数 0 で呼び出します。
	 */
	protected void onWindowClosingByEscapeKey(ActionEvent e) {
		closeDialog(0);
	}

	/**
	 * ウィンドウが最初に可視になったときに呼び出されます。
	 */
	protected void onWindowOpened(WindowEvent e) {}

	/**
	 * ユーザーが、ウィンドウのシステムメニューでウィンドウを閉じようとしたときに呼び出されます。
	 * このメソッドでは、{@link #closeDialog(int))} を引数 0 で呼び出します。
	 * @param e
	 */
	protected void onWindowClosing(WindowEvent e) {
		closeDialog(0);
	}

	/**
	 * ウィンドウに対する <code>dispose</code> の呼び出し結果として、ウィンドウがクローズされたときに呼び出されます。
	 */
	protected void onWindowClosed(WindowEvent e) {}

	/**
	 * ウィンドウが通常の状態から最小化された状態に変更されたときに呼び出されます。
	 */
	protected void onWindowIconified(WindowEvent e) {}

	/**
	 * ウィンドウが最小化された状態から通常の状態に変更されたときに呼び出されます。
	 */
	protected void onWindowDeiconified(WindowEvent e) {}

	/**
	 * ウィンドウがアクティブウィンドウに設定されると呼び出されます。
	 */
	protected void onWindowActivated(WindowEvent e) {}

	/**
	 * ウィンドウがアクティブウィンドウでなくなったときに呼び出されます。
	 */
	protected void onWindowDeactivated(WindowEvent e) {}
	
	//************************************************************
	// Window state events
	//************************************************************

	/**
	 * {@link #isWindowStateEventsEnabled()} が <tt>true</tt> を返すとき、
	 * ウィンドウの状態が変更されると呼び出されます。
	 */
	protected void onWindowStateChanged(WindowEvent e) {}
	
	//************************************************************
	// Window focus events
	//************************************************************

	/**
	 * {@link #isWindowFocusEventsEnabled()} が <tt>true</tt> を返すとき、
	 * ウィンドウがフォーカスされたウィンドウに設定されたときに呼び出されます。
	 */
	protected void onWindowGainedFocus(WindowEvent e) {}
	
	/**
	 * {@link #isWindowFocusEventsEnabled()} が <tt>true</tt> を返すとき、
	 * ウィンドウがフォーカスされたウィンドウではなくなったときに呼び出されます。
	 */
	protected void onWindowLostFocus(WindowEvent e) {}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * ダイアログボタンパネルに配置するボタンの間隔を取得する。
	 * @return	ボタンの間隔
	 */
	protected int getDialogButtonSpacing() {
		return 5;
	}

	/**
	 * ダイアログメインパネルのボーダーを生成する。
	 * 通常、ダイアログボーダーならびにボタンパネルとの間隔を確保するためのボーダーを生成する。
	 * @return	生成されたボーダー、もしくは <tt>null</tt>
	 */
	protected Border createMainPaneBorder() {
		return BorderFactory.createEmptyBorder(5,5,5,5);
	}

	/**
	 * ダイアログボタンパネルのボーダーを生成する。
	 * 通常、ダイアログボーダーならびにメインパネルとの間隔を確保するためのボーダーを生成する。
	 * @return	生成されたボーダー、もしくは <tt>null</tt>
	 */
	protected Border createButtonsPaneBorder() {
		return BorderFactory.createEmptyBorder(5,5,5,5);
	}

//	/**
//	 * {@link #isKeepMinimumSize()} が返す状態に関係なく、
//	 * ウィンドウのサイズが <code>getMinimumSize()</code> が返すサイズよりも小さいときに
//	 * その最小サイズに合わせます。
//	 */
//	protected void ensureMinimumSize() {
//		Dimension dmMin = getMinimumSize();
//		if (dmMin != null && (getWidth() < dmMin.width || getHeight() < dmMin.height)) {
//			setSize(Math.max(getWidth(), dmMin.width), Math.max(getHeight(), dmMin.height));
//		}
//	}

	/**
	 * このクラス標準の <code>WindowStateListener</code> が設定されている場合に <tt>true</tt> を返します。
	 */
	protected boolean isWindowStateEventsEnabled() {
		if (_listenerWindowStateEvents != null) {
			WindowStateListener[] listeners = getWindowStateListeners();
			for (WindowStateListener l : listeners) {
				if (l == _listenerWindowStateEvents) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * このクラス標準の <code>WindowStateListener</code> の有効／無効を設定します。
	 * @param toEnable	有効にする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected void enableWindowStateEvents(boolean toEnable) {
		boolean curEnabled = isWindowStateEventsEnabled();
		if (!curEnabled && toEnable) {
			addWindowStateListener(getDefaultWindowStateListener());
		}
		else if (curEnabled && !toEnable) {
			removeWindowStateListener(_listenerWindowStateEvents);
			_listenerWindowStateEvents = null;
		}
	}

	/**
	 * このクラス標準の <code>WindowFocusListener</code> が設定されている場合に <tt>true</tt> を返します。
	 */
	protected boolean isWindowFocusEventsEnabled() {
		if (_listenerWindowFocusEvents != null) {
			WindowFocusListener[] listeners = getWindowFocusListeners();
			for (WindowFocusListener l : listeners) {
				if (l == _listenerWindowFocusEvents) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * このクラス標準の <code>WindowFocusListener</code> の有効／無効を設定します。
	 * @param toEnable	有効にする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected void enableWindowFocusEvents(boolean toEnable) {
		boolean curEnabled = isWindowFocusEventsEnabled();
		if (!curEnabled && toEnable) {
			addWindowFocusListener(getDefaultWindowFocusListener());
		}
		else if (curEnabled && !toEnable) {
			removeWindowFocusListener(_listenerWindowFocusEvents);
			_listenerWindowFocusEvents = null;
		}
	}
	
	protected ComponentListener getDefaultComponentListener() {
		if (_listenerComponentEvents == null) {
			_listenerComponentEvents = new ComponentListener(){
				@Override
				public void componentShown(ComponentEvent e) {
					onWindowShown(e);
				}
				
				@Override
				public void componentHidden(ComponentEvent e) {
					onWindowHidden(e);
				}

				@Override
				public void componentMoved(ComponentEvent e) {
					saveWindowLocationOnMoved(e);
					onWindowMoved(e);
				}

				@Override
				public void componentResized(ComponentEvent e) {
					saveWindowSizeOnResized(e);
					onWindowResized(e);
				}
			};
		}
		return _listenerComponentEvents;
	}
	
	protected WindowListener getDefaultWindowListener() {
		if (_listenerWindowEvents == null) {
			_listenerWindowEvents = new WindowListener(){
				@Override
				public void windowOpened(WindowEvent e) {
					adjustWindowBoundsWhenOpened(e);
					onWindowOpened(e);
				}

				@Override
				public void windowClosing(WindowEvent e) {
					onWindowClosing(e);
				}

				@Override
				public void windowClosed(WindowEvent e) {
					onWindowClosed(e);
				}
				
				@Override
				public void windowActivated(WindowEvent e) {
					onWindowActivated(e);
				}

				@Override
				public void windowDeactivated(WindowEvent e) {
					onWindowDeactivated(e);
				}

				@Override
				public void windowIconified(WindowEvent e) {
					onWindowIconified(e);
				}

				@Override
				public void windowDeiconified(WindowEvent e) {
					onWindowDeiconified(e);
				}
			};
		}
		return _listenerWindowEvents;
	}
	
	protected WindowStateListener getDefaultWindowStateListener() {
		if (_listenerWindowStateEvents == null) {
			_listenerWindowStateEvents = new WindowStateListener(){
				@Override
				public void windowStateChanged(WindowEvent e) {
					onWindowStateChanged(e);
				}
			};
		}
		return _listenerWindowStateEvents;
	}
	
	protected WindowFocusListener getDefaultWindowFocusListener() {
		if (_listenerWindowFocusEvents == null) {
			_listenerWindowFocusEvents = new WindowFocusListener(){
				@Override
				public void windowGainedFocus(WindowEvent e) {
					onWindowGainedFocus(e);
				}

				@Override
				public void windowLostFocus(WindowEvent e) {
					onWindowLostFocus(e);
				}
			};
		}
		return _listenerWindowFocusEvents;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
