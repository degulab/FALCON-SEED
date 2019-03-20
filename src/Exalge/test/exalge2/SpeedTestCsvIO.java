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
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
package exalge2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Random;

import junit.framework.TestCase;
import exalge2.io.FileUtil;
import exalge2.nio.csv.ChannelCsvReader;
import exalge2.nio.csv.ChannelCsvWriter;
import exalge2.util.ExQuarterTimeKey;
import exalge2.util.Strings;

/**
 * CSV形式ファイルの入出力速度チェック
 */
public class SpeedTestCsvIO extends TestCase {
	
	static private final String CSV_WORKDIR = "testdata/CsvPerformance";
	static private final String CSV_STREAM_DEFAULT = "algeStreamDefault.csv";
	static private final String CSV_STREAM_SJIS    = "algeStreamSJIS.csv";
	static private final String CSV_STREAM_UTF8    = "algeStreamUTF8.csv";
	static private final String CSV_CHANNELS_DEFAULT  = "algeChannelsDefault.csv";
	static private final String CSV_CHANNELS_SJIS     = "algeChannelsSJIS.csv";
	static private final String CSV_CHANNELS_UTF8     = "algeChannelsUTF8.csv";
	static private final String CSV_CH_HEAP_DEFAULT   = "algeChHeapDefault.csv";
	static private final String CSV_CH_HEAP_SJIS      = "algeChHeapSJIS.csv";
	static private final String CSV_CH_HEAP_UTF8      = "algeChHeapUTF8.csv";
	static private final String CSV_CH_DIRECT_DEFAULT = "algeChDirectDefault.csv";
	static private final String CSV_CH_DIRECT_SJIS    = "algeChDirectSJIS.csv";
	static private final String CSV_CH_DIRECT_UTF8    = "algeChDirectUTF8.csv";
	
	static private final String[] TEXTS1 = new String[]{
		"あ", "い", "う", "え", "お",
		"か", "き", "く", "け", "こ",
		"が", "ぎ", "ぐ", "げ", "ご",
		"さ", "し", "す", "せ", "そ",
		"ざ", "じ", "ず", "ぜ", "ぞ",
		"た", "ち", "つ", "て", "と",
		"だ", "ぢ", "づ", "で", "ど",
		"な", "に", "ぬ", "ね", "の",
		"は", "ひ", "ふ", "へ", "ほ",
		"ば", "び", "ぶ", "べ", "ぼ",
		"ぱ", "ぴ", "ぷ", "ぺ", "ぽ",
		"ま", "み", "む", "め", "も",
		"や", "ゆ", "よ",
		"ら", "り", "る", "れ", "ろ",
		"わ",
	};
	
	static private final String[] TEXTS2 = new String[]{
		"あ", "い", "う", "え", "お",
		"か", "き", "く", "け", "こ",
		"が", "ぎ", "ぐ", "げ", "ご",
		"さ", "し", "す", "せ", "そ",
		"ざ", "じ", "ず", "ぜ", "ぞ",
		"た", "ち", "つ", "て", "と", "っ",
		"だ", "ぢ", "づ", "で", "ど",
		"な", "に", "ぬ", "ね", "の",
		"は", "ひ", "ふ", "へ", "ほ",
		"ば", "び", "ぶ", "べ", "ぼ",
		"ぱ", "ぴ", "ぷ", "ぺ", "ぽ",
		"ま", "み", "む", "め", "も",
		"や", "ゆ", "よ", "ゃ", "ゅ", "ょ",
		"ら", "り", "る", "れ", "ろ",
		"わ", "を", "ん",
	};
	
	static private final int MAX_TEXTS = 10;
	static private final long INIT_SEED = 20090209L;
	static private final int MAX_NAMES = 100;
	static private final int MAX_SUBJECTS = 100;
	static private final int MAX_TIMES = 100;

	private Random[] randForName;
	private Random   randForHat;
	private String[] baseNames;
	private String[] baseTimes;
	private String[] baseSubjects;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		// 乱数生成(HAT)
		randForHat = new Random(INIT_SEED);
		
		// 乱数生成(Names)
		randForName = new Random[MAX_TEXTS];
		for (int i = 0; i < MAX_TEXTS; i++) {
			long seed = randForHat.nextLong();
			randForName[i] = new Random(seed);
		}
		
