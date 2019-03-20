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
 * @(#)PlotStyles.java	2.1.0	2013/07/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

/**
 * プロットの描画スタイル。
 * 
 * @version 2.1.0	2013/07/22
 * @since 2.1.0
 */
public class PlotStyles implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 設定内容が変更された場合は <tt>true</tt> **/
	protected boolean	_modified;

	/** 描画幅 **/
	private float	_strokeWidth	= PlotConstants.DEFAULT_WIDTH;

	/** 記号点の描画半径(Pixel) **/
	private int		_radius		= PlotConstants.DEFAULT_MARK_RADIUS;

	/** 記号点の描画直径(Pixel) **/
	private int		_diameter	= PlotConstants.DEFAULT_MARK_DIAMETER;

	/** 誤差範囲の端点に描画する線分の長さ(Pixel) **/
	private int		_errorBarLegLength	= PlotConstants.DEFAULT_ERRORBAR_LEG_LENGTH;

	/** 点描画スタイル **/
	private PlotMarkStyles	_markStyle = PlotMarkStyles.NONE;
	
	// for Line chart

	/** 線描画スタイル **/
	private PlotLineStyles	_lineStyle = PlotLineStyles.SOLID;

	/** データ値接続線の端点を描画する場合は <tt>true</tt>、描画しない場合は <tt>false</tt> **/
	private boolean	_markDisconnections;

	/** X 軸への垂線を描画する場合は <tt>true</tt>、描画しない場合は <tt>false</tt> **/
	private boolean	_impulses;
	
	/** 無効値も線でつなぐフラグ **/
	private boolean	_connectInvalidValues;
	
	// for Bar chart

	/** 棒グラフを描画する場合は <tt>true</tt> **/
	private boolean _bars = false;

	/** データ系列ごとの棒グラフの x 軸オフセット(x 軸の単位) **/
	private double	_barOffset = 0.05;

	/** 棒グラフの幅(x 軸の単位) **/
	private double	_barWidth = 0.5;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PlotStyles() {
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * この設定内容が変更されている場合は <tt>true</tt> を返す。
	 */
	public boolean isModified() {
		return _modified;
	}

	/**
	 * この設定内容の変更フラグを指定する。
	 * @param toModified	変更状態とする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public void setModifiedFlag(boolean toModified) {
		_modified = toModified;
	}

	/**
	 * Pixel 単位の描画幅を返す。
	 */
	public float getStrokeWidth() {
		return _strokeWidth;
	}

	/**
	 * 記号点の Pixel 単位の描画半径を返す。
	 */
	public int getRadius() {
		return _radius;
	}

	/**
	 * 記号点の Pixel 単位の描画直径を返す。
	 */
	public int getDiameter() {
		return _diameter;
	}

	/**
	 * 誤差範囲の端点に描画する線分の Pixel 単位の長さを返す。
	 */
	public int getErrorBarLegLength() {
		return _errorBarLegLength;
	}

	/**
	 * 点を描画する場合は <tt>true</tt>、描画しない場合は <tt>false</tt> を返す。
	 */
	public boolean isDrawPoints() {
		return (_markStyle != PlotMarkStyles.NONE);
	}

	/**
	 * 現在設定されている点描画スタイルを返す。
	 */
	public PlotMarkStyles getMarkStyle() {
		return _markStyle;
	}

	/**
	 * 指定された点描画スタイルを設定する。
	 * @param style	点描画スタイル
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setMarkStyle(PlotMarkStyles style) {
		if (style == null)
			style = PlotMarkStyles.NONE;
		if (style != _markStyle) {
			_modified = true;
			_markStyle = style;
			return true;
		}
		return false;
	}

	/**
	 * データ値を結ぶ直線を描画する場合は <tt>true</tt>、描画しない場合は <tt>false</tt> を返す。
	 */
	public boolean isLineConnected() {
		return (_lineStyle != PlotLineStyles.NONE);
	}

	/**
	 * データ系列ごとに異なる線種で描画する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	public boolean isLineDifferenceEachSeries() {
		return (_lineStyle == PlotLineStyles.VARIOUS);
	}

	/**
	 * 現在設定されている、データ値を結ぶ直線の描画スタイルを返す。
	 */
	public PlotLineStyles getLineStyle() {
		return _lineStyle;
	}

	/**
	 * データ値を結ぶ直線の描画スタイルを設定する。
	 * @param style	線描画スタイル
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setLineStyle(PlotLineStyles style) {
		if (style == null)
			style = PlotLineStyles.NONE;
		if (style != _lineStyle) {
			_modified = true;
			_lineStyle = style;
			return true;
		}
		return false;
	}

	/**
	 * データ値を結ぶ直線の端点に点を描画する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	public boolean isDrawMarkDisconnection() {
		return _markDisconnections;
	}

	/**
	 * データ値を結ぶ直線の端点に点を描画するかどうかを設定する。
	 * @param toDraw	端点を描画する場合は <tt>true</tt>、描画しない場合は <tt>false</tt>
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setDrawMarkDisconnection(boolean toDraw) {
		if (toDraw != _markDisconnections) {
			_modified = true;
			_markDisconnections = toDraw;
			return true;
		}
		return false;
	}

	/**
	 * データ点から X 軸への垂線を描画する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	public boolean isDrawImpulses() {
		return _impulses;
	}

	/**
	 * データ点から X 軸への垂線を描画するかどうかを設定する。
	 * @param toDraw	垂線を描画する場合は <tt>true</tt>、描画しない場合は <tt>false</tt>
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setDrawImpulses(boolean toDraw) {
		if (toDraw != _impulses) {
			_modified = true;
			_impulses = toDraw;
			return true;
		}
		return false;
	}

	/**
	 * 無効値があっても線でつなぐ場合に <tt>true</tt> を返す。
	 */
	public boolean isInvalidValueConnection() {
		return _connectInvalidValues;
	}

	/**
	 * 無効値があっても線でつなぐかどうかを設定する。
	 * @param toConnect		無効値を無視して線でつなぐ場合は <tt>true</tt>、無効値で線を切断する場合は <tt>false</tt>
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setInvalidValueConnection(boolean toConnect) {
		if (toConnect != _connectInvalidValues) {
			_modified = true;
			_connectInvalidValues = toConnect;
			return true;
		}
		return false;
	}

	/**
	 * 棒グラフを描画する場合は <tt>true</tt>、描画しない場合は <tt>false</tt> を返す。
	 */
	public boolean isDrawBars() {
		return _bars;
	}

	/**
	 * 棒グラフを描画するかどうかを設定する。
	 * @param toDraw	描画する場合は <tt>true</tt>、描画しない場合は <tt>false</tt>
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setDrawBars(boolean toDraw) {
		if (toDraw != _bars) {
			_modified = true;
			_bars = toDraw;
			return true;
		}
		return false;
	}

	/**
	 * データ系列間の棒グラフ描画オフセットを返す。
	 * このオフセット値は、データ値のスケールとなる。
	 */
	public double getBarOffset() {
		return _barOffset;
	}

	/**
	 * データ系列間の棒グラフ描画オフセットを設定する。
	 * @param offset	データ値スケールでのオフセット値
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setBarOffset(double offset) {
		if (offset != _barOffset) {
			_modified = true;
			_barOffset = offset;
			return true;
		}
		return false;
	}

	/**
	 * データ値スケールでの棒グラフ幅を返す。
	 */
	public double getBarWidth() {
		return _barWidth;
	}

	/**
	 * データ値スケールでの棒グラフ幅を設定する。
	 * @param width	データ値スケールでの幅
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setBarWidth(double width) {
		if (width != _barWidth) {
			_modified = true;
			_barWidth = width;
			return true;
		}
		return false;
	}

	@Override
	public PlotStyles clone() {
		try {
			return (PlotStyles)super.clone();
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError(PlotStyles.class.getName() + " class is not cloneable.");
		}
	}

	@Override
	public int hashCode() {
		int h = _modified ? 1231 : 1237;
		h = 31 * h + Float.valueOf(_strokeWidth).hashCode();
		h = 31 * h + _radius;
		h = 31 * h + _diameter;
		h = 31 * h + _errorBarLegLength;
		h = 31 * h + (_markStyle==null ? 0 : _markStyle.hashCode());
		h = 31 * h + (_lineStyle==null ? 0 : _lineStyle.hashCode());
		h = 31 * h + (_markDisconnections ? 1231 : 1237);
		h = 31 * h + (_impulses ? 1231 : 1237);
		h = 31 * h + (_bars ? 1231 : 1237);
		h = 31 * h + Double.valueOf(_barOffset).hashCode();
		h = 31 * h + Double.valueOf(_barWidth).hashCode();
		
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj.getClass() != PlotStyles.class)
			return false;
		
		return equalValues((PlotStyles)obj);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean equalValues(PlotStyles aStyle) {
		if (aStyle._modified != this._modified)
			return false;
		
		if (aStyle._strokeWidth != this._strokeWidth)
			return false;
		
		if (aStyle._radius != this._radius)
			return false;
		
		if (aStyle._diameter != this._diameter)
			return false;
		
		if (aStyle._errorBarLegLength != this._errorBarLegLength)
			return false;
		
		if (aStyle._markStyle != this._markStyle)
			return false;
		
		if (aStyle._lineStyle != this._lineStyle)
			return false;
		
		if (aStyle._markDisconnections != this._markDisconnections)
			return false;
		
		if (aStyle._impulses != this._impulses)
			return false;
		
		if (aStyle._bars != this._bars)
			return false;
		
		if (aStyle._barOffset != this._barOffset)
			return false;
		
		if (aStyle._barWidth != this._barWidth)
			return false;
		
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
