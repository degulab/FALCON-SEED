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
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)AADLMacroGraphDialog.java	1.20	2012/03/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.graph.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.macro.graph.AADLMacroGraphData;
import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.ProgressMonitorTask;


/**
 * AADLマクロファイルのグラフを表示するダイアログ。
 * 
 * @version 1.20	2012/03/19
 * @since 1.20
 */
public class AADLMacroGraphDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(640, 480);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final AADLMacroGraphData	_graphdata;

	private DrawImagePanel	_paneImage;
	private JScrollPane	_paneScroll;
	
	private JButton		_btnSaveImage;
	private JButton		_btnExportDot;
//	private JButton		_btnExpansion;
//	private JButton		_btnReduction;
//	private JButton		_btnRealSize;
//	private JButton		_btnFitToView;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLMacroGraphDialog(Frame owner, String title, boolean modal, AADLMacroGraphData graphData) {
		super(owner, title, modal);
		if (graphData == null)
			throw new NullPointerException("The Graph data is null.");
		setConfiguration(AppSettings.AADLMACROGRAPH_DLG, AppSettings.getInstance().getConfiguration());
		this._graphdata = graphData;
	}
	
	public AADLMacroGraphDialog(Dialog owner, String title, boolean modal, AADLMacroGraphData graphData) {
		super(owner, title, modal);
		if (graphData == null)
			throw new NullPointerException("The Graph data is null.");
		setConfiguration(AppSettings.AADLMACROGRAPH_DLG, AppSettings.getInstance().getConfiguration());
		this._graphdata = graphData;
	}
	
	@Override
	public void initialComponent() {
		// コンポーネントの初期化
		setStoreLocation(false);
		super.initialComponent();
		
		// 設定情報の反映
		restoreConfiguration();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public AADLMacroGraphData getGraphData() {
		return _graphdata;
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected void initDialog() {
		super.initDialog();
	}
	
	protected void onSaveImageButton(ActionEvent ae) {
		String title = RunnerMessages.getInstance().AADLMacroGraphDlg_Title_ImageChooser;
		File fSave = FileChooserManager.chooseSaveFileAndCheckOverwrite(this, title,
							FileChooserManager.getLastChooseDocumentFile(), FileChooserManager.getPngImageFileFilter());
		if (fSave == null) {
			return;		// user canceled.
		}
		FileChooserManager.setLastChooseDocumentFile(fSave);
		
		// 保存
		FileSaveProgressMonitorTask task = new FileSaveProgressMonitorTask(title, _graphdata.getImageFile(), ModuleFileManager.fromJavaFile(fSave));
		task.execute(this);
		if (task.getErrorCause() != null) {
			String errmsg = CommonMessages.getInstance().msgCouldNotWriteFile;
			AppLogger.error(CommonMessages.formatErrorMessage("Save GraphViz image : " + errmsg,
					task.getErrorCause(), String.valueOf(_graphdata.getImageFile()), String.valueOf(fSave)));
			Application.showErrorMessage(this, errmsg);
		}
	}
	
	protected void onExportDotButton(ActionEvent ae) {
		String title = RunnerMessages.getInstance().AADLMacroGraphDlg_Title_DotChooser;
		File fSave = FileChooserManager.chooseSaveFileAndCheckOverwrite(this, title,
							FileChooserManager.getLastChooseDocumentFile(), FileChooserManager.getGraphVizDotFileFilter());
		if (fSave == null) {
			return;		// user canceled.
		}
		FileChooserManager.setLastChooseDocumentFile(fSave);
		
		// 保存
		FileSaveProgressMonitorTask task = new FileSaveProgressMonitorTask(title, _graphdata.getDotFile(), ModuleFileManager.fromJavaFile(fSave));
		task.execute(this);
		if (task.getErrorCause() != null) {
			String errmsg = CommonMessages.getInstance().msgCouldNotWriteFile;
			AppLogger.error(CommonMessages.formatErrorMessage("Save GraphViz dot file : " + errmsg,
					task.getErrorCause(), String.valueOf(_graphdata.getDotFile()), String.valueOf(fSave)));
			Application.showErrorMessage(this, errmsg);
		}
	}
	
	protected void onExpansionButton(ActionEvent ae) {
		_paneImage.setScale(_paneImage.getScale() * 2.0);
		_paneScroll.revalidate();
		_paneScroll.repaint();
	}
	
	protected void onReductionButton(ActionEvent ae) {
		_paneImage.setScale(_paneImage.getScale() * 0.5);
		_paneScroll.revalidate();
		_paneScroll.repaint();
	}
	
	protected void onRealSizeButton(ActionEvent ae) {
		_paneImage.setScale(1.0);
		_paneScroll.revalidate();
		_paneScroll.repaint();
	}
	
	protected void onFitToViewButton(ActionEvent ae) {
		Rectangle rcViewport = _paneScroll.getViewport().getBounds();
		Dimension dmSize = _paneScroll.getSize();
		dmSize.width  -= (rcViewport.x * 2);
		dmSize.height -= (rcViewport.y * 2);
		if (dmSize.width <= 0 || dmSize.height <= 0) {
			// 表示領域なし
			return;
		}
		
		int iw = _paneImage.getImageWidth();
		int ih = _paneImage.getImageHeight();
		if (iw > 0 && ih > 0) {
			if (dmSize.width >= iw || dmSize.height >= ih) {
				// ピクセル等倍
				_paneImage.setScale(1.0);
			}
			else {
				double daw = (double)dmSize.width / (double)iw;
				double dah = (double)dmSize.height / (double)ih;
				_paneImage.setScale(Math.min(daw, dah));
			}
			_paneScroll.revalidate();
			_paneScroll.repaint();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected JButton createCancelButton() {
		JButton btn = super.createCancelButton();
		btn.setText(CommonMessages.getInstance().Button_Close);
		return btn;
	}

	@Override
	protected JButton createOkButton() {
		// OKボタンは表示しない
		return null;
	}

	@Override
	protected JButton createApplyButton() {
		// no apply button
		return null;
	}
	
	protected JPanel createMainPanel() {
		// create local components
		_paneImage  = new DrawImagePanel(_graphdata.getGraphImage());
		_paneScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_paneScroll.setViewportView(_paneImage);
		//--- buttons
		_btnSaveImage = CommonResources.createIconButton(CommonResources.ICON_FILE_SAVE, RunnerMessages.getInstance().AADLMacroGraphDlg_tooltip_SaveImage);
		_btnExportDot = CommonResources.createIconButton(CommonResources.ICON_EXPORT, RunnerMessages.getInstance().AADLMacroGraphDlg_tooltip_ExportDot);
//		_btnExpansion = new JButton("拡大");
//		_btnReduction = new JButton("縮小");
//		_btnRealSize  = new JButton("ピクセル等倍");
//		_btnFitToView = new JButton("画面サイズに合わせる");
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.add(_btnSaveImage);
		toolbar.addSeparator();
		toolbar.add(_btnExportDot);
//		toolbar.addSeparator();
//		toolbar.add(_btnExpansion);
//		toolbar.add(_btnReduction);
//		toolbar.addSeparator();
//		toolbar.add(_btnRealSize);
//		toolbar.add(_btnFitToView);
		
		// create Main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 0, 5));
		mainPanel.add(_paneScroll, BorderLayout.CENTER);
		mainPanel.add(toolbar, BorderLayout.NORTH);
		
		return mainPanel;
	}

	@Override
	protected void setupActions() {
		super.setupActions();
		
		_btnSaveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				onSaveImageButton(ae);
			}
		});
		//---
		_btnExportDot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				onExportDotButton(ae);
			}
		});
