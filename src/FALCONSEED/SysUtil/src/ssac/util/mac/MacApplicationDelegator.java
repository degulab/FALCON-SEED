/*
 * @(#)MacUtilities.java	2012/11/06
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacUtilities.java	2012/07/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.mac;

import java.awt.Image;
import java.awt.PopupMenu;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

/**
 * Mac OS X 用のアプリケーションオブジェクト利用のための仲介クラス。
 * 
 * @version 2012/11/06
 */
@SuppressWarnings({ "restriction", "deprecation" })
public class MacApplicationDelegator
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private ApplicationAdapter _curAppListener = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * Dock メニューを取得する。
	 * @return	Dock メニューの {@link java.awt.PopupMenu} オブジェクト
	 */
	static public PopupMenu getDockMenu() {
		return Application.getApplication().getDockMenu();
	}

	/**
	 * Dock メニューを設定する
	 * @param pmenu	{@link java.awt.PopupMenu} オブジェクト
	 */
	static public void setDockMenu(PopupMenu pmenu) {
		Application.getApplication().setDockMenu(pmenu);
	}

	/**
	 * アプリケーションの Dock アイコンを取得する。
	 * @return	{@link java.awt.Image} オブジェクト
	 */
	static public Image getDockIconImage() {
		return Application.getApplication().getDockIconImage();
	}

	/**
	 * アプリケーションの Dock アイコンを設定する。
	 * @param icon	{@link java.awt.Image} オブジェクト
	 */
	static public void setDockIconImage(Image icon) {
		Application.getApplication().setDockIconImage(icon);
	}

	/**
	 * アプリケーションの Dock アイコンに、アイコンバッジを設定する。
	 * @param badge	アイコンバッジとして表示する文字列
	 */
	static public void setDockIconBadge(String badge) {
		Application.getApplication().setDockIconBadge(badge);
	}

	/**
	 * スクリーンメニューのシステムメニュー項目に対応するハンドラを設定する。
	 * このメソッドによってすでにハンドラが設定されている場合、既存のハンドラを
	 * 除去した後、指定されたハンドラを設定する。
	 * このメソッドでハンドリングするイベントは、About と Quit のみ。
	 * <em>handler</em> に <tt>null</tt> を指定した場合、既存のハンドラを除去するのみとなる。
	 * @param handler	<code>MacScreenMenuHandler</code> オブジェクト
	 */
	static public synchronized void setupScreenMenuHandler(final MacScreenMenuHandler handler) {
		Application app = Application.getApplication();
		
		// 既存のハンドラを除去
		if (_curAppListener != null) {
			app.removeApplicationListener(_curAppListener);
			_curAppListener = null;
		}
		
		// 新しいハンドラを設定
		if (handler != null) {
			_curAppListener = new ApplicationAdapter(){
				@Override
				public void handleAbout(ApplicationEvent event) {
					handler.onMacMenuAbout();
					event.setHandled(true);		// OSXデフォルトのダイアログを表示させない
				}

				@Override
				public void handleQuit(ApplicationEvent event) {
					handler.onMacMenuQuit();
					event.setHandled(false);	// 自動的に終了させない
				}
			};
			app.addApplicationListener(_curAppListener);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
