package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import utils.MyDateUtils;

import constants.ItemStateType;
import constants.LifeStateType;
import constants.OpenLevel;
import constants.TwitterCountry;
import constants.TwitterLanguage;
import constants.TwitterTimeZone;

import static utils.MyDateUtils.parseOrNull;
import static utils.MyDateUtils.noCheckedParse;
import static utils.MyDateUtils.toDate;

import models.items.ItemBasePOJO;
import models.items.ItemCore;
import models.items.ItemId;
import models.items.Tweet;
import models.items.TwitterTweetPOJO;
import models.items.TwitterUserPOJO;
import models.items.User;
import models.items.ItemCore.Builder;
import models.items.multi.Geo;
import models.items.multi.UserName;
import models.items.twitter.InReplyTo;
import models.items.twitter.TwitterAccessToken;
import models.labels.Category;
import models.labels.CategoryPOJO;
import models.labels.LabelBasePOJO;
import models.labels.Tag;
import models.labels.TagPOJO;
import models.labels.categories.Word;

/**
 * テスト用モデル記述クラス
 *
 * @author H.Kezuka
 */
public class TestModels {

	public static final String YMD1 = "2010-01-02";
	public static final String YMD2 = "2010-03-04";
	public static final String YMD3 = "2011-05-06";
	public static final String YMD4 = "2011-07-08";
	public static final String YMD5 = "2012-09-10";
	public static final String YMD6 = "2012-10-10";
	public static final String YMD7 = "2012-12-12";
	public static final String ANCIENT = "1000-01-01";
	public static final String FUTURE = "3000-01-01";

	private static Date TODAY;

	public static final Date date1() {
		return parseOrNull(YMD1);
	}

	public static final Date date2() {
		return parseOrNull(YMD2);
	}

	public static final Date date3() {
		return parseOrNull(YMD3);
	}

	public static final Date date4() {
		return parseOrNull(YMD4);
	}

	public static final Date date5() {
		return parseOrNull(YMD5);
	}

	public static final Date date6() {
		return parseOrNull(YMD6);
	}

	public static final Date date7() {
		return parseOrNull(YMD7);
	}

	public static final Date today() {
		if (TODAY == null) {
			TODAY = new Date();
		}
		return TODAY;
	}

	public static final Date ancient() {
		return noCheckedParse(ANCIENT);
	}

	public static final Date future() {
		return noCheckedParse(FUTURE);
	}

	/* ************************************************************ */
	/*
	 * ItemId
	 */
	/* ************************************************************ */
/*
 * Play上で動かさないと、saveに際してJPA側でエラーが出るので注意。
 * なので、この方式はできない
 */
//	public static final ItemId[] iids = new ItemId[10];
//
//	static {
//		for (int i = 0; i < iids.length; i++) {
//			iids[i] = new ItemId();
//			iids[i].save();
//		}
//	}

	/*
	 * iids[0][0] //User1
	 * iids[0][1] //User2
	 * iids[0][2] //User3
	 * iids[1][0] //Tweet1
	 * iids[1][1] //Tweet2
	 * iids[1][2] //Tweet3
	 */
	private ItemId[][] iids = new ItemId[2][5];
	private static final int USER = 0;
	private static final int TWEET = 1;

	// -------------------------------------+
	// 生成
	public static final ItemId newId() {
		ItemId id = new ItemId();
		id.save();
		return id;
	}

	// -------------------------------------+
	// アクセッサ
	private final void setItemId(int _i1, int _i2) {
		if (iids[_i1][_i2] == null) {
			iids[_i1][_i2] = new ItemId();
		}
		else if (iids[_i1][_i2].isPersistent() == false) {
			iids[_i1][_i2] = iids[_i1][_i2].mergeAll();
		}
		iids[_i1][_i2].save();
	}

	public final ItemId getItemId(int _i1, int _i2) {
		setItemId(_i1, _i2);
		return iids[_i1][_i2];
	}

