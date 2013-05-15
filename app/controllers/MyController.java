package controllers;

import java.util.Date;

import models.Account;
import models.labels.Category;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;

/**
 * 独自コントローラー基底クラス.
 *
 * @author H.Kezuka
 */
//@With(LogInterceptor.class)
public class MyController extends Controller {

	// =============================================*
	// 定数
	// =============================================*
	public static final String CONSTANT_LOCALPATH_PROP_NAME = "constant.localpath";

	/* ************************************************************ */
	/*
	 * 例外のログ処理
	 */
	/* ************************************************************ */
	//
	@Catch(value = Throwable.class, priority = 1)
	public static void logThrowable(Throwable _throwable) {
		// Custom error logging…
		Logger.error("EXCEPTION %s", _throwable);
	}

	@Catch(value = IllegalStateException.class, priority = 2)
	public static void logIllegalState(Throwable _throwable) {
		Logger.error("Illegal state %s…", _throwable);
	}

//	@Before
//	static void debugPrint() {
//		System.out.println(
//				new Throwable().getStackTrace()[0].getMethodName());
//		System.out.println(
//				Thread.currentThread().getStackTrace()[i].getMethodName());
//	}

	/* ************************************************************ */
	/*
	 * テンプレート変数
	*/
	/* ************************************************************ */
	@Before
	static void defineTemplateArgs() {
		renderArgs.put("siteName", Play.configuration.getProperty("site.name"));
		renderArgs.put("siteBaseline",
				Play.configuration.getProperty("site.baseline"));
		renderArgs.put("categories", Category.findAll());
		String sessionId = session.getId();
		Account account = getCacheAccount(sessionId);
		if (account!= null) {
			renderArgs.put("account", account);
		}
	}

	/* ************************************************************ */
	/*
	 * ヘルパメソッド
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * フラッシュ保存情報
	 * セッションより短い、遷移時に渡すだけの情報。
	 * データは 1 リクエストまで保持される。
	 * フラッシュ向けのクッキーは署名されていないため、
	 * ユーザによって変更される可能性がある。
	 *
	 * 2013/05/02 flash -> session に変更
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// 定数
	public static final String LOGIN_STATUS_PREFIX = "lgin";

	// (flash)ログインフラグ
    protected static boolean isLogin(String _sessionId) {
        return toBoolean(session.get(LOGIN_STATUS_PREFIX + _sessionId));
    }

    protected static void setLogin(String _sessionId, boolean _isLogin) {
        session.put(LOGIN_STATUS_PREFIX + _sessionId, _isLogin);
    }

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * セッション保存情報
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// 定数
	public static final String USER_ID_PREFIX = "uid";
	public static final String SCREEN_NAME_PREFIX = "snm";
	public static final long USER_ID_DEFAULT = -1;

	// (session)Twitter.userId
	protected static long getSessionUserId(String _sessionId) {
		if(isBlank(_sessionId)){
			return USER_ID_DEFAULT;
		}
		String uid = session.get(USER_ID_PREFIX + _sessionId);
		return (uid != null) ? Long.parseLong(uid) : USER_ID_DEFAULT;
	}

	protected static void setSessionUserId(String _sessionId, long _id) {
		if(isBlank(_sessionId)){
			return;
		}
		session.put(USER_ID_PREFIX + _sessionId, _id);
	}

	protected static void removeSessionUserId(String _sessionId) {
		session.remove(USER_ID_PREFIX + _sessionId);
	}

	// =============================================*
	// (session)Twitter.screenName
	protected static String getSessionScreenName(String _sessionId) {
		if(isBlank(_sessionId)){
			return null;
		}
		return session.get(SCREEN_NAME_PREFIX + _sessionId);
	}

	protected static void setSessionScreenName(String _sessionId, String _screenName) {
		if(isBlank(_sessionId)){
			return;
		}
		session.put(SCREEN_NAME_PREFIX + _sessionId, _screenName);
	}

	protected static void removeSessionScreenName(String _sessionId) {
		session.remove(SCREEN_NAME_PREFIX + _sessionId);
	}

	// =============================================*
	protected static void printSessionData(
			long _userId, String _screenName, int _number) {
		Logger.debug("userId    (%d):%s", _number, _userId);
		Logger.debug("screenName(%d):%s", _number, _screenName);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * キャッシュ保存情報
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// 定数
	public static final String REQUEST_TOKEN_PREFIX = "req";
	public static final String SESSION_ID_PREFIX = "sid";
	public static final String ACCOUNT_PREFIX = "acnt";

	public static final String CACHE_LIFE_PROP_NAME = "cache.life";

	// クラスメンバ
	public static String CACHE_LIFE;

	protected static String setCacheLife() {
		return Play.configuration.getProperty(CACHE_LIFE_PROP_NAME);
	}

	protected static String getCacheLife() {
		if (isBlank(CACHE_LIFE)) {
			setCacheLife();
			Logger.debug("CACHE_LIFE_PROP_NAME:%s", CACHE_LIFE);
		}
		return CACHE_LIFE;
	}

	// =============================================*
//	// (cache)アクセストークン

	// =============================================*
	// (cache)リクエストトークン
	protected static RequestToken getCacheRequestToken(String _sessionId) {
		if(isBlank(_sessionId)){
			return null;
		}
		return Cache.get(REQUEST_TOKEN_PREFIX + _sessionId, RequestToken.class);
	}

	protected static void setCacheRequestToken(
			String _sessionId, RequestToken _token, String _cacheLife) {
		if(isBlank(_sessionId)){
			return;
		}
		Cache.set(REQUEST_TOKEN_PREFIX + _sessionId, _token, _cacheLife);
	}

	protected static void deleteCacheRequestToken(String _sessionId) {
		Cache.delete(REQUEST_TOKEN_PREFIX + _sessionId);
	}

	// =============================================*
	// (cache)アカウント情報用クラス
	protected static Account getCacheAccount(String _sessionId) {
		if(isBlank(_sessionId)){
			return null;
		}
		return Cache.get(ACCOUNT_PREFIX + _sessionId, Account.class);
	}

	protected static void setCacheAccount(
			String _sessionId, Account _pojo, String _cacheLife) {
		if(isBlank(_sessionId)){
			return;
		}
		Cache.set(ACCOUNT_PREFIX + _sessionId, _pojo, _cacheLife);
	}

	protected static void deleteCacheAccount(String _sessionId) {
		Cache.delete(ACCOUNT_PREFIX + _sessionId);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

}

/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
