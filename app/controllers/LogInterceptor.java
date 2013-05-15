/*
 * 作成日 2011/10/12
 * 修正日 2012/02/23 命名ルールの変更による
 * 修正日 2012/06/05 コメントの変更（WZアウトライン用記号の削除）
 */
package controllers;

import play.Logger;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;


/**
 * ログインターセプター(Method)・コントローラー.
 *
 * @author H.Kezuka
 */
public class LogInterceptor extends Controller {

	@Before
	/**
	 * void method
	 */
	public static void begin() {
		Logger.info("[BEGIN]........" + request.controller + "/"
				+ request.actionMethod);
	}

	@After
	/**
	 * void method
	 */
	public static void end() {
		Logger.info("[E N D]........" + request.controller + "/"
				+ request.actionMethod + "\n");
	}

}