	/* ************************************************************ */
	/*
	 * ItemBase
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * POJO
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final ItemBasePOJO userBase01() {
		ItemBasePOJO pojo = new ItemBasePOJO();
		pojo.itemId = getItemId(USER, 0);
		pojo.createdAt = date1();
		pojo.enteredAt = today();
		return pojo;
	}

	public final ItemBasePOJO userBase02() {
		ItemBasePOJO pojo = new ItemBasePOJO();
		pojo.itemId = getItemId(USER, 1);
		pojo.createdAt = date2();
		pojo.enteredAt = today();
		return pojo;
	}

	public final ItemBasePOJO userBase03() {
		ItemBasePOJO pojo = new ItemBasePOJO();
		pojo.itemId = getItemId(USER, 2);
		pojo.createdAt = date3();
		pojo.enteredAt = today();
		return pojo;
	}

	//Fixture関連
	public final ItemBasePOJO userBase04() {
		ItemBasePOJO pojo = new ItemBasePOJO();
		pojo.itemId = getItemId(USER, 3);
		pojo.createdAt = toDate("2010-09-24");
		pojo.enteredAt = toDate("2013-01-12");
		return pojo;
	}

	// -------------------------------------+
	public final ItemBasePOJO twtBase01() {
		ItemBasePOJO pojo = new ItemBasePOJO();
		pojo.itemId = getItemId(TWEET, 0);
		pojo.createdAt = date1();
		pojo.enteredAt = today();
		return pojo;
	}

	public final ItemBasePOJO twtBase02() {
		ItemBasePOJO pojo = new ItemBasePOJO();
		pojo.itemId = getItemId(TWEET, 1);
		pojo.createdAt = date2();
		pojo.enteredAt = today();
		return pojo;
	}

	public final ItemBasePOJO twtBase03() {
		ItemBasePOJO pojo = new ItemBasePOJO();
		pojo.itemId = getItemId(TWEET, 2);
		pojo.createdAt = date3();
		pojo.enteredAt = today();
		return pojo;
	}

	//Fixture関連
	public final ItemBasePOJO twtBase04() {
		ItemBasePOJO pojo = new ItemBasePOJO();
		pojo.itemId = getItemId(TWEET, 3);
		pojo.createdAt = date3();
		pojo.enteredAt = today();
		return pojo;
	}

	/* ************************************************************ */
	/*
	 * DummyItem
	 */
	/* ************************************************************ */
	public final ItemCore dummyItem01() {
		return new ItemCore.Builder()
				// -------------------------------------+
				// コア
				.itemId(getItemId(USER, 0))// ItemId
				// .serialCode()//String
				.createdAt(date1())// Date
				.enteredAt(date1())// Date
				// .state(ItemStateType.OK)// ItemStateType
				// -------------------------------------+
				.build();
	}

	/* ************************************************************ */
	/*
	 * TwitterAccessToken
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 新規インスタンス作成
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final TwitterAccessToken atoken01() {
		TwitterAccessToken token = new TwitterAccessToken();
		token.accessKey = "hoge";
		token.accessKeySecret = "fuga";
		return token;
	}

	public final TwitterAccessToken atoken02() {
		TwitterAccessToken token = new TwitterAccessToken();
		token.accessKey = "jiro";
		token.accessKeySecret = "jiroro";
		return token;
	}

	public final TwitterAccessToken atoken03() {
		TwitterAccessToken token = new TwitterAccessToken();
		token.accessKey = "miko";
		token.accessKeySecret = "mikoko";
		return token;
	}

	// Fixture関連
	public final TwitterAccessToken atokenNoData() {
		TwitterAccessToken token = new TwitterAccessToken();
		token.accessKey = "";
		token.accessKeySecret = "";
		return token;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * キャッシュインスタンスの取得
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * Play上で動かさないと、saveに際してJPA側でエラーが出るので注意。
	 * staticで準備することはできない。
	 */
	public TwitterAccessToken tokens[] = new TwitterAccessToken[4];

	// -------------------------------------+
	// アクセッサ
	private final void setAccessToken(int _i) {
		if (tokens[_i] == null) {
			tokens[0] = atoken01();
			tokens[1] = atoken02();
			tokens[2] = atoken03();
			tokens[3] = atokenNoData();
		}
	}

	public final TwitterAccessToken getAccessToken(int _i) {
		setAccessToken(_i);
		return tokens[_i];
	}

