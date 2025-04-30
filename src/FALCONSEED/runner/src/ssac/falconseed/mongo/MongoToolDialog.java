/*
 * @(#)MongoToolDialog.java	3.4.0	2020/03/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mongo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Base64;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONEventType;
import net.arnx.jsonic.JSONReader;
import redundantalge.db.mongo.MongoAlgeError;
import redundantalge.db.mongo.MongoConnectionError;
import redundantalge.db.mongo.MongoServerURI;
import redundantalge.db.mongo.MongoSession;
import redundantalge.db.mongo.MongoUtil;
import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.RunnerResources;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.logging.AppLogger;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.MaskedNumberFormatter;
import ssac.util.swing.ProgressMonitorTask;
import ssac.util.swing.menu.ToolBarButton;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * MongoDBツール・ダイアログ
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class MongoToolDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	static private final Dimension DM_MIN_SIZE = new Dimension(640, 480);
	
	static public final int	EMPTY_ROWHEADER_WIDTH	= 10;
	
	static protected final char		CHAR_PASSWORD_MASK	= '*';
	static protected final String	DEFAULT_AUTHDB_NAME	= "admin";
	
	static protected final String	SUBKEY_MONGO_HOST				= ".mongo.hostname";
	static protected final String	SUBKEY_MONGO_PORT				= ".mongo.port";
	static protected final String	SUBKEY_MONGO_USER				= ".mongo.user";
	static protected final String	SUBKEY_MONGO_PASS				= ".mongo.pass";
	static protected final String	SUBKEY_MONGO_DBNAME				= ".mongo.database";
	static protected final String	SUBKEY_MONGO_AUTHDB_USE			= ".mongo.authdb.use";
	static protected final String	SUBKEY_MONGO_AUTHDB_NAME		= ".mongo.authdb.name";
	static protected final String	SUBKEY_MCOLS_NEW_WITH_OBJID		= ".mongo.cols.new.import.withObjectId";
	static protected final String	SUBKEY_MCOLS_IMPORT_WITH_OBJID	= ".mongo.cols.import.withObjectId";
	static protected final String	SUBKEY_MCOLS_EXPORT_WITH_OBJID	= ".mongo.cols.export.withObjectId";
	static protected final String	SUBKEY_IMPORT_REPLACE			= ".import.type.replace";
	static protected final String	SUBKEY_IMPORT_JSON				= ".import.json";
	static protected final String	SUBKEY_EXPORT_JSON				= ".export.json";
	
	static protected final String	CMD_MONGO_SHOW_PASSWORD		= "mongo.password.show";
	static protected final String	CMD_MONGO_USE_AUTHDB		= "mongo.authdb.use";
	static protected final String	CMD_MONGO_CONNECT			= "mongo.connect";
	static protected final String	CMD_MONGO_DISCONNECT		= "mongo.disconnect";
	static protected final String	CMD_MCOLS_REFRESH			= "mcols.refresh";
	static protected final String	CMD_MCOLS_DELETE			= "mcols.del";
	static protected final String	CMD_MCOLS_NEW_CREATE		= "mcols.new.create";
	static protected final String	CMD_MCOLS_NEW_IMPORT_JSON	= "mcols.new.import.json";
	static protected final String	CMD_MCOLS_IMPORT_JSON		= "mcols.import.json";
	static protected final String	CMD_MCOLS_IMPORT_APPEND		= "mcols.import.append";
	static protected final String	CMD_MCOLS_IMPORT_REPLACE	= "mcols.import.replace";
	static protected final String	CMD_MCOLS_EXPORT_JSON		= "mcols.export.json";
	
	static protected final int		MONGO_STATE_DISCONNECTED	= 0;
	static protected final int		MONGO_STATE_DISCONNECTING	= 1;
	static protected final int		MONGO_STATE_CONNECTING		= 10;
	static protected final int		MONGO_STATE_CONNECTED		= 11;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** MongoDB への接続状態 **/
	private int				_mongoSessionStatus = MONGO_STATE_DISCONNECTED;
	/** MongoDB セッション **/
	private MongoSession	_msession;
	
	/** Mongo 接続設定とコレクション一覧を分割するペイン **/
	private JSplitPane		_splitTop;
	/** コレクションテーブルと機能パネルを分割するペイン **/
	private JSplitPane		_splitHorz;
	
	/** MongoDB ホスト名エディットボックス **/
	private JTextField		_editMongoHost;
	/** MongoDB ポート番号エディットボックス **/
	private JNetworkPortNumberTextField	_editMongoPort;
	/** MongoDB データベース名エディットボックス **/
	private JTextField		_editMongoDbName;
	/** MongoDB ユーザー名エディットボックス **/
	private JTextField		_editMongoUser;
	/** MongoDB パスワードエディットボックス **/
	private JPasswordField	_editMongoPass;
	/** パスワード表示チェックボックス **/
	private JCheckBox		_chkShowPass;
	/** 認証データベース名を指定するチェックボックス **/
	private JCheckBox		_chkUseAuthDb;
	/** 認証データベース名を指定するかどうかの直前のステータス **/
	private boolean			_lastUseAuthDb;
	/** MongoDB 認証データベース名エディットボックス **/
	private JTextField		_editMongoAuthDb;
	/** MongoDB 認証データベース名のユーザー入力保存、デフォルトは admin **/
	private String			_strMongoAuthDb;
	/** MongoDB 接続状態を表すアイコンラベル **/
	private JLabel			_lblMongoSessionStatus;
	/** MongoDB 接続ボタン **/
	private JButton			_btnMongoConnect;
	/** MongoDB 切断ボタン **/
	private JButton			_btnMongoDisconnect;
	
	/** MongoDB に関する機能ボタン **/
	private JToolBar		_tbarFunctions;
	/** コレクションリストの更新ボタン **/
	private JButton	_btnToolRefreshCollections;
	/** コレクションの削除ボタン **/
	private JButton	_btnToolDelCollection;
	
	/** 現在のコレクション一覧を保持するテーブルモデル **/
	private MongoToolSortedCollectionTableModel	_tableModel;
	/** コレクション一覧テーブル **/
	private SpreadSheetTable	_table;

	/** 新規コレクション作成機能パネル **/
	private JPanel			_pnlNewCollection;
	/** コレクションインポート機能パネル **/
	private JPanel			_pnlImportCollection;
	/** コレクションエクスポート機能パネル **/
	private JPanel			_pnlExportCollection;
	/** 新規コレクション名エディットボックスのラベル **/
	private JLabel			_lblNewCollectionName;
	/** 新規コレクション名エディットボックス **/
	private JTextField		_editNewCollectionName;
	/** 新規コレクション作成ボタン **/
	private JButton			_btnNewCollectionCreate;
	/** インポートにオブジェクト ID を含めることを選択するチェックボックス **/
	private JCheckBox		_chkNewCollectionImportWithObjectId;
	/** 新規コレクションのインポートボタン **/
	private JButton			_btnNewCollectionImportJson;
	/** インポート先のコレクションに追加するラジオボタン **/
	private JRadioButton	_rdoImportCollectionAppend;
	/** インポート先のコレクションを置き換えるラジオボタン **/
	private JRadioButton	_rdoImportCollectionReplace;
	/** インポートにオブジェクト ID を含めることを選択するチェックボックス **/
	private JCheckBox		_chkImportCollectionWithObjectId;
	/** JSON ファイルをインポートするボタン **/
	private JButton			_btnImportCollectionJson;
	/** エクスポートにオブジェクトID を含めることを選択するチェックボックス **/
	private JCheckBox		_chkExportCollectionWithObjectId;
	/** JSON ファイルにエクスポートするボタン **/
	private JButton			_btnExportCollectionJson;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MongoToolDialog(Frame owner, boolean modal) {
		super(owner, RunnerMessages.getInstance().MongoToolDlg_title, modal);
		setConfiguration(AppSettings.MONGODB_TOOL_DLG, AppSettings.getInstance().getConfiguration());
		//RunnerFrame mainframe = (RunnerFrame)ModuleRunner.getApplicationMainFrame();
		//_vfDataUserRoot = mainframe.getDataFileUserRootDirectory();
		//_vfFormatter = mainframe.getDataFilePathFormatter();
		//_vfExcelFile = ModuleFileManager.fromJavaFile(excelfile);
	}
	
	public MongoToolDialog(Dialog owner, boolean modal) {
		super(owner, RunnerMessages.getInstance().MongoToolDlg_title, modal);
		setConfiguration(AppSettings.MONGODB_TOOL_DLG, AppSettings.getInstance().getConfiguration());
		//RunnerFrame mainframe = (RunnerFrame)ModuleRunner.getApplicationMainFrame();
		//_vfDataUserRoot = mainframe.getDataFileUserRootDirectory();
		//_vfFormatter = mainframe.getDataFilePathFormatter();
		//_vfExcelFile = ModuleFileManager.fromJavaFile(excelfile);
	}
	
	@Override
	public void initialComponent() {
		// コンポーネント生成
		createComponents();
		
		// コンポーネントの初期化
		super.initialComponent();
		
//		// Excel ファイルの表示
//		_stcExcelFilePath.setText(_editPane.formatDestPath(_vfExcelFile));
//		
//		// データモデルを設定
//		_editPane.setModel(_datamodel);
		
		// 設定情報の反映
		restoreConfiguration();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public void restoreConfiguration() {
		super.restoreConfiguration();
		
		// defaults
		String  mongoHost = "";
		int     mongoPort = MongoUtil.MONGO_DEFAULT_PORT;
		String  mongoDbName = "";
		String  mongoUser   = "";
		String  mongoPass   = "";
		boolean mongoAuthDbUse = false;
		String  mongoAuthDbName = DEFAULT_AUTHDB_NAME;
		boolean flgNewColsImportWithObjId = false;
		boolean flgImportWithObjectId = false;
		boolean flgExportWithObjectId = false;
		boolean flgImportReplace = false;
		
		// restore
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null) {
			//--- host
			mongoHost = config.getString(prefix+SUBKEY_MONGO_HOST, mongoHost);
			//--- port
			mongoPort = config.getIntegerValue(prefix+SUBKEY_MONGO_PORT, mongoPort);
			//--- database
			mongoDbName = config.getString(prefix+SUBKEY_MONGO_DBNAME, mongoDbName);
			//--- user
			mongoUser = config.getString(prefix+SUBKEY_MONGO_USER, mongoUser);
			//--- pass
			String b64str = config.getString(prefix+SUBKEY_MONGO_PASS, "");
			if (!b64str.isEmpty()) {
				try {
					byte[] bytes = b64str.getBytes(StandardCharsets.UTF_8);
					Base64.Decoder b64decoder = Base64.getUrlDecoder();
					byte[] result = b64decoder.decode(bytes);
					mongoPass = new String(result, StandardCharsets.UTF_8);
				}
				catch (Throwable ex) {
					String msg = String.format("Failed to decode MongoDB password from Configuration[%s]", prefix+SUBKEY_MONGO_PASS);
					AppLogger.warn(msg, ex);
				}
			}
			//--- authdb
			mongoAuthDbUse = config.getBooleanValue(prefix+SUBKEY_MONGO_AUTHDB_USE, mongoAuthDbUse);
			mongoAuthDbName = config.getString(prefix+SUBKEY_MONGO_AUTHDB_NAME, mongoAuthDbName);
			//--- with ObjectId
			flgNewColsImportWithObjId = config.getBooleanValue(prefix+SUBKEY_MCOLS_NEW_WITH_OBJID, flgNewColsImportWithObjId);
			flgImportWithObjectId     = config.getBooleanValue(prefix+SUBKEY_MCOLS_IMPORT_WITH_OBJID, flgImportWithObjectId);
			flgExportWithObjectId     = config.getBooleanValue(prefix+SUBKEY_MCOLS_EXPORT_WITH_OBJID, flgExportWithObjectId);
			//--- import type
			flgImportReplace = config.getBooleanValue(prefix+SUBKEY_IMPORT_REPLACE, flgImportReplace);
		}
		
		// restore
		_editMongoHost.setText(mongoHost);
		_editMongoPort.setValue(mongoPort);
		_editMongoDbName.setText(mongoDbName);
		_editMongoUser.setText(mongoUser);
		_editMongoPass.setEchoChar(CHAR_PASSWORD_MASK);
		_editMongoPass.setText(mongoPass);
		_lastUseAuthDb = mongoAuthDbUse;
		_chkUseAuthDb.setSelected(mongoAuthDbUse);
		_strMongoAuthDb = mongoAuthDbName;
		_editMongoAuthDb.setText(mongoAuthDbUse ? mongoAuthDbName : "");
		_chkNewCollectionImportWithObjectId.setSelected(flgNewColsImportWithObjId);
		_chkImportCollectionWithObjectId.setSelected(flgImportWithObjectId);
		_chkExportCollectionWithObjectId.setSelected(flgExportWithObjectId);
		if (flgImportReplace) {
			_rdoImportCollectionAppend.setSelected(false);
			_rdoImportCollectionReplace.setSelected(true);
		} else {
			_rdoImportCollectionAppend.setSelected(true);
			_rdoImportCollectionReplace.setSelected(false);
		}
		refreshPasswordTextField();
		refreshAuthDbTextField();
		refreshDisplaySessionStatus();
		refreshDisplayCollectionTablePanel();
	}

	@Override
	public void storeConfiguration() {
		super.storeConfiguration();
		
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config == null) {
			return;
		}
		
		String strValue;
		//--- Mongo host
		strValue = _editMongoHost.getText();
		if (strValue != null && !strValue.isEmpty())
			config.setString(prefix+SUBKEY_MONGO_HOST, strValue);
		else
			config.clearProperty(prefix+SUBKEY_MONGO_HOST);
		//--- Mongo port
		int port = MongoUtil.MONGO_DEFAULT_PORT;
		try {
			port = Integer.parseInt(_editMongoPort.getText());
		} catch (Throwable ex) {
			port = MongoUtil.MONGO_DEFAULT_PORT;
		}
		if (port > 0 && port != MongoUtil.MONGO_DEFAULT_PORT)
			config.setIntegerValue(prefix+SUBKEY_MONGO_PORT, port);
		else
			config.clearProperty(prefix+SUBKEY_MONGO_PORT);
		//--- Mongo database
		strValue = _editMongoDbName.getText();
		if (strValue != null && !strValue.isEmpty())
			config.setString(prefix+SUBKEY_MONGO_DBNAME, strValue);
		else
			config.clearProperty(prefix+SUBKEY_MONGO_DBNAME);
		//--- Mongo user
		strValue = _editMongoUser.getText();
		if (strValue != null && !strValue.isEmpty())
			config.setString(prefix+SUBKEY_MONGO_USER, strValue);
		else
			config.clearProperty(prefix+SUBKEY_MONGO_USER);
		//--- Mongo pass
		char[] pass = _editMongoPass.getPassword();
		if (pass != null && pass.length > 0) {
			Base64.Encoder b64encoder = Base64.getUrlEncoder();
			String encoded = b64encoder.encodeToString(String.valueOf(pass).getBytes(StandardCharsets.UTF_8));
			config.setString(prefix+SUBKEY_MONGO_PASS, encoded);
			Arrays.fill(pass, '\0');
		}
		else
			config.clearProperty(prefix+SUBKEY_MONGO_PASS);
		//--- Mongo authdb
		config.setBooleanValue(prefix+SUBKEY_MONGO_AUTHDB_USE, _chkUseAuthDb.isSelected());
		if (_chkUseAuthDb.isSelected()) {
			strValue = (_chkUseAuthDb.isSelected() ? _editMongoAuthDb.getText() : _strMongoAuthDb);
			if (strValue != null && !strValue.isEmpty() && !DEFAULT_AUTHDB_NAME.equals(strValue))
				config.setString(prefix+SUBKEY_MONGO_AUTHDB_NAME, strValue);
			else
				config.clearProperty(prefix+SUBKEY_MONGO_AUTHDB_NAME);
		}
		else {
			config.clearProperty(prefix+SUBKEY_MONGO_AUTHDB_NAME);
		}
		//--- With Object ID
		config.setBooleanValue(prefix+SUBKEY_MCOLS_NEW_WITH_OBJID, _chkNewCollectionImportWithObjectId.isSelected());
		config.setBooleanValue(prefix+SUBKEY_MCOLS_IMPORT_WITH_OBJID, _chkImportCollectionWithObjectId.isSelected());
		config.setBooleanValue(prefix+SUBKEY_MCOLS_EXPORT_WITH_OBJID, _chkExportCollectionWithObjectId.isSelected());
		//--- import type
		config.setBooleanValue(prefix+SUBKEY_IMPORT_REPLACE, _rdoImportCollectionReplace.isSelected());
	}

	/**
	 * ダイアログが表示されている場合はダイアログを閉じ、リソースを開放する。
	 * このメソッドでは、{@link java.awt.Window#dispose()} を呼び出す。
	 */
	public void destroy() {
		if (this.isDisplayable()) {
			dialogClose(DialogResult_Cancel);
		}
	}

	//------------------------------------------------------------
	// Event handlers
	//------------------------------------------------------------
	
	protected void onClickedActionButton(ActionEvent ae) {
		String cmd = ae.getActionCommand();
		switch (cmd) {
			case CMD_MCOLS_NEW_CREATE:
				onClickedNewCollectionCreateButton();
				break;
			case CMD_MCOLS_NEW_IMPORT_JSON:
				onClickedNewCollectionImportJsonButton();
				break;
			case CMD_MCOLS_IMPORT_APPEND:
				onClickedImportCollectionAppendRadioButton();
				break;
			case CMD_MCOLS_IMPORT_REPLACE:
				onClickedImportCollectionReplaceRadioButton();
				break;
			case CMD_MCOLS_IMPORT_JSON:
				onClickedImportCollectionJsonButton();
				break;
			case CMD_MCOLS_EXPORT_JSON:
				onClickedExportCollectionJsonButton();
				break;
			case CMD_MCOLS_REFRESH:
				onClickedRefreshCollectionsButton();
				break;
			case CMD_MCOLS_DELETE:
				onClickedDeleteCollectionButton();
				break;
			case CMD_MONGO_DISCONNECT:
				onClickedMongoDisconnectButton();
				break;
			case CMD_MONGO_CONNECT:
				onClickedMongoConnectButton();
				break;
			case CMD_MONGO_SHOW_PASSWORD:
				onClickedShowPasswordCheckBox();
				break;
			case CMD_MONGO_USE_AUTHDB:
				onClickedUseAuthDbCheckBox();
				break;
		}
	}
	
	protected void onClickedUseAuthDbCheckBox() {
		refreshAuthDbTextField();
	}
	
	protected void onClickedShowPasswordCheckBox() {
		refreshPasswordTextField();
	}
	
	protected void onClickedMongoConnectButton() {
		if (_mongoSessionStatus != MONGO_STATE_DISCONNECTED)
			return;
		
		// check parameters
		String host = _editMongoHost.getText();
		if (host==null || host.isEmpty()) {
			Application.showErrorMessage(this, RunnerMessages.getInstance().MongoToolDlg_err_emptyHost);
			_editMongoHost.requestFocus();
			return;
		}
		int port = (Integer)_editMongoPort.getValue();
		String dbName = _editMongoDbName.getText();
		if (dbName==null || dbName.isEmpty()) {
			Application.showErrorMessage(this, RunnerMessages.getInstance().MongoToolDlg_err_emptyDbName);
			_editMongoDbName.requestFocus();
			return;
		}
		String authDbName = (_chkUseAuthDb.isSelected() ? _editMongoAuthDb.getText() : dbName);
		if (authDbName == null || authDbName.isEmpty()) {
			Application.showErrorMessage(this, RunnerMessages.getInstance().MongoToolDlg_err_emptyAuthDb);
			_editMongoAuthDb.requestFocus();
			return;
		}
		String user = _editMongoUser.getText();
		if (user != null && user.isEmpty()) {
			user = null;
		}
		String pass;
		if (user != null && !user.isEmpty()) {
			char[] cpass = _editMongoPass.getPassword();
			if (cpass != null && cpass.length > 0) {
				pass = new String(cpass);
			} else {
				Application.showErrorMessage(this, RunnerMessages.getInstance().MongoToolDlg_err_emptyPassword);
				_editMongoPass.requestFocus();
				return;
			}
		}
		else {
			pass = null;
		}
		final MongoServerURI muri;
		try {
			muri = new MongoServerURI(host, port, authDbName, user, pass, dbName);
		}
		catch (Throwable ex) {
			String msg = RunnerMessages.getInstance().MongoToolDlg_err_access_failed;
			msg += "\r\n";
			msg += MongoUtil.formatExceptionMessage(ex);
			Application.showErrorMessage(MongoToolDialog.this, msg);
			_mongoSessionStatus = MONGO_STATE_DISCONNECTED;
			refreshDisplaySessionStatus();
			requestFocusInWindow();
			return;
		}
		
		// 接続
		_mongoSessionStatus = MONGO_STATE_CONNECTING;
		refreshDisplaySessionStatus();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				java.awt.Cursor oldCursor = MongoToolDialog.this.getCursor();
				MongoToolDialog.this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
				MongoSession msession = null; 
				try {
					//msession = new LocalMongoSession(muri);
					msession = MongoSession.getOrNewSession(muri);
					msession.ensureConnection();
					
					// 接続成功
					//--- MongoDB コレクションテーブルを更新
					_tableModel.setMongoSession(msession);
				}
				catch (Throwable ex) {
					String msg = RunnerMessages.getInstance().MongoToolDlg_err_access_failed;
					msg += "\r\n";
					msg += MongoUtil.formatExceptionMessage(ex);
					Application.showErrorMessage(MongoToolDialog.this, msg);
					try {
						if (msession != null) {
							msession.closeSession();
						}
					} catch (Throwable ignoreEx) {}
					_mongoSessionStatus = MONGO_STATE_DISCONNECTED;
					_tableModel.setMongoSession(null);
					refreshDisplaySessionStatus();
					requestFocusInWindow();
					return;
				}
				finally {
					MongoToolDialog.this.setCursor(oldCursor);
				}
				
				// 接続成功
				AppLogger.debug("[MongoToolDialog - Clicked Connect button] MongoDB session is connected.");
				_msession = msession;
				_mongoSessionStatus = MONGO_STATE_CONNECTED;
				refreshDisplaySessionStatus();
				refreshDisplayCollectionTablePanel();
				requestFocusInWindow();
			}
		});
	}
	
	protected void onClickedMongoDisconnectButton() {
		if (_mongoSessionStatus != MONGO_STATE_CONNECTED)
			return;
		
		if (_msession != null) {
			_mongoSessionStatus = MONGO_STATE_DISCONNECTING;
			refreshDisplaySessionStatus();
			refreshDisplayCollectionTablePanel();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					java.awt.Cursor oldCursor = MongoToolDialog.this.getCursor();
					MongoToolDialog.this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
					try {
						_msession.closeSession();
						AppLogger.debug("[MongoToolDialog - Clicked Disconnect button] MongoDB session is closed.");
					}
					catch (Throwable ignoreEx) {}
					finally {
						MongoToolDialog.this.setCursor(oldCursor);
					}
					_msession = null;
					_tableModel.setMongoSession(null);
					_mongoSessionStatus = MONGO_STATE_DISCONNECTED;
					refreshDisplaySessionStatus();
					refreshDisplayCollectionTablePanel();
					requestFocusInWindow();
				}
			});
		}
		else {
			_mongoSessionStatus = MONGO_STATE_DISCONNECTED;
			refreshDisplaySessionStatus();
			refreshDisplayCollectionTablePanel();
			requestFocusInWindow();
		}
	}
	
	protected void onClickedRefreshCollectionsButton() {
		if (_mongoSessionStatus != MONGO_STATE_CONNECTED)
			return;	// 何もしない
		
		try {
			_tableModel.refresh();
		}
		catch (Throwable ex) {
			String msg = RunnerMessages.getInstance().MongoToolDlg_err_access_failed;
			msg += "\r\n";
			msg += MongoUtil.formatExceptionMessage(ex);
			Application.showErrorMessage(MongoToolDialog.this, msg);
		}
		refreshDisplayCollectionTableFunctions();
		requestFocusInWindow();
	}
	
	protected void onClickedDeleteCollectionButton() {
		if (_mongoSessionStatus != MONGO_STATE_CONNECTED)
			return;	// 何もしない
		int rowIndex = _table.getSelectedRow();
		if (rowIndex < 0)
			return;	// no selection
		
		// コレクションの削除確認
		rowIndex = _table.convertRowIndexToModel(rowIndex);
		{
			String msg = String.format(RunnerMessages.getInstance().MongoToolDlg_confirm_delete, _tableModel.get(rowIndex).getCollectionName());
			int ret = JOptionPane.showConfirmDialog(this, msg,
					RunnerMessages.getInstance().MongoToolDlg_tooltip_mcol_del,
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (ret != JOptionPane.YES_OPTION) {
				// user canceled
				requestFocusInWindow();
				return;
			}
		}

		// コレクションの削除
		try {
			_tableModel.deleteCollection(rowIndex);
		}
		catch (Throwable ex) {
			String msg = RunnerMessages.getInstance().MongoToolDlg_err_access_failed;
			msg += "\r\n";
			msg += MongoUtil.formatExceptionMessage(ex);
			Application.showErrorMessage(MongoToolDialog.this, msg);
		}
		refreshDisplayCollectionTableFunctions();
		requestFocusInWindow();
	}
	
	protected void onClickedNewCollectionCreateButton() {
		if (_mongoSessionStatus != MONGO_STATE_CONNECTED)
			return;	// 何もしない
		
		// validation
		String colname = _editNewCollectionName.getText();
		if (colname == null || colname.isEmpty()) {
			Application.showErrorMessage(this, RunnerMessages.getInstance().MongoToolDlg_err_emptyNewColName);
			_editNewCollectionName.requestFocus();
			return;
		}
		if (_tableModel.containsName(colname)) {
			Application.showErrorMessage(this, RunnerMessages.getInstance().MongoToolDlg_err_existNewColName);
			_editNewCollectionName.requestFocus();
			return;
		}
		
		// create collection
		int newRowIndex = -1;
		try {
			newRowIndex = _tableModel.createCollection(colname);
		}
		catch (Throwable ex) {
			String msg = RunnerMessages.getInstance().MongoToolDlg_err_createCollection;
			msg += "\r\n";
			msg += MongoUtil.formatExceptionMessage(ex);
			Application.showErrorMessage(MongoToolDialog.this, msg);
			requestFocusInWindow();
			return;
		}
		newRowIndex = _table.convertRowIndexToView(newRowIndex);
		_table.setRowSelectionInterval(newRowIndex, newRowIndex);
		_table.scrollToVisibleCell(newRowIndex, 0);
		refreshDisplayCollectionTableFunctions();
		requestFocusInWindow();
	}
	
	protected void onClickedNewCollectionImportJsonButton() {
		if (_mongoSessionStatus != MONGO_STATE_CONNECTED)
			return;	// 何もしない
		
		// validation
		String newColName = _editNewCollectionName.getText();
		if (newColName == null || newColName.isEmpty()) {
			Application.showErrorMessage(this, RunnerMessages.getInstance().MongoToolDlg_err_emptyNewColName);
			_editNewCollectionName.requestFocus();
			return;
		}
		if (_tableModel.containsName(newColName)) {
			Application.showErrorMessage(this, RunnerMessages.getInstance().MongoToolDlg_err_existNewColName);
			_editNewCollectionName.requestFocus();
			return;
		}
		
		// choose file
		File initFile = restoreImportTarget();
		File selectedFile = FileChooserManager.chooseOpenFile(this, RunnerMessages.getInstance().MongoToolDlg_btn_import_json, false, initFile, FileChooserManager.getJsonFileFilter());
		if (selectedFile == null) {
			// user canceled
			return;
		}
		storeImportTarget(selectedFile);
		
		// create and import JSON
		String desc = String.format("%s -> [%s]", selectedFile.getName(), newColName);
		JsonFileImportProgressMonitorTask task = new JsonFileImportProgressMonitorTask(
				RunnerMessages.getInstance().MongoToolDlg_btn_import_json,	// title
				desc,
				selectedFile,										// target file
				true,												// not create collection
				newColName,											// collection name
				_msession.getDatabase(),							// database
				null,												// collection object
				false,												// replace
				_chkNewCollectionImportWithObjectId.isSelected());
		boolean result;
		try {
			result = task.execute(this);
		}
		catch (Throwable ex) {
			String msg = RunnerMessages.getInstance().MongoToolDlg_err_importCollection;
			msg += "\r\n";
			String exmsg = ex.getLocalizedMessage();
			msg += (exmsg==null || exmsg.isEmpty() ? ex.toString() : exmsg);
			AppLogger.error(msg, ex);
			Application.showErrorMessage(this, msg);
			if (task.isSucceededToCreateNewCollection()) {
				int newRowIndex = _tableModel.put(task.getCollection());
				newRowIndex = _table.convertRowIndexToView(newRowIndex);
				_table.setRowSelectionInterval(newRowIndex, newRowIndex);
				_table.scrollToVisibleCell(newRowIndex, 0);
				refreshDisplayCollectionTableFunctions();
			}
			requestFocusInWindow();
			return;
		}
		// check error
		if (!result) {
			String errmsg = null;
			Throwable taskex = task.getErrorCause();
			if (taskex instanceof MongoException || taskex instanceof MongoConnectionError || taskex instanceof MongoAlgeError) {
				errmsg = RunnerMessages.getInstance().MongoToolDlg_err_importCollection;
				errmsg += "\r\n";
				errmsg += MongoUtil.formatExceptionMessage(taskex);
			}
			else if (taskex instanceof OutOfMemoryError) {
				errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			}
			else if (taskex != null) {
				errmsg = RunnerMessages.getInstance().MongoToolDlg_err_importCollection;
				errmsg += "\r\n";
				String exmsg = taskex.getLocalizedMessage();
				errmsg += (exmsg==null || exmsg.isEmpty() ? taskex.toString() : exmsg);
			}
			if (errmsg != null) {
				AppLogger.error(errmsg, taskex);
				Application.showErrorMessage(this, errmsg);
			}
		}
		if (task.isSucceededToCreateNewCollection()) {
			int newRowIndex = _tableModel.put(task.getCollection());
			newRowIndex = _table.convertRowIndexToView(newRowIndex);
			_table.setRowSelectionInterval(newRowIndex, newRowIndex);
			_table.scrollToVisibleCell(newRowIndex, 0);
			refreshDisplayCollectionTableFunctions();
		}
		requestFocusInWindow();
	}
	
	protected void onClickedImportCollectionAppendRadioButton() {
		if (_mongoSessionStatus != MONGO_STATE_CONNECTED)
			return;	// 何もしない
		_rdoImportCollectionAppend.setSelected(true);
		_rdoImportCollectionReplace.setSelected(false);
	}
	
	protected void onClickedImportCollectionReplaceRadioButton() {
		if (_mongoSessionStatus != MONGO_STATE_CONNECTED)
			return;	// 何もしない
		_rdoImportCollectionAppend.setSelected(false);
		_rdoImportCollectionReplace.setSelected(true);
	}
	
	protected void onClickedImportCollectionJsonButton() {
		if (_mongoSessionStatus != MONGO_STATE_CONNECTED)
			return;	// 何もしない
		int rowIndex = _table.getSelectedRow();
		if (rowIndex < 0)
			return;	// no selection
		rowIndex = _table.convertRowIndexToModel(rowIndex);
		
		// choose file
		File initFile = restoreImportTarget();
		File selectedFile = FileChooserManager.chooseOpenFile(this, RunnerMessages.getInstance().MongoToolDlg_btn_import_json, false, initFile, FileChooserManager.getJsonFileFilter());
		if (selectedFile == null) {
			// user canceled
			return;
		}
		storeImportTarget(selectedFile);
		
		// 置換実行前の確認
		if (_rdoImportCollectionReplace.isSelected()) {
			String msg = String.format(RunnerMessages.getInstance().MongoToolDlg_confirm_replace,
										_tableModel.get(rowIndex).getCollectionName(), selectedFile.getName());
			int ret = JOptionPane.showConfirmDialog(this, msg,
					RunnerMessages.getInstance().MongoToolDlg_rdo_import_replace,
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (ret != JOptionPane.YES_OPTION) {
				// user canceled
				requestFocusInWindow();
				return;
			}
		}
		
		// import JSON
		String desc = String.format("%s -> [%s]", selectedFile.getName(), _tableModel.get(rowIndex).getCollectionName());
		JsonFileImportProgressMonitorTask task = new JsonFileImportProgressMonitorTask(
				RunnerMessages.getInstance().MongoToolDlg_btn_import_json,	// title
				desc,
				selectedFile,										// target file
				false,												// not create collection
				_tableModel.get(rowIndex).getCollectionName(),		// collection name
				_msession.getDatabase(),							// database
				_tableModel.get(rowIndex).getCollectionObject(),	// collection object
				_rdoImportCollectionReplace.isSelected(),			// replace
				_chkImportCollectionWithObjectId.isSelected());
		boolean result;
		try {
			result = task.execute(this);
		}
		catch (Throwable ex) {
			String msg = RunnerMessages.getInstance().MongoToolDlg_err_importCollection;
			msg += "\r\n";
			String exmsg = ex.getLocalizedMessage();
			msg += (exmsg==null || exmsg.isEmpty() ? ex.toString() : exmsg);
			AppLogger.error(msg, ex);
			Application.showErrorMessage(this, msg);
			requestFocusInWindow();
			return;
		}
		// 念のため、テーブルの内容を更新
		if (_tableModel.get(rowIndex).refresh()) {
			_tableModel.fireTableRowsUpdated(rowIndex, rowIndex);
		}
		// check error
		if (!result) {
			String errmsg = null;
			Throwable taskex = task.getErrorCause();
			if (taskex instanceof MongoException || taskex instanceof MongoConnectionError || taskex instanceof MongoAlgeError) {
				errmsg = RunnerMessages.getInstance().MongoToolDlg_err_importCollection;
				errmsg += "\r\n";
				errmsg += MongoUtil.formatExceptionMessage(taskex);
			}
			else if (taskex instanceof OutOfMemoryError) {
				errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			}
			else if (taskex != null) {
				errmsg = RunnerMessages.getInstance().MongoToolDlg_err_importCollection;
				errmsg += "\r\n";
				String exmsg = taskex.getLocalizedMessage();
				errmsg += (exmsg==null || exmsg.isEmpty() ? taskex.toString() : exmsg);
			}
			if (errmsg != null) {
				AppLogger.error(errmsg, taskex);
				Application.showErrorMessage(this, errmsg);
			}
		}
		requestFocusInWindow();
	}
	
	protected void onClickedExportCollectionJsonButton() {
		if (_mongoSessionStatus != MONGO_STATE_CONNECTED)
			return;	// 何もしない
		int rowIndex = _table.getSelectedRow();
		if (rowIndex < 0)
			return;	// no selection
		rowIndex = _table.convertRowIndexToModel(rowIndex);
		
		// choose file
		File initFile = restoreExportTarget();
		File selectedFile = FileChooserManager.chooseSaveFileAndCheckOverwrite(this, RunnerMessages.getInstance().MongoToolDlg_btn_export_json, initFile, FileChooserManager.getJsonFileFilter());
		if (selectedFile == null) {
			// user canceled
			return;
		}
		storeExportTarget(selectedFile);
		
		// export to JSON
		String desc = String.format("[%s] -> %s", _tableModel.get(rowIndex).getCollectionName(), selectedFile.getName());
		JsonFileExportProgressMonitorTask task = new JsonFileExportProgressMonitorTask(
				RunnerMessages.getInstance().MongoToolDlg_btn_export_json,	// title
				desc,
				_tableModel.get(rowIndex).getCollectionObject(),
				selectedFile,
				_chkExportCollectionWithObjectId.isSelected());
		boolean result;
		try {
			result = task.execute(this);
		}
		catch (Throwable ex) {
			String msg = RunnerMessages.getInstance().MongoToolDlg_err_exportCollection;
			msg += "\r\n";
			String exmsg = ex.getLocalizedMessage();
			msg += (exmsg==null || exmsg.isEmpty() ? ex.toString() : exmsg);
			AppLogger.error(msg, ex);
			Application.showErrorMessage(this, msg);
			requestFocusInWindow();
			return;
		}
		// check error
		if (!result) {
			String errmsg = null;
			Throwable taskex = task.getErrorCause();
			if (taskex instanceof MongoException || taskex instanceof MongoConnectionError || taskex instanceof MongoAlgeError) {
				errmsg = RunnerMessages.getInstance().MongoToolDlg_err_exportCollection;
				errmsg += "\r\n";
				errmsg += MongoUtil.formatExceptionMessage(taskex);
			}
			else if (taskex instanceof OutOfMemoryError) {
				errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			}
			else if (taskex != null) {
				errmsg = RunnerMessages.getInstance().MongoToolDlg_err_exportCollection;
				errmsg += "\r\n";
				String exmsg = taskex.getLocalizedMessage();
				errmsg += (exmsg==null || exmsg.isEmpty() ? taskex.toString() : exmsg);
			}
			if (errmsg != null) {
				AppLogger.error(errmsg, taskex);
				Application.showErrorMessage(this, errmsg);
			}
		}
		requestFocusInWindow();
	}
	
	protected void onCollectionTableSelectionChanged(ListSelectionEvent lse) {
		refreshDisplayCollectionTableFunctions();
	}
	
	protected void onCollectionTableModelChanged(TableModelEvent tme) {
		// row header width
		int rowHeaderWidth = _table.getTableRowHeader().getFixedCellWidth();
		int cntRows = _table.getRowCount();
		if (cntRows > 0 && rowHeaderWidth > 0) {
			// set fit width to row header
			_table.getTableRowHeader().setFixedCellWidth(-1);
		}
		else if (cntRows <= 0 && rowHeaderWidth < 0) {
			// set fixed width to row header
			_table.getTableRowHeader().setFixedCellWidth(EMPTY_ROWHEADER_WIDTH);
		}
	}

	//------------------------------------------------------------
	// Implement AbBasicDialog interfaces
	//------------------------------------------------------------

	@Override
	protected void setupMainContents() {
		JPanel pnlConfig = createConfigPanel();
		JPanel pnlCollections = createCollectionTablePanel();
		
		JPanel pnlNewCol = createNewCollectionPanel();
		JPanel pnlImport = createImportJsonPanel();
		JPanel pnlExport = createExportJsonPanel();
		Box funcbox = Box.createVerticalBox();
		funcbox.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		funcbox.add(pnlNewCol);
		funcbox.add(Box.createVerticalStrut(5));
		funcbox.add(pnlImport);
		funcbox.add(Box.createVerticalStrut(5));
		funcbox.add(pnlExport);
		funcbox.add(Box.createVerticalGlue());

		_splitHorz = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		_splitHorz.setResizeWeight(1);
		_splitHorz.setEnabled(true);
		_splitHorz.setLeftComponent(pnlCollections);
		_splitHorz.setRightComponent(funcbox);
		
		_splitTop = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		_splitTop.setResizeWeight(0);
		_splitTop.setEnabled(false);
		_splitTop.setTopComponent(pnlConfig);
		//_splitTop.setBottomComponent(pnlCollections);
		_splitTop.setBottomComponent(_splitHorz);
		
		// add to main panel
		this.getContentPane().add(_splitTop, BorderLayout.CENTER);
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected JButton createApplyButton() {
		// no apply button
		return null;
	}

	@Override
	protected JButton createOkButton() {
		// no OK button
		return null;
	}

	@Override
	protected JButton createCancelButton() {
		JButton btn = super.createCancelButton();
		btn.setText(CommonMessages.getInstance().Button_Close);
		return btn;
	}

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();
		
		this.setResizable(true);
		//this.setStoreLocation(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}

	@Override
	protected void setupActions() {
		super.setupActions();
		
		// buttons
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickedActionButton(e);
			}
		};
		_chkShowPass.addActionListener(al);
		_btnMongoConnect.addActionListener(al);
		_btnMongoDisconnect.addActionListener(al);
		_btnToolDelCollection.addActionListener(al);
		_btnToolRefreshCollections.addActionListener(al);
		_btnNewCollectionCreate.addActionListener(al);
		_btnNewCollectionImportJson.addActionListener(al);
		_rdoImportCollectionAppend.addActionListener(al);
		_rdoImportCollectionReplace.addActionListener(al);
		_btnImportCollectionJson.addActionListener(al);
		_btnExportCollectionJson.addActionListener(al);
		_chkUseAuthDb.addActionListener(al);
//		_chkUseAuthDb.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				
//			}
//		});
		
		// table selection
		_table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onCollectionTableSelectionChanged(e);
			}
		});
		
		// table model
		_tableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				onCollectionTableModelChanged(e);
			}
		});
		
		
		
