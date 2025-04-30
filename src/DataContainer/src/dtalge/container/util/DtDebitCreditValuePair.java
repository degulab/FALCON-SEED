package dtalge.container.util;

import java.math.BigDecimal;

import exalge2.ExBase;

/**
 * 借方と貸方のペアとして、交換代数基底と値の組を保持するクラス。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtDebitCreditValuePair
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 借方の値、設定されていない場合は <code>null</code> **/
	protected BigDecimal	_debitValue;
	/** 借方の交換代数基底、設定されていない場合は <code>null</code> **/
	protected ExBase		_debitBase;
	/** 貸方の値、設定されていない場合は <code>null</code> **/
	protected BigDecimal	_creditValue;
	/** 貸方の交換代数基底、設定されていない場合は <code>null</code> **/
	protected ExBase		_creditBase;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtDebitCreditValuePair()
	{
		_debitValue = null;
		_debitBase = null;
		_creditValue = null;
		_creditBase = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * すべての要素をクリアする。
	 */
	public void clearAllItems() {
		clearDebitItem();
		clearCreditItem();
	}
	
	/**
	 * 借方の要素のみをクリアする。
	 */
	public void clearDebitItem() {
		_debitValue = null;
		_debitBase = null;
	}
	
	/**
	 * 貸方の要素のみをクリアする。
	 */
	public void clearCreditItem() {
		_creditValue = null;
		_creditBase = null;
	}
	
	/**
	 * 借方の交換代数基底と値の組が保持されているかを判定する。
	 * @return	借方の交換代数基底と値の組が保持されている場合は true、それ以外の場合は false
	 */
	public boolean hasDebitItem() {
		return (_debitBase != null);
	}
	
	/**
	 * 貸方の交換代数基底と値の組が保持されているかを判定する。
	 * @return	貸方の交換代数基底と値の組が保持されている場合は true、それ以外の場合は false
	 */
	public boolean hasCreditItem() {
		return (_creditBase != null);
	}
	
	/**
	 * 借方・貸方のいずれかの交換代数基底と値の組が保持されているかを判定する。
	 * @return	借方・貸方のいずれかの交換代数基底と値の組が保持されている場合は true、それ以外の場合は false
	 */
	public boolean hasAnyItem() {
		return (_debitBase != null || _creditBase != null);
	}
	
	/**
	 * 借方・貸方の両方の交換代数基底と値の組が保持されているかを判定する。
	 * @return	借方・貸方の両方の交換代数基底と値の組が保持されている場合は true、それ以外の場合は false
	 */
	public boolean hasBothItems() {
		return (_debitBase != null && _creditBase != null);
	}
	
	/**
	 * <em>debitSide</em> に指定された側の交換代数基底と値の組が保持されているかを判定する。
	 * @param debitSide	debitSide	true の場合は借方、false の場合は貸方
	 * @return	保持されている場合は true、それ以外の場合は false
	 */
	public boolean hasItemBySide(boolean debitSide) {
		if (debitSide) {
			return (_debitBase != null);
		}
		else {
			return (_creditBase != null);
		}
	}
	
	/**
	 * 借方要素の値を取得する。
	 * @return	借方要素の値、設定されていない場合は <code>null</code>
	 */
	public BigDecimal getDebitValue() {
		return _debitValue;
	}
	
	/**
	 * 借方要素の交換代数基底を取得する。
	 * @return	借方要素の交換代数基底、設定されていない場合は <code>null</code>
	 */
	public ExBase getDebitBase() {
		return _debitBase;
	}
	
	/**
	 * 貸方要素の値を取得する。
	 * @return	貸方要素の値、設定されていない場合は <code>null</code>
	 */
	public BigDecimal getCreditValue() {
		return _creditValue;
	}
	
	/**
	 * 貸方要素の交換代数基底を取得する。
	 * @return	貸方要素の交換代数基底、設定されていない場合は <code>null</code>
	 */
	public ExBase getCreditBase() {
		return _creditBase;
	}

	/**
	 * 借方の要素として、交換代数基底と値の組を設定する。
	 * @param value	値
	 * @param base	交換代数基底
	 * @throws NullPointerException	<em>base</em> が <code>null</code> の場合
	 */
	public void setDebitItem(BigDecimal value, ExBase base) {
		if (base == null)
			throw new NullPointerException();
		
		_debitValue = value;
		_debitBase  = base;
	}
	
	/**
	 * 貸方の要素として、交換代数基底と値の組を設定する。
	 * @param value	値
	 * @param base	交換代数基底
	 * @throws NullPointerException	<em>base</em> が <code>null</code> の場合
	 */
	public void setCreditItem(BigDecimal value, ExBase base) {
		if (base == null)
			throw new NullPointerException();
		
		_creditValue = value;
		_creditBase  = base;
	}
	
	/**
	 * <em>debitSide</em> の指定に従い、交換代数基底と値の組を設定する。
	 * なお、このメソッドでは交換代数基底のハットの有無を無視する。
	 * @param debitSide	true の場合は借方、false の場合は貸方
	 * @param value	値
	 * @param base	交換代数基底
	 * @throws NullPointerException	<em>base</em> が <code>null</code> の場合
	 */
	public void setItemBySide(boolean debitSide, BigDecimal value, ExBase base) {
		if (base == null)
			throw new NullPointerException();

		if (debitSide) {
			// debit
			_debitValue = value;
			_debitBase  = base;
		}
		else {
			// credit
			_creditValue = value;
			_creditBase  = base;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