	/* ************************************************************ */
	/*
	 * TwitterUser
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * POJO
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final TwitterUserPOJO tuPojo01() {

		TwitterUserPOJO pojo = new TwitterUserPOJO();

		pojo.base = userBase01();
		// -------------------------------------+
		pojo.userId = 1L;
		pojo.screenName = "taro";
		pojo.userName = "山田太郎";
		pojo.profileImageUrl = "http://google.com";
		pojo.profileImageUrlHttps = "https://google.com";
		// -------------------------------------+
		pojo.language = TwitterLanguage.getEnumByCode("ja");
		pojo.country = TwitterCountry.getEnumByCode("jp");
		pojo.timeZone = TwitterTimeZone.getEnumByName("Tokyo");
		pojo.utcOffset = 0;
		pojo.location = "調布市";
		pojo.description = "こんにちは、タローです。";
		pojo.url = "http://google.com";
		// -------------------------------------+
		pojo.friendsCount = 100;
		pojo.followersCount = 200;
		pojo.favouritesCount = 10;
		pojo.statusesCount = 10000;
		pojo.listedCount = 0;
		// -------------------------------------+
		pojo.isFollowing = false;
		pojo.isProtected = false;
		pojo.verified = true;
		pojo.accessToken = getAccessToken(0);

		return pojo;
	}

	public final TwitterUserPOJO tuPojo02() {

		TwitterUserPOJO pojo = new TwitterUserPOJO();

		pojo.base = userBase02();
		// -------------------------------------+
		pojo.userId = 2L;
		pojo.screenName = "taro";
		pojo.userName = "スズキジロー";
		pojo.profileImageUrl = "http://yahoo.com";
		pojo.profileImageUrlHttps = "https://yahoo.com";
		// -------------------------------------+
		pojo.language = TwitterLanguage.getEnumByCode("ja");
		pojo.country = TwitterCountry.getEnumByCode("jp");
		pojo.timeZone = TwitterTimeZone.getEnumByName("Osaka");
		pojo.utcOffset = 0;
		pojo.location = "大阪市";
		pojo.description = "ジローでっせ。";
		pojo.url = "http://yahoo.com";
		// -------------------------------------+
		pojo.friendsCount = 155;
		pojo.followersCount = 255;
		pojo.favouritesCount = 15;
		pojo.statusesCount = 10555;
		pojo.listedCount = 5;
		// -------------------------------------+
		pojo.isFollowing = false;
		pojo.isProtected = false;
		pojo.verified = true;
		pojo.accessToken = getAccessToken(1);

		return pojo;
	}

	public final TwitterUserPOJO tuPojo03() {

		TwitterUserPOJO pojo = new TwitterUserPOJO();

		pojo.base = userBase03();
		// -------------------------------------+
		pojo.userId = 3L;
		pojo.screenName = "taro";
		pojo.userName = "佐藤三子";
		pojo.profileImageUrl = "http://google.com";
		pojo.profileImageUrlHttps = "https://google.com";
		// -------------------------------------+
		pojo.language = TwitterLanguage.getEnumByCode("ja");
		pojo.country = TwitterCountry.getEnumByCode("jp");
		pojo.timeZone = TwitterTimeZone.getEnumByName("Tokyo");
		pojo.utcOffset = 0;
		pojo.location = "三鷹市";
		pojo.description = "おはようございます、ミコです。";
		pojo.url = "http://google.com";
		// -------------------------------------+
		pojo.friendsCount = 333;
		pojo.followersCount = 3333;
		pojo.favouritesCount = 1333;
		pojo.statusesCount = 300000;
		pojo.listedCount = 3;
		// -------------------------------------+
		pojo.isFollowing = false;
		pojo.isProtected = false;
		pojo.verified = true;
		pojo.accessToken = getAccessToken(2);

		return pojo;
	}

	// Fixture関連
	public final TwitterUserPOJO tuPojo04() {

		TwitterUserPOJO pojo = new TwitterUserPOJO();

		pojo.base = userBase04();
		// -------------------------------------+
		pojo.userId = 4L;
		pojo.screenName = "goro_san";
		pojo.userName = "田中五郎";
		pojo.profileImageUrl = "http://a0.twimg.com/profile_images/1404776532/hime_afro3_normal.jpg";
		pojo.profileImageUrlHttps = "https://si0.twimg.com/profile_images/1404776532/hime_afro3_normal.jpg";
		// -------------------------------------+
		pojo.language = TwitterLanguage.getEnumByCode("ja");
		pojo.country = TwitterCountry.getEnumByCode("jp");
		pojo.timeZone = TwitterTimeZone.getEnumByName("Tokyo");
		pojo.utcOffset = 0;
		pojo.location = "dummy";
		pojo.description = "dummy";
		pojo.url = null;
		// -------------------------------------+
		pojo.friendsCount = 0;
		pojo.followersCount = 0;
		pojo.favouritesCount = 0;
		pojo.statusesCount = 0;
		pojo.listedCount = 0;
		// -------------------------------------+
		pojo.isFollowing = false;
		pojo.isProtected = false;
		pojo.verified = false;
		pojo.accessToken = getAccessToken(3);

		return pojo;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 新規インスタンス作成
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final User newTwitterUser(TwitterUserPOJO _pojo) {

		return new User.Builder()
				// Sub's fields
				.userId(_pojo.userId)
				.screenName(_pojo.screenName)
				.userName(_pojo.userName)
				.profileImageUrl(_pojo.profileImageUrl)
				.profileImageUrlHttps(_pojo.profileImageUrlHttps)
				// -------------------------------------+
				.language(_pojo.language)
				.country(_pojo.country)
				.timeZone(_pojo.timeZone)
				.utcOffset(_pojo.utcOffset)
				.location(_pojo.location)
				.description(_pojo.description)
				.url(_pojo.url)
				// -------------------------------------+
				.friendsCount(_pojo.friendsCount)
				.followersCount(_pojo.followersCount)
				.favouritesCount(_pojo.favouritesCount)
				.statusesCount(_pojo.statusesCount)
				.listedCount(_pojo.listedCount)
				// -------------------------------------+
				.isFollowing(_pojo.isFollowing)
				.isProtected(_pojo.isProtected)
				.verified(_pojo.verified)
				// .accessKey(_ent.accessToken.accessKey)
//				.accessKeySecret(_ent.accessToken.accessKeySecret)
				.accessToken(_pojo.accessToken)
				// -------------------------------------+
				// Super's fields
				.itemId(_pojo.base.itemId)
				.createdAt(_pojo.base.createdAt)
				.enteredAt(_pojo.base.enteredAt)
				.state(_pojo.base.state)
				// -------------------------------------+
				.build();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// ラッパー
	public final User newTwitterUser01() {
		User user = newTwitterUser(tuPojo01());
		user.save();
		return user;
	}

	public final User newTwitterUser02() {
		User user = newTwitterUser(tuPojo02());
		user.save();
		return user;
	}

	public final User newTwitterUser03() {
		User user = newTwitterUser(tuPojo03());
		user.save();
		return user;
	}

	// Fixture関連
	public final User newTwitterUser04() {
		User user = newTwitterUser(tuPojo04());
		user.save();
		return user;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * キャッシュインスタンスの取得
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * Play上で動かさないと、saveに際してJPA側でエラーが出るので注意。
	 * staticで準備することはできない。
	 */
	public User users[] = new User[4];