		// 名前基底キーの生成
		StringBuilder sb = new StringBuilder(MAX_TEXTS);
		baseNames = new String[MAX_NAMES];
		for (int i = 0; i < MAX_NAMES; i++) {
			sb.setLength(0);
			String text = TEXTS1[randForName[0].nextInt(TEXTS1.length)];
			sb.append(text);
			
			for (int j = 1; j < MAX_TEXTS; j++) {
				text = TEXTS2[randForName[j].nextInt(TEXTS2.length)];
				sb.append(text);
			}
			
			baseNames[i] = sb.toString();
		}
		
		// 主体基底キーの生成
		baseSubjects = new String[MAX_SUBJECTS];
		for (int i = 0; i < MAX_SUBJECTS; i++) {
			sb.setLength(0);
			String text = TEXTS1[randForName[0].nextInt(TEXTS1.length)];
			sb.append(text);
			
			for (int j = 1; j < MAX_TEXTS; j++) {
				text = TEXTS2[randForName[j].nextInt(TEXTS2.length)];
				sb.append(text);
			}
			
			baseSubjects[i] = sb.toString();
		}
		
		// 時間基底キーの生成
		baseTimes = new String[MAX_TIMES];
		ExQuarterTimeKey time = new ExQuarterTimeKey(2001, 1);
		for (int i = 0; i < MAX_TIMES; i++) {
			String text = time.toString();
			baseTimes[i] = text;
			time.addUnit(1);
		}
		
