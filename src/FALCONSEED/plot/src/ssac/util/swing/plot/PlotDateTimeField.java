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
 * @(#)PlotDateTimeField.java	2.1.0	2013/08/13
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.awt.FontMetrics;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * <code>日付型</code> のデータ列に関する情報を保持するクラス。
 * 
 * @version 2.1.0	2013/08/13
 * @since 2.1.0
 */
public class PlotDateTimeField extends PlotDataField
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String TFORMAT_ONLY_YEAR	= "%1$tY";
	static protected final String TFORMAT_YEAR_MONTH	= "%1$tY/%1$tm";
	static protected final String TFORMAT_MONTH_DAY	= "%1$tm/%1$td";
	static protected final String TFORMAT_DATE		= "%1$tY/%1$tm/%1$td";
	static protected final String TFORMAT_DAY_TIME	= "%1$tm/%1$td %1$tR";
	static protected final String TFORMAT_HOUR_MIN	= "%1$tR";
	static protected final String TFORMAT_TIME_SEC	= "%1$tT";
	static protected final String TFORMAT_TIME_MILLI	= "%1$tT.%1$tL";
	static protected final String TFORMAT_ONLY_SEC	= "%1$tS";
	static protected final String TFORMAT_SEC_MILLI	= "%1$tS.%1$tL";
	static protected final String TFORMAT_ONLY_MILLI	= "%1$tL";
	static protected final String TFORMAT_MIDDLE	= "%1$tY/%1$tm/%1$td %1$tR";
	static protected final String TFORMAT_LONG		= "%1$tY/%1$tm/%1$td %1$tT";
	static protected final String TFORMAT_FULL		= "%1$tY/%1$tm/%1$td %1$tT.%1$tL";
	
	static protected final String TPATTERN_ONLY_YEAR	= "9999";
	static protected final String TPATTERN_YEAR_MONTH	= "9999/99";
	static protected final String TPATTERN_MONTH_DAY	= "99/99";
	static protected final String TPATTERN_DATE		= "9999/99/99";
	static protected final String TPATTERN_DAY_TIME	= "99/99 99:99";
	static protected final String TPATTERN_HOUR_MIN	= "99:99";
	static protected final String TPATTERN_TIME_SEC	= "99:99:99";
	static protected final String TPATTERN_TIME_MILLI	= "99:99:99.999";
	static protected final String TPATTERN_ONLY_SEC	= "99";
	static protected final String TPATTERN_SEC_MILLI	= "99.999";
	static protected final String TPATTERN_ONLY_MILLI	= "999";
	static protected final String TPATTERN_MIDDLE		= "9999/99/99 99:99";
	static protected final String TPATTERN_LONG			= "9999/99/99 99:99:99";
	static protected final String TPATTERN_FULL			= "9999/99/99 99:99:99.999";
	
	static protected final int	XTICK_SPACING = 10;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final PlotDateTimeFormats	_dtFormats;
	protected Calendar	_minDateTime;
	protected Calendar	_maxDateTime;

	protected int		_numTicks = 0;
	protected int		_maxTicksWidth = 0;
	protected String	_unitLabel = null;
	protected ArrayList<String> _tickLabels;
	protected ArrayList<Double> _tickValues;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで、新しいオブジェクトを生成する。
	 * <em>formats</em> に <tt>null</tt> を指定した場合、デフォルトのフォーマットが設定される。
	 * フィールド名は、データテーブルから取得する。
	 * このメソッドでは、フィールドインデックスはチェックしない。
	 * @param targetTable	対象データテーブル
	 * @param fieldIndex	データテーブル内の対象列インデックス
	 * @param formats		文字列を日付時刻値に変換するためのフォーマット定義
	 * @throws NullPointerException	<em>targetTable</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>fieldIndex</em> が範囲外の場合
	 */
	public PlotDateTimeField(IDataTable targetTable, int fieldIndex, PlotDateTimeFormats formats) {
		this(targetTable, fieldIndex, formats, null);
	}

	/**
	 * 指定されたパラメータで、新しいオブジェクトを生成する。
	 * <em>formats</em> に <tt>null</tt> を指定した場合、デフォルトのフォーマットが設定される。
	 * <em>fieldName</em> に <tt>null</tt> を指定した場合、データテーブルからフィールド名を取得する。
	 * このメソッドでは、フィールドインデックスはチェックしない。
	 * @param targetTable	対象データテーブル
	 * @param fieldIndex	データテーブル内の対象列インデックス
	 * @param formats		文字列を日付時刻値に変換するためのフォーマット定義
	 * @param fieldName		任意のフィールド名
	 * @throws NullPointerException	<em>targetTable</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>fieldIndex</em> が範囲外の場合
	 */
	public PlotDateTimeField(IDataTable targetTable, int fieldIndex, PlotDateTimeFormats formats, String fieldName) {
		super(targetTable, fieldIndex, Calendar.class, fieldName);
		if (formats == null) {
			_dtFormats = new PlotDateTimeFormats();
		} else {
			_dtFormats = formats;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public PlotDateTimeFormats getDateTimeFormats() {
		return _dtFormats;
	}
	
	public Calendar getDateTimeValue(long index) {
		Calendar cal = null;
		Object fvalue = getFieldValue(index);
		if (fvalue != null) {
			String strValue = fvalue.toString();
			if (strValue.length() > 0) {
				cal = _dtFormats.parse(strValue);
			}
		}
		
		if (isInvalidValueAsZero() && cal == null) {
			cal = Calendar.getInstance();
			cal.setTimeInMillis(0L);
		}
		
		return cal;
	}
	
	public Calendar getMinimumDateTimeValue() {
		return _minDateTime;
	}
	
	public Calendar getMaximumDateTimeValue() {
		return _maxDateTime;
	}
	
	/**
	 * データ型が数値の場合はその値、それ以外の場合はプロット位置を示す数値を返す。
	 * 無効値の場合は <tt>null</tt> を返す。
	 * <p>このクラスでは、エポックからの経過ミリ秒を返す。
	 * @param index	データレコード先頭からのインデックス
	 * @return	プロットする数値、無効値の場合は <tt>null</tt>
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	@Override
	public BigDecimal getDecimalValue(long index) {
		Calendar cal = getDateTimeValue(index);
		if (cal != null) {
			return new BigDecimal(cal.getTimeInMillis());
		} else {
			return null;
		}
	}

	/**
	 * このオブジェクトの有効数値範囲を、指定されたレコードの値で更新する。
	 * @param index	データレコードのインデックス
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	@Override
	public void refreshDecimalRange(long index) {
		Calendar cal = getDateTimeValue(index);
		if (cal != null) {
			// min
			if (_minDateTime == null)
				_minDateTime = cal;
			else if (cal.compareTo(_minDateTime) < 0)
				_minDateTime = cal;
			
			// max
			if (_maxDateTime == null)
				_maxDateTime = cal;
			else if (cal.compareTo(_maxDateTime) > 0)
				_maxDateTime = cal;
			
			// decimal value
			BigDecimal value = new BigDecimal(cal.getTimeInMillis());
			// min
			if (_minDecimal == null)
				_minDecimal = value;
			else if (value.compareTo(_minDecimal) < 0)
				_minDecimal = value;
			
			// max
			if (_maxDecimal == null)
				_maxDecimal = value;
			else if (value.compareTo(_maxDecimal) > 0)
				_maxDecimal = value;
		}
	}
	
	public int getTicksCount() {
		return _numTicks;
	}
	
	public int getMaximumTickLabelWidth() {
		return _maxTicksWidth;
	}
	
	public String getUnitLabel() {
		return _unitLabel;
	}
	
	public List<String> getTickLabels() {
		if (_tickLabels != null) {
			return Collections.unmodifiableList(_tickLabels);
		} else {
			return Collections.emptyList();
		}
	}
	
	public List<Double> getTickValues() {
		if (_tickValues != null) {
			return Collections.unmodifiableList(_tickValues);
		} else {
			return Collections.emptyList();
		}
	}
	
	public void updateXTickLabelsByWidth(int width, double minValue, double maxValue, FontMetrics fm) {
		clearTicks();
		
		// check value
		if (Double.isNaN(minValue) || Double.isInfinite(minValue) || Double.isNaN(maxValue) || Double.isInfinite(maxValue)) {
			return;		// no ticks
		}

		// create Calendar instance
		BigDecimal dMin = new BigDecimal(minValue).setScale(0, BigDecimal.ROUND_CEILING);
		BigDecimal dMax = new BigDecimal(maxValue).setScale(0, BigDecimal.ROUND_FLOOR);
		Calendar calMin = (_minDateTime==null ? Calendar.getInstance() : (Calendar)_minDateTime.clone());
		Calendar calMax = (_maxDateTime==null ? Calendar.getInstance() : (Calendar)_maxDateTime.clone());
		calMin.setTimeInMillis(dMin.longValue());
		calMax.setTimeInMillis(dMax.longValue());
		if (calMin.compareTo(calMax) > 0) {
			// no tick
			return;
		}
		
		// calc tick
		int numTicks = calcXTicksCountByWidth(width, calMin, calMax, fm);
		if (numTicks <= 0) {
			return;		// no ticks
		}
		
		// update ticks
		updateTicks(numTicks, calMin, calMax, fm);
	}
	
	public void updateTickLabels(int maxTicks, double minValue, double maxValue, FontMetrics fm) {
		clearTicks();
		
		if (maxTicks <= 0) {
			return;		// no ticks
		}
		
		// check value
		if (Double.isNaN(minValue) || Double.isInfinite(minValue) || Double.isNaN(maxValue) || Double.isInfinite(maxValue)) {
			return;		// no tick
		}

		// create Calendar instance
		BigDecimal dMin = new BigDecimal(minValue).setScale(0, BigDecimal.ROUND_CEILING);
		BigDecimal dMax = new BigDecimal(maxValue).setScale(0, BigDecimal.ROUND_FLOOR);
		Calendar calMin = (_minDateTime==null ? Calendar.getInstance() : (Calendar)_minDateTime.clone());
		Calendar calMax = (_maxDateTime==null ? Calendar.getInstance() : (Calendar)_maxDateTime.clone());
		calMin.setTimeInMillis(dMin.longValue());
		calMax.setTimeInMillis(dMax.longValue());
		if (calMin.compareTo(calMax) > 0) {
			// no tick
			return;
		}
		
		// update ticks
		updateTicks(maxTicks, calMin, calMax, fm);
	}

	//------------------------------------------------------------
	// Internal helper methods
	//------------------------------------------------------------
	
	static protected long getYearSpan(Calendar calMin, Calendar calMax) {
		return (calMax.get(Calendar.YEAR) - calMin.get(Calendar.YEAR));
	}
	
	static protected long getMonthSpan(Calendar calMin, Calendar calMax) {
		long sYear = getYearSpan(calMin, calMax);
		if (sYear > 0)
			return (12 - calMin.get(Calendar.MONTH) + calMax.get(Calendar.MONTH) + (sYear>1 ? sYear-1 : 0) * 12);
		else if (sYear < 0)
			return -(12 - calMax.get(Calendar.MONTH) + calMin.get(Calendar.MONTH) + (sYear>1 ? sYear-1 : 0) * 12);
		else
			return (calMax.get(Calendar.MONTH) - calMin.get(Calendar.MONTH));
	}
	
	static protected long getDaySpan(Calendar calMin, Calendar calMax) {
		return (calMax.getTimeInMillis()/86400000L - calMin.getTimeInMillis()/86400000L);
	}
	
	static protected long getHourSpan(Calendar calMin, Calendar calMax) {
		return (calMax.getTimeInMillis()/3600000L - calMin.getTimeInMillis()/3600000L);
	}
	
	static protected long getMinuteSpan(Calendar calMin, Calendar calMax) {
		return (calMax.getTimeInMillis()/60000L - calMin.getTimeInMillis()/60000L);
	}
	
	static protected long getSecondSpan(Calendar calMin, Calendar calMax) {
		return (calMax.getTimeInMillis()/1000L - calMin.getTimeInMillis()/1000L);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void clearTicks() {
		_numTicks = 0;
		_maxTicksWidth = 0;
		_unitLabel = null;
		_tickLabels = null;
		_tickValues = null;
	}
	
	protected int calcXTicksCountByWidth(int width, Calendar calMin, Calendar calMax, FontMetrics fm) {
		if (calMin.getTimeInMillis() == calMax.getTimeInMillis()) {
			// 同一時刻
			return 1;
		}
		
		int num;
		long sYear   = getYearSpan(calMin, calMax);
		long sMonth  = getMonthSpan(calMin, calMax);
		long sDay    = getDaySpan(calMin, calMax);
		long sHour   = getHourSpan(calMin, calMax);
		long sMinute = getMinuteSpan(calMin, calMax);
		long sSecond = getSecondSpan(calMin, calMax);
		
		if (sDay != 0) {
			// 日が異なる
			if (sYear != 0) {
				// 年単位表示
				num = width / (fm.stringWidth(TPATTERN_ONLY_YEAR) + XTICK_SPACING);
				if (num <= 2 || num <= sYear) {
					// 年単位表示
					return num;
				}
				// 月単位
				num = width / (fm.stringWidth(TPATTERN_YEAR_MONTH) + XTICK_SPACING);
				if (num <= 2 || num <= sMonth) {
					// 年月表示
					return num;
				}
				// 日単位(年月日表示)
				num = width / (fm.stringWidth(TPATTERN_DATE) + XTICK_SPACING);
			}
			else if (sMonth != 0) {
				// 月日表示(同一年)
				num = width / (fm.stringWidth(TPATTERN_MONTH_DAY) + XTICK_SPACING);
			}
			else {
				// 日単位(同一月)
				num = width / (fm.stringWidth(TPATTERN_MONTH_DAY) + XTICK_SPACING);
				if (num <= 2 || num <= sDay) {
					// 月日表示
					return num;
				}
				// 時間単位(日時刻表示)
				num = width / (fm.stringWidth(TPATTERN_DAY_TIME) + XTICK_SPACING);
			}
		}
		else if (sYear != 0 || sMonth != 0) {
			// 同一日だが、年または月が異なるので、年からフル表示
			if (sHour != 0) {
				// 時分単位
				num = width / (fm.stringWidth(TPATTERN_MIDDLE) + XTICK_SPACING);
			}
			else if (sMinute != 0) {
				// 時分単位
				num = width / (fm.stringWidth(TPATTERN_MIDDLE) + XTICK_SPACING);
				if (num <= 2 || num <= sMinute) {
					// 時分表示
					return num;
				}
				// 時分秒表示
				num = width / (fm.stringWidth(TPATTERN_LONG) + XTICK_SPACING);
			}
			else if (sSecond != 0) {
				// 秒単位
				num = width / (fm.stringWidth(TPATTERN_LONG) + XTICK_SPACING);
				if (num <= 2 || num <= sSecond) {
					// 時分秒表示
					return num;
				}
				// ミリ秒表示
				num = width / (fm.stringWidth(TPATTERN_FULL) + XTICK_SPACING);
			}
			else {
				// ミリ秒単位
				num = width / (fm.stringWidth(TPATTERN_FULL) + XTICK_SPACING);
			}
		}
		else if (sHour != 0) {
			// （同一日）時間が異なる
			num = width / (fm.stringWidth(TPATTERN_HOUR_MIN) + XTICK_SPACING);
		}
		else if (sMinute != 0) {
			// （同一時）分が異なる
			num = width / (fm.stringWidth(TPATTERN_HOUR_MIN) + XTICK_SPACING);
			if (num <= 2 || num <= sMinute) {
				// 時分表示
				return num;
			}
			// 秒単位表示
			num = width / (fm.stringWidth(TPATTERN_TIME_SEC) + XTICK_SPACING);
		}
		else if (sSecond != 0) {
			// （同一時分）秒が異なる
			num = width / (fm.stringWidth(TPATTERN_ONLY_SEC) + XTICK_SPACING);
			if (num <= 2 || num <= sSecond) {
				// 秒のみ表示
				return num;
			}
			// 秒ミリ秒表示
			num = width / (fm.stringWidth(TPATTERN_SEC_MILLI) + XTICK_SPACING);
		}
		else {
			// （同一秒）ミリ秒単位
			num = width / (fm.stringWidth(TPATTERN_ONLY_MILLI) + XTICK_SPACING);
		}
		
		return num;
	}
	
	protected void updateTicks(int maxTicks, Calendar calMin, Calendar calMax, FontMetrics fm) {
		if (calMin.getTimeInMillis() == calMax.getTimeInMillis()) {
			_tickLabels = new ArrayList<String>(1);
			_tickValues = new ArrayList<Double>(1);
			_tickLabels.add(String.format(TFORMAT_FULL, calMin));
			_tickValues.add((double)calMin.getTimeInMillis());
			_numTicks = 1;
			return;
		}
		
		if (maxTicks < 2) {
			maxTicks = 2;
		}
		_tickLabels = new ArrayList<String>(maxTicks);
		_tickValues = new ArrayList<Double>(maxTicks);
		
		long sYear   = getYearSpan(calMin, calMax);
		long sMonth  = getMonthSpan(calMin, calMax);
		long sDay    = getDaySpan(calMin, calMax);
		long sHour   = getHourSpan(calMin, calMax);
		long sMinute = getMinuteSpan(calMin, calMax);
		long sSecond = getSecondSpan(calMin, calMax);
		long sMilli  = calMax.getTimeInMillis() - calMin.getTimeInMillis();
		
		int addField;
		int initStep;
		int step;
		String unitValue = null;
		String calformat = null;
		Calendar calStep = (Calendar)calMin.clone();
		calStep.setTimeInMillis(0L);
		
		if (sDay != 0) {
			// 日が異なる
			if (sYear != 0) {
				// 年が異なる
				unitValue = null;
				calStep.set(Calendar.YEAR, calMin.get(Calendar.YEAR));
				if (sYear >= maxTicks) {
					// 年単位表示
					addField = Calendar.YEAR;
					initStep = 1;
					step = (int)Math.ceil((double)sYear / (double)maxTicks);
					calformat = TFORMAT_ONLY_YEAR;
				}
				else if (sMonth >= maxTicks) {
					// 月単位表示
					if ((sMonth / 6) > maxTicks) {
						// 6ヶ月単位で表示できない場合は、年単位
						addField = Calendar.YEAR;
						initStep = 1;
						step = 1;
						calformat = TFORMAT_ONLY_YEAR;
					}
					else {
						// 月単位表示
						calStep.set(Calendar.MONTH, calMin.get(Calendar.MONTH));
						addField = Calendar.MONTH;
						initStep = 1;
						step = (int)Math.ceil((double)sMonth / (double)maxTicks);
						calformat = TFORMAT_YEAR_MONTH;
					}
				}
				else {
					// 日単位表示
					calStep.set(Calendar.MONTH, calMin.get(Calendar.MONTH));
					if ((sDay / 15) > maxTicks) {
						// 15日単位で表示できない場合は、月単位
						addField = Calendar.MONTH;
						initStep = 1;
						step = 1;
						calformat = TFORMAT_YEAR_MONTH;
					}
					else {
						// 日単位表示
						calStep.set(Calendar.DAY_OF_MONTH, calMin.get(Calendar.DAY_OF_MONTH));
						addField = Calendar.DAY_OF_MONTH;
						initStep = 1;
						step = (int)Math.ceil((double)sDay / (double)maxTicks);
						calformat = TFORMAT_DATE;
					}
				}
			}
			else if (sMonth != 0) {
				// 月が異なる
				unitValue = String.format(TFORMAT_ONLY_YEAR, calMin);
				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), 1);
				calformat = TFORMAT_MONTH_DAY;
				if (sMonth >= maxTicks) {
					// 月単位表示
					addField = Calendar.MONTH;
					initStep = 1;
					step = (int)Math.ceil((double)sMonth / (double)maxTicks);
				}
				else if ((sDay / 15) > maxTicks) {
					// 15日単位で表示できない場合は、月単位
					addField = Calendar.MONTH;
					initStep = 1;
					step = 1;
				}
				else {
					// 日単位
					calStep.set(Calendar.DAY_OF_MONTH, calMin.get(Calendar.DAY_OF_MONTH));
					addField = Calendar.DAY_OF_MONTH;
					initStep = 1;
					step = (int)Math.ceil((double)sDay / (double)maxTicks);
				}
			}
			else {
				// 日が異なる
				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH));
				if (sDay >= maxTicks) {
					// 日単位表示
					unitValue = String.format(TFORMAT_ONLY_YEAR, calMin);
					addField = Calendar.DAY_OF_MONTH;
					initStep = 1;
					step = (int)Math.ceil((double)sDay / (double)maxTicks);
					calformat = TFORMAT_MONTH_DAY;
				}
				else if ((sHour / 12) > maxTicks) {
					// 12時間単位で表示できない場合は、日単位
					unitValue = String.format(TFORMAT_ONLY_YEAR, calMin);
					addField = Calendar.DAY_OF_MONTH;
					initStep = 1;
					step = 1;
					calformat = TFORMAT_MONTH_DAY;
				}
				else {
					// 時間単位
					calStep.set(Calendar.HOUR_OF_DAY, calMin.get(Calendar.HOUR_OF_DAY));
					unitValue = String.format(TFORMAT_YEAR_MONTH, calMin);
					addField = Calendar.HOUR_OF_DAY;
					initStep = 1;
					step = (int)Math.ceil((double)sHour / (double)maxTicks);
					calformat = TFORMAT_DAY_TIME;
				}
			}
		}
		else if (sYear != 0 || sMonth != 0) {
			// 同一日だが、年または月が異なるので、年からフル表示
			unitValue = null;
			if (sHour != 0) {
				// 時間が異なる
				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
						calMin.get(Calendar.HOUR_OF_DAY), 0);
				calformat = TFORMAT_MIDDLE;
				if (sHour >= maxTicks) {
					// 時間単位表示
					addField = Calendar.HOUR_OF_DAY;
					initStep = 1;
					step = (int)Math.ceil((double)sHour / (double)maxTicks);
				}
				else if ((sMinute / 30) > maxTicks) {
					// 30分単位で表示できない場合は、時間単位
					addField = Calendar.HOUR_OF_DAY;
					initStep = 1;
					step = 1;
				}
				else {
					// 分単位
					calStep.set(Calendar.MINUTE, calMin.get(Calendar.MINUTE));
					addField = Calendar.MINUTE;
					initStep = 1;
					step = (int)Math.ceil((double)sMinute / (double)maxTicks);
				}
			}
			else if (sMinute != 0) {
				// 分が異なる
				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
						calMin.get(Calendar.HOUR_OF_DAY), calMin.get(Calendar.MINUTE));
				if (sMinute >= maxTicks) {
					// 分単位表示
					addField = Calendar.MINUTE;
					initStep = 1;
					step = (int)Math.ceil((double)sMinute / (double)maxTicks);
					calformat = TFORMAT_MIDDLE;
				}
				else if ((sSecond / 30) > maxTicks) {
					// 30秒単位で表示できない場合は、分単位
					addField = Calendar.MINUTE;
					initStep = 1;
					step = (int)Math.ceil((double)sMinute / (double)maxTicks);
					calformat = TFORMAT_MIDDLE;
				}
				else {
					// 秒単位
					calStep.set(Calendar.SECOND, calMin.get(Calendar.SECOND));
					addField = Calendar.SECOND;
					initStep = 1;
					step = (int)Math.ceil((double)sSecond / (double)maxTicks);
					calformat = TFORMAT_LONG;
				}
			}
			else if (sSecond != 0) {
				// 秒が異なる
				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
						calMin.get(Calendar.HOUR_OF_DAY), calMin.get(Calendar.MINUTE), calMin.get(Calendar.SECOND));
				if (sSecond >= maxTicks) {
					// 秒単位表示
					addField = Calendar.SECOND;
					initStep = 1;
					step = (int)Math.ceil((double)sSecond / (double)maxTicks);
					calformat = TFORMAT_LONG;
				}
				else if ((sMilli / 500) > maxTicks) {
					// 500ミリ秒単位で表示できない場合は、秒単位
					addField = Calendar.SECOND;
					initStep = 1;
					step = 1;
					calformat = TFORMAT_LONG;
				}
				else {
					// ミリ秒単位
					calStep.set(Calendar.MILLISECOND, calMin.get(Calendar.MILLISECOND));
					addField = Calendar.MILLISECOND;
					initStep = 1;
					step = (int)Math.ceil((double)sMilli / (double)maxTicks);
					calformat = TFORMAT_FULL;
				}
			}
			else {
				// ミリ秒単位
				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
						calMin.get(Calendar.HOUR_OF_DAY), calMin.get(Calendar.MINUTE), calMin.get(Calendar.SECOND));
				calStep.set(Calendar.MILLISECOND, calMin.get(Calendar.MILLISECOND));
				addField = Calendar.MILLISECOND;
				initStep = 1;
				step = (int)Math.ceil((double)sMilli / (double)maxTicks);
				calformat = TFORMAT_FULL;
			}
		}
		else if (sHour != 0) {
			// （同一日）時間が異なる
			unitValue = String.format(TFORMAT_DATE, calMin);
			calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
					calMin.get(Calendar.HOUR_OF_DAY), 0);
			calformat = TFORMAT_HOUR_MIN;
			if (sHour >= maxTicks) {
				// 時間単位表示
				addField = Calendar.HOUR_OF_DAY;
				initStep = 1;
				step = (int)Math.ceil((double)sHour / (double)maxTicks);
			}
			else if ((sMinute / 30) > maxTicks) {
				// 30分単位で表示できない場合は、時間単位
				addField = Calendar.HOUR_OF_DAY;
				initStep = 1;
				step = 1;
			}
			else {
				// 分単位
				calStep.set(Calendar.MINUTE, calMin.get(Calendar.MINUTE));
				addField = Calendar.MINUTE;
				initStep = 1;
				step = (int)Math.ceil((double)sMinute / (double)maxTicks);
			}
		}
		else if (sMinute != 0) {
			// （同一日）分が異なる
			unitValue = String.format(TFORMAT_DATE, calMin);
			calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
					calMin.get(Calendar.HOUR_OF_DAY), calMin.get(Calendar.MINUTE));
			if (sMinute >= maxTicks) {
				// 分単位表示
				addField = Calendar.MINUTE;
				initStep = 1;
				step = (int)Math.ceil((double)sMinute / (double)maxTicks);
				calformat = TFORMAT_HOUR_MIN;
			}
			else if ((sSecond / 30) > maxTicks) {
				// 30秒単位で表示できない場合は、分単位
				addField = Calendar.MINUTE;
				initStep = 1;
				step = (int)Math.ceil((double)sMinute / (double)maxTicks);
				calformat = TFORMAT_HOUR_MIN;
			}
			else {
				// 秒単位
				calStep.set(Calendar.SECOND, calMin.get(Calendar.SECOND));
				addField = Calendar.SECOND;
				initStep = 1;
				step = (int)Math.ceil((double)sSecond / (double)maxTicks);
				calformat = TFORMAT_TIME_SEC;
			}
		}
		else if (sSecond != 0) {
			// （同一日）秒が異なる
			unitValue = String.format(TFORMAT_DAY_TIME, calMin);
			calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
					calMin.get(Calendar.HOUR_OF_DAY), calMin.get(Calendar.MINUTE), calMin.get(Calendar.SECOND));
			if (sSecond >= maxTicks) {
				// 秒単位表示
				addField = Calendar.SECOND;
				initStep = 1;
				step = (int)Math.ceil((double)sSecond / (double)maxTicks);
				calformat = TFORMAT_ONLY_SEC;
			}
			else if ((sMilli / 500) > maxTicks) {
				// 500ミリ秒単位で表示できない場合は、秒単位
				addField = Calendar.SECOND;
				initStep = 1;
				step = 1;
				calformat = TFORMAT_ONLY_SEC;
			}
			else {
				// ミリ秒単位
				calStep.set(Calendar.MILLISECOND, calMin.get(Calendar.MILLISECOND));
				addField = Calendar.MILLISECOND;
				initStep = 1;
				step = (int)Math.ceil((double)sMilli / (double)maxTicks);
				calformat = TFORMAT_SEC_MILLI;
			}
		}
		else {
			// （同一日）ミリ秒単位
			unitValue = String.format(TFORMAT_LONG, calMin);
			calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
					calMin.get(Calendar.HOUR_OF_DAY), calMin.get(Calendar.MINUTE), calMin.get(Calendar.SECOND));
			calStep.set(Calendar.MILLISECOND, calMin.get(Calendar.MILLISECOND));
			addField = Calendar.MILLISECOND;
			initStep = 1;
			step = (int)Math.ceil((double)sMilli / (double)maxTicks);
			calformat = TFORMAT_ONLY_MILLI;
		}

		// 更新
		_unitLabel = unitValue;
		
		for (; calStep.compareTo(calMin) < 0; ) {
			calStep.add(addField, initStep);
		}
		
		if (fm != null) {
			for (; calStep.compareTo(calMax) <= 0; ) {
				String label = String.format(calformat, calStep);
				double value = (double)calStep.getTimeInMillis();
				_tickLabels.add(label);
				_tickValues.add(value);
				calStep.add(addField, step);
				_maxTicksWidth = Math.max(_maxTicksWidth, fm.stringWidth(label));
			}
		}
		else {
			for (; calStep.compareTo(calMax) <= 0; ) {
				String label = String.format(calformat, calStep);
				double value = (double)calStep.getTimeInMillis();
				_tickLabels.add(label);
				_tickValues.add(value);
				calStep.add(addField, step);
			}
		}
		_numTicks = _tickLabels.size();
	}
	
//	protected void updateTicks(int maxTicks, double min, double max, FontMetrics fm) {
//		_maxTicksWidth = 0;
//		_unitLabel = null;
//		_tickLabels = new ArrayList<String>(maxTicks);
//		_tickValues = new ArrayList<Double>(maxTicks);
//		int minTicks = Math.min(MIN_TICKS, maxTicks);
//		
//		if (Double.isNaN(min) || Double.isInfinite(min) || Double.isNaN(max) || Double.isInfinite(max)) {
//			return;	// no tick
//		}
//		
//		BigDecimal dMin = new BigDecimal(min).setScale(0, BigDecimal.ROUND_CEILING);
//		BigDecimal dMax = new BigDecimal(max).setScale(0, BigDecimal.ROUND_FLOOR);
//		Calendar calMin = (Calendar)_minDateTime.clone();
//		Calendar calMax = (Calendar)_maxDateTime.clone();
//		calMin.setTimeInMillis(dMin.longValue());
//		calMax.setTimeInMillis(dMax.longValue());
//		
//		int step;
//		int addField;
//		String calformat;
//		Calendar calStep = (Calendar)calMin.clone();
//		calStep.set(Calendar.MILLISECOND, 0);
//		
//		long spanYears   = calMax.get(Calendar.YEAR) - calMin.get(Calendar.YEAR) + 1;
//		long spanMonths  = 0;
//		long spanDays    = 0;
//		long spanHours   = 0;
//		if (spanYears > 1) {
//			spanMonths = (12 - calMin.get(Calendar.MONTH)) + calMax.get(Calendar.MONTH) + 1 + ((spanYears>2 ? spanYears-2 : 0) * 12);
//		} else {
//			spanMonths = calMax.get(Calendar.MONTH) - calMin.get(Calendar.MONTH) + 1;
//		}
//		if (spanMonths > 1) {
//			spanDays = (366 - calMin.get(Calendar.DAY_OF_YEAR)) + calMax.get(Calendar.DAY_OF_YEAR) + ((spanYears>2 ? spanYears-2 : 0) * 365);
//		} else {
//			spanDays = calMax.get(Calendar.DAY_OF_YEAR) - calMin.get(Calendar.DAY_OF_YEAR) + 1;
//		}
//		if (spanDays > 1) {
//			spanHours = (24 - calMin.get(Calendar.HOUR_OF_DAY)) + calMax.get(Calendar.HOUR_OF_DAY) + 1 + ((spanDays>2 ? spanDays-2 : 0) * 24);
//		} else {
//			spanHours = calMax.get(Calendar.HOUR_OF_DAY) - calMin.get(Calendar.HOUR_OF_DAY) + 1;
//		}
//		
//		if (spanYears > maxTicks) {
//			// 年単位
//			addField = Calendar.YEAR;
//			step = (int)Math.ceil((double)spanYears / (double)maxTicks);
//			calStep.set(calMin.get(Calendar.YEAR), 0, 1, 0, 0, 0);
//			calformat = "%1$tY";
//		}
//		else if (spanMonths > maxTicks) {
//			// 月もしくは年単位
//			if ((spanMonths / 6) > maxTicks) {
//				// 年単位
//				addField = Calendar.YEAR;
//				step = 1;
//				calStep.set(calMin.get(Calendar.YEAR), 0, 1, 0, 0, 0);
//				calformat = "%1$tY";
//			}
//			else if ((spanMonths / 3) > maxTicks) {
//				// 6ヶ月単位
//				addField = Calendar.MONTH;
//				step = 6;
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH)/step*step, 1, 0, 0, 0);
//				calformat = "%1$tY/%1$tm";
//			}
//			else {
//				// 3ヶ月単位
//				addField = Calendar.MONTH;
//				step = 3;
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH)/step*step, 1, 0, 0, 0);
//				calformat = "%1$tY/%1$tm";
//			}
//		}
//		else if (spanDays > maxTicks) {
//			// 日もしくは月単位
//			if ((spanDays / 15) > maxTicks) {
//				// 月単位
//				addField = Calendar.MONTH;
//				step = 1;
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), 1, 0, 0, 0);
//				calformat = "%1$tY/%1$tm";
//			}
//			else {
//				if ((spanDays / 10) > maxTicks) {
//					// 15日単位
//					step = 15;
//				}
//				else if ((spanDays / 5) > maxTicks) {
//					// 10日単位
//					addField = Calendar.DAY_OF_MONTH;
//					step = 10;
//					int day = calMin.get(Calendar.DAY_OF_MONTH)/step*step+1;
//					calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), day, 0, 0, 0);
//					if (day > 30) {
//						calStep.set(Calendar.DAY_OF_MONTH, 1);
//						calStep.add(Calendar.MONTH, 1);
//					}
//					calformat = "%1$tY/%1$tm/%1$te";
//				}
//				else {
//					// 5日単位
//					addField = Calendar.DAY_OF_MONTH;
//					step = 5;
//					int day = calMin.get(Calendar.DAY_OF_MONTH)/step*step+1;
//					calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), day, 0, 0, 0);
//					if (day > 30) {
//						calStep.set(Calendar.DAY_OF_MONTH, 1);
//						calStep.add(Calendar.MONTH, 1);
//					}
//					calformat = "%1$tY/%1$tm/%1$te";
//				}
//				addField = Calendar.DAY_OF_MONTH;
//				int day = calMin.get(Calendar.DAY_OF_MONTH)/step*step+1;
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), day, 0, 0, 0);
//				if (day > 30) {
//					calStep.set(Calendar.DAY_OF_MONTH, 1);
//					calStep.add(Calendar.MONTH, 1);
//				}
//				if (spanYears > 1) {
//					// 異なる年
//					calformat = "%1$tY/%1$tm/%1$te";
//				} else {
//					// 同一年
//					_unitLabel = String.format("%1$tY", calMin);
//					calformat = "%1$tm/%1$te";
//				}
//			}
//		}
//		else if (spanHours > maxTicks) {
//			// 時間もしくは日単位
//			if ((spanHours / 12) > maxTicks) {
//				// 日単位
//				addField = Calendar.DAY_OF_YEAR;
//				step = 1;
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
//				calformat = "%1$tY/%1$tm/%1$te";
//				if (spanYears > 1) {
//					// 異なる年
//					calformat = "%1$tY/%1$tm/%1$te";
//				} else {
//					// 同一年
//					_unitLabel = String.format("%1$tY", calMin);
//					calformat = "%1$tm/%1$te";
//				}
//			}
//			else if ((spanHours / 6) > maxTicks) {
//				// 12時間単位
//				addField = Calendar.HOUR_OF_DAY;
//				step = 12;
//				int hour = calMin.get(Calendar.HOUR_OF_DAY)/step*step;
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH), hour, 0, 0);
//				if (spanDays > 1) {
//					// 異なる日
//					calformat = "%1$tY/%1$tm/%1$te";
//				}
//				
//				if (spanDays <= 1) {
//					// 同一日
//					_unitLabel = String.format("%1$tY/%1$tm/%1$te");
//				}
//			}
//		}
//		
//		if (calMin.get(Calendar.YEAR) != calMax.get(Calendar.YEAR)) {
//			// 年が異なる
//			int yspan = calMax.get(Calendar.YEAR) - calMin.get(Calendar.YEAR) + 1;
//			if (yspan >= minTicks) {
//				// 年ベース
//				addField = Calendar.YEAR;
//				step = (int)Math.ceil((double)yspan / (double)maxTicks);
//				calStep.set(calMin.get(Calendar.YEAR), 0, 1, 0, 0, 0);
//				calformat = "%1$tY";
//			}
//			else {
//				// 月ベース
//				int mspan = (12 - calMin.get(Calendar.MONTH)) + (calMax.get(Calendar.MONTH) + 1) + ((yspan>2 ? yspan-2 : 0) * 12);
//				addField = Calendar.MONTH;
//				step = (int)Math.ceil((double)mspan / (double)maxTicks);
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), 1, 0, 0, 0);
//				calformat = "%1$tY-%1$tb";
//			}
//		}
//		else if (calMin.get(Calendar.MONTH) != calMax.get(Calendar.MONTH)) {
//			// 月が異なる
//			_unitLabel = String.format("%1$tY", calMin);
//			int mspan = calMax.get(Calendar.MONTH) - calMin.get(Calendar.MONTH) + 1;
//			if (mspan >= minTicks) {
//				// 月ベース
//				addField = Calendar.MONTH;
//				step = (int)Math.ceil((double)mspan / (double)maxTicks);
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), 1, 0, 0, 0);
//				calformat = "%1$tb";
//			}
//			else {
//				// 日ベース
//				int dspan = calMax.get(Calendar.DAY_OF_YEAR) - calMin.get(Calendar.DAY_OF_YEAR) + 1;
//				addField = Calendar.DAY_OF_YEAR;
//				step = (int)Math.ceil((double)dspan / (double)maxTicks);
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
//				calformat = "%1$tb-%1$te";
//			}
//		}
//		else if (calMin.get(Calendar.DAY_OF_MONTH) != calMax.get(Calendar.DAY_OF_MONTH)) {
//			// 日が異なる
//			_unitLabel = String.format("%1$tY-%1$tb", calMin);
//			int dspan = calMax.get(Calendar.DAY_OF_YEAR) - calMin.get(Calendar.DAY_OF_YEAR) + 1;
//			if (dspan >= minTicks) {
//				// 日ベース
//				addField = Calendar.DAY_OF_YEAR;
//				addField = Calendar.DAY_OF_YEAR;
//				step = (int)Math.ceil((double)dspan / (double)maxTicks);
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
//				calformat = "%1$te";
//			}
//			else {
//				// 時間ベース
//				int hspan = (24 - calMin.get(Calendar.HOUR_OF_DAY)) + (calMax.get(Calendar.HOUR_OF_DAY) + 1) + ((dspan>2 ? dspan-2 : 0) * 24);
//				addField = Calendar.HOUR_OF_DAY;
//				step = (int)Math.ceil((double)hspan / (double)maxTicks);
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH), calMin.get(Calendar.HOUR_OF_DAY), 0, 0);
//				calformat = "%1$te %1$tR";
//			}
//		}
//		else if (calMin.get(Calendar.HOUR_OF_DAY) != calMax.get(Calendar.HOUR_OF_DAY)) {
//			// 時が異なる
//			_unitLabel = String.format("%1$tY-%1$tb-%1$te", calMin);
//			int hspan = calMax.get(Calendar.HOUR_OF_DAY) - calMax.get(Calendar.HOUR_OF_DAY) + 1;
//			if (hspan >= minTicks) {
//				// 時間ベース
//				addField = Calendar.HOUR_OF_DAY;
//				step = (int)Math.ceil((double)hspan / (double)maxTicks);
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH), calMin.get(Calendar.HOUR_OF_DAY), 0, 0);
//				calformat = "%1$tR";
//			}
//			else {
//				// 分ベース
//				int mspan = (60-calMin.get(Calendar.MINUTE)) + (calMax.get(Calendar.MINUTE) + 1) + (hspan>2 ? hspan-2 : 0) * 60;
//				addField = Calendar.MINUTE;
//				step = (int)Math.ceil((double)mspan / (double)maxTicks);
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
//						calMin.get(Calendar.HOUR_OF_DAY), calMin.get(Calendar.MINUTE), 0);
//				calformat = "%1$tR";
//			}
//		}
//		else if (calMin.get(Calendar.MINUTE) != calMax.get(Calendar.MINUTE)) {
//			// 分が異なる
//			_unitLabel = String.format("%1$tY-%1$tb-%1$te", calMin);
//			int mspan = calMax.get(Calendar.MINUTE) - calMin.get(Calendar.MINUTE) + 1;
//			if (mspan >= minTicks) {
//				// 分ベース
//				addField = Calendar.MINUTE;
//				step = (int)Math.ceil((double)mspan / (double)maxTicks);
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
//						calMin.get(Calendar.HOUR_OF_DAY), calMin.get(Calendar.MINUTE), 0);
//				calformat = "%1$tR";
//			}
//			else {
//				// 秒ベース
//				int sspan = (60-calMin.get(Calendar.SECOND)) + (calMax.get(Calendar.SECOND) + 1) + (mspan>2 ? mspan-2 : 0) * 60;
//				addField = Calendar.SECOND;
//				step = (int)Math.ceil((double)sspan / (double)maxTicks);
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
//						calMin.get(Calendar.HOUR_OF_DAY), calMin.get(Calendar.MINUTE), calMin.get(Calendar.SECOND));
//				calformat = "%1$tT";
//			}
//		}
//		else if (calMin.compareTo(calMax) != 0) {
//			// 秒もしくはミリ秒が異なる
//			_unitLabel = String.format("%1$tY-%1$tb-%1$te %1$tR", calMin);
//			int sspan = calMax.get(Calendar.SECOND) - calMin.get(Calendar.SECOND) + 1;
//			if (sspan >= minTicks) {
//				// 秒ベース
//				addField = Calendar.SECOND;
//				step = (int)Math.ceil((double)sspan / (double)maxTicks);
//				calStep.set(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH), calMin.get(Calendar.DAY_OF_MONTH),
//						calMin.get(Calendar.HOUR_OF_DAY), calMin.get(Calendar.MINUTE), calMin.get(Calendar.SECOND));
//				calformat = "%1$tS";
//			}
//			else {
//				// ミリ秒ベース
//				int ispan = (int)(calMax.getTimeInMillis() - calMin.getTimeInMillis()) + 1;
//				addField = Calendar.MILLISECOND;
//				step = (int)Math.ceil((double)ispan / (double)maxTicks);
//				calStep.setTimeInMillis(calMin.getTimeInMillis());
//				calformat = "%1$tS.%1$tL";
//			}
//		}
//		else {
//			// same time
//			_unitLabel = String.format("%1$tY-%1$tb-%1$te %1$tR", calMin);
//			addField = Calendar.MILLISECOND;
//			step = 1;
//			calStep.setTimeInMillis(calMin.getTimeInMillis());
//			calformat = "%1$tS.%1$tL";
//		}
//		
//		// create ticks
//		if (calStep.compareTo(calMin) < 0) {
//			calStep.add(addField, 1);
//		}
//		if (fm != null) {
//			for (; calStep.compareTo(calMax) <= 0; ) {
//				String label = String.format(calformat, calStep);
//				double value = (double)calStep.getTimeInMillis();
//				_tickLabels.add(label);
//				_tickValues.add(value);
//				calStep.add(addField, step);
//				_maxTicksWidth = Math.max(_maxTicksWidth, fm.stringWidth(label));
//			}
//		}
//		else {
//			for (; calStep.compareTo(calMax) <= 0; ) {
//				String label = String.format(calformat, calStep);
//				double value = (double)calStep.getTimeInMillis();
//				_tickLabels.add(label);
//				_tickValues.add(value);
//				calStep.add(addField, step);
//			}
//		}
//	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
