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
 * @(#)ChartViewFrame.java	3.2.2	2015/10/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ChartViewFrame.java	2.1.0	2013/08/30
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.plot.ChartConfigModel.ChartConfigRecordNumberField;
import ssac.falconseed.plot.ChartConfigModel.ChartDataTypes;
import ssac.falconseed.plot.ChartConfigModel.ChartInvalidValuePolicy;
import ssac.falconseed.plot.ChartConfigModel.ChartStyles;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.AbSubFrame;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.ProgressMonitorTask;
import ssac.util.swing.plot.CustomPlot;
import ssac.util.swing.plot.DefaultDataSeries;
import ssac.util.swing.plot.IDataField;
import ssac.util.swing.plot.PlotDataField;
import ssac.util.swing.plot.PlotDataSeries;
import ssac.util.swing.plot.PlotDateTimeField;
import ssac.util.swing.plot.PlotDateTimeFormats;
import ssac.util.swing.plot.PlotDecimalField;
import ssac.util.swing.plot.PlotLineStyles;
import ssac.util.swing.plot.PlotMarkStyles;
import ssac.util.swing.plot.PlotModel;
import ssac.util.swing.plot.PlotRecordNumberField;
import ssac.util.swing.plot.PlotStringField;
import ssac.util.swing.plot.PlotStyles;

/**
 * チャートを表示するフレーム。
 * タスクバーへの表示も行うため、フレームとする。
 * 
 * @version 3.2.2
 * @since 2.1.0
 */
public class ChartViewFrame extends AbSubFrame
{
	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(800, 600);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** チャート設定 **/
	private ChartConfigModel	_configModel;
	/** チャート描画パネル **/
	private CustomPlot			_plotPanel;
	/** このダイアログのツールバー **/
	private JToolBar			_toolbar;
	/** イメージを保存ボタン **/
	private JButton			_btnSaveImage;
	/** 更新ボタン **/
	private JButton			_btnRefresh;
	/** ファイル表示ボタン **/
	private JButton			_btnCsvView;
	/** 設定ボタン **/
	private JButton			_btnConfig;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ChartViewFrame(RunnerFrame owner, ChartConfigModel chartModel) {
		super(owner, RunnerMessages.getInstance().ChartConfigDlg_title);
		if (chartModel == null)
			throw new NullPointerException("ChartConfigModel object is null.");
		_configModel = chartModel;
		updateDialogTitle();
		setConfiguration(AppSettings.CHARTVIEW_DLG, AppSettings.getInstance().getConfiguration());
		
		// icon
		Image iconImage = ModuleRunner.getApplicationMainFrame().getIconImage();
		if (iconImage != null) {
			setIconImage(iconImage);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@Override
	public RunnerFrame getParentWindow() {
		return (RunnerFrame)super.getParentWindow();
	}
	
	public ChartConfigModel getConfigModel() {
		return _configModel;
	}

	/**
	 * このウィンドウリソースを破棄する。
	 */
	public void destroy() {
		if (isDisplayable()) {
			closeWindow(0);
		}
		_configModel = null;
	}
	
	public void setModel(ChartConfigModel newModel) {
		if (newModel == null)
			throw new NullPointerException("ChartConfigModel object is null.");

		// set new chart config model
		_configModel = newModel;
		refreshView();
	}
	
	public void refreshView() {
		updateDialogTitle();
		refreshPlotModel();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_plotPanel.refreshPlotImage();
				requestFocusInWindow();
			}
		});
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected void onSaveImageButton(ActionEvent ae) {
		BufferedImage img = _plotPanel.getDrawingImage();
		if (img == null) {
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartViewNoImage);
			return;
		}
		
		String title = RunnerMessages.getInstance().ChartViewDlg_Title_ImageChooser;
		File fSave = FileChooserManager.chooseSaveFileAndCheckOverwrite(this, title,
							FileChooserManager.getLastChooseDocumentFile(), FileChooserManager.getPngImageFileFilter());
		if (fSave == null) {
			return;		// user canceled.
		}
		FileChooserManager.setLastChooseDocumentFile(fSave);
		
		// 保存
		FileSaveProgressMonitorTask task = new FileSaveProgressMonitorTask(title, img, ModuleFileManager.fromJavaFile(fSave));
		task.execute(this);
		if (task.getErrorCause() != null) {
			String errmsg = CommonMessages.getInstance().msgCouldNotWriteFile;
			AppLogger.error(CommonMessages.formatErrorMessage("Save Chart image : " + errmsg,
					task.getErrorCause(), String.valueOf(fSave)));
			Application.showErrorMessage(this, errmsg);
		}
	}

	protected void onRefreshButton(ActionEvent ae) {
		// 現在の設定で再描画
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_plotPanel.refreshPlotImage();
			}
		});
	}
	
	protected void onChartConfigButton(ActionEvent ae) {
		// 設定変更
		ChartConfigModel newModel = new ChartConfigModel(_configModel);
		// ダイアログ作成
		ChartConfigDialog dlg = new ChartConfigDialog(this, newModel, true);
		dlg.initialComponent();
		dlg.setVisible(true);
		if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
			// 新しいモデルに更新
			_configModel = newModel;
			updateDialogTitle();
			refreshPlotModel();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					_plotPanel.refreshPlotImage();
				}
			});
		}
	}
	
	protected void onChartCsvViewButton(ActionEvent ae) {
		RunnerFrame frame = getParentWindow();
		frame.toFront();
		frame.openEditorFromFile(_configModel.getTargetFile());
		frame.setFocusToActiveEditor();
	}

	//------------------------------------------------------------
	// Error messages
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void updateDialogTitle() {
		StringBuilder sb = new StringBuilder();
		//--- チャート種類
		switch (_configModel.getChartStyle()) {
			case SCATTER :
				sb.append("[");
				sb.append(RunnerMessages.getInstance().ChartStyle_Scatter);
				sb.append("] ");
				break;
			case LINE :
				sb.append("[");
				sb.append(RunnerMessages.getInstance().ChartStyle_Line);
				sb.append("] ");
				break;
		}
		//--- チャートタイトル
		String strChartTitle = _configModel.getChartTitle();
		if (strChartTitle != null && strChartTitle.length() > 0) {
			sb.append(strChartTitle);
			sb.append(" ");
		}
		//--- ファイル名
		sb.append("(");
		sb.append(_configModel.getTargetFile().getName());
		sb.append(")");
		
		// タイトル設定
		setTitle(sb.toString());
	}
	
	protected String makeFieldKey(ChartDataTypes type, IDataField field) {
		if (field instanceof ChartConfigRecordNumberField) {
			// record number field
			return "Record#";
		} else {
			// data field
			return (type.toString() + "_" + String.valueOf(field.getFieldIndex()));
		}
	}
	
	protected PlotDataField createPlotDataField(ChartDataTypes type, PlotDateTimeFormats formats, IDataField field) {
		if (field instanceof ChartConfigRecordNumberField) {
			return new PlotRecordNumberField(field.getDataTable(), ((ChartConfigModel)field.getDataTable()).getFirstDataRecordIndex(), field.getFieldName());
		}
		else if (type == ChartDataTypes.TEXT) {
			return new PlotStringField(field.getDataTable(), field.getFieldIndex());
		}
		else if (type == ChartDataTypes.DATETIME) {
			return new PlotDateTimeField(field.getDataTable(), field.getFieldIndex(), formats);
		}
		else {
			return new PlotDecimalField(field.getDataTable(), field.getFieldIndex());
		}
	}

	/**
	 * 現在の設定から、<code>PlotDataField</code> オブジェクトのマップを生成する。
	 * これは、同じデータ型の同じフィールドのインスタンスを共通化するため。
	 * @return	生成されたマップ
	 */
	protected Map<String, PlotDataField> createPlotDataFieldMap() {
		boolean invalidAsZero = (_configModel.getInvalidValuePolicy() == ChartInvalidValuePolicy.AS_ZERO);
		PlotDateTimeFormats dtFormats = _configModel.getCustomDateTimeFormats();
		if (dtFormats == null) {
			dtFormats = new PlotDateTimeFormats();
		}
		ChartDataTypes xType = _configModel.getXDataType();
		ChartDataTypes yType = _configModel.getYDataType();
		
		HashMap<String, PlotDataField> map = new HashMap<String, PlotDataField>();
		ArrayList<DefaultDataSeries> list = _configModel.getDataSeriesList();
		String key;
		IDataField field;
		for (DefaultDataSeries series : list) {
			
			// x-field
			field = series.getXField();
			key = makeFieldKey(xType, field);
			if (!map.containsKey(key)) {
				PlotDataField plotField = createPlotDataField(xType, dtFormats, field);
				plotField.setInvalidValueAsZero(invalidAsZero);
				map.put(key, plotField);
			}
			
			// y-field
			field = series.getYField();
			key = makeFieldKey(yType, field);
			if (!map.containsKey(key)) {
				PlotDataField plotField = createPlotDataField(yType, dtFormats, field);
				plotField.setInvalidValueAsZero(invalidAsZero);
				map.put(key, plotField);
			}
		}
		
		return map;
	}
	
	protected void refreshPlotModel() {
		// プロットイメージを除去
		_plotPanel.clearPlotImage();
		
		// 現在のチャート設定から、データ系列のフィールドデータを生成
		Map<String, PlotDataField> map = createPlotDataFieldMap();
		
		PlotModel pmodel = _plotPanel.getPlotModel();
		if (!pmodel.getDataTable().equals(_configModel)) {
			pmodel = new PlotModel(_configModel);
			_plotPanel.setPlotModel(pmodel);
		}
		PlotStyles pstyle = pmodel.getStyles();
		
		// 描画スタイルを設定
		if (_configModel.getChartStyle() == ChartStyles.LINE) {
			// 折れ線
			pstyle.setLineStyle(PlotLineStyles.SOLID);
			pstyle.setMarkStyle(PlotMarkStyles.DOTS);
		} else {
			// 点(散布図)
			pstyle.setLineStyle(PlotLineStyles.NONE);
			pstyle.setMarkStyle(PlotMarkStyles.DOTS);
		}
		pstyle.setInvalidValueConnection(_configModel.getInvalidValuePolicy() == ChartInvalidValuePolicy.CONNECTED);
		
		// 描画情報
		ChartDataTypes xType = _configModel.getXDataType();
		ChartDataTypes yType = _configModel.getYDataType();
		PlotDataField xTickField = null;
		PlotDataField yTickField = null;
		pmodel.clearAllSeries();
		pmodel.setTitle(_configModel.getChartTitle());
		pmodel.setXLabel(_configModel.getXaxisLabel());
		pmodel.setYLabel(_configModel.getYaxisLabel());
		ArrayList<DefaultDataSeries> seriesList = _configModel.getDataSeriesList();
		for (DefaultDataSeries series : seriesList) {
			PlotDataField xField = map.get(makeFieldKey(xType, series.getXField()));
			PlotDataField yField = map.get(makeFieldKey(yType, series.getYField()));
			if (xTickField == null)
				xTickField = xField;
			if (yTickField == null)
				yTickField = yField;
			PlotDataSeries plotSeries = new PlotDataSeries(xField, yField);
			plotSeries.setLegend(series.getLegend());
			pmodel.addSeries(plotSeries);
		}
		pmodel.setXTicksField(xTickField);
		pmodel.setYTicksField(yTickField);
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}
//	public Dimension getDefaultWindowSize() {
//		return DM_MIN_SIZE;
//	}

	@Override
	protected void onInitialContents() {
		// create content components
		createContentComponents();
		
		// add to main panel
		this.getContentPane().add(_plotPanel, BorderLayout.CENTER);
		
		// add tool bar
		this.getContentPane().add(_toolbar, BorderLayout.NORTH);
	}

	@Override
	protected void onSetupConditions() {
		super.onSetupConditions();
		
		setResizable(true);
//		setStoreWindowLocation(true);
//		setStoreWindowSize(true);
		setStoreLocation(true);
		setStoreSize(true);
		setStoreWindowState(true);
		setKeepMinimumSize(false);
		setCloseByEscapeKeyEnabled(true);
	}

	@Override
	protected void onSetupActions() {
		super.onSetupActions();
		
		_btnSaveImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSaveImageButton(e);
			}
		});
		_btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onRefreshButton(e);
			}
		});
		_btnConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onChartConfigButton(e);
			}
		});
		_btnCsvView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onChartCsvViewButton(e);
			}
		});
	}

