/**
 * 
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
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ssac.util.properties.ExConfiguration;
import ssac.util.swing.tree.AbstractTreeModel;

/**
 * @author ishizuka
 *
 */
public class FolderBrowserDialog extends JDialog implements IDialogResult
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -5229463449734976456L;

	static private final Enumeration<FolderTreeNode> EMPTY_ENUMERATION
		= new Enumeration<FolderTreeNode>() {
					public boolean hasMoreElements() { return false; }
					public FolderTreeNode nextElement() {
						throw new NoSuchElementException("No more elements");
					}
				};
	
	static public final String TEXT_CREATE	= "Create a Folder";
	static public final String TEXT_CANCEL	= "Cancel";
	static public final String TEXT_OK		= "OK";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 'Create a Folder' button **/
	protected final JButton	btnCreate;
	/** 'Cancel' button **/
	protected final JButton	btnCancel;
	/** 'OK' button **/
	protected final JButton	btnOK;
	
	protected final File		fileRoot;
	protected final File		fileInitial;

	/** main panel **/
	protected final JPanel	mainPanel;
	/** folder tree **/
	protected final JTree		folderTree;
	
	private boolean flgSaveLocation    = true;
	private boolean flgSaveSize        = true;
	private boolean flgKeepMinimumSize = true;
	
	protected int dialogResult = DialogResult_None;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FolderBrowserDialog(File rootFolder, File initialFolder)
		throws HeadlessException
	{
        this(rootFolder, initialFolder, (Frame)null, false);
	}

	public FolderBrowserDialog(File rootFolder, File initialFolder, Frame owner)
		throws HeadlessException
	{
        this(rootFolder, initialFolder, owner, false);
	}

	public FolderBrowserDialog(File rootFolder, File initialFolder, Frame owner, boolean modal)
		throws HeadlessException
	{
        this(rootFolder, initialFolder, owner, null, modal);
	}

	public FolderBrowserDialog(File rootFolder, File initialFolder, Frame owner, String title)
		throws HeadlessException
	{
        this(rootFolder, initialFolder, owner, title, false);     
	}

	public FolderBrowserDialog(File rootFolder, File initialFolder, Dialog owner)
		throws HeadlessException
	{
        this(rootFolder, initialFolder, owner, false);
	}

	public FolderBrowserDialog(File rootFolder, File initialFolder, Dialog owner, boolean modal)
		throws HeadlessException
	{
        this(rootFolder, initialFolder, owner, null, modal);
	}

	public FolderBrowserDialog(File rootFolder, File initialFolder, Dialog owner, String title)
		throws HeadlessException
	{
        this(rootFolder, initialFolder, owner, title, false);     
	}

	public FolderBrowserDialog(File rootFolder, File initialFolder, Dialog owner, String title, boolean modal, GraphicsConfiguration gc)
		throws HeadlessException
	{
		super(owner, title, modal, gc);
		if (rootFolder != null && !rootFolder.isDirectory())
			throw new IllegalArgumentException("rootFolder argument is not directory.");
		if (initialFolder != null && !initialFolder.isDirectory())
			throw new IllegalArgumentException("initialFolder argument is not directory.");
		this.fileRoot = rootFolder;
		this.fileInitial = initialFolder;
		this.btnCreate  = new JButton(TEXT_CREATE);
		this.btnCancel  = new JButton(TEXT_CANCEL);
		this.btnOK      = new JButton(TEXT_OK);
		this.mainPanel  = new JPanel();
		this.folderTree = createTreeComponent();
		initialComponent();
	}

	public FolderBrowserDialog(File rootFolder, File initialFolder, Dialog owner, String title, boolean modal)
		throws HeadlessException
	{
		super(owner, title, modal);
		if (rootFolder != null && !rootFolder.isDirectory())
			throw new IllegalArgumentException("rootFolder argument is not directory.");
		if (initialFolder != null && !initialFolder.isDirectory())
			throw new IllegalArgumentException("initialFolder argument is not directory.");
		this.fileRoot = rootFolder;
		this.fileInitial = initialFolder;
		this.btnCreate  = new JButton(TEXT_CREATE);
		this.btnCancel  = new JButton(TEXT_CANCEL);
		this.btnOK      = new JButton(TEXT_OK);
		this.mainPanel  = new JPanel();
		this.folderTree = createTreeComponent();
		initialComponent();
	}

	public FolderBrowserDialog(File rootFolder, File initialFolder, Frame owner, String title, boolean modal, GraphicsConfiguration gc)
	{
		super(owner, title, modal, gc);
		if (rootFolder != null && !rootFolder.isDirectory())
			throw new IllegalArgumentException("rootFolder argument is not directory.");
		if (initialFolder != null && !initialFolder.isDirectory())
			throw new IllegalArgumentException("initialFolder argument is not directory.");
		this.fileRoot = rootFolder;
		this.fileInitial = initialFolder;
		this.btnCreate  = new JButton(TEXT_CREATE);
		this.btnCancel  = new JButton(TEXT_CANCEL);
		this.btnOK      = new JButton(TEXT_OK);
		this.mainPanel  = new JPanel();
		this.folderTree = createTreeComponent();
		initialComponent();
	}

	public FolderBrowserDialog(File rootFolder, File initialFolder, Frame owner, String title, boolean modal)
		throws HeadlessException
	{
		super(owner, title, modal);
		if (rootFolder != null && !rootFolder.isDirectory())
			throw new IllegalArgumentException("rootFolder argument is not directory.");
		if (initialFolder != null && !initialFolder.isDirectory())
			throw new IllegalArgumentException("initialFolder argument is not directory.");
		this.fileRoot = rootFolder;
		this.fileInitial = initialFolder;
		this.btnCreate  = new JButton(TEXT_CREATE);
		this.btnCancel  = new JButton(TEXT_CANCEL);
		this.btnOK      = new JButton(TEXT_OK);
		this.mainPanel  = new JPanel();
		this.folderTree = createTreeComponent();
		initialComponent();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このダイアログが終了したときのステータスを返す。
	 */
	public int getDialogResult() {
		return dialogResult;
	}

	/**
	 * フォルダ作成ボタンのインスタンスを返す。
	 */
	public JButton getCreateButton() {
		return btnCreate;
	}

	/**
	 * キャンセルボタンのインスタンスを返す。
	 */
	public JButton getCancelButton() {
		return btnCancel;
	}

	/**
	 * OKボタンのインスタンスを返す。
	 */
	public JButton getOkButton() {
		return btnOK;
	}

	/**
	 * メインパネルのインスタンスを返す。
	 */
	public JPanel getMainPanel() {
		return mainPanel;
	}

	/**
	 * フォルダ構成を表示するツリーコンポーネントのインスタンスを返す。
	 */
	public JTree getTreeComponent() {
		return folderTree;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected TreeModel createTreeModel() {
		FileSystemView view = FileSystemView.getFileSystemView();
		File[] roots = view.getRoots();

		File fRoot = roots[0];
		FolderTreeModel newModel = new FolderTreeModel(fRoot);
		
		return newModel;
	}

	/**
	 * このダイアログで適用されるツリーコンポーネントを生成する。
	 * @return	生成された <code>JTree</code> オブジェクト
	 */
	protected JTree createTreeComponent() {
		JTree tree = new JTree();
		tree.setEditable(false);
		tree.setRootVisible(true);
		tree.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		return tree;
	}

	/**
	 * ダイアログのコンポーネントを初期化する。
	 * このダイアログの初期化のために、コンストラクタから呼び出される。
	 */
	protected void initialComponent() {
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		
		// setup Main panel
		setupMainPanel();
		
		// setup Buttons
		setupButtonPanel();
		
		// finalize
		//this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		
		// setup actions
		setupBaseActions();
	}

	/**
	 * ダイアログの初期化。
	 * このダイアログが初めて表示される直前に呼び出される。
	 */
	protected void initDialog() {
	}
	
	protected void loadSettings(String prefix, ExConfiguration config) {
		// restore window bounds
		Point     wloc  = (config==null ? null : config.getWindowLocation(prefix));
		Dimension wsize = (config==null ? null : config.getWindowSize(prefix));
		if (wsize == null) {
			wsize = getDefaultSize();
		}
		WindowRectangle wrc = SwingTools.adjustWindowLocationIntoScreen(this, wloc, wsize, null);
		wloc  = wrc.getLocation();
		wsize = wrc.getSize();
		
		// size
		if (wsize != null) {
			setPreferredSize(wsize);
			setSize(wsize);
		} else {
			pack();
		}
		
		// location
		if (wloc != null) {
			setLocation(wloc);
		} else {
			wloc = getDefaultLocation();
			if (wloc != null) {
				setLocation(wloc);
			} else {
				setLocationRelativeTo(getOwner());
			}
		}
	}
	
	protected void saveSettings(String prefix, ExConfiguration config) {
		// location
		if (isSaveLocation()) {
			config.setWindowLocation(prefix, this.getLocationOnScreen());
		}
		
		// size
		if (isSaveSize()) {
			Dimension defSize = getDefaultSize();
			Dimension curSize = getSize();
			// defSize == curSize なら、プロパティのエントリは null とする
			if (defSize != null && defSize.equals(curSize)) {
				curSize = null;
			}
			config.setWindowSize(prefix, curSize);
		}
	}
	
	protected boolean isSaveLocation() {
		return this.flgSaveLocation;
	}
	
	protected boolean isSaveSize() {
		return this.flgSaveSize;
	}
	
	protected void setSaveLocation(boolean toAllow) {
		this.flgSaveLocation = toAllow;
	}
	
	protected void setSaveSize(boolean toAllow) {
		this.flgSaveSize = toAllow;
	}
	
	protected boolean isKeepMinimumSize() {
		return this.flgKeepMinimumSize;
	}
	
	protected void setKeepMinimumSize(boolean toAllow) {
		this.flgKeepMinimumSize = toAllow;
	}
	
	protected Point getDefaultLocation() {
		return null;
	}
	
	protected Dimension getDefaultSize() {
		return null;
	}
	
	protected void setCreateButtonText(String text) {
		btnCreate.setText(text);
	}
	
	protected void setOKButtonText(String text) {
		btnOK.setText(text);
	}
	
	protected void setCancelButtonText(String text) {
		btnCancel.setText(text);
	}
	
	protected void loadSettings() {
		// place holder
	}
	
	protected void saveSettings() {
		// place holder
	}
	
	protected void setupMainPanel() {
		// setup Tree model
		TreeModel model = createTreeModel();
		folderTree.setModel(model);
		
		// setup scroll view
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sc.setViewportView(folderTree);
		
		// setup panel
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		// add to panel
		mainPanel.add(sc, BorderLayout.CENTER);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
	protected void setupButtonPanel() {
		int cbWidth = 0;
		int maxWidth = 0;
		int maxHeight = 0;
		Dimension dim;
		//---
		dim = this.btnCreate.getPreferredSize();
		cbWidth = dim.width;
		maxHeight = Math.max(maxHeight, dim.height);
		//---
		dim = this.btnOK.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		//---
		dim = this.btnCancel.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		//---
		dim = new Dimension(cbWidth, maxHeight);
		this.btnCreate.setPreferredSize(dim);
		dim = new Dimension(maxWidth, maxHeight);
		this.btnOK    .setPreferredSize(dim);
		this.btnCancel.setPreferredSize(dim);
		
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(this.btnCreate);
		btnBox.add(Box.createHorizontalGlue());
		btnBox.add(this.btnOK);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(this.btnCancel);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(Box.createRigidArea(new Dimension(0, dim.height+10)));
		
		// setup default button
		this.btnOK.setDefaultCapable(true);
		
		this.getContentPane().add(btnBox, BorderLayout.SOUTH);
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected void onButtonCreate() {
		
	}
	
	protected boolean onButtonOK() {
		return true;	// Close dialog
	}
	
	protected boolean onButtonCancel() {
		return true;	// Close dialog
	}
	
	protected void dialogClose(int result) {
		dialogResult = result;
		saveSettings();
		dispose();
	}
	
	//------------------------------------------------------------
	// Actions
	//------------------------------------------------------------
	
	private void setupBaseActions() {
		//--- Create button event
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onButtonCreate();
			}
		});
		
		//--- OK button event
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean canClose = onButtonOK();
				if (canClose) {
					dialogClose(DialogResult_OK);
				}
			}
		});
		
		//--- Cancel button event
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean canClose = onButtonCancel();
				if (canClose) {
					dialogClose(DialogResult_Cancel);
				}
			}
		});

		//--- Window event
		DialogWindowListener wl = new DialogWindowListener();
		this.addWindowListener(wl);
		
		//--- Component event
		DialogComponentListener cl = new DialogComponentListener();
		this.addComponentListener(cl);
	}
	
	//------------------------------------------------------------
	// Window actions
	//------------------------------------------------------------
	
	protected void onWindowOpened(WindowEvent e) {
		loadSettings();
		SwingTools.adjustWindowBoundsIntoWindowCenterScreen(this);
		initDialog();
	}
	
	protected void onWindowClosing(WindowEvent e) {
		// [Cancel]ボタンと同様の動作とする
		boolean canClose = onButtonCancel();
		if (canClose) {
			dialogClose(DialogResult_Cancel);
		}
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
	    public void windowOpened(WindowEvent e) { onWindowOpened(e); }

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
	    public void componentMoved(ComponentEvent e) { onMoved(e); }
	    
	    //--- Window resized
	    public void componentResized(ComponentEvent e) {
	    	onResized(e);
	    	if (isKeepMinimumSize()) {
	    		Dimension dmMin = getMinimumSize();
	    		int cw = getSize().width;
	    		int ch = getSize().height;
	    		if (dmMin != null && (cw < dmMin.width || ch < dmMin.height)) {
	    			setSize((cw<dmMin.width)?dmMin.width:cw, (ch<dmMin.height)?dmMin.height:ch);
	    		}
	    	}
	    }

	    //--- Window shown
	    public void componentShown(ComponentEvent e) { onShown(e); }

	    //--- Window hidden
	    public void componentHidden(ComponentEvent e) { onHidden(e); }
	}
	
	static protected class FolderTreeNode implements TreeNode
	{
		private final File		_file;
		
		private FolderTreeNode	_parent;
		private boolean		_leaf;
		private boolean		_loaded;
		private long			_lastModified;
		
		private ArrayList<FolderTreeNode> _children;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		public FolderTreeNode(File file) {
			this(file, null);
		}
		
		public FolderTreeNode(File file, FolderTreeNode parent) {
			if (file == null)
				throw new IllegalArgumentException("file argument is null!");
			this._file = file;
			this._parent = parent;
			this._leaf = !file.isDirectory();
			this._lastModified = file.lastModified();
			
			this._loaded = false;
			this._children = null;
		}
		
		public File getFile() {
			return _file;
		}
		
		public FolderTreeNode getParent() {
			return _parent;
		}
		
		public void setParent(FolderTreeNode parent) {
			this._parent = parent;
		}
		
		public boolean getAllowsChildren() {
			return (!_leaf);
		}

		public boolean isLeaf() {
			return _leaf;
		}

		public int getChildCount() {
			if (!isLoaded())
				loadChildren();
			return (_children==null ? 0 : _children.size());
		}

		public FolderTreeNode getChildAt(int childIndex) {
			if (!isLoaded())
				loadChildren();
			if (_children == null)
				throw new ArrayIndexOutOfBoundsException("node has no children");
			return _children.get(childIndex);
		}

		/**
		 * 指定されたノードがこのノードの子ノードである場合に、その子ノードの
		 * インデックスを返す。指定されたノードがこのノードの子ノードではない
		 * 場合は (-1) を返す。
		 */
		public int getIndex(TreeNode aChild) {
			if (!isLoaded())
				loadChildren();
			if (aChild == null)
				throw new IllegalArgumentException("aChild argument is null!");
			if (_children==null || _children.isEmpty())
				return (-1);
			if (aChild.getParent() != this)
				return (-1);
			return _children.indexOf(aChild);
		}

		public Enumeration children() {
			if (!isLoaded())
				loadChildren();
			if (_children==null)
				return EMPTY_ENUMERATION;
			else {
				return new Enumeration<FolderTreeNode>(){
								ArrayList<FolderTreeNode> _list = _children;
								int _index = 0;
								
								public boolean hasMoreElements() {
									return (_index < _list.size());
								}
								
								public FolderTreeNode nextElement() {
									if (_index < _list.size()) {
										return _list.get(_index++);
									}
									throw new NoSuchElementException("Children Enumeration");
								}
							};
			}
		}
		
		@Override
		public String toString() {
			return _file.getName();
		}
		
		protected boolean isLoaded() {
			long lm = _file.lastModified();
			if (lm != _lastModified) {
				_lastModified = lm;
				_loaded = false;
				_leaf = !_file.isDirectory();
				if (_children != null) {
					_children.clear();
				}
			}
			
			return _loaded;
		}
		
		protected File[] getChildFiles(File targetFile) {
			// リスト取得
			File[] flist = targetFile.listFiles(new FileFilter(){
				public boolean accept(File pathname) {
					// ディレクトリのみを許可
					return pathname.isDirectory();
				}
			});
			
			// 語彙順に並べ替え
			if (flist != null && flist.length > 1) {
				Arrays.sort(flist);
			}
			
			return flist;
		}

		protected void loadChildren() {
			_loaded = true;
			if (_leaf) {
				if (_children != null) {
					_children.clear();
					_children = null;
				}
			}
			else {
				File[] flist = getChildFiles(_file);
				if (flist != null) {
					if (_children == null) {
						_children = new ArrayList<FolderTreeNode>(flist.length);
					} else {
						_children.clear();
					}
					for (File f : flist) {
						FolderTreeNode newNode = new FolderTreeNode(f, this);
						_children.add(newNode);
					}
				}
				else {
					if (_children != null) {
						_children.clear();
						_children = null;
					}
				}
			}
		}
		
	}
	
	static protected class FolderTreeModel extends AbstractTreeModel
	{
		public FolderTreeModel(File rootFile) {
			super(rootFile==null ? null : new FolderTreeNode(rootFile));
		}
		
		public File getRootFile() {
			FolderTreeNode root = getRoot();
			return (root==null ? null : root.getFile());
		}
		
		@Override
		public FolderTreeNode getRoot() {
			return (FolderTreeNode)super.getRoot();
		}

		@Override
		public void setRoot(TreeNode root) {
			throw new UnsupportedOperationException("Not supported!");
		}

		public void valueForPathChanged(TreePath path, Object newValue) {
			throw new UnsupportedOperationException("Not supported!");
		}
	}
	
	//------------------------------------------------------------
	//
	//------------------------------------------------------------
}
