/**
 * 
 */
package exalgetest01;

import java.io.File;
import java.util.Iterator;

import javax.swing.JOptionPane;

import exalge2.ExAlgeSet;
import exalge2.ExBaseSet;
import exalge2.Exalge;
import exalge2.TransTable;
import exalge2.io.csv.CsvFormatException;

/*
 * 交換代数テストモジュール (Test01)
 * 
 * 簡単なテストケースを実装。
 *   ・test01 - 時間キーのみの置換を行う。置換結果を単一のファイルに出力。
 *   ・test02 - 基底集合ファイルに指定された基底集合にてプロジェクションを行う。結果を単一のファイルに出力。
 *   ・test03 - 振替変換テーブルにより、交換代数集合を振替変換する。結果を単一のファイルに出力。
 *   ・test04 - 交換代数集合に対して、sum()、bar演算を行う。結果を単一のファイルに出力。
 *
 */
public class Test01 {
	
	public Test01() {
		super();
	}
	
	private void appendToReadError(StringBuffer sb, String filename, CsvFormatException ex) {
		sb.append("Failed to read from ");
		sb.append(filename);
		sb.append("! (factor[");
		sb.append(ex.getIllegalFactor());
		sb.append("] / line[");
		sb.append(ex.getLineNumber());
		sb.append("] / column[");
		sb.append(ex.getColumnNumber());
		sb.append("]\n");
	}
	
	private void appendToReadError(StringBuffer sb, String filename, Exception ex) {
		sb.append("Failed to read from ");
		sb.append(filename);
		sb.append("! [");
		sb.append(ex.getMessage());
		sb.append("]\n");
	}
	
	private void appendToWriterError(StringBuffer sb, String filename, Exception ex) {
		sb.append("Failed to write to ");
		sb.append(filename);
		sb.append("! [");
		sb.append(ex.getMessage());
		sb.append("]\n");
	}
	
	private void showMessage(String title, StringBuffer sb) {
		if (title != null && title.length() > 0) {
			JOptionPane.showMessageDialog(null, sb.toString(), title, JOptionPane.PLAIN_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, sb.toString());
		}
	}
	
	/**
	 * ［テストケース01］
	 * 交換代数集合の全基底について、時間キーを置換する。
	 * テストのため、置換後の時間キーは "Y2006"固定としている。
	 * 
	 * @param inFile1	入力する交換代数集合のCSVファイル名(パス含む)を指定する。
	 * @param inFile2	入力する交換代数集合のCSVファイル名(パス含む)を指定する。
	 * @param inFile3	入力する交換代数集合のCSVファイル名(パス含む)を指定する。
	 * @param outFile	置換結果となる交換代数集合の出力先CSVファイル名(パス含む)を指定する。
	 */
	public boolean test01(String inFile1, String inFile2, String inFile3, String outFile) {
		StringBuffer sb = new StringBuffer();
		sb.append("exalgetest01.Test01.test01(inFile1, inFile2, inFile3, outFile) running...\n");
		sb.append("   - inFile1 : \"" + inFile1 + "\"\n");
		sb.append("   - inFile2 : \"" + inFile2 + "\"\n");
		sb.append("   - inFile3 : \"" + inFile3 + "\"\n");
		sb.append("   - outFile : \"" + outFile + "\"\n");
		sb.append("\n");
		
		// read inFile1
		ExAlgeSet set1 = null;
		try {
			set1 = ExAlgeSet.fromCSV(new File(inFile1));
		}
		catch (CsvFormatException ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile1", ex);
			showMessage("test01", sb);
			return false;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile1", ex);
			showMessage("test01", sb);
			return false;
		}

		// read inFile2
		ExAlgeSet set2 = null;
		try {
			set2 = ExAlgeSet.fromCSV(new File(inFile2));
		}
		catch (CsvFormatException ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile2", ex);
			showMessage("test01", sb);
			return false;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile2", ex);
			showMessage("test01", sb);
			return false;
		}
		
		// read inFile3
		ExAlgeSet set3 = null;
		try {
			set3 = ExAlgeSet.fromCSV(new File(inFile3));
		}
		catch (CsvFormatException ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile3", ex);
			showMessage("test01", sb);
			return false;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile3", ex);
			showMessage("test01", sb);
			return false;
		}
		
		sb.append("replace time key to \"Y2006\"...");

		// 時間キーを置換
		final String Y2006 = "Y2006";
		Exalge alge;
		ExAlgeSet ret = new ExAlgeSet();
		alge = set1.sum();
		alge = alge.replaceTimeKey(Y2006);
		ret.add(alge);
		alge = set2.sum();
		alge = alge.replaceTimeKey(Y2006);
		ret.add(alge);
		alge = set3.sum();
		alge = alge.replaceTimeKey(Y2006);
		ret.add(alge);
		
		sb.append("OK!\n");
	
		// 結果をCSVファイルに出力
		try {
			ret.toCSV(new File(outFile));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToWriterError(sb, "outFile", ex);
			showMessage("test01", sb);
			return false;
		}
		
		sb.append("\n...completed!");
		showMessage("test01", sb);
		
		return true;
	}
	
