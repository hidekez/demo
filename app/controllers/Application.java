package controllers;

import java.util.HashMap;

import models.Account;
import models.labels.Category;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.With;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * アプリケーション・コントローラー.
 *
 * @author H.Kezuka
 */
@With(LogInterceptor.class)
public class Application extends MyController {

	/**
	 * 表示(index).
	 */
	// NOTE 放置してからのアクセスでTemplateNotFoundExceptionが発生する
	public static void index() {
		//		render(setupAccount());
		setupAccount(false);
		render();// レンダーしないとリダイレクトできない
		//		redirect("index.html");// リダイレクトいらないね
	}

	/*
	 * 【セッション関連フローメモ】
	 * セッション有無
	 * ＿なし →トップページへ（非ログイン）
	 * ＿あり
	 * ＿＿ログインフラグ有無（フラッシュチェック）
	 * ＿＿＿なし →トップページへ（非ログイン）
	 * ＿＿＿＿アクセストークン有無(キャッシュチェック)
	 * ＿＿＿＿＿なし
	 * ＿＿＿＿＿＿アクセストークン有無(データベースチェック)
	 * ＿＿＿＿＿＿＿なし →非ログインページへ
	 * ＿＿＿＿＿＿＿あり →ログイン状態でのページへ
	 * ＿＿＿＿＿＿＿＿アクセストークンセット(データベースからキャッシュへ)
	 * ＿＿＿＿＿あり →ログイン状態でのページへ
	 * ＿＿＿あり →ログイン状態でのページへ
	 *
	 * アクセストークン保存(Cache)
	 * ユーザー名再セット(Session)
	 * ユーザーID再セット(Session)
	 * ログインフラグセット(Session)
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

	/**
	 * アカウント毎セットアップ
	 * standby
	 *
	 * @param throughToEnd
	 * 		Cacheのリフレッシュのために最後まで処理を通すフラグ
	 *
	 * @return ログインしているか否か
	 */
	public static boolean setupAccount(boolean throughToEnd) {

		// =============================================*
		// セッションチェック
		// =============================================*
		String sessionId = session.getId();

		Logger.debug("sessionId:" + sessionId);

		if (isLogin(sessionId)) {
			Logger.debug("Login");// ログイン済み
			if (!throughToEnd) {
				return true;
			}
		}

		long userId = getSessionUserId(sessionId);
		String screenName = getSessionScreenName(sessionId);

		printSessionData(userId, screenName, 1);// デバッグプリント

		if (userId <= USER_ID_DEFAULT || isBlank(screenName)) {
			Logger.debug("No session");// セッションなし
			return false;
		}

		// =============================================*
		// キャッシュチェック
		// =============================================*
		Account account = getCacheAccount(sessionId);
		Logger.debug("account(1/cache):" + account);// =>loginUser.screenName

		if (account == null) {
			// データベースから取得（実際にはEntityManager）
			account = Account.findByOuterId(userId).first();
		}
		Logger.debug("account(2/database):" + account);// =>loginUser.screenName

		// FIXME ここおかしい？
		// Databaseには登録されているのでそこから取得できるはずなのだが、
		// nullになってしまう場合あり。
		// sessionClear()しておくことにした。
		// NOTES アカウント作成はLogin処理の中で行っている
		if (account == null) {
			Logger.debug("No account info");// アカウント情報なし
			sessionClear();
			return false;
		}

		// アクセストークンの確認
		if (account.accessToken == null) {
			Logger.debug("No account token");// アクセストークンなし、例外？
			// persistの処理いるかも？
			// TODO account.copyAccessTokenFromUser
			String key = account.loginUser.accessToken.accessKey;
			String secret = account.loginUser.accessToken.accessKeySecret;
			if (isNotBlank(key) && isNotBlank(secret)) {
				account.accessToken = new AccessToken(key, secret);
				// TODO screenNameやuserIdをセットする必要あるか調べること
			}
		}
		// TODO ここはこれでいいのか？
		if (account.accessToken != null) {
			setLogin(sessionId, true);
		}

		setCacheAccount(sessionId, account, getCacheLife());
		Logger.debug("isLogin:" + isLogin(sessionId));

		return isLogin(sessionId);

	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

	/**
	 * セッションとキャッシュの削除処理.
	 */
	public static void sessionClear() {

		String sessionId = session.getId();

		Logger.debug("Cache clear");// キャッシュクリア
		deleteCacheAccount(sessionId);

		Logger.debug("Session clear");// セッションクリア
		session.clear();

		redirect("/");
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

	/**
	 * ユーザー情報のAjax配信.
	 */
	public static void ajaxUserInfo() {

		boolean isLogin = setupAccount(true);
		String sessionId = session.getId();

		String appPath = Play.applicationPath.toString();
		String locPath = Play.configuration
				.getProperty(CONSTANT_LOCALPATH_PROP_NAME);// "myserver"
		boolean isLocal = false;

		if (-1 < appPath.indexOf(locPath)) {
			isLocal = true;
		}

		HashMap<String, Object> accountInfo = new HashMap<String, Object>();
		accountInfo.put("loc", isLocal);
		accountInfo.put("dev", Play.mode.isDev());
		accountInfo.put(USER_ID_PREFIX, getSessionUserId(sessionId));
		accountInfo.put(SCREEN_NAME_PREFIX, getSessionScreenName(sessionId));
		accountInfo.put("login", isLogin);

		renderJSON(accountInfo);
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
