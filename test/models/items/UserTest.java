package models.items;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.ibm.icu.util.Calendar;

import play.Logger;
import play.data.validation.Validation;
import play.test.Fixtures;
import static org.hamcrest.CoreMatchers.*;

import constants.TwitterCountry;
import constants.TwitterLanguage;
import constants.TwitterTimeZone;

import utils.MyDateUtils;
import validation.MyURLCheck;
import validation.TwitterScreenNameCheck;
import validation.TwitterUserNameCheck;

import models.TestModels;
import models.items.User;
import models.items.multi.UserName;
import models.items.twitter.ITwitterConstant;
import models.items.twitter.TwitterAccessToken;
import mytests.UnitTestBase;

/**
 * つぶやきユーザーモデル・テスト
 *
 * @author H.Kezuka
 */
public class UserTest extends UnitTestBase {

	User actual;
	TwitterUserPOJO expected;

	TestModels tm = new TestModels();

	/* ************************************************************ */

	public void assertModel(
			User _actual, TwitterUserPOJO _expected) {

		// アイテム共通
		assertSame("itemId", _actual.itemId, _expected.base.itemId);// ItemId
//		assertEquals("serialCode", _actual.serialCode, _expected.base.serialCode);// String
		assertEquals("createdAt", _actual.getCreatedAt(),
				_expected.base.createdAt);// Date
		assertEquals("enteredAt", _actual.getEnteredAt(),
				_expected.base.enteredAt);// Date
		assertSame("state", _actual.state, _expected.base.state);// ItemStateType
		// -------------------------------------+
		// コア
		assertEquals("userId", _actual.userId, _expected.userId);// long
		assertEquals("screenName", _actual.screenName, _expected.screenName);// String
		assertEquals("userName", _actual.userName, _expected.userName);// String
		assertEquals("profileImageUrl", _actual.profileImageUrl,
				_expected.profileImageUrl);// String
		assertEquals("profileImageUrlHttps", _actual.profileImageUrlHttps,
				_expected.profileImageUrlHttps);// String
		// -------------------------------------+
		// プロフィール
		assertEquals("language", _actual.getLanguage(), _expected.language);// TwitterLanguage
		assertEquals("country", _actual.getCountry(), _expected.country);// TwitterCountry
		assertEquals("timeZone", _actual.getTimeZone(), _expected.timeZone);// TwitterTimeZone
		assertEquals("utcOffset", _actual.utcOffset, _expected.utcOffset);// int
		assertEquals("location", _actual.location, _expected.location);// String
		assertEquals("description", _actual.description, _expected.description);// String
		assertEquals("url", _actual.url, _expected.url);// String//String
		// -------------------------------------+
		// 数値
		assertEquals("friendsCount", _actual.friendsCount,
				_expected.friendsCount);// int
		assertEquals("followersCount", _actual.followersCount,
				_expected.followersCount);// int
		assertEquals("statusesCount", _actual.statusesCount,
				_expected.statusesCount);// int
		assertEquals("favouritesCount", _actual.favouritesCount,
				_expected.favouritesCount);// int
		assertEquals("listedCount", _actual.listedCount, _expected.listedCount);// int
		// -------------------------------------+
		// フラグ
		assertEquals("isFollowing", _actual.isFollowing, _expected.isFollowing);// boolean
		assertEquals("isProtected", _actual.isProtected,
				_expected.isProtected);// boolean
		assertEquals("verified", _actual.verified, _expected.verified);// boolean
		// -------------------------------------+
		// 認証用データ
		assertEquals("accessKey", _actual.accessToken.accessKey,
				_expected.accessToken.accessKey);// TwitterAccessToken
		assertEquals("accessKeySecret", _actual.accessToken.accessKeySecret,
				_expected.accessToken.accessKeySecret);// TwitterAccessToken

	}

	// 引数なしで、インスタンスフィールドを使用
	public void assertModel() {
		assertModel(actual, expected);
		Logger.debug("actual:%s", actual);
	}

