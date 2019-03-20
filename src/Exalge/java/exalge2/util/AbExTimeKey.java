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
 *  Copyright 2007-2008  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)AbExTimeKey.java	0.94	2008/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbExTimeKey.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.util;

import java.text.DecimalFormat;

/**
 * {@link ExTimeKey} インタフェースの共通の機能。
 * 
 * @version 0.94	2008/05/16
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.91
 */
public abstract class AbExTimeKey implements ExTimeKey
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final int minYear = 1;
	static protected final int maxYear = 9999;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected int year;
	protected int unit;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// implement ExTimeKey interfaces
	//------------------------------------------------------------
	
	public boolean isValidYear(int year) {
		if (getMinYear() <= year && year <= getMaxYear())
			return true;
		else
			return false;
	}

	public int getYear() {
		return this.year;
	}
	
	public int getMinYear() {
		return minYear;
	}
	
	public int getMaxYear() {
		return maxYear;
	}
	
	public int setYear(int year) {
		if (!isValidYear(year)) {
			throw new IllegalArgumentException("Year out of range");
		}
		
		this.year = year;
		return this.year;
	}
	
	public int addYear(int amount) {
		int newyear = this.year + amount;
		return setYear(newyear);
	}
	
	public boolean isValidUnit(int unit) {
		if (getMinUnit() <= unit && unit <= getMaxUnit())
			return true;
		else
			return false;
	}
	
	public int getUnit() {
		return this.unit;
	}
	
	public int setUnit(int unit) {
		if (!isValidUnit(unit)) {
			throw new IllegalArgumentException("Unit out of range");
		}
		
		this.unit = unit;
		return this.unit;
	}
	
	public int addUnit(int amount) {
		int newunit = this.unit + amount;
		return setUnit(newunit);
	}

	//------------------------------------------------------------
	// implement java.lang.Object interfaces
	//------------------------------------------------------------

	/**
	 * ハッシュコード値を返す。<br>
	 * ハッシュコード値は、基底の一意文字列キーのハッシュコード値に相当する。
	 * そのため、基底キーが同一であれば、ハッシュコード値も同一となる。
	 * 
	 * @return ハッシュコード値
	 *  
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (this.unit * 10000 + this.year);
	}
	
	/**
	 * 時間キーの値が等しいかどうかを検証する。
	 * <br>等しいとは、同一のフォーマットで値が等値のものを示す。
	 * 
	 * @param obj 検証する値の一方
	 * 
	 * @return 等値であれば <tt>true</tt>
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (this.getClass() == obj.getClass()) {
			if (this.year == ((AbExTimeKey)obj).year) {
				if (this.unit == ((AbExTimeKey)obj).unit) {
					return true;
				}
			}
		}
		
		// Not equal
		return false;
	}
	

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String toFormattedYearString() {
		return toFormattedString(this.year, "'Y'0000");
	}
	
	protected String toFormattedString(int value, String strFormat) {
		DecimalFormat dfm = new DecimalFormat(strFormat);
		return dfm.format(value);
	}
}