//		_chkAllOutToTemp.setHandler(new JTriStateCheckBox.TriStateHandler() {
//			@Override
//			public Status nextState(JTriStateCheckBox.Status currentState) {
//				return onNextSelectAllOutToTemp(currentState);
//			}
//		});
//		
//		_chkAllShowDest.setHandler(new JTriStateCheckBox.TriStateHandler() {
//			@Override
//			public Status nextState(JTriStateCheckBox.Status currentState) {
//				return onNextSelectAllShowDest(currentState);
//			}
//		});
//		
//		_btnChooseBaseOutDir.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				onButtonChooseBaseOutDir();
//			}
//		});
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	protected boolean doCancelAction() {
		//--- dispose MongoDB session
		if (_msession != null) {
			try {
				_msession.closeSession();
				AppLogger.debug("[MongoToolDialog - doCancelAction] MongoDB session is closed.");
			}
			catch (Throwable ignoreEx) {}
			_msession = null;
			_tableModel.setMongoSession(null);
			_mongoSessionStatus = MONGO_STATE_DISCONNECTED;
		}
		return super.doCancelAction();
	}

	/**
	 * ダイアログ下部のボタンパネルを生成する。
	 * これは編集時専用のパネルであり、コンソールの表示設定も含む。
	 */
	@Override
	protected JComponent createButtonsPanel() {
		JButton[] buttons = createButtons();
		if (buttons == null || buttons.length < 1) {
			// no buttons
			return null;
		}
		
		int maxHeight = adjustButtonSize(buttons);
		
//		// create local components
//		_chkAllOutToTemp = new JTriStateCheckBox(RunnerMessages.getInstance().MExecDefEditDlg_Arg_OutToTemp);
//		_chkAllShowDest  = new JTriStateCheckBox(RunnerMessages.getInstance().MExecDefEditDlg_Arg_ShowResultCsv);
//		_chkAllOutToTemp.setSelected(AppSettings.getInstance().getConfiguration().getBooleanValue(AppSettings.Excel2CSV_DESTEDIT_OUT2TEMP));
//		_chkAllShowDest .setSelected(AppSettings.getInstance().getConfiguration().getBooleanValue(AppSettings.Excel2CSV_DESTEDIT_SHOWDEST));
//		_chkAllOutToTemp.setMargin(new Insets(0,0,0,0));
//		_chkAllShowDest .setMargin(new Insets(0,0,0,0));
		
		// Layout
		Box btnBox = Box.createHorizontalBox();
		Box chkBox = Box.createVerticalBox();
//		chkBox.add(_chkAllShowDest);
		chkBox.add(Box.createVerticalGlue());
//		chkBox.add(_chkAllOutToTemp);
		btnBox.add(chkBox);
		//---
		btnBox.add(Box.createHorizontalGlue());
		for (JButton btn : buttons) {
			btnBox.add(btn);
			btnBox.add(Box.createHorizontalStrut(5));
		}
		btnBox.add(Box.createRigidArea(new Dimension(0, maxHeight+10)));
		return btnBox;
	}

	@Override
	protected void initDialog() {
		super.initDialog();

//		// scroll to top item
//		getEditPane().scrollTopToVisible();
		
//		// verify
//		getEditPane().verifyItemValues();
		
		// refresh tree context menu
//		updateTreeContextMenu();
	}

	@Override
	protected void onWindowClosed(WindowEvent e) {
//		_handler.onClosedDialog(this);
	}
	
	@Override
	protected void onShown(ComponentEvent e) {
//		_handler.onShownDialog(this);
	}

	@Override
	protected void onHidden(ComponentEvent e) {
//		_handler.onHiddenDialog(this);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected File restoreImportTarget() {
		String strPath;
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null) {
			strPath = config.getLastFilename(prefix+SUBKEY_IMPORT_JSON);
		}
		else {
			strPath = null;
		}
		return (strPath==null || strPath.isEmpty() ? null : new File(strPath));
	}
	
	protected void storeImportTarget(File lastFile) {
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null) {
			config.setLastFilename(prefix+SUBKEY_IMPORT_JSON, lastFile==null ? null : lastFile.getAbsolutePath());
		}
	}
	
	protected File restoreExportTarget() {
		String strPath;
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null) {
			strPath = config.getLastFilename(prefix+SUBKEY_EXPORT_JSON);
		}
		else {
			strPath = null;
		}
		return (strPath==null || strPath.isEmpty() ? null : new File(strPath));
	}
	
	protected void storeExportTarget(File lastFile) {
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null) {
			config.setLastFilename(prefix+SUBKEY_EXPORT_JSON, lastFile==null ? null : lastFile.getAbsolutePath());
		}
	}
	
	protected void refreshPasswordTextField() {
		if (_chkShowPass.isSelected()) {
			_editMongoPass.setEchoChar('\0');
		} else {
			_editMongoPass.setEchoChar(CHAR_PASSWORD_MASK);
		}
	}
	
	protected void refreshAuthDbTextField() {
		if (_chkUseAuthDb.isSelected()) {
			_editMongoAuthDb.setEnabled(true);
			_editMongoAuthDb.setEditable(_mongoSessionStatus==MONGO_STATE_DISCONNECTED);
			if (!_lastUseAuthDb) {
				// state changed
				_lastUseAuthDb = true;
				_editMongoAuthDb.setText(_strMongoAuthDb);
			}
		}
		else {
			_editMongoAuthDb.setEnabled(false);
			_editMongoAuthDb.setEditable(false);
			if (_lastUseAuthDb) {
				// state changed
				_lastUseAuthDb = false;
				_strMongoAuthDb = _editMongoAuthDb.getText();
				_editMongoAuthDb.setText("");
			}
		}
	}
	
	protected void refreshDisplaySessionStatus() {
		if (_mongoSessionStatus == MONGO_STATE_CONNECTED) {
			_lblMongoSessionStatus.setIcon(RunnerResources.ICON_BALL_GREEN);
			_btnMongoConnect.setEnabled(false);
			_btnMongoDisconnect.setEnabled(true);
			_editMongoHost.setEditable(false);
			_editMongoPort.setEditable(false);
			_editMongoDbName.setEditable(false);
			_editMongoUser.setEditable(false);
			_editMongoPass.setEditable(false);
			_chkUseAuthDb.setEnabled(false);
			_editMongoAuthDb.setEditable(false);
		}
		else if (_mongoSessionStatus == MONGO_STATE_CONNECTING) {
			_lblMongoSessionStatus.setIcon(RunnerResources.ICON_BALL_YELLOW);
			_btnMongoConnect.setEnabled(false);
			_btnMongoDisconnect.setEnabled(false);
			_editMongoHost.setEditable(false);
			_editMongoPort.setEditable(false);
			_editMongoDbName.setEditable(false);
			_editMongoUser.setEditable(false);
			_editMongoPass.setEditable(false);
			_chkUseAuthDb.setEnabled(false);
			_editMongoAuthDb.setEditable(false);
		}
		else if (_mongoSessionStatus == MONGO_STATE_DISCONNECTING) {
			// disconnecting
			_lblMongoSessionStatus.setIcon(RunnerResources.ICON_BALL_YELLOW);
			_btnMongoConnect.setEnabled(false);
			_btnMongoDisconnect.setEnabled(false);
			_editMongoHost.setEditable(false);
			_editMongoPort.setEditable(false);
			_editMongoDbName.setEditable(false);
			_editMongoUser.setEditable(false);
			_editMongoPass.setEditable(false);
			_chkUseAuthDb.setEnabled(false);
			_editMongoAuthDb.setEditable(false);
		}
		else {
			// disconnected
			_lblMongoSessionStatus.setIcon(RunnerResources.ICON_BALL_RED);
			_btnMongoConnect.setEnabled(true);
			_btnMongoDisconnect.setEnabled(false);
			_editMongoHost.setEditable(true);
			_editMongoPort.setEditable(true);
			_editMongoDbName.setEditable(true);
			_editMongoUser.setEditable(true);
			_editMongoPass.setEditable(true);
			_chkUseAuthDb.setEnabled(true);
			_editMongoAuthDb.setEditable(_chkUseAuthDb.isSelected());
		}
	}
	
	protected void refreshDisplayCollectionTablePanel() {
		if (_mongoSessionStatus == MONGO_STATE_CONNECTED) {
			// MongoDB へ接続済み
			_tbarFunctions.setEnabled(true);
			_btnToolRefreshCollections.setEnabled(true);
			_pnlNewCollection.setEnabled(true);
			_lblNewCollectionName.setEnabled(true);
			_editNewCollectionName.setEnabled(true);
			_chkNewCollectionImportWithObjectId.setEnabled(true);
			_btnNewCollectionCreate.setEnabled(true);
			_btnNewCollectionImportJson.setEnabled(true);
			_pnlImportCollection.setEnabled(true);
			_rdoImportCollectionAppend.setEnabled(true);
			_rdoImportCollectionReplace.setEnabled(true);
			_chkImportCollectionWithObjectId.setEnabled(true);
			_pnlExportCollection.setEnabled(true);
			_chkExportCollectionWithObjectId.setEnabled(true);
			refreshDisplayCollectionTableFunctions();
		}
		else {
			// MongoDB へ未接続
			_tbarFunctions.setEnabled(false);
			_btnToolDelCollection.setEnabled(false);
			_btnToolRefreshCollections.setEnabled(false);
			_pnlNewCollection.setEnabled(false);
			_lblNewCollectionName.setEnabled(false);
			_editNewCollectionName.setEnabled(false);
			_chkNewCollectionImportWithObjectId.setEnabled(false);
			_btnNewCollectionCreate.setEnabled(false);
			_btnNewCollectionImportJson.setEnabled(false);
			_pnlImportCollection.setEnabled(false);
			_rdoImportCollectionAppend.setEnabled(false);
			_rdoImportCollectionReplace.setEnabled(false);
			_chkImportCollectionWithObjectId.setEnabled(false);
			_btnImportCollectionJson.setEnabled(false);
			_pnlExportCollection.setEnabled(false);
			_chkExportCollectionWithObjectId.setEnabled(false);
			_btnExportCollectionJson.setEnabled(false);
		}
	}
	
	protected void refreshDisplayCollectionTableFunctions() {
		int selcnt = _table.getSelectedRowCount();
		if (selcnt > 0) {
			// 選択あり
			_btnToolDelCollection.setEnabled(true);
			_btnImportCollectionJson.setEnabled(true);
			_btnExportCollectionJson.setEnabled(true);
		}
		else {
			// 選択なし
			_btnToolDelCollection.setEnabled(false);
			_btnImportCollectionJson.setEnabled(false);
			_btnExportCollectionJson.setEnabled(false);
		}
	}
	
	protected JButton createToolBarButton(String command, Icon icon, String tooltip) {
		ToolBarButton btn = new ToolBarButton(icon);
		btn.setActionCommand(command);
		btn.setToolTipText(tooltip);
		btn.setRequestFocusEnabled(false);
		btn.setMargin(new Insets(0, 0, 0, 0));
		return btn;
	}
	
	protected void createComponents() {
		//--- MongoDB : hostname
		_editMongoHost = new JTextField();
		
		//--- MongoDB : port
		_editMongoPort = new JNetworkPortNumberTextField();
		_editMongoPort.setEditable(true);
		_editMongoPort.setValue(Integer.valueOf(65535));
		Dimension dm = _editMongoPort.getPreferredSize();
		dm.width += 20;
		_editMongoPort.setMinimumSize(dm);
		_editMongoPort.setPreferredSize(dm);
		
		//--- MongoDB : database name
		_editMongoDbName = new JTextField();
		
		//--- MongoDB : user
		_editMongoUser = new JTextField();
		
		//--- MongoDB : password
		_editMongoPass = new JPasswordField();
		_editMongoPass.setEchoChar(CHAR_PASSWORD_MASK);
		_chkShowPass = new JCheckBox(RunnerMessages.getInstance().MongoToolDlg_chk_showpass);
		_chkShowPass.setActionCommand(CMD_MONGO_SHOW_PASSWORD);
		
		//--- MongoDB : auth db name
		_editMongoAuthDb = new JTextField();
		//_chkUseAuthDb = new JCheckBox(RunnerMessages.getInstance().MongoToolDlg_chk_use_authdb);
		_chkUseAuthDb = new JCheckBox(RunnerMessages.getInstance().MongoToolDlg_label_authdb);
		_chkUseAuthDb.setActionCommand(CMD_MONGO_USE_AUTHDB);
		
		//--- buttons
		_lblMongoSessionStatus = new JLabel(RunnerResources.ICON_BALL_RED);
		_btnMongoConnect = new JButton(RunnerMessages.getInstance().MongoToolDlg_btn_connect);
		_btnMongoConnect.setActionCommand(CMD_MONGO_CONNECT);
		_btnMongoDisconnect = new JButton(RunnerMessages.getInstance().MongoToolDlg_btn_disconnect);
		_btnMongoDisconnect.setActionCommand(CMD_MONGO_DISCONNECT);
		
		//--- toolbar
		_tbarFunctions = new JToolBar(JToolBar.HORIZONTAL);
		_tbarFunctions.setFloatable(false);	// 移動禁止
		//_tbarFunctions.setBorderPainted(true);	// ボーダーを描画
		//_tbarFunctions.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		_btnToolRefreshCollections = createToolBarButton(CMD_MCOLS_REFRESH, CommonResources.ICON_REFRESH, RunnerMessages.getInstance().MongoToolDlg_tooltip_mcol_refresh);
		_btnToolDelCollection = createToolBarButton(CMD_MCOLS_DELETE, CommonResources.ICON_DELETE, RunnerMessages.getInstance().MongoToolDlg_tooltip_mcol_del);
		
		//--- collection table
		_tableModel = new MongoToolSortedCollectionTableModel();
		_table = new SpreadSheetTable(_tableModel);
		_table.setRowSelectionAllowed(true);
		_table.setColumnSelectionAllowed(false);
		_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_table.getTableHeader().setReorderingAllowed(false);	// 列入れ替え禁止
		_table.getTableHeader().setResizingAllowed(true);		// 列サイズ変更可能
		_table.setupVisibleGrid();								// グリッド表示(Mac対策)
		_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		//--- new collection functions
		_pnlNewCollection = new JPanel(new GridBagLayout());
		_lblNewCollectionName = new JLabel(RunnerMessages.getInstance().MongoToolCollectionTableColumn_name);
		_editNewCollectionName = new JTextField();
		_btnNewCollectionCreate = new JButton(RunnerMessages.getInstance().MongoToolDlg_btn_newcol_create, RunnerResources.ICON_DB_CREATE);
		_btnNewCollectionCreate.setActionCommand(CMD_MCOLS_NEW_CREATE);
		_btnNewCollectionImportJson = new JButton(RunnerMessages.getInstance().MongoToolDlg_btn_newcol_import_json, RunnerResources.ICON_DB_NEW_IMPORT);
		_btnNewCollectionImportJson.setActionCommand(CMD_MCOLS_NEW_IMPORT_JSON);
		_chkNewCollectionImportWithObjectId = new JCheckBox(RunnerMessages.getInstance().MongoToolDlg_chk_withObjectId);
		
		//--- import functions
		_pnlImportCollection = new JPanel(new GridBagLayout());
		_rdoImportCollectionAppend = new JRadioButton(RunnerMessages.getInstance().MongoToolDlg_rdo_import_append);
		_rdoImportCollectionAppend.setActionCommand(CMD_MCOLS_IMPORT_APPEND);
		_rdoImportCollectionReplace = new JRadioButton(RunnerMessages.getInstance().MongoToolDlg_rdo_import_replace);
		_rdoImportCollectionReplace.setActionCommand(CMD_MCOLS_IMPORT_REPLACE);
		_chkImportCollectionWithObjectId = new JCheckBox(RunnerMessages.getInstance().MongoToolDlg_chk_withObjectId);
		_btnImportCollectionJson = new JButton(RunnerMessages.getInstance().MongoToolDlg_btn_import_json, RunnerResources.ICON_DB_IMPORT);
		_btnImportCollectionJson.setActionCommand(CMD_MCOLS_IMPORT_JSON);
		
		//--- export functions
		_pnlExportCollection = new JPanel(new GridBagLayout());
		_chkExportCollectionWithObjectId = new JCheckBox(RunnerMessages.getInstance().MongoToolDlg_chk_withObjectId);
		_btnExportCollectionJson = new JButton(RunnerMessages.getInstance().MongoToolDlg_btn_export_json, RunnerResources.ICON_DB_EXPORT);
		_btnExportCollectionJson.setActionCommand(CMD_MCOLS_EXPORT_JSON);
	}
	
	protected JPanel createConfigPanel() {
		JPanel pnlParams = new JPanel(new GridBagLayout());
		
		// labels
		JLabel lblHost   = new JLabel(RunnerMessages.getInstance().MongoToolDlg_label_hostname);
		JLabel lblPort   = new JLabel(RunnerMessages.getInstance().MongoToolDlg_label_portno);
		JLabel lblDbName = new JLabel(RunnerMessages.getInstance().MongoToolDlg_label_dbname);
		JLabel lblUser   = new JLabel(RunnerMessages.getInstance().MongoToolDlg_label_username);
		JLabel lblPass   = new JLabel(RunnerMessages.getInstance().MongoToolDlg_label_password);
		
		// layout
		Insets leftInsets = new Insets(0, 0, 5, 3);
		Insets midInsets = new Insets(0, 10, 5, 3);
		Insets ctlInsets = new Insets(0, 0, 5, 0);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx    = 0;
		gbc.weighty    = 0;
		gbc.gridx      = 0;
		gbc.gridy      = 0;
		//--- host
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.insets = leftInsets;
		gbc.fill    = GridBagConstraints.NONE;
		gbc.anchor  = GridBagConstraints.EAST;
		pnlParams.add(lblHost, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.insets = ctlInsets;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		pnlParams.add(_editMongoHost, gbc);
		//--- port
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.insets = midInsets;
		gbc.fill    = GridBagConstraints.NONE;
		gbc.anchor  = GridBagConstraints.EAST;
		pnlParams.add(lblPort, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.insets = ctlInsets;
		gbc.fill    = GridBagConstraints.NONE;
		gbc.anchor  = GridBagConstraints.WEST;
		pnlParams.add(_editMongoPort, gbc);
		//--- DB name
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.insets = midInsets;
		gbc.fill    = GridBagConstraints.NONE;
		gbc.anchor  = GridBagConstraints.EAST;
		pnlParams.add(lblDbName, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.insets = ctlInsets;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		pnlParams.add(_editMongoDbName, gbc);
		//--- user
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.insets = leftInsets;
		gbc.fill    = GridBagConstraints.NONE;
		gbc.anchor  = GridBagConstraints.EAST;
		pnlParams.add(lblUser, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.insets = ctlInsets;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		pnlParams.add(_editMongoUser, gbc);
		//--- pass
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.insets = midInsets;
		gbc.fill    = GridBagConstraints.NONE;
		gbc.anchor  = GridBagConstraints.EAST;
		pnlParams.add(lblPass, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.insets = ctlInsets;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		pnlParams.add(_editMongoPass, gbc);
		//--- auth-db
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.insets = midInsets;
		gbc.fill    = GridBagConstraints.NONE;
		gbc.anchor  = GridBagConstraints.EAST;
		pnlParams.add(_chkUseAuthDb, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.insets = ctlInsets;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		pnlParams.add(_editMongoAuthDb, gbc);
		//--- dummy
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.insets = leftInsets;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		pnlParams.add(new JLabel(), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.insets = ctlInsets;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		pnlParams.add(new JLabel(), gbc);
		//--- show pass
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.insets = midInsets;
		gbc.fill    = GridBagConstraints.NONE;
		gbc.anchor  = GridBagConstraints.EAST;
		pnlParams.add(new JLabel(), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.insets = ctlInsets;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		pnlParams.add(_chkShowPass, gbc);
		//--- dummy
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.insets = midInsets;
		gbc.fill    = GridBagConstraints.NONE;
		gbc.anchor  = GridBagConstraints.EAST;
		pnlParams.add(new JLabel(), gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.insets = ctlInsets;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		pnlParams.add(new JLabel(), gbc);
		
		// connection buttons
		Box btnbox = Box.createHorizontalBox();
		btnbox.add(Box.createHorizontalGlue());
		btnbox.add(_lblMongoSessionStatus);
		btnbox.add(Box.createHorizontalStrut(10));
		btnbox.add(_btnMongoConnect);
		btnbox.add(Box.createHorizontalStrut(5));
		btnbox.add(_btnMongoDisconnect);
		btnbox.add(Box.createHorizontalGlue());
		
		// config panel
		JPanel pnlConfig = new JPanel(new BorderLayout());
		pnlConfig.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		pnlConfig.add(pnlParams, BorderLayout.CENTER);
		pnlConfig.add(btnbox, BorderLayout.SOUTH);
		
		return pnlConfig;
	}
	
	protected JToolBar setupFunctionToolBar() {
		_tbarFunctions.add(new JLabel(RunnerMessages.getInstance().MongoToolDlg_label_collection));
		_tbarFunctions.add(Box.createHorizontalGlue());
		_tbarFunctions.add(_btnToolRefreshCollections);
		_tbarFunctions.add(Box.createHorizontalStrut(10));
		_tbarFunctions.add(_btnToolDelCollection);
		_tbarFunctions.add(Box.createHorizontalStrut(30));
//		_tbarFunctions.add(_btnToolNewCollection);
//		_tbarFunctions.add(Box.createHorizontalStrut(5));
//		_tbarFunctions.add(Box.createHorizontalStrut(5));
//		_tbarFunctions.addSeparator();
//		_tbarFunctions.add(_chkToolWithObjectId);
//		_tbarFunctions.add(Box.createHorizontalStrut(5));
//		_tbarFunctions.add(_btnToolJsonImport);
//		_tbarFunctions.add(Box.createHorizontalStrut(5));
//		_tbarFunctions.add(_btnToolJsonExport);
		return _tbarFunctions;
	}
	
	protected JPanel createCollectionTablePanel() {
		JPanel pnl = new JPanel(new BorderLayout());
		
		//--- toolbar
		pnl.add(setupFunctionToolBar(), BorderLayout.NORTH);
		
		//--- table
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sc.setViewportView(_table);
		sc.setRowHeaderView(_table.getTableRowHeader());
		sc.setCorner(JScrollPane.UPPER_LEFT_CORNER, SpreadSheetTable.createUpperLeftCornerComponent());
		_table.getTableRowHeader().setFixedCellWidth(EMPTY_ROWHEADER_WIDTH);
		
		//_table.getTableHeader().getColumnModel().getColumn(0).setWidth(100);
		_table.getColumnModel().getColumn(0).setPreferredWidth(200);
		
		pnl.add(sc, BorderLayout.CENTER);
		return pnl;
	}
	
	protected JPanel createNewCollectionPanel() {
		Border bd = BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(RunnerMessages.getInstance().MongoToolDlg_label_newCollection),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)
					);
		_pnlNewCollection.setBorder(bd);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx    = 0;
		gbc.weighty    = 0;
		gbc.gridx      = 0;
		gbc.gridy      = 0;
		//--- label
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 0, 3, 3);
		gbc.fill    = GridBagConstraints.NONE;
		gbc.anchor  = GridBagConstraints.WEST;
		_pnlNewCollection.add(_lblNewCollectionName, gbc);
		//--- text field
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.insets = new Insets(0, 0, 3, 0);
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		_pnlNewCollection.add(_editNewCollectionName, gbc);
		//--- button(create)
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		_pnlNewCollection.add(_btnNewCollectionCreate, gbc);
		//--- check box
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		_pnlNewCollection.add(_chkNewCollectionImportWithObjectId, gbc);
		//--- button(import)
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.insets  = new Insets(0,0,0,0);
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		_pnlNewCollection.add(_btnNewCollectionImportJson, gbc);
		
		
		return _pnlNewCollection;
	}
	
	protected JPanel createImportJsonPanel() {
		Border bd = BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(RunnerMessages.getInstance().MongoToolDlg_label_importCollection),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)
					);
		_pnlImportCollection.setBorder(bd);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx    = 1;
		gbc.weighty    = 0;
		gbc.gridx      = 0;
		gbc.gridy      = 0;
		//--- radio buttons
		gbc.gridx = 0;
		gbc.insets  = new Insets(0, 0, 3, 0);
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		_pnlImportCollection.add(_rdoImportCollectionAppend, gbc);
		gbc.gridy++;
		_pnlImportCollection.add(_rdoImportCollectionReplace, gbc);
		gbc.gridy++;
		_pnlImportCollection.add(_chkImportCollectionWithObjectId, gbc);
		//--- button
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 0, 0);
		_pnlImportCollection.add(_btnImportCollectionJson, gbc);
		
		return _pnlImportCollection;
	}
	
	protected JPanel createExportJsonPanel() {
		Border bd = BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(RunnerMessages.getInstance().MongoToolDlg_label_exportCollection),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)
					);
		_pnlExportCollection.setBorder(bd);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx    = 1;
		gbc.weighty    = 0;
		gbc.gridx      = 0;
		gbc.gridy      = 0;
		//--- check box
		gbc.insets = new Insets(0, 0, 3, 0);
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.WEST;
		_pnlExportCollection.add(_chkExportCollectionWithObjectId, gbc);
		//--- button
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 0, 0);
		_pnlExportCollection.add(_btnExportCollectionJson, gbc);
		
		return _pnlExportCollection;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class NetworkPortNumberFormatterFactory extends DefaultFormatterFactory
	{
		private static final long serialVersionUID = 1L;

		private static NumberFormatter	_numberFormatter;
		static {
			_numberFormatter = new MaskedNumberFormatter();
			_numberFormatter.setValueClass(Integer.class);
			((NumberFormat)_numberFormatter.getFormat()).setGroupingUsed(false);
			_numberFormatter.setMinimum(Integer.valueOf(1));
			_numberFormatter.setMaximum(Integer.valueOf(65535));
		}
		
		public NetworkPortNumberFormatterFactory() {
			super(_numberFormatter, _numberFormatter, _numberFormatter);
		}
	}
	
	static protected class JNetworkPortNumberTextField extends JFormattedTextField
	{
		private static final long serialVersionUID = 1L;

		public JNetworkPortNumberTextField() {
			super();
			setFormatterFactory(new NetworkPortNumberFormatterFactory());
			setHorizontalAlignment(JTextField.RIGHT);
			//((AbstractDocument)getDocument()).setDocumentFilter(new IntegerDocumentFilter());
		}
	}
	
	/**
	 * インポート用プログレスモニタータスク。
	 * 
	 * @version 3.4.0
	 * @since 3.4.0
	 */
	static protected class JsonFileImportProgressMonitorTask extends ProgressMonitorTask
	{
		static protected final String	MONGO_OBJ_ID_KEY	= "_id";
		
		/** ソースとなる JSON ファイル **/
		protected File		_srcJsonFile;
		/** 新規作成を要求するフラグ **/
		protected boolean	_requestCreate;
		/** 既存コレクションの置換を要求するフラグ **/
		protected boolean	_requestReplace;
		/** 新規作成用コレクション名(新規作成でない場合は <tt>null</tt>) **/
		protected String	_newColName;
		/** MongoDB データベース **/
		protected MongoDatabase				_mdb;
		/** インポート対象の MongoDB コレクション **/
		protected MongoCollection<Document>	_dstCollection;
		/** MongoDB のオブジェクト ID も含めることを示すフラグ **/
		protected boolean	_withObjectId;
		/** 結果はどうあれ、コレクションが作成された場合に <tt>true</tt> **/
		protected boolean	_createdNewCollection;
		
		public JsonFileImportProgressMonitorTask(String title, String desc, File srcFile, boolean create, String colName,
													MongoDatabase mdb, MongoCollection<Document> mcol, boolean replace, boolean withObjectId)
		{
			super(title, desc, null, 0, 0, 100);
			_srcJsonFile = srcFile;
			_requestCreate = create;
			_requestReplace = replace;
			_withObjectId   = withObjectId;
			_mdb            = mdb;
			_dstCollection  = mcol;
			_newColName     = colName;
			_createdNewCollection = false;
			setValue(0);
		}
		
		public boolean isSucceededToCreateNewCollection() {
			return _createdNewCollection;
		}
		
		public MongoCollection<Document> getCollection() {
			return _dstCollection;
		}
		
		protected Document parseKeyValueObject(JSONReader jreader) throws IOException
		{
			JSONEventType jtype;
			Document doc = new Document();
			String lastName = "";
			while ((jtype = jreader.next()) != null) {
				if (jtype == JSONEventType.END_OBJECT) {
					break;
				}
				else if (jtype == JSONEventType.NAME) {
					lastName = jreader.getString();
				}
				else {
					if (MONGO_OBJ_ID_KEY.equals(lastName)) {
						// MongoDB object ID
						Object objId = jreader.getValue(Object.class);
						if (_withObjectId) {
							doc.append(lastName, objId);
						}
					}
					else {
						doc.append(lastName, jreader.getValue(Object.class));
					}
				}
			}
			doc = Document.parse(doc.toJson());
			return doc;
		}
		
		@Override
		public void processTask() throws Throwable
		{
			// 新規作成
			if (_requestCreate) {
				_mdb.createCollection(_newColName);
				_requestReplace = false;	// 新規作成なら置換なし
				_dstCollection = _mdb.getCollection(_newColName);
				_createdNewCollection = true;	// コレクション作成成功
			}
			
			// 置換のための、全ドキュメント削除
			if (_requestReplace) {
				_dstCollection.deleteMany(new Document());
			}
			
			// データサイズ
			//long flen = _srcJsonFile.length();
			//long cntRead  = 0L;	// 読込ドキュメントバイト数
			//long cntWrite = 0L;	// 書き込み済みドキュメント数
			
			// インポート
			FileInputStream fis = null;
			InputStreamReader isr = null;
			JSONReader jreader = null;
			try {
				fis = new FileInputStream(_srcJsonFile);
				isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
				jreader = new JSON().getReader(isr);
				
				// JSONオブジェクトを読み込む
				JSONEventType jtype;
				while (!isTerminateRequested() && (jtype = jreader.next()) != null) {
					if (jtype == JSONEventType.START_ARRAY) {
						// リスト型
						while (!isTerminateRequested() && (jtype = jreader.next()) != null) {
							if (jtype == JSONEventType.END_ARRAY) {
								break;
							}
							else if (jtype == JSONEventType.START_OBJECT) {
								Document doc = parseKeyValueObject(jreader);
								//--- MongoDB コレクションへ追加
								_dstCollection.insertOne(doc);
							}
							else if (jtype == JSONEventType.NULL) {
								// 最上位JSON配列要素の NULL 値は無視
							}
							else {
								throw new RuntimeException(RunnerMessages.getInstance().MongoToolDlg_err_jsonTopListElemNotKeyVal);
							}
						}
					}
					else if (jtype == JSONEventType.START_OBJECT) {
						// マップ型
						Document doc = parseKeyValueObject(jreader);
						//--- MongoDB コレクションへ追加
						_dstCollection.insertOne(doc);
					}
					else {
						// その他のデータ型
						throw new RuntimeException(RunnerMessages.getInstance().MongoToolDlg_err_jsonStartPrimitive);
					}
				}
			}
			finally {
				ssac.util.io.Files.closeStream(isr);
				ssac.util.io.Files.closeStream(fis);
				jreader = null;
				isr = null;
				fis = null;
			}
		}
	}
	
	/**
	 * エクスポート用プログレスモニタータスク。
	 * 
	 * @version 3.4.0
	 * @since 3.4.0
	 */
	static protected class JsonFileExportProgressMonitorTask extends ProgressMonitorTask
	{
		/** ソースとなる MongoDB コレクション **/
		protected MongoCollection<Document>	_srcCollection;
		/** 出力先 JSON ファイル **/
		protected File		_dstJsonFile;
		/** MongoDB のオブジェクト ID も含めることを示すフラグ **/
		protected boolean	_withObjectId;
		
		public JsonFileExportProgressMonitorTask(String title, String desc, MongoCollection<Document> mcol, File dstFile, boolean withObjectId) {
			super(title, desc, null, 0, 0, 100);
			_srcCollection = mcol;
			_dstJsonFile   = dstFile;
			_withObjectId  = withObjectId;
			setValue(0);
		}
		
		protected String serializeJson(Document doc) {
			return com.mongodb.util.JSON.serialize(doc);
		}
		
		@Override
		public void processTask() throws Throwable
		{
			// ドキュメントカウント
			long numDocuments = _srcCollection.countDocuments();
			long cntRead  = 0L;		// 読み込み済みドキュメント数
			long cntWrite = 0L;		// 書き込み済みドキュメント数
			
			// エクスポート
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			BufferedWriter bw = null;
			MongoCursor<Document> mcursor = null;
			try {
				fos = new FileOutputStream(_dstJsonFile);
				osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
				bw = new BufferedWriter(osw);
				
				// 検索
				FindIterable<Document> results = _srcCollection.find();
				if (!_withObjectId) {
					results = results.projection(Projections.exclude("_id"));
				}
				
				// 入出力
				mcursor = results.iterator();
				bw.append('[');
				//--- first document
				Document doc;
				for (; mcursor.hasNext(); ) {
					if (isTerminateRequested()) {
						return;	// user canceled
					}
					doc = mcursor.next();
					++cntRead;
					if (!doc.isEmpty()) {
						bw.newLine();
						bw.append(serializeJson(doc));
						++cntWrite;
						setValue((int)(((double)cntRead / (double)numDocuments) * 100.0));
						break;
					}
					else {
						setValue((int)(((double)cntRead / (double)numDocuments) * 100.0));
					}
				}
				//--- next documents
				for (; mcursor.hasNext(); ) {
					if (isTerminateRequested()) {
						return;	// user canceled
					}
					doc = mcursor.next();
					++cntRead;
					if (!doc.isEmpty()) {
						bw.append(',');
						bw.newLine();
						bw.append(serializeJson(doc));
						++cntWrite;
					}
					setValue((int)(((double)cntRead / (double)numDocuments) * 100.0));
				}
				//--- close array
				bw.newLine();
				bw.append(']');
				bw.newLine();
				setValue(100);
			}
			finally {
				if (mcursor != null) {
					try {
						mcursor.close();
					} catch (Throwable ignoreEx) {}
					mcursor = null;
				}
				ssac.util.io.Files.closeStream(bw);
				ssac.util.io.Files.closeStream(osw);
				ssac.util.io.Files.closeStream(fos);
			}
		}
	}
}
