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
 * @(#)AbBasicDialog.java	3.2.2	2015/10/15 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbBasicDialog.java	2.1.0	2013/08/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbBasicDialog.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import ssac.aadl.common.CommonMessages;
import ssac.util.properties.ExConfiguration;

/**
 * 基本的なボタンを備えたダイアログ・コンポーネントの抽象クラス。
 * このダイアログは、基本的に[Apply]ボタン、[OK]ボタン、[Cancel]ボタンを
 * ダイアログ下端(<code>BorderLayout</code> の <code>SOUTH</code> エリア)に
 * 配置する。<br>
 * ダイアログが閉じられると、その要因は {@link IDialogResult.getDialogResult()} メソッドで
 * 取得できる。このメソッドは基本的に、[OK]ボタンによってダイアログが閉じられた場合は
 * {@link ssac.util.swing.IDialogResult.DialogResult_OK} を返し、[OK]ボタン以外の
 * 要因によって閉じられた場合は {@link ssac.util.swing.IDialogResult.DialogResult_Cancel} を返す。
 * <b>注意：</b>
 * <blockquote>
 * このクラスのコンストラクタを呼び出した直後は、必ず {@link #initialComponent()} を呼び出すこと。
 * {@link #initialComponent()} の呼び出しによって、ダイアログ内のコンポーネントが初期化される。
 * </blockquote>
 * 
 * @version 3.2.2
 * @since 1.17
 */
public abstract class AbBasicDialog extends AbBaseDialog implements IDialogResult
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 4255026989577149103L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** [Apply] ボタンのアクション **/
	private Action			_applyAction;
	/** [OK]ボタンのアクション **/
	private Action			_okAction;
	/** [Cancel]ボタンのアクション **/
	private Action			_cancelAction;

	/** Apply ボタン **/
	private JButton		_btnApply;
	/** Cancel ボタン **/
	private JButton		_btnCancel;
	/** OK ボタン **/
	private JButton		_btnOK;

	/** ダイアログを閉じたときのステータス **/
	private int	_dialogResult = IDialogResult.DialogResult_None;

//	/** 位置情報を保存するフラグ **/
//	private boolean	_flgStoreLocation = true;
//	/** サイズ情報を保存するフラグ **/
//	private boolean	_flgStoreSize = true;
//	/** 最小サイズを維持するフラグ **/
//	private boolean	_flgKeepMinimumSize = false;
	/** このダイアログの状態を保存する際のキープレフィックス **/
	private String		_configPrefix;
	/** このダイアログの状態を保存する <code>ExConfiguration</code> オブジェクト **/
	private ExConfiguration	_configProps;
	/** コンポーネントの初期化が完了していることを示すフラグ **/
	private boolean	_alreadyInitialized = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * モーダルダイアログを、指定されたタイトル、指定された所有者で生成する。
	 * <em>owner</em> が <tt>null</tt> の場合、共有の非表示フレームがダイアログの所有者として設定される。すべてのコンストラクタがこれに従う。
	 * このコンストラクタはコンポーネントのローカルプロパティを <code>JComponent.getDefaultLocale</code> によって返された値に設定する。 
	 * 
	 * @param owner		ダイアログを表示する <code>Frame</code>
	 * @param title		ダイアログのタイトルバーに表示される文字列
	 * @throws HeadlessException	GraphicsEnvironment.isHeadless() が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Frame, String)
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
	 */
	public AbBasicDialog(Frame owner, String title)
	throws HeadlessException
	{
		this(owner, title, true);
	}
	
	/**
	 * モーダルダイアログを、指定されたタイトル、指定された所有者で生成する。
	 * このコンストラクタはコンポーネントのローカルプロパティを <code>JComponent.getDefaultLocale</code> によって返された値に設定する。 
	 * 
	 * @param owner		ダイアログを表示する <tt>null</tt> でない <code>Dialog</code>
	 * @param title		ダイアログのタイトルバーに表示される文字列
	 * @throws HeadlessException	GraphicsEnvironment.isHeadless() が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Dialog, String)
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
	 */
	public AbBasicDialog(Dialog owner, String title)
	throws HeadlessException
	{
		this(owner, title, true);
	}
	
	/**
	 * モーダルまたはモーダルでないダイアログを、タイトルなしで、指定された所有者で生成する。
	 * <em>owner</em> が <tt>null</tt> の場合、共有の非表示フレームがダイアログの所有者として設定される。すべてのコンストラクタがこれに従う。
	 * このコンストラクタはコンポーネントのローカルプロパティを <code>JComponent.getDefaultLocale</code> によって返された値に設定する。 
	 * 
	 * @param owner		ダイアログを表示する <code>Frame</code>
	 * @param modal		モーダルダイアログである場合は <tt>true</tt>。
	 * 					アクティブなときに他のウィンドウが同時にアクティブになることができるダイアログである場合は <tt>false</tt>
	 * @throws HeadlessException	GraphicsEnvironment.isHeadless() が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Frame, boolean)
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
	 */
	public AbBasicDialog(Frame owner, boolean modal)
	throws HeadlessException
	{
		this(owner, null, modal);
	}
	
	/**
	 * モーダルまたはモーダルでないダイアログを、タイトルなしで、指定された所有者で生成する。
	 * このコンストラクタはコンポーネントのローカルプロパティを <code>JComponent.getDefaultLocale</code> によって返された値に設定する。 
	 * 
	 * @param owner		ダイアログを表示する <tt>null</tt> でない <code>Dialog</code>
	 * @param modal		モーダルダイアログである場合は <tt>true</tt>。
	 * 					アクティブなときに他のウィンドウが同時にアクティブになることができるダイアログである場合は <tt>false</tt>
	 * @throws HeadlessException	GraphicsEnvironment.isHeadless() が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Dialog, boolean)
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
	 */
	public AbBasicDialog(Dialog owner, boolean modal)
	throws HeadlessException
	{
		this(owner, null, modal);
	}
	
	/**
	 * モーダルまたはモーダルでないダイアログを、指定されたタイトル、指定された所有者で生成する。
	 * <em>owner</em> が <tt>null</tt> の場合、共有の非表示フレームがダイアログの所有者として設定される。すべてのコンストラクタがこれに従う。
	 * このコンストラクタはコンポーネントのローカルプロパティを <code>JComponent.getDefaultLocale</code> によって返された値に設定する。 
	 * 
	 * @param owner		ダイアログを表示する <code>Frame</code>
	 * @param title		ダイアログのタイトルバーに表示される文字列
	 * @param modal		モーダルダイアログである場合は <tt>true</tt>。
	 * 					アクティブなときに他のウィンドウが同時にアクティブになることができるダイアログである場合は <tt>false</tt>
	 * @throws HeadlessException	GraphicsEnvironment.isHeadless() が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Frame, String, boolean)
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
	 */
	public AbBasicDialog(Frame owner, String title, boolean modal)
	throws HeadlessException
	{
		super(owner, title, modal);
	}

	/**
	 * モーダルまたはモーダルでないダイアログを、指定されたタイトル、指定された所有者で生成する。
	 * このコンストラクタはコンポーネントのローカルプロパティを <code>JComponent.getDefaultLocale</code> によって返された値に設定する。 
	 * 
	 * @param owner		ダイアログを表示する <tt>null</tt> でない <code>Dialog</code>
	 * @param title		ダイアログのタイトルバーに表示される文字列
	 * @param modal		モーダルダイアログである場合は <tt>true</tt>。
	 * 					アクティブなときに他のウィンドウが同時にアクティブになることができるダイアログである場合は <tt>false</tt>
	 * @throws HeadlessException	GraphicsEnvironment.isHeadless() が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Dialog, String, boolean)
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
	 */
	public AbBasicDialog(Dialog owner, String title, boolean modal)
	throws HeadlessException
	{
		super(owner, title, modal);
	}

	/**
	 * モーダルまたはモーダルでないダイアログを、指定されたタイトル、所有者、および <code>GraphicsConfiguration</code> で生成する。
	 * 
	 * @param owner		ダイアログを表示する <code>Frame</code>
	 * @param title		ダイアログのタイトルバーに表示される文字列
	 * @param modal		モーダルダイアログである場合は <tt>true</tt>。
	 * 					アクティブなときに他のウィンドウが同時にアクティブになることができるダイアログである場合は <tt>false</tt>
	 * @param gc		ターゲットスクリーンデバイスの <code>GraphicsConfiguration</code>。<code>gc</code> が <tt>null</tt> の場合は、
	 * 					同じ <code>GraphicsConfiguration</code> を所有フレームとして使用
	 * @throws HeadlessException	GraphicsEnvironment.isHeadless() が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Frame, String, boolean, GraphicsConfiguration)
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
	 */
	public AbBasicDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc)
	throws HeadlessException
	{
		super(owner, title, modal, gc);
	}

	/**
	 * モーダルまたはモーダルでないダイアログを、指定されたタイトル、所有者、および <code>GraphicsConfiguration</code> で生成する。
	 * 
	 * @param owner		ダイアログを表示する <tt>null</tt> でない <code>Dialog</code>
	 * @param title		ダイアログのタイトルバーに表示される文字列
	 * @param modal		モーダルダイアログである場合は <tt>true</tt>。
	 * 					アクティブなときに他のウィンドウが同時にアクティブになることができるダイアログである場合は <tt>false</tt>
	 * @param gc		ターゲットスクリーンデバイスの <code>GraphicsConfiguration</code>。<code>gc</code> が <tt>null</tt> の場合は、
	 * 					同じ <code>GraphicsConfiguration</code> を所有ダイアログとして使用
	 * @throws HeadlessException	GraphicsEnvironment.isHeadless() が <tt>true</tt> を返す場合
	 * @see javax.swing.JDialog#JDialog(Dialog, String, boolean, GraphicsConfiguration)
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
	 */
	public AbBasicDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc)
	throws HeadlessException
	{
		super(owner, title, modal, gc);
	}

	//------------------------------------------------------------
	// Initialize
	//------------------------------------------------------------

	/**
	 * このダイアログ内のすべてのコンポーネントを初期化する。
	 * このメソッドを呼び出した直後に、初期化済みフラグが <tt>true</tt> に設定される。
	 * @throws IllegalStateException	このメソッドが 2 回以上呼び出された場合
	 */
	public void initialComponent() {
		// check
		if (_alreadyInitialized) {
			throw new IllegalStateException("This component already initialized.");
		}
		_alreadyInitialized = true;
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		
		// setup buttons
		setupButtons();
		
		// setup main contents
		setupMainContents();
		
		// setup dialog conditions
		setupDialogConditions();
		
		// setup actions
		setupActions();
	}

	/**
	 * ダイアログ下端に表示するボタンのセットアップ。
	 */
	protected void setupButtons() {
		// create button component instance
		JComponent component = createButtonsPanel();
		
		// layout button panel
		if (component != null) {
			this.getContentPane().add(component, BorderLayout.SOUTH);
		}
		
		// setup default button
		setupDefaultButton();
		
		// setup escape key bind
		setupEscapeKeyBind();
	}

	/**
	 * 標準のボタンを設定する。
	 * [OK]ボタンが存在する場合は[OK]ボタンを標準ボタンに、
	 * [OK]ボタンが存在しない場合は[Cancel]ボタンを標準ボタンに設定する。
	 * [Cancel]ボタンも存在しない場合は、なにも行わない。
	 * <p>
	 * 標準ボタンの設定には {@link javax.swing.JButton#setDefaultCapable(boolean)} の引数に <tt>true</tt> を
	 * 指定し、このダイアログのルートペインに標準ボタンとするボタンを設定する。
	 * <p>
	 * このメソッドは、{@link #setupButtons()} から呼び出される。
	 */
	protected void setupDefaultButton() {
		if (_btnOK != null) {
			_btnOK.setDefaultCapable(true);
			this.getRootPane().setDefaultButton(_btnOK);
		}
		else if (_btnCancel != null) {
			_btnCancel.setDefaultCapable(true);
			this.getRootPane().setDefaultButton(_btnCancel);
		}
	}

	/**
	 * [Escape] キーが押されたときに {@link #doClickCancelButton()} が呼び出されるよう、
	 * [Escape] キーアクションを設定する。
	 * <p>
	 * このメソッドは、{@link #setupButtons()} から呼び出される。
	 */
	protected void setupEscapeKeyBind() {
		// create action
		Action action = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent ae) {
				onClickCancelButton();
			}
		};
		
		// bind [esc] key
		InputMap imap = this.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel-by-esc");
		this.getRootPane().getActionMap().put("cancel-by-esc", action);
	}

	/**
	 * ダイアログのコンテンツのセットアップ。
	 */
	protected void setupMainContents() {
		// place holder
	}

	/**
	 * ダイアログの状態を初期化する。
	 */
	protected void setupDialogConditions() {
		this.setResizable(false);
		//this.pack();
		this.setLocationRelativeTo(null);
	}

	/**
	 * ダイアログのアクションをセットアップする。
	 */
	protected void setupActions() {
		// Window event
		DialogWindowListener wl = new DialogWindowListener();
		this.addWindowListener(wl);
		
		// Component event
		DialogComponentListener cl = new DialogComponentListener();
		this.addComponentListener(cl);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ダイアログが閉じた要因を返す。
	 */
	public int getDialogResult() {
		return _dialogResult;
	}

	/**
	 * ダイアログの状態を保存するキーのプレフィックスを返す。
	 * プレフィックスが設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getConfigurationPrefix() {
		return _configPrefix;
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

	/**
	 * ダイアログの状態を、このダイアログに設定されている <code>ExConfiguration</code> オブジェクトから
	 * 復元する。
	 */
	public void restoreConfiguration() {
		restoreWindowStatus(getConfiguration(), getConfigurationPrefix());
//		// get configuration
//		String prefix = getConfigurationPrefix();
//		ExConfiguration config = getConfiguration();
//		
//		// restore window size
//		Dimension winSize = null;
//		if (config != null && isStoreSize()) {
//			winSize = config.getWindowSize(prefix);
//		}
//		if (winSize == null) {
//			winSize = getDefaultSize();
//			if (winSize == null) {
//				winSize = this.getPreferredSize();
//			}
//		}
//		this.setSize(winSize);
//		
//		// restore location
//		Point winPos = null;
//		if (config != null && isStoreLocation()) {
//			winPos = config.getWindowLocation(prefix);
//		}
//		if (winPos == null) {
//			winPos = getDefaultLocation();
//		}
//		if (winPos != null) {
//			//winPos = SwingTools.convertIntoDesktop(winPos, winSize);
//			this.setLocation(winPos);
//		} else {
//			//this.setLocationRelativeTo(null);
//			this.setLocationRelativeTo(getOwner());
//		}
//		
//		// adjust screen size
//		Rectangle rcOriginal = getBounds();
//		if (!SwingTools.containsInAllScreenDesktopBounds(rcOriginal)) {
//			Rectangle rcAdjusted = SwingTools.convertIntoAllScreenDesktopBounds(rcOriginal);
//			setBounds(rcAdjusted);
//		}
	}

	/**
	 * ダイアログの状態を、このダイアログに設定されている <code>ExConfiguration</code> オブジェクトに
	 * 保存する。<br>
	 * このメソッドは、ダイアログが閉じるときに自動的に呼び出される。
	 */
	public void storeConfiguration() {
		storeWindowStatus(getConfiguration(), getConfigurationPrefix());
//		// get configuration
//		String prefix = getConfigurationPrefix();
//		ExConfiguration config = getConfiguration();
//		if (config == null) {
//			// no store configurations
//			return;
//		}
//		
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

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	/**
	 * このダイアログが初めて表示される直前に呼び出される。
	 */
	protected void initDialog() {
	}
	
	/**
	 * [Apply]ボタン押下時に呼び出されるイベントハンドラ。
	 * このメソッドがどのような値を返しても、ダイアログは変化しない。
	 */
	protected void doApplyAction() {
		// no entry
	}

	/**
	 * [OK]ボタン押下時に呼び出されるイベントハンドラ。
	 * このメソッドが <tt>true</tt> を返すと、ダイアログが閉じられる。
	 * @return	処理が成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	protected boolean doOkAction() {
		return true;
	}
	
	/**
	 * [Cancel]ボタン押下時、もしくはダイアログが閉じられたときに呼び出されるイベントハンドラ。
	 * このメソッドが <tt>true</tt> を返すと、ダイアログが閉じられる。
	 * @return	処理が成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	protected boolean doCancelAction() {
		return true;
	}

	/**
	 * ダイアログが閉じられるときに、ダイアログを状態を保存するするために呼び出される。
	 */
	protected void doStoreConfiguration() {
		storeConfiguration();
	}

	/**
	 * 指定されたステータスを設定し、ダイアログを閉じる。
	 * @param result	ダイアログが閉じられた要因
	 */
	protected void dialogClose(int result) {
		setDialogResult(result);
		doStoreConfiguration();
		dispose();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * [OK]ボタン押下時に呼び出される。
	 * この動作は、{@link #doOkButtonAction()} メソッドを呼び出し、
	 * そのメソッドが <tt>true</tt> を返した場合のみ、{@link IDialogResult#DialogResult_OK} を
	 * ダイアログリザルトに設定してダイアログを閉じる。
	 */
	protected void onClickOkButton() {
		if (doOkAction()) {
			// close dialog
			dialogClose(IDialogResult.DialogResult_OK);
		}
	}

	/**
	 * [Cancel]ボタン押下時に呼び出される。
	 * この動作は、{@link #doCancelButtonAction(ActionEvent)} メソッドを呼び出し、
	 * そのメソッドが <tt>true</tt> を返した場合のみ、{@link IDialogResult#DialogResult_Cancel} を
	 * ダイアログリザルトに設定してダイアログを閉じる。
	 */
	protected void onClickCancelButton() {
		if (doCancelAction()) {
			// close dialog
			dialogClose(IDialogResult.DialogResult_Cancel);
		}
	}

	/**
	 * ダイアログのリザルトを設定する。
	 * このメソッドで指定する値は、{@link IDialogResult} の定数を設定する。
	 * @param result	このダイアログのリザルトとする {@link IDialogResult} の静的定数
	 */
	protected void setDialogResult(int result) {
		_dialogResult = result;
	}

//	/**
//	 * ダイアログ位置の保存が許可されている場合は <tt>true</tt> を返す。
//	 */
//	protected boolean isStoreLocation() {
//		return _flgStoreLocation;
//	}
//
//	/**
//	 * ダイアログサイズの保存が許可されている場合は <tt>true</tt> を返す。
//	 */
//	protected boolean isStoreSize() {
//		return _flgStoreSize;
//	}
//
//	/**
//	 * ダイアログ位置の保存を許可もしくは禁止する。
//	 * @param toAllow	保存を許可する場合は <tt>true</tt>、禁止する場合は <tt>false</tt>
//	 */
//	protected void setStoreLocation(boolean toAllow) {
//		_flgStoreLocation = toAllow;
//	}
//
//	/**
//	 * ダイアログサイズの保存を許可もしくは禁止する。
//	 * @param toAllow	保存を許可する場合は <tt>true</tt>、禁止する場合は <tt>false</tt>
//	 */
//	protected void setStoreSize(boolean toAllow) {
//		_flgStoreSize = toAllow;
//	}
//
//	/**
//	 * ダイアログの最小サイズを維持する場合に <tt>true</tt> を返す。
//	 */
//	protected boolean isKeepMinimumSize() {
//		return _flgKeepMinimumSize;
//	}
//
//	/**
//	 * ダイアログ最小サイズを維持するかどうかを設定する。
//	 * @param toAllow	最小サイズを維持する場合は <tt>true</tt>、維持しない場合は <tt>false</tt>
//	 */
//	protected void setKeepMinimumSize(boolean toAllow) {
//		_flgKeepMinimumSize = toAllow;
//	}

	/**
	 * このダイアログの標準位置を返す。
	 * @return	ダイアログの標準位置を返す。標準位置を指定しない場合は <tt>null</tt> を返す。
	 */
	protected Point getDefaultLocation() {
		return null;
	}

//	/**
//	 * このダイアログの標準サイズを返す。
//	 * @return	ダイアログの標準サイズを返す。標準サイズを指定しない場合は <tt>null</tt> を返す。
//	 */
//	protected Dimension getDefaultSize() {
//		return null;
//	}

	/**
	 * [Apply]ボタンのオブジェクトを返す。
	 * @return	<code>JButton</code> オブジェクトを返す。ボタンが作成されていない場合は <tt>null</tt> を返す。
	 */
	protected JButton getApplyButton() {
		return _btnApply;
	}
	
	/**
	 * [OK]ボタンのオブジェクトを返す。
	 * @return	<code>JButton</code> オブジェクトを返す。ボタンが作成されていない場合は <tt>null</tt> を返す。
	 */
	protected JButton getCancelButton() {
		return _btnCancel;
	}
	
	/**
	 * [Cancel]ボタンのオブジェクトを返す。
	 * @return	<code>JButton</code> オブジェクトを返す。ボタンが作成されていない場合は <tt>null</tt> を返す。
	 */
	protected JButton getOkButton() {
		return _btnOK;
	}

	/**
	 * {@link #getApplyButtonAction()} メソッドが返すアクションで、[Apply]ボタンを作成する。
	 * {@link #getApplyButtonAction()} メソッドが <tt>null</tt> を返した場合、アクションが未定義のボタンを生成する。
	 * このメソッドは、{@link #createButtons()} 内でボタンをセットアップする際に呼び出される。
	 * @return	<code>JButton</code> オブジェクトを返す。ボタンを作成しない場合は <tt>null</tt> を返す。
	 */
	protected JButton createApplyButton() {
		JButton btn;
		Action action = getApplyButtonAction();
		if (action == null) {
			btn = new JButton(getDefaultApplyButtonCaption());
		} else {
			btn = new JButton(action);
		}
		return btn;
	}
	
	/**
	 * {@link #getOkButtonAction()} メソッドが返すアクションで、[OK]ボタンを作成する。
	 * {@link #getOkButtonAction()} メソッドが <tt>null</tt> を返した場合、アクションが未定義のボタンを生成する。
	 * [OK]ボタンを作成する。
	 * このメソッドは、{@link #createButtons()} 内でボタンをセットアップする際に呼び出される。
	 * @return	<code>JButton</code> オブジェクトを返す。ボタンを作成しない場合は <tt>null</tt> を返す。
	 */
	protected JButton createOkButton() {
		JButton btn;
		Action action = getOkButtonAction();
		if (action == null) {
			btn = new JButton(getDefaultOkButtonCaption());
		} else {
			btn = new JButton(action);
		}
		return btn;
	}
	
	/**
	 * {@link #getCancelButtonAction()} メソッドが返すアクションで、[Cancel]ボタンを作成する。
	 * {@link #getCancelButtonAction()} メソッドが <tt>null</tt> を返した場合、アクションが未定義のボタンを生成する。
	 * [Cancel]ボタンを作成する。
	 * このメソッドは、{@link #createButtons()} 内でボタンをセットアップする際に呼び出される。
	 * @return	<code>JButton</code> オブジェクトを返す。ボタンを作成しない場合は <tt>null</tt> を返す。
	 */
	protected JButton createCancelButton() {
		JButton btn;
		Action action = getCancelButtonAction();
		if (action == null) {
			btn = new JButton(getDefaultCancelButtonCaption());
		} else {
			btn = new JButton(action);
		}
		return btn;
	}

	/**
	 * [Apply]ボタンの標準のキャプションを取得する。
	 * @return	標準のキャプションとなる文字列
	 */
	protected String getDefaultApplyButtonCaption() {
		return CommonMessages.getInstance().Button_Apply;
	}

	/**
	 * [OK]ボタンの標準のキャプションを取得する。
	 * @return	標準のキャプションとなる文字列
	 */
	protected String getDefaultOkButtonCaption() {
		return CommonMessages.getInstance().Button_OK;
	}
	
	/**
	 * [Cancel]ボタンの標準のキャプションを取得する。
	 * @return	標準のキャプションとなる文字列
	 */
	protected String getDefaultCancelButtonCaption() {
		return CommonMessages.getInstance().Button_Cancel;
	}

	/**
	 * [Apply]ボタンのアクションを取得する。
	 * @return	アクション
	 */
	protected Action getApplyButtonAction() {
		if (_applyAction == null) {
			_applyAction = createApplyButtonAction();
		}
		return _applyAction;
	}

	/**
	 * [OK]ボタンのアクションを取得する。
	 * @return	アクション
	 */
	protected Action getOkButtonAction() {
		if (_okAction == null) {
			_okAction = createOkButtonAction();
		}
		return _okAction;
	}

	/**
	 * [Cancel]ボタンのアクションを取得する。
	 * @return	アクション
	 */
	protected Action getCancelButtonAction() {
		if (_cancelAction == null) {
			_cancelAction = createCancelButtonAction();
		}
		return _cancelAction;
	}

	/**
	 * [Apply]ボタン用のボタンアクションを生成する。
	 * このアクションでは、{@link #doApplyButtonAction()} を呼び出す。
	 * @return	生成されたアクション
	 */
	protected Action createApplyButtonAction() {
		Action action = new AbstractAction(getDefaultApplyButtonCaption()) {
			public void actionPerformed(ActionEvent ae) {
				doApplyAction();
			}
		};
		return action;
	}

	/**
	 * [OK]ボタン用のボタンアクションを生成する。
	 * このアクションでは、{@link #doClickOkButton()} メソッドを呼び出す。
	 * @return	生成されたアクション
	 */
	protected Action createOkButtonAction() {
		Action action = new AbstractAction(getDefaultOkButtonCaption()) {
			public void actionPerformed(ActionEvent ae) {
				onClickOkButton();
			}
		};
		return action;
	}
	
	/**
	 * [Cancel]ボタン用のボタンアクションを生成する。
	 * このアクションでは、{@link #doClickCancelButton()} メソッドを呼び出す。
	 * @return	生成されたアクション
	 */
	protected Action createCancelButtonAction() {
		Action action = new AbstractAction(getDefaultCancelButtonCaption()) {
			public void actionPerformed(ActionEvent ae) {
				onClickCancelButton();
			}
		};
		return action;
	}

	/**
	 * 各ボタンを生成する。
	 * @return	生成されたボタンの配列
	 */
	protected JButton[] createButtons() {
		List<JButton> btnlist = new ArrayList<JButton>(3);
		
		_btnApply = createApplyButton();
		if (_btnApply != null)
			btnlist.add(_btnApply);
		
		_btnOK = createOkButton();
		if (_btnOK != null)
			btnlist.add(_btnOK);
		
		_btnCancel = createCancelButton();
		if (_btnCancel != null)
			btnlist.add(_btnCancel);
		
		return btnlist.toArray(new JButton[btnlist.size()]);
	}

	/**
	 * ボタンのサイズを調整する。
	 * デフォルトの動作では、すべてのボタンの幅と高さを最大のものに合わせる。
	 * 
	 * @param buttons	<code>JButton</code> オブジェクトの配列
	 * @return	ボタンの高さの最大値を返す。ボタンが一つも存在しない場合は 0 を返す。
	 */
	protected int adjustButtonSize(JButton[] buttons) {
		if (buttons == null || buttons.length < 1) {
			return 0;
		}
		
		Dimension dim;
		if (buttons.length > 1) {
			int maxWidth = 0;
			int maxHeight = 0;
			for (JButton btn : buttons) {
				dim = btn.getPreferredSize();
				maxWidth = Math.max(maxWidth, dim.width);
				maxHeight = Math.max(maxHeight, dim.height);
			}
			dim = new Dimension(maxWidth, maxHeight);
			for (JButton btn : buttons) {
				btn.setPreferredSize(dim);
			}
		} else {
			dim = buttons[0].getPreferredSize();
		}
		
		return dim.height;
	}

	/**
	 * このダイアログの下端に配置するボタンのパネルを生成する。
	 * @return	ボタンのパネルとなるコンポーネント
	 */
	protected JComponent createButtonsPanel() {
		JButton[] buttons = createButtons();
		if (buttons == null || buttons.length < 1) {
			// no buttons
			return null;
		}
		
		int maxHeight = adjustButtonSize(buttons);
		
		// Layout
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalGlue());
		for (JButton btn : buttons) {
			btnBox.add(btn);
			btnBox.add(Box.createHorizontalStrut(5));
		}
		btnBox.add(Box.createRigidArea(new Dimension(0, maxHeight+10)));
		return btnBox;
	}
	
	//------------------------------------------------------------
	// Window actions
	//------------------------------------------------------------
	
	protected void onWindowOpened(WindowEvent e) {
		initDialog();
	}
	
	protected void onWindowClosing(WindowEvent e) {
		// [Cancel]ボタンと同様の動作とする
		onClickCancelButton();
	}
	
	protected void onWindowClosed(WindowEvent e) {}
	
	protected void onWindowIconified(WindowEvent e) {}
	
	protected void onWindowDeiconified(WindowEvent e) {}
	
	protected void onWindowActivated(WindowEvent e) {}
	
	protected void onWindowDeactivated(WindowEvent e) {}
	
	protected void onWindowStateChanged(WindowEvent e) {}
	
	protected void onWindowGainedFocus(WindowEvent e) {}
	
	protected void onWindowLostFocus(WindowEvent e) {}
	
	protected class DialogWindowListener implements WindowListener, WindowStateListener, WindowFocusListener
	{
	    public void windowOpened(WindowEvent e) {
	    	adjustWindowBoundsWhenOpened(e);
	    	onWindowOpened(e);
	    }

	    public void windowClosing(WindowEvent e) { onWindowClosing(e); }

	    public void windowClosed(WindowEvent e) { onWindowClosed(e); }

	    public void windowIconified(WindowEvent e) { onWindowIconified(e); }

	    public void windowDeiconified(WindowEvent e) { onWindowDeiconified(e); }

	    public void windowActivated(WindowEvent e) { onWindowActivated(e); }

	    public void windowDeactivated(WindowEvent e) { onWindowDeactivated(e); }

	    public void windowStateChanged(WindowEvent e) { onWindowStateChanged(e); }

	    public void windowGainedFocus(WindowEvent e) { onWindowGainedFocus(e); }

	    public void windowLostFocus(WindowEvent e) { onWindowLostFocus(e); }
	}
	
	//------------------------------------------------------------
	// Component actions
	//------------------------------------------------------------
	
	protected void onMoved(ComponentEvent e) {}
	
	protected void onResized(ComponentEvent e) {}
	
	protected void onShown(ComponentEvent e) {}
	
	protected void onHidden(ComponentEvent e) {}
	
	protected class DialogComponentListener implements ComponentListener {
		//--- Window moved
	    public void componentMoved(ComponentEvent e) {
	    	saveWindowLocationOnMoved(e);
	    	onMoved(e);
	    }
	    
	    //--- Window resized
	    public void componentResized(ComponentEvent e) {
	    	saveWindowSizeOnResized(e);
	    	onResized(e);
//	    	if (isKeepMinimumSize()) {
//	    		Dimension dmMin = getMinimumSize();
//	    		int cw = getSize().width;
//	    		int ch = getSize().height;
//	    		if (dmMin != null && (cw < dmMin.width || ch < dmMin.height)) {
//	    			setSize((cw<dmMin.width)?dmMin.width:cw, (ch<dmMin.height)?dmMin.height:ch);
//	    		}
//	    	}
	    }

	    //--- Window shown
	    public void componentShown(ComponentEvent e) { onShown(e); }

	    //--- Window hidden
	    public void componentHidden(ComponentEvent e) { onHidden(e); }
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
