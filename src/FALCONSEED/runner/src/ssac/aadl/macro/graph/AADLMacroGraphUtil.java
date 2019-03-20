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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)AADLMacroGraphUtil.java	2.0.0	2012/10/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMacroGraphUtil.java	1.20	2012/03/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import ssac.util.io.Files;
import ssac.util.io.VirtualFile;

/**
 * AADLマクロファイルを GraphViz の DOT 言語に出力する。
 * 
 * @version 2.0.0	2012/10/26
 * @since 1.20
 */
public class AADLMacroGraphUtil
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

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public String createDotByMacro(VirtualFile macroFile, String macroEncoding) throws IOException
	{
		//ラベル・形状情報のコレクション（ノード名⇒（ラベル文字列、形状）のハッシュ）
		HashMap<String, ArrayList<?>> labelshapemap = new HashMap<String, ArrayList<?>>();
		//フロー（矢印）情報のコレクション（（始点ノード名、終点ノード名）の配列）
		ArrayList<ArrayList<?>> flowset = new ArrayList<ArrayList<?>>();

		// read macro
		InputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			// open macro file
			fis = macroFile.getInputStream();
			isr = new InputStreamReader(fis, macroEncoding);
			br = new BufferedReader(isr);
			
			//amfファイルからの各行読み込みバッファ
			String buff;
			//amfファイルの何行目を読んでいるか
			int recno = 0;

			while((buff = br.readLine()) != null) {
				recno++;

				//amfの１行をカンマ区切りで切り出す
				String[] array = buff.split(",(?=(([^\"]*\"){2})*[^\"]*$)");

				//java行とmacro行のみを処理の対象
				if(array[0].equals("java") || array[0].equals("macro")) {
					//コメント列とモジュール列を取り出す
					String comment = array[2];
					String module = array[3]; //以下モジュールと呼ぶがサブマクロを指すこともある

					//同じモジュールが繰り返し使われることがあるので、一意なノード名を別途作る
					String modulestr = "module" + Integer.toString(recno);
					ArrayList<String> labelshapemodule = new ArrayList<String>();
					//モジュールのラベル文字列は、モジュール名とコメントを結合させたものにする
					String modulecomment = module + "\\n" + comment;
					//モジュールをラベル・形状情報のコレクションに登録
					labelshapemodule.add(modulecomment);
					labelshapemodule.add("ellipse"); //モジュールは楕円
					labelshapemap.put(modulestr, labelshapemodule);

					//引数（[IN][STR][OUT]）取り出し
					for (int i = 7 ; i < array.length ; i++){
						if(array[i].equals("[IN]") || array[i].equals("[STR]") || array[i].equals("[OUT]")) {
							//引数の値（ファイル名など）を取り出す
							String value = array[i+1];

							//[IN][STR][OUT]をラベル・形状情報のコレクションに登録
							ArrayList<String> labelshape = new ArrayList<String>();
							labelshape.add(value);
							if(array[i].equals("[IN]") || array[i].equals("[OUT]"))
								labelshape.add("box"); //[IN][OUT]は矩形
							else
								labelshape.add("plaintext"); //[STR]はテキストのみ
							labelshapemap.put(value, labelshape);

							//モジュールと[IN][STR][OUT]の組をラベル・形状情報のコレクションに登録
							ArrayList<String> flow = new ArrayList<String>();
							if(array[i].equals("[IN]") || array[i].equals("[STR]")) {
								flow.add(value); //[IN][STR]のときはモジュールに向かって矢印を引く
								flow.add(modulestr);
							} else {
								flow.add(modulestr); //[OUT]のときはモジュールから矢印を引く
								flow.add(value);
							}
							flowset.add(flow);
						}
					}
				}
			}
		}
		finally {
			Files.closeStream(br);
			Files.closeStream(isr);
			Files.closeStream(fis);
			br  = null;
			isr = null;
			fis = null;
		}
		
		// write dot
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		//ヘッダ部分の書き出し
		pw.println("digraph AADLmacroDraw {");
		pw.println("  node [fontname = \"MS UI Gothic\"];");

		//フロー（矢印）情報のコレクション書き出し
		Iterator<ArrayList<?>> flowite = flowset.iterator();
		while(flowite.hasNext()){
			ArrayList<?> value = flowite.next();
			pw.println("  \"" + value.get(0) + "\" -> \"" + value.get(1) + "\";");
		}

		//ラベル・形状情報のコレクション書き出し
		Set<String> keys = labelshapemap.keySet();
		Iterator<String> labelshapeite = keys.iterator();
		while(labelshapeite.hasNext()){
			String key = (String)labelshapeite.next();
			ArrayList<?> value = labelshapemap.get(key);
			pw.println("  \"" + key + "\" [label = \"" + value.get(0) + "\", shape=" + value.get(1) + "];");
		}

		//終端部分の書き出し
		pw.println("}");
		
		return sw.toString();
	}
	
	static public void exportMacroToDot(VirtualFile macroFile, String macroEncoding, VirtualFile dotFile)
	throws IOException
	{
		//ラベル・形状情報のコレクション（ノード名⇒（ラベル文字列、形状）のハッシュ）
		HashMap<String, ArrayList<?>> labelshapemap = new HashMap<String, ArrayList<?>>();
		//フロー（矢印）情報のコレクション（（始点ノード名、終点ノード名）の配列）
		ArrayList<ArrayList<?>> flowset = new ArrayList<ArrayList<?>>();

		// read macro
		InputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			// open macro file
			fis = macroFile.getInputStream();
			isr = new InputStreamReader(fis, macroEncoding);
			br = new BufferedReader(isr);
			
			//amfファイルからの各行読み込みバッファ
			String buff;
			//amfファイルの何行目を読んでいるか
			int recno = 0;

			while((buff = br.readLine()) != null) {
				recno++;

				//amfの１行をカンマ区切りで切り出す
				String[] array = buff.split(",(?=(([^\"]*\"){2})*[^\"]*$)");

				//java行とmacro行のみを処理の対象
				if ("java".equalsIgnoreCase(array[0]) || "macro".equalsIgnoreCase(array[0])) {
					//コメント列とモジュール列を取り出す
					String comment = array[2];
					String module = array[3]; //以下モジュールと呼ぶがサブマクロを指すこともある

					//同じモジュールが繰り返し使われることがあるので、一意なノード名を別途作る
					String modulestr = "module" + Integer.toString(recno);
					ArrayList<String> labelshapemodule = new ArrayList<String>();
					//モジュールのラベル文字列は、モジュール名とコメントを結合させたものにする
					String modulecomment = module + "\\n" + comment;
					//モジュールをラベル・形状情報のコレクションに登録
					labelshapemodule.add(modulecomment);
					labelshapemodule.add("ellipse"); //モジュールは楕円
					labelshapemap.put(modulestr, labelshapemodule);

					//引数（[IN][STR][OUT]）取り出し
					for (int i = 7 ; i < array.length ; i++){
						if ("[IN]".equalsIgnoreCase(array[i]) || "[STR]".equalsIgnoreCase(array[i]) || "[OUT]".equalsIgnoreCase(array[i])) {
							//引数の値（ファイル名など）を取り出す
							String value = array[i+1];

							//[IN][STR][OUT]をラベル・形状情報のコレクションに登録
							ArrayList<String> labelshape = new ArrayList<String>();
							labelshape.add(value);
							if ("[IN]".equalsIgnoreCase(array[i]) || "[OUT]".equalsIgnoreCase(array[i]))
								labelshape.add("box"); //[IN][OUT]は矩形
							else
								labelshape.add("plaintext"); //[STR]はテキストのみ
							labelshapemap.put(value, labelshape);

							//モジュールと[IN][STR][OUT]の組をラベル・形状情報のコレクションに登録
							ArrayList<String> flow = new ArrayList<String>();
							if ("[IN]".equalsIgnoreCase(array[i]) || "[STR]".equalsIgnoreCase(array[i])) {
								flow.add(value); //[IN][STR]のときはモジュールに向かって矢印を引く
								flow.add(modulestr);
							} else {
								flow.add(modulestr); //[OUT]のときはモジュールから矢印を引く
								flow.add(value);
							}
							flowset.add(flow);
						}
					}
				}
			}
		}
		finally {
			Files.closeStream(br);
			Files.closeStream(isr);
			Files.closeStream(fis);
			br  = null;
			isr = null;
			fis = null;
		}
		
		// write dot
		OutputStream fos = null;
		OutputStreamWriter osw = null;
		PrintWriter pw = null;
		try {
			// open file
			fos = dotFile.getOutputStream();
			osw = new OutputStreamWriter(fos, "UTF-8");
			pw = new PrintWriter(osw);

			//ヘッダ部分の書き出し
			pw.println("digraph AADLmacroDraw {");
			pw.println("  node [fontname = \"MS UI Gothic\"];");

			//フロー（矢印）情報のコレクション書き出し
			Iterator<ArrayList<?>> flowite = flowset.iterator();
			while(flowite.hasNext()){
				ArrayList<?> value = flowite.next();
				pw.println("  \"" + value.get(0) + "\" -> \"" + value.get(1) + "\";");
			}

			//ラベル・形状情報のコレクション書き出し
			Set<String> keys = labelshapemap.keySet();
			Iterator<String> labelshapeite = keys.iterator();
			while(labelshapeite.hasNext()){
				String key = (String)labelshapeite.next();
				ArrayList<?> value = labelshapemap.get(key);
				pw.println("  \"" + key + "\" [label = \"" + value.get(0) + "\", shape=" + value.get(1) + "];");
			}

			//終端部分の書き出し
			pw.println("}");
		}
		finally {
			Files.closeStream(pw);
			Files.closeStream(osw);
			Files.closeStream(fos);
			pw  = null;
			osw = null;
			fos = null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

// オリジナルソース記述：(MRI)八木様作成 : 2011/10/25
//    public static void main(String[] args) {
//		try{
//
//			/*
//			 ** amfファイルの読み込み処理
//			 */
//
//			FileInputStream fis = new FileInputStream(args[0]);
//			//amfファイルの文字コードはSJIS(MS932)を想定
//			InputStreamReader isr = new InputStreamReader(fis, "Shift-JIS"); 
//			BufferedReader br = new BufferedReader(isr);
//			//amfファイルからの各行読み込みバッファ
//			String buff;
//			//amfファイルの何行目を読んでいるか
//			int recno = 0;
//
//			//ラベル・形状情報のコレクション（ノード名⇒（ラベル文字列、形状）のハッシュ）
//			HashMap<String, ArrayList> labelshapemap = new HashMap<String, ArrayList>();
//			//フロー（矢印）情報のコレクション（（始点ノード名、終点ノード名）の配列）
//			ArrayList<ArrayList> flowset = new ArrayList<ArrayList>();
//
//			while((buff = br.readLine()) != null) {
//				recno++;
//
//				//amfの１行をカンマ区切りで切り出す
//				String[] array = buff.split(",(?=(([^\"]*\"){2})*[^\"]*$)");
//
//				//java行とmacro行のみを処理の対象
//				if(array[0].equals("java") || array[0].equals("macro")) {
//					//コメント列とモジュール列を取り出す
//					String comment = array[2];
//					String module = array[3]; //以下モジュールと呼ぶがサブマクロを指すこともある
//
//					//同じモジュールが繰り返し使われることがあるので、一意なノード名を別途作る
//					String modulestr = "module" + Integer.toString(recno);
//					ArrayList<String> labelshapemodule = new ArrayList<String>();
//					//モジュールのラベル文字列は、モジュール名とコメントを結合させたものにする
//					String modulecomment = module + "\\n" + comment;
//					//モジュールをラベル・形状情報のコレクションに登録
//					labelshapemodule.add(modulecomment);
//					labelshapemodule.add("ellipse"); //モジュールは楕円
//					labelshapemap.put(modulestr, labelshapemodule);
//
//					//引数（[IN][STR][OUT]）取り出し
//					for (int i = 7 ; i < array.length ; i++){
//						if(array[i].equals("[IN]") || array[i].equals("[STR]") || array[i].equals("[OUT]")) {
//							//引数の値（ファイル名など）を取り出す
//							String value = array[i+1];
//
//							//[IN][STR][OUT]をラベル・形状情報のコレクションに登録
//							ArrayList<String> labelshape = new ArrayList<String>();
//							labelshape.add(value);
//							if(array[i].equals("[IN]") || array[i].equals("[OUT]"))
//								labelshape.add("box"); //[IN][OUT]は矩形
//							else
//								labelshape.add("plaintext"); //[STR]はテキストのみ
//							labelshapemap.put(value, labelshape);
//
//							//モジュールと[IN][STR][OUT]の組をラベル・形状情報のコレクションに登録
//							ArrayList<String> flow = new ArrayList<String>();
//							if(array[i].equals("[IN]") || array[i].equals("[STR]")) {
//								flow.add(value); //[IN][STR]のときはモジュールに向かって矢印を引く
//								flow.add(modulestr);
//							} else {
//								flow.add(modulestr); //[OUT]のときはモジュールから矢印を引く
//								flow.add(value);
//							}
//							flowset.add(flow);
//						}
//					}
//				}
//			}
//
//			br.close();
//			isr.close();
//			fis.close();
//
//			/*
//			 ** dotファイルの書き出し処理
//			 */
//
//			FileOutputStream fos = new FileOutputStream(args[1]);
//			OutputStreamWriter osr = new OutputStreamWriter(fos, "UTF-8");
//			PrintWriter pw = new PrintWriter(osr);
//
//			//ヘッダ部分の書き出し
//			pw.println("digraph AADLmacroDraw {");
//			pw.println("  node [fontname = \"MS UI Gothic\"];");
//
//			//フロー（矢印）情報のコレクション書き出し
//			Iterator flowite = flowset.iterator();
//			while(flowite.hasNext()){
//				ArrayList value = (ArrayList) flowite.next();
//				pw.println("  \"" + value.get(0) + "\" -> \"" + value.get(1) + "\";");
//			}
//
//			//ラベル・形状情報のコレクション書き出し
//			Set<String> keys = labelshapemap.keySet();
//			Iterator labelshapeite = keys.iterator();
//			while(labelshapeite.hasNext()){
//				String key = (String)labelshapeite.next();
//				ArrayList value = (ArrayList) labelshapemap.get(key);
//				pw.println("  \"" + key + "\" [label = \"" + value.get(0) + "\", shape=" + value.get(1) + "];");
//			}
//
//			//終端部分の書き出し
//			pw.println("}");
//
//			pw.close();
//			osr.close();
//			fos.close();
//
//		} catch (IOException e) {
//			System.out.println("エラー");
//		}
//	}
}
