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
 */
/*
 * @(#)IMExecArgsDialog.java	1.22	2012/08/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.util.io.VirtualFile;
import ssac.util.swing.IDialogResult;

/**
 * モジュール実行時引数ダイアログ共通のインタフェース。
 * 
 * @version 1.22	2012/08/22
 * @since 1.22
 */
public interface IMExecArgsDialog extends IDialogResult
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このコンポーネントに、エディタビューで表示されているファイルの抽象パスを設定する。
	 * @param newFile	設定する抽象パス。抽象パスを設定しない場合は <tt>null</tt>
	 */
	public void setViewingFileOnEditor(VirtualFile newFile);

	/**
	 * ダイアログが表示されている場合はダイアログを閉じ、リソースを開放する。
	 * このメソッドでは、{@link java.awt.Window#dispose()} を呼び出す。
	 */
	public void destroy();

	/**
	 * このコンポーネントが表示可能かどうかを判定します。コンポーネントは、ネイティブスクリーンリソースに接続されている場合に表示可能になります。
	 * コンポーネントが表示可能になるのは、包含関係の階層に追加されたとき、あるいは包含関係の階層が表示可能になったときです。
	 * 包含関係の階層は、その上位のウィンドウがパックされるか、可視になると表示可能になります。コンポーネントが表示不可になるのは、
	 * 表示可能な包含関係の階層から削除されたとき、あるいは包含関係の階層が表示不可になったときです。
	 * 包含関係の階層は、上位のウィンドウが破棄されると表示不可になります。
	 * @return	コンポーネントが表示可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isDisplayable();

	/**
	 * 親が可視になったときにこのコンポーネントが可視になるかどうかを判定します。
	 * 初期状態ではコンポーネントは可視ですが、Frame オブジェクトなどのトップレベルコンポーネントの場合は例外です。
	 * @return	コンポーネントが可視の場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isVisible();

	/**
	 * このウィンドウが可視の場合、このウィンドウを前面に移動してフォーカスされたウィンドウにできるようにします。
	 * このウィンドウをスタックの最上位に置き、この VM のほかのすべてのウィンドウの前に表示します。このウィンドウが可視でない場合、処理は何も行われません。
	 * 一部のプラットフォームでは、ほかのウィンドウを持つウィンドウをこのような所有されたウィンドウの一番上に表示することはできません。
	 * 一部のプラットフォームでは、この VM をネイティブアプリケーションのウィンドウ、またはほかの VM のウィンドウの上に置くことを許可していません。
	 * このアクセス権はこの VM のウィンドウがフォーカス済みであるかどうかに依存します。
	 * このウィンドウをスタックされる順のできるだけ上位に移動するあらゆる試行が行われます。
	 * ただし、開発者は、すべての状況でこのメソッドによって、このウィンドウがほかのすべてのウィンドウの上に移動するものと見なさないでください。
	 * ネイティブなウィンドウ処理システムのバリエーションのため、フォーカスされたアクティブなウィンドウに対して行われた変更は保証されません。
	 * 開発者は、このウィンドウが WINDOW_GAINED_FOCUS または WINDOW_ACTIVATED イベントを受け取るまで、このウィンドウがフォーカスされているか、
	 * またはアクティブなウィンドウであると決して見なしてはなりません。最上位のウィンドウがフォーカスされたウィンドウであるプラットフォームでは、
	 * まだフォーカスされていない場合は、このメソッドは、ほとんどの場合このウィンドウをフォーカスします。
	 * スタックされる順が、通常フォーカスされたウィンドウに影響しないプラットフォームでは、このメソッドは、
	 * フォーカスされたアクティブなウィンドウをほとんど変更されないままにします。
	 * このメソッドがこのウィンドウをフォーカスされた状態にし、このウィンドウが Frame または Dialog の場合、
	 * このウィンドウもアクティブになります。このウィンドウがフォーカスされているが Frame または Dialog でない場合、
	 * このウィンドウの最初の Frame または Dialog がアクティブになります。
	 */
	public void toFront();
}