		// フォルダの生成
		ensureDirectory(CSV_WORKDIR);
	}
	
	static public File ensureDirectory(String path) throws IOException
	{
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				String msg = "the specified directory is a file";
				throw new IOException(msg);
			} else if (file.isDirectory()) {
				return file;
			} else {
				String msg = "the specified directory is not known type";
				throw new IOException(msg);
			}
		} else {
			if (file.mkdir()) {
				return file;
			} else {
				String msg = "failed to create the specified directory";
				throw new IOException(msg);
			}
		}
	}
	
	static public int countOfLine(Reader reader) throws IOException {
		boolean hasChar = false;
		int lines = 0;
		int read;
		while ((read = reader.read()) >= 0) {
			if (read == '\r') {
				lines++;
				hasChar = false;
				read = reader.read();
				if (read < 0) {
					break;
				}
				else if (read != '\n') {
					hasChar = true;
				}
			}
			else if (read == '\n') {
				lines++;
				hasChar = false;
			}
			else if (!hasChar) {
				hasChar = true;
			}
		}
		if (hasChar) {
			lines++;
		}
		return lines;
	}
	
	public void testExalgeIn() {
		System.out.println("//");
		System.out.println("// Start testExalgeIn");
		System.out.println("//");
		
		File csvFile = new File(CSV_WORKDIR, CSV_STREAM_DEFAULT);
		if (!csvFile.exists()) {
			System.err.println(".....Test file is not found!");
			return;
		}
		
		// ファイルの行数をカウント
		int  lines = 0;
		long tcSpan = 0L;
		long tcStart, tcEnd;
		final int charBufSize_x2 = 16384;
		final int charBufSize_x4 = 32768;
		final int charBufSize_x10 = 81920;
		final int charBufSize_x64 = 524288;
		
		//--- no buffer
		System.out.println();
		System.out.println("<< Buffer not used >>");
		tcStart = System.currentTimeMillis();
		{
			FileReader fr = null;
			try {
				fr = new FileReader(csvFile);
				lines = countOfLine(fr);
			}
			catch (Throwable ex) {
				lines = -1;
				System.err.println(".....Failed to read!");
			}
			finally {
				if (fr != null) {
					FileUtil.closeStream(fr);
					fr = null;
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Number of line : " + lines);
		System.out.println("    - time span : " + tcSpan + " ms");
		
		//--- buffer default
		System.out.println();
		System.out.println("<< Buffer for default size >>");
		tcStart = System.currentTimeMillis();
		{
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(csvFile));
				lines = countOfLine(br);
			}
			catch (Throwable ex) {
				lines = -1;
				System.err.println(".....Failed to read!");
			}
			finally {
				if (br != null) {
					FileUtil.closeStream(br);
					br = null;
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Number of line : " + lines);
		System.out.println("    - time span : " + tcSpan + " ms");
		
		//--- buffer 16384 chars
		System.out.println();
		System.out.println("<< Buffer for 16384 chars >>");
		tcStart = System.currentTimeMillis();
		{
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(csvFile), charBufSize_x2);
				lines = countOfLine(br);
			}
			catch (Throwable ex) {
				lines = -1;
				System.err.println(".....Failed to read!");
			}
			finally {
				if (br != null) {
					FileUtil.closeStream(br);
					br = null;
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Number of line : " + lines);
		System.out.println("    - time span : " + tcSpan + " ms");
		
		//--- buffer 32768 chars
		System.out.println();
		System.out.println("<< Buffer for 32768 chars >>");
		tcStart = System.currentTimeMillis();
		{
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(csvFile), charBufSize_x4);
				lines = countOfLine(br);
			}
			catch (Throwable ex) {
				lines = -1;
				System.err.println(".....Failed to read!");
			}
			finally {
				if (br != null) {
					FileUtil.closeStream(br);
					br = null;
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Number of line : " + lines);
		System.out.println("    - time span : " + tcSpan + " ms");
		
		//--- buffer 81920 chars
		System.out.println();
		System.out.println("<< Buffer for 81920 chars >>");
		tcStart = System.currentTimeMillis();
		{
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(csvFile), charBufSize_x10);
				lines = countOfLine(br);
			}
			catch (Throwable ex) {
				lines = -1;
				System.err.println(".....Failed to read!");
			}
			finally {
				if (br != null) {
					FileUtil.closeStream(br);
					br = null;
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Number of line : " + lines);
		System.out.println("    - time span : " + tcSpan + " ms");
		
		//--- buffer 524288 chars
		System.out.println();
		System.out.println("<< Buffer for 524288 chars >>");
		tcStart = System.currentTimeMillis();
		{
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(csvFile), charBufSize_x64);
				lines = countOfLine(br);
			}
			catch (Throwable ex) {
				lines = -1;
				System.err.println(".....Failed to read!");
			}
			finally {
				if (br != null) {
					FileUtil.closeStream(br);
					br = null;
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Number of line : " + lines);
		System.out.println("    - time span : " + tcSpan + " ms");

		// Read stream default(FileReader)
		System.out.println();
		System.out.println("<< Read stream default(FileReader) >>");
		tcStart = System.currentTimeMillis();
		Exalge readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader(csvFile);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");

		// Read stream with count of line
		System.out.println();
		System.out.println("<< Read stream with Count of line >>");
		tcStart = System.currentTimeMillis();
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				int numAlges = 0;
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(csvFile));
					numAlges = countOfLine(br);
				}
				finally {
					if (br != null) {
						FileUtil.closeStream(br);
						br = null;
					}
				}
				if (numAlges > 0) {
					readalge = new Exalge(numAlges);
				}
				reader = new ChannelCsvReader(csvFile);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		
		System.out.println("//");
		System.out.println("// testExalgeIn finished!");
		System.out.println("//");
	}
	
	public void testExalgeInOutCsv() {
		long count = 0L;
		long tcSpan = 0L;
		long tcStart, tcEnd;
		File csvFile;
		
		System.out.println("//");
		System.out.println("// Start testExalgeInOutCsv");
		System.out.println("//");

		// create ExAlgeSet
		System.out.println();
		System.out.println("<< Creates ExAlgeSet >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		ExAlgeSet algeset = new ExAlgeSet();
		for (String strName : baseNames) {
			for (String strTime : baseTimes) {
				for (String strSubject : baseSubjects) {
					ExBase base = new ExBase(strName, ExBase.NO_HAT, ExBase.OMITTED, strTime, strSubject);
					count++;
					BigDecimal value = new BigDecimal(count);
					Exalge alge = new Exalge(base, value);
					algeset.add(alge);
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - created " + algeset.size() + " alges.");
		System.out.println("    - creating time span : " + tcSpan + " ms");

		// sum ExAlgeSet
		System.out.println();
		System.out.println("<< sum(ExAlgeSet) >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		Exalge testalge = algeset.sum();
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - sum(" + algeset.size() + " alges) -> result " + testalge.getNumElements() + " elements.");
		System.out.println("    - creating time span : " + tcSpan + " ms");

		// Write stream default(FileWriter)
		System.out.println();
		System.out.println("<< Write stream default(FileWriter) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_STREAM_DEFAULT);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter(csvFile);
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");

		// Write stream SJIS(OutputStreamWriter)
		System.out.println();
		System.out.println("<< Write stream SJIS(OutputStreamWriter) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_STREAM_SJIS);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter(csvFile, "MS932");
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");
		
		// Write stream UTF8(OutputStreamWriter)
		System.out.println();
		System.out.println("<< Write stream UTF8(OutputStreamWriter) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_STREAM_UTF8);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter(csvFile, "UTF8");
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");
		
		// Write channels Default(Channels.newWriter)
		System.out.println();
		System.out.println("<< Write channels default(Channels.newWriter) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CHANNELS_DEFAULT);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter("", csvFile);
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");
		
		// Write channels SJIS(Channels.newWriter)
		System.out.println();
		System.out.println("<< Write channels SJIS(Channels.newWriter) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CHANNELS_SJIS);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter("MS932", csvFile);
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");
		
		// Write channels UTF8(Channels.newWriter)
		System.out.println();
		System.out.println("<< Write channels UTF8(Channels.newWriter) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CHANNELS_UTF8);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter("UTF8", csvFile);
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");
		
		// Write channel heap buffer default
		System.out.println();
		System.out.println("<< Write channel heap buffer default >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_HEAP_DEFAULT);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter(csvFile, "", false);
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");
		
		// Write channel heap buffer SJIS
		System.out.println();
		System.out.println("<< Write channel heap buffer SJIS >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_HEAP_SJIS);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter(csvFile, "MS932", false);
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");
		
		// Write channel heap buffer UTF8
		System.out.println();
		System.out.println("<< Write channel heap buffer UTF8 >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_HEAP_UTF8);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter(csvFile, "UTF8", false);
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");
		
		// Write channel direct buffer default
		System.out.println();
		System.out.println("<< Write channel direct buffer default >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_DIRECT_DEFAULT);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter(csvFile, "", true);
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");
		
		// Write channel direct buffer SJIS
		System.out.println();
		System.out.println("<< Write channel direct buffer SJIS >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_DIRECT_SJIS);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter(csvFile, "MS932", true);
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");
		
		// Write channel direct buffer UTF8
		System.out.println();
		System.out.println("<< Write channel direct buffer UTF8 >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_DIRECT_UTF8);
		{
			ChannelCsvWriter writer = null;
			try {
				writer = new ChannelCsvWriter(csvFile, "UTF8", true);
				writeToCsv(testalge, writer);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Write Exalge(" + testalge.getNumElements() + " elems) to \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes).");
		System.out.println("    - Writing time span : " + tcSpan + " ms");
		
		Exalge readalge;

		// Read stream default(FileReader)
		System.out.println();
		System.out.println("<< Read stream default(FileReader) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_STREAM_DEFAULT);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader(csvFile);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);

		// Read stream SJIS(OutputStreamWriter)
		System.out.println();
		System.out.println("<< Read stream SJIS(InputStreamReader) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_STREAM_SJIS);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader(csvFile, "MS932");
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);
		
		// Read stream UTF8(OutputStreamWriter)
		System.out.println();
		System.out.println("<< Read stream UTF8(InputStreamReader) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_STREAM_UTF8);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader(csvFile, "UTF8");
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);
		
		// Read channels Default(Channels.newReader)
		System.out.println();
		System.out.println("<< Read channels default(Channels.newReader) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CHANNELS_DEFAULT);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader("", csvFile);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);
		
		// Read channels SJIS(Channels.newReader)
		System.out.println();
		System.out.println("<< Read channels SJIS(Channels.newReader) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CHANNELS_SJIS);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader("MS932", csvFile);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);
		
		// Read channels UTF8(Channels.newReader)
		System.out.println();
		System.out.println("<< Read channels UTF8(Channels.newReader) >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CHANNELS_UTF8);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader("UTF8", csvFile);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);
		
		// Read channel heap buffer default
		System.out.println();
		System.out.println("<< Read channel heap buffer default >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_HEAP_DEFAULT);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader(csvFile, "", false);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);
		
		// Read channel heap buffer SJIS
		System.out.println();
		System.out.println("<< Read channel heap buffer SJIS >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_HEAP_SJIS);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader(csvFile, "MS932", false);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);
		
		// Read channel heap buffer UTF8
		System.out.println();
		System.out.println("<< Read channel heap buffer UTF8 >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_HEAP_UTF8);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader(csvFile, "UTF8", false);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);
		
		// Read channel direct buffer default
		System.out.println();
		System.out.println("<< Read channel direct buffer default >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_DIRECT_DEFAULT);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader(csvFile, "", true);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);
		
		// Read channel direct buffer SJIS
		System.out.println();
		System.out.println("<< Read channel direct buffer SJIS >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_DIRECT_SJIS);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader(csvFile, "MS932", true);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);
		
		// Read channel direct buffer UTF8
		System.out.println();
		System.out.println("<< Read channel direct buffer UTF8 >>");
		tcStart = System.currentTimeMillis();
		csvFile = new File(CSV_WORKDIR, CSV_CH_DIRECT_UTF8);
		readalge = new Exalge();
		{
			ChannelCsvReader reader = null;
			try {
				reader = new ChannelCsvReader(csvFile, "UTF8", true);
				readFromCsv(readalge, reader);
			}
			catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - Read \"" + csvFile.getPath() + "\"(" + csvFile.length() + " bytes) to Exalge(" + readalge.getNumElements() + " elems).");
		System.out.println("    - Reading time span : " + tcSpan + " ms");
		assertEquals(testalge, readalge);
		
		System.out.println("//");
		System.out.println("// testExalgeInOutCsv finished!");
		System.out.println("//");
	}
	
	static private final void writeToCsv(Exalge alge, ChannelCsvWriter writer) throws IOException {
		for (ExBase base : alge.data.keySet()) {
			BigDecimal exval = alge.data.get(base);
			writer.writeField(false, exval.stripTrailingZeros().toPlainString());
			writer.writeField(false, base.getHatKey());
			writer.writeField(false, base.getNameKey());
			writer.writeField(false, base.getUnitKey());
			writer.writeField(false, base.getTimeKey());
			writer.writeField(true, base.getSubjectKey());
			//writer.flush();
		}
		writer.flush();
	}
	
	static private final void readFromCsv(Exalge alge, ChannelCsvReader reader) throws IOException {
		ChannelCsvReader.ChannelCsvRecord record;
		while ((record = reader.readRecord()) != null) {
			// check blank line
			if (!record.hasFields()) {
				// skip blank line
				continue;
			}
			
			// check comment line
			if (record.getRecord().startsWith("#")) {
				// skip comment line
				continue;
			}
			
			// read value
			BigDecimal value = null;
			{
				ChannelCsvReader.ChannelCsvField field = record.getField(0);
				try {
					value = new BigDecimal(field.getValue());
				}
				catch (Throwable ex) {
					throw new exalge2.nio.csv.ChannelCsvFormatException("Illegal value : " + String.valueOf(field.getValue()), field.getLineNo(), 1);
				}
			}
			
			// read base
			ExBase newBase = new ExBase("init", ExBase.NO_HAT);
			{
				ChannelCsvReader.ChannelCsvField field;
				String strValue;
				
				// ハットキー
				field = record.getField(1);
				strValue = ExBase.Util.filterBaseKey(field.getValue(), false);
				if (Strings.isNullOrEmpty(strValue)) {
					throw new exalge2.nio.csv.ChannelCsvFormatException("Illegal Hat key : " + String.valueOf(field.getValue()), field.getLineNo(), 2);
				}
				newBase._baseKeys[ExBase.KEY_HAT] = strValue;
				
				// 名前キー
				field = record.getField(2);
				strValue = ExBase.Util.filterBaseKey(field.getValue(), false);
				if (strValue == null) {
					throw new exalge2.nio.csv.ChannelCsvFormatException("Illegal Name key : " + String.valueOf(field.getValue()), field.getLineNo(), 3);
				}
				newBase._baseKeys[ExBase.KEY_NAME] = strValue;
				
				// 単位キー
				field = record.getField(3);
				strValue = ExBase.Util.filterBaseKey(field.getValue(), true);
				if (strValue == null) {
					throw new exalge2.nio.csv.ChannelCsvFormatException("Illegal Unit key : " + String.valueOf(field.getValue()), field.getLineNo(), 4);
				}
				newBase._baseKeys[ExBase.KEY_EXT_UNIT] = strValue;
				
				// 時間キー
				field = record.getField(4);
				strValue = ExBase.Util.filterBaseKey(field.getValue(), true);
				if (strValue == null) {
					throw new exalge2.nio.csv.ChannelCsvFormatException("Illegal Time key : " + String.valueOf(field.getValue()), field.getLineNo(), 5);
				}
				newBase._baseKeys[ExBase.KEY_EXT_TIME] = strValue;
				
				// 主体キー
				field = record.getField(5);
				strValue = ExBase.Util.filterBaseKey(field.getValue(), true);
				if (strValue == null) {
					throw new exalge2.nio.csv.ChannelCsvFormatException("Illegal Subject key : " + String.valueOf(field.getValue()), field.getLineNo(), 6);
				}
				newBase._baseKeys[ExBase.KEY_EXT_SUBJECT] = strValue;
				
				// 内部情報の更新
				newBase.setupStatus();
			}
			
			// 代入(加算)
			alge.plusValue(newBase, value);
		}
	}
}