//	@Override
//	protected void onWindowOpened(WindowEvent e) {
//		// 最初の描画
//		refreshPlotModel();
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				_plotPanel.refreshPlotImage();
//				if (_plotPanel.getDrawingImage() == null) {
//					// no image
//					closeWindow(0);
//				}
//			}
//		});
//	}

	//------------------------------------------------------------
	// Create components
	//------------------------------------------------------------

	protected void createContentComponents() {
		// プロットコンポーネントの生成
		_plotPanel = new CustomPlot();
		
		// ツールバーの生成
		_btnSaveImage = CommonResources.createIconButton(CommonResources.ICON_FILE_SAVE, RunnerMessages.getInstance().ChartViewDlg_tooltip_SaveImage);
		_btnRefresh   = CommonResources.createIconButton(CommonResources.ICON_REFRESH, RunnerMessages.getInstance().ChartViewDlg_tooltip_Refresh);
		_btnCsvView   = CommonResources.createIconButton(CommonResources.ICON_CSVFILE, RunnerMessages.getInstance().ChartViewDlg_tooltip_CsvView);
		_btnConfig    = CommonResources.createIconButton(CommonResources.ICON_SETTING, RunnerMessages.getInstance().ChartViewDlg_tooltip_Config);
		_toolbar = new JToolBar(JToolBar.HORIZONTAL);
		_toolbar.add(_btnSaveImage);
		_toolbar.addSeparator();
		_toolbar.add(_btnRefresh);
		_toolbar.add(Box.createHorizontalGlue());
		_toolbar.add(_btnCsvView);
		_toolbar.addSeparator();
		_toolbar.add(_btnConfig);
		
		_toolbar.setFloatable(false);
	}
	
	protected JLabel createStaticLabel() {
		JLabel label = new JLabel();
		Border tfBorder = UIManager.getBorder("TextField.border");
		Insets tfInsets = UIManager.getInsets("TextField.margin");
		Color  tfColor  = UIManager.getColor("TextField.background");
		label.setBackground(tfColor);
		if (tfInsets != null) {
			label.setBorder(BorderFactory.createCompoundBorder(tfBorder,
					BorderFactory.createEmptyBorder(tfInsets.top, tfInsets.left, tfInsets.bottom, tfInsets.right)));
		} else {
			label.setBorder(tfBorder);
		}
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		return label;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class FileSaveProgressMonitorTask extends ProgressMonitorTask implements IIOWriteProgressListener
	{
		private final BufferedImage	_imgSource;
		private final VirtualFile		_vfDest;
		
		public FileSaveProgressMonitorTask(String title, BufferedImage source, VirtualFile dest)
		{
			super(title, null, null, 0, 0, 100, false);
			if (source == null)
				throw new NullPointerException("The source image is null.");
			if (dest == null)
				throw new NullPointerException("The destination file is null.");
			this._imgSource = source;
			this._vfDest    = dest;
		}

		@Override
		public void processTask() throws Throwable {
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("PNG");
			if (writers.hasNext()) {
				ImageWriter writer = writers.next();
				writer.addIIOWriteProgressListener(this);

				OutputStream oStream = null;
				ImageOutputStream imgStream = null;
				try {
					oStream = _vfDest.getOutputStream();
					imgStream = ImageIO.createImageOutputStream(oStream);
					writer.setOutput(imgStream);
					writer.write(_imgSource);
					writer.dispose();
					imgStream.flush();
				}
				finally {
					if (imgStream != null) {
						try {
							imgStream.close();
						} catch (IOException ignoreEx) {}
					}
					if (oStream != null) {
						try {
							oStream.close();
						} catch (IOException ignoreEx) {}
					}
				}
			}
			else {
				// not supported
				throw new IOException("'PNG' format is not supported!");
			}
			setValue(100);
		}

		@Override
		public void imageComplete(ImageWriter source) {
			setValue(100);
		}

		@Override
		public void imageProgress(ImageWriter source, float percentageDone) {
			setValue((int)percentageDone);
		}

		@Override
		public void imageStarted(ImageWriter source, int imageIndex) {
		}

		@Override
		public void thumbnailComplete(ImageWriter source) {
		}

		@Override
		public void thumbnailProgress(ImageWriter source, float percentageDone) {
		}

		@Override
		public void thumbnailStarted(ImageWriter source, int imageIndex, int thumbnailIndex) {
		}

		@Override
		public void writeAborted(ImageWriter source) {
		}
	}
}