	// -------------------------------------+
	// アクセッサ
	private final void setUser(int _i) {
		if (users[_i] == null) {
			users[0] = newTwitterUser01();
			users[1] = newTwitterUser02();
			users[2] = newTwitterUser03();
			users[3] = newTwitterUser04();
		}
		else if (users[_i].isPersistent() == false) {
			users[_i] = users[_i].mergeAll();
		}
		users[_i].save();
	}

	public final User getUser(int _i) {
		setUser(_i);
		return users[_i];
	}

	/* ************************************************************ */
	/*
	 * TwitterTweet
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * POJO
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final TwitterTweetPOJO ttPojo01() {

		TwitterTweetPOJO pojo = new TwitterTweetPOJO();

		pojo.base = twtBase01();
		// -------------------------------------+
		pojo.statusId = 1L;
		pojo.text = "こんにちは、タローです。";
		pojo.author = getUser(0);
		pojo.language = TwitterLanguage.getEnumByCode("ja");
		pojo.country = TwitterCountry.getEnumByCode("jp");
		pojo.isInReply = false;
		pojo.hasGeo = true;

		return pojo;
	}

	public final TwitterTweetPOJO ttPojo02() {

		TwitterTweetPOJO pojo = new TwitterTweetPOJO();

		pojo.base = twtBase02();
		// -------------------------------------+
		pojo.statusId = 2L;
		pojo.text = "タローさんへ、ジローより。";
		pojo.author = getUser(1);
		pojo.language = TwitterLanguage.getEnumByCode("ja");
		pojo.country = TwitterCountry.getEnumByCode("jp");
		pojo.isInReply = true;
		pojo.hasGeo = true;

		return pojo;
	}

	public final TwitterTweetPOJO ttPojo03() {

		TwitterTweetPOJO pojo = new TwitterTweetPOJO();

		pojo.base = twtBase03();
		// -------------------------------------+
		pojo.statusId = 3L;
		pojo.text = "Hello world!";
		pojo.author = getUser(2);
		pojo.language = TwitterLanguage.getEnumByCode("ja");
		pojo.country = TwitterCountry.getEnumByCode("jp");
		pojo.isInReply = false;
		pojo.hasGeo = false;

		return pojo;
	}

	// Fixture関連
	public final TwitterTweetPOJO ttPojo04() {

		TwitterTweetPOJO pojo = new TwitterTweetPOJO();

		pojo.base = twtBase04();
		// -------------------------------------+
		pojo.statusId = 101L;
		pojo.text = "東京は一日晴れ模様";
		pojo.author = getUser(3);
		pojo.language = TwitterLanguage.getEnumByCode("ja");
		pojo.country = TwitterCountry.getEnumByCode("jp");
		pojo.isInReply = false;
		pojo.hasGeo = false;

		return pojo;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 新規インスタンス作成
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final Tweet newTwitterTweet(TwitterTweetPOJO _pojo) {

		return new Tweet.Builder()
				// Sub's fields
				.statusId(_pojo.statusId)
				.text(_pojo.text)
				.author(_pojo.author)
				.language(_pojo.language)
				.country(_pojo.country)
				.isInReply(_pojo.isInReply)
				.hasGeo(_pojo.hasGeo)
				// -------------------------------------+
				// Super's fields
				.itemId(_pojo.base.itemId)
				.createdAt(_pojo.base.createdAt)
				.enteredAt(_pojo.base.enteredAt)
				.state(_pojo.base.state)
				// -------------------------------------+
				.build();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// ラッパー
	public final Tweet newTwitterTweet01() {
		return newTwitterTweet(ttPojo01());
	}

	public final Tweet newTwitterTweet02() {
		return newTwitterTweet(ttPojo02());
	}

	public final Tweet newTwitterTweet03() {
		return newTwitterTweet(ttPojo03());
	}

	public final Tweet newTwitterTweet04() {
		return newTwitterTweet(ttPojo04());
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * キャッシュインスタンスの取得
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * Play上で動かさないと、saveに際してJPA側でエラーが出るので注意。
	 * staticで準備することはできない。
	 */
	public Tweet tweets[] = new Tweet[4];

