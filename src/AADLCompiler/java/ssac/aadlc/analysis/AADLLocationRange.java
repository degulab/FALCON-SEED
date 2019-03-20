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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AADLLocationRange.java	1.30	2009/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

/**
 * AADLコード位置の範囲情報
 * 
 * @version 1.30	2009/12/02
 * 
 * @since 1.30
 */
public final class AADLLocationRange implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final AADLLocation EMPTY = new AADLLocation();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private AADLLocation min;
	private AADLLocation max;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLLocationRange() {
		this(EMPTY, EMPTY);
	}
	
	public AADLLocationRange(AADLLocation min, AADLLocation max) {
		set(min, max);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 範囲が存在しない場合(min と max が等しい場合)に <tt>true</tt> を返す。
	 */
	public boolean isEmpty() {
		return (min.equals(max));
	}
	
	public void clear() {
		min = EMPTY;
		max = EMPTY;
	}
	
	public AADLLocation getMin() {
		return min;
	}
	
	public AADLLocation getMax() {
		return max;
	}
	
	public int getMinLine() {
		return min.line();
	}
	
	public int getMaxLine() {
		return max.line();
	}
	
	public int getMinCharPositionInLine() {
		return min.charPositionInLine();
	}
	
	public int getMaxCharPositionInLine() {
		return max.charPositionInLine();
	}
	
	public boolean contains(int lineNo, int charPosInLine) {
		if (min.line() > lineNo)
			return false;
		if (max.line() < lineNo)
			return false;
		
		if (min.line() == lineNo) {
			if (min.charPositionInLine() > charPosInLine)
				return false;
			if (max.line() == lineNo && max.charPositionInLine() < charPosInLine)
				return false;
		}
		else if (max.line() == lineNo) {
			if (max.charPositionInLine() < charPosInLine)
				return false;
			if (min.line() == lineNo && min.charPositionInLine() > charPosInLine)
				return false;
		}
		
		return true;
	}
	
	public boolean contains(AADLLocation loc) {
		return contains(loc.line(), loc.charPositionInLine());
	}
	
	public boolean intersects(AADLLocationRange range) {
		if (range.max.compareTo(this.min) < 0)
			return false;
		if (range.min.compareTo(this.max) > 0)
			return false;
		
		return true;
	}
	
	public AADLLocationRange intersection(AADLLocationRange range) {
		if (!intersects(range))
			return new AADLLocationRange();	// not intersect
		
		AADLLocation newMin = AADLLocation.max(this.min, range.min);
		AADLLocation newMax = AADLLocation.min(this.max, range.max);
		return new AADLLocationRange(newMin, newMax);
	}
	
	public AADLLocationRange union(AADLLocationRange range) {
		AADLLocation newMin = AADLLocation.min(this.min, range.min);
		AADLLocation newMax = AADLLocation.max(this.max, range.max);
		return new AADLLocationRange(newMin, newMax);
	}
	
	public void set(int minLineNo, int minCharPosInLine, int maxLineNo, int maxCharPosInLine) {
		AADLLocation lmin = new AADLLocation(minLineNo, minCharPosInLine);
		AADLLocation lmax = new AADLLocation(maxLineNo, maxCharPosInLine);
		
		if (lmax.compareTo(lmin) < 0) {
			this.min = lmax;
			this.max = lmin;
		} else {
			this.min = lmin;
			this.max = lmax;
		}
	}
	
	public void set(AADLLocation min, AADLLocation max) {
		if (min == null)
			throw new NullPointerException("min is null.");
		if (max == null)
			throw new NullPointerException("max is null.");
		if (max.compareTo(min) < 0) {
			this.min = max;
			this.max = min;
		} else {
			this.min = min;
			this.max = max;
		}
	}
	
	public boolean grow(int lineNo, int charPosInLine) {
		return grow(new AADLLocation(lineNo, charPosInLine));
	}
	
	public boolean grow(AADLLocation newLocation) {
		if (min.compareTo(newLocation) > 0) {
			this.min = newLocation;
			return true;
		}
		
		if (max.compareTo(newLocation) < 0) {
			this.max = newLocation;
			return true;
		}
		
		return false;
	}

	//------------------------------------------------------------
	// Implement Object interfaces
	//------------------------------------------------------------

	@Override
	protected AADLLocationRange clone() throws CloneNotSupportedException {
		try {
			AADLLocationRange v = (AADLLocationRange)super.clone();
			return v;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public int hashCode() {
		return (min.hashCode() ^ max.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof AADLLocationRange) {
			AADLLocationRange range = (AADLLocationRange)obj;
			if (range.min.equals(this.min) && range.max.equals(this.max)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		return (min.toString() + "-" + max.toString());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