	/* ************************************************************ */
	/*
	 * ビルダーを利用した新規作成
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void ビルダー_新規作成_01() {
		printTitle();

		expected = tm.tuPojo01();
		actual = new User.Builder()
				.userId(1L)
				.screenName("taro")
				.userName("山田太郎")
				.profileImageUrl("http://google.com")
				.profileImageUrlHttps("https://google.com")
				// -------------------------------------+
				.language(TwitterLanguage.JAPANESE.getCode())
				.country(TwitterCountry.JAPAN.getCode())
				.timeZone(TwitterTimeZone.TOKYO.getName())
				.utcOffset(0)
				.location("調布市")
				.description("こんにちは、タローです。")
				.url("http://google.com")
				// -------------------------------------+
				.friendsCount(100)
				.followersCount(200)
				.favouritesCount(10)
				.statusesCount(10000)
				.listedCount(0)
				// -------------------------------------+
				.isFollowing(false)
				.isProtected(false)
				.verified(true)
				// .accessKey("hoge")
//				.accessKeySecret("fuga")
				.accessToken("hoge", "fuga")
				// -------------------------------------+
				.itemId(tm.getItemId(0, 0))
				// .createdAt(MyDateUtils.parseOrNull("2010-01-02"))
				.createdAt(tm.date1())
				.enteredAt(tm.today())
				// -------------------------------------+
				.build();

		assertModel();
	}

	@Test
	public void ビルダー_新規作成_02() {
		printTitle();

		expected = new TwitterUserPOJO();
		expected.userId = 1L;
		expected.screenName = "taro";
		expected.userName = "山田太郎";
		expected.profileImageUrl = "http://google.com";
		expected.profileImageUrlHttps = "https://google.com";
		// -------------------------------------+
		expected.language = TwitterLanguage.JAPANESE;
		expected.country = TwitterCountry.JAPAN;
		expected.timeZone = TwitterTimeZone.TOKYO;
		expected.utcOffset = 0;
		expected.location = "調布市";
		expected.description = "こんにちは、タローです。";
		expected.url = "http://google.com";
		// -------------------------------------+
		expected.friendsCount = 100;
		expected.followersCount = 200;
		expected.favouritesCount = 10;
		expected.statusesCount = 10000;
		expected.listedCount = 0;
		// -------------------------------------+
		expected.isFollowing = false;
		expected.isProtected = false;
		expected.verified = true;
		expected.accessToken = new TwitterAccessToken("hoge", "fuga");
		// -------------------------------------+
		expected.base.itemId = tm.userBase01().itemId;
		expected.base.createdAt = MyDateUtils.parseOrNull("2010-01-02");
		expected.base.enteredAt = tm.today();
		// -------------------------------------+
		// actual = tm.TWITTER_USER_01;
		actual = tm.newTwitterUser01();

		assertModel();
	}

	@Test
	public void ビルダー_新規作成_TrimToNull() {
		printTitle();

		expected = tm.tuPojo01();
		expected.userName = null;
		expected.screenName = null;
		expected.profileImageUrl = null;
		expected.profileImageUrlHttps = null;

		actual = new User.Builder()
				.userId(1L)
				.screenName("")
				.userName("    ")
				.profileImageUrl("\t\t")
				.profileImageUrlHttps("\n  ")
				// -------------------------------------+
				.language(TwitterLanguage.JAPANESE.getCode())
				.country(TwitterCountry.JAPAN.getCode())
				.timeZone(TwitterTimeZone.TOKYO.getName())
				.utcOffset(0)
				.location("   調布市  　   ")
				.description("\t 　\tこんにちは、タローです。\n\n　")
				.url("　　http://google.com　　")
				// -------------------------------------+
				.friendsCount(100)
				.followersCount(200)
				.favouritesCount(10)
				.statusesCount(10000)
				.listedCount(0)
				// -------------------------------------+
				.isFollowing(false)
				.isProtected(false)
				.verified(true)
				// .accessKey("hoge")
//				.accessKeySecret("fuga")
				.accessToken("hoge", "fuga")
				// -------------------------------------+
				.itemId(tm.getItemId(0, 0))
				// .createdAt(MyDateUtils.parseOrNull("2010-01-02"))
				.createdAt(tm.date1())
				.enteredAt(tm.today())
				// -------------------------------------+
				.build();

		assertModel();
	}

	/* ************************************************************ */
	/*
	 * 保存
	 */
	/* ************************************************************ */
	@Test
	public void 保存_save_01() {
		printTitle();

		actual = tm.newTwitterUser01();
//		actual.save();//newTwitterUserXX内でセーブするように変更

		assertThat(actual.isPersistent(), is(true));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void 保存_valid_01() {
		printTitle();

		actual = tm.newTwitterUser01();
		boolean result = actual.valid();

		assertThat(result, is(true));
	}

	@Test
	public void 保存_valid_02() {
		printTitle();

		actual = tm.newTwitterUser02();
		boolean result = actual.valid();

		assertThat(result, is(true));
	}

	@Test
	public void 保存_valid_03() {
		printTitle();

		actual = tm.newTwitterUser03();
		boolean result = actual.valid();

		assertThat(result, is(true));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void 保存_validAndSave_01() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.saved = actual.validateAndSave();

		assertThat(actual.isPersistent(), is(true));
		assertThat(actual.saved, is(true));
	}

	@Test
	public void 保存_validAndSave_02() {
		printTitle();

		actual = tm.newTwitterUser02();
		actual.saved = actual.validateAndSave();

		assertThat(actual.isPersistent(), is(true));
		assertThat(actual.saved, is(true));
	}

	@Test
	public void 保存_validAndSave_03() {
		printTitle();

		actual = tm.newTwitterUser03();
		actual.saved = actual.validateAndSave();

		assertThat(actual.isPersistent(), is(true));
		assertThat(actual.saved, is(true));
	}

	/* ************************************************************ */
	/*
	 * バリデーション
	 */
	/* ************************************************************ */
	private void assertValidation(boolean _expected) {
		boolean result = actual.valid();
		showErrors();
		assertThat(result, is(_expected));
	}

	// =============================================*
	@Test
	public void バリデーション_userId_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.userId = ITwitterConstant.USER_ID_MIN;

		assertValidation(true);
	}