	// -------------------------------------+
	// アクセッサ
	private final void setTweet(int _i) {
		if (tweets[_i] == null) {
			tweets[0] = newTwitterTweet01();
			tweets[1] = newTwitterTweet02();
			tweets[2] = newTwitterTweet03();
			tweets[3] = newTwitterTweet04();
		}
		else if (tweets[_i].isPersistent() == false) {
			tweets[_i] = tweets[_i].mergeAll();
		}
		tweets[_i].save();
	}

	public final Tweet getTweet(int _i) {
		setTweet(_i);
		return tweets[_i];
	}

	/* ************************************************************ */
	/*
	 * Account
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * POJO
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final AccountPOJO accPojo01() {

		AccountPOJO pojo = new AccountPOJO();

		pojo.loginUser = getUser(0);
		pojo.enteredAt = date5();
		pojo.visitedAt = today();
		pojo.life = LifeStateType.ALIVE;

		return pojo;
	}

	public final AccountPOJO accPojo02() {

		AccountPOJO pojo = new AccountPOJO();

		pojo.loginUser = getUser(1);
		pojo.enteredAt = date6();
		pojo.visitedAt = today();
		pojo.life = LifeStateType.ALIVE;

		return pojo;
	}

	public final AccountPOJO accPojo03() {

		AccountPOJO pojo = new AccountPOJO();

		pojo.loginUser = getUser(2);
		pojo.enteredAt = date7();
		pojo.visitedAt = today();
		pojo.life = LifeStateType.ALIVE;

		return pojo;
	}

	// Fixture関連
	public final AccountPOJO accPojo04() {

		AccountPOJO pojo = new AccountPOJO();

		pojo.loginUser = getUser(3);
		pojo.enteredAt = toDate("2013-01-12");
		pojo.visitedAt = toDate("2013-01-12");
		pojo.life = LifeStateType.ALIVE;

		return pojo;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 新規インスタンス作成
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final Account newAccount(AccountPOJO _pojo) {

		return new Account(
				_pojo.loginUser,
				_pojo.enteredAt,
				_pojo.visitedAt,
				_pojo.life);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// ラッパー
	public final Account newAccount01() {
		Account acc = newAccount(accPojo01());
		acc.save();
		return acc;
	}

	public final Account newAccount02() {
		Account acc = newAccount(accPojo02());
		acc.save();
		return acc;
	}

	public final Account newAccount03() {
		Account acc = newAccount(accPojo03());
		acc.save();
		return acc;
	}

	public final Account newAccount04() {
		Account acc = newAccount(accPojo04());
		acc.save();
		return acc;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * キャッシュインスタンスの取得
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * Play上で動かさないと、saveに際してJPA側でエラーが出るので注意。
	 * staticで準備することはできない。
	 */
	public Account acnts[] = new Account[4];

