/*
 * @(#)BigIterator.java	0.991	2019/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)BigIterator.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.db;

import java.util.Iterator;

/**
 * 大容量データの一要素イテレータのインタフェース。
 * <p>このインスタンスの利用後、極力 {@link #closeCursor()} を呼び出すこと。
 * 
 * @version 0.991
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigIterator<TElem> extends Iterator<TElem>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトが管理するカーソルを破棄する。
	 * <p>このメソッドの呼び出し以降、要素にはアクセスできないので注意。
	 */
	public void closeCursor();
}
