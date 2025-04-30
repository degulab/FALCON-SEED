/*
 * @(#)LibsInJarInfo.java	4.0.0	2021/08/27 : for Java11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;

/**
 * コンパイラーが生成する Jar ファイルに含めるライブラリ情報を保持するクラス。
 * <blockquote>
 * このクラスの実装は、変更が影響しないよう完全なクローンに対応する。
 * </blockquote>
 * 
 * @version 4.0.0
 * @since 4.0.0
 */
public class LibsInJarInfo extends ArrayList<LibFileInfo> implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 3580423294902848073L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public LibsInJarInfo()
	{
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトインスタンスのクローンを生成する。
	 * このメソッドが返すインスタンスは、クローン元の内容に影響しないディープコピーとなる。
	 * @return このオブジェクトインスタンスのディープコピーとなるクローン
	 */
	@Override
	public LibsInJarInfo clone()
	{
		LibsInJarInfo v = (LibsInJarInfo)super.clone();
		for (int index = 0; index < this.size(); ++index) {
			LibFileInfo elem = this.get(index);
			v.set(index, elem==null ? null : elem.clone());
		}
		return v;
	}
	
	/**
	 * 指定されたファイルから、ライブラリ情報を読み込む。
	 * ライブラリ情報ファイルは、必ず UTF-8 文字コードで読み込まれる。
	 * @param targetFile	読み込み対象ファイルの抽象パス
	 * @throws FileNotFoundException	ファイルが見つからない場合
	 * @throws IOException	入出力エラーが発生した場合、もしくはファイルの内容が適切ではない場合
	 */
	public void loadFromFile(File targetFile)
			throws FileNotFoundException, IOException
	{
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			// open
			fis = new FileInputStream(targetFile);
			isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			br = new BufferedReader(isr);
			Path baseDir = targetFile.toPath().toAbsolutePath().normalize().getParent(); 
			
			// read
			int lineno = 0;
			String line = br.readLine();
			while (line != null) {
				++lineno;
				if (!line.isEmpty()) {
					LibFileInfo info = parseFileInfo(targetFile, baseDir, lineno, line);
					if (info != null) {
						add(info);
					}
				}
				line = br.readLine();
			}
		}
		finally {
			// close
			closeSilent(br);
			closeSilent(isr);
			closeSilent(fis);
		}
	}
	
	/**
	 * このオブジェクトが保持するライブラリ情報を、指定されたファイルに保存する。
	 * ライブラリ情報ファイルは、必ず UTF-8 文字コードで保存される。
	 * 
	 * @param targetFile	保存先ファイルの抽象パス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void saveToFile(File targetFile) throws IOException
	{
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			// open
			fos = new FileOutputStream(targetFile);
			osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			bw = new BufferedWriter(osw);
			Path baseDir = targetFile.toPath().toAbsolutePath().normalize().getParent(); 
			
			// write
			for (LibFileInfo info : this) {
				writeFileInfo(bw, baseDir, info);
			}
			bw.flush();
		}
		finally {
			// close
			closeSilent(bw);
			closeSilent(osw);
			closeSilent(fos);
		}
	}
	
	/**
	 * このオブジェクトが保持するライブラリ情報を、テンポラリファイルに保存する。
	 * ライブラリ情報ファイルは、必ず UTF-8 文字コードで保存される。
	 * このメソッドで生成されたテンポラリファイルは、終了時に破棄されるようマークされる。
	 * 
	 * @return	生成されたファイルの位置を示す抽象パス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public File saveToTempFile() throws IOException
	{
		File tmpFile = File.createTempFile("AadlLibsInfo", ".csv");
		tmpFile.deleteOnExit();
		saveToFile(tmpFile);
		return tmpFile;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void closeSilent(Closeable stream)
	{
		if (stream != null) {
			try {
				stream.close();
			}
			catch (Throwable ex) {}	// ignore exception
		}
	}
	
	protected LibFileInfo parseFileInfo(File targetFile, Path baseDir, int lineno, String line) throws IOException
	{
		String[] fields = parseCsvLine(line);
		if (fields.length < 1) {
			// empty
			return null;
		}
		
		// file type
		LibFileType filetype = LibFileType.fromName(fields[0]);
		if (filetype == null) {
			String msg = "(line=" + String.valueOf(lineno) + ", column=1) Invalid library file type: " + String.valueOf(fields[0]);
			if (targetFile != null) {
				msg += "\n  file: " + targetFile.toString();
			}
			throw new IOException(msg);
		}
		LibFileInfo info = new LibFileInfo(filetype);
		
		// parameters
		for (int fi = 1; fi < fields.length; ++fi) {
			String val = fields[fi];
			if (val == null || val.isEmpty()) continue;
			
			int delim = val.indexOf('=');
			if (delim < 0) {
				String msg = "(line=" + String.valueOf(lineno) + ", column=" + String.valueOf(fi+1)
								+ ") Parameter name is not found: " + String.valueOf(val);
				if (targetFile != null) {
					msg += "\n  file: " + targetFile.toString();
				}
				throw new IOException(msg);
			}
			LibInfoParamType paramtype = LibInfoParamType.fromName(val.substring(0, delim+1));
			if (paramtype == null) {
				//String msg = "(line=" + String.valueOf(lineno) + ", column=" + String.valueOf(fi+1)
				//				+ ") Skipped unknown parameter name: " + String.valueOf(val);
				//if (targetFile != null) {
				//	msg += "\n  file: " + targetFile.toString();
				//}
				continue;	// skipped
			}
			
			String paramValue = val.substring(delim+1);
			if (paramtype == LibInfoParamType.NAME) {
				info.setLibName(paramValue);
			}
			else if (paramtype == LibInfoParamType.PATH) {
				if (paramValue.isEmpty()) {
					String msg = "(line=" + String.valueOf(lineno) + ", column=" + String.valueOf(fi+1)
									+ ") Library's path is empty: " + String.valueOf(val);
					if (targetFile != null) {
						msg += "\n  file: " + targetFile.toString();
					}
					throw new IOException(msg);
				}
				Path p;
				try {
					p = Paths.get(paramValue);
				}
				catch (InvalidPathException ex) {
					String msg = "(line=" + String.valueOf(lineno) + ", column=" + String.valueOf(fi+1)
									+ ") Library's path is not valid: " + String.valueOf(val);
					if (targetFile != null) {
						msg += "\n  file: " + targetFile.toString();
					}
					throw new IOException(msg);
				}
				if (!p.isAbsolute()) {
					if (baseDir != null) {
						p = baseDir.resolve(p);
					} else {
						p = p.toAbsolutePath();
					}
				}
				p = p.normalize();
				if (Files.notExists(p)) {
					String msg = "(line=" + String.valueOf(lineno) + ", column=" + String.valueOf(fi+1)
									+ ") Library file does not exist: " + String.valueOf(val);
					if (targetFile != null) {
						msg += "\n  file: " + targetFile.toString();
					}
					throw new IOException(msg);
				}
				info.setLibFile(p.toFile());
			}
			else if (paramtype == LibInfoParamType.LICENSE_NAME) {
				info.setLicenseName(paramValue);
			}
			else if (paramtype == LibInfoParamType.LICENSE_FILE) {
				if (paramValue.isEmpty()) {
					continue;	// skip
				}
				Path p;
				try {
					p = Paths.get(paramValue);
				}
				catch (InvalidPathException ex) {
					String msg = "(line=" + String.valueOf(lineno) + ", column=" + String.valueOf(fi+1)
									+ ") License file path is not valid: " + String.valueOf(val);
					if (targetFile != null) {
						msg += "\n  file: " + targetFile.toString();
					}
					throw new IOException(msg);
				}
				if (!p.isAbsolute()) {
					if (baseDir != null) {
						p = baseDir.resolve(p);
					} else {
						p = p.toAbsolutePath();
					}
				}
				p = p.normalize();
				if (Files.notExists(p)) {
					String msg = "(line=" + String.valueOf(lineno) + ", column=" + String.valueOf(fi+1)
									+ ") License file does not exist: " + String.valueOf(val);
					if (targetFile != null) {
						msg += "\n  file: " + targetFile.toString();
					}
					throw new IOException(msg);
				}
				info.setLicenseFile(p.toFile());
			}
		}
		return info;
	}
	
	protected void writeFileInfo(BufferedWriter bw, Path baseDir, LibFileInfo info) throws IOException
	{
		// file type
		bw.write(info.getFileType().toString());
		
		// lib-name
		if (!info.isEmptyLibName()) {
			bw.write(',');
			bw.write(enquote(LibInfoParamType.NAME.toString() + info.getLibName()));
		}
		
		// lib-path
		if (info.getLibFile() != null) {
			Path p = info.getLibFile().toPath();
			if (p.isAbsolute() && baseDir != null && p.startsWith(baseDir)) {
				p = p.relativize(baseDir);
			}
			bw.write(',');
			bw.write(enquote(LibInfoParamType.PATH.toString() + p.toString()));
		}
		
		// license-name
		if (info.hasLicense()) {
			bw.write(',');
			bw.write(enquote(LibInfoParamType.LICENSE_NAME.toString() + info.getLicenseName()));
		}
		
		// license-file
		if (info.getLicenseFile() != null) {
			Path p = info.getLicenseFile().toPath();
			if (p.isAbsolute() && baseDir != null && p.startsWith(baseDir)) {
				p = p.relativize(baseDir);
			}
			bw.write(',');
			bw.write(enquote(LibInfoParamType.LICENSE_FILE.toString() + p.toString()));
		}
		
		// new-line
		bw.newLine();
	}
	
	/**
	 * 指定された文字列をCSV出力用にダブルクオートでエンコードする。
	 * ダブルクオートが付加されるのは、" か , を含んでいる場合のみとなる。
	 * このとき " を "" に置き換える。
	 * 指定された文字列が null もしくは長さ 0 の文字列の場合は、長さ 0 の文字列を返す。
	 * 
	 * @param text 処理したい文字列
	 * @return 処理済文字列
	 * @since 4.0.0
	 */
	protected String enquote(String text) {
		// exist text?
		if (text == null || text.length() <= 0)
			return "";
		
		// exist '"' or ','
		if (0 > text.indexOf('"') && 0 > text.indexOf(','))
			return text;
		
		// enquote
		StringBuffer sb = new StringBuffer(text.length() + 10);
		sb.append('"');
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if ('"' == ch) {
				sb.append("\"\"");
			} else {
				sb.append(ch);
			}
		}
		sb.append('"');
		return sb.toString();
	}

	/**
	 * 1行のCSVフォーマット文字列から、各カラムの文字列を取得する。
	 * カラムの区切りは , とし、最後の区切り文字以降の文字列長が 0 で
	 * ある場合は、長さ 0 の文字列を最後のカラム文字列として返す。
	 * ダブルクオーテーション(")で囲まれた文字列は区切り文字を無視する。
	 * また、ダブルクオーテーションで囲まれた文字列内では、連続した
	 * ダブルクオーテーションは、単一ダブルクオーテーション文字とする。
	 * 区切り文字間の空白は文字として扱う。
	 * 
	 * @param text CSVフォーマットの文字列
	 * @return カラム毎の文字列の配列
	 * @since 4.0.0
	 */
	protected String[] parseCsvLine(String text) {
		// check text
		if (text == null || text.length() <= 0) {
			return new String[0];	// no text
		}
		
		// parse csv format
		final Vector<String> vec = new Vector<String>();
	    final StringBuffer sb = new StringBuffer(text.length());
	    
	    boolean inquote = false;
	    for (int iPos = 0; iPos < text.length(); iPos++) {
	    	char ch = text.charAt(iPos);
	    	if (inquote) {
	    		// '"'トークン内
	    		if (ch == '"') {
	    			char nch = ((iPos+1) < text.length() ? text.charAt(iPos+1) : '\0');
	    			if (nch == '"') {
	    				// トークン内のエスケープ
	    				sb.append(ch);
	    				iPos++;	// skip escape char
	    			}
	    			else {
	    				// トークン終了
	    				inquote = false;
	    			}
	    		}
	    		else {
	    			sb.append(ch);
	    		}
	    	}
	    	else if (ch == '"') {
	    		if (sb.length() > 0) {
	    			// カラム先頭の'"'でなければ、通常の文字として扱う
	    			sb.append(ch);
	    		}
	    		else {
	    			// カラム先頭の'"'は、トークン開始
	    			inquote = true;
	    		}
	    	}
	    	else if (ch == ',') {
	    		// カラム終端
	    		vec.add(sb.toString());
	    		sb.delete(0, sb.length());
	    	}
	    	else if (ch == '\r' || ch == '\n' || ch == '\u0085' || ch == '\u2028' || ch == '\u2029') {
	    		// 改行文字(Java)は、CSV行の終端とする
	    		break;
	    	}
	    	else {
	    		// 通常文字
	    		sb.append(ch);
	    	}
	    }
	    // 最終カラムのデータ追加
	    vec.add(sb.toString());
	    
	    // completed
	    return vec.toArray(new String[vec.size()]);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
