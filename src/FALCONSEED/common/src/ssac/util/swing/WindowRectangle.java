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
 * @(#)WindowRectangle.java	3.2.2	2015/10/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ssac.util.Objects;

/**
 * ウィンドウの位置とサイズを保持するクラス。
 * <p>このクラスでは、{@link java.awt.Point} と {@link java.awt.Dimension} の
 * インスタンスを保持し、それぞれ <tt>null</tt> を保持することもできる。
 * <p><b>注意：</b>
 * <blockquote>
 * この実装は、同期化されていない。
 * </blockquote>
 * 
 * @version 3.2.2
 * @since 3.2.2
 */
public class WindowRectangle implements Shape, Cloneable, java.io.Serializable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 552572351445373712L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 領域の位置を示す値 **/
	private Point		_pt;
	/** 領域のサイズを示す値 **/
	private Dimension	_sz;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 位置およびサイズが未定義の、空の領域を生成する。
	 */
	public WindowRectangle() {
		_pt = null;
		_sz = null;
	}

	/**
	 * 指定された位置とサイズを持つ、新しい領域を生成する。
	 * @param p	設定する位置、もしくは <tt>null</tt>
	 * @param d	設定するサイズ、もしくは <tt>null</tt>
	 */
	public WindowRectangle(Point p, Dimension d) {
		_pt = (p==null ? null : new Point(p));
		_sz = (d==null ? null : new Dimension(d));
	}

	/**
	 * 指定された位置のみを持ち、サイズが未定義の、新しい領域を生成する。
	 * @param p	設定する位置、もしくは <tt>null</tt>
	 */
	public WindowRectangle(Point p) {
		_pt = (p==null ? null : new Point(p));
		_sz = null;
	}

	/**
	 * 指定されたサイズのみを持ち、位置が未定義の、新しい領域を生成する。
	 * @param d	設定するサイズ、もしくは <tt>null</tt>
	 */
	public WindowRectangle(Dimension d) {
		_pt = null;
		_sz = (d==null ? null : new Dimension(d));
	}

	/**
	 * 指定された領域と同じ、新しい領域を生成する。
	 * @param wrc	初期値となる領域
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public WindowRectangle(WindowRectangle wrc) {
		_pt = (wrc._pt==null ? null : new Point(wrc._pt));
		_sz = (wrc._sz==null ? null : new Dimension(wrc._sz));
	}

	/**
	 * 指定された領域と同じ、新しい領域を生成する。
	 * @param r	初期値となる領域
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public WindowRectangle(Rectangle r) {
	}

	/**
	 * 指定された領域と同じ、新しい領域を生成する。
	 * @param x	初期値となる領域の X 座標
	 * @param y	初期値となる領域の Y 座標
	 * @param w	初期値となる領域の幅
	 * @param h	初期値となる領域の高さ
	 */
	public WindowRectangle(int x, int y, int w, int h) {
		_pt = new Point(x, y);
		_sz = new Dimension(w, h);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * この領域の X 座標を <code>int</code> 精度で返す。
	 * @return	この領域の X 座標、位置に <tt>null</tt> がセットされている場合は 0
	 */
	public int getX() {
		return (_pt==null ? 0 : _pt.x);
	}

	/**
	 * この領域の Y 座標を <code>int</code> 精度で返す。
	 * @return	この領域の Y 座標、位置に <tt>null</tt> がセットされている場合は 0
	 */
	public int getY() {
		return (_pt==null ? 0 : _pt.y);
	}

	/**
	 * この領域の幅を <code>int</code> 精度で返す。
	 * @return	この領域の幅、サイズに <tt>null</tt> がセットされている場合は 0
	 */
	public int getWidth() {
		return (_sz==null ? 0 : _sz.width);
	}

	/**
	 * この領域の高さを <code>int</code> 精度で返す。
	 * @return	この領域の高さ、サイズに <tt>null</tt> がセットされている場合は 0
	 */
	public int getHeight() {
		return (_sz==null ? 0 : _sz.height);
	}

	/**
	 * この領域の X 座標の最小値を取得する。
	 * @return	この領域の X 座標の最小値、サイズが設定されていない場合は位置の X 座標、位置が設定されていない場合は 0
	 */
	public int getMinX() {
		if (_sz == null) {
			return getX();
		}
		else if (_pt == null) {
			return 0;
		}
		else if (_sz.width < 0) {
			return (_pt.x + _sz.width);
		}
		else {
			return _pt.x;
		}
	}

	/**
	 * この領域の X 座標の最大値を取得する。
	 * @return	この領域の X 座標の最大値、サイズが設定されていない場合は位置の X 座標、位置が設定されていない場合は 0
	 */
	public int getMaxX() {
		if (_sz == null) {
			return getX();
		}
		else if (_pt == null) {
			return 0;
		}
		else if (_sz.width < 0) {
			return _pt.x;
		}
		else {
			return (_pt.x + _sz.width);
		}
	}
	
	/**
	 * この領域の Y 座標の最小値を取得する。
	 * @return	この領域の Y 座標の最小値、サイズが設定されていない場合は位置の Y 座標、位置が設定されていない場合は 0
	 */
	public int getMinY() {
		if (_sz == null) {
			return getY();
		}
		else if (_pt == null) {
			return 0;
		}
		else if (_sz.height < 0) {
			return (_pt.y + _sz.height);
		}
		else {
			return _pt.y;
		}
	}
	
	/**
	 * この領域の Y 座標の最大値を取得する。
	 * @return	この領域の Y 座標の最大値、サイズが設定されていない場合は位置の Y 座標、位置が設定されていない場合は 0
	 */
	public int getMaxY() {
		if (_sz == null) {
			return getY();
		}
		else if (_pt == null) {
			return 0;
		}
		else if (_sz.height < 0) {
			return _pt.y;
		}
		else {
			return (_pt.y + _sz.height);
		}
	}

	/**
	 * この領域の位置を返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドが返すインスタンスは、このオブジェクトが保持するインスタンスそのものであるため、
	 * このメソッドが返すオブジェクトの内容を変更した場合は、このオブジェクトの内容も変更される。
	 * </blockquote>
	 * @return	このオブジェクトが保持する <code>Point</code> オブジェクト、位置が設定されていない場合は <tt>null</tt>
	 */
	public Point getLocation() {
		return _pt;
	}

	/**
	 * 指定された位置を、この領域に設定する。
	 * @param p	新しい位置、もしくは <tt>null</tt>
	 * @return	位置が変更された場合は <tt>true</tt>
	 */
	public boolean setLocation(Point p) {
		if (Objects.isEqual(_pt, p))
			return false;	// same value
		//--- modified
		if (p == null) {
			_pt = null;
		}
		else if (_pt == null) {
			_pt = new Point(p);
		}
		else {
			_pt.setLocation(p.x, p.y);
		}
		return true;
	}

	/**
	 * 指定された位置を、この領域に設定する。
	 * @param x	新しい位置の X 座標
	 * @param y	新しい位置の Y 座標
	 * @return	位置が変更された場合は <tt>true</tt>
	 */
	public boolean setLocation(int x, int y) {
		if (_pt != null && _pt.x==x && _pt.y==y)
			return false;	// same value
		_pt = new Point(x, y);
		return true;
	}

	/**
	 * この領域のサイズを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドが返すインスタンスは、このオブジェクトが保持するインスタンスそのものであるため、
	 * このメソッドが返すオブジェクトの内容を変更した場合は、このオブジェクトの内容も変更される。
	 * </blockquote>
	 * @return	このオブジェクトが保持する <code>Dimension</code> オブジェクト、サイズが設定されていない場合は <tt>null</tt>
	 */
	public Dimension getSize() {
		return _sz;
	}

	/**
	 * 指定されたサイズを、この領域に設定する。
	 * @param d	新しいサイズ、もしくは <tt>null</tt>
	 * @return	サイズが変更された場合は <tt>true</tt>
	 */
	public boolean setSize(Dimension d) {
		if (Objects.isEqual(_sz, d))
			return false;	// same value
		//--- modified
		if (d == null) {
			_sz = null;
		}
		else if (_sz == null) {
			_sz = new Dimension(d);
		}
		else {
			_sz.setSize(d.width, d.height);
		}
		return true;
	}

	/**
	 * 指定されたサイズを、この領域に設定する。
	 * @param w	新しいサイズの幅
	 * @param h	新しいサイズの高さ
	 * @return	サイズが変更された場合は <tt>true</tt>
	 */
	public boolean setSize(int w, int h) {
		if (_sz != null && _sz.width==w && _sz.height==h)
			return false;	// same value
		_sz = new Dimension(w, h);
		return true;
	}

	/**
	 * 指定された領域を、この領域として設定する。
	 * @param wrc	新しい領域
	 * @return	領域が変更された場合は <tt>true</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean setBounds(WindowRectangle wrc) {
		return (setLocation(wrc.getLocation()) || setSize(wrc.getSize()));
	}

	/**
	 * 指定された領域を、この領域として設定する。
	 * @param r	新しい領域
	 * @return	領域が変更された場合は <tt>true</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean setBounds(Rectangle r) {
		return (setLocation(r.x, r.y) || setSize(r.width, r.height));
	}

	/**
	 * 指定された位置とサイズを、この領域の位置とサイズとして設定する。
	 * @param p	新しい位置、もしくは <tt>null</tt>
	 * @param d	新しいサイズ、もしくは <tt>null</tt>
	 * @return	領域が変更された場合は <tt>true</tt>
	 */
	public boolean setBounds(Point p, Dimension d) {
		return (setLocation(p) || setSize(d));
	}

	/**
	 * 指定された位置とサイズを、この領域の位置とサイズとして設定する。
	 * @param x	新しい位置の X 座標
	 * @param y	新しい位置の Y 座標
	 * @param w	新しい幅
	 * @param h	新しい高さ
	 * @return	領域が変更された場合は <tt>true</tt>
	 */
	public boolean setBounds(int x, int y, int w, int h) {
		return (setLocation(x, y) || setSize(w, h));
	}

	/**
	 * この領域が空かどうかを判定する。
	 * 空とは、位置またはサイズが設定されていない、もしくは幅と高さのどちらかが 0 以下の場合となる。
	 * @return	空である場合は <tt>true</tt>
	 */
	public boolean isEmpty() {
		return (_pt == null || _sz == null || _sz.width <= 0 || _sz.height <= 0);
	}

	/**
	 * この領域が、指定された位置を含むかどうかを判定する。
	 * @param p	判定する位置
	 * @return	この領域が空ではなく、指定された位置が含まれる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}

	/**
	 * この領域が、指定された位置を含むかどうかを判定する。
	 * @param x	判定する位置の X 座標
	 * @param y	判定する位置の Y 座標
	 * @return	この領域が空ではなく、指定された位置が含まれる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean contains(int x, int y) {
		if (isEmpty())
			return false;
		
		if (x < _pt.x || y < _pt.y)
			return false;
		
		int mx = _pt.x + _sz.width;
		int my = _pt.y + _sz.height;
		return (mx > x && my > y);
	}

	/**
	 * この領域が、指定された領域を完全に含むかどうかを判定する。
	 * @param wrc	判定する領域
	 * @return	指定された領域とこの領域が空ではなく、指定された領域が完全に含まれる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean contains(WindowRectangle wrc) {
		if (wrc.isEmpty())
			return false;
		else
			return contains(wrc.getX(), wrc.getY(), wrc.getWidth(), wrc.getHeight());
	}

	/**
	 * この領域が、指定された領域を完全に含むかどうかを判定する。
	 * @param r	判定する領域
	 * @return	指定された領域とこの領域が空ではなく、指定された領域が完全に含まれる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean contains(Rectangle r) {
		return contains(r.x, r.y, r.width, r.height);
	}

	/**
	 * この領域が、指定された領域を完全に含むかどうかを判定する。
	 * @param x	判定する領域の X 座標
	 * @param y	判定する領域の Y 座標
	 * @param w	判定する領域の幅
	 * @param h	判定する領域の高さ
	 * @return	指定された領域とこの領域が空ではなく、指定された領域が完全に含まれる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean contains(int x, int y, int w, int h) {
		if (isEmpty())
			return false;
		if (w < 0 || h < 0)
			return false;

		// check left-top corner
		if (x < _pt.x || y < _pt.y)
			return false;
		
		// check right-bottom corner
		int mx = _pt.x + _sz.width;
		int my = _pt.y + _sz.height;
		int ix = x + w;
		int iy = y + h;
		return ((mx > x) && (my > y) && (mx >= ix) && (my >= iy));
	}

	/**
	 * この領域と指定された領域とが交差するかどうかを判定する。
	 * 共通部分が空ではない場合、2 つの領域は交差する。
	 * @param wrc	判定する領域
	 * @return	指定された領域とこの領域が交差する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean intersects(WindowRectangle wrc) {
		if (wrc.isEmpty())
			return false;
		return intersects(wrc._pt.x, wrc._pt.y, wrc._sz.width, wrc._sz.height);
	}

	/**
	 * この領域と指定された領域とが交差するかどうかを判定する。
	 * 共通部分が空ではない場合、2 つの領域は交差する。
	 * @param r	判定する領域
	 * @return	指定された領域とこの領域が交差する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean intersects(Rectangle r) {
		return intersects(r.x, r.y, r.width, r.height);
	}

	/**
	 * この領域と指定された領域とが交差するかどうかを判定する。
	 * 共通部分が空ではない場合、2 つの領域は交差する。
	 * @param x	判定する領域の X 座標
	 * @param y	判定する領域の Y 座標
	 * @param w	判定する領域の幅
	 * @param h	判定する領域の高さ
	 * @return	指定された領域とこの領域が交差する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean intersects(int x, int y, int w, int h) {
		if (isEmpty() || w <= 0 || h <= 0)
			return false;
		
		int tx = _pt.x;
		int ty = _pt.y;
		int tw = tx + _sz.width;
		int th = ty + _sz.height;
		
		int rw = x + w;
		int rh = y + h;

		return ((rw < x || rw > tx) && (rh < y || rh > ty) &&
				(tw < tx || tw > x) && (th < ty || th > y));
	}

	/**
	 * この領域と指定された領域との共通部分を算出し、2 つの領域の共通部分を表す新しい <code>WindowRectangle</code> を返す。
	 * 2 つの領域が交差しない場合、空の領域が返される。
	 * @param wrc	対象の領域
	 * @return	指定された領域とこの領域の共通部分を表す領域、交差しない場合は空の領域
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public WindowRectangle intersection(WindowRectangle wrc) {
		if (wrc.isEmpty())
			return new WindowRectangle();
		return intersection(wrc._pt.x, wrc._pt.y, wrc._sz.width, wrc._sz.height);
	}

	/**
	 * この領域と指定された領域との共通部分を算出し、2 つの領域の共通部分を表す新しい <code>WindowRectangle</code> を返す。
	 * 2 つの領域が交差しない場合、空の領域が返される。
	 * @param r	対象の領域
	 * @return	指定された領域とこの領域の共通部分を表す領域、交差しない場合は空の領域
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public WindowRectangle intersection(Rectangle r) {
		return intersection(r.x, r.y, r.width, r.height);
	}

	/**
	 * この領域と指定された領域との共通部分を算出し、2 つの領域の共通部分を表す新しい <code>WindowRectangle</code> を返す。
	 * 2 つの領域が交差しない場合、空の領域が返される。
	 * @param x	対象の領域の X 座標
	 * @param y	対象の領域の Y 座標
	 * @param w	対象の領域の幅
	 * @param h	対象の領域の高さ
	 * @return	指定された領域とこの領域の共通部分を表す領域、交差しない場合は空の領域
	 */
	public WindowRectangle intersection(int x, int y, int w, int h) {
		if (isEmpty() || w <= 0 || h <= 0)
			return new WindowRectangle();
		
		int tx1 = _pt.x;
		int ty1 = _pt.y;
		
		long tx2 = (long)tx1 + (long)_sz.width;
		long ty2 = (long)ty1 + (long)_sz.height;
		long rx2 = (long)x + (long)w;
		long ry2 = (long)y + (long)h;
		
		if (tx1 < x)   tx1 = x;
		if (ty1 < y)   ty1 = y;
		if (tx2 > rx2) tx2 = rx2;
		if (ty2 > ry2) ty2 = ry2;
		tx2 -= tx1;
		ty2 -= ty1;
		
		if (tx2 <= 0L || ty2 <= 0L)
			return new WindowRectangle();	// no intersection
		else
			return new WindowRectangle(tx1, ty1, (int)tx2, (int)ty2);
	}

	/**
	 * この領域と指定された領域との結合領域を算出し、その領域を表す新しい <code>WindowRectangle</code> を返す。
	 * <p>このメソッドでは、どちらか一方の領域が空の場合、もう一方の領域が結合領域となる。
	 * どちらも空の場合は、位置およびサイズが未定義の空の領域が結合領域となる。
	 * @param wrc	結合する領域
	 * @return	指定された領域とこの領域の両方が収まる最小の領域
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public WindowRectangle union(WindowRectangle wrc) {
		if (wrc.isEmpty()) {
			return (isEmpty() ? new WindowRectangle() : new WindowRectangle(this));
		}
		else {
			return union(wrc._pt.x, wrc._pt.y, wrc._sz.width, wrc._sz.height);
		}
	}
	
	/**
	 * この領域と指定された領域との結合領域を算出し、その領域を表す新しい <code>WindowRectangle</code> を返す。
	 * <p>このメソッドでは、どちらか一方の領域が空の場合、もう一方の領域が結合領域となる。
	 * どちらも空の場合は、位置およびサイズが未定義の空の領域が結合領域となる。
	 * @param wrc	結合する領域
	 * @return	指定された領域とこの領域の両方が収まる最小の領域
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public WindowRectangle union(Rectangle r) {
		return union(r.x, r.y, r.width, r.height);
	}

	/**
	 * この領域と指定された領域との結合領域を算出し、その領域を表す新しい <code>WindowRectangle</code> を返す。
	 * <p>このメソッドでは、どちらか一方の領域が空の場合、もう一方の領域が結合領域となる。
	 * どちらも空の場合は、位置およびサイズが未定義の空の領域が結合領域となる。
	 * @param x	結合する領域の X 座標
	 * @param y	結合する領域の Y 座標
	 * @param w	結合する領域の幅
	 * @param h	結合する領域の高さ
	 * @return	指定された領域とこの領域の両方が収まる最小の領域
	 */
	public WindowRectangle union(int x, int y, int w, int h) {
		if (isEmpty()) {
			return ((w <= 0 || h <= 0) ? new WindowRectangle() : new WindowRectangle(x, y, w, h));
		}
		else if (w <= 0 || h <= 0) {
			return new WindowRectangle(this);
		}
		
		// union
		int tx1 = _pt.x;
		int ty1 = _pt.y;
		
		long tx2 = (long)tx1 + (long)_sz.width;
		long ty2 = (long)ty1 + (long)_sz.height;
		long rx2 = (long)x + (long)w;
		long ry2 = (long)y + (long)h;
		
		if (tx1 > x)   tx1 = x;
		if (ty1 > y)   ty1 = y;
		if (tx2 < rx2) tx2 = rx2;
		if (ty2 < ry2) ty2 = ry2;
		tx2 -= tx1;
		ty2 -= ty1;
		if (tx2 > Integer.MAX_VALUE) tx2 = Integer.MAX_VALUE;
		if (ty2 > Integer.MAX_VALUE) ty2 = Integer.MAX_VALUE;
		
		return new WindowRectangle(tx1, ty1, (int)tx2, (int)ty2);
	}

	//------------------------------------------------------------
	// Implements Shape interfaces
	//------------------------------------------------------------

	/**
	 * この領域の境界を表す <code>Rectangle</code> を取得する。
	 * @return	新しい <code>Rectangle</code>
	 */
	@Override
	public Rectangle getBounds() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	/**
	 * この領域の境界を表す <code>Rectangle</code> を取得する。
	 * @return	新しい <code>Rectangle</code>
	 */
	@Override
	public Rectangle2D getBounds2D() {
		return getBounds();
	}

	@Override
	public boolean contains(double x, double y) {
		if (isEmpty())
			return false;
		
		double x0 = _pt.x;
		double y0 = _pt.y;
		double w0 = _sz.width;
		double h0 = _sz.height;
		return (x >= x0 && y >= y0 && x < x0 + w0 && y < y0 + h0);
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		if (isEmpty() || w <= 0 || h <= 0)
			return false;
		
		double x0 = _pt.x;
		double y0 = _pt.y;
		double w0 = _sz.width;
		double h0 = _sz.height;
		return (x + w > x0 && y + h > y0 && x < x0 + w0 && y < y0 + h0);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		if (isEmpty() || w <= 0 || h <= 0)
			return false;
		
		double x0 = _pt.x;
		double y0 = _pt.y;
		double w0 = _sz.width;
		double h0 = _sz.height;
		return (x >= x0 && y >= y0 && (x + w) <= x0 + w0 && (y + h) <= y0 + h0);
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return getBounds().getPathIterator(at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getBounds().getPathIterator(at, flatness);
	}

	//------------------------------------------------------------
	// Implements Object interfaces
	//------------------------------------------------------------

	@Override
	public WindowRectangle clone() {
		try {
			WindowRectangle wrc = (WindowRectangle)super.clone();
			wrc._pt = (Point)(this._pt==null ? null : this._pt.clone());
			wrc._sz = (Dimension)(this._sz==null ? null : this._sz.clone());
			return wrc;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public int hashCode() {
		int h = (_pt==null ? 0 : _pt.hashCode());
		h = 31 * h + (_sz==null ? 0 : _sz.hashCode());
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof WindowRectangle) {
			WindowRectangle wrc = (WindowRectangle)obj;
			if (Objects.isEqual(this._pt, wrc._pt) && Objects.isEqual(this._sz, wrc._sz)) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("[location=");
		sb.append(_pt);
		sb.append(",size=");
		sb.append(_sz);
		sb.append("]");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
