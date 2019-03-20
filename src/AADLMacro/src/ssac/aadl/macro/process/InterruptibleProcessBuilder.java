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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)InterruptibleManagedProcess.java	2.0.0	2014/03/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ssac.aadl.macro.util.Classes;
import ssac.aadl.macro.util.io.Files;
import ssac.aadl.macro.util.io.JarFileInfo;
import ssac.aadl.macro.util.io.ReportPrinter;
import ssac.aadl.macro.util.io.ReportStream;

/**
 * 外部からの中断可能なプロセスを起動するプロセスビルダー。
 * <p>このプロセスビルダーは、指定されたコマンドラインの起動コマンドが 'java' である場合、
 * 中断可能なプロセスでラップしたコマンドラインを生成する。このとき、プロセス生存制御ファイルが
 * 指定されていない場合、テンポラリ領域に独自の生存制御ファイルを生成する。<br>
 * 起動コマンドが 'java' ではない場合、指定されたコマンドラインのままプロセスを起動する。
 * この場合、プロセスは強制終了による中断処理となる。
 * 
 * @version 2.0.0	2014/03/18
 * @since 2.0.0
 */
public class InterruptibleProcessBuilder
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** プロセス生存制御ファイルのプレフィックス **/
	static public final String	ALIVEFILE_PREFIX	= "JALIVE";
	/** プロセス生存制御ファイルのサフィックス(拡張子) **/
	static public final String	ALIVEFILE_SUFFIX	= ".alive";
	/** プロセス強制終了制御ファイルのサフィックス(拡張子) **/
	static public final String	KILLFILE_SUFFIX	= ".kill";

	/** JAVA コマンドパターン **/
	static protected final Pattern[] JAVACMD_PATTERNS = {
		Pattern.compile(".*[/\\\\]?java", Pattern.DOTALL | Pattern.CASE_INSENSITIVE),
		Pattern.compile(".*[/\\\\]?javaw", Pattern.DOTALL | Pattern.CASE_INSENSITIVE),
		Pattern.compile(".*[/\\\\]?java\\.exe", Pattern.DOTALL | Pattern.CASE_INSENSITIVE),
		Pattern.compile(".*[/\\\\]?javaw\\.exe", Pattern.DOTALL | Pattern.CASE_INSENSITIVE),
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ラッパープロセスのメインクラスのクラスパス(絶対パス) **/
	static private String	s_wrapperProcessMainClassPath = null;

	/** プロセスビルダーオブジェクト **/
	private final ProcessBuilder	_builder;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたコマンドラインを持つプロセスビルダーを構築する。
	 * このコンストラクタは、<em>command</em> リストのコピーを作成しない。
	 * 以降のリストの更新は、プロセスビルダーの状態に反映される。
	 * <em>command</em> のプログラムが 'java' コマンドの場合、中断可能なプロセスとして実行される。
	 * なお、<em>command</em> が有効なコマンドかどうかはチェックされない。
	 * @param command	プログラムとプログラムの引数を含むリスト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public InterruptibleProcessBuilder(List<String> command) {
		_builder = new ProcessBuilder(command);
	}

	/**
	 * 指定されたコマンドラインを持つプロセスビルダーを構築する。
	 * このコンストラクタは、<em>command</em> 配列と同じ文字列を含む文字列リストを生成する。
	 * <em>command</em> のプログラムが 'java' コマンドの場合、中断可能なプロセスとして実行される。
	 * なお、<em>command</em> が有効なコマンドかどうかはチェックされない。
	 * @param command	プログラムとプログラムの引数を含む文字列配列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public InterruptibleProcessBuilder(String...command) {
		_builder = new ProcessBuilder(command);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * プロセス生存制御ファイルを生成する。
	 * プロセス生存制御ファイルのプレフィックスは &quot;JALIVE&quot;、サフィックスは &quot;.alive&quot; として、
	 * テンポラリディレクトリに作成される。作成後、{@link java.io.File#deleteOnExit()} の呼び出しにより、
	 * 呼び出し側プロセス終了後に破棄されるファイルとして設定される。
	 * @return	作成したファイルの抽象パス
	 * @throws IOException	ファイルの作成に失敗した場合
	 */
	static public File createProcessAliveFile() throws IOException
	{
		File fAlive = File.createTempFile(ALIVEFILE_PREFIX, ALIVEFILE_SUFFIX);
		fAlive.deleteOnExit();
		return fAlive;
	}
	
	/**
	 * プロセス停止制御ファイルを生成する。
	 * プロセス停止制御ファイルのプレフィックスは &quot;JALIVE&quot;、サフィックスは &quot;.kill&quot; として、
	 * テンポラリディレクトリに作成される。作成後、{@link java.io.File#deleteOnExit()} の呼び出しにより、
	 * 呼び出し側プロセス終了後に破棄されるファイルとして設定される。
	 * @return	作成したファイルの抽象パス
	 * @throws IOException	ファイルの作成に失敗した場合
	 */
	static public File createProcessKillFile() throws IOException
	{
		File fAlive = File.createTempFile(ALIVEFILE_PREFIX, KILLFILE_SUFFIX);
		fAlive.deleteOnExit();
		return fAlive;
	}

	/**
	 * このプロセスビルダーのコマンドラインを設定する。
	 * このメソッドは、<em>command</em> リストのコピーを作成しない。
	 * 以降のリストの更新は、プロセスビルダーの状態に反映される。
	 * <em>command</em> のプログラムが 'java' コマンドの場合、中断可能なプロセスとして実行される。
	 * なお、<em>command</em> が有効なコマンドかどうかはチェックされない。
	 * @param command	プログラムとプログラムの引数を含むリスト
	 * @return	このプロセスビルダー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public InterruptibleProcessBuilder command(List<String> command) {
		_builder.command(command);
		return this;
	}

	/**
	 * このプロセスビルダーのコマンドラインを設定する。
	 * このメソッドは、<em>command</em> 配列と同じ文字列を含む文字列リストを生成する。
	 * <em>command</em> のプログラムが 'java' コマンドの場合、中断可能なプロセスとして実行される。
	 * なお、<em>command</em> が有効なコマンドかどうかはチェックされない。
	 * @param command	プログラムとプログラムの引数を含む文字列配列
	 * @return	このプロセスビルダー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public InterruptibleProcessBuilder command(String...command) {
		_builder.command(command);
		return this;
	}

	/**
	 * プロセスビルダーのコマンドラインを返す。
	 * 返されたリストはコピーではないため、以降のリスト更新は、プロセスビルダーの状態に反映される。
	 * @return	プログラムとプログラムの引数を含むリスト
	 */
	public List<String> command() {
		return _builder.command();
	}

	/**
	 * このプロセスビルダーの環境の文字列マップのビューを返す。
	 * プロセスビルダーが作成されるたびに、環境は現在のプロセス環境のコピーに初期化される。
	 * このオブジェクトの {@link #start()} メソッドで起動される以降のサブプロセスは、このマップを環境として使用する。
	 * @return	このプロセスビルダーの環境
	 * @throws SecurityException	セキュリティーマネージャーが存在し、{@link java.lang.SecurityManager#checkPermission checkPermission} メソッドがプロセス環境へのアクセスを許可しないとき
	 * @see java.lang.ProcessBuilder#environment()
	 */
	public Map<String,String> environment() {
		return _builder.environment();
	}

	/**
	 * このプロセスビルダーの作業ディレクトリを返す。
	 * これ以降、このオブジェクトの {@link #start()} メソッドで起動されたサブプロセスは、このディレクトリを作業ディレクトリとして使用する。
	 * 戻り値は <tt>null</tt> の可能性がある。これは、現在の Java プロセスの作業ディレクトリ (通常は <code>user.dir</code> システムプロパティーで指定されたディレクトリ) を
	 * 子プロセスの作業ディレクトリとして使用することを意味する。
	 * @return	このプロセスビルダーの作業ディレクトリ
	 */
	public File directory() {
		return _builder.directory();
	}

	/**
	 * このプロセスビルダーの作業ディレクトリを設定する。
	 * これ以降、このオブジェクトの {@link #start()} メソッドで起動されたサブプロセスは、このディレクトリを作業ディレクトリとして使用する。
	 * 引数に <tt>null</tt> を指定した場合、現在の Java プロセスの作業ディレクトリ (通常は <code>user.dir</code> システムプロパティーで指定されたディレクトリ) を
	 * 子プロセスの作業ディレクトリとして使用することを意味する。
	 * @param directory	新規作業ディレクトリ
	 * @return	このプロセスビルダー
	 */
	public InterruptibleProcessBuilder directory(File directory) {
		_builder.directory(directory);
		return this;
	}

	/**
	 * このプロセスビルダーが標準エラーと標準出力をマージするかどうかを判定する。
	 * このメソッドが <tt>true</tt> を返した場合、このオブジェクトの {@link #start()} メソッドで起動されたサブプロセスにより生成されるエラー出力は、
	 * 標準出力とマージされる。これにより、{@link java.lang.Process#getInputStream()} メソッドを使って、エラー出力と標準出力の両方を読み取ることができる。
	 * マージのより、エラーメッセージと対応する出力との相関を示すのが容易になる。初期値は <tt>false</tt>。
	 * @return	このプロセスビルダーの <code>redirectErrorStream</code> プロパティー
	 */
	public boolean redirectErrorStream() {
		return _builder.redirectErrorStream();
	}

	/**
	 * このプロセスビルダーの <code>redirectErrorStream</code> プロパティーを設定する。
	 * このプロパティーが <tt>true</tt> の場合、このオブジェクトの {@link #start()} メソッドで起動されたサブプロセスにより生成されるエラー出力は、
	 * 標準出力とマージされる。これにより、{@link java.lang.Process#getInputStream()} メソッドを使って、エラー出力と標準出力の両方を読み取ることができる。
	 * マージのより、エラーメッセージと対応する出力との相関を示すのが容易になる。初期値は <tt>false</tt>。
	 * @param redirectErrorStream	新しいプロパティーの値
	 * @return	このプロセスビルダー
	 */
	public InterruptibleProcessBuilder redirectErrorStream(boolean redirectErrorStream) {
		_builder.redirectErrorStream(redirectErrorStream);
		return this;
	}

	/**
	 * このプロセスビルダーの属性を使って新規プロセスを起動する。
	 * この新規プロセスは、{@link #directory()} で指定された作業ディレクトリの、{@link #environment()} で指定されたプロセス環境を持つ、
	 * {@link #command()} で指定されたコマンドと引数を呼び出す。
	 * <p>サブプロセスの標準出力および標準エラー出力の内容は、自動生成されるスレッドによって、このプロセスの標準出力、標準エラー出力にそれぞれ出力される。
	 * <p>このメソッドは、{@link #start start} メソッドを次の引数で呼び出すのと同じ結果となる。
	 * <blockquote>
	 * <code>start(System.out, false, System.err, false);
	 * </blockquote>
	 * @return	サブプロセスを管理するための、新規 {@link InterruptibleManagedProcess} オブジェクト
	 * @throws NullPointerException	コマンドリストの要素が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	コマンドが空 (サイズが 0) のリストの場合
	 * @throws SecurityException	セキュリティーマネージャーが存在し、{@link SecurityManager#checkExec checkExec} メソッドがサブプロセスの作成を許可しない場合
	 * @throws IOException	入出力エラーが発生した場合
	 * @see #start(OutputStream, boolean, OutputStream, boolean)
	 */
	public InterruptibleManagedProcess start() throws IOException
	{
		return start(new ReportStream(System.out), false, new ReportStream(System.err), false);
	}

	/**
	 * このプロセスビルダーの属性を使って新規プロセスを起動する。
	 * この新規プロセスは、{@link #directory()} で指定された作業ディレクトリの、{@link #environment()} で指定されたプロセス環境を持つ、
	 * {@link #command()} で指定されたコマンドと引数を呼び出す。
	 * <p>サブプロセスの標準出力および標準エラー出力の内容は、自動生成されるスレッドによって、指定されたストリームに出力される。
	 * <em>outReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準出力の内容は破棄される。
	 * <em>errReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準エラー出力の内容は破棄される。
	 * <p>このメソッドは、{@link #start start} メソッドを次の引数で呼び出すのと同じ結果となる。
	 * <blockquote>
	 * <code>start(<em>outReceiver</em>, false, <em>errReceiver</em>, false);
	 * </blockquote>
	 * @param outReceiver			プロセス標準出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @param errReceiver			プロセス標準エラー出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @return	サブプロセスを管理するための、新規 {@link InterruptibleManagedProcess} オブジェクト
	 * @throws NullPointerException	コマンドリストの要素が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	コマンドが空 (サイズが 0) のリストの場合
	 * @throws SecurityException	セキュリティーマネージャーが存在し、{@link SecurityManager#checkExec checkExec} メソッドがサブプロセスの作成を許可しない場合
	 * @throws IOException	入出力エラーが発生した場合
	 * @see #start(OutputStream, boolean, OutputStream, boolean)
	 */
	public InterruptibleManagedProcess start(ReportPrinter outReceiver, ReportPrinter errReceiver) throws IOException
	{
		return start(outReceiver, false, errReceiver, false);
	}

	/**
	 * このプロセスビルダーの属性を使って新規プロセスを起動する。
	 * この新規プロセスは、{@link #directory()} で指定された作業ディレクトリの、{@link #environment()} で指定されたプロセス環境を持つ、
	 * {@link #command()} で指定されたコマンドと引数を呼び出す。
	 * <p>サブプロセスの標準出力および標準エラー出力の内容は、自動生成されるスレッドによって、指定されたストリームに出力される。
	 * <em>outReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準出力の内容は破棄される。
	 * <em>errReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準エラー出力の内容は破棄される。
	 * <p>このメソッドは、コマンドが有効なオペレーティングシステムコマンドであることをチェックする。
	 * どのコマンドが有効かはシステム依存となるが、コマンドは少なくとも <tt>null</tt> 以外の文字列の空でないリストである必要がある。
	 * <p>コマンドが 'Java' コマンドの場合、中断可能なプロセスとしてラッパープロセスを起動します。このラッパープロセスでは、指定されたメインクラスをメインスレッドから直接呼び出す。
	 * また、このラッパープロセスがプロセス生存制御ファイルを監視し、この生存制御ファイルが消去されたときにプロセスを中断させる。
	 * プロセス生存制御ファイルが指定されていない場合、サブプロセスを実行する前に生存制御ファイルを自動的に生成する。
	 * <p>セキュリティーマネージャーが存在する場合は、<code>command</code> 配列の最初のコンポーネントを引数として使用し {@link SecurityManager#checkExec checkExec} メソッドが呼び出される。
	 * この結果、{@link java.lang.SecurityException} がスローされることがある。
	 * <p>オペレーティングシステムプロセスの起動はシステムに依存する。発生する可能性がある不具合は次の通り。
	 * <ul>
	 * <li>オペレーティングシステムプログラムファイルが見つからなかった
	 * <li>プログラムファイルへのアクセス権が拒否された
	 * <li>作業ディレクトリが存在しない
	 * <li>Java コマンドの内容が適切でない
	 * <li>プロセス生存制御ファイルが作成できない
	 * </ul>
	 * 以上のケースでは、例外がスローされる。例外の正確な特性はシステムに依存するが、これは常に {@link java.io.IOException} のサブクラスになる。
	 * このプロセスビルダーへの変更は、このメソッドから返されたプロセスオブジェクトに影響を及ぼさない。
	 * @param outReceiver			プロセス標準出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @param autoCloseOutReceiver	<em>outReceiver</em> をプロセス終了時に自動的に閉じる場合は <tt>true</tt>、閉じない場合は <tt>false</tt> を指定する
	 * @param errReceiver			プロセス標準エラー出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @param autoCloseErrReceiver	<em>errReceiver</em> をプロセス終了時に自動的に閉じる場合は <tt>true</tt>、閉じない場合は <tt>false</tt> を指定する
	 * @return	サブプロセスを管理するための、新規 {@link InterruptibleManagedProcess} オブジェクト
	 * @throws NullPointerException	コマンドリストの要素が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	コマンドが空 (サイズが 0) のリストの場合
	 * @throws SecurityException	セキュリティーマネージャーが存在し、{@link SecurityManager#checkExec checkExec} メソッドがサブプロセスの作成を許可しない場合
	 * @throws IOException	入出力エラーが発生した場合
	 * @see java.lang.Runtime#exec(String[], String[], File)
	 * @see java.lang.SecurityManager#checkExec(String)
	 */
	public InterruptibleManagedProcess start(ReportPrinter outReceiver, boolean autoCloseOutReceiver,
											ReportPrinter errReceiver, boolean autoCloseErrReceiver)
	throws IOException
	{
		// JAVA コマンドか判定
		boolean javaCommand = isJavaCommand(_builder.command());
		
		// JAVA コマンド以外は、コマンドラインそのままでプロセス起動
		if (!javaCommand) {
			if (VerboseStream.isDebug()) {
				VerboseStream.categoryDebug("InterruptibleProcessBuilder#start", "Start target non-java process...");
				List<String> cmdlist = _builder.command();
				for (int i = 0; i < cmdlist.size(); ++i) {
					VerboseStream.formatCategoryDebug("InterruptibleProcessBuilder#start", "command[%d]=%s", i, String.valueOf(cmdlist.get(i)));
				}
			}
			Process proc = _builder.start();
			InterruptibleManagedProcess managedProc = new InterruptibleManagedProcess(null, null, proc,
																		outReceiver, autoCloseOutReceiver,
																		errReceiver, autoCloseErrReceiver);
			return managedProc;
		}
		
		// 制御ファイルの生成
		File fAlive = null;
		File fKill  = null;
		try {
			fAlive = createProcessAliveFile();
			fKill  = createProcessKillFile();
		}
		finally {
			if (fAlive == null || fKill == null) {
				if (fAlive != null) {
					fAlive.delete();
				}
				if (fKill != null) {
					fKill.delete();
				}
			}
		}
		
		// 新しいコマンドラインの生成
		List<String> newCmdList;
		if (findJarOptionPosition(_builder.command(), 1) < 0) {
			// '-jar' オプションなし
			newCmdList = createInterruptibleJavaCommand(fAlive, fKill);
		} else {
			// '-jar' オプションによる Jar ファイル起動
			newCmdList = createInterruptibleJarCommand(fAlive, fKill);
		}
		
		// 新しいプロセスビルダーの生成
		ProcessBuilder pb = new ProcessBuilder(newCmdList);
		pb.directory(_builder.directory());
		pb.redirectErrorStream(_builder.redirectErrorStream());
		
		// プロセスの生成
		if (VerboseStream.isDebug()) {
			VerboseStream.categoryDebug("InterruptibleProcessBuilder#start", "Start target process...");
			for (int i = 0; i < newCmdList.size(); ++i) {
				VerboseStream.formatCategoryDebug("InterruptibleProcessBuilder#start", "command[%d]=%s", i, String.valueOf(newCmdList.get(i)));
			}
		}
		Process proc = pb.start();
		InterruptibleManagedProcess managedProc = new InterruptibleManagedProcess(fAlive, fKill, proc,
																		outReceiver, autoCloseOutReceiver,
																		errReceiver, autoCloseErrReceiver);
		return managedProc;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * プロセス制御ファイルが正当化を確認する。
	 * @param file	対象ファイルの抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定されたファイルが存在しない、もしくはファイルではない場合
	 */
	static protected void validProcessControlFile(File file) {
		if (!file.exists())
			throw new IllegalArgumentException("Process control file does not exists.");
		if (!file.isFile())
			throw new IllegalArgumentException("Process control file is not a file.");
	}

	/**
	 * コマンドリストのコマンドが JAVA コマンドでなければ例外をスローする。
	 * @param cmdlist	コマンドリスト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	JAVA コマンドではない場合
	 */
	protected void validJavaCommand(List<String> cmdlist) {
		if (!isJavaCommand(cmdlist)) {
			// no java command
			throw new IllegalStateException("Command is not Java : " + cmdlist.toString());
		}
	}

	/**
	 * コマンドリストのコマンドが JAVA コマンドかどうかを判定する。
	 * @param cmdlist	コマンドリスト
	 * @return	JAVA コマンドであれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected boolean isJavaCommand(List<String> cmdlist) {
		if (!cmdlist.isEmpty()) {
			String strCommand = cmdlist.get(0);
			if (strCommand != null && strCommand.length() > 0) {
				for (Pattern pat : JAVACMD_PATTERNS) {
					if (pat.matcher(strCommand).matches()) {
						return true;
					}
				}
			}
		}
		// not java command
		return false;
	}

	/**
	 * JAVA コマンドオプションの '-jar' オプションかどうかを判定する。
	 * @param value	引数の文字列
	 * @return	jar オプションであれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean isJarVmOption(String value) {
		return ("-jar".equals(value));
	}

	/**
	 * JAVA コマンドオプションの '-cp' もしくは '-classpath' かどうかを判定する。
	 * @param value	引数の文字列
	 * @return	クラスパスオプションであれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean isClassPathVmOption(String value) {
		if ("-classpath".equals(value) || "-cp".equals(value)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * JAVA コマンドのオプションのうち、後続の値を必要とするオプションかどうかを判定する。
	 * 現時点で、後続の値を必要とするオプションは、'-cp' もしくは '-classpath' のみである。
	 * @param value	引数の文字列
	 * @return	後続の値を必要とするオプションであれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean isNextSkipVmOption(String value) {
		// 現時点では、classpath 以外にオプションと値が連続する引数はない
		return isClassPathVmOption(value);
	}

	/**
	 * JAVA コマンドのオプションのうち、後続の値を必要としない単独オプションかどうかを判定する。
	 * @param value	引数の文字列
	 * @return	単独オプションであれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean isOneShotVmOption(String value) {
		if (value.startsWith("-")) {
			return (!isNextSkipVmOption(value));
		} else {
			return false;
		}
	}

	/**
	 * JAVA コマンド引数リストから、'-jar' オプションの位置を検索する。
	 * @param cmdlist	コマンドリスト
	 * @param beginPos	検索開始位置
	 * @return	オプションが見つかった場合はそのオプションの位置を示すインデックス、見つからなかった場合は (-1)
	 */
	protected int findJarOptionPosition(List<String> cmdlist, int beginPos) {
		int len = cmdlist.size();
		int index = Math.max(beginPos, 1);
		for (; index < len; ++index) {
			String cmd = cmdlist.get(index);
			if (isNextSkipVmOption(cmd)) {
				++index;
			}
			else if (isJarVmOption(cmd)) {
				return index;
			}
			else if (!isOneShotVmOption(cmd)) {
				break;	// no vm option
			}
		}
		// class path option not found
		return (-1);
	}

	/**
	 * JAVA コマンド引数リストから、クラスパスの位置を検索する。
	 * このメソッドが検索するのは、'-cp' もしくは '-classpath' オプションの次の引数となる。
	 * @param cmdlist	コマンドリスト
	 * @param beginPos	検索開始位置
	 * @return	クラスパスが見つかった場合はそのクラスパスの値の位置を示すインデックス、見つからなかった場合は (-1)
	 */
	protected int findClassPassPosition(List<String> cmdlist, int beginPos) {
		int len = cmdlist.size();
		int index = Math.max(beginPos, 1);
		for (; index < len; ++index) {
			String cmd = cmdlist.get(index);
			if (isNextSkipVmOption(cmd)) {
				++index;
				if (isClassPathVmOption(cmd)) {
					return index;
				}
			}
			else if (!isOneShotVmOption(cmd)) {
				break;	// no vm option
			}
		}
		// class path option not found
		return (-1);
	}

	/**
	 * JAVA コマンド引数リストから、メインクラス名の位置を検索する。
	 * @param cmdlist	コマンドリスト
	 * @param beginPos	検索開始位置
	 * @return	メインクラス名が見つかった場合はその位置を示すインデックス、見つからなかった場合は (-1)
	 */
	protected int findMainClassPosition(List<String> cmdlist, int beginPos) {
		int len = cmdlist.size();
		int index = Math.max(beginPos, 1);
		for (; index < len; ++index) {
			String cmd = cmdlist.get(index);
			if (isNextSkipVmOption(cmd)) {
				++index;
			}
			else if (!isOneShotVmOption(cmd)) {
				// Main class name
				return index;
			}
		}
		// Main class name not found
		return (-1);
	}

	/**
	 * JAVA コマンド引数リストから、'-jar' オプションをすべて削除する。
	 * @param cmdlist	コマンドリスト
	 * @return	削除した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean removeJarOption(List<String> cmdlist) {
		boolean removed = false;
		for (;;) {
			int pos = findJarOptionPosition(cmdlist, 1);
			if (pos < 0) {
				break;	// not found
			} else {
				// found class path value, remove
				cmdlist.remove(pos);
				removed = true;
			}
		}
		return removed;
	}

	/**
	 * JAVA コマンド引数リストから、クラスパスオプションをすべて削除する。
	 * @param cmdlist	コマンドリスト
	 * @return	削除した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean removeClassPathOption(List<String> cmdlist) {
		boolean removed = false;
		for (;;) {
			int pos = findClassPassPosition(cmdlist, 1);
			if (pos < 0) {
				break;	// not found
			} else {
				// found class path value, remove
				cmdlist.remove(pos);
				cmdlist.remove(pos-1);
				removed = true;
			}
		}
		return removed;
	}

	/**
	 * ラッパープロセスのメインクラスのクラスパスを取得する。
	 * @return	<code>InterruptibleJavaMainProcessImpl</code> クラスのクラスパス(絶対パス)を示す文字列
	 */
	protected String getInterruptibleProcessClassPath() {
		if (s_wrapperProcessMainClassPath == null) {
			File f = Classes.getClassSource(InterruptibleJavaMainProcessImpl.class);
			s_wrapperProcessMainClassPath = f.getAbsolutePath();
		}
		return s_wrapperProcessMainClassPath;
	}

	/**
	 * ラッパープロセスのメインクラス名を取得する。
	 * @return	<code>InterruptibleJavaMainProcessImpl</code> クラスの完全名
	 */
	protected String getInterruptibleProcessClassName() {
		return InterruptibleJavaMainProcessImpl.class.getName();
	}

	/**
	 * JAVA コマンドラインの引数から、ラッパープロセス実行用コマンドラインを生成する。
	 * このメソッドは、'-jar' オプションが指定された Java コマンドを前提としており、
	 * 起動する Jar ファイルから、クラスパスとメインクラスを取得して、新しいコマンドラインを生成する。
	 * @param fileForAlive	プロセス生存制御ファイルの抽象パス
	 * @param fileForKill	プロセス強制終了制御ファイルの抽象パス
	 * @return	生成されたコマンドライン
	 * @throws IOException	Jar ファイルおよびメインクラス名が見つからない場合
	 */
	protected List<String> createInterruptibleJarCommand(File fileForAlive, File fileForKill) throws IOException
	{
		// copy original command line
		ArrayList<String> cmdlist = new ArrayList<String>(_builder.command().size() + 5);
		cmdlist.addAll(_builder.command());
		
		// クラスパスの指定は除去する
		removeClassPathOption(cmdlist);
		
		// '-jar' オプションの指定は除去する
		removeJarOption(cmdlist);
		
		// メインクラス名(jarファイル名)を取得
		int mainNamePos = findMainClassPosition(cmdlist, 1);
		if (mainNamePos < 0) {
			// Main-class name not found
			throw new IOException("Jar file name not found! : " + cmdlist.toString());
		}
		File jarFile = new File(cmdlist.get(mainNamePos));
		if (!jarFile.isAbsolute()) {
			// jar ファイル名が絶対パスではない場合、実行ディレクトリから絶対パスを取得する
			File workdir = _builder.directory();
			if (workdir != null) {
				jarFile = new File(workdir, cmdlist.get(mainNamePos)).getAbsoluteFile();
			} else {
				jarFile = jarFile.getAbsoluteFile();
			}
			jarFile = Files.normalizeFile(jarFile);
		}
		
		// jar ファイルの存在チェック
		if (!jarFile.exists()) {
			throw new FileNotFoundException("\"" + jarFile.toString() + "\" file not found!");
		}
		
		// jar ファイルの解析
		JarFileInfo jarinfo = new JarFileInfo(jarFile);
		String jarClassPath = Classes.toClassPathString(null, jarinfo.getManifestClassPathArray());
		String jarMainClass = jarinfo.getManifestMainClass();
		if (jarMainClass == null || jarMainClass.length() <= 0) {
			// no main class in manifest
			throw new IOException("Main class name not found in Jar manifest! : " + cmdlist.toString());
		}
		
		// Jarファイル名をメインクラス名に変更
		cmdlist.set(mainNamePos, jarMainClass);
		
		// modify arguments for Interruptible process
		cmdlist.add(mainNamePos++, getInterruptibleProcessClassName());
		cmdlist.add(mainNamePos++, fileForAlive.getAbsolutePath());
		cmdlist.add(mainNamePos++, fileForKill.getAbsolutePath());
		
		// クラスパスの追加
		StringBuilder sbClassPath = new StringBuilder();
		sbClassPath.append(getInterruptibleProcessClassPath());
		sbClassPath.append(File.pathSeparator);
		if (jarClassPath.length() > 0) {
			sbClassPath.append(jarClassPath);
		}
		else {
			// no class-path
			//--- check environment variable
			String strEnvClassPath = System.getenv("CLASSPATH");
			if (strEnvClassPath != null && strEnvClassPath.length() > 0) {
				//--- has class-path in environment
				sbClassPath.append(strEnvClassPath);
			} else {
				//--- no class-path, class-path is current"."
				sbClassPath.append(".");
			}
		}
		//--- insert class-path
		cmdlist.add(1, "-cp");
		cmdlist.add(2, sbClassPath.toString());
		
		// succeeded
		return cmdlist;
	}

	/**
	 * JAVA コマンドラインの引数から、ラッパープロセス実行用コマンドラインを生成する。
	 * このメソッドは、クラスパスとメインクラスが指定された Java コマンドを前提としている。
	 * @param fileForAlive	プロセス生存制御ファイルの抽象パス
	 * @param fileForKill	プロセス強制終了制御ファイルの抽象パス
	 * @return	生成されたコマンドライン
	 * @throws IOException	メインクラス名が見つからない場合
	 */
	protected List<String> createInterruptibleJavaCommand(File fileForAlive, File fileForKill) throws IOException
	{
		// copy original command line
		ArrayList<String> cmdlist = new ArrayList<String>(_builder.command().size() + 5);
		cmdlist.addAll(_builder.command());
		
		// find class pass posision
		int cpValuePos = findClassPassPosition(cmdlist, 1);
		
		// modify class-path
		StringBuilder sbClassPath = new StringBuilder();
		sbClassPath.append(getInterruptibleProcessClassPath());
		sbClassPath.append(File.pathSeparator);
		if (cpValuePos > 0) {
			// class-path specified
			sbClassPath.append(cmdlist.get(cpValuePos));
			//--- change class-path
			cmdlist.set(cpValuePos, sbClassPath.toString());
		}
		else {
			// no class-path
			//--- check environment variable
			String strEnvClassPath = System.getenv("CLASSPATH");
			if (strEnvClassPath != null && strEnvClassPath.length() > 0) {
				//--- has class-path in environment
				sbClassPath.append(strEnvClassPath);
			} else {
				//--- no class-path, class-path is current"."
				sbClassPath.append(".");
			}
			//--- insert class-path
			cmdlist.add(1, "-cp");
			cmdlist.add(2, sbClassPath.toString());
		}
		
		// find main class name position
		int mainNamePos = findMainClassPosition(cmdlist, 1);
		if (mainNamePos < 0) {
			// Main-class name not found
			throw new IOException("Main class name not found! : " + cmdlist.toString());
		}
		
		// modify arguments for Interruptible process
		cmdlist.add(mainNamePos++, getInterruptibleProcessClassName());
		cmdlist.add(mainNamePos++, fileForAlive.getAbsolutePath());
		cmdlist.add(mainNamePos++, fileForKill.getAbsolutePath());
		
		// succeeded
		return cmdlist;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
