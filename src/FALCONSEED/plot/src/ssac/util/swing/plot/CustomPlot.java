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
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)CustomPlot.java	2.1.0	2013/07/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

import ptolemy.plot.EPSGraphics;
import ptolemy.plot.PlotPoint;
import ptolemy.util.RunnableExceptionCatcher;

/**
 * <code>ptolemy.plot.Plot</code> のカスタマイズ。
 * 
 * @version 2.1.0	2013/07/16
 * @since 2.1.0
 */
public class CustomPlot extends CustomPlotBox
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 4961107640649855692L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	protected PlotModel	_plotModel;

	/** 誤差範囲の端点に描画する線分の長さ(Pixel) **/
	private int		_errorBarLegLength	= PlotConstants.DEFAULT_ERRORBAR_LEG_LENGTH;

	//------------------------------------------------------------
	// Original protected fields of ptolemy.plot.Plot
	//------------------------------------------------------------

	/** The current dataset. */
	protected int _currentdataset = -1;

	/** An indicator of the marks style.  See _parseLine method for
	 * interpretation.
	 */
	protected volatile int _marks;

	//------------------------------------------------------------
	// Original private fields of ptolemy.plot.Plot
	//------------------------------------------------------------

//	/** The initial default width.
//	 */
//	private static final float _DEFAULT_WIDTH = 2f;
//
//	// Half of the length of the error bar horizontal leg length;
//	private static final int _ERRORBAR_LEG_LENGTH = 5;
//
//	/** @serial Is this the first datapoint in a set? */
//	private boolean _firstInSet = true;

	/** The width of the current stroke.  Only effective if the
	 *  Graphics is a Graphics2D.
	 *
	 */
	private float _width = PlotConstants.DEFAULT_WIDTH;

	/** @serial Give the radius of a point for efficiency. */
	private int _radius = PlotConstants.DEFAULT_MARK_RADIUS;

	/** @serial Give the diameter of a point for efficiency. */
	private int _diameter = PlotConstants.DEFAULT_MARK_DIAMETER;

	/** True when disconnections should be marked.*/
	private boolean _markDisconnections = false;

	/** @serial True if this is a bar plot. */
	private boolean _bars = false;

	/** @serial Offset per dataset in x axis units. */
	private volatile double _barOffset = 0.05;

	/** @serial Width of a bar in x axis units. */
	private volatile double _barWidth = 0.5;

	/** @serial True if the points are connected. */
	private boolean _connected = true;

	/** @serial Format information on a per data set basis. */
	private ArrayList<Format> _formats = new ArrayList<Format>();

	/** Cached copy of graphics, needed to reset when we are exporting
	 *  to EPS.
	 */
	private Graphics _graphics = null;

	/** @serial True if this is an impulse plot. */
	private boolean _impulses = false;

	/** Initial value for elements in _prevx and _prevy that indicate
	 *  we have not yet seen data.
	 */
	private static final Long _INITIAL_PREVIOUS_VALUE = Long.MIN_VALUE;

	// We keep track of the last dot that has been add to be able to
	// remove the dot again in case an extra point was added afterwards.
	private HashMap<Integer, PlotPoint> _lastPointWithExtraDot = new HashMap<Integer, PlotPoint>();

	// A stroke of width 1.
	private static final BasicStroke _LINE_STROKE1 = new BasicStroke(1f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

	// A stroke of width 2.
	private static final BasicStroke _LINE_STROKE2 = new BasicStroke(2f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

	/** True if different line styles should be used. */
	private boolean _lineStyles = false;

	/** True if different line styles should be used. */
	private static String[] _LINE_STYLES_ARRAY = { "solid", "dotted", "dashed",
		"dotdashed", "dotdotdashed" };

	/** @serial The highest data set used. */
	private int _maxDataset = -1;

	// Maximum number of different marks
	// NOTE: There are 11 colors in the base class.  Combined with 10
	// marks, that makes 110 unique signal identities.
	private static final int _MAX_MARKS = 10;

	// True when a bin has been changed and it need to be repainted
	// by the next scheduled repaint.
	private boolean _needBinRedraw = false;

	// True when a the plot need to be refilled
	// by the next scheduled repaint.
	private boolean _needPlotRefill = false;

	/** @serial Number of points to persist for. */
	private int _pointsPersistence = 0;

	/** @serial Information about the previously plotted point. */
	private ArrayList<Long> _prevxpos = new ArrayList<Long>();

	/** @serial Information about the previously plotted point. */
	private ArrayList<Long> _prevypos = new ArrayList<Long>();

	/** @serial Information about the previously erased point. */
	private ArrayList<Long> _prevErasedxpos = new ArrayList<Long>();

	/** @serial Information about the previously erased point. */
	private ArrayList<Long> _prevErasedypos = new ArrayList<Long>();

	/** @serial Persistence in units of the horizontal axis. */
	private double _xPersistence = 0.0;

	/** @serial Flag indicating validity of _xBottom, _xTop,
	 *  _yBottom, and _yTop.
	 */
	private boolean _xyInvalid = true;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public CustomPlot() {
		this((PlotModel)null);
		
	}

	public CustomPlot(PlotModel model) {
		super();
		setPlotModel(model);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public PlotModel getPlotModel() {
		return _plotModel;
	}

	public void setPlotModel(PlotModel newModel) {
		if (newModel == null) {
			_plotModel = createDefaultPlotModel();
		} else {
			_plotModel = newModel;
		}
		_plotModel.setSettingsModifiedFlag(true);
		_plotModel.resetDataConstraints();
		clear(true);
	}

	@Override
	public synchronized void paintComponent(Graphics graphics) {
		// update chart informations
		
		// call superclass
		super.paintComponent(graphics);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected PlotModel createDefaultPlotModel() {
		return new PlotModel(EmptyDataTable.instance);
	}

	//------------------------------------------------------------
	// Local value accessor for Plot interfaces
	//------------------------------------------------------------

    @Override
	protected void setupProgressMaxValue() {
    	// 最大ステップ更新
		super.setupProgressMaxValue();

		BigInteger maxStep = BigInteger.ZERO; 
		DrawProgressTask task = _drawingTask;
		if (task != null) {
			maxStep = task.getMaxStep();
		}
		
		// データ系列から未更新のフィールドを収集
		long numRecords = _plotModel.getDataTable().getDataRecordCount();
		int numSeries = _plotModel.getSeriesCount();
		PlotDataField field;
		TreeMap<Integer, PlotDataField> tmap = new TreeMap<Integer, PlotDataField>();
		for (int i = 0; i < numSeries; ++i) {
			PlotDataSeries series = _plotModel.getSeries(i);
			
			// X-Field
			field = series.getXField();
			if (field != null && !field.isDecimalRangeUpdated()) {
				tmap.put(field.getFieldIndex(), field);
			}
			// Y-Field
			field = series.getYField();
			if (field != null && !field.isDecimalRangeUpdated()) {
				tmap.put(field.getFieldIndex(), field);
			}
		}
		if (!tmap.isEmpty()) {
			maxStep = maxStep.add(BigInteger.valueOf(numRecords));
		}
		
		// 描画時のデータ走査回数を計算(maxStep += (dataRecordCount * seriesCount))
		maxStep = maxStep.add(BigInteger.valueOf(numRecords).multiply(BigInteger.valueOf(numSeries)));
		if (task != null) {
			task.setMaxStep(maxStep);
		}
		
		if (terminateByCanceled()) {
			return;		// canceled
		}
		
		// データ系列の数値範囲更新
		if (!tmap.isEmpty()) {
			_plotModel.clearDataConstraints();
			PlotDataField[] refreshFields = tmap.values().toArray(new PlotDataField[tmap.size()]);
			for (long rindex = 0L; rindex < numRecords; ++rindex) {
				for (PlotDataField f : refreshFields) {
					f.refreshDecimalRange(rindex);
				}
				incrementProgressStep();
				if (terminateByCanceled()) {
					return;	// canceled
				}
			}
			//--- 更新完了フラグ設定
			for (PlotDataField f : refreshFields) {
				f.decimalRangeUpdateFinished();
			}
		}
		
		// 全体の統計情報を更新する
		_plotModel.updateEntireConstraints();
		
		// チャート描画設定を更新
    	updateChartSettings();
	}
	
	protected void updateChartSettings() {
		if (_plotImage != null && !_plotModel.isModifiedSettings()) {
			// 変更なし
			return;
		}

		// 状態取得
		int numSeries = _plotModel.getSeriesCount();
		PlotStyles curStyles = _plotModel.getStyles();
		
		// グラフ設定のクリア
		clear(true);
		//--- clear local status
		_maxDataset = numSeries;
		_formats.clear();
		_prevxpos.clear();
		_prevypos.clear();
		_prevErasedxpos.clear();
		_prevErasedypos.clear();
		_lastPointWithExtraDot.clear();
		
		// タイトル、ラベル
		setTitle(_plotModel.getTitle());
		setXLabel(_plotModel.getXLabel());
		setYLabel(_plotModel.getYLabel());
		setXTickField(_plotModel.getXTicksField());
		setYTickField(_plotModel.getYTicksField());
		
		// update internal status
		_currentdataset = -1;
		_marks = curStyles.getMarkStyle().id();
		_width = curStyles.getStrokeWidth();
		_radius = curStyles.getRadius();
		_diameter = curStyles.getDiameter();
		_markDisconnections = curStyles.isDrawMarkDisconnection();
		_bars = curStyles.isDrawBars();
		_barOffset = curStyles.getBarOffset();
		_barWidth  = curStyles.getBarWidth();
		_connected = curStyles.isLineConnected();
		_impulses = curStyles.isDrawImpulses();
		_lineStyles = curStyles.isLineDifferenceEachSeries();
		
		// データ系列
		for (int dataset = 0; dataset < numSeries; ++dataset) {
			PlotDataSeries series = _plotModel.getSeries(dataset);
			
			// Legend
			addLegend(dataset, series.getAvailableLegend());
			
			// format for data series
			_formats.add(new Format());
			
			// initialize temporary values
			_prevxpos.add(_INITIAL_PREVIOUS_VALUE);
			_prevypos.add(_INITIAL_PREVIOUS_VALUE);
			_prevErasedxpos.add(_INITIAL_PREVIOUS_VALUE);
			_prevErasedypos.add(_INITIAL_PREVIOUS_VALUE);
			_lastPointWithExtraDot.put(dataset, null);
		}
		
		// reset modified flags
		_plotModel.setSettingsModifiedFlag(false);
		curStyles.setModifiedFlag(false);
		
		// refresh constraints
		double dXMin = _plotModel.convertXDecimalToDouble(_plotModel.getXEntireMinimumDecimalValue());
		double dXMax = _plotModel.convertXDecimalToDouble(_plotModel.getXEntireMaximumDecimalValue());
		double dYMin = _plotModel.convertYDecimalToDouble(_plotModel.getYEntireMinimumDecimalValue());
		double dYMax = _plotModel.convertYDecimalToDouble(_plotModel.getYEntireMaximumDecimalValue());
		refreshLocalConstraints(dXMin, dYMin, 0, 0, false);	// no error bar
		refreshLocalConstraints(dXMax, dYMax, 0, 0, false);	// no error bar
		if (!Double.isNaN(dXMin) && !Double.isNaN(dXMax)) {
			setXRange(dXMin, dXMax);
		}
		if (!Double.isNaN(dYMin) && !Double.isNaN(dYMax)) {
			setYRange(dYMin, dYMax);
		}
	}

	// Return true if the specified dataset is connected by default.
    private boolean _isConnected(int dataset) {
    	// no check index
        Format fmt = _formats.get(dataset);
        if (fmt.connectedUseDefault) {
            return _connected;
        } else {
            return fmt.connected;
        }
    }

    protected double logXValue(double x) {
    	if (_xlog) {
    		if (x <= 0.0) {
    			System.err.println("Can't plot non-positive X values "
    					+ "when the logarithmic X axis value is specified: "
    					+ x);
    			return Double.NaN;
    		}

    		//x = Math.log(x) * _LOG10SCALE;
    		x = Math.log10(x);
    	}

    	return x;
    }
    
    protected double wrapXValue(double x) {
    	if (_wrap) {
    		double width = _wrapHigh - _wrapLow;

    		if (x < _wrapLow) {
    			x += (width * Math.floor(1.0 + ((_wrapLow - x) / width)));
    		} else if (x > _wrapHigh) {
    			x -= (width * Math.floor(1.0 + ((x - _wrapHigh) / width)));

    			// NOTE: Could quantization errors be a problem here?
    			if (Math.abs(x - _wrapLow) < 0.00001) {
    				x = _wrapHigh;
    			}
    		}
    	}
    	
    	return x;
    }

    protected double logYValue(double y) {
    	if (_ylog) {
    		if (y <= 0.0) {
    			System.err.println("Can't plot non-positive Y values "
    					+ "when the logarithmic Y axis value is specified: "
    					+ y);
    			return Double.NaN;
    		}

    		y = Math.log(y) * _LOG10SCALE;
    	}

    	return y;
    }

    protected double logYLowErrorBar(double yLowEB) {
    	if (_ylog) {
    		if (yLowEB <= 0.0) {
    			System.err
    			.println("Can't plot non-positive Y(Low error bar) values "
    					+ "when the logarithmic Y axis value is specified: "
    					+ yLowEB);
    			return Double.NaN;
    		}

    		//yLowEB = Math.log(yLowEB) * _LOG10SCALE;
    		yLowEB = Math.log10(yLowEB);
    	}

    	return yLowEB;
    }

    protected double logYHighErrorBar(double yHighEB) {
    	if (_ylog) {
    		if (yHighEB <= 0.0) {
    			System.err
    			.println("Can't plot non-positive Y(High error bar) values "
    					+ "when the logarithmic Y axis value is specified: "
    					+ yHighEB);
    			return Double.NaN;
    		}

    		//yHighEB = Math.log(yHighEB) * _LOG10SCALE;
    		yHighEB = Math.log10(yHighEB);
    	}

    	return yHighEB;
    }
    
    protected void refreshLocalConstraints(double x, double y, double yLowEB, double yHighEB, boolean errorBar)
    {
    	if (!Double.isNaN(x)) {
    		x = logXValue(x);
    		if (!Double.isNaN(x)) {
    			x = wrapXValue(x);
        		
        		if (x < _xBottom) {
        			if (_automaticRescale() && _xTop != -Double.MAX_VALUE
        					&& _xBottom != Double.MAX_VALUE) {
//        				needPlotRefill = true;
        				_xBottom = x - (_xTop - _xBottom);
        			} else {
        				_xBottom = x;
        			}
        		}

        		if (x > _xTop) {
        			if (_automaticRescale() && _xTop != -Double.MAX_VALUE
        					&& _xBottom != Double.MAX_VALUE) {
//        				needPlotRefill = true;
        				_xTop = x + _xTop - _xBottom;
        			} else {
        				_xTop = x;
        			}
        		}
    		}
    	}

    	if (!Double.isNaN(y)) {
    		y = logYValue(y);
    		if (!Double.isNaN(y)) {
        		if (y < _yBottom) {
        			if (_automaticRescale() && _yTop != -Double.MAX_VALUE
        					&& _yBottom != Double.MAX_VALUE) {
//        				needPlotRefill = true;
        				_yBottom = y - (_yTop - _yBottom);
        			} else {
        				_yBottom = y;
        			}
        		}

        		if (y > _yTop) {
        			if (_automaticRescale() && _yTop != -Double.MAX_VALUE
        					&& _yBottom != Double.MAX_VALUE) {
//        				needPlotRefill = true;
        				_yTop = y + _yTop - _yBottom;
        			} else {
        				_yTop = y;
        			}
        		}
        		
        		if (errorBar) {
        			if (!Double.isNaN(yLowEB)) {
        				yLowEB = logYLowErrorBar(yLowEB);
        				if (!Double.isNaN(yLowEB)) {
                    		if (yLowEB < _yBottom) {
                    			_yBottom = yLowEB;
                    		}

                    		if (yLowEB > _yTop) {
                    			_yTop = yLowEB;
                    		}
        				}
        			}
        			
        			if (!Double.isNaN(yHighEB)) {
        				yHighEB = logYHighErrorBar(yHighEB);
        				if (!Double.isNaN(yHighEB)) {
                    		if (yHighEB < _yBottom) {
                    			_yBottom = yHighEB;
                    		}

                    		if (yHighEB > _yTop) {
                    			_yTop = yHighEB;
                    		}
        				}
        			}
        		}
    		}
    	}
    }
    
    private PlotPoint createPlotPoint(int dataset, double x, double y, double yLowEB, double yHighEB, boolean connected, boolean errorBar)
    {
    	double originalx = x;
    	
    	if (!Double.isNaN(x)) {
    		x = logXValue(x);
    		if (!Double.isNaN(x)) {
    			originalx = x;
    			x = wrapXValue(x);
    		}
    	}
    	
    	if (!Double.isNaN(y)) {
    		y = logYValue(y);
    		if (!Double.isNaN(y)) {
    			if (errorBar && !Double.isNaN(yLowEB) && !Double.isNaN(yHighEB)) {
    				yLowEB = logYLowErrorBar(yLowEB);
    				yHighEB = logYHighErrorBar(yHighEB);
    				if (Double.isNaN(yLowEB) || Double.isNaN(yHighEB)) {
    					errorBar = false;
    				}
    			} else {
    				errorBar = false;
    			}
    		} else {
    			errorBar = false;
    		}
    	}
    	
    	PlotPoint pt = new PlotPoint();
    	pt.originalx = originalx;
    	pt.x = x;
    	pt.y = y;
    	pt.connected = connected && _isConnected(dataset);
    	if (errorBar) {
    		pt.yLowEB  = yLowEB;
    		pt.yHighEB = yHighEB;
    		pt.errorBar = true;
    	}
    	
//    	if (_wrap) {
//    		// Do not connect points if wrapping...
//    		PlotPoint old = points.get(size - 1);
//
//    		if (old.x > x) {
//    			pt.connected = false;
//    		}
//    	}
    	
    	return pt;
    }

	protected Bin appendPointToBin(int dataset, Bin curBin, PlotPoint point, long dataRecordIndex) {
    	long xpos = _ulx + (long) ((point.x - _xMin) * _xscale);
    	long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
    	int nbrOfBins = 0;	// データセット内の Bin の数

    	Bin newBin = null;
    	if (curBin != null && curBin.xpos == xpos) {
    		// same x position
    		curBin.addPoint(point, dataRecordIndex, ypos);
    	}
    	else {
    		// create new bin
    		newBin = new Bin(xpos, dataset);
    		newBin.addPoint(point, dataRecordIndex, ypos);
    	}
    	return newBin;
    }

	//------------------------------------------------------------
	// Override methods of ptolemy.plot.PlotBox
	//------------------------------------------------------------

	@Override
	protected synchronized void _drawPlot(Graphics graphics, boolean clearfirst, Rectangle drawRectangle)
	{
		// BRDebug System.err.println("_drawPlot(begin)");
		if (_graphics == null) {
			_graphics = graphics;
		} else if (graphics != _graphics) {
			_graphics = graphics;
		}

		// We must call PlotBox._drawPlot() before calling _drawPlotPoint
		// so that _xscale and _yscale are set.
		super._drawPlot(graphics, clearfirst, drawRectangle);

		// Divide the points into different Bins. This should be done each time
		// _xscale and _yscale are set
		//_dividePointsIntoBins();

		boolean connectInvalidValue = _plotModel.getStyles().isInvalidValueConnection();
		PlotPoint point = null;
		Bin prevBin=null, nextBin=null, curBin=null;
		long numRecords = _plotModel.getDataTable().getDataRecordCount();
		int numSeries = _plotModel.getSeriesCount();
		for (int dataset = numSeries - 1; dataset >= 0; dataset--) {
			PlotDataSeries series = _plotModel.getSeries(dataset);
			boolean connected = false;
			
			for (long rindex = 0L; rindex < numRecords; ++rindex) {
				// 描画点の取得
				double dx = _plotModel.convertXDecimalToDouble(series.getXDecimalValue(rindex));
				double dy = _plotModel.convertYDecimalToDouble(series.getYDecimalValue(rindex));
				incrementProgressStep();
				if (Double.isNaN(dx) || Double.isInfinite(dx) || Double.isNaN(dy) || Double.isInfinite(dy)) {
					if (!connectInvalidValue) {
						connected = false;	// 無効値で線を分断
					}
					continue;	// 無効値の含まれる系列はスキップ
				}
				point = createPlotPoint(dataset, dx, dy, 0, 0, connected, false);
				nextBin = appendPointToBin(dataset, curBin, point, rindex);
				if (nextBin != null) {
					if (curBin != null) {
						_drawBin(graphics, dataset, prevBin, curBin);
					}
					if (prevBin != null) {
						prevBin.clearAllPoints();
					}
					prevBin = curBin;
					curBin = nextBin;
					nextBin = null;
				}
				connected = _connected;
			}
			if (curBin != null) {
				_drawBin(graphics, dataset, prevBin, curBin);
				
				if (_markDisconnections && _marks == 0) {
					Bin bin = curBin;
					boolean connectedFlag = _connected;
					PlotPoint lastPoint = point;
					if (connectedFlag && lastPoint.connected) {
						// In case the point is not connected there is already a dot.
						_setColorForDrawing(graphics, dataset, false);
						long xpos = bin.xpos;
						long ypos = _lry - (long) ((lastPoint.y - _yMin) * _yscale);
						// BRDebug System.out.println("_drawPlot");
						_drawPoint(graphics, dataset, xpos, ypos, true, 2 /*dots*/);
						_resetColorForDrawing(graphics, false);

						// We keep track of the last dot that has been add to be able to
						// remove the dot again in case an extra point was added afterwards.
						_lastPointWithExtraDot.put(dataset, lastPoint);
					}
				}
				
				if (prevBin != null)
					prevBin.clearAllPoints();
				curBin.clearAllPoints();
			}
			prevBin = null;
			curBin  = null;
			nextBin = null;
		}
	}
	
	private void _drawBin(Graphics graphics, int dataset, Bin prevBin, Bin curBin) {
		_setColorForDrawing(graphics, dataset, false);

		if (_lineStyles) {
			int lineStyle = dataset % _LINE_STYLES_ARRAY.length;
			_setLineStyle(_LINE_STYLES_ARRAY[lineStyle], dataset);
		}

		//ArrayList<Bin> bins = _bins.get(dataset);
		//Bin bin = bins.get(binIndex);
		long xpos = curBin.xpos;

		if (!curBin.needReplot()) {
			return;
		}

		boolean connectedFlag = _connected;
		
		PlotPointNode firstNode = curBin.getFirstPoint();
		PlotPointNode lastNode  = curBin.getLastPoint();

//		long startPosition = curBin.nextPointToPlot();
//		long endPosition = curBin.afterLastPointIndex();

		//ArrayList<PlotPoint> points = _points.get(dataset);

		// Check to see whether the dataset has a marks directive
		int marks = _marks;

		// Draw decorations that may be specified on a per-dataset basis
		Format fmt = _formats.get(dataset);

		if (!fmt.marksUseDefault) {
			marks = fmt.marks;
		}

//		if (_markDisconnections && marks == 0 && endPosition > startPosition && startPosition > 0) {
		if (_markDisconnections && marks == 0 && prevBin != null && !prevBin.isPointsEmpty()) {
//			PlotPoint previousPoint = points.get(startPosition - 1);
			PlotPointNode previousPoint = prevBin.getLastPoint();
//			if (!(connectedFlag && points.get(startPosition).connected)) {
			if (!(connectedFlag && firstNode.point.connected)) {
//				if (connectedFlag && previousPoint.connected) {
				if (connectedFlag && previousPoint.point.connected) {
//					if (_lastPointWithExtraDot.get(dataset) != previousPoint) {
					if (_lastPointWithExtraDot.get(dataset) != previousPoint.point) {
						long prevypos = _prevypos.get(dataset);
						long prevxpos = _prevxpos.get(dataset);
						// BRDebug System.out.println("Plotting point:" + prevxpos + ", " + prevypos +  ", position :" + (startPosition-1) + ", previous");
						_drawPoint(graphics, dataset, prevxpos, prevypos, true,
								2 /*dots*/);
					} else {
						// BRDebug System.out.println("Skipping point");

						// We already painted this dot in the _drawplot code. No need
						// to draw the same point again here.
						// Now reset the flag:
						_lastPointWithExtraDot.put(dataset, null);
					}
				}
			} else {
//				if (_lastPointWithExtraDot.get(dataset) == previousPoint) {
				if (_lastPointWithExtraDot.get(dataset) == previousPoint.point) {
					long prevypos = _prevypos.get(dataset);
					long prevxpos = _prevxpos.get(dataset);
					// BRDebug System.err.println("Erasing point:" + prevxpos + ", " + prevypos +  ", position :" + (startPosition-1) + ", previous");

					// We keep track of the last dot that has been add to be able to
					// remove the dot again in case an extra point was added afterwards.
					// The erasing happens by drawing the point again with the same color (EXOR)

					_setColorForDrawing(graphics, dataset, true);
					_drawPoint(graphics, dataset, prevxpos, prevypos, true, 2 /*dots*/);
					_resetColorForDrawing(graphics, true);
					_setColorForDrawing(graphics, dataset, false);
				}
			}
		}

//		if (connectedFlag && bin.needConnectionWithPreviousBin()) {
		if (connectedFlag && curBin.needConnectionWithPreviousBin() && prevBin != null && !prevBin.isPointsEmpty()) {
//			Bin previousBin = bins.get(binIndex - 1);
//			_drawLine(graphics, dataset, xpos, bin.firstYPos(),
//					previousBin.xpos, previousBin.lastYPos(), true,
//					_DEFAULT_WIDTH);
			_drawLine(graphics, dataset, xpos, curBin.firstYPos(),
					prevBin.xpos, prevBin.lastYPos(), true,
					PlotConstants.DEFAULT_WIDTH);
		}

//		if (connectedFlag && bin.isConnected() && bin.rangeChanged() && bin.minYPos() != bin.maxYPos()) {
//			_drawLine(graphics, dataset, xpos, bin.minYPos(), xpos, bin
//					.maxYPos(), true, _DEFAULT_WIDTH);
//		}
		if (connectedFlag && curBin.isConnected() && curBin.rangeChanged() && curBin.minYPos() != curBin.maxYPos()) {
			_drawLine(graphics, dataset, xpos, curBin.minYPos(), xpos, curBin.maxYPos(), true, PlotConstants.DEFAULT_WIDTH);
		}

		if ((fmt.impulsesUseDefault && _impulses)
				|| (!fmt.impulsesUseDefault && fmt.impulses)) {
			long prevypos = _prevypos.get(dataset);
			long prevxpos = _prevxpos.get(dataset);

//			for (int i = startPosition; i < endPosition; ++i) {
//				PlotPoint point = points.get(i);
//				long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
//				if (prevypos != ypos || prevxpos != xpos) {
//					_drawImpulse(graphics, xpos, ypos, true);
//					prevypos = ypos;
//					prevxpos = xpos;
//				}
//			}
			for (PlotPointNode node = firstNode; node != null; node = node.next()) {
				long ypos = node.ypos;
				if (prevypos != ypos || prevxpos != xpos) {
					_drawImpulse(graphics, xpos, ypos, true);
					prevypos = ypos;
					prevxpos = xpos;
				}
			}
		}

		{
			long prevypos = _prevypos.get(dataset);
			long prevxpos = _prevxpos.get(dataset);

//			for (int i = startPosition; i < endPosition; ++i) {
//				PlotPoint point = points.get(i);
//
//				// I a point is not connected, we mark it with a dot.
//				if (_marks != 0
//				|| (_markDisconnections && !(connectedFlag && point.connected))) {
//					long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
//					if (prevypos != ypos || prevxpos != xpos) {
//						int updatedMarks = marks;
//						if (!(connectedFlag && point.connected) && marks == 0) {
//							updatedMarks = 2; // marking style: dots
//						}
//						// BRDebug System.out.println("Plotting point:" + xpos + ", " + ypos +  ", position :" + (i) + ", current");
//						_drawPoint(graphics, dataset, xpos, ypos, true,
//								updatedMarks);
//						prevypos = ypos;
//						prevxpos = xpos;
//					}
//				}
//
//			}
			for (PlotPointNode node = firstNode; node != null; node = node.next()) {
				// I a point is not connected, we mark it with a dot.
				if (_marks != 0 || (_markDisconnections && !(connectedFlag && node.point.connected))) {
					long ypos = node.ypos;
					if (prevypos != ypos || prevxpos != xpos) {
						int updatedMarks = marks;
						if (!(connectedFlag && node.point.connected) && marks == 0) {
							updatedMarks = 2; // marking style: dots
						}
						// BRDebug System.out.println("Plotting point:" + xpos + ", " + ypos +  ", position :" + (i) + ", current");
						_drawPoint(graphics, dataset, xpos, ypos, true, updatedMarks);
						prevypos = ypos;
						prevxpos = xpos;
					}
				}
			}
		}
		if (_bars) {
			long prevypos = _prevypos.get(dataset);
			long prevxpos = _prevxpos.get(dataset);
//			for (int i = startPosition; i < endPosition; ++i) {
//				PlotPoint point = points.get(i);
//				long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
//				if (prevypos != ypos || prevxpos != xpos) {
//					_drawBar(graphics, dataset, xpos, ypos, true);
//					prevypos = ypos;
//					prevxpos = xpos;
//				}
//			}
			for (PlotPointNode node = firstNode; node != null; node = node.next()) {
				long ypos = node.ypos;
				if (prevypos != ypos || prevxpos != xpos) {
					_drawBar(graphics, dataset, xpos, ypos, true);
					prevypos = ypos;
					prevxpos = xpos;
				}
			}
		}
		
//		if (bin.errorBar()) {
		if (curBin.errorBar()) {
			long prevypos = _prevypos.get(dataset);
			long prevxpos = _prevxpos.get(dataset);
//			for (int i = startPosition; i < endPosition; ++i) {
//				PlotPoint point = points.get(i);
//				if (point.errorBar) {
//					long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
//					if (prevypos != ypos || prevxpos != xpos) {
//						_drawErrorBar(
//								graphics,
//								dataset,
//								xpos,
//								_lry
//								- (long) ((point.yLowEB - _yMin) * _yscale),
//								_lry
//								- (long) ((point.yHighEB - _yMin) * _yscale),
//								true);
//						prevypos = ypos;
//						prevxpos = xpos;
//
//					}
//				}
//			}
			for (PlotPointNode node = firstNode; node != null; node = node.next()) {
				if (node.point.errorBar) {
					long ypos = node.ypos;
					if (prevypos != ypos || prevxpos != xpos) {
						_drawErrorBar(
								graphics,
								dataset,
								xpos,
								_lry
								- (long) ((node.point.yLowEB - _yMin) * _yscale),
								_lry
								- (long) ((node.point.yHighEB - _yMin) * _yscale),
								true);
						prevypos = ypos;
						prevxpos = xpos;
					}
				}
			}
		}

		_prevxpos.set(dataset, xpos);
//		_prevypos.set(dataset, bin.lastYPos());
		_prevypos.set(dataset, curBin.lastYPos());

		curBin.resetDisplayStateAfterPlot();

		_resetColorForDrawing(graphics, false);
    }

    //------------------------------------------------------------
    // Internal methods of ptolemy.plot.Plot
    //------------------------------------------------------------
    
    protected void _setLineStyle(String styleString, int dataset) {
    	float[] dashvalues;
 
    	Format format = _formats.get(dataset);
    	if (styleString.equalsIgnoreCase("solid")) {
    		format.lineStroke = new BasicStroke(_width, BasicStroke.CAP_BUTT,
    				BasicStroke.JOIN_BEVEL, 0);
    		///_graphics.setStroke(stroke);
    	} else if (styleString.equalsIgnoreCase("dotted")) {
    		dashvalues = new float[2];
    		dashvalues[0] = (float) 2.0;
    		dashvalues[1] = (float) 2.0;
    		format.lineStroke = new BasicStroke(_width, BasicStroke.CAP_BUTT,
    				BasicStroke.JOIN_BEVEL, 0, dashvalues, 0);
    	} else if (styleString.equalsIgnoreCase("dashed")) {
    		dashvalues = new float[2];
    		dashvalues[0] = (float) 8.0;
    		dashvalues[1] = (float) 4.0;
    		format.lineStroke = new BasicStroke(_width, BasicStroke.CAP_ROUND,
    				BasicStroke.JOIN_BEVEL, 0, dashvalues, 0);
    	} else if (styleString.equalsIgnoreCase("dotdashed")) {
    		dashvalues = new float[4];
    		dashvalues[0] = (float) 2.0;
    		dashvalues[1] = (float) 2.0;
    		dashvalues[2] = (float) 8.0;
    		dashvalues[3] = (float) 2.0;
    		format.lineStroke = new BasicStroke(_width, BasicStroke.CAP_BUTT,
    				BasicStroke.JOIN_BEVEL, 0, dashvalues, 0);
    	} else if (styleString.equalsIgnoreCase("dotdotdashed")) {
    		dashvalues = new float[6];
    		dashvalues[0] = (float) 2.0;
    		dashvalues[1] = (float) 2.0;
    		dashvalues[2] = (float) 2.0;
    		dashvalues[3] = (float) 2.0;
    		dashvalues[4] = (float) 8.0;
    		dashvalues[5] = (float) 2.0;
    		format.lineStroke = new BasicStroke(_width, BasicStroke.CAP_BUTT,
    				BasicStroke.JOIN_BEVEL, 0, dashvalues, 0);
    	} else {
    		StringBuffer results = new StringBuffer();
    		for (String style : java.util.Arrays.asList(_LINE_STYLES_ARRAY)) {
    			if (results.length() > 0) {
    				results.append(", ");
    			}
    			results.append("\"" + style + "\"");
    		}
    		throw new IllegalArgumentException("Line style \"" + styleString
    				+ "\" is not found, style must be one of " + results);
    	}
    	format.lineStyle = styleString;
    	format.lineStyleUseDefault = false;
    }

	// Only pure java's graphics methods
	/** If the graphics argument is an instance of Graphics2D, then set
	 *  the current stroke to the specified width.  Otherwise, do nothing.
	 *  @param graphics The graphics object.
	 *  @param width The width.
	 */
	protected void _setWidth(Graphics graphics, float width)
	{
		_width = width;

		// For historical reasons, the API here only assumes Graphics
		// objects, not Graphics2D.
		if (graphics instanceof Graphics2D) {
			// We cache the two most common cases.
			if (width == 1f) {
				((Graphics2D) graphics).setStroke(_LINE_STROKE1);
			} else if (width == 2f) {
				((Graphics2D) graphics).setStroke(_LINE_STROKE2);
			} else {
				((Graphics2D) graphics).setStroke(new BasicStroke(width,
						BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
			}
		}
	}

	/** Reset the color for drawing. This typically needs to happen after having drawn
	 *  a bin or erasing one.
	 *  @param graphics The graphics context.
	 *  @param forceExorWithBackground Restore the paint made back from exor mode
	 */
	private void _resetColorForDrawing(Graphics graphics,
			boolean forceExorWithBackground) {
		// Restore the color, in case the box gets redrawn.
		graphics.setColor(_foreground);

		if ((_pointsPersistence > 0) || (_xPersistence > 0.0)
				|| forceExorWithBackground) {
			// Restore paint mode in case axes get redrawn.
			graphics.setPaintMode();
		}
	}

	/** Set the color for drawing. This typically needs to happen before drawing
	 *  a bin or erasing one.
	 *  @param graphics The graphics context.
	 *  @param dataset The index of the dataset.
	 *  @param forceExorWithBackground Force to go into exor mode.
	 */
	private void _setColorForDrawing(Graphics graphics, int dataset,
			boolean forceExorWithBackground) {
		if ((_pointsPersistence > 0) || (_xPersistence > 0.0)
				|| forceExorWithBackground) {
			// To allow erasing to work by just redrawing the points.
			if (_background == null) {
				// java.awt.Component.setBackground(color) says that
				// if the color "parameter is null then this component
				// will inherit the  background color of its parent."
				graphics.setXORMode(getBackground());
			} else {
				graphics.setXORMode(_background);
			}
		}

		// Set the color
		if (_usecolor) {
			int color = dataset % _colors.length;
			graphics.setColor(_colors[color]);
		} else {
			graphics.setColor(_foreground);
		}
	}

	// Only pure java's graphics methods
	/** Draw bar from the specified point to the y axis.
	 *  If the specified point is below the y axis or outside the
	 *  x range, do nothing.  If the <i>clip</i> argument is true,
	 *  then do not draw above the y range.
	 *  Note that paintComponent() should be called before
	 *  calling this method so that _xscale and _yscale are properly set.
	 *  This method should be called only from the event dispatch thread.
	 *  It is not synchronized, so its caller should be.
	 *  @param graphics The graphics context.
	 *  @param dataset The index of the dataset.
	 *  @param xpos The x position.
	 *  @param ypos The y position.
	 *  @param clip If true, then do not draw outside the range.
	 */
	protected void _drawBar(Graphics graphics, int dataset, long xpos, long ypos, boolean clip)
	{
		if (clip) {
			if (ypos < _uly) {
				ypos = _uly;
			}

			if (ypos > _lry) {
				ypos = _lry;
			}
		}

		if ((ypos <= _lry) && (xpos <= _lrx) && (xpos >= _ulx)) {
			// left x position of bar.
			int barlx = (int) (xpos - ((_barWidth * _xscale) / 2) + (dataset
					* _barOffset * _xscale));

			// right x position of bar
			int barrx = (int) (barlx + (_barWidth * _xscale));

			if (barlx < _ulx) {
				barlx = _ulx;
			}

			if (barrx > _lrx) {
				barrx = _lrx;
			}

			// Make sure that a bar is always at least one pixel wide.
			if (barlx >= barrx) {
				barrx = barlx + 1;
			}

			// The y position of the zero line.
			long zeroypos = _lry - (long) ((0 - _yMin) * _yscale);

			if (_lry < zeroypos) {
				zeroypos = _lry;
			}

			if (_uly > zeroypos) {
				zeroypos = _uly;
			}

			if ((_yMin >= 0) || (ypos <= zeroypos)) {
				graphics.fillRect(barlx, (int) ypos, barrx - barlx,
						(int) (zeroypos - ypos));
			} else {
				graphics.fillRect(barlx, (int) zeroypos, barrx - barlx,
						(int) (ypos - zeroypos));
			}
		}
	}

	/** Draw an error bar for the specified yLowEB and yHighEB values.
	 *  If the specified point is below the y axis or outside the
	 *  x range, do nothing.  If the <i>clip</i> argument is true,
	 *  then do not draw above the y range.
	 *  This method should be called only from the event dispatch thread.
	 *  It is not synchronized, so its caller should be.
	 *  @param graphics The graphics context.
	 *  @param dataset The index of the dataset.
	 *  @param xpos The x position.
	 *  @param yLowEBPos The lower y position of the error bar.
	 *  @param yHighEBPos The upper y position of the error bar.
	 *  @param clip If true, then do not draw above the range.
	 */
	protected void _drawErrorBar(Graphics graphics, int dataset, long xpos, long yLowEBPos, long yHighEBPos, boolean clip)
	{
//		_drawLine(graphics, dataset, xpos - _ERRORBAR_LEG_LENGTH, yHighEBPos, xpos + _ERRORBAR_LEG_LENGTH, yHighEBPos, clip);
		_drawLine(graphics, dataset, xpos - _errorBarLegLength, yHighEBPos, xpos + _errorBarLegLength, yHighEBPos, clip);
		_drawLine(graphics, dataset, xpos, yLowEBPos, xpos, yHighEBPos, clip);
//		_drawLine(graphics, dataset, xpos - _ERRORBAR_LEG_LENGTH, yLowEBPos, xpos + _ERRORBAR_LEG_LENGTH, yLowEBPos, clip);
		_drawLine(graphics, dataset, xpos - _errorBarLegLength, yLowEBPos, xpos + _errorBarLegLength, yLowEBPos, clip);
	}

	// Only pure java's graphics methods, (Actually, call _setWidth() private method)
	/** Draw a line from the specified point to the y axis.
	 *  If the specified point is below the y axis or outside the
	 *  x range, do nothing.  If the <i>clip</i> argument is true,
	 *  then do not draw above the y range.
	 *  This method should be called only from the event dispatch thread.
	 *  It is not synchronized, so its caller should be.
	 *  @param graphics The graphics context.
	 *  @param xpos The x position.
	 *  @param ypos The y position.
	 *  @param clip If true, then do not draw outside the range.
	 */
	protected void _drawImpulse(Graphics graphics, long xpos, long ypos, boolean clip)
	{
		if (clip) {
			if (ypos < _uly) {
				ypos = _uly;
			}

			if (ypos > _lry) {
				ypos = _lry;
			}
		}

		if ((ypos <= _lry) && (xpos <= _lrx) && (xpos >= _ulx)) {
			// The y position of the zero line.
			double zeroypos = _lry - (long) ((0 - _yMin) * _yscale);

			if (_lry < zeroypos) {
				zeroypos = _lry;
			}

			if (_uly > zeroypos) {
				zeroypos = _uly;
			}

			_setWidth(graphics, 1f);
			graphics.drawLine((int) xpos, (int) ypos, (int) xpos, (int) zeroypos);
		}
	}

	// Only pure java's graphics methods
	/** Draw a line from the specified starting point to the specified
	 *  ending point.  The current color is used.  If the <i>clip</i> argument
	 *  is true, then draw only that portion of the line that lies within the
	 *  plotting rectangle. This method draws a line one pixel wide.
	 *  This method should be called only from the event dispatch thread.
	 *  It is not synchronized, so its caller should be.
	 *  @param graphics The graphics context.
	 *  @param dataset The index of the dataset.
	 *  @param startx The starting x position.
	 *  @param starty The starting y position.
	 *  @param endx The ending x position.
	 *  @param endy The ending y position.
	 *  @param clip If true, then do not draw outside the range.
	 */
	protected void _drawLine(Graphics graphics, int dataset, long startx,
			long starty, long endx, long endy, boolean clip)
	{
		_drawLine(graphics, dataset, startx, starty, endx, endy, clip, 1f);
	}

	// Only pure java's graphics methods
	/** Draw a line from the specified starting point to the specified
	 *  ending point.  The current color is used.  If the <i>clip</i> argument
	 *  is true, then draw only that portion of the line that lies within the
	 *  plotting rectangle.  The width argument is ignored if the graphics
	 *  argument is not an instance of Graphics2D.
	 *  This method should be called only from the event dispatch thread.
	 *  It is not synchronized, so its caller should be.
	 *  @param graphics The graphics context.
	 *  @param dataset The index of the dataset.
	 *  @param startx The starting x position.
	 *  @param starty The starting y position.
	 *  @param endx The ending x position.
	 *  @param endy The ending y position.
	 *  @param clip If true, then do not draw outside the range.
	 *  @param width The thickness of the line.
	 */
	protected void _drawLine(Graphics graphics, int dataset, long startx,
			long starty, long endx, long endy, boolean clip, float width)
	{
		_setWidth(graphics, width);

		Format format = _formats.get(dataset);
		Stroke previousStroke = null;
		if (!format.lineStyleUseDefault && graphics instanceof Graphics2D) {
			previousStroke = ((Graphics2D) graphics).getStroke();
			// Draw a dashed or dotted line
			((Graphics2D) graphics).setStroke(format.lineStroke);
		}

		if (clip) {
			// Rule out impossible cases.
			if (!(((endx <= _ulx) && (startx <= _ulx))
					|| ((endx >= _lrx) && (startx >= _lrx))
					|| ((endy <= _uly) && (starty <= _uly)) || ((endy >= _lry) && (starty >= _lry)))) {
				// If the end point is out of x range, adjust
				// end point to boundary.
				// The integer arithmetic has to be done with longs so as
				// to not loose precision on extremely close zooms.
				if (startx != endx) {
					if (endx < _ulx) {
						endy = (int) (endy + (((starty - endy) * (_ulx - endx)) / (startx - endx)));
						endx = _ulx;
					} else if (endx > _lrx) {
						endy = (int) (endy + (((starty - endy) * (_lrx - endx)) / (startx - endx)));
						endx = _lrx;
					}
				}

				// If end point is out of y range, adjust to boundary.
				// Note that y increases downward
				if (starty != endy) {
					if (endy < _uly) {
						endx = (int) (endx + (((startx - endx) * (_uly - endy)) / (starty - endy)));
						endy = _uly;
					} else if (endy > _lry) {
						endx = (int) (endx + (((startx - endx) * (_lry - endy)) / (starty - endy)));
						endy = _lry;
					}
				}

				// Adjust current point to lie on the boundary.
				if (startx != endx) {
					if (startx < _ulx) {
						starty = (int) (starty + (((endy - starty) * (_ulx - startx)) / (endx - startx)));
						startx = _ulx;
					} else if (startx > _lrx) {
						starty = (int) (starty + (((endy - starty) * (_lrx - startx)) / (endx - startx)));
						startx = _lrx;
					}
				}

				if (starty != endy) {
					if (starty < _uly) {
						startx = (int) (startx + (((endx - startx) * (_uly - starty)) / (endy - starty)));
						starty = _uly;
					} else if (starty > _lry) {
						startx = (int) (startx + (((endx - startx) * (_lry - starty)) / (endy - starty)));
						starty = _lry;
					}
				}
			}

			// Are the new points in range?
			if ((endx >= _ulx) && (endx <= _lrx) && (endy >= _uly)
					&& (endy <= _lry) && (startx >= _ulx) && (startx <= _lrx)
					&& (starty >= _uly) && (starty <= _lry)) {
				graphics.drawLine((int) startx, (int) starty, (int) endx,
						(int) endy);
			}
		} else {
			// draw unconditionally.
			graphics.drawLine((int) startx, (int) starty, (int) endx,
					(int) endy);
		}
		if (previousStroke != null) {
			((Graphics2D) graphics).setStroke(previousStroke);
		}
	}

	/** Put a mark corresponding to the specified dataset at the
	 *  specified x and y position. The mark is drawn in the current
	 *  color. What kind of mark is drawn depends on the _marks
	 *  variable and the dataset argument. If the fourth argument is
	 *  true, then check the range and plot only points that
	 *  are in range.
	 *  This method should be called only from the event dispatch thread.
	 *  It is not synchronized, so its caller should be.
	 *  @param graphics The graphics context.
	 *  @param dataset The index of the dataset.
	 *  @param xpos The x position.
	 *  @param ypos The y position.
	 *  @param clip If true, then do not draw outside the range.
	 */
	protected void _drawPoint(Graphics graphics, int dataset, long xpos,
			long ypos, boolean clip) {
		// Check to see whether the dataset has a marks directive
		Format fmt = _formats.get(dataset);
		int marks = _marks;

		if (!fmt.marksUseDefault) {
			marks = fmt.marks;
		}
		_drawPoint(graphics, dataset, xpos, ypos, clip, marks);
	}

	/** Put a mark corresponding to the specified dataset at the
	 *  specified x and y position. The mark is drawn in the current
	 *  color. What kind of mark is drawn depends on the marks
	 *  argument and the dataset argument. If the fourth argument is
	 *  true, then check the range and plot only points that
	 *  are in range.
	 *  This method should be called only from the event dispatch thread.
	 *  It is not synchronized, so its caller should be.
	 *  @param graphics The graphics context.
	 *  @param dataset The index of the dataset.
	 *  @param xpos The x position.
	 *  @param ypos The y position.
	 *  @param clip If true, then do not draw outside the range.
	 *  @param marks The marks that have to be used for plotting the point.
	 */
	private void _drawPoint(Graphics graphics, int dataset, long xpos, long ypos, boolean clip, final int marks)
	{
		// BRDebug System.out.println("_drawPoint, " + xpos + ", " + ypos);

		// If the point is not out of range, draw it.
		boolean pointinside = (ypos <= _lry) && (ypos >= _uly)
				&& (xpos <= _lrx) && (xpos >= _ulx);

		if (!clip || pointinside) {
			int xposi = (int) xpos;
			int yposi = (int) ypos;

			// If the point is out of range, and being drawn, then it is
			// probably a legend point.  When printing in black and white,
			// we want to use a line rather than a point for the legend.
			// (So that line patterns are visible). The only exception is
			// when the marks style uses distinct marks, or if there is
			// no line being drawn.
			// NOTE: It is unfortunate to have to test the class of graphics,
			// but there is no easy way around this that I can think of.
			if (!pointinside && (marks != 3) && _isConnected(dataset)
					&& ((graphics instanceof EPSGraphics) || !_usecolor)) {
				// Use our line styles.
				_drawLine(graphics, dataset, xposi - 6, yposi, xposi + 6,
						yposi, false, _width);
			} else {
				// Color display.  Use normal legend.
				switch (marks) {
				case 0:

					// If no mark style is given, draw a filled rectangle.
					// This is used, for example, to draw the legend.
					graphics.fillRect(xposi - 6, yposi - 6, 6, 6);
					break;

				case 1:

					// points -- use 3-pixel ovals.
					graphics.fillOval(xposi - 1, yposi - 1, 3, 3);
					break;

				case 2:

					// dots
					graphics.fillOval(xposi - _radius, yposi - _radius,
							_diameter, _diameter);
					break;

				case 3:

					// various
					int[] xpoints;

					// various
					int[] ypoints;

					// Points are only distinguished up to _MAX_MARKS data sets.
					int mark = dataset % _MAX_MARKS;

					switch (mark) {
					case 0:

						// filled circle
						graphics.fillOval(xposi - _radius, yposi - _radius,
								_diameter, _diameter);
						break;

					case 1:

						// cross
						graphics.drawLine(xposi - _radius, yposi - _radius,
								xposi + _radius, yposi + _radius);
						graphics.drawLine(xposi + _radius, yposi - _radius,
								xposi - _radius, yposi + _radius);
						break;

					case 2:

						// square
						graphics.drawRect(xposi - _radius, yposi - _radius,
								_diameter, _diameter);
						break;

					case 3:

						// filled triangle
						xpoints = new int[4];
						ypoints = new int[4];
						xpoints[0] = xposi;
						ypoints[0] = yposi - _radius;
						xpoints[1] = xposi + _radius;
						ypoints[1] = yposi + _radius;
						xpoints[2] = xposi - _radius;
						ypoints[2] = yposi + _radius;
						xpoints[3] = xposi;
						ypoints[3] = yposi - _radius;
						graphics.fillPolygon(xpoints, ypoints, 4);
						break;

					case 4:

						// diamond
						xpoints = new int[5];
						ypoints = new int[5];
						xpoints[0] = xposi;
						ypoints[0] = yposi - _radius;
						xpoints[1] = xposi + _radius;
						ypoints[1] = yposi;
						xpoints[2] = xposi;
						ypoints[2] = yposi + _radius;
						xpoints[3] = xposi - _radius;
						ypoints[3] = yposi;
						xpoints[4] = xposi;
						ypoints[4] = yposi - _radius;
						graphics.drawPolygon(xpoints, ypoints, 5);
						break;

					case 5:

						// circle
						graphics.drawOval(xposi - _radius, yposi - _radius,
								_diameter, _diameter);
						break;

					case 6:

						// plus sign
						graphics.drawLine(xposi, yposi - _radius, xposi, yposi
								+ _radius);
						graphics.drawLine(xposi - _radius, yposi, xposi
								+ _radius, yposi);
						break;

					case 7:

						// filled square
						graphics.fillRect(xposi - _radius, yposi - _radius,
								_diameter, _diameter);
						break;

					case 8:

						// triangle
						xpoints = new int[4];
						ypoints = new int[4];
						xpoints[0] = xposi;
						ypoints[0] = yposi - _radius;
						xpoints[1] = xposi + _radius;
						ypoints[1] = yposi + _radius;
						xpoints[2] = xposi - _radius;
						ypoints[2] = yposi + _radius;
						xpoints[3] = xposi;
						ypoints[3] = yposi - _radius;
						graphics.drawPolygon(xpoints, ypoints, 4);
						break;

					case 9:

						// filled diamond
						xpoints = new int[5];
						ypoints = new int[5];
						xpoints[0] = xposi;
						ypoints[0] = yposi - _radius;
						xpoints[1] = xposi + _radius;
						ypoints[1] = yposi;
						xpoints[2] = xposi;
						ypoints[2] = yposi + _radius;
						xpoints[3] = xposi - _radius;
						ypoints[3] = yposi;
						xpoints[4] = xposi;
						ypoints[4] = yposi - _radius;
						graphics.fillPolygon(xpoints, ypoints, 5);
						break;
					}

					break;

				case 4:

					// bigdots
					//graphics.setColor(_marksColor);
					if (graphics instanceof Graphics2D) {
						Object obj = ((Graphics2D) graphics)
								.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
						((Graphics2D) graphics).setRenderingHint(
								RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);
						graphics.fillOval(xposi - 4, yposi - 4, 8, 8);
						((Graphics2D) graphics).setRenderingHint(
								RenderingHints.KEY_ANTIALIASING, obj);
					} else {
						graphics.fillOval(xposi - 4, yposi - 4, 8, 8);
					}
					break;

				case 5:

					// If the mark style is pixels, draw a filled rectangle.
					graphics.fillRect(xposi, yposi, 1, 1);
					break;

				default:
					// none
				}
			}
		}
	}

	//------------------------------------------------------------
	// Original ptolemy.plot.Plot class, Override ptolemy.plot.PlotBox interfaces
	//------------------------------------------------------------

	@Override
	public synchronized void zoom(double lowx, double lowy, double highx, double highy) {
		// super.zoom(lowx, lowy, highx, highy);
		//--- Zoom 機能は使用しない
	}

    @Override
	void _zoom(int x, int y) {
		// super._zoom(x, y);
		//--- Zoom 機能は使用しない
	}

	@Override
	void _zoomBox(int x, int y) {
		//super._zoomBox(x, y);
		//--- Zoom 機能は使用しない
	}

	@Override
	void _zoomStart(int x, int y) {
		//super._zoomStart(x, y);
		//--- Zoom 機能は使用しない
	}

	@Override
    public synchronized void fillPlot() {
		throw new UnsupportedOperationException("PlotPane#fillPlot() is not supported!");
    }

	@Deprecated
	@Override
    public void parseFile(String filespec, URL documentBase) {
    	throw new UnsupportedOperationException("PlotPane#parseFile(String, URL) is not supported!");
    }

	@Override
    public synchronized void read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("PlotPane#read(InputStream) is not supported!");
    }

	@Override
    public synchronized void samplePlot() {
        // This needs to be done in the event thread.
        Runnable sample = new RunnableExceptionCatcher(new Runnable() {
            public void run() {
                synchronized (CustomPlot.this) {
                	PlotModel model;
                	DefaultDataTable table = new DefaultDataTable();
                	table.setHeaderValue(0, 0, "xdata", true);
                	table.setHeaderValue(0, 1, "datetime", true);
                	table.setHeaderValue(0, 2, "data2", true);
                	table.setHeaderValue(0, 3, "data3", true);
                	table.setHeaderValue(0, 4, "data4", true);
                	table.setHeaderValue(0, 5, "data5", true);
                	table.setHeaderValue(1, 2, "A", true);
                	table.setHeaderValue(1, 3, "B", true);
                	table.setHeaderValue(1, 4, "C", true);
                	table.setHeaderValue(1, 5, "D", true);
                	table.setHeaderValue(1, 6, "E", true);
                	
                	Calendar cal = Calendar.getInstance();
                	PlotDateTimeFormats formats = new PlotDateTimeFormats();
                	
                	for (int i = 0; i <= 100; i++) {
                		double xvalue = i;
                		
                		cal.add(Calendar.DAY_OF_MONTH, 10);
                		
                		table.setValue(i, 0, xvalue, true);
                		table.setValue(i, 1, formats.format(cal), true);
                		table.setValue(i, 2, (5 * Math.cos((Math.PI * i) / 20)), true);
                		table.setValue(i, 3, (4.5 * Math.cos((Math.PI * i) / 25)), true);
                		table.setValue(i, 4, (4 * Math.cos((Math.PI * i) / 30)), true);
                		table.setValue(i, 5, (3.5 * Math.cos((Math.PI * i) / 35)), true);
                		table.setValue(i, 6, (3 * Math.cos((Math.PI * i) / 40)), true);
                		table.setValue(i, 7, (2.5 * Math.cos((Math.PI * i) / 45)), true);
                		table.setValue(i, 8, (2 * Math.cos((Math.PI * i) / 50)), true);
                		table.setValue(i, 9, (1.5 * Math.cos((Math.PI * i) / 55)), true);
                		table.setValue(i, 10, (1 * Math.cos((Math.PI * i) / 60)), true);
                		table.setValue(i, 11, (0.5 * Math.cos((Math.PI * i) / 65)), true);
                	}
                	
                	model = new PlotModel(table);
                	model.setTitle("Sample plot");
                	model.setXLabel("time");
                	model.setYLabel("value");
                	model.getStyles().setMarkStyle(PlotMarkStyles.NONE);
                	model.getStyles().setDrawImpulses(true);
                	model.getStyles().setLineStyle(PlotLineStyles.SOLID);

                	PlotDataField xfield = new PlotDateTimeField(table, 1, formats);
                	for (int i = 2; i < table.getFieldCount(); i++) {
                		PlotDataField yfield = new PlotDecimalField(table, i);
                		PlotDataSeries series = new PlotDataSeries(xfield, yfield);
                		model.addSeries(series);
                	}
                	
                	model.setXTicksField(xfield);
                	//model.setYTicksField(yfield);

                	setPlotModel(model);
                	updateChartSettings();
                } // synchronized

                repaint();
            } // run method
        }); // Runnable class

        deferIfNecessary(sample);
    }

    /** Write plot data information to the specified output stream in PlotML.
     *  @param output A buffered print writer.
     */
	@Override
    public synchronized void writeData(PrintWriter output) {
		throw new UnsupportedOperationException("PlotPane#writeData(PrintWriter) is not supported!");
    }

	@Override
    public synchronized void writeFormat(PrintWriter output) {
		throw new UnsupportedOperationException("PlotPane#writeFormat(PrintWriter) is not supported!");
    }

	@Override
    protected boolean _parseLine(String line) {
		throw new UnsupportedOperationException("PlotPane#_parseLine(String) is not supported!");
    }

	@Override
    protected void _resetScheduledTasks() {
		throw new UnsupportedOperationException("PlotPane#_resetScheduledTasks() is not supported!");
    }

	@Override
    protected void _scheduledRedraw() {
		throw new UnsupportedOperationException("PlotPane#_scheduledRedraw() is not supported!");
    }

    @Deprecated
	@Override
    protected void _writeOldSyntax(PrintWriter output) {
        super._writeOldSyntax(output);
        
        throw new UnsupportedOperationException("PlotPane#writeOldSyntax(PrintWriter) is not supported!");
    }

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

    private class Bin {
        public Bin(long xPos, int dataset) {
            _dataset = dataset;
            xpos = xPos;
        }

        public void addPoint(PlotPoint point, long pointIndex, long ypos) {
            long absolutePointIndex = pointIndex;
            //The absolute point index is a index in the list of
            //  all points that once existed in the plot

            if (_maxYPos < ypos) {
                _maxYPos = ypos;
                _rangeChanged = true;
            }
            if (_minYPos > ypos) {
                _minYPos = ypos;
                _rangeChanged = true;
            }

            if (_firstPointIndex == -1) {
                _needConnectionWithPreviousBin = point.connected;
                _firstYPos = ypos;
                _firstPointIndex = absolutePointIndex;
                _nextPointToPlot = _firstPointIndex;
            } else {
                _isConnected |= point.connected;
                // if one point is connected within the bin, all points will be (it is difficult to do this otherwise)

                assert _afterLastPointIndex == absolutePointIndex; //Bin intervals should be contiguous intervals
            }

            _afterLastPointIndex = absolutePointIndex + 1;
            _lastYPos = ypos;

            _errorBar |= point.errorBar;
            
            PlotPointNode node = new PlotPointNode(pointIndex, xpos, ypos, point);
            if (_lastPoint == null) {
            	_firstPoint = node;
            	_lastPoint  = node;
            } else {
            	node.prev = _lastPoint;
            	_lastPoint.next = node;
            	_lastPoint = node;
            }
        	++_numPoints;
        }

        /**
         * Return the position after the last point of the range of points within the bin
         * This index is the index within the current points of the plot.
         */
        public long afterLastPointIndex() {
            assert _firstPointIndex != -1;
            return _afterLastPointIndex;
        }

        /**
         *  Return true in case there is one point that needs an error bar, otherwise false
         */
        public boolean errorBar() {
            return _errorBar;
        }

        /**
         * Return the position of the first point of the range of points within the bin
         * This index is the index within the current points of the plot.
         */
        public long firstPointIndex() {
            assert _firstPointIndex != -1;
            return _firstPointIndex;
        }

        /**
         *  Return the y position of the first added point in the bin.
         */
        // Precondition: only valid in case there is at least one point
        public long firstYPos() {
            assert _firstPointIndex != -1;
            return _firstYPos;
        }

        /**
         *  Return the minimum y position of the bin.
         * Precondition: only valid in case there is at least one point
         */
        public long minYPos() {
            assert _firstPointIndex != -1;
            return _minYPos;
        }

        /**
         * Return the y position of the last added point in the bin.
         * Precondition: only valid in case there is at least one point
         */
        public long lastYPos() {
            assert _firstPointIndex != -1;
            return _lastYPos;
        }

        /**
         * Return the maximum y position of the bin.
         * Precondition: only valid in case there is at least one point
         */
        public long maxYPos() {
            assert _firstPointIndex != -1;
            return _maxYPos;
        }

        /**
         * Return whether a line should be drawn with the previous bin.
         * After you have reset the display state, the boolean return false
         */
        public boolean needConnectionWithPreviousBin() {
            return _needConnectionWithPreviousBin;
        }

        /**
         * Return true a line has been drawn to the previous bin.
         */
        public boolean isConnectedWithPreviousBin() {
            return _isConnectedWithPreviousBin;
        }

        public boolean isConnected() {
            return _isConnected;
        }

        /**
         * Return true when the bin should be plotted (again)
         */
        public boolean needReplot() {
            return _needConnectionWithPreviousBin || _rangeChanged
                    || _nextPointToPlot != _afterLastPointIndex;
        }

        /**
         * Return the position of the next point of the bin that should be plotted
         * This index is the index within the current points of the plot.
         */
        public long nextPointToPlot() {
            return _nextPointToPlot;
        }

        /**
         * Return true when the rangeChanged
         */
        public boolean rangeChanged() {
            return _rangeChanged;
        }

        /**
         * Reset the plot state for this bin when you have
         * plotted this bin/
         */
        public void resetDisplayStateAfterPlot() {
            if (_needConnectionWithPreviousBin) {
                _isConnectedWithPreviousBin = true;
                _needConnectionWithPreviousBin = false;
            }
            _rangeChanged = false;
            _nextPointToPlot = _afterLastPointIndex;
        }

//        /**
//         * Disconnect this bin with the previous bin.
//         */
//        public void setNotConnectedWithPreviousBin() {
//            _needConnectionWithPreviousBin = false;
//            _isConnectedWithPreviousBin = false;
//            _points.get(_dataset).get(
//                    _firstPointIndex - _pointInBinOffset.get(_dataset)).connected = false;
//        }

        public final long xpos;

        private long _afterLastPointIndex = 0;

        private int _dataset = 0;

        // _errorBar is true in case there is one point that needs an error bar, otherwise false
        private boolean _errorBar = false;

        private long _firstPointIndex = -1;

        private long _firstYPos = java.lang.Long.MIN_VALUE;

        private boolean _isConnected = false;
        private boolean _isConnectedWithPreviousBin = false;

        private long _lastYPos = java.lang.Long.MIN_VALUE;

        private long _maxYPos = java.lang.Long.MIN_VALUE;
        private long _minYPos = java.lang.Long.MAX_VALUE;

        // Indicate whether a line has to be added with previous bin or not
        // Once the line has been drawn, the boolean should be switched to false
        private boolean _needConnectionWithPreviousBin = false;

        private boolean _rangeChanged = false;
        private long _nextPointToPlot = 0;
        
        private long			_numPoints = 0L;
        private PlotPointNode	_firstPoint = null;
        private PlotPointNode	_lastPoint  = null;
        
        public boolean isPointsEmpty() {
        	return (_numPoints == 0L);
        }
        
        public long getNumPoints() {
        	return _numPoints;
        }
        
        public PlotPointNode getFirstPoint() {
        	return _firstPoint;
        }
        
        public PlotPointNode getLastPoint() {
        	return _lastPoint;
        }
        
        public void clearAllPoints() {
        	PlotPointNode nextNode = _firstPoint;
        	_firstPoint = null;
        	_lastPoint = null;
        	_numPoints = 0;
        	for (; nextNode != null; ) {
        		PlotPointNode curNode = nextNode;
        		nextNode = curNode.next;
        		curNode.next = null;
        		curNode.prev = null;
        	}
        }
    }
    
    private static class PlotPointNode {
    	public final long recordIndex;
    	public final long xpos;
    	public final long ypos;
    	public final PlotPoint point;
    	protected PlotPointNode prev;
    	protected PlotPointNode next;
    	
    	public PlotPointNode(long inRecordIndex, long x, long y, PlotPoint inPoint) {
    		recordIndex = inRecordIndex;
    		xpos = x;
    		ypos = y;
    		point = inPoint;
    	}
    	
    	public boolean isFirst() {
    		return (prev==null);
    	}
    	
    	public boolean isLast() {
    		return (next==null);
    	}
    	
    	public boolean hasPrev() {
    		return (prev != null);
    	}
    	
    	public boolean hasNext() {
    		return (next != null);
    	}
    	
    	public PlotPointNode next() {
    		return next;
    	}
    	
    	public PlotPointNode prev() {
    		return prev;
    	}
    }

	private static class Format implements Serializable {
		// FindBugs suggests making this class static so as to decrease
		// the size of instances and avoid dangling references.

		// Indicate whether the current dataset is connected.
		public boolean connected;

		// Indicate whether the above variable should be ignored.
		public boolean connectedUseDefault = true;

		// Indicate whether a stem plot should be drawn for this data set.
		// This is ignored unless the following variable is set to false.
		public boolean impulses;

		// Indicate whether the above variable should be ignored.
		public boolean impulsesUseDefault = true;

		// The stroke used for lines
		// This is ignored unless strokeUseDefault is true
		public BasicStroke lineStroke;

		// The name of the stroke, see Plot.setLineStyle()
		// This is ignored unless strokeUseDefault is true
		public String lineStyle;

		// Indicate whether lineStroke should be used
		public boolean lineStyleUseDefault = true;

		// Indicate what type of mark to use.
		// This is ignored unless the following variable is set to false.
		public int marks;

		// Indicate whether the above variable should be ignored.
		public boolean marksUseDefault = true;
	}
}
