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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ChartDialogManager.java	2.1.0	2013/08/30
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.plot;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.menu.RunnerMenuResources;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.util.swing.IDialogResult;

/**
 * チャート関連ダイアログを管理するクラス。
 * このクラスでは、1つのCSVファイルに対する、1つのチャート表示フレームと1つのチャート設定ダイアログを保持する。
 * チャート表示フレームとチャート設定ダイアログは、同時には表示されないものとし、常にどちらかがアクティブな
 * 状態とする。
 * 
 * @version 2.1.0	2013/08/30
 * @since 2.1.0
 */
public class ChartDialogManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** メインフレーム **/
	private RunnerFrame					_mainFrame;
	/** チャートビュー **/
	private ManagedChartViewFrame		_chartView;
	/** チャート設定ダイアログ **/
	private ManagedChartConfigDialog	_dlgConfig;
	/** チャート設定ダイアログの親ウィンドウ(dummy) **/
	private JFrame						_dlgParent;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ChartDialogManager(RunnerFrame frame) {
		if (frame == null)
			throw new NullPointerException("RunnerFrame object is null.");
		_mainFrame  = frame;
		_dlgConfig  = null;
		_chartView  = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isChartWindowDocumentFile(File targetFile) {
		return (isChartConfigDocumentFile(targetFile) || isChartViewDocumentFile(targetFile));
	}
	
	public boolean isChartConfigDocumentFile(File targetFile) {
		if (targetFile != null && _dlgConfig != null) {
			if (targetFile.equals(_dlgConfig.getConfigModel().getTargetFile())) {
				// The specified file is chart config data.
				return true;
			}
		}
		// no chart data
		return false;
	}
	
	public boolean isChartViewDocumentFile(File targetFile) {
		if (targetFile != null && _chartView != null) {
			if (targetFile.equals(_chartView.getConfigModel().getTargetFile())) {
				// The specified file is chart view data.
				return true;
			}
		}
		// no chart data
		return false;
	}
	
	public boolean isVisibleAnyWindow() {
		return (isVisibleChartConfigDialog() || isVisibleChartViewFrame());
	}
	
	public boolean isVisibleChartConfigDialog() {
		return (_dlgConfig != null && _dlgConfig.isVisible());
	}
	
	public boolean isVisibleChartViewFrame() {
		return (_chartView != null && _chartView.isVisible());
	}

	/**
	 * 指定されたファイルを保持するチャートウィンドウを破棄する。
	 * @param targetFile	対象ファイル
	 * @return	すべてのウィンドウが破棄された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean destroyChartByFile(File targetFile) {
		if (isChartViewDocumentFile(targetFile)) {
			destroyChartViewFrame();
		}
		boolean discardedView = (_chartView==null || !_chartView.isDisplayable());
		
		if (isChartConfigDocumentFile(targetFile)) {
			destroyChartConfigDialog();
		}
		boolean discardedConfig = (_dlgConfig==null || !_dlgConfig.isDisplayable());
		
		return (discardedView && discardedConfig);
	}
	
	public void destroyChartConfigDialog() {
		if (_dlgConfig != null) {
			_dlgConfig.destroy();
			_dlgConfig = null;
			
			if (_dlgParent != null) {
				if (_dlgParent.isDisplayable()) {
					_dlgParent.dispose();
				}
				_dlgParent = null;
			}
		}
	}
	
	public void destroyChartViewFrame() {
		if (_chartView != null) {
			_chartView.destroy();
			_chartView = null;
		}
	}
	
	public void activeWindowToFront() {
		if (_dlgConfig != null && _dlgConfig.isVisible()) {
			if (_chartView != null && _chartView.isVisible()) {
				_chartView.toFront();
			}
			_dlgConfig.toFront();
		}
		else if (_chartView != null && _chartView.isVisible()) {
			_chartView.toFront();
		}
	}
	
	public void setFocusToActiveWindow() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (_dlgConfig != null && _dlgConfig.isVisible()) {
					_dlgConfig.requestFocusInWindow();
				}
				else if (_chartView != null && _chartView.isVisible()) {
					_chartView.requestFocusInWindow();
				}
			}
		});
	}
	
	public void doChartConfig(final ChartConfigModel model) {
		// destroy config dialog
		if (_dlgConfig != null) {
			_dlgConfig.destroy();
			_dlgConfig = null;
		}
		
		// check view exists
		if (_chartView != null) {
			if (!_chartView.isDisplayable()) {
				_chartView = null;
			}
		}
		
		// check dummy frame
		if (_dlgParent == null) {
			_dlgParent = createDummyFrame();
		}
		
		// create new dialog
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_dlgConfig = new ManagedChartConfigDialog(_dlgParent, model);
				_dlgConfig.initialComponent();
				_dlgConfig.setVisible(true);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_dlgConfig.requestFocusInWindow();
					}
				});
				_mainFrame.updateMenuItem(RunnerMenuResources.ID_HIDE_SHOW_CHARTWINDOW);
			}
		});
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected JFrame createDummyFrame() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setIconImage(ModuleRunner.getAppIconImage());
		return frame;
	}
	
	protected ManagedChartViewFrame createChartViewFrame(ChartConfigModel model) {
		ManagedChartViewFrame view = new ManagedChartViewFrame(_mainFrame, model);
		view.initialComponent();
		return view;
	}
	
	protected void onConfigDialogClosed(WindowEvent we) {
		if (_dlgConfig != null && we.getSource() == _dlgConfig) {
			ManagedChartConfigDialog dlg = _dlgConfig;
			_dlgConfig = null;
			dlg.destroy();
			
			if (dlg.getDialogResult() == IDialogResult.DialogResult_OK) {
				// change configuration of chart
				ChartConfigModel model = dlg.getConfigModel();
				// show chart view
				if (_chartView == null) {
					_chartView = createChartViewFrame(model);
					_chartView.setVisible(true);
					_chartView.refreshView();
				} else {
					_chartView.setVisible(true);
					_chartView.setModel(model);
				}
				_mainFrame.updateMenuItem(RunnerMenuResources.ID_HIDE_SHOW_CHARTWINDOW);
			}
			else {
				// user canceled
				if (_chartView != null && _chartView.isVisible()) {
					// re-visible chart view
					_chartView.toFront();
					_mainFrame.updateMenuItem(RunnerMenuResources.ID_HIDE_SHOW_CHARTWINDOW);
				}
				else {
					// delete chart window
					_mainFrame.deleteChartWindow();
					_mainFrame.setFocusToActiveView();
				}
			}
		}
	}
	
	protected void onChartViewClosed(WindowEvent we) {
		if (_chartView != null && we.getSource() == _chartView) {
			ManagedChartViewFrame view = _chartView;
			_chartView = null;
			view.destroy();
			
			if (_dlgConfig != null && _dlgConfig.isVisible()) {
				_dlgConfig.toFront();
				_mainFrame.updateMenuItem(RunnerMenuResources.ID_HIDE_SHOW_CHARTWINDOW);
			}
			else {
				_mainFrame.deleteChartWindow();
				_mainFrame.setFocusToActiveView();
			}
		}
	}
	
	static boolean isEqualTargetFile(ChartConfigModel model1, ChartConfigModel model2) {
		return (model1.getTargetFile().equals(model2.getTargetFile()));
	}
	
	static boolean isEqualTargetFile(ChartConfigModel model, File file) {
		return (model.getTargetFile().equals(file));
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	class ManagedChartConfigDialog extends ChartConfigDialog
	{
		private static final long serialVersionUID = 1L;

		public ManagedChartConfigDialog(Frame owner, ChartConfigModel chartModel) {
			super(owner, chartModel, false);
		}

		@Override
		protected void onShowTargetFile(ActionEvent ae) {
			_mainFrame.toFront();
			_mainFrame.openEditorFromFile(_dlgConfig.getConfigModel().getTargetFile());
			_mainFrame.setFocusToActiveEditor();
		}

		@Override
		protected void onWindowClosed(WindowEvent e) {
			onConfigDialogClosed(e);
		}
	}
	
	class ManagedChartViewFrame extends ChartViewFrame
	{
		private static final long serialVersionUID = 1L;

		public ManagedChartViewFrame(RunnerFrame owner, ChartConfigModel chartModel) {
			super(owner, chartModel);
		}

		@Override
		protected void onChartConfigButton(ActionEvent ae) {
			// 設定変更
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ChartConfigModel newModel = new ChartConfigModel(ManagedChartViewFrame.this.getConfigModel());
					doChartConfig(newModel);
				}
			});
		}

		@Override
		protected void onWindowClosed(WindowEvent e) {
			onChartViewClosed(e);
		}
	}
}
