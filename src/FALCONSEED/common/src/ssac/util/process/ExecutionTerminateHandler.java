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
 * @(#)ExecutionTerminateHandler.java	3.0.0	2014/03/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

/**
 * コマンド実行終了時に、コマンド（プロセス）監視スレッドから呼び出されるインタフェース。
 * 
 * @version 3.0.0	2014/03/25
 * @since 3.0.0
 */
public interface ExecutionTerminateHandler
{
	/**
	 * コマンド（プロセス）終了時に呼び出されるメソッド。
	 * このメソッドは、コマンド（プロセス）が終了の状態に関わらず、実行が停止した時点で、
	 * コマンド監視スレッドから呼び出される。
	 * <p><b><i>注意：</i></b>
	 * <blockquote>
	 * このメソッド内での処理は、コマンド監視スレッド内での処理となる。
	 * </blockquote>
	 * @param executor	実行が終了したプロセスを管理するコマンドエグゼキューター
	 */
	public void executionTerminated(InterruptibleCommandExecutor executor);
}
