import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import exalge2.ExBase;
import exalge2.Exalge;

/**
 * 
 */

/**
 * @author ishizuka
 *
 */
public class TestGUI {
	private JFrame frame;
	private JPanel mainPanel;
	private JTextArea taExalge1;
	private JTextArea taExalge2;
	private JTextField tfValue;
	private JTextArea taResult;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestGUI gui = new TestGUI();
		gui.createFrame();
		gui.frame.setSize(800, 600);
		//gui.frame.pack();
		gui.frame.setVisible(true);
	}

	private void createFrame() {
		frame = new JFrame("交換代数コアモジュールのテスト");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// パネル
		JLabel lbl;
		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		mainPanel = new JPanel(layout);
		
		gbc.insets = new Insets(2,2,2,2);
		
		// ラベル
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		//--- 交換代数１
		gbc.gridx = 0;
		gbc.gridy = 0;
		lbl = new JLabel("Exalge 1：");
		mainPanel.add(lbl, gbc);
		//--- 交換代数２
		gbc.gridx = 0;
		gbc.gridy = 1;
		lbl = new JLabel("Exalge 2：");
		mainPanel.add(lbl, gbc);
		//--- 実数
		gbc.gridx = 0;
		gbc.gridy = 2;
		lbl = new JLabel("実数：");
		mainPanel.add(lbl, gbc);
		//--- 結果
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = 0;
		gbc.gridy = 3;
		lbl = new JLabel("結果：");
		mainPanel.add(lbl, gbc);
		
		// text
		JScrollPane scroll;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;
		gbc.weighty = 1;
		//--- 交換代数１
		gbc.gridx = 1;
		gbc.gridy = 0;
		taExalge1 = new JTextArea();
		taExalge1.setEditable(true);
		taExalge1.setLineWrap(true);
		scroll = new JScrollPane(taExalge1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(scroll, gbc);
		//--- 交換代数２
		gbc.gridx = 1;
		gbc.gridy = 1;
		taExalge2 = new JTextArea();
		taExalge2.setEditable(true);
		taExalge2.setLineWrap(true);
		scroll = new JScrollPane(taExalge2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(scroll, gbc);
		//--- 実数
		tfValue = new JTextField();
		tfValue.setEditable(true);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		gbc.gridx = 1;
		gbc.gridy = 2;
		mainPanel.add(tfValue, gbc);
		//--- 結果
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.fill = GridBagConstraints.BOTH;
		taResult = new JTextArea();
		taResult.setEditable(false);
		taResult.setLineWrap(true);
		scroll = new JScrollPane(taResult, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(scroll, gbc);
		
		// Buttons
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 5;
		gbc.gridx = 2;
		gbc.gridy = 0;
		mainPanel.add(createButtons(), gbc);
		
		frame.getContentPane().add(mainPanel);
	}
	
	private JComponent createButtons() {
		Dimension dmMax;
		Dimension dm;
		Box b = Box.createVerticalBox();
		JButton btn;
		//--- multi
		btn = new JButton("Exalge1 * 実数");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonMulti1();
			}
		});
		dmMax = btn.getPreferredSize();
		b.add(btn);
		//--- divide
		btn = new JButton("Exalge1 / 実数");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonDivide1();
			}
		});
		dm = btn.getPreferredSize();
		dmMax.setSize(Math.max(dm.width, dmMax.width), Math.max(dm.height, dmMax.height));
		b.add(btn);
		//--- hat
		btn = new JButton("^ Exalge1");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonHat1();
			}
		});
		dm = btn.getPreferredSize();
		dmMax.setSize(Math.max(dm.width, dmMax.width), Math.max(dm.height, dmMax.height));
		b.add(btn);
		//--- sum
		btn = new JButton("sum(Exalge1,Exalge2)");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonSum();
			}
		});
		dm = btn.getPreferredSize();
		dmMax.setSize(Math.max(dm.width, dmMax.width), Math.max(dm.height, dmMax.height));
		b.add(btn);
		//--- bar
		btn = new JButton("|Exalge1|");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonBar1();
			}
		});
		dm = btn.getPreferredSize();
		dmMax.setSize(Math.max(dm.width, dmMax.width), Math.max(dm.height, dmMax.height));
		b.add(btn);
		//--- @
		btn = new JButton("Exalge1 @ Exalge2");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonElementMulti();
			}
		});
		dm = btn.getPreferredSize();
		dmMax.setSize(Math.max(dm.width, dmMax.width), Math.max(dm.height, dmMax.height));
		b.add(btn);
		//--- 整形
		b.add(Box.createVerticalGlue());
		
		return b;
	}
	
	private void onButtonMulti1() {
		Exalge alge1 = getExalge1FromText();
		BigDecimal val = getValueFromText();
		
		if (alge1 == null || val == null) {
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("Exalge1:\n");
		sb.append(alge1.toFormattedString());
		sb.append("\n");
		sb.append("Exalge1 × ");
		sb.append(val);
		sb.append(" ＝\n");

		Exalge ret = null;
		try {
			ret = alge1.multiple(val);
			sb.append(ret.toFormattedString());
			sb.append("\n");
		} catch (Exception ex) {
			ex.printStackTrace();
			sb.append("エラー：" + ex.getMessage() + "\n");
		}
		
		taResult.setText(sb.toString());
	}
	
	private void onButtonDivide1() {
		Exalge alge1 = getExalge1FromText();
		BigDecimal val = getValueFromText();
		
		if (alge1 == null || val == null) {
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("Exalge1:\n");
		sb.append(alge1.toFormattedString());
		sb.append("\n");
		sb.append("Exalge1 ÷ ");
		sb.append(val);
		sb.append(" ＝\n");

		Exalge ret = null;
		try {
			ret = alge1.divide(val);
			sb.append(ret.toFormattedString());
			sb.append("\n");
		} catch (Exception ex) {
			ex.printStackTrace();
			sb.append("エラー：" + ex.getMessage() + "\n");
		}
		
		taResult.setText(sb.toString());
	}
	
	private void onButtonHat1() {
		Exalge alge1 = getExalge1FromText();
		if (alge1 == null) {
			return;
		}
		
		// Hat
		StringBuffer sb = new StringBuffer();
		
		sb.append("Exalge1:\n");
		sb.append(alge1.toFormattedString());
		sb.append("\n");
		sb.append("^ Exalge1 ＝\n");
		
		Exalge ret = null;
		try {
			ret = alge1.hat();
			sb.append(ret.toFormattedString());
			sb.append("\n");
		} catch (Exception ex) {
			ex.printStackTrace();
			sb.append("エラー：" + ex.getMessage() + "\n");
		}
		
		taResult.setText(sb.toString());
	}
	
	private void onButtonSum() {
		Exalge alge1 = getExalge1FromText();
		Exalge alge2 = getExalge2FromText();
		if (alge1 == null || alge2 == null) {
			return;
		}
		
		// sum(alge1, alge2)
		StringBuffer sb = new StringBuffer();
		
		sb.append("Exalge1:\n");
		sb.append(alge1.toFormattedString());
		sb.append("\n");
		sb.append("Exalge2:\n");
		sb.append(alge2.toFormattedString());
		sb.append("\n");
		sb.append("Exalge1 ＋ Exalge2 ＝\n");
		
		Exalge ret = null;
		try {
			ret = alge1.plus(alge2);
			sb.append(ret.toFormattedString());
			sb.append("\n");
		} catch (Exception ex) {
			ex.printStackTrace();
			sb.append("エラー：" + ex.getMessage() + "\n");
		}
		
		taResult.setText(sb.toString());
	}
	
	private void onButtonBar1() {
		Exalge alge1 = getExalge1FromText();
		if (alge1 == null) {
			return;
		}
		
		// bar
		StringBuffer sb = new StringBuffer();
		
		sb.append("Exalge1:\n");
		sb.append(alge1.toFormattedString());
		sb.append("\n");
		sb.append("|Exalge1| ＝\n");
		
		Exalge ret = null;
		try {
			ret = alge1.bar();
			sb.append(ret.toFormattedString());
			sb.append("\n");
		} catch (Exception ex) {
			ex.printStackTrace();
			sb.append("エラー：" + ex.getMessage() + "\n");
		}
		
		taResult.setText(sb.toString());
	}
	
	private void onButtonElementMulti() {
		Exalge alge1 = getExalge1FromText();
		Exalge alge2 = getExalge2FromText();
		if (alge1 == null || alge2 == null) {
			return;
		}
		
		// alge1 @ alge2
		StringBuffer sb = new StringBuffer();
		
		sb.append("Exalge1:\n");
		sb.append(alge1.toFormattedString());
		sb.append("\n");
		sb.append("Exalge2:\n");
		sb.append(alge2.toFormattedString());
		sb.append("\n");
		sb.append("Exalge1 ＠ Exalge2 ＝\n");
		
		Exalge ret = null;
		try {
			ret = alge1.elementMultiple(alge2);
			sb.append(ret.toFormattedString());
			sb.append("\n");
		} catch (Exception ex) {
			ex.printStackTrace();
			sb.append("エラー：" + ex.getMessage() + "\n");
		}
		
		taResult.setText(sb.toString());
	}
	
	private Exalge getExalge1FromText() {
		String text = taExalge1.getText();
		Exalge ret = null;
		try {
			ret = convertExalge(text);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(frame, "Exalge1 が、交換代数に変換できません");
		}
		return ret;
	}
	
	private Exalge getExalge2FromText() {
		String text = taExalge2.getText();
		Exalge ret = null;
		try {
			ret = convertExalge(text);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(frame, "Exalge2 が、交換代数に変換できません");
		}
		return ret;
	}
	
	private BigDecimal getValueFromText() {
		String text = tfValue.getText().replaceAll("\\s+", "");
		BigDecimal ret = null;
		try {
			ret = new BigDecimal(text);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(frame, "実数が数値ではありません");
		}
		return ret;
	}

	private Exalge convertExalge(String text) {
		String target = text.replaceAll("\\s+", "");
		String[] values = target.split("[<>]");
		
		Exalge ret = new Exalge();
		BigDecimal val = BigDecimal.ZERO;
		for (int i = 0; i < values.length; i++) {
			String str = values[i];
			if (str.indexOf(",") >= 0) {
				// 基底のはず
				ExBase base = convertExBase(str);
				ret = ret.plus(base, val);
				val = BigDecimal.ZERO;
			}
			else {
				// 実数値のはず
				val = new BigDecimal(str);
			}
		}
		return ret;
	}
	
	private ExBase convertExBase(String text) {
		String[] keys = text.split(",");

		ExBase ret;
		String nameKey = keys[0];
		String hatKey = keys[1];
		int num = keys.length - 2;
		if (num > 0) {
			String[] extkeys = new String[num];
			int k = 0;
			for (int i = 2; i < keys.length; i++) {
				extkeys[k] = keys[i];
				k++;
			}
			ret = new ExBase(nameKey, hatKey, extkeys);
		}
		else {
			ret = new ExBase(nameKey, hatKey);
		}
		return ret;
	}
}