	// プロジェクション
	/**
	 * ［テストケース02］
	 * 交換代数集合に対し、指定された基底集合でプロジェクションを行う。
	 * 
	 * @param inFile	入力する交換代数集合のCSVファイル名(パス含む)を指定する。
	 * @param basesetFile	入力する基底集合のCSVファイル名(パス含む)を指定する。
	 * @param outFile	演算結果となる交換代数集合の出力先CSVファイル名(パス含む)を指定する。
	 */
	public boolean test02(String inFile, String basesetFile, String outFile) {
		StringBuffer sb = new StringBuffer();
		sb.append("exalgetest01.Test01.test02(inFile, basesetFile, outFile) running...\n");
		sb.append("   - inFile : \"" + inFile + "\"\n");
		sb.append("   - basesetFile : \"" + basesetFile + "\"\n");
		sb.append("   - outFile : \"" + outFile + "\"\n");
		sb.append("\n");

		// read inFile
		ExAlgeSet	set;
		try {
			set = ExAlgeSet.fromCSV(new File(inFile));
		}
		catch (CsvFormatException ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile", ex);
			showMessage("test02", sb);
			return false;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile", ex);
			showMessage("test02", sb);
			return false;
		}

		// read basesetFile
		ExBaseSet	bases;
		try {
			bases = ExBaseSet.fromCSV(new File(basesetFile));
		}
		catch (CsvFormatException ex) {
			ex.printStackTrace();
			appendToReadError(sb, "basesetFile", ex);
			showMessage("test02", sb);
			return false;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToReadError(sb, "basesetFile", ex);
			showMessage("test02", sb);
			return false;
		}
		
		sb.append("outFile = Proj[basesetFile](inFile)...");
		
		// プロジェクション
		ExAlgeSet ret = set.projection(bases);
		
		sb.append("OK!\n");

		// 結果をCSVファイルに出力
		try {
			ret.toCSV(new File(outFile));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToWriterError(sb, "outFile", ex);
			showMessage("test02", sb);
			return false;
		}
		
		sb.append("\n...completed!");
		showMessage("test02", sb);
		
		return true;
	}

	// 振替変換
	/**
	 * ［テストケース03］
	 * 交換代数集合に対し、指定された振替変換テーブルで振替変換を行う。
	 * 
	 * @param inFile	入力する交換代数集合のCSVファイル名(パス含む)を指定する。
	 * @param tableFile	入力する振替変換テーブルのCSVファイル名(パス含む)を指定する。
	 * @param outFile	演算結果となる交換代数集合の出力先CSVファイル名(パス含む)を指定する。
	 */
	public boolean test03(String inFile, String tableFile, String outFile) {
		StringBuffer sb = new StringBuffer();
		sb.append("exalgetest01.Test01.test03(inFile, tableFile, outFile) running...\n");
		sb.append("   - inFile : \"" + inFile + "\"\n");
		sb.append("   - tableFile : \"" + tableFile + "\"\n");
		sb.append("   - outFile : \"" + outFile + "\"\n");
		sb.append("\n");

		// read inFile
		ExAlgeSet	set;
		try {
			set = ExAlgeSet.fromCSV(new File(inFile));
		}
		catch (CsvFormatException ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile", ex);
			showMessage("test03", sb);
			return false;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile", ex);
			showMessage("test03", sb);
			return false;
		}

		// read basesetFile
		TransTable table;
		try {
			table = TransTable.fromCSV(new File(tableFile));
		}
		catch (CsvFormatException ex) {
			ex.printStackTrace();
			appendToReadError(sb, "tableFile", ex);
			showMessage("test03", sb);
			return false;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToReadError(sb, "tableFile", ex);
			showMessage("test03", sb);
			return false;
		}
		
		sb.append("aggregation inFile by tableFile...");
		
		// 振替
		ExAlgeSet ret = new ExAlgeSet();
		Iterator<Exalge> it = set.iterator();
		while (it.hasNext()) {
			Exalge salge = it.next();
			Exalge dalge = salge.aggreTransfer(table);
			//System.out.println("Transfered [" + dalge.toFormattedString() + "]");
			ret.add(dalge);
		}
		
		sb.append("OK!\n");

		// 結果をCSVファイルに出力
		try {
			ret.toCSV(new File(outFile));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToWriterError(sb, "outFile", ex);
			showMessage("test03", sb);
			return false;
		}
		
		sb.append("\n...completed!");
		showMessage("test03", sb);
		
		return true;
	}

	// Sum -> Bar演算
	/**
	 * ［テストケース04］
	 * 交換代数集合に対して、sum演算、bar演算を実行する。
	 * 
	 * @param inFile	入力する交換代数集合のCSVファイル名(パス含む)を指定する。
	 * @param outFile	演算結果となる交換代数集合の出力先CSVファイル名(パス含む)を指定する。
	 */
	public boolean test04(String inFile, String outFile) {
		StringBuffer sb = new StringBuffer();
		sb.append("exalgetest01.Test01.test04(inFile, outFile) running...\n");
		sb.append("   - inFile : \"" + inFile + "\"\n");
		sb.append("   - outFile : \"" + outFile + "\"\n");
		sb.append("\n");

		// read inFile
		ExAlgeSet	inSet;
		try {
			inSet = ExAlgeSet.fromCSV(new File(inFile));
		}
		catch (CsvFormatException ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile", ex);
			showMessage("test04", sb);
			return false;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToReadError(sb, "inFile", ex);
			showMessage("test04", sb);
			return false;
		}
		
		sb.append("outFile = ~(sum(inFile))...");
		
		// Sum -> Bar
		Exalge alge = inSet.sum();
		Exalge ret = alge.bar();
		
		sb.append("OK!\n");
		
		// 結果をCSVファイルに出力する
		try {
			ret.toCSV(new File(outFile));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			appendToWriterError(sb, "outFile", ex);
			showMessage("test04", sb);
			return false;
		}
		
		sb.append("\n...completed!");
		showMessage("test04", sb);
		
		return true;
	}
}
