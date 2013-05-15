package controllers;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Date;
import java.util.List;

import models.Account;
import models.items.User;
import models.items.twitter.TwitterAccessToken;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.validation.Error;
import play.data.validation.Validation;
import play.mvc.With;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * ツイッターアクション・コントローラー.
 *
 * @author H.Kezuka
 */

@With(LogInterceptor.class)
public class TwitterAction extends MyController {

	private static String CONSUMER_KEY;
	private static String CONSUMER_KEY_SECRET;

	static {
		CONSUMER_KEY = Play.configuration.getProperty("twitter.CONSUMER_KEY");
		CONSUMER_KEY_SECRET = Play.configuration.getProperty("twitter.CONSUMER_SECRET");
	}

	/* ************************************************************ */
	/*
	 * ヘルパメソッド
	 */
	/* ************************************************************ */

	/**
	 * ツイッターオブジェクト作成.
	 */
	// TODO スタティックな情報で無駄に毎回処理している。要リファクタリング
	public static Twitter getTwitter() {
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			twitter.setOAuthConsumer(
					CONSUMER_KEY,
					CONSUMER_KEY_SECRET
			);
		}
		catch (Exception e) {
			Logger.error("TwitterException:%s…", e);
		}
		return twitter;
	}

	/* ************************************************************ */
	/*
	 * ログイン・ログアウト
	 */
	/* ************************************************************ */

	/**
	 * ツイッター・ログイン.
	 */
	public static void login() {

		String sessionId = session.getId();

		if (isLogin(sessionId)) {
			redirect("/");
		}

		Account account = getCacheAccount(sessionId);

		// -------------------------------------+
		// ログイン済み
		if (account != null && account.accessToken != null) {
			Logger.debug("ログイン済み");
			setLogin(sessionId, true);
			redirect("/");
		}
		// -------------------------------------+
		// 未ログイン
		else {
			Logger.debug("未ログイン");
			Twitter twitter = getTwitter();

			try {
				String url = Play.configuration
						.getProperty("application.baseUrl")
						+ "callback";
				Logger.debug("url:" + url);
				RequestToken requestToken = twitter.getOAuthRequestToken(url);
				// TODO エラーの告知用変数とクライアント側への表示処理が必要★★★
				if (requestToken == null) {
					Logger.info("requestToken is null");
					redirect("/");
				}
				setCacheRequestToken(sessionId, requestToken, getCacheLife());

				redirect(requestToken.getAuthenticationURL());

			}
			catch (TwitterException e) {
				Logger.error("TwitterException:%s…", e);
			}
		}
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

	/**
	 * ツイッター・ログイン・コールバック.
	 *
	 * @param oauth_verifier
	 * 		OAuth証明文字列
	 */
	public static void callback(String oauth_verifier) {
		Logger.debug("oauth_verifier:" + oauth_verifier);

		String sessionId = session.getId();
		Account account = getCacheAccount(sessionId);
		if (account == null) {
			System.out.println("account is null");
		}

		// 認証画面で認証を拒否して戻ってきた場合の対応
		if (isBlank(oauth_verifier)) {
			Logger.info("oauth_verifier is null");
			if (account != null) {
				account.clearAuthentication();
				account.validateAndSave();
				setCacheAccount(sessionId, account, getCacheLife());
			}
			logout();
		}

		// Twitter twitter = Cache.get( "twt" + sessionId, Twitter.class );
		Twitter twitter = getTwitter();
		RequestToken requestToken = getCacheRequestToken(sessionId);

		// TODO エラーの告知用変数とクライアント側への表示処理が必要★★★
		if (requestToken == null) {
			Logger.info("requestToken is null");
			if (account != null) {
				account.clearAuthentication();
				account.validateAndSave();
				setCacheAccount(sessionId, account, getCacheLife());
			}
			setLogin(sessionId, false);
			redirect("/");
		}

		try {
			// =============================================*
			// アクセストークン取得
			// =============================================*
			twitter.getOAuthAccessToken(requestToken, oauth_verifier);
			deleteCacheRequestToken(sessionId);

			AccessToken token = twitter.getOAuthAccessToken();
			long userId = twitter.getId();
			String screenName = twitter.getScreenName();

			setSessionUserId(sessionId, userId);
			setSessionScreenName(sessionId, screenName);
			printSessionData(userId, screenName, 1);// デバッグプリント

			// =============================================*
			// ユーザー処理
			// =============================================*
			// twitter4jユーザー
			twitter4j.User twitterUser = twitter.verifyCredentials();
			Logger.debug("twitterUser.screenName:" + twitterUser.getScreenName());

			// (MyApp)ユーザー
			User user;

			if (account != null && account.loginUser != null) {
				user = account.loginUser;
				user.copy(twitterUser);
			}
			else {
				Logger.debug("ユーザー検索");
				user = User.findAndUpdateOrCreate(twitterUser);
			}
			Logger.debug("user.screenName:" + user.screenName);

			// accessToken コピー
			user.accessToken = new TwitterAccessToken(token);
			user.authenticated = true;

			if (user.validateAndSave()) {
				Logger.debug("user Updated");
			}
			else if (Validation.hasErrors()) {
				for (Error error : Validation.errors()) {
					Logger.debug(error.message());
				}
			}

			// =============================================*
			// アカウント処理
			// =============================================*
			// 新規アカウント作成
			if (account == null) {
				Logger.debug("Account Create");
				Logger.debug("user.screenName:" + user.screenName);
				account = new Account(user);
			}
			// アクセストークンの入れ替え
			else if (user.saved) {
				account.loginUser = user;
				account.copyAccessToken();
			}

			Logger.debug("Account:" + account);

			if (account.loginUser.accessToken == null) {
				Logger.warn("account.loginUser.accessToken == null");
			}
			else {
				account.copyAccessTokenFromUser();
			}

			// -------------------------------------+
			// アカウントセーブ＆キャッシュセット
			// -------------------------------------+
			if (account.validateAndSave()) {
				Logger.debug("Account is Ready");
				setCacheAccount(sessionId, account, getCacheLife());
				Logger.debug(account.toString());
				Logger.debug("Account開始 (" + account.id + ")"
						+ account.loginUser.screenName);
			}
			// -------------------------------------+
			else if (Validation.hasErrors()) {
				Logger.debug("repostUser has Error");
				for (Error error : Validation.errors()) {
					Logger.debug(error.message());
				}
			}
		}
		// Twitter4J例外
		catch (TwitterException e) {
			// TODO 例外処理
			// e.printStackTrace();
			Logger.error("TwitterException:%s…", e);

		}
		// OAuth処理において発生する可能性あり
		catch (IllegalStateException ie) {
			Logger.debug("repostUser has Error");
		}

		redirect("/");

	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

	/**
	 * ツイッター・ログアウト.
	 */
	public static void logout() {
		Application.sessionClear();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

	/**
	 * ツイッター・つぶやき投稿.
	 *
	 * @param rqtwt
	 * 		つぶやき文字列
	 */
	public static void tweet(String rqtwt) {

		String sessionId = session.getId();

		if (isLogin(sessionId) == false) {
			redirect("/");
		}

		Twitter twitter = getTwitter();

		Account account = getCacheAccount(sessionId);

		// 念のため
		if (account.accessToken == null) {
			setLogin(sessionId, false);
			redirect("/");
		}
		try {
			twitter.setOAuthAccessToken(account.accessToken);
			twitter.updateStatus(rqtwt);
		}
		catch (TwitterException e) {
			// TODO 例外処理
			// e.printStackTrace();
			Logger.error("TwitterException:%s…", e);
		}
		setLogin(sessionId, true);
		redirect("/");
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

	/**
	 * ツイッター・ホームタイムライン取得・出力処理.
	 *
	 * @deprecated
	 */
	public static void showHomeTimeline() {

		String sessionId = session.getId();

		if (isLogin(sessionId) == false) {
			redirect("/");
		}

		Twitter twitter = getTwitter();

		Account account = getCacheAccount(sessionId);

		if (account.accessToken == null) {
			setLogin(sessionId, false);
			renderText("error");
		}
		try {
			twitter.setOAuthAccessToken(account.accessToken);
			List<Status> statuses = twitter.getHomeTimeline();
			setLogin(sessionId, true);
			renderJSON(statuses);
		}
		catch (TwitterException e) {
			// TODO 例外処理
			// e.printStackTrace();
			Logger.error("TwitterException:%s…", e);
		}
	}

	/**
	 * ツイッター・つぶやき取得・出力処理.
	 *
	 * @param unitId
	 */
	public static void getTweets(String unitId) {

		String sessionId = session.getId();

		if (isLogin(sessionId) == false) {
			Logger.debug("****** 1 *********");
//			redirect("/");
			redirect("Application.index");
		}

		Twitter twitter = getTwitter();

		Account account = getCacheAccount(sessionId);

		// cache.lifeによる消失など
		if (account == null) {
			Logger.debug("****** 2 *********");
			setLogin(sessionId, false);
//			redirect("/");
//			Application.index();
			redirect("Application.index");
		}
		if (account.accessToken == null) {
			setLogin(sessionId, false);
			renderText("error");
		}
		try {
			twitter.setOAuthAccessToken(account.accessToken);
			List<Status> statuses;
			if("tl".equals(unitId)){
				statuses = twitter.getHomeTimeline();
			}
			else if("rt".equals(unitId)){
				// TODO 自分のつぶやきでRTされたもの。正しくない。
				statuses = twitter.getRetweetsOfMe();
			}
			else if("fav".equals(unitId)){
				statuses = twitter.getFavorites();
			}
			else{
				return;
			}
//			setLogin(sessionId, true);
			renderJSON(statuses);
		}
		catch (TwitterException e) {
			// TODO 例外処理
			// e.printStackTrace();
			Logger.error("TwitterException:%s…", e);
		}
	}
}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
