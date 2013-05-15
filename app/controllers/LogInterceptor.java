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