//		//---
//		_btnExpansion.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				onExpansionButton(ae);
//			}
//		});
//		//---
//		_btnReduction.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				onReductionButton(ae);
//			}
//		});
//		//---
//		_btnRealSize.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				onRealSizeButton(ae);
//			}
//		});
//		//---
//		_btnFitToView.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				onFitToViewButton(ae);
//			}
//		});
	}

	@Override
	protected void setupMainContents() {
		JPanel mainPanel = createMainPanel();
		
		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
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

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class FileSaveProgressMonitorTask extends ProgressMonitorTask
	{
		static private int BUF_SIZE = 50000;
		
		private final VirtualFile	_vfSource;
		private final VirtualFile	_vfDest;
		private long	_maxValue;
		private long	_curValue;
		
		public FileSaveProgressMonitorTask(String title, VirtualFile source, VirtualFile dest)
		{
			super(title, null, null, 0, 0, 100, false);
			if (source == null)
				throw new NullPointerException("The source file is null.");
			if (dest == null)
				throw new NullPointerException("The destination file is null.");
			this._vfSource = source;
			this._vfDest   = dest;
		}

		@Override
		public void processTask() throws Throwable {
			// ファイルサイズ取得
			_maxValue = _vfSource.length();
			setMaximum(100);
			
			copyFile(_vfSource, _vfDest);
		}
		
		protected void setLongValue(long value) {
			_curValue = value;
			if (_maxValue > 0L) {
				setValue((int)((double)_curValue / (double)_maxValue * 100.0));
			}
		}
		
		protected void addLongValue(long value) {
			_curValue += value;
			if (_maxValue > 0L) {
				setValue((int)((double)_curValue / (double)_maxValue * 100.0));
			}
		}
		
		protected long copyFile(VirtualFile source, VirtualFile dest)
		throws IOException
		{
			byte[] buffer = new byte[BUF_SIZE];
			
			InputStream fis = null;
			OutputStream fos = null;
			long ret = 0L;
			try {
				fis = source.getInputStream();
				fos = dest.getOutputStream();
				ret = copyFile(fis, fos, buffer);
				//--- set attribute by java.io.File
				if (!source.canWrite())
					dest.setReadOnly();
				dest.setLastModified(source.lastModified());
			}
			finally {
				if (fis != null) {
					Files.closeStream(fis);
					fis = null;
				}
				if (fos != null) {
					Files.closeStream(fos);
					fos = null;
				}
			}
			
			//--- update attributes
			try {
				if (!source.canWrite()) {
					dest.setReadOnly();
				}
			} catch (Throwable ignoreEx) {}
			try {
				dest.setLastModified(source.lastModified());
			} catch (Throwable ignoreEx) {}
			
			return ret;
		}
		
		protected long copyFile(InputStream inStream, OutputStream outStream, byte[] buffer)
		throws IOException
		{
			long bytesCopied = 0;
			int read = -1;
			
			while ((read = inStream.read(buffer, 0, buffer.length)) != -1) {
				outStream.write(buffer, 0, read);
				addLongValue(read);
				bytesCopied += read;
			}

			return bytesCopied;
		}
	}
}
