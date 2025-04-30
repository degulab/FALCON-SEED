package exalgetest01;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/*
 * 交換代数テストモジュールを実行するためのメインクラス。
 */
public class Test01Main {

	/*
	 * テストモジュールのランチャ
	 * テストの入出力ファイルを指定
	 */
	static private void testRun() {
		// テスト
		boolean result;
		
		System.out.println("<<< Test start! >>>");
		
		Test01 test = new Test01();
		
		//--- test01
		System.out.println("test01 started");
		final String inTest01a = "testdata/test01apple.csv";
		final String inTest01b = "testdata/test01orange.csv";
		final String inTest01c = "testdata/test01banana.csv";
		final String outTest01 = "testdata/test01out.csv";
		result = test.test01(inTest01a, inTest01b, inTest01c, outTest01);
		System.out.println("---> finished [" + result + "]");
		
		//--- test02
		System.out.println("test02 started");
		final String bases = "testdata/test01bases.csv";
		final String outTest02 = "testdata/test02out.csv";
		result = test.test02(outTest01, bases, outTest02);
		System.out.println("---> finished [" + result + "]");
		
		//--- test03
		System.out.println("test03 started");
		final String inTable = "testdata/test01transtable.csv";
		final String outTest03 = "testdata/test03out.csv";
		result = test.test03(outTest02, inTable, outTest03);
		System.out.println("---> finished [" + result + "]");
		
		//--- test04
		System.out.println("test04 started");
		final String outTest04 = "testdata/test04out.csv";
		result = test.test04(outTest03, outTest04);
		System.out.println("---> finished [" + result + "]");
		
		System.out.println("<<< End of Test >>>");
		
		JOptionPane.showMessageDialog(null, "Test finished!");
		System.exit(0);	// プログラム終了
	}

	/*
	 * エントリメソッド
	 */
	public static void main(String[] args) {
		// テスト準備
		JFrame frame = new JFrame("テスト01");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel label = new JLabel("テスト実行中...");
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.addComponentListener(new ComponentListener(){
		    public void componentResized(ComponentEvent e) {}
		    public void componentMoved(ComponentEvent e) {}
		    public void componentShown(ComponentEvent e) {
		    	testRun();
		    }
		    public void componentHidden(ComponentEvent e) {}
			
		});
		frame.pack();
		frame.setVisible(true);
	}

}