	@Test
	public void バリデーション_userId_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.userId = ITwitterConstant.USER_ID_MIN + 1;

		assertValidation(true);
	}

	@Test
	public void バリデーション_userId_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.userId = ITwitterConstant.USER_ID_MIN - 1;

		assertValidation(false);

		actual.save();// セーブはできてしまうという確認
		assertThat(actual.isPersistent(), is(true));
	}

	// =============================================*
	@Test
	public void バリデーション_screenName_Null() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.screenName = null;

		assertValidation(false);
	}

	@Test
	public void バリデーション_screenName_Empty() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.screenName = "";// 空文字

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_screenName_TrimToNull() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.screenName = "   ";

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_screenName_下限_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		int min = ITwitterConstant.SCREEN_NAME_MIN_LENGTH;
		actual.screenName = StringUtils.repeat('a', min);

		assertValidation(true);
	}

	@Test
	public void バリデーション_screenName_下限_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterUser01();
		int min = ITwitterConstant.SCREEN_NAME_MIN_LENGTH;
		actual.screenName = StringUtils.repeat('a', min + 1);

		assertValidation(true);
	}

	@Test
	public void バリデーション_screenName_下限_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterUser01();
		int min = ITwitterConstant.SCREEN_NAME_MIN_LENGTH;
		actual.screenName = StringUtils.repeat('a', min - 1);// 字数オーバー

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_screenName_上限_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.SCREEN_NAME_MAX_LENGTH;
		actual.screenName = StringUtils.repeat('a', max);

		assertValidation(true);
	}

	@Test
	public void バリデーション_screenName_上限_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.SCREEN_NAME_MAX_LENGTH;
		actual.screenName = StringUtils.repeat('a', max - 1);

		assertValidation(true);
	}

	@Test
	public void バリデーション_screenName_上限_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.SCREEN_NAME_MAX_LENGTH;
		actual.screenName = StringUtils.repeat('a', max + 1);// 字数オーバー

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_screenName_不正な文字列1() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.screenName = "NG-CHR";

		assertValidation(false);
	}

	@Test
	public void バリデーション_screenName_不正な文字列2() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.screenName = "********";

		assertValidation(false);
	}

	@Test
	public void バリデーション_screenName_不正な文字列3() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.screenName = "ああああああ";

		assertValidation(false);
	}

	@Test
	public void バリデーション_screenName_不正な文字列_OK() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.screenName = "1_Ok_";

		assertValidation(true);
	}

	// =============================================*
	@Test
	public void バリデーション_userName_Null() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.userName = null;

		assertValidation(false);
	}

	@Test
	public void バリデーション_userName_Empty() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.userName = "";// 空文字

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_userName_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.USER_NAME_MAX_LENGTH;
		actual.userName = StringUtils.repeat('a', max);

		assertValidation(true);
	}

	@Test
	public void バリデーション_userName_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.USER_NAME_MAX_LENGTH;
		actual.userName = StringUtils.repeat('a', max - 1);

		assertValidation(true);
	}

	@Test
	public void バリデーション_userName_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.USER_NAME_MAX_LENGTH;
		actual.userName = StringUtils.repeat('a', max + 1);// 字数オーバー

		assertValidation(false);
	}

	// =============================================*
	@Test
	public void バリデーション_profileImageUrl_Null() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.profileImageUrl = null;

		assertValidation(false);
	}

	@Test
	public void バリデーション_profileImageUrl_Empty() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.profileImageUrl = "";// 空文字

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_profileImageUrl_下限_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		int min = MyURLCheck.MIN_LENGTH;
		actual.profileImageUrl = "http://t.co";// ぴったり

		assertThat(actual.profileImageUrl.length(), is(min));
		assertValidation(true);
	}

	@Test
	public void バリデーション_profileImageUrl_下限_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterUser01();
		int min = MyURLCheck.MIN_LENGTH;
		actual.profileImageUrl = "http://t.co/";// 境界値+1

		assertThat(actual.profileImageUrl.length(), is(min + 1));
		assertValidation(true);
	}

	@Test
	public void バリデーション_profileImageUrl_下限_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterUser01();
		int min = MyURLCheck.MIN_LENGTH;
		actual.profileImageUrl = "http://t.c";// 字数足りず

		assertThat(actual.profileImageUrl.length(), is(min - 1));
		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_profileImageUrl_上限_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = MyURLCheck.MAX_LENGTH;
		actual.profileImageUrl += "/" + StringUtils.repeat('a', max);// 字数オーバー
		actual.profileImageUrl = StringUtils.left(actual.profileImageUrl, max);// ぴったり

		Logger.debug("actual.profileImageUrl:%s", actual.profileImageUrl);

		assertValidation(true);
	}

	@Test
	public void バリデーション_profileImageUrl_上限_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = MyURLCheck.MAX_LENGTH;
		actual.profileImageUrl += "/" + StringUtils.repeat('a', max);// 字数オーバー
		actual.profileImageUrl = StringUtils.left(actual.profileImageUrl,
				max - 1);

		Logger.debug("actual.profileImageUrl:%s", actual.profileImageUrl);

		assertValidation(true);
	}

	@Test
	public void バリデーション_profileImageUrl_上限_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = MyURLCheck.MAX_LENGTH;
		actual.profileImageUrl += "/" + StringUtils.repeat('a', max);// 字数オーバー
		actual.profileImageUrl = StringUtils.left(actual.profileImageUrl,
				max + 1);// 字数オーバー

		Logger.debug("actual.profileImageUrl:%s", actual.profileImageUrl);

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_profileImageUrl_不正な文字列1() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.profileImageUrl = "hoge;||fuga.piyo!>>";// 不正

		assertValidation(false);
	}

	@Test
	public void バリデーション_profileImageUrl_不正な文字列2() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.profileImageUrl = "http:||fuga.piyo!>>";// 不正

		assertValidation(false);
	}

	@Test
	public void バリデーション_profileImageUrl_不正な文字列3() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.profileImageUrl = "ftp://hoge.com";// スキーマ違反

		assertValidation(false);
	}

	// =============================================*
	@Test
	public void バリデーション_profileImageUrlHttps_Null() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.profileImageUrlHttps = null;

		assertValidation(false);
	}

	@Test
	public void バリデーション_profileImageUrlHttps_Empty() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.profileImageUrlHttps = "";// 空文字

		assertValidation(false);
	}

	/*
	 * 長さの境界値については省略する。
	 * profileImageUrlと同じ判定処理なため。
	 * スキーマについては、こちらはhttpsのみと制限しているのでテストしておく。
	 * 適合データはすでにチェック済みなので割愛。
	 */
	@Test
	public void バリデーション_profileImageUrlHttps_不正な文字列1() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.profileImageUrlHttps = "file://sample.txt";// スキーマ違反

		assertValidation(false);
	}

	@Test
	public void バリデーション_profileImageUrlHttps_不正な文字列2() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.profileImageUrlHttps = "http://sample.txt";// スキーマ違反

		assertValidation(false);
	}

	// =============================================*
	@Test
	public void バリデーション_createdAt_Null() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.setCreatedAt(null);

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_createdAt_Ancient_遠い過去() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.setCreatedAt(tm.ancient());// @InFuture(BIRTH_OF_TWEET)による

		Logger.debug("TestModels.ancient():%s", tm.ancient());
		Logger.debug("actual.getCreatedAt()___:%s", actual.getCreatedAt());

		assertValidation(false);
	}

	@Test
	public void バリデーション_createdAt_Future_遠い未来() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.setCreatedAt(tm.future());// @InPastによる

		Logger.debug("TestModels.future():%s", tm.future());
		Logger.debug("actual.getCreatedAt()__:%s", actual.getCreatedAt());

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_createdAt_未来_境界値_現在時刻() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.setCreatedAt(MyDateUtils.now());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());

		assertValidation(true);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_createdAt_過去_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.setCreatedAt(ITwitterConstant.LIMIT_OF_TWEET_DATETIME.toDate());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());
		Logger.debug("actual.getCreatedAt().time__:%s", actual.getCreatedAt()
				.getTime());
		Logger.debug("LIMIT_OF_TWEET_DATETIME.time:%s",
				ITwitterConstant.LIMIT_OF_TWEET_DATETIME.toDate().getTime());

		assertValidation(false);// 同値だとエラー扱いなので注意
	}

	@Test
	public void バリデーション_createdAt_過去_境界値_ちょい足し() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.setCreatedAt(ITwitterConstant.LIMIT_OF_TWEET_DATETIME
				.plusMillis(1).toDate());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());
		Logger.debug("actual.getCreatedAt().time__:%s", actual.getCreatedAt()
				.getTime());
		Logger.debug("LIMIT_OF_TWEET_DATETIME.time:%s",
				ITwitterConstant.LIMIT_OF_TWEET_DATETIME.toDate().getTime());

		assertValidation(true);// １ミリセカンドでも進んでいればOK
	}

	@Test
	public void バリデーション_createdAt_過去_境界値_圏内_秒() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.setCreatedAt(ITwitterConstant.LIMIT_OF_TWEET_DATETIME
				.plusSeconds(1)
				.toDate());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());
		System.out.println("createdAt:"+ actual.getCreatedAt());
		System.out.println("enteredAt:"+ actual.getEnteredAt());

		assertValidation(true);
	}

	@Test
	public void バリデーション_createdAt_過去_境界値_圏外_日() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.setCreatedAt(ITwitterConstant.LIMIT_OF_TWEET_DATETIME.minusDays(
				1)
				.toDate());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());

		assertValidation(false);
	}

	// 日付の変化がポイント
	@Test
	public void バリデーション_createdAt_過去_境界値_圏外_秒() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.setCreatedAt(ITwitterConstant.LIMIT_OF_TWEET_DATETIME
				.minusSeconds(1)
				.toDate());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());

		assertValidation(false);
	}

	// =============================================*
	@Test
	public void バリデーション_utcOffset_Null() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.utcOffset = null;

		assertValidation(true);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_utcOffset_下限_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		int min = -ITwitterConstant.UTC_OFFSET_RANGE;
		actual.utcOffset = min;

		assertValidation(true);
	}

	@Test
	public void バリデーション_utcOffset_下限_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterUser01();
		int min = -ITwitterConstant.UTC_OFFSET_RANGE;
		actual.utcOffset = min + 1;

		assertValidation(true);
	}

	@Test
	public void バリデーション_utcOffset_下限_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterUser01();
		int min = -ITwitterConstant.UTC_OFFSET_RANGE;
		actual.utcOffset = min - 1;

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_utcOffset_上限_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.UTC_OFFSET_RANGE;
		actual.utcOffset = max;

		assertValidation(true);
	}

	@Test
	public void バリデーション_utcOffset_上限_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.UTC_OFFSET_RANGE;
		actual.utcOffset = max - 1;

		assertValidation(true);
	}

	@Test
	public void バリデーション_utcOffset_上限_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.UTC_OFFSET_RANGE;
		actual.utcOffset = max + 1;

		assertValidation(false);
	}

	// =============================================*
	@Test
	public void バリデーション_location_Null() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.location = null;

		assertValidation(true);// nullはOK
	}

	@Test
	public void バリデーション_location_Empty() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.location = "";// 空文字

		assertValidation(true);// 空文字はOK
	}

	// -------------------------------------+
	@Test
	public void バリデーション_location_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.LOCATION_MAX_LENGTH;
		actual.location = StringUtils.repeat('a', max);

		assertValidation(true);
	}

	@Test
	public void バリデーション_location_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.LOCATION_MAX_LENGTH;
		actual.location = StringUtils.repeat('a', max - 1);

		assertValidation(true);
	}

	@Test
	public void バリデーション_location_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.LOCATION_MAX_LENGTH;
		actual.location = StringUtils.repeat('a', max + 1);// 字数オーバー

		assertValidation(false);
	}

	// =============================================*
	@Test
	public void バリデーション_description_Null() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.description = null;

		assertValidation(true);// nullはOK
	}

	@Test
	public void バリデーション_description_Empty() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.description = "";// 空文字

		assertValidation(true);// 空文字はOK
	}

	// -------------------------------------+
	@Test
	public void バリデーション_description_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.DESCRIPTION_MAX_LENGTH;
		actual.description = StringUtils.repeat('a', max);

		assertValidation(true);
	}

	@Test
	public void バリデーション_description_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.DESCRIPTION_MAX_LENGTH;
		actual.description = StringUtils.repeat('a', max - 1);

		assertValidation(true);
	}

	@Test
	public void バリデーション_description_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterUser01();
		int max = ITwitterConstant.DESCRIPTION_MAX_LENGTH;
		actual.description = StringUtils.repeat('a', max + 1);// 字数オーバー

		assertValidation(false);
	}

	// =============================================*
	// 代表で１つだけ。他は割愛。
	@Test
	public void バリデーション_friendsCount_境界値() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.friendsCount = ITwitterConstant.COUNT_MIN;

		assertValidation(true);
	}

	@Test
	public void バリデーション_friendsCount_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.friendsCount = ITwitterConstant.COUNT_MIN + 1;

		assertValidation(true);
	}

	@Test
	public void バリデーション_friendsCount_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterUser01();
		actual.friendsCount = ITwitterConstant.COUNT_MIN - 1;

		assertValidation(false);
	}

	/* ************************************************************ */
	/*
	 * Fixture
	 */
	/* ************************************************************ */
	@Test
	public void Fixture読み込み_01(){
		printTitle();

		Fixtures.deleteDatabase();
		Fixtures.loadModels("test_models_ver20130112.yml");

		assertThat(User.count(),is(4L));
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
