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
 * @(#)JMaskedNumberSpinner.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatterFactory;

/**
 * 数値入力用スピンコントロール。
 * このクラスの現在の実装では、コンストラクタで指定したパラメータでコンポーネントの振る舞いが決定される。
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.14
 */
public class JMaskedNumberSpinner extends JSpinner
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準的な設定で <code>JMaskedNumberSpinner</code> を生成する。
	 */
	public JMaskedNumberSpinner() {
		super(new SpinnerNumberModel());
		setup(null);
	}

	/**
	 * 指定されたフォーマットで、<code>JMaskedNumberSpinner</code> を生成する。
	 * @param decimalFormatPattern	非地域対応のパターン文字列
	 * @see java.text.DecimalFormat#DecimalFormat(String)
	 */
	public JMaskedNumberSpinner(String decimalFormatPattern) {
		this(new DecimalFormat(decimalFormatPattern));
	}

	/**
	 * 指定されたフォーマットで、<code>JMaskedNumberSpinner</code> を生成する。
	 * @param format	数値フォーマットとする <code>DecimalFormat</code>
	 */
	public JMaskedNumberSpinner(DecimalFormat format) {
		super();
		setup(format);
	}

	/**
	 * 指定された数値範囲の <code>JMaskedNumberSpinner</code> を生成する。
	 * @param value		現在値
	 * @param minimum	最小値
	 * @param maximum	最大値
	 * @param stepSize	数値間隔
	 * @see javax.swing.SpinnerNumberModel#SpinnerNumberModel(Number, Comparable, Comparable, Number)
	 */
	public JMaskedNumberSpinner(Number value, Comparable minimum, Comparable maximum, Number stepSize) {
		this((DecimalFormat)null, value, minimum, maximum, stepSize);
	}
	
	/**
	 * 指定された数値範囲の <code>JMaskedNumberSpinner</code> を生成する。
	 * @param value		現在値
	 * @param minimum	最小値
	 * @param maximum	最大値
	 * @param stepSize	数値間隔
	 * @see javax.swing.SpinnerNumberModel#SpinnerNumberModel(int, int, int, int)
	 */
	public JMaskedNumberSpinner(int value, int minimum, int maximum, int stepSize) {
		this((DecimalFormat)null, value, minimum, maximum, stepSize);
	}
	
	/**
	 * 指定されたフォーマットで、指定された数値範囲の <code>JMaskedNumberSpinner</code> を生成する。
	 * @param decimalFormatPattern	非地域対応のパターン文字列
	 * @param value		現在値
	 * @param minimum	最小値
	 * @param maximum	最大値
	 * @param stepSize	数値間隔
	 * @see java.text.DecimalFormat#DecimalFormat(String)
	 * @see javax.swing.SpinnerNumberModel#SpinnerNumberModel(Number, Comparable, Comparable, Number)
	 */
	public JMaskedNumberSpinner(String decimalFormatPattern, Number value, Comparable minimum, Comparable maximum, Number stepSize) {
		this(new DecimalFormat(decimalFormatPattern), value, minimum, maximum, stepSize);
	}
	
	/**
	 * 指定されたフォーマットで、指定された数値範囲の <code>JMaskedNumberSpinner</code> を生成する。
	 * @param decimalFormatPattern	非地域対応のパターン文字列
	 * @param value		現在値
	 * @param minimum	最小値
	 * @param maximum	最大値
	 * @param stepSize	数値間隔
	 * @see java.text.DecimalFormat#DecimalFormat(String)
	 * @see javax.swing.SpinnerNumberModel#SpinnerNumberModel(int, int, int, int)
	 */
	public JMaskedNumberSpinner(String decimalFormatPattern, int value, int minimum, int maximum, int stepSize) {
		this(new DecimalFormat(decimalFormatPattern), value, minimum, maximum, stepSize);
	}
	
	/**
	 * 指定されたフォーマットで、指定された数値範囲の <code>JMaskedNumberSpinner</code> を生成する。
	 * @param format	数値フォーマットとする <code>DecimalFormat</code>
	 * @param value		現在値
	 * @param minimum	最小値
	 * @param maximum	最大値
	 * @param stepSize	数値間隔
	 * @see java.text.DecimalFormat#DecimalFormat(String)
	 * @see javax.swing.SpinnerNumberModel#SpinnerNumberModel(Number, Comparable, Comparable, Number)
	 */
	public JMaskedNumberSpinner(DecimalFormat format, Number value, Comparable minimum, Comparable maximum, Number stepSize) {
		super(new SpinnerNumberModel(value, minimum, maximum, stepSize));
		setup(format);
	}
	
	/**
	 * 指定されたフォーマットで、指定された数値範囲の <code>JMaskedNumberSpinner</code> を生成する。
	 * @param format	数値フォーマットとする <code>DecimalFormat</code>
	 * @param value		現在値
	 * @param minimum	最小値
	 * @param maximum	最大値
	 * @param stepSize	数値間隔
	 * @see java.text.DecimalFormat#DecimalFormat(String)
	 * @see javax.swing.SpinnerNumberModel#SpinnerNumberModel(int, int, int, int)
	 */
	public JMaskedNumberSpinner(DecimalFormat format, int value, int minimum, int maximum, int stepSize) {
		super(new SpinnerNumberModel(value, minimum, maximum, stepSize));
		setup(format);
	}

	protected JMaskedNumberSpinner(SpinnerModel model) {
		super(model);
		setup(null);
	}

	/**
	 * 指定されたフォーマットで、内部エディタを初期化する。
	 * @param format	数値フォーマットとする <code>DecimalFormat</code>
	 */
	protected void setup(DecimalFormat format) {
		// setup Editor
		MaskedNumberEditor editor;
		if (format == null) {
			editor = new MaskedNumberEditor(this);
		} else {
			editor = new MaskedNumberEditor(this, format);
		}
		setEditor(editor);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	@Override
	protected JComponent createEditor(SpinnerModel model) {
		if (model instanceof SpinnerNumberModel) {
			return new MaskedNumberEditor(this);
		} else {
			return super.createEditor(model);
		}
	}

	/**
	 * スピンコントロールのテキストエディタと連動するフォーマッター
	 */
    static private class MaskedNumberEditorFormatter extends MaskedNumberFormatter
    {
    	private final SpinnerNumberModel model;
    	
    	MaskedNumberEditorFormatter(SpinnerNumberModel model) {
    		super();
    		this.model = model;
    		setValueClass(model.getValue().getClass());
    	}
    	
    	MaskedNumberEditorFormatter(SpinnerNumberModel model, DecimalFormat format) {
    		super(format);
    		this.model = model;
    		setValueClass(model.getValue().getClass());
    	}

    	public void setMinimum(Comparable min) {
    		model.setMinimum(min);
    	}

    	public Comparable getMinimum() {
    		return  model.getMinimum();
    	}

    	public void setMaximum(Comparable max) {
    		model.setMaximum(max);
    	}

    	public Comparable getMaximum() {
    		return model.getMaximum();
    	}
    }

    /**
     * スピンコントロールのテキストエディタの実装
     */
	static protected class MaskedNumberEditor extends DefaultEditor
	{
		public MaskedNumberEditor(JSpinner spinner) {
			this(spinner, new DecimalFormat("#0"));
		}
		
		public MaskedNumberEditor(JSpinner spinner, DecimalFormat format) {
			super(spinner);
			if (!(spinner.getModel() instanceof SpinnerNumberModel)) {
				throw new IllegalArgumentException("model not a SpinnerNumberModel");
			}
			
			SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
			MaskedNumberEditorFormatter formatter = new MaskedNumberEditorFormatter(model, format);
			
			DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
			JFormattedTextField ftf = getTextField();
			ftf.setEditable(true);
			ftf.setFormatterFactory(factory);
			ftf.setHorizontalAlignment(JTextField.RIGHT);
			
			try {
				String minString = formatter.valueToString(model.getMinimum());
				String maxString = formatter.valueToString(model.getMaximum());
				ftf.setColumns(Math.max(maxString.length(), minString.length()));
			}
			catch (ParseException ex) {
				ex = null;
			}
		}
		
		public DecimalFormat getFormat() {
			return (DecimalFormat)((MaskedNumberEditorFormatter)(getTextField().getFormatter())).getFormat();
		}
		
		public SpinnerNumberModel getModel() {
			return (SpinnerNumberModel)(getSpinner().getModel());
		}
	}
}