	// -------------------------------------+
	// アクセッサ
	private final void setAccount(int _i) {
		if (acnts[_i] == null) {
			acnts[0] = newAccount01();
			acnts[1] = newAccount02();
			acnts[2] = newAccount03();
			acnts[3] = newAccount04();
		}
		else if (acnts[_i].isPersistent() == false) {
			acnts[_i] = acnts[_i].mergeAll();
		}
		acnts[_i].save();
	}

	public final Account getAccount(int _i) {
		setAccount(_i);
		return acnts[_i];
	}

	/* ************************************************************ */
	/*
	 * LabelBase
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * POJO
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final LabelBasePOJO catBase01() {
		LabelBasePOJO pojo = new LabelBasePOJO();
		pojo.serialCode = "cat-hoge";
		pojo.displayName = "ホゲ";
		return pojo;
	}

	public final LabelBasePOJO catBase02() {
		LabelBasePOJO pojo = new LabelBasePOJO();
		pojo.serialCode = "cat-piyo";
		pojo.displayName = "ピヨ";
		return pojo;
	}

	public final LabelBasePOJO catBase03() {
		LabelBasePOJO pojo = new LabelBasePOJO();
		pojo.serialCode = "cat-fuga";
		pojo.displayName = "フガ";
		return pojo;
	}

	// Fixture関連
	public final LabelBasePOJO catBase04() {
		LabelBasePOJO pojo = new LabelBasePOJO();
		pojo.serialCode = "cat-biz";
		pojo.displayName = "ビジネス";
		return pojo;
	}

	//-------------------------------------+
	public final LabelBasePOJO tagBase01() {
		LabelBasePOJO pojo = new LabelBasePOJO();
		pojo.serialCode = "tag-red";
		pojo.displayName = "赤";
		return pojo;
	}

	public final LabelBasePOJO tagBase02() {
		LabelBasePOJO pojo = new LabelBasePOJO();
		pojo.serialCode = "tag-blue";
		pojo.displayName = "青";
		return pojo;
	}

	public final LabelBasePOJO tagBase03() {
		LabelBasePOJO pojo = new LabelBasePOJO();
		pojo.serialCode = "tag-green";
		pojo.displayName = "緑";
		return pojo;
	}

	// Fixture関連
	public final LabelBasePOJO tagBase04() {
		LabelBasePOJO pojo = new LabelBasePOJO();
		pojo.serialCode = "tag-taro-red";
		pojo.displayName = "赤";
		return pojo;
	}

	/* ************************************************************ */
	/*
	 * Category
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * POJO
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final CategoryPOJO catPojo01() {

		CategoryPOJO pojo = new CategoryPOJO();

		pojo.base = catBase01();

		return pojo;
	}

	public final CategoryPOJO catPojo02() {

		CategoryPOJO pojo = new CategoryPOJO();

		pojo.base = catBase02();

		return pojo;
	}

	public final CategoryPOJO catPojo03() {

		CategoryPOJO pojo = new CategoryPOJO();

		pojo.base = catBase03();

		return pojo;
	}

	// Fixture関連
	public final CategoryPOJO catPojo04() {

		CategoryPOJO pojo = new CategoryPOJO();

		pojo.base = catBase04();

		return pojo;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 新規インスタンス作成
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final Category newCategory(CategoryPOJO _pojo) {

		return new Category(
				_pojo.base.serialCode,
				_pojo.base.displayName);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// ラッパー
	public final Category newCategory01() {
		return newCategory(catPojo01());
	}

	public final Category newCategory02() {
		return newCategory(catPojo02());
	}

	public final Category newCategory03() {
		return newCategory(catPojo03());
	}

	public final Category newCategory04() {
		return newCategory(catPojo04());
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * キャッシュインスタンスの取得
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * Play上で動かさないと、saveに際してJPA側でエラーが出るので注意。
	 * staticで準備することはできない。
	 */
	public Category cats[] = new Category[4];

