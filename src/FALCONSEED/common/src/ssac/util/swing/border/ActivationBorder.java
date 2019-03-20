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
 * @(#)ActivationBorder.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * アクティブ、非アクティブの状態を示すボーダークラス。
 * このボーダーは、アクティブな状態を示すボーダーと非アクティブな状態を
 * 示すボーダーを切り替え可能な特殊なボーダーとなる。
 * このクラスには、アクティブな状態を示すボーダーと非アクティブな状態を
 * 示すボーダーを指定する。
 * オリジナルボーダーを指定した場合、アクティブボーダーと非アクティブボーダーに
 * 組み合わされる。
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.14
 */
public class ActivationBorder implements Border
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** オリジナルボーダー **/
	private final Border	_orgBorder;
	/** アクティブな状態を表すボーダー **/
	private final Border	_activatedBorder;
	/** 非アクティブな状態を示すボーダー **/
	private final Border	_deactivatedBorder;
	
	/** 現在描画対象のボーダー **/
	private Border	_selectedBorder;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたアクティブボーダーと非アクティブボーダーで、
	 * 新しいインスタンスを生成する。
	 * このコンストラクタでは、オリジナルボーダーは使用しない。
	 * @param activatedBorder	アクティブな状態を表すボーダー
	 * @param deactivatedBorder	非アクティブな状態を表すボーダー
	 * @throws IllegalArgumentException	どちらかの引数が <tt>null</tt> の場合
	 */
	public ActivationBorder(Border activatedBorder, Border deactivatedBorder) {
		if (activatedBorder == null)
			throw new IllegalArgumentException("activatedBorder argument is null!");
		if (deactivatedBorder == null)
			throw new IllegalArgumentException("deactivatedBorder argument is null!");
		this._activatedBorder = activatedBorder;
		this._deactivatedBorder = deactivatedBorder;
		this._orgBorder = null;
		this._selectedBorder = this._deactivatedBorder;
	}

	/**
	 * 指定されたアクティブボーダーと非アクティブボーダーで、新しいインスタンスを生成する。
	 * <p>
	 * オリジナルボーダーが <tt>null</tt> ではない場合、指定されたアクティブボーダー、
	 * 非アクティブボーダーが合成される。<code>activationForOuter</code> が <tt>true</tt> の場合は
	 * オリジナルボーダーは内側になるように合成され、<tt>false</tt> の場合はオリジナルボーダーが
	 * 外側になるように合成される。なお、非アクティブボーダーが <tt>null</tt> の場合は
	 * オリジナルボーダーがそのまま非アクティブボーダーとして使用される。<br>
	 * オリジナルボーダーが <tt>null</tt> の場合は、指定されたアクティブボーダーと
	 * 非アクティブボーダーがそのまま使用される。
	 * 
	 * @param activatedBorder		アクティブな状態を表すボーダー
	 * @param deactivatedBorder		非アクティブな状態を表すボーダー
	 * @param originalBorder		オリジナルボーダー
	 * @param activationForOuter	オリジナルボーダーを内側に合成する場合は <tt>true</tt>、
	 * 								外側に合成する場合は <tt>false</tt> を指定する。
	 * @throws IllegalArgumentException	<code>activatedBorder</code> が <tt>null</tt> の場合、
	 * 										もしくは、<code>deactivatedBorder</code> と <code>originalBorder</code> の
	 * 										どちらも <tt>null</tt> の場合
	 */
	public ActivationBorder(Border activatedBorder, Border deactivatedBorder,
							Border originalBorder, boolean activationForOuter)
	{
		if (activatedBorder == null)
			throw new IllegalArgumentException("activatedBorder argument is null!");
		if (deactivatedBorder == null && originalBorder == null)
			throw new IllegalArgumentException("deactivatedBorder and originalBorder arguments are null!");

		if (originalBorder == null) {
			// オリジナルは無し
			this._activatedBorder = activatedBorder;
			this._deactivatedBorder = deactivatedBorder;
		}else if (activationForOuter) {
			// アクティベーションは外側、オリジナルは内側
			if (deactivatedBorder == null) {
				this._activatedBorder = BorderFactory.createCompoundBorder(activatedBorder, originalBorder);
				this._deactivatedBorder = originalBorder;
			} else {
				this._activatedBorder = BorderFactory.createCompoundBorder(activatedBorder, originalBorder);
				this._deactivatedBorder = BorderFactory.createCompoundBorder(deactivatedBorder, originalBorder);
			}
		} else {
			// オリジナルは外側、アクティベーションは内側
			if (deactivatedBorder == null) {
				this._activatedBorder = BorderFactory.createCompoundBorder(activatedBorder, originalBorder);
				this._deactivatedBorder = originalBorder;
			} else {
				this._activatedBorder = BorderFactory.createCompoundBorder(activatedBorder, originalBorder);
				this._deactivatedBorder = BorderFactory.createCompoundBorder(deactivatedBorder, originalBorder);
			}
		}
		this._orgBorder = originalBorder;
		this._selectedBorder = this._deactivatedBorder;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public Border getOriginalBorder() {
		return _orgBorder;
	}
	
	public Border getActivatedBorder() {
		return _activatedBorder;
	}
	
	public Border getDeactivatedBorder() {
		return _deactivatedBorder;
	}
	
	public Border getSelectedBorder() {
		return _selectedBorder;
	}
	
	public boolean isActivatedBorderSelected() {
		return (_activatedBorder == _selectedBorder);
	}
	
	public boolean isDeactivatedBorderSelected() {
		return (_deactivatedBorder == _selectedBorder);
	}
	
	public void selectActivatedBorder() {
		_selectedBorder = _activatedBorder;
	}
	
	public void selectDeactivatedBorder() {
		_selectedBorder = _deactivatedBorder;
	}

	//------------------------------------------------------------
	// Implements Border interfaces
	//------------------------------------------------------------

	public Insets getBorderInsets(Component c) {
		return _selectedBorder.getBorderInsets(c);
	}

	public boolean isBorderOpaque() {
		return _selectedBorder.isBorderOpaque();
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		_selectedBorder.paintBorder(c, g, x, y, width, height);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