	// -------------------------------------+
	// アクセッサ
	private final void setCategory(int _i) {
		if (cats[_i] == null) {
			cats[0] = newCategory01();
			cats[1] = newCategory02();
			cats[2] = newCategory03();
			cats[3] = newCategory04();
		}
		else if (cats[_i].isPersistent() == false) {
			cats[_i] = cats[_i].mergeAll();
		}
		cats[_i].save();
	}

	public final Category getCategory(int _i) {
		setCategory(_i);
		return cats[_i];
	}

	/* ************************************************************ */
	/*
	 * Tag
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * POJO
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final TagPOJO tagPojo01() {

		TagPOJO pojo = new TagPOJO();

		pojo.base = tagBase01();
		// -------------------------------------+
		pojo.openLevel = OpenLevel.PUBLIC;
		pojo.author = getAccount(0);

		return pojo;
	}

	public final TagPOJO tagPojo02() {

		TagPOJO pojo = new TagPOJO();

		pojo.base = tagBase02();
		// -------------------------------------+
		pojo.openLevel = OpenLevel.PUBLIC;
		pojo.author = getAccount(1);

		return pojo;
	}

	public final TagPOJO tagPojo03() {

		TagPOJO pojo = new TagPOJO();

		pojo.base = tagBase03();
		// -------------------------------------+
		pojo.openLevel = OpenLevel.PUBLIC;
		pojo.author = getAccount(2);

		return pojo;
	}

	// Fixture関連
	public final TagPOJO tagPojo04() {

		TagPOJO pojo = new TagPOJO();

		pojo.base = tagBase04();
		// -------------------------------------+
		pojo.openLevel = OpenLevel.PUBLIC;
		pojo.author = getAccount(3);

		return pojo;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 新規インスタンス作成
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public final Tag newTag(TagPOJO _pojo) {

		return new Tag(
				_pojo.base.serialCode,
				_pojo.base.displayName,
				_pojo.author,
				_pojo.openLevel);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// ラッパー
	public final Tag newTag01() {
		return newTag(tagPojo01());
	}

	public final Tag newTag02() {
		return newTag(tagPojo02());
	}

	public final Tag newTag03() {
		return newTag(tagPojo03());
	}

	public final Tag newTag04() {
		return newTag(tagPojo04());
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * キャッシュインスタンスの取得
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * Play上で動かさないと、saveに際してJPA側でエラーが出るので注意。
	 * staticで準備することはできない。
	 */
	public Tag tags[] = new Tag[4];

	// -------------------------------------+
	// アクセッサ
	private final void setTag(int _i) {
		if (tags[_i] == null) {
			tags[0] = newTag01();
			tags[1] = newTag02();
			tags[2] = newTag03();
			tags[3] = newTag04();
		}
		else if (tags[_i].isPersistent() == false) {
			tags[_i] = tags[_i].mergeAll();
		}
		tags[_i].save();
	}

	public final Tag getTag(int _i) {
		setTag(_i);
		return tags[_i];
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
